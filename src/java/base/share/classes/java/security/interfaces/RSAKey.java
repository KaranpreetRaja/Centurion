/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.interfaces;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

/**
 * The interface to a public or private key in
 * <a href="https://tools.ietf.org/rfc/rfc8017.txt">PKCS#1 v2.2</a> standard,
 * such as those for RSA, or RSASSA-PSS algorithms.
 *
 * @author Jan Luehe
 *
 * @see RSAPublicKey
 * @see RSAPrivateKey
 *
 * @since 1.3
 */

public interface RSAKey {

    /**
     * Returns the modulus.
     *
     * @return the modulus
     */
    BigInteger getModulus();

    /**
     * Returns the parameters associated with this key.
     * The parameters are optional and may be either
     * explicitly specified or implicitly created during
     * key pair generation.
     *
     * @implSpec
     * The default implementation returns {@code null}.
     *
     * @return the associated parameters, may be null
     * @since 11
     */
    default AlgorithmParameterSpec getParams() {
        return null;
    }
}
