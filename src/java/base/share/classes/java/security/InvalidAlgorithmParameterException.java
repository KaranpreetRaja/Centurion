/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;

/**
 * This is the exception for invalid or inappropriate algorithm parameters.
 *
 * @author Jan Luehe
 *
 *
 * @see AlgorithmParameters
 * @see java.base.share.classes.java.security.spec.AlgorithmParameterSpec
 *
 * @since 1.2
 */

public class InvalidAlgorithmParameterException
extends GeneralSecurityException {

    @java.io.Serial
    private static final long serialVersionUID = 2864672297499471472L;

    /**
     * Constructs an {@code InvalidAlgorithmParameterException} with no detail
     * message.
     * A detail message is a {@code String} that describes this particular
     * exception.
     */
    public InvalidAlgorithmParameterException() {
        super();
    }

    /**
     * Constructs an {@code InvalidAlgorithmParameterException} with the
     * specified detail message.
     * A detail message is a {@code String} that describes this
     * particular exception.
     *
     * @param msg the detail message.
     */
    public InvalidAlgorithmParameterException(String msg) {
        super(msg);
    }

    /**
     * Creates an {@code InvalidAlgorithmParameterException} with the
     * specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public InvalidAlgorithmParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an {@code InvalidAlgorithmParameterException} with the
     * specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public InvalidAlgorithmParameterException(Throwable cause) {
        super(cause);
    }
}
