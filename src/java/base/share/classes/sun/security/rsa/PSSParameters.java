/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.rsa;

import java.io.*;
import java.base.share.classes.sun.security.util.*;
import java.base.share.classes.sun.security.x509.*;
import java.security.AlgorithmParametersSpi;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

/**
 * This class implements the PSS parameters used with the RSA
 * signatures in PSS padding. Here is its ASN.1 definition:
 * RSASSA-PSS-params ::= SEQUENCE {
 *   hashAlgorithm      [0] HashAlgorithm     DEFAULT sha1,
 *   maskGenAlgorithm   [1] MaskGenAlgorithm  DEFAULT mgf1SHA1,
 *   saltLength         [2] INTEGER           DEFAULT 20
 *   trailerField       [3] TrailerField      DEFAULT trailerFieldBC
 * }
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public final class PSSParameters extends AlgorithmParametersSpi {

    private PSSParameterSpec spec;

    public PSSParameters() {
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec paramSpec)
            throws InvalidParameterSpecException {
        if (!(paramSpec instanceof PSSParameterSpec spec)) {
            throw new InvalidParameterSpecException
                ("Inappropriate parameter specification");
        }

        String mgfName = spec.getMGFAlgorithm();
        if (!spec.getMGFAlgorithm().equalsIgnoreCase("MGF1")) {
            throw new InvalidParameterSpecException("Unsupported mgf " +
                mgfName + "; MGF1 only");
        }
        AlgorithmParameterSpec mgfSpec = spec.getMGFParameters();
        if (!(mgfSpec instanceof MGF1ParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate mgf " +
                "parameters; non-null MGF1ParameterSpec only");
        }
        this.spec = spec;
    }

    @Override
    protected void engineInit(byte[] encoded) throws IOException {
        // first initialize with the ASN.1 DEFAULT values defined in PKCS #1
        // v2.2 since the encoding bytes may not define all fields
        String mdName = "SHA-1";
        MGF1ParameterSpec mgfSpec = MGF1ParameterSpec.SHA1;
        int saltLength = 20;
        int trailerField = PSSParameterSpec.TRAILER_FIELD_BC;

        DerInputStream der = new DerInputStream(encoded);
        DerValue[] datum = der.getSequence(4);

        for (DerValue d : datum) {
            if (d.isContextSpecific((byte) 0x00)) {
                // hash algid
                mdName = AlgorithmId.parse
                    (d.data.getDerValue()).getName();
            } else if (d.isContextSpecific((byte) 0x01)) {
                // mgf algid
                AlgorithmId val = AlgorithmId.parse(d.data.getDerValue());
                if (!val.getOID().equals(AlgorithmId.MGF1_oid)) {
                    throw new IOException("Only MGF1 mgf is supported");
                }

                byte[] encodedParams = val.getEncodedParams();
                if (encodedParams == null) {
                    throw new IOException("Missing MGF1 parameters");
                }
                AlgorithmId params = AlgorithmId.parse(
                        new DerValue(encodedParams));
                String mgfDigestName = params.getName();
                switch (mgfDigestName) {
                case "SHA-1":
                    mgfSpec = MGF1ParameterSpec.SHA1;
                    break;
                case "SHA-224":
                    mgfSpec = MGF1ParameterSpec.SHA224;
                    break;
                case "SHA-256":
                    mgfSpec = MGF1ParameterSpec.SHA256;
                    break;
                case "SHA-384":
                    mgfSpec = MGF1ParameterSpec.SHA384;
                    break;
                case "SHA-512":
                    mgfSpec = MGF1ParameterSpec.SHA512;
                    break;
                case "SHA-512/224":
                    mgfSpec = MGF1ParameterSpec.SHA512_224;
                    break;
                case "SHA-512/256":
                    mgfSpec = MGF1ParameterSpec.SHA512_256;
                    break;
                case "SHA3-224":
                    mgfSpec = MGF1ParameterSpec.SHA3_224;
                    break;
                case "SHA3-256":
                    mgfSpec = MGF1ParameterSpec.SHA3_256;
                    break;
                case "SHA3-384":
                    mgfSpec = MGF1ParameterSpec.SHA3_384;
                    break;
                case "SHA3-512":
                    mgfSpec = MGF1ParameterSpec.SHA3_512;
                    break;
                default:
                    throw new IOException
                        ("Unrecognized message digest algorithm " +
                        mgfDigestName);
                }
            } else if (d.isContextSpecific((byte) 0x02)) {
                // salt length
                saltLength = d.data.getDerValue().getInteger();
                if (saltLength < 0) {
                    throw new IOException("Negative value for saltLength");
                }
            } else if (d.isContextSpecific((byte) 0x03)) {
                // trailer field
                trailerField = d.data.getDerValue().getInteger();
                if (trailerField != 1) {
                    throw new IOException("Unsupported trailerField value " +
                    trailerField);
                }
            } else {
                throw new IOException("Invalid encoded PSSParameters");
            }
        }

        this.spec = new PSSParameterSpec(mdName, "MGF1", mgfSpec,
                saltLength, trailerField);
    }

    @Override
    protected void engineInit(byte[] encoded, String decodingMethod)
            throws IOException {
        if ((decodingMethod != null) &&
            (!decodingMethod.equalsIgnoreCase("ASN.1"))) {
            throw new IllegalArgumentException("Only support ASN.1 format");
        }
        engineInit(encoded);
    }

    @Override
    protected <T extends AlgorithmParameterSpec>
            T engineGetParameterSpec(Class<T> paramSpec)
            throws InvalidParameterSpecException {
        if (paramSpec.isAssignableFrom(PSSParameterSpec.class)) {
            return paramSpec.cast(spec);
        } else {
            throw new InvalidParameterSpecException
                ("Inappropriate parameter specification");
        }
    }

    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return getEncoded(spec);
    }

    @Override
    protected byte[] engineGetEncoded(String encMethod) throws IOException {
        if ((encMethod != null) &&
            (!encMethod.equalsIgnoreCase("ASN.1"))) {
            throw new IllegalArgumentException("Only support ASN.1 format");
        }
        return engineGetEncoded();
    }

    @Override
    protected String engineToString() {
        return spec.toString();
    }

    /**
     * Returns the encoding of a {@link PSSParameterSpec} object. This method
     * is used in this class and {@link AlgorithmId}.
     *
     * @param spec a {@code PSSParameterSpec} object
     * @return its DER encoding
     * @throws IOException if the name of a MessageDigest or MaskGenAlgorithm
     *          is unsupported
     */
    public static byte[] getEncoded(PSSParameterSpec spec) throws IOException {

        AlgorithmParameterSpec mgfSpec = spec.getMGFParameters();
        if (!(mgfSpec instanceof MGF1ParameterSpec mgf1Spec)) {
            throw new IOException("Cannot encode " + mgfSpec);
        }

        DerOutputStream tmp = new DerOutputStream();
        DerOutputStream tmp2, tmp3;

        // MD
        AlgorithmId mdAlgId;
        try {
            mdAlgId = AlgorithmId.get(spec.getDigestAlgorithm());
        } catch (NoSuchAlgorithmException nsae) {
            throw new IOException("AlgorithmId " + spec.getDigestAlgorithm() +
                    " impl not found");
        }
        if (!mdAlgId.getOID().equals(AlgorithmId.SHA_oid)) {
            tmp2 = new DerOutputStream();
            mdAlgId.encode(tmp2);
            tmp.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte) 0),
                    tmp2);
        }

        // MGF
        AlgorithmId mgfDigestId;
        try {
            mgfDigestId = AlgorithmId.get(mgf1Spec.getDigestAlgorithm());
        } catch (NoSuchAlgorithmException nase) {
            throw new IOException("AlgorithmId " +
                    mgf1Spec.getDigestAlgorithm() + " impl not found");
        }

        if (!mgfDigestId.getOID().equals(AlgorithmId.SHA_oid)) {
            tmp2 = new DerOutputStream();
            tmp2.putOID(AlgorithmId.MGF1_oid);
            mgfDigestId.encode(tmp2);
            tmp3 = new DerOutputStream();
            tmp3.write(DerValue.tag_Sequence, tmp2);
            tmp.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte) 1),
                    tmp3);
        }

        // SaltLength
        if (spec.getSaltLength() != 20) {
            tmp2 = new DerOutputStream();
            tmp2.putInteger(spec.getSaltLength());
            tmp.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte) 2),
                    tmp2);
        }

        // TrailerField
        if (spec.getTrailerField() != PSSParameterSpec.TRAILER_FIELD_BC) {
            tmp2 = new DerOutputStream();
            tmp2.putInteger(spec.getTrailerField());
            tmp.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte) 3),
                    tmp2);
        }

        // Put all together under a SEQUENCE tag
        DerOutputStream out = new DerOutputStream();
        out.write(DerValue.tag_Sequence, tmp);
        return out.toByteArray();
    }
}
