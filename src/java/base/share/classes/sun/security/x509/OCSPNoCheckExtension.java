/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;

/**
 * Represent the OCSP NoCheck Extension from RFC2560.
 * <p>
 * A CA may specify that an OCSP client can trust a responder for the
 * lifetime of the responder's certificate. The CA does so by including
 * the extension id-pkix-ocsp-nocheck. This SHOULD be a non-critical
 * extension. The value of the extension should be NULL. CAs issuing
 * such a certificate should realize that a compromise of the
 * responder's key is as serious as the compromise of a CA key used to
 * sign CRLs, at least for the validity period of this certificate. CA's
 * may choose to issue this type of certificate with a very short
 * lifetime and renew it frequently.
 * <pre>
 * id-pkix-ocsp-nocheck OBJECT IDENTIFIER ::= { id-pkix-ocsp 5 }
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 * @see Extension
 */
public class OCSPNoCheckExtension extends Extension {

    public static final String NAME = "OCSPNoCheck";

    /**
     * Create a OCSPNoCheckExtension
     */
    public OCSPNoCheckExtension() throws IOException {
        this.extensionId = PKIXExtensions.OCSPNoCheck_Id;
        this.critical = false;
        this.extensionValue = new byte[0];
    }

    /**
     * Create the extension from the passed DER encoded value.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value an array of DER encoded bytes of the actual value.
     * @exception IOException on error.
     */
    public OCSPNoCheckExtension(Boolean critical, Object value)
        throws IOException {

        this.extensionId = PKIXExtensions.OCSPNoCheck_Id;
        this.critical = critical.booleanValue();

        // the value should be null, just ignore it here.
        this.extensionValue = new byte[0];
    }

    /**
     * Return the name of this extension.
     */
    @Override
    public String getName() {
        return NAME;
    }
}
