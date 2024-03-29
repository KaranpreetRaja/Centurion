/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;

import java.util.*;

import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

/**
 * The Subject Information Access Extension (OID = 1.3.6.1.5.5.7.1.11).
 * <p>
 * The subject information access extension indicates how to access
 * information and services for the subject of the certificate in which
 * the extension appears.  When the subject is a CA, information and
 * services may include certificate validation services and CA policy
 * data.  When the subject is an end entity, the information describes
 * the type of services offered and how to access them.  In this case,
 * the contents of this extension are defined in the protocol
 * specifications for the supported services.  This extension may be
 * included in end entity or CA certificates.  Conforming CAs MUST mark
 * this extension as non-critical.
 * <p>
 * This extension is defined in <a href="https://tools.ietf.org/html/rfc5280">
 * Internet X.509 PKI Certificate and Certificate Revocation List
 * (CRL) Profile</a>. The profile permits
 * the extension to be included in end-entity or CA certificates,
 * and it must be marked as non-critical. Its ASN.1 definition is as follows:
 * <pre>
 *   id-pe-subjectInfoAccess OBJECT IDENTIFIER ::= { id-pe 11 }
 *
 *   SubjectInfoAccessSyntax  ::=
 *          SEQUENCE SIZE (1..MAX) OF AccessDescription
 *
 *   AccessDescription  ::=  SEQUENCE {
 *          accessMethod          OBJECT IDENTIFIER,
 *          accessLocation        GeneralName  }
 * </pre>
 *
 * @see Extension
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public class SubjectInfoAccessExtension extends Extension {

    public static final String NAME = "SubjectInfoAccess";

    /**
     * The List of AccessDescription objects.
     */
    private List<AccessDescription> accessDescriptions;

    /**
     * Create an SubjectInfoAccessExtension from a List of
     * AccessDescription; the criticality is set to false.
     *
     * @param accessDescriptions the List of AccessDescription,
     *                           cannot be null or empty.
     */
    public SubjectInfoAccessExtension(
            List<AccessDescription> accessDescriptions) {
        if (accessDescriptions == null || accessDescriptions.isEmpty()) {
            throw new IllegalArgumentException(
                    "accessDescriptions cannot be null or empty");
        }
        this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
        this.critical = false;
        this.accessDescriptions = accessDescriptions;
        encodeThis();
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value Array of DER encoded bytes of the actual value.
     * @exception IOException on error.
     */
    public SubjectInfoAccessExtension(Boolean critical, Object value)
            throws IOException {
        this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
        this.critical = critical.booleanValue();

        if (!(value instanceof byte[])) {
            throw new IOException("Illegal argument type");
        }

        extensionValue = (byte[])value;
        DerValue val = new DerValue(extensionValue);
        if (val.tag != DerValue.tag_Sequence) {
            throw new IOException("Invalid encoding for " +
                                  "SubjectInfoAccessExtension.");
        }
        accessDescriptions = new ArrayList<>();
        while (val.data.available() != 0) {
            DerValue seq = val.data.getDerValue();
            AccessDescription accessDescription = new AccessDescription(seq);
            accessDescriptions.add(accessDescription);
        }
    }

    /**
     * Return the list of AccessDescription objects.
     */
    public List<AccessDescription> getAccessDescriptions() {
        return accessDescriptions;
    }

    /**
     * Return the name of this extension.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Write the extension to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the extension to.
     */
    @Override
    public void encode(DerOutputStream out) {
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
            this.critical = false;
            encodeThis();
        }
        super.encode(out);
    }

    // Encode this extension value
    private void encodeThis() {
        if (accessDescriptions.isEmpty()) {
            this.extensionValue = null;
        } else {
            DerOutputStream ads = new DerOutputStream();
            for (AccessDescription accessDescription : accessDescriptions) {
                accessDescription.encode(ads);
            }
            DerOutputStream seq = new DerOutputStream();
            seq.write(DerValue.tag_Sequence, ads);
            this.extensionValue = seq.toByteArray();
        }
    }

    /**
     * Return the extension as user readable string.
     */
    public String toString() {
        return super.toString() +
            "SubjectInfoAccess [\n  " + accessDescriptions + "\n]\n";
    }
}
