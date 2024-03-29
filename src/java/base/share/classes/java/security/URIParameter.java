/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */


package java.base.share.classes.java.security;

/**
 * A parameter that contains a URI pointing to data intended for a
 * PolicySpi or ConfigurationSpi implementation.
 *
 * @since 1.6
 */
@SuppressWarnings("removal")
public class URIParameter implements
        Policy.Parameters, javax.security.auth.login.Configuration.Parameters {

    private final java.net.URI uri;

    /**
     * Constructs a {@code URIParameter} with the URI pointing to
     * data intended for an SPI implementation.
     *
     * @param uri the URI pointing to the data.
     *
     * @throws    NullPointerException if the specified URI is {@code null}.
     */
    public URIParameter(java.net.URI uri) {
        if (uri == null) {
            throw new NullPointerException("invalid null URI");
        }
        this.uri = uri;
    }

    /**
     * Returns the URI.
     *
     * @return uri the URI.
     */
    public java.net.URI getURI() {
        return uri;
    }
}
