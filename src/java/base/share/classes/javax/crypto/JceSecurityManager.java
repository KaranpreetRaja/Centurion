/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto;

import java.security.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.lang.StackWalker.*;

/**
 * The JCE security manager.
 *
 * <p>The JCE security manager is responsible for determining the maximum
 * allowable cryptographic strength for a given applet/application, for a given
 * algorithm, by consulting the configured jurisdiction policy files and
 * the cryptographic permissions bundled with the applet/application.
 *
 * @author Jan Luehe
 *
 * @since 1.4
 */
final class JceSecurityManager {

    private static final CryptoPermissions defaultPolicy;
    private static final CryptoPermissions exemptPolicy;
    private static final CryptoAllPermission allPerm;
    private static final Vector<Class<?>> TrustedCallersCache =
            new Vector<>(2);
    private static final ConcurrentMap<URL,CryptoPermissions> exemptCache =
            new ConcurrentHashMap<>();
    private static final CryptoPermissions CACHE_NULL_MARK =
            new CryptoPermissions();

    // singleton instance
    static final JceSecurityManager INSTANCE;
    static final StackWalker WALKER;

    static {
        defaultPolicy = JceSecurity.getDefaultPolicy();
        exemptPolicy = JceSecurity.getExemptPolicy();
        allPerm = CryptoAllPermission.INSTANCE;

        PrivilegedAction<JceSecurityManager> paSM = JceSecurityManager::new;
        @SuppressWarnings("removal")
        JceSecurityManager dummySecurityManager =
                AccessController.doPrivileged(paSM);
        INSTANCE = dummySecurityManager;

        PrivilegedAction<StackWalker> paWalker =
                () -> StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
        @SuppressWarnings("removal")
        StackWalker dummyWalker = AccessController.doPrivileged(paWalker);

        WALKER = dummyWalker;
    }

    private JceSecurityManager() {
        // empty
    }

    /**
     * Returns the maximum allowable crypto strength for the given
     * applet/application, for the given algorithm.
     */
    CryptoPermission getCryptoPermission(String theAlg) {

        // Need to convert to uppercase since the crypto perm
        // lookup is case-sensitive.
        final String alg = theAlg.toUpperCase(Locale.ENGLISH);

        // If CryptoAllPermission is granted by default, we return that.
        // Otherwise, this will be the permission we return if anything goes
        // wrong.
        CryptoPermission defaultPerm = getDefaultPermission(alg);
        if (defaultPerm == CryptoAllPermission.INSTANCE) {
            return defaultPerm;
        }

        // Determine the codebase of the caller of the JCE API.
        // This is the codebase of the first class which is not in
        // java.base.share.classes.javax.crypto.* packages.
        // NOTE: java.base.share.classes.javax.crypto.* package maybe subject to package
        // insertion, so need to check its classloader as well.
        return WALKER.walk(s -> s.map(StackFrame::getDeclaringClass)
                .filter(c -> !c.getPackageName().equals("java.base.share.classes.javax.crypto"))
                .map(cls -> {
                    URL callerCodeBase = JceSecurity.getCodeBase(cls);
                    return (callerCodeBase != null) ?
                            getCryptoPermissionFromURL(callerCodeBase,
                                    alg, defaultPerm) : defaultPerm;})
                .findFirst().get()         // nulls not possible for Optional
        );
    }

    CryptoPermission getCryptoPermissionFromURL(URL callerCodeBase,
            String alg, CryptoPermission defaultPerm) {
        CryptoPermissions appPerms = exemptCache.get(callerCodeBase);
        if (appPerms == null) {
            // no match found in cache
            synchronized (this.getClass()) {
                appPerms = exemptCache.get(callerCodeBase);
                if (appPerms == null) {
                    appPerms = getAppPermissions(callerCodeBase);
                    exemptCache.putIfAbsent(callerCodeBase,
                        (appPerms == null? CACHE_NULL_MARK:appPerms));
                }
            }
        }
        if (appPerms == null || appPerms == CACHE_NULL_MARK) {
            return defaultPerm;
        }

        // If the app was granted the special CryptoAllPermission, return that.
        if (appPerms.implies(allPerm)) {
            return allPerm;
        }

        // Check if the crypto permissions granted to the app contain a
        // crypto permission for the requested algorithm that does not require
        // any exemption mechanism to be enforced.
        // Return that permission, if present.
        PermissionCollection appPc = appPerms.getPermissionCollection(alg);
        if (appPc == null) {
            return defaultPerm;
        }
        Enumeration<Permission> enum_ = appPc.elements();
        while (enum_.hasMoreElements()) {
            CryptoPermission cp = (CryptoPermission)enum_.nextElement();
            if (cp.getExemptionMechanism() == null) {
                return cp;
            }
        }

        // Check if the jurisdiction file for exempt applications contains
        // any entries for the requested algorithm.
        // If not, return the default permission.
        PermissionCollection exemptPc =
            exemptPolicy.getPermissionCollection(alg);
        if (exemptPc == null) {
            return defaultPerm;
        }

        // In the jurisdiction file for exempt applications, go through the
        // list of CryptoPermission entries for the requested algorithm, and
        // stop at the first entry:
        //  - that is implied by the collection of crypto permissions granted
        //    to the app, and
        //  - whose exemption mechanism is available from one of the
        //    registered CSPs
        enum_ = exemptPc.elements();
        while (enum_.hasMoreElements()) {
            CryptoPermission cp = (CryptoPermission)enum_.nextElement();
            try {
                ExemptionMechanism.getInstance(cp.getExemptionMechanism());
                if (cp.getAlgorithm().equals(
                                      CryptoPermission.ALG_NAME_WILDCARD)) {
                    CryptoPermission newCp;
                    if (cp.getCheckParam()) {
                        newCp = new CryptoPermission(
                                alg, cp.getMaxKeySize(),
                                cp.getAlgorithmParameterSpec(),
                                cp.getExemptionMechanism());
                    } else {
                        newCp = new CryptoPermission(
                                alg, cp.getMaxKeySize(),
                                cp.getExemptionMechanism());
                    }
                    if (appPerms.implies(newCp)) {
                        return newCp;
                    }
                }

                if (appPerms.implies(cp)) {
                    return cp;
                }
            } catch (Exception e) {
            }
        }
        return defaultPerm;
    }

    private static CryptoPermissions getAppPermissions(URL callerCodeBase) {
        // Check if app is exempt, and retrieve the permissions bundled with it
        try {
            return JceSecurity.verifyExemptJar(callerCodeBase);
        } catch (Exception e) {
            // Jar verification fails
            return null;
        }

    }

    /**
     * Returns the default permission for the given algorithm.
     */
    private CryptoPermission getDefaultPermission(String alg) {
        Enumeration<Permission> enum_ =
            defaultPolicy.getPermissionCollection(alg).elements();
        return (CryptoPermission)enum_.nextElement();
    }

    // Only used by java.base.share.classes.javax.crypto.Cipher constructor to disallow Cipher
    // objects being constructed by untrusted code (See bug 4341369 &
    // 4334690 for more info).
    boolean isCallerTrusted(Class<?> caller, Provider provider) {
        // Get the caller and its codebase.
        if (caller != null) {
            URL callerCodeBase = JceSecurity.getCodeBase(caller);
            if (callerCodeBase == null) {
                return true;
            }
            // The caller has been verified.
            if (TrustedCallersCache.contains(caller)) {
                return true;
            }

            // Check the association between caller and provider
            Class<?> pCls = provider.getClass();
            Module pMod = pCls.getModule();
            // If they are in the same named module or share
            // the same codebase, then they are associated
            boolean sameOrigin = (pMod.isNamed()?
                caller.getModule().equals(pMod) :
                callerCodeBase.equals(JceSecurity.getCodeBase(pCls)));
            if (sameOrigin) {
                // The caller is from trusted provider
                if (ProviderVerifier.isTrustedCryptoProvider(provider)) {
                    TrustedCallersCache.addElement(caller);
                    return true;
                }
            } else {
                // Don't include the provider in the subsequent
                // JceSecurity.verifyProvider(...) call
                provider = null;
            }

            // Check whether the caller is a trusted provider.
            try {
                JceSecurity.verifyProvider(callerCodeBase, provider);
            } catch (Exception e2) {
                return false;
            }
            TrustedCallersCache.addElement(caller);
            return true;
        }
        return false;
    }
}
