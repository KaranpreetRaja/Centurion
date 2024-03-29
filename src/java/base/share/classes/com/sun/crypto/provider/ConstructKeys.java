/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.base.share.classes.jdk.internal.access.SharedSecrets;

import java.base.share.classes.java.security.Key;
import java.base.share.classes.java.security.PublicKey;
import java.base.share.classes.java.security.PrivateKey;
import java.base.share.classes.java.security.KeyFactory;
import java.base.share.classes.java.security.InvalidKeyException;
import java.base.share.classes.java.security.NoSuchAlgorithmException;
import java.base.share.classes.java.security.spec.PKCS8EncodedKeySpec;
import java.base.share.classes.java.security.spec.X509EncodedKeySpec;
import java.base.share.classes.java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.base.share.classes.javax.crypto.SecretKey;
import java.base.share.classes.javax.crypto.Cipher;
import java.base.share.classes.javax.crypto.spec.SecretKeySpec;

/**
 * This class is a helper class which construct key objects
 * from encoded keys.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

final class ConstructKeys {

    private static final PublicKey constructPublicKey(byte[] encodedKey,
            int ofs, int len, String encodedKeyAlgorithm)
            throws InvalidKeyException, NoSuchAlgorithmException {
        PublicKey key = null;
        byte[] keyBytes = (ofs == 0 && encodedKey.length == len)
                ? encodedKey : Arrays.copyOfRange(encodedKey, ofs, ofs + len);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory =
                KeyFactory.getInstance(encodedKeyAlgorithm,
                    SunJCE.getInstance());
            key = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException nsae) {
            // Try to see whether there is another
            // provider which supports this algorithm
            try {
                KeyFactory keyFactory =
                    KeyFactory.getInstance(encodedKeyAlgorithm);
                key = keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException nsae2) {
                throw new NoSuchAlgorithmException("No installed providers " +
                                                   "can create keys for the " +
                                                   encodedKeyAlgorithm +
                                                   "algorithm");
            } catch (InvalidKeySpecException ikse2) {
                throw new InvalidKeyException("Cannot construct public key", ikse2);
            }
        } catch (InvalidKeySpecException ikse) {
            throw new InvalidKeyException("Cannot construct public key", ikse);
        }

        return key;
    }

    private static final PrivateKey constructPrivateKey(byte[] encodedKey,
            int ofs, int len, String encodedKeyAlgorithm)
            throws InvalidKeyException, NoSuchAlgorithmException {
        PrivateKey key = null;
        byte[] keyBytes = (ofs == 0 && encodedKey.length == len)
                ? encodedKey : Arrays.copyOfRange(encodedKey, ofs, ofs + len);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory =
                KeyFactory.getInstance(encodedKeyAlgorithm,
                    SunJCE.getInstance());
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException nsae) {
            // Try to see whether there is another
            // provider which supports this algorithm
            try {
                KeyFactory keyFactory =
                    KeyFactory.getInstance(encodedKeyAlgorithm);
                key = keyFactory.generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException nsae2) {
                throw new NoSuchAlgorithmException("No installed providers " +
                                                   "can create keys for the " +
                                                   encodedKeyAlgorithm +
                                                   "algorithm");
            } catch (InvalidKeySpecException ikse2) {
                throw new InvalidKeyException("Cannot construct private key", ikse2);
            }
        } catch (InvalidKeySpecException ikse) {
            throw new InvalidKeyException("Cannot construct private key", ikse);
        } finally {
            SharedSecrets.getJavaSecuritySpecAccess().clearEncodedKeySpec(keySpec);
            if (keyBytes != encodedKey) {
                Arrays.fill(keyBytes, (byte)0);
            }
        }

        return key;
    }

    private static final SecretKey constructSecretKey(byte[] encodedKey,
            int ofs, int len, String encodedKeyAlgorithm) {
        return (new SecretKeySpec(encodedKey, ofs, len, encodedKeyAlgorithm));
    }

    static final Key constructKey(byte[] encoding, String keyAlgorithm,
            int keyType) throws InvalidKeyException, NoSuchAlgorithmException {
        return constructKey(encoding, 0, encoding.length, keyAlgorithm,
                keyType);
    }

    static final Key constructKey(byte[] encoding, int ofs, int len,
            String keyAlgorithm, int keyType)
            throws InvalidKeyException, NoSuchAlgorithmException {
        return switch (keyType) {
            case Cipher.SECRET_KEY -> ConstructKeys.constructSecretKey(
                    encoding, ofs, len, keyAlgorithm);
            case Cipher.PRIVATE_KEY -> ConstructKeys.constructPrivateKey(
                    encoding, ofs, len, keyAlgorithm);
            case Cipher.PUBLIC_KEY -> ConstructKeys.constructPublicKey(
                    encoding, ofs, len, keyAlgorithm);
            default -> throw new NoSuchAlgorithmException("Unsupported key type");
        };
    }
}
