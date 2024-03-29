/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

/**
 * ObjectOutput extends the DataOutput interface to include writing of objects.
 * DataOutput includes methods for output of primitive types, ObjectOutput
 * extends that interface to include objects, arrays, and Strings.
 *
 * @see java.base.share.classes.java.io.InputStream
 * @see java.base.share.classes.java.io.ObjectOutputStream
 * @see java.base.share.classes.java.io.ObjectInputStream
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public interface ObjectOutput extends DataOutput, AutoCloseable {
    /**
     * Write an object to the underlying storage or stream.  The
     * class that implements this interface defines how the object is
     * written.
     *
     * @param     obj the object to be written
     * @throws    IOException Any of the usual Input/Output related exceptions.
     */
    public void writeObject(Object obj)
      throws IOException;

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     * @param     b the byte
     * @throws    IOException If an I/O error has occurred.
     */
    public void write(int b) throws IOException;

    /**
     * Writes an array of bytes. This method will block until the bytes
     * are actually written.
     * @param     b the data to be written
     * @throws    IOException If an I/O error has occurred.
     */
    public void write(byte[] b) throws IOException;

    /**
     * Writes a sub array of bytes.
     * @param     b the data to be written
     * @param     off       the start offset in the data
     * @param     len       the number of bytes that are written
     * @throws    IOException If an I/O error has occurred.
     * @throws    IndexOutOfBoundsException {@inheritDoc}
     */
    public void write(byte[] b, int off, int len) throws IOException;

    /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     * @throws    IOException If an I/O error has occurred.
     */
    public void flush() throws IOException;

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @throws    IOException If an I/O error has occurred.
     */
    public void close() throws IOException;
}
