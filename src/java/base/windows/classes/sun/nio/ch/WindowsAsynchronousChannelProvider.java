/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.ch;


import java.nio.channels.*;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.io.IOException;

/*
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023 
 */

public class WindowsAsynchronousChannelProvider
    extends AsynchronousChannelProvider
{
    private static volatile Iocp defaultIocp;

    public WindowsAsynchronousChannelProvider() {
        // nothing to do
    }

    private Iocp defaultIocp() throws IOException {
        if (defaultIocp == null) {
            synchronized (WindowsAsynchronousChannelProvider.class) {
                if (defaultIocp == null) {
                    // default thread pool may be shared with AsynchronousFileChannels
                    defaultIocp = new Iocp(this, ThreadPool.getDefault()).start();
                }
            }
        }
        return defaultIocp;
    }

    @Override
    public AsynchronousChannelGroup openAsynchronousChannelGroup(int nThreads, ThreadFactory factory)
        throws IOException
    {
        return new Iocp(this, ThreadPool.create(nThreads, factory)).start();
    }

    @Override
    public AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService executor, int initialSize)
        throws IOException
    {
        return new Iocp(this, ThreadPool.wrap(executor, initialSize)).start();
    }

    private Iocp toIocp(AsynchronousChannelGroup group) throws IOException {
        if (group == null) {
            return defaultIocp();
        } else {
            if (!(group instanceof Iocp))
                throw new IllegalChannelGroupException();
            return (Iocp)group;
        }
    }

    @Override
    public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup group)
        throws IOException
    {
        return new WindowsAsynchronousServerSocketChannelImpl(toIocp(group));
    }

    @Override
    public AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup group)
        throws IOException
    {
        return new WindowsAsynchronousSocketChannelImpl(toIocp(group));
    }
}