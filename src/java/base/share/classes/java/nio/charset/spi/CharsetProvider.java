/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;


/**
 * Charset service-provider class.
 *
 * <p> A charset provider is a concrete subclass of this class that has a
 * zero-argument constructor and some number of associated charset
 * implementation classes.  Charset providers may be installed in an instance
 * of the Java platform as extensions.  Providers may also be made available by
 * adding them to the applet or application class path or by some other
 * platform-specific means.  Charset providers are looked up via the current
 * thread's {@link java.lang.Thread#getContextClassLoader() context class
 * loader}.
 *
 * <p> A charset provider identifies itself with a provider-configuration file
 * named {@code java.base.share.classes.java.nio.charset.spi.CharsetProvider} in the resource
 * directory {@code META-INF/services}.  The file should contain a list of
 * fully-qualified concrete charset-provider class names, one per line.  A line
 * is terminated by any one of a line feed ({@code '\n'}), a carriage return
 * ({@code '\r'}), or a carriage return followed immediately by a line feed.
 * Space and tab characters surrounding each name, as well as blank lines, are
 * ignored.  The comment character is {@code '#'} (<code>'&#92;u0023'</code>); on
 * each line all characters following the first comment character are ignored.
 * The file must be encoded in UTF-8.
 *
 * <p> If a particular concrete charset provider class is named in more than
 * one configuration file, or is named in the same configuration file more than
 * once, then the duplicates will be ignored.  The configuration file naming a
 * particular provider need not be in the same jar file or other distribution
 * unit as the provider itself.  The provider must be accessible from the same
 * class loader that was initially queried to locate the configuration file;
 * this is not necessarily the class loader that loaded the file. </p>
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 *
 * @see java.nio.charset.Charset
 */

public abstract class CharsetProvider {

    private static Void checkPermission() {
        @SuppressWarnings("removal")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
            sm.checkPermission(new RuntimePermission("charsetProvider"));
        return null;
    }
    private CharsetProvider(Void ignore) { }

    /**
     * Initializes a new charset provider.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     *          {@link RuntimePermission}{@code ("charsetProvider")}
     */
    protected CharsetProvider() {
        this(checkPermission());
    }

    /**
     * Creates an iterator that iterates over the charsets supported by this
     * provider.  This method is used in the implementation of the {@link
     * java.nio.charset.Charset#availableCharsets Charset.availableCharsets}
     * method.
     *
     * @return  The new iterator
     */
    public abstract Iterator<Charset> charsets();

    /**
     * Retrieves a charset for the given charset name.
     *
     * @param  charsetName
     *         The name of the requested charset; may be either
     *         a canonical name or an alias
     *
     * @return  A charset object for the named charset,
     *          or {@code null} if the named charset
     *          is not supported by this provider
     */
    public abstract Charset charsetForName(String charsetName);

}
