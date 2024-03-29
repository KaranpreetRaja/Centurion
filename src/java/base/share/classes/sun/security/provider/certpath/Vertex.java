/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.base.share.classes.sun.security.util.Debug;
import java.base.share.classes.sun.security.x509.AuthorityKeyIdentifierExtension;
import java.base.share.classes.sun.security.x509.KeyIdentifier;
import java.base.share.classes.sun.security.x509.SubjectKeyIdentifierExtension;
import java.base.share.classes.sun.security.x509.X509CertImpl;

/**
 * This class represents a vertex in the adjacency list. A
 * vertex in the builder's view is just a distinguished name
 * in the directory.  The Vertex contains a certificate
 * along an attempted certification path, along with a pointer
 * to a list of certificates that followed this one in various
 * attempted certification paths.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
final class Vertex {

    private static final Debug debug = Debug.getInstance("certpath");
    private final X509Certificate cert;
    private int index;
    private Throwable throwable;

    /**
     * Constructor; creates vertex with index of -1
     * Use setIndex method to set another index.
     *
     * @param cert X509Certificate associated with vertex
     */
    Vertex(X509Certificate cert) {
        this.cert = cert;
        this.index = -1;
    }

    /**
     * return the certificate for this vertex
     *
     * @return X509Certificate
     */
    public X509Certificate getCertificate() {
        return cert;
    }

    /**
     * get the index for this vertex, where the index is the row of the
     * adjacency list that contains certificates that could follow this
     * certificate.
     *
     * @return int index for this vertex, or -1 if no following certificates.
     */
    public int getIndex() {
        return index;
    }

    /**
     * set the index for this vertex, where the index is the row of the
     * adjacency list that contains certificates that could follow this
     * certificate.
     *
     * @param ndx int index for vertex, or -1 if no following certificates.
     */
    void setIndex(int ndx) {
        index = ndx;
    }

    /**
     * return the throwable associated with this vertex;
     * returns null if none.
     *
     * @return Throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * set throwable associated with this vertex; default value is null.
     *
     * @param throwable Throwable associated with this vertex
     *                  (or null)
     */
    void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Return full string representation of vertex
     *
     * @return String representation of vertex
     */
    @Override
    public String toString() {
        return certToString() + throwableToString() + indexToString();
    }

    /**
     * Return string representation of this vertex's
     * certificate information.
     *
     * @return String representation of certificate info
     */
    public String certToString() {
        StringBuilder sb = new StringBuilder();

        X509CertImpl x509Cert;
        try {
            x509Cert = X509CertImpl.toImpl(cert);
        } catch (CertificateException ce) {
            if (debug != null) {
                debug.println("Vertex.certToString() unexpected exception");
                ce.printStackTrace();
            }
            return sb.toString();
        }

        sb.append("Issuer:     ").append
                (x509Cert.getIssuerX500Principal()).append("\n");
        sb.append("Subject:    ").append
                (x509Cert.getSubjectX500Principal()).append("\n");
        sb.append("SerialNum:  ").append
                (x509Cert.getSerialNumber().toString(16)).append("\n");
        sb.append("Expires:    ").append
                (x509Cert.getNotAfter().toString()).append("\n");
        boolean[] iUID = x509Cert.getIssuerUniqueID();
        if (iUID != null) {
            sb.append("IssuerUID:  ");
            for (boolean b : iUID) {
                sb.append(b ? 1 : 0);
            }
            sb.append("\n");
        }
        boolean[] sUID = x509Cert.getSubjectUniqueID();
        if (sUID != null) {
            sb.append("SubjectUID: ");
            for (boolean b : sUID) {
                sb.append(b ? 1 : 0);
            }
            sb.append("\n");
        }
        SubjectKeyIdentifierExtension sKeyID =
                x509Cert.getSubjectKeyIdentifierExtension();
        if (sKeyID != null) {
            KeyIdentifier keyID = sKeyID.getKeyIdentifier();
            sb.append("SubjKeyID:  ").append(keyID.toString());
        }
        AuthorityKeyIdentifierExtension aKeyID =
                x509Cert.getAuthorityKeyIdentifierExtension();
        if (aKeyID != null) {
            KeyIdentifier keyID = aKeyID.getKeyIdentifier();
            sb.append("AuthKeyID:  ").append(keyID.toString());
        }
        return sb.toString();
    }

    /**
     * return Vertex throwable as String compatible with
     * the way toString returns other information
     *
     * @return String form of exception (or "none")
     */
    public String throwableToString() {
        StringBuilder sb = new StringBuilder("Exception:  ");
        if (throwable != null)
            sb.append(throwable.toString());
        else
            sb.append("null");
        sb.append("\n");
        return sb.toString();
    }

    /**
     * return Vertex index as String compatible with
     * the way other Vertex.xToString() methods display
     * information.
     *
     * @return String form of index as "Last cert?  [Yes/No]"
     */
    public String moreToString() {
        return "Last cert?  " + ((index == -1) ? "Yes" : "No") +
                "\n";
    }

    /**
     * return Vertex index as String compatible with
     * the way other Vertex.xToString() methods displays other information.
     *
     * @return String form of index as "Index:     [numeric index]"
     */
    public String indexToString() {
        return "Index:      " + index + "\n";
    }
}
