/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto;

import java.security.*;
import java.security.spec.*;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the {@code SecretKeyFactory} class.
 * All the abstract methods in this class must be implemented by each
 * cryptographic service provider who wishes to supply the implementation
 * of a secret-key factory for a particular algorithm.
 *
 * <P> A provider should document all the key specifications supported by its
 * secret key factory.
 * For example, the DES secret-key factory supplied by the "SunJCE" provider
 * supports {@code DESKeySpec} as a transparent representation of DES
 * keys, and that provider's secret-key factory for Triple DES keys supports
 * {@code DESedeKeySpec} as a transparent representation of Triple DES
 * keys.
 *
 * @author Jan Luehe
 *
 * @see SecretKey
 * @see java.base.share.classes.javax.crypto.spec.DESKeySpec
 * @see java.base.share.classes.javax.crypto.spec.DESedeKeySpec
 * @since 1.4
 */

public abstract class SecretKeyFactorySpi {

    /**
     * Constructor for subclasses to call.
     */
    public SecretKeyFactorySpi() {}

    /**
     * Generates a {@code SecretKey} object from the
     * provided key specification (key material).
     *
     * @param keySpec the specification (key material) of the secret key
     *
     * @return the secret key
     *
     * @exception InvalidKeySpecException if the given key specification
     * is inappropriate for this secret-key factory to produce a secret key.
     */
    protected abstract SecretKey engineGenerateSecret(KeySpec keySpec)
        throws InvalidKeySpecException;

    /**
     * Returns a specification (key material) of the given key
     * object in the requested format.
     *
     * @param key the key
     *
     * @param keySpec the requested format in which the key material shall be
     * returned
     *
     * @return the underlying key specification (key material) in the
     * requested format
     *
     * @exception InvalidKeySpecException if the requested key specification is
     * inappropriate for the given key (e.g., the algorithms associated with
     * {@code key} and {@code keySpec} do not match, or
     * {@code key} references a key on a cryptographic hardware device
     * whereas {@code keySpec} is the specification of a software-based
     * key), or the given key cannot be dealt with
     * (e.g., the given key has an algorithm or format not supported by this
     * secret-key factory).
     */
    protected abstract KeySpec engineGetKeySpec(SecretKey key, Class<?> keySpec)
        throws InvalidKeySpecException;

    /**
     * Translates a key object, whose provider may be unknown or
     * potentially untrusted, into a corresponding key object of this
     * secret-key factory.
     *
     * @param key the key whose provider is unknown or untrusted
     *
     * @return the translated key
     *
     * @exception InvalidKeyException if the given key cannot be processed
     * by this secret-key factory.
     */
    protected abstract SecretKey engineTranslateKey(SecretKey key)
        throws InvalidKeyException;
}
