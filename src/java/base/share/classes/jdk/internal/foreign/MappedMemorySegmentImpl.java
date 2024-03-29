/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.foreign;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.nio.ByteBuffer;
import jdk.internal.access.foreign.UnmapperProxy;
import jdk.internal.misc.ScopedMemoryAccess;

/**
 * Implementation for a mapped memory segments. A mapped memory segment is a native memory segment, which
 * additionally features an {@link UnmapperProxy} object. This object provides detailed information about the
 * memory mapped segment, such as the file descriptor associated with the mapping. This information is crucial
 * in order to correctly reconstruct a byte buffer object from the segment (see {@link #makeByteBuffer()}).
 */
public sealed class MappedMemorySegmentImpl extends NativeMemorySegmentImpl {

    private final UnmapperProxy unmapper;

    static final ScopedMemoryAccess SCOPED_MEMORY_ACCESS = ScopedMemoryAccess.getScopedMemoryAccess();

    public MappedMemorySegmentImpl(long min, UnmapperProxy unmapper, long length, boolean readOnly, SegmentScope scope) {
        super(min, length, readOnly, scope);
        this.unmapper = unmapper;
    }

    @Override
    ByteBuffer makeByteBuffer() {
        return NIO_ACCESS.newMappedByteBuffer(unmapper, min, (int)length, null,
                scope == MemorySessionImpl.GLOBAL ? null : this);
    }

    @Override
    MappedMemorySegmentImpl dup(long offset, long size, boolean readOnly, SegmentScope scope) {
        return new MappedMemorySegmentImpl(min + offset, unmapper, size, readOnly, scope);
    }

    // mapped segment methods

    @Override
    public MappedMemorySegmentImpl asSlice(long offset, long newSize) {
        return (MappedMemorySegmentImpl)super.asSlice(offset, newSize);
    }

    @Override
    public boolean isMapped() {
        return true;
    }

    // support for mapped segments

    public MemorySegment segment() {
        return MappedMemorySegmentImpl.this;
    }

    public void load() {
        SCOPED_MEMORY_ACCESS.load(sessionImpl(), min, unmapper.isSync(), length);
    }

    public void unload() {
        SCOPED_MEMORY_ACCESS.unload(sessionImpl(), min, unmapper.isSync(), length);
    }

    public boolean isLoaded() {
        return SCOPED_MEMORY_ACCESS.isLoaded(sessionImpl(), min, unmapper.isSync(), length);
    }

    public void force() {
        SCOPED_MEMORY_ACCESS.force(sessionImpl(), unmapper.fileDescriptor(), min, unmapper.isSync(), 0, length);
    }

    public static final class EmptyMappedMemorySegmentImpl extends MappedMemorySegmentImpl {

        public EmptyMappedMemorySegmentImpl(boolean readOnly, MemorySessionImpl session) {
            super(0, null, 0, readOnly, session);
        }

        @Override
        public void load() {
            // do nothing
        }

        @Override
        public void unload() {
            // do nothing
        }

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void force() {
            // do nothing
        }
    }
}
