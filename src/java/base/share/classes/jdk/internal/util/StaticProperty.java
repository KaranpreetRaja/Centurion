/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.util;

import java.util.Properties;

/**
 * System Property access for internal use only.
 * Read-only access to System property values initialized during Phase 1
 * are cached.  Setting, clearing, or modifying the value using
 * {@link System#setProperty} or {@link System#getProperties()} is ignored.
 * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
 * in these access methods. The caller of these methods should take care to ensure
 * that the returned property is not made accessible to untrusted code.</strong>
 */
public final class StaticProperty {

    // The class static initialization is triggered to initialize these final
    // fields during init Phase 1 and before a security manager is set.
    private static final String JAVA_HOME;
    private static final String USER_HOME;
    private static final String USER_DIR;
    private static final String USER_NAME;
    private static final String JAVA_LIBRARY_PATH;
    private static final String SUN_BOOT_LIBRARY_PATH;
    private static final String JDK_SERIAL_FILTER;
    private static final String JDK_SERIAL_FILTER_FACTORY;
    private static final String JAVA_IO_TMPDIR;
    private static final String NATIVE_ENCODING;
    private static final String FILE_ENCODING;
    private static final String JAVA_PROPERTIES_DATE;
    private static final String SUN_JNU_ENCODING;
    private static final String JAVA_LOCALE_USE_OLD_ISO_CODES;

    private StaticProperty() {}

    static {
        Properties props = System.getProperties();
        JAVA_HOME = getProperty(props, "java.home");
        USER_HOME = getProperty(props, "user.home");
        USER_DIR  = getProperty(props, "user.dir");
        USER_NAME = getProperty(props, "user.name");
        JAVA_IO_TMPDIR = getProperty(props, "java.io.tmpdir");
        JAVA_LIBRARY_PATH = getProperty(props, "java.library.path", "");
        SUN_BOOT_LIBRARY_PATH = getProperty(props, "sun.boot.library.path", "");
        JDK_SERIAL_FILTER = getProperty(props, "jdk.serialFilter", null);
        JDK_SERIAL_FILTER_FACTORY = getProperty(props, "jdk.serialFilterFactory", null);
        NATIVE_ENCODING = getProperty(props, "native.encoding");
        FILE_ENCODING = getProperty(props, "file.encoding");
        JAVA_PROPERTIES_DATE = getProperty(props, "java.properties.date", null);
        SUN_JNU_ENCODING = getProperty(props, "sun.jnu.encoding");
        JAVA_LOCALE_USE_OLD_ISO_CODES = getProperty(props, "java.locale.useOldISOCodes", "");
    }

    private static String getProperty(Properties props, String key) {
        String v = props.getProperty(key);
        if (v == null) {
            throw new InternalError("null property: " + key);
        }
        return v;
    }

    private static String getProperty(Properties props, String key,
                                      String defaultVal) {
        String v = props.getProperty(key);
        return (v == null) ? defaultVal : v;
    }

    /**
     * {@return the {@code java.home} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String javaHome() {
        return JAVA_HOME;
    }

    /**
     * {@return the {@code user.home} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String userHome() {
        return USER_HOME;
    }

    /**
     * {@return the {@code user.dir} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String userDir() {
        return USER_DIR;
    }

    /**
     * {@return the {@code user.name} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String userName() {
        return USER_NAME;
    }

    /**
     * {@return the {@code java.library.path} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String javaLibraryPath() {
        return JAVA_LIBRARY_PATH;
    }

    /**
     * {@return the {@code java.io.tmpdir} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String javaIoTmpDir() {
        return JAVA_IO_TMPDIR;
    }

    /**
     * {@return the {@code sun.boot.library.path} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String sunBootLibraryPath() {
        return SUN_BOOT_LIBRARY_PATH;
    }


    /**
     * {@return the {@code jdk.serialFilter} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String jdkSerialFilter() {
        return JDK_SERIAL_FILTER;
    }


    /**
     * {@return the {@code jdk.serialFilterFactory} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String jdkSerialFilterFactory() {
        return JDK_SERIAL_FILTER_FACTORY;
    }

    /**
     * {@return the {@code native.encoding} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String nativeEncoding() {
        return NATIVE_ENCODING;
    }

    /**
     * {@return the {@code file.encoding} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String fileEncoding() {
        return FILE_ENCODING;
    }

    /**
     * {@return the {@code java.properties.date} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method.</strong>
     */
    public static String javaPropertiesDate() {
        return JAVA_PROPERTIES_DATE;
    }

    /**
     * {@return the {@code sun.jnu.encoding} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String jnuEncoding() {
        return SUN_JNU_ENCODING;
    }

    /**
     * {@return the {@code java.locale.useOldISOCodes} system property}
     *
     * <strong>{@link SecurityManager#checkPropertyAccess} is NOT checked
     * in this method. The caller of this method should take care to ensure
     * that the returned property is not made accessible to untrusted code.</strong>
     */
    public static String javaLocaleUseOldISOCodes() {
        return JAVA_LOCALE_USE_OLD_ISO_CODES;
    }
}
