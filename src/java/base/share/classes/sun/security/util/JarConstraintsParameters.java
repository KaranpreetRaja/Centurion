/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

import java.security.CodeSigner;
import java.security.Key;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.base.share.classes.sun.security.validator.Validator;

/**
 * This class contains parameters for checking signed JARs against
 * constraints specified in the jdk.jar.disabledAlgorithms security
 * property.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public class JarConstraintsParameters implements ConstraintsParameters {

    // true if chain is anchored by a JDK root CA
    private boolean anchorIsJdkCA;
    private boolean anchorIsJdkCASet;
    // The timestamp of the signed JAR file, if timestamped
    private final Date timestamp;
    // The keys of the signers and TSA
    private final Set<Key> keys;
    // The certs in the signers and TSA chain that are issued by the trust anchor
    private final Set<X509Certificate> certsIssuedByAnchor;
    // The extended exception message
    private String message;

    /**
     * Create a JarConstraintsParameters.
     *
     * @param signers the CodeSigners that signed the JAR
     */
    public JarConstraintsParameters(CodeSigner[] signers) {
        this.keys = new HashSet<>();
        this.certsIssuedByAnchor = new HashSet<>();
        Date latestTimestamp = null;
        boolean skipTimestamp = false;

        // Iterate over the signers and extract the keys, the latest
        // timestamp, and the last certificate of each chain which can be
        // used for checking if the signer's certificate chains back to a
        // JDK root CA
        for (CodeSigner signer : signers) {
            addToCertsAndKeys(signer.getSignerCertPath());
            Timestamp timestamp = signer.getTimestamp();
            if (timestamp == null) {
                // this means one of the signers doesn't have a timestamp
                // and the JAR should be treated as if it isn't timestamped
                latestTimestamp = null;
                skipTimestamp = true;
            } else {
                // add the key and last cert of TSA too
                addToCertsAndKeys(timestamp.getSignerCertPath());
                if (!skipTimestamp) {
                    Date timestampDate = timestamp.getTimestamp();
                    if (latestTimestamp == null) {
                        latestTimestamp = timestampDate;
                    } else {
                        if (latestTimestamp.before(timestampDate)) {
                            latestTimestamp = timestampDate;
                        }
                    }
                }
            }
        }
        this.timestamp = latestTimestamp;
    }

    public JarConstraintsParameters(List<X509Certificate> chain, Date timestamp) {
        this.keys = new HashSet<>();
        this.certsIssuedByAnchor = new HashSet<>();
        addToCertsAndKeys(chain);
        this.timestamp = timestamp;
    }

    // extract last certificate and signer's public key from chain
    private void addToCertsAndKeys(CertPath cp) {
        @SuppressWarnings("unchecked")
        List<X509Certificate> chain =
            (List<X509Certificate>)cp.getCertificates();
        addToCertsAndKeys(chain);
    }

    private void addToCertsAndKeys(List<X509Certificate> chain) {
        if (!chain.isEmpty()) {
            this.certsIssuedByAnchor.add(chain.get(chain.size() - 1));
            this.keys.add(chain.get(0).getPublicKey());
        }
    }

    @Override
    public String getVariant() {
        return Validator.VAR_GENERIC;
    }

    /**
     * Since loading the cacerts keystore can be an expensive operation,
     * this is only performed if this method is called during a "jdkCA"
     * constraints check of a disabled algorithm, and the result is cached.
     *
     * @return true if at least one of the certificates are issued by a
     *              JDK root CA
     */
    @Override
    public boolean anchorIsJdkCA() {
        if (anchorIsJdkCASet) {
            return anchorIsJdkCA;
        }
        for (X509Certificate cert : certsIssuedByAnchor) {
            if (AnchorCertificates.issuerOf(cert)) {
                anchorIsJdkCA = true;
                break;
            }
        }
        anchorIsJdkCASet = true;
        return anchorIsJdkCA;
    }

    @Override
    public Date getDate() {
        return timestamp;
    }

    @Override
    public Set<Key> getKeys() {
        return keys;
    }

    /**
     * Sets the extended error message. Note: this should be used
     * carefully as it is specific to the attribute/entry/file being checked.
     *
     * @param file the name of the signature related file being verified
     * @param target the attribute containing the algorithm that is being
     *        checked
     */
    public void setExtendedExceptionMsg(String file, String target) {
        message = " used" + (target != null ? " with " + target : "") +
                  " in " + file + " file.";
    }

    @Override
    public String extendedExceptionMsg() {
        return message == null ? "." : message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[\n");
        sb.append("  Variant: ").append(getVariant());
        sb.append("\n  Certs Issued by Anchor:");
        for (X509Certificate cert : certsIssuedByAnchor) {
            sb.append("\n    Cert Issuer: ")
              .append(cert.getIssuerX500Principal());
            sb.append("\n    Cert Subject: ")
              .append(cert.getSubjectX500Principal());
        }
        for (Key key : keys) {
            sb.append("\n  Key: ").append(key.getAlgorithm());
        }
        if (timestamp != null) {
            sb.append("\n  Timestamp: ").append(timestamp);
        }
        sb.append("\n]");
        return sb.toString();
    }
}
