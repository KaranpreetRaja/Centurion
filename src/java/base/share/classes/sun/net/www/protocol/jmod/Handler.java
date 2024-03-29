/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.net.www.protocol.jmod;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.MalformedURLException;
import java.io.IOException;

/**
 * Placeholder protocol handler for the jmod protocol.
 */

public class Handler extends URLStreamHandler {
    public Handler() { }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        String s = url.toString();
        int index = s.indexOf("!/");
        if (index == -1)
            throw new MalformedURLException("no !/ found in url spec:" + s);

        throw new IOException("Can't connect to jmod URL");
    }
}
