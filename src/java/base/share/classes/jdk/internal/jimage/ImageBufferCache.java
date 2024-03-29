/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.jimage;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * @implNote This class needs to maintain JDK 8 source compatibility.
 *
 * It is used internally in the JDK to implement jimage/jrtfs access,
 * but also compiled and delivered as part of the jrt-fs.jar to support access
 * to the jimage file provided by the shipped JDK by tools running on JDK 8.
 */
class ImageBufferCache {
    private static final int MAX_CACHED_BUFFERS = 3;
    private static final int LARGE_BUFFER = 0x10000;

    /*
     * We used to have a class BufferReference extending from WeakReference<ByteBuffer>.
     * BufferReference class had an  instance field called "capacity". This field was
     * used to make DECREASING_CAPACITY_NULLS_LAST comparator stable in the presence
     * of GC clearing the WeakReference concurrently.
     *
     * But this scheme results in metaspace leak. The thread local is alive till the
     * the thread is alive. And so ImageBufferCache$BufferReference class was kept alive.
     * Because this class and ImageBufferCache$BufferReference are all loaded by a URL
     * class loader from jrt-fs.jar, the class loader and so all the classes loaded by it
     * were alive!
     *
     * Solution is to avoid using a URL loader loaded class type with thread local. All we
     * need is a pair of WeakReference<ByteBuffer>, Integer (saved capacity for stability
     * of comparator). We use Map.Entry as pair implementation. With this, all types used
     * with thread local are bootstrap types and so no metaspace leak.
     */
    @SuppressWarnings("unchecked")
    private static final ThreadLocal<Map.Entry<WeakReference<ByteBuffer>, Integer>[]> CACHE =
        new ThreadLocal<Map.Entry<WeakReference<ByteBuffer>, Integer>[]>() {
            @Override
            protected Map.Entry<WeakReference<ByteBuffer>, Integer>[] initialValue() {
                // 1 extra slot to simplify logic of releaseBuffer()
                return (Map.Entry<WeakReference<ByteBuffer>, Integer>[])new Map.Entry<?,?>[MAX_CACHED_BUFFERS + 1];
            }
        };

    private static ByteBuffer allocateBuffer(long size) {
        return ByteBuffer.allocateDirect((int)((size + 0xFFF) & ~0xFFF));
    }

    static ByteBuffer getBuffer(long size) {
        if (size < 0 || Integer.MAX_VALUE < size) {
            throw new IndexOutOfBoundsException("size");
        }

        ByteBuffer result = null;

        if (size > LARGE_BUFFER) {
            result = allocateBuffer(size);
        } else {
            Map.Entry<WeakReference<ByteBuffer>, Integer>[] cache = CACHE.get();

            // buffers are ordered by decreasing capacity
            // cache[MAX_CACHED_BUFFERS] is always null
            for (int i = MAX_CACHED_BUFFERS - 1; i >= 0; i--) {
                Map.Entry<WeakReference<ByteBuffer>, Integer> reference = cache[i];

                if (reference != null) {
                    ByteBuffer buffer = getByteBuffer(reference);

                    if (buffer != null && size <= buffer.capacity()) {
                        cache[i] = null;
                        result = buffer;
                        result.rewind();
                        break;
                    }
                }
            }

            if (result == null) {
                result = allocateBuffer(size);
            }
        }

        result.limit((int)size);

        return result;
    }

    static void releaseBuffer(ByteBuffer buffer) {
        if (buffer.capacity() > LARGE_BUFFER) {
            return;
        }

        Map.Entry<WeakReference<ByteBuffer>, Integer>[] cache = CACHE.get();

        // expunge cleared BufferRef(s)
        for (int i = 0; i < MAX_CACHED_BUFFERS; i++) {
            Map.Entry<WeakReference<ByteBuffer>, Integer> reference = cache[i];
            if (reference != null && getByteBuffer(reference) == null) {
                cache[i] = null;
            }
        }

        // insert buffer back with new BufferRef wrapping it
        cache[MAX_CACHED_BUFFERS] = newCacheEntry(buffer);
        Arrays.sort(cache, DECREASING_CAPACITY_NULLS_LAST);
        // squeeze the smallest one out
        cache[MAX_CACHED_BUFFERS] = null;
    }

    private static Map.Entry<WeakReference<ByteBuffer>, Integer> newCacheEntry(ByteBuffer bb) {
        return new AbstractMap.SimpleEntry<WeakReference<ByteBuffer>, Integer>(
                    new WeakReference<ByteBuffer>(bb), bb.capacity());
    }

    private static int getCapacity(Map.Entry<WeakReference<ByteBuffer>, Integer> e) {
        return e == null? 0 : e.getValue();
    }

    private static ByteBuffer getByteBuffer(Map.Entry<WeakReference<ByteBuffer>, Integer> e) {
        return e == null? null : e.getKey().get();
    }

    private static Comparator<Map.Entry<WeakReference<ByteBuffer>, Integer>> DECREASING_CAPACITY_NULLS_LAST =
        new Comparator<Map.Entry<WeakReference<ByteBuffer>, Integer>>() {
            @Override
            public int compare(Map.Entry<WeakReference<ByteBuffer>, Integer> br1,
                        Map.Entry<WeakReference<ByteBuffer>, Integer> br2) {
                return Integer.compare(getCapacity(br1), getCapacity(br2));
            }
        };
}
