/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.net;

/**
 * This interface defines a factory for {@code URL} stream
 * protocol handlers.
 *
 * <p> A URL stream handler factory is used as specified in the
 * {@linkplain java.base.share.classes.java.net.URL#URL(String,String,int,String) URL constructor}.
 *
 * @author  Arthur van Hoff
 * @see     java.base.share.classes.java.net.URL
 * @see     java.base.share.classes.java.net.URLStreamHandler
 * @since   1.0
 */
public interface URLStreamHandlerFactory {
    /**
     * Creates a new {@code URLStreamHandler} instance with the specified
     * protocol.
     *
     * @param   protocol   the protocol ("{@code ftp}",
     *                     "{@code http}", "{@code nntp}", etc.).
     * @return  a {@code URLStreamHandler} for the specific protocol, or {@code
     *          null} if this factory cannot create a handler for the specific
     *          protocol
     * @see     java.base.share.classes.java.net.URLStreamHandler
     */
    URLStreamHandler createURLStreamHandler(String protocol);
}
