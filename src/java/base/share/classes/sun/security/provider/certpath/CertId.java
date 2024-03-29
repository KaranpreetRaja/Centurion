/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import java.base.share.classes.sun.security.util.HexDumpEncoder;
import java.base.share.classes.sun.security.x509.*;
import java.base.share.classes.sun.security.util.*;

/**
 * This class corresponds to the CertId field in OCSP Request
 * and the OCSP Response. The ASN.1 definition for CertID is defined
 * in RFC 2560 as:
 * <pre>
 *
 * CertID          ::=     SEQUENCE {
 *      hashAlgorithm       AlgorithmIdentifier,
 *      issuerNameHash      OCTET STRING, -- Hash of Issuer's DN
 *      issuerKeyHash       OCTET STRING, -- Hash of Issuers public key
 *      serialNumber        CertificateSerialNumber
 *      }
 *
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public class CertId implements DerEncoder {

    private static final boolean debug = false;
    private static final AlgorithmId SHA1_ALGID
        = new AlgorithmId(AlgorithmId.SHA_oid);
    private final AlgorithmId hashAlgId;
    private final byte[] issuerNameHash;
    private final byte[] issuerKeyHash;
    private final SerialNumber certSerialNumber;
    private int myhash = -1; // hashcode for this CertId

    /**
     * Creates a CertId. The hash algorithm used is SHA-1.
     */
    public CertId(X509Certificate issuerCert, SerialNumber serialNumber)
        throws IOException {

        this(issuerCert.getSubjectX500Principal(),
             issuerCert.getPublicKey(), serialNumber);
    }

    public CertId(X500Principal issuerName, PublicKey issuerKey,
                  SerialNumber serialNumber) throws IOException {

        // compute issuerNameHash
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException nsae) {
            throw new IOException("Unable to create CertId", nsae);
        }
        hashAlgId = SHA1_ALGID;
        md.update(issuerName.getEncoded());
        issuerNameHash = md.digest();

        // compute issuerKeyHash (remove the tag and length)
        byte[] pubKey = issuerKey.getEncoded();
        DerValue val = new DerValue(pubKey);
        DerValue[] seq = new DerValue[2];
        seq[0] = val.data.getDerValue(); // AlgorithmID
        seq[1] = val.data.getDerValue(); // Key
        byte[] keyBytes = seq[1].getBitString();
        md.update(keyBytes);
        issuerKeyHash = md.digest();
        certSerialNumber = serialNumber;

        if (debug) {
            HexDumpEncoder encoder = new HexDumpEncoder();
            System.out.println("Issuer Name is " + issuerName);
            System.out.println("issuerNameHash is " +
                encoder.encodeBuffer(issuerNameHash));
            System.out.println("issuerKeyHash is " +
                encoder.encodeBuffer(issuerKeyHash));
            System.out.println("SerialNumber is " + serialNumber.getNumber());
        }
    }

    /**
     * Creates a CertId from its ASN.1 DER encoding.
     */
    public CertId(DerInputStream derIn) throws IOException {
        hashAlgId = AlgorithmId.parse(derIn.getDerValue());
        issuerNameHash = derIn.getOctetString();
        issuerKeyHash = derIn.getOctetString();
        certSerialNumber = new SerialNumber(derIn);
    }

    /**
     * Return the hash algorithm identifier.
     */
    public AlgorithmId getHashAlgorithm() {
        return hashAlgId;
    }

    /**
     * Return the hash value for the issuer name.
     */
    public byte[] getIssuerNameHash() {
        return issuerNameHash;
    }

    /**
     * Return the hash value for the issuer key.
     */
    public byte[] getIssuerKeyHash() {
        return issuerKeyHash;
    }

    /**
     * Return the serial number.
     */
    public BigInteger getSerialNumber() {
        return certSerialNumber.getNumber();
    }

    /**
     * Encode the CertId using ASN.1 DER.
     * The hash algorithm used is SHA-1.
     */
    @Override
    public void encode(DerOutputStream out) {

        DerOutputStream tmp = new DerOutputStream();
        hashAlgId.encode(tmp);
        tmp.putOctetString(issuerNameHash);
        tmp.putOctetString(issuerKeyHash);
        certSerialNumber.encode(tmp);
        out.write(DerValue.tag_Sequence, tmp);

        if (debug) {
            HexDumpEncoder encoder = new HexDumpEncoder();
            System.out.println("Encoded certId is " +
                encoder.encode(out.toByteArray()));
        }
    }

    /**
     * Returns a hashcode value for this CertId.
     *
     * @return the hashcode value.
     */
    @Override public int hashCode() {
        if (myhash == -1) {
            myhash = hashAlgId.hashCode();
            for (int i = 0; i < issuerNameHash.length; i++) {
                myhash += issuerNameHash[i] * i;
            }
            for (int i = 0; i < issuerKeyHash.length; i++) {
                myhash += issuerKeyHash[i] * i;
            }
            myhash += certSerialNumber.getNumber().hashCode();
        }
        return myhash;
    }

    /**
     * Compares this CertId for equality with the specified
     * object. Two CertId objects are considered equal if their hash algorithms,
     * their issuer name and issuer key hash values and their serial numbers
     * are equal.
     *
     * @param other the object to test for equality with this object.
     * @return true if the objects are considered equal, false otherwise.
     */
    @Override public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CertId that)) {
            return false;
        }

        return hashAlgId.equals(that.getHashAlgorithm()) &&
                Arrays.equals(issuerNameHash, that.getIssuerNameHash()) &&
                Arrays.equals(issuerKeyHash, that.getIssuerKeyHash()) &&
                certSerialNumber.getNumber().equals(that.getSerialNumber());
    }

    /**
     * Create a string representation of the CertId.
     */
    @Override public String toString() {
        HexDumpEncoder encoder = new HexDumpEncoder();
        return "CertId \n" +
                "Algorithm: " + hashAlgId.toString() + "\n" +
                "issuerNameHash \n" +
                encoder.encode(issuerNameHash) +
                "\nissuerKeyHash: \n" +
                encoder.encode(issuerKeyHash) +
                "\n" + certSerialNumber.toString();
    }
}
