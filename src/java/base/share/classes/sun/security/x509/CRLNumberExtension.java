/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.math.BigInteger;

import sun.security.util.*;

/**
 * Represent the CRL Number Extension.
 *
 * <p>This extension, if present, conveys a monotonically increasing
 * sequence number for each CRL issued by a given CA through a specific
 * CA X.500 Directory entry or CRL distribution point. This extension
 * allows users to easily determine when a particular CRL supersedes
 * another CRL.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 * @see Extension
 */
public class CRLNumberExtension extends Extension {

    public static final String NAME = "CRLNumber";

    private static final String LABEL = "CRL Number";

    private BigInteger crlNumber;
    private final String extensionName;
    private final String extensionLabel;

    // Encode this extension value
    private void encodeThis() {
        if (crlNumber == null) {
            this.extensionValue = null;
            return;
        }
        DerOutputStream os = new DerOutputStream();
        os.putInteger(this.crlNumber);
        this.extensionValue = os.toByteArray();
    }

    /**
     * Create a CRLNumberExtension with the integer value .
     * The criticality is set to false.
     *
     * @param crlNum the value to be set for the extension.
     */
    public CRLNumberExtension(int crlNum) {
        this(PKIXExtensions.CRLNumber_Id, false, BigInteger.valueOf(crlNum),
        NAME, LABEL);
    }

    /**
     * Create a CRLNumberExtension with the BigInteger value .
     * The criticality is set to false.
     *
     * @param crlNum the value to be set for the extension, cannot be null
     */
    public CRLNumberExtension(BigInteger crlNum) {
        this(PKIXExtensions.CRLNumber_Id, false, crlNum, NAME, LABEL);
    }

    /**
     * Creates the extension (also called by the subclass).
     */
    protected CRLNumberExtension(ObjectIdentifier extensionId,
            boolean isCritical, BigInteger crlNum, String extensionName,
            String extensionLabel) {

        if (crlNum == null) {
            throw new IllegalArgumentException("CRL number cannot be null");
        }
        this.extensionId = extensionId;
        this.critical = isCritical;
        this.crlNumber = crlNum;
        this.extensionName = extensionName;
        this.extensionLabel = extensionLabel;
        encodeThis();
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value an array of DER encoded bytes of the actual value.
     * @exception ClassCastException if value is not an array of bytes
     * @exception IOException on error.
     */
    public CRLNumberExtension(Boolean critical, Object value)
    throws IOException {
        this(PKIXExtensions.CRLNumber_Id, critical, value, NAME, LABEL);
    }

    /**
     * Creates the extension (also called by the subclass).
     */
    protected CRLNumberExtension(ObjectIdentifier extensionId,
        Boolean critical, Object value, String extensionName,
        String extensionLabel) throws IOException {

        this.extensionId = extensionId;
        this.critical = critical.booleanValue();
        this.extensionValue = (byte[]) value;
        DerValue val = new DerValue(this.extensionValue);
        this.crlNumber = val.getBigInteger();
        this.extensionName = extensionName;
        this.extensionLabel = extensionLabel;
    }

    /**
     * Get the crlNumber value.
     */
    public BigInteger getCrlNumber() {
        return crlNumber;
    }


    /**
     * Returns a printable representation of the CRLNumberExtension.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString())
            .append(extensionLabel)
            .append(": ");
        if (crlNumber != null) {
            sb.append(Debug.toHexString(crlNumber));
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Write the extension to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the extension to.
     */
    @Override
    public void encode(DerOutputStream out) {
        encode(out, PKIXExtensions.CRLNumber_Id, true);
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
     * Return the name of this extension.
     */
    @Override
    public String getName() {
        return extensionName;
    }
}
