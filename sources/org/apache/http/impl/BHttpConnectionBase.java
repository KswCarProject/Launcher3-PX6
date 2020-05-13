package org.apache.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpMessage;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.ContentLengthOutputStream;
import org.apache.http.impl.io.EmptyInputStream;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.IdentityOutputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.NetUtils;

public class BHttpConnectionBase implements HttpConnection, HttpInetConnection {
    private final HttpConnectionMetricsImpl connMetrics;
    private final SessionInputBufferImpl inbuffer;
    private final ContentLengthStrategy incomingContentStrategy;
    private final MessageConstraints messageConstraints;
    private final SessionOutputBufferImpl outbuffer;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final AtomicReference<Socket> socketHolder;

    protected BHttpConnectionBase(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints messageConstraints2, ContentLengthStrategy incomingContentStrategy2, ContentLengthStrategy outgoingContentStrategy2) {
        Args.positive(buffersize, "Buffer size");
        HttpTransportMetricsImpl inTransportMetrics = new HttpTransportMetricsImpl();
        HttpTransportMetricsImpl outTransportMetrics = new HttpTransportMetricsImpl();
        this.inbuffer = new SessionInputBufferImpl(inTransportMetrics, buffersize, -1, messageConstraints2 != null ? messageConstraints2 : MessageConstraints.DEFAULT, chardecoder);
        this.outbuffer = new SessionOutputBufferImpl(outTransportMetrics, buffersize, fragmentSizeHint, charencoder);
        this.messageConstraints = messageConstraints2;
        this.connMetrics = new HttpConnectionMetricsImpl(inTransportMetrics, outTransportMetrics);
        this.incomingContentStrategy = incomingContentStrategy2 == null ? LaxContentLengthStrategy.INSTANCE : incomingContentStrategy2;
        this.outgoingContentStrategy = outgoingContentStrategy2 == null ? StrictContentLengthStrategy.INSTANCE : outgoingContentStrategy2;
        this.socketHolder = new AtomicReference<>();
    }

    /* access modifiers changed from: protected */
    public void ensureOpen() throws IOException {
        Socket socket = this.socketHolder.get();
        if (socket == null) {
            throw new ConnectionClosedException("Connection is closed");
        }
        if (!this.inbuffer.isBound()) {
            this.inbuffer.bind(getSocketInputStream(socket));
        }
        if (!this.outbuffer.isBound()) {
            this.outbuffer.bind(getSocketOutputStream(socket));
        }
    }

    /* access modifiers changed from: protected */
    public InputStream getSocketInputStream(Socket socket) throws IOException {
        return socket.getInputStream();
    }

    /* access modifiers changed from: protected */
    public OutputStream getSocketOutputStream(Socket socket) throws IOException {
        return socket.getOutputStream();
    }

    /* access modifiers changed from: protected */
    public void bind(Socket socket) throws IOException {
        Args.notNull(socket, "Socket");
        this.socketHolder.set(socket);
        this.inbuffer.bind((InputStream) null);
        this.outbuffer.bind((OutputStream) null);
    }

    /* access modifiers changed from: protected */
    public SessionInputBuffer getSessionInputBuffer() {
        return this.inbuffer;
    }

    /* access modifiers changed from: protected */
    public SessionOutputBuffer getSessionOutputBuffer() {
        return this.outbuffer;
    }

    /* access modifiers changed from: protected */
    public void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public boolean isOpen() {
        return this.socketHolder.get() != null;
    }

    /* access modifiers changed from: protected */
    public Socket getSocket() {
        return this.socketHolder.get();
    }

    /* access modifiers changed from: protected */
    public OutputStream createOutputStream(long len, SessionOutputBuffer outbuffer2) {
        if (len == -2) {
            return new ChunkedOutputStream(2048, outbuffer2);
        }
        if (len == -1) {
            return new IdentityOutputStream(outbuffer2);
        }
        return new ContentLengthOutputStream(outbuffer2, len);
    }

    /* access modifiers changed from: protected */
    public OutputStream prepareOutput(HttpMessage message) throws HttpException {
        return createOutputStream(this.outgoingContentStrategy.determineLength(message), this.outbuffer);
    }

    /* access modifiers changed from: protected */
    public InputStream createInputStream(long len, SessionInputBuffer inbuffer2) {
        if (len == -2) {
            return new ChunkedInputStream(inbuffer2, this.messageConstraints);
        }
        if (len == -1) {
            return new IdentityInputStream(inbuffer2);
        }
        if (len == 0) {
            return EmptyInputStream.INSTANCE;
        }
        return new ContentLengthInputStream(inbuffer2, len);
    }

    /* access modifiers changed from: protected */
    public HttpEntity prepareInput(HttpMessage message) throws HttpException {
        BasicHttpEntity entity = new BasicHttpEntity();
        long len = this.incomingContentStrategy.determineLength(message);
        InputStream instream = createInputStream(len, this.inbuffer);
        if (len == -2) {
            entity.setChunked(true);
            entity.setContentLength(-1);
            entity.setContent(instream);
        } else if (len == -1) {
            entity.setChunked(false);
            entity.setContentLength(-1);
            entity.setContent(instream);
        } else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(instream);
        }
        Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }

    public InetAddress getLocalAddress() {
        Socket socket = this.socketHolder.get();
        if (socket != null) {
            return socket.getLocalAddress();
        }
        return null;
    }

    public int getLocalPort() {
        Socket socket = this.socketHolder.get();
        if (socket != null) {
            return socket.getLocalPort();
        }
        return -1;
    }

    public InetAddress getRemoteAddress() {
        Socket socket = this.socketHolder.get();
        if (socket != null) {
            return socket.getInetAddress();
        }
        return null;
    }

    public int getRemotePort() {
        Socket socket = this.socketHolder.get();
        if (socket != null) {
            return socket.getPort();
        }
        return -1;
    }

    public void setSocketTimeout(int timeout) {
        Socket socket = this.socketHolder.get();
        if (socket != null) {
            try {
                socket.setSoTimeout(timeout);
            } catch (SocketException e) {
            }
        }
    }

    public int getSocketTimeout() {
        Socket socket = this.socketHolder.get();
        if (socket == null) {
            return -1;
        }
        try {
            return socket.getSoTimeout();
        } catch (SocketException e) {
            return -1;
        }
    }

    public void shutdown() throws IOException {
        Socket socket = this.socketHolder.getAndSet((Object) null);
        if (socket != null) {
            try {
                socket.setSoLinger(true, 0);
            } catch (IOException e) {
            } finally {
                socket.close();
            }
        }
    }

    public void close() throws IOException {
        Socket socket = this.socketHolder.getAndSet((Object) null);
        if (socket != null) {
            try {
                this.inbuffer.clear();
                this.outbuffer.flush();
                try {
                    socket.shutdownOutput();
                } catch (IOException e) {
                }
                try {
                    socket.shutdownInput();
                } catch (IOException | UnsupportedOperationException e2) {
                }
            } finally {
                socket.close();
            }
        }
    }

    /* JADX INFO: finally extract failed */
    private int fillInputBuffer(int timeout) throws IOException {
        Socket socket = this.socketHolder.get();
        int oldtimeout = socket.getSoTimeout();
        try {
            socket.setSoTimeout(timeout);
            int fillBuffer = this.inbuffer.fillBuffer();
            socket.setSoTimeout(oldtimeout);
            return fillBuffer;
        } catch (Throwable th) {
            socket.setSoTimeout(oldtimeout);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public boolean awaitInput(int timeout) throws IOException {
        if (this.inbuffer.hasBufferedData()) {
            return true;
        }
        fillInputBuffer(timeout);
        return this.inbuffer.hasBufferedData();
    }

    public boolean isStale() {
        if (!isOpen()) {
            return true;
        }
        try {
            if (fillInputBuffer(1) >= 0) {
                return false;
            }
            return true;
        } catch (SocketTimeoutException e) {
            return false;
        } catch (IOException e2) {
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void incrementRequestCount() {
        this.connMetrics.incrementRequestCount();
    }

    /* access modifiers changed from: protected */
    public void incrementResponseCount() {
        this.connMetrics.incrementResponseCount();
    }

    public HttpConnectionMetrics getMetrics() {
        return this.connMetrics;
    }

    public String toString() {
        Socket socket = this.socketHolder.get();
        if (socket == null) {
            return "[Not bound]";
        }
        StringBuilder buffer = new StringBuilder();
        SocketAddress remoteAddress = socket.getRemoteSocketAddress();
        SocketAddress localAddress = socket.getLocalSocketAddress();
        if (!(remoteAddress == null || localAddress == null)) {
            NetUtils.formatAddress(buffer, localAddress);
            buffer.append("<->");
            NetUtils.formatAddress(buffer, remoteAddress);
        }
        return buffer.toString();
    }
}
