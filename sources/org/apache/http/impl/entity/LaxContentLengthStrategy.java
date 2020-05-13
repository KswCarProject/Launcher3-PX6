package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxContentLengthStrategy implements ContentLengthStrategy {
    public static final LaxContentLengthStrategy INSTANCE = new LaxContentLengthStrategy();
    private final int implicitLen;

    public LaxContentLengthStrategy(int implicitLen2) {
        this.implicitLen = implicitLen2;
    }

    public LaxContentLengthStrategy() {
        this(-1);
    }

    public long determineLength(HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            try {
                HeaderElement[] encodings = transferEncodingHeader.getElements();
                int len = encodings.length;
                if (HTTP.IDENTITY_CODING.equalsIgnoreCase(transferEncodingHeader.getValue())) {
                    return -1;
                }
                return (len <= 0 || !HTTP.CHUNK_CODING.equalsIgnoreCase(encodings[len + -1].getName())) ? -1 : -2;
            } catch (ParseException px) {
                throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, px);
            }
        } else if (message.getFirstHeader("Content-Length") == null) {
            return (long) this.implicitLen;
        } else {
            long contentlen = -1;
            Header[] headers = message.getHeaders("Content-Length");
            int i = headers.length - 1;
            while (i >= 0) {
                try {
                    contentlen = Long.parseLong(headers[i].getValue());
                    break;
                } catch (NumberFormatException e) {
                    i--;
                }
            }
            if (contentlen < 0) {
                return -1;
            }
            return contentlen;
        }
    }
}
