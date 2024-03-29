/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.reflect.misc;

import java.io.IOException;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

class Trampoline {
    static {
        if (Trampoline.class.getClassLoader() == null) {
            throw new Error(
                "Trampoline must not be defined by the bootstrap classloader");
        }
    }

    @SuppressWarnings("removal")
    private static void ensureInvocableMethod(Method m)
        throws InvocationTargetException
    {
        Class<?> clazz = m.getDeclaringClass();
        if (clazz.equals(AccessController.class) ||
            clazz.equals(Method.class) ||
            clazz.getName().startsWith("java.lang.invoke."))
            throw new InvocationTargetException(
                new UnsupportedOperationException("invocation not supported"));
    }

    private static Object invoke(Method m, Object obj, Object[] params)
        throws InvocationTargetException, IllegalAccessException
    {
        ensureInvocableMethod(m);
        return m.invoke(obj, params);
    }
}

/*
 * Create a trampoline class.
 */
public final class MethodUtil extends SecureClassLoader {
    private static final String MISC_PKG = "sun.reflect.misc.";
    private static final String TRAMPOLINE = MISC_PKG + "Trampoline";
    private static final Method bounce = getTrampoline();

    private MethodUtil() {
        super();
    }

    public static Method getMethod(Class<?> cls, String name, Class<?>[] args)
        throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getMethod(name, args);
    }

    public static Method[] getMethods(Class<?> cls) {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getMethods();
    }

    /*
     * Bounce through the trampoline.
     */
    public static Object invoke(Method m, Object obj, Object[] params)
        throws InvocationTargetException, IllegalAccessException {
        try {
            return bounce.invoke(null, new Object[] {m, obj, params});
        } catch (InvocationTargetException ie) {
            Throwable t = ie.getCause();

            if (t instanceof InvocationTargetException) {
                throw (InvocationTargetException)t;
            } else if (t instanceof IllegalAccessException) {
                throw (IllegalAccessException)t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            } else if (t instanceof Error) {
                throw (Error)t;
            } else {
                throw new Error("Unexpected invocation error", t);
            }
        } catch (IllegalAccessException iae) {
            // this can't happen
            throw new Error("Unexpected invocation error", iae);
        }
    }

    @SuppressWarnings("removal")
    private static Method getTrampoline() {
        try {
            return AccessController.doPrivileged(
                new PrivilegedExceptionAction<Method>() {
                    public Method run() throws Exception {
                        Class<?> t = getTrampolineClass();
                        Class<?>[] types = {
                            Method.class, Object.class, Object[].class
                        };
                        Method b = t.getDeclaredMethod("invoke", types);
                        b.setAccessible(true);
                        return b;
                    }
                });
        } catch (Exception e) {
            throw new InternalError("bouncer cannot be found", e);
        }
    }


    protected synchronized Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        // First, check if the class has already been loaded
        ReflectUtil.checkPackageAccess(name);
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findClass(name);
            } catch (ClassNotFoundException e) {
                // Fall through ...
            }
            if (c == null) {
                c = getParent().loadClass(name);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }


    protected Class<?> findClass(final String name)
        throws ClassNotFoundException
    {
        if (!name.startsWith(MISC_PKG)) {
            throw new ClassNotFoundException(name);
        }
        String path = name.replace('.', '/').concat(".class");
        try {
            InputStream in = Object.class.getModule().getResourceAsStream(path);
            if (in != null) {
                try (in) {
                    byte[] b = in.readAllBytes();
                    return defineClass(name, b);
                }
            }
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }

        throw new ClassNotFoundException(name);
    }


    /*
     * Define the proxy classes
     */
    private Class<?> defineClass(String name, byte[] b) throws IOException {
        CodeSource cs = new CodeSource(null, (java.security.cert.Certificate[])null);
        if (!name.equals(TRAMPOLINE)) {
            throw new IOException("MethodUtil: bad name " + name);
        }
        return defineClass(name, b, 0, b.length, cs);
    }

    protected PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection perms = super.getPermissions(codesource);
        perms.add(new AllPermission());
        return perms;
    }

    private static Class<?> getTrampolineClass() {
        try {
            return Class.forName(TRAMPOLINE, true, new MethodUtil());
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

}
