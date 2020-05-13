package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequestFactory;
import org.apache.http.ParseException;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
public class HttpRequestParser extends AbstractMessageParser<HttpMessage> {
    private final CharArrayBuffer lineBuf = new CharArrayBuffer(128);
    private final HttpRequestFactory requestFactory;

    public HttpRequestParser(SessionInputBuffer buffer, LineParser parser, HttpRequestFactory requestFactory2, HttpParams params) {
        super(buffer, parser, params);
        this.requestFactory = (HttpRequestFactory) Args.notNull(requestFactory2, "Request factory");
    }

    /* access modifiers changed from: protected */
    public HttpMessage parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        if (sessionBuffer.readLine(this.lineBuf) == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        return this.requestFactory.newHttpRequest(this.lineParser.parseRequestLine(this.lineBuf, new ParserCursor(0, this.lineBuf.length())));
    }
}
