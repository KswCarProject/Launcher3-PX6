package org.apache.http.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.io.EofSensor;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.message.LineParser;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public abstract class AbstractHttpClientConnection implements HttpClientConnection {
    private final EntityDeserializer entitydeserializer = createEntityDeserializer();
    private final EntitySerializer entityserializer = createEntitySerializer();
    private EofSensor eofSensor = null;
    private SessionInputBuffer inbuffer = null;
    private HttpConnectionMetricsImpl metrics = null;
    private SessionOutputBuffer outbuffer = null;
    private HttpMessageWriter<HttpRequest> requestWriter = null;
    private HttpMessageParser<HttpResponse> responseParser = null;

    /* access modifiers changed from: protected */
    public abstract void assertOpen() throws IllegalStateException;

    /* access modifiers changed from: protected */
    public EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new LaxContentLengthStrategy());
    }

    /* access modifiers changed from: protected */
    public EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    /* access modifiers changed from: protected */
    public HttpResponseFactory createHttpResponseFactory() {
        return DefaultHttpResponseFactory.INSTANCE;
    }

    /* access modifiers changed from: protected */
    public HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, (LineParser) null, responseFactory, params);
    }

    /* access modifiers changed from: protected */
    public HttpMessageWriter<HttpRequest> createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new HttpRequestWriter(buffer, (LineFormatter) null, params);
    }

    /* access modifiers changed from: protected */
    public HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }

    /* access modifiers changed from: protected */
    public void init(SessionInputBuffer inbuffer2, SessionOutputBuffer outbuffer2, HttpParams params) {
        this.inbuffer = (SessionInputBuffer) Args.notNull(inbuffer2, "Input session buffer");
        this.outbuffer = (SessionOutputBuffer) Args.notNull(outbuffer2, "Output session buffer");
        if (inbuffer2 instanceof EofSensor) {
            this.eofSensor = (EofSensor) inbuffer2;
        }
        this.responseParser = createResponseParser(inbuffer2, createHttpResponseFactory(), params);
        this.requestWriter = createRequestWriter(outbuffer2, params);
        this.metrics = createConnectionMetrics(inbuffer2.getMetrics(), outbuffer2.getMetrics());
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        assertOpen();
        try {
            return this.inbuffer.isDataAvailable(timeout);
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        assertOpen();
        this.requestWriter.write(request);
        this.metrics.incrementRequestCount();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        assertOpen();
        if (request.getEntity() != null) {
            this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
        }
    }

    /* access modifiers changed from: protected */
    public void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        assertOpen();
        HttpResponse response = this.responseParser.parse();
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
        return response;
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        assertOpen();
        response.setEntity(this.entitydeserializer.deserialize(this.inbuffer, response));
    }

    /* access modifiers changed from: protected */
    public boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }

    public boolean isStale() {
        if (!isOpen() || isEof()) {
            return true;
        }
        try {
            this.inbuffer.isDataAvailable(1);
            return isEof();
        } catch (SocketTimeoutException e) {
            return false;
        } catch (IOException e2) {
            return true;
        }
    }

    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
