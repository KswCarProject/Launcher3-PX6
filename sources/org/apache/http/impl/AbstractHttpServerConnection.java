package org.apache.http.impl;

import java.io.IOException;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpResponseWriter;
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
public abstract class AbstractHttpServerConnection implements HttpServerConnection {
    private final EntityDeserializer entitydeserializer = createEntityDeserializer();
    private final EntitySerializer entityserializer = createEntitySerializer();
    private EofSensor eofSensor = null;
    private SessionInputBuffer inbuffer = null;
    private HttpConnectionMetricsImpl metrics = null;
    private SessionOutputBuffer outbuffer = null;
    private HttpMessageParser<HttpRequest> requestParser = null;
    private HttpMessageWriter<HttpResponse> responseWriter = null;

    /* access modifiers changed from: protected */
    public abstract void assertOpen() throws IllegalStateException;

    /* access modifiers changed from: protected */
    public EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0)));
    }

    /* access modifiers changed from: protected */
    public EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    /* access modifiers changed from: protected */
    public HttpRequestFactory createHttpRequestFactory() {
        return DefaultHttpRequestFactory.INSTANCE;
    }

    /* access modifiers changed from: protected */
    public HttpMessageParser<HttpRequest> createRequestParser(SessionInputBuffer buffer, HttpRequestFactory requestFactory, HttpParams params) {
        return new DefaultHttpRequestParser(buffer, (LineParser) null, requestFactory, params);
    }

    /* access modifiers changed from: protected */
    public HttpMessageWriter<HttpResponse> createResponseWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new HttpResponseWriter(buffer, (LineFormatter) null, params);
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
        this.requestParser = createRequestParser(inbuffer2, createHttpRequestFactory(), params);
        this.responseWriter = createResponseWriter(outbuffer2, params);
        this.metrics = createConnectionMetrics(inbuffer2.getMetrics(), outbuffer2.getMetrics());
    }

    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        assertOpen();
        HttpRequest request = this.requestParser.parse();
        this.metrics.incrementRequestCount();
        return request;
    }

    public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        assertOpen();
        request.setEntity(this.entitydeserializer.deserialize(this.inbuffer, request));
    }

    /* access modifiers changed from: protected */
    public void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        assertOpen();
        this.responseWriter.write(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
    }

    public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
        if (response.getEntity() != null) {
            this.entityserializer.serialize(this.outbuffer, response, response.getEntity());
        }
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
        } catch (IOException e) {
            return true;
        }
    }

    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
