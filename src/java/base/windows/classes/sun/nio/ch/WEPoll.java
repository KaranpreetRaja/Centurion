/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.ch;

import java.io.IOException;
import jdk.internal.misc.Unsafe;

/**
 * Provides access to wepoll.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023 
 */
class WEPoll {
    private static final Unsafe UNSAFE = Unsafe.getUnsafe();
    private static final int ADDRESS_SIZE = UNSAFE.addressSize();

    private WEPoll() { }

    /**
     * typedef union epoll_data {
     *     void *ptr;
     *     int fd;
     *     uint32_t u32;
     *     uint64_t u64;
     *     SOCKET sock;   // Windows specific
     *     HANDLE hnd;    // Windows specific
     *  } epoll_data_t;
     *
     * struct epoll_event {
     *     uint32_t events;
     *     epoll_data_t data;
     * }
     */
    static {
        IOUtil.load();
    }
    private static final int SIZEOF_EPOLLEVENT   = eventSize();
    private static final int OFFSETOF_EVENTS     = eventsOffset();
    private static final int OFFSETOF_SOCK       = dataOffset();

    // opcodes
    static final int EPOLL_CTL_ADD  = 1;
    static final int EPOLL_CTL_MOD  = 2;
    static final int EPOLL_CTL_DEL  = 3;

    // events
    static final int EPOLLIN   = (1 << 0);
    static final int EPOLLPRI  = (1 << 1);
    static final int EPOLLOUT  = (1 << 2);
    static final int EPOLLERR  = (1 << 3);
    static final int EPOLLHUP  = (1 << 4);

    // flags
    static final int EPOLLONESHOT = (1 << 31);

    /**
     * Allocates a poll array to handle up to {@code count} events.
     */
    static long allocatePollArray(int count) {
        long size = (long) count * SIZEOF_EPOLLEVENT;
        long base = UNSAFE.allocateMemory(size);
        UNSAFE.setMemory(base, size, (byte) 0);
        return base;
    }

    /**
     * Free a poll array
     */
    static void freePollArray(long address) {
        UNSAFE.freeMemory(address);
    }

    /**
     * Returns event[i];
     */
    static long getEvent(long address, int i) {
        return address + (SIZEOF_EPOLLEVENT*i);
    }

    /**
     * Returns event->data.socket
     */
    static long getSocket(long eventAddress) {
        if (ADDRESS_SIZE == 8) {
            return UNSAFE.getLong(eventAddress + OFFSETOF_SOCK);
        } else {
            return UNSAFE.getInt(eventAddress + OFFSETOF_SOCK);
        }
    }

    /**
     * Return event->data.socket as an int file descriptor
     */
    static int getDescriptor(long eventAddress) {
        long s = getSocket(eventAddress);
        int fd = (int) s;
        assert ((long) fd) == s;
        return fd;
    }

    /**
     * Returns event->events
     */
    static int getEvents(long eventAddress) {
        return UNSAFE.getInt(eventAddress + OFFSETOF_EVENTS);
    }

    // -- Native methods --

    private static native int eventSize();

    private static native int eventsOffset();

    private static native int dataOffset();

    static native long create() throws IOException;

    static native int ctl(long h, int opcode, long s, int events);

    static native int wait(long h, long pollAddress, int numfds, int timeout)
        throws IOException;

    static native void close(long h);
}