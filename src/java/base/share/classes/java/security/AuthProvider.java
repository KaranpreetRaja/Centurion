/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;

/**
 * This class defines login and logout methods for a provider.
 *
 * <p> While callers may invoke {@code login} directly,
 * the provider may also invoke {@code login} on behalf of callers
 * if it determines that a login must be performed
 * prior to certain operations.
 *
 * @since 1.5
 */
public abstract class AuthProvider extends Provider {

    @java.io.Serial
    private static final long serialVersionUID = 4197859053084546461L;

    /**
     * Constructs a provider with the specified name, version number,
     * and information.
     *
     * @param name the provider name.
     * @param version the provider version number.
     * @param info a description of the provider and its services.
     * @deprecated use {@link #AuthProvider(String, String, String)} instead.
     */
    @Deprecated(since="9")
    protected AuthProvider(String name, double version, String info) {
        super(name, Double.toString(version), info);
    }

    /**
     * Constructs a provider with the specified name, version string,
     * and information.
     *
     * @param name the provider name.
     * @param versionStr the provider version string.
     * @param info a description of the provider and its services.
     * @since 9
     */
    protected AuthProvider(String name, String versionStr, String info) {
        super(name, versionStr, info);
    }

    /**
     * Log in to this provider.
     *
     * <p> The provider relies on a {@code CallbackHandler}
     * to obtain authentication information from the caller
     * (a PIN, for example).  If the caller passes a {@code null}
     * handler to this method, the provider uses the handler set in the
     * {@code setCallbackHandler} method.
     * If no handler was set in that method, the provider queries the
     * <i>auth.login.defaultCallbackHandler</i> security property
     * for the fully qualified class name of a default handler implementation.
     * If the security property is not set,
     * the provider is assumed to have alternative means
     * for obtaining authentication information.
     *
     * @param subject the {@code Subject} which may contain
     *          principals/credentials used for authentication,
     *          or may be populated with additional principals/credentials
     *          after successful authentication has completed.
     *          This parameter may be {@code null}.
     * @param handler the {@code CallbackHandler} used by
     *          this provider to obtain authentication information
     *          from the caller, which may be {@code null}
     *
     * @throws IllegalStateException if the provider requires configuration
     * and {@link #configure} has not been called
     * @throws LoginException if the login operation fails
     * @throws SecurityException if the caller does not pass a
     *  security check for
     *  {@code SecurityPermission("authProvider.name")},
     *  where {@code name} is the value returned by
     *  this provider's {@code getName} method
     */
    public abstract void login(Subject subject, CallbackHandler handler)
        throws LoginException;

    /**
     * Log out from this provider.
     *
     * @throws IllegalStateException if the provider requires configuration
     * and {@link #configure} has not been called
     * @throws LoginException if the logout operation fails
     * @throws SecurityException if the caller does not pass a
     *  security check for
     *  {@code SecurityPermission("authProvider.name")},
     *  where {@code name} is the value returned by
     *  this provider's {@code getName} method
     */
    public abstract void logout() throws LoginException;

    /**
     * Set a {@code CallbackHandler}.
     *
     * <p> The provider uses this handler if one is not passed to the
     * {@code login} method.  The provider also uses this handler
     * if it invokes {@code login} on behalf of callers.
     * In either case if a handler is not set via this method,
     * the provider queries the
     * <i>auth.login.defaultCallbackHandler</i> security property
     * for the fully qualified class name of a default handler implementation.
     * If the security property is not set,
     * the provider is assumed to have alternative means
     * for obtaining authentication information.
     *
     * @param handler a {@code CallbackHandler} for obtaining
     *          authentication information, which may be {@code null}
     *
     * @throws IllegalStateException if the provider requires configuration
     * and {@link #configure} has not been called
     * @throws SecurityException if the caller does not pass a
     *  security check for
     *  {@code SecurityPermission("authProvider.name")},
     *  where {@code name} is the value returned by
     *  this provider's {@code getName} method
     */
    public abstract void setCallbackHandler(CallbackHandler handler);
}
