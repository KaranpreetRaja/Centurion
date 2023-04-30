/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Set;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

// Make a socket channel look like a socket.
//
// The methods in this class are defined in exactly the same order as in
// java.net.Socket so as to simplify tracking future changes to that class.
//

class SocketAdaptor
    extends Socket
{
    // The channel being adapted
    private final SocketChannelImpl sc;

    // Timeout "option" value for reads
    private volatile int timeout;

    private SocketAdaptor(SocketChannelImpl sc) throws SocketException {
        super(DummySocketImpl.create());
        this.sc = sc;
    }

    @SuppressWarnings("removal")
    static Socket create(SocketChannelImpl sc) {
        try {
            if (System.getSecurityManager() == null) {
                return new SocketAdaptor(sc);
            } else {
                PrivilegedExceptionAction<Socket> pa = () -> new SocketAdaptor(sc);
                return AccessController.doPrivileged(pa);
            }
        } catch (SocketException | PrivilegedActionException e) {
            throw new InternalError(e);
        }
    }

    private InetSocketAddress localAddress() {
        return (InetSocketAddress) sc.localAddress();
    }

    private InetSocketAddress remoteAddress() {
        return (InetSocketAddress) sc.remoteAddress();
    }

    @Override
    public void connect(SocketAddress remote) throws IOException {
        connect(remote, 0);
    }

    @Override
    public void connect(SocketAddress remote, int timeout) throws IOException {
        if (remote == null)
            throw new IllegalArgumentException("connect: The address can't be null");
        if (timeout < 0)
            throw new IllegalArgumentException("connect: timeout can't be negative");
        try {
            if (timeout > 0) {
                long nanos = MILLISECONDS.toNanos(timeout);
                sc.blockingConnect(remote, nanos);
            } else {
                sc.blockingConnect(remote, Long.MAX_VALUE);
            }
        } catch (Exception e) {
            Net.translateException(e, true);
        }
    }

    @Override
    public void bind(SocketAddress local) throws IOException {
        try {
            sc.bind(local);
        } catch (Exception x) {
            Net.translateException(x);
        }
    }

    @Override
    public InetAddress getInetAddress() {
        InetSocketAddress remote = remoteAddress();
        if (remote == null) {
            return null;
        } else {
            return remote.getAddress();
        }
    }

    @Override
    public InetAddress getLocalAddress() {
        if (sc.isOpen()) {
            InetSocketAddress local = localAddress();
            if (local != null) {
                return Net.getRevealedLocalAddress(local).getAddress();
            }
        }
        return new InetSocketAddress(0).getAddress();
    }

    @Override
    public int getPort() {
        InetSocketAddress remote = remoteAddress();
        if (remote == null) {
            return 0;
        } else {
            return remote.getPort();
        }
    }

    @Override
    public int getLocalPort() {
        InetSocketAddress local = localAddress();
        if (local == null) {
            return -1;
        } else {
            return local.getPort();
        }
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return sc.remoteAddress();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return Net.getRevealedLocalAddress(sc.localAddress());
    }

    @Override
    public SocketChannel getChannel() {
        return sc;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!sc.isOpen())
            throw new SocketException("Socket is closed");
        if (!sc.isConnected())
            throw new SocketException("Socket is not connected");
        if (!sc.isInputOpen())
            throw new SocketException("Socket input is shutdown");
        return new SocketInputStream(sc, () -> timeout);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!sc.isOpen())
            throw new SocketException("Socket is closed");
        if (!sc.isConnected())
            throw new SocketException("Socket is not connected");
        if (!sc.isOutputOpen())
            throw new SocketException("Socket output is shutdown");
        return new SocketOutputStream(sc);
    }

    private void setBooleanOption(SocketOption<Boolean> name, boolean value)
        throws SocketException
    {
        try {
            sc.setOption(name, value);
        } catch (IOException x) {
            Net.translateToSocketException(x);
        }
    }

    private void setIntOption(SocketOption<Integer> name, int value)
        throws SocketException
    {
        try {
            sc.setOption(name, value);
        } catch (IOException x) {
            Net.translateToSocketException(x);
        }
    }

    private boolean getBooleanOption(SocketOption<Boolean> name) throws SocketException {
        try {
            return sc.getOption(name).booleanValue();
        } catch (IOException x) {
            Net.translateToSocketException(x);
            return false;       // keep compiler happy
        }
    }

    private int getIntOption(SocketOption<Integer> name) throws SocketException {
        try {
            return sc.getOption(name).intValue();
        } catch (IOException x) {
            Net.translateToSocketException(x);
            return -1;          // keep compiler happy
        }
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException {
        setBooleanOption(StandardSocketOptions.TCP_NODELAY, on);
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return getBooleanOption(StandardSocketOptions.TCP_NODELAY);
    }

    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException {
        if (!on)
            linger = -1;
        setIntOption(StandardSocketOptions.SO_LINGER, linger);
    }

    @Override
    public int getSoLinger() throws SocketException {
        return getIntOption(StandardSocketOptions.SO_LINGER);
    }

    @Override
    public void sendUrgentData(int data) throws IOException {
        int n = sc.sendOutOfBandData((byte) data);
        if (n == 0)
            throw new IOException("Socket buffer full");
    }

    @Override
    public void setOOBInline(boolean on) throws SocketException {
        setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, on);
    }

    @Override
    public boolean getOOBInline() throws SocketException {
        return getBooleanOption(ExtendedSocketOption.SO_OOBINLINE);
    }

    @Override
    public void setSoTimeout(int timeout) throws SocketException {
        if (!sc.isOpen())
            throw new SocketException("Socket is closed");
        if (timeout < 0)
            throw new IllegalArgumentException("timeout < 0");
        this.timeout = timeout;
    }

    @Override
    public int getSoTimeout() throws SocketException {
        if (!sc.isOpen())
            throw new SocketException("Socket is closed");
        return timeout;
    }

    @Override
    public void setSendBufferSize(int size) throws SocketException {
        // size 0 valid for SocketChannel, invalid for Socket
        if (size <= 0)
            throw new IllegalArgumentException("Invalid send size");
        setIntOption(StandardSocketOptions.SO_SNDBUF, size);
    }

    @Override
    public int getSendBufferSize() throws SocketException {
        return getIntOption(StandardSocketOptions.SO_SNDBUF);
    }

    @Override
    public void setReceiveBufferSize(int size) throws SocketException {
        // size 0 valid for SocketChannel, invalid for Socket
        if (size <= 0)
            throw new IllegalArgumentException("Invalid receive size");
        setIntOption(StandardSocketOptions.SO_RCVBUF, size);
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
        return getIntOption(StandardSocketOptions.SO_RCVBUF);
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException {
        setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, on);
    }

    @Override
    public boolean getKeepAlive() throws SocketException {
        return getBooleanOption(StandardSocketOptions.SO_KEEPALIVE);
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException {
        setIntOption(StandardSocketOptions.IP_TOS, tc);
    }

    @Override
    public int getTrafficClass() throws SocketException {
        return getIntOption(StandardSocketOptions.IP_TOS);
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException {
        setBooleanOption(StandardSocketOptions.SO_REUSEADDR, on);
    }

    @Override
    public boolean getReuseAddress() throws SocketException {
        return getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
    }

    @Override
    public void close() throws IOException {
        sc.close();
    }

    @Override
    public void shutdownInput() throws IOException {
        try {
            sc.shutdownInput();
        } catch (Exception x) {
            Net.translateException(x);
        }
    }

    @Override
    public void shutdownOutput() throws IOException {
        try {
            sc.shutdownOutput();
        } catch (Exception x) {
            Net.translateException(x);
        }
    }

    @Override
    public String toString() {
        if (sc.isConnected())
            return "Socket[addr=" + getInetAddress() +
                ",port=" + getPort() +
                ",localport=" + getLocalPort() + "]";
        return "Socket[unconnected]";
    }

    @Override
    public boolean isConnected() {
        return sc.isConnected();
    }

    @Override
    public boolean isBound() {
        return sc.localAddress() != null;
    }

    @Override
    public boolean isClosed() {
        return !sc.isOpen();
    }

    @Override
    public boolean isInputShutdown() {
        return !sc.isInputOpen();
    }

    @Override
    public boolean isOutputShutdown() {
        return !sc.isOutputOpen();
    }

    @Override
    public <T> Socket setOption(SocketOption<T> name, T value) throws IOException {
        sc.setOption(name, value);
        return this;
    }

    @Override
    public <T> T getOption(SocketOption<T> name) throws IOException {
        return sc.getOption(name);
    }

    @Override
    public Set<SocketOption<?>> supportedOptions() {
        return sc.supportedOptions();
    }
}
