/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyFactorySpi;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHParameterSpec;

/**
 * This class implements the Diffie-Hellman key factory of the Sun provider.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
public final class DHKeyFactory extends KeyFactorySpi {

    /**
     * Empty constructor
     */
    public DHKeyFactory() {
    }

    /**
     * Generates a public key object from the provided key specification
     * (key material).
     *
     * @param keySpec the specification (key material) of the public key
     *
     * @return the public key
     *
     * @exception InvalidKeySpecException if the given key specification
     * is inappropriate for this key factory to produce a public key.
     */
    protected PublicKey engineGeneratePublic(KeySpec keySpec)
        throws InvalidKeySpecException
    {
        try {
            if (keySpec instanceof DHPublicKeySpec) {
                DHPublicKeySpec dhPubKeySpec = (DHPublicKeySpec)keySpec;
                return new DHPublicKey(dhPubKeySpec.getY(),
                                       dhPubKeySpec.getP(),
                                       dhPubKeySpec.getG());

            } else if (keySpec instanceof X509EncodedKeySpec) {
                return new DHPublicKey
                    (((X509EncodedKeySpec)keySpec).getEncoded());

            } else {
                throw new InvalidKeySpecException
                    ("Inappropriate key specification");
            }
        } catch (InvalidKeyException e) {
            throw new InvalidKeySpecException
                ("Inappropriate key specification", e);
        }
    }

    /**
     * Generates a private key object from the provided key specification
     * (key material).
     *
     * @param keySpec the specification (key material) of the private key
     *
     * @return the private key
     *
     * @exception InvalidKeySpecException if the given key specification
     * is inappropriate for this key factory to produce a private key.
     */
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec)
            throws InvalidKeySpecException {
        try {
            if (keySpec instanceof DHPrivateKeySpec) {
                DHPrivateKeySpec dhPrivKeySpec = (DHPrivateKeySpec)keySpec;
                return new DHPrivateKey(dhPrivKeySpec.getX(),
                                        dhPrivKeySpec.getP(),
                                        dhPrivKeySpec.getG());

            } else if (keySpec instanceof PKCS8EncodedKeySpec) {
                byte[] encoded = ((PKCS8EncodedKeySpec)keySpec).getEncoded();
                try {
                    return new DHPrivateKey(encoded);
                } finally {
                    Arrays.fill(encoded, (byte)0);
                }
            } else {
                throw new InvalidKeySpecException
                    ("Inappropriate key specification");
            }
        } catch (InvalidKeyException e) {
            throw new InvalidKeySpecException
                ("Inappropriate key specification", e);
        }
    }

    /**
     * Returns a specification (key material) of the given key object
     * in the requested format.
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
     * inappropriate for the given key, or the given key cannot be processed
     * (e.g., the given key has an unrecognized algorithm or format).
     */
    protected <T extends KeySpec>
        T engineGetKeySpec(Key key, Class<T> keySpec)
        throws InvalidKeySpecException {
        DHParameterSpec params;

        if (key instanceof javax.crypto.interfaces.DHPublicKey) {

            if (keySpec.isAssignableFrom(DHPublicKeySpec.class)) {
                javax.crypto.interfaces.DHPublicKey dhPubKey
                    = (javax.crypto.interfaces.DHPublicKey) key;
                params = dhPubKey.getParams();
                return keySpec.cast(new DHPublicKeySpec(dhPubKey.getY(),
                                                        params.getP(),
                                                        params.getG()));

            } else if (keySpec.isAssignableFrom(X509EncodedKeySpec.class)) {
                return keySpec.cast(new X509EncodedKeySpec(key.getEncoded()));

            } else {
                throw new InvalidKeySpecException
                    ("Inappropriate key specification");
            }

        } else if (key instanceof javax.crypto.interfaces.DHPrivateKey) {

            if (keySpec.isAssignableFrom(DHPrivateKeySpec.class)) {
                javax.crypto.interfaces.DHPrivateKey dhPrivKey
                    = (javax.crypto.interfaces.DHPrivateKey)key;
                params = dhPrivKey.getParams();
                return keySpec.cast(new DHPrivateKeySpec(dhPrivKey.getX(),
                                                         params.getP(),
                                                         params.getG()));

            } else if (keySpec.isAssignableFrom(PKCS8EncodedKeySpec.class)) {
                byte[] encoded = key.getEncoded();
                try {
                    return keySpec.cast(new PKCS8EncodedKeySpec(encoded));
                } finally {
                    Arrays.fill(encoded, (byte)0);
                }
            } else {
                throw new InvalidKeySpecException
                    ("Inappropriate key specification");
            }

        } else {
            throw new InvalidKeySpecException("Inappropriate key type");
        }
    }

    /**
     * Translates a key object, whose provider may be unknown or potentially
     * untrusted, into a corresponding key object of this key factory.
     *
     * @param key the key whose provider is unknown or untrusted
     *
     * @return the translated key
     *
     * @exception InvalidKeyException if the given key cannot be processed by
     * this key factory.
     */
    protected Key engineTranslateKey(Key key)
        throws InvalidKeyException
    {
        try {

            if (key instanceof javax.crypto.interfaces.DHPublicKey) {
                // Check if key originates from this factory
                if (key instanceof java.base.share.classes.com.sun.crypto.provider.DHPublicKey) {
                    return key;
                }
                // Convert key to spec
                DHPublicKeySpec dhPubKeySpec
                    = engineGetKeySpec(key, DHPublicKeySpec.class);
                // Create key from spec, and return it
                return engineGeneratePublic(dhPubKeySpec);

            } else if (key instanceof javax.crypto.interfaces.DHPrivateKey) {
                // Check if key originates from this factory
                if (key instanceof java.base.share.classes.com.sun.crypto.provider.DHPrivateKey) {
                    return key;
                }
                // Convert key to spec
                DHPrivateKeySpec dhPrivKeySpec
                    = engineGetKeySpec(key, DHPrivateKeySpec.class);
                // Create key from spec, and return it
                return engineGeneratePrivate(dhPrivKeySpec);

            } else {
                throw new InvalidKeyException("Wrong algorithm type");
            }

        } catch (InvalidKeySpecException e) {
            throw new InvalidKeyException("Cannot translate key", e);
        }
    }
}
