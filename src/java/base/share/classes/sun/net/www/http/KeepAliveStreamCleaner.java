/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.net.www.http;

import java.io.IOException;
import java.util.LinkedList;
import sun.net.NetProperties;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to cleanup any remaining data that may be on a KeepAliveStream
 * so that the connection can be cached in the KeepAliveCache.
 * Instances of this class can be used as a FIFO queue for KeepAliveCleanerEntry objects.
 * Executing this Runnable removes each KeepAliveCleanerEntry from the Queue, reads
 * the reamining bytes on its KeepAliveStream, and if successful puts the connection in
 * the KeepAliveCache.
 *
 * @author Chris Hegarty
 */

@SuppressWarnings("serial")  // never serialized
class KeepAliveStreamCleaner
    extends LinkedList<KeepAliveCleanerEntry>
    implements Runnable
{
    // maximum amount of remaining data that we will try to cleanup
    protected static int MAX_DATA_REMAINING = 512;

    // maximum amount of KeepAliveStreams to be queued
    protected static int MAX_CAPACITY = 10;

    // timeout for both socket and poll on the queue
    protected static final int TIMEOUT = 5000;

    // max retries for skipping data
    private static final int MAX_RETRIES = 5;

    static {
        final String maxDataKey = "http.KeepAlive.remainingData";
        @SuppressWarnings("removal")
        int maxData = AccessController.doPrivileged(
            new PrivilegedAction<Integer>() {
                public Integer run() {
                    return NetProperties.getInteger(maxDataKey, MAX_DATA_REMAINING);
                }}).intValue() * 1024;
        MAX_DATA_REMAINING = maxData;

        final String maxCapacityKey = "http.KeepAlive.queuedConnections";
        @SuppressWarnings("removal")
        int maxCapacity = AccessController.doPrivileged(
            new PrivilegedAction<Integer>() {
                public Integer run() {
                    return NetProperties.getInteger(maxCapacityKey, MAX_CAPACITY);
                }}).intValue();
        MAX_CAPACITY = maxCapacity;

    }

    private final ReentrantLock queueLock = new ReentrantLock();
    private final Condition waiter = queueLock.newCondition();

    final void signalAll() {
        waiter.signalAll();
    }

    final void lock() {
        queueLock.lock();
    }

    final void unlock() {
        queueLock.unlock();
    }

    @Override
    public boolean offer(KeepAliveCleanerEntry e) {
        if (size() >= MAX_CAPACITY)
            return false;

        return super.offer(e);
    }

    @Override
    public void run()
    {
        KeepAliveCleanerEntry kace = null;

        do {
            try {
                lock();
                try {
                    long before = System.currentTimeMillis();
                    long timeout = TIMEOUT;
                    while ((kace = poll()) == null) {
                        waiter.await(timeout, TimeUnit.MILLISECONDS);

                        long after = System.currentTimeMillis();
                        long elapsed = after - before;
                        if (elapsed > timeout) {
                            /* one last try */
                            kace = poll();
                            break;
                        }
                        before = after;
                        timeout -= elapsed;
                    }
                } finally {
                    unlock();
                }

                if(kace == null)
                    break;

                KeepAliveStream kas = kace.getKeepAliveStream();

                if (kas != null) {
                    kas.lock();
                    try {
                        HttpClient hc = kace.getHttpClient();
                        try {
                            if (hc != null && !hc.isInKeepAliveCache()) {
                                int oldTimeout = hc.getReadTimeout();
                                hc.setReadTimeout(TIMEOUT);
                                long remainingToRead = kas.remainingToRead();
                                if (remainingToRead > 0) {
                                    long n = 0;
                                    int retries = 0;
                                    while (n < remainingToRead && retries < MAX_RETRIES) {
                                        remainingToRead = remainingToRead - n;
                                        n = kas.skip(remainingToRead);
                                        if (n == 0)
                                            retries++;
                                    }
                                    remainingToRead = remainingToRead - n;
                                }
                                if (remainingToRead == 0) {
                                    hc.setReadTimeout(oldTimeout);
                                    hc.finished();
                                } else
                                    hc.closeServer();
                            }
                        } catch (IOException ioe) {
                            hc.closeServer();
                        } finally {
                            kas.setClosed();
                        }
                    } finally {
                        kas.unlock();
                    }
                }
            } catch (InterruptedException ie) { }
        } while (kace != null);
    }
}
