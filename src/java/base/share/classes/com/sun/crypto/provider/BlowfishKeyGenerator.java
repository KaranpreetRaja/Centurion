/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.base.share.classes.java.security.SecureRandom;
import java.base.share.classes.java.security.InvalidParameterException;
import java.base.share.classes.java.security.InvalidAlgorithmParameterException;
import java.base.share.classes.java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.base.share.classes.javax.crypto.KeyGeneratorSpi;
import java.base.share.classes.javax.crypto.SecretKey;
import java.base.share.classes.javax.crypto.spec.SecretKeySpec;

/**
 * This class generates a secret key for use with the Blowfish algorithm.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

public final class BlowfishKeyGenerator extends KeyGeneratorSpi {

    private SecureRandom random = null;
    private int keysize = 16; // default keysize (in number of bytes)

    /**
     * Empty constructor
     */
    public BlowfishKeyGenerator() {
    }

    /**
     * Initializes this key generator.
     *
     * @param random the source of randomness for this generator
     */
    protected void engineInit(SecureRandom random) {
        this.random = random;
    }

    /**
     * Initializes this key generator with the specified parameter
     * set and a user-provided source of randomness.
     *
     * @param params the key generation parameters
     * @param random the source of randomness for this key generator
     *
     * @exception InvalidAlgorithmParameterException if <code>params</code> is
     * inappropriate for this key generator
     */
    protected void engineInit(AlgorithmParameterSpec params,
                              SecureRandom random)
        throws InvalidAlgorithmParameterException
    {
        throw new InvalidAlgorithmParameterException
            ("Blowfish key generation does not take any parameters");
    }

    /**
     * Initializes this key generator for a certain keysize, using the given
     * source of randomness.
     *
     * @param keysize the keysize. This is an algorithm-specific
     * metric specified in number of bits.
     * @param random the source of randomness for this key generator
     */
    protected void engineInit(int keysize, SecureRandom random) {
        if (((keysize % 8) != 0) || (keysize < 32) || (keysize > 448)) {
            throw new InvalidParameterException("Keysize must be "
                                                + "multiple of 8, and can "
                                                + "only range from 32 to 448 "
                                                + "(inclusive)");
        }
        this.keysize = keysize / 8;
        this.engineInit(random);
    }

    /**
     * Generates a Blowfish key.
     *
     * @return the new Blowfish key
     */
    protected SecretKey engineGenerateKey() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }

        byte[] keyBytes = new byte[this.keysize];
        this.random.nextBytes(keyBytes);

        try {
            return new SecretKeySpec(keyBytes, "Blowfish");
        } finally {
            Arrays.fill(keyBytes, (byte)0);
        }
    }
}
