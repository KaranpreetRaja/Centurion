/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.file;

import java.base.share.classes.java.nio.file.spi.FileSystemProvider;
import java.net.URI;

/**
 * This class consists exclusively of static methods that return a {@link Path}
 * by converting a path string or {@link URI}.
 *
 * @apiNote
 * It is recommended to obtain a {@code Path} via the {@code Path.of} methods
 * instead of via the {@code get} methods defined in this class as this class
 * may be deprecated in a future release.
 *
 * @since 1.7
 * @see Path
 */

public final class Paths {
    private Paths() { }

    /**
     * Converts a path string, or a sequence of strings that when joined form
     * a path string, to a {@code Path}.
     *
     * @implSpec
     * This method simply invokes {@link Path#of(String,String...)
     * Path.of(String, String...)} with the given parameters.
     *
     * @param   first
     *          the path string or initial part of the path string
     * @param   more
     *          additional strings to be joined to form the path string
     *
     * @return  the resulting {@code Path}
     *
     * @throws  InvalidPathException
     *          if the path string cannot be converted to a {@code Path}
     *
     * @see FileSystem#getPath
     * @see Path#of(String,String...)
     */
    public static Path get(String first, String... more) {
        return Path.of(first, more);
    }

    /**
     * Converts the given URI to a {@link Path} object.
     *
     * @implSpec
     * This method simply invokes {@link Path#of(URI) Path.of(URI)} with the
     * given parameter.
     *
     * @param   uri
     *          the URI to convert
     *
     * @return  the resulting {@code Path}
     *
     * @throws  IllegalArgumentException
     *          if preconditions on the {@code uri} parameter do not hold. The
     *          format of the URI is provider specific.
     * @throws  FileSystemNotFoundException
     *          The file system, identified by the URI, does not exist and
     *          cannot be created automatically, or the provider identified by
     *          the URI's scheme component is not installed
     * @throws  SecurityException
     *          if a security manager is installed and it denies an unspecified
     *          permission to access the file system
     *
     * @see Path#of(URI)
     */
    public static Path get(URI uri) {
        return Path.of(uri);
    }
}
