/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.net.ssl;

import java.security.*;

/**
 * This class is for various network permissions.
 * An SSLPermission contains a name (also referred to as a "target name") but
 * no actions list; you either have the named permission
 * or you don't.
 * <P>
 * The target name is the name of the network permission (see below). The naming
 * convention follows the  hierarchical property naming convention.
 * Also, an asterisk
 * may appear at the end of the name, following a ".", or by itself, to
 * signify a wildcard match. For example: "foo.*" and "*" signify a wildcard
 * match, while "*foo" and "a*b" do not.
 * <P>
 * The following table lists all the possible SSLPermission target names,
 * and for each provides a description of what the permission allows
 * and a discussion of the risks of granting code the permission.
 *
 * <table class="striped">
 * <caption style="display:none">permission name, what it allows, and associated risks</caption>
 * <thead>
 * <tr>
 * <th scope="col">Permission Target Name</th>
 * <th scope="col">What the Permission Allows</th>
 * <th scope="col">Risks of Allowing this Permission</th>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 *   <th scope="row">setHostnameVerifier</th>
 *   <td>The ability to set a callback which can decide whether to
 * allow a mismatch between the host being connected to by
 * an HttpsURLConnection and the common name field in
 * server certificate.
 *  </td>
 *   <td>Malicious
 * code can set a verifier that monitors host names visited by
 * HttpsURLConnection requests or that allows server certificates
 * with invalid common names.
 * </td>
 * </tr>
 *
 * <tr>
 *   <th scope="row">getSSLSessionContext</th>
 *   <td>The ability to get the SSLSessionContext of an SSLSession.
 * </td>
 *   <td>Malicious code may monitor sessions which have been established
 * with SSL peers or might invalidate sessions to slow down performance.
 * </td>
 * </tr>
 *
 * <tr>
 *   <th scope="row">setDefaultSSLContext</th>
 *   <td>The ability to set the default SSL context
 * </td>
 *   <td>Malicious code can set a context that monitors the opening of
 * connections or the plaintext data that is transmitted.
 * </td>
 * </tr>
 *
 * </tbody>
 * </table>
 *
 * @see java.security.BasicPermission
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 *
 * @since 1.4
 * @author Marianne Mueller
 * @author Roland Schemers
 */

public final class SSLPermission extends BasicPermission {

    @java.io.Serial
    private static final long serialVersionUID = -3456898025505876775L;

    /**
     * Creates a new SSLPermission with the specified name.
     * The name is the symbolic name of the SSLPermission, such as
     * "setDefaultAuthenticator", etc. An asterisk
     * may appear at the end of the name, following a ".", or by itself, to
     * signify a wildcard match.
     *
     * @param name the name of the SSLPermission.
     *
     * @throws NullPointerException if <code>name</code> is null.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */

    public SSLPermission(String name)
    {
        super(name);
    }

    /**
     * Creates a new SSLPermission object with the specified name.
     * The name is the symbolic name of the SSLPermission, and the
     * actions String is currently unused and should be null.
     *
     * @param name the name of the SSLPermission.
     * @param actions ignored.
     *
     * @throws NullPointerException if <code>name</code> is null.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */

    public SSLPermission(String name, String actions)
    {
        super(name, actions);
    }
}
