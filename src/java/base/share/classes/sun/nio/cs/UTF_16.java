/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

public class UTF_16 extends Unicode
{

    public UTF_16() {
        super("UTF-16", StandardCharsets.aliases_UTF_16());
    }

    public String historicalName() {
        return "UTF-16";
    }

    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }

    private static class Decoder extends UnicodeDecoder {

        public Decoder(Charset cs) {
            super(cs, NONE);
        }
    }

    private static class Encoder extends UnicodeEncoder {

        public Encoder(Charset cs) {
            super(cs, BIG, true);
        }
    }

}
