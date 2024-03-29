/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.net.ext;

import java.io.FileDescriptor;
import java.net.SocketException;
import java.net.SocketOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines the infrastructure to support extended socket options, beyond those
 * defined in {@link java.net.StandardSocketOptions}.
 *
 * Extended socket options are accessed through the jdk.net API, which is in
 * the jdk.net module.
 */
public abstract class ExtendedSocketOptions {

    public static final short SOCK_STREAM = 1;
    public static final short SOCK_DGRAM = 2;

    private final Set<SocketOption<?>> options;
    private final Set<SocketOption<?>> datagramOptions;
    private final Set<SocketOption<?>> clientStreamOptions;
    private final Set<SocketOption<?>> serverStreamOptions;
    private final Set<SocketOption<?>> unixDomainClientOptions;

    /** Tells whether or not the option is supported. */
    public final boolean isOptionSupported(SocketOption<?> option) {
        return options().contains(option);
    }

    /** Return the, possibly empty, set of extended socket options available. */
    public final Set<SocketOption<?>> options() { return options; }

    /**
     * Returns the (possibly empty) set of extended socket options for
     * stream-oriented listening sockets.
     */
    public static Set<SocketOption<?>> serverSocketOptions() {
        return getInstance().options0(SOCK_STREAM, true);
    }

    /**
     * Returns the (possibly empty) set of extended socket options for
     * stream-oriented connecting sockets.
     */
    public static Set<SocketOption<?>> clientSocketOptions() {
        return getInstance().options0(SOCK_STREAM, false);
    }

    /**
     * Return the, possibly empty, set of extended socket options available for
     * Unix domain client sockets. Note, there are no extended
     * Unix domain server options.
     */
    private final Set<SocketOption<?>> unixDomainClientOptions() {
        return unixDomainClientOptions;
    }

    public static Set<SocketOption<?>> unixDomainSocketOptions() {
        return getInstance().unixDomainClientOptions();
    }

    /**
     * Returns the (possibly empty) set of extended socket options for
     * datagram-oriented sockets.
     */
    public static Set<SocketOption<?>> datagramSocketOptions() {
        return getInstance().options0(SOCK_DGRAM, false);
    }

    private static boolean isDatagramOption(SocketOption<?> option) {
        if (option.name().startsWith("TCP_") || isUnixDomainOption(option)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isUnixDomainOption(SocketOption<?> option) {
        return option.name().equals("SO_PEERCRED");
    }

    private static boolean isStreamOption(SocketOption<?> option, boolean server) {
        if (option.name().startsWith("UDP_") || isUnixDomainOption(option)
            || option.name().equals("IP_DONTFRAGMENT")) {
            return false;
        } else {
            return true;
        }
    }

    private Set<SocketOption<?>> options0(short type, boolean server) {
        switch (type) {
            case SOCK_DGRAM:
                return datagramOptions;
            case SOCK_STREAM:
                if (server) {
                    return serverStreamOptions;
                } else {
                    return clientStreamOptions;
                }
            default:
                //this will never happen
                throw new IllegalArgumentException("Invalid socket option type");
        }
    }

    /** Sets the value of a socket option, for the given socket. */
    public abstract void setOption(FileDescriptor fd, SocketOption<?> option, Object value, boolean isIPv6)
            throws SocketException;

    /** Returns the value of a socket option, for the given socket. */
    public abstract Object getOption(FileDescriptor fd, SocketOption<?> option, boolean isIPv6)
            throws SocketException;

    protected ExtendedSocketOptions(Set<SocketOption<?>> options) {
        this.options = options;
        var datagramOptions = new HashSet<SocketOption<?>>();
        var serverStreamOptions = new HashSet<SocketOption<?>>();
        var clientStreamOptions = new HashSet<SocketOption<?>>();
        var unixDomainClientOptions = new HashSet<SocketOption<?>>();
        for (var option : options) {
            if (isDatagramOption(option)) {
                datagramOptions.add(option);
            }
            if (isStreamOption(option, true)) {
                serverStreamOptions.add(option);
            }
            if (isStreamOption(option, false)) {
                clientStreamOptions.add(option);
            }
            if (isUnixDomainOption(option)) {
                unixDomainClientOptions.add(option);
            }
        }
        this.datagramOptions = Set.copyOf(datagramOptions);
        this.serverStreamOptions = Set.copyOf(serverStreamOptions);
        this.clientStreamOptions = Set.copyOf(clientStreamOptions);
        this.unixDomainClientOptions = Set.copyOf(unixDomainClientOptions);
    }

    private static volatile ExtendedSocketOptions instance;

    public static ExtendedSocketOptions getInstance() {
        ExtendedSocketOptions ext = instance;
        if (ext != null) {
            return ext;
        }
        try {
            // If the class is present, it will be initialized which
            // triggers registration of the extended socket options.
            Class<?> c = Class.forName("jdk.net.ExtendedSocketOptions");
            ext = instance;
        } catch (ClassNotFoundException e) {
            synchronized (ExtendedSocketOptions.class) {
                ext = instance;
                if (ext != null) {
                    return ext;
                }
                // the jdk.net module is not present => no extended socket options
                ext = instance = new NoExtendedSocketOptions();
            }
        }
        return ext;
    }

    /** Registers support for extended socket options. Invoked by the jdk.net module. */
    public static synchronized void register(ExtendedSocketOptions extOptions) {
        if (instance != null)
            throw new InternalError("Attempting to reregister extended options");

        instance = extOptions;
    }

    static final class NoExtendedSocketOptions extends ExtendedSocketOptions {

        NoExtendedSocketOptions() {
            super(Collections.<SocketOption<?>>emptySet());
        }

        @Override
        public void setOption(FileDescriptor fd, SocketOption<?> option, Object value, boolean isIPv6)
            throws SocketException
        {
            throw new UnsupportedOperationException(
                    "no extended options: " + option.name());
        }

        @Override
        public Object getOption(FileDescriptor fd, SocketOption<?> option, boolean isIPv6)
            throws SocketException
        {
            throw new UnsupportedOperationException(
                    "no extended options: " + option.name());
        }
    }
}
