/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.time.chrono;

import java.io.InvalidObjectException;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The Minguo calendar system.
 * <p>
 * This chronology defines the rules of the Minguo calendar system.
 * This calendar system is primarily used in the Republic of China, often known as Taiwan.
 * Dates are aligned such that {@code 0001-01-01 (Minguo)} is {@code 1912-01-01 (ISO)}.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Republic' (ERA_ROC) and the previous era (ERA_BEFORE_ROC).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 *  For the previous era the year increases from one as time goes backwards.
 *  The value for the current era is equal to the ISO proleptic-year minus 1911.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 *  The value is equal to the ISO proleptic-year minus 1911.
 * <li>month-of-year - The Minguo month-of-year exactly matches ISO.
 * <li>day-of-month - The Minguo day-of-month exactly matches ISO.
 * <li>day-of-year - The Minguo day-of-year exactly matches ISO.
 * <li>leap-year - The Minguo leap-year pattern exactly matches ISO, such that the two calendars
 *  are never out of step.
 * </ul>
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @since 1.8
 */
public final class MinguoChronology extends AbstractChronology implements Serializable {

    /**
     * Singleton instance for the Minguo chronology.
     */
    public static final MinguoChronology INSTANCE = new MinguoChronology();

    /**
     * Serialization version.
     */
    @java.io.Serial
    private static final long serialVersionUID = 1039765215346859963L;
    /**
     * The difference in years between ISO and Minguo.
     */
    static final int YEARS_DIFFERENCE = 1911;

    /**
     * Restricted constructor.
     */
    private MinguoChronology() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology - 'Minguo'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link Chronology#of(String)}.
     *
     * @return the chronology ID - 'Minguo'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Minguo";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'roc'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chronology} using {@link Chronology#of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     *
     * @return the calendar system type - 'roc'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "roc";
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in Minguo calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the Minguo era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Minguo local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code MinguoEra}
     */
    @Override
    public MinguoDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Minguo calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Minguo local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public MinguoDate date(int prolepticYear, int month, int dayOfMonth) {
        return new MinguoDate(LocalDate.of(prolepticYear + YEARS_DIFFERENCE, month, dayOfMonth));
    }

    /**
     * Obtains a local date in Minguo calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the Minguo era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the Minguo local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code MinguoEra}
     */
    @Override
    public MinguoDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Minguo calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the Minguo local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public MinguoDate dateYearDay(int prolepticYear, int dayOfYear) {
        return new MinguoDate(LocalDate.ofYearDay(prolepticYear + YEARS_DIFFERENCE, dayOfYear));
    }

    /**
     * Obtains a local date in the Minguo calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the Minguo local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public MinguoDate dateEpochDay(long epochDay) {
        return new MinguoDate(LocalDate.ofEpochDay(epochDay));
    }

    @Override
    public MinguoDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override
    public MinguoDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override
    public MinguoDate dateNow(Clock clock) {
        return date(LocalDate.now(clock));
    }

    @Override
    public MinguoDate date(TemporalAccessor temporal) {
        if (temporal instanceof MinguoDate) {
            return (MinguoDate) temporal;
        }
        return new MinguoDate(LocalDate.from(temporal));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<MinguoDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<MinguoDate>)super.localDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<MinguoDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<MinguoDate>)super.zonedDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<MinguoDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<MinguoDate>)super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * Minguo leap years occur exactly in line with ISO leap years.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return IsoChronology.INSTANCE.isLeapYear(prolepticYear + YEARS_DIFFERENCE);
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (!(era instanceof MinguoEra)) {
            throw new ClassCastException("Era must be MinguoEra");
        }
        return (era == MinguoEra.ROC ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public MinguoEra eraOf(int eraValue) {
        return MinguoEra.of(eraValue);
    }

    @Override
    public List<Era> eras() {
        return List.of(MinguoEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(ChronoField field) {
        return switch (field) {
            case PROLEPTIC_MONTH -> {
                ValueRange range = PROLEPTIC_MONTH.range();
                yield ValueRange.of(range.getMinimum() - YEARS_DIFFERENCE * 12L, range.getMaximum() - YEARS_DIFFERENCE * 12L);
            }
            case YEAR_OF_ERA -> {
                ValueRange range = YEAR.range();
                yield ValueRange.of(1, range.getMaximum() - YEARS_DIFFERENCE, -range.getMinimum() + 1 + YEARS_DIFFERENCE);
            }
            case YEAR -> {
                ValueRange range = YEAR.range();
                yield ValueRange.of(range.getMinimum() - YEARS_DIFFERENCE, range.getMaximum() - YEARS_DIFFERENCE);
            }
            default -> field.range();
        };
    }

    //-----------------------------------------------------------------------
    @Override  // override for return type
    public MinguoDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (MinguoDate) super.resolveDate(fieldValues, resolverStyle);
    }

    //-----------------------------------------------------------------------
    /**
     * {@code MinguoChronology} is an ISO based chronology, which supports fields
     * in {@link IsoFields}, such as {@link IsoFields#DAY_OF_QUARTER DAY_OF_QUARTER}
     * and {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR}.
     * @see IsoFields
     * @return {@code true}
     * @since 19
     */
    @Override
    public boolean isIsoBased() {
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Writes the Chronology using a
     * <a href="{@docRoot}/serialized-form.html#java.base.share.classes.java.time.chrono.Ser">dedicated serialized form</a>.
     * @serialData
     * <pre>
     *  out.writeByte(1);     // identifies a Chronology
     *  out.writeUTF(getId());
     * </pre>
     *
     * @return the instance of {@code Ser}, not null
     */
    @Override
    @java.io.Serial
    Object writeReplace() {
        return super.writeReplace();
    }

    /**
     * Defend against malicious streams.
     *
     * @param s the stream to read
     * @throws InvalidObjectException always
     */
    @java.io.Serial
    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
