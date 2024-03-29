/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.util.locale.provider;

import java.util.Map;

/**
 * LocaleData meta info SPI
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public interface LocaleDataMetaInfo {

    /**
     * Returns the type of LocaleProviderAdapter for which this LocaleData
     * provides the data.
     * @return type The type of the adapter.
     */
    public LocaleProviderAdapter.Type getType();

    /**
     * Returns the string concatenation of the supported language tags in
     * this LocaleData instance
     * @param category category of the locale data.
     * @return concatenated language tags, separated by a space.
     */
    public String availableLanguageTags(String category);

    /**
     * Returns a map for time zone ids to their canonical ids.
     * The map key is either an LDML's short id, or a valid
     * TZDB zone id.
     * @return map of ids to their canonical ids.
     */
    default public Map<String, String>  tzCanonicalIDs() {
        return null;
    }

    /**
     * Returns a map for  language aliases which specifies mapping from source language
     * to from which it should be replaced.
     * @return map of source language to replacement language, separated by a space.
     */
   default public Map<String, String> getLanguageAliasMap(){
       return null;
   }
}
