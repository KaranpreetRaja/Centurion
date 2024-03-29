/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang.constant;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

import static java.base.share.classes.java.lang.constant.ConstantDescs.CD_void;
import static java.base.share.classes.java.lang.constant.ConstantUtils.validateClassOrInterface;
import static java.base.share.classes.java.lang.constant.ConstantUtils.validateMemberName;
import static java.base.share.classes.java.lang.constant.DirectMethodHandleDesc.Kind.CONSTRUCTOR;
import static java.util.Objects.requireNonNull;

/**
 * A <a href="package-summary.html#nominal">nominal descriptor</a> for a direct
 * {@link MethodHandle}.  A {@linkplain DirectMethodHandleDescImpl} corresponds to
 * a {@code Constant_MethodHandle_info} entry in the constant pool of a classfile.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
final class DirectMethodHandleDescImpl implements DirectMethodHandleDesc {

    private final Kind kind;
    private final ClassDesc owner;
    private final String name;
    private final MethodTypeDesc invocationType;

    /**
     * Constructs a {@linkplain DirectMethodHandleDescImpl} for a method or field
     * from a kind, owner, name, and type
     *
     * @param kind the kind of the method handle
     * @param owner the declaring class or interface for the method
     * @param name the unqualified name of the method (ignored if {@code kind} is {@code CONSTRUCTOR})
     * @param type the lookup type of the method
     * @throws NullPointerException if any non-ignored argument is null
     * @throws IllegalArgumentException if {@code kind} describes a field accessor,
     * and {@code type} is not consistent with that kind of field accessor, or if
     * {@code kind} describes a constructor, and the return type of {@code type}
     * is not {@code void}
     * @jvms 4.2.2 Unqualified Names
     */
    DirectMethodHandleDescImpl(Kind kind, ClassDesc owner, String name, MethodTypeDesc type) {
        if (kind == CONSTRUCTOR)
            name = "<init>";

        requireNonNull(kind);
        validateClassOrInterface(requireNonNull(owner));
        validateMemberName(requireNonNull(name), true);
        requireNonNull(type);

        switch (kind) {
            case CONSTRUCTOR   -> validateConstructor(type);
            case GETTER        -> validateFieldType(type, false, true);
            case SETTER        -> validateFieldType(type, true, true);
            case STATIC_GETTER -> validateFieldType(type, false, false);
            case STATIC_SETTER -> validateFieldType(type, true, false);
        }

        this.kind = kind;
        this.owner = owner;
        this.name = name;
        if (kind.isVirtualMethod())
            this.invocationType = type.insertParameterTypes(0, owner);
        else if (kind == CONSTRUCTOR)
            this.invocationType = type.changeReturnType(owner);
        else
            this.invocationType = type;
    }

    private static void validateFieldType(MethodTypeDesc type, boolean isSetter, boolean isVirtual) {
        boolean isVoid = type.returnType().descriptorString().equals("V");
        int expectedParams = (isSetter ? 1 : 0) + (isVirtual ? 1 : 0);
        if (isVoid != isSetter
            || type.parameterCount() != expectedParams
            || (isVirtual && type.parameterType(0).isPrimitive())) {
            String expectedType = String.format("(%s%s)%s", (isVirtual ? "R" : ""),
                                                (isSetter ? "T" : ""), (isSetter ? "V" : "T"));
            throw new IllegalArgumentException(String.format("Expected type of %s for getter, found %s", expectedType, type));
        }
    }

    private static void validateConstructor(MethodTypeDesc type) {
        if (!type.returnType().descriptorString().equals("V")) {
            throw new IllegalArgumentException(String.format("Expected type of (T*)V for constructor, found %s", type));
        }
    }

    @Override
    public Kind kind() { return kind; }

    @Override
    public int refKind() { return kind.refKind; }

    @Override
    public boolean isOwnerInterface() { return kind.isInterface; }

    @Override
    public ClassDesc owner() {
        return owner;
    }

    @Override
    public String methodName() {
        return name;
    }

    @Override
    public MethodTypeDesc invocationType() {
        return invocationType;
    }

    @Override
    public String lookupDescriptor() {
        return switch (kind) {
            case VIRTUAL,
                 SPECIAL,
                 INTERFACE_VIRTUAL,
                 INTERFACE_SPECIAL        -> invocationType.dropParameterTypes(0, 1).descriptorString();
            case STATIC,
                 INTERFACE_STATIC         -> invocationType.descriptorString();
            case CONSTRUCTOR              -> invocationType.changeReturnType(CD_void).descriptorString();
            case GETTER,
                 STATIC_GETTER            -> invocationType.returnType().descriptorString();
            case SETTER                   -> invocationType.parameterType(1).descriptorString();
            case STATIC_SETTER            -> invocationType.parameterType(0).descriptorString();
            default -> throw new IllegalStateException(kind.toString());
        };
    }

    public MethodHandle resolveConstantDesc(MethodHandles.Lookup lookup)
            throws ReflectiveOperationException {
        Class<?> resolvedOwner = (Class<?>) owner.resolveConstantDesc(lookup);
        MethodType invocationType = (MethodType) this.invocationType().resolveConstantDesc(lookup);
        return switch (kind) {
            case STATIC,
                 INTERFACE_STATIC           -> lookup.findStatic(resolvedOwner, name, invocationType);
            case VIRTUAL,
                 INTERFACE_VIRTUAL          -> lookup.findVirtual(resolvedOwner, name, invocationType.dropParameterTypes(0, 1));
            case SPECIAL,
                 INTERFACE_SPECIAL          -> lookup.findSpecial(resolvedOwner, name, invocationType.dropParameterTypes(0, 1), lookup.lookupClass());
            case CONSTRUCTOR                -> lookup.findConstructor(resolvedOwner, invocationType.changeReturnType(void.class));
            case GETTER                     -> lookup.findGetter(resolvedOwner, name, invocationType.returnType());
            case STATIC_GETTER              -> lookup.findStaticGetter(resolvedOwner, name, invocationType.returnType());
            case SETTER                     -> lookup.findSetter(resolvedOwner, name, invocationType.parameterType(1));
            case STATIC_SETTER              -> lookup.findStaticSetter(resolvedOwner, name, invocationType.parameterType(0));
            default -> throw new IllegalStateException(kind.name());
        };
    }

    /**
     * Returns {@code true} if this {@linkplain DirectMethodHandleDescImpl} is
     * equal to another {@linkplain DirectMethodHandleDescImpl}.  Equality is
     * determined by the two descriptors having equal kind, owner, name, and type
     * descriptor.
     * @param o a {@code DirectMethodHandleDescImpl} to compare to this
     *       {@code DirectMethodHandleDescImpl}
     * @return {@code true} if the specified {@code DirectMethodHandleDescImpl}
     *      is equal to this {@code DirectMethodHandleDescImpl}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectMethodHandleDescImpl desc = (DirectMethodHandleDescImpl) o;
        return kind == desc.kind &&
               Objects.equals(owner, desc.owner) &&
               Objects.equals(name, desc.name) &&
               Objects.equals(invocationType, desc.invocationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, owner, name, invocationType);
    }

    @Override
    public String toString() {
        return String.format("MethodHandleDesc[%s/%s::%s%s]", kind, owner.displayName(), name, invocationType.displayDescriptor());
    }
}
