/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;

/**
 * This class is a simple holder for a key pair (a public key and a
 * private key). It does not enforce any security, and, when initialized,
 * should be treated like a PrivateKey.
 *
 * @see PublicKey
 * @see PrivateKey
 *
 * @author Benjamin Renaud
 * @since 1.1
 */

public final class KeyPair implements java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = -7565189502268009837L;

    /** The private key. */
    private final PrivateKey privateKey;

    /** The public key. */
    private final PublicKey publicKey;

    /**
     * Constructs a key pair from the given public key and private key.
     *
     * <p>Note that this constructor only stores references to the public
     * and private key components in the generated key pair. This is safe,
     * because {@code Key} objects are immutable.
     *
     * @param publicKey the public key.
     *
     * @param privateKey the private key.
     */
    public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Returns a reference to the public key component of this key pair.
     *
     * @return a reference to the public key.
     */
    public PublicKey getPublic() {
        return publicKey;
    }

    /**
     * Returns a reference to the private key component of this key pair.
     *
     * @return a reference to the private key.
     */
    public PrivateKey getPrivate() {
        return privateKey;
    }
}
