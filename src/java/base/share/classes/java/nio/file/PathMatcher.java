/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.file;

/**
 * An interface that is implemented by objects that perform match operations on
 * paths.
 *
 * @since 1.7
 *
 * @see FileSystem#getPathMatcher
 * @see Files#newDirectoryStream(Path,String)
 */
@FunctionalInterface
public interface PathMatcher {
    /**
     * Tells if given path matches this matcher's pattern.
     *
     * @param   path
     *          the path to match
     *
     * @return  {@code true} if, and only if, the path matches this
     *          matcher's pattern
     */
    boolean matches(Path path);
}
