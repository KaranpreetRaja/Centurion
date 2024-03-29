/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import sun.security.util.*;

/**
 * This class defines the SerialNumber class used by certificates.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public class SerialNumber {
    private BigInteger  serialNum;

    // Construct the class from the DerValue
    private void construct(DerValue derVal) throws IOException {
        serialNum = derVal.getBigInteger();
        if (derVal.data.available() != 0) {
            throw new IOException("Excess SerialNumber data");
        }
    }

    /**
     * The default constructor for this class using BigInteger.
     *
     * @param num the BigInteger number used to create the serial number.
     */
    public SerialNumber(BigInteger num) {
        serialNum = num;
    }

    /**
     * The default constructor for this class using int.
     *
     * @param num the BigInteger number used to create the serial number.
     */
    public SerialNumber(int num) {
        serialNum = BigInteger.valueOf(num);
    }

    /**
     * Create the object, decoding the values from the passed DER stream.
     *
     * @param in the DerInputStream to read the SerialNumber from.
     * @exception IOException on decoding errors.
     */
    public SerialNumber(DerInputStream in) throws IOException {
        DerValue derVal = in.getDerValue();
        construct(derVal);
    }

    /**
     * Create the object, decoding the values from the passed DerValue.
     *
     * @param val the DerValue to read the SerialNumber from.
     * @exception IOException on decoding errors.
     */
    public SerialNumber(DerValue val) throws IOException {
        construct(val);
    }

    /**
     * Create the object, decoding the values from the passed stream.
     *
     * @param in the InputStream to read the SerialNumber from.
     * @exception IOException on decoding errors.
     */
    public SerialNumber(InputStream in) throws IOException {
        DerValue derVal = new DerValue(in);
        construct(derVal);
    }

    /**
     * Return the SerialNumber as user readable string.
     */
    public String toString() {
        return "SerialNumber: [" + Debug.toHexString(serialNum) + ']';
    }

    /**
     * Encode the SerialNumber in DER form to the stream.
     *
     * @param out the DerOutputStream to marshal the contents to.
     */
    public void encode(DerOutputStream out) {
        out.putInteger(serialNum);
    }

    /**
     * Return the serial number.
     */
    public BigInteger getNumber() {
        return serialNum;
    }
}
