/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

import java.io.IOException;

import java.security.*;
import java.security.spec.*;

/**
 * This class implements encoding and decoding of Elliptic Curve parameters
 * as specified in RFC 3279.
 *
 * However, only named curves are currently supported.
 *
 * ASN.1 from RFC 3279 follows. Note that X9.62 (2005) has added some additional
 * options.
 *
 * <pre>
 *    EcpkParameters ::= CHOICE {
 *      ecParameters  ECParameters,
 *      namedCurve    OBJECT IDENTIFIER,
 *      implicitlyCA  NULL }
 *
 *    ECParameters ::= SEQUENCE {
 *       version   ECPVer,          -- version is always 1
 *       fieldID   FieldID,         -- identifies the finite field over
 *                                  -- which the curve is defined
 *       curve     Curve,           -- coefficients a and b of the
 *                                  -- elliptic curve
 *       base      ECPoint,         -- specifies the base point P
 *                                  -- on the elliptic curve
 *       order     INTEGER,         -- the order n of the base point
 *       cofactor  INTEGER OPTIONAL -- The integer h = #E(Fq)/n
 *       }
 *
 *    ECPVer ::= INTEGER {ecpVer1(1)}
 *
 *    Curve ::= SEQUENCE {
 *       a         FieldElement,
 *       b         FieldElement,
 *       seed      BIT STRING OPTIONAL }
 *
 *    FieldElement ::= OCTET STRING
 *
 *    ECPoint ::= OCTET STRING
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public final class ECParameters extends AlgorithmParametersSpi {

    // used by ECPublicKeyImpl and ECPrivateKeyImpl
    public static AlgorithmParameters getAlgorithmParameters(ECParameterSpec spec)
            throws InvalidKeyException {
        try {
            AlgorithmParameters params =
                AlgorithmParameters.getInstance("EC", "SunEC");
            params.init(spec);
            return params;
        } catch (GeneralSecurityException e) {
            throw new InvalidKeyException("EC parameters error", e);
        }
    }

    /*
     * The parameters these AlgorithmParameters object represents.
     * Currently, it is always an instance of NamedCurve.
     */
    private NamedCurve namedCurve;

    // A public constructor is required by AlgorithmParameters class.
    public ECParameters() {
        // empty
    }

    // AlgorithmParameterSpi methods

    protected void engineInit(AlgorithmParameterSpec paramSpec)
            throws InvalidParameterSpecException {

        if (paramSpec == null) {
            throw new InvalidParameterSpecException
                ("paramSpec must not be null");
        }

        if (paramSpec instanceof NamedCurve) {
            namedCurve = (NamedCurve)paramSpec;
            return;
        }

        if (paramSpec instanceof ECParameterSpec) {
            namedCurve = CurveDB.lookup((ECParameterSpec)paramSpec);
        } else if (paramSpec instanceof ECGenParameterSpec) {
            String name = ((ECGenParameterSpec)paramSpec).getName();
            namedCurve = CurveDB.lookup(name);
        } else if (paramSpec instanceof ECKeySizeParameterSpec) {
            int keySize = ((ECKeySizeParameterSpec)paramSpec).getKeySize();
            namedCurve = CurveDB.lookup(keySize);
        } else {
            throw new InvalidParameterSpecException(
                "Only ECParameterSpec, ECGenParameterSpec " +
                "and ECKeySizeParameterSpec supported");
        }

        if (namedCurve == null) {
            throw new InvalidParameterSpecException(
                "Not a supported curve: " + paramSpec);
        }
    }

    protected void engineInit(byte[] params) throws IOException {
        DerValue encodedParams = new DerValue(params);
        if (encodedParams.tag == DerValue.tag_ObjectId) {
            ObjectIdentifier oid = encodedParams.getOID();
            NamedCurve spec = CurveDB.lookup(oid.toString());
            if (spec == null) {
                throw new IOException("Unknown named curve: " + oid);
            }

            namedCurve = spec;
            return;
        }

        throw new IOException("Only named ECParameters supported");

        // The code below is incomplete.
        // It is left as a starting point for a complete parsing implementation.

/*
        if (encodedParams.tag != DerValue.tag_Sequence) {
            throw new IOException("Unsupported EC parameters, tag: " +
                encodedParams.tag);
        }

        encodedParams.data.reset();

        DerInputStream in = encodedParams.data;

        int version = in.getInteger();
        if (version != 1) {
            throw new IOException("Unsupported EC parameters version: " +
               version);
        }
        ECField field = parseField(in);
        EllipticCurve curve = parseCurve(in, field);
        ECPoint point = parsePoint(in, curve);

        BigInteger order = in.getBigInteger();
        int cofactor = 0;

        if (in.available() != 0) {
            cofactor = in.getInteger();
        }

        // XXX HashAlgorithm optional

        if (encodedParams.data.available() != 0) {
            throw new IOException("encoded params have " +
                                  encodedParams.data.available() +
                                  " extra bytes");
        }

        return new ECParameterSpec(curve, point, order, cofactor);
*/
    }

    protected void engineInit(byte[] params, String decodingMethod)
            throws IOException {
        engineInit(params);
    }

    protected <T extends AlgorithmParameterSpec> T
            engineGetParameterSpec(Class<T> spec)
            throws InvalidParameterSpecException {

        if (spec.isAssignableFrom(ECParameterSpec.class)) {
            return spec.cast(namedCurve);
        }

        if (spec.isAssignableFrom(ECGenParameterSpec.class)) {
            // Ensure the name is the Object ID
            String name = namedCurve.getObjectId();
            return spec.cast(new ECGenParameterSpec(name));
        }

        if (spec.isAssignableFrom(ECKeySizeParameterSpec.class)) {
            int keySize = namedCurve.getCurve().getField().getFieldSize();
            return spec.cast(new ECKeySizeParameterSpec(keySize));
        }

        throw new InvalidParameterSpecException(
            "Only ECParameterSpec, ECGenParameterSpec " +
            "and ECKeySizeParameterSpec supported");
    }

    protected byte[] engineGetEncoded() throws IOException {
        return namedCurve.getEncoded();
    }

    protected byte[] engineGetEncoded(String encodingMethod)
            throws IOException {
        return engineGetEncoded();
    }

    protected String engineToString() {
        if (namedCurve == null) {
            return "Not initialized";
        }

        return namedCurve.toString();
    }
}
