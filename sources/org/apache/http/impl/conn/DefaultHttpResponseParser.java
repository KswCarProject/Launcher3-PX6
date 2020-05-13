package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse> {
    private final CharArrayBuffer lineBuf;
    private final Log log;
    private final HttpResponseFactory responseFactory;

    @Deprecated
    public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory2, HttpParams params) {
        super(buffer, parser, params);
        this.log = LogFactory.getLog(getClass());
        Args.notNull(responseFactory2, "Response factory");
        this.responseFactory = responseFactory2;
        this.lineBuf = new CharArrayBuffer(128);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory2, MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        this.log = LogFactory.getLog(getClass());
        this.responseFactory = responseFactory2 == null ? DefaultHttpResponseFactory.INSTANCE : responseFactory2;
        this.lineBuf = new CharArrayBuffer(128);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        this(buffer, (LineParser) null, (HttpResponseFactory) null, constraints);
    }

    public DefaultHttpResponseParser(SessionInputBuffer buffer) {
        this(buffer, (LineParser) null, (HttpResponseFactory) null, MessageConstraints.DEFAULT);
    }

    /* access modifiers changed from: protected */
    public HttpResponse parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException {
        int count = 0;
        while (true) {
            this.lineBuf.clear();
            int i = sessionBuffer.readLine(this.lineBuf);
            if (i == -1 && count == 0) {
                throw new NoHttpResponseException("The target server failed to respond");
            }
            ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
            if (this.lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
                return this.responseFactory.newHttpResponse(this.lineParser.parseStatusLine(this.lineBuf, cursor), (HttpContext) null);
            } else if (i != -1 && !reject(this.lineBuf, count)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Garbage in response: " + this.lineBuf.toString());
                }
                count++;
            }
        }
        throw new ProtocolException("The server failed to respond with a valid HTTP response");
    }

    /* access modifiers changed from: protected */
    public boolean reject(CharArrayBuffer line, int count) {
        return false;
    }
}
