/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;


import jdk.internal.misc.VM;

/**
 * Package-private utility class containing data structures and logic
 * governing the virtual-machine shutdown sequence.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 *
 * @see java.io.Console
 * @see ApplicationShutdownHooks
 * @see java.io.DeleteOnExitHook
 */

class Shutdown {

    // The system shutdown hooks are registered with a predefined slot.
    // The list of shutdown hooks is as follows:
    // (0) Console restore hook
    // (1) ApplicationShutdownHooks that invokes all registered application
    //     shutdown hooks and waits until they finish
    // (2) DeleteOnExit hook
    private static final int MAX_SYSTEM_HOOKS = 10;
    private static final Runnable[] hooks = new Runnable[MAX_SYSTEM_HOOKS];

    // the index of the currently running shutdown hook to the hooks array
    private static int currentRunningHook = -1;

    /* The preceding static fields are protected by this lock */
    private static class Lock { };
    private static Object lock = new Lock();

    /* Lock object for the native halt method */
    private static Object haltLock = new Lock();

    /**
     * Add a new system shutdown hook.  Checks the shutdown state and
     * the hook itself, but does not do any security checks.
     *
     * The registerShutdownInProgress parameter should be false except
     * registering the DeleteOnExitHook since the first file may
     * be added to the delete on exit list by the application shutdown
     * hooks.
     *
     * @param slot  the slot in the shutdown hook array, whose element
     *              will be invoked in order during shutdown
     * @param registerShutdownInProgress true to allow the hook
     *              to be registered even if the shutdown is in progress.
     * @param hook  the hook to be registered
     *
     * @throws IllegalStateException
     *         if registerShutdownInProgress is false and shutdown is in progress; or
     *         if registerShutdownInProgress is true and the shutdown process
     *         already passes the given slot
     */
    static void add(int slot, boolean registerShutdownInProgress, Runnable hook) {
        if (slot < 0 || slot >= MAX_SYSTEM_HOOKS) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        synchronized (lock) {
            if (hooks[slot] != null)
                throw new InternalError("Shutdown hook at slot " + slot + " already registered");

            if (!registerShutdownInProgress) {
                if (currentRunningHook >= 0)
                    throw new IllegalStateException("Shutdown in progress");
            } else {
                if (VM.isShutdown() || slot <= currentRunningHook)
                    throw new IllegalStateException("Shutdown in progress");
            }

            hooks[slot] = hook;
        }
    }

    /* Run all system shutdown hooks.
     *
     * The system shutdown hooks are run in the thread synchronized on
     * Shutdown.class.  Other threads calling Runtime::exit, Runtime::halt
     * or JNI DestroyJavaVM will block indefinitely.
     *
     * ApplicationShutdownHooks is registered as one single hook that starts
     * all application shutdown hooks and waits until they finish.
     */
    private static void runHooks() {
        synchronized (lock) {
            /* Guard against the possibility of a daemon thread invoking exit
             * after DestroyJavaVM initiates the shutdown sequence
             */
            if (VM.isShutdown()) return;
        }

        for (int i=0; i < MAX_SYSTEM_HOOKS; i++) {
            try {
                Runnable hook;
                synchronized (lock) {
                    // acquire the lock to make sure the hook registered during
                    // shutdown is visible here.
                    currentRunningHook = i;
                    hook = hooks[i];
                }
                if (hook != null) hook.run();
            } catch (Throwable t) {
                // ignore
            }
        }

        // set shutdown state
        VM.shutdown();
    }

    /* Notify the VM that it's time to halt. */
    static native void beforeHalt();

    /* The halt method is synchronized on the halt lock
     * to avoid corruption of the delete-on-shutdown file list.
     * It invokes the true native halt method.
     */
    static void halt(int status) {
        synchronized (haltLock) {
            halt0(status);
        }
    }

    static native void halt0(int status);

    /* Invoked by Runtime.exit, which does all the security checks.
     * Also invoked by handlers for system-provided termination events,
     * which should pass a nonzero status code.
     */
    static void exit(int status) {
        System.Logger log = getRuntimeExitLogger(); // Locate the logger without holding the lock;
        synchronized (Shutdown.class) {
            /* Synchronize on the class object, causing any other thread
             * that attempts to initiate shutdown to stall indefinitely
             */
            if (log != null) {
                Throwable throwable = new Throwable("Runtime.exit(" + status + ")");
                log.log(System.Logger.Level.DEBUG, "Runtime.exit() called with status: " + status,
                        throwable);
            }
            beforeHalt();
            runHooks();
            halt(status);
        }
    }

    /* Locate and return the logger for Shutdown.exit, if it is functional and DEBUG enabled.
     * Exceptions should not prevent System.exit; the exception is printed and otherwise ignored.
     */
    private static System.Logger getRuntimeExitLogger() {
        try {
            System.Logger log = System.getLogger("java.base.share.classes.java.lang.Runtime");
            return (log.isLoggable(System.Logger.Level.DEBUG)) ? log : null;
        } catch (Throwable throwable) {
            // Exceptions from locating the Logger are printed but do not prevent exit
            System.err.println("Runtime.exit() log finder failed with: " + throwable.getMessage());
        }
        return null;
    }


    /* Invoked by the JNI DestroyJavaVM procedure when the last non-daemon
     * thread has finished.  Unlike the exit method, this method does not
     * actually halt the VM.
     */
    static void shutdown() {
        synchronized (Shutdown.class) {
            runHooks();
        }
    }

}
