/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.*;
import java.security.interfaces.DSAPublicKey;
import java.util.*;
import javax.security.auth.x500.X500Principal;

import java.base.share.classes.sun.security.util.Debug;
import java.base.share.classes.sun.security.validator.Validator;

/**
 * Common utility methods and classes used by the PKIX CertPathValidator and
 * CertPathBuilder implementation.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
class PKIX {

    private static final Debug debug = Debug.getInstance("certpath");

    private PKIX() { }

    static boolean isDSAPublicKeyWithoutParams(PublicKey publicKey) {
        return (publicKey instanceof DSAPublicKey &&
               ((DSAPublicKey)publicKey).getParams() == null);
    }

    static ValidatorParams checkParams(CertPath cp, CertPathParameters params)
        throws InvalidAlgorithmParameterException
    {
        if (!(params instanceof PKIXParameters)) {
            throw new InvalidAlgorithmParameterException("inappropriate "
                + "params, must be an instance of PKIXParameters");
        }
        return new ValidatorParams(cp, (PKIXParameters)params);
    }

    static BuilderParams checkBuilderParams(CertPathParameters params)
        throws InvalidAlgorithmParameterException
    {
        if (!(params instanceof PKIXBuilderParameters)) {
            throw new InvalidAlgorithmParameterException("inappropriate "
                + "params, must be an instance of PKIXBuilderParameters");
        }
        return new BuilderParams((PKIXBuilderParameters)params);
    }

    /**
     * PKIXParameters that are shared by the PKIX CertPathValidator
     * implementation. Provides additional functionality and avoids
     * unnecessary cloning.
     */
    static class ValidatorParams {
        private final PKIXParameters params;
        private CertPath certPath;
        private List<PKIXCertPathChecker> checkers;
        private List<CertStore> stores;
        private boolean gotDate;
        private Date date;
        private Set<String> policies;
        private boolean gotConstraints;
        private CertSelector constraints;
        private final Set<TrustAnchor> anchors;
        private List<X509Certificate> certs;
        private Timestamp timestamp;
        private Date timestampDate;
        private String variant = Validator.VAR_GENERIC;

        ValidatorParams(CertPath cp, PKIXParameters params)
            throws InvalidAlgorithmParameterException
        {
            this(params);
            if (!cp.getType().equals("X.509") && !cp.getType().equals("X509")) {
                throw new InvalidAlgorithmParameterException("inappropriate "
                    + "CertPath type specified, must be X.509 or X509");
            }
            this.certPath = cp;
        }

        ValidatorParams(PKIXParameters params)
            throws InvalidAlgorithmParameterException
        {
            if (params instanceof PKIXExtendedParameters) {
                timestamp = ((PKIXExtendedParameters) params).getTimestamp();
                variant = ((PKIXExtendedParameters) params).getVariant();
            }

            this.anchors = params.getTrustAnchors();
            // Make sure that none of the trust anchors include name constraints
            // (not supported).
            for (TrustAnchor anchor : this.anchors) {
                if (anchor.getNameConstraints() != null) {
                    throw new InvalidAlgorithmParameterException
                        ("name constraints in trust anchor not supported");
                }
            }
            this.params = params;
        }

        CertPath certPath() {
            return certPath;
        }
        // called by CertPathBuilder after path has been built
        void setCertPath(CertPath cp) {
            this.certPath = cp;
        }
        List<X509Certificate> certificates() {
            if (certs == null) {
                if (certPath == null) {
                    certs = Collections.emptyList();
                } else {
                    // Reverse the ordering for validation so that the target
                    // cert is the last certificate
                    @SuppressWarnings("unchecked")
                    List<X509Certificate> xc = new ArrayList<>
                        ((List<X509Certificate>)certPath.getCertificates());
                    Collections.reverse(xc);
                    certs = xc;
                }
            }
            return certs;
        }
        List<PKIXCertPathChecker> certPathCheckers() {
            if (checkers == null)
                checkers = params.getCertPathCheckers();
            return checkers;
        }
        List<CertStore> certStores() {
            if (stores == null)
                stores = params.getCertStores();
            return stores;
        }
        // The date() param is used when enforcing the validity period
        // of certificates and when checking the time period of revocation data.
        // The main difference between the date() and timestamp() method is
        // that the date() method only uses the timestamp (if specified)
        // for certificates in a code signer's chain.
        Date date() {
            if (!gotDate) {
                // Use timestamp if checking signed code that is
                // timestamped, otherwise use date parameter.
                // Note that TSA server certificates do not use the
                // timestamp, which means that an expired TSA certificate
                // is considered a validation failure. This policy means
                // that signed and timestamped code is valid until the TSA
                // certificate expires (assuming all other checks are valid).
                if (timestamp != null &&
                    variant.equals(Validator.VAR_CODE_SIGNING)) {
                    date = timestamp.getTimestamp();
                } else {
                    date = params.getDate();
                    if (date == null)
                        date = new Date();
                }
                gotDate = true;
            }
            return date;
        }
        Set<String> initialPolicies() {
            if (policies == null)
                policies = params.getInitialPolicies();
            return policies;
        }
        CertSelector targetCertConstraints() {
            if (!gotConstraints) {
                constraints = params.getTargetCertConstraints();
                gotConstraints = true;
            }
            return constraints;
        }
        Set<TrustAnchor> trustAnchors() {
            return anchors;
        }
        boolean revocationEnabled() {
            return params.isRevocationEnabled();
        }
        boolean policyMappingInhibited() {
            return params.isPolicyMappingInhibited();
        }
        boolean explicitPolicyRequired() {
            return params.isExplicitPolicyRequired();
        }
        boolean policyQualifiersRejected() {
            return params.getPolicyQualifiersRejected();
        }
        String sigProvider() { return params.getSigProvider(); }
        boolean anyPolicyInhibited() { return params.isAnyPolicyInhibited(); }

        // in rare cases we need access to the original params, for example
        // in order to clone CertPathCheckers before building a new chain
        PKIXParameters getPKIXParameters() {
            return params;
        }

        String variant() {
            return variant;
        }
        // The timestamp() param is passed as the date param when creating an
        // AlgorithmChecker. An AlgorithmChecker always uses the timestamp
        // if specified in order to enforce the denyAfter constraint.
        Date timestamp() {
            // return timestamp date if set, otherwise use date parameter
            if (timestampDate == null) {
                timestampDate = (timestamp != null)
                    ? timestamp.getTimestamp() : date();
            }
            return timestampDate;
        }
    }

    static class BuilderParams extends ValidatorParams {
        private PKIXBuilderParameters params;
        private List<CertStore> stores;
        private X500Principal targetSubject;

        BuilderParams(PKIXBuilderParameters params)
            throws InvalidAlgorithmParameterException
        {
            super(params);
            checkParams(params);
        }
        private void checkParams(PKIXBuilderParameters params)
            throws InvalidAlgorithmParameterException
        {
            CertSelector sel = targetCertConstraints();
            if (!(sel instanceof X509CertSelector)) {
                throw new InvalidAlgorithmParameterException("the "
                    + "targetCertConstraints parameter must be an "
                    + "X509CertSelector");
            }
            this.params = params;
            this.targetSubject = getTargetSubject(
                certStores(), (X509CertSelector)targetCertConstraints());
        }
        @Override List<CertStore> certStores() {
            if (stores == null) {
                // reorder CertStores so that local CertStores are tried first
                stores = new ArrayList<>(params.getCertStores());
                stores.sort(new CertStoreComparator());
            }
            return stores;
        }
        int maxPathLength() { return params.getMaxPathLength(); }
        PKIXBuilderParameters params() { return params; }
        X500Principal targetSubject() { return targetSubject; }

        /**
         * Returns the target subject DN from the first X509Certificate that
         * is fetched that matches the specified X509CertSelector.
         */
        private static X500Principal getTargetSubject(List<CertStore> stores,
                                                      X509CertSelector sel)
            throws InvalidAlgorithmParameterException
        {
            X500Principal subject = sel.getSubject();
            if (subject != null) {
                return subject;
            }
            X509Certificate cert = sel.getCertificate();
            if (cert != null) {
                subject = cert.getSubjectX500Principal();
            }
            if (subject != null) {
                return subject;
            }
            for (CertStore store : stores) {
                try {
                    Collection<? extends Certificate> certs =
                            store.getCertificates(sel);
                    if (!certs.isEmpty()) {
                        X509Certificate xc =
                            (X509Certificate)certs.iterator().next();
                        return xc.getSubjectX500Principal();
                    }
                } catch (CertStoreException e) {
                    // ignore but log it
                    if (debug != null) {
                        debug.println("BuilderParams.getTargetSubjectDN: " +
                            "non-fatal exception retrieving certs: " + e);
                        e.printStackTrace();
                    }
                }
            }
            throw new InvalidAlgorithmParameterException
                ("Could not determine unique target subject");
        }
    }

    /**
     * A CertStoreException with additional information about the type of
     * CertStore that generated the exception.
     */
    static class CertStoreTypeException extends CertStoreException {
        @java.io.Serial
        private static final long serialVersionUID = 7463352639238322556L;

        private final String type;

        CertStoreTypeException(String type, CertStoreException cse) {
            super(cse.getMessage(), cse.getCause());
            this.type = type;
        }
        String getType() {
            return type;
        }
    }

    /**
     * Comparator that orders CertStores so that local CertStores come before
     * remote CertStores.
     */
    private static class CertStoreComparator implements Comparator<CertStore> {
        @Override
        public int compare(CertStore store1, CertStore store2) {
            if (store1.getType().equals("Collection") ||
                store1.getCertStoreParameters() instanceof
                CollectionCertStoreParameters) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
