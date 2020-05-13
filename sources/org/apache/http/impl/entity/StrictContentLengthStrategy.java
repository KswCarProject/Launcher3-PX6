package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class StrictContentLengthStrategy implements ContentLengthStrategy {
    public static final StrictContentLengthStrategy INSTANCE = new StrictContentLengthStrategy();
    private final int implicitLen;

    public StrictContentLengthStrategy(int implicitLen2) {
        this.implicitLen = implicitLen2;
    }

    public StrictContentLengthStrategy() {
        this(-1);
    }

    public long determineLength(HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            String s = transferEncodingHeader.getValue();
            if (HTTP.CHUNK_CODING.equalsIgnoreCase(s)) {
                if (!message.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                    return -2;
                }
                throw new ProtocolException("Chunked transfer encoding not allowed for " + message.getProtocolVersion());
            } else if (HTTP.IDENTITY_CODING.equalsIgnoreCase(s)) {
                return -1;
            } else {
                throw new ProtocolException("Unsupported transfer encoding: " + s);
            }
        } else {
            Header contentLengthHeader = message.getFirstHeader("Content-Length");
            if (contentLengthHeader == null) {
                return (long) this.implicitLen;
            }
            String s2 = contentLengthHeader.getValue();
            try {
                long len = Long.parseLong(s2);
                if (len >= 0) {
                    return len;
                }
                throw new ProtocolException("Negative content length: " + s2);
            } catch (NumberFormatException e) {
                throw new ProtocolException("Invalid content length: " + s2);
            }
        }
    }
}
