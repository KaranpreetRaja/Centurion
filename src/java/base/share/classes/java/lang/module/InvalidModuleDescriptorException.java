/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang.module;

/**
 * Thrown when reading a module descriptor and the module descriptor is found
 * to be malformed or otherwise cannot be interpreted as a module descriptor.
 *
 * @see ModuleDescriptor#read
 * @since 9
 */
public class InvalidModuleDescriptorException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 4863390386809347380L;

    /**
     * Constructs an {@code InvalidModuleDescriptorException} with no detail
     * message.
     */
    public InvalidModuleDescriptorException() {
    }

    /**
     * Constructs an {@code InvalidModuleDescriptorException} with the
     * specified detail message.
     *
     * @param msg
     *        The detail message; can be {@code null}
     */
    public InvalidModuleDescriptorException(String msg) {
        super(msg);
    }
}
