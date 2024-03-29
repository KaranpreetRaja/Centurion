/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.util.Objects;

import sun.security.util.*;

/**
 * Represent the CertificatePolicyMap ASN.1 object.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public class CertificatePolicyMap implements DerEncoder {
    private final CertificatePolicyId issuerDomain;
    private final CertificatePolicyId subjectDomain;

    /**
     * Create a CertificatePolicyMap with the passed CertificatePolicyId's.
     *
     * @param issuer the CertificatePolicyId for the issuer CA.
     * @param subject the CertificatePolicyId for the subject CA.
     */
    public CertificatePolicyMap(CertificatePolicyId issuer,
                                CertificatePolicyId subject) {
        this.issuerDomain = Objects.requireNonNull(issuer);
        this.subjectDomain = Objects.requireNonNull(subject);
    }

    /**
     * Create the CertificatePolicyMap from the DER encoded value.
     *
     * @param val the DER encoded value of the same.
     */
    public CertificatePolicyMap(DerValue val) throws IOException {
        if (val.tag != DerValue.tag_Sequence) {
            throw new IOException("Invalid encoding for CertificatePolicyMap");
        }
        issuerDomain = new CertificatePolicyId(val.data.getDerValue());
        subjectDomain = new CertificatePolicyId(val.data.getDerValue());
    }

    /**
     * Return the issuer CA part of the policy map.
     */
    public CertificatePolicyId getIssuerIdentifier() {
        return (issuerDomain);
    }

    /**
     * Return the subject CA part of the policy map.
     */
    public CertificatePolicyId getSubjectIdentifier() {
        return (subjectDomain);
    }

    /**
     * Returns a printable representation of the CertificatePolicyId.
     */
    public String toString() {

        return ("CertificatePolicyMap: [\n"
                 + "IssuerDomain:" + issuerDomain.toString()
                 + "SubjectDomain:" + subjectDomain.toString()
                 + "]\n");
    }

    /**
     * Write the CertificatePolicyMap to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the object to.
     */
    @Override
    public void encode(DerOutputStream out) {
        DerOutputStream tmp = new DerOutputStream();

        issuerDomain.encode(tmp);
        subjectDomain.encode(tmp);
        out.write(DerValue.tag_Sequence,tmp);
    }
}
