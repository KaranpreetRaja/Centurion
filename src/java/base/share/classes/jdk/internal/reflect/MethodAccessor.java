/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.reflect;

import java.lang.reflect.InvocationTargetException;

/** This interface provides the declaration for
    java.lang.reflect.Method.invoke(). Each Method object is
    configured with a (possibly dynamically-generated) class which
    implements this interface.
*/

public interface MethodAccessor {
    /** Matches specification in {@link java.lang.reflect.Method} */
    public Object invoke(Object obj, Object[] args)
        throws IllegalArgumentException, InvocationTargetException;

    public Object invoke(Object obj, Object[] args, Class<?> caller)
            throws IllegalArgumentException, InvocationTargetException;
}
