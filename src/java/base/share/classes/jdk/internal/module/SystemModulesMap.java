/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.module;

/**
 * This class is generated/overridden at link time to return the names of the
 * SystemModules classes generated at link time.
 *
 * @see SystemModuleFinders
 * @see jdk.tools.jlink.internal.plugins.SystemModulesPlugin
 */

class SystemModulesMap {

    /**
     * Returns the SystemModules object to reconstitute all modules or null
     * if this is an exploded build.
     */
    static SystemModules allSystemModules() {
        return null;
    }

    /**
     * Returns the SystemModules object to reconstitute default modules or null
     * if this is an exploded build.
     */
    static SystemModules defaultSystemModules() {
        return null;
    }

    /**
     * Returns the array of initial module names identified at link time.
     */
    static String[] moduleNames() {
        return new String[0];
    }

    /**
     * Returns the array of SystemModules class names. The elements
     * correspond to the elements in the array returned by moduleNames().
     */
    static String[] classNames() {
        return new String[0];
    }
}