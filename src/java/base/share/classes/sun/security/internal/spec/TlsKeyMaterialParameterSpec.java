/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.internal.spec;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.SecretKey;

/**
 * Parameters for SSL/TLS key material generation.
 * This class is used to initialize KeyGenerator of the type
 * "TlsKeyMaterial". The keys returned by such KeyGenerators will be
 * instances of {@link TlsKeyMaterialSpec}.
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
public class TlsKeyMaterialParameterSpec implements AlgorithmParameterSpec {

    private final SecretKey masterSecret;
    private final int majorVersion, minorVersion;
    private final byte[] clientRandom, serverRandom;
    private final String cipherAlgorithm;
    private final int cipherKeyLength, ivLength, macKeyLength;
    private final int expandedCipherKeyLength; // == 0 for domestic ciphersuites
    private final String prfHashAlg;
    private final int prfHashLength;
    private final int prfBlockSize;

    /**
     * Constructs a new TlsKeyMaterialParameterSpec.
     *
     * @param masterSecret the master secret
     * @param majorVersion the major number of the protocol version
     * @param minorVersion the minor number of the protocol version
     * @param clientRandom the client's random value
     * @param serverRandom the server's random value
     * @param cipherAlgorithm the algorithm name of the cipher keys to
     *    be generated
     * @param cipherKeyLength if 0, no cipher keys will be generated;
     *    otherwise, the length in bytes of cipher keys to be
     *    generated for domestic cipher suites; for cipher suites defined as
     *    exportable, the number of key material bytes to be generated;
     * @param expandedCipherKeyLength 0 for domestic cipher suites; for
     *    exportable cipher suites the length in bytes of the key to be
     *    generated.
     * @param ivLength the length in bytes of the initialization vector
     *    to be generated, or 0 if no initialization vector is required
     * @param macKeyLength the length in bytes of the MAC key to be generated
     * @param prfHashAlg the name of the TLS PRF hash algorithm to use.
     *        Used only for TLS 1.2+.  TLS1.1 and earlier use a fixed PRF.
     * @param prfHashLength the output length of the TLS PRF hash algorithm.
     *        Used only for TLS 1.2+.
     * @param prfBlockSize the input block size of the TLS PRF hash algorithm.
     *        Used only for TLS 1.2+.
     *
     * @throws NullPointerException if masterSecret, clientRandom,
     *   serverRandom, or cipherAlgorithm are null
     * @throws IllegalArgumentException if the algorithm of masterSecret is
     *   not TlsMasterSecret, or if majorVersion or minorVersion are
     *   negative or larger than 255; or if cipherKeyLength, expandedKeyLength,
     *   ivLength, or macKeyLength are negative
     */
    public TlsKeyMaterialParameterSpec(SecretKey masterSecret,
            int majorVersion, int minorVersion, byte[] clientRandom,
            byte[] serverRandom, String cipherAlgorithm, int cipherKeyLength,
            int expandedCipherKeyLength, int ivLength, int macKeyLength,
            String prfHashAlg, int prfHashLength, int prfBlockSize) {
        if (!masterSecret.getAlgorithm().equals("TlsMasterSecret")) {
            throw new IllegalArgumentException("Not a TLS master secret");
        }
        if (cipherAlgorithm == null) {
            throw new NullPointerException();
        }
        this.masterSecret = masterSecret;
        this.majorVersion =
            TlsMasterSecretParameterSpec.checkVersion(majorVersion);
        this.minorVersion =
            TlsMasterSecretParameterSpec.checkVersion(minorVersion);
        this.clientRandom = clientRandom.clone();
        this.serverRandom = serverRandom.clone();
        this.cipherAlgorithm = cipherAlgorithm;
        this.cipherKeyLength = checkSign(cipherKeyLength);
        this.expandedCipherKeyLength = checkSign(expandedCipherKeyLength);
        this.ivLength = checkSign(ivLength);
        this.macKeyLength = checkSign(macKeyLength);
        this.prfHashAlg = prfHashAlg;
        this.prfHashLength = prfHashLength;
        this.prfBlockSize = prfBlockSize;
    }

    private static int checkSign(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("Value must not be negative");
        }
        return k;
    }

    /**
     * Returns the master secret.
     *
     * @return the master secret.
     */
    public SecretKey getMasterSecret() {
        return masterSecret;
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
     * Returns the cipher algorithm.
     *
     * @return the cipher algorithm.
     */
    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    /**
     * Returns the length in bytes of the encryption key to be generated.
     *
     * @return the length in bytes of the encryption key to be generated.
     */
    public int getCipherKeyLength() {
        return cipherKeyLength;
    }

    /**
     * Returns the length in bytes of the expanded encryption key to be
     * generated. Returns zero if the expanded encryption key is not
     * supposed to be generated.
     *
     * @return the length in bytes of the expanded encryption key to be
     *     generated.
     */
    public int getExpandedCipherKeyLength() {
        // TLS v1.1 disables the exportable weak cipher suites.
        if (majorVersion >= 0x03 && minorVersion >= 0x02) {
            return 0;
        }
        return expandedCipherKeyLength;
    }

    /**
     * Returns the length in bytes of the initialization vector to be
     * generated. Returns zero if the initialization vector is not
     * supposed to be generated.
     *
     * @return the length in bytes of the initialization vector to be
     *     generated.
     */
    public int getIvLength() {
        return ivLength;
    }

    /**
     * Returns the length in bytes of the MAC key to be generated.
     *
     * @return the length in bytes of the MAC key to be generated.
     */
    public int getMacKeyLength() {
        return macKeyLength;
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
     * @return the hash algorithm block size
     */
    public int getPRFBlockSize() {
        return prfBlockSize;
    }
}
