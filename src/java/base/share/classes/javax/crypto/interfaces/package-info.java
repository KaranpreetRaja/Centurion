/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

/**
 * Provides interfaces for Diffie-Hellman keys as defined in RSA
 * Laboratories' PKCS #3.
 *
 * <P>Note that these interfaces are intended only for key
 * implementations whose key material is accessible and
 * available. These interfaces are not intended for key implementations
 * whose key material resides in inaccessible, protected storage (such
 * as in a hardware device).
 *
 * <P>For more developer information on how to use these interfaces,
 * including information on how to design <code>Key</code> classes for
 * hardware devices, please refer to the cryptographic provider
 * developer guide:
 *
 * <ul>
 *   <li> {@extLink security_guide_impl_provider
 *     How to Implement a Provider in the Java Cryptography Architecture}</li>
 * </ul>
 *
 * <h2>Package Specification</h2>
 *
 * <ul>
 *   <li>PKCS #3: Diffie-Hellman Key-Agreement Standard, Version 1.4,
 *       November 1993.</li>
 * </ul>
 *
 * <h2>Related Documentation</h2>
 *
 * For further documentation, please see:
 * <ul>
 *   <li>
 *     {@extLink security_guide_jca
 *       Java Cryptography Architecture (JCA) Reference Guide}</li>
 * </ul>
 *
 * @since 1.4
 */
package java.base.share.classes.javax.crypto.interfaces;
