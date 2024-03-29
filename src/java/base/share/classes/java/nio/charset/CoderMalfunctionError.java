/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.charset;


/**
 * Error thrown when the {@link CharsetDecoder#decodeLoop decodeLoop} method of
 * a {@link CharsetDecoder}, or the {@link CharsetEncoder#encodeLoop
 * encodeLoop} method of a {@link CharsetEncoder}, throws an unexpected
 * exception.
 *
 * @since 1.4
 */

public class CoderMalfunctionError
    extends Error
{

    @java.io.Serial
    private static final long serialVersionUID = -1151412348057794301L;

    /**
     * Initializes an instance of this class.
     *
     * @param  cause
     *         The unexpected exception that was thrown
     */
    public CoderMalfunctionError(Exception cause) {
        super(cause);
    }

}
