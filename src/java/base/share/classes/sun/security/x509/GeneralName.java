/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;

import sun.security.util.*;

/**
 * This class implements the ASN.1 GeneralName object class.
 * <p>
 * The ASN.1 syntax for this is:
 * <pre>
 * GeneralName ::= CHOICE {
 *    otherName                       [0]     OtherName,
 *    rfc822Name                      [1]     IA5String,
 *    dNSName                         [2]     IA5String,
 *    x400Address                     [3]     ORAddress,
 *    directoryName                   [4]     Name,
 *    ediPartyName                    [5]     EDIPartyName,
 *    uniformResourceIdentifier       [6]     IA5String,
 *    iPAddress                       [7]     OCTET STRING,
 *    registeredID                    [8]     OBJECT IDENTIFIER
 * }
 * </pre>
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public class GeneralName implements DerEncoder {

    // Private data members
    private final GeneralNameInterface name;

    /**
     * Default constructor for the class.
     *
     * @param name the selected CHOICE from the list.
     * @throws NullPointerException if name is null
     */
    public GeneralName(GeneralNameInterface name) {
        if (name == null) {
            throw new NullPointerException("GeneralName must not be null");
        }
        this.name = name;
    }

    /**
     * Create the object from its DER encoded value.
     *
     * @param encName the DER encoded GeneralName.
     */
    public GeneralName(DerValue encName) throws IOException {
        this(encName, false);
    }

    /**
     * Create the object from its DER encoded value.
     *
     * @param encName the DER encoded GeneralName.
     * @param nameConstraint true if general name is a name constraint
     */
    public GeneralName(DerValue encName, boolean nameConstraint)
        throws IOException {
        short tag = (byte)(encName.tag & 0x1f);

        // All names except for NAME_DIRECTORY should be encoded with the
        // IMPLICIT tag.
        switch (tag) {
        case GeneralNameInterface.NAME_ANY:
            if (encName.isContextSpecific() && encName.isConstructed()) {
                encName.resetTag(DerValue.tag_Sequence);
                name = new OtherName(encName);
            } else {
                throw new IOException("Invalid encoding of Other-Name");
            }
            break;

        case GeneralNameInterface.NAME_RFC822:
            if (encName.isContextSpecific() && !encName.isConstructed()) {
                encName.resetTag(DerValue.tag_IA5String);
                name = new RFC822Name(encName);
            } else {
                throw new IOException("Invalid encoding of RFC822 name");
            }
            break;

        case GeneralNameInterface.NAME_DNS:
            if (encName.isContextSpecific() && !encName.isConstructed()) {
                encName.resetTag(DerValue.tag_IA5String);
                name = new DNSName(encName);
            } else {
                throw new IOException("Invalid encoding of DNSName");
            }
            break;

        case GeneralNameInterface.NAME_X400:
            if (encName.isContextSpecific() && encName.isConstructed()) {
                encName.resetTag(DerValue.tag_IA5String);
                name = new X400Address(encName);
            } else {
                throw new IOException("Invalid encoding of X400Address name");
            }
            break;

        case GeneralNameInterface.NAME_URI:
            if (encName.isContextSpecific() && !encName.isConstructed()) {
                encName.resetTag(DerValue.tag_IA5String);
                name = (nameConstraint ? URIName.nameConstraint(encName) :
                        new URIName(encName));
            } else {
                throw new IOException("Invalid encoding of URI");
            }
            break;

        case GeneralNameInterface.NAME_IP:
            if (encName.isContextSpecific() && !encName.isConstructed()) {
                encName.resetTag(DerValue.tag_OctetString);
                name = new IPAddressName(encName);
            } else {
                throw new IOException("Invalid encoding of IP address");
            }
            break;

        case GeneralNameInterface.NAME_OID:
            if (encName.isContextSpecific() && !encName.isConstructed()) {
                encName.resetTag(DerValue.tag_ObjectId);
                name = new OIDName(encName);
            } else {
                throw new IOException("Invalid encoding of OID name");
            }
            break;

        case GeneralNameInterface.NAME_DIRECTORY:
            if (encName.isContextSpecific() && encName.isConstructed()) {
                name = new X500Name(encName.getData());
            } else {
                throw new IOException("Invalid encoding of Directory name");
            }
            break;

        case GeneralNameInterface.NAME_EDI:
            if (encName.isContextSpecific() && encName.isConstructed()) {
                encName.resetTag(DerValue.tag_Sequence);
                name = new EDIPartyName(encName);
            } else {
                throw new IOException("Invalid encoding of EDI name");
            }
            break;

        default:
            throw new IOException("Unrecognized GeneralName tag, ("
                                  + tag +")");
        }
    }

    /**
     * Return the type of the general name.
     */
    public int getType() {
        return name.getType();
    }

    /**
     * Return the GeneralNameInterface name.
     */
    public GeneralNameInterface getName() {
        //XXXX May want to consider cloning this
        return name;
    }

    /**
     * Return the name as user readable string
     */
    public String toString() {
        return name.toString();
    }

    /**
     * Compare this GeneralName with another
     *
     * @param other GeneralName to compare to this
     * @return true if match
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GeneralName))
            return false;
        GeneralNameInterface otherGNI = ((GeneralName)other).name;
        try {
            return name.constrains(otherGNI) == GeneralNameInterface.NAME_MATCH;
        } catch (UnsupportedOperationException ioe) {
            return false;
        }
    }

    /**
     * Returns the hash code for this GeneralName.
     *
     * @return a hash code value.
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Encode the name to the specified DerOutputStream.
     *
     * @param out the DerOutputStream to encode the GeneralName to.
     */
    @Override
    public void encode(DerOutputStream out) {
        DerOutputStream tmp = new DerOutputStream();
        name.encode(tmp);
        int nameType = name.getType();
        if (nameType == GeneralNameInterface.NAME_ANY ||
            nameType == GeneralNameInterface.NAME_X400 ||
            nameType == GeneralNameInterface.NAME_EDI) {

            // implicit, constructed form
            out.writeImplicit(DerValue.createTag(DerValue.TAG_CONTEXT,
                              true, (byte)nameType), tmp);
        } else if (nameType == GeneralNameInterface.NAME_DIRECTORY) {
            // explicit, constructed form since underlying tag is CHOICE
            // (see X.680 section 30.6, part c)
            out.write(DerValue.createTag(DerValue.TAG_CONTEXT,
                                         true, (byte)nameType), tmp);
        } else {
            // implicit, primitive form
            out.writeImplicit(DerValue.createTag(DerValue.TAG_CONTEXT,
                              false, (byte)nameType), tmp);
        }
    }
}
