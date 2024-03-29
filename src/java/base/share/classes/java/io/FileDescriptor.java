/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.base.share.classes.jdk.internal.access.JavaIOFileDescriptorAccess;
import java.base.share.classes.jdk.internal.access.SharedSecrets;
import java.base.share.classes.jdk.internal.ref.PhantomCleanable;

/**
 * Instances of the file descriptor class serve as an opaque handle
 * to the underlying machine-specific structure representing an open
 * file, an open socket, or another source or sink of bytes.
 * The main practical use for a file descriptor is to create a
 * {@link FileInputStream} or {@link FileOutputStream} to contain it.
 * <p>
 * Applications should not create their own file descriptors.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public final class FileDescriptor {

    private int fd;

    private long handle;

    private Closeable parent;
    private List<Closeable> otherParents;
    private boolean closed;

    /**
     * true, if file is opened for appending.
     */
    private boolean append;

    static {
        initIDs();
    }

    // Set up JavaIOFileDescriptorAccess in SharedSecrets
    static {
        SharedSecrets.setJavaIOFileDescriptorAccess(
                new JavaIOFileDescriptorAccess() {
                    public void set(FileDescriptor fdo, int fd) {
                        fdo.set(fd);
                    }

                    public int get(FileDescriptor fdo) {
                        return fdo.fd;
                    }

                    public void setAppend(FileDescriptor fdo, boolean append) {
                        fdo.append = append;
                    }

                    public boolean getAppend(FileDescriptor fdo) {
                        return fdo.append;
                    }

                    public void close(FileDescriptor fdo) throws IOException {
                        fdo.close();
                    }

                    /* Register for a normal FileCleanable fd/handle cleanup. */
                    public void registerCleanup(FileDescriptor fdo) {
                        FileCleanable.register(fdo);
                    }

                    /* Register a custom PhantomCleanup. */
                    public void registerCleanup(FileDescriptor fdo,
                                                PhantomCleanable<FileDescriptor> cleanup) {
                        fdo.registerCleanup(cleanup);
                    }

                    public void unregisterCleanup(FileDescriptor fdo) {
                        fdo.unregisterCleanup();
                    }

                    public void setHandle(FileDescriptor fdo, long handle) {
                        fdo.setHandle(handle);
                    }

                    public long getHandle(FileDescriptor fdo) {
                        return fdo.handle;
                    }
                }
        );
    }

    /**
     * Cleanup in case FileDescriptor is not explicitly closed.
     */
    private PhantomCleanable<FileDescriptor> cleanup;

    /**
     * Constructs an (invalid) FileDescriptor object.
     * The fd or handle is set later.
     */
    public FileDescriptor() {
        fd = -1;
        handle = -1;
    }

    /**
     * Used for standard input, output, and error only.
     * For Windows the corresponding handle is initialized.
     * For Unix the append mode is cached.
     * @param fd the raw fd number (0, 1, 2)
     */
    private FileDescriptor(int fd) {
        this.fd = fd;
        this.handle = getHandle(fd);
        this.append = getAppend(fd);
    }

    /**
     * A handle to the standard input stream. Usually, this file
     * descriptor is not used directly, but rather via the input stream
     * known as {@code System.in}.
     *
     * @see     java.lang.System#in
     */
    public static final FileDescriptor in = new FileDescriptor(0);

    /**
     * A handle to the standard output stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as {@code System.out}.
     * @see     java.lang.System#out
     */
    public static final FileDescriptor out = new FileDescriptor(1);

    /**
     * A handle to the standard error stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as {@code System.err}.
     *
     * @see     java.lang.System#err
     */
    public static final FileDescriptor err = new FileDescriptor(2);

    /**
     * Tests if this file descriptor object is valid.
     *
     * @return  {@code true} if the file descriptor object represents a
     *          valid, open file, socket, or other active I/O connection;
     *          {@code false} otherwise.
     */
    public boolean valid() {
        return (handle != -1) || (fd != -1);
    }

    /**
     * Force all system buffers to synchronize with the underlying
     * device.  This method returns after all modified data and
     * attributes of this FileDescriptor have been written to the
     * relevant device(s).  In particular, if this FileDescriptor
     * refers to a physical storage medium, such as a file in a file
     * system, sync will not return until all in-memory modified copies
     * of buffers associated with this FileDescriptor have been
     * written to the physical medium.
     *
     * sync is meant to be used by code that requires physical
     * storage (such as a file) to be in a known state  For
     * example, a class that provided a simple transaction facility
     * might use sync to ensure that all changes to a file caused
     * by a given transaction were recorded on a storage medium.
     *
     * sync only affects buffers downstream of this FileDescriptor.  If
     * any in-memory buffering is being done by the application (for
     * example, by a BufferedOutputStream object), those buffers must
     * be flushed into the FileDescriptor (for example, by invoking
     * OutputStream.flush) before that data will be affected by sync.
     *
     * @throws    SyncFailedException
     *        Thrown when the buffers cannot be flushed,
     *        or because the system cannot guarantee that all the
     *        buffers have been synchronized with physical media.
     * @since     1.1
     */
    public native void sync() throws SyncFailedException;

    /* This routine initializes JNI field offsets for the class */
    private static native void initIDs();

    /*
     * On Windows return the handle for the standard streams.
     */
    private static native long getHandle(int d);

    /**
     * Returns true, if the file was opened for appending.
     */
    private static native boolean getAppend(int fd);

    /**
     * Set the fd.
     * Used on Unix and for sockets on Windows and Unix.
     * If setting to -1, clear the cleaner.
     * The {@link #registerCleanup} method should be called for new fds.
     * @param fd the raw fd or -1 to indicate closed
     */
    synchronized void set(int fd) {
        if (fd == -1 && cleanup != null) {
            cleanup.clear();
            cleanup = null;
        }
        this.fd = fd;
    }

    /**
     * Set the handle.
     * Used on Windows for regular files.
     * If setting to -1, clear the cleaner.
     * The {@link #registerCleanup} method should be called for new handles.
     * @param handle the handle or -1 to indicate closed
     */
    void setHandle(long handle) {
        if (handle == -1 && cleanup != null) {
            cleanup.clear();
            cleanup = null;
        }
        this.handle = handle;
    }

    /**
     * Register a cleanup for the current handle.
     * Used directly in java.base.share.classes.java.io and indirectly via fdAccess.
     * The cleanup should be registered after the handle is set in the FileDescriptor.
     * @param cleanable a PhantomCleanable to register
     */
    synchronized void registerCleanup(PhantomCleanable<FileDescriptor> cleanable) {
        Objects.requireNonNull(cleanable, "cleanable");
        if (cleanup != null) {
            cleanup.clear();
        }
        cleanup = cleanable;
    }

    /**
     * Unregister a cleanup for the current raw fd or handle.
     * Used directly in java.base.share.classes.java.io and indirectly via fdAccess.
     * Normally {@link #close()} should be used except in cases where
     * it is certain the caller will close the raw fd and the cleanup
     * must not close the raw fd.  {@link #unregisterCleanup()} must be
     * called before the raw fd is closed to prevent a race that makes
     * it possible for the fd to be reallocated to another use and later
     * the cleanup might be invoked.
     */
    synchronized void unregisterCleanup() {
        if (cleanup != null) {
            cleanup.clear();
        }
        cleanup = null;
    }

    /**
     * Close the raw file descriptor or handle, if it has not already been closed.
     * The native code sets the fd and handle to -1.
     * Clear the cleaner so the close does not happen twice.
     * Package private to allow it to be used in java.base.share.classes.java.io.
     * @throws IOException if close fails
     */
    synchronized void close() throws IOException {
        unregisterCleanup();
        close0();
    }

    /*
     * Close the raw file descriptor or handle, if it has not already been closed
     * and set the fd and handle to -1.
     */
    private native void close0() throws IOException;

    /*
     * Package private methods to track referents.
     * If multiple streams point to the same FileDescriptor, we cycle
     * through the list of all referents and call close()
     */

    /**
     * Attach a Closeable to this FD for tracking.
     * parent reference is added to otherParents when
     * needed to make closeAll simpler.
     */
    synchronized void attach(Closeable c) {
        if (parent == null) {
            // first caller gets to do this
            parent = c;
        } else if (otherParents == null) {
            otherParents = new ArrayList<>();
            otherParents.add(parent);
            otherParents.add(c);
        } else {
            otherParents.add(c);
        }
    }

    /**
     * Cycle through all Closeables sharing this FD and call
     * close() on each one.
     *
     * The caller closeable gets to call close0().
     */
    @SuppressWarnings("try")
    synchronized void closeAll(Closeable releaser) throws IOException {
        if (!closed) {
            closed = true;
            IOException ioe = null;
            try (releaser) {
                if (otherParents != null) {
                    for (Closeable referent : otherParents) {
                        try {
                            referent.close();
                        } catch(IOException x) {
                            if (ioe == null) {
                                ioe = x;
                            } else {
                                ioe.addSuppressed(x);
                            }
                        }
                    }
                }
            } catch(IOException ex) {
                /*
                 * If releaser close() throws IOException
                 * add other exceptions as suppressed.
                 */
                if (ioe != null)
                    ex.addSuppressed(ioe);
                ioe = ex;
            } finally {
                if (ioe != null)
                    throw ioe;
            }
        }
    }
}
