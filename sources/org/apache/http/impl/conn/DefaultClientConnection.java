package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.impl.SocketHttpClientConnection;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
public class DefaultClientConnection extends SocketHttpClientConnection implements OperatedClientConnection, ManagedHttpClientConnection, HttpContext {
    private final Map<String, Object> attributes = new HashMap();
    private boolean connSecure;
    private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
    private final Log log = LogFactory.getLog(getClass());
    private volatile boolean shutdown;
    private volatile Socket socket;
    private HttpHost targetHost;
    private final Log wireLog = LogFactory.getLog("org.apache.http.wire");

    public String getId() {
        return null;
    }

    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    public final boolean isSecure() {
        return this.connSecure;
    }

    public final Socket getSocket() {
        return this.socket;
    }

    public SSLSession getSSLSession() {
        if (this.socket instanceof SSLSocket) {
            return ((SSLSocket) this.socket).getSession();
        }
        return null;
    }

    public void opening(Socket sock, HttpHost target) throws IOException {
        assertNotOpen();
        this.socket = sock;
        this.targetHost = target;
        if (this.shutdown) {
            sock.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
    }

    public void openCompleted(boolean secure, HttpParams params) throws IOException {
        Args.notNull(params, "Parameters");
        assertNotOpen();
        this.connSecure = secure;
        bind(this.socket, params);
    }

    public void shutdown() throws IOException {
        this.shutdown = true;
        try {
            super.shutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " shut down");
            }
            Socket sock = this.socket;
            if (sock != null) {
                sock.close();
            }
        } catch (IOException ex) {
            this.log.debug("I/O error shutting down connection", ex);
        }
    }

    public void close() throws IOException {
        try {
            super.close();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " closed");
            }
        } catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }

    /* access modifiers changed from: protected */
    public SessionInputBuffer createSessionInputBuffer(Socket socket2, int buffersize, HttpParams params) throws IOException {
        if (buffersize <= 0) {
            buffersize = 8192;
        }
        SessionInputBuffer inbuffer = super.createSessionInputBuffer(socket2, buffersize, params);
        if (this.wireLog.isDebugEnabled()) {
            return new LoggingSessionInputBuffer(inbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return inbuffer;
    }

    /* access modifiers changed from: protected */
    public SessionOutputBuffer createSessionOutputBuffer(Socket socket2, int buffersize, HttpParams params) throws IOException {
        if (buffersize <= 0) {
            buffersize = 8192;
        }
        SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket2, buffersize, params);
        if (this.wireLog.isDebugEnabled()) {
            return new LoggingSessionOutputBuffer(outbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return outbuffer;
    }

    /* access modifiers changed from: protected */
    public HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, (LineParser) null, responseFactory, params);
    }

    public void bind(Socket socket2) throws IOException {
        bind(socket2, new BasicHttpParams());
    }

    public void update(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
        assertOpen();
        Args.notNull(target, "Target host");
        Args.notNull(params, "Parameters");
        if (sock != null) {
            this.socket = sock;
            bind(sock, params);
        }
        this.targetHost = target;
        this.connSecure = secure;
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        HttpResponse response = super.receiveResponseHeader();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Receiving response: " + response.getStatusLine());
        }
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug("<< " + response.getStatusLine().toString());
            Header[] arr$ = response.getAllHeaders();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                this.headerLog.debug("<< " + arr$[i$].toString());
            }
        }
        return response;
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending request: " + request.getRequestLine());
        }
        super.sendRequestHeader(request);
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug(">> " + request.getRequestLine().toString());
            Header[] arr$ = request.getAllHeaders();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                this.headerLog.debug(">> " + arr$[i$].toString());
            }
        }
    }

    public Object getAttribute(String id) {
        return this.attributes.get(id);
    }

    public Object removeAttribute(String id) {
        return this.attributes.remove(id);
    }

    public void setAttribute(String id, Object obj) {
        this.attributes.put(id, obj);
    }
}
