/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * Thrown when an application tries to access a type using a string
 * representing the type's name, but no definition for the type with
 * the specified name can be found.   This exception differs from
 * {@link ClassNotFoundException} in that {@code ClassNotFoundException} is a
 * checked exception, whereas this exception is unchecked.
 *
 * <p>Note that this exception may be used when undefined type variables
 * are accessed as well as when types (e.g., classes, interfaces or
 * annotation types) are loaded.
 * In particular, this exception can be thrown by the {@linkplain
 * java.base.share.classes.java.lang.reflect.AnnotatedElement API used to read annotations
 * reflectively}.
 * 
 * @see     java.base.share.classes.java.lang.reflect.AnnotatedElement
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class TypeNotPresentException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = -5101214195716534496L;

    /**
     * The type name.
     */
    private String typeName;

    /**
     * Constructs a {@code TypeNotPresentException} for the named type
     * with the specified cause.
     *
     * @param typeName the fully qualified name of the unavailable type
     * @param cause the exception that was thrown when the system attempted to
     *    load the named type, or {@code null} if unavailable or inapplicable
     */
    public TypeNotPresentException(String typeName, Throwable cause) {
        super("Type " + typeName + " not present", cause);
        this.typeName = typeName;
    }

    /**
     * Returns the fully qualified name of the unavailable type.
     *
     * @return the fully qualified name of the unavailable type
     */
    public String typeName() { return typeName;}
}
