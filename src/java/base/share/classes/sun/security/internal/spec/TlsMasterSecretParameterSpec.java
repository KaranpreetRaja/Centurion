/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.internal.spec;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.SecretKey;

/**
 * Parameters for SSL/TLS master secret generation.
 * This class encapsulates the information necessary to calculate an SSL/TLS
 * master secret from the premaster secret and other parameters.
 * It is used to initialize KeyGenerators of the type "TlsMasterSecret".
 *
 * <p>Instances of this class are immutable.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 * @deprecated Sun JDK internal use only --- WILL BE REMOVED in a future
 * release.
 */
@Deprecated
public class TlsMasterSecretParameterSpec implements AlgorithmParameterSpec {

    private final SecretKey premasterSecret;
    private final int majorVersion, minorVersion;
    private final byte[] clientRandom, serverRandom;
    private final byte[] extendedMasterSecretSessionHash;
    private final String prfHashAlg;
    private final int prfHashLength;
    private final int prfBlockSize;

    /**
     * Constructs a new TlsMasterSecretParameterSpec.
     *
     * <p>The <code>getAlgorithm()</code> method of <code>premasterSecret</code>
     * should return <code>"TlsRsaPremasterSecret"</code> if the key exchange
     * algorithm was RSA and <code>"TlsPremasterSecret"</code> otherwise.
     *
     * @param premasterSecret the premaster secret
     * @param majorVersion the major number of the protocol version
     * @param minorVersion the minor number of the protocol version
     * @param clientRandom the client's random value
     * @param serverRandom the server's random value
     * @param prfHashAlg the name of the TLS PRF hash algorithm to use.
     *        Used only for TLS 1.2+.  TLS1.1 and earlier use a fixed PRF.
     * @param prfHashLength the output length of the TLS PRF hash algorithm.
     *        Used only for TLS 1.2+.
     * @param prfBlockSize the input block size of the TLS PRF hash algorithm.
     *        Used only for TLS 1.2+.
     *
     * @throws NullPointerException if premasterSecret, clientRandom,
     *   or serverRandom are null
     * @throws IllegalArgumentException if minorVersion or majorVersion are
     *   negative or larger than 255
     */
    public TlsMasterSecretParameterSpec(SecretKey premasterSecret,
            int majorVersion, int minorVersion,
            byte[] clientRandom, byte[] serverRandom,
            String prfHashAlg, int prfHashLength, int prfBlockSize) {
        this(premasterSecret, majorVersion, minorVersion,
                clientRandom, serverRandom,
                new byte[0],
                prfHashAlg, prfHashLength, prfBlockSize);
    }

    /**
     * Constructs a new TlsMasterSecretParameterSpec.
     *
     * <p>The <code>getAlgorithm()</code> method of <code>premasterSecret</code>
     * should return <code>"TlsRsaPremasterSecret"</code> if the key exchange
     * algorithm was RSA and <code>"TlsPremasterSecret"</code> otherwise.
     *
     * @param premasterSecret the premaster secret
     * @param majorVersion the major number of the protocol version
     * @param minorVersion the minor number of the protocol version
     * @param extendedMasterSecretSessionHash the session hash for
     *        Extended Master Secret
     * @param prfHashAlg the name of the TLS PRF hash algorithm to use.
     *        Used only for TLS 1.2+.  TLS1.1 and earlier use a fixed PRF.
     * @param prfHashLength the output length of the TLS PRF hash algorithm.
     *        Used only for TLS 1.2+.
     * @param prfBlockSize the input block size of the TLS PRF hash algorithm.
     *        Used only for TLS 1.2+.
     *
     * @throws NullPointerException if premasterSecret is null
     * @throws IllegalArgumentException if minorVersion or majorVersion are
     *   negative or larger than 255
     */
    public TlsMasterSecretParameterSpec(SecretKey premasterSecret,
            int majorVersion, int minorVersion,
            byte[] extendedMasterSecretSessionHash,
            String prfHashAlg, int prfHashLength, int prfBlockSize) {
        this(premasterSecret, majorVersion, minorVersion,
                new byte[0], new byte[0],
                extendedMasterSecretSessionHash,
                prfHashAlg, prfHashLength, prfBlockSize);
    }

    private TlsMasterSecretParameterSpec(SecretKey premasterSecret,
            int majorVersion, int minorVersion,
            byte[] clientRandom, byte[] serverRandom,
            byte[] extendedMasterSecretSessionHash,
            String prfHashAlg, int prfHashLength, int prfBlockSize) {
        if (premasterSecret == null) {
            throw new NullPointerException("premasterSecret must not be null");
        }
        this.premasterSecret = premasterSecret;
        this.majorVersion = checkVersion(majorVersion);
        this.minorVersion = checkVersion(minorVersion);
        this.clientRandom = clientRandom.clone();
        this.serverRandom = serverRandom.clone();
        this.extendedMasterSecretSessionHash =
                (extendedMasterSecretSessionHash != null ?
                        extendedMasterSecretSessionHash.clone() : new byte[0]);
        this.prfHashAlg = prfHashAlg;
        this.prfHashLength = prfHashLength;
        this.prfBlockSize = prfBlockSize;
    }

    static int checkVersion(int version) {
        if ((version < 0) || (version > 255)) {
            throw new IllegalArgumentException(
                        "Version must be between 0 and 255");
        }
        return version;
    }

    /**
     * Returns the premaster secret.
     *
     * @return the premaster secret.
     */
    public SecretKey getPremasterSecret() {
        return premasterSecret;
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number.
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Returns the minor version number.
     *
     * @return the minor version number.
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Returns a copy of the client's random value.
     *
     * @return a copy of the client's random value.
     */
    public byte[] getClientRandom() {
        return clientRandom.clone();
    }

    /**
     * Returns a copy of the server's random value.
     *
     * @return a copy of the server's random value.
     */
    public byte[] getServerRandom() {
        return serverRandom.clone();
    }

    /**
     * Returns a copy of the Extended Master Secret session hash.
     *
     * @return a copy of the Extended Master Secret session hash, or an empty
     *         array if no extended master secret session hash was provided
     *         at instantiation time
     */
    public byte[] getExtendedMasterSecretSessionHash() {
        return extendedMasterSecretSessionHash.clone();
    }

    /**
     * Obtains the PRF hash algorithm to use in the PRF calculation.
     *
     * @return the hash algorithm.
     */
    public String getPRFHashAlg() {
        return prfHashAlg;
    }

    /**
     * Obtains the length of the PRF hash algorithm.
     *
     * @return the hash algorithm length.
     */
    public int getPRFHashLength() {
        return prfHashLength;
    }

    /**
     * Obtains the block size of the PRF hash algorithm.
     *
     * @return the hash algorithm block size.
     */
    public int getPRFBlockSize() {
        return prfBlockSize;
    }
}
