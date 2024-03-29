/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang.invoke;

import java.util.List;

/**
 * An entity that has a type descriptor.
 *
 * @since 12
 */
public interface TypeDescriptor {
    /**
     * Returns the descriptor string for this {@code TypeDescriptor} object.
     *
     * If this {@code TypeDescriptor} object can be described in nominal form,
     * then this method returns a type descriptor as specified in JVMS {@jvms 4.3}.
     * The result descriptor string can be used to produce
     * a {@linkplain java.lang.constant.ConstantDesc nominal descriptor}.
     *
     * Otherwise, the result string is not a type descriptor.
     * No {@linkplain java.lang.constant.ConstantDesc nominal descriptor}
     * can be produced from the result string.
     *
     * @return the descriptor string for this {@code TypeDescriptor} object
     * @jvms 4.3.2 Field Descriptors
     * @jvms 4.3.3 Method Descriptors
     */
    String descriptorString();


    /**
     * An entity that has a field type descriptor.
     * Field descriptors conforming to JVMS {@jvms 4.3.2} can be described
     * nominally via {@link Class#describeConstable Class::describeConstable};
     * otherwise they cannot be described nominally.
     *
     * @param <F> the class implementing {@linkplain TypeDescriptor.OfField}
     * @jvms 4.3.2 Field Descriptors
     * @since 12
     */
    interface OfField<F extends TypeDescriptor.OfField<F>> extends TypeDescriptor {
        /**
         * Does this field descriptor describe an array type?
         * @return whether this field descriptor describes an array type
         */
        boolean isArray();

        /**
         * Does this field descriptor describe a primitive type (including void.)
         *
         * @return whether this field descriptor describes a primitive type
         */
        boolean isPrimitive();

        /**
         * If this field descriptor describes an array type, return
         * a descriptor for its component type, otherwise return {@code null}.
         * @return the component type, or {@code null} if this field descriptor does
         * not describe an array type
         */
        F componentType();

        /**
         * Return a descriptor for the array type whose component type is described by this
         * descriptor
         * @return the descriptor for the array type
         */
        F arrayType();
    }


    /**
     * An entity that has a method type descriptor
     * Method descriptors conforming to JVMS {@jvms 4.3.3} can be described
     * nominally via {@link MethodType#describeConstable MethodType::describeConstable};
     * otherwise they cannot be described nominally.
     *
     * @param <F> the type representing field type descriptors
     * @param <M> the class implementing {@linkplain TypeDescriptor.OfMethod}
     * @jvms 4.3.2 Field Descriptors
     * @jvms 4.3.3 Method Descriptors
     * @since 12
     */
    interface OfMethod<F extends TypeDescriptor.OfField<F>, M extends TypeDescriptor.OfMethod<F, M>>
            extends TypeDescriptor {

        /**
         * Return the number of parameters in the method type
         * @return the number of parameters
         */
        int parameterCount();

        /**
         * Return a field descriptor describing the requested parameter of the method type
         * described by this descriptor
         * @param i the index of the parameter
         * @return a field descriptor for the requested parameter type
         * @throws IndexOutOfBoundsException if the index is outside the half-open
         * range {[0, parameterCount)}
         */
        F parameterType(int i);

        /**
         * Return a field descriptor describing the return type of the method type described
         * by this descriptor
         * @return a field descriptor for the return type
         */
        F returnType();

        /**
         * Return an array of field descriptors for the parameter types of the method type
         * described by this descriptor
         * @return field descriptors for the parameter types
         */
        F[] parameterArray();

        /**
         * Return an immutable list of field descriptors for the parameter types of the method type
         * described by this descriptor
         * @return field descriptors for the parameter types
         */
        List<F> parameterList();

        /**
         * Return a method descriptor that is identical to this one, except that the return
         * type has been changed to the specified type
         *
         * @param newReturn a field descriptor for the new return type
         * @throws NullPointerException if any argument is {@code null}
         * @return the new method descriptor
         */
        M changeReturnType(F newReturn);

        /**
         * Return a method descriptor that is identical to this one,
         * except that a single parameter type has been changed to the specified type.
         *
         * @param index the index of the parameter to change
         * @param paramType a field descriptor describing the new parameter type
         * @return the new method descriptor
         * @throws NullPointerException if any argument is {@code null}
         * @throws IndexOutOfBoundsException if the index is outside the half-open
         * range {[0, parameterCount)}
         */
        M changeParameterType(int index, F paramType);

        /**
         * Return a method descriptor that is identical to this one,
         * except that a range of parameter types have been removed.
         *
         * @param start the index of the first parameter to remove
         * @param end the index after the last parameter to remove
         * @return the new method descriptor
         *
         * @throws IndexOutOfBoundsException if {@code start} is outside the half-open
         * range {@code [0, parameterCount)}, or {@code end} is outside the closed range
         * {@code [0, parameterCount]}, or if {@code start > end}
         */
        M dropParameterTypes(int start, int end);

        /**
         * Return a method descriptor that is identical to this one,
         * except that a range of additional parameter types have been inserted.
         *
         * @param pos the index at which to insert the first inserted parameter
         * @param paramTypes field descriptors describing the new parameter types
         *                   to insert
         * @return the new method descriptor
         * @throws NullPointerException if any argument is {@code null}
         * @throws IndexOutOfBoundsException if {@code pos} is outside the closed
         * range {[0, parameterCount]}
         */
        @SuppressWarnings("unchecked")
        M insertParameterTypes(int pos, F... paramTypes);
    }
}
