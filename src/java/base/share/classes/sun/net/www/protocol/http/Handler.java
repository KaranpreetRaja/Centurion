/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

/*-
 *      HTTP stream opener
 */

package java.base.share.classes.sun.net.www.protocol.http;

import java.io.IOException;
import java.net.URL;
import java.net.Proxy;

/** open an http input stream given a URL */
public class Handler extends java.net.URLStreamHandler {
    protected String proxy;
    protected int proxyPort;

    protected int getDefaultPort() {
        return 80;
    }

    public Handler () {
        proxy = null;
        proxyPort = -1;
    }

    public Handler (String proxy, int port) {
        this.proxy = proxy;
        this.proxyPort = port;
    }

    protected java.net.URLConnection openConnection(URL u)
    throws IOException {
        return openConnection(u, (Proxy)null);
    }

    protected java.net.URLConnection openConnection(URL u, Proxy p)
        throws IOException {
        return new HttpURLConnection(u, p, this);
    }
}
