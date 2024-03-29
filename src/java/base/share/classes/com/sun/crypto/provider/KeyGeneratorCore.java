/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * KeyGeneratore core implementation and individual key generator
 * implementations. Because of US export regulations, we cannot use
 * subclassing to achieve code sharing between the key generator
 * implementations for our various algorithms. Instead, we have the
 * core implementation in this KeyGeneratorCore class, which is used
 * by the individual implementations. See those further down in this
 * file.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
final class KeyGeneratorCore {

    // algorithm name to use for the generator keys
    private final String name;

    // default key size in bits
    private final int defaultKeySize;

    // current key size in bits
    private int keySize;

    // PRNG to use
    private SecureRandom random;

    /**
     * Construct a new KeyGeneratorCore object with the specified name
     * and defaultKeySize. Initialize to default key size in case the
     * application does not call any of the init() methods.
     */
    KeyGeneratorCore(String name, int defaultKeySize) {
        this.name = name;
        this.defaultKeySize = defaultKeySize;
        implInit(null);
    }

    // implementation for engineInit(), see JCE doc
    // reset keySize to default
    void implInit(SecureRandom random) {
        this.keySize = defaultKeySize;
        this.random = random;
    }

    // implementation for engineInit(), see JCE doc
    // we do not support any parameters
    void implInit(AlgorithmParameterSpec params, SecureRandom random)
            throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException
            (name + " key generation does not take any parameters");
    }

    // implementation for engineInit(), see JCE doc
    // we enforce a general 40 bit minimum key size for security
    void implInit(int keysize, SecureRandom random) {
        if (keysize < 40) {
            throw new InvalidParameterException
                ("Key length must be at least 40 bits");
        }
        this.keySize = keysize;
        this.random = random;
    }

    // implementation for engineInit(), see JCE doc
    // generate the key
    SecretKey implGenerateKey() {
        if (random == null) {
            random = SunJCE.getRandom();
        }
        byte[] b = new byte[(keySize + 7) >> 3];
        random.nextBytes(b);
        try {
            return new SecretKeySpec(b, name);
        } finally {
            Arrays.fill(b, (byte)0);
        }
    }

    // nested static classes for the Hmac key generator
    abstract static class HmacKG extends KeyGeneratorSpi {
        private final KeyGeneratorCore core;
        protected HmacKG(String algoName, int len) {
            core = new KeyGeneratorCore(algoName, len);
        }
        @Override
        protected void engineInit(SecureRandom random) {
            core.implInit(random);
        }
        @Override
        protected void engineInit(AlgorithmParameterSpec params,
                SecureRandom random) throws InvalidAlgorithmParameterException {
            core.implInit(params, random);
        }
        @Override
        protected void engineInit(int keySize, SecureRandom random) {
            core.implInit(keySize, random);
        }
        @Override
        protected SecretKey engineGenerateKey() {
            return core.implGenerateKey();
        }

        public static final class SHA224 extends HmacKG {
            public SHA224() {
                super("HmacSHA224", 224);
            }
        }
        public static final class SHA256 extends HmacKG {
            public SHA256() {
                super("HmacSHA256", 256);
            }
        }
        public static final class SHA384 extends HmacKG {
            public SHA384() {
                super("HmacSHA384", 384);
            }
        }
        public static final class SHA512 extends HmacKG {
            public SHA512() {
                super("HmacSHA512", 512);
            }
        }
        public static final class SHA512_224 extends HmacKG {
            public SHA512_224() {
                super("HmacSHA512/224", 224);
            }
        }
        public static final class SHA512_256 extends HmacKG {
            public SHA512_256() {
                super("HmacSHA512/256", 256);
            }
        }
        public static final class SHA3_224 extends HmacKG {
            public SHA3_224() {
                super("HmacSHA3-224", 224);
            }
        }
        public static final class SHA3_256 extends HmacKG {
            public SHA3_256() {
                super("HmacSHA3-256", 256);
            }
        }
        public static final class SHA3_384 extends HmacKG {
            public SHA3_384() {
                super("HmacSHA3-384", 384);
            }
        }
        public static final class SHA3_512 extends HmacKG {
            public SHA3_512() {
                super("HmacSHA3-512", 512);
            }
        }
    }

    // nested static class for the RC2 key generator
    public static final class RC2KeyGenerator extends KeyGeneratorSpi {
        private final KeyGeneratorCore core;
        public RC2KeyGenerator() {
            core = new KeyGeneratorCore("RC2", 128);
        }
        @Override
        protected void engineInit(SecureRandom random) {
            core.implInit(random);
        }
        @Override
        protected void engineInit(AlgorithmParameterSpec params,
                SecureRandom random) throws InvalidAlgorithmParameterException {
            core.implInit(params, random);
        }
        @Override
        protected void engineInit(int keySize, SecureRandom random) {
            if ((keySize < 40) || (keySize > 1024)) {
                throw new InvalidParameterException("Key length for RC2"
                    + " must be between 40 and 1024 bits");
            }
            core.implInit(keySize, random);
        }
        @Override
        protected SecretKey engineGenerateKey() {
            return core.implGenerateKey();
        }
    }

    // nested static class for the ARCFOUR (RC4) key generator
    public static final class ARCFOURKeyGenerator extends KeyGeneratorSpi {
        private final KeyGeneratorCore core;
        public ARCFOURKeyGenerator() {
            core = new KeyGeneratorCore("ARCFOUR", 128);
        }
        @Override
        protected void engineInit(SecureRandom random) {
            core.implInit(random);
        }
        @Override
        protected void engineInit(AlgorithmParameterSpec params,
                SecureRandom random) throws InvalidAlgorithmParameterException {
            core.implInit(params, random);
        }
        @Override
        protected void engineInit(int keySize, SecureRandom random) {
            if ((keySize < 40) || (keySize > 1024)) {
                throw new InvalidParameterException("Key length for ARCFOUR"
                    + " must be between 40 and 1024 bits");
            }
            core.implInit(keySize, random);
        }
        @Override
        protected SecretKey engineGenerateKey() {
            return core.implGenerateKey();
        }
    }

    // nested static class for the ChaCha20 key generator
    public static final class ChaCha20KeyGenerator extends KeyGeneratorSpi {
        private final KeyGeneratorCore core;
        public ChaCha20KeyGenerator() {
            core = new KeyGeneratorCore("ChaCha20", 256);
        }
        @Override
        protected void engineInit(SecureRandom random) {
            core.implInit(random);
        }
        @Override
        protected void engineInit(AlgorithmParameterSpec params,
                SecureRandom random) throws InvalidAlgorithmParameterException {
            core.implInit(params, random);
        }
        @Override
        protected void engineInit(int keySize, SecureRandom random) {
            if (keySize != 256) {
                throw new InvalidParameterException(
                        "Key length for ChaCha20 must be 256 bits");
            }
            core.implInit(keySize, random);
        }
        @Override
        protected SecretKey engineGenerateKey() {
            return core.implGenerateKey();
        }
    }
}
