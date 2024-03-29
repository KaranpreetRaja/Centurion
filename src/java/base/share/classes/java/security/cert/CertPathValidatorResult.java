/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.cert;

/**
 * A specification of the result of a certification path validator algorithm.
 * <p>
 * The purpose of this interface is to group (and provide type safety
 * for) all certification path validator results. All results returned
 * by the {@link CertPathValidator#validate CertPathValidator.validate}
 * method must implement this interface.
 *
 * @see CertPathValidator
 *
 * @since       1.4
 * @author      Yassir Elley
 */
public interface CertPathValidatorResult extends Cloneable {

    /**
     * Makes a copy of this {@code CertPathValidatorResult}. Changes to the
     * copy will not affect the original and vice versa.
     *
     * @return a copy of this {@code CertPathValidatorResult}
     */
    Object clone();
}
