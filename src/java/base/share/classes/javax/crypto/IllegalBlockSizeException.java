/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto;

/**
 * This exception is thrown when the length of data provided to a block
 * cipher is incorrect, i.e., does not match the block size of the cipher.
 *
 * @author Jan Luehe
 *
 * @since 1.4
 */

public class IllegalBlockSizeException
    extends java.security.GeneralSecurityException {

    @java.io.Serial
    private static final long serialVersionUID = -1965144811953540392L;

    /**
     * Constructs an {@code IllegalBlockSizeException} with no detail message.
     * A detail message is a {@code String}  that describes this particular
     * exception.
     */
    public IllegalBlockSizeException() {
        super();
    }

    /**
     * Constructs an {@code IllegalBlockSizeException} with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public IllegalBlockSizeException(String msg) {
        super(msg);
    }
}
