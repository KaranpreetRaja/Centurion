/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.util.resources;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Set;

/**
 * Subclass of <code>ResourceBundle</code> with special
 * functionality for time zone names. The additional functionality:
 * <ul>
 * <li>Preserves the order of entries in the <code>getContents</code>
 *     array for the enumeration returned by <code>getKeys</code>.
 * <li>Inserts the time zone ID (the key of the bundle entries) into
 *     the string arrays returned by <code>handleGetObject</code>.
 * </ul>
 * All <code>TimeZoneNames</code> resource bundles must extend this
 * class and implement the <code>getContents</code> method.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public abstract class TimeZoneNamesBundle extends OpenListResourceBundle {

    /**
     * Maps time zone IDs to locale-specific names.
     * The value returned is an array of five strings:
     * <ul>
     * <li>The time zone ID (same as the key, not localized).
     * <li>The long name of the time zone in standard time (localized).
     * <li>The short name of the time zone in standard time (localized).
     * <li>The long name of the time zone in daylight savings time (localized).
     * <li>The short name of the time zone in daylight savings time (localized).
     * <li>The long name of the time zone in generic form (localized).
     * <li>The short name of the time zone in generic form (localized).
     * </ul>
     * The localized names come from the subclasses's
     * <code>getContents</code> implementations, while the time zone
     * ID is inserted into the returned array by this method.
     */
    @Override
    public Object handleGetObject(String key) {
        Object val = super.handleGetObject(key);
        if (val instanceof String[]) {
            String[] contents = (String[]) val;
            int clen = contents.length;
            String[] tmpobj = new String[7];
            tmpobj[0] = key;
            System.arraycopy(contents, 0, tmpobj, 1, clen);
            return tmpobj;
        }
        return val;
    }

    /**
     * Use LinkedHashMap to preserve the order of bundle entries.
     */
    @Override
    protected <K, V> Map<K, V> createMap(int size) {
        return LinkedHashMap.newLinkedHashMap(size);
    }

    /**
     * Use LinkedHashSet to preserve the key order.
     * @param <E> the type of elements
     * @return a Set
     */
    @Override
    protected <E> Set<E> createSet() {
        return new LinkedHashSet<>();
    }

    /**
     * Provides key/value mappings for a specific
     * resource bundle. Each entry of the array
     * returned must be an array with two elements:
     * <ul>
     * <li>The key, which must be a string.
     * <li>The value, which must be an array of
     *     four strings:
     *     <ul>
     *     <li>The long name of the time zone in standard time.
     *     <li>The short name of the time zone in standard time.
     *     <li>The long name of the time zone in daylight savings time.
     *     <li>The short name of the time zone in daylight savings time.
     *     </ul>
     * </ul>
     */
    protected abstract Object[][] getContents();
}
