/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.BindException;
import java.net.NetPermission;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import sun.nio.fs.AbstractFileSystemProvider;

class UnixDomainSockets {
    private UnixDomainSockets() { }

    private static class UnnamedHolder {
        static final UnixDomainSocketAddress UNNAMED = UnixDomainSocketAddress.of("");
    }

    private static final boolean supported;

    private static final String tempDir = UnixDomainSocketsUtil.getTempDir();

    private static final NetPermission accessUnixDomainSocket =
            new NetPermission("accessUnixDomainSocket");

    static boolean isSupported() {
        return supported;
    }

    static void checkPermission() {
        @SuppressWarnings("removal")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
            sm.checkPermission(accessUnixDomainSocket);
    }

    static UnixDomainSocketAddress getRevealedLocalAddress(SocketAddress sa) {
        UnixDomainSocketAddress addr = (UnixDomainSocketAddress) sa;
        try {
            checkPermission();
            // Security check passed
        } catch (SecurityException e) {
            // Return unnamed address only if security check fails
            addr = unnamed();
        }
        return addr;
    }

    static UnixDomainSocketAddress localAddress(FileDescriptor fd) throws IOException {
        String path = new String(localAddress0(fd), UnixDomainSocketsUtil.getCharset());
        return UnixDomainSocketAddress.of(path);
    }

    private static native byte[] localAddress0(FileDescriptor fd) throws IOException;

    @SuppressWarnings("removal")
    static String getRevealedLocalAddressAsString(SocketAddress sa) {
        return (System.getSecurityManager() != null) ? sa.toString() : "";
    }

    static UnixDomainSocketAddress checkAddress(SocketAddress sa) {
        if (sa == null)
            throw new NullPointerException();
        if (!(sa instanceof UnixDomainSocketAddress))
            throw new UnsupportedAddressTypeException();
        return (UnixDomainSocketAddress) sa;
    }

    static byte[] getPathBytes(Path path) {
        FileSystemProvider provider = FileSystems.getDefault().provider();
        return ((AbstractFileSystemProvider) provider).getSunPathForSocketFile(path);
    }

    static FileDescriptor socket() throws IOException {
        return IOUtil.newFD(socket0());
    }

    static void bind(FileDescriptor fd, Path addr) throws IOException {
        byte[] path = getPathBytes(addr);
        if (path.length == 0) {
            throw new BindException("Server socket cannot bind to unnamed address");
        }
        bind0(fd, path);
    }

    private static Random getRandom() {
        try {
            return SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom(); // This should not fail
        }
    }

    private static final Random random = getRandom();

    /**
     * Return a possible temporary name to bind to, which is different for each call
     * Name is of the form <temp dir>/socket_<random>
     */
    static UnixDomainSocketAddress generateTempName() throws IOException {
        String dir = UnixDomainSockets.tempDir;
        if (dir == null)
            throw new BindException("Could not locate temporary directory for sockets");
        int rnd = random.nextInt(Integer.MAX_VALUE);
        try {
            final Path path = Path.of(dir, "socket_" + rnd);
            if (path.getFileSystem().provider() != sun.nio.fs.DefaultFileSystemProvider.instance()) {
                throw new UnsupportedOperationException(
                        "Unix Domain Sockets not supported on non-default file system");
            }
            return UnixDomainSocketAddress.of(path);
        } catch (InvalidPathException e) {
            throw new BindException("Invalid temporary directory");
        }
    }

    static int connect(FileDescriptor fd, SocketAddress sa) throws IOException {
        return UnixDomainSockets.connect(fd, ((UnixDomainSocketAddress) sa).getPath());
    }

    static int connect(FileDescriptor fd, Path path) throws IOException {
        return connect0(fd, getPathBytes(path));
    }

    static int accept(FileDescriptor fd, FileDescriptor newfd, String[] paths)
        throws IOException
    {
        Object[] array  = new Object[1];
        int n = accept0(fd, newfd, array);
        if (n > 0) {
            byte[] bytes = (byte[]) array[0];
            paths[0] = new String(bytes, UnixDomainSocketsUtil.getCharset());
        }
        return n;
    }

    static UnixDomainSocketAddress unnamed() {
        return UnnamedHolder.UNNAMED;
    }

    private static native boolean init();

    private static native int socket0() throws IOException;

    private static native void bind0(FileDescriptor fd, byte[] path)
        throws IOException;

    private static native int connect0(FileDescriptor fd, byte[] path)
        throws IOException;

    private static native int accept0(FileDescriptor fd, FileDescriptor newfd, Object[] array)
        throws IOException;

    static {
        // Load all required native libs
        IOUtil.load();
        supported = init();
    }
}
