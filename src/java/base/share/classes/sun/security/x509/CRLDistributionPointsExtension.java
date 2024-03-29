/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;

import java.util.*;

import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

/**
 * Represent the CRL Distribution Points Extension (OID = 2.5.29.31).
 * <p>
 * The CRL distribution points extension identifies how CRL information
 * is obtained.  The extension SHOULD be non-critical, but the PKIX profile
 * recommends support for this extension by CAs and applications.
 * <p>
 * For PKIX, if the cRLDistributionPoints extension contains a
 * DistributionPointName of type URI, the following semantics MUST be
 * assumed: the URI is a pointer to the current CRL for the associated
 * reasons and will be issued by the associated cRLIssuer.  The
 * expected values for the URI conform to the following rules.  The
 * name MUST be a non-relative URL, and MUST follow the URL syntax and
 * encoding rules specified in [RFC 1738].  The name must include both
 * a scheme (e.g., "http" or "ftp") and a scheme-specific-part.  The
 * scheme- specific-part must include a fully qualified domain name or
 * IP address as the host.  As specified in [RFC 1738], the scheme
 * name is not case-sensitive (e.g., "http" is equivalent to "HTTP").
 * The host part is also not case-sensitive, but other components of
 * the scheme-specific-part may be case-sensitive. When comparing
 * URIs, conforming implementations MUST compare the scheme and host
 * without regard to case, but assume the remainder of the
 * scheme-specific-part is case-sensitive.  Processing rules for other
 * values are not defined by this specification.  If the
 * distributionPoint omits reasons, the CRL MUST include revocations
 * for all reasons. If the distributionPoint omits cRLIssuer, the CRL
 * MUST be issued by the CA that issued the certificate.
 * <p>
 * The ASN.1 definition for this is:
 * <pre>
 * id-ce-cRLDistributionPoints OBJECT IDENTIFIER ::=  { id-ce 31 }
 *
 * cRLDistributionPoints ::= {
 *      CRLDistPointsSyntax }
 *
 * CRLDistPointsSyntax ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 * @see DistributionPoint
 * @see Extension
 */
public class CRLDistributionPointsExtension extends Extension {

    public static final String NAME = "CRLDistributionPoints";

    /**
     * The List of DistributionPoint objects.
     */
    private List<DistributionPoint> distributionPoints;

    private final String extensionName;

    /**
     * Create a CRLDistributionPointsExtension from a List of
     * DistributionPoint; the criticality is set to false.
     *
     * @param distributionPoints the list of distribution points
     */
    public CRLDistributionPointsExtension(
            List<DistributionPoint> distributionPoints) {

        this(false, distributionPoints);
    }

    /**
     * Create a CRLDistributionPointsExtension from a List of
     * DistributionPoint.
     *
     * @param isCritical the criticality setting.
     * @param distributionPoints the list of distribution points,
     *                           cannot be null or empty.
     */
    public CRLDistributionPointsExtension(boolean isCritical,
        List<DistributionPoint> distributionPoints) {

        this(PKIXExtensions.CRLDistributionPoints_Id, isCritical,
            distributionPoints, NAME);
    }

    /**
     * Creates the extension (also called by the subclass).
     */
    protected CRLDistributionPointsExtension(ObjectIdentifier extensionId,
            boolean isCritical, List<DistributionPoint> distributionPoints,
            String extensionName) {

        if (distributionPoints == null || distributionPoints.isEmpty()) {
            throw new IllegalArgumentException(
                    "distribution points cannot be null or empty");
        }

        this.extensionId = extensionId;
        this.critical = isCritical;
        this.distributionPoints = distributionPoints;
        encodeThis();
        this.extensionName = extensionName;
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value Array of DER encoded bytes of the actual value.
     * @exception IOException on error.
     */
    public CRLDistributionPointsExtension(Boolean critical, Object value)
            throws IOException {
        this(PKIXExtensions.CRLDistributionPoints_Id, critical, value, NAME);
    }

    /**
     * Creates the extension (also called by the subclass).
     */
    protected CRLDistributionPointsExtension(ObjectIdentifier extensionId,
        Boolean critical, Object value, String extensionName)
            throws IOException {

        this.extensionId = extensionId;
        this.critical = critical.booleanValue();

        if (!(value instanceof byte[])) {
            throw new IOException("Illegal argument type");
        }

        extensionValue = (byte[])value;
        DerValue val = new DerValue(extensionValue);
        if (val.tag != DerValue.tag_Sequence) {
            throw new IOException("Invalid encoding for " + extensionName +
                                  " extension.");
        }
        distributionPoints = new ArrayList<>();
        while (val.data.available() != 0) {
            DerValue seq = val.data.getDerValue();
            DistributionPoint point = new DistributionPoint(seq);
            distributionPoints.add(point);
        }
        this.extensionName = extensionName;
    }

    /**
     * Return the name of this extension.
     */
    @Override
    public String getName() {
        return extensionName;
    }

    /**
     * Write the extension to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the extension to.
     */
    @Override
    public void encode(DerOutputStream out) {
        encode(out, PKIXExtensions.CRLDistributionPoints_Id, false);
    }

    /**
     * Write the extension to the DerOutputStream.
     * (Also called by the subclass)
     */
    protected void encode(DerOutputStream out, ObjectIdentifier extensionId,
            boolean isCritical) {

        if (this.extensionValue == null) {
            this.extensionId = extensionId;
            this.critical = isCritical;
            encodeThis();
        }
        super.encode(out);
    }

   /**
     * Get the DistributionPoint value.
     */
    public List<DistributionPoint> getDistributionPoints() {
        return distributionPoints;
    }



     // Encode this extension value
    private void encodeThis() {
        if (distributionPoints.isEmpty()) {
            this.extensionValue = null;
        } else {
            DerOutputStream pnts = new DerOutputStream();
            for (DistributionPoint point : distributionPoints) {
                point.encode(pnts);
            }
            DerOutputStream seq = new DerOutputStream();
            seq.write(DerValue.tag_Sequence, pnts);
            this.extensionValue = seq.toByteArray();
        }
    }

    /**
     * Return the extension as user readable string.
     */
    public String toString() {
        return super.toString() + extensionName + " [\n  "
               + distributionPoints + "]\n";
    }

}
