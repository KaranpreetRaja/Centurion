/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.net.www.content.text;
import java.io.InputStream;
import java.io.FilterInputStream;

/**
 * PlainTextInputStream class extends the FilterInputStream class.
 * Currently all calls to the PlainTextInputStream object will call
 * the corresponding methods in the FilterInputStream class.  Hence
 * for now its use is more semantic.
 *
 * @author Sunita Mani
 */
public class PlainTextInputStream extends FilterInputStream {

    /**
     * Calls FilterInputStream's constructor.
     * @param is an InputStream
     */
    PlainTextInputStream(InputStream is) {
        super(is);
    }
}
