package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.protocol.HttpContext;

@Deprecated
public abstract class AbstractClientConnAdapter implements ManagedClientConnection, HttpContext {
    private final ClientConnectionManager connManager;
    private volatile long duration = Long.MAX_VALUE;
    private volatile boolean markedReusable = false;
    private volatile boolean released = false;
    private volatile OperatedClientConnection wrappedConnection;

    protected AbstractClientConnAdapter(ClientConnectionManager mgr, OperatedClientConnection conn) {
        this.connManager = mgr;
        this.wrappedConnection = conn;
    }

    /* access modifiers changed from: protected */
    public synchronized void detach() {
        this.wrappedConnection = null;
        this.duration = Long.MAX_VALUE;
    }

    /* access modifiers changed from: protected */
    public OperatedClientConnection getWrappedConnection() {
        return this.wrappedConnection;
    }

    /* access modifiers changed from: protected */
    public ClientConnectionManager getManager() {
        return this.connManager;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public final void assertNotAborted() throws InterruptedIOException {
        if (isReleased()) {
            throw new InterruptedIOException("Connection has been shut down");
        }
    }

    /* access modifiers changed from: protected */
    public boolean isReleased() {
        return this.released;
    }

    /* access modifiers changed from: protected */
    public final void assertValid(OperatedClientConnection wrappedConn) throws ConnectionShutdownException {
        if (isReleased() || wrappedConn == null) {
            throw new ConnectionShutdownException();
        }
    }

    public boolean isOpen() {
        OperatedClientConnection conn = getWrappedConnection();
        if (conn == null) {
            return false;
        }
        return conn.isOpen();
    }

    public boolean isStale() {
        OperatedClientConnection conn;
        if (!isReleased() && (conn = getWrappedConnection()) != null) {
            return conn.isStale();
        }
        return true;
    }

    public void setSocketTimeout(int timeout) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        conn.setSocketTimeout(timeout);
    }

    public int getSocketTimeout() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getSocketTimeout();
    }

    public HttpConnectionMetrics getMetrics() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getMetrics();
    }

    public void flush() throws IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        conn.flush();
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.isResponseAvailable(timeout);
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        conn.receiveResponseEntity(response);
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        return conn.receiveResponseHeader();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        conn.sendRequestEntity(request);
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        conn.sendRequestHeader(request);
    }

    public InetAddress getLocalAddress() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getLocalAddress();
    }

    public int getLocalPort() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getLocalPort();
    }

    public InetAddress getRemoteAddress() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getRemoteAddress();
    }

    public int getRemotePort() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getRemotePort();
    }

    public boolean isSecure() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.isSecure();
    }

    public void bind(Socket socket) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Socket getSocket() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (!isOpen()) {
            return null;
        }
        return conn.getSocket();
    }

    public SSLSession getSSLSession() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (!isOpen()) {
            return null;
        }
        Socket sock = conn.getSocket();
        if (sock instanceof SSLSocket) {
            return ((SSLSocket) sock).getSession();
        }
        return null;
    }

    public void markReusable() {
        this.markedReusable = true;
    }

    public void unmarkReusable() {
        this.markedReusable = false;
    }

    public boolean isMarkedReusable() {
        return this.markedReusable;
    }

    public void setIdleDuration(long duration2, TimeUnit unit) {
        if (duration2 > 0) {
            this.duration = unit.toMillis(duration2);
        } else {
            this.duration = -1;
        }
    }

    public synchronized void releaseConnection() {
        if (!this.released) {
            this.released = true;
            this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void abortConnection() {
        if (!this.released) {
            this.released = true;
            unmarkReusable();
            try {
                shutdown();
            } catch (IOException e) {
            }
            this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
        }
    }

    public Object getAttribute(String id) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (conn instanceof HttpContext) {
            return ((HttpContext) conn).getAttribute(id);
        }
        return null;
    }

    public Object removeAttribute(String id) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (conn instanceof HttpContext) {
            return ((HttpContext) conn).removeAttribute(id);
        }
        return null;
    }

    public void setAttribute(String id, Object obj) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (conn instanceof HttpContext) {
            ((HttpContext) conn).setAttribute(id, obj);
        }
    }
}
