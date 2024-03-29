/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.time.format;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Context object used during date and time parsing.
 * <p>
 * This class represents the current state of the parse.
 * It has the ability to store and retrieve the parsed values and manage optional segments.
 * It also provides key information to the parsing methods.
 * <p>
 * Once parsing is complete, the {@link #toUnresolved()} is used to obtain the unresolved
 * result data. The {@link #toResolved()} is used to obtain the resolved result.
 *
 * @implSpec
 * This class is a mutable context intended for use from a single thread.
 * Usage of the class is thread-safe within standard parsing as a new instance of this class
 * is automatically created for each parse and parsing is single-threaded
 *
 * @since 1.8
 */
final class DateTimeParseContext {

    /**
     * The formatter, not null.
     */
    private DateTimeFormatter formatter;
    /**
     * Whether to parse using case sensitively.
     */
    private boolean caseSensitive = true;
    /**
     * Whether to parse using strict rules.
     */
    private boolean strict = true;
    /**
     * The list of parsed data.
     */
    private final ArrayList<Parsed> parsed = new ArrayList<>();
    /**
     * List of Consumers<Chronology> to be notified if the Chronology changes.
     */
    private ArrayList<Consumer<Chronology>> chronoListeners = null;

    /**
     * Creates a new instance of the context.
     *
     * @param formatter  the formatter controlling the parse, not null
     */
    DateTimeParseContext(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
        parsed.add(new Parsed());
    }

    /**
     * Creates a copy of this context.
     * This retains the case sensitive and strict flags.
     */
    DateTimeParseContext copy() {
        DateTimeParseContext newContext = new DateTimeParseContext(formatter);
        newContext.caseSensitive = caseSensitive;
        newContext.strict = strict;
        return newContext;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale.
     * <p>
     * This locale is used to control localization in the parse except
     * where localization is controlled by the DecimalStyle.
     *
     * @return the locale, not null
     */
    Locale getLocale() {
        return formatter.getLocale();
    }

    /**
     * Gets the DecimalStyle.
     * <p>
     * The DecimalStyle controls the numeric parsing.
     *
     * @return the DecimalStyle, not null
     */
    DecimalStyle getDecimalStyle() {
        return formatter.getDecimalStyle();
    }

    /**
     * Gets the effective chronology during parsing.
     *
     * @return the effective parsing chronology, not null
     */
    Chronology getEffectiveChronology() {
        Chronology chrono = currentParsed().chrono;
        if (chrono == null) {
            chrono = formatter.getChronology();
            if (chrono == null) {
                chrono = IsoChronology.INSTANCE;
            }
        }
        return chrono;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if parsing is case sensitive.
     *
     * @return true if parsing is case sensitive, false if case insensitive
     */
    boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the parsing is case sensitive or not.
     *
     * @param caseSensitive  changes the parsing to be case sensitive or not from now on
     */
    void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    //-----------------------------------------------------------------------
    /**
     * Helper to compare two {@code CharSequence} instances.
     * This uses {@link #isCaseSensitive()}.
     *
     * @param cs1  the first character sequence, not null
     * @param offset1  the offset into the first sequence, valid
     * @param cs2  the second character sequence, not null
     * @param offset2  the offset into the second sequence, valid
     * @param length  the length to check, valid
     * @return true if equal
     */
    boolean subSequenceEquals(CharSequence cs1, int offset1, CharSequence cs2, int offset2, int length) {
        if (offset1 + length > cs1.length() || offset2 + length > cs2.length()) {
            return false;
        }
        if (isCaseSensitive()) {
            for (int i = 0; i < length; i++) {
                char ch1 = cs1.charAt(offset1 + i);
                char ch2 = cs2.charAt(offset2 + i);
                if (ch1 != ch2) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                char ch1 = cs1.charAt(offset1 + i);
                char ch2 = cs2.charAt(offset2 + i);
                if (ch1 != ch2 && Character.toUpperCase(ch1) != Character.toUpperCase(ch2) &&
                        Character.toLowerCase(ch1) != Character.toLowerCase(ch2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper to compare two {@code char}.
     * This uses {@link #isCaseSensitive()}.
     *
     * @param ch1  the first character
     * @param ch2  the second character
     * @return true if equal
     */
    boolean charEquals(char ch1, char ch2) {
        if (isCaseSensitive()) {
            return ch1 == ch2;
        }
        return charEqualsIgnoreCase(ch1, ch2);
    }

    /**
     * Compares two characters ignoring case.
     *
     * @param c1  the first
     * @param c2  the second
     * @return true if equal
     */
    static boolean charEqualsIgnoreCase(char c1, char c2) {
        return c1 == c2 ||
                Character.toUpperCase(c1) == Character.toUpperCase(c2) ||
                Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if parsing is strict.
     * <p>
     * Strict parsing requires exact matching of the text and sign styles.
     *
     * @return true if parsing is strict, false if lenient
     */
    boolean isStrict() {
        return strict;
    }

    /**
     * Sets whether parsing is strict or lenient.
     *
     * @param strict  changes the parsing to be strict or lenient from now on
     */
    void setStrict(boolean strict) {
        this.strict = strict;
    }

    //-----------------------------------------------------------------------
    /**
     * Starts the parsing of an optional segment of the input.
     */
    void startOptional() {
        parsed.add(currentParsed().copy());
    }

    /**
     * Ends the parsing of an optional segment of the input.
     *
     * @param successful  whether the optional segment was successfully parsed
     */
    void endOptional(boolean successful) {
        if (successful) {
            parsed.remove(parsed.size() - 2);
        } else {
            parsed.remove(parsed.size() - 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currently active temporal objects.
     *
     * @return the current temporal objects, not null
     */
    private Parsed currentParsed() {
        return parsed.get(parsed.size() - 1);
    }

    /**
     * Gets the unresolved result of the parse.
     *
     * @return the result of the parse, not null
     */
    Parsed toUnresolved() {
        return currentParsed();
    }

    /**
     * Gets the resolved result of the parse.
     *
     * @return the result of the parse, not null
     */
    TemporalAccessor toResolved(ResolverStyle resolverStyle, Set<TemporalField> resolverFields) {
        Parsed parsed = currentParsed();
        parsed.chrono = getEffectiveChronology();
        parsed.zone = (parsed.zone != null ? parsed.zone : formatter.getZone());
        return parsed.resolve(resolverStyle, resolverFields);
    }


    //-----------------------------------------------------------------------
    /**
     * Gets the first value that was parsed for the specified field.
     * <p>
     * This searches the results of the parse, returning the first value found
     * for the specified field. No attempt is made to derive a value.
     * The field may have an out of range value.
     * For example, the day-of-month might be set to 50, or the hour to 1000.
     *
     * @param field  the field to query from the map, null returns null
     * @return the value mapped to the specified field, null if field was not parsed
     */
    Long getParsed(TemporalField field) {
        return currentParsed().fieldValues.get(field);
    }

    /**
     * Stores the parsed field.
     * <p>
     * This stores a field-value pair that has been parsed.
     * The value stored may be out of range for the field - no checks are performed.
     *
     * @param field  the field to set in the field-value map, not null
     * @param value  the value to set in the field-value map
     * @param errorPos  the position of the field being parsed
     * @param successPos  the position after the field being parsed
     * @return the new position
     */
    int setParsedField(TemporalField field, long value, int errorPos, int successPos) {
        Objects.requireNonNull(field, "field");
        Long old = currentParsed().fieldValues.put(field, value);
        return (old != null && old.longValue() != value) ? ~errorPos : successPos;
    }

    /**
     * Stores the parsed chronology.
     * <p>
     * This stores the chronology that has been parsed.
     * No validation is performed other than ensuring it is not null.
     * <p>
     * The list of listeners is copied and cleared so that each
     * listener is called only once.  A listener can add itself again
     * if it needs to be notified of future changes.
     *
     * @param chrono  the parsed chronology, not null
     */
    void setParsed(Chronology chrono) {
        Objects.requireNonNull(chrono, "chrono");
        currentParsed().chrono = chrono;
        if (chronoListeners != null && !chronoListeners.isEmpty()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Consumer<Chronology>[] tmp = new Consumer[1];
            Consumer<Chronology>[] listeners = chronoListeners.toArray(tmp);
            chronoListeners.clear();
            for (Consumer<Chronology> l : listeners) {
                l.accept(chrono);
            }
        }
    }

    /**
     * Adds a Consumer<Chronology> to the list of listeners to be notified
     * if the Chronology changes.
     * @param listener a Consumer<Chronology> to be called when Chronology changes
     */
    void addChronoChangedListener(Consumer<Chronology> listener) {
        if (chronoListeners == null) {
            chronoListeners = new ArrayList<>();
        }
        chronoListeners.add(listener);
    }

    /**
     * Stores the parsed zone.
     * <p>
     * This stores the zone that has been parsed.
     * No validation is performed other than ensuring it is not null.
     *
     * @param zone  the parsed zone, not null
     */
    void setParsed(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        currentParsed().zone = zone;
    }

    /**
     * Stores the parsed zone name type.
     * <p>
     * This stores the zone name type that has been parsed.
     * The parsed type should either be;
     * <ul>
     *     <li>{@link DateTimeFormatterBuilder.ZoneTextPrinterParser#UNDEFINED}</li>
     *     <li>{@link DateTimeFormatterBuilder.ZoneTextPrinterParser#STD}</li>
     *     <li>{@link DateTimeFormatterBuilder.ZoneTextPrinterParser#DST}</li>
     *     <li>{@link DateTimeFormatterBuilder.ZoneTextPrinterParser#GENERIC}</li>
     * </ul>
     *
     * @param type  the parsed zone name type
     */
    void setParsedZoneNameType(int type) {
        currentParsed().zoneNameType = type;
    }

    /**
     * Stores the parsed leap second.
     */
    void setParsedLeapSecond() {
        currentParsed().leapSecond = true;
    }

    /**
     * Stores the parsed day period.
     *
     * @param dayPeriod the parsed day period
     */
    void setParsedDayPeriod(DateTimeFormatterBuilder.DayPeriod dayPeriod) {
        currentParsed().dayPeriod = dayPeriod;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string version of the context for debugging.
     *
     * @return a string representation of the context data, not null
     */
    @Override
    public String toString() {
        return currentParsed().toString();
    }

}
