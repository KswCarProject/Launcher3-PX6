package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.protocol.HttpContext;

public class DefaultManagedHttpClientConnection extends DefaultBHttpClientConnection implements ManagedHttpClientConnection, HttpContext {
    private final Map<String, Object> attributes;
    private final String id;
    private volatile boolean shutdown;

    public DefaultManagedHttpClientConnection(String id2, int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.id = id2;
        this.attributes = new ConcurrentHashMap();
    }

    public DefaultManagedHttpClientConnection(String id2, int buffersize) {
        this(id2, buffersize, buffersize, (CharsetDecoder) null, (CharsetEncoder) null, (MessageConstraints) null, (ContentLengthStrategy) null, (ContentLengthStrategy) null, (HttpMessageWriterFactory<HttpRequest>) null, (HttpMessageParserFactory<HttpResponse>) null);
    }

    public String getId() {
        return this.id;
    }

    public void shutdown() throws IOException {
        this.shutdown = true;
        super.shutdown();
    }

    public Object getAttribute(String id2) {
        return this.attributes.get(id2);
    }

    public Object removeAttribute(String id2) {
        return this.attributes.remove(id2);
    }

    public void setAttribute(String id2, Object obj) {
        this.attributes.put(id2, obj);
    }

    public void bind(Socket socket) throws IOException {
        if (this.shutdown) {
            socket.close();
            throw new InterruptedIOException("Connection already shutdown");
        } else {
            super.bind(socket);
        }
    }

    public Socket getSocket() {
        return super.getSocket();
    }

    public SSLSession getSSLSession() {
        Socket socket = super.getSocket();
        if (socket instanceof SSLSocket) {
            return ((SSLSocket) socket).getSession();
        }
        return null;
    }
}
