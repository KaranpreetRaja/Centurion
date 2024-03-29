/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang.annotation;

/**
 * Thrown to indicate that a program has attempted to access an element of
 * an annotation interface that was added to the annotation interface definition
 * after the annotation was compiled (or serialized). This exception will not be
 * thrown if the new element has a default value.
 * This exception can be thrown by the {@linkplain
 * java.lang.reflect.AnnotatedElement API used to read annotations
 * reflectively}.
 *
 * @see     java.lang.reflect.AnnotatedElement
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class IncompleteAnnotationException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 8445097402741811912L;

    /**
     * The annotation interface.
     */
    private Class<? extends Annotation> annotationType;
    /**
     * The element name.
     */
    private String elementName;

    /**
     * Constructs an IncompleteAnnotationException to indicate that
     * the named element was missing from the specified annotation interface.
     *
     * @param annotationType the Class object for the annotation interface
     * @param elementName the name of the missing element
     * @throws NullPointerException if either parameter is {@code null}
     */
    public IncompleteAnnotationException(
            Class<? extends Annotation> annotationType,
            String elementName) {
        super(annotationType.getName() + " missing element " +
              elementName.toString());

        this.annotationType = annotationType;
        this.elementName = elementName;
    }

    /**
     * Returns the Class object for the annotation interface with the
     * missing element.
     *
     * @return the Class object for the annotation interface with the
     *     missing element
     */
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    /**
     * Returns the name of the missing element.
     *
     * @return the name of the missing element
     */
    public String elementName() {
        return elementName;
    }
}
