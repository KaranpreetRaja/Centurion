/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * A class implements the {@code Cloneable} interface to
 * indicate to the {@link java.base.share.classes.java.lang.Object#clone()} method that it
 * is legal for that method to make a
 * field-for-field copy of instances of that class.
 * <p>
 * Invoking Object's clone method on an instance that does not implement the
 * {@code Cloneable} interface results in the exception
 * {@code CloneNotSupportedException} being thrown.
 * <p>
 * By convention, classes that implement this interface should override
 * {@code Object.clone} (which is protected) with a public method.
 * See {@link java.base.share.classes.java.lang.Object#clone()} for details on overriding this
 * method.
 * <p>
 * Note that this interface does <i>not</i> contain the {@code clone} method.
 * Therefore, it is not possible to clone an object merely by virtue of the
 * fact that it implements this interface.  Even if the clone method is invoked
 * reflectively, there is no guarantee that it will succeed.
 *
 * @see     java.base.share.classes.java.lang.CloneNotSupportedException
 * @see     java.base.share.classes.java.lang.Object#clone()
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public interface Cloneable {
}
