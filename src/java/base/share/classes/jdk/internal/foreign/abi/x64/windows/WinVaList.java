/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.foreign.abi.x64.windows;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.VaList;
import java.lang.foreign.ValueLayout;

import jdk.internal.foreign.MemorySessionImpl;
import jdk.internal.foreign.abi.SharedUtils;
import jdk.internal.foreign.abi.SharedUtils.SimpleVaArg;

import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jdk.internal.foreign.PlatformLayouts.Win64.C_POINTER;

// see vadefs.h (VC header)
//
// in short
// -> va_list is just a pointer to a buffer with 64 bit entries.
// -> non-power-of-two-sized, or larger than 64 bit types passed by reference.
// -> other types passed in 64 bit slots by normal function calling convention.
//
// X64 va_arg impl:
//
//    typedef char* va_list;
//
//    #define __crt_va_arg(ap, t)                                               \
//        ((sizeof(t) > sizeof(__int64) || (sizeof(t) & (sizeof(t) - 1)) != 0) \
//            ? **(t**)((ap += sizeof(__int64)) - sizeof(__int64))             \
//            :  *(t* )((ap += sizeof(__int64)) - sizeof(__int64)))
//
public non-sealed class WinVaList implements VaList {
    private static final long VA_SLOT_SIZE_BYTES = 8;
    private static final VarHandle VH_address = C_POINTER.varHandle();

    private static final VaList EMPTY = new SharedUtils.EmptyVaList(MemorySegment.NULL);

    private MemorySegment segment;

    private WinVaList(MemorySegment segment) {
        this.segment = segment;
    }

    public static final VaList empty() {
        return EMPTY;
    }

    @Override
    public int nextVarg(ValueLayout.OfInt layout) {
        return (int) read(layout);
    }

    @Override
    public long nextVarg(ValueLayout.OfLong layout) {
        return (long) read(layout);
    }

    @Override
    public double nextVarg(ValueLayout.OfDouble layout) {
        return (double) read(layout);
    }

    @Override
    public MemorySegment nextVarg(ValueLayout.OfAddress layout) {
        return (MemorySegment) read(layout);
    }

    @Override
    public MemorySegment nextVarg(GroupLayout layout, SegmentAllocator allocator) {
        Objects.requireNonNull(allocator);
        return (MemorySegment) read(layout, allocator);
    }

    private Object read(MemoryLayout layout) {
        return read(layout, SharedUtils.THROWING_ALLOCATOR);
    }

    private Object read(MemoryLayout layout, SegmentAllocator allocator) {
        Objects.requireNonNull(layout);
        Object res;
        checkElement(layout);
        if (layout instanceof GroupLayout) {
            TypeClass typeClass = TypeClass.typeClassFor(layout, false);
            res = switch (typeClass) {
                case STRUCT_REFERENCE -> {
                    MemorySegment structAddr = (MemorySegment) VH_address.get(segment);
                    MemorySegment struct = MemorySegment.ofAddress(structAddr.address(), layout.byteSize(), segment.scope());
                    MemorySegment seg = allocator.allocate(layout);
                    seg.copyFrom(struct);
                    yield seg;
                }
                case STRUCT_REGISTER ->
                    allocator.allocate(layout).copyFrom(segment.asSlice(0, layout.byteSize()));
                default -> throw new IllegalStateException("Unexpected TypeClass: " + typeClass);
            };
        } else {
            VarHandle reader = layout.varHandle();
            res = reader.get(segment);
        }
        segment = segment.asSlice(VA_SLOT_SIZE_BYTES);
        return res;
    }

    private void checkElement(MemoryLayout layout) {
        if (segment.byteSize() < VA_SLOT_SIZE_BYTES) {
            throw SharedUtils.newVaListNSEE(layout);
        }
    }

    @Override
    public void skip(MemoryLayout... layouts) {
        Objects.requireNonNull(layouts);
        ((MemorySessionImpl) segment.scope()).checkValidState();
        for (MemoryLayout layout : layouts) {
            Objects.requireNonNull(layout);
            checkElement(layout);
            segment = segment.asSlice(VA_SLOT_SIZE_BYTES);
        }
    }

    static WinVaList ofAddress(long address, SegmentScope scope) {
        return new WinVaList(MemorySegment.ofAddress(address, Long.MAX_VALUE, scope));
    }

    static Builder builder(SegmentScope scope) {
        return new Builder(scope);
    }

    @Override
    public VaList copy() {
        ((MemorySessionImpl) segment.scope()).checkValidState();
        return new WinVaList(segment);
    }

    @Override
    public MemorySegment segment() {
        // make sure that returned segment cannot be accessed
        return segment.asSlice(0, 0);
    }

    public static non-sealed class Builder implements VaList.Builder {

        private final SegmentScope scope;
        private final List<SimpleVaArg> args = new ArrayList<>();

        public Builder(SegmentScope scope) {
            ((MemorySessionImpl) scope).checkValidState();
            this.scope = scope;
        }

        private Builder arg(MemoryLayout layout, Object value) {
            Objects.requireNonNull(layout);
            Objects.requireNonNull(value);
            args.add(new SimpleVaArg(layout, value));
            return this;
        }

        @Override
        public Builder addVarg(ValueLayout.OfInt layout, int value) {
            return arg(layout, value);
        }

        @Override
        public Builder addVarg(ValueLayout.OfLong layout, long value) {
            return arg(layout, value);
        }

        @Override
        public Builder addVarg(ValueLayout.OfDouble layout, double value) {
            return arg(layout, value);
        }

        @Override
        public Builder addVarg(ValueLayout.OfAddress layout, MemorySegment value) {
            return arg(layout, value);
        }

        @Override
        public Builder addVarg(GroupLayout layout, MemorySegment value) {
            return arg(layout, value);
        }

        public VaList build() {
            if (args.isEmpty()) {
                return EMPTY;
            }

            MemorySegment segment = MemorySegment.allocateNative(VA_SLOT_SIZE_BYTES * args.size(), scope);
            MemorySegment cursor = segment;

            for (SimpleVaArg arg : args) {
                if (arg.layout instanceof GroupLayout) {
                    MemorySegment msArg = ((MemorySegment) arg.value);
                    TypeClass typeClass = TypeClass.typeClassFor(arg.layout, false);
                    switch (typeClass) {
                        case STRUCT_REFERENCE -> {
                            MemorySegment copy = MemorySegment.allocateNative(arg.layout, scope);
                            copy.copyFrom(msArg); // by-value
                            VH_address.set(cursor, copy);
                        }
                        case STRUCT_REGISTER ->
                            cursor.copyFrom(msArg.asSlice(0, VA_SLOT_SIZE_BYTES));
                        default -> throw new IllegalStateException("Unexpected TypeClass: " + typeClass);
                    }
                } else {
                    VarHandle writer = arg.varHandle();
                    writer.set(cursor, arg.value);
                }
                cursor = cursor.asSlice(VA_SLOT_SIZE_BYTES);
            }

            return new WinVaList(segment);
        }
    }
}
