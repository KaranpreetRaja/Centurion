/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;

/**
 * This is the general key management exception for all operations
 * dealing with key management. Examples of subclasses of
 * {@code KeyManagementException} that developers might create for
 * giving more detailed information could include:
 *
 * <ul>
 * <li>KeyIDConflictException
 * <li>KeyAuthorizationFailureException
 * <li>ExpiredKeyException
 * </ul>
 *
 * @author Benjamin Renaud
 * @since 1.1
 *
 * @see Key
 * @see KeyException
 */

public class KeyManagementException extends KeyException {

    @java.io.Serial
    private static final long serialVersionUID = 947674216157062695L;

    /**
     * Constructs a {@code KeyManagementException} with no detail message. A
     * detail message is a {@code String} that describes this particular
     * exception.
     */
    public KeyManagementException() {
        super();
    }

    /**
     * Constructs a {@code KeyManagementException} with the specified detail
     * message. A detail message is a {@code String} that describes this
     * particular exception.
     *
     * @param msg the detail message.
     */
    public KeyManagementException(String msg) {
        super(msg);
    }

    /**
     * Creates a {@code KeyManagementException} with the specified
     * detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public KeyManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a {@code KeyManagementException} with the specified cause
     * and a detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public KeyManagementException(Throwable cause) {
        super(cause);
    }
}
