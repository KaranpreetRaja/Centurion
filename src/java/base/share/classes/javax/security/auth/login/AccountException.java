/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.security.auth.login;

/**
 * A generic account exception.
 *
 * @since 1.5
 */
public class AccountException extends LoginException {

    @java.io.Serial
    private static final long serialVersionUID = -2112878680072211787L;

    /**
     * Constructs a AccountException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public AccountException() {
        super();
    }

    /**
     * Constructs a AccountException with the specified detail message.
     * A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the detail message.
     */
    public AccountException(String msg) {
        super(msg);
    }
}
