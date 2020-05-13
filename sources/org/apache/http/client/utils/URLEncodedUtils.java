package org.apache.http.client.utils;

import android.support.v4.media.TransportMediator;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class URLEncodedUtils {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final BitSet PATHSAFE = new BitSet(256);
    private static final BitSet PUNCT = new BitSet(256);
    private static final char QP_SEP_A = '&';
    private static final char QP_SEP_S = ';';
    private static final int RADIX = 16;
    private static final BitSet RESERVED = new BitSet(256);
    private static final BitSet UNRESERVED = new BitSet(256);
    private static final BitSet URIC = new BitSet(256);
    private static final BitSet URLENCODER = new BitSet(256);
    private static final BitSet USERINFO = new BitSet(256);

    public static List<NameValuePair> parse(URI uri, String charsetName) {
        return parse(uri, charsetName != null ? Charset.forName(charsetName) : null);
    }

    public static List<NameValuePair> parse(URI uri, Charset charset) {
        Args.notNull(uri, "URI");
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        return parse(query, charset);
    }

    /* JADX INFO: finally extract failed */
    public static List<NameValuePair> parse(HttpEntity entity) throws IOException {
        Args.notNull(entity, "HTTP entity");
        ContentType contentType = ContentType.get(entity);
        if (contentType == null || !contentType.getMimeType().equalsIgnoreCase(CONTENT_TYPE)) {
            return Collections.emptyList();
        }
        long len = entity.getContentLength();
        Args.check(len <= 2147483647L, "HTTP entity is too large");
        Charset charset = contentType.getCharset() != null ? contentType.getCharset() : HTTP.DEF_CONTENT_CHARSET;
        InputStream instream = entity.getContent();
        if (instream == null) {
            return Collections.emptyList();
        }
        try {
            CharArrayBuffer buf = new CharArrayBuffer(len > 0 ? (int) len : 1024);
            Reader reader = new InputStreamReader(instream, charset);
            char[] tmp = new char[1024];
            while (true) {
                int l = reader.read(tmp);
                if (l == -1) {
                    break;
                }
                buf.append(tmp, 0, l);
            }
            instream.close();
            if (buf.length() == 0) {
                return Collections.emptyList();
            }
            return parse(buf, charset, QP_SEP_A);
        } catch (Throwable th) {
            instream.close();
            throw th;
        }
    }

    public static boolean isEncoded(HttpEntity entity) {
        Args.notNull(entity, "HTTP entity");
        Header h = entity.getContentType();
        if (h == null) {
            return false;
        }
        HeaderElement[] elems = h.getElements();
        if (elems.length > 0) {
            return elems[0].getName().equalsIgnoreCase(CONTENT_TYPE);
        }
        return false;
    }

    @Deprecated
    public static void parse(List<NameValuePair> parameters, Scanner scanner, String charset) {
        parse(parameters, scanner, "[&;]", charset);
    }

    @Deprecated
    public static void parse(List<NameValuePair> parameters, Scanner scanner, String parameterSepartorPattern, String charset) {
        String name;
        String value;
        scanner.useDelimiter(parameterSepartorPattern);
        while (scanner.hasNext()) {
            String token = scanner.next();
            int i = token.indexOf(NAME_VALUE_SEPARATOR);
            if (i != -1) {
                name = decodeFormFields(token.substring(0, i).trim(), charset);
                value = decodeFormFields(token.substring(i + 1).trim(), charset);
            } else {
                name = decodeFormFields(token.trim(), charset);
                value = null;
            }
            parameters.add(new BasicNameValuePair(name, value));
        }
    }

    public static List<NameValuePair> parse(String s, Charset charset) {
        if (s == null) {
            return Collections.emptyList();
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, charset, QP_SEP_A, QP_SEP_S);
    }

    public static List<NameValuePair> parse(String s, Charset charset, char... separators) {
        if (s == null) {
            return Collections.emptyList();
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, charset, separators);
    }

    public static List<NameValuePair> parse(CharArrayBuffer buf, Charset charset, char... separators) {
        Args.notNull(buf, "Char array buffer");
        TokenParser tokenParser = TokenParser.INSTANCE;
        BitSet delimSet = new BitSet();
        for (char separator : separators) {
            delimSet.set(separator);
        }
        ParserCursor cursor = new ParserCursor(0, buf.length());
        List<NameValuePair> list = new ArrayList<>();
        while (!cursor.atEnd()) {
            delimSet.set(61);
            String name = tokenParser.parseToken(buf, cursor, delimSet);
            String value = null;
            if (!cursor.atEnd()) {
                int delim = buf.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (delim == 61) {
                    delimSet.clear(61);
                    value = tokenParser.parseValue(buf, cursor, delimSet);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
            if (!name.isEmpty()) {
                list.add(new BasicNameValuePair(decodeFormFields(name, charset), decodeFormFields(value, charset)));
            }
        }
        return list;
    }

    public static String format(List<? extends NameValuePair> parameters, String charset) {
        return format(parameters, (char) QP_SEP_A, charset);
    }

    public static String format(List<? extends NameValuePair> parameters, char parameterSeparator, String charset) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair parameter : parameters) {
            String encodedName = encodeFormFields(parameter.getName(), charset);
            String encodedValue = encodeFormFields(parameter.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    public static String format(Iterable<? extends NameValuePair> parameters, Charset charset) {
        return format(parameters, (char) QP_SEP_A, charset);
    }

    public static String format(Iterable<? extends NameValuePair> parameters, char parameterSeparator, Charset charset) {
        Args.notNull(parameters, "Parameters");
        StringBuilder result = new StringBuilder();
        for (NameValuePair parameter : parameters) {
            String encodedName = encodeFormFields(parameter.getName(), charset);
            String encodedValue = encodeFormFields(parameter.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    static {
        for (int i = 97; i <= 122; i++) {
            UNRESERVED.set(i);
        }
        for (int i2 = 65; i2 <= 90; i2++) {
            UNRESERVED.set(i2);
        }
        for (int i3 = 48; i3 <= 57; i3++) {
            UNRESERVED.set(i3);
        }
        UNRESERVED.set(95);
        UNRESERVED.set(45);
        UNRESERVED.set(46);
        UNRESERVED.set(42);
        URLENCODER.or(UNRESERVED);
        UNRESERVED.set(33);
        UNRESERVED.set(TransportMediator.KEYCODE_MEDIA_PLAY);
        UNRESERVED.set(39);
        UNRESERVED.set(40);
        UNRESERVED.set(41);
        PUNCT.set(44);
        PUNCT.set(59);
        PUNCT.set(58);
        PUNCT.set(36);
        PUNCT.set(38);
        PUNCT.set(43);
        PUNCT.set(61);
        USERINFO.or(UNRESERVED);
        USERINFO.or(PUNCT);
        PATHSAFE.or(UNRESERVED);
        PATHSAFE.set(47);
        PATHSAFE.set(59);
        PATHSAFE.set(58);
        PATHSAFE.set(64);
        PATHSAFE.set(38);
        PATHSAFE.set(61);
        PATHSAFE.set(43);
        PATHSAFE.set(36);
        PATHSAFE.set(44);
        RESERVED.set(59);
        RESERVED.set(47);
        RESERVED.set(63);
        RESERVED.set(58);
        RESERVED.set(64);
        RESERVED.set(38);
        RESERVED.set(61);
        RESERVED.set(43);
        RESERVED.set(36);
        RESERVED.set(44);
        RESERVED.set(91);
        RESERVED.set(93);
        URIC.or(RESERVED);
        URIC.or(UNRESERVED);
    }

    private static String urlEncode(String content, Charset charset, BitSet safechars, boolean blankAsPlus) {
        if (content == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        ByteBuffer bb = charset.encode(content);
        while (bb.hasRemaining()) {
            int b = bb.get() & 255;
            if (safechars.get(b)) {
                buf.append((char) b);
            } else if (!blankAsPlus || b != 32) {
                buf.append("%");
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 15, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 15, 16));
                buf.append(hex1);
                buf.append(hex2);
            } else {
                buf.append('+');
            }
        }
        return buf.toString();
    }

    private static String urlDecode(String content, Charset charset, boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.allocate(content.length());
        CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                char uc = cb.get();
                char lc = cb.get();
                int u = Character.digit(uc, 16);
                int l = Character.digit(lc, 16);
                if (u == -1 || l == -1) {
                    bb.put((byte) 37);
                    bb.put((byte) uc);
                    bb.put((byte) lc);
                } else {
                    bb.put((byte) ((u << 4) + l));
                }
            } else if (!plusAsBlank || c != '+') {
                bb.put((byte) c);
            } else {
                bb.put((byte) 32);
            }
        }
        bb.flip();
        return charset.decode(bb).toString();
    }

    private static String decodeFormFields(String content, String charset) {
        if (content == null) {
            return null;
        }
        return urlDecode(content, charset != null ? Charset.forName(charset) : Consts.UTF_8, true);
    }

    private static String decodeFormFields(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        if (charset == null) {
            charset = Consts.UTF_8;
        }
        return urlDecode(content, charset, true);
    }

    private static String encodeFormFields(String content, String charset) {
        if (content == null) {
            return null;
        }
        return urlEncode(content, charset != null ? Charset.forName(charset) : Consts.UTF_8, URLENCODER, true);
    }

    private static String encodeFormFields(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        if (charset == null) {
            charset = Consts.UTF_8;
        }
        return urlEncode(content, charset, URLENCODER, true);
    }

    static String encUserInfo(String content, Charset charset) {
        return urlEncode(content, charset, USERINFO, false);
    }

    static String encUric(String content, Charset charset) {
        return urlEncode(content, charset, URIC, false);
    }

    static String encPath(String content, Charset charset) {
        return urlEncode(content, charset, PATHSAFE, false);
    }
}
