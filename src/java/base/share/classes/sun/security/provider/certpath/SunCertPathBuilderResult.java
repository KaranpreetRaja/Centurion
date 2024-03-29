/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.provider.certpath;

import java.base.share.classes.sun.security.util.Debug;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;

/**
 * This class represents the result of a SunCertPathBuilder build.
 * Since all paths returned by the SunCertPathProvider are PKIX validated
 * the result contains the valid policy tree and subject public key returned
 * by the algorithm. It also contains the trust anchor and debug information
 * represented in the form of an adjacency list.
 *
 * @see PKIXCertPathBuilderResult
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
//@@@ Note: this class is not in public API and access to adjacency list is
//@@@ intended for debugging/replay of Sun PKIX CertPathBuilder implementation.

public final class SunCertPathBuilderResult extends PKIXCertPathBuilderResult {

    private static final Debug debug = Debug.getInstance("certpath");

    private final AdjacencyList adjList;

    /**
     * Creates a SunCertPathBuilderResult instance.
     *
     * @param certPath the validated <code>CertPath</code>
     * @param trustAnchor a <code>TrustAnchor</code> describing the CA that
     * served as a trust anchor for the certification path
     * @param policyTree the valid policy tree, or <code>null</code>
     * if there are no valid policies
     * @param subjectPublicKey the public key of the subject
     * @param adjList an Adjacency list containing debug information
     */
    SunCertPathBuilderResult(CertPath certPath,
        TrustAnchor trustAnchor, PolicyNode policyTree,
        PublicKey subjectPublicKey, AdjacencyList adjList)
    {
        super(certPath, trustAnchor, policyTree, subjectPublicKey);
        this.adjList = adjList;
    }

    /**
     * Returns the adjacency list containing information about the build.
     *
     * @return The adjacency list containing information about the build.
     */
    public AdjacencyList getAdjacencyList() {
        return adjList;
    }
}
