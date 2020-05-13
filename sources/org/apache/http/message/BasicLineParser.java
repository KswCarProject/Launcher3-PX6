package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicLineParser implements LineParser {
    @Deprecated
    public static final BasicLineParser DEFAULT = new BasicLineParser();
    public static final BasicLineParser INSTANCE = new BasicLineParser();
    protected final ProtocolVersion protocol;

    public BasicLineParser(ProtocolVersion proto) {
        this.protocol = proto == null ? HttpVersion.HTTP_1_1 : proto;
    }

    public BasicLineParser() {
        this((ProtocolVersion) null);
    }

    public static ProtocolVersion parseProtocolVersion(String value, LineParser parser) throws ParseException {
        Args.notNull(value, "Value");
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        ParserCursor cursor = new ParserCursor(0, value.length());
        if (parser == null) {
            parser = INSTANCE;
        }
        return parser.parseProtocolVersion(buffer, cursor);
    }

    public ProtocolVersion parseProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        String protoname = this.protocol.getProtocol();
        int protolength = protoname.length();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        skipWhitespace(buffer, cursor);
        int i = cursor.getPos();
        if (i + protolength + 4 > indexTo) {
            throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
        }
        boolean ok = true;
        int j = 0;
        while (ok && j < protolength) {
            ok = buffer.charAt(i + j) == protoname.charAt(j);
            j++;
        }
        if (ok) {
            ok = buffer.charAt(i + protolength) == '/';
        }
        if (!ok) {
            throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
        }
        int i2 = i + protolength + 1;
        int period = buffer.indexOf(46, i2, indexTo);
        if (period == -1) {
            throw new ParseException("Invalid protocol version number: " + buffer.substring(indexFrom, indexTo));
        }
        try {
            int major = Integer.parseInt(buffer.substringTrimmed(i2, period));
            int i3 = period + 1;
            int blank = buffer.indexOf(32, i3, indexTo);
            if (blank == -1) {
                blank = indexTo;
            }
            try {
                int minor = Integer.parseInt(buffer.substringTrimmed(i3, blank));
                cursor.updatePos(blank);
                return createProtocolVersion(major, minor);
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid protocol minor version number: " + buffer.substring(indexFrom, indexTo));
            }
        } catch (NumberFormatException e2) {
            throw new ParseException("Invalid protocol major version number: " + buffer.substring(indexFrom, indexTo));
        }
    }

    /* access modifiers changed from: protected */
    public ProtocolVersion createProtocolVersion(int major, int minor) {
        return this.protocol.forVersion(major, minor);
    }

    public boolean hasProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        int index = cursor.getPos();
        String protoname = this.protocol.getProtocol();
        int protolength = protoname.length();
        if (buffer.length() < protolength + 4) {
            return false;
        }
        if (index < 0) {
            index = (buffer.length() - 4) - protolength;
        } else if (index == 0) {
            while (index < buffer.length() && HTTP.isWhitespace(buffer.charAt(index))) {
                index++;
            }
        }
        if (index + protolength + 4 > buffer.length()) {
            return false;
        }
        boolean ok = true;
        int j = 0;
        while (ok && j < protolength) {
            if (buffer.charAt(index + j) == protoname.charAt(j)) {
                ok = true;
            } else {
                ok = false;
            }
            j++;
        }
        if (ok) {
            if (buffer.charAt(index + protolength) == '/') {
                ok = true;
            } else {
                ok = false;
            }
        }
        return ok;
    }

    public static RequestLine parseRequestLine(String value, LineParser parser) throws ParseException {
        Args.notNull(value, "Value");
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        ParserCursor cursor = new ParserCursor(0, value.length());
        if (parser == null) {
            parser = INSTANCE;
        }
        return parser.parseRequestLine(buffer, cursor);
    }

    public RequestLine parseRequestLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        try {
            skipWhitespace(buffer, cursor);
            int i = cursor.getPos();
            int blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
            }
            String method = buffer.substringTrimmed(i, blank);
            cursor.updatePos(blank);
            skipWhitespace(buffer, cursor);
            int i2 = cursor.getPos();
            int blank2 = buffer.indexOf(32, i2, indexTo);
            if (blank2 < 0) {
                throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
            }
            String uri = buffer.substringTrimmed(i2, blank2);
            cursor.updatePos(blank2);
            ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
            skipWhitespace(buffer, cursor);
            if (cursor.atEnd()) {
                return createRequestLine(method, uri, ver);
            }
            throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
        }
    }

    /* access modifiers changed from: protected */
    public RequestLine createRequestLine(String method, String uri, ProtocolVersion ver) {
        return new BasicRequestLine(method, uri, ver);
    }

    public static StatusLine parseStatusLine(String value, LineParser parser) throws ParseException {
        Args.notNull(value, "Value");
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        ParserCursor cursor = new ParserCursor(0, value.length());
        if (parser == null) {
            parser = INSTANCE;
        }
        return parser.parseStatusLine(buffer, cursor);
    }

    public StatusLine parseStatusLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        String reasonPhrase;
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        try {
            ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
            skipWhitespace(buffer, cursor);
            int i = cursor.getPos();
            int blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                blank = indexTo;
            }
            String s = buffer.substringTrimmed(i, blank);
            for (int j = 0; j < s.length(); j++) {
                if (!Character.isDigit(s.charAt(j))) {
                    throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
                }
            }
            int statusCode = Integer.parseInt(s);
            int i2 = blank;
            if (i2 < indexTo) {
                reasonPhrase = buffer.substringTrimmed(i2, indexTo);
            } else {
                reasonPhrase = "";
            }
            return createStatusLine(ver, statusCode, reasonPhrase);
        } catch (NumberFormatException e) {
            throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
        } catch (IndexOutOfBoundsException e2) {
            throw new ParseException("Invalid status line: " + buffer.substring(indexFrom, indexTo));
        }
    }

    /* access modifiers changed from: protected */
    public StatusLine createStatusLine(ProtocolVersion ver, int status, String reason) {
        return new BasicStatusLine(ver, status, reason);
    }

    public static Header parseHeader(String value, LineParser parser) throws ParseException {
        Args.notNull(value, "Value");
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        if (parser == null) {
            parser = INSTANCE;
        }
        return parser.parseHeader(buffer);
    }

    public Header parseHeader(CharArrayBuffer buffer) throws ParseException {
        return new BufferedHeader(buffer);
    }

    /* access modifiers changed from: protected */
    public void skipWhitespace(CharArrayBuffer buffer, ParserCursor cursor) {
        int pos = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        while (pos < indexTo && HTTP.isWhitespace(buffer.charAt(pos))) {
            pos++;
        }
        cursor.updatePos(pos);
    }
}
