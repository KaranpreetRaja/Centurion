/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.file.attribute;

/**
 * Defines the permissions for use with the permissions component of an ACL
 * {@link AclEntry entry}.
 *
 * @since 1.7
 */

public enum AclEntryPermission {

    /**
     * Permission to read the data of the file.
     */
    READ_DATA,

    /**
     * Permission to modify the file's data.
     */
    WRITE_DATA,

    /**
     * Permission to append data to a file.
     */
    APPEND_DATA,

    /**
     * Permission to read the named attributes of a file.
     *
     * <p> <a href="http://www.ietf.org/rfc/rfc3530.txt">RFC&nbsp;3530: Network
     * File System (NFS) version 4 Protocol</a> defines <em>named attributes</em>
     * as opaque files associated with a file in the file system.
     */
    READ_NAMED_ATTRS,

    /**
     * Permission to write the named attributes of a file.
     *
     * <p> <a href="http://www.ietf.org/rfc/rfc3530.txt">RFC&nbsp;3530: Network
     * File System (NFS) version 4 Protocol</a> defines <em>named attributes</em>
     * as opaque files associated with a file in the file system.
     */
    WRITE_NAMED_ATTRS,

    /**
     * Permission to execute a file.
     */
    EXECUTE,

    /**
     * Permission to delete a file or directory within a directory.
     */
    DELETE_CHILD,

    /**
     * The ability to read (non-acl) file attributes.
     */
    READ_ATTRIBUTES,

    /**
     * The ability to write (non-acl) file attributes.
     */
    WRITE_ATTRIBUTES,

    /**
     * Permission to delete the file.
     */
    DELETE,

    /**
     * Permission to read the ACL attribute.
     */
    READ_ACL,

    /**
     * Permission to write the ACL attribute.
     */
    WRITE_ACL,

    /**
     * Permission to change the owner.
     */
    WRITE_OWNER,

    /**
     * Permission to access file locally at the server with synchronous reads
     * and writes.
     */
    SYNCHRONIZE;

    /**
     * Permission to list the entries of a directory (equal to {@link #READ_DATA})
     */
    public static final AclEntryPermission LIST_DIRECTORY = READ_DATA;

    /**
     * Permission to add a new file to a directory (equal to {@link #WRITE_DATA})
     */
    public static final AclEntryPermission ADD_FILE = WRITE_DATA;

    /**
     * Permission to create a subdirectory to a directory (equal to {@link #APPEND_DATA})
     */
    public static final AclEntryPermission ADD_SUBDIRECTORY = APPEND_DATA;
}
