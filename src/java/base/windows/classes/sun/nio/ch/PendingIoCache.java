/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.ch;


import java.nio.channels.*;
import java.util.*;
import jdk.internal.misc.Unsafe;

/**
 * Maintains a mapping of pending I/O requests (identified by the address of
 * an OVERLAPPED structure) to Futures.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023 
 */

class PendingIoCache {
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final int addressSize = unsafe.addressSize();

    private static int dependsArch(int value32, int value64) {
        return (addressSize == 4) ? value32 : value64;
    }

    /*
     * typedef struct _OVERLAPPED {
     *     DWORD  Internal;
     *     DWORD  InternalHigh;
     *     DWORD  Offset;
     *     DWORD  OffsetHigh;
     *     HANDLE hEvent;
     * } OVERLAPPED;
     */
    private static final int SIZEOF_OVERLAPPED = dependsArch(20, 32);

    // set to true when closed
    private boolean closed;

    // set to true when thread is waiting for all I/O operations to complete
    private boolean closePending;

    // maps OVERLAPPED to PendingFuture
    @SuppressWarnings("rawtypes")
    private final Map<Long,PendingFuture> pendingIoMap =
        new HashMap<Long,PendingFuture>();

    // per-channel cache of OVERLAPPED structures
    private long[] overlappedCache = new long[4];
    private int overlappedCacheCount = 0;

    PendingIoCache() {
    }

    long add(PendingFuture<?,?> result) {
        synchronized (this) {
            if (closed)
                throw new AssertionError("Should not get here");
            long ov;
            if (overlappedCacheCount > 0) {
                ov = overlappedCache[--overlappedCacheCount];
            } else {
                ov = unsafe.allocateMemory(SIZEOF_OVERLAPPED);
            }
            pendingIoMap.put(ov, result);
            return ov;
        }
    }

    @SuppressWarnings("unchecked")
    <V,A> PendingFuture<V,A> remove(long overlapped) {
        synchronized (this) {
            PendingFuture<V,A> res = pendingIoMap.remove(overlapped);
            if (res != null) {
                if (overlappedCacheCount < overlappedCache.length) {
                    overlappedCache[overlappedCacheCount++] = overlapped;
                } else {
                    // cache full or channel closing
                    unsafe.freeMemory(overlapped);
                }
                // notify closing thread.
                if (closePending) {
                    this.notifyAll();
                }
            }
            return res;
        }
    }

    void close() {
        synchronized (this) {
            if (closed)
                return;

            // handle case where I/O operations that have not completed.
            if (!pendingIoMap.isEmpty())
                clearPendingIoMap();

            // release memory for any cached OVERLAPPED structures
            while (overlappedCacheCount > 0) {
                unsafe.freeMemory( overlappedCache[--overlappedCacheCount] );
            }

            // done
            closed = true;
        }
    }

    private void clearPendingIoMap() {
        assert Thread.holdsLock(this);

        // wait up to 50ms for the I/O operations to complete
        closePending = true;
        try {
            this.wait(50);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
        }
        closePending = false;
        if (pendingIoMap.isEmpty())
            return;

        // cause all pending I/O operations to fail
        // simulate the failure of all pending I/O operations.
        for (Long ov: pendingIoMap.keySet()) {
            PendingFuture<?,?> result = pendingIoMap.get(ov);

            // make I/O port aware of the stale OVERLAPPED structure
            Iocp iocp = (Iocp)((Groupable)result.channel()).group();
            iocp.makeStale(ov);

            // execute a task that invokes the result handler's failed method
            final Iocp.ResultHandler rh = (Iocp.ResultHandler)result.getContext();
            Runnable task = new Runnable() {
                public void run() {
                    rh.failed(-1, new AsynchronousCloseException());
                }
            };
            iocp.executeOnPooledThread(task);
        }
        pendingIoMap.clear();
    }
}