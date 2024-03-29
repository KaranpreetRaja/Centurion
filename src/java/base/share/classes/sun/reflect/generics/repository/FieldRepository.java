/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.reflect.generics.repository;


import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.visitor.Reifier;

/**
 * This class represents the generic type information for a constructor.
 * The code is not dependent on a particular reflective implementation.
 * It is designed to be used unchanged by at least core reflection and JDI.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */
public class FieldRepository extends AbstractRepository<TypeSignature> {

    /** The generic type info.  Lazily initialized. */
    private volatile Type genericType;

    // protected, to enforce use of static factory yet allow subclassing
    protected FieldRepository(String rawSig, GenericsFactory f) {
      super(rawSig, f);
    }

    protected TypeSignature parse(String s) {
        return SignatureParser.make().parseTypeSig(s);
    }

    /**
     * Static factory method.
     * @param rawSig - the generic signature of the reflective object
     * that this repository is servicing
     * @param f - a factory that will provide instances of reflective
     * objects when this repository converts its AST
     * @return a {@code FieldRepository} that manages the generic type
     * information represented in the signature {@code rawSig}
     */
    public static FieldRepository make(String rawSig, GenericsFactory f) {
        return new FieldRepository(rawSig, f);
    }

    /*
     * When queried for a particular piece of type information, the
     * general pattern is to consult the corresponding cached value.
     * If the corresponding field is non-null, it is returned.
     * If not, it is created lazily. This is done by selecting the appropriate
     * part of the tree and transforming it into a reflective object
     * using a visitor, which is created by feeding it the factory
     * with which the repository was created.
     */

    public Type getGenericType() {
        Type value = genericType;
        if (value == null) {
            value = computeGenericType();
            genericType = value;
        }
        return value;
    }

    private Type computeGenericType() {
        Reifier r = getReifier();       // obtain visitor
        getTree().accept(r);            // reify subtree
        return r.getResult();           // extract result from visitor
    }
}
