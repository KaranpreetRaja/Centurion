/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.util.*;
import java.io.IOException;

import sun.security.util.*;

/**
 * This object class represents the GeneralNames type required in
 * X509 certificates.
 * <p>The ASN.1 syntax for this is:
 * <pre>
 * GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName
 * </pre>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 *
 */
public class GeneralNames {

    private final List<GeneralName> names;

    /**
     * Create the GeneralNames, decoding from the passed DerValue.
     *
     * @param derVal the DerValue to construct the GeneralNames from.
     * @exception IOException on error.
     */
    public GeneralNames(DerValue derVal) throws IOException {
        this();
        if (derVal.tag != DerValue.tag_Sequence) {
            throw new IOException("Invalid encoding for GeneralNames.");
        }
        if (derVal.data.available() == 0) {
            throw new IOException("No data available in "
                                      + "passed DER encoded value.");
        }
        // Decode all the GeneralName's
        while (derVal.data.available() != 0) {
            DerValue encName = derVal.data.getDerValue();

            GeneralName name = new GeneralName(encName);
            add(name);
        }
    }

    /**
     * The default constructor for this class.
     */
    public GeneralNames() {
        names = new ArrayList<>();
    }

    public GeneralNames add(GeneralName name) {
        if (name == null) {
            throw new NullPointerException();
        }
        names.add(name);
        return this;
    }

    public GeneralName get(int index) {
        return names.get(index);
    }

    public boolean isEmpty() {
        return names.isEmpty();
    }

    public int size() {
        return names.size();
    }

    public Iterator<GeneralName> iterator() {
        return names.iterator();
    }

    public List<GeneralName> names() {
        return names;
    }

    /**
     * Write the extension to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the extension to.
     */
    public void encode(DerOutputStream out) {
        if (isEmpty()) {
            return;
        }

        DerOutputStream temp = new DerOutputStream();
        for (GeneralName gn : names) {
            gn.encode(temp);
        }
        out.write(DerValue.tag_Sequence, temp);
    }

    /**
     * compare this GeneralNames to other object for equality
     *
     * @return true if this equals obj
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GeneralNames other)) {
            return false;
        }
        return this.names.equals(other.names);
    }

    public int hashCode() {
        return names.hashCode();
    }

    public String toString() {
        return names.toString();
    }

}
