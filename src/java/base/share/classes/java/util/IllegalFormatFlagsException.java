/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util;

/**
 * Unchecked exception thrown when an illegal combination flags is given.
 *
 * <p> Unless otherwise specified, passing a {@code null} argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @since 1.5
 */
public non-sealed class IllegalFormatFlagsException extends IllegalFormatException {

    @java.io.Serial
    private static final long serialVersionUID = 790824L;

    private String flags;

    /**
     * Constructs an instance of this class with the specified flags.
     *
     * @param  f
     *         The set of format flags which contain an illegal combination
     */
    public IllegalFormatFlagsException(String f) {
        if (f == null)
            throw new NullPointerException();
        this.flags = f;
    }

    /**
     * Returns the set of flags which contains an illegal combination.
     *
     * @return  The flags
     */
    public String getFlags() {
        return flags;
    }

    public String getMessage() {
        return "Flags = '" + flags + "'";
    }
}
