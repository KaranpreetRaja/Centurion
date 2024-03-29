/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.net.ssl;

import java.nio.ByteBuffer;

/**
 * An encapsulation of the result state produced by
 * {@code SSLEngine} I/O calls.
 *
 * <p> A {@code SSLEngine} provides a means for establishing
 * secure communication sessions between two peers.  {@code SSLEngine}
 * operations typically consume bytes from an input buffer and produce
 * bytes in an output buffer.  This class provides operational result
 * values describing the state of the {@code SSLEngine}, including
 * indications of what operations are needed to finish an
 * ongoing handshake.  Lastly, it reports the number of bytes consumed
 * and produced as a result of this operation.
 *
 * @see SSLEngine
 * @see SSLEngine#wrap(ByteBuffer, ByteBuffer)
 * @see SSLEngine#unwrap(ByteBuffer, ByteBuffer)
 *
 * @author Brad R. Wetmore
 * @since 1.5
 */

public class SSLEngineResult {

    /**
     * An {@code SSLEngineResult} enum describing the overall result
     * of the {@code SSLEngine} operation.
     *
     * The {@code Status} value does not reflect the
     * state of a {@code SSLEngine} handshake currently
     * in progress.  The {@code SSLEngineResult's HandshakeStatus}
     * should be consulted for that information.
     *
     * @author Brad R. Wetmore
     * @since 1.5
     */
    public enum Status {

        /**
         * The {@code SSLEngine} was not able to unwrap the
         * incoming data because there were not enough source bytes
         * available to make a complete packet.
         *
         * <P>
         * Repeat the call once more bytes are available.
         */
        BUFFER_UNDERFLOW,

        /**
         * The {@code SSLEngine} was not able to process the
         * operation because there are not enough bytes available in the
         * destination buffer to hold the result.
         * <P>
         * Repeat the call once more bytes are available.
         *
         * @see SSLSession#getPacketBufferSize()
         * @see SSLSession#getApplicationBufferSize()
         */
        BUFFER_OVERFLOW,

        /**
         * The {@code SSLEngine} completed the operation, and
         * is available to process similar calls.
         */
        OK,

        /**
         * The operation just closed this side of the
         * {@code SSLEngine}, or the operation
         * could not be completed because it was already closed.
         */
        CLOSED
    }

    /**
     * An {@code SSLEngineResult} enum describing the current
     * handshaking state of this {@code SSLEngine}.
     *
     * @author Brad R. Wetmore
     * @since 1.5
     */
    public enum HandshakeStatus {

        /**
         * The {@code SSLEngine} is not currently handshaking.
         */
        NOT_HANDSHAKING,

        /**
         * The {@code SSLEngine} has just finished handshaking.
         * <P>
         * This value is only generated by a call to
         * {@code SSLEngine.wrap()/unwrap()} when that call
         * finishes a handshake.  It is never generated by
         * {@code SSLEngine.getHandshakeStatus()}.
         *
         * @see SSLEngine#wrap(ByteBuffer, ByteBuffer)
         * @see SSLEngine#unwrap(ByteBuffer, ByteBuffer)
         * @see SSLEngine#getHandshakeStatus()
         */
        FINISHED,

        /**
         * The {@code SSLEngine} needs the results of one (or more)
         * delegated tasks before handshaking can continue.
         *
         * @see SSLEngine#getDelegatedTask()
         */
        NEED_TASK,

        /**
         * The {@code SSLEngine} must send data to the remote side
         * before handshaking can continue, so {@code SSLEngine.wrap()}
         * should be called.
         *
         * @see SSLEngine#wrap(ByteBuffer, ByteBuffer)
         */
        NEED_WRAP,

        /**
         * The {@code SSLEngine} needs to receive data from the
         * remote side before handshaking can continue.
         */
        NEED_UNWRAP,

        /**
         * The {@code SSLEngine} needs to unwrap before handshaking can
         * continue.
         * <P>
         * This value is used to indicate that not-yet-interpreted data
         * has been previously received from the remote side, and does
         * not need to be received again.
         * <P>
         * This handshake status only applies to DTLS.
         *
         * @since   9
         */
        NEED_UNWRAP_AGAIN
    }


    private final Status status;
    private final HandshakeStatus handshakeStatus;
    private final int bytesConsumed;
    private final int bytesProduced;
    private final long sequenceNumber;

    /**
     * Initializes a new instance of this class.
     *
     * @param   status
     *          the return value of the operation.
     *
     * @param   handshakeStatus
     *          the current handshaking status.
     *
     * @param   bytesConsumed
     *          the number of bytes consumed from the source ByteBuffer
     *
     * @param   bytesProduced
     *          the number of bytes placed into the destination ByteBuffer
     *
     * @throws  IllegalArgumentException
     *          if the {@code status} or {@code handshakeStatus}
     *          arguments are null, or if {@code bytesConsumed} or
     *          {@code bytesProduced} is negative.
     */
    public SSLEngineResult(Status status, HandshakeStatus handshakeStatus,
            int bytesConsumed, int bytesProduced) {
        this(status, handshakeStatus, bytesConsumed, bytesProduced, -1);
    }

    /**
     * Initializes a new instance of this class.
     *
     * @param   status
     *          the return value of the operation.
     *
     * @param   handshakeStatus
     *          the current handshaking status.
     *
     * @param   bytesConsumed
     *          the number of bytes consumed from the source ByteBuffer
     *
     * @param   bytesProduced
     *          the number of bytes placed into the destination ByteBuffer
     *
     * @param   sequenceNumber
     *          the sequence number (unsigned long) of the produced or
     *          consumed SSL/TLS/DTLS record, or {@code -1L} if no record
     *          produced or consumed
     *
     * @throws  IllegalArgumentException
     *          if the {@code status} or {@code handshakeStatus}
     *          arguments are null, or if {@code bytesConsumed} or
     *          {@code bytesProduced} is negative
     *
     * @since   9
     */
    public SSLEngineResult(Status status, HandshakeStatus handshakeStatus,
            int bytesConsumed, int bytesProduced, long sequenceNumber) {

        if ((status == null) || (handshakeStatus == null) ||
                (bytesConsumed < 0) || (bytesProduced < 0)) {
            throw new IllegalArgumentException("Invalid Parameter(s)");
        }

        this.status = status;
        this.handshakeStatus = handshakeStatus;
        this.bytesConsumed = bytesConsumed;
        this.bytesProduced = bytesProduced;
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the return value of this {@code SSLEngine} operation.
     *
     * @return  the return value
     */
    public final Status getStatus() {
        return status;
    }

    /**
     * Gets the handshake status of this {@code SSLEngine}
     * operation.
     *
     * @return  the handshake status
     */
    public final HandshakeStatus getHandshakeStatus() {
        return handshakeStatus;
    }

    /**
     * Returns the number of bytes consumed from the input buffer.
     *
     * @return  the number of bytes consumed.
     */
    public final int bytesConsumed() {
        return bytesConsumed;
    }

    /**
     * Returns the number of bytes written to the output buffer.
     *
     * @return  the number of bytes produced
     */
    public final int bytesProduced() {
        return bytesProduced;
    }

    /**
     * Returns the sequence number of the produced or consumed SSL/TLS/DTLS
     * record (optional operation).
     *
     * @apiNote  Note that sequence number is an unsigned long and cannot
     *           exceed {@code -1L}.  It is desired to use the unsigned
     *           long comparing mode for comparison of unsigned long values
     *           (see also {@link java.lang.Long#compareUnsigned(long, long)
     *           Long.compareUnsigned()}).
     *           <P>
     *           For DTLS protocols, the first 16 bits of the sequence
     *           number is a counter value (epoch) that is incremented on
     *           every cipher state change.  The remaining 48 bits on the
     *           right side of the sequence number represents the sequence
     *           of the record, which is maintained separately for each epoch.
     *
     * @implNote It is recommended that providers should never allow the
     *           sequence number incremented to {@code -1L}.  If the sequence
     *           number is close to wrapping, renegotiate should be requested,
     *           otherwise the connection should be closed immediately.
     *           This should be carried on automatically by the underlying
     *           implementation.
     *
     * @return  the sequence number of the produced or consumed SSL/TLS/DTLS
     *          record; or {@code -1L} if no record is produced or consumed,
     *          or this operation is not supported by the underlying provider
     *
     * @see     java.lang.Long#compareUnsigned(long, long)
     *
     * @since   9
     */
    public final long sequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Returns a String representation of this object.
     */
    @Override
    public String toString() {
        return ("Status = " + status +
            " HandshakeStatus = " + handshakeStatus +
            "\nbytesConsumed = " + bytesConsumed +
            " bytesProduced = " + bytesProduced +
            (sequenceNumber == -1 ? "" :
                " sequenceNumber = " + Long.toUnsignedString(sequenceNumber)));
    }
}
