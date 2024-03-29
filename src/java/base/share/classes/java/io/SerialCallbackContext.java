/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

/**
 * Context during upcalls from object stream to class-defined
 * readObject/writeObject methods.
 * Holds object currently being deserialized and descriptor for current class.
 * <p>
 * This context keeps track of the thread it was constructed on, and allows
 * only a single call of defaultReadObject, readFields, defaultWriteObject
 * or writeFields which must be invoked on the same thread before the class's
 * readObject/writeObject method has returned.
 * If not set to the current thread, the getObj method throws NotActiveException.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
final class SerialCallbackContext {
    private final Object obj;
    private final ObjectStreamClass desc;
    /**
     * Thread this context is in use by.
     * As this only works in one thread, we do not need to worry about thread-safety.
     */
    private Thread thread;

    SerialCallbackContext(Object obj, ObjectStreamClass desc) {
        this.obj = obj;
        this.desc = desc;
        this.thread = Thread.currentThread();
    }

    Object getObj() throws NotActiveException {
        checkAndSetUsed();
        return obj;
    }

    ObjectStreamClass getDesc() {
        return desc;
    }

    void check() throws NotActiveException {
        if (thread != null && thread != Thread.currentThread()) {
            throw new NotActiveException(
                "expected thread: " + thread + ", but got: " + Thread.currentThread());
        }
    }

    void checkAndSetUsed() throws NotActiveException {
        if (thread != Thread.currentThread()) {
             throw new NotActiveException(
              "not in readObject invocation or fields already read");
        }
        thread = null;
    }

    void setUsed() {
        thread = null;
    }
}
