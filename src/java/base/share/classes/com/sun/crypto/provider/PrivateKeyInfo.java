/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.math.*;
import java.io.*;
import java.util.Arrays;

import java.base.share.classes.sun.security.x509.AlgorithmId;
import java.base.share.classes.sun.security.util.*;

/**
 * This class implements the <code>PrivateKeyInfo</code> type,
 * which is defined in PKCS #8 as follows:
 *
 * <pre>
 * PrivateKeyInfo ::=  SEQUENCE {
 *     version   INTEGER,
 *     privateKeyAlgorithm   AlgorithmIdentifier,
 *     privateKey   OCTET STRING,
 *     attributes   [0] IMPLICIT Attributes OPTIONAL }
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
final class PrivateKeyInfo {

    // the version number defined by the PKCS #8 standard
    private static final BigInteger VERSION = BigInteger.ZERO;

    // the private-key algorithm
    private AlgorithmId algid;

    // the private-key value
    private byte[] privkey;

    /**
     * Constructs a PKCS#8 PrivateKeyInfo from its ASN.1 encoding.
     */
    PrivateKeyInfo(byte[] encoded) throws IOException {
        DerValue val = new DerValue(encoded);

        try {
            if (val.tag != DerValue.tag_Sequence)
                throw new IOException("private key parse error: not a sequence");

            // version
            BigInteger parsedVersion = val.data.getBigInteger();
            if (!parsedVersion.equals(VERSION)) {
                throw new IOException("version mismatch: (supported: " +
                        VERSION + ", parsed: " + parsedVersion);
            }

            // privateKeyAlgorithm
            this.algid = AlgorithmId.parse(val.data.getDerValue());

            // privateKey
            this.privkey = val.data.getOctetString();

            // OPTIONAL attributes not supported yet
        } finally {
            val.clear();
        }
    }

    /**
     * Returns the private-key algorithm.
     */
    AlgorithmId getAlgorithm() {
        return this.algid;
    }

    public void clear() {
        Arrays.fill(privkey, (byte)0);
    }
}
