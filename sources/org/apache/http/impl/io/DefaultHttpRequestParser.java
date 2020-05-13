package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.ParseException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class DefaultHttpRequestParser extends AbstractMessageParser<HttpRequest> {
    private final CharArrayBuffer lineBuf;
    private final HttpRequestFactory requestFactory;

    @Deprecated
    public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory2, HttpParams params) {
        super(buffer, lineParser, params);
        this.requestFactory = (HttpRequestFactory) Args.notNull(requestFactory2, "Request factory");
        this.lineBuf = new CharArrayBuffer(128);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory2, MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        this.requestFactory = requestFactory2 == null ? DefaultHttpRequestFactory.INSTANCE : requestFactory2;
        this.lineBuf = new CharArrayBuffer(128);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        this(buffer, (LineParser) null, (HttpRequestFactory) null, constraints);
    }

    public DefaultHttpRequestParser(SessionInputBuffer buffer) {
        this(buffer, (LineParser) null, (HttpRequestFactory) null, MessageConstraints.DEFAULT);
    }

    /* access modifiers changed from: protected */
    public HttpRequest parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        if (sessionBuffer.readLine(this.lineBuf) == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        return this.requestFactory.newHttpRequest(this.lineParser.parseRequestLine(this.lineBuf, new ParserCursor(0, this.lineBuf.length())));
    }
}
