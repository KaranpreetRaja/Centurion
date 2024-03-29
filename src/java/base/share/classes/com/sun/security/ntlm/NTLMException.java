/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.com.sun.security.ntlm;

import java.security.GeneralSecurityException;

/**
 * An NTLM-related Exception
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */
public final class NTLMException extends GeneralSecurityException {
    @java.io.Serial
    private static final long serialVersionUID = -3298539507906689430L;

    /**
     * If the incoming packet is invalid.
     */
    public static final int PACKET_READ_ERROR = 1;

    /**
     * If the client cannot get a domain value from the server and the
     * caller has not provided one.
     */
    public static final int NO_DOMAIN_INFO = 2;

    /**
     * If the client name is not found on server's user database.
     */
    public static final int USER_UNKNOWN = 3;

    /**
     * If authentication fails.
     */
    public static final int AUTH_FAILED = 4;

    /**
     * If an illegal version string is provided.
     */
    public static final int BAD_VERSION = 5;

    /**
     * Protocol errors.
     */
    public static final int PROTOCOL = 6;

    /**
     * If an invalid input is provided.
     */
    public static final int INVALID_INPUT = 7;

    private int errorCode;

    /**
     * Constructs an NTLMException object.
     * @param errorCode the error code, which can be retrieved by
     * the {@link #errorCode() } method.
     * @param msg the string message, which can be retrieved by
     * the {@link Exception#getMessage() } method.
     */
    public NTLMException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this NTLMException.
     * @return the error code
     */
    public int errorCode() {
        return errorCode;
    }
}
