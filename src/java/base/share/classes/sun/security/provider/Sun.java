/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider;

import java.util.*;
import java.security.*;

import static java.base.share.classes.sun.security.util.SecurityConstants.PROVIDER_VER;

/**
 * The SUN Security Provider.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

public final class Sun extends Provider {

    @java.io.Serial
    private static final long serialVersionUID = 6440182097568097204L;

    private static final String INFO = "SUN " +
    "(DSA key/parameter generation; DSA signing; SHA-1, MD5 digests; " +
    "SecureRandom; X.509 certificates; PKCS12, JKS & DKS keystores; " +
    "PKIX CertPathValidator; " +
    "PKIX CertPathBuilder; LDAP, Collection CertStores, JavaPolicy Policy; " +
    "JavaLoginConfig Configuration)";

    @SuppressWarnings("removal")
    public Sun() {
        /* We are the SUN provider */
        super("SUN", PROVIDER_VER, INFO);

        Provider p = this;
        Iterator<Provider.Service> serviceIter = new SunEntries(p).iterator();

        // if there is no security manager installed, put directly into
        // the provider
        if (System.getSecurityManager() == null) {
            putEntries(serviceIter);
        } else {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    putEntries(serviceIter);
                    return null;
                }
            });
        }
    }

    void putEntries(Iterator<Provider.Service> i) {
        while (i.hasNext()) {
            putService(i.next());
        }
    }
}
