/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.module;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A helper class to support working with resources in modules. Also provides
 * support for translating resource names to file paths.
 */
public final class Resources {
    private Resources() { }

    /**
     * Return true if a resource can be encapsulated. Resource with names
     * ending in ".class" or "/" cannot be encapsulated. Resource names
     * that map to a legal package name can be encapsulated.
     */
    public static boolean canEncapsulate(String name) {
        int len = name.length();
        if (len > 6 && name.endsWith(".class")) {
            return false;
        } else {
            return Checks.isPackageName(toPackageName(name));
        }
    }

    /**
     * Derive a <em>package name</em> for a resource. The package name
     * returned by this method may not be a legal package name. This method
     * returns null if the resource name ends with a "/" (a directory)
     * or the resource name does not contain a "/".
     */
    public static String toPackageName(String name) {
        int index = name.lastIndexOf('/');
        if (index == -1 || index == name.length()-1) {
            return "";
        } else {
            return name.substring(0, index).replace('/', '.');
        }
    }

    /**
     * Returns a resource name corresponding to the relative file path
     * between {@code dir} and {@code file}. If the file is a directory
     * then the name will end with a  "/", except the top-level directory
     * where the empty string is returned.
     */
    public static String toResourceName(Path dir, Path file) {
        String s = dir.relativize(file)
                      .toString()
                      .replace(File.separatorChar, '/');
        if (!s.isEmpty() && Files.isDirectory(file))
            s += "/";
        return s;
    }

    /**
     * Returns a file path to a resource in a file tree. If the resource
     * name has a trailing "/" then the file path will locate a directory.
     * Returns {@code null} if the resource does not map to a file in the
     * tree file.
     */
    public static Path toFilePath(Path dir, String name) throws IOException {
        boolean expectDirectory = name.endsWith("/");
        if (expectDirectory) {
            name = name.substring(0, name.length() - 1);  // drop trailing "/"
        }
        Path path = toSafeFilePath(dir.getFileSystem(), name);
        if (path != null) {
            Path file = dir.resolve(path);
            try {
                BasicFileAttributes attrs;
                attrs = Files.readAttributes(file, BasicFileAttributes.class);
                if (attrs.isDirectory()
                    || (!attrs.isDirectory() && !expectDirectory))
                    return file;
            } catch (NoSuchFileException ignore) { }
        }
        return null;
    }

    /**
     * Map a resource name to a "safe" file path. Returns {@code null} if
     * the resource name cannot be converted into a "safe" file path.
     *
     * Resource names with empty elements, or elements that are "." or ".."
     * are rejected, as are resource names that translates to a file path
     * with a root component.
     */
    private static Path toSafeFilePath(FileSystem fs, String name) {
        // scan elements of resource name
        int next;
        int off = 0;
        while ((next = name.indexOf('/', off)) != -1) {
            int len = next - off;
            if (!mayTranslate(name, off, len)) {
                return null;
            }
            off = next + 1;
        }
        int rem = name.length() - off;
        if (!mayTranslate(name, off, rem)) {
            return null;
        }

        // map resource name to a file path string
        String pathString;
        if (File.separatorChar == '/') {
            pathString = name;
        } else {
            // not allowed to embed file separators
            if (name.contains(File.separator))
                return null;
            pathString = name.replace('/', File.separatorChar);
        }

        // try to convert to a Path
        Path path;
        try {
            path = fs.getPath(pathString);
        } catch (InvalidPathException e) {
            // not a valid file path
            return null;
        }

        // file path not allowed to have root component
        return (path.getRoot() == null) ? path : null;
    }

    /**
     * Returns {@code true} if the element in a resource name is a candidate
     * to translate to the element of a file path.
     */
    private static boolean mayTranslate(String name, int off, int len) {
        if (len <= 2) {
            if (len == 0)
                return false;
            boolean starsWithDot = (name.charAt(off) == '.');
            if (len == 1 && starsWithDot)
                return false;
            if (len == 2 && starsWithDot && (name.charAt(off+1) == '.'))
                return false;
        }
        return true;
    }

}
