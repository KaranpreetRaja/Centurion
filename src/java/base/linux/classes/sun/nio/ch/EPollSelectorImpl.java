/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.linux.classes.sun.nio.ch;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.base.share.classes.jdk.internal.misc.Blocker;

import static java.base.linux.classes.sun.nio.ch.EPoll.EPOLLIN;
import static java.base.linux.classes.sun.nio.ch.EPoll.EPOLL_CTL_ADD;
import static java.base.linux.classes.sun.nio.ch.EPoll.EPOLL_CTL_DEL;
import static java.base.linux.classes.sun.nio.ch.EPoll.EPOLL_CTL_MOD;


/**
 * Linux epoll based Selector implementation
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

class EPollSelectorImpl extends SelectorImpl {

    // maximum number of events to poll in one call to epoll_wait
    private static final int NUM_EPOLLEVENTS = Math.min(IOUtil.fdLimit(), 1024);

    // epoll file descriptor
    private final int epfd;

    // address of poll array when polling with epoll_wait
    private final long pollArrayAddress;

    // eventfd object used for interrupt
    private final EventFD eventfd;

    // maps file descriptor to selection key, synchronize on selector
    private final Map<Integer, SelectionKeyImpl> fdToKey = new HashMap<>();

    // pending new registrations/updates, queued by setEventOps
    private final Object updateLock = new Object();
    private final Deque<SelectionKeyImpl> updateKeys = new ArrayDeque<>();

    // interrupt triggering and clearing
    private final Object interruptLock = new Object();
    private boolean interruptTriggered;

    EPollSelectorImpl(SelectorProvider sp) throws IOException {
        super(sp);

        this.epfd = EPoll.create();
        this.pollArrayAddress = EPoll.allocatePollArray(NUM_EPOLLEVENTS);

        try {
            this.eventfd = new EventFD();
            IOUtil.configureBlocking(IOUtil.newFD(eventfd.efd()), false);
        } catch (IOException ioe) {
            EPoll.freePollArray(pollArrayAddress);
            FileDispatcherImpl.closeIntFD(epfd);
            throw ioe;
        }

        // register the eventfd object for wakeups
        EPoll.ctl(epfd, EPOLL_CTL_ADD, eventfd.efd(), EPOLLIN);
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
        boolean timedPoll = (to > 0);

        int numEntries;
        processUpdateQueue();
        processDeregisterQueue();
        try {
            begin(blocking);

            do {
                long startTime = timedPoll ? System.nanoTime() : 0;
                long comp = Blocker.begin(blocking);
                try {
                    numEntries = EPoll.wait(epfd, pollArrayAddress, NUM_EPOLLEVENTS, to);
                } finally {
                    Blocker.end(comp);
                }
                if (numEntries == IOStatus.INTERRUPTED && timedPoll) {
                    // timed poll interrupted so need to adjust timeout
                    long adjust = System.nanoTime() - startTime;
                    to -= (int) TimeUnit.NANOSECONDS.toMillis(adjust);
                    if (to <= 0) {
                        // timeout expired so no retry
                        numEntries = 0;
                    }
                }
            } while (numEntries == IOStatus.INTERRUPTED);
            assert IOStatus.check(numEntries);

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

                    int newEvents = ski.translateInterestOps();
                    int registeredEvents = ski.registeredEvents();
                    if (newEvents != registeredEvents) {
                        if (newEvents == 0) {
                            // remove from epoll
                            EPoll.ctl(epfd, EPOLL_CTL_DEL, fd, 0);
                        } else {
                            if (registeredEvents == 0) {
                                // add to epoll
                                EPoll.ctl(epfd, EPOLL_CTL_ADD, fd, newEvents);
                            } else {
                                // modify events
                                EPoll.ctl(epfd, EPOLL_CTL_MOD, fd, newEvents);
                            }
                        }
                        ski.registeredEvents(newEvents);
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
        for (int i=0; i<numEntries; i++) {
            long event = EPoll.getEvent(pollArrayAddress, i);
            int fd = EPoll.getDescriptor(event);
            if (fd == eventfd.efd()) {
                interrupted = true;
            } else {
                SelectionKeyImpl ski = fdToKey.get(fd);
                if (ski != null) {
                    int rOps = EPoll.getEvents(event);
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
        assert Thread.holdsLock(this);

        // prevent further wakeup
        synchronized (interruptLock) {
            interruptTriggered = true;
        }

        FileDispatcherImpl.closeIntFD(epfd);
        EPoll.freePollArray(pollArrayAddress);

        eventfd.close();
    }

    @Override
    protected void implDereg(SelectionKeyImpl ski) throws IOException {
        assert !ski.isValid();
        assert Thread.holdsLock(this);

        int fd = ski.getFDVal();
        if (fdToKey.remove(fd) != null) {
            if (ski.registeredEvents() != 0) {
                EPoll.ctl(epfd, EPOLL_CTL_DEL, fd, 0);
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
                    eventfd.set();
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
            eventfd.reset();
            interruptTriggered = false;
        }
    }
}
