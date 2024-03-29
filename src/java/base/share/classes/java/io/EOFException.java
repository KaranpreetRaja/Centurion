/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

/**
 * Signals that an end of file or end of stream has been reached
 * unexpectedly during input.
 * <p>
 * This exception is mainly used by data input streams to signal end of
 * stream. Note that many other input operations return a special value on
 * end of stream rather than throwing an exception.
 *
 * @see     java.base.share.classes.java.io.DataInputStream
 * @see     java.base.share.classes.java.io.IOException
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
public class EOFException extends IOException {
    @java.base.share.classes.java.io.Serial
    private static final long serialVersionUID = 6433858223774886977L;

    /**
     * Constructs an {@code EOFException} with {@code null}
     * as its error detail message.
     */
    public EOFException() {
        super();
    }

    /**
     * Constructs an {@code EOFException} with the specified detail
     * message. The string {@code s} may later be retrieved by the
     * {@link java.lang.Throwable#getMessage} method of class
     * {@code java.lang.Throwable}.
     *
     * @param   s   the detail message.
     */
    public EOFException(String s) {
        super(s);
    }
}
