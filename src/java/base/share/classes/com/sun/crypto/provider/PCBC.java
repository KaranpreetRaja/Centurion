/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.security.ProviderException;
import java.base.share.classes.sun.security.util.ArrayUtil;

/**
 * This class represents ciphers in Plaintext Cipher Block Chaining (PCBC)
 * mode.
 *
 * <p>This mode is implemented independently of a particular cipher.
 * Ciphers to which this mode should apply (e.g., DES) must be
 * <i>plugged-in</i> using the constructor.
 *
 * <p>NOTE: This class does not deal with buffering or padding.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

final class PCBC extends FeedbackCipher {

    /*
     * output buffer
     */
    private final byte[] k;

    // variables for save/restore calls
    private byte[] kSave = null;

    PCBC(SymmetricCipher embeddedCipher) {
        super(embeddedCipher);
        k = new byte[blockSize];
    }

    /**
     * Gets the name of this feedback mode.
     *
     * @return the string <code>PCBC</code>
     */
    String getFeedback() {
        return "PCBC";
    }

    /**
     * Initializes the cipher in the specified mode with the given key
     * and iv.
     *
     * @param decrypting flag indicating encryption or decryption
     * @param algorithm the algorithm name
     * @param key the key
     * @param iv the iv
     *
     * @exception InvalidKeyException if the given key is inappropriate for
     * initializing this cipher
     */
    void init(boolean decrypting, String algorithm, byte[] key, byte[] iv)
            throws InvalidKeyException {
        if ((key == null) || (iv == null) || (iv.length != blockSize)) {
            throw new InvalidKeyException("Internal error");
        }
        this.iv = iv;
        reset();
        embeddedCipher.init(decrypting, algorithm, key);
    }

    /**
     * Resets the iv to its original value.
     * This is used when doFinal is called in the Cipher class, so that the
     * cipher can be reused (with its original iv).
     */
    void reset() {
        System.arraycopy(iv, 0, k, 0, blockSize);
    }

    /**
     * Save the current content of this cipher.
     */
    void save() {
        if (kSave == null) {
            kSave = new byte[blockSize];
        }
        System.arraycopy(k, 0, kSave, 0, blockSize);

    }

    /**
     * Restores the content of this cipher to the previous saved one.
     */
    void restore() {
        System.arraycopy(kSave, 0, k, 0, blockSize);
    }

    /**
     * Performs encryption operation.
     *
     * <p>The input plain text <code>plain</code>, starting at
     * <code>plainOffset</code> and ending at
     * <code>(plainOffset + plainLen - 1)</code>, is encrypted.
     * The result is stored in <code>cipher</code>, starting at
     * <code>cipherOffset</code>.
     *
     * @param plain the buffer with the input data to be encrypted
     * @param plainOffset the offset in <code>plain</code>
     * @param plainLen the length of the input data
     * @param cipher the buffer for the result
     * @param cipherOffset the offset in <code>cipher</code>
     * @exception ProviderException if <code>plainLen</code> is not
     * a multiple of the block size
     * @return the length of the encrypted data
     */
    int encrypt(byte[] plain, int plainOffset, int plainLen,
                byte[] cipher, int cipherOffset)
    {
        ArrayUtil.blockSizeCheck(plainLen, blockSize);
        ArrayUtil.nullAndBoundsCheck(plain, plainOffset, plainLen);
        ArrayUtil.nullAndBoundsCheck(cipher, cipherOffset, plainLen);

        int i;
        int endIndex = plainOffset + plainLen;

        for (; plainOffset < endIndex;
             plainOffset += blockSize, cipherOffset += blockSize) {
            for (i = 0; i < blockSize; i++) {
                k[i] ^= plain[i + plainOffset];
            }
            embeddedCipher.encryptBlock(k, 0, cipher, cipherOffset);
            for (i = 0; i < blockSize; i++) {
                k[i] = (byte)(plain[i + plainOffset] ^ cipher[i + cipherOffset]);
            }
        }
        return plainLen;
    }

    /**
     * Performs decryption operation.
     *
     * <p>The input cipher text <code>cipher</code>, starting at
     * <code>cipherOffset</code> and ending at
     * <code>(cipherOffset + cipherLen - 1)</code>, is decrypted.
     * The result is stored in <code>plain</code>, starting at
     * <code>plainOffset</code>.
     *
     * @param cipher the buffer with the input data to be decrypted
     * @param cipherOffset the offset in <code>cipherOffset</code>
     * @param cipherLen the length of the input data
     * @param plain the buffer for the result
     * @param plainOffset the offset in <code>plain</code>
     * @exception ProviderException if <code>cipherLen</code> is not
     * a multiple of the block size
     * @return the length of the decrypted data
     */
    int decrypt(byte[] cipher, int cipherOffset, int cipherLen,
                byte[] plain, int plainOffset)
    {
        ArrayUtil.blockSizeCheck(cipherLen, blockSize);
        ArrayUtil.nullAndBoundsCheck(cipher, cipherOffset, cipherLen);
        ArrayUtil.nullAndBoundsCheck(plain, plainOffset, cipherLen);

        int i;
        int endIndex = cipherOffset + cipherLen;

        for (; cipherOffset < endIndex;
             plainOffset += blockSize, cipherOffset += blockSize) {
            embeddedCipher.decryptBlock(cipher, cipherOffset,
                                   plain, plainOffset);
            for (i = 0; i < blockSize; i++) {
                plain[i + plainOffset] ^= k[i];
            }
            for (i = 0; i < blockSize; i++) {
                k[i] = (byte)(plain[i + plainOffset] ^ cipher[i + cipherOffset]);
            }
        }
        return cipherLen;
    }
}
