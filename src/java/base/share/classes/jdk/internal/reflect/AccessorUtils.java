/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.reflect;

import java.lang.invoke.WrongMethodTypeException;
import java.util.Set;

/**
 * Utility methods used by DirectMethodHandleAccessor and DirectConstructorHandleAccessor
 */
public class AccessorUtils {
    /**
     * Determines if the given exception thrown by MethodHandle::invokeExact
     * is caused by an illegal argument passed to Method::invoke or
     * Constructor::newInstance.  This method inspects the stack trace of
     * the exception to detect if it is thrown by the method handle core
     * implementation or the implementation of the reflected method or constructor.
     *
     * MethodHandle::invoke throws ClassCastException if the receiver object
     * is not an instance of the declaring class of the method if the method
     * is an instance method, or if a parameter value cannot be converted
     * to the corresponding formal parameter type.  It throws
     * NullPointerException if the receiver object is null if the method
     * is an instance method, or if unboxing operation of a parameter fails
     * because the parameter value is null.  It throws WrongMethodTypeException
     * if the method type mismatches.
     *
     * @param accessorType the accessor class that does the method handle invocation
     * @param e ClassCastException, NullPointerException or WrongMethodTypeException
     */
    static boolean isIllegalArgument(Class<?> accessorType, RuntimeException e) {
        assert(e instanceof ClassCastException || e instanceof NullPointerException ||
               e instanceof WrongMethodTypeException);

        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length == 0) {
            return false;       // would this happen?
        }

        int i = 0;
        StackTraceElement frame = stackTrace[0];
        // Class::cast and Objects::requiresNonNull may be thrown by the implementation
        // of the reflected method/constructor.  Skip them and continue.
        if ((frame.getClassName().equals("java.lang.Class") && frame.getMethodName().equals("cast"))
                || (frame.getClassName().equals("java.util.Objects") && frame.getMethodName().equals("requiresNonNull"))) {
            i++;
        }
        for (; i < stackTrace.length; i++) {
            frame = stackTrace[i];
            String cname = frame.getClassName();
            // it's illegal argument if this exception is thrown from accessorType
            if (cname.equals(accessorType.getName())) {
                return true;
            }
            // if this exception is thrown from an unnamed module or not from java.base
            // then i.e. not from method handle core implementation
            if (frame.getModuleName() == null || !frame.getModuleName().equals("java.base")) {
                return false;
            }
            int index = cname.lastIndexOf(".");
            String pn = index > 0 ? cname.substring(0, index) : "";
            // exception thrown from java.base but not from core reflection/method handle internals
            if (!IMPL_PACKAGES.contains(pn)) {
                return false;
            }
            // If Constructor::newInstance is invoked by Method::invoke or vice versa,
            // so the exception is thrown from the implementation body of the reflected
            // method or constructor
            if ((accessorType == DirectMethodHandleAccessor.class
                    && cname.startsWith(DirectConstructorHandleAccessor.class.getName()))
                || (accessorType == DirectConstructorHandleAccessor.class
                        && cname.startsWith(DirectMethodHandleAccessor.class.getName()))) {
                // thrown from another reflection accessor impl class
                return false;
            }
        }
        return false;
    }

    private static final Set<String> IMPL_PACKAGES = Set.of(
            "java.lang.reflect",
            "java.lang.invoke",
            "java.base.share.classes.jdk.internal.reflect",
            "sun.invoke.util"
    );
}
