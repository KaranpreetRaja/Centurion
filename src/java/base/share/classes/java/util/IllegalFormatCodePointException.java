/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util;

/**
 * Unchecked exception thrown when a character with an invalid Unicode code
 * point as defined by {@link Character#isValidCodePoint} is passed to the
 * {@link Formatter}.
 *
 * <p> Unless otherwise specified, passing a {@code null} argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @since 1.5
 */
public non-sealed class IllegalFormatCodePointException extends IllegalFormatException {

    @java.io.Serial
    private static final long serialVersionUID = 19080630L;

    private int c;

    /**
     * Constructs an instance of this class with the specified illegal code
     * point as defined by {@link Character#isValidCodePoint}.
     *
     * @param  c
     *         The illegal Unicode code point
     */
    public IllegalFormatCodePointException(int c) {
        this.c = c;
    }

    /**
     * Returns the illegal code point as defined by {@link
     * Character#isValidCodePoint}.
     *
     * @return  The illegal Unicode code point
     */
    public int getCodePoint() {
        return c;
    }

    public String getMessage() {
        return String.format("Code point = %#x", c);
    }
}
