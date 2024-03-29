/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.jimage.decompressor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.base.share.classes.jdk.internal.jimage.decompressor.ResourceDecompressor.StringsProvider;

/**
 *
 * A resource header for compressed resource. This class is handled internally,
 * you don't have to add header to the resource, headers are added automatically
 * for compressed resources.
 *
 * @implNote This class needs to maintain JDK 8 source compatibility.
 *
 * It is used internally in the JDK to implement jimage/jrtfs access,
 * but also compiled and delivered as part of the jrtfs.jar to support access
 * to the jimage file provided by the shipped JDK by tools running on JDK 8.
 */
public final class CompressedResourceHeader {

    private static final int SIZE = 29;
    public static final int MAGIC = 0xCAFEFAFA;
    private final long uncompressedSize;
    private final long compressedSize;
    private final int decompressorNameOffset;
    private final int contentOffset;
    private final boolean isTerminal;

    public CompressedResourceHeader(long compressedSize,
            long uncompressedSize, int decompressorNameOffset, int contentOffset,
            boolean isTerminal) {
        this.compressedSize = compressedSize;
        this.uncompressedSize = uncompressedSize;
        this.decompressorNameOffset = decompressorNameOffset;
        this.contentOffset = contentOffset;
        this.isTerminal = isTerminal;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public int getDecompressorNameOffset() {
        return decompressorNameOffset;
    }

    public int getContentOffset() {
        return contentOffset;
    }

    public String getStoredContent(StringsProvider provider) {
        Objects.requireNonNull(provider);
        if(contentOffset == -1) {
            return null;
        }
        return provider.getString(contentOffset);
    }

    public long getUncompressedSize() {
        return uncompressedSize;
    }

    public long getResourceSize() {
        return compressedSize;
    }

    public byte[] getBytes(ByteOrder order) {
        Objects.requireNonNull(order);
        ByteBuffer buffer = ByteBuffer.allocate(SIZE);
        buffer.order(order);
        buffer.putInt(MAGIC);
        buffer.putLong(compressedSize);
        buffer.putLong(uncompressedSize);
        buffer.putInt(decompressorNameOffset);
        buffer.putInt(contentOffset);
        buffer.put(isTerminal ? (byte)1 : (byte)0);
        return buffer.array();
    }

    public static int getSize() {
        return SIZE;
    }

    public static CompressedResourceHeader readFromResource(ByteOrder order,
            byte[] resource) {
        Objects.requireNonNull(order);
        Objects.requireNonNull(resource);
        if (resource.length < getSize()) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(resource, 0, SIZE);
        buffer.order(order);
        int magic = buffer.getInt();
        if(magic != MAGIC) {
            return null;
        }
        long size = buffer.getLong();
        long uncompressedSize = buffer.getLong();
        int decompressorNameOffset = buffer.getInt();
        int contentIndex = buffer.getInt();
        byte isTerminal = buffer.get();
        return new CompressedResourceHeader(size, uncompressedSize,
                decompressorNameOffset, contentIndex, isTerminal == 1);
    }
}
