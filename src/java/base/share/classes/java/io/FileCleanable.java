/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

import java.base.share.classes.jdk.internal.access.JavaIOFileDescriptorAccess;
import java.base.share.classes.jdk.internal.access.SharedSecrets;
import java.base.share.classes.jdk.internal.ref.CleanerFactory;
import java.base.share.classes.jdk.internal.ref.PhantomCleanable;

import java.lang.ref.Cleaner;

/**
 * Cleanable for a FileDescriptor when it becomes phantom reachable.
 * For regular fds on Unix and regular handles on Windows
 * register a cleanup if fd != -1 or handle != -1.
 * <p>
 * Subclassed from {@code PhantomCleanable} so that {@code clear} can be
 * called to disable the cleanup when the handle is closed by any means other
 * than calling {@link FileDescriptor#close}.
 * Otherwise, it might incorrectly close the handle after it has been reused.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
final class FileCleanable extends PhantomCleanable<FileDescriptor> {

    // Access to FileDescriptor private fields;
    // avoids making fd and handle package private
    private static final JavaIOFileDescriptorAccess fdAccess =
            SharedSecrets.getJavaIOFileDescriptorAccess();

    /*
     * Raw close of the file fd and/or handle.
     * Used only for last chance cleanup.
     */
    private static native void cleanupClose0(int fd, long handle) throws IOException;

    // The raw fd to close
    private final int fd;

    // The handle to close
    private final long handle;

    /**
     * Register a Cleanable with the FileDescriptor
     * if the FileDescriptor is non-null and valid.
     * @implNote
     * An exception (OutOfMemoryException) will leave the FileDescriptor
     * having allocated resources and leak the fd/handle.
     *
     * @param fdo the FileDescriptor; may be null
     */
    static void register(FileDescriptor fdo) {
        if (fdo != null && fdo.valid()) {
            int fd = fdAccess.get(fdo);
            long handle = fdAccess.getHandle(fdo);
            fdo.registerCleanup(new FileCleanable(fdo, CleanerFactory.cleaner(), fd, handle));
        }
    }

    /**
     * Unregister a Cleanable from the FileDescriptor.
     * @param fdo the FileDescriptor; may be null
     */
    static void unregister(FileDescriptor fdo) {
        if (fdo != null) {
            fdo.unregisterCleanup();
        }
    }

    /**
     * Constructor for a phantom cleanable reference.
     *
     * @param obj     the object to monitor
     * @param cleaner the cleaner
     * @param fd      file descriptor to close
     * @param handle  handle to close
     */
    private FileCleanable(FileDescriptor obj, Cleaner cleaner, int fd, long handle) {
        super(obj, cleaner);
        this.fd = fd;
        this.handle = handle;
    }

    /**
     * Close the native handle or fd.
     */
    @Override
    protected void performCleanup() {
        try {
            cleanupClose0(fd, handle);
        } catch (IOException ioe) {
            throw new UncheckedIOException("close", ioe);
        }
    }
}
