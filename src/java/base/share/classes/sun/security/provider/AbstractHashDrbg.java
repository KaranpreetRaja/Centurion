/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider;

import java.base.share.classes.sun.security.util.HexDumpEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */
public abstract class AbstractHashDrbg extends AbstractDrbg {

    protected int outLen;
    protected int seedLen;

    private static int alg2strength(String algorithm) {
        switch (algorithm.toUpperCase(Locale.ROOT)) {
            case "SHA-224":
            case "SHA-512/224":
                return 192;
            case "SHA-256":
            case "SHA-512/256":
            case "SHA-384":
            case "SHA-512":
                return 256;
            default:
                throw new IllegalArgumentException(algorithm +
                        " not supported in Hash_DBRG");
        }
    }

    protected void chooseAlgorithmAndStrength() {
        if (requestedAlgorithm != null) {
            algorithm = requestedAlgorithm.toUpperCase(Locale.ROOT);
            int supportedStrength = alg2strength(algorithm);
            if (requestedInstantiationSecurityStrength >= 0) {
                int tryStrength = getStandardStrength(
                        requestedInstantiationSecurityStrength);
                if (tryStrength > supportedStrength) {
                    throw new IllegalArgumentException(algorithm +
                            " does not support strength " +
                            requestedInstantiationSecurityStrength);
                }
                this.securityStrength = tryStrength;
            } else {
                this.securityStrength = Math.min(DEFAULT_STRENGTH, supportedStrength);
            }
        } else {
            int tryStrength = (requestedInstantiationSecurityStrength < 0) ?
                    DEFAULT_STRENGTH : requestedInstantiationSecurityStrength;
            tryStrength = getStandardStrength(tryStrength);
            // The default algorithm which is enough for all strengths.
            // Remember to sync with "securerandom.drbg.config" in java.security
            algorithm = "SHA-256";
            this.securityStrength = tryStrength;
        }
        switch (algorithm.toUpperCase(Locale.ROOT)) {
            case "SHA-224":
            case "SHA-512/224":
                this.seedLen = 440 / 8;
                this.outLen = 224 / 8;
                break;
            case "SHA-256":
            case "SHA-512/256":
                this.seedLen = 440 / 8;
                this.outLen = 256 / 8;
                break;
            case "SHA-384":
                this.seedLen = 888 / 8;
                this.outLen = 384 / 8;
                break;
            case "SHA-512":
                this.seedLen = 888 / 8;
                this.outLen = 512 / 8;
                break;
            default:
                throw new IllegalArgumentException(algorithm +
                        " not supported in Hash_DBRG");
        }
        this.minLength = this.securityStrength / 8;
    }

    @Override
    public void instantiateAlgorithm(byte[] entropy) {
        if (debug != null) {
            debug.println(this, "instantiate");
        }

        // 800-90Ar1 10.1.1.2: Hash_DRBG Instantiate Process.
        // 800-90Ar1 10.1.2.3: Hmac_DRBG Instantiate Process.

        // Step 1: entropy_input || nonce || personalization_string.
        List<byte[]> inputs = new ArrayList<>(3);
        inputs.add(entropy);
        inputs.add(nonce);
        if (personalizationString != null) {
            inputs.add(personalizationString);
        }
        hashReseedInternal(inputs);
    }

    @Override
    protected void reseedAlgorithm(
            byte[] ei,
            byte[] additionalInput) {
        if (debug != null) {
            debug.println(this, "reseedAlgorithm\n" +
                    new HexDumpEncoder().encodeBuffer(ei) + "\n" +
                    ((additionalInput == null) ? "" :
                        new HexDumpEncoder().encodeBuffer(additionalInput)));
        }

        // 800-90Ar1 10.1.1.3: Hash_DRBG Reseed Process.
        // 800-90Ar1 10.1.2.4: Hmac_DRBG Reseed Process.

        // Step 1: entropy_input || additional_input.
        List<byte[]> inputs = new ArrayList<>(2);
        inputs.add(ei);
        if (additionalInput != null) {
            inputs.add(additionalInput);
        }
        hashReseedInternal(inputs);
    }

    /**
     * Operates on multiple inputs.
     * @param inputs not null, each element neither null
     */
    protected abstract void hashReseedInternal(List<byte[]> inputs);
}
