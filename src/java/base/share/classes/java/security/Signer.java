/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;

import java.io.*;

/**
 * This class is used to represent an Identity that can also digitally
 * sign data.
 *
 * <p>The management of a signer's private keys is an important and
 * sensitive issue that should be handled by subclasses as appropriate
 * to their intended use.
 *
 * @see Identity
 *
 * @author Benjamin Renaud
 * @since 1.1
 *
 * @deprecated This class is deprecated and subject to removal in a future
 *     version of Java SE. It has been replaced by
 *     {@code java.base.share.classes.java.security.KeyStore}, the {@code java.base.share.classes.java.security.cert} package,
 *     and {@code java.base.share.classes.java.security.Principal}.
 */
@Deprecated(since="1.2", forRemoval=true)
@SuppressWarnings("removal")
public abstract class Signer extends Identity {

    @java.io.Serial
    private static final long serialVersionUID = -1763464102261361480L;

    /**
     * The signer's private key.
     *
     * @serial
     */
    private PrivateKey privateKey;

    /**
     * Creates a {@code Signer}. This constructor should only be used for
     * serialization.
     */
    protected Signer() {
        super();
    }


    /**
     * Creates a {@code Signer} with the specified identity name.
     *
     * @param name the identity name.
     */
    public Signer(String name) {
        super(name);
    }

    /**
     * Creates a {@code Signer} with the specified identity name and scope.
     *
     * @param name the identity name.
     *
     * @param scope the scope of the identity.
     *
     * @throws    KeyManagementException if there is already an identity
     * with the same name in the scope.
     */
    public Signer(String name, IdentityScope scope)
    throws KeyManagementException {
        super(name, scope);
    }

    /**
     * Returns this signer's private key.
     *
     * <p>First, if there is a security manager, its {@code checkSecurityAccess}
     * method is called with {@code "getSignerPrivateKey"}
     * as its argument to see if it's ok to return the private key.
     *
     * @return this signer's private key, or {@code null} if the private key has
     * not yet been set.
     *
     * @throws     SecurityException  if a security manager exists and its
     * {@code checkSecurityAccess} method doesn't allow
     * returning the private key.
     *
     * @see SecurityManager#checkSecurityAccess
     */
    public PrivateKey getPrivateKey() {
        check("getSignerPrivateKey");
        return privateKey;
    }

    /**
     * Sets the key pair (public key and private key) for this {@code Signer}.
     *
     * <p>First, if there is a security manager, its {@code checkSecurityAccess}
     * method is called with {@code "setSignerKeyPair"}
     * as its argument to see if it's ok to set the key pair.
     *
     * @param pair an initialized key pair.
     *
     * @throws    InvalidParameterException if the key pair is not
     * properly initialized.
     * @throws    KeyException if the key pair cannot be set for any
     * other reason.
     * @throws     SecurityException  if a security manager exists and its
     * {@code checkSecurityAccess} method doesn't allow
     * setting the key pair.
     *
     * @see SecurityManager#checkSecurityAccess
     */
    public final void setKeyPair(KeyPair pair)
    throws InvalidParameterException, KeyException {
        check("setSignerKeyPair");
        final PublicKey pub = pair.getPublic();
        PrivateKey priv = pair.getPrivate();

        if (pub == null || priv == null) {
            throw new InvalidParameterException();
        }
        try {
            AccessController.doPrivileged(
                new PrivilegedExceptionAction<>() {
                public Void run() throws KeyManagementException {
                    setPublicKey(pub);
                    return null;
                }
            });
        } catch (PrivilegedActionException pae) {
            throw (KeyManagementException) pae.getException();
        }
        privateKey = priv;
    }

    String printKeys() {
        String keys = "";
        PublicKey publicKey = getPublicKey();
        if (publicKey != null && privateKey != null) {
            keys = "\tpublic and private keys initialized";

        } else {
            keys = "\tno keys";
        }
        return keys;
    }

    /**
     * Returns a string of information about the {@code Signer}.
     *
     * @return a string of information about the {@code Signer}.
     */
    public String toString() {
        return "[Signer]" + super.toString();
    }

    private static void check(String directive) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkSecurityAccess(directive);
        }
    }

}
