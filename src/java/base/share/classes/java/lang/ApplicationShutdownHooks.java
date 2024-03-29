/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;

/**
 * Class to track and run user level shutdown hooks registered through
 * {@link Runtime#addShutdownHook Runtime.addShutdownHook}.
 *
 * @see java.base.share.classes.java.lang.Runtime#addShutdownHook
 * @see java.base.share.classes.java.lang.Runtime#removeShutdownHook]
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */

class ApplicationShutdownHooks {
    /* The set of registered hooks */
    private static IdentityHashMap<Thread, Thread> hooks;
    static {
        try {
            Shutdown.add(1 /* shutdown hook invocation order */,
                false /* not registered if shutdown in progress */,
                new Runnable() {
                    public void run() {
                        runHooks();
                    }
                }
            );
            hooks = new IdentityHashMap<>();
        } catch (IllegalStateException e) {
            // application shutdown hooks cannot be added if
            // shutdown is in progress.
            hooks = null;
        }
    }


    private ApplicationShutdownHooks() {}

    /* Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     */
    static synchronized void add(Thread hook) {
        if(hooks == null)
            throw new IllegalStateException("Shutdown in progress");

        if (hook.isAlive())
            throw new IllegalArgumentException("Hook already running");

        if (hooks.containsKey(hook))
            throw new IllegalArgumentException("Hook previously registered");

        hooks.put(hook, hook);
    }

    /* Remove a previously-registered hook.  Like the add method, this method
     * does not do any security checks.
     */
    static synchronized boolean remove(Thread hook) {
        if(hooks == null)
            throw new IllegalStateException("Shutdown in progress");

        if (hook == null)
            throw new NullPointerException();

        return hooks.remove(hook) != null;
    }

    /* Iterates over all application hooks creating a new thread for each
     * to run in. Hooks are run concurrently and this method waits for
     * them to finish.
     */
    static void runHooks() {
        Collection<Thread> threads;
        synchronized(ApplicationShutdownHooks.class) {
            threads = hooks.keySet();
            hooks = null;
        }
        for (Thread hook : threads) {
            try {
                hook.start();
            } catch (IllegalThreadStateException ignore) {
                // already started
            } catch (RejectedExecutionException ignore) {
                // scheduler shutdown?
                assert hook.isVirtual();
            }
        }
        for (Thread hook : threads) {
            while (true) {
                try {
                    hook.join();
                    break;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
