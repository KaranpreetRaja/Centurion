/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;

import jdk.internal.access.JavaLangAccess;
import jdk.internal.access.SharedSecrets;

/** Utility class which assists in calling defineClass() by
    creating a new class loader which delegates to the one needed in
    order for proper resolution of the given bytecodes to occur. */

class ClassDefiner {
    static final JavaLangAccess JLA = SharedSecrets.getJavaLangAccess();

    /** <P> We define generated code into a new class loader which
      delegates to the defining loader of the target class. It is
      necessary for the VM to be able to resolve references to the
      target class from the generated bytecodes, which could not occur
      if the generated code was loaded into the bootstrap class
      loader. </P>

      <P> There are two primary reasons for creating a new loader
      instead of defining these bytecodes directly into the defining
      loader of the target class: first, it avoids any possible
      security risk of having these bytecodes in the same loader.
      Second, it allows the generated bytecodes to be unloaded earlier
      than would otherwise be possible, decreasing run-time
      footprint. </P>
    */
    static Class<?> defineClass(String name, byte[] bytes, int off, int len,
                                final ClassLoader parentClassLoader)
    {
        @SuppressWarnings("removal")
        ClassLoader newLoader = AccessController.doPrivileged(
            new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                        return new DelegatingClassLoader(parentClassLoader);
                    }
                });
        return JLA.defineClass(newLoader, name, bytes, null, "__ClassDefiner__");
    }
}


// NOTE: this class's name and presence are known to the virtual
// machine as of the fix for 4474172.
class DelegatingClassLoader extends ClassLoader {
    DelegatingClassLoader(ClassLoader parent) {
        super(parent);
    }
}
