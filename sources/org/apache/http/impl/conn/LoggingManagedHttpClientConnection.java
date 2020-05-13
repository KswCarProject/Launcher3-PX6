package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

class LoggingManagedHttpClientConnection extends DefaultManagedHttpClientConnection {
    private final Log headerlog;
    private final Log log;
    private final Wire wire;

    public LoggingManagedHttpClientConnection(String id, Log log2, Log headerlog2, Log wirelog, int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(id, buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.log = log2;
        this.headerlog = headerlog2;
        this.wire = new Wire(wirelog, id);
    }

    public void close() throws IOException {
        if (super.isOpen()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(getId() + ": Close connection");
            }
            super.close();
        }
    }

    public void setSocketTimeout(int timeout) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(getId() + ": set socket timeout to " + timeout);
        }
        super.setSocketTimeout(timeout);
    }

    public void shutdown() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(getId() + ": Shutdown connection");
        }
        super.shutdown();
    }

    /* access modifiers changed from: protected */
    public InputStream getSocketInputStream(Socket socket) throws IOException {
        InputStream in = super.getSocketInputStream(socket);
        if (this.wire.enabled()) {
            return new LoggingInputStream(in, this.wire);
        }
        return in;
    }

    /* access modifiers changed from: protected */
    public OutputStream getSocketOutputStream(Socket socket) throws IOException {
        OutputStream out = super.getSocketOutputStream(socket);
        if (this.wire.enabled()) {
            return new LoggingOutputStream(out, this.wire);
        }
        return out;
    }

    /* access modifiers changed from: protected */
    public void onResponseReceived(HttpResponse response) {
        if (response != null && this.headerlog.isDebugEnabled()) {
            this.headerlog.debug(getId() + " << " + response.getStatusLine().toString());
            Header[] arr$ = response.getAllHeaders();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                this.headerlog.debug(getId() + " << " + arr$[i$].toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRequestSubmitted(HttpRequest request) {
        if (request != null && this.headerlog.isDebugEnabled()) {
            this.headerlog.debug(getId() + " >> " + request.getRequestLine().toString());
            Header[] arr$ = request.getAllHeaders();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                this.headerlog.debug(getId() + " >> " + arr$[i$].toString());
            }
        }
    }
}
