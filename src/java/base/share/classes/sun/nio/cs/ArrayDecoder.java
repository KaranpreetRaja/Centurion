/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.cs;

/**
 * FastPath byte[]->char[] decoder, REPLACE on malformed or
 * unmappable input.
 *
 * FastPath encoded byte[]-> "String Latin1 coding" byte[] decoder for use when
 * charset is always decodable to the internal String Latin1 coding byte[], ie. all mappings <=0xff
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

public interface ArrayDecoder {
    int decode(byte[] src, int off, int len, char[] dst);

    default boolean isASCIICompatible() {
        return false;
    }

    // Is always decodable to internal String Latin1 coding, ie. all mappings <= 0xff
    default boolean isLatin1Decodable() {
        return false;
    }

    // Decode to internal String Latin1 coding byte[] fastpath for when isLatin1Decodable == true
    default int decodeToLatin1(byte[] src, int sp, int len, byte[] dst) {
        return 0;
    }
}
