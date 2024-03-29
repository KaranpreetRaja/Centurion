/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.security.auth;

/**
 * Signals that a {@code refresh} operation failed.
 *
 * <p> This exception is thrown by credentials implementing
 * the {@code Refreshable} interface when the {@code refresh}
 * method fails.
 *
 * @since 1.4
 */
public class RefreshFailedException extends Exception {

    @java.io.Serial
    private static final long serialVersionUID = 5058444488565265840L;

    /**
     * Constructs a RefreshFailedException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public RefreshFailedException() {
        super();
    }

    /**
     * Constructs a RefreshFailedException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the detail message.
     */
    public RefreshFailedException(String msg) {
        super(msg);
    }
}
