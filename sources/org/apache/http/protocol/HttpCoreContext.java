package org.apache.http.protocol;

import org.apache.http.HttpConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.Args;

public class HttpCoreContext implements HttpContext {
    public static final String HTTP_CONNECTION = "http.connection";
    public static final String HTTP_REQUEST = "http.request";
    public static final String HTTP_REQ_SENT = "http.request_sent";
    public static final String HTTP_RESPONSE = "http.response";
    public static final String HTTP_TARGET_HOST = "http.target_host";
    private final HttpContext context;

    public static HttpCoreContext create() {
        return new HttpCoreContext(new BasicHttpContext());
    }

    public static HttpCoreContext adapt(HttpContext context2) {
        Args.notNull(context2, "HTTP context");
        if (context2 instanceof HttpCoreContext) {
            return (HttpCoreContext) context2;
        }
        return new HttpCoreContext(context2);
    }

    public HttpCoreContext(HttpContext context2) {
        this.context = context2;
    }

    public HttpCoreContext() {
        this.context = new BasicHttpContext();
    }

    public Object getAttribute(String id) {
        return this.context.getAttribute(id);
    }

    public void setAttribute(String id, Object obj) {
        this.context.setAttribute(id, obj);
    }

    public Object removeAttribute(String id) {
        return this.context.removeAttribute(id);
    }

    public <T> T getAttribute(String attribname, Class<T> clazz) {
        Args.notNull(clazz, "Attribute class");
        Object obj = getAttribute(attribname);
        if (obj == null) {
            return null;
        }
        return clazz.cast(obj);
    }

    public <T extends HttpConnection> T getConnection(Class<T> clazz) {
        return (HttpConnection) getAttribute("http.connection", clazz);
    }

    public HttpConnection getConnection() {
        return (HttpConnection) getAttribute("http.connection", HttpConnection.class);
    }

    public HttpRequest getRequest() {
        return (HttpRequest) getAttribute("http.request", HttpRequest.class);
    }

    public boolean isRequestSent() {
        Boolean b = (Boolean) getAttribute("http.request_sent", Boolean.class);
        return b != null && b.booleanValue();
    }

    public HttpResponse getResponse() {
        return (HttpResponse) getAttribute("http.response", HttpResponse.class);
    }

    public void setTargetHost(HttpHost host) {
        setAttribute("http.target_host", host);
    }

    public HttpHost getTargetHost() {
        return (HttpHost) getAttribute("http.target_host", HttpHost.class);
    }
}
