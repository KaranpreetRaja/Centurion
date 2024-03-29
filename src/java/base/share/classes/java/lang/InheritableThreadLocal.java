/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * This class extends {@code ThreadLocal} to provide inheritance of values
 * from parent thread to child thread: when a child thread is created, the
 * child receives initial values for all inheritable thread-local variables
 * for which the parent has values.  Normally the child's values will be
 * identical to the parent's; however, the child's value can be made an
 * arbitrary function of the parent's by overriding the {@code childValue}
 * method in this class.
 *
 * <p>Inheritable thread-local variables are used in preference to
 * ordinary thread-local variables when the per-thread-attribute being
 * maintained in the variable (e.g., User ID, Transaction ID) must be
 * automatically transmitted to any child threads that are created.
 *
 * <p>Note: During the creation of a new {@link
 * Thread#Thread(ThreadGroup,Runnable,String,long,boolean) thread}, it is
 * possible to <i>opt out</i> of receiving initial values for inheritable
 * thread-local variables.
 * @param <T> the type of the inheritable thread local's value
 *
 * @see     ThreadLocal
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 * @see Thread.Builder#inheritInheritableThreadLocals(boolean)
 */

public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    /**
     * Creates an inheritable thread local variable.
     */
    public InheritableThreadLocal() {}

    /**
     * Computes the child's initial value for this inheritable thread-local
     * variable as a function of the parent's value at the time the child
     * thread is created.  This method is called from within the parent
     * thread before the child is started.
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     *
     * @param parentValue the parent thread's value
     * @return the child thread's initial value
     */
    protected T childValue(T parentValue) {
        return parentValue;
    }

    /**
     * Get the map associated with a ThreadLocal.
     *
     * @param t the current thread
     */
    @Override
    ThreadLocalMap getMap(Thread t) {
        return t.inheritableThreadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the table.
     */
    @Override
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}
