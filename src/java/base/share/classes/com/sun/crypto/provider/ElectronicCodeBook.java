/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.security.ProviderException;
import java.base.share.classes.sun.security.util.ArrayUtil;
import java.util.Objects;
import java.base.share.classes.jdk.internal.vm.annotation.IntrinsicCandidate;

/**
 * This class represents ciphers in electronic codebook (ECB) mode.
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

final class ElectronicCodeBook extends FeedbackCipher {

    ElectronicCodeBook(SymmetricCipher embeddedCipher) {
        super(embeddedCipher);
    }

    /**
     * Gets the name of the feedback mechanism
     *
     * @return the name of the feedback mechanism
     */
    String getFeedback() {
        return "ECB";
    }

    /**
     * Resets the iv to its original value.
     * This is used when doFinal is called in the Cipher class, so that the
     * cipher can be reused (with its original iv).
     */
    void reset() {
        // empty
    }

    /**
     * Save the current content of this cipher.
     */
    void save() {}

    /**
     * Restores the content of this cipher to the previous saved one.
     */
    void restore() {}

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
        if ((key == null) || (iv != null)) {
            throw new InvalidKeyException("Internal error");
        }
        embeddedCipher.init(decrypting, algorithm, key);
    }

    @IntrinsicCandidate
    private int implECBEncrypt(byte [] in, int inOff, int len, byte[] out, int outOff) {
        for (int i = len; i >= blockSize; i -= blockSize) {
            embeddedCipher.encryptBlock(in, inOff, out, outOff);
            inOff += blockSize;
            outOff += blockSize;
        }
        return len;
    }

    /**
     * Performs encryption operation.
     *
     * <p>The input plain text <code>in</code>, starting at
     * <code>inOff</code> and ending at * <code>(inOff + len - 1)</code>,
     * is encrypted. The result is stored in <code>out</code>, starting at
     * <code>outOff</code>.
     *
     * @param in the buffer with the input data to be encrypted
     * @param inOff the offset in <code>plain</code>
     * @param len the length of the input data
     * @param out the buffer for the result
     * @param outOff the offset in <code>cipher</code>
     * @exception ProviderException if <code>len</code> is not
     * a multiple of the block size
     * @return the length of the encrypted data
     */
    int encrypt(byte[] in, int inOff, int len, byte[] out, int outOff) {
        ArrayUtil.blockSizeCheck(len, blockSize);
        ArrayUtil.nullAndBoundsCheck(in, inOff, len);
        ArrayUtil.nullAndBoundsCheck(out, outOff, len);
        return implECBEncrypt(in, inOff, len, out, outOff);
    }

    @IntrinsicCandidate
    private int implECBDecrypt(byte [] in, int inOff, int len, byte[] out, int outOff) {
        for (int i = len; i >= blockSize; i -= blockSize) {
            embeddedCipher.decryptBlock(in, inOff, out, outOff);
            inOff += blockSize;
            outOff += blockSize;
        }
        return len;
    }

    /**
     * Performs decryption operation.
     *
     * <p>The input cipher text <code>in</code>, starting at
     * <code>inOff</code> and ending at * <code>(inOff + len - 1)</code>,
     * is decrypted.The result is stored in <code>out</code>, starting at
     * <code>outOff</code>.
     *
     * @param in the buffer with the input data to be decrypted
     * @param inOff the offset in <code>cipherOffset</code>
     * @param len the length of the input data
     * @param out the buffer for the result
     * @param outOff the offset in <code>plain</code>
     * @exception ProviderException if <code>len</code> is not
     * a multiple of the block size
     * @return the length of the decrypted data
     */
    int decrypt(byte[] in, int inOff, int len, byte[] out, int outOff) {
        ArrayUtil.blockSizeCheck(len, blockSize);
        ArrayUtil.nullAndBoundsCheck(in, inOff, len);
        ArrayUtil.nullAndBoundsCheck(out, outOff, len);
        return implECBDecrypt(in, inOff, len, out, outOff);
   }
}
