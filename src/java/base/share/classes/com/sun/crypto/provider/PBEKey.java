/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.lang.ref.Reference;
import java.security.MessageDigest;
import java.security.KeyRep;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;

import jdk.internal.ref.CleanerFactory;

/**
 * This class represents a PBE key.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 *
 */
final class PBEKey implements SecretKey {

    @java.io.Serial
    static final long serialVersionUID = -2234768909660948176L;

    private byte[] key;

    private String type;

    /**
     * Creates a PBE key from a given PBE key specification.
     *
     * @param keytype the given PBE key specification
     */
    PBEKey(PBEKeySpec keySpec, String keytype, boolean useCleaner)
            throws InvalidKeySpecException {
        char[] passwd = keySpec.getPassword();
        if (passwd == null) {
            // Should allow an empty password.
            passwd = new char[0];
        }
        // Accept "\0" to signify "zero-length password with no terminator".
        if (!(passwd.length == 1 && passwd[0] == 0)) {
            for (int i=0; i<passwd.length; i++) {
                if ((passwd[i] < '\u0020') || (passwd[i] > '\u007E')) {
                    throw new InvalidKeySpecException("Password is not ASCII");
                }
            }
        }
        this.key = new byte[passwd.length];
        for (int i=0; i<passwd.length; i++)
            this.key[i] = (byte) (passwd[i] & 0x7f);
        Arrays.fill(passwd, '\0');
        type = keytype;

        // Use the cleaner to zero the key when no longer referenced
        if (useCleaner) {
            final byte[] k = this.key;
            CleanerFactory.cleaner().register(this,
                () -> Arrays.fill(k, (byte) 0x00));
        }
    }

    public byte[] getEncoded() {
        // The key is zeroized by finalize()
        // The reachability fence ensures finalize() isn't called early
        byte[] result = key.clone();
        Reference.reachabilityFence(this);
        return result;
    }

    public String getAlgorithm() {
        return type;
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
        return(retval ^= getAlgorithm().toLowerCase(Locale.ENGLISH).hashCode());
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof SecretKey))
            return false;

        SecretKey that = (SecretKey)obj;

        if (!(that.getAlgorithm().equalsIgnoreCase(type)))
            return false;

        byte[] thatEncoded = that.getEncoded();
        boolean ret = MessageDigest.isEqual(this.key, thatEncoded);
        Arrays.fill(thatEncoded, (byte)0x00);
        return ret;
    }

    /**
     * Clears the internal copy of the key.
     *
     */
    @Override
    public void destroy() {
        if (key != null) {
            Arrays.fill(key, (byte) 0x00);
            key = null;
        }
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
     * Replace the PBE key to be serialized.
     *
     * @return the standard KeyRep object to be serialized
     *
     * @throws java.io.ObjectStreamException if a new object representing
     * this PBE key could not be created
     */
    @java.io.Serial
    private Object writeReplace() throws java.io.ObjectStreamException {
        return new KeyRep(KeyRep.Type.SECRET,
                getAlgorithm(),
                getFormat(),
                key);
    }
}
