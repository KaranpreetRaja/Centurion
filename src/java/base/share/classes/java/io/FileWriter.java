/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

import java.nio.charset.Charset;

/**
 * Writes text to character files using a default buffer size. Encoding from characters
 * to bytes uses either a specified {@linkplain Charset charset}
 * or the {@linkplain Charset#defaultCharset() default charset}.
 *
 * <p>
 * Whether or not a file is available or may be created depends upon the
 * underlying platform.  Some platforms, in particular, allow a file to be
 * opened for writing by only one {@code FileWriter} (or other file-writing
 * object) at a time.  In such situations the constructors in this class
 * will fail if the file involved is already open.
 *
 * <p>
 * The {@code FileWriter} is meant for writing streams of characters. For writing
 * streams of raw bytes, consider using a {@code FileOutputStream}.
 *
 * @see OutputStreamWriter
 * @see FileOutputStream
 * @see Charset#defaultCharset()
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */

public class FileWriter extends OutputStreamWriter {

    /**
     * Constructs a {@code FileWriter} given a file name, using the
     * {@linkplain Charset#defaultCharset() default charset}
     *
     * @param fileName  String The system-dependent filename.
     * @throws IOException  if the named file exists but is a directory rather
     *                  than a regular file, does not exist but cannot be
     *                  created, or cannot be opened for any other reason
     * @see Charset#defaultCharset()
     */
    public FileWriter(String fileName) throws IOException {
        super(new FileOutputStream(fileName));
    }

    /**
     * Constructs a {@code FileWriter} given a file name and a boolean indicating
     * whether to append the data written, using the
     * {@linkplain Charset#defaultCharset() default charset}.
     *
     * @param fileName  String The system-dependent filename.
     * @param append    boolean if {@code true}, then data will be written
     *                  to the end of the file rather than the beginning.
     * @throws IOException  if the named file exists but is a directory rather
     *                  than a regular file, does not exist but cannot be
     *                  created, or cannot be opened for any other reason
     * @see Charset#defaultCharset()
     */
    public FileWriter(String fileName, boolean append) throws IOException {
        super(new FileOutputStream(fileName, append));
    }

    /**
     * Constructs a {@code FileWriter} given the {@code File} to write,
     * using the {@linkplain Charset#defaultCharset() default charset}
     *
     * @param file  the {@code File} to write.
     * @throws IOException  if the file exists but is a directory rather than
     *                  a regular file, does not exist but cannot be created,
     *                  or cannot be opened for any other reason
     * @see Charset#defaultCharset()
     */
    public FileWriter(File file) throws IOException {
        super(new FileOutputStream(file));
    }

    /**
     * Constructs a {@code FileWriter} given the {@code File} to write and
     * a boolean indicating whether to append the data written, using the
     * {@linkplain Charset#defaultCharset() default charset}.
     *
     * @param file  the {@code File} to write
     * @param     append    if {@code true}, then bytes will be written
     *                      to the end of the file rather than the beginning
     * @throws IOException  if the file exists but is a directory rather than
     *                  a regular file, does not exist but cannot be created,
     *                  or cannot be opened for any other reason
     * @see Charset#defaultCharset()
     * @since 1.4
     */
    public FileWriter(File file, boolean append) throws IOException {
        super(new FileOutputStream(file, append));
    }

    /**
     * Constructs a {@code FileWriter} given a file descriptor,
     * using the {@linkplain Charset#defaultCharset() default charset}.
     *
     * @param fd  the {@code FileDescriptor} to write.
     * @see Charset#defaultCharset()
     */
    public FileWriter(FileDescriptor fd) {
        super(new FileOutputStream(fd));
    }


    /**
     * Constructs a {@code FileWriter} given a file name and
     * {@linkplain Charset charset}.
     *
     * @param fileName  the name of the file to write
     * @param charset the {@linkplain Charset charset}
     * @throws IOException  if the named file exists but is a directory rather
     *                  than a regular file, does not exist but cannot be
     *                  created, or cannot be opened for any other reason
     *
     * @since 11
     */
    public FileWriter(String fileName, Charset charset) throws IOException {
        super(new FileOutputStream(fileName), charset);
    }

    /**
     * Constructs a {@code FileWriter} given a file name,
     * {@linkplain Charset charset} and a boolean indicating
     * whether to append the data written.
     *
     * @param fileName  the name of the file to write
     * @param charset the {@linkplain Charset charset}
     * @param append    a boolean. If {@code true}, the writer will write the data
     *                  to the end of the file rather than the beginning.
     * @throws IOException  if the named file exists but is a directory rather
     *                  than a regular file, does not exist but cannot be
     *                  created, or cannot be opened for any other reason
     *
     * @since 11
     */
    public FileWriter(String fileName, Charset charset, boolean append) throws IOException {
        super(new FileOutputStream(fileName, append), charset);
    }

    /**
     * Constructs a {@code FileWriter} given the {@code File} to write and
     * {@linkplain Charset charset}.
     *
     * @param file  the {@code File} to write
     * @param charset the {@linkplain Charset charset}
     * @throws IOException  if the file exists but is a directory rather than
     *                  a regular file, does not exist but cannot be created,
     *                  or cannot be opened for any other reason
     *
     * @since 11
     */
    public FileWriter(File file, Charset charset) throws IOException {
        super(new FileOutputStream(file), charset);
    }

    /**
     * Constructs a {@code FileWriter} given the {@code File} to write,
     * {@linkplain Charset charset} and a boolean indicating
     * whether to append the data written.
     *
     * @param file  the {@code File} to write
     * @param charset the {@linkplain Charset charset}
     * @param append    a boolean. If {@code true}, the writer will write the data
     *                  to the end of the file rather than the beginning.
     * @throws IOException  if the file exists but is a directory rather than
     *                  a regular file, does not exist but cannot be created,
     *                  or cannot be opened for any other reason
     * @since 11
     */
    public FileWriter(File file, Charset charset, boolean append) throws IOException {
        super(new FileOutputStream(file, append), charset);
    }
}
