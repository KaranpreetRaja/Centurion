/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import java.base.share.classes.sun.reflect.generics.visitor.Reifier;

/**
 * Common infrastructure for things that lazily generate reflective generics
 * objects.
 * <p> In all these cases, one needs produce a visitor that will, on demand,
 * traverse the stored AST(s) and reify them into reflective objects.
 * The visitor needs to be initialized with a factory, which will be
 * provided when the instance is initialized.
 * The factory should be cached.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
*/
public abstract class LazyReflectiveObjectGenerator {
    private final GenericsFactory factory; // cached factory

    protected LazyReflectiveObjectGenerator(GenericsFactory f) {
        factory = f;
    }

    // accessor for factory
    private GenericsFactory getFactory() {
        return factory;
    }

    // produce a reifying visitor (could this be typed as a TypeTreeVisitor?
    protected Reifier getReifier(){return Reifier.make(getFactory());}

    Type[] reifyBounds(FieldTypeSignature[] boundASTs) {
        final int length = boundASTs.length;
        final Type[] bounds = new Type[length];
        // iterate over bound trees, reifying each in turn
        for (int i = 0; i < length; i++) {
            Reifier r = getReifier();
            boundASTs[i].accept(r);
            bounds[i] = r.getResult();
        }
        return bounds;
    }

}
