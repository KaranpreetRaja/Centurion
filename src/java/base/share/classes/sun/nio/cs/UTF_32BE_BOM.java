/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.cs;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

public class UTF_32BE_BOM extends Unicode {
    public UTF_32BE_BOM() {
        super("X-UTF-32BE-BOM", StandardCharsets.aliases_UTF_32BE_BOM());
    }

    public String historicalName() {
        return "X-UTF-32BE-BOM";
    }

    public CharsetDecoder newDecoder() {
        return new UTF_32Coder.Decoder(this, UTF_32Coder.BIG);
    }

    public CharsetEncoder newEncoder() {
        return new UTF_32Coder.Encoder(this, UTF_32Coder.BIG, true);
    }
}
