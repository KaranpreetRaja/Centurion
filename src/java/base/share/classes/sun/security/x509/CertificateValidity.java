/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.security.cert.*;
import java.util.Date;
import java.util.Objects;

import sun.security.util.*;

/**
 * This class defines the interval for which the certificate is valid.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 * @see DerEncoder
 */
public class CertificateValidity implements DerEncoder {

    public static final String NAME = "validity";
    /**
     * YR_2050 date and time set to Jan01 00:00 2050 GMT
     */
    static final long YR_2050 = 2524608000000L;

    // Private data members
    private final Date        notBefore;
    private final Date        notAfter;

    // Returns the first time the certificate is valid.
    public Date getNotBefore() {
        return new Date(notBefore.getTime());
    }

    // Returns the last time the certificate is valid.
    public Date getNotAfter() {
       return new Date(notAfter.getTime());
    }

    /**
     * The constructor for this class for the specified interval.
     *
     * @param notBefore the date and time before which the certificate
     *                   is not valid
     * @param notAfter the date and time after which the certificate is
     *                  not valid
     */
    public CertificateValidity(Date notBefore, Date notAfter) {
        this.notBefore = Objects.requireNonNull(notBefore);
        this.notAfter = Objects.requireNonNull(notAfter);
    }

    /**
     * Create the object, decoding the values from the passed DER stream.
     *
     * @param in the DerInputStream to read the CertificateValidity from
     * @exception IOException on decoding errors.
     */
    public CertificateValidity(DerInputStream in) throws IOException {
        DerValue derVal = in.getDerValue();
        if (derVal.tag != DerValue.tag_Sequence) {
            throw new IOException("Invalid encoded CertificateValidity, " +
                                  "starting sequence tag missing.");
        }
        // check if UTCTime encoded or GeneralizedTime
        if (derVal.data.available() == 0)
            throw new IOException("No data encoded for CertificateValidity");

        DerInputStream derIn = new DerInputStream(derVal.toByteArray());
        DerValue[] seq = derIn.getSequence(2);
        if (seq.length != 2)
            throw new IOException("Invalid encoding for CertificateValidity");

        if (seq[0].tag == DerValue.tag_UtcTime) {
            notBefore = derVal.data.getUTCTime();
        } else if (seq[0].tag == DerValue.tag_GeneralizedTime) {
            notBefore = derVal.data.getGeneralizedTime();
        } else {
            throw new IOException("Invalid encoding for CertificateValidity");
        }

        if (seq[1].tag == DerValue.tag_UtcTime) {
            notAfter = derVal.data.getUTCTime();
        } else if (seq[1].tag == DerValue.tag_GeneralizedTime) {
            notAfter = derVal.data.getGeneralizedTime();
        } else {
            throw new IOException("Invalid encoding for CertificateValidity");
        }
    }

    /**
     * Return the validity period as user readable string.
     */
    public String toString() {
        return "Validity: [From: " + notBefore +
               ",\n               To: " + notAfter + ']';
    }

    /**
     * Encode the CertificateValidity period in DER form to the stream.
     *
     * @param out the DerOutputStream to marshal the contents to.
     */
    @Override
    public void encode(DerOutputStream out) {

        DerOutputStream pair = new DerOutputStream();

        if (notBefore.getTime() < YR_2050) {
            pair.putUTCTime(notBefore);
        } else
            pair.putGeneralizedTime(notBefore);

        if (notAfter.getTime() < YR_2050) {
            pair.putUTCTime(notAfter);
        } else {
            pair.putGeneralizedTime(notAfter);
        }
        out.write(DerValue.tag_Sequence, pair);
    }

    /**
     * Verify that the current time is within the validity period.
     *
     * @exception CertificateExpiredException if the certificate has expired.
     * @exception CertificateNotYetValidException if the certificate is not
     * yet valid.
     */
    public void valid()
    throws CertificateNotYetValidException, CertificateExpiredException {
        Date now = new Date();
        valid(now);
    }

    /**
     * Verify that the passed time is within the validity period.
     * @param now the Date against which to compare the validity
     * period.
     *
     * @exception CertificateExpiredException if the certificate has expired
     * with respect to the <code>Date</code> supplied.
     * @exception CertificateNotYetValidException if the certificate is not
     * yet valid with respect to the <code>Date</code> supplied.
     *
     */
    public void valid(Date now)
    throws CertificateNotYetValidException, CertificateExpiredException {
        /*
         * we use the internal Dates rather than the passed in Date
         * because someone could override the Date methods after()
         * and before() to do something entirely different.
         */
        if (notBefore.after(now)) {
            throw new CertificateNotYetValidException("NotBefore: " +
                                                      notBefore.toString());
        }
        if (notAfter.before(now)) {
            throw new CertificateExpiredException("NotAfter: " +
                                                  notAfter.toString());
        }
    }
}
