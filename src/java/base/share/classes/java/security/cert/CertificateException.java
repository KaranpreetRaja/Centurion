/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.cert;

import java.security.GeneralSecurityException;

/**
 * This exception indicates one of a variety of certificate problems.
 *
 * @author Hemma Prafullchandra
 * @since 1.2
 * @see Certificate
 */
public class CertificateException extends GeneralSecurityException {

    @java.io.Serial
    private static final long serialVersionUID = 3192535253797119798L;

    /**
     * Constructs a certificate exception with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public CertificateException() {
        super();
    }

    /**
     * Constructs a certificate exception with the given detail
     * message. A detail message is a String that describes this
     * particular exception.
     *
     * @param msg the detail message.
     */
    public CertificateException(String msg) {
        super(msg);
    }

    /**
     * Creates a {@code CertificateException} with the specified
     * detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public CertificateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a {@code CertificateException} with the specified cause
     * and a detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public CertificateException(Throwable cause) {
        super(cause);
    }
}
