/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.linux.classes.jdk.internal.platform;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Cgroup version agnostic controller logic
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
public interface CgroupSubsystemController {

    public static final String EMPTY_STR = "";

    public String path();

    /**
     * getStringValue
     *
     * Return the first line of the file "param" argument from the controller.
     *
     * TODO:  Consider using weak references for caching BufferedReader object.
     *
     * @param controller
     * @param param
     * @return Returns the contents of the file specified by param or null if
     *         an error occurs.
     */
    public static String getStringValue(CgroupSubsystemController controller, String param) {
        if (controller == null) return null;

        try {
            return CgroupUtil.readStringValue(controller, param);
        }
        catch (IOException e) {
            return null;
        }

    }

    /**
     * Get an entry from file "param" within the "controller" directory path
     * which matches string "match". Applies "conversion" to the matching line.
     *
     * @param controller
     * @param param
     * @param match
     * @param conversion
     * @param defaultRetval
     * @return The long value as derived by applying "conversion" to the matching
     *         line or "defaultRetval" if there was an error or no match found.
     */
    public static long getLongValueMatchingLine(CgroupSubsystemController controller,
                                                     String param,
                                                     String match,
                                                     Function<String, Long> conversion,
                                                     long defaultRetval) {
        long retval = defaultRetval;
        if (controller == null) {
            return retval;
        }
        try {
            Path filePath = Paths.get(controller.path(), param);
            List<String> lines = CgroupUtil.readAllLinesPrivileged(filePath);
            for (String line : lines) {
                if (line.startsWith(match)) {
                    retval = conversion.apply(line);
                    break;
                }
            }
        } catch (IOException e) {
            // Ignore. Default is unlimited.
        }
        return retval;
    }

    /**
     * Get a long value from directory "controller" and file "param", by
     * applying "conversion" to the string value within the file.
     *
     * @param controller
     * @param param
     * @param conversion
     * @param defaultRetval
     * @return The converted long value or "defaultRetval" if there was an
     *         error.
     */
    public static long getLongValue(CgroupSubsystemController controller,
                                    String param,
                                    Function<String, Long> conversion,
                                    long defaultRetval) {
        String strval = getStringValue(controller, param);
        if (strval == null) return defaultRetval;
        return conversion.apply(strval);
    }

    /**
     * Get a double value from file "param" within "controller".
     *
     * @param controller
     * @param param
     * @param defaultRetval
     * @return The double value or "defaultRetval" if there was an error.
     */
    public static double getDoubleValue(CgroupSubsystemController controller, String param, double defaultRetval) {
        String strval = getStringValue(controller, param);

        if (strval == null) return defaultRetval;

        double retval = Double.parseDouble(strval);

        return retval;
    }

    /**
     * getLongEntry
     *
     * Return the long value from the line containing the string "entryname"
     * within file "param" in the "controller".
     *
     * TODO:  Consider using weak references for caching BufferedReader object.
     *
     * @param controller
     * @param param
     * @param entryname
     * @return long value or "defaultRetval" if there was an error or no match
     *         was found.
     */
    public static long getLongEntry(CgroupSubsystemController controller, String param, String entryname, long defaultRetval) {
        if (controller == null) return defaultRetval;

        try (Stream<String> lines = CgroupUtil.readFilePrivileged(Paths.get(controller.path(), param))) {

            Optional<String> result = lines.map(line -> line.split(" "))
                                           .filter(line -> (line.length == 2 &&
                                                   line[0].equals(entryname)))
                                           .map(line -> line[1])
                                           .findFirst();

            return result.isPresent() ? Long.parseLong(result.get()) : defaultRetval;
        } catch (UncheckedIOException | IOException e) {
            return defaultRetval;
        }
    }

    /**
     * stringRangeToIntArray
     *
     * Convert a string in the form of  1,3-4,6 to an array of
     * integers containing all the numbers in the range.
     *
     * @param range
     * @return int[] containing a sorted list of numbers as represented by
     *         the string range. Returns null if there was an error or the input
     *         was an empty string.
     */
    public static int[] stringRangeToIntArray(String range) {
        if (range == null || EMPTY_STR.equals(range)) return null;

        ArrayList<Integer> results = new ArrayList<>();
        String strs[] = range.split(",");
        for (String str : strs) {
            if (str.contains("-")) {
                String lohi[] = str.split("-");
                // validate format
                if (lohi.length != 2) {
                    continue;
                }
                int lo = Integer.parseInt(lohi[0]);
                int hi = Integer.parseInt(lohi[1]);
                for (int i = lo; i <= hi; i++) {
                    results.add(i);
                }
            }
            else {
                results.add(Integer.parseInt(str));
            }
        }

        // sort results
        results.sort(null);

        // convert ArrayList to primitive int array
        int[] ints = new int[results.size()];
        int i = 0;
        for (Integer n : results) {
            ints[i++] = n;
        }

        return ints;
    }

    /**
     * Convert a number from its string representation to a long.
     *
     * @param strval
     * @param overflowRetval
     * @param defaultRetval
     * @return The converted long value. "overflowRetval" is returned if the
     *         string representation exceeds the range of type long.
     *         "defaultRetval" is returned if another type of error occurred
     *         during conversion.
     */
    public static long convertStringToLong(String strval, long overflowRetval, long defaultRetval) {
        long retval = defaultRetval;
        if (strval == null) return retval;

        try {
            retval = Long.parseLong(strval);
        } catch (NumberFormatException e) {
            // For some properties (e.g. memory.limit_in_bytes, cgroups v1) we may overflow
            // the range of signed long. In this case, return overflowRetval
            BigInteger b = new BigInteger(strval);
            if (b.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                return overflowRetval;
            }
        }
        return retval;
    }

}
