/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.jimage.decompressor;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * Constant Pool strings sharing Decompressor factory.
 *
 * @implNote This class needs to maintain JDK 8 source compatibility.
 *
 * It is used internally in the JDK to implement jimage/jrtfs access,
 * but also compiled and delivered as part of the jrtfs.jar to support access
 * to the jimage file provided by the shipped JDK by tools running on JDK 8.
 */
public class StringSharingDecompressorFactory extends ResourceDecompressorFactory {

    public static final String NAME = "compact-cp";
    public StringSharingDecompressorFactory() {
        super(NAME);
    }

    @Override
    public ResourceDecompressor newDecompressor(Properties properties)
            throws IOException {
        return new StringSharingDecompressor(properties);
    }
}
