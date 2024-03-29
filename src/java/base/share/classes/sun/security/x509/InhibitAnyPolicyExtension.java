/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;

import sun.security.util.*;

/**
 * This class represents the Inhibit Any-Policy Extension.
 *
 * <p>The inhibit any-policy extension can be used in certificates issued
 * to CAs. The inhibit any-policy indicates that the special any-policy
 * OID, with the value {2 5 29 32 0}, is not considered an explicit
 * match for other certificate policies.  The value indicates the number
 * of additional certificates that may appear in the path before any-
 * policy is no longer permitted.  For example, a value of one indicates
 * that any-policy may be processed in certificates issued by the sub-
 * ject of this certificate, but not in additional certificates in the
 * path.
 * <p>
 * This extension MUST be critical.
 * <p>
 * The ASN.1 syntax for this extension is:
 * <pre>{@code
 * id-ce-inhibitAnyPolicy OBJECT IDENTIFIER ::=  { id-ce 54 }
 *
 * InhibitAnyPolicy ::= SkipCerts
 *
 * SkipCerts ::= INTEGER (0..MAX)
 * }</pre>
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 * @see Extension
 */
public class InhibitAnyPolicyExtension extends Extension {

    /**
     * Object identifier for "any-policy"
     */
    public static ObjectIdentifier AnyPolicy_Id =
            ObjectIdentifier.of(KnownOIDs.CE_CERT_POLICIES_ANY);

    public static final String NAME = "InhibitAnyPolicy";

    // Private data members
    private int skipCerts = Integer.MAX_VALUE;

    // Encode this extension value
    private void encodeThis() {
        DerOutputStream out = new DerOutputStream();
        out.putInteger(skipCerts);
        this.extensionValue = out.toByteArray();
    }

    /**
     * Default constructor for this object.
     *
     * @param skipCerts specifies the depth of the certification path.
     *                  Use value of -1 to request unlimited depth.
     */
    public InhibitAnyPolicyExtension(int skipCerts) {
        if (skipCerts < -1)
            throw new IllegalArgumentException("Invalid value for skipCerts");
        if (skipCerts == -1)
            this.skipCerts = Integer.MAX_VALUE;
        else
            this.skipCerts = skipCerts;
        this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
        critical = true;
        encodeThis();
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical criticality flag to use.  Must be true for this
     *                 extension.
     * @param value a byte array holding the DER-encoded extension value.
     * @exception ClassCastException if value is not an array of bytes
     * @exception IOException on error.
     */
    public InhibitAnyPolicyExtension(Boolean critical, Object value)
        throws IOException {

        this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;

        if (!critical.booleanValue())
            throw new IOException("Criticality cannot be false for " +
                                  "InhibitAnyPolicy");
        this.critical = true;

        this.extensionValue = (byte[]) value;
        DerValue val = new DerValue(this.extensionValue);
        if (val.tag != DerValue.tag_Integer)
            throw new IOException("Invalid encoding of InhibitAnyPolicy: "
                                  + "data not integer");

        if (val.data == null)
            throw new IOException("Invalid encoding of InhibitAnyPolicy: "
                                  + "null data");
        int skipCertsValue = val.getInteger();
        if (skipCertsValue < -1)
            throw new IOException("Invalid value for skipCerts");
        if (skipCertsValue == -1) {
            this.skipCerts = Integer.MAX_VALUE;
        } else {
            this.skipCerts = skipCertsValue;
        }
    }

    /**
     * Return user readable form of extension.
     */
    public String toString() {
        return super.toString() + "InhibitAnyPolicy: " + skipCerts + "\n";
    }

    /**
     * Encode this extension value to the output stream.
     *
     * @param out the DerOutputStream to encode the extension to.
     */
    @Override
    public void encode(DerOutputStream out) {
        if (extensionValue == null) {
            this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
            critical = true;
            encodeThis();
        }
        super.encode(out);
    }

    public int getSkipCerts() {
        return skipCerts;
    }

    /**
     * Return the name of this extension.
     *
     * @return name of extension.
     */
    @Override
    public String getName() {
        return NAME;
    }
}
