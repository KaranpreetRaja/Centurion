/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * Thrown to indicate that an attempt has been made to store the
 * wrong type of object into an array of objects. For example, the
 * following code generates an {@code ArrayStoreException}:
 * <blockquote><pre>
 *     Object x[] = new String[3];
 *     x[0] = Integer.valueOf(0);
 * </pre></blockquote>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class ArrayStoreException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = -4522193890499838241L;

    /**
     * Constructs an {@code ArrayStoreException} with no detail message.
     */
    public ArrayStoreException() {
        super();
    }

    /**
     * Constructs an {@code ArrayStoreException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public ArrayStoreException(String s) {
        super(s);
    }
}
