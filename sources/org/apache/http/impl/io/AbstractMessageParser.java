package org.apache.http.impl.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.MessageConstraintException;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.TokenParser;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public abstract class AbstractMessageParser<T extends HttpMessage> implements HttpMessageParser<T> {
    private static final int HEADERS = 1;
    private static final int HEAD_LINE = 0;
    private final List<CharArrayBuffer> headerLines;
    protected final LineParser lineParser;
    private T message;
    private final MessageConstraints messageConstraints;
    private final SessionInputBuffer sessionBuffer;
    private int state;

    /* access modifiers changed from: protected */
    public abstract T parseHead(SessionInputBuffer sessionInputBuffer) throws IOException, HttpException, ParseException;

    @Deprecated
    public AbstractMessageParser(SessionInputBuffer buffer, LineParser parser, HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        Args.notNull(params, "HTTP parameters");
        this.sessionBuffer = buffer;
        this.messageConstraints = HttpParamConfig.getMessageConstraints(params);
        this.lineParser = parser == null ? BasicLineParser.INSTANCE : parser;
        this.headerLines = new ArrayList();
        this.state = 0;
    }

    public AbstractMessageParser(SessionInputBuffer buffer, LineParser lineParser2, MessageConstraints constraints) {
        this.sessionBuffer = (SessionInputBuffer) Args.notNull(buffer, "Session input buffer");
        this.lineParser = lineParser2 == null ? BasicLineParser.INSTANCE : lineParser2;
        this.messageConstraints = constraints == null ? MessageConstraints.DEFAULT : constraints;
        this.headerLines = new ArrayList();
        this.state = 0;
    }

    public static Header[] parseHeaders(SessionInputBuffer inbuffer, int maxHeaderCount, int maxLineLen, LineParser parser) throws HttpException, IOException {
        List<CharArrayBuffer> headerLines2 = new ArrayList<>();
        if (parser == null) {
            parser = BasicLineParser.INSTANCE;
        }
        return parseHeaders(inbuffer, maxHeaderCount, maxLineLen, parser, headerLines2);
    }

    public static Header[] parseHeaders(SessionInputBuffer inbuffer, int maxHeaderCount, int maxLineLen, LineParser parser, List<CharArrayBuffer> headerLines2) throws HttpException, IOException {
        Args.notNull(inbuffer, "Session input buffer");
        Args.notNull(parser, "Line parser");
        Args.notNull(headerLines2, "Header line list");
        CharArrayBuffer current = null;
        CharArrayBuffer previous = null;
        while (true) {
            if (current == null) {
                current = new CharArrayBuffer(64);
            } else {
                current.clear();
            }
            if (inbuffer.readLine(current) == -1 || current.length() < 1) {
                Header[] headers = new Header[headerLines2.size()];
                int i = 0;
            } else {
                if ((current.charAt(0) == ' ' || current.charAt(0) == 9) && previous != null) {
                    int i2 = 0;
                    while (i2 < current.length() && ((ch = current.charAt(i2)) == ' ' || ch == 9)) {
                        i2++;
                    }
                    if (maxLineLen <= 0 || ((previous.length() + 1) + current.length()) - i2 <= maxLineLen) {
                        previous.append((char) TokenParser.SP);
                        previous.append(current, i2, current.length() - i2);
                    } else {
                        throw new MessageConstraintException("Maximum line length limit exceeded");
                    }
                } else {
                    headerLines2.add(current);
                    previous = current;
                    current = null;
                }
                if (maxHeaderCount > 0 && headerLines2.size() >= maxHeaderCount) {
                    throw new MessageConstraintException("Maximum header count exceeded");
                }
            }
        }
        Header[] headers2 = new Header[headerLines2.size()];
        int i3 = 0;
        while (i3 < headerLines2.size()) {
            try {
                headers2[i3] = parser.parseHeader(headerLines2.get(i3));
                i3++;
            } catch (ParseException ex) {
                throw new ProtocolException(ex.getMessage());
            }
        }
        return headers2;
    }

    public T parse() throws IOException, HttpException {
        switch (this.state) {
            case 0:
                try {
                    this.message = parseHead(this.sessionBuffer);
                    this.state = 1;
                    break;
                } catch (ParseException px) {
                    throw new ProtocolException(px.getMessage(), px);
                }
            case 1:
                break;
            default:
                throw new IllegalStateException("Inconsistent parser state");
        }
        this.message.setHeaders(parseHeaders(this.sessionBuffer, this.messageConstraints.getMaxHeaderCount(), this.messageConstraints.getMaxLineLength(), this.lineParser, this.headerLines));
        T result = this.message;
        this.message = null;
        this.headerLines.clear();
        this.state = 0;
        return result;
    }
}
