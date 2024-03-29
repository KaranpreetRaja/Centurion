/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;

/**
 * This is the generic KeyStore exception.
 *
 * @author Jan Luehe
 *
 *
 * @since 1.2
 */

public class KeyStoreException extends GeneralSecurityException {

    @java.io.Serial
    private static final long serialVersionUID = -1119353179322377262L;

    /**
     * Constructs a {@code KeyStoreException} with no detail message.  (A
     * detail message is a {@code String} that describes this particular
     * exception.)
     */
    public KeyStoreException() {
        super();
    }

    /**
     * Constructs a {@code KeyStoreException} with the specified detail
     * message.  (A detail message is a {@code String} that describes this
     * particular exception.)
     *
     * @param msg the detail message.
     */
   public KeyStoreException(String msg) {
       super(msg);
    }

    /**
     * Creates a {@code KeyStoreException} with the specified
     * detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public KeyStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a {@code KeyStoreException} with the specified cause
     * and a detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public KeyStoreException(Throwable cause) {
        super(cause);
    }
}
