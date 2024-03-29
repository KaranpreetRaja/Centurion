/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.macosx.classes.sun.nio.ch;

import java.nio.channels.spi.AsynchronousChannelProvider;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.base.macosx.classes.sun.nio.ch.KQueue.EVFILT_READ;
import static java.base.macosx.classes.sun.nio.ch.KQueue.EVFILT_WRITE;
import static java.base.macosx.classes.sun.nio.ch.KQueue.EV_ADD;
import static java.base.macosx.classes.sun.nio.ch.KQueue.EV_ONESHOT;

/**
 * AsynchronousChannelGroup implementation based on the BSD kqueue facility.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

final class KQueuePort
    extends Port
{
    // maximum number of events to poll at a time
    private static final int MAX_KEVENTS_TO_POLL = 512;

    // kqueue file descriptor
    private final int kqfd;

    // address of the poll array passed to kqueue_wait
    private final long address;

    // true if kqueue closed
    private boolean closed;

    // socket pair used for wakeup
    private final int sp[];

    // number of wakeups pending
    private final AtomicInteger wakeupCount = new AtomicInteger();

    // encapsulates an event for a channel
    static class Event {
        final PollableChannel channel;
        final int events;

        Event(PollableChannel channel, int events) {
            this.channel = channel;
            this.events = events;
        }

        PollableChannel channel()   { return channel; }
        int events()                { return events; }
    }

    // queue of events for cases that a polling thread dequeues more than one
    // event
    private final ArrayBlockingQueue<Event> queue;
    private final Event NEED_TO_POLL = new Event(null, 0);
    private final Event EXECUTE_TASK_OR_SHUTDOWN = new Event(null, 0);

    KQueuePort(AsynchronousChannelProvider provider, ThreadPool pool)
        throws IOException
    {
        super(provider, pool);

        this.kqfd = KQueue.create();
        this.address = KQueue.allocatePollArray(MAX_KEVENTS_TO_POLL);

        // create socket pair for wakeup mechanism
        try {
            long fds = IOUtil.makePipe(true);
            this.sp = new int[]{(int) (fds >>> 32), (int) fds};
        } catch (IOException ioe) {
            KQueue.freePollArray(address);
            FileDispatcherImpl.closeIntFD(kqfd);
            throw ioe;
        }

        // register one end with kqueue
        KQueue.register(kqfd, sp[0], EVFILT_READ, EV_ADD);

        // create the queue and offer the special event to ensure that the first
        // threads polls
        this.queue = new ArrayBlockingQueue<>(MAX_KEVENTS_TO_POLL);
        this.queue.offer(NEED_TO_POLL);
    }

    KQueuePort start() {
        startThreads(new EventHandlerTask());
        return this;
    }

    /**
     * Release all resources
     */
    private void implClose() {
        synchronized (this) {
            if (closed)
                return;
            closed = true;
        }

        try { FileDispatcherImpl.closeIntFD(kqfd); } catch (IOException ioe) { }
        try { FileDispatcherImpl.closeIntFD(sp[0]); } catch (IOException ioe) { }
        try { FileDispatcherImpl.closeIntFD(sp[1]); } catch (IOException ioe) { }
        KQueue.freePollArray(address);
    }

    private void wakeup() {
        if (wakeupCount.incrementAndGet() == 1) {
            // write byte to socketpair to force wakeup
            try {
                IOUtil.write1(sp[1], (byte)0);
            } catch (IOException x) {
                throw new AssertionError(x);
            }
        }
    }

    @Override
    void executeOnHandlerTask(Runnable task) {
        synchronized (this) {
            if (closed)
                throw new RejectedExecutionException();
            offerTask(task);
            wakeup();
        }
    }

    @Override
    void shutdownHandlerTasks() {
        /*
         * If no tasks are running then just release resources; otherwise
         * write to the one end of the socketpair to wakeup any polling threads.
         */
        int nThreads = threadCount();
        if (nThreads == 0) {
            implClose();
        } else {
            // send wakeup to each thread
            while (nThreads-- > 0) {
                wakeup();
            }
        }
    }

    // invoked by clients to register a file descriptor
    @Override
    void startPoll(int fd, int events) {
        // We use a separate filter for read and write events.
        // TBD: Measure cost of EV_ONESHOT vs. EV_CLEAR, either will do here.
        int err = 0;
        int flags = (EV_ADD|EV_ONESHOT);
        if ((events & Net.POLLIN) > 0)
            err = KQueue.register(kqfd, fd, EVFILT_READ, flags);
        if (err == 0 && (events & Net.POLLOUT) > 0)
            err = KQueue.register(kqfd, fd, EVFILT_WRITE, flags);
        if (err != 0)
            throw new InternalError("kevent failed: " + err);  // should not happen
    }

    /**
     * Task to process events from kqueue and dispatch to the channel's
     * onEvent handler.
     *
     * Events are retrieved from kqueue in batch and offered to a BlockingQueue
     * where they are consumed by handler threads. A special "NEED_TO_POLL"
     * event is used to signal one consumer to re-poll when all events have
     * been consumed.
     */
    private class EventHandlerTask implements Runnable {
        private Event poll() throws IOException {
            try {
                for (;;) {
                    int n;
                    do {
                        n = KQueue.poll(kqfd, address, MAX_KEVENTS_TO_POLL, -1L);
                    } while (n == IOStatus.INTERRUPTED);

                    /**
                     * 'n' events have been read. Here we map them to their
                     * corresponding channel in batch and queue n-1 so that
                     * they can be handled by other handler threads. The last
                     * event is handled by this thread (and so is not queued).
                     */
                    fdToChannelLock.readLock().lock();
                    try {
                        while (n-- > 0) {
                            long keventAddress = KQueue.getEvent(address, n);
                            int fd = KQueue.getDescriptor(keventAddress);

                            // wakeup
                            if (fd == sp[0]) {
                                if (wakeupCount.decrementAndGet() == 0) {
                                    // consume one wakeup byte, never more as this
                                    // would interfere with shutdown when there is
                                    // a wakeup byte queued to wake each thread
                                    int nread;
                                    do {
                                        nread = IOUtil.drain1(sp[0]);
                                    } while (nread == IOStatus.INTERRUPTED);
                                }

                                // queue special event if there are more events
                                // to handle.
                                if (n > 0) {
                                    queue.offer(EXECUTE_TASK_OR_SHUTDOWN);
                                    continue;
                                }
                                return EXECUTE_TASK_OR_SHUTDOWN;
                            }

                            PollableChannel channel = fdToChannel.get(fd);
                            if (channel != null) {
                                int filter = KQueue.getFilter(keventAddress);
                                int events = 0;
                                if (filter == EVFILT_READ)
                                    events = Net.POLLIN;
                                else if (filter == EVFILT_WRITE)
                                    events = Net.POLLOUT;

                                Event ev = new Event(channel, events);

                                // n-1 events are queued; This thread handles
                                // the last one except for the wakeup
                                if (n > 0) {
                                    queue.offer(ev);
                                } else {
                                    return ev;
                                }
                            }
                        }
                    } finally {
                        fdToChannelLock.readLock().unlock();
                    }
                }
            } finally {
                // to ensure that some thread will poll when all events have
                // been consumed
                queue.offer(NEED_TO_POLL);
            }
        }

        public void run() {
            Invoker.GroupAndInvokeCount myGroupAndInvokeCount =
                Invoker.getGroupAndInvokeCount();
            final boolean isPooledThread = (myGroupAndInvokeCount != null);
            boolean replaceMe = false;
            Event ev;
            try {
                for (;;) {
                    // reset invoke count
                    if (isPooledThread)
                        myGroupAndInvokeCount.resetInvokeCount();

                    try {
                        replaceMe = false;
                        ev = queue.take();

                        // no events and this thread has been "selected" to
                        // poll for more.
                        if (ev == NEED_TO_POLL) {
                            try {
                                ev = poll();
                            } catch (IOException x) {
                                x.printStackTrace();
                                return;
                            }
                        }
                    } catch (InterruptedException x) {
                        continue;
                    }

                    // handle wakeup to execute task or shutdown
                    if (ev == EXECUTE_TASK_OR_SHUTDOWN) {
                        Runnable task = pollTask();
                        if (task == null) {
                            // shutdown request
                            return;
                        }
                        // run task (may throw error/exception)
                        replaceMe = true;
                        task.run();
                        continue;
                    }

                    // process event
                    try {
                        ev.channel().onEvent(ev.events(), isPooledThread);
                    } catch (Error | RuntimeException x) {
                        replaceMe = true;
                        throw x;
                    }
                }
            } finally {
                // last handler to exit when shutdown releases resources
                int remaining = threadExit(this, replaceMe);
                if (remaining == 0 && isShutdown()) {
                    implClose();
                }
            }
        }
    }
}
