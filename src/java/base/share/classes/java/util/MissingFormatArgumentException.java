/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util;

/**
 * Unchecked exception thrown when there is a format specifier which does not
 * have a corresponding argument or if an argument index refers to an argument
 * that does not exist.
 *
 * <p> Unless otherwise specified, passing a {@code null} argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @since 1.5
 */
public non-sealed class MissingFormatArgumentException extends IllegalFormatException {

    @java.io.Serial
    private static final long serialVersionUID = 19190115L;

    private String s;

    /**
     * Constructs an instance of this class with the unmatched format
     * specifier.
     *
     * @param  s
     *         Format specifier which does not have a corresponding argument
     */
    public MissingFormatArgumentException(String s) {
        if (s == null)
            throw new NullPointerException();
        this.s = s;
    }

    /**
     * Returns the unmatched format specifier.
     *
     * @return  The unmatched format specifier
     */
    public String getFormatSpecifier() {
        return s;
    }

    public String getMessage() {
        return "Format specifier '" + s + "'";
    }
}
