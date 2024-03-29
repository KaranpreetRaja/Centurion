/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.PBEKeySpec;
import java.util.HashSet;
import java.util.Locale;

/**
 * This class implements a key factory for PBE keys according to PKCS#5,
 * meaning that the password must consist of printable ASCII characters
 * (values 32 to 126 decimal inclusive) and only the low order 8 bits
 * of each password character are used.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 *
 */
abstract class PBEKeyFactory extends SecretKeyFactorySpi {

    private String type;
    private static HashSet<String> validTypes;

    /**
     * Simple constructor
     */
    private PBEKeyFactory(String keytype) {
        type = keytype;
    }

    static {
        validTypes = HashSet.newHashSet(17);
        validTypes.add("PBEWithMD5AndDES".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithSHA1AndDESede".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithSHA1AndRC2_40".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithSHA1AndRC2_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithSHA1AndRC4_40".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithSHA1AndRC4_128".toUpperCase(Locale.ENGLISH));
        // Proprietary algorithm.
        validTypes.add("PBEWithMD5AndTripleDES".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA1AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA224AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA256AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA384AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA512AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA512/224AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA512/256AndAES_128".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA1AndAES_256".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA224AndAES_256".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA256AndAES_256".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA384AndAES_256".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA512AndAES_256".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA512/224AndAES_256".toUpperCase(Locale.ENGLISH));
        validTypes.add("PBEWithHmacSHA512/256AndAES_256".toUpperCase(Locale.ENGLISH));
    }

    public static final class PBEWithMD5AndDES extends PBEKeyFactory {
        public PBEWithMD5AndDES() {
            super("PBEWithMD5AndDES");
        }
    }

    public static final class PBEWithSHA1AndDESede extends PBEKeyFactory {
        public PBEWithSHA1AndDESede() {
            super("PBEWithSHA1AndDESede");
        }
    }

    public static final class PBEWithSHA1AndRC2_40 extends PBEKeyFactory {
        public PBEWithSHA1AndRC2_40() {
            super("PBEWithSHA1AndRC2_40");
        }
    }

    public static final class PBEWithSHA1AndRC2_128 extends PBEKeyFactory {
        public PBEWithSHA1AndRC2_128() {
            super("PBEWithSHA1AndRC2_128");
        }
    }

    public static final class PBEWithSHA1AndRC4_40 extends PBEKeyFactory {
        public PBEWithSHA1AndRC4_40() {
            super("PBEWithSHA1AndRC4_40");
        }
    }

    public static final class PBEWithSHA1AndRC4_128 extends PBEKeyFactory {
        public PBEWithSHA1AndRC4_128() {
            super("PBEWithSHA1AndRC4_128");
        }
    }

    /*
     * Private proprietary algorithm for supporting JCEKS.
     */
    public static final class PBEWithMD5AndTripleDES extends PBEKeyFactory {
        public PBEWithMD5AndTripleDES() {
            super("PBEWithMD5AndTripleDES");
        }
    }

    public static final class PBEWithHmacSHA1AndAES_128 extends PBEKeyFactory {
        public PBEWithHmacSHA1AndAES_128() {
            super("PBEWithHmacSHA1AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA224AndAES_128
            extends PBEKeyFactory {
        public PBEWithHmacSHA224AndAES_128() {
            super("PBEWithHmacSHA224AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA256AndAES_128
            extends PBEKeyFactory {
        public PBEWithHmacSHA256AndAES_128() {
            super("PBEWithHmacSHA256AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA384AndAES_128
            extends PBEKeyFactory {
        public PBEWithHmacSHA384AndAES_128() {
            super("PBEWithHmacSHA384AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA512AndAES_128
            extends PBEKeyFactory {
        public PBEWithHmacSHA512AndAES_128() {
            super("PBEWithHmacSHA512AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA512_224AndAES_128
            extends PBEKeyFactory {
        public PBEWithHmacSHA512_224AndAES_128() {
            super("PBEWithHmacSHA512/224AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA512_256AndAES_128
            extends PBEKeyFactory {
        public PBEWithHmacSHA512_256AndAES_128() {
            super("PBEWithHmacSHA512/256AndAES_128");
        }
    }

    public static final class PBEWithHmacSHA1AndAES_256 extends PBEKeyFactory {
        public PBEWithHmacSHA1AndAES_256() {
            super("PBEWithHmacSHA1AndAES_256");
        }
    }

    public static final class PBEWithHmacSHA224AndAES_256
            extends PBEKeyFactory {
        public PBEWithHmacSHA224AndAES_256() {
            super("PBEWithHmacSHA224AndAES_256");
        }
    }

    public static final class PBEWithHmacSHA256AndAES_256
            extends PBEKeyFactory {
        public PBEWithHmacSHA256AndAES_256() {
            super("PBEWithHmacSHA256AndAES_256");
        }
    }

    public static final class PBEWithHmacSHA384AndAES_256
            extends PBEKeyFactory {
        public PBEWithHmacSHA384AndAES_256() {
            super("PBEWithHmacSHA384AndAES_256");
        }
    }

    public static final class PBEWithHmacSHA512AndAES_256
            extends PBEKeyFactory {
        public PBEWithHmacSHA512AndAES_256() {
            super("PBEWithHmacSHA512AndAES_256");
        }
    }

    public static final class PBEWithHmacSHA512_224AndAES_256
            extends PBEKeyFactory {
        public PBEWithHmacSHA512_224AndAES_256() {
            super("PBEWithHmacSHA512/224AndAES_256");
        }
    }

    public static final class PBEWithHmacSHA512_256AndAES_256
            extends PBEKeyFactory {
        public PBEWithHmacSHA512_256AndAES_256() {
            super("PBEWithHmacSHA512/256AndAES_256");
        }
    }

    /**
     * Generates a <code>SecretKey</code> object from the provided key
     * specification (key material).
     *
     * @param keySpec the specification (key material) of the secret key
     *
     * @return the secret key
     *
     * @exception InvalidKeySpecException if the given key specification
     * is inappropriate for this key factory to produce a public key.
     */
    protected SecretKey engineGenerateSecret(KeySpec keySpec)
        throws InvalidKeySpecException
    {
        if (!(keySpec instanceof PBEKeySpec)) {
            throw new InvalidKeySpecException("Invalid key spec");
        }
        return new PBEKey((PBEKeySpec)keySpec, type, true);
    }

    /**
     * Returns a specification (key material) of the given key
     * in the requested format.
     *
     * @param key the key
     *
     * @param keySpecCl the requested format in which the key material shall be
     * returned
     *
     * @return the underlying key specification (key material) in the
     * requested format
     *
     * @exception InvalidKeySpecException if the requested key specification is
     * inappropriate for the given key, or the given key cannot be processed
     * (e.g., the given key has an unrecognized algorithm or format).
     */
    protected KeySpec engineGetKeySpec(SecretKey key, Class<?> keySpecCl)
        throws InvalidKeySpecException {
        if ((key instanceof SecretKey)
            && (validTypes.contains(key.getAlgorithm().toUpperCase(Locale.ENGLISH)))
            && (key.getFormat().equalsIgnoreCase("RAW"))) {

            // Check if requested key spec is amongst the valid ones
            if ((keySpecCl != null)
                    && keySpecCl.isAssignableFrom(PBEKeySpec.class)) {
                byte[] passwdBytes = key.getEncoded();
                char[] passwdChars = new char[passwdBytes.length];
                for (int i=0; i<passwdChars.length; i++)
                    passwdChars[i] = (char) (passwdBytes[i] & 0x7f);
                PBEKeySpec ret = new PBEKeySpec(passwdChars);
                // password char[] was cloned in PBEKeySpec constructor,
                // so we can zero it out here
                java.util.Arrays.fill(passwdChars, ' ');
                java.util.Arrays.fill(passwdBytes, (byte)0x00);
                return ret;
            } else {
                throw new InvalidKeySpecException("Invalid key spec");
            }
        } else {
            throw new InvalidKeySpecException("Invalid key "
                                              + "format/algorithm");
        }
    }

    /**
     * Translates a <code>SecretKey</code> object, whose provider may be
     * unknown or potentially untrusted, into a corresponding
     * <code>SecretKey</code> object of this key factory.
     *
     * @param key the key whose provider is unknown or untrusted
     *
     * @return the translated key
     *
     * @exception InvalidKeyException if the given key cannot be processed by
     * this key factory.
     */
    protected SecretKey engineTranslateKey(SecretKey key)
        throws InvalidKeyException
    {
        try {
            if ((key != null) &&
                (validTypes.contains(key.getAlgorithm().toUpperCase(Locale.ENGLISH))) &&
                (key.getFormat().equalsIgnoreCase("RAW"))) {

                // Check if key originates from this factory
                if (key instanceof java.base.share.classes.com.sun.crypto.provider.PBEKey) {
                    return key;
                }

                // Convert key to spec
                PBEKeySpec pbeKeySpec = (PBEKeySpec)engineGetKeySpec
                    (key, PBEKeySpec.class);

                try {
                    // Create key from spec, and return it
                    return engineGenerateSecret(pbeKeySpec);
                } finally {
                    pbeKeySpec.clearPassword();
                }
            } else {
                throw new InvalidKeyException("Invalid key format/algorithm");
            }

        } catch (InvalidKeySpecException ikse) {
            throw new InvalidKeyException("Cannot translate key: "
                                          + ikse.getMessage());
        }
    }
}
