/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.net;

import java.security.cert.Certificate;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Represents a cache response originally retrieved through secure
 * means, such as TLS.
 *
 * @since 1.5
 */
public abstract class SecureCacheResponse extends CacheResponse {
    /**
     * Constructor for subclasses to call.
     */
    public SecureCacheResponse() {}

    /**
     * Returns the cipher suite in use on the original connection that
     * retrieved the network resource.
     *
     * @return a string representing the cipher suite
     */
    public abstract String getCipherSuite();

    /**
     * Returns the certificate chain that were sent to the server during
     * handshaking of the original connection that retrieved the
     * network resource.  Note: This method is useful only
     * when using certificate-based cipher suites.
     *
     * @return an immutable List of Certificate representing the
     *           certificate chain that was sent to the server. If no
     *           certificate chain was sent, null will be returned.
     * @see #getLocalPrincipal()
     */
    public abstract List<Certificate> getLocalCertificateChain();

    /**
     * Returns the server's certificate chain, which was established as
     * part of defining the session in the original connection that
     * retrieved the network resource, from cache.  Note: This method
     * can be used only when using certificate-based cipher suites;
     * using it with non-certificate-based cipher suites, such as
     * Kerberos, will throw an SSLPeerUnverifiedException.
     *
     * @return an immutable List of Certificate representing the server's
     *         certificate chain.
     * @throws SSLPeerUnverifiedException if the peer is not verified.
     * @see #getPeerPrincipal()
     */
    public abstract List<Certificate> getServerCertificateChain()
        throws SSLPeerUnverifiedException;

    /**
     * Returns the server's principal which was established as part of
     * defining the session during the original connection that
     * retrieved the network resource.
     *
     * @return the server's principal. Returns an X500Principal of the
     * end-entity certificate for X509-based cipher suites, and
     * KerberosPrincipal for Kerberos cipher suites.
     *
     * @throws SSLPeerUnverifiedException if the peer was not verified.
     *
     * @see #getServerCertificateChain()
     * @see #getLocalPrincipal()
     */
     public abstract Principal getPeerPrincipal()
             throws SSLPeerUnverifiedException;

     /**
      * Returns the principal that was sent to the server during
      * handshaking in the original connection that retrieved the
      * network resource.
      *
      * @return the principal sent to the server. Returns an X500Principal
      * of the end-entity certificate for X509-based cipher suites, and
      * KerberosPrincipal for Kerberos cipher suites. If no principal was
      * sent, then null is returned.
      *
      * @see #getLocalCertificateChain()
      * @see #getPeerPrincipal()
      */
     public abstract Principal getLocalPrincipal();

    /**
     * Returns an {@link Optional} containing the {@code SSLSession} in
     * use on the original connection that retrieved the network resource.
     * Returns an empty {@code Optional} if the underlying implementation
     * does not support this method.
     *
     * @implSpec For compatibility, the default implementation of this
     *           method returns an empty {@code Optional}.  Subclasses
     *           should override this method with an appropriate
     *           implementation since an application may need to access
     *           additional parameters associated with the SSL session.
     *
     * @return   an {@link Optional} containing the {@code SSLSession} in
     *           use on the original connection
     *
     * @see SSLSession
     *
     * @since 12
     */
    public Optional<SSLSession> getSSLSession() {
        return Optional.empty();
    }
}
