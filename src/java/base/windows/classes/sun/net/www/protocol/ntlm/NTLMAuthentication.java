/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.net.www.protocol.ntlm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import sun.net.NetProperties;
import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthenticationInfo;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

/**
 * NTLMAuthentication:
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023 
 */

public class NTLMAuthentication extends AuthenticationInfo {

    private static final long serialVersionUID = 100L;

    private static final NTLMAuthenticationCallback NTLMAuthCallback =
        NTLMAuthenticationCallback.getNTLMAuthenticationCallback();

    private String hostname;
    /* Domain to use if not specified by user */
    private static final String defaultDomain;
    /* Whether cache is enabled for NTLM */
    private static final boolean ntlmCache;

    enum TransparentAuth {
        DISABLED,      // disable for all hosts (default)
        TRUSTED_HOSTS, // use Windows trusted hosts settings
        ALL_HOSTS      // attempt for all hosts
    }

    private static final TransparentAuth authMode;

    static {
        Properties props = GetPropertyAction.privilegedGetProperties();
        defaultDomain = props.getProperty("http.auth.ntlm.domain", "domain");
        String ntlmCacheProp = props.getProperty("jdk.ntlm.cache", "true");
        ntlmCache = Boolean.parseBoolean(ntlmCacheProp);
        @SuppressWarnings("removal")
        String modeProp = java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<String>() {
                public String run() {
                    return NetProperties.get("jdk.http.ntlm.transparentAuth");
                }
            });

        if ("trustedHosts".equalsIgnoreCase(modeProp))
            authMode = TransparentAuth.TRUSTED_HOSTS;
        else if ("allHosts".equalsIgnoreCase(modeProp))
            authMode = TransparentAuth.ALL_HOSTS;
        else
            authMode = TransparentAuth.DISABLED;
    }

    @SuppressWarnings("removal")
    private void init0() {

        hostname = java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<String>() {
            public String run() {
                String localhost;
                try {
                    localhost = InetAddress.getLocalHost().getHostName().toUpperCase();
                } catch (UnknownHostException e) {
                     localhost = "localhost";
                }
                return localhost;
            }
        });
        int x = hostname.indexOf ('.');
        if (x != -1) {
            hostname = hostname.substring (0, x);
        }
    }

    String username;
    String ntdomain;
    String password;

    /**
     * Create a NTLMAuthentication:
     * Username may be specified as {@literal domain<BACKSLASH>username}
     * in the application Authenticator.
     * If this notation is not used, then the domain will be taken
     * from a system property: "http.auth.ntlm.domain".
     */
    public NTLMAuthentication(boolean isProxy, URL url, PasswordAuthentication pw,
                              String authenticatorKey) {
        super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,
              AuthScheme.NTLM,
              url,
              "",
              Objects.requireNonNull(authenticatorKey));
        init (pw);
    }

    private void init (PasswordAuthentication pw) {
        this.pw = pw;
        if (pw != null) {
            String s = pw.getUserName();
            int i = s.indexOf ('\\');
            if (i == -1) {
                username = s;
                ntdomain = defaultDomain;
            } else {
                ntdomain = s.substring (0, i).toUpperCase();
                username = s.substring (i+1);
            }
            password = new String (pw.getPassword());
        } else {
            /* credentials will be acquired from OS */
            username = null;
            ntdomain = null;
            password = null;
        }
        init0();
    }

   /**
    * Constructor used for proxy entries
    */
    public NTLMAuthentication(boolean isProxy, String host, int port,
                              PasswordAuthentication pw,
                              String authenticatorKey) {
        super(isProxy?PROXY_AUTHENTICATION:SERVER_AUTHENTICATION,
              AuthScheme.NTLM,
              host,
              port,
              "",
              Objects.requireNonNull(authenticatorKey));
        init (pw);
    }

    @Override
    protected boolean useAuthCache() {
        return ntlmCache && super.useAuthCache();
    }

    /**
     * @return true if this authentication supports preemptive authorization
     */
    @Override
    public boolean supportsPreemptiveAuthorization() {
        return false;
    }

    /**
     * @return true if NTLM supported transparently (no password needed, SSO)
     */
    public static boolean supportsTransparentAuth() {
        return true;
    }

    /**
     * Returns true if the given site is trusted, i.e. we can try
     * transparent Authentication.
     */
    public static boolean isTrustedSite(URL url) {
        if (NTLMAuthCallback != null)
            return NTLMAuthCallback.isTrustedSite(url);

        switch (authMode) {
            case TRUSTED_HOSTS:
                return isTrustedSite(url.toString());
            case ALL_HOSTS:
                return true;
            default:
                return false;
        }
    }

    private static final boolean isTrustedSiteAvailable = isTrustedSiteAvailable();

    private static native boolean isTrustedSiteAvailable();

    private static boolean isTrustedSite(String url) {
        if (isTrustedSiteAvailable)
            return isTrustedSite0(url);
        return false;
    }

    private static native boolean isTrustedSite0(String url);

    /**
     * Not supported. Must use the setHeaders() method
     */
    @Override
    public String getHeaderValue(URL url, String method) {
        throw new RuntimeException ("getHeaderValue not supported");
    }

    /**
     * Check if the header indicates that the current auth. parameters are stale.
     * If so, then replace the relevant field with the new value
     * and return true. Otherwise return false.
     * returning true means the request can be retried with the same userid/password
     * returning false means we have to go back to the user to ask for a new
     * username password.
     */
    @Override
    public boolean isAuthorizationStale (String header) {
        return false; /* should not be called for ntlm */
    }

    /**
     * Set header(s) on the given connection.
     * @param conn The connection to apply the header(s) to
     * @param p A source of header values for this connection, not used because
     *          HeaderParser converts the fields to lower case, use raw instead
     * @param raw The raw header field.
     * @return true if all goes well, false if no headers were set.
     */
    @Override
    public boolean setHeaders(HttpURLConnection conn, HeaderParser p, String raw) {

        // no need to synchronize here:
        //   already locked by s.n.w.p.h.HttpURLConnection
        assert conn.isLockHeldByCurrentThread();

        try {
            NTLMAuthSequence seq = (NTLMAuthSequence)conn.authObj();
            if (seq == null) {
                seq = new NTLMAuthSequence (username, password, ntdomain);
                conn.authObj(seq);
            }
            String response = "NTLM " + seq.getAuthHeader (raw.length()>6?raw.substring(5):null);
            conn.setAuthenticationProperty(getHeaderName(), response);
            if (seq.isComplete()) {
                conn.authObj(null);
            }
            return true;
        } catch (IOException e) {
            conn.authObj(null);
            return false;
        }
    }
}