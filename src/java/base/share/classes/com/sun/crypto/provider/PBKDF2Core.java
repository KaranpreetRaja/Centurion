/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class implements a key factory for PBE keys derived using
 * PBKDF2 with HmacSHA1, HmacSHA224, HmacSHA256, HmacSHA384, HmacSHA512,
 * HmacSHA512/224, and HmacSHA512/256 pseudo random function (PRF) as
 * defined in PKCS#5 v2.1.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 *
 */
abstract class PBKDF2Core extends SecretKeyFactorySpi {

    private final String prfAlgo;

    PBKDF2Core(String prfAlgo) {
        this.prfAlgo = prfAlgo;
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
        if (keySpec instanceof PBEKeySpec ks) {
            return new PBKDF2KeyImpl(ks, prfAlgo);
        } else {
            throw new InvalidKeySpecException("Only PBEKeySpec is accepted");
        }
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
     * @exception InvalidKeySpecException if the requested key
     * specification is inappropriate for the given key, or the
     * given key cannot be processed (e.g., the given key has an
     * unrecognized algorithm or format).
     */
    protected KeySpec engineGetKeySpec(SecretKey key, Class<?> keySpecCl)
        throws InvalidKeySpecException {
        if (key instanceof javax.crypto.interfaces.PBEKey pKey) {
            // Check if requested key spec is amongst the valid ones
            if ((keySpecCl != null)
                    && keySpecCl.isAssignableFrom(PBEKeySpec.class)) {
                char[] passwd = pKey.getPassword();
                byte[] encoded = pKey.getEncoded();
                try {
                    return new PBEKeySpec(passwd, pKey.getSalt(),
                            pKey.getIterationCount(), encoded.length * 8);
                } finally {
                    if (passwd != null) {
                        Arrays.fill(passwd, (char) 0);
                    }
                    Arrays.fill(encoded, (byte)0);
                }
            } else {
                throw new InvalidKeySpecException
                        ("Only PBEKeySpec is accepted");
            }
        } else {
            throw new InvalidKeySpecException("Only PBEKey is accepted");
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
        throws InvalidKeyException {
        if ((key != null) &&
            (key.getAlgorithm().equalsIgnoreCase("PBKDF2With" + prfAlgo)) &&
            (key.getFormat().equalsIgnoreCase("RAW"))) {

            // Check if key originates from this factory
            if (key instanceof java.base.share.classes.com.sun.crypto.provider.PBKDF2KeyImpl) {
                return key;
            }
            // Check if key implements the PBEKey
            if (key instanceof javax.crypto.interfaces.PBEKey pKey) {
                char[] password = pKey.getPassword();
                byte[] encoding = pKey.getEncoded();
                PBEKeySpec spec =
                        new PBEKeySpec(password,
                                pKey.getSalt(),
                                pKey.getIterationCount(),
                                encoding.length*8);
                try {
                    return new PBKDF2KeyImpl(spec, prfAlgo);
                } catch (InvalidKeySpecException re) {
                    throw new InvalidKeyException
                        ("Invalid key component(s)", re);
                } finally {
                    if (password != null) {
                        Arrays.fill(password, (char) 0);
                        spec.clearPassword();
                    }
                    Arrays.fill(encoding, (byte)0);
                }
            } else {
                throw new InvalidKeyException("Only PBEKey is accepted");
            }
        }
        throw new InvalidKeyException("Only PBKDF2With" + prfAlgo +
                " key with RAW format is accepted");
    }

    public static final class HmacSHA1 extends PBKDF2Core {
        public HmacSHA1() {
            super("HmacSHA1");
        }
    }

    public static final class HmacSHA224 extends PBKDF2Core {
        public HmacSHA224() {
            super("HmacSHA224");
        }
    }

    public static final class HmacSHA256 extends PBKDF2Core {
        public HmacSHA256() {
            super("HmacSHA256");
        }
    }

    public static final class HmacSHA384 extends PBKDF2Core {
        public HmacSHA384() {
            super("HmacSHA384");
        }
    }

    public static final class HmacSHA512 extends PBKDF2Core {
        public HmacSHA512() {
            super("HmacSHA512");
        }
    }

    public static final class HmacSHA512_224 extends PBKDF2Core {
        public HmacSHA512_224() {
            super("HmacSHA512/224");
        }
    }

    public static final class HmacSHA512_256 extends PBKDF2Core {
        public HmacSHA512_256() {
            super("HmacSHA512/256");
        }
    }
}
