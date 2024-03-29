/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.file;

/**
 * Checked exception thrown when a file system loop, or cycle, is encountered.
 *
 * @since 1.7
 * @see Files#walkFileTree
 */

public class FileSystemLoopException
    extends FileSystemException
{
    @java.io.Serial
    private static final long serialVersionUID = 4843039591949217617L;

    /**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file causing the cycle or {@code null} if
     *          not known
     */
    public FileSystemLoopException(String file) {
        super(file);
    }
}
