/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.loader;

import jdk.internal.misc.VM;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RawNativeLibraries has the following properties:
 * 1. Native libraries loaded in this RawNativeLibraries instance are
 *    not JNI native libraries.  Hence JNI_OnLoad and JNI_OnUnload will
 *    be ignored.  No support for linking of native method.
 * 2. Native libraries not auto-unloaded.  They may be explicitly unloaded
 *    via NativeLibraries::unload.
 * 3. No relationship with class loaders.
 */
public final class RawNativeLibraries {
    final Set<RawNativeLibraryImpl> libraries = ConcurrentHashMap.newKeySet();
    final Class<?> caller;

    private RawNativeLibraries(MethodHandles.Lookup trustedCaller) {
        this.caller = trustedCaller.lookupClass();
    }

    /**
     * Creates a RawNativeLibraries instance that has no relationship with
     * any class loaders and disabled auto unloading.
     *
     * This static factory method is restricted for JDK trusted class use.
     */
    public static RawNativeLibraries newInstance(MethodHandles.Lookup trustedCaller) {
        if (!trustedCaller.hasFullPrivilegeAccess() ||
                !VM.isSystemDomainLoader(trustedCaller.lookupClass().getClassLoader())) {
            throw new InternalError(trustedCaller + " does not have access to raw native library loading");
        }
        return new RawNativeLibraries(trustedCaller);
    }

    /*
     * Load a native library from the given path.  Returns null if the given
     * library is determined to be non-loadable, which is system-dependent.
     *
     * The library is opened with the platform-specific library loading
     * mechanism. If this method is called with the same path multiple times,
     * the library is opened the same number of times. To close the library
     * of the given path, {@code #unload} must be called on all the
     * {@code NativeLibrary} instances that load it.
     *
     * @param path the path of the native library
     */
    @SuppressWarnings("removal")
    public NativeLibrary load(Path path) {
        String name = AccessController.doPrivileged(new PrivilegedAction<>() {
            public String run() {
                try {
                    return path.toRealPath().toString();
                } catch (IOException e) {
                    return null;
                }
            }
        });
        if (name == null) {
            return null;
        }
        return load(name);
    }

    /**
     * Load a native library of the given pathname, which is platform-specific.
     * Returns null if it fails to load the given pathname.
     *
     * If the given pathname does not contain a name-separator character,
     * for example on Unix a slash character, the library search strategy
     * is system-dependent for example on Unix, see dlopen.
     *
     * @apiNote
     * The {@code pathname} argument is platform-specific.
     * {@link System#mapLibraryName} can be used to convert a name to
     * a platform-specific pathname:
     * {@snippet
     *     RawNativeLibraries libs = RawNativeLibraries.newInstance(MethodHandles.lookup());
     *     NativeLibrary lib = libs.load(System.mapLibraryName("blas"));
     * }
     *
     * The library is opened with the platform-specific library loading
     * mechanism. If this method is called with the same pathname multiple times,
     * the library is opened the same number of times. To close the library
     * of the given path, {@code #unload} must be called on all the
     * {@code NativeLibrary} instances that load it.
     *
     * @param pathname the pathname of the native library
     * @see System#mapLibraryName(String)
     */
    public NativeLibrary load(String pathname) {
        RawNativeLibraryImpl lib = new RawNativeLibraryImpl(pathname);
        if (!lib.open()) {
            return null;
        }
        libraries.add(lib);
        return lib;
    }

    /*
     * Unloads the given native library.  Each {@code NativeLibrary}
     * instance can be unloaded only once.
     *
     * The native library may remain opened after this method is called.
     * Refer to the platform-specific library loading mechanism, for example,
     * dlopen/dlclose on Unix or LoadLibrary/FreeLibrary on Windows.
     *
     * @throws IllegalArgumentException if the given library is not
     * loaded by this RawNativeLibraries or has already been unloaded
     */
    public void unload(NativeLibrary lib) {
        Objects.requireNonNull(lib);
        if (!libraries.remove(lib)) {
            throw new IllegalArgumentException("can't unload " + lib.name() + " loaded from " + lib);
        }
        RawNativeLibraryImpl nl = (RawNativeLibraryImpl)lib;
        nl.close();
    }

    static class RawNativeLibraryImpl extends NativeLibrary {
        // the name of the raw native library.
        final String name;
        // opaque handle to raw native library, used in native code.
        long handle;

        RawNativeLibraryImpl(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public long find(String name) {
            return findEntry0(handle, name);
        }

        /*
         * Loads the named native library.
         */
        boolean open() {
            if (handle != 0) {
                throw new InternalError("Native library " + name + " has been loaded");
            }
            return load0(this, name);
        }

        /*
         * Close this native library.
         */
        void close() {
            unload0(name, handle);
        }
    }

    private static native boolean load0(RawNativeLibraryImpl impl, String name);
    private static native void unload0(String name, long handle);
}

