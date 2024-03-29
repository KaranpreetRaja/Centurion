/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.file;

/**
 * Checked exception thrown when a file system operation fails because a file
 * is not a symbolic link.
 *
 * @since 1.7
 */

public class NotLinkException
    extends FileSystemException
{
    @java.io.Serial
    static final long serialVersionUID = -388655596416518021L;

    /**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file or {@code null} if not known
     */
    public NotLinkException(String file) {
        super(file);
    }

    /**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file or {@code null} if not known
     * @param   other
     *          a string identifying the other file or {@code null} if not known
     * @param   reason
     *          a reason message with additional information or {@code null}
     */
    public NotLinkException(String file, String other, String reason) {
        super(file, other, reason);
    }
}
