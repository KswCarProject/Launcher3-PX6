package org.apache.http.client.entity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class UrlEncodedFormEntity extends StringEntity {
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UrlEncodedFormEntity(List<? extends NameValuePair> parameters, String charset) throws UnsupportedEncodingException {
        super(URLEncodedUtils.format(parameters, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create(URLEncodedUtils.CONTENT_TYPE, charset));
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters, Charset charset) {
        super(URLEncodedUtils.format(parameters, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET), ContentType.create(URLEncodedUtils.CONTENT_TYPE, charset));
    }

    public UrlEncodedFormEntity(List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
        this((Iterable<? extends NameValuePair>) parameters, (Charset) null);
    }

    public UrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters) {
        this(parameters, (Charset) null);
    }
}
