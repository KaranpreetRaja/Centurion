/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.security.spec;

import java.math.BigInteger;
import java.util.Objects;

/**
 * An elliptic curve point used to specify keys as defined by
 * <a href="https://tools.ietf.org/html/rfc8032">RFC 8032: Edwards-Curve
 * Digital Signature Algorithm (EdDSA)</a>. These points are distinct from the
 * points represented by {@code ECPoint}, and they are intended for use with
 * algorithms based on RFC 8032 such as the EdDSA {@code Signature} algorithm.
 * <p>
 * An EdEC point is specified by its y-coordinate value and a boolean that
 * indicates whether the x-coordinate is odd. The y-coordinate is an
 * element of the field of integers modulo some value p that is determined by
 * the algorithm parameters. This field element is represented by a
 * {@code BigInteger}, and implementations that consume objects of this class
 * may reject integer values which are not in the range [0, p).
 *
 * @since 15
 */

public final class EdECPoint {

    private final boolean xOdd;
    private final BigInteger y;

    /**
     * Construct an EdECPoint.
     *
     * @param xOdd whether the x-coordinate is odd.
     * @param y the y-coordinate, represented using a {@code BigInteger}.
     *
     * @throws NullPointerException if {@code y} is null.
     */
    public EdECPoint(boolean xOdd, BigInteger y) {

        Objects.requireNonNull(y, "y must not be null");

        this.xOdd = xOdd;
        this.y = y;
    }

    /**
     * Get whether the x-coordinate of the point is odd.
     *
     * @return a boolean indicating whether the x-coordinate is odd.
     */
    public boolean isXOdd() {
        return xOdd;
    }

    /**
     * Get the y-coordinate of the point.
     *
     * @return the y-coordinate, represented using a {@code BigInteger}.
     */
    public BigInteger getY() {
        return y;
    }
}
