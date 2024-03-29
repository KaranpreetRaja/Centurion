/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.base.share.classes.jdk.internal.misc.InternalLock;
import java.base.share.classes.sun.nio.cs.StreamDecoder;

/**
 * An InputStreamReader is a bridge from byte streams to character streams: It
 * reads bytes and decodes them into characters using a specified {@link
 * Charset charset}.  The charset that it uses
 * may be specified by name or may be given explicitly, or the
 * {@link Charset#defaultCharset() default charset} may be used.
 *
 * <p> Each invocation of one of an InputStreamReader's read() methods may
 * cause one or more bytes to be read from the underlying byte-input stream.
 * To enable the efficient conversion of bytes to characters, more bytes may
 * be read ahead from the underlying stream than are necessary to satisfy the
 * current read operation.
 *
 * <p> For top efficiency, consider wrapping an InputStreamReader within a
 * BufferedReader.  For example:
 *
 * <pre>
 * BufferedReader in
 *   = new BufferedReader(new InputStreamReader(anInputStream));
 * </pre>
 *
 * @see BufferedReader
 * @see InputStream
 * @see Charset
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */

public class InputStreamReader extends Reader {
    private final StreamDecoder sd;

    /**
     * Return the lock object for the given reader's stream decoder.
     * If the reader type is trusted then an internal lock can be used. If the
     * reader type is not trusted then the reader object is the lock.
     */
    private static Object lockFor(InputStreamReader reader) {
        Class<?> clazz = reader.getClass();
        if (clazz == InputStreamReader.class || clazz == FileReader.class) {
            return InternalLock.newLockOr(reader);
        } else {
            return reader;
        }
    }

    /**
     * Creates an InputStreamReader that uses the
     * {@link Charset#defaultCharset() default charset}.
     *
     * @param  in   An InputStream
     *
     * @see Charset#defaultCharset()
     */
    public InputStreamReader(InputStream in) {
        super(in);
        Charset cs = Charset.defaultCharset();
        sd = StreamDecoder.forInputStreamReader(in, lockFor(this), cs);
    }

    /**
     * Creates an InputStreamReader that uses the named charset.
     *
     * @param  in
     *         An InputStream
     *
     * @param  charsetName
     *         The name of a supported {@link Charset charset}
     *
     * @throws     UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public InputStreamReader(InputStream in, String charsetName)
        throws UnsupportedEncodingException
    {
        super(in);
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        sd = StreamDecoder.forInputStreamReader(in, lockFor(this), charsetName);
    }

    /**
     * Creates an InputStreamReader that uses the given charset.
     *
     * @param  in       An InputStream
     * @param  cs       A charset
     *
     * @since 1.4
     */
    public InputStreamReader(InputStream in, Charset cs) {
        super(in);
        if (cs == null)
            throw new NullPointerException("charset");
        sd = StreamDecoder.forInputStreamReader(in, lockFor(this), cs);
    }

    /**
     * Creates an InputStreamReader that uses the given charset decoder.
     *
     * @param  in       An InputStream
     * @param  dec      A charset decoder
     *
     * @since 1.4
     */
    public InputStreamReader(InputStream in, CharsetDecoder dec) {
        super(in);
        if (dec == null)
            throw new NullPointerException("charset decoder");
        sd = StreamDecoder.forInputStreamReader(in, lockFor(this), dec);
    }

    /**
     * Returns the name of the character encoding being used by this stream.
     *
     * <p> If the encoding has an historical name then that name is returned;
     * otherwise the encoding's canonical name is returned.
     *
     * <p> If this instance was created with the {@link
     * #InputStreamReader(InputStream, String)} constructor then the returned
     * name, being unique for the encoding, may differ from the name passed to
     * the constructor. This method will return {@code null} if the
     * stream has been closed.
     * </p>
     * @return The historical name of this encoding, or
     *         {@code null} if the stream has been closed
     *
     * @see Charset
     *
     * @revised 1.4
     */
    public String getEncoding() {
        return sd.getEncoding();
    }

    public int read(CharBuffer target) throws IOException {
        return sd.read(target);
    }

    /**
     * Reads a single character.
     *
     * @return The character read, or -1 if the end of the stream has been
     *         reached
     *
     * @throws     IOException  If an I/O error occurs
     */
    public int read() throws IOException {
        return sd.read();
    }

    /**
     * {@inheritDoc}
     * @throws     IndexOutOfBoundsException  {@inheritDoc}
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        return sd.read(cbuf, off, len);
    }

    /**
     * Tells whether this stream is ready to be read.  An InputStreamReader is
     * ready if its input buffer is not empty, or if bytes are available to be
     * read from the underlying byte stream.
     *
     * @throws     IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
        return sd.ready();
    }

    public void close() throws IOException {
        sd.close();
    }
}
