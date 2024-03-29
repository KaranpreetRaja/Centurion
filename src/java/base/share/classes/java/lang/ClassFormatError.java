/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * Thrown when the Java Virtual Machine attempts to read a class
 * file and determines that the file is malformed or otherwise cannot
 * be interpreted as a class file.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class ClassFormatError extends LinkageError {
    @java.io.Serial
    private static final long serialVersionUID = -8420114879011949195L;

    /**
     * Constructs a {@code ClassFormatError} with no detail message.
     */
    public ClassFormatError() {
        super();
    }

    /**
     * Constructs a {@code ClassFormatError} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public ClassFormatError(String s) {
        super(s);
    }
}
