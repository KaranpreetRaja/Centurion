/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang.annotation;
import java.lang.reflect.Method;

/**
 * Thrown to indicate that a program has attempted to access an element of
 * an annotation whose type has changed after the annotation was compiled
 * (or serialized).
 * This exception can be thrown by the {@linkplain
 * java.lang.reflect.AnnotatedElement API used to read annotations
 * reflectively}.
 *
 * @see     java.lang.reflect.AnnotatedElement
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class AnnotationTypeMismatchException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 8125925355765570191L;

    /**
     * The {@code Method} object for the annotation element.
     */
    private final transient Method element;

    /**
     * The (erroneous) type of data found in the annotation.  This string
     * may, but is not required to, contain the value as well.  The exact
     * format of the string is unspecified.
     */
    private final String foundType;

    /**
     * Constructs an AnnotationTypeMismatchException for the specified
     * annotation type element and found data type.
     *
     * @param element the {@code Method} object for the annotation
     * element, may be {@code null}
     * @param foundType the (erroneous) type of data found in the annotation.
     *        This string may, but is not required to, contain the value
     *        as well.  The exact format of the string is unspecified,
     *        may be {@code null}.
     */
    public AnnotationTypeMismatchException(Method element, String foundType) {
        super("Incorrectly typed data found for annotation element " + element
              + " (Found data of type " + foundType + ")");
        this.element = element;
        this.foundType = foundType;
    }

    /**
     * Returns the {@code Method} object for the incorrectly typed element.
     * The value may be unavailable if this exception has been
     * serialized and then read back in.
     *
     * @return the {@code Method} object for the incorrectly typed
     * element, or {@code null} if unavailable
     */
    public Method element() {
        return this.element;
    }

    /**
     * Returns the type of data found in the incorrectly typed element.
     * The returned string may, but is not required to, contain the value
     * as well.  The exact format of the string is unspecified and the string
     * may be {@code null}.
     *
     * @return the type of data found in the incorrectly typed element
     */
    public String foundType() {
        return this.foundType;
    }
}
