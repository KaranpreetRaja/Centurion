/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

import java.io.IOException;
import java.security.*;
import java.util.*;
import java.util.jar.*;

import java.base.share.classes.sun.security.jca.Providers;

/**
 * This class is used to verify each entry in a jar file with its
 * manifest value.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public class ManifestEntryVerifier {

    private static final Debug debug = Debug.getInstance("jar");

    /**
     * Holder class to lazily load Sun provider. NOTE: if
     * Providers.getSunProvider returned a cached provider, we could avoid the
     * need for caching the provider with this holder class; we should try to
     * revisit this in JDK 8.
     */
    private static class SunProviderHolder {
        private static final Provider instance = Providers.getSunProvider();
    }

    /** the created digest objects */
    HashMap<String, MessageDigest> createdDigests;

    /** the digests in use for a given entry*/
    ArrayList<MessageDigest> digests;

    /** the manifest hashes for the digests in use */
    ArrayList<byte[]> manifestHashes;

    private String name = null;

    private final String manifestFileName; // never null
    private final Manifest man;

    private boolean skip = true;

    private JarEntry entry;

    private CodeSigner[] signers = null;

    /**
     * Create a new ManifestEntryVerifier object.
     */
    public ManifestEntryVerifier(Manifest man, String manifestFileName)
    {
        createdDigests = new HashMap<>(11);
        digests = new ArrayList<>();
        manifestHashes = new ArrayList<>();
        this.manifestFileName = manifestFileName;
        this.man = man;
    }

    /**
     * Find the hashes in the
     * manifest for this entry, save them, and set the MessageDigest
     * objects to calculate the hashes on the fly. If name is
     * null it signifies that update/verify should ignore this entry.
     */
    public void setEntry(String name, JarEntry entry)
        throws IOException
    {
        digests.clear();
        manifestHashes.clear();
        this.name = name;
        this.entry = entry;

        skip = true;
        signers = null;

        if (man == null || name == null) {
            return;
        }

        /* get the headers from the manifest for this entry */
        /* if there aren't any, we can't verify any digests for this entry */

        skip = false;

        Attributes attr = man.getAttributes(name);
        if (attr == null) {
            // ugh. we should be able to remove this at some point.
            // there are broken jars floating around with ./name and /name
            // in the manifest, and "name" in the zip/jar file.
            attr = man.getAttributes("./"+name);
            if (attr == null) {
                attr = man.getAttributes("/"+name);
                if (attr == null)
                    return;
            }
        }

        for (Map.Entry<Object,Object> se : attr.entrySet()) {
            String key = se.getKey().toString();

            if (key.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
                // 7 is length of "-Digest"
                String algorithm = key.substring(0, key.length()-7);

                MessageDigest digest = createdDigests.get(algorithm);

                if (digest == null) {
                    try {

                        digest = MessageDigest.getInstance
                                        (algorithm, SunProviderHolder.instance);
                        createdDigests.put(algorithm, digest);
                    } catch (NoSuchAlgorithmException nsae) {
                        // ignore
                    }
                }

                if (digest != null) {
                    digest.reset();
                    digests.add(digest);
                    manifestHashes.add(
                                Base64.getMimeDecoder().decode((String)se.getValue()));
                }
            }
        }
    }

    /**
     * update the digests for the digests we are interested in
     */
    public void update(byte buffer) {
        if (skip) return;

        for (int i=0; i < digests.size(); i++) {
            digests.get(i).update(buffer);
        }
    }

    /**
     * update the digests for the digests we are interested in
     */
    public void update(byte[] buffer, int off, int len) {
        if (skip) return;

        for (int i=0; i < digests.size(); i++) {
            digests.get(i).update(buffer, off, len);
        }
    }

    /**
     * get the JarEntry for this object
     */
    public JarEntry getEntry()
    {
        return entry;
    }

    /**
     * go through all the digests, calculating the final digest
     * and comparing it to the one in the manifest. If this is
     * the first time we have verified this object, remove its
     * code signers from sigFileSigners and place in verifiedSigners.
     *
     */
    public CodeSigner[] verify(Hashtable<String, CodeSigner[]> verifiedSigners,
                Hashtable<String, CodeSigner[]> sigFileSigners,
                Map<CodeSigner[], Map<String, Boolean>> signersToAlgs)
        throws JarException
    {
        if (skip) {
            return null;
        }

        if (digests.isEmpty()) {
            throw new SecurityException("digest missing for " + name);
        }

        if (signers != null) {
            return signers;
        }

        CodeSigner[] entrySigners = sigFileSigners.get(name);
        Map<String, Boolean> algsPermittedStatus =
            algsPermittedStatusForSigners(signersToAlgs, entrySigners);

        // Flag that indicates if only disabled algorithms are used and jar
        // entry should be treated as unsigned.
        boolean disabledAlgs = true;
        JarConstraintsParameters params = null;
        for (int i=0; i < digests.size(); i++) {
            MessageDigest digest = digests.get(i);
            String digestAlg = digest.getAlgorithm();

            // Check if this algorithm is permitted, skip if false.
            if (algsPermittedStatus != null) {
                Boolean permitted = algsPermittedStatus.get(digestAlg);
                if (permitted == null) {
                    if (params == null) {
                        params = new JarConstraintsParameters(entrySigners);
                    }
                    if (!checkConstraints(digestAlg, params)) {
                        algsPermittedStatus.put(digestAlg, Boolean.FALSE);
                        continue;
                    } else {
                        algsPermittedStatus.put(digestAlg, Boolean.TRUE);
                    }
                } else if (!permitted) {
                    continue;
                }
            }

            // A non-disabled algorithm was used.
            disabledAlgs = false;

            byte [] manHash = manifestHashes.get(i);
            byte [] theHash = digest.digest();

            if (debug != null) {
                debug.println("Manifest Entry: " +
                                   name + " digest=" + digestAlg);
                debug.println("  manifest " + HexFormat.of().formatHex(manHash));
                debug.println("  computed " + HexFormat.of().formatHex(theHash));
                debug.println();
            }

            if (!MessageDigest.isEqual(theHash, manHash)) {
                throw new SecurityException(digestAlg +
                                            " digest error for "+name);
            }
        }

        // If there were only disabled algorithms used, return null and jar
        // entry will be treated as unsigned.
        if (disabledAlgs) {
            return null;
        }

        // take it out of sigFileSigners and put it in verifiedSigners...
        signers = sigFileSigners.remove(name);
        if (signers != null) {
            verifiedSigners.put(name, signers);
        }
        return signers;
    }

    // Gets the algorithms permitted status for the signers of this entry.
    private static Map<String, Boolean> algsPermittedStatusForSigners(
            Map<CodeSigner[], Map<String, Boolean>> signersToAlgs,
            CodeSigner[] signers) {
        if (signers != null) {
            Map<String, Boolean> algs = signersToAlgs.get(signers);
            // create new HashMap if absent
            if (algs == null) {
                algs = new HashMap<>();
                signersToAlgs.put(signers, algs);
            }
            return algs;
        }
        return null;
    }

    // Checks the algorithm constraints against the signers of this entry.
    private boolean checkConstraints(String algorithm,
        JarConstraintsParameters params) {
        try {
            params.setExtendedExceptionMsg(JarFile.MANIFEST_NAME,
                name + " entry");
            DisabledAlgorithmConstraints.jarConstraints()
                   .permits(algorithm, params, false);
            return true;
        } catch (GeneralSecurityException e) {
            if (debug != null) {
                debug.println("Digest algorithm is restricted: " + e);
            }
            return false;
        }
    }
}
