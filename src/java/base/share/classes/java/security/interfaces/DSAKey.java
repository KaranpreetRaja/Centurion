/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.interfaces;

/**
 * The interface to a DSA public or private key. DSA (Digital Signature
 * Algorithm) is defined in NIST's FIPS-186.
 *
 * @see DSAParams
 * @see java.security.Key
 * @see java.security.Signature
 *
 * @author Benjamin Renaud
 * @author Josh Bloch
 * @since 1.1
 */
public interface DSAKey {

    /**
     * Returns the DSA-specific key parameters. These parameters are
     * never secret.
     *
     * @return the DSA-specific key parameters.
     *
     * @see DSAParams
     */
    DSAParams getParams();
}
