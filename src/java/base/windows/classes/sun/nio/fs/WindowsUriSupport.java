/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.fs;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility methods to convert between Path and URIs.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023
 */

class WindowsUriSupport {
    private WindowsUriSupport() {
    }

    // suffix for IPv6 literal address
    private static final String IPV6_LITERAL_SUFFIX = ".ipv6-literal.net";

    /**
     * Returns URI to represent the given (absolute) path
     */
    private static URI toUri(String path, boolean isUnc, boolean addSlash) {
        String uriHost;
        String uriPath;

        if (isUnc) {
            int slash = path.indexOf('\\', 2);
            uriHost = path.substring(2, slash);
            uriPath = path.substring(slash).replace('\\', '/');

            // handle IPv6 literal addresses
            // 1. drop .ivp6-literal.net
            // 2. replace "-" with ":"
            // 3. replace "s" with "%" (zone/scopeID delimiter)
            if (uriHost.endsWith(IPV6_LITERAL_SUFFIX)) {
                uriHost = uriHost
                    .substring(0, uriHost.length() - IPV6_LITERAL_SUFFIX.length())
                    .replace('-', ':')
                    .replace('s', '%');
            }
        } else {
            uriHost = "";
            uriPath = "/" + path.replace('\\', '/');
        }

        // append slash if known to be directory
        if (addSlash)
            uriPath += "/";

        // return file:///C:/My%20Documents or file://server/share/foo
        try {
            return new URI("file", uriHost, uriPath, null);
        } catch (URISyntaxException x) {
            if (!isUnc)
                throw new AssertionError(x);
        }

        // if we get here it means we've got a UNC with reserved characters
        // in the server name. The authority component cannot contain escaped
        // octets so fallback to encoding the server name into the URI path
        // component.
        uriPath = "//" + path.replace('\\', '/');
        if (addSlash)
            uriPath += "/";
        try {
            return new URI("file", null, uriPath, null);
        } catch (URISyntaxException x) {
            throw new AssertionError(x);
        }
    }

    /**
     * Converts given Path to a URI
     */
    static URI toUri(WindowsPath path) {
        path = path.toAbsolutePath();
        String s = path.toString();

        // trailing slash will be added if file is a directory. Skip check if
        // already have trailing space
        boolean addSlash = false;
        if (!s.endsWith("\\")) {
            try {
                 path.checkRead();
                 addSlash = WindowsFileAttributes.get(path, true).isDirectory();
            } catch (SecurityException | WindowsException x) {
            }
        }

        return toUri(s, path.isUnc(), addSlash);
    }

    /**
     * Converts given URI to a Path
     */
    static WindowsPath fromUri(WindowsFileSystem fs, URI uri) {
        if (!uri.isAbsolute())
            throw new IllegalArgumentException("URI is not absolute");
        if (uri.isOpaque())
            throw new IllegalArgumentException("URI is not hierarchical");
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equalsIgnoreCase("file"))
            throw new IllegalArgumentException("URI scheme is not \"file\"");
        if (uri.getRawFragment() != null)
            throw new IllegalArgumentException("URI has a fragment component");
        if (uri.getRawQuery() != null)
            throw new IllegalArgumentException("URI has a query component");
        String path = uri.getPath();
        if (path.isEmpty())
            throw new IllegalArgumentException("URI path component is empty");

        // UNC
        String auth = uri.getRawAuthority();
        if (auth != null && !auth.isEmpty()) {
            String host = uri.getHost();
            if (host == null)
                throw new IllegalArgumentException("URI authority component has undefined host");
            if (uri.getUserInfo() != null)
                throw new IllegalArgumentException("URI authority component has user-info");
            if (uri.getPort() != -1)
                throw new IllegalArgumentException("URI authority component has port number");

            // IPv6 literal
            // 1. drop enclosing brackets
            // 2. replace ":" with "-"
            // 3. replace "%" with "s" (zone/scopeID delimiter)
            // 4. Append .ivp6-literal.net
            if (host.startsWith("[")) {
                host = host.substring(1, host.length()-1)
                           .replace(':', '-')
                           .replace('%', 's');
                host += IPV6_LITERAL_SUFFIX;
            }

            // reconstitute the UNC
            path = "\\\\" + host + path;
        } else {
            if ((path.length() > 2) && (path.charAt(2) == ':')) {
                // "/c:/foo" --> "c:/foo"
                path = path.substring(1);
            }
        }
        return WindowsPath.parse(fs, path);
    }
}
