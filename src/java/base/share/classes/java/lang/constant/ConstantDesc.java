/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang.constant;

import java.lang.Enum.EnumDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle.VarHandleDesc;

/**
 * A <a href="package-summary.html#nominal">nominal descriptor</a> for a loadable
 * constant value, as defined in JVMS {@jvms 4.4}. Such a descriptor can be resolved via
 * {@link ConstantDesc#resolveConstantDesc(MethodHandles.Lookup)} to yield the
 * constant value itself.
 *
 * <p>Class names in a nominal descriptor, like class names in the constant pool
 * of a classfile, must be interpreted with respect to a particular class
 * loader, which is not part of the nominal descriptor.
 *
 * <p>Static constants that are expressible natively in the constant pool ({@link String},
 * {@link Integer}, {@link Long}, {@link Float}, and {@link Double}) implement
 * {@link ConstantDesc}, and serve as nominal descriptors for themselves.
 * Native linkable constants ({@link Class}, {@link MethodType}, and
 * {@link MethodHandle}) have counterpart {@linkplain ConstantDesc} types:
 * {@link ClassDesc}, {@link MethodTypeDesc}, and {@link MethodHandleDesc}.
 * Other constants are represented by subtypes of {@link DynamicConstantDesc}.
 *
 * <p>APIs that perform generation or parsing of bytecode are encouraged to use
 * {@linkplain ConstantDesc} to describe the operand of an {@code ldc} instruction
 * (including dynamic constants), the static bootstrap arguments of
 * dynamic constants and {@code invokedynamic} instructions, and other
 * bytecodes or classfile structures that make use of the constant pool.
 *
 * <p>Constants describing various common constants (such as {@link ClassDesc}
 * instances for platform types) can be found in {@link ConstantDescs}.
 *
 * <p>Implementations of {@linkplain ConstantDesc} should be immutable
 * and their behavior should not rely on object identity.
 *
 * <p>Non-platform classes should not implement {@linkplain ConstantDesc} directly.
 * Instead, they should extend {@link DynamicConstantDesc} (as {@link EnumDesc}
 * and {@link VarHandleDesc} do.)
 *
 * <p>Nominal descriptors should be compared using the
 * {@link Object#equals(Object)} method. There is no guarantee that any
 * particular entity will always be represented by the same descriptor instance.
 *
 * @see Constable
 * @see ConstantDescs
 *
 * @jvms 4.4 The Constant Pool
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 * @sealedGraph
 */
public sealed interface ConstantDesc
        permits ClassDesc,
                MethodHandleDesc,
                MethodTypeDesc,
                Double,
                DynamicConstantDesc,
                Float,
                Integer,
                Long,
                String {
    /**
     * Resolves this descriptor reflectively, emulating the resolution behavior
     * of JVMS {@jvms 5.4.3} and the access control behavior of JVMS {@jvms 5.4.4}.  The resolution
     * and access control context is provided by the {@link MethodHandles.Lookup}
     * parameter.  No caching of the resulting value is performed.
     *
     * @param lookup The {@link MethodHandles.Lookup} to provide name resolution
     *               and access control context
     * @return the resolved constant value
     * @throws ReflectiveOperationException if a class, method, or field
     * could not be reflectively resolved in the course of resolution
     * @throws LinkageError if a linkage error occurs
     *
     * @apiNote {@linkplain MethodTypeDesc} can represent method type descriptors
     * that are not representable by {@linkplain MethodType}, such as methods with
     * more than 255 parameter slots, so attempts to resolve these may result in errors.
     *
     * @jvms 5.4.3 Resolution
     * @jvms 5.4.4 Access Control
     */
    Object resolveConstantDesc(MethodHandles.Lookup lookup) throws ReflectiveOperationException;
}
