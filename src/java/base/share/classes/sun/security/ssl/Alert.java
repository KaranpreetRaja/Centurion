/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Locale;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;

/**
 * SSL/(D)TLS Alter description
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
enum Alert {
    // Please refer to TLS Alert Registry for the latest (D)TLS Alert values:
    //     https://www.iana.org/assignments/tls-parameters/
    CLOSE_NOTIFY            ((byte)0,   "close_notify", false),
    UNEXPECTED_MESSAGE      ((byte)10,  "unexpected_message", false),
    BAD_RECORD_MAC          ((byte)20,  "bad_record_mac", false),
    DECRYPTION_FAILED       ((byte)21,  "decryption_failed", false),
    RECORD_OVERFLOW         ((byte)22,  "record_overflow", false),
    DECOMPRESSION_FAILURE   ((byte)30,  "decompression_failure", false),
    HANDSHAKE_FAILURE       ((byte)40,  "handshake_failure", true),
    NO_CERTIFICATE          ((byte)41,  "no_certificate", true),
    BAD_CERTIFICATE         ((byte)42,  "bad_certificate", true),
    UNSUPPORTED_CERTIFICATE ((byte)43,  "unsupported_certificate", true),
    CERTIFICATE_REVOKED     ((byte)44,  "certificate_revoked", true),
    CERTIFICATE_EXPIRED     ((byte)45,  "certificate_expired", true),
    CERTIFICATE_UNKNOWN     ((byte)46,  "certificate_unknown", true),
    ILLEGAL_PARAMETER       ((byte)47,  "illegal_parameter", true),
    UNKNOWN_CA              ((byte)48,  "unknown_ca", true),
    ACCESS_DENIED           ((byte)49,  "access_denied", true),
    DECODE_ERROR            ((byte)50,  "decode_error", true),
    DECRYPT_ERROR           ((byte)51,  "decrypt_error", true),
    EXPORT_RESTRICTION      ((byte)60,  "export_restriction", true),
    PROTOCOL_VERSION        ((byte)70,  "protocol_version", true),
    INSUFFICIENT_SECURITY   ((byte)71,  "insufficient_security", true),
    INTERNAL_ERROR          ((byte)80,  "internal_error", false),
    INAPPROPRIATE_FALLBACK  ((byte)86,  "inappropriate_fallback", false),
    USER_CANCELED           ((byte)90,  "user_canceled", false),
    NO_RENEGOTIATION        ((byte)100, "no_renegotiation", true),
    MISSING_EXTENSION       ((byte)109, "missing_extension", true),
    UNSUPPORTED_EXTENSION   ((byte)110, "unsupported_extension", true),
    CERT_UNOBTAINABLE       ((byte)111, "certificate_unobtainable", true),
    UNRECOGNIZED_NAME       ((byte)112, "unrecognized_name", true),
    BAD_CERT_STATUS_RESPONSE((byte)113,
                                    "bad_certificate_status_response", true),
    BAD_CERT_HASH_VALUE     ((byte)114, "bad_certificate_hash_value", true),
    UNKNOWN_PSK_IDENTITY    ((byte)115, "unknown_psk_identity", true),
    CERTIFICATE_REQUIRED    ((byte)116, "certificate_required", true),
    NO_APPLICATION_PROTOCOL ((byte)120, "no_application_protocol", true);

    // ordinal value of the Alert
    final byte id;

    // description of the Alert
    final String description;

    // Does the alert happen during handshake only?
    final boolean handshakeOnly;

    // Alert message consumer
    static final SSLConsumer alertConsumer = new AlertConsumer();

    Alert(byte id, String description, boolean handshakeOnly) {
        this.id = id;
        this.description = description;
        this.handshakeOnly = handshakeOnly;
    }

    static Alert valueOf(byte id) {
        for (Alert al : Alert.values()) {
            if (al.id == id) {
                return al;
            }
        }

        return null;
    }

    static String nameOf(byte id) {
        for (Alert al : Alert.values()) {
            if (al.id == id) {
                return al.description;
            }
        }

        return "UNKNOWN ALERT (" + (id & 0x0FF) + ")";
    }

    SSLException createSSLException(String reason) {
        return createSSLException(reason, null);
    }

    SSLException createSSLException(String reason, Throwable cause) {
        if (reason == null) {
            reason = (cause != null) ? cause.getMessage() : "";
        }

        if (cause instanceof IOException) {
            return new SSLException(reason, cause);
        } else if ((this == UNEXPECTED_MESSAGE)) {
            return new SSLProtocolException(reason, cause);
        } else if (handshakeOnly) {
            return new SSLHandshakeException(reason, cause);
        } else {
            return new SSLException(reason, cause);
        }
    }

    /**
     * SSL/(D)TLS Alert level.
     */
    enum Level {
        WARNING ((byte)1, "warning"),
        FATAL   ((byte)2, "fatal");

        // ordinal value of the Alert level
        final byte level;

        // description of the Alert level
        final String description;

        Level(byte level, String description) {
            this.level = level;
            this.description = description;
        }

        static Level valueOf(byte level) {
            for (Level lv : Level.values()) {
                if (lv.level == level) {
                    return lv;
                }
            }

            return null;
        }

        static String nameOf(byte level) {
            for (Level lv : Level.values()) {
                if (lv.level == level) {
                    return lv.description;
                }
            }

            return "UNKNOWN ALERT LEVEL (" + (level & 0x0FF) + ")";
        }
    }

    /**
     * The Alert message.
     */
    private static final class AlertMessage {
        private final byte level;       // level
        private final byte id;          // description

        AlertMessage(TransportContext context,
                ByteBuffer m) throws IOException {
            //  struct {
            //      AlertLevel level;
            //      AlertDescription description;
            //  } Alert;
            if (m.remaining() != 2) {
                throw context.fatal(Alert.ILLEGAL_PARAMETER,
                    "Invalid Alert message: no sufficient data");
            }

            this.level = m.get();   // level
            this.id = m.get();      // description
        }

        @Override
        public String toString() {
            MessageFormat messageFormat = new MessageFormat(
                    """
                            "Alert": '{'
                              "level"      : "{0}",
                              "description": "{1}"
                            '}'""",
                    Locale.ENGLISH);

            Object[] messageFields = {
                Level.nameOf(level),
                Alert.nameOf(id)
            };

            return messageFormat.format(messageFields);
        }
    }

    /**
     * Consumer of alert messages
     */
    private static final class AlertConsumer implements SSLConsumer {
        // Prevent instantiation of this class.
        private AlertConsumer() {
            // blank
        }

        @Override
        public void consume(ConnectionContext context,
                ByteBuffer m) throws IOException {
            TransportContext tc = (TransportContext)context;

            AlertMessage am = new AlertMessage(tc, m);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Received alert message", am);
            }

            Level level = Level.valueOf(am.level);
            Alert alert = Alert.valueOf(am.id);
            if (alert == Alert.CLOSE_NOTIFY) {
                tc.isInputCloseNotified = true;
                tc.closeInbound();

                if (tc.peerUserCanceled) {
                    tc.closeOutbound();
                } else if (tc.handshakeContext != null) {
                    throw tc.fatal(Alert.UNEXPECTED_MESSAGE,
                            "Received close_notify during handshake");
                }
            } else if (alert == Alert.USER_CANCELED) {
                if (level == Level.WARNING) {
                    tc.peerUserCanceled = true;
                } else {
                    throw tc.fatal(alert,
                            "Received fatal close_notify alert", true, null);
                }
            } else if ((level == Level.WARNING) && (alert != null)) {
                // Terminate the connection if an alert with a level of warning
                // is received during handshaking, except the no_certificate
                // warning.
                if (alert.handshakeOnly && (tc.handshakeContext != null)) {
                    // It's OK to get a no_certificate alert from a client of
                    // which we requested client authentication.  However,
                    // if we required it, then this is not acceptable.
                    if (tc.sslConfig.isClientMode ||
                            alert != Alert.NO_CERTIFICATE ||
                            (tc.sslConfig.clientAuthType !=
                                    ClientAuthType.CLIENT_AUTH_REQUESTED)) {
                        throw tc.fatal(Alert.HANDSHAKE_FAILURE,
                            "received handshake warning: " + alert.description);
                    } else {
                        // Otherwise, ignore the warning but remove the
                        // Certificate and CertificateVerify handshake
                        // consumer so the state machine doesn't expect it.
                        tc.handshakeContext.handshakeConsumers.remove(
                                SSLHandshake.CERTIFICATE.id);
                        tc.handshakeContext.handshakeConsumers.remove(
                                SSLHandshake.CERTIFICATE_VERIFY.id);
                    }
                }  // Otherwise, ignore the warning
            } else {    // fatal or unknown
                String diagnostic;
                if (alert == null) {
                    alert = Alert.UNEXPECTED_MESSAGE;
                    diagnostic = "Unknown alert description (" + am.id + ")";
                } else {
                    diagnostic = "Received fatal alert: " + alert.description;
                }

                throw tc.fatal(alert, diagnostic, true, null);
            }
        }
    }
}
