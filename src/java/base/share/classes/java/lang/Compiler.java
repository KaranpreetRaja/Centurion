/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.lang;

/**
 * The {@code Compiler} class is provided to support Java-to-native-code
 * compilers and related services. By design, the {@code Compiler} class does
 * nothing; it serves as a placeholder for a JIT compiler implementation.
 * If no compiler is available, these methods do nothing.
 *
 * @deprecated JIT compilers and their technologies vary too widely to
 * be controlled effectively by a standardized interface. As such, many
 * JIT compiler implementations ignore this interface, and are instead
 * controllable by implementation-specific mechanisms such as command-line
 * options. This class is subject to removal in a future version of Java SE.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */

@Deprecated(since="9", forRemoval=true)
public final class Compiler  {
    private Compiler() {}               // don't make instances

    /**
     * Compiles the specified class.
     *
     * @param  clazz
     *         A class
     *
     * @return  {@code true} if the compilation succeeded; {@code false} if the
     *          compilation failed or no compiler is available
     *
     * @throws  NullPointerException
     *          If {@code clazz} is {@code null}
     */
    public static boolean compileClass(Class<?> clazz) {
        return false;
    }

    /**
     * Compiles all classes whose name matches the specified string.
     *
     * @param  string
     *         The name of the classes to compile
     *
     * @return  {@code true} if the compilation succeeded; {@code false} if the
     *          compilation failed or no compiler is available
     *
     * @throws  NullPointerException
     *          If {@code string} is {@code null}
     */
    public static boolean compileClasses(String string) {
        return false;
    }

    /**
     * Examines the argument type and its fields and perform some documented
     * operation.  No specific operations are required.
     *
     * @param  any
     *         An argument
     *
     * @return  A compiler-specific value, or {@code null} if no compiler is
     *          available
     *
     * @throws  NullPointerException
     *          If {@code any} is {@code null}
     */
    public static Object command(Object any) {
        return null;
    }

    /**
     * Cause the Compiler to resume operation.
     */
    public static void enable() { }

    /**
     * Cause the Compiler to cease operation.
     */
    public static void disable() { }
}
