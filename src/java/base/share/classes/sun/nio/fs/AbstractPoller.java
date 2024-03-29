/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.fs;

import java.nio.file.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.util.*;

/**
 * Base implementation of background poller thread used in watch service
 * implementations. A poller thread waits on events from the file system and
 * also services "requests" from clients to register for new events or cancel
 * existing registrations.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

abstract class AbstractPoller implements Runnable {

    // requests pending to the poller thread
    private final ArrayDeque<Request> requests;

    // set to true when shutdown
    private boolean shutdown;

    protected AbstractPoller() {
        this.requests = new ArrayDeque<>();
        this.shutdown = false;
    }

    /**
     * Starts the poller thread
     */
    @SuppressWarnings("removal")
    public void start() {
        final Runnable thisRunnable = this;
        AccessController.doPrivileged(new PrivilegedAction<>() {
            @Override
            public Object run() {
                Thread thr = new Thread(null,
                                        thisRunnable,
                                        "FileSystemWatchService",
                                        0,
                                        false);
                thr.setDaemon(true);
                thr.start();
                return null;
            }
         });
    }

    /**
     * Wakeup poller thread so that it can service pending requests
     */
    abstract void wakeup() throws IOException;

    /**
     * Executed by poller thread to register directory for changes
     */
    abstract Object implRegister(Path path,
                                 Set<? extends WatchEvent.Kind<?>> events,
                                 WatchEvent.Modifier... modifiers);

    /**
     * Executed by poller thread to cancel key
     */
    abstract void implCancelKey(WatchKey key);

    /**
     * Executed by poller thread to shutdown and cancel all keys
     */
    abstract void implCloseAll();

    /**
     * Requests, and waits on, poller thread to register given file.
     */
    final WatchKey register(Path dir,
                            WatchEvent.Kind<?>[] events,
                            WatchEvent.Modifier... modifiers)
        throws IOException
    {
        // validate arguments before request to poller
        if (dir == null)
            throw new NullPointerException();
        Set<WatchEvent.Kind<?>> eventSet = HashSet.newHashSet(events.length);
        for (WatchEvent.Kind<?> event: events) {
            // standard events
            if (event == StandardWatchEventKinds.ENTRY_CREATE ||
                event == StandardWatchEventKinds.ENTRY_MODIFY ||
                event == StandardWatchEventKinds.ENTRY_DELETE)
            {
                eventSet.add(event);
                continue;
            }

            // OVERFLOW is ignored
            if (event == StandardWatchEventKinds.OVERFLOW)
                continue;

            // null/unsupported
            if (event == null)
                throw new NullPointerException("An element in event set is 'null'");
            throw new UnsupportedOperationException(event.name());
        }
        if (eventSet.isEmpty())
            throw new IllegalArgumentException("No events to register");
        return (WatchKey)invoke(RequestType.REGISTER, dir, eventSet, modifiers);
    }

    /**
     * Cancels, and waits on, poller thread to cancel given key.
     */
    final void cancel(WatchKey key) {
        try {
            invoke(RequestType.CANCEL, key);
        } catch (IOException x) {
            // should not happen
            throw new AssertionError(x.getMessage());
        }
    }

    /**
     * Shutdown poller thread
     */
    final void close() throws IOException {
        invoke(RequestType.CLOSE);
    }

    /**
     * Types of request that the poller thread must handle
     */
    private static enum RequestType {
        REGISTER,
        CANCEL,
        CLOSE;
    }

    /**
     * Encapsulates a request (command) to the poller thread.
     */
    private static class Request {
        private final RequestType type;
        private final Object[] params;

        private boolean completed = false;
        private Object result = null;

        Request(RequestType type, Object... params) {
            this.type = type;
            this.params = params;
        }

        RequestType type() {
            return type;
        }

        Object[] parameters() {
            return params;
        }

        void release(Object result) {
            synchronized (this) {
                this.completed = true;
                this.result = result;
                notifyAll();
            }
        }

        /**
         * Await completion of the request. The return value is the result of
         * the request.
         */
        Object awaitResult() {
            boolean interrupted = false;
            synchronized (this) {
                while (!completed) {
                    try {
                        wait();
                    } catch (InterruptedException x) {
                        interrupted = true;
                    }
                }
                if (interrupted)
                    Thread.currentThread().interrupt();
                return result;
            }
        }
    }

    /**
     * Enqueues request to poller thread and waits for result
     */
    private Object invoke(RequestType type, Object... params) throws IOException {
        // submit request
        Request req = new Request(type, params);
        synchronized (requests) {
            if (shutdown) {
                throw new ClosedWatchServiceException();
            }
            requests.add(req);

            // wakeup thread
            wakeup();
        }

        // wait for result
        Object result = req.awaitResult();

        if (result instanceof RuntimeException)
            throw (RuntimeException)result;
        if (result instanceof IOException )
            throw (IOException)result;
        return result;
    }

    /**
     * Invoked by poller thread to process all pending requests
     *
     * @return  true if poller thread should shutdown
     */
    @SuppressWarnings("unchecked")
    boolean processRequests() {
        synchronized (requests) {
            Request req;
            while ((req = requests.poll()) != null) {
                // if in process of shutdown then reject request
                if (shutdown) {
                    req.release(new ClosedWatchServiceException());
                    continue;
                }

                switch (req.type()) {
                    /**
                     * Register directory
                     */
                    case REGISTER: {
                        Object[] params = req.parameters();
                        Path path = (Path)params[0];
                        Set<? extends WatchEvent.Kind<?>> events =
                            (Set<? extends WatchEvent.Kind<?>>)params[1];
                        WatchEvent.Modifier[] modifiers =
                            (WatchEvent.Modifier[])params[2];
                        req.release(implRegister(path, events, modifiers));
                        break;
                    }
                    /**
                     * Cancel existing key
                     */
                    case CANCEL : {
                        Object[] params = req.parameters();
                        WatchKey key = (WatchKey)params[0];
                        implCancelKey(key);
                        req.release(null);
                        break;
                    }
                    /**
                     * Close watch service
                     */
                    case CLOSE: {
                        implCloseAll();
                        req.release(null);
                        shutdown = true;
                        break;
                    }

                    default:
                        req.release(new IOException("request not recognized"));
                }
            }
        }
        return shutdown;
    }
}
