/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.TimeZoneNameProvider;
import sun.util.calendar.ZoneInfo;
import sun.util.cldr.CLDRLocaleProviderAdapter;
import static sun.util.locale.provider.LocaleProviderAdapter.Type;

/**
 * Utility class that deals with the localized time zone names
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public final class TimeZoneNameUtility {

    /**
     * cache to hold time zone localized strings. Keyed by Locale
     */
    private static final ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData =
        new ConcurrentHashMap<>();

    /**
     * Cache for managing display names per timezone per locale
     * The structure is:
     *     Map(key=id, value=SoftReference(Map(key=locale, value=displaynames)))
     */
    private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames =
        new ConcurrentHashMap<>();

    /**
     * get time zone localized strings. Enumerate all keys.
     */
    public static String[][] getZoneStrings(Locale locale) {
        String[][] zones;
        SoftReference<String[][]> data = cachedZoneData.get(locale);

        if (data == null || ((zones = data.get()) == null)) {
            zones = loadZoneStrings(locale);
            data = new SoftReference<>(zones);
            cachedZoneData.put(locale, data);
        }

        return zones;
    }

    private static String[][] loadZoneStrings(Locale locale) {
        // If the provider is a TimeZoneNameProviderImpl, call its getZoneStrings
        // in order to avoid per-ID retrieval.
        LocaleProviderAdapter adapter = LocaleProviderAdapter.getAdapter(TimeZoneNameProvider.class, locale);
        TimeZoneNameProvider provider = adapter.getTimeZoneNameProvider();
        if (provider instanceof TimeZoneNameProviderImpl) {
            String[][] zoneStrings = ((TimeZoneNameProviderImpl)provider).getZoneStrings(locale);

            if (zoneStrings.length == 0 && locale.equals(Locale.ROOT)) {
                // Unlike other *Name provider, zoneStrings search won't do the fallback
                // name search. If the ResourceBundle found for the root locale contains no
                // zoneStrings, just use the one for English, assuming English bundle
                // contains all the tzids and their names.
                zoneStrings= getZoneStrings(Locale.ENGLISH);
            }

            return zoneStrings;
        }

        // Performs per-ID retrieval.
        Set<String> zoneIDs = LocaleProviderAdapter.forJRE().getLocaleResources(locale).getZoneIDs();
        List<String[]> zones = new ArrayList<>();
        for (String key : zoneIDs) {
            String[] names = retrieveDisplayNamesImpl(key, locale);
            if (names != null) {
                zones.add(names);
            }
        }

        String[][] zonesArray = new String[zones.size()][];
        return zones.toArray(zonesArray);
    }

    /**
     * Retrieve display names for a time zone ID.
     */
    public static String[] retrieveDisplayNames(String id, Locale locale) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(locale);

        return retrieveDisplayNamesImpl(id, locale);
    }

    /**
     * Retrieves a generic time zone display name for a time zone ID.
     *
     * @param id     time zone ID
     * @param style  TimeZone.LONG or TimeZone.SHORT
     * @param locale desired Locale
     * @return the requested generic time zone display name, or null if not found.
     */
    public static String retrieveGenericDisplayName(String id, int style, Locale locale) {
        String[] names = retrieveDisplayNamesImpl(id, locale);
        if (Objects.nonNull(names)) {
            return names[6 - style];
        } else {
            return null;
        }
    }

    /**
     * Retrieves a standard or daylight-saving time name for the given time zone ID.
     *
     * @param id       time zone ID
     * @param daylight true for a daylight saving time name, or false for a standard time name
     * @param style    TimeZone.LONG or TimeZone.SHORT
     * @param locale   desired Locale
     * @return the requested time zone name, or null if not found.
     */
    public static String retrieveDisplayName(String id, boolean daylight, int style, Locale locale) {
        String[] names = retrieveDisplayNamesImpl(id, locale);
        if (Objects.nonNull(names)) {
            return names[(daylight ? 4 : 2) - style];
        } else {
            return null;
        }
    }

    /**
     * Converts the time zone id from LDML's 5-letter id to tzdb's id
     *
     * @param shortID       time zone short ID defined in LDML
     * @return the tzdb's time zone ID
     */
    public static Optional<String> convertLDMLShortID(String shortID) {
        return canonicalTZID(shortID);
    }

    /**
     * Returns the canonical ID for the given ID
     */
    public static Optional<String> canonicalTZID(String id) {
        return ((CLDRLocaleProviderAdapter)LocaleProviderAdapter.forType(Type.CLDR))
                    .canonicalTZID(id);
    }

    private static String[] retrieveDisplayNamesImpl(String id, Locale locale) {
        LocaleServiceProviderPool pool =
            LocaleServiceProviderPool.getPool(TimeZoneNameProvider.class);
        String[] names;
        Map<Locale, String[]> perLocale = null;

        SoftReference<Map<Locale, String[]>> ref = cachedDisplayNames.get(id);
        if (Objects.nonNull(ref)) {
            perLocale = ref.get();
            if (Objects.nonNull(perLocale)) {
                names = perLocale.get(locale);
                if (Objects.nonNull(names)) {
                    return names;
                }
            }
        }

        // build names array
        names = new String[7];
        names[0] = id;
        for (int i = 1; i <= 6; i ++) {
            names[i] = pool.getLocalizedObject(TimeZoneNameGetter.INSTANCE, locale,
                    i<5 ? (i<3 ? "std" : "dst") : "generic", i%2, id);
        }

        if (Objects.isNull(perLocale)) {
            perLocale = new ConcurrentHashMap<>();
        }
        perLocale.put(locale, names);
        ref = new SoftReference<>(perLocale);
        cachedDisplayNames.put(id, ref);
        return names;
    }


    /**
     * Obtains a localized time zone strings from a TimeZoneNameProvider
     * implementation.
     */
    private static class TimeZoneNameGetter
        implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider,
                                                                   String> {
        private static final TimeZoneNameGetter INSTANCE =
            new TimeZoneNameGetter();

        @Override
        public String getObject(TimeZoneNameProvider timeZoneNameProvider,
                                Locale locale,
                                String requestID,
                                Object... params) {
            assert params.length == 2;
            int style = (int) params[0];
            String tzid = (String) params[1];
            String value = getName(timeZoneNameProvider, locale, requestID, style, tzid);
            if (value == null) {
                Map<String, String> aliases = ZoneInfo.getAliasTable();
                if (aliases != null) {
                    String canonicalID = aliases.get(tzid);
                    if (canonicalID != null) {
                        value = getName(timeZoneNameProvider, locale, requestID, style, canonicalID);
                    }
                    if (value == null) {
                        value = examineAliases(timeZoneNameProvider, locale, requestID,
                                     canonicalID != null ? canonicalID : tzid, style, aliases);
                    }
                }
            }

            return value;
        }

        private static String examineAliases(TimeZoneNameProvider tznp, Locale locale,
                                             String requestID, String tzid, int style,
                                             Map<String, String> aliases) {
            for (Map.Entry<String, String> entry : aliases.entrySet()) {
                if (entry.getValue().equals(tzid)) {
                    String alias = entry.getKey();
                    String name = getName(tznp, locale, requestID, style, alias);
                    if (name != null) {
                        return name;
                    }
                    name = examineAliases(tznp, locale, requestID, alias, style, aliases);
                    if (name != null) {
                        return name;
                    }
                }
            }
            return null;
        }

        private static String getName(TimeZoneNameProvider timeZoneNameProvider,
                                      Locale locale, String requestID, int style, String tzid) {
            String value = null;
            switch (requestID) {
            case "std":
                value = timeZoneNameProvider.getDisplayName(tzid, false, style, locale);
                break;
            case "dst":
                value = timeZoneNameProvider.getDisplayName(tzid, true, style, locale);
                break;
            case "generic":
                value = timeZoneNameProvider.getGenericDisplayName(tzid, style, locale);
                break;
            }
            return value;
        }
    }

    // No instantiation
    private TimeZoneNameUtility() {
    }
}
