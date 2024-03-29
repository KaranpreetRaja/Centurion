/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider;

import java.io.*;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;

import java.base.share.classes.sun.security.util.Debug;
import java.base.share.classes.sun.security.util.DerValue;
import java.base.share.classes.sun.security.util.DerOutputStream;

/**
 * This class implements the parameter set used by the
 * Digital Signature Algorithm as specified in the FIPS 186
 * standard.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

public class DSAParameters extends AlgorithmParametersSpi {

    // the prime (p)
    protected BigInteger p;

    // the sub-prime (q)
    protected BigInteger q;

    // the base (g)
    protected BigInteger g;

    protected void engineInit(AlgorithmParameterSpec paramSpec)
        throws InvalidParameterSpecException {
            if (!(paramSpec instanceof DSAParameterSpec)) {
                throw new InvalidParameterSpecException
                    ("Inappropriate parameter specification");
            }
            this.p = ((DSAParameterSpec)paramSpec).getP();
            this.q = ((DSAParameterSpec)paramSpec).getQ();
            this.g = ((DSAParameterSpec)paramSpec).getG();
    }

    protected void engineInit(byte[] params) throws IOException {
        DerValue encodedParams = new DerValue(params);

        if (encodedParams.tag != DerValue.tag_Sequence) {
            throw new IOException("DSA params parsing error");
        }

        encodedParams.data.reset();

        this.p = encodedParams.data.getBigInteger();
        this.q = encodedParams.data.getBigInteger();
        this.g = encodedParams.data.getBigInteger();

        if (encodedParams.data.available() != 0) {
            throw new IOException("encoded params have " +
                                  encodedParams.data.available() +
                                  " extra bytes");
        }
    }

    protected void engineInit(byte[] params, String decodingMethod)
        throws IOException {
            engineInit(params);
    }

    protected <T extends AlgorithmParameterSpec>
        T engineGetParameterSpec(Class<T> paramSpec)
        throws InvalidParameterSpecException
    {
            try {
                Class<?> dsaParamSpec = Class.forName
                    ("java.security.spec.DSAParameterSpec");
                if (paramSpec.isAssignableFrom(dsaParamSpec)) {
                    return paramSpec.cast(
                            new DSAParameterSpec(this.p, this.q, this.g));
                } else {
                    throw new InvalidParameterSpecException
                        ("Inappropriate parameter Specification");
                }
            } catch (ClassNotFoundException e) {
                throw new InvalidParameterSpecException
                    ("Unsupported parameter specification: " + e.getMessage());
            }
    }

    protected byte[] engineGetEncoded() throws IOException {
        DerOutputStream out = new DerOutputStream();
        DerOutputStream bytes = new DerOutputStream();

        bytes.putInteger(p);
        bytes.putInteger(q);
        bytes.putInteger(g);
        out.write(DerValue.tag_Sequence, bytes);
        return out.toByteArray();
    }

    protected byte[] engineGetEncoded(String encodingMethod)
        throws IOException {
            return engineGetEncoded();
    }

    /*
     * Returns a formatted string describing the parameters.
     */
    protected String engineToString() {
        return "\n\tp: " + Debug.toHexString(p)
            + "\n\tq: " + Debug.toHexString(q)
            + "\n\tg: " + Debug.toHexString(g)
            + "\n";
    }
}
