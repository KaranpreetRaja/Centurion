/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.cert;

/**
 * Certificate Expired Exception. This is thrown whenever the current
 * {@code Date} or the specified {@code Date} is after the
 * {@code notAfter} date/time specified in the validity period
 * of the certificate.
 *
 * @author Hemma Prafullchandra
 * @since 1.2
 */
public class CertificateExpiredException extends CertificateException {

    @java.io.Serial
    private static final long serialVersionUID = 9071001339691533771L;

    /**
     * Constructs a CertificateExpiredException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public CertificateExpiredException() {
        super();
    }

    /**
     * Constructs a CertificateExpiredException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.
     *
     * @param message the detail message.
     */
    public CertificateExpiredException(String message) {
        super(message);
    }
}
