/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.text;

import jdk.internal.icu.text.NormalizerBase;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */

public class CollatorUtilities {

    public static int toLegacyMode(NormalizerBase.Mode mode) {
        // find the index of the legacy mode in the table;
        // if it's not there, default to Collator.NO_DECOMPOSITION (0)
        int legacyMode = legacyModeMap.length;
        while (legacyMode > 0) {
            --legacyMode;
            if (legacyModeMap[legacyMode] == mode) {
                break;
            }
        }
        return legacyMode;
    }

    public static NormalizerBase.Mode toNormalizerMode(int mode) {
        NormalizerBase.Mode normalizerMode;

        try {
            normalizerMode = legacyModeMap[mode];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            normalizerMode = NormalizerBase.NONE;
        }
        return normalizerMode;

    }


    static NormalizerBase.Mode[] legacyModeMap = {
        NormalizerBase.NONE,   // Collator.NO_DECOMPOSITION
        NormalizerBase.NFD,    // Collator.CANONICAL_DECOMPOSITION
        NormalizerBase.NFKD,   // Collator.FULL_DECOMPOSITION
    };

}
