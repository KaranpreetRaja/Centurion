/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.unix.classes.sun.net.www.protocol.file;

import java.net.URLConnection;
import java.net.URL;
import java.net.Proxy;
import java.net.URLStreamHandler;
import java.io.IOException;
import sun.net.www.ParseUtil;
import java.io.File;

/**
 * Open an file input stream given a URL.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public class Handler extends URLStreamHandler {

    protected void parseURL(URL u, String spec, int start, int limit) {
        /*
         * Ugly backwards compatibility. Flip any file separator
         * characters to be forward slashes. This is a nop on Unix
         * and "fixes" win32 file paths. According to RFC 2396,
         * only forward slashes may be used to represent hierarchy
         * separation in a URL but previous releases unfortunately
         * performed this "fixup" behavior in the file URL parsing code
         * rather than forcing this to be fixed in the caller of the URL
         * class where it belongs. Since backslash is an "unwise"
         * character that would normally be encoded if literally intended
         * as a non-separator character the damage of veering away from the
         * specification is presumably limited.
         */
        super.parseURL(u, spec.replace(File.separatorChar, '/'), start, limit);
    }

    public URLConnection openConnection(URL u)
        throws IOException {
        return openConnection(u, null);
    }

    public URLConnection openConnection(URL u, Proxy p)
           throws IOException {
        String host = u.getHost();
        if (host == null || host.isEmpty() || host.equals("~") ||
            host.equalsIgnoreCase("localhost")) {
            File file = new File(ParseUtil.decode(u.getPath()));
            return createFileURLConnection(u, file);
        }

        /* If you reach here, it implies that you have a hostname
           so attempt an ftp connection.
         */
        URLConnection uc;
        URL ru;

        try {
            @SuppressWarnings("deprecation")
            var _unused = ru = new URL("ftp", host, u.getFile() +
                             (u.getRef() == null ? "": "#" + u.getRef()));
            if (p != null) {
                uc = ru.openConnection(p);
            } else {
                uc = ru.openConnection();
            }
        } catch (IOException e) {
            uc = null;
        }
        if (uc == null) {
            throw new IOException("Unable to connect to: " +
                                                       u.toExternalForm());
        }
        return uc;
    }

    // Template method to be overridden by Java Plug-in. [stanleyh]
    //
    protected URLConnection createFileURLConnection(URL u, File file)
    {
        return new FileURLConnection(u, file);
    }

    /**
     * Compares the host components of two URLs.
     * @param u1 the URL of the first host to compare
     * @param u2 the URL of the second host to compare
     * @return  {@code true} if and only if they
     * are equal, {@code false} otherwise.
     */
    protected boolean hostsEqual(URL u1, URL u2) {
        /*
         * Special case for file: URLs
         * per RFC 1738 no hostname is equivalent to 'localhost'
         * i.e. file:///path is equal to file://localhost/path
         */
        String s1 = u1.getHost();
        String s2 = u2.getHost();
        if ("localhost".equalsIgnoreCase(s1) && (s2 == null || s2.isEmpty()))
            return true;
        if ("localhost".equalsIgnoreCase(s2) && (s1 == null || s1.isEmpty()))
            return true;
        return super.hostsEqual(u1, u2);
    }
}
