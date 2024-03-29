/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.ssl;

import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.util.Set;
import javax.net.ssl.*;
import java.base.share.classes.sun.security.util.DisabledAlgorithmConstraints;
import static java.base.share.classes.sun.security.util.DisabledAlgorithmConstraints.*;

/**
 * Algorithm constraints for disabled algorithms property
 *
 * See the "jdk.certpath.disabledAlgorithms" specification in java.security
 * for the syntax of the disabled algorithm string.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

final class SSLAlgorithmConstraints implements AlgorithmConstraints {

    private static final AlgorithmConstraints tlsDisabledAlgConstraints =
            new DisabledAlgorithmConstraints(PROPERTY_TLS_DISABLED_ALGS,
                    new SSLAlgorithmDecomposer());

    private static final AlgorithmConstraints x509DisabledAlgConstraints =
            new DisabledAlgorithmConstraints(PROPERTY_CERTPATH_DISABLED_ALGS,
                    new SSLAlgorithmDecomposer(true));

    private final AlgorithmConstraints userSpecifiedConstraints;
    private final AlgorithmConstraints peerSpecifiedConstraints;

    private final boolean enabledX509DisabledAlgConstraints;

    // the default algorithm constraints
    static final AlgorithmConstraints DEFAULT =
                        new SSLAlgorithmConstraints(null, true);

    // the default SSL only algorithm constraints
    static final AlgorithmConstraints DEFAULT_SSL_ONLY =
                        new SSLAlgorithmConstraints(null, false);

    private SSLAlgorithmConstraints(AlgorithmConstraints userSpecifiedConstraints,
                                    boolean enabledX509DisabledAlgConstraints) {
        this(userSpecifiedConstraints, null, enabledX509DisabledAlgConstraints);
    }

    private SSLAlgorithmConstraints(
            AlgorithmConstraints userSpecifiedConstraints,
            SupportedSignatureAlgorithmConstraints peerSpecifiedConstraints,
            boolean withDefaultCertPathConstraints) {
        this.userSpecifiedConstraints = userSpecifiedConstraints;
        this.peerSpecifiedConstraints = peerSpecifiedConstraints;
        this.enabledX509DisabledAlgConstraints = withDefaultCertPathConstraints;
    }

    /**
     * Returns a SSLAlgorithmConstraints instance that checks the provided
     * {@code userSpecifiedConstraints} in addition to standard checks.
     * Returns a singleton instance if parameter is null or DEFAULT.
     * @param userSpecifiedConstraints additional constraints to check
     * @return a SSLAlgorithmConstraints instance
     */
    static AlgorithmConstraints wrap(AlgorithmConstraints userSpecifiedConstraints) {
        return wrap(userSpecifiedConstraints, true);
    }

    private static AlgorithmConstraints wrap(
            AlgorithmConstraints userSpecifiedConstraints,
            boolean withDefaultCertPathConstraints) {
        if (nullIfDefault(userSpecifiedConstraints) == null) {
            return withDefaultCertPathConstraints ? DEFAULT : DEFAULT_SSL_ONLY;
        }
        return new SSLAlgorithmConstraints(userSpecifiedConstraints,
                withDefaultCertPathConstraints);
    }

    /**
     * Returns a SSLAlgorithmConstraints instance that checks the constraints
     * configured for the given {@code socket} in addition to standard checks.
     * Returns a singleton instance if the constraints are null or DEFAULT.
     * @param socket socket with configured constraints
     * @return a SSLAlgorithmConstraints instance
     */
    static AlgorithmConstraints forSocket(SSLSocket socket,
                                          boolean withDefaultCertPathConstraints) {
        AlgorithmConstraints userSpecifiedConstraints =
                getUserSpecifiedConstraints(socket);
        return wrap(userSpecifiedConstraints, withDefaultCertPathConstraints);
    }

    static SSLAlgorithmConstraints forSocket(
            SSLSocket socket,
            String[] supportedAlgorithms,
            boolean withDefaultCertPathConstraints) {
        return new SSLAlgorithmConstraints(
                nullIfDefault(getUserSpecifiedConstraints(socket)),
                new SupportedSignatureAlgorithmConstraints(supportedAlgorithms),
                withDefaultCertPathConstraints);
    }

    /**
     * Returns a SSLAlgorithmConstraints instance that checks the constraints
     * configured for the given {@code engine} in addition to standard checks.
     * Returns a singleton instance if the constraints are null or DEFAULT.
     * @param engine engine with configured constraints
     * @return a SSLAlgorithmConstraints instance
     */
    static AlgorithmConstraints forEngine(SSLEngine engine,
                                          boolean withDefaultCertPathConstraints) {
        AlgorithmConstraints userSpecifiedConstraints =
                getUserSpecifiedConstraints(engine);
        return wrap(userSpecifiedConstraints, withDefaultCertPathConstraints);
    }

    static SSLAlgorithmConstraints forEngine(
            SSLEngine engine,
            String[] supportedAlgorithms,
            boolean withDefaultCertPathConstraints) {
        return new SSLAlgorithmConstraints(
                nullIfDefault(getUserSpecifiedConstraints(engine)),
                new SupportedSignatureAlgorithmConstraints(supportedAlgorithms),
                withDefaultCertPathConstraints);
    }

    private static AlgorithmConstraints nullIfDefault(
            AlgorithmConstraints constraints) {
        return constraints == DEFAULT ? null : constraints;
    }

    private static AlgorithmConstraints getUserSpecifiedConstraints(
            SSLEngine engine) {
        if (engine != null) {
            // Note that the KeyManager or TrustManager implementation may be
            // not implemented in the same provider as SSLSocket/SSLEngine.
            // Please check the instance before casting to use SSLEngineImpl.
            if (engine instanceof SSLEngineImpl) {
                HandshakeContext hc =
                        ((SSLEngineImpl)engine).conContext.handshakeContext;
                if (hc != null) {
                    return hc.sslConfig.userSpecifiedAlgorithmConstraints;
                }
            }

            return engine.getSSLParameters().getAlgorithmConstraints();
        }

        return null;
    }

    private static AlgorithmConstraints getUserSpecifiedConstraints(
            SSLSocket socket) {
        if (socket != null) {
            // Note that the KeyManager or TrustManager implementation may be
            // not implemented in the same provider as SSLSocket/SSLEngine.
            // Please check the instance before casting to use SSLSocketImpl.
            if (socket instanceof SSLSocketImpl) {
                HandshakeContext hc =
                        ((SSLSocketImpl)socket).conContext.handshakeContext;
                if (hc != null) {
                    return hc.sslConfig.userSpecifiedAlgorithmConstraints;
                }
            }

            return socket.getSSLParameters().getAlgorithmConstraints();
        }

        return null;
    }

    @Override
    public boolean permits(Set<CryptoPrimitive> primitives,
            String algorithm, AlgorithmParameters parameters) {

        boolean permitted = true;

        if (peerSpecifiedConstraints != null) {
            permitted = peerSpecifiedConstraints.permits(
                                    primitives, algorithm, parameters);
        }

        if (permitted && userSpecifiedConstraints != null) {
            permitted = userSpecifiedConstraints.permits(
                                    primitives, algorithm, parameters);
        }

        if (permitted) {
            permitted = tlsDisabledAlgConstraints.permits(
                                    primitives, algorithm, parameters);
        }

        if (permitted && enabledX509DisabledAlgConstraints) {
            permitted = x509DisabledAlgConstraints.permits(
                                    primitives, algorithm, parameters);
        }

        return permitted;
    }

    @Override
    public boolean permits(Set<CryptoPrimitive> primitives, Key key) {

        boolean permitted = true;

        if (peerSpecifiedConstraints != null) {
            permitted = peerSpecifiedConstraints.permits(primitives, key);
        }

        if (permitted && userSpecifiedConstraints != null) {
            permitted = userSpecifiedConstraints.permits(primitives, key);
        }

        if (permitted) {
            permitted = tlsDisabledAlgConstraints.permits(primitives, key);
        }

        if (permitted && enabledX509DisabledAlgConstraints) {
            permitted = x509DisabledAlgConstraints.permits(primitives, key);
        }

        return permitted;
    }

    @Override
    public boolean permits(Set<CryptoPrimitive> primitives,
            String algorithm, Key key, AlgorithmParameters parameters) {

        boolean permitted = true;

        if (peerSpecifiedConstraints != null) {
            permitted = peerSpecifiedConstraints.permits(
                                    primitives, algorithm, key, parameters);
        }

        if (permitted && userSpecifiedConstraints != null) {
            permitted = userSpecifiedConstraints.permits(
                                    primitives, algorithm, key, parameters);
        }

        if (permitted) {
            permitted = tlsDisabledAlgConstraints.permits(
                                    primitives, algorithm, key, parameters);
        }

        if (permitted && enabledX509DisabledAlgConstraints) {
            permitted = x509DisabledAlgConstraints.permits(
                                    primitives, algorithm, key, parameters);
        }

        return permitted;
    }


    private static class SupportedSignatureAlgorithmConstraints
                                    implements AlgorithmConstraints {
        // supported signature algorithms
        private final String[] supportedAlgorithms;

        SupportedSignatureAlgorithmConstraints(String[] supportedAlgorithms) {
            if (supportedAlgorithms != null) {
                this.supportedAlgorithms = supportedAlgorithms.clone();
            } else {
                this.supportedAlgorithms = null;
            }
        }

        @Override
        public boolean permits(Set<CryptoPrimitive> primitives,
                String algorithm, AlgorithmParameters parameters) {

            if (algorithm == null || algorithm.isEmpty()) {
                throw new IllegalArgumentException(
                        "No algorithm name specified");
            }

            if (primitives == null || primitives.isEmpty()) {
                throw new IllegalArgumentException(
                        "No cryptographic primitive specified");
            }

            if (supportedAlgorithms == null ||
                        supportedAlgorithms.length == 0) {
                return false;
            }

            // trim the MGF part: <digest>with<encryption>and<mgf>
            int position = algorithm.indexOf("and");
            if (position > 0) {
                algorithm = algorithm.substring(0, position);
            }

            for (String supportedAlgorithm : supportedAlgorithms) {
                if (algorithm.equalsIgnoreCase(supportedAlgorithm)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public final boolean permits(Set<CryptoPrimitive> primitives, Key key) {
            return true;
        }

        @Override
        public final boolean permits(Set<CryptoPrimitive> primitives,
                String algorithm, Key key, AlgorithmParameters parameters) {

            if (algorithm == null || algorithm.isEmpty()) {
                throw new IllegalArgumentException(
                        "No algorithm name specified");
            }

            return permits(primitives, algorithm, parameters);
        }
    }
}
