/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.net;

import java.io.IOException;

/**
 * Thrown to indicate that a malformed URL has occurred. Either no
 * legal protocol could be found in a specification string or the
 * string could not be parsed.
 *
 * @author  Arthur van Hoff
 * @since   1.0
 */
public class MalformedURLException extends IOException {
    @java.io.Serial
    private static final long serialVersionUID = -182787522200415866L;

    /**
     * Constructs a {@code MalformedURLException} with no detail message.
     */
    public MalformedURLException() {
    }

    /**
     * Constructs a {@code MalformedURLException} with the
     * specified detail message.
     *
     * @param   msg   the detail message.
     */
    public MalformedURLException(String msg) {
        super(msg);
    }
}
