package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

public class HttpRequestWrapper extends AbstractHttpMessage implements HttpUriRequest {
    private final String method;
    private final HttpRequest original;
    private RequestLine requestLine;
    private final HttpHost target;
    private URI uri;
    private ProtocolVersion version;

    private HttpRequestWrapper(HttpRequest request, HttpHost target2) {
        this.original = (HttpRequest) Args.notNull(request, "HTTP request");
        this.target = target2;
        this.version = this.original.getRequestLine().getProtocolVersion();
        this.method = this.original.getRequestLine().getMethod();
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest) request).getURI();
        } else {
            this.uri = null;
        }
        setHeaders(request.getAllHeaders());
    }

    public ProtocolVersion getProtocolVersion() {
        return this.version != null ? this.version : this.original.getProtocolVersion();
    }

    public void setProtocolVersion(ProtocolVersion version2) {
        this.version = version2;
        this.requestLine = null;
    }

    public URI getURI() {
        return this.uri;
    }

    public void setURI(URI uri2) {
        this.uri = uri2;
        this.requestLine = null;
    }

    public String getMethod() {
        return this.method;
    }

    public void abort() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean isAborted() {
        return false;
    }

    public RequestLine getRequestLine() {
        String requestUri;
        if (this.requestLine == null) {
            if (this.uri != null) {
                requestUri = this.uri.toASCIIString();
            } else {
                requestUri = this.original.getRequestLine().getUri();
            }
            if (requestUri == null || requestUri.isEmpty()) {
                requestUri = "/";
            }
            this.requestLine = new BasicRequestLine(this.method, requestUri, getProtocolVersion());
        }
        return this.requestLine;
    }

    public HttpRequest getOriginal() {
        return this.original;
    }

    public HttpHost getTarget() {
        return this.target;
    }

    public String toString() {
        return getRequestLine() + " " + this.headergroup;
    }

    static class HttpEntityEnclosingRequestWrapper extends HttpRequestWrapper implements HttpEntityEnclosingRequest {
        private HttpEntity entity;

        HttpEntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request, HttpHost target) {
            super(request, target);
            this.entity = request.getEntity();
        }

        public HttpEntity getEntity() {
            return this.entity;
        }

        public void setEntity(HttpEntity entity2) {
            this.entity = entity2;
        }

        public boolean expectContinue() {
            Header expect = getFirstHeader("Expect");
            return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
        }
    }

    public static HttpRequestWrapper wrap(HttpRequest request) {
        return wrap(request, (HttpHost) null);
    }

    public static HttpRequestWrapper wrap(HttpRequest request, HttpHost target2) {
        Args.notNull(request, "HTTP request");
        if (request instanceof HttpEntityEnclosingRequest) {
            return new HttpEntityEnclosingRequestWrapper((HttpEntityEnclosingRequest) request, target2);
        }
        return new HttpRequestWrapper(request, target2);
    }

    @Deprecated
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = this.original.getParams().copy();
        }
        return this.params;
    }
}
