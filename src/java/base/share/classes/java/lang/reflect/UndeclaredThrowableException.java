/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import jdk.internal.access.SharedSecrets;

/**
 * Thrown by a method invocation on a proxy instance if its invocation
 * handler's {@link InvocationHandler#invoke invoke} method throws a
 * checked exception (a {@code Throwable} that is not assignable
 * to {@code RuntimeException} or {@code Error}) that
 * is not assignable to any of the exception types declared in the
 * {@code throws} clause of the method that was invoked on the
 * proxy instance and dispatched to the invocation handler.
 *
 * <p>An {@code UndeclaredThrowableException} instance contains
 * the undeclared checked exception that was thrown by the invocation
 * handler, and it can be retrieved with the
 * {@code getUndeclaredThrowable()} method.
 * {@code UndeclaredThrowableException} extends
 * {@code RuntimeException}, so it is an unchecked exception
 * that wraps a checked exception.
 *
 * @author      Peter Jones
 * @see         InvocationHandler
 * @since       1.3
 */
public class UndeclaredThrowableException extends RuntimeException {
    @java.io.Serial
    static final long serialVersionUID = 330127114055056639L;

    /**
     * Constructs an {@code UndeclaredThrowableException} with the
     * specified {@code Throwable}.
     *
     * @param   undeclaredThrowable the undeclared checked exception
     *          that was thrown
     */
    public UndeclaredThrowableException(Throwable undeclaredThrowable) {
        super(null, undeclaredThrowable);  // Disallow initCause
    }

    /**
     * Constructs an {@code UndeclaredThrowableException} with the
     * specified {@code Throwable} and a detail message.
     *
     * @param   undeclaredThrowable the undeclared checked exception
     *          that was thrown
     * @param   s the detail message
     */
    public UndeclaredThrowableException(Throwable undeclaredThrowable,
                                        String s)
    {
        super(s, undeclaredThrowable);  // Disallow initCause
    }

    /**
     * Returns the {@code Throwable} instance wrapped in this
     * {@code UndeclaredThrowableException}, which may be {@code null}.
     *
     * @apiNote
     * This method predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the undeclared checked exception that was thrown
     */
    public Throwable getUndeclaredThrowable() {
        return super.getCause();
    }

    /**
     * Serializable fields for UndeclaredThrowableException.
     *
     * @serialField undeclaredThrowable Throwable the undeclared exception
     */
    @java.io.Serial
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("undeclaredThrowable", Throwable.class)
    };

    /**
     * Reconstitutes the UndeclaredThrowableException instance from a stream
     * and initialize the cause properly when deserializing from an older
     * version.
     *
     * @param  s the {@code ObjectInputStream} from which data is read
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a serialized class cannot be loaded
     */
    @java.io.Serial
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = s.readFields();
        Throwable exception = (Throwable) fields.get("undeclaredThrowable", null);
        if (exception != null) {
            SharedSecrets.getJavaLangAccess().setCause(this, exception);
        }
    }

    /**
     * To maintain compatibility with older implementation, write a serial
     * "ex" field with the cause as the value.
     *
     * @param  out the {@code ObjectOutputStream} to which data is written
     * @throws IOException if an I/O error occurs
     */
    @java.io.Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("undeclaredThrowable", super.getCause());
        out.writeFields();
    }
}
