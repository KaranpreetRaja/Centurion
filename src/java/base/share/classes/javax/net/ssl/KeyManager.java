/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.net.ssl;

/**
 * This is the base interface for JSSE key managers.
 * <P>
 * <code>KeyManager</code>s are responsible for managing the
 * key material which is used to authenticate the local SSLSocket
 * to its peer.  If no key material is available, the socket will
 * be unable to present authentication credentials.
 * <P>
 * <code>KeyManager</code>s are created by either
 * using a <code>KeyManagerFactory</code>,
 * or by implementing one of the <code>KeyManager</code> subclasses.
 *
 * @since 1.4
 * @see KeyManagerFactory
 */
public interface KeyManager {
}
