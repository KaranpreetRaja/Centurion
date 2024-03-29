/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.security.interfaces;

import java.security.spec.ECParameterSpec;

/**
 * The interface to an elliptic curve (EC) key.
 *
 * @author Valerie Peng
 *
 * @since 1.5
 */
public interface ECKey {
    /**
     * Returns the domain parameters associated
     * with this key. The domain parameters are
     * either explicitly specified or implicitly
     * created during key generation.
     * @return the associated domain parameters.
     */
    ECParameterSpec getParams();
}
