/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;

/**
 * The range of valid values for a date-time field.
 * <p>
 * All {@link TemporalField} instances have a valid range of values.
 * For example, the ISO day-of-month runs from 1 to somewhere between 28 and 31.
 * This class captures that valid range.
 * <p>
 * It is important to be aware of the limitations of this class.
 * Only the minimum and maximum values are provided.
 * It is possible for there to be invalid values within the outer range.
 * For example, a weird field may have valid values of 1, 2, 4, 6, 7, thus
 * have a range of '1 - 7', despite that fact that values 3 and 5 are invalid.
 * <p>
 * Instances of this class are not tied to a specific field.
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @since 1.8
 */
public final class ValueRange implements Serializable {

    /**
     * Serialization version.
     */
    @java.io.Serial
    private static final long serialVersionUID = -7317881728594519368L;

    /**
     * The smallest minimum value.
     */
    private final long minSmallest;
    /**
     * The largest minimum value.
     */
    private final long minLargest;
    /**
     * The smallest maximum value.
     */
    private final long maxSmallest;
    /**
     * The largest maximum value.
     */
    private final long maxLargest;

    /**
     * Obtains a fixed value range.
     * <p>
     * This factory obtains a range where the minimum and maximum values are fixed.
     * For example, the ISO month-of-year always runs from 1 to 12.
     *
     * @param min  the minimum value
     * @param max  the maximum value
     * @return the ValueRange for min, max, not null
     * @throws IllegalArgumentException if the minimum is greater than the maximum
     */
    public static ValueRange of(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
        return new ValueRange(min, min, max, max);
    }

    /**
     * Obtains a variable value range.
     * <p>
     * This factory obtains a range where the minimum value is fixed and the maximum value may vary.
     * For example, the ISO day-of-month always starts at 1, but ends between 28 and 31.
     *
     * @param min  the minimum value
     * @param maxSmallest  the smallest maximum value
     * @param maxLargest  the largest maximum value
     * @return the ValueRange for min, smallest max, largest max, not null
     * @throws IllegalArgumentException if
     *     the minimum is greater than the smallest maximum,
     *  or the smallest maximum is greater than the largest maximum
     */
    public static ValueRange of(long min, long maxSmallest, long maxLargest) {
        if (min > maxSmallest) {
            throw new IllegalArgumentException("Minimum value must be less than smallest maximum value");
        }
        return of(min, min, maxSmallest, maxLargest);
    }

    /**
     * Obtains a fully variable value range.
     * <p>
     * This factory obtains a range where both the minimum and maximum value may vary.
     *
     * @param minSmallest  the smallest minimum value
     * @param minLargest  the largest minimum value
     * @param maxSmallest  the smallest maximum value
     * @param maxLargest  the largest maximum value
     * @return the ValueRange for smallest min, largest min, smallest max, largest max, not null
     * @throws IllegalArgumentException if
     *     the smallest minimum is greater than the smallest maximum,
     *  or the smallest maximum is greater than the largest maximum,
     *  or the largest minimum is greater than the largest maximum,
     *  or the smallest minimum is greater than the largest minimum
     */
    public static ValueRange of(long minSmallest, long minLargest, long maxSmallest, long maxLargest) {
        if (minSmallest > minLargest) {
            throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
        }
        if (maxSmallest > maxLargest) {
            throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
        }
        if (minLargest > maxLargest) {
            throw new IllegalArgumentException("Largest minimum value must be less than largest maximum value");
        }
        if (minSmallest > maxSmallest) {
            throw new IllegalArgumentException("Smallest minimum value must be less than smallest maximum value");
        }
        return new ValueRange(minSmallest, minLargest, maxSmallest, maxLargest);
    }

    /**
     * Restrictive constructor.
     *
     * @param minSmallest  the smallest minimum value
     * @param minLargest  the largest minimum value
     * @param maxSmallest  the smallest minimum value
     * @param maxLargest  the largest minimum value
     */
    private ValueRange(long minSmallest, long minLargest, long maxSmallest, long maxLargest) {
        this.minSmallest = minSmallest;
        this.minLargest = minLargest;
        this.maxSmallest = maxSmallest;
        this.maxLargest = maxLargest;
    }

    //-----------------------------------------------------------------------
    /**
     * Is the value range fixed and fully known.
     * <p>
     * For example, the ISO day-of-month runs from 1 to between 28 and 31.
     * Since there is uncertainty about the maximum value, the range is not fixed.
     * However, for the month of January, the range is always 1 to 31, thus it is fixed.
     *
     * @return true if the set of values is fixed
     */
    public boolean isFixed() {
        return minSmallest == minLargest && maxSmallest == maxLargest;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the minimum value that the field can take.
     * <p>
     * For example, the ISO day-of-month always starts at 1.
     * The minimum is therefore 1.
     *
     * @return the minimum value for this field
     */
    public long getMinimum() {
        return minSmallest;
    }

    /**
     * Gets the largest possible minimum value that the field can take.
     * <p>
     * For example, the ISO day-of-month always starts at 1.
     * The largest minimum is therefore 1.
     *
     * @return the largest possible minimum value for this field
     */
    public long getLargestMinimum() {
        return minLargest;
    }

    /**
     * Gets the smallest possible maximum value that the field can take.
     * <p>
     * For example, the ISO day-of-month runs to between 28 and 31 days.
     * The smallest maximum is therefore 28.
     *
     * @return the smallest possible maximum value for this field
     */
    public long getSmallestMaximum() {
        return maxSmallest;
    }

    /**
     * Gets the maximum value that the field can take.
     * <p>
     * For example, the ISO day-of-month runs to between 28 and 31 days.
     * The maximum is therefore 31.
     *
     * @return the maximum value for this field
     */
    public long getMaximum() {
        return maxLargest;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if all values in the range fit in an {@code int}.
     * <p>
     * This checks that all valid values are within the bounds of an {@code int}.
     * <p>
     * For example, the ISO month-of-year has values from 1 to 12, which fits in an {@code int}.
     * By comparison, ISO nano-of-day runs from 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This implementation uses {@link #getMinimum()} and {@link #getMaximum()}.
     *
     * @return true if a valid value always fits in an {@code int}
     */
    public boolean isIntValue() {
        return getMinimum() >= Integer.MIN_VALUE && getMaximum() <= Integer.MAX_VALUE;
    }

    /**
     * Checks if the value is within the valid range.
     * <p>
     * This checks that the value is within the stored range of values.
     *
     * @param value  the value to check
     * @return true if the value is valid
     */
    public boolean isValidValue(long value) {
        return (value >= getMinimum() && value <= getMaximum());
    }

    /**
     * Checks if the value is within the valid range and that all values
     * in the range fit in an {@code int}.
     * <p>
     * This method combines {@link #isIntValue()} and {@link #isValidValue(long)}.
     *
     * @param value  the value to check
     * @return true if the value is valid and fits in an {@code int}
     */
    public boolean isValidIntValue(long value) {
        return isIntValue() && isValidValue(value);
    }

    /**
     * Checks that the specified value is valid.
     * <p>
     * This validates that the value is within the valid range of values.
     * The field is only used to improve the error message.
     *
     * @param value  the value to check
     * @param field  the field being checked, may be null
     * @return the value that was passed in
     * @see #isValidValue(long)
     */
    public long checkValidValue(long value, TemporalField field) {
        if (isValidValue(value) == false) {
            throw new DateTimeException(genInvalidFieldMessage(field, value));
        }
        return value;
    }

    /**
     * Checks that the specified value is valid and fits in an {@code int}.
     * <p>
     * This validates that the value is within the valid range of values and that
     * all valid values are within the bounds of an {@code int}.
     * The field is only used to improve the error message.
     *
     * @param value  the value to check
     * @param field  the field being checked, may be null
     * @return the value that was passed in
     * @see #isValidIntValue(long)
     */
    public int checkValidIntValue(long value, TemporalField field) {
        if (isValidIntValue(value) == false) {
            throw new DateTimeException(genInvalidFieldMessage(field, value));
        }
        return (int) value;
    }

    private String genInvalidFieldMessage(TemporalField field, long value) {
        if (field != null) {
            return "Invalid value for " + field + " (valid values " + this + "): " + value;
        } else {
            return "Invalid value (valid values " + this + "): " + value;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Restore the state of an ValueRange from the stream.
     * Check that the values are valid.
     *
     * @param s the stream to read
     * @throws IOException if an I/O error occurs
     * @throws InvalidObjectException if
     *     the smallest minimum is greater than the smallest maximum,
     *  or the smallest maximum is greater than the largest maximum
     *  or the largest minimum is greater than the largest maximum
     * @throws ClassNotFoundException if a class cannot be resolved
     */
    @java.io.Serial
    private void readObject(ObjectInputStream s)
         throws IOException, ClassNotFoundException, InvalidObjectException
    {
        s.defaultReadObject();
        if (minSmallest > minLargest) {
            throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value");
        }
        if (maxSmallest > maxLargest) {
            throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value");
        }
        if (minLargest > maxLargest) {
            throw new InvalidObjectException("Minimum value must be less than maximum value");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is equal to another range.
     * <p>
     * The comparison is based on the four values, minimum, largest minimum,
     * smallest maximum and maximum.
     * Only objects of type {@code ValueRange} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other range
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return (obj instanceof ValueRange other)
                && minSmallest == other.minSmallest
                && minLargest == other.minLargest
                && maxSmallest == other.maxSmallest
                && maxLargest == other.maxLargest;
    }

    /**
     * A hash code for this range.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        long hash = minSmallest + (minLargest << 16) + (minLargest >> 48) +
                (maxSmallest << 32) + (maxSmallest >> 32) + (maxLargest << 48) +
                (maxLargest >> 16);
        return Long.hashCode(hash);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this range as a {@code String}.
     * <p>
     * The format will be '{min}/{largestMin} - {smallestMax}/{max}',
     * where the largestMin or smallestMax sections may be omitted, together
     * with associated slash, if they are the same as the min or max.
     *
     * @return a string representation of this range, not null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(minSmallest);
        if (minSmallest != minLargest) {
            buf.append('/').append(minLargest);
        }
        buf.append(" - ").append(maxSmallest);
        if (maxSmallest != maxLargest) {
            buf.append('/').append(maxLargest);
        }
        return buf.toString();
    }

}
