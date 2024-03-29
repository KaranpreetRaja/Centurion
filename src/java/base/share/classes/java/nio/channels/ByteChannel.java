/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.channels;

import java.io.IOException;


/**
 * A channel that can read and write bytes.  This interface simply unifies
 * {@link ReadableByteChannel} and {@link WritableByteChannel}; it does not
 * specify any new operations.
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */

public interface ByteChannel
    extends ReadableByteChannel, WritableByteChannel
{

}
