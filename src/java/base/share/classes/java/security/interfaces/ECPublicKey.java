/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.security.interfaces;

import java.security.PublicKey;
import java.security.spec.ECPoint;

/**
 * The interface to an elliptic curve (EC) public key.
 *
 * @author Valerie Peng
 *
 *
 * @see PublicKey
 * @see ECKey
 * @see java.security.spec.ECPoint
 *
 * @since 1.5
 */
public interface ECPublicKey extends PublicKey, ECKey {

   /**
    * The class fingerprint that is set to indicate
    * serialization compatibility.
    *
    * @deprecated A {@code serialVersionUID} field in an interface is
    * ineffectual. Do not use; no replacement.
    */
    @Deprecated
    @SuppressWarnings("serial")
    @java.io.Serial
   long serialVersionUID = -3314988629879632826L;

    /**
     * Returns the public point W.
     * @return the public point W.
     */
    ECPoint getW();
}
