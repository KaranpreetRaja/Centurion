/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.fs;

import java.nio.file.InvalidPathException;

/**
 * A parser of Windows path strings
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023
 */

class WindowsPathParser {
    private WindowsPathParser() { }

    /**
     * The result of a parse operation
     */
    static class Result {
        private final WindowsPathType type;
        private final String root;
        private final String path;

        Result(WindowsPathType type, String root, String path) {
            this.type = type;
            this.root = root;
            this.path = path;
        }

        /**
         * The path type
         */
        WindowsPathType type() {
            return type;
        }

        /**
         * The root component
         */
        String root() {
            return root;
        }

        /**
         * The normalized path (includes root)
         */
        String path() {
            return path;
        }
    }

    /**
     * Parses the given input as a Windows path
     */
    static Result parse(String input) {
        return parse(input, true);
    }

    /**
     * Parses the given input as a Windows path where it is known that the
     * path is already normalized.
     */
    static Result parseNormalizedPath(String input) {
        return parse(input, false);
    }

    /**
     * Parses the given input as a Windows path.
     *
     * @param   requireToNormalize
     *          Indicates if the path requires to be normalized
     */
    private static Result parse(String input, boolean requireToNormalize) {
        // if a prefix is present, remove it and note the expected path type
        final WindowsPathType expectedType;
        if (input.startsWith("\\\\?\\")) {
            if (input.startsWith("UNC\\", 4)) {
                expectedType = WindowsPathType.UNC;
                input = "\\\\" + input.substring(8);
            } else {
                expectedType = WindowsPathType.ABSOLUTE;
                input = input.substring(4);
            }
        } else {
            expectedType = null;
        }

        String root = "";
        WindowsPathType type = null;

        int len = input.length();
        int off = 0;
        if (len > 1) {
            char c0 = input.charAt(0);
            char c1 = input.charAt(1);
            char c = 0;
            int next = 2;
            if (isSlash(c0) && isSlash(c1)) {
                // UNC: We keep the first two slash, collapse all the
                // following, then take the hostname and share name out,
                // meanwhile collapsing all the redundant slashes.
                type = WindowsPathType.UNC;
                off = nextNonSlash(input, next, len);
                next = nextSlash(input, off, len);
                if (off == next)
                    throw new InvalidPathException(input, "UNC path is missing hostname");
                String host = input.substring(off, next);  //host
                off = nextNonSlash(input, next, len);
                next = nextSlash(input, off, len);
                if (off == next)
                    throw new InvalidPathException(input, "UNC path is missing sharename");
                root = "\\\\" + host + "\\" + input.substring(off, next) + "\\";
                off = next;
            } else {
                if (isLetter(c0) && c1 == ':') {
                    char c2;
                    if (len > 2 && isSlash(c2 = input.charAt(2))) {
                        // avoid concatenation when root is "D:\"
                        if (c2 == '\\') {
                            root = input.substring(0, 3);
                        } else {
                            root = input.substring(0, 2) + '\\';
                        }
                        off = 3;
                        type = WindowsPathType.ABSOLUTE;
                    } else {
                        root = input.substring(0, 2);
                        off = 2;
                        type = WindowsPathType.DRIVE_RELATIVE;
                    }
                }
            }
        }
        if (off == 0) {
            if (len > 0 && isSlash(input.charAt(0))) {
                type = WindowsPathType.DIRECTORY_RELATIVE;
                root = "\\";
            } else {
                type = WindowsPathType.RELATIVE;
            }
        }

        if (expectedType != null && type != expectedType) {
            if (expectedType == WindowsPathType.ABSOLUTE) { // long path prefix
                throw new InvalidPathException(input, "Long path prefix can only be used with an absolute path");
            } else if (expectedType == WindowsPathType.UNC) { // long UNC path prefix
                throw new InvalidPathException(input, "Long UNC path prefix can only be used with a UNC path");
            }
        }

        if (requireToNormalize) {
            StringBuilder sb = new StringBuilder(input.length());
            sb.append(root);
            return new Result(type, root, normalize(sb, input, off));
        } else {
            return new Result(type, root, input);
        }
    }

    /**
     * Remove redundant slashes from the rest of the path, forcing all slashes
     * into the preferred slash.
     */
    private static String normalize(StringBuilder sb, String path, int off) {
        int len = path.length();
        off = nextNonSlash(path, off, len);
        int start = off;
        char lastC = 0;
        while (off < len) {
            char c = path.charAt(off);
            if (isSlash(c)) {
                if (lastC == ' ')
                    throw new InvalidPathException(path,
                                                   "Trailing char <" + lastC + ">",
                                                   off - 1);
                sb.append(path, start, off);
                off = nextNonSlash(path, off, len);
                if (off != len)   //no slash at the end of normalized path
                    sb.append('\\');
                start = off;
            } else {
                if (isInvalidPathChar(c))
                    throw new InvalidPathException(path,
                                                   "Illegal char <" + c + ">",
                                                   off);
                lastC = c;
                off++;
            }
        }
        if (start != off) {
            if (lastC == ' ')
                throw new InvalidPathException(path,
                                               "Trailing char <" + lastC + ">",
                                               off - 1);
            sb.append(path, start, off);
        }
        return sb.toString();
    }

    private static final boolean isSlash(char c) {
        return (c == '\\') || (c == '/');
    }

    private static final int nextNonSlash(String path, int off, int end) {
        while (off < end && isSlash(path.charAt(off))) { off++; }
        return off;
    }

    private static final int nextSlash(String path, int off, int end) {
        char c;
        while (off < end && !isSlash(c=path.charAt(off))) {
            if (isInvalidPathChar(c))
                throw new InvalidPathException(path,
                                               "Illegal character [" + c + "] in path",
                                               off);
            off++;
        }
        return off;
    }

    private static final boolean isLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }

    // Reserved characters for window path name
    private static final String reservedChars = "<>:\"|?*";
    private static final boolean isInvalidPathChar(char ch) {
        return ch < '\u0020' || reservedChars.indexOf(ch) != -1;
    }
}
