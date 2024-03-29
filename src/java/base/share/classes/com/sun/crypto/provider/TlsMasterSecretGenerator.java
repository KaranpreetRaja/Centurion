/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.*;

import java.base.share.classes.sun.security.internal.interfaces.TlsMasterSecret;
import java.base.share.classes.sun.security.internal.spec.TlsMasterSecretParameterSpec;

import static java.base.share.classes.com.sun.crypto.provider.TlsPrfGenerator.*;

/**
 * KeyGenerator implementation for the SSL/TLS master secret derivation.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
public final class TlsMasterSecretGenerator extends KeyGeneratorSpi {

    private static final String MSG = "TlsMasterSecretGenerator must be "
        + "initialized using a TlsMasterSecretParameterSpec";

    @SuppressWarnings("deprecation")
    private TlsMasterSecretParameterSpec spec;

    private int protocolVersion;

    public TlsMasterSecretGenerator() {
    }

    protected void engineInit(SecureRandom random) {
        throw new InvalidParameterException(MSG);
    }

    @SuppressWarnings("deprecation")
    protected void engineInit(AlgorithmParameterSpec params,
            SecureRandom random) throws InvalidAlgorithmParameterException {
        if (params instanceof TlsMasterSecretParameterSpec == false) {
            throw new InvalidAlgorithmParameterException(MSG);
        }
        this.spec = (TlsMasterSecretParameterSpec)params;
        if ("RAW".equals(spec.getPremasterSecret().getFormat()) == false) {
            throw new InvalidAlgorithmParameterException(
                "Key format must be RAW");
        }
        protocolVersion = (spec.getMajorVersion() << 8)
            | spec.getMinorVersion();
        if ((protocolVersion < 0x0300) || (protocolVersion > 0x0303)) {
            throw new InvalidAlgorithmParameterException(
                "Only SSL 3.0, TLS 1.0/1.1/1.2 supported");
        }
    }

    protected void engineInit(int keysize, SecureRandom random) {
        throw new InvalidParameterException(MSG);
    }

    protected SecretKey engineGenerateKey() {
        if (spec == null) {
            throw new IllegalStateException(
                "TlsMasterSecretGenerator must be initialized");
        }
        SecretKey premasterKey = spec.getPremasterSecret();
        byte[] premaster = premasterKey.getEncoded();

        int premasterMajor, premasterMinor;
        if (premasterKey.getAlgorithm().equals("TlsRsaPremasterSecret")) {
            // RSA
            premasterMajor = premaster[0] & 0xff;
            premasterMinor = premaster[1] & 0xff;
        } else {
            // DH, others
            premasterMajor = -1;
            premasterMinor = -1;
        }

        try {
            byte[] master;
            if (protocolVersion >= 0x0301) {
                byte[] label;
                byte[] seed;
                byte[] extendedMasterSecretSessionHash =
                        spec.getExtendedMasterSecretSessionHash();
                if (extendedMasterSecretSessionHash.length != 0) {
                    label = LABEL_EXTENDED_MASTER_SECRET;
                    seed = extendedMasterSecretSessionHash;
                } else {
                    byte[] clientRandom = spec.getClientRandom();
                    byte[] serverRandom = spec.getServerRandom();
                    label = LABEL_MASTER_SECRET;
                    seed = concat(clientRandom, serverRandom);
                }
                master = ((protocolVersion >= 0x0303) ?
                        doTLS12PRF(premaster, label, seed, 48,
                                spec.getPRFHashAlg(), spec.getPRFHashLength(),
                                spec.getPRFBlockSize()) :
                        doTLS10PRF(premaster, label, seed, 48));
            } else {
                master = new byte[48];
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                MessageDigest sha = MessageDigest.getInstance("SHA");

                byte[] clientRandom = spec.getClientRandom();
                byte[] serverRandom = spec.getServerRandom();
                byte[] tmp = new byte[20];
                for (int i = 0; i < 3; i++) {
                    sha.update(SSL3_CONST[i]);
                    sha.update(premaster);
                    sha.update(clientRandom);
                    sha.update(serverRandom);
                    sha.digest(tmp, 0, 20);
                    sha.reset();

                    md5.update(premaster);
                    md5.update(tmp);
                    md5.digest(master, i << 4, 16);
                    md5.reset();
                }
            }
            // master is referenced inside the TlsMasterSecretKey.
            // Do not touch it anymore.
            return new TlsMasterSecretKey(master, premasterMajor,
                premasterMinor);
        } catch (NoSuchAlgorithmException | DigestException e) {
            throw new ProviderException(e);
        } finally {
            if (premaster != null) {
                Arrays.fill(premaster, (byte)0);
            }
        }
    }

   @SuppressWarnings("deprecation")
   private static final class TlsMasterSecretKey implements TlsMasterSecret {
        @java.io.Serial
        private static final long serialVersionUID = 1019571680375368880L;

        private byte[] key;
        private final int majorVersion, minorVersion;

        TlsMasterSecretKey(byte[] key, int majorVersion, int minorVersion) {
            this.key = key;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }

        public int getMajorVersion() {
            return majorVersion;
        }

        public int getMinorVersion() {
            return minorVersion;
        }

        public String getAlgorithm() {
            return "TlsMasterSecret";
        }

        public String getFormat() {
            return "RAW";
        }

        public byte[] getEncoded() {
            return key.clone();
        }

    }
}

