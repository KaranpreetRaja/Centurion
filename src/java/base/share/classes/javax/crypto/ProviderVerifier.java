/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.jar.*;

/**
 * This class verifies Provider/Policy resources found at a URL
 * (currently only JAR files and any supporting JAR files), and
 * determines whether they may be used in this implementation.
 *
 * The JCE in OpenJDK has an open cryptographic interface, meaning it
 * does not restrict which providers can be used.  Compliance with
 * United States export controls and with local law governing the
 * import/export of products incorporating the JCE in the OpenJDK is
 * the responsibility of the licensee.
 *
 * @since 1.7
 */
final class ProviderVerifier {

    // The URL for the JAR file we want to verify.
    private final URL jarURL;
    private final boolean savePerms;
    private CryptoPermissions appPerms = null;

    /**
     * Creates a {@code ProviderVerifier} object to verify the given URL.
     *
     * @param jarURL the JAR file to be verified.
     * @param savePerms if {@code true}, save the permissions allowed by the
     *          exemption mechanism
     */
    ProviderVerifier(URL jarURL, boolean savePerms) {
        this(jarURL, null, savePerms);
    }

    /**
     * Creates a {@code ProviderVerifier} object to verify the given URL.
     *
     * @param jarURL the JAR file to be verified
     * @param provider the corresponding provider.
     * @param savePerms if {@code true}, save the permissions allowed by the
     *          exemption mechanism
     */
    ProviderVerifier(URL jarURL, Provider provider, boolean savePerms) {
        this.jarURL = jarURL;
        this.savePerms = savePerms;
    }

    /**
     * Verify the JAR file is signed by an entity which has a certificate
     * issued by a trusted CA.
     *
     * In OpenJDK, we just need to examine the "cryptoperms" file to see
     * if any permissions were bundled together with this jar file.
     */
    void verify() throws IOException {

        // Short-circuit.  If we weren't asked to save any, we're done.
        if (!savePerms) {
            return;
        }

        // If the protocol of jarURL isn't "jar", we should
        // construct a JAR URL so we can open a JarURLConnection
        // for verifying this provider.
        @SuppressWarnings("deprecation")
        final URL url = jarURL.getProtocol().equalsIgnoreCase("jar")?
                        jarURL : new URL("jar:" + jarURL + "!/");

        JarFile jf = null;
        try {

            // Get a link to the Jarfile to search.
            try {
                @SuppressWarnings("removal")
                var tmp = AccessController.doPrivileged(
                        (PrivilegedExceptionAction<JarFile>) () -> {
                            JarURLConnection conn =
                                (JarURLConnection) url.openConnection();
                            // You could do some caching here as
                            // an optimization.
                            conn.setUseCaches(false);
                            return conn.getJarFile();
                        });
                jf = tmp;
            } catch (java.security.PrivilegedActionException pae) {
                throw new SecurityException("Cannot load " + url,
                    pae.getCause());
            }

            if (jf != null) {
                JarEntry je = jf.getJarEntry("cryptoPerms");
                if (je == null) {
                    throw new JarException(
                        "Can not find cryptoPerms");
                }
                try {
                    appPerms = new CryptoPermissions();
                    appPerms.load(jf.getInputStream(je));
                } catch (Exception ex) {
                    JarException jex =
                        new JarException("Cannot load/parse" + jarURL);
                    jex.initCause(ex);
                    throw jex;
                }
            }
        } finally {
            // Only call close() when caching is not enabled.
            // Otherwise, exceptions will be thrown for all
            // subsequent accesses of this cached jar.
            if (jf != null) {
                jf.close();
            }
        }
    }

    /**
     * Verify that the provided certs include the
     * framework signing certificate.
     *
     * @param certs the list of certs to be checked.
     * @throws Exception if the list of certs did not contain
     *          the framework signing certificate
     */
    static void verifyPolicySigned(java.security.cert.Certificate[] certs)
            throws Exception {
    }

    /**
     * Returns {@code true} if the given provider is JDK trusted crypto provider
     * if the implementation supports fast-path verification.
     */
    static boolean isTrustedCryptoProvider(Provider provider) {
        return false;
    }

    /**
     * Returns the permissions which are bundled with the JAR file,
     * aka the "cryptoperms" file.
     *
     * NOTE: if this {@code ProviderVerifier} instance is constructed
     * with "savePerms" equal to {@code false}, then this method would always
     * return {@code null}.
     */
    CryptoPermissions getPermissions() {
        return appPerms;
    }
}
