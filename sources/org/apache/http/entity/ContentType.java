package org.apache.http.entity;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.TextUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class ContentType implements Serializable {
    public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
    public static final ContentType APPLICATION_FORM_URLENCODED = create(URLEncodedUtils.CONTENT_TYPE, Consts.ISO_8859_1);
    public static final ContentType APPLICATION_JSON = create("application/json", Consts.UTF_8);
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset) null);
    public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
    public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
    public static final ContentType APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
    private static final Map<String, ContentType> CONTENT_TYPE_MAP;
    public static final ContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;
    public static final ContentType DEFAULT_TEXT = TEXT_PLAIN;
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
    public static final ContentType TEXT_HTML = create("text/html", Consts.ISO_8859_1);
    public static final ContentType TEXT_PLAIN = create(HTTP.PLAIN_TEXT_TYPE, Consts.ISO_8859_1);
    public static final ContentType TEXT_XML = create("text/xml", Consts.ISO_8859_1);
    public static final ContentType WILDCARD = create("*/*", (Charset) null);
    private static final long serialVersionUID = -7768694718232371896L;
    private final Charset charset;
    private final String mimeType;
    private final NameValuePair[] params;

    static {
        ContentType[] contentTypes = {APPLICATION_ATOM_XML, APPLICATION_FORM_URLENCODED, APPLICATION_JSON, APPLICATION_SVG_XML, APPLICATION_XHTML_XML, APPLICATION_XML, MULTIPART_FORM_DATA, TEXT_HTML, TEXT_PLAIN, TEXT_XML};
        HashMap<String, ContentType> map = new HashMap<>();
        for (ContentType contentType : contentTypes) {
            map.put(contentType.getMimeType(), contentType);
        }
        CONTENT_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    ContentType(String mimeType2, Charset charset2) {
        this.mimeType = mimeType2;
        this.charset = charset2;
        this.params = null;
    }

    ContentType(String mimeType2, Charset charset2, NameValuePair[] params2) {
        this.mimeType = mimeType2;
        this.charset = charset2;
        this.params = params2;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getParameter(String name) {
        Args.notEmpty(name, "Parameter name");
        if (this.params == null) {
            return null;
        }
        for (NameValuePair param : this.params) {
            if (param.getName().equalsIgnoreCase(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    public String toString() {
        CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(this.mimeType);
        if (this.params != null) {
            buf.append("; ");
            BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
        } else if (this.charset != null) {
            buf.append(HTTP.CHARSET_PARAM);
            buf.append(this.charset.name());
        }
        return buf.toString();
    }

    private static boolean valid(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\"' || ch == ',' || ch == ';') {
                return false;
            }
        }
        return true;
    }

    public static ContentType create(String mimeType2, Charset charset2) {
        String normalizedMimeType = ((String) Args.notBlank(mimeType2, "MIME type")).toLowerCase(Locale.ROOT);
        Args.check(valid(normalizedMimeType), "MIME type may not contain reserved characters");
        return new ContentType(normalizedMimeType, charset2);
    }

    public static ContentType create(String mimeType2) {
        return create(mimeType2, (Charset) null);
    }

    public static ContentType create(String mimeType2, String charset2) throws UnsupportedCharsetException {
        return create(mimeType2, !TextUtils.isBlank(charset2) ? Charset.forName(charset2) : null);
    }

    private static ContentType create(HeaderElement helem, boolean strict) {
        return create(helem.getName(), helem.getParameters(), strict);
    }

    private static ContentType create(String mimeType2, NameValuePair[] params2, boolean strict) {
        Charset charset2 = null;
        NameValuePair[] arr$ = params2;
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            NameValuePair param = arr$[i$];
            if (param.getName().equalsIgnoreCase("charset")) {
                String s = param.getValue();
                if (!TextUtils.isBlank(s)) {
                    try {
                        charset2 = Charset.forName(s);
                    } catch (UnsupportedCharsetException ex) {
                        if (strict) {
                            throw ex;
                        }
                    }
                }
            } else {
                i$++;
            }
        }
        if (params2 == null || params2.length <= 0) {
            params2 = null;
        }
        return new ContentType(mimeType2, charset2, params2);
    }

    public static ContentType create(String mimeType2, NameValuePair... params2) throws UnsupportedCharsetException {
        Args.check(valid(((String) Args.notBlank(mimeType2, "MIME type")).toLowerCase(Locale.ROOT)), "MIME type may not contain reserved characters");
        return create(mimeType2, params2, true);
    }

    public static ContentType parse(String s) throws ParseException, UnsupportedCharsetException {
        Args.notNull(s, "Content type");
        CharArrayBuffer buf = new CharArrayBuffer(s.length());
        buf.append(s);
        HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buf, new ParserCursor(0, s.length()));
        if (elements.length > 0) {
            return create(elements[0], true);
        }
        throw new ParseException("Invalid content type: " + s);
    }

    public static ContentType get(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        Header header;
        if (entity == null || (header = entity.getContentType()) == null) {
            return null;
        }
        HeaderElement[] elements = header.getElements();
        if (elements.length > 0) {
            return create(elements[0], true);
        }
        return null;
    }

    public static ContentType getLenient(HttpEntity entity) {
        Header header;
        if (entity == null || (header = entity.getContentType()) == null) {
            return null;
        }
        try {
            HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                return create(elements[0], false);
            }
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static ContentType getOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        ContentType contentType = get(entity);
        return contentType != null ? contentType : DEFAULT_TEXT;
    }

    public static ContentType getLenientOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        ContentType contentType = get(entity);
        return contentType != null ? contentType : DEFAULT_TEXT;
    }

    public static ContentType getByMimeType(String mimeType2) {
        if (mimeType2 == null) {
            return null;
        }
        return CONTENT_TYPE_MAP.get(mimeType2);
    }

    public ContentType withCharset(Charset charset2) {
        return create(getMimeType(), charset2);
    }

    public ContentType withCharset(String charset2) {
        return create(getMimeType(), charset2);
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public ContentType withParameters(NameValuePair... params2) throws UnsupportedCharsetException {
        if (params2.length == 0) {
            return this;
        }
        Map<String, String> paramMap = new LinkedHashMap<>();
        if (this.params != null) {
            for (NameValuePair param : this.params) {
                paramMap.put(param.getName(), param.getValue());
            }
        }
        for (NameValuePair param2 : params2) {
            paramMap.put(param2.getName(), param2.getValue());
        }
        List<NameValuePair> newParams = new ArrayList<>(paramMap.size() + 1);
        if (this.charset != null && !paramMap.containsKey("charset")) {
            newParams.add(new BasicNameValuePair("charset", this.charset.name()));
        }
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            newParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return create(getMimeType(), (NameValuePair[]) newParams.toArray(new NameValuePair[newParams.size()]), true);
    }
}
