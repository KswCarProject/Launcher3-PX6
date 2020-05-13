package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class HttpService {
    private volatile ConnectionReuseStrategy connStrategy;
    private volatile HttpExpectationVerifier expectationVerifier;
    private volatile HttpRequestHandlerMapper handlerMapper;
    private volatile HttpParams params;
    private volatile HttpProcessor processor;
    private volatile HttpResponseFactory responseFactory;

    @Deprecated
    public HttpService(HttpProcessor processor2, ConnectionReuseStrategy connStrategy2, HttpResponseFactory responseFactory2, HttpRequestHandlerResolver handlerResolver, HttpExpectationVerifier expectationVerifier2, HttpParams params2) {
        this(processor2, connStrategy2, responseFactory2, (HttpRequestHandlerMapper) new HttpRequestHandlerResolverAdapter(handlerResolver), expectationVerifier2);
        this.params = params2;
    }

    @Deprecated
    public HttpService(HttpProcessor processor2, ConnectionReuseStrategy connStrategy2, HttpResponseFactory responseFactory2, HttpRequestHandlerResolver handlerResolver, HttpParams params2) {
        this(processor2, connStrategy2, responseFactory2, (HttpRequestHandlerMapper) new HttpRequestHandlerResolverAdapter(handlerResolver), (HttpExpectationVerifier) null);
        this.params = params2;
    }

    @Deprecated
    public HttpService(HttpProcessor proc, ConnectionReuseStrategy connStrategy2, HttpResponseFactory responseFactory2) {
        this.params = null;
        this.processor = null;
        this.handlerMapper = null;
        this.connStrategy = null;
        this.responseFactory = null;
        this.expectationVerifier = null;
        setHttpProcessor(proc);
        setConnReuseStrategy(connStrategy2);
        setResponseFactory(responseFactory2);
    }

    public HttpService(HttpProcessor processor2, ConnectionReuseStrategy connStrategy2, HttpResponseFactory responseFactory2, HttpRequestHandlerMapper handlerMapper2, HttpExpectationVerifier expectationVerifier2) {
        this.params = null;
        this.processor = null;
        this.handlerMapper = null;
        this.connStrategy = null;
        this.responseFactory = null;
        this.expectationVerifier = null;
        this.processor = (HttpProcessor) Args.notNull(processor2, "HTTP processor");
        this.connStrategy = connStrategy2 == null ? DefaultConnectionReuseStrategy.INSTANCE : connStrategy2;
        this.responseFactory = responseFactory2 == null ? DefaultHttpResponseFactory.INSTANCE : responseFactory2;
        this.handlerMapper = handlerMapper2;
        this.expectationVerifier = expectationVerifier2;
    }

    public HttpService(HttpProcessor processor2, ConnectionReuseStrategy connStrategy2, HttpResponseFactory responseFactory2, HttpRequestHandlerMapper handlerMapper2) {
        this(processor2, connStrategy2, responseFactory2, handlerMapper2, (HttpExpectationVerifier) null);
    }

    public HttpService(HttpProcessor processor2, HttpRequestHandlerMapper handlerMapper2) {
        this(processor2, (ConnectionReuseStrategy) null, (HttpResponseFactory) null, handlerMapper2, (HttpExpectationVerifier) null);
    }

    @Deprecated
    public void setHttpProcessor(HttpProcessor processor2) {
        Args.notNull(processor2, "HTTP processor");
        this.processor = processor2;
    }

    @Deprecated
    public void setConnReuseStrategy(ConnectionReuseStrategy connStrategy2) {
        Args.notNull(connStrategy2, "Connection reuse strategy");
        this.connStrategy = connStrategy2;
    }

    @Deprecated
    public void setResponseFactory(HttpResponseFactory responseFactory2) {
        Args.notNull(responseFactory2, "Response factory");
        this.responseFactory = responseFactory2;
    }

    @Deprecated
    public void setParams(HttpParams params2) {
        this.params = params2;
    }

    @Deprecated
    public void setHandlerResolver(HttpRequestHandlerResolver handlerResolver) {
        this.handlerMapper = new HttpRequestHandlerResolverAdapter(handlerResolver);
    }

    @Deprecated
    public void setExpectationVerifier(HttpExpectationVerifier expectationVerifier2) {
        this.expectationVerifier = expectationVerifier2;
    }

    @Deprecated
    public HttpParams getParams() {
        return this.params;
    }

    public void handleRequest(HttpServerConnection conn, HttpContext context) throws IOException, HttpException {
        context.setAttribute("http.connection", conn);
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = conn.receiveRequestHeader();
            if (request instanceof HttpEntityEnclosingRequest) {
                if (((HttpEntityEnclosingRequest) request).expectContinue()) {
                    response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 100, context);
                    if (this.expectationVerifier != null) {
                        try {
                            this.expectationVerifier.verify(request, response, context);
                        } catch (HttpException ex) {
                            response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, HttpStatus.SC_INTERNAL_SERVER_ERROR, context);
                            handleException(ex, response);
                        }
                    }
                    if (response.getStatusLine().getStatusCode() < 200) {
                        conn.sendResponseHeader(response);
                        conn.flush();
                        response = null;
                        conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
                    }
                } else {
                    conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
                }
            }
            context.setAttribute("http.request", request);
            if (response == null) {
                response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 200, context);
                this.processor.process(request, context);
                doService(request, response, context);
            }
            if (request instanceof HttpEntityEnclosingRequest) {
                EntityUtils.consume(((HttpEntityEnclosingRequest) request).getEntity());
            }
        } catch (HttpException ex2) {
            response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, HttpStatus.SC_INTERNAL_SERVER_ERROR, context);
            handleException(ex2, response);
        }
        context.setAttribute("http.response", response);
        this.processor.process(response, context);
        conn.sendResponseHeader(response);
        if (canResponseHaveBody(request, response)) {
            conn.sendResponseEntity(response);
        }
        conn.flush();
        if (!this.connStrategy.keepAlive(response, context)) {
            conn.close();
        }
    }

    private boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
        int status;
        if ((request != null && HttpHead.METHOD_NAME.equalsIgnoreCase(request.getRequestLine().getMethod())) || (status = response.getStatusLine().getStatusCode()) < 200 || status == 204 || status == 304 || status == 205) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void handleException(HttpException ex, HttpResponse response) {
        if (ex instanceof MethodNotSupportedException) {
            response.setStatusCode(501);
        } else if (ex instanceof UnsupportedHttpVersionException) {
            response.setStatusCode(HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED);
        } else if (ex instanceof ProtocolException) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        } else {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        String message = ex.getMessage();
        if (message == null) {
            message = ex.toString();
        }
        ByteArrayEntity entity = new ByteArrayEntity(EncodingUtils.getAsciiBytes(message));
        entity.setContentType("text/plain; charset=US-ASCII");
        response.setEntity(entity);
    }

    /* access modifiers changed from: protected */
    public void doService(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRequestHandler handler = null;
        if (this.handlerMapper != null) {
            handler = this.handlerMapper.lookup(request);
        }
        if (handler != null) {
            handler.handle(request, response, context);
        } else {
            response.setStatusCode(501);
        }
    }

    @Deprecated
    private static class HttpRequestHandlerResolverAdapter implements HttpRequestHandlerMapper {
        private final HttpRequestHandlerResolver resolver;

        public HttpRequestHandlerResolverAdapter(HttpRequestHandlerResolver resolver2) {
            this.resolver = resolver2;
        }

        public HttpRequestHandler lookup(HttpRequest request) {
            return this.resolver.lookup(request.getRequestLine().getUri());
        }
    }
}
