/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.spec;

/**
 * This class specifies the set of parameters used with mask generation
 * function MGF1 in OAEP Padding and RSASSA-PSS signature scheme, as
 * defined in the
 * <a href="https://tools.ietf.org/rfc/rfc8017.txt">PKCS#1 v2.2</a> standard.
 *
 * <p>Its ASN.1 definition in PKCS#1 standard is described below:
 * <pre>
 * PKCS1MGFAlgorithms    ALGORITHM-IDENTIFIER ::= {
 *   { OID id-mgf1 PARAMETERS HashAlgorithm },
 *   ...  -- Allows for future expansion --
 * }
 * </pre>
 * where
 * <pre>
 * HashAlgorithm ::= AlgorithmIdentifier {
 *   {OAEP-PSSDigestAlgorithms}
 * }
 *
 * OAEP-PSSDigestAlgorithms    ALGORITHM-IDENTIFIER ::= {
 *   { OID id-sha1       PARAMETERS NULL }|
 *   { OID id-sha224     PARAMETERS NULL }|
 *   { OID id-sha256     PARAMETERS NULL }|
 *   { OID id-sha384     PARAMETERS NULL }|
 *   { OID id-sha512     PARAMETERS NULL }|
 *   { OID id-sha512-224 PARAMETERS NULL }|
 *   { OID id-sha512-256 PARAMETERS NULL },
 *   ...  -- Allows for future expansion --
 * }
 * </pre>
 * @see PSSParameterSpec
 * @see javax.crypto.spec.OAEPParameterSpec
 *
 * @author Valerie Peng
 *
 * @since 1.5
 */
public class MGF1ParameterSpec implements AlgorithmParameterSpec {

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-1" message digest.
     */
    public static final MGF1ParameterSpec SHA1 =
        new MGF1ParameterSpec("SHA-1");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-224" message digest.
     */
    public static final MGF1ParameterSpec SHA224 =
        new MGF1ParameterSpec("SHA-224");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-256" message digest.
     */
    public static final MGF1ParameterSpec SHA256 =
        new MGF1ParameterSpec("SHA-256");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-384" message digest.
     */
    public static final MGF1ParameterSpec SHA384 =
        new MGF1ParameterSpec("SHA-384");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-512" message digest.
     */
    public static final MGF1ParameterSpec SHA512 =
        new MGF1ParameterSpec("SHA-512");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-512/224" message digest.
     *
     * @since 11
     */
    public static final MGF1ParameterSpec SHA512_224 =
        new MGF1ParameterSpec("SHA-512/224");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA-512/256" message digest.
     *
     * @since 11
     */
    public static final MGF1ParameterSpec SHA512_256 =
        new MGF1ParameterSpec("SHA-512/256");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA3-224" message digest.
     *
     * @since 16
     */
    public static final MGF1ParameterSpec SHA3_224 =
        new MGF1ParameterSpec("SHA3-224");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA3-256" message digest.
     *
     * @since 16
     */
    public static final MGF1ParameterSpec SHA3_256 =
        new MGF1ParameterSpec("SHA3-256");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA3-384" message digest.
     *
     * @since 16
     */
    public static final MGF1ParameterSpec SHA3_384 =
        new MGF1ParameterSpec("SHA3-384");

    /**
     * The {@code MGF1ParameterSpec} uses a "SHA3-512" message digest.
     *
     * @since 16
     */
    public static final MGF1ParameterSpec SHA3_512 =
        new MGF1ParameterSpec("SHA3-512");

    private final String mdName;

    /**
     * Constructs a parameter set for mask generation function MGF1
     * as defined in the PKCS #1 standard.
     *
     * @param mdName the algorithm name for the message digest
     * used in this mask generation function MGF1.
     * @throws    NullPointerException if {@code mdName} is null.
     */
    public MGF1ParameterSpec(String mdName) {
        if (mdName == null) {
            throw new NullPointerException("digest algorithm is null");
        }
        this.mdName = mdName;
    }

    /**
     * Returns the algorithm name of the message digest used by the mask
     * generation function.
     *
     * @return the algorithm name of the message digest.
     */
    public String getDigestAlgorithm() {
        return mdName;
    }

    @Override
    public String toString() {
        return "MGF1ParameterSpec[hashAlgorithm=" + mdName + "]";
    }
}
