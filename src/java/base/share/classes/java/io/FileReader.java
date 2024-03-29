/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

import java.nio.charset.Charset;

/**
 * Reads text from character files using a default buffer size. Decoding from bytes
 * to characters uses either a specified {@linkplain Charset charset}
 * or the {@linkplain Charset#defaultCharset() default charset}.
 *
 * <p>
 * The {@code FileReader} is meant for reading streams of characters. For reading
 * streams of raw bytes, consider using a {@code FileInputStream}.
 *
 * @see InputStreamReader
 * @see FileInputStream
 * @see Charset#defaultCharset()
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class FileReader extends InputStreamReader {

   /**
    * Creates a new {@code FileReader}, given the name of the file to read,
    * using the {@linkplain Charset#defaultCharset() default charset}.
    *
    * @param      fileName the name of the file to read
    * @throws     FileNotFoundException  if the named file does not exist,
    *             is a directory rather than a regular file,
    *             or for some other reason cannot be opened for
    *             reading.
    * @see        Charset#defaultCharset()
    */
    public FileReader(String fileName) throws FileNotFoundException {
        super(new FileInputStream(fileName));
    }

   /**
    * Creates a new {@code FileReader}, given the {@code File} to read,
    * using the {@linkplain Charset#defaultCharset() default charset}.
    *
    * @param      file the {@code File} to read
    * @throws     FileNotFoundException  if the file does not exist,
    *             is a directory rather than a regular file,
    *             or for some other reason cannot be opened for
    *             reading.
    * @see        Charset#defaultCharset()
    */
    public FileReader(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
    }

   /**
    * Creates a new {@code FileReader}, given the {@code FileDescriptor} to read,
    * using the {@linkplain Charset#defaultCharset() default charset}.
    *
    * @param fd the {@code FileDescriptor} to read
    * @see Charset#defaultCharset()
    */
    public FileReader(FileDescriptor fd) {
        super(new FileInputStream(fd));
    }

   /**
    * Creates a new {@code FileReader}, given the name of the file to read
    * and the {@linkplain Charset charset}.
    *
    * @param      fileName the name of the file to read
    * @param      charset the {@linkplain Charset charset}
    * @throws     IOException  if the named file does not exist,
    *             is a directory rather than a regular file,
    *             or for some other reason cannot be opened for
    *             reading.
    *
    * @since 11
    */
    public FileReader(String fileName, Charset charset) throws IOException {
        super(new FileInputStream(fileName), charset);
    }

   /**
    * Creates a new {@code FileReader}, given the {@code File} to read and
    * the {@linkplain Charset charset}.
    *
    * @param      file the {@code File} to read
    * @param      charset the {@linkplain Charset charset}
    * @throws     IOException  if the file does not exist,
    *             is a directory rather than a regular file,
    *             or for some other reason cannot be opened for
    *             reading.
    *
    * @since 11
    */
    public FileReader(File file, Charset charset) throws IOException {
        super(new FileInputStream(file), charset);
    }
}
