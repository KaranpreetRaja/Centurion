/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;

import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;

import java.base.share.classes.sun.security.util.*;
import java.base.share.classes.sun.security.x509.X509Key;

import java.base.share.classes.sun.security.rsa.RSAUtil.KeyType;

/**
 * RSA public key implementation for "RSA", "RSASSA-PSS" algorithms.
 *
 * Note: RSA keys must be at least 512 bits long
 *
 * @see RSAPrivateCrtKeyImpl
 * @see RSAPrivateKeyImpl
 * @see RSAKeyFactory
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public final class RSAPublicKeyImpl extends X509Key implements RSAPublicKey {

    @java.io.Serial
    private static final long serialVersionUID = 2644735423591199609L;
    private static final BigInteger THREE = BigInteger.valueOf(3);

    private BigInteger n;       // modulus
    private BigInteger e;       // public exponent

    private final transient KeyType type;

    // optional parameters associated with this RSA key
    // specified in the encoding of its AlgorithmId
    // must be null for "RSA" keys.
    private final transient AlgorithmParameterSpec keyParams;

    /**
     * Generate a new RSAPublicKey from the specified type, format, and
     * encoding.
     * Also used by SunPKCS11 provider.
     */
    public static RSAPublicKey newKey(KeyType type, String format,
            byte[] encoded) throws InvalidKeyException {
        RSAPublicKey key;
        switch (format) {
        case "X.509":
            key = new RSAPublicKeyImpl(encoded);
            RSAKeyFactory.checkKeyAlgo(key, type.keyAlgo);
            break;
        case "PKCS#1":
            try {
                BigInteger[] comps = parseASN1(encoded);
                key = new RSAPublicKeyImpl(type, null, comps[0], comps[1]);
            } catch (IOException ioe) {
                throw new InvalidKeyException("Invalid PKCS#1 encoding", ioe);
            }
            break;
        default:
            throw new InvalidKeyException("Unsupported RSA PublicKey format: " +
                    format);
        }
        return key;
    }

    /**
     * Generate a new RSAPublicKey from the specified type and components.
     * Also used by SunPKCS11 provider.
     */
    public static RSAPublicKey newKey(KeyType type,
            AlgorithmParameterSpec params, BigInteger n, BigInteger e)
            throws InvalidKeyException {
        return new RSAPublicKeyImpl(type, params, n, e);
    }

    /**
     * Construct an RSA key from the specified type and components. Used by
     * RSAKeyFactory and RSAKeyPairGenerator.
     */
    RSAPublicKeyImpl(KeyType type, AlgorithmParameterSpec keyParams,
            BigInteger n, BigInteger e) throws InvalidKeyException {

        RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), e);
        checkExponentRange(n, e);

        this.n = n;
        this.e = e;

        try {
            // validate and generate algid encoding
            algid = RSAUtil.createAlgorithmId(type, keyParams);
        } catch (ProviderException pe) {
            throw new InvalidKeyException(pe);
        }

        this.type = type;
        this.keyParams = keyParams;

        // generate the key encoding
        DerOutputStream out = new DerOutputStream();
        out.putInteger(n);
        out.putInteger(e);
        byte[] keyArray =
                new DerValue(DerValue.tag_Sequence,
                        out.toByteArray()).toByteArray();
        setKey(new BitArray(keyArray.length * 8, keyArray));
    }

    /**
     * Construct a key from its encoding.
     */
    private RSAPublicKeyImpl(byte[] encoded) throws InvalidKeyException {
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("Missing key encoding");
        }
        decode(encoded); // this sets n and e value
        RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), e);
        checkExponentRange(n, e);

        try {
            // check the validity of oid and params
            Object[] o = RSAUtil.getTypeAndParamSpec(algid);
            this.type = (KeyType) o[0];
            this.keyParams = (AlgorithmParameterSpec) o[1];
        } catch (ProviderException e) {
            throw new InvalidKeyException(e);
        }
    }

    // pkg private utility method for checking RSA modulus and public exponent
    static void checkExponentRange(BigInteger mod, BigInteger exp)
            throws InvalidKeyException {
        // the exponent should be smaller than the modulus
        if (exp.compareTo(mod) >= 0) {
            throw new InvalidKeyException("exponent is larger than modulus");
        }

        // the exponent should be at least 3
        if (exp.compareTo(THREE) < 0) {
            throw new InvalidKeyException("exponent is smaller than 3");
        }
    }

    // see JCA doc
    @Override
    public String getAlgorithm() {
        return type.keyAlgo;
    }

    // see JCA doc
    @Override
    public BigInteger getModulus() {
        return n;
    }

    // see JCA doc
    @Override
    public BigInteger getPublicExponent() {
        return e;
    }

    // see JCA doc
    @Override
    public AlgorithmParameterSpec getParams() {
        return keyParams;
    }

    // utility method for parsing DER encoding of RSA public keys in PKCS#1
    // format as defined in RFC 8017 Appendix A.1.1, i.e. SEQ of n and e.
    private static BigInteger[] parseASN1(byte[] raw) throws IOException {
        DerValue derValue = new DerValue(raw);
        if (derValue.tag != DerValue.tag_Sequence) {
            throw new IOException("Not a SEQUENCE");
        }
        BigInteger[] result = new BigInteger[2]; // n, e
        result[0] = derValue.data.getPositiveBigInteger();
        result[1] = derValue.data.getPositiveBigInteger();
        if (derValue.data.available() != 0) {
            throw new IOException("Extra data available");
        }
        return result;
    }

    /**
     * Parse the key. Called by X509Key.
     */
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            BigInteger[] comps = parseASN1(getKey().toByteArray());
            n = comps[0];
            e = comps[1];
        } catch (IOException e) {
            throw new InvalidKeyException("Invalid RSA public key", e);
        }
    }

    // return a string representation of this key for debugging
    @Override
    public String toString() {
        return "Sun " + type.keyAlgo + " public key, " + n.bitLength()
               + " bits" + "\n  params: " + keyParams + "\n  modulus: " + n
               + "\n  public exponent: " + e;
    }

    @java.io.Serial
    protected Object writeReplace() throws java.io.ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC,
                        getAlgorithm(),
                        getFormat(),
                        getEncoded());
    }
}
