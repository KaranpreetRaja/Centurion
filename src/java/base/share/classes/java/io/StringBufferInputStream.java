/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

/**
 * This class allows an application to create an input stream in
 * which the bytes read are supplied by the contents of a string.
 * Applications can also read bytes from a byte array by using a
 * {@code ByteArrayInputStream}.
 * <p>
 * Only the low eight bits of each character in the string are used by
 * this class.
 *
 * @see        java.base.share.classes.java.io.ByteArrayInputStream
 * @see        java.base.share.classes.java.io.StringReader
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 * @deprecated This class does not properly convert characters into bytes.  As
 *             of JDK&nbsp;1.1, the preferred way to create a stream from a
 *             string is via the {@code StringReader} class.
 */
@Deprecated
public class StringBufferInputStream extends InputStream {
    /**
     * The string from which bytes are read.
     */
    protected String buffer;

    /**
     * The index of the next character to read from the input stream buffer.
     *
     * @see        java.base.share.classes.java.io.StringBufferInputStream#buffer
     */
    protected int pos;

    /**
     * The number of valid characters in the input stream buffer.
     *
     * @see        java.base.share.classes.java.io.StringBufferInputStream#buffer
     */
    protected int count;

    /**
     * Creates a string input stream to read data from the specified string.
     *
     * @param      s   the underlying input buffer.
     */
    public StringBufferInputStream(String s) {
        this.buffer = s;
        count = s.length();
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an {@code int} in the range
     * {@code 0} to {@code 255}. If no byte is available
     * because the end of the stream has been reached, the value
     * {@code -1} is returned.
     *
     * @implSpec
     * The {@code read} method of
     * {@code StringBufferInputStream} cannot block. It returns the
     * low eight bits of the next character in this input stream's buffer.
     *
     * @return     {@inheritDoc}
     */
    @Override
    public synchronized int read() {
        return (pos < count) ? (buffer.charAt(pos++) & 0xFF) : -1;
    }

    /**
     * Reads up to {@code len} bytes of data from this input stream
     * into an array of bytes.
     * @implSpec
     * The {@code read} method of
     * {@code StringBufferInputStream} cannot block. It copies the
     * low eight bits from the characters in this input stream's buffer into
     * the byte array argument.
     *
     * @param      b     {@inheritDoc}
     * @param      off   {@inheritDoc}
     * @param      len   {@inheritDoc}
     * @return     {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public synchronized int read(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                   ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        buffer.getBytes(pos, pos + len, b, off);
        pos += len;
        return len;
    }

    /**
     * Skips {@code n} bytes of input from this input stream. Fewer
     * bytes might be skipped if the end of the input stream is reached.
     *
     * @param      n   {@inheritDoc}
     * @return     the actual number of bytes skipped.
     */
    @Override
    public synchronized long skip(long n) {
        if (n < 0) {
            return 0;
        }
        if (n > count - pos) {
            n = count - pos;
        }
        pos += (int) n;
        return n;
    }

    /**
     * Returns the number of bytes that can be read from the input
     * stream without blocking.
     *
     * @return     the value of {@code count - pos}, which is the
     *             number of bytes remaining to be read from the input buffer.
     */
    @Override
    public synchronized int available() {
        return count - pos;
    }

    /**
     * Resets the input stream to begin reading from the first character
     * of this input stream's underlying buffer.
     */
    @Override
    public synchronized void reset() {
        pos = 0;
    }
}
