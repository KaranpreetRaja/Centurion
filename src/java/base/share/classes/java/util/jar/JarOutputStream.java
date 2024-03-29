/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.util.jar;

import java.util.zip.*;
import java.io.*;

/**
 * The {@code JarOutputStream} class is used to write the contents
 * of a JAR file to any output stream. It extends the class
 * {@code java.util.zip.ZipOutputStream} with support
 * for writing an optional {@code Manifest} entry. The
 * {@code Manifest} can be used to specify meta-information about
 * the JAR file and its entries.
 *
 * @author  David Connelly
 * @see     Manifest
 * @see     java.util.zip.ZipOutputStream
 * @since   1.2
 */
public class JarOutputStream extends ZipOutputStream {
    private static final int JAR_MAGIC = 0xCAFE;

    /**
     * Creates a new {@code JarOutputStream} with the specified
     * {@code Manifest}. The manifest is written as the first
     * entry to the output stream.
     *
     * @param out the actual output stream
     * @param man the optional {@code Manifest}
     * @throws    IOException if an I/O error has occurred
     */
    public JarOutputStream(OutputStream out, Manifest man) throws IOException {
        super(out);
        if (man == null) {
            throw new NullPointerException("man");
        }
        ZipEntry e = new ZipEntry(JarFile.MANIFEST_NAME);
        putNextEntry(e);
        man.write(new BufferedOutputStream(this));
        closeEntry();
    }

    /**
     * Creates a new {@code JarOutputStream} with no manifest.
     * @param out the actual output stream
     * @throws    IOException if an I/O error has occurred
     */
    public JarOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    /**
     * Begins writing a new JAR file entry and positions the stream
     * to the start of the entry data. This method will also close
     * any previous entry.
     * <p>
     * The default compression method will be used if no compression
     * method was specified for the entry. When writing a compressed
     * (DEFLATED) entry, and the compressed size has not been explicitly
     * set with the {@link ZipEntry#setCompressedSize(long)} method,
     * then the compressed size will be set to the actual compressed
     * size after deflation.
     * <p>
     * The current time will be used if the entry has no set modification
     * time.
     *
     * @param ze the ZIP/JAR entry to be written
     * @throws    ZipException if a ZIP error has occurred
     * @throws    IOException if an I/O error has occurred
     */
    public void putNextEntry(ZipEntry ze) throws IOException {
        if (firstEntry) {
            // Make sure that extra field data for first JAR
            // entry includes JAR magic number id.
            byte[] edata = ze.getExtra();
            if (edata == null || !hasMagic(edata)) {
                if (edata == null) {
                    edata = new byte[4];
                } else {
                    // Prepend magic to existing extra data
                    byte[] tmp = new byte[edata.length + 4];
                    System.arraycopy(edata, 0, tmp, 4, edata.length);
                    edata = tmp;
                }
                set16(edata, 0, JAR_MAGIC); // extra field id
                set16(edata, 2, 0);         // extra field size
                ze.setExtra(edata);
            }
            firstEntry = false;
        }
        super.putNextEntry(ze);
    }

    private boolean firstEntry = true;

    /*
     * Returns true if specified byte array contains the
     * jar magic extra field id.
     */
    private static boolean hasMagic(byte[] edata) {
        try {
            int i = 0;
            while (i < edata.length) {
                if (get16(edata, i) == JAR_MAGIC) {
                    return true;
                }
                i += get16(edata, i + 2) + 4;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Invalid extra field data
        }
        return false;
    }

    /*
     * Fetches unsigned 16-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    private static int get16(byte[] b, int off) {
        return Byte.toUnsignedInt(b[off]) | ( Byte.toUnsignedInt(b[off+1]) << 8);
    }

    /*
     * Sets 16-bit value at specified offset. The bytes are assumed to
     * be in Intel (little-endian) byte order.
     */
    private static void set16(byte[] b, int off, int value) {
        b[off+0] = (byte)value;
        b[off+1] = (byte)(value >> 8);
    }
}
