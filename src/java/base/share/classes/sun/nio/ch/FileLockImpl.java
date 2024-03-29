/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.ch;

import java.io.IOException;
import java.nio.channels.*;

public class FileLockImpl
    extends FileLock
{
    private volatile boolean invalid;

    FileLockImpl(FileChannel channel, long position, long size, boolean shared)
    {
        super(channel, position, size, shared);
    }

    FileLockImpl(AsynchronousFileChannel channel, long position, long size, boolean shared)
    {
        super(channel, position, size, shared);
    }

    public boolean isValid() {
        return !invalid;
    }

    void invalidate() {
        assert Thread.holdsLock(this);
        invalid = true;
    }

    public synchronized void release() throws IOException {
        Channel ch = acquiredBy();
        if (!ch.isOpen())
            throw new ClosedChannelException();
        if (isValid()) {
            if (ch instanceof FileChannelImpl)
                ((FileChannelImpl)ch).release(this);
            else if (ch instanceof AsynchronousFileChannelImpl)
                ((AsynchronousFileChannelImpl)ch).release(this);
            else throw new AssertionError();
            invalidate();
        }
    }
}
