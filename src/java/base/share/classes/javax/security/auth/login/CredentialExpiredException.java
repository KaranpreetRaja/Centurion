/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.security.auth.login;

/**
 * Signals that a {@code Credential} has expired.
 *
 * <p> This exception is thrown by LoginModules when they determine
 * that a {@code Credential} has expired.
 * For example, a {@code LoginModule} authenticating a user
 * in its {@code login} method may determine that the user's
 * password, although entered correctly, has expired.  In this case
 * the {@code LoginModule} throws this exception to notify
 * the application.  The application can then take the appropriate
 * steps to assist the user in updating the password.
 *
 * @since 1.4
 */
public class CredentialExpiredException extends CredentialException {

    @java.io.Serial
    private static final long serialVersionUID = -5344739593859737937L;

    /**
     * Constructs a CredentialExpiredException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public CredentialExpiredException() {
        super();
    }

    /**
     * Constructs a CredentialExpiredException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the detail message.
     */
    public CredentialExpiredException(String msg) {
        super(msg);
    }
}
