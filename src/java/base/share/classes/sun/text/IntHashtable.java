/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.text;

/** Simple internal class for doing hash mapping. Much, much faster than the
 * standard Hashtable for integer to integer mappings,
 * and doesn't require object creation.<br>
 * If a key is not found, the defaultValue is returned.
 * Note: the keys are limited to values above Integer.MIN_VALUE+1.<br>
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public final class IntHashtable {

    public IntHashtable () {
        initialize(3);
    }

    public IntHashtable (int initialSize) {
        initialize(leastGreaterPrimeIndex((int)(initialSize/HIGH_WATER_FACTOR)));
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void put(int key, int value) {
        if (count > highWaterMark) {
            rehash();
        }
        int index = find(key);
        if (keyList[index] <= MAX_UNUSED) {      // deleted or empty
            keyList[index] = key;
            ++count;
        }
        values[index] = value;                   // reset value
    }

    public int get(int key) {
        return values[find(key)];
    }

    public void remove(int key) {
        int index = find(key);
        if (keyList[index] > MAX_UNUSED) {       // neither deleted nor empty
            keyList[index] = DELETED;            // set to deleted
            values[index] = defaultValue;        // set to default
            --count;
            if (count < lowWaterMark) {
                rehash();
            }
        }
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int newValue) {
        defaultValue = newValue;
        rehash();
    }

    public boolean equals (Object that) {
        if (that.getClass() != this.getClass()) return false;

        IntHashtable other = (IntHashtable) that;
        if (other.size() != count || other.defaultValue != defaultValue) {
                return false;
        }
        for (int i = 0; i < keyList.length; ++i) {
            int key = keyList[i];
            if (key > MAX_UNUSED && other.get(key) != values[i])
                return false;
        }
        return true;
    }

    public int hashCode() {
        // NOTE:  This function isn't actually used anywhere in this package, but it's here
        // in case this class is ever used to make sure we uphold the invariants about
        // hashCode() and equals()

        // WARNING:  This function hasn't undergone rigorous testing to make sure it actually
        // gives good distribution.  We've eyeballed the results, and they appear okay, but
        // you copy this algorithm (or these seed and multiplier values) at your own risk.
        //                                        --rtg 8/17/99

        int result = 465;   // an arbitrary seed value
        int scrambler = 1362796821; // an arbitrary multiplier.
        for (int i = 0; i < keyList.length; ++i) {
            // this line just scrambles the bits as each value is added into the
            // has value.  This helps to make sure we affect all the bits and that
            // the same values in a different order will produce a different hash value
            result = result * scrambler + 1;
            result += keyList[i];
        }
        for (int i = 0; i < values.length; ++i) {
            result = result * scrambler + 1;
            result += values[i];
        }
        return result;
    }

    public Object clone ()
                    throws CloneNotSupportedException {
        IntHashtable result = (IntHashtable) super.clone();
        values = values.clone();
        keyList = keyList.clone();
        return result;
    }

    // =======================PRIVATES============================
    private int defaultValue = 0;

    // the tables have to have prime-number lengths. Rather than compute
    // primes, we just keep a table, with the current index we are using.
    private int primeIndex;

    // highWaterFactor determines the maximum number of elements before
    // a rehash. Can be tuned for different performance/storage characteristics.
    private static final float HIGH_WATER_FACTOR = 0.4F;
    private int highWaterMark;

    // lowWaterFactor determines the minimum number of elements before
    // a rehash. Can be tuned for different performance/storage characteristics.
    private static final float LOW_WATER_FACTOR = 0.0F;
    private int lowWaterMark;

    private int count;

    // we use two arrays to minimize allocations
    private int[] values;
    private int[] keyList;

    private static final int EMPTY   = Integer.MIN_VALUE;
    private static final int DELETED = EMPTY + 1;
    private static final int MAX_UNUSED = DELETED;

    private void initialize (int primeIndex) {
        if (primeIndex < 0) {
            primeIndex = 0;
        } else if (primeIndex >= PRIMES.length) {
            System.out.println("TOO BIG");
            primeIndex = PRIMES.length - 1;
            // throw new java.util.IllegalArgumentError();
        }
        this.primeIndex = primeIndex;
        int initialSize = PRIMES[primeIndex];
        values = new int[initialSize];
        keyList = new int[initialSize];
        for (int i = 0; i < initialSize; ++i) {
            keyList[i] = EMPTY;
            values[i] = defaultValue;
        }
        count = 0;
        lowWaterMark = (int)(initialSize * LOW_WATER_FACTOR);
        highWaterMark = (int)(initialSize * HIGH_WATER_FACTOR);
    }

    private void rehash() {
        int[] oldValues = values;
        int[] oldkeyList = keyList;
        int newPrimeIndex = primeIndex;
        if (count > highWaterMark) {
            ++newPrimeIndex;
        } else if (count < lowWaterMark) {
            newPrimeIndex -= 2;
        }
        initialize(newPrimeIndex);
        for (int i = oldValues.length - 1; i >= 0; --i) {
            int key = oldkeyList[i];
            if (key > MAX_UNUSED) {
                    putInternal(key, oldValues[i]);
            }
        }
    }

    public void putInternal (int key, int value) {
        int index = find(key);
        if (keyList[index] < MAX_UNUSED) {      // deleted or empty
            keyList[index] = key;
            ++count;
        }
        values[index] = value;                  // reset value
    }

    private int find (int key) {
        if (key <= MAX_UNUSED)
            throw new IllegalArgumentException("key can't be less than 0xFFFFFFFE");
        int firstDeleted = -1;  // assume invalid index
        int index = (key ^ 0x4000000) % keyList.length;
        if (index < 0) index = -index; // positive only
        int jump = 0; // lazy evaluate
        while (true) {
            int tableHash = keyList[index];
            if (tableHash == key) {                 // quick check
                return index;
            } else if (tableHash > MAX_UNUSED) {    // neither correct nor unused
                // ignore
            } else if (tableHash == EMPTY) {        // empty, end o' the line
                if (firstDeleted >= 0) {
                    index = firstDeleted;           // reset if had deleted slot
                }
                return index;
            } else if (firstDeleted < 0) {          // remember first deleted
                    firstDeleted = index;
            }
            if (jump == 0) {                        // lazy compute jump
                jump = (key % (keyList.length - 1));
                if (jump < 0) jump = -jump;
                ++jump;
            }

            index = (index + jump) % keyList.length;
            if (index == firstDeleted) {
                // We've searched all entries for the given key.
                return index;
            }
        }
    }

    private static int leastGreaterPrimeIndex(int source) {
        int i;
        for (i = 0; i < PRIMES.length; ++i) {
            if (source < PRIMES[i]) {
                break;
            }
        }
        return (i == 0) ? 0 : (i - 1);
    }

    // This list is the result of buildList below. Can be tuned for different
    // performance/storage characteristics.
    private static final int[] PRIMES = {
        17, 37, 67, 131, 257,
        521, 1031, 2053, 4099, 8209, 16411, 32771, 65537,
        131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259,
        33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, 2147483647
    };
}
