/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.net.www.protocol.file;

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
 * @edited 19/4/2023
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

    public URLConnection openConnection(URL url)
        throws IOException {
        return openConnection(url, null);
    }

    public URLConnection openConnection(URL url, Proxy p)
           throws IOException {

        String path;
        String file = url.getFile();
        String host = url.getHost();

        path = ParseUtil.decode(file);
        path = path.replace('/', '\\');
        path = path.replace('|', ':');

        if ((host == null) || host.isEmpty() ||
                host.equalsIgnoreCase("localhost") ||
                host.equals("~")) {
           return createFileURLConnection(url, new File(path));
        }

        /*
         * attempt to treat this as a UNC path. See 4180841
         */
        path = "\\\\" + host + path;
        File f = new File(path);
        if (f.exists()) {
            return new UNCFileURLConnection(url, f, path);
        }

        /*
         * Now attempt an ftp connection.
         */
        URLConnection uc;
        URL newurl;

        try {
            @SuppressWarnings("deprecation")
            var _unused = newurl = new URL("ftp", host, file +
                            (url.getRef() == null ? "":
                            "#" + url.getRef()));
            if (p != null) {
                uc = newurl.openConnection(p);
            } else {
                uc = newurl.openConnection();
            }
        } catch (IOException e) {
            uc = null;
        }
        if (uc == null) {
            throw new IOException("Unable to connect to: " +
                                        url.toExternalForm());
        }
        return uc;
    }

    /**
     * Template method to be overridden by Java Plug-in. [stanleyh]
     */
    protected URLConnection createFileURLConnection(URL url, File file) {
        return new FileURLConnection(url, file);
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