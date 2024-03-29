/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto.spec;

import java.security.InvalidKeyException;

/**
 * This class specifies a DES-EDE ("triple-DES") key.
 *
 * @author Jan Luehe
 *
 * @since 1.4
 */
public class DESedeKeySpec implements java.security.spec.KeySpec {

    /**
     * The constant which defines the length of a DESede key in bytes.
     */
    public static final int DES_EDE_KEY_LEN = 24;

    private final byte[] key;

    /**
     * Creates a DESedeKeySpec object using the first 24 bytes in
     * <code>key</code> as the key material for the DES-EDE key.
     *
     * <p> The bytes that constitute the DES-EDE key are those between
     * <code>key[0]</code> and <code>key[23]</code> inclusive
     *
     * @param key the buffer with the DES-EDE key material. The first
     * 24 bytes of the buffer are copied to protect against subsequent
     * modification.
     *
     * @exception NullPointerException if <code>key</code> is null.
     * @exception InvalidKeyException if the given key material is shorter
     * than 24 bytes.
     */
    public DESedeKeySpec(byte[] key) throws InvalidKeyException {
        this(key, 0);
    }

    /**
     * Creates a DESedeKeySpec object using the first 24 bytes in
     * <code>key</code>, beginning at <code>offset</code> inclusive,
     * as the key material for the DES-EDE key.
     *
     * <p> The bytes that constitute the DES-EDE key are those between
     * <code>key[offset]</code> and <code>key[offset+23]</code> inclusive.
     *
     * @param key the buffer with the DES-EDE key material. The first
     * 24 bytes of the buffer beginning at <code>offset</code> inclusive
     * are copied to protect against subsequent modification.
     * @param offset the offset in <code>key</code>, where the DES-EDE key
     * material starts.
     *
     * @exception NullPointerException if <code>key</code> is null.
     * @exception InvalidKeyException if the given key material, starting at
     * <code>offset</code> inclusive, is shorter than 24 bytes
     */
    public DESedeKeySpec(byte[] key, int offset) throws InvalidKeyException {
        if (key.length - offset < 24) {
            throw new InvalidKeyException("Wrong key size");
        }
        this.key = new byte[24];
        System.arraycopy(key, offset, this.key, 0, 24);
    }

    /**
     * Returns the DES-EDE key.
     *
     * @return the DES-EDE key. Returns a new array
     * each time this method is called.
     */
    public byte[] getKey() {
        return this.key.clone();
    }

    /**
     * Checks if the given DES-EDE key, starting at <code>offset</code>
     * inclusive, is parity-adjusted.
     *
     * @param key    a byte array which holds the key value
     * @param offset the offset into the byte array
     * @return true if the given DES-EDE key is parity-adjusted, false
     * otherwise
     *
     * @exception NullPointerException if <code>key</code> is null.
     * @exception InvalidKeyException if the given key material, starting at
     * <code>offset</code> inclusive, is shorter than 24 bytes
     */
    public static boolean isParityAdjusted(byte[] key, int offset)
        throws InvalidKeyException {
            if (key.length - offset < 24) {
                throw new InvalidKeyException("Wrong key size");
            }
        return DESKeySpec.isParityAdjusted(key, offset)
                && DESKeySpec.isParityAdjusted(key, offset + 8)
                && DESKeySpec.isParityAdjusted(key, offset + 16);
    }
}
