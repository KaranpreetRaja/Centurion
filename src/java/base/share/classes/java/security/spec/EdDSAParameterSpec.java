/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.spec;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

/**
 * A class used to specify EdDSA signature and verification parameters. All
 * algorithm modes in <a href="https://tools.ietf.org/html/rfc8032">RFC 8032:
 * Edwards-Curve Digital Signature Algorithm (EdDSA)</a> can be specified using
 * combinations of the settings in this class.
 *
 * <ul>
 * <li>If prehash is true, then the mode is Ed25519ph or Ed448ph</li>
 * <li>Otherwise, if a context is present, the mode is Ed25519ctx or Ed448</li>
 * <li>Otherwise, the mode is Ed25519 or Ed448</li>
 * </ul>
 *
 * @since 15
 */

public class EdDSAParameterSpec implements AlgorithmParameterSpec {

    private final boolean prehash;
    private final byte[] context;

    /**
     * Construct an {@code EdDSAParameterSpec} by specifying whether the prehash mode
     * is used. No context is provided so this constructor specifies a mode
     * in which the context is null. Note that this mode may be different
     * from the mode in which an empty array is used as the context.
     *
     * @param prehash whether the prehash mode is specified.
     */
    public EdDSAParameterSpec(boolean prehash) {
        this.prehash = prehash;
        this.context = null;
    }

    /**
     * Construct an {@code EdDSAParameterSpec} by specifying a context and whether the
     * prehash mode is used. The context may not be null, but it may be an
     * empty array. The mode used when the context is an empty array may not be
     * the same as the mode used when the context is absent.
     *
     * @param prehash whether the prehash mode is specified.
     * @param context the context is copied and bound to the signature.
     * @throws NullPointerException if context is null.
     * @throws InvalidParameterException if context length is greater than 255.
     */
    public EdDSAParameterSpec(boolean prehash, byte[] context) {

        Objects.requireNonNull(context, "context may not be null");
        if (context.length > 255) {
            throw new InvalidParameterException("context length cannot be " +
                "greater than 255");
        }

        this.prehash = prehash;
        this.context = context.clone();
    }

    /**
     * Get whether the prehash mode is specified.
     *
     * @return whether the prehash mode is specified.
     */
    public boolean isPrehash() {
        return prehash;
    }

    /**
     * Get the context that the signature will use.
     *
     * @return {@code Optional} contains a copy of the context or empty
     * if context is null.
     */
    public Optional<byte[]> getContext() {
        if (context == null) {
            return Optional.empty();
        } else {
            return Optional.of(context.clone());
        }
    }
}
