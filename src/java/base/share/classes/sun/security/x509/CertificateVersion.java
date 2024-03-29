/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.io.InputStream;

import sun.security.util.*;

/**
 * This class defines the version of the X509 Certificate.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 * @see DerEncoder
 */
public class CertificateVersion implements DerEncoder {
    /**
     * X509Certificate Version 1
     */
    public static final int     V1 = 0;
    /**
     * X509Certificate Version 2
     */
    public static final int     V2 = 1;
    /**
     * X509Certificate Version 3
     */
    public static final int     V3 = 2;

    public static final String NAME = "version";

    // Private data members
    int version = V1;

    // Returns the version number.
    public int getVersion() {
        return version;
    }

    // Construct the class from the passed DerValue
    private void construct(DerValue derVal) throws IOException {
        if (derVal.isConstructed() && derVal.isContextSpecific()) {
            derVal = derVal.data.getDerValue();
            version = derVal.getInteger();
            if (derVal.data.available() != 0) {
                throw new IOException("X.509 version, bad format");
            }
        }
    }

    /**
     * The default constructor for this class,
     *  sets the version to 0 (i.e. X.509 version 1).
     */
    public CertificateVersion() {
        version = V1;
    }

    /**
     * The constructor for this class for the required version.
     *
     * @param version the version for the certificate.
     * @exception IOException if the version is not valid.
     */
    public CertificateVersion(int version) throws IOException {

        // check that it is a valid version
        if (version == V1 || version == V2 || version == V3)
            this.version = version;
        else {
            throw new IOException("X.509 Certificate version " +
                                   version + " not supported.\n");
        }
    }

    /**
     * Create the object, decoding the values from the passed DER stream.
     *
     * @param in the DerInputStream to read the CertificateVersion from.
     * @exception IOException on decoding errors.
     */
    public CertificateVersion(DerInputStream in) throws IOException {
        version = V1;
        DerValue derVal = in.getDerValue();

        construct(derVal);
    }

    /**
     * Create the object, decoding the values from the passed stream.
     *
     * @param in the InputStream to read the CertificateVersion from.
     * @exception IOException on decoding errors.
     */
    public CertificateVersion(InputStream in) throws IOException {
        version = V1;
        DerValue derVal = new DerValue(in);

        construct(derVal);
    }

    /**
     * Create the object, decoding the values from the passed DerValue.
     *
     * @param val the Der encoded value.
     * @exception IOException on decoding errors.
     */
    public CertificateVersion(DerValue val) throws IOException {
        version = V1;

        construct(val);
    }

    /**
     * Return the version number of the certificate.
     */
    public String toString() {
        return "Version: V" + (version+1);
    }

    /**
     * Encode the CertificateVersion period in DER form to the stream.
     *
     * @param out the DerOutputStream to marshal the contents to.
     */
    @Override
    public void encode(DerOutputStream out) {
        // Nothing for default
        if (version == V1) {
            return;
        }
        DerOutputStream tmp = new DerOutputStream();
        tmp.putInteger(version);

        out.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0),
                  tmp);
    }

   /**
     * Compare versions.
     */
    public int compare(int vers) {
        return version - vers;
    }
}
