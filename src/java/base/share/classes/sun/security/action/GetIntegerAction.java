/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.action;

import java.security.AccessController;

/**
 * A convenience class for retrieving the integer value of a system property
 * as a privileged action.
 *
 * <p>An instance of this class can be used as the argument of
 * <code>AccessController.doPrivileged</code>.
 *
 * <p>The following code retrieves the integer value of the system
 * property named <code>"prop"</code> as a privileged action. Since it does
 * not pass a default value to be used in case the property
 * <code>"prop"</code> is not defined, it has to check the result for
 * <code>null</code>:
 *
 * <pre>
 * Integer tmp = java.security.AccessController.doPrivileged
 *     (new sun.security.action.GetIntegerAction("prop"));
 * int i;
 * if (tmp != null) {
 *     i = tmp.intValue();
 * }
 * </pre>
 *
 * <p>The following code retrieves the integer value of the system
 * property named <code>"prop"</code> as a privileged action, and also passes
 * a default value to be used in case the property <code>"prop"</code> is not
 * defined:
 *
 * <pre>
 * int i = ((Integer)java.security.AccessController.doPrivileged(
 *                         new GetIntegerAction("prop", 3))).intValue();
 * </pre>
 *
 * @see java.security.PrivilegedAction
 * @see java.security.AccessController
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */

public class GetIntegerAction
        implements java.security.PrivilegedAction<Integer> {
    private final String theProp;
    private final int defaultVal;
    private final boolean defaultSet;

    /**
     * Constructor that takes the name of the system property whose integer
     * value needs to be determined.
     *
     * @param theProp the name of the system property.
     */
    public GetIntegerAction(String theProp) {
        this.theProp = theProp;
        this.defaultVal = 0;
        this.defaultSet = false;
    }

    /**
     * Constructor that takes the name of the system property and the default
     * value of that property.
     *
     * @param theProp the name of the system property.
     * @param defaultVal the default value.
     */
    public GetIntegerAction(String theProp, int defaultVal) {
        this.theProp = theProp;
        this.defaultVal = defaultVal;
        this.defaultSet = true;
    }

    /**
     * Determines the integer value of the system property whose name was
     * specified in the constructor.
     *
     * <p>If there is no property of the specified name, or if the property
     * does not have the correct numeric format, then an <code>Integer</code>
     * object representing the default value that was specified in the
     * constructor is returned, or <code>null</code> if no default value was
     * specified.
     *
     * @return the <code>Integer</code> value of the property.
     */
    public Integer run() {
        Integer value = Integer.getInteger(theProp);
        if ((value == null) && defaultSet)
            return defaultVal;
        return value;
    }

    /**
     * Convenience method to get a property without going through doPrivileged
     * if no security manager is present. This is unsafe for inclusion in a
     * public API but allowable here since this class is now encapsulated.
     *
     * Note that this method performs a privileged action using caller-provided
     * inputs. The caller of this method should take care to ensure that the
     * inputs are not tainted and the returned property is not made accessible
     * to untrusted code if it contains sensitive information.
     *
     * @param theProp the name of the system property.
     */
    @SuppressWarnings("removal")
    public static Integer privilegedGetProperty(String theProp) {
        if (System.getSecurityManager() == null) {
            return Integer.getInteger(theProp);
        } else {
            return AccessController.doPrivileged(
                    new GetIntegerAction(theProp));
        }
    }

    /**
     * Convenience method to get a property without going through doPrivileged
     * if no security manager is present. This is unsafe for inclusion in a
     * public API but allowable here since this class is now encapsulated.
     *
     * Note that this method performs a privileged action using caller-provided
     * inputs. The caller of this method should take care to ensure that the
     * inputs are not tainted and the returned property is not made accessible
     * to untrusted code if it contains sensitive information.
     *
     * @param theProp the name of the system property.
     * @param defaultVal the default value.
     */
    @SuppressWarnings("removal")
    public static Integer privilegedGetProperty(String theProp,
            int defaultVal) {
        Integer value;
        if (System.getSecurityManager() == null) {
            value = Integer.getInteger(theProp);
        } else {
            value = AccessController.doPrivileged(
                    new GetIntegerAction(theProp));
        }
        return (value != null) ? value : defaultVal;
    }
}
