/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

/**
 * Plain text file handler.
 * @author  Steven B. Byrne
 */
package java.base.share.classes.sun.net.www.content.text;
import java.net.*;
import java.io.InputStream;
import java.io.IOException;

public class plain extends ContentHandler {
    /**
     * Returns a PlainTextInputStream object from which data
     * can be read.
     */
    public Object getContent(URLConnection uc) {
        try {
            InputStream is = uc.getInputStream();
            return new PlainTextInputStream(uc.getInputStream());
        } catch (IOException e) {
            return "Error reading document:\n" + e.toString();
        }
    }
}
