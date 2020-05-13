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
public class DefaultBHttpClientConnectionFactory implements HttpConnectionFactory<DefaultBHttpClientConnection> {
    public static final DefaultBHttpClientConnectionFactory INSTANCE = new DefaultBHttpClientConnectionFactory();
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final HttpMessageParserFactory<HttpResponse> responseParserFactory;

    public DefaultBHttpClientConnectionFactory(ConnectionConfig cconfig2, ContentLengthStrategy incomingContentStrategy2, ContentLengthStrategy outgoingContentStrategy2, HttpMessageWriterFactory<HttpRequest> requestWriterFactory2, HttpMessageParserFactory<HttpResponse> responseParserFactory2) {
        this.cconfig = cconfig2 == null ? ConnectionConfig.DEFAULT : cconfig2;
        this.incomingContentStrategy = incomingContentStrategy2;
        this.outgoingContentStrategy = outgoingContentStrategy2;
        this.requestWriterFactory = requestWriterFactory2;
        this.responseParserFactory = responseParserFactory2;
    }

    public DefaultBHttpClientConnectionFactory(ConnectionConfig cconfig2, HttpMessageWriterFactory<HttpRequest> requestWriterFactory2, HttpMessageParserFactory<HttpResponse> responseParserFactory2) {
        this(cconfig2, (ContentLengthStrategy) null, (ContentLengthStrategy) null, requestWriterFactory2, responseParserFactory2);
    }

    public DefaultBHttpClientConnectionFactory(ConnectionConfig cconfig2) {
        this(cconfig2, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageWriterFactory<HttpRequest>) null, (HttpMessageParserFactory<HttpResponse>) null);
    }

    public DefaultBHttpClientConnectionFactory() {
        this((ConnectionConfig) null, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageWriterFactory<HttpRequest>) null, (HttpMessageParserFactory<HttpResponse>) null);
    }

    public DefaultBHttpClientConnection createConnection(Socket socket) throws IOException {
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
        conn.bind(socket);
        return conn;
    }
}
