/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.access;

import java.lang.reflect.*;
import jdk.internal.reflect.*;

/** An interface which gives privileged packages Java-level access to
    internals of java.lang.reflect. */

public interface JavaLangReflectAccess {
    /** Creates a new java.lang.reflect.Constructor. Access checks as
      per java.lang.reflect.AccessibleObject are not overridden. */
    public <T> Constructor<T> newConstructor(Class<T> declaringClass,
                                             Class<?>[] parameterTypes,
                                             Class<?>[] checkedExceptions,
                                             int modifiers,
                                             int slot,
                                             String signature,
                                             byte[] annotations,
                                             byte[] parameterAnnotations);

    /** Gets the MethodAccessor object for a java.lang.reflect.Method */
    public MethodAccessor getMethodAccessor(Method m);

    /** Sets the MethodAccessor object for a java.lang.reflect.Method */
    public void setMethodAccessor(Method m, MethodAccessor accessor);

    /** Gets the ConstructorAccessor object for a
        java.lang.reflect.Constructor */
    public ConstructorAccessor getConstructorAccessor(Constructor<?> c);

    /** Sets the ConstructorAccessor object for a
        java.lang.reflect.Constructor */
    public void setConstructorAccessor(Constructor<?> c,
                                       ConstructorAccessor accessor);

    /** Gets the byte[] that encodes TypeAnnotations on an Executable. */
    public byte[] getExecutableTypeAnnotationBytes(Executable ex);

    /** Gets the "slot" field from a Constructor (used for serialization) */
    public int getConstructorSlot(Constructor<?> c);

    /** Gets the "signature" field from a Constructor (used for serialization) */
    public String getConstructorSignature(Constructor<?> c);

    /** Gets the "annotations" field from a Constructor (used for serialization) */
    public byte[] getConstructorAnnotations(Constructor<?> c);

    /** Gets the "parameterAnnotations" field from a Constructor (used for serialization) */
    public byte[] getConstructorParameterAnnotations(Constructor<?> c);

    /** Gets the shared array of parameter types of an Executable. */
    public Class<?>[] getExecutableSharedParameterTypes(Executable ex);

    //
    // Copying routines, needed to quickly fabricate new Field,
    // Method, and Constructor objects from templates
    //

    /** Makes a "child" copy of a Method */
    public Method      copyMethod(Method arg);

    /** Makes a copy of this non-root a Method */
    public Method      leafCopyMethod(Method arg);

    /** Makes a "child" copy of a Field */
    public Field       copyField(Field arg);

    /** Makes a "child" copy of a Constructor */
    public <T> Constructor<T> copyConstructor(Constructor<T> arg);

    /** Gets the root of the given AccessibleObject object; null if arg is the root */
    public <T extends AccessibleObject> T getRoot(T obj);

    /** Tests if this is a trusted final field */
    public boolean isTrustedFinalField(Field f);

    /** Returns a new instance created by the given constructor with access check */
    public <T> T newInstance(Constructor<T> ctor, Object[] args, Class<?> caller)
        throws IllegalAccessException, InstantiationException, InvocationTargetException;

    /** Invokes the given default method if the method's declaring interface is
     *  accessible to the given caller.  Otherwise, IllegalAccessException will
     *  be thrown.  If the caller is null, no access check is performed.
     */
    public Object invokeDefault(Object proxy, Method method, Object[] args, Class<?> caller)
        throws Throwable;
}
