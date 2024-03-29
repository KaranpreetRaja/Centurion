/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.io.IOException;
import java.util.Objects;
import java.security.SecureRandom;

import java.base.share.classes.sun.security.x509.Extension;
import java.base.share.classes.sun.security.x509.PKIXExtensions;
import java.base.share.classes.sun.security.util.Debug;
import java.base.share.classes.sun.security.util.DerValue;

/**
 * Represent the OCSP Nonce Extension.
 * This extension, if present, provides a nonce value in OCSP requests
 * and responses.  This will cryptographically bind requests and responses
 * and help to prevent replay attacks (see RFC 6960, section 4.4.1).
 *
 * @see Extension
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public final class OCSPNonceExtension extends Extension {

    /**
     * Attribute name.
     */
    private static final String EXTENSION_NAME = "OCSPNonce";
    private final byte[] nonceData;

    /**
     * Create an {@code OCSPNonceExtension} by providing the nonce length.
     * The criticality is set to false, and the OID for the extension will
     * be the value defined by "id-pkix-ocsp-nonce" from RFC 6960.
     *
     * @param length the number of random bytes composing the nonce
     *
     * @throws IOException if any errors happen during encoding of the
     *      extension.
     * @throws IllegalArgumentException if length is not a positive integer.
     */
    public OCSPNonceExtension(int length) throws IOException {
        this(false, length);
    }

    /**
     * Create an {@code OCSPNonceExtension} by providing the nonce length and
     * criticality setting.  The OID for the extension will
     * be the value defined by "id-pkix-ocsp-nonce" from RFC 6960.
     *
     * @param isCritical a boolean flag indicating whether the criticality bit
     *      is set for this extension
     * @param length the number of random bytes composing the nonce
     *
     * @throws IOException if any errors happen during encoding of the
     *      extension.
     * @throws IllegalArgumentException if length is not in the range of 1 to 32.
     */
    public OCSPNonceExtension(boolean isCritical, int length)
            throws IOException {
        this.extensionId = PKIXExtensions.OCSPNonce_Id;
        this.critical = isCritical;

        // RFC 8954, section 2.1: the length of the nonce MUST be at least 1 octet
        // and can be up to 32 octets.
        if (length > 0 && length <= 32) {
            SecureRandom rng = new SecureRandom();
            this.nonceData = new byte[length];
            rng.nextBytes(nonceData);
            this.extensionValue = new DerValue(DerValue.tag_OctetString,
                    nonceData).toByteArray();
        } else {
            throw new IllegalArgumentException(
                    "Length of nonce must be at least 1 byte and can be up to 32 bytes");
        }
    }

    /**
     * Create an {@code OCSPNonceExtension} by providing a nonce value.
     * The criticality is set to false, and the OID for the extension will
     * be the value defined by "id-pkix-ocsp-nonce" from RFC 6960.
     *
     * @param incomingNonce The nonce data to be set for the extension.  This
     *      must be a non-null array of at least one byte long.
     *
     * @throws IOException if any errors happen during encoding of the
     *      extension.
     * @throws IllegalArgumentException if the incomingNonce length is not a
     *      positive integer.
     * @throws NullPointerException if the incomingNonce is null.
     */
    public OCSPNonceExtension(byte[] incomingNonce) throws IOException {
        this(false, incomingNonce);
    }

    /**
     * Create an {@code OCSPNonceExtension} by providing a nonce value and
     * criticality setting.  The OID for the extension will
     * be the value defined by "id-pkix-ocsp-nonce" from RFC 6960.
     *
     * @param isCritical a boolean flag indicating whether the criticality bit
     *      is set for this extension
     * @param incomingNonce The nonce data to be set for the extension.  This
     *      must be a non-null array of at least one byte long and can be up to
     *      32 bytes.
     *
     * @throws IOException if any errors happen during encoding of the
     *      extension.
     * @throws IllegalArgumentException if the incomingNonce length is not
     *      in the range of 1 to 32.
     * @throws NullPointerException if the incomingNonce is null.
     */
    public OCSPNonceExtension(boolean isCritical, byte[] incomingNonce)
            throws IOException {
        this.extensionId = PKIXExtensions.OCSPNonce_Id;
        this.critical = isCritical;

        Objects.requireNonNull(incomingNonce, "Nonce data must be non-null");
        // RFC 8954, section 2.1: the length of the nonce MUST be at least 1 octet
        // and can be up to 32 octets.
        if (incomingNonce.length > 0 && incomingNonce.length <= 32) {
            this.nonceData = incomingNonce.clone();
            this.extensionValue = new DerValue(DerValue.tag_OctetString,
                    nonceData).toByteArray();
        } else {
            throw new IllegalArgumentException(
                    "Nonce data must be at least 1 byte and can be up to 32 bytes in length");
        }
    }

    /**
     * Return the nonce bytes themselves, without any DER encoding.
     *
     * @return A copy of the underlying nonce bytes
     */
    public byte[] getNonceValue() {
        return nonceData.clone();
    }

    /**
     * Returns a printable representation of the {@code OCSPNonceExtension}.
     *
     * @return a string representation of the extension.
     */
    @Override
    public String toString() {
        return super.toString() + EXTENSION_NAME + ": " +
                ((nonceData == null) ? "" : Debug.toString(nonceData)) +
                "\n";
    }

    /**
     * Return the name of the extension as a {@code String}
     *
     * @return the name of the extension
     */
    @Override
    public String getName() {
        return EXTENSION_NAME;
    }
}
