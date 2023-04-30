/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.text;

/**
 * A RuleBasedCollationKey is a concrete implementation of CollationKey class.
 * The RuleBasedCollationKey class is used by the RuleBasedCollator class.
 */

final class RuleBasedCollationKey extends CollationKey {
    /**
     * Compare this RuleBasedCollationKey to target. The collation rules of the
     * Collator object which created these keys are applied. <strong>Note:</strong>
     * RuleBasedCollationKeys created by different Collators can not be compared.
     * @param target target RuleBasedCollationKey
     * @return Returns an integer value. Value is less than zero if this is less
     * than target, value is zero if this and target are equal and value is greater than
     * zero if this is greater than target.
     * @see java.base.share.classes.java.text.Collator#compare
     */
    public int compareTo(CollationKey target)
    {
        int result = key.compareTo(((RuleBasedCollationKey)(target)).key);
        if (result <= Collator.LESS)
            return Collator.LESS;
        else if (result >= Collator.GREATER)
            return Collator.GREATER;
        return Collator.EQUAL;
    }

    /**
     * Compare this RuleBasedCollationKey and the target for equality.
     * The collation rules of the Collator object which created these keys are applied.
     * <strong>Note:</strong> RuleBasedCollationKeys created by different Collators can not be
     * compared.
     * @param target the RuleBasedCollationKey to compare to.
     * @return Returns true if two objects are equal, false otherwise.
     */
    public boolean equals(Object target) {
        if (this == target) return true;
        if (target == null || !getClass().equals(target.getClass())) {
            return false;
        }
        RuleBasedCollationKey other = (RuleBasedCollationKey)target;
        return key.equals(other.key);
    }

    /**
     * Creates a hash code for this RuleBasedCollationKey. The hash value is calculated on the
     * key itself, not the String from which the key was created.  Thus
     * if x and y are RuleBasedCollationKeys, then x.hashCode(x) == y.hashCode() if
     * x.equals(y) is true.  This allows language-sensitive comparison in a hash table.
     * See the CollatinKey class description for an example.
     * @return the hash value based on the string's collation order.
     */
    public int hashCode() {
        return (key.hashCode());
    }

    /**
     * Converts the RuleBasedCollationKey to a sequence of bits. If two RuleBasedCollationKeys
     * could be legitimately compared, then one could compare the byte arrays
     * for each of those keys to obtain the same result.  Byte arrays are
     * organized most significant byte first.
     */
    public byte[] toByteArray() {

        char[] src = key.toCharArray();
        byte[] dest = new byte[ 2*src.length ];
        int j = 0;
        for( int i=0; i<src.length; i++ ) {
            dest[j++] = (byte)(src[i] >>> 8);
            dest[j++] = (byte)(src[i] & 0x00ff);
        }
        return dest;
    }

    /**
     * A RuleBasedCollationKey can only be generated by Collator objects.
     */
    RuleBasedCollationKey(String source, String key) {
        super(source);
        this.key = key;
    }
    private String key = null;

}
