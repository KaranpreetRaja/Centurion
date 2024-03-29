/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

/**
 * Thrown when the Serialization runtime detects one of the following
 * problems with a Class.
 * <UL>
 * <LI> The serial version of the class does not match that of the class
 *      descriptor read from the stream
 * <LI> The class contains unknown datatypes
 * <LI> The class does not have an accessible no-arg constructor
 * <LI> The ObjectStreamClass of an enum constant does not represent
 *      an enum type
 * <LI> Other conditions given in the <cite>Java Object Serialization
 *      Specification</cite>
 * </UL>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public class InvalidClassException extends ObjectStreamException {

    @java.base.share.classes.java.io.Serial
    private static final long serialVersionUID = -4333316296251054416L;

    /**
     * Name of the invalid class.
     *
     * @serial Name of the invalid class.
     */
    public String classname;

    /**
     * Report an InvalidClassException for the reason specified.
     *
     * @param reason  String describing the reason for the exception.
     */
    public InvalidClassException(String reason) {
        super(reason);
    }

    /**
     * Constructs an InvalidClassException object.
     *
     * @param cname   a String naming the invalid class.
     * @param reason  a String describing the reason for the exception.
     */
    public InvalidClassException(String cname, String reason) {
        super(reason);
        classname = cname;
    }

    /**
     * Report an InvalidClassException for the reason and cause specified.
     *
     * @param reason  String describing the reason for the exception.
     * @param cause the cause
     * @since 19
     */
    public InvalidClassException(String reason, Throwable cause) {
        super(reason, cause);
    }

    /**
     * Report an InvalidClassException for the reason and cause specified.
     *
     * @param cname   a String naming the invalid class.
     * @param reason  String describing the reason for the exception.
     * @param cause the cause
     * @since 19
     */
    public InvalidClassException(String cname, String reason, Throwable cause) {
        super(reason, cause);
        classname = cname;
    }

    /**
     * Produce the message and include the classname, if present.
     */
    @Override
    public String getMessage() {
        if (classname == null)
            return super.getMessage();
        else
            return classname + "; " + super.getMessage();
    }
}
