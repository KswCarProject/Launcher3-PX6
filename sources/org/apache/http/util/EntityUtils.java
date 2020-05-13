package org.apache.http.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

public final class EntityUtils {
    private EntityUtils() {
    }

    public static void consumeQuietly(HttpEntity entity) {
        try {
            consume(entity);
        } catch (IOException e) {
        }
    }

    public static void consume(HttpEntity entity) throws IOException {
        InputStream instream;
        if (entity != null && entity.isStreaming() && (instream = entity.getContent()) != null) {
            instream.close();
        }
    }

    public static void updateEntity(HttpResponse response, HttpEntity entity) throws IOException {
        Args.notNull(response, "Response");
        consume(response.getEntity());
        response.setEntity(entity);
    }

    public static byte[] toByteArray(HttpEntity entity) throws IOException {
        boolean z = false;
        Args.notNull(entity, "Entity");
        InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            if (entity.getContentLength() <= 2147483647L) {
                z = true;
            }
            Args.check(z, "HTTP entity too large to be buffered in memory");
            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            byte[] tmp = new byte[4096];
            while (true) {
                int l = instream.read(tmp);
                if (l == -1) {
                    return buffer.toByteArray();
                }
                buffer.append(tmp, 0, l);
            }
        } finally {
            instream.close();
        }
    }

    @Deprecated
    public static String getContentCharSet(HttpEntity entity) throws ParseException {
        NameValuePair param;
        Args.notNull(entity, "Entity");
        if (entity.getContentType() == null) {
            return null;
        }
        HeaderElement[] values = entity.getContentType().getElements();
        if (values.length <= 0 || (param = values[0].getParameterByName("charset")) == null) {
            return null;
        }
        return param.getValue();
    }

    @Deprecated
    public static String getContentMimeType(HttpEntity entity) throws ParseException {
        Args.notNull(entity, "Entity");
        if (entity.getContentType() == null) {
            return null;
        }
        HeaderElement[] values = entity.getContentType().getElements();
        if (values.length > 0) {
            return values[0].getName();
        }
        return null;
    }

    private static String toString(HttpEntity entity, ContentType contentType) throws IOException {
        String str = null;
        boolean z = false;
        InputStream instream = entity.getContent();
        if (instream != null) {
            try {
                if (entity.getContentLength() <= 2147483647L) {
                    z = true;
                }
                Args.check(z, "HTTP entity too large to be buffered in memory");
                int i = (int) entity.getContentLength();
                if (i < 0) {
                    i = 4096;
                }
                Charset charset = null;
                if (contentType != null && (charset = contentType.getCharset()) == null) {
                    ContentType defaultContentType = ContentType.getByMimeType(contentType.getMimeType());
                    if (defaultContentType != null) {
                        charset = defaultContentType.getCharset();
                    } else {
                        charset = null;
                    }
                }
                if (charset == null) {
                    charset = HTTP.DEF_CONTENT_CHARSET;
                }
                Reader reader = new InputStreamReader(instream, charset);
                CharArrayBuffer buffer = new CharArrayBuffer(i);
                char[] tmp = new char[1024];
                while (true) {
                    int l = reader.read(tmp);
                    if (l == -1) {
                        break;
                    }
                    buffer.append(tmp, 0, l);
                }
                str = buffer.toString();
            } finally {
                instream.close();
            }
        }
        return str;
    }

    public static String toString(HttpEntity entity, Charset defaultCharset) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        ContentType contentType = null;
        try {
            contentType = ContentType.get(entity);
        } catch (UnsupportedCharsetException ex) {
            if (defaultCharset == null) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
        }
        if (contentType == null) {
            contentType = ContentType.DEFAULT_TEXT.withCharset(defaultCharset);
        } else if (contentType.getCharset() == null) {
            contentType = contentType.withCharset(defaultCharset);
        }
        return toString(entity, contentType);
    }

    public static String toString(HttpEntity entity, String defaultCharset) throws IOException, ParseException {
        return toString(entity, defaultCharset != null ? Charset.forName(defaultCharset) : null);
    }

    public static String toString(HttpEntity entity) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        return toString(entity, ContentType.get(entity));
    }
}
