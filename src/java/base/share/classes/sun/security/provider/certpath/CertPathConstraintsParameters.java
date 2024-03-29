/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.security.Key;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

import java.base.share.classes.sun.security.util.ConstraintsParameters;
import java.base.share.classes.sun.security.validator.Validator;

/**
 * This class contains parameters for checking certificates against
 * constraints specified in the jdk.certpath.disabledAlgorithms security
 * property.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public class CertPathConstraintsParameters implements ConstraintsParameters {
    // The public key of the certificate
    private final Key key;
    // The certificate's trust anchor which will be checked against the
    // jdkCA constraint, if specified.
    private final TrustAnchor anchor;
    // The PKIXParameter validity date or the timestamp of the signed JAR
    // file, if this chain is associated with a timestamped signed JAR.
    private final Date date;
    // The variant or usage of this certificate
    private final String variant;
    // The certificate being checked (may be null if a raw public key, a CRL
    // or an OCSPResponse is being checked)
    private final X509Certificate cert;

    public CertPathConstraintsParameters(X509Certificate cert,
            String variant, TrustAnchor anchor, Date date) {
        this(cert.getPublicKey(), variant, anchor, date, cert);
    }

    public CertPathConstraintsParameters(Key key, String variant,
            TrustAnchor anchor, Date date) {
        this(key, variant, anchor, date, null);
    }

    private CertPathConstraintsParameters(Key key, String variant,
            TrustAnchor anchor, Date date, X509Certificate cert) {
        this.key = key;
        this.variant = (variant == null ? Validator.VAR_GENERIC : variant);
        this.anchor = anchor;
        this.date = date;
        this.cert = cert;
    }

    @Override
    public boolean anchorIsJdkCA() {
        return CertPathHelper.isJdkCA(anchor);
    }

    @Override
    public Set<Key> getKeys() {
        return (key == null) ? Set.of() : Set.of(key);
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getVariant() {
        return variant;
    }

    @Override
    public String extendedExceptionMsg() {
        return (cert == null ? "."
                 : " used with certificate: " +
                   cert.getSubjectX500Principal());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[\n");
        sb.append("  Variant: ").append(variant);
        if (anchor != null) {
            sb.append("\n  Anchor: ").append(anchor);
        }
        if (cert != null) {
            sb.append("\n  Cert Issuer: ")
              .append(cert.getIssuerX500Principal());
            sb.append("\n  Cert Subject: ")
              .append(cert.getSubjectX500Principal());
        }
        if (key != null) {
            sb.append("\n  Key: ").append(key.getAlgorithm());
        }
        if (date != null) {
            sb.append("\n  Date: ").append(date);
        }
        sb.append("\n]");
        return sb.toString();
    }
}
