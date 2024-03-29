/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.io.InputStream;

import javax.security.auth.x500.X500Principal;

import sun.security.util.*;

/**
 * This class defines the X500Name attribute for the Certificate.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 * @see DerEncoder
 */
public class CertificateSubjectName implements DerEncoder {

    public static final String NAME = "subject";

    // Private data member
    private X500Name    dnName;

    // cached X500Principal version of the name
    private X500Principal dnPrincipal;

    /**
     * Default constructor for the certificate attribute.
     *
     * @param name the X500Name
     */
    public CertificateSubjectName(X500Name name) {
        this.dnName = name;
    }

    /**
     * Create the object, decoding the values from the passed DER stream.
     *
     * @param in the DerInputStream to read the X500Name from.
     * @exception IOException on decoding errors.
     */
    public CertificateSubjectName(DerInputStream in) throws IOException {
        dnName = new X500Name(in);
    }

    /**
     * Create the object, decoding the values from the passed stream.
     *
     * @param in the InputStream to read the X500Name from.
     * @exception IOException on decoding errors.
     */
    public CertificateSubjectName(InputStream in) throws IOException {
        DerValue derVal = new DerValue(in);
        dnName = new X500Name(derVal);
    }

    /**
     * Return the name as user readable string.
     */
    public String toString() {
        if (dnName == null) return "";
        return dnName.toString();
    }

    /**
     * Encode the name in DER form to the stream.
     *
     * @param out the DerOutputStream to marshal the contents to.
     */
    @Override
    public void encode(DerOutputStream out) {
        dnName.encode(out);
    }
}
