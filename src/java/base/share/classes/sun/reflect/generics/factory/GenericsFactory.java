/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.reflect.generics.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.base.share.classes.sun.reflect.generics.tree.FieldTypeSignature;

/**
 * A factory interface for reflective objects representing generic types.
 * Implementors (such as core reflection or JDI, or possibly javadoc
 * will manufacture instances of (potentially) different classes
 * in response to invocations of the methods described here.
 * <p> The intent is that reflective systems use these factories to
 * produce generic type information on demand.
 * Certain components of such reflective systems can be independent
 * of a specific implementation by using this interface. For example,
 * repositories of generic type information are initialized with a
 * factory conforming to this interface, and use it to generate the
 * type information they are required to provide. As a result, such
 * repository code can be shared across different reflective systems.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */
public interface GenericsFactory {
    /**
     * Returns a new type variable declaration. Note that {@code name}
     * may be empty (but not {@code null}). If {@code bounds} is
     * empty, a bound of {@code java.lang.Object} is used.
     * @param name The name of the type variable
     * @param bounds An array of abstract syntax trees representing
     * the upper bound(s) on the type variable being declared
     * @return a new type variable declaration
     * @throws NullPointerException if any of the actual parameters
     * or any of the elements of {@code bounds} are {@code null}.
     */
    TypeVariable<?> makeTypeVariable(String name,
                                     FieldTypeSignature[] bounds);
    /**
     * Returns an instance of the {@code ParameterizedType} interface
     * that corresponds to a generic type instantiation of the
     * generic declaration {@code declaration} with actual type arguments
     * {@code typeArgs}.
     * If {@code owner} is {@code null}, the declaring class of
     * {@code declaration} is used as the owner of this parameterized
     * type.
     * <p> This method throws a MalformedParameterizedTypeException
     * under the following circumstances:
     * If the type declaration does not represent a generic declaration
     * (i.e., it is not an instance of {@code GenericDeclaration}).
     * If the number of actual type arguments (i.e., the size of the
     * array {@code typeArgs}) does not correspond to the number of
     * formal type arguments.
     * If any of the actual type arguments is not an instance of the
     * bounds on the corresponding formal.
     * @param declaration - the generic type declaration that is to be
     * instantiated
     * @param typeArgs - the list of actual type arguments
     * @return - a parameterized type representing the instantiation
     * of the declaration with the actual type arguments
     * @throws MalformedParameterizedTypeException if the instantiation
     * is invalid
     * @throws NullPointerException if any of {@code declaration},
     * {@code typeArgs}
     * or any of the elements of {@code typeArgs} are {@code null}
     */
    ParameterizedType makeParameterizedType(Type declaration,
                                            Type[] typeArgs,
                                            Type owner);

    /**
     * Returns the type variable with name {@code name}, if such
     * a type variable is declared in the
     * scope used to create this factory.
     * Returns {@code null} otherwise.
     * @param name - the name of the type variable to search for
     * @return - the type variable with name {@code name}, or {@code null}
     * @throws  NullPointerException if any of actual parameters are
     * {@code null}
     */
    TypeVariable<?> findTypeVariable(String name);

    /**
     * Returns a new wildcard type variable. If
     * {@code ubs} is empty, a bound of {@code java.lang.Object} is used.
     * @param ubs An array of abstract syntax trees representing
     * the upper bound(s) on the type variable being declared
     * @param lbs An array of abstract syntax trees representing
     * the lower bound(s) on the type variable being declared
     * @return a new wildcard type variable
     * @throws NullPointerException if any of the actual parameters
     * or any of the elements of {@code ubs} or {@code lbs} are
     * {@code null}
     */
    WildcardType makeWildcard(FieldTypeSignature[] ubs,
                              FieldTypeSignature[] lbs);

    Type makeNamedType(String name);

    /**
     * Returns a (possibly generic) array type.
     * If the component type is a parameterized type, it must
     * only have unbounded wildcard arguments, otherwise
     * a MalformedParameterizedTypeException is thrown.
     * @param componentType - the component type of the array
     * @return a (possibly generic) array type.
     * @throws MalformedParameterizedTypeException if {@code componentType}
     * is a parameterized type with non-wildcard type arguments
     * @throws NullPointerException if any of the actual parameters
     * are {@code null}
     */
    Type makeArrayType(Type componentType);

    /**
     * Returns the reflective representation of type {@code byte}.
     * @return the reflective representation of type {@code byte}.
     */
    Type makeByte();

    /**
     * Returns the reflective representation of type {@code boolean}.
     * @return the reflective representation of type {@code boolean}.
     */
    Type makeBool();

    /**
     * Returns the reflective representation of type {@code short}.
     * @return the reflective representation of type {@code short}.
     */
    Type makeShort();

    /**
     * Returns the reflective representation of type {@code char}.
     * @return the reflective representation of type {@code char}.
     */
    Type makeChar();

    /**
     * Returns the reflective representation of type {@code int}.
     * @return the reflective representation of type {@code int}.
     */
    Type makeInt();

    /**
     * Returns the reflective representation of type {@code long}.
     * @return the reflective representation of type {@code long}.
     */
    Type makeLong();

    /**
     * Returns the reflective representation of type {@code float}.
     * @return the reflective representation of type {@code float}.
     */
    Type makeFloat();

    /**
     * Returns the reflective representation of type {@code double}.
     * @return the reflective representation of type {@code double}.
     */
    Type makeDouble();

    /**
     * Returns the reflective representation of {@code void}.
     * @return the reflective representation of {@code void}.
     */
    Type makeVoid();
}
