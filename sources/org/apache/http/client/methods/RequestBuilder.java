package org.apache.http.client.methods;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

public class RequestBuilder {
    private Charset charset;
    private RequestConfig config;
    private HttpEntity entity;
    private HeaderGroup headergroup;
    private String method;
    private List<NameValuePair> parameters;
    private URI uri;
    private ProtocolVersion version;

    RequestBuilder(String method2) {
        this.charset = Consts.UTF_8;
        this.method = method2;
    }

    RequestBuilder(String method2, URI uri2) {
        this.method = method2;
        this.uri = uri2;
    }

    RequestBuilder(String method2, String uri2) {
        this.method = method2;
        this.uri = uri2 != null ? URI.create(uri2) : null;
    }

    RequestBuilder() {
        this((String) null);
    }

    public static RequestBuilder create(String method2) {
        Args.notBlank(method2, "HTTP method");
        return new RequestBuilder(method2);
    }

    public static RequestBuilder get() {
        return new RequestBuilder(HttpGet.METHOD_NAME);
    }

    public static RequestBuilder get(URI uri2) {
        return new RequestBuilder(HttpGet.METHOD_NAME, uri2);
    }

    public static RequestBuilder get(String uri2) {
        return new RequestBuilder(HttpGet.METHOD_NAME, uri2);
    }

    public static RequestBuilder head() {
        return new RequestBuilder(HttpHead.METHOD_NAME);
    }

    public static RequestBuilder head(URI uri2) {
        return new RequestBuilder(HttpHead.METHOD_NAME, uri2);
    }

    public static RequestBuilder head(String uri2) {
        return new RequestBuilder(HttpHead.METHOD_NAME, uri2);
    }

    public static RequestBuilder patch() {
        return new RequestBuilder(HttpPatch.METHOD_NAME);
    }

    public static RequestBuilder patch(URI uri2) {
        return new RequestBuilder(HttpPatch.METHOD_NAME, uri2);
    }

    public static RequestBuilder patch(String uri2) {
        return new RequestBuilder(HttpPatch.METHOD_NAME, uri2);
    }

    public static RequestBuilder post() {
        return new RequestBuilder(HttpPost.METHOD_NAME);
    }

    public static RequestBuilder post(URI uri2) {
        return new RequestBuilder(HttpPost.METHOD_NAME, uri2);
    }

    public static RequestBuilder post(String uri2) {
        return new RequestBuilder(HttpPost.METHOD_NAME, uri2);
    }

    public static RequestBuilder put() {
        return new RequestBuilder(HttpPut.METHOD_NAME);
    }

    public static RequestBuilder put(URI uri2) {
        return new RequestBuilder(HttpPut.METHOD_NAME, uri2);
    }

    public static RequestBuilder put(String uri2) {
        return new RequestBuilder(HttpPut.METHOD_NAME, uri2);
    }

    public static RequestBuilder delete() {
        return new RequestBuilder(HttpDelete.METHOD_NAME);
    }

    public static RequestBuilder delete(URI uri2) {
        return new RequestBuilder(HttpDelete.METHOD_NAME, uri2);
    }

    public static RequestBuilder delete(String uri2) {
        return new RequestBuilder(HttpDelete.METHOD_NAME, uri2);
    }

    public static RequestBuilder trace() {
        return new RequestBuilder(HttpTrace.METHOD_NAME);
    }

    public static RequestBuilder trace(URI uri2) {
        return new RequestBuilder(HttpTrace.METHOD_NAME, uri2);
    }

    public static RequestBuilder trace(String uri2) {
        return new RequestBuilder(HttpTrace.METHOD_NAME, uri2);
    }

    public static RequestBuilder options() {
        return new RequestBuilder(HttpOptions.METHOD_NAME);
    }

    public static RequestBuilder options(URI uri2) {
        return new RequestBuilder(HttpOptions.METHOD_NAME, uri2);
    }

    public static RequestBuilder options(String uri2) {
        return new RequestBuilder(HttpOptions.METHOD_NAME, uri2);
    }

    public static RequestBuilder copy(HttpRequest request) {
        Args.notNull(request, "HTTP request");
        return new RequestBuilder().doCopy(request);
    }

    private RequestBuilder doCopy(HttpRequest request) {
        if (request != null) {
            this.method = request.getRequestLine().getMethod();
            this.version = request.getRequestLine().getProtocolVersion();
            if (this.headergroup == null) {
                this.headergroup = new HeaderGroup();
            }
            this.headergroup.clear();
            this.headergroup.setHeaders(request.getAllHeaders());
            this.parameters = null;
            this.entity = null;
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity originalEntity = ((HttpEntityEnclosingRequest) request).getEntity();
                ContentType contentType = ContentType.get(originalEntity);
                if (contentType == null || !contentType.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
                    this.entity = originalEntity;
                } else {
                    try {
                        List<NameValuePair> formParams = URLEncodedUtils.parse(originalEntity);
                        if (!formParams.isEmpty()) {
                            this.parameters = formParams;
                        }
                    } catch (IOException e) {
                    }
                }
            }
            if (request instanceof HttpUriRequest) {
                this.uri = ((HttpUriRequest) request).getURI();
            } else {
                this.uri = URI.create(request.getRequestLine().getUri());
            }
            if (request instanceof Configurable) {
                this.config = ((Configurable) request).getConfig();
            } else {
                this.config = null;
            }
        }
        return this;
    }

    public RequestBuilder setCharset(Charset charset2) {
        this.charset = charset2;
        return this;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getMethod() {
        return this.method;
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }

    public RequestBuilder setVersion(ProtocolVersion version2) {
        this.version = version2;
        return this;
    }

    public URI getUri() {
        return this.uri;
    }

    public RequestBuilder setUri(URI uri2) {
        this.uri = uri2;
        return this;
    }

    public RequestBuilder setUri(String uri2) {
        this.uri = uri2 != null ? URI.create(uri2) : null;
        return this;
    }

    public Header getFirstHeader(String name) {
        if (this.headergroup != null) {
            return this.headergroup.getFirstHeader(name);
        }
        return null;
    }

    public Header getLastHeader(String name) {
        if (this.headergroup != null) {
            return this.headergroup.getLastHeader(name);
        }
        return null;
    }

    public Header[] getHeaders(String name) {
        if (this.headergroup != null) {
            return this.headergroup.getHeaders(name);
        }
        return null;
    }

    public RequestBuilder addHeader(Header header) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.addHeader(header);
        return this;
    }

    public RequestBuilder addHeader(String name, String value) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.addHeader(new BasicHeader(name, value));
        return this;
    }

    public RequestBuilder removeHeader(Header header) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.removeHeader(header);
        return this;
    }

    public RequestBuilder removeHeaders(String name) {
        if (!(name == null || this.headergroup == null)) {
            HeaderIterator i = this.headergroup.iterator();
            while (i.hasNext()) {
                if (name.equalsIgnoreCase(i.nextHeader().getName())) {
                    i.remove();
                }
            }
        }
        return this;
    }

    public RequestBuilder setHeader(Header header) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.updateHeader(header);
        return this;
    }

    public RequestBuilder setHeader(String name, String value) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.updateHeader(new BasicHeader(name, value));
        return this;
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public RequestBuilder setEntity(HttpEntity entity2) {
        this.entity = entity2;
        return this;
    }

    public List<NameValuePair> getParameters() {
        return this.parameters != null ? new ArrayList(this.parameters) : new ArrayList();
    }

    public RequestBuilder addParameter(NameValuePair nvp) {
        Args.notNull(nvp, "Name value pair");
        if (this.parameters == null) {
            this.parameters = new LinkedList();
        }
        this.parameters.add(nvp);
        return this;
    }

    public RequestBuilder addParameter(String name, String value) {
        return addParameter(new BasicNameValuePair(name, value));
    }

    public RequestBuilder addParameters(NameValuePair... nvps) {
        for (NameValuePair nvp : nvps) {
            addParameter(nvp);
        }
        return this;
    }

    public RequestConfig getConfig() {
        return this.config;
    }

    public RequestBuilder setConfig(RequestConfig config2) {
        this.config = config2;
        return this;
    }

    public HttpUriRequest build() {
        HttpRequestBase result;
        Charset charset2;
        URI uriNotNull = this.uri != null ? this.uri : URI.create("/");
        HttpEntity entityCopy = this.entity;
        if (this.parameters != null && !this.parameters.isEmpty()) {
            if (entityCopy != null || (!HttpPost.METHOD_NAME.equalsIgnoreCase(this.method) && !HttpPut.METHOD_NAME.equalsIgnoreCase(this.method))) {
                try {
                    uriNotNull = new URIBuilder(uriNotNull).setCharset(this.charset).addParameters(this.parameters).build();
                } catch (URISyntaxException e) {
                }
            } else {
                List<NameValuePair> list = this.parameters;
                if (this.charset != null) {
                    charset2 = this.charset;
                } else {
                    charset2 = HTTP.DEF_CONTENT_CHARSET;
                }
                entityCopy = new UrlEncodedFormEntity((Iterable<? extends NameValuePair>) list, charset2);
            }
        }
        if (entityCopy == null) {
            result = new InternalRequest(this.method);
        } else {
            InternalEntityEclosingRequest request = new InternalEntityEclosingRequest(this.method);
            request.setEntity(entityCopy);
            result = request;
        }
        result.setProtocolVersion(this.version);
        result.setURI(uriNotNull);
        if (this.headergroup != null) {
            result.setHeaders(this.headergroup.getAllHeaders());
        }
        result.setConfig(this.config);
        return result;
    }

    static class InternalRequest extends HttpRequestBase {
        private final String method;

        InternalRequest(String method2) {
            this.method = method2;
        }

        public String getMethod() {
            return this.method;
        }
    }

    static class InternalEntityEclosingRequest extends HttpEntityEnclosingRequestBase {
        private final String method;

        InternalEntityEclosingRequest(String method2) {
            this.method = method2;
        }

        public String getMethod() {
            return this.method;
        }
    }
}
