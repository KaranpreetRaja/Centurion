/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * The class contains common functionality for algorithm constraints classes.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public abstract class AbstractAlgorithmConstraints
        implements AlgorithmConstraints {

    protected final AlgorithmDecomposer decomposer;

    protected AbstractAlgorithmConstraints(AlgorithmDecomposer decomposer) {
        this.decomposer = decomposer;
    }

    // Get algorithm constraints from the specified security property.
    static Set<String> getAlgorithms(String propertyName) {
        @SuppressWarnings("removal")
        String property = AccessController.doPrivileged(
                new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return Security.getProperty(propertyName);
                    }
                });

        String[] algorithmsInProperty = null;
        if (property != null && !property.isEmpty()) {
            // remove double quote marks from beginning/end of the property
            if (property.length() >= 2 && property.charAt(0) == '"' &&
                    property.charAt(property.length() - 1) == '"') {
                property = property.substring(1, property.length() - 1);
            }
            algorithmsInProperty = property.split(",");
            for (int i = 0; i < algorithmsInProperty.length; i++) {
                algorithmsInProperty[i] = algorithmsInProperty[i].trim();
            }
        }

        // map the disabled algorithms
        if (algorithmsInProperty == null) {
            return Collections.emptySet();
        }
        Set<String> algorithmsInPropertySet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        algorithmsInPropertySet.addAll(Arrays.asList(algorithmsInProperty));
        return algorithmsInPropertySet;
    }

    static boolean checkAlgorithm(Set<String> algorithms, String algorithm,
            AlgorithmDecomposer decomposer) {
        if (algorithm == null || algorithm.isEmpty()) {
            throw new IllegalArgumentException("No algorithm name specified");
        }

        if (algorithms.contains(algorithm)) {
            return false;
        }

        // decompose the algorithm into sub-elements
        Set<String> elements = decomposer.decompose(algorithm);

        // check the element of the elements
        for (String element : elements) {
            if (algorithms.contains(element)) {
                return false;
            }
        }

        return true;
    }

}
