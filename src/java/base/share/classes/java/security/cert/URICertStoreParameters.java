/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.cert;

import java.net.URI;

/**
 * Parameters used as input for {@code CertStore} algorithms which use
 * information contained in a URI to retrieve certificates and CRLs.
 * <p>
 * This class is used to provide necessary configuration parameters
 * through a URI as defined in RFC 5280 to implementations of
 * {@code CertStore} algorithms.
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * Unless otherwise specified, the methods defined in this class are not
 * thread-safe. Multiple threads that need to access a single
 * object concurrently should synchronize amongst themselves and
 * provide the necessary locking. Multiple threads each manipulating
 * separate objects need not synchronize.
 *
 * @since       9
 * @see         CertStore
 * @see         java.net.URI
 */
public final class URICertStoreParameters implements CertStoreParameters {

    /**
     * The uri, cannot be null
     */
    private final URI uri;

    /*
     * Hash code for this parameters object.
     */
    private int myhash = -1;

    /**
     * Creates an instance of {@code URICertStoreParameters} with the
     * specified URI.
     *
     * @param uri the URI which contains configuration information.
     * @throws NullPointerException if {@code uri} is null
     */
    public URICertStoreParameters(URI uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        this.uri = uri;
    }

    /**
     * Returns the URI used to construct this
     * {@code URICertStoreParameters} object.
     *
     * @return the URI.
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Returns a copy of this object. Changes to the copy will not affect
     * the original and vice versa.
     *
     * @return the copy
     */
    @Override
    public URICertStoreParameters clone() {
        try {
            return new URICertStoreParameters(uri);
        } catch (NullPointerException e) {
            /* Cannot happen */
            throw new InternalError(e.toString(), e);
        }
    }

    /**
     * Returns a hash code value for this parameters object.
     * The hash code is generated using the URI supplied at construction.
     *
     * @return a hash code value for this parameters object.
     */
    @Override
    public int hashCode() {
        if (myhash == -1) {
            myhash = uri.hashCode()*7;
        }
        return myhash;
    }

    /**
     * Compares the specified object with this parameters object for equality.
     * Two URICertStoreParameters are considered equal if the URIs used
     * to construct them are equal.
     *
     * @param p the object to test for equality with this parameters object.
     *
     * @return true if the specified object is equal to this parameters object.
     */
    @Override
    public boolean equals(Object p) {
        if (p == this) {
            return true;
        }
        return p instanceof URICertStoreParameters other
                && uri.equals(other.getURI());
    }

    /**
     * Returns a formatted string describing the parameters
     * including the URI used to construct this object.
     *
     * @return a formatted string describing the parameters
     */
    @Override
    public String toString() {
        return "URICertStoreParameters: " + uri.toString();
    }
}
