/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.util.Vector;
import java.util.List;
import java.util.Collections;

import sun.security.util.*;

/**
 * This class defines the certificate policy set ASN.1 object.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */
public class CertificatePolicySet implements DerEncoder {

    private final Vector<CertificatePolicyId> ids;

    /**
     * The default constructor for this class.
     *
     * @param ids the sequence of CertificatePolicyId's.
     */
    public CertificatePolicySet(Vector<CertificatePolicyId> ids) {
        this.ids = ids;
    }

    /**
     * Create the object from the DerValue.
     *
     * @param in the passed DerInputStream.
     * @exception IOException on decoding errors.
     */
    public CertificatePolicySet(DerInputStream in) throws IOException {
        ids = new Vector<>();
        DerValue[] seq = in.getSequence(5);

        for (int i = 0; i < seq.length; i++) {
            CertificatePolicyId id = new CertificatePolicyId(seq[i]);
            ids.addElement(id);
        }
    }

    /**
     * Return printable form of the object.
     */
    public String toString() {

        return ("CertificatePolicySet:[\n"
                 + ids.toString()
                 + "]\n");
    }

    /**
     * Encode the policy set to the output stream.
     *
     * @param out the DerOutputStream to encode the data to.
     */
    @Override
    public void encode(DerOutputStream out) {
        DerOutputStream tmp = new DerOutputStream();

        for (int i = 0; i < ids.size(); i++) {
            ids.elementAt(i).encode(tmp);
        }
        out.write(DerValue.tag_Sequence,tmp);
    }

    /**
     * Return the sequence of CertificatePolicyIds.
     *
     * @return A List containing the CertificatePolicyId objects.
     *
     */
    public List<CertificatePolicyId> getCertPolicyIds() {
        return Collections.unmodifiableList(ids);
    }
}
