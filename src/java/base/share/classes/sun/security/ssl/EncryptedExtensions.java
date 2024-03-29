/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Locale;
import java.base.share.classes.sun.security.ssl.SSLHandshake.HandshakeMessage;

/**
 * Pack of the EncryptedExtensions handshake message.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

final class EncryptedExtensions {
    static final HandshakeProducer handshakeProducer =
        new EncryptedExtensionsProducer();
    static final SSLConsumer handshakeConsumer =
        new EncryptedExtensionsConsumer();

    /**
     * The EncryptedExtensions handshake message.
     */
    static final class EncryptedExtensionsMessage extends HandshakeMessage {
        private final SSLExtensions extensions;

        EncryptedExtensionsMessage(
                HandshakeContext handshakeContext) {
            super(handshakeContext);
            this.extensions = new SSLExtensions(this);
        }

        EncryptedExtensionsMessage(HandshakeContext handshakeContext,
                ByteBuffer m) throws IOException {
            super(handshakeContext);

            // struct {
            //     Extension extensions<0..2^16-1>;
            // } EncryptedExtensions;
            if (m.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER,
                        "Invalid EncryptedExtensions handshake message: " +
                        "no sufficient data");
            }

            SSLExtension[] encryptedExtensions =
                    handshakeContext.sslConfig.getEnabledExtensions(
                            SSLHandshake.ENCRYPTED_EXTENSIONS);
            this.extensions = new SSLExtensions(this, m, encryptedExtensions);
        }

        @Override
        SSLHandshake handshakeType() {
            return SSLHandshake.ENCRYPTED_EXTENSIONS;
        }

        @Override
        int messageLength() {
            int extLen = extensions.length();
            if (extLen == 0) {
                extLen = 2;     // empty extensions
            }
            return extLen;
        }

        @Override
        void send(HandshakeOutStream hos) throws IOException {
            // Is it an empty extensions?
            if (extensions.length() == 0) {
                hos.putInt16(0);
            } else {
                extensions.send(hos);
            }
        }

        @Override
        public String toString() {
            MessageFormat messageFormat = new MessageFormat(
                    """
                            "EncryptedExtensions": [
                            {0}
                            ]""",
                    Locale.ENGLISH);
            Object[] messageFields = {
                Utilities.indent(extensions.toString())
            };

            return messageFormat.format(messageFields);
        }
    }

    /**
     * The EncryptedExtensions handshake message consumer.
     */
    private static final class EncryptedExtensionsProducer
            implements HandshakeProducer {
        // Prevent instantiation of this class.
        private EncryptedExtensionsProducer() {
            // blank
        }

        @Override
        public byte[] produce(ConnectionContext context,
                HandshakeMessage message) throws IOException {
            // The producing happens in server side only.
            ServerHandshakeContext shc = (ServerHandshakeContext)context;

            EncryptedExtensionsMessage eem =
                    new EncryptedExtensionsMessage(shc);
            SSLExtension[] extTypes =
                    shc.sslConfig.getEnabledExtensions(
                            SSLHandshake.ENCRYPTED_EXTENSIONS,
                            shc.negotiatedProtocol);
            eem.extensions.produce(shc, extTypes);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced EncryptedExtensions message", eem);
            }

            // Output the handshake message.
            eem.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();

            // The handshake message has been delivered.
            return null;
        }
    }

    /**
     * The EncryptedExtensions handshake message consumer.
     */
    private static final class EncryptedExtensionsConsumer
            implements SSLConsumer {
        // Prevent instantiation of this class.
        private EncryptedExtensionsConsumer() {
            // blank
        }

        @Override
        public void consume(ConnectionContext context,
                ByteBuffer message) throws IOException {
            // The consuming happens in client side only.
            ClientHandshakeContext chc = (ClientHandshakeContext)context;

            // clean up this consumer
            chc.handshakeConsumers.remove(SSLHandshake.ENCRYPTED_EXTENSIONS.id);

            EncryptedExtensionsMessage eem =
                    new EncryptedExtensionsMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine(
                        "Consuming EncryptedExtensions handshake message", eem);
            }

            //
            // validate
            //
            SSLExtension[] extTypes = chc.sslConfig.getEnabledExtensions(
                    SSLHandshake.ENCRYPTED_EXTENSIONS);
            eem.extensions.consumeOnLoad(chc, extTypes);

            //
            // update
            //
            eem.extensions.consumeOnTrade(chc, extTypes);

            //
            // produce
            //
            // Need no new handshake message producers here.
        }
    }
}
