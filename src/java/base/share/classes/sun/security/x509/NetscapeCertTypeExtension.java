/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;

import sun.security.util.*;

/**
 * Represents Netscape Certificate Type Extension.
 * The details are defined
 * <a href=http://www.netscape.com/eng/security/comm4-cert-exts.html>
 * here </a>.
 *
 * <p>This extension, if present, defines both the purpose
 * (e.g., encipherment, signature, certificate signing) and the application
 * (e.g., SSL, S/Mime or Object Signing of the key contained in the
 * certificate). This extension has been superseded by IETF PKIX extensions
 * but is provided here for compatibility reasons.
 *
 * @author Hemma Prafullchandra
 * @see Extension
 */

public class NetscapeCertTypeExtension extends Extension {

    public static final String NAME = "NetscapeCertType";
    public static final String SSL_CLIENT = "ssl_client";
    public static final String SSL_SERVER = "ssl_server";
    public static final String S_MIME = "s_mime";
    public static final String OBJECT_SIGNING = "object_signing";
    public static final String SSL_CA = "ssl_ca";
    public static final String S_MIME_CA = "s_mime_ca";
    public static final String OBJECT_SIGNING_CA = "object_signing_ca";

    /**
     * Object identifier for the Netscape-Cert-Type extension.
     */
    public static ObjectIdentifier NetscapeCertType_Id =
            ObjectIdentifier.of(KnownOIDs.NETSCAPE_CertType);

    private boolean[] bitString;

    private static class MapEntry {
        String mName;
        int mPosition;

        MapEntry(String name, int position) {
            mName = name;
            mPosition = position;
        }
    }

    private static final MapEntry[] mMapData = {
        new MapEntry(SSL_CLIENT, 0),
        new MapEntry(SSL_SERVER, 1),
        new MapEntry(S_MIME, 2),
        new MapEntry(OBJECT_SIGNING, 3),
        // note that bit 4 is reserved
        new MapEntry(SSL_CA, 5),
        new MapEntry(S_MIME_CA, 6),
        new MapEntry(OBJECT_SIGNING_CA, 7),
    };

    private static int getPosition(String name) throws IOException {
        for (int i = 0; i < mMapData.length; i++) {
            if (name.equalsIgnoreCase(mMapData[i].mName))
                return mMapData[i].mPosition;
        }
        throw new IOException("Attribute name [" + name
                             + "] not recognized by NetscapeCertType.");
    }

    // Encode this extension value
    private void encodeThis() {
        DerOutputStream os = new DerOutputStream();
        os.putTruncatedUnalignedBitString(new BitArray(this.bitString));
        this.extensionValue = os.toByteArray();
    }

    /**
     * Check if bit is set.
     *
     * @param position the position in the bit string to check.
     */
    private boolean isSet(int position) {
        return (position < bitString.length) &&
                bitString[position];
    }

    /**
     * Set the bit at the specified position.
     */
    private void set(int position, boolean val) {
        // enlarge bitString if necessary
        if (position >= bitString.length) {
            boolean[] tmp = new boolean[position+1];
            System.arraycopy(bitString, 0, tmp, 0, bitString.length);
            bitString = tmp;
        }
        bitString[position] = val;
    }

    /**
     * Create a NetscapeCertTypeExtension with the passed bit settings.
     * The criticality is set to true.
     *
     * @param bitString the bits to be set for the extension.
     */
    public NetscapeCertTypeExtension(byte[] bitString) {
        this.bitString =
            new BitArray(bitString.length*8, bitString).toBooleanArray();
        this.extensionId = NetscapeCertType_Id;
        this.critical = true;
        encodeThis();
    }

    /**
     * Create a NetscapeCertTypeExtension with the passed bit settings.
     * The criticality is set to true.
     *
     * @param bitString the bits to be set for the extension.
     */
    public NetscapeCertTypeExtension(boolean[] bitString) {
        this.bitString = bitString;
        this.extensionId = NetscapeCertType_Id;
        this.critical = true;
        encodeThis();
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value an array of DER encoded bytes of the actual value.
     * @exception ClassCastException if value is not an array of bytes
     * @exception IOException on error.
     */
    public NetscapeCertTypeExtension(Boolean critical, Object value)
    throws IOException {
        this.extensionId = NetscapeCertType_Id;
        this.critical = critical.booleanValue();
        this.extensionValue = (byte[]) value;
        DerValue val = new DerValue(this.extensionValue);
        this.bitString = val.getUnalignedBitString().toBooleanArray();
    }

    /**
     * Create a default key usage.
     */
    public NetscapeCertTypeExtension() {
        extensionId = NetscapeCertType_Id;
        critical = true;
        bitString = new boolean[0];
    }

    /**
     * Set the attribute value.
     */
    public void set(String name, Boolean val) throws IOException {
        set(getPosition(name), val);
        encodeThis();
    }

    /**
     * Get the attribute value.
     */
    public boolean get(String name) throws IOException {
        return isSet(getPosition(name));
    }

    /**
     * Returns a printable representation of the NetscapeCertType.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("NetscapeCertType [\n");

        if (isSet(0)) {
            sb.append("   SSL client\n");
        }
        if (isSet(1)) {
            sb.append("   SSL server\n");
        }
        if (isSet(2)) {
            sb.append("   S/MIME\n");
        }
        if (isSet(3)) {
            sb.append("   Object Signing\n");
        }
        if (isSet(5)) {
            sb.append("   SSL CA\n");
        }
        if (isSet(6)) {
            sb.append("   S/MIME CA\n");
        }
        if (isSet(7)) {
            sb.append("   Object Signing CA");
        }

        sb.append("]\n");
        return sb.toString();
    }

    /**
     * Write the extension to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the extension to.
     */
    @Override
    public void encode(DerOutputStream out) {
        if (this.extensionValue == null) {
            this.extensionId = NetscapeCertType_Id;
            this.critical = true;
            encodeThis();
        }
        super.encode(out);
    }


    /**
     * Return the name of this extension.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Get a boolean array representing the bits of this extension,
     * as it maps to the KeyUsage extension.
     * @return the bit values of this extension mapped to the bit values
     * of the KeyUsage extension as an array of booleans.
     */
    public boolean[] getKeyUsageMappedBits() {
        KeyUsageExtension keyUsage = new KeyUsageExtension();

        try {
            if (isSet(getPosition(SSL_CLIENT)) ||
                isSet(getPosition(S_MIME)) ||
                isSet(getPosition(OBJECT_SIGNING)))
                keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);

            if (isSet(getPosition(SSL_SERVER)))
                keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);

            if (isSet(getPosition(SSL_CA)) ||
                isSet(getPosition(S_MIME_CA)) ||
                isSet(getPosition(OBJECT_SIGNING_CA)))
                keyUsage.set(KeyUsageExtension.KEY_CERTSIGN, true);
        } catch (IOException e) { }
        return keyUsage.getBits();
    }
}
