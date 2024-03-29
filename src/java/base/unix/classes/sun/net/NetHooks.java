/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.unix.classes.sun.net;

import java.net.InetAddress;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Defines static methods to be invoked prior to binding or connecting TCP sockets.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */

public final class NetHooks {

    /**
     * A provider with hooks to allow sockets be converted prior to binding or
     * connecting a TCP socket.
     *
     * <p> Concrete implementations of this class should define a zero-argument
     * constructor and implement the abstract methods specified below.
     */
    public abstract static class Provider {
        /**
         * Initializes a new instance of this class.
         */
        protected Provider() {}

        /**
         * Invoked prior to binding a TCP socket.
         */
        public abstract void implBeforeTcpBind(FileDescriptor fdObj,
                                               InetAddress address,
                                               int port)
            throws IOException;

        /**
         * Invoked prior to connecting an unbound TCP socket.
         */
        public abstract void implBeforeTcpConnect(FileDescriptor fdObj,
                                                 InetAddress address,
                                                 int port)
            throws IOException;
    }

    /**
     * For now, we load the SDP provider on Solaris. In the future this may
     * be changed to use the ServiceLoader facility to allow the deployment of
     * other providers.
     */
    private static final Provider provider = new sun.net.sdp.SdpProvider();

    /**
     * Invoke prior to binding a TCP socket.
     */
    public static void beforeTcpBind(FileDescriptor fdObj,
                                     InetAddress address,
                                     int port)
        throws IOException
    {
        provider.implBeforeTcpBind(fdObj, address, port);
    }

    /**
     * Invoke prior to connecting an unbound TCP socket.
     */
    public static void beforeTcpConnect(FileDescriptor fdObj,
                                        InetAddress address,
                                        int port)
        throws IOException
    {
        provider.implBeforeTcpConnect(fdObj, address, port);
    }
}
