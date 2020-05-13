package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultBHttpServerConnectionFactory implements HttpConnectionFactory<DefaultBHttpServerConnection> {
    public static final DefaultBHttpServerConnectionFactory INSTANCE = new DefaultBHttpServerConnectionFactory();
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final HttpMessageParserFactory<HttpRequest> requestParserFactory;
    private final HttpMessageWriterFactory<HttpResponse> responseWriterFactory;

    public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig2, ContentLengthStrategy incomingContentStrategy2, ContentLengthStrategy outgoingContentStrategy2, HttpMessageParserFactory<HttpRequest> requestParserFactory2, HttpMessageWriterFactory<HttpResponse> responseWriterFactory2) {
        this.cconfig = cconfig2 == null ? ConnectionConfig.DEFAULT : cconfig2;
        this.incomingContentStrategy = incomingContentStrategy2;
        this.outgoingContentStrategy = outgoingContentStrategy2;
        this.requestParserFactory = requestParserFactory2;
        this.responseWriterFactory = responseWriterFactory2;
    }

    public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig2, HttpMessageParserFactory<HttpRequest> requestParserFactory2, HttpMessageWriterFactory<HttpResponse> responseWriterFactory2) {
        this(cconfig2, (ContentLengthStrategy) null, (ContentLengthStrategy) null, requestParserFactory2, responseWriterFactory2);
    }

    public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig2) {
        this(cconfig2, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageParserFactory<HttpRequest>) null, (HttpMessageWriterFactory<HttpResponse>) null);
    }

    public DefaultBHttpServerConnectionFactory() {
        this((ConnectionConfig) null, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageParserFactory<HttpRequest>) null, (HttpMessageWriterFactory<HttpResponse>) null);
    }

    public DefaultBHttpServerConnection createConnection(Socket socket) throws IOException {
        DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
        conn.bind(socket);
        return conn;
    }
}
