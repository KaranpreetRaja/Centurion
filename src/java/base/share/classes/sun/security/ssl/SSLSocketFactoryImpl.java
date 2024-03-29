/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.ssl;

import java.io.*;
import java.net.*;
import javax.net.ssl.SSLSocketFactory;


/**
 * Implementation of an SSL socket factory.  This provides the public
 * hooks to create SSL sockets, using a "high level" programming
 * interface which encapsulates system security policy defaults rather than
 * offering application flexibility.  In particular, it uses a configurable
 * authentication context (and the keys held there) rather than offering
 * any flexibility about which keys to use; that context defaults to the
 * process-default context, but may be explicitly specified.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public final class SSLSocketFactoryImpl extends SSLSocketFactory {

    private final SSLContextImpl context;

    /**
     * Constructor used to instantiate the default factory. This method is
     * only called if the old "ssl.SocketFactory.provider" property in the
     * java.security file is set.
     */
    public SSLSocketFactoryImpl() throws Exception {
        this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
    }

    /**
     * Constructs an SSL socket factory.
     */
    SSLSocketFactoryImpl(SSLContextImpl context) {
        this.context = context;
    }

    /**
     * Creates an unconnected socket.
     *
     * @return the unconnected socket
     * @see java.net.Socket#connect(java.net.SocketAddress, int)
     */
    @Override
    public Socket createSocket() {
        return new SSLSocketImpl(context);
    }

    /**
     * Constructs an SSL connection to a named host at a specified port.
     * This acts as the SSL client, and may authenticate itself or rejoin
     * existing SSL sessions allowed by the authentication context which
     * has been configured.
     *
     * @param host name of the host with which to connect
     * @param port number of the server's port
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return new SSLSocketImpl(context, host, port);
    }

    /**
     * Returns a socket layered over an existing socket to a
     * ServerSocket on the named host, at the given port.  This
     * constructor can be used when tunneling SSL through a proxy. The
     * host and port refer to the logical destination server.  This
     * socket is configured using the socket options established for
     * this factory.
     *
     * @param s the existing socket
     * @param host the server host
     * @param port the server port
     * @param autoClose close the underlying socket when this socket is closed
     *
     * @exception IOException if the connection can't be established
     * @exception UnknownHostException if the host is not known
     */
    @Override
    public Socket createSocket(Socket s, String host, int port,
            boolean autoClose) throws IOException {
        return new SSLSocketImpl(context, s, host, port, autoClose);
    }

    @Override
    public Socket createSocket(Socket s, InputStream consumed,
            boolean autoClose) throws IOException {
        if (s == null) {
            throw new NullPointerException(
                    "the existing socket cannot be null");
        }

        return new SSLSocketImpl(context, s, consumed, autoClose);
    }

    /**
     * Constructs an SSL connection to a server at a specified address
     * and TCP port.  This acts as the SSL client, and may authenticate
     * itself or rejoin existing SSL sessions allowed by the authentication
     * context which has been configured.
     *
     * @param address the server's host
     * @param port its port
     */
    @Override
    public Socket createSocket(InetAddress address, int port)
    throws IOException
    {
        return new SSLSocketImpl(context, address, port);
    }


    /**
     * Constructs an SSL connection to a named host at a specified port.
     * This acts as the SSL client, and may authenticate itself or rejoin
     * existing SSL sessions allowed by the authentication context which
     * has been configured. The socket will also bind() to the local
     * address and port supplied.
     */
    @Override
    public Socket createSocket(String host, int port,
        InetAddress clientAddress, int clientPort)
    throws IOException
    {
        return new SSLSocketImpl(context, host, port,
                clientAddress, clientPort);
    }

    /**
     * Constructs an SSL connection to a server at a specified address
     * and TCP port.  This acts as the SSL client, and may authenticate
     * itself or rejoin existing SSL sessions allowed by the authentication
     * context which has been configured. The socket will also bind() to
     * the local address and port supplied.
     */
    @Override
    public Socket createSocket(InetAddress address, int port,
        InetAddress clientAddress, int clientPort)
    throws IOException
    {
        return new SSLSocketImpl(context, address, port,
                clientAddress, clientPort);
    }


    /**
     * Returns the subset of the supported cipher suites which are
     * enabled by default.  These cipher suites all provide a minimum
     * quality of service whereby the server authenticates itself
     * (preventing person-in-the-middle attacks) and where traffic
     * is encrypted to provide confidentiality.
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return CipherSuite.namesOf(context.getDefaultCipherSuites(false));
    }

    /**
     * Returns the names of the cipher suites which could be enabled for use
     * on an SSL connection.  Normally, only a subset of these will actually
     * be enabled by default, since this list may include cipher suites which
     * do not support the mutual authentication of servers and clients, or
     * which do not protect data confidentiality.  Servers may also need
     * certain kinds of certificates to use certain cipher suites.
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return CipherSuite.namesOf(context.getSupportedCipherSuites());
    }
}
