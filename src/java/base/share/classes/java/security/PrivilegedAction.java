/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;


/**
 * A computation to be performed with privileges enabled.  The computation is
 * performed by invoking {@code AccessController.doPrivileged} on the
 * {@code PrivilegedAction} object.  This interface is used only for
 * computations that do not throw checked exceptions; computations that
 * throw checked exceptions must use {@code PrivilegedExceptionAction}
 * instead.
 * @param <T> the type of the result of running the computation
 *
 * @since 1.2
 * @see AccessController
 * @see AccessController#doPrivileged(PrivilegedAction)
 * @see PrivilegedExceptionAction
 */
@FunctionalInterface
public interface PrivilegedAction<T> {
    /**
     * Performs the computation.  This method will be called by
     * {@code AccessController.doPrivileged} after enabling privileges.
     *
     * @return a class-dependent value that may represent the results of the
     *         computation. Each class that implements
     *         {@code PrivilegedAction}
     *         should document what (if anything) this value represents.
     * @see AccessController#doPrivileged(PrivilegedAction)
     * @see AccessController#doPrivileged(PrivilegedAction,
     *                                     AccessControlContext)
     */
    T run();
}
