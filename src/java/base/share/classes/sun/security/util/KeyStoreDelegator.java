/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

/**
 * This class delegates to a primary or secondary keystore implementation.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public class KeyStoreDelegator extends KeyStoreSpi {

    private static final String KEYSTORE_TYPE_COMPAT = "keystore.type.compat";
    private static final Debug debug = Debug.getInstance("keystore");

    private final String primaryType;   // the primary keystore's type
    private final String secondaryType; // the secondary keystore's type
    private final Class<? extends KeyStoreSpi> primaryKeyStore;
                                        // the primary keystore's class
    private final Class<? extends KeyStoreSpi> secondaryKeyStore;
                                        // the secondary keystore's class
    private String type; // the delegate's type
    private KeyStoreSpi keystore; // the delegate
    private final boolean compatModeEnabled;

    public KeyStoreDelegator(
        String primaryType,
        Class<? extends KeyStoreSpi> primaryKeyStore,
        String secondaryType,
        Class<? extends KeyStoreSpi> secondaryKeyStore) {

        // Check whether compatibility mode has been disabled
        @SuppressWarnings("removal")
        var prop = AccessController.doPrivileged((PrivilegedAction<String>) () ->
                        Security.getProperty(KEYSTORE_TYPE_COMPAT));
        compatModeEnabled = "true".equalsIgnoreCase(prop);

        if (compatModeEnabled) {
            this.primaryType = primaryType;
            this.secondaryType = secondaryType;
            this.primaryKeyStore = primaryKeyStore;
            this.secondaryKeyStore = secondaryKeyStore;
        } else {
            this.primaryType = primaryType;
            this.secondaryType = null;
            this.primaryKeyStore = primaryKeyStore;
            this.secondaryKeyStore = null;

            if (debug != null) {
                debug.println("WARNING: compatibility mode disabled for " +
                    primaryType + " and " + secondaryType + " keystore types");
            }
        }
    }

    @Override
    public Key engineGetKey(String alias, char[] password)
        throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return keystore.engineGetKey(alias, password);
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        return keystore.engineGetCertificateChain(alias);
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        return keystore.engineGetCertificate(alias);
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        return keystore.engineGetCreationDate(alias);
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password,
        Certificate[] chain) throws KeyStoreException {
        keystore.engineSetKeyEntry(alias, key, password, chain);
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain)
        throws KeyStoreException {
        keystore.engineSetKeyEntry(alias, key, chain);
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert)
        throws KeyStoreException {
        keystore.engineSetCertificateEntry(alias, cert);
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        keystore.engineDeleteEntry(alias);
    }

    @Override
    public Set<KeyStore.Entry.Attribute> engineGetAttributes(String alias) {
        return keystore.engineGetAttributes(alias);
    }

    @Override
    public Enumeration<String> engineAliases() {
        return keystore.engineAliases();
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        return keystore.engineContainsAlias(alias);
    }

    @Override
    public int engineSize() {
        return keystore.engineSize();
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        return keystore.engineIsKeyEntry(alias);
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        return keystore.engineIsCertificateEntry(alias);
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        return keystore.engineGetCertificateAlias(cert);
    }

    @Override
    public KeyStore.Entry engineGetEntry(String alias,
        KeyStore.ProtectionParameter protParam)
            throws KeyStoreException, NoSuchAlgorithmException,
                UnrecoverableEntryException {
        return keystore.engineGetEntry(alias, protParam);
    }

    @Override
    public void engineSetEntry(String alias, KeyStore.Entry entry,
        KeyStore.ProtectionParameter protParam)
            throws KeyStoreException {
        keystore.engineSetEntry(alias, entry, protParam);
    }

    @Override
    public boolean engineEntryInstanceOf(String alias,
        Class<? extends KeyStore.Entry> entryClass) {
        return keystore.engineEntryInstanceOf(alias, entryClass);
    }

    @Override
    public void engineStore(OutputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException {

        if (debug != null) {
            debug.println("Storing keystore in " + type + " format");
        }
        keystore.engineStore(stream, password);
    }

    @Override
    public void engineLoad(InputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException {

        // A new keystore is always created in the primary keystore format
        if (stream == null) {
            try {
                @SuppressWarnings("deprecation")
                KeyStoreSpi tmp = primaryKeyStore.newInstance();
                keystore = tmp;
            } catch (InstantiationException | IllegalAccessException e) {
                // can safely ignore
            }
            type = primaryType;

            if (debug != null) {
                debug.println("Creating a new keystore in " + type + " format");
            }
            keystore.engineLoad(stream, password);

        } else {
            // First try the primary keystore then try the secondary keystore
            InputStream bufferedStream = new BufferedInputStream(stream);
            bufferedStream.mark(Integer.MAX_VALUE);

            try {
                @SuppressWarnings("deprecation")
                KeyStoreSpi tmp = primaryKeyStore.newInstance();
                tmp.engineLoad(bufferedStream, password);
                keystore = tmp;
                type = primaryType;

            } catch (Exception e) {

                // incorrect password
                if (e instanceof IOException &&
                    e.getCause() instanceof UnrecoverableKeyException) {
                    throw (IOException)e;
                }

                try {
                    // Ignore secondary keystore when no compatibility mode
                    if (!compatModeEnabled) {
                        throw e;
                    }

                    @SuppressWarnings("deprecation")
                    KeyStoreSpi tmp = secondaryKeyStore.newInstance();
                    bufferedStream.reset();
                    tmp.engineLoad(bufferedStream, password);
                    keystore = tmp;
                    type = secondaryType;

                    if (debug != null) {
                        debug.println("WARNING: switching from " +
                          primaryType + " to " + secondaryType +
                          " keystore file format has altered the " +
                          "keystore security level");
                    }

                } catch (InstantiationException |
                    IllegalAccessException e2) {
                    // can safely ignore

                } catch (IOException |
                    NoSuchAlgorithmException |
                    CertificateException e3) {

                    // incorrect password
                    if (e3 instanceof IOException &&
                        e3.getCause() instanceof UnrecoverableKeyException) {
                        throw (IOException)e3;
                    }
                    // rethrow the outer exception
                    if (e instanceof IOException) {
                        throw (IOException)e;
                    } else if (e instanceof CertificateException) {
                        throw (CertificateException)e;
                    } else if (e instanceof NoSuchAlgorithmException) {
                        throw (NoSuchAlgorithmException)e;
                    } else if (e instanceof RuntimeException){
                        throw (RuntimeException)e;
                    }
                }
            }

            if (debug != null) {
                debug.println("Loaded a keystore in " + type + " format");
            }
        }
    }

    /**
     * Probe the first few bytes of the keystore data stream for a valid
     * keystore encoding. Only the primary keystore implementation is probed.
     */
    @Override
    public boolean engineProbe(InputStream stream) throws IOException {

        boolean result = false;

        try {
            @SuppressWarnings("deprecation")
            KeyStoreSpi tmp = primaryKeyStore.newInstance();
            keystore = tmp;
            type = primaryType;
            result = keystore.engineProbe(stream);

        } catch (Exception e) {
            throw new IOException(e);

        } finally {
            // reset
            if (!result) {
                type = null;
                keystore = null;
            }
        }

        return result;
    }
}
