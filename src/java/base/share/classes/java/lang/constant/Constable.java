/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang.constant;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Optional;

/**
 * Represents a type which is <em>constable</em>.  A constable type is one whose
 * values are constants that can be represented in the constant pool of a Java
 * classfile as described in JVMS {@jvms 4.4}, and whose instances can describe themselves
 * nominally as a {@link ConstantDesc}.
 *
 * <p>Some constable types have a native representation in the constant pool:
 * {@link String}, {@link Integer}, {@link Long}, {@link Float},
 * {@link Double}, {@link Class}, {@link MethodType}, and {@link MethodHandle}.
 * The types {@link String}, {@link Integer}, {@link Long}, {@link Float},
 * and {@link Double} serve as their own nominal descriptors; {@link Class},
 * {@link MethodType}, and {@link MethodHandle} have corresponding nominal
 * descriptors {@link ClassDesc}, {@link MethodTypeDesc}, and {@link MethodHandleDesc}.
 *
 * <p>Other reference types can be constable if their instances can describe
 * themselves in nominal form as a {@link ConstantDesc}. Examples in the Java SE
 * Platform API are types that support Java language features such as {@link Enum},
 * and runtime support classes such as {@link VarHandle}.  These are typically
 * described with a {@link DynamicConstantDesc}, which describes dynamically
 * generated constants (JVMS {@jvms 4.4.10}).
 *
 * <p>The nominal form of an instance of a constable type is obtained via
 * {@link #describeConstable()}. A {@linkplain Constable} need
 * not be able to (or may choose not to) describe all its instances in the form of
 * a {@link ConstantDesc}; this method returns an {@link Optional} that can be
 * empty to indicate that a nominal descriptor could not be created for an instance.
 * (For example, {@link MethodHandle} will produce nominal descriptors for direct
 * method handles, but not necessarily those produced by method handle
 * combinators.)
 * @jvms 4.4 The Constant Pool
 * @jvms 4.4.10 The {@code CONSTANT_Dynamic_info} and {@code CONSTANT_InvokeDynamic_info} Structures
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
public interface Constable {
    /**
     * Returns an {@link Optional} containing the nominal descriptor for this
     * instance, if one can be constructed, or an empty {@link Optional}
     * if one cannot be constructed.
     *
     * @return An {@link Optional} containing the resulting nominal descriptor,
     * or an empty {@link Optional} if one cannot be constructed.
     */
    Optional<? extends ConstantDesc> describeConstable();
}
