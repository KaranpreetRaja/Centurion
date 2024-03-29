/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.cert.*;
import java.util.*;
import javax.net.ssl.*;

/**
 * A CertStore that retrieves an SSL server's certificate chain.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public final class SSLServerCertStore extends CertStoreSpi {

    private final URI uri;
    private static final GetChainTrustManager trustManager;
    private static final SSLSocketFactory socketFactory;
    private static final HostnameVerifier hostnameVerifier;

    static {
        trustManager = new GetChainTrustManager();
        hostnameVerifier = (hostname, session) -> true;

        SSLSocketFactory tempFactory;
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { trustManager }, null);
            tempFactory = context.getSocketFactory();
        } catch (GeneralSecurityException gse) {
            tempFactory = null;
        }

        socketFactory = tempFactory;
    }

    SSLServerCertStore(URI uri) throws InvalidAlgorithmParameterException {
        super(null);
        this.uri = uri;
    }

    public Collection<X509Certificate> engineGetCertificates
            (CertSelector selector) throws CertStoreException {

        try {
            URLConnection urlConn = uri.toURL().openConnection();
            if (urlConn instanceof HttpsURLConnection https) {
                if (socketFactory == null) {
                    throw new CertStoreException(
                        "No initialized SSLSocketFactory");
                }

                https.setSSLSocketFactory(socketFactory);
                https.setHostnameVerifier(hostnameVerifier);
                synchronized (trustManager) {
                    try {
                        https.connect();
                        return getMatchingCerts(
                            trustManager.serverChain, selector);
                    } catch (IOException ioe) {
                        // If the server certificate has already been
                        // retrieved, don't mind the connection state.
                        if (trustManager.exchangedServerCerts) {
                            return getMatchingCerts(
                                trustManager.serverChain, selector);
                        }

                        // otherwise, rethrow the exception
                        throw ioe;
                    } finally {
                        trustManager.cleanup();
                    }
                }
            }
        } catch (IOException ioe) {
            throw new CertStoreException(ioe);
        }

        return Collections.emptySet();
    }

    private static List<X509Certificate> getMatchingCerts
        (List<X509Certificate> certs, CertSelector selector)
    {
        // if selector not specified, all certs match
        if (selector == null) {
            return certs;
        }
        List<X509Certificate> matchedCerts = new ArrayList<>(certs.size());
        for (X509Certificate cert : certs) {
            if (selector.match(cert)) {
                matchedCerts.add(cert);
            }
        }
        return matchedCerts;
    }

    public Collection<X509CRL> engineGetCRLs(CRLSelector selector)
        throws CertStoreException
    {
        throw new UnsupportedOperationException();
    }

    public static CertStore getInstance(URI uri)
        throws InvalidAlgorithmParameterException
    {
        return new CS(new SSLServerCertStore(uri), null, "SSLServer", null);
    }

    /*
     * An X509ExtendedTrustManager that ignores the server certificate
     * validation.
     */
    private static class GetChainTrustManager
            extends X509ExtendedTrustManager {

        private List<X509Certificate> serverChain =
                        Collections.emptyList();
        private boolean exchangedServerCerts = false;

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain,
                String authType) throws CertificateException {

            throw new UnsupportedOperationException();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType,
                Socket socket) throws CertificateException {

            throw new UnsupportedOperationException();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType,
                SSLEngine engine) throws CertificateException {

            throw new UnsupportedOperationException();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain,
                String authType) throws CertificateException {

            exchangedServerCerts = true;
            this.serverChain = (chain == null)
                           ? Collections.emptyList()
                           : Arrays.asList(chain);

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType,
                Socket socket) throws CertificateException {

            checkServerTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType,
                SSLEngine engine) throws CertificateException {

            checkServerTrusted(chain, authType);
        }

        void cleanup() {
            exchangedServerCerts = false;
            serverChain = Collections.emptyList();
        }
    }

    /**
     * This class allows the SSLServerCertStore to be accessed as a CertStore.
     */
    private static class CS extends CertStore {
        protected CS(CertStoreSpi spi, Provider p, String type,
                     CertStoreParameters params)
        {
            super(spi, p, type, params);
        }
    }
}
