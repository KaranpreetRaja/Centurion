/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.security.auth.callback;

/**
 * <p> Underlying security services instantiate and pass a
 * {@code TextOutputCallback} to the {@code handle}
 * method of a {@code CallbackHandler} to display information messages,
 * warning messages and error messages.
 *
 * @since 1.4
 * @see java.base.share.classes.javax.security.auth.callback.CallbackHandler
 */
public class TextOutputCallback implements Callback, java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1689502495511663102L;

    /** Information message. */
    public static final int INFORMATION         = 0;
    /** Warning message. */
    public static final int WARNING             = 1;
    /** Error message. */
    public static final int ERROR               = 2;

    /**
     * @serial
     * @since 1.4
     */
    private final int messageType;
    /**
     * @serial
     * @since 1.4
     */
    private final String message;

    /**
     * Construct a TextOutputCallback with a message type and message
     * to be displayed.
     *
     * @param messageType the message type ({@code INFORMATION},
     *                  {@code WARNING} or {@code ERROR}).
     *
     * @param message the message to be displayed.
     *
     * @exception IllegalArgumentException if {@code messageType}
     *                  is not either {@code INFORMATION},
     *                  {@code WARNING} or {@code ERROR},
     *                  if {@code message} is null,
     *                  or if {@code message} has a length of 0.
     */
    public TextOutputCallback(int messageType, String message) {
        if ((messageType != INFORMATION &&
                messageType != WARNING && messageType != ERROR) ||
            message == null || message.isEmpty())
            throw new IllegalArgumentException();

        this.messageType = messageType;
        this.message = message;
    }

    /**
     * Get the message type.
     *
     * @return the message type ({@code INFORMATION},
     *                  {@code WARNING} or {@code ERROR}).
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Get the message to be displayed.
     *
     * @return the message to be displayed.
     */
    public String getMessage() {
        return message;
    }
}
