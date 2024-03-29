/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util;

/**
 * Unchecked exception thrown when the format width is required.
 *
 * <p> Unless otherwise specified, passing a {@code null} argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @since 1.5
 */
public non-sealed class MissingFormatWidthException extends IllegalFormatException {

    @java.io.Serial
    private static final long serialVersionUID = 15560123L;

    private String s;

    /**
     * Constructs an instance of this class with the specified format
     * specifier.
     *
     * @param  s
     *         The format specifier which does not have a width
     */
    public MissingFormatWidthException(String s) {
        if (s == null)
            throw new NullPointerException();
        this.s = s;
    }

    /**
     * Returns the format specifier which does not have a width.
     *
     * @return  The format specifier which does not have a width
     */
    public String getFormatSpecifier() {
        return s;
    }

    public String getMessage() {
        return s;
    }
}
