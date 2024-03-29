/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.pkcs;

import java.io.IOException;

import java.base.share.classes.sun.security.util.DerValue;
import java.base.share.classes.sun.security.util.HexDumpEncoder;
import java.base.share.classes.sun.security.x509.GeneralNames;
import java.base.share.classes.sun.security.x509.SerialNumber;

/**
 * This class represents a signing certificate attribute.
 * Its attribute value is defined by the following ASN.1 definition.
 * <pre>
 *
 *   id-aa-signingCertificate OBJECT IDENTIFIER ::= { iso(1)
 *     member-body(2) us(840) rsadsi(113549) pkcs(1) pkcs9(9)
 *     smime(16) id-aa(2) 12 }
 *
 *   SigningCertificate ::=  SEQUENCE {
 *       certs       SEQUENCE OF ESSCertID,
 *       policies    SEQUENCE OF PolicyInformation OPTIONAL
 *   }
 *
 *   ESSCertID ::=  SEQUENCE {
 *       certHash        Hash,
 *       issuerSerial    IssuerSerial OPTIONAL
 *   }
 *
 *   Hash ::= OCTET STRING -- SHA1 hash of entire certificate
 *
 *   IssuerSerial ::= SEQUENCE {
 *       issuer         GeneralNames,
 *       serialNumber   CertificateSerialNumber
 *   }
 *
 *   PolicyInformation ::= SEQUENCE {
 *       policyIdentifier   CertPolicyId,
 *       policyQualifiers   SEQUENCE SIZE (1..MAX) OF
 *               PolicyQualifierInfo OPTIONAL }
 *
 *   CertPolicyId ::= OBJECT IDENTIFIER
 *
 *   PolicyQualifierInfo ::= SEQUENCE {
 *       policyQualifierId  PolicyQualifierId,
 *       qualifier        ANY DEFINED BY policyQualifierId }
 *
 *   -- Implementations that recognize additional policy qualifiers MUST
 *   -- augment the following definition for PolicyQualifierId
 *
 *   PolicyQualifierId ::= OBJECT IDENTIFIER ( id-qt-cps | id-qt-unotice )
 *
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */
class SigningCertificateInfo {

    private byte[] ber;
    private ESSCertId[] certId = null;

    SigningCertificateInfo(byte[] ber) throws IOException {
        parse(ber);
        this.ber = ber;
    }

    byte[] toByteArray() {
        return ber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < certId.length; i++) {
            sb.append(certId[i].toString());
        }
        // format policies as a string
        sb.append("\n]");

        return sb.toString();
    }

    private void parse(byte[] bytes) throws IOException {

        // Parse signingCertificate
        DerValue derValue = new DerValue(bytes);
        if (derValue.tag != DerValue.tag_Sequence) {
            throw new IOException("Bad encoding for signingCertificate");
        }

        // Parse certs
        DerValue[] certs = derValue.data.getSequence(1);
        certId = new ESSCertId[certs.length];
        for (int i = 0; i < certs.length; i++) {
            certId[i] = new ESSCertId(certs[i]);
        }

        // Parse policies, if present
        if (derValue.data.available() > 0) {
            DerValue[] policies = derValue.data.getSequence(1);
            for (int i = 0; i < policies.length; i++) {
                // parse PolicyInformation
            }
        }
    }

    static class ESSCertId {

        private static volatile HexDumpEncoder hexDumper;

        private final byte[] certHash;
        private final GeneralNames issuer;
        private final SerialNumber serialNumber;

        ESSCertId(DerValue certId) throws IOException {
            // Parse certHash
            certHash = certId.data.getDerValue().toByteArray();

            // Parse issuerSerial, if present
            if (certId.data.available() > 0) {
                DerValue issuerSerial = certId.data.getDerValue();
                // Parse issuer
                issuer = new GeneralNames(issuerSerial.data.getDerValue());
                // Parse serialNumber
                serialNumber = new SerialNumber(issuerSerial.data.getDerValue());
            } else {
                issuer = null;
                serialNumber = null;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[\n\tCertificate hash (SHA-1):\n");
            if (hexDumper == null) {
                hexDumper = new HexDumpEncoder();
            }
            sb.append(hexDumper.encode(certHash));
            if (issuer != null && serialNumber != null) {
                sb.append("\n\tIssuer: " + issuer + "\n");
                sb.append("\t" + serialNumber);
            }
            sb.append("\n]");
            return sb.toString();
        }
    }
}
