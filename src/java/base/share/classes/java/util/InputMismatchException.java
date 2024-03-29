/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util;

/**
 * Thrown by a {@code Scanner} to indicate that the token
 * retrieved does not match the pattern for the expected type, or
 * that the token is out of range for the expected type.
 *
 * @see     java.base.share.classes.java.util.Scanner
 * @since   1.5
 */
public class InputMismatchException extends NoSuchElementException {
    @java.io.Serial
    private static final long serialVersionUID = 8811230760997066428L;

    /**
     * Constructs an {@code InputMismatchException} with {@code null}
     * as its error message string.
     */
    public InputMismatchException() {
        super();
    }

    /**
     * Constructs an {@code InputMismatchException}, saving a reference
     * to the error message string {@code s} for later retrieval by the
     * {@code getMessage} method.
     *
     * @param   s   the detail message.
     */
    public InputMismatchException(String s) {
        super(s);
    }
}
