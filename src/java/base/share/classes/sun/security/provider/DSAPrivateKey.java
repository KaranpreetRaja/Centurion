/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider;

import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.AlgorithmParameters;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.interfaces.DSAParams;
import java.util.Arrays;

import java.base.share.classes.sun.security.x509.AlgIdDSA;
import java.base.share.classes.sun.security.pkcs.PKCS8Key;
import java.base.share.classes.sun.security.util.DerValue;
import java.base.share.classes.sun.security.util.DerInputStream;

/**
 * A PKCS#8 private key for the Digital Signature Algorithm.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 * @see DSAPublicKey
 * @see AlgIdDSA
 * @see DSA
 */

public final class DSAPrivateKey extends PKCS8Key
        implements java.security.interfaces.DSAPrivateKey, Serializable {

    /** use serialVersionUID from JDK 1.1. for interoperability */
    @java.io.Serial
    private static final long serialVersionUID = -3244453684193605938L;

    /* the private key */
    private final BigInteger x;

    /**
     * Make a DSA private key out of a private key and three parameters.
     */
    public DSAPrivateKey(BigInteger x, BigInteger p,
                         BigInteger q, BigInteger g) {
        this.x = x;
        algid = new AlgIdDSA(p, q, g);

        byte[] xbytes = x.toByteArray();
        DerValue val = new DerValue(DerValue.tag_Integer, xbytes);
        key = val.toByteArray();
        val.clear();
        Arrays.fill(xbytes, (byte)0);
    }

    /**
     * Make a DSA private key from its DER encoding (PKCS #8).
     */
    public DSAPrivateKey(byte[] encoded) throws InvalidKeyException {
        super(encoded);
        try {
            DerInputStream in = new DerInputStream(key);
            x = in.getBigInteger();
        } catch (IOException e) {
            throw new InvalidKeyException(e.getMessage(), e);
        }
    }

    /**
     * Returns the DSA parameters associated with this key, or null if the
     * parameters could not be parsed.
     */
    public DSAParams getParams() {
        try {
            if (algid instanceof DSAParams) {
                return (DSAParams)algid;
            } else {
                DSAParameterSpec paramSpec;
                AlgorithmParameters algParams = algid.getParameters();
                if (algParams == null) {
                    return null;
                }
                paramSpec = algParams.getParameterSpec(DSAParameterSpec.class);
                return paramSpec;
            }
        } catch (InvalidParameterSpecException e) {
            return null;
        }
    }

    /**
     * Get the raw private key, x, without the parameters.
     */
    public BigInteger getX() {
        return x;
    }
}
