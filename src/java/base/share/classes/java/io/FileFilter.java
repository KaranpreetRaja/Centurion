/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;


/**
 * A filter for abstract pathnames.
 *
 * <p> Instances of this interface may be passed to the
 * {@link File#listFiles(java.base.share.classes.java.io.FileFilter) listFiles(FileFilter)} method
 * of the {@link java.base.share.classes.java.io.File} class.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
@FunctionalInterface
public interface FileFilter {

    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     *
     * @param  pathname  The abstract pathname to be tested
     * @return  {@code true} if and only if {@code pathname}
     *          should be included
     */
    boolean accept(File pathname);
}
