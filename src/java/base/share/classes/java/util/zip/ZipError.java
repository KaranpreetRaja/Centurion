/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util.zip;

/**
 * Signals that an unrecoverable error has occurred.
 *
 * @author  Dave Bristor
 * @since   1.6
 */
public class ZipError extends InternalError {
    @java.io.Serial
    private static final long serialVersionUID = 853973422266861979L;

    /**
     * Constructs a ZipError with the given detail message.
     * @param s the {@code String} containing a detail message
     */
    public ZipError(String s) {
        super(s);
    }
}
