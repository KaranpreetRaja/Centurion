/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLHandshakeException;
import java.base.share.classes.sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import java.base.share.classes.sun.security.util.KeyUtil;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

final class RSAKeyExchange {
    static final SSLPossessionGenerator poGenerator =
            new EphemeralRSAPossessionGenerator();
    static final SSLKeyAgreementGenerator kaGenerator =
            new RSAKAGenerator();

    static final class EphemeralRSAPossession implements SSLPossession {
        // Proof of possession of the private key corresponding to the public
        // key for which a certificate is being provided for authentication.
        final RSAPublicKey        popPublicKey;
        final PrivateKey          popPrivateKey;

        EphemeralRSAPossession(PrivateKey popPrivateKey,
                RSAPublicKey popPublicKey) {
            this.popPublicKey = popPublicKey;
            this.popPrivateKey = popPrivateKey;
        }
    }

    static final class EphemeralRSACredentials implements SSLCredentials {
        final RSAPublicKey popPublicKey;

        EphemeralRSACredentials(RSAPublicKey popPublicKey) {
            this.popPublicKey = popPublicKey;
        }
    }

    private static final class EphemeralRSAPossessionGenerator
            implements SSLPossessionGenerator {
        // Prevent instantiation of this class.
        private EphemeralRSAPossessionGenerator() {
            // blank
        }

        @Override
        public SSLPossession createPossession(HandshakeContext context) {
            try {
                EphemeralKeyManager ekm =
                        context.sslContext.getEphemeralKeyManager();
                KeyPair kp = ekm.getRSAKeyPair(
                        true, context.sslContext.getSecureRandom());
                if (kp != null) {
                    return new EphemeralRSAPossession(
                            kp.getPrivate(), (RSAPublicKey)kp.getPublic());
                } else {
                    // Could not generate the ephemeral key, ignore.
                    return null;
                }
            } catch (RuntimeException rte) {
                // Could not determine keylength, ignore.
                return null;
            }
        }
    }

    static final
            class RSAPremasterSecret implements SSLPossession, SSLCredentials {
        final SecretKey premasterSecret;

        RSAPremasterSecret(SecretKey premasterSecret) {
            this.premasterSecret = premasterSecret;
        }

        byte[] getEncoded(PublicKey publicKey,
                SecureRandom secureRandom) throws GeneralSecurityException {
            Cipher cipher = Cipher.getInstance(JsseJce.CIPHER_RSA_PKCS1);
            cipher.init(Cipher.WRAP_MODE, publicKey, secureRandom);
            return cipher.wrap(premasterSecret);
        }

        @SuppressWarnings("deprecation")
        static RSAPremasterSecret createPremasterSecret(
                ClientHandshakeContext chc) throws GeneralSecurityException {
            String algorithm = chc.negotiatedProtocol.useTLS12PlusSpec() ?
                    "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
            KeyGenerator kg = KeyGenerator.getInstance(algorithm);
            TlsRsaPremasterSecretParameterSpec spec =
                    new TlsRsaPremasterSecretParameterSpec(
                            chc.clientHelloVersion,
                            chc.negotiatedProtocol.id);
            kg.init(spec, chc.sslContext.getSecureRandom());

            return new RSAPremasterSecret(kg.generateKey());
        }

        @SuppressWarnings("deprecation")
        static RSAPremasterSecret decode(ServerHandshakeContext shc,
                PrivateKey privateKey,
                byte[] encrypted) throws GeneralSecurityException {

            byte[] encoded = null;
            boolean needFailover;
            Cipher cipher = Cipher.getInstance(JsseJce.CIPHER_RSA_PKCS1);
            try {
                // Try UNWRAP_MODE mode firstly.
                cipher.init(Cipher.UNWRAP_MODE, privateKey,
                        new TlsRsaPremasterSecretParameterSpec(
                                shc.clientHelloVersion,
                                shc.negotiatedProtocol.id),
                                shc.sslContext.getSecureRandom());

                // The provider selection can be delayed, please don't call
                // any Cipher method before the call to Cipher.init().
                needFailover = !KeyUtil.isOracleJCEProvider(
                        cipher.getProvider().getName());
            } catch (InvalidKeyException | UnsupportedOperationException iue) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("The Cipher provider "
                            + safeProviderName(cipher)
                            + " caused exception: " + iue.getMessage());
                }

                needFailover = true;
            }

            SecretKey preMaster;
            if (needFailover) {
                // The cipher might be spoiled by unsuccessful call to init(),
                // so request a fresh instance
                cipher = Cipher.getInstance(JsseJce.CIPHER_RSA_PKCS1);

                // Use DECRYPT_MODE and dispose the previous initialization.
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                boolean failed = false;
                try {
                    encoded = cipher.doFinal(encrypted);
                } catch (BadPaddingException bpe) {
                    // Note: encoded == null
                    failed = true;
                }
                encoded = KeyUtil.checkTlsPreMasterSecretKey(
                        shc.clientHelloVersion, shc.negotiatedProtocol.id,
                        shc.sslContext.getSecureRandom(), encoded, failed);
                preMaster = generatePremasterSecret(
                        shc.clientHelloVersion, shc.negotiatedProtocol.id,
                        encoded, shc.sslContext.getSecureRandom());
            } else {
                // the cipher should have been initialized
                preMaster = (SecretKey)cipher.unwrap(encrypted,
                        "TlsRsaPremasterSecret", Cipher.SECRET_KEY);
            }

            return new RSAPremasterSecret(preMaster);
        }

        /*
         * Retrieving the cipher's provider name for the debug purposes
         * can throw an exception by itself.
         */
        private static String safeProviderName(Cipher cipher) {
            try {
                return cipher.getProvider().toString();
            } catch (Exception e) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Retrieving The Cipher provider name" +
                            " caused exception ", e);
                }
            }
            try {
                return cipher.toString() + " (provider name not available)";
            } catch (Exception e) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Retrieving The Cipher name" +
                            " caused exception ", e);
                }
            }

            return "(cipher/provider names not available)";
        }

        // generate a premaster secret with the specified version number
        @SuppressWarnings("deprecation")
        private static SecretKey generatePremasterSecret(
                int clientVersion, int serverVersion, byte[] encodedSecret,
                SecureRandom generator) throws GeneralSecurityException {

            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Generating a premaster secret");
            }

            try {
                String s = ((clientVersion >= ProtocolVersion.TLS12.id) ?
                    "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret");
                KeyGenerator kg = KeyGenerator.getInstance(s);
                kg.init(new TlsRsaPremasterSecretParameterSpec(
                        clientVersion, serverVersion, encodedSecret),
                        generator);
                return kg.generateKey();
            } catch (InvalidAlgorithmParameterException |
                    NoSuchAlgorithmException iae) {
                // unlikely to happen, otherwise, must be a provider exception
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("RSA premaster secret generation error", iae);
                }

                throw new GeneralSecurityException(
                        "Could not generate premaster secret", iae);
            }
        }
    }

    private static final
            class RSAKAGenerator implements SSLKeyAgreementGenerator {
        // Prevent instantiation of this class.
        private RSAKAGenerator() {
            // blank
        }

        @Override
        public SSLKeyDerivation createKeyDerivation(
                HandshakeContext context) throws IOException {
            RSAPremasterSecret premaster = null;
            if (context instanceof ClientHandshakeContext) {
                for (SSLPossession possession : context.handshakePossessions) {
                    if (possession instanceof RSAPremasterSecret) {
                        premaster = (RSAPremasterSecret)possession;
                        break;
                    }
                }
            } else {
                for (SSLCredentials credential : context.handshakeCredentials) {
                    if (credential instanceof RSAPremasterSecret) {
                        premaster = (RSAPremasterSecret)credential;
                        break;
                    }
                }
            }

            if (premaster == null) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE,
                    "No sufficient RSA key agreement parameters negotiated");
            }

            return new RSAKAKeyDerivation(context, premaster.premasterSecret);
        }

        private static final
                class RSAKAKeyDerivation implements SSLKeyDerivation {
            private final HandshakeContext context;
            private final SecretKey preMasterSecret;

            RSAKAKeyDerivation(
                    HandshakeContext context, SecretKey preMasterSecret) {
                this.context = context;
                this.preMasterSecret = preMasterSecret;
            }

            @Override
            public SecretKey deriveKey(String algorithm,
                    AlgorithmParameterSpec params) throws IOException {
                SSLMasterKeyDerivation mskd =
                        SSLMasterKeyDerivation.valueOf(
                                context.negotiatedProtocol);
                if (mskd == null) {
                    // unlikely
                    throw new SSLHandshakeException(
                            "No expected master key derivation for protocol: " +
                            context.negotiatedProtocol.name);
                }
                SSLKeyDerivation kd = mskd.createKeyDerivation(
                        context, preMasterSecret);
                return kd.deriveKey("MasterSecret", params);
            }
        }
    }
}
