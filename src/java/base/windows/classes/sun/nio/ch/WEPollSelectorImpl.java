/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import jdk.internal.misc.Blocker;

import static sun.nio.ch.WEPoll.*;

/**
 * Windows wepoll based Selector implementation
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023 
 */
class WEPollSelectorImpl extends SelectorImpl {
    // maximum number of events to poll in one call to epoll_wait
    private static final int NUM_EPOLLEVENTS = 256;

    // wepoll handle
    private final long eph;

    // address of epoll_event array when polling with epoll_wait
    private final long pollArrayAddress;

    // maps SOCKET to selection key, synchronize on selector
    private final Map<Integer, SelectionKeyImpl> fdToKey = new HashMap<>();

    // pending new registrations/updates, queued by setEventOps
    private final Object updateLock = new Object();
    private final Deque<SelectionKeyImpl> updateKeys = new ArrayDeque<>();

    // interrupt/wakeup
    private final Object interruptLock = new Object();
    private boolean interruptTriggered;
    private final PipeImpl pipe;
    private final int fd0Val, fd1Val;

    WEPollSelectorImpl(SelectorProvider sp) throws IOException {
        super(sp);

        this.eph = WEPoll.create();
        this.pollArrayAddress = WEPoll.allocatePollArray(NUM_EPOLLEVENTS);

        // wakeup support
        try {
            this.pipe = new PipeImpl(sp, /* AF_UNIX */ true, /*buffering*/ false);
        } catch (IOException ioe) {
            WEPoll.freePollArray(pollArrayAddress);
            WEPoll.close(eph);
            throw ioe;
        }
        this.fd0Val = pipe.source().getFDVal();
        this.fd1Val = pipe.sink().getFDVal();

        // register one end of the pipe for wakeups
        WEPoll.ctl(eph, EPOLL_CTL_ADD, fd0Val, WEPoll.EPOLLIN);
    }

    private void ensureOpen() {
        if (!isOpen())
            throw new ClosedSelectorException();
    }

    @Override
    protected int doSelect(Consumer<SelectionKey> action, long timeout)
        throws IOException
    {
        assert Thread.holdsLock(this);

        // epoll_wait timeout is int
        int to = (int) Math.min(timeout, Integer.MAX_VALUE);
        boolean blocking = (to != 0);

        int numEntries;
        processUpdateQueue();
        processDeregisterQueue();
        try {
            begin(blocking);
            long comp = Blocker.begin(blocking);
            try {
                numEntries = WEPoll.wait(eph, pollArrayAddress, NUM_EPOLLEVENTS, to);
            } finally {
                Blocker.end(comp);
            }
        } finally {
            end(blocking);
        }
        processDeregisterQueue();
        return processEvents(numEntries, action);
    }

    /**
     * Process changes to the interest ops.
     */
    private void processUpdateQueue() {
        assert Thread.holdsLock(this);

        synchronized (updateLock) {
            SelectionKeyImpl ski;
            while ((ski = updateKeys.pollFirst()) != null) {
                if (ski.isValid()) {
                    int fd = ski.getFDVal();
                    // add to fdToKey if needed
                    SelectionKeyImpl previous = fdToKey.putIfAbsent(fd, ski);
                    assert (previous == null) || (previous == ski);
                    int newOps = ski.translateInterestOps();
                    int registeredOps = ski.registeredEvents();
                    if (newOps != registeredOps) {
                        if (newOps == 0) {
                            // remove from epoll
                            WEPoll.ctl(eph, EPOLL_CTL_DEL, fd, 0);
                        } else {
                            int events = toEPollEvents(newOps);
                            if (registeredOps == 0) {
                                // add to epoll
                                WEPoll.ctl(eph, EPOLL_CTL_ADD, fd, events);
                            } else {
                                // modify events
                                WEPoll.ctl(eph, EPOLL_CTL_MOD, fd, events);
                            }
                        }
                        ski.registeredEvents(newOps);
                    }
                }
            }
        }
    }

    /**
     * Process the polled events.
     * If the interrupt fd has been selected, drain it and clear the interrupt.
     */
    private int processEvents(int numEntries, Consumer<SelectionKey> action)
        throws IOException
    {
        assert Thread.holdsLock(this);

        boolean interrupted = false;
        int numKeysUpdated = 0;
        for (int i = 0; i < numEntries; i++) {
            long event = WEPoll.getEvent(pollArrayAddress, i);
            int fd = WEPoll.getDescriptor(event);
            if (fd == fd0Val) {
                interrupted = true;
            } else {
                SelectionKeyImpl ski = fdToKey.get(fd);
                if (ski != null) {
                    int events = WEPoll.getEvents(event);
                    if ((events & WEPoll.EPOLLPRI) != 0) {
                        Net.discardOOB(ski.getFD());
                    }
                    int rOps = toReadyOps(events);
                    numKeysUpdated += processReadyEvents(rOps, ski, action);
                }
            }
        }

        if (interrupted) {
            clearInterrupt();
        }

        return numKeysUpdated;
    }

    @Override
    protected void implClose() throws IOException {
        assert !isOpen() && Thread.holdsLock(this);

        // prevent further wakeup
        synchronized (interruptLock) {
            interruptTriggered = true;
        }

        // close the epoll port and free resources
        WEPoll.close(eph);
        WEPoll.freePollArray(pollArrayAddress);
        pipe.sink().close();
        pipe.source().close();
    }

    @Override
    protected void implDereg(SelectionKeyImpl ski) throws IOException {
        assert !ski.isValid() && Thread.holdsLock(this);

        int fd = ski.getFDVal();
        if (fdToKey.remove(fd) != null) {
            if (ski.registeredEvents() != 0) {
                WEPoll.ctl(eph, EPOLL_CTL_DEL, fd, 0);
                ski.registeredEvents(0);
            }
        } else {
            assert ski.registeredEvents() == 0;
        }
    }

    @Override
    public void setEventOps(SelectionKeyImpl ski) {
        ensureOpen();
        synchronized (updateLock) {
            updateKeys.addLast(ski);
        }
    }

    @Override
    public Selector wakeup() {
        synchronized (interruptLock) {
            if (!interruptTriggered) {
                try {
                    IOUtil.write1(fd1Val, (byte) 0);
                } catch (IOException ioe) {
                    throw new InternalError(ioe);
                }
                interruptTriggered = true;
            }
        }
        return this;
    }

    private void clearInterrupt() throws IOException {
        synchronized (interruptLock) {
            IOUtil.drain(fd0Val);
            interruptTriggered = false;
        }
    }

    /**
     * Maps interest ops to epoll events
     */
    private static int toEPollEvents(int ops) {
        int events = EPOLLPRI;
        if ((ops & Net.POLLIN) != 0)
            events |= EPOLLIN;
        if ((ops & (Net.POLLOUT | Net.POLLCONN)) != 0)
            events |= EPOLLOUT;
        return events;
    }

    /**
     * Map epoll events to ready ops
     */
    private static int toReadyOps(int events) {
        int ops = 0;
        if ((events & WEPoll.EPOLLIN) != 0) ops |= Net.POLLIN;
        if ((events & WEPoll.EPOLLOUT) != 0) ops |= (Net.POLLOUT | Net.POLLCONN);
        if ((events & WEPoll.EPOLLHUP) != 0) ops |= Net.POLLHUP;
        if ((events & WEPoll.EPOLLERR) != 0) ops |= Net.POLLERR;
        return ops;
    }
}