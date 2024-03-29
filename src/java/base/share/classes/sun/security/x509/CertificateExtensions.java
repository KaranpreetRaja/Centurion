/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.x509;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.CertificateException;
import java.util.*;

import sun.security.util.*;

/**
 * This class defines the Extensions attribute for the Certificate.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 * @see DerEncoder
 */
public class CertificateExtensions implements DerEncoder {

    public static final String NAME = "extensions";

    private static final Debug debug = Debug.getInstance("x509");

    private final Map<String,Extension> map = Collections.synchronizedMap(
            new TreeMap<>());
    private boolean unsupportedCritExt = false;

    private Map<String,Extension> unparseableExtensions;

    /**
     * Default constructor.
     */
    public CertificateExtensions() { }

    /**
     * Create the object, decoding the values from the passed DER stream.
     *
     * @param in the DerInputStream to read the Extension from.
     * @exception IOException on decoding errors.
     */
    public CertificateExtensions(DerInputStream in) throws IOException {
        init(in);
    }

    // helper routine
    private void init(DerInputStream in) throws IOException {

        DerValue[] exts = in.getSequence(5);

        for (int i = 0; i < exts.length; i++) {
            Extension ext = new Extension(exts[i]);
            parseExtension(ext);
        }
    }

    private static final Class<?>[] PARAMS = {Boolean.class, Object.class};

    // Parse the encoded extension
    private void parseExtension(Extension ext) throws IOException {
        try {
            Class<?> extClass = OIDMap.getClass(ext.getExtensionId());
            if (extClass == null) {   // Unsupported extension
                if (ext.isCritical()) {
                    unsupportedCritExt = true;
                }
                if (map.put(ext.getExtensionId().toString(), ext) == null) {
                    return;
                } else {
                    throw new IOException("Duplicate extensions not allowed");
                }
            }
            Constructor<?> cons = extClass.getConstructor(PARAMS);

            Object[] passed = new Object[] {Boolean.valueOf(ext.isCritical()),
                    ext.getExtensionValue()};
            Extension certExt = (Extension) cons.newInstance(passed);
            if (map.put(certExt.getName(), certExt) != null) {
                throw new IOException("Duplicate extensions not allowed");
            }
        } catch (InvocationTargetException invk) {
            Throwable e = invk.getCause();
            if (!ext.isCritical()) {
                // ignore errors parsing non-critical extensions
                if (unparseableExtensions == null) {
                    unparseableExtensions = new TreeMap<>();
                }
                unparseableExtensions.put(ext.getExtensionId().toString(),
                        new UnparseableExtension(ext, e));
                if (debug != null) {
                    debug.println("Debug info only." +
                       " Error parsing extension: " + ext);
                    e.printStackTrace();
                    HexDumpEncoder h = new HexDumpEncoder();
                    System.err.println(h.encodeBuffer(ext.getExtensionValue()));
                }
                return;
            }
            if (e instanceof IOException) {
                throw (IOException)e;
            } else {
                throw new IOException(e);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Encode the extensions in DER form to the stream, setting
     * the context specific tag as needed in the X.509 v3 certificate.
     *
     * @param out the DerOutputStream to marshal the contents to.
     */
    @Override
    public void encode(DerOutputStream out) {
        encode(out, false);
    }

    /**
     * Encode the extensions in DER form to the stream.
     *
     * @param out the DerOutputStream to marshal the contents to.
     * @param isCertReq if true then no context specific tag is added.
     */
    public void encode(DerOutputStream out, boolean isCertReq) {
        DerOutputStream extOut = new DerOutputStream();
        for (Extension ext : map.values()) {
            ext.encode(extOut);
        }

        if (!isCertReq) { // certificate
            DerOutputStream seq = new DerOutputStream();
            seq.write(DerValue.tag_Sequence, extOut);
            out.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)3),
                    seq);
        } else {
            out.write(DerValue.tag_Sequence, extOut);
        }
    }

    /**
     * Set the extension value.
     * @param name the extension name used in the cache.
     * @param ext the extension to set.
     */
    public void setExtension(String name, Extension ext) {
        map.put(name, ext);
    }

    /**
     * Get the extension with this alias.
     *
     * @param alias the identifier string for the extension to retrieve.
     *              Could be one of "x509.info.extensions.ExtensionName",
     *              "ExtensionName", "2.3.4.5".
     */
    public Extension getExtension(String alias) {
        String name;
        if (alias.startsWith(X509CertImpl.NAME)) {
            int index = alias.lastIndexOf('.');
            name = alias.substring(index + 1);
        } else {
            name = alias;
        }
        return map.get(name);
    }

    /**
     * Delete the extension value.
     * @param name the extension name used in the lookup.
     * @exception IOException if named extension is not found.
     */
    public void delete(String name) throws IOException {
        Object obj = map.get(name);
        if (obj == null) {
            throw new IOException("No extension found with name " + name);
        }
        map.remove(name);
    }

    public String getNameByOid(ObjectIdentifier oid) {
        for (String name: map.keySet()) {
            if (map.get(name).getExtensionId().equals(oid)) {
                return name;
            }
        }
        return null;
    }


    /**
     * Return a collection view of the extensions.
     * @return a collection view of the extensions in this Certificate.
     */
    public Collection<Extension> getAllExtensions() {
        return map.values();
    }

    public Map<String,Extension> getUnparseableExtensions() {
        return (unparseableExtensions == null) ?
                Collections.emptyMap() : unparseableExtensions;
    }

    /**
     * Return true if a critical extension is found that is
     * not supported, otherwise return false.
     */
    public boolean hasUnsupportedCriticalExtension() {
        return unsupportedCritExt;
    }

    /**
     * Compares this CertificateExtensions for equality with the specified
     * object. If the {@code other} object is an
     * {@code instanceof} {@code CertificateExtensions}, then
     * all the entries are compared with the entries from this.
     *
     * @param other the object to test for equality with this
     * CertificateExtensions.
     * @return true iff all the entries match that of the Other,
     * false otherwise.
     */
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof CertificateExtensions otherCX))
            return false;

        Collection<Extension> otherX = otherCX.getAllExtensions();
        if (otherX.size() != map.size())
            return false;

        Extension thisExt;
        String key;
        for (Extension otherExt : otherX) {
            key = otherExt.getName();
            thisExt = map.get(key);
            if (thisExt == null)
                return false;
            if (! thisExt.equals(otherExt))
                return false;
        }
        return this.getUnparseableExtensions().equals(
                otherCX.getUnparseableExtensions());
    }

    /**
     * Returns a hashcode value for this CertificateExtensions.
     *
     * @return the hashcode value.
     */
    public int hashCode() {
        return map.hashCode() + getUnparseableExtensions().hashCode();
    }

    /**
     * Returns a string representation of this {@code CertificateExtensions}
     * object in the form of a set of entries, enclosed in braces and separated
     * by the ASCII characters "<code>,&nbsp;</code>" (comma and space).
     * <p>Overrides to {@code toString} method of {@code Object}.
     *
     * @return  a string representation of this CertificateExtensions.
     */
    public String toString() {
        return map.toString();
    }
}
