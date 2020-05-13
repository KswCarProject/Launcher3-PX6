package org.apache.http.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.io.DefaultHttpResponseParserFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.util.Args;

public class DefaultBHttpClientConnection extends BHttpConnectionBase implements HttpClientConnection {
    private final HttpMessageWriter<HttpRequest> requestWriter;
    private final HttpMessageParser<HttpResponse> responseParser;

    public DefaultBHttpClientConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy);
        this.requestWriter = (requestWriterFactory == null ? DefaultHttpRequestWriterFactory.INSTANCE : requestWriterFactory).create(getSessionOutputBuffer());
        this.responseParser = (responseParserFactory == null ? DefaultHttpResponseParserFactory.INSTANCE : responseParserFactory).create(getSessionInputBuffer(), constraints);
    }

    public DefaultBHttpClientConnection(int buffersize, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints) {
        this(buffersize, buffersize, chardecoder, charencoder, constraints, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageWriterFactory<HttpRequest>) null, (HttpMessageParserFactory<HttpResponse>) null);
    }

    public DefaultBHttpClientConnection(int buffersize) {
        this(buffersize, buffersize, (CharsetDecoder) null, (CharsetEncoder) null, (MessageConstraints) null, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageWriterFactory<HttpRequest>) null, (HttpMessageParserFactory<HttpResponse>) null);
    }

    /* access modifiers changed from: protected */
    public void onResponseReceived(HttpResponse response) {
    }

    /* access modifiers changed from: protected */
    public void onRequestSubmitted(HttpRequest request) {
    }

    public void bind(Socket socket) throws IOException {
        super.bind(socket);
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        ensureOpen();
        try {
            return awaitInput(timeout);
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        ensureOpen();
        this.requestWriter.write(request);
        onRequestSubmitted(request);
        incrementRequestCount();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        ensureOpen();
        HttpEntity entity = request.getEntity();
        if (entity != null) {
            OutputStream outstream = prepareOutput(request);
            entity.writeTo(outstream);
            outstream.close();
        }
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        ensureOpen();
        HttpResponse response = this.responseParser.parse();
        onResponseReceived(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            incrementResponseCount();
        }
        return response;
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        ensureOpen();
        response.setEntity(prepareInput(response));
    }

    public void flush() throws IOException {
        ensureOpen();
        doFlush();
    }
}
