/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang.invoke;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static java.base.share.classes.java.lang.invoke.MethodHandleStatics.UNSAFE;

/**
 * The base class for generated byte array and byte buffer view
 * implementations
 */
abstract class VarHandleByteArrayBase {
    // Buffer.address
    static final long BUFFER_ADDRESS
            = UNSAFE.objectFieldOffset(Buffer.class, "address");

    // Buffer.limit
    static final long BUFFER_LIMIT
            = UNSAFE.objectFieldOffset(Buffer.class, "limit");

    // ByteBuffer.hb
    static final long BYTE_BUFFER_HB
            = UNSAFE.objectFieldOffset(ByteBuffer.class, "hb");

    // ByteBuffer.isReadOnly
    static final long BYTE_BUFFER_IS_READ_ONLY
            = UNSAFE.objectFieldOffset(ByteBuffer.class, "isReadOnly");

    static final boolean BE = UNSAFE.isBigEndian();

    static IllegalStateException newIllegalStateExceptionForMisalignedAccess(int index) {
        return new IllegalStateException("Misaligned access at index: " + index);
    }
}
