/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.lang.ref.Reference;
import java.base.share.classes.java.security.MessageDigest;
import java.base.share.classes.java.security.KeyRep;
import java.base.share.classes.java.security.InvalidKeyException;
import java.base.share.classes.javax.crypto.SecretKey;
import java.base.share.classes.javax.crypto.spec.DESKeySpec;

import java.base.share.classes.jdk.internal.ref.CleanerFactory;

/**
 * This class represents a DES key.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

final class DESKey implements SecretKey {

    @java.io.Serial
    static final long serialVersionUID = 7724971015953279128L;

    private byte[] key;

    /**
     * Uses the first 8 bytes of the given key as the DES key.
     *
     * @param key the buffer with the DES key bytes.
     *
     * @exception InvalidKeyException if less than 8 bytes are available for
     * the key.
     */
    DESKey(byte[] key) throws InvalidKeyException {
        this(key, 0);
    }

    /**
     * Uses the first 8 bytes in <code>key</code>, beginning at
     * <code>offset</code>, as the DES key
     *
     * @param key the buffer with the DES key bytes.
     * @param offset the offset in <code>key</code>, where the DES key bytes
     * start.
     *
     * @exception InvalidKeyException if less than 8 bytes are available for
     * the key.
     */
    DESKey(byte[] key, int offset) throws InvalidKeyException {
        if (key == null || key.length - offset < DESKeySpec.DES_KEY_LEN) {
            throw new InvalidKeyException("Wrong key size");
        }
        this.key = new byte[DESKeySpec.DES_KEY_LEN];
        System.arraycopy(key, offset, this.key, 0, DESKeySpec.DES_KEY_LEN);
        DESKeyGenerator.setParityBit(this.key, 0);

        // Use the cleaner to zero the key when no longer referenced
        final byte[] k = this.key;
        CleanerFactory.cleaner().register(this,
                () -> java.util.Arrays.fill(k, (byte)0x00));
    }

    public byte[] getEncoded() {
        // Return a copy of the key, rather than a reference,
        // so that the key data cannot be modified from outside

        // The key is zeroized by finalize()
        // The reachability fence ensures finalize() isn't called early
        byte[] result = key.clone();
        Reference.reachabilityFence(this);
        return result;
    }

    public String getAlgorithm() {
        return "DES";
    }

    public String getFormat() {
        return "RAW";
    }

    /**
     * Calculates a hash code value for the object.
     * Objects that are equal will also have the same hashcode.
     */
    public int hashCode() {
        int retval = 0;
        for (int i = 1; i < this.key.length; i++) {
            retval += this.key[i] * i;
        }
        return(retval ^= "des".hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof SecretKey))
            return false;

        String thatAlg = ((SecretKey)obj).getAlgorithm();
        if (!(thatAlg.equalsIgnoreCase("DES")))
            return false;

        byte[] thatKey = ((SecretKey)obj).getEncoded();
        boolean ret = MessageDigest.isEqual(this.key, thatKey);
        java.util.Arrays.fill(thatKey, (byte)0x00);
        return ret;
    }

    /**
     * readObject is called to restore the state of this key from
     * a stream.
     */
    @java.io.Serial
    private void readObject(java.io.ObjectInputStream s)
         throws java.io.IOException, ClassNotFoundException
    {
        s.defaultReadObject();
        key = key.clone();
    }

    /**
     * Replace the DES key to be serialized.
     *
     * @return the standard KeyRep object to be serialized
     *
     * @throws java.io.ObjectStreamException if a new object representing
     * this DES key could not be created
     */
    @java.io.Serial
    private Object writeReplace() throws java.io.ObjectStreamException {
        return new KeyRep(KeyRep.Type.SECRET,
                        getAlgorithm(),
                        getFormat(),
                        key);
    }
}
