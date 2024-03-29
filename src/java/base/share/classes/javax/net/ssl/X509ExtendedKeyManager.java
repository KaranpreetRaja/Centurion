/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.net.ssl;

import java.security.Principal;

/**
 * Abstract class that provides for extension of the X509KeyManager
 * interface.
 * <P>
 * Methods in this class should be overridden to provide actual
 * implementations.
 *
 * @since 1.5
 * @author Brad R. Wetmore
 */
public abstract class X509ExtendedKeyManager implements X509KeyManager {

    /**
     * Constructor used by subclasses only.
     */
    protected X509ExtendedKeyManager() {
    }

    /**
     * Choose an alias to authenticate the client side of an
     * <code>SSLEngine</code> connection given the public key type
     * and the list of certificate issuer authorities recognized by
     * the peer (if any).
     * <P>
     * The default implementation returns null.
     *
     * @param keyType the key algorithm type name(s), ordered
     *          with the most-preferred key type first.
     * @param issuers the list of acceptable CA issuer subject names
     *          or null if it does not matter which issuers are used.
     * @param engine the <code>SSLEngine</code> to be used for this
     *          connection.  This parameter can be null, which indicates
     *          that implementations of this interface are free to
     *          select an alias applicable to any engine.
     * @return the alias name for the desired key, or null if there
     *          are no matches.
     */
    public String chooseEngineClientAlias(String[] keyType,
            Principal[] issuers, SSLEngine engine) {
        return null;
    }

    /**
     * Choose an alias to authenticate the server side of an
     * <code>SSLEngine</code> connection given the public key type
     * and the list of certificate issuer authorities recognized by
     * the peer (if any).
     * <P>
     * The default implementation returns null.
     *
     * @param keyType the key algorithm type name.
     * @param issuers the list of acceptable CA issuer subject names
     *          or null if it does not matter which issuers are used.
     * @param engine the <code>SSLEngine</code> to be used for this
     *          connection.  This parameter can be null, which indicates
     *          that implementations of this interface are free to
     *          select an alias applicable to any engine.
     * @return the alias name for the desired key, or null if there
     *          are no matches.
     */
    public String chooseEngineServerAlias(String keyType,
            Principal[] issuers, SSLEngine engine) {
        return null;
    }

}
