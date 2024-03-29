/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.security.cert.X509Certificate;

/**
 * Describes one step of a certification path build, consisting of a
 * <code>Vertex</code> state description, a certificate, a possible throwable,
 * and a result code.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 * @see sun.security.provider.certpath.Vertex
 */
public class BuildStep {

    private final Vertex    vertex;
    private X509Certificate cert;
    private Throwable       throwable;
    private final int       result;

    /**
     * result code associated with a certificate that may continue a path from
     * the current certificate.
     */
    public static final int POSSIBLE = 1;

    /**
     * result code associated with a certificate that was tried, but that
     * represents an unsuccessful path, so the certificate has been backed out
     * to allow backtracking to the next possible path.
     */
    public static final int BACK = 2;

    /**
     * result code associated with a certificate that successfully continues the
     * current path, but does not yet reach the target.
     */
    public static final int FOLLOW = 3;

    /**
     * result code associated with a certificate that represents the end of the
     * last possible path, where no path successfully reached the target.
     */
    public static final int FAIL = 4;

    /**
     * result code associated with a certificate that represents the end of a
     * path that successfully reaches the target.
     */
    public static final int SUCCEED = 5;

    /**
     * construct a BuildStep
     *
     * @param vtx description of the vertex at this step
     * @param res result, where result is one of POSSIBLE, BACK,
     *            FOLLOW, FAIL, SUCCEED
     */
    public BuildStep(Vertex vtx, int res) {
        vertex = vtx;
        if (vertex != null) {
            cert = vertex.getCertificate();
            throwable = vertex.getThrowable();
        }
        result = res;
    }

    /**
     * return vertex description for this build step
     *
     * @return Vertex
     */
    public Vertex getVertex() {
        return vertex;
    }

    /**
     * return the certificate associated with this build step
     *
     * @return X509Certificate
     */
    public X509Certificate getCertificate() {
        return cert;
    }

    /**
     * return string form of issuer name from certificate associated with this
     * build step
     *
     * @return String form of issuer name or null, if no certificate.
     */
    public String getIssuerName() {
        return getIssuerName(null);
    }

    /**
     * return string form of issuer name from certificate associated with this
     * build step, or a default name if no certificate associated with this
     * build step, or if issuer name could not be obtained from the certificate.
     *
     * @param defaultName name to use as default if unable to return an issuer
     * name from the certificate, or if no certificate.
     * @return String form of issuer name or defaultName, if no certificate or
     * exception received while trying to extract issuer name from certificate.
     */
    public String getIssuerName(String defaultName) {
        return (cert == null ? defaultName
                             : cert.getIssuerX500Principal().toString());
    }

    /**
     * return string form of subject name from certificate associated with this
     * build step.
     *
     * @return String form of subject name or null, if no certificate.
     */
    public String getSubjectName() {
        return getSubjectName(null);
    }

    /**
     * return string form of subject name from certificate associated with this
     * build step, or a default name if no certificate associated with this
     * build step, or if subject name could not be obtained from the
     * certificate.
     *
     * @param defaultName name to use as default if unable to return a subject
     * name from the certificate, or if no certificate.
     * @return String form of subject name or defaultName, if no certificate or
     * if an exception was received while attempting to extract the subject name
     * from the certificate.
     */
    public String getSubjectName(String defaultName) {
        return (cert == null ? defaultName
                             : cert.getSubjectX500Principal().toString());
    }

    /**
     * return the exception associated with this build step.
     *
     * @return Throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * return the result code associated with this build step.  The result codes
     * are POSSIBLE, FOLLOW, BACK, FAIL, SUCCEED.
     *
     * @return int result code
     */
    public int getResult() {
        return result;
    }

    /**
     * return a string representing the meaning of the result code associated
     * with this build step.
     *
     * @param   res    result code
     * @return String string representing meaning of the result code
     */
    public String resultToString(int res) {
        return switch (res) {
            case POSSIBLE -> "Certificate to be tried.\n";
            case BACK -> "Certificate backed out since path does not "
                    + "satisfy build requirements.\n";
            case FOLLOW, SUCCEED -> "Certificate satisfies conditions.\n";
            case FAIL -> "Certificate backed out since path does not "
                    + "satisfy conditions.\n";
            default -> "Internal error: Invalid step result value.\n";
        };
    }

    /**
     * return a string representation of this build step, showing minimal
     * detail.
     *
     * @return String
     */
    @Override
    public String toString() {
        String out;
        switch (result) {
        case BACK:
        case FAIL:
            out = resultToString(result);
            out = out + vertex.throwableToString();
            break;
        case FOLLOW:
        case SUCCEED:
        case POSSIBLE:
            out = resultToString(result);
            break;
        default:
            out = "Internal Error: Invalid step result\n";
        }
        return out;
    }

    /**
     * return a string representation of this build step, showing all detail of
     * the vertex state appropriate to the result of this build step, and the
     * certificate contents.
     *
     * @return String
     */
    public String verboseToString() {
        String out = resultToString(getResult());
        switch (result) {
        case BACK:
        case FAIL:
            out = out + vertex.throwableToString();
            break;
        case FOLLOW:
        case SUCCEED:
            out = out + vertex.moreToString();
            break;
        default:
            break;
        }
        out = out + "Certificate contains:\n" + vertex.certToString();
        return out;
    }

    /**
     * return a string representation of this build step, including all possible
     * detail of the vertex state, but not including the certificate contents.
     *
     * @return String
     */
    public String fullToString() {
        return resultToString(getResult()) + vertex.toString();
    }
}
