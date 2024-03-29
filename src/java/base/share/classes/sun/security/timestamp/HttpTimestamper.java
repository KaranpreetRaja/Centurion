/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.timestamp;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.base.share.classes.sun.security.util.Debug;

/**
 * A timestamper that communicates with a Timestamping Authority (TSA)
 * over HTTP.
 * It supports the Time-Stamp Protocol defined in:
 * <a href="http://www.ietf.org/rfc/rfc3161.txt">RFC 3161</a>.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public class HttpTimestamper implements Timestamper {

    private static final int CONNECT_TIMEOUT = 15000; // 15 seconds

    // The MIME type for a timestamp query
    private static final String TS_QUERY_MIME_TYPE =
        "application/timestamp-query";

    // The MIME type for a timestamp reply
    private static final String TS_REPLY_MIME_TYPE =
        "application/timestamp-reply";

    private static final Debug debug = Debug.getInstance("ts");

    /*
     * HTTP URI identifying the location of the TSA
     */
    private final URI tsaURI;

    /**
     * Creates a timestamper that connects to the specified TSA.
     *
     * @param tsaURI The location of the TSA. It must be an HTTP or HTTPS URI.
     * @throws IllegalArgumentException if tsaURI is not an HTTP or HTTPS URI
     */
    public HttpTimestamper(URI tsaURI) {
        if (!tsaURI.getScheme().equalsIgnoreCase("http") &&
                !tsaURI.getScheme().equalsIgnoreCase("https")) {
            throw new IllegalArgumentException(
                    "TSA must be an HTTP or HTTPS URI");
        }
        this.tsaURI = tsaURI;
    }

    /**
     * Connects to the TSA and requests a timestamp.
     *
     * @param tsQuery The timestamp query.
     * @return The result of the timestamp query.
     * @throws IOException The exception is thrown if a problem occurs while
     *         communicating with the TSA.
     */
    public TSResponse generateTimestamp(TSRequest tsQuery) throws IOException {

        HttpURLConnection connection =
            (HttpURLConnection) tsaURI.toURL().openConnection();
        connection.setDoOutput(true);
        connection.setUseCaches(false); // ignore cache
        connection.setRequestProperty("Content-Type", TS_QUERY_MIME_TYPE);
        connection.setRequestMethod("POST");
        // Avoids the "hang" when a proxy is required but none has been set.
        connection.setConnectTimeout(CONNECT_TIMEOUT);

        if (debug != null) {
            Set<Map.Entry<String, List<String>>> headers =
                connection.getRequestProperties().entrySet();
            debug.println(connection.getRequestMethod() + " " + tsaURI +
                " HTTP/1.1");
            for (Map.Entry<String, List<String>> e : headers) {
                debug.println("  " + e);
            }
            debug.println();
        }
        connection.connect(); // No HTTP authentication is performed

        // Send the request
        try (var output = new DataOutputStream(connection.getOutputStream())) {
            byte[] request = tsQuery.encode();
            output.write(request, 0, request.length);
            output.flush();
            if (debug != null) {
                debug.println("sent timestamp query (length=" +
                        request.length + ")");
            }
        }

        // Receive the reply
        byte[] replyBuffer;
        try (var input = connection.getInputStream()) {
            if (debug != null) {
                String header = connection.getHeaderField(0);
                debug.println(header);
                int i = 1;
                while ((header = connection.getHeaderField(i)) != null) {
                    String key = connection.getHeaderFieldKey(i);
                    debug.println("  " + ((key==null) ? "" : key + ": ") +
                        header);
                    i++;
                }
                debug.println();
            }
            verifyMimeType(connection.getContentType());

            int clen = connection.getContentLength();
            replyBuffer = input.readAllBytes();
            if (clen != -1 && replyBuffer.length != clen)
                throw new EOFException("Expected:" + clen +
                                       ", read:" + replyBuffer.length);

            if (debug != null) {
                debug.println("received timestamp response (length=" +
                        replyBuffer.length + ")");
            }
        }
        return new TSResponse(replyBuffer);
    }

    /*
     * Checks that the MIME content type is a timestamp reply.
     *
     * @param contentType The MIME content type to be checked.
     * @throws IOException The exception is thrown if a mismatch occurs.
     */
    private static void verifyMimeType(String contentType) throws IOException {
        if (! TS_REPLY_MIME_TYPE.equalsIgnoreCase(contentType)) {
            throw new IOException("MIME Content-Type is not " +
                TS_REPLY_MIME_TYPE);
        }
    }
}
