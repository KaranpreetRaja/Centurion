/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.sun.net.ftp;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceConfigurationError;
//import java.util.ServiceLoader;

/**
 * Service provider class for FtpClient.
 * Sub-classes of FtpClientProvider provide an implementation of {@link FtpClient}
 * and associated classes. Applications do not normally use this class directly.
 * See {@link #provider() } for how providers are found and loaded.
 *
 * @since 1.7
 */
public abstract class FtpClientProvider {

    /**
     * Creates a FtpClient from this provider.
     *
     * @return The created {@link FtpClient}.
     */
    public abstract FtpClient createFtpClient();
    private static final Object lock = new Object();
    private static FtpClientProvider provider = null;

    /**
     * Initializes a new instance of this class.
     *
     * @throws SecurityException if a security manager is installed and it denies
     *         {@link RuntimePermission}{@code ("ftpClientProvider")}
     */
    protected FtpClientProvider() {
        @SuppressWarnings("removal")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("ftpClientProvider"));
        }
    }

    private static boolean loadProviderFromProperty() {
        String cm = System.getProperty("java.base.share.classes.sun.net.ftpClientProvider");
        if (cm == null) {
            return false;
        }
        try {
            @SuppressWarnings("deprecation")
            Object o = Class.forName(cm, true, null).newInstance();
            provider = (FtpClientProvider)o;
            return true;
        } catch (ClassNotFoundException |
                 IllegalAccessException |
                 InstantiationException |
                 SecurityException x) {
            throw new ServiceConfigurationError(x.toString());
        }
    }

    private static boolean loadProviderAsService() {
//        Iterator<FtpClientProvider> i =
//                ServiceLoader.load(FtpClientProvider.class,
//                                   ClassLoader.getSystemClassLoader()).iterator();
//
//        while (i.hasNext()) {
//            try {
//                provider = i.next();
//                return true;
//            } catch (ServiceConfigurationError sce) {
//                if (sce.getCause() instanceof SecurityException) {
//                    // Ignore, try next provider, if any
//                    continue;
//                }
//                throw sce;
//            }
//        }
        return false;
    }

    /**
     * Returns the system wide default FtpClientProvider for this invocation of
     * the Java virtual machine.
     *
     * <p> The first invocation of this method locates the default provider
     * object as follows: </p>
     *
     * <ol>
     *
     *   <li><p> If the system property
     *   {@code java.net.FtpClientProvider} is defined then it is
     *   taken to be the fully-qualified name of a concrete provider class.
     *   The class is loaded and instantiated; if this process fails then an
     *   unspecified unchecked error or exception is thrown.  </p></li>
     *
     *   <li><p> If a provider class has been installed in a jar file that is
     *   visible to the system class loader, and that jar file contains a
     *   provider-configuration file named
     *   {@code java.net.FtpClientProvider} in the resource
     *   directory {@code META-INF/services}, then the first class name
     *   specified in that file is taken.  The class is loaded and
     *   instantiated; if this process fails then an unspecified unchecked error or exception is
     *   thrown.  </p></li>
     *
     *   <li><p> Finally, if no provider has been specified by any of the above
     *   means then the system-default provider class is instantiated and the
     *   result is returned.  </p></li>
     *
     * </ol>
     *
     * <p> Subsequent invocations of this method return the provider that was
     * returned by the first invocation.  </p>
     *
     * @return  The system-wide default FtpClientProvider
     */
    @SuppressWarnings("removal")
    public static FtpClientProvider provider() {
        synchronized (lock) {
            if (provider != null) {
                return provider;
            }
            return (FtpClientProvider) AccessController.doPrivileged(
                    new PrivilegedAction<Object>() {

                        public Object run() {
                            if (loadProviderFromProperty()) {
                                return provider;
                            }
                            if (loadProviderAsService()) {
                                return provider;
                            }
                            provider = new java.base.share.classes.sun.net.ftp.impl.DefaultFtpClientProvider();
                            return provider;
                        }
                    });
        }
    }
}
