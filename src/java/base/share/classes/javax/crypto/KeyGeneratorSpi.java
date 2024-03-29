/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto;

import java.security.*;
import java.security.spec.*;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the {@code KeyGenerator} class.
 * All the abstract methods in this class must be implemented by each
 * cryptographic service provider who wishes to supply the implementation
 * of a key generator for a particular algorithm.
 *
 * <p>In case the client does not explicitly initialize the KeyGenerator
 * (via a call to an {@code init} method), each provider must
 * supply (and document) a default initialization.
 * See the Keysize Restriction sections of the
 * {@extLink security_guide_jdk_providers JDK Providers}
 * document for information on the KeyGenerator defaults used by
 * JDK providers.
 * However, note that defaults may vary across different providers.
 * Additionally, the default value for a provider may change in a future
 * version. Therefore, it is recommended to explicitly initialize the
 * KeyGenerator instead of relying on provider-specific defaults.
 *
 * @author Jan Luehe
 *
 * @see SecretKey
 * @since 1.4
 */

public abstract class KeyGeneratorSpi {

    /**
     * Constructor for subclasses to call.
     */
    public KeyGeneratorSpi() {}

    /**
     * Initializes the key generator.
     *
     * @param random the source of randomness for this generator
     */
    protected abstract void engineInit(SecureRandom random);

    /**
     * Initializes the key generator with the specified parameter
     * set and a user-provided source of randomness.
     *
     * @param params the key generation parameters
     * @param random the source of randomness for this key generator
     *
     * @exception InvalidAlgorithmParameterException if {@code params} is
     * inappropriate for this key generator
     */
    protected abstract void engineInit(AlgorithmParameterSpec params,
                                       SecureRandom random)
        throws InvalidAlgorithmParameterException;

    /**
     * Initializes this key generator for a certain keysize, using the given
     * source of randomness.
     *
     * @param keysize the keysize. This is an algorithm-specific metric,
     * specified in number of bits.
     * @param random the source of randomness for this key generator
     *
     * @exception InvalidParameterException if the keysize is wrong or not
     * supported.
     */
    protected abstract void engineInit(int keysize, SecureRandom random);

    /**
     * Generates a secret key.
     *
     * @return the new key
     */
    protected abstract SecretKey engineGenerateKey();
}
