/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * Thrown to indicate that an assertion has failed.
 *
 * <p>The seven one-argument public constructors provided by this
 * class ensure that the assertion error returned by the invocation:
 * <pre>
 *     new AssertionError(<i>expression</i>)
 * </pre>
 * has as its detail message the <i>string conversion</i> of
 * <i>expression</i> (as defined in section {@jls 5.1.11} of
 * <cite>The Java Language Specification</cite>),
 * regardless of the type of <i>expression</i>.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */

public class AssertionError extends Error {
    @java.io.Serial
    private static final long serialVersionUID = -5013299493970297370L;

    /**
     * Constructs an AssertionError with no detail message.
     */
    public AssertionError() {
    }

    /**
     * This internal constructor does no processing on its string argument,
     * even if it is a null reference.  The public constructors will
     * never call this constructor with a null argument.
     */
    private AssertionError(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified object, which is converted to a string as
     * defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *<p>
     * If the specified object is an instance of {@code Throwable}, it
     * becomes the <i>cause</i> of the newly constructed assertion error.
     *
     * @param detailMessage value to be used in constructing detail message
     * @see   Throwable#getCause()
     */
    public AssertionError(Object detailMessage) {
        this(String.valueOf(detailMessage));
        if (detailMessage instanceof Throwable)
            initCause((Throwable) detailMessage);
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified {@code boolean}, which is converted to
     * a string as defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *
     * @param detailMessage value to be used in constructing detail message
     */
    public AssertionError(boolean detailMessage) {
        this(String.valueOf(detailMessage));
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified {@code char}, which is converted to a
     * string as defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *
     * @param detailMessage value to be used in constructing detail message
     */
    public AssertionError(char detailMessage) {
        this(String.valueOf(detailMessage));
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified {@code int}, which is converted to a
     * string as defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *
     * @param detailMessage value to be used in constructing detail message
     */
    public AssertionError(int detailMessage) {
        this(String.valueOf(detailMessage));
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified {@code long}, which is converted to a
     * string as defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *
     * @param detailMessage value to be used in constructing detail message
     */
    public AssertionError(long detailMessage) {
        this(String.valueOf(detailMessage));
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified {@code float}, which is converted to a
     * string as defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *
     * @param detailMessage value to be used in constructing detail message
     */
    public AssertionError(float detailMessage) {
        this(String.valueOf(detailMessage));
    }

    /**
     * Constructs an AssertionError with its detail message derived
     * from the specified {@code double}, which is converted to a
     * string as defined in section {@jls 5.1.11} of
     * <cite>The Java Language Specification</cite>.
     *
     * @param detailMessage value to be used in constructing detail message
     */
    public AssertionError(double detailMessage) {
        this(String.valueOf(detailMessage));
    }

    /**
     * Constructs a new {@code AssertionError} with the specified
     * detail message and cause.
     *
     * <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this error's detail message.
     *
     * @param  message the detail message, may be {@code null}
     * @param  cause the cause, may be {@code null}
     *
     * @since 1.7
     */
    public AssertionError(String message, Throwable cause) {
        super(message, cause);
    }
}
