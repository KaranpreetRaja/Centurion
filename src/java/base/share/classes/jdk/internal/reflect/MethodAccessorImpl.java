/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.reflect;

import java.lang.reflect.InvocationTargetException;

/** <P> Package-private implementation of the MethodAccessor interface
    which has access to all classes and all fields, regardless of
    language restrictions. See MagicAccessor. </P>

    <P> This class is known to the VM; do not change its name without
    also changing the VM's code. </P>

    <P> NOTE: ALL methods of subclasses are skipped during security
    walks up the stack. The assumption is that the only such methods
    that will persistently show up on the stack are the implementing
    methods for java.lang.reflect.Method.invoke(). </P>
*/

abstract class MethodAccessorImpl extends MagicAccessorImpl
    implements MethodAccessor {
    /** Matches specification in {@link java.lang.reflect.Method} */
    public abstract Object invoke(Object obj, Object[] args)
        throws IllegalArgumentException, InvocationTargetException;

    public Object invoke(Object obj, Object[] args, Class<?> caller)
            throws IllegalArgumentException, InvocationTargetException {
        return invoke(obj, args);
    }
}
