/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.foreign;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.nio.ByteBuffer;
import java.util.Optional;

import jdk.internal.misc.Unsafe;
import jdk.internal.misc.VM;
import jdk.internal.vm.annotation.ForceInline;
import sun.security.action.GetBooleanAction;

/**
 * Implementation for native memory segments. A native memory segment is essentially a wrapper around
 * a native long address.
 */
public sealed class NativeMemorySegmentImpl extends AbstractMemorySegmentImpl permits MappedMemorySegmentImpl {

    private static final Unsafe UNSAFE = Unsafe.getUnsafe();

    // The maximum alignment supported by malloc - typically 16 on
    // 64-bit platforms and 8 on 32-bit platforms.
    private static final long MAX_MALLOC_ALIGN = Unsafe.ADDRESS_SIZE == 4 ? 8 : 16;
    private static final boolean SKIP_ZERO_MEMORY = GetBooleanAction.privilegedGetProperty("java.base.share.classes.jdk.internal.foreign.skipZeroMemory");

    final long min;

    @ForceInline
    NativeMemorySegmentImpl(long min, long length, boolean readOnly, SegmentScope scope) {
        super(length, readOnly, scope);
        this.min = min;
    }

    @Override
    public long address() {
        return min;
    }

    @Override
    public Optional<Object> array() {
        return Optional.empty();
    }

    @ForceInline
    @Override
    NativeMemorySegmentImpl dup(long offset, long size, boolean readOnly, SegmentScope scope) {
        return new NativeMemorySegmentImpl(min + offset, size, readOnly, scope);
    }

    @Override
    ByteBuffer makeByteBuffer() {
        return NIO_ACCESS.newDirectByteBuffer(min, (int) this.length, null,
                scope == MemorySessionImpl.GLOBAL ? null : this);
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public long unsafeGetOffset() {
        return min;
    }

    @Override
    public Object unsafeGetBase() {
        return null;
    }

    @Override
    public long maxAlignMask() {
        return 0;
    }

    // factories

    public static MemorySegment makeNativeSegment(long byteSize, long byteAlignment, SegmentScope scope) {
        MemorySessionImpl sessionImpl = (MemorySessionImpl) scope;
        sessionImpl.checkValidState();
        if (VM.isDirectMemoryPageAligned()) {
            byteAlignment = Math.max(byteAlignment, NIO_ACCESS.pageSize());
        }
        long alignedSize = Math.max(1L, byteAlignment > MAX_MALLOC_ALIGN ?
                byteSize + (byteAlignment - 1) :
                byteSize);

        NIO_ACCESS.reserveMemory(alignedSize, byteSize);

        long buf = UNSAFE.allocateMemory(alignedSize);
        if (!SKIP_ZERO_MEMORY) {
            UNSAFE.setMemory(buf, alignedSize, (byte)0);
        }
        long alignedBuf = Utils.alignUp(buf, byteAlignment);
        AbstractMemorySegmentImpl segment = new NativeMemorySegmentImpl(buf, alignedSize,
                false, scope);
        sessionImpl.addOrCleanupIfFail(new MemorySessionImpl.ResourceList.ResourceCleanup() {
            @Override
            public void cleanup() {
                UNSAFE.freeMemory(buf);
                NIO_ACCESS.unreserveMemory(alignedSize, byteSize);
            }
        });
        if (alignedSize != byteSize) {
            long delta = alignedBuf - buf;
            segment = segment.asSlice(delta, byteSize);
        }
        return segment;
    }

    // Unsafe native segment factories. These are used by the implementation code, to skip the sanity checks
    // associated with MemorySegment::ofAddress.

    @ForceInline
    public static MemorySegment makeNativeSegmentUnchecked(long min, long byteSize, SegmentScope scope, Runnable action) {
        MemorySessionImpl sessionImpl = (MemorySessionImpl) scope;
        if (action == null) {
            sessionImpl.checkValidState();
        } else {
            sessionImpl.addCloseAction(action);
        }
        return new NativeMemorySegmentImpl(min, byteSize, false, scope);
    }

    @ForceInline
    public static MemorySegment makeNativeSegmentUnchecked(long min, long byteSize, SegmentScope scope) {
        MemorySessionImpl sessionImpl = (MemorySessionImpl) scope;
        sessionImpl.checkValidState();
        return new NativeMemorySegmentImpl(min, byteSize, false, scope);
    }

    @ForceInline
    public static MemorySegment makeNativeSegmentUnchecked(long min, long byteSize) {
        return new NativeMemorySegmentImpl(min, byteSize, false, SegmentScope.global());
    }
}
