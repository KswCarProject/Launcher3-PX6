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
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
class ManagedClientConnectionImpl implements ManagedClientConnection {
    private volatile long duration = Long.MAX_VALUE;
    private final ClientConnectionManager manager;
    private final ClientConnectionOperator operator;
    private volatile HttpPoolEntry poolEntry;
    private volatile boolean reusable = false;

    ManagedClientConnectionImpl(ClientConnectionManager manager2, ClientConnectionOperator operator2, HttpPoolEntry entry) {
        Args.notNull(manager2, "Connection manager");
        Args.notNull(operator2, "Connection operator");
        Args.notNull(entry, "HTTP pool entry");
        this.manager = manager2;
        this.operator = operator2;
        this.poolEntry = entry;
    }

    public String getId() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public HttpPoolEntry getPoolEntry() {
        return this.poolEntry;
    }

    /* access modifiers changed from: package-private */
    public HttpPoolEntry detach() {
        HttpPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }

    public ClientConnectionManager getManager() {
        return this.manager;
    }

    private OperatedClientConnection getConnection() {
        HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        return (OperatedClientConnection) local.getConnection();
    }

    private OperatedClientConnection ensureConnection() {
        HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            return (OperatedClientConnection) local.getConnection();
        }
        throw new ConnectionShutdownException();
    }

    private HttpPoolEntry ensurePoolEntry() {
        HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            return local;
        }
        throw new ConnectionShutdownException();
    }

    public void close() throws IOException {
        HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            local.getTracker().reset();
            ((OperatedClientConnection) local.getConnection()).close();
        }
    }

    public void shutdown() throws IOException {
        HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            local.getTracker().reset();
            ((OperatedClientConnection) local.getConnection()).shutdown();
        }
    }

    public boolean isOpen() {
        OperatedClientConnection conn = getConnection();
        if (conn != null) {
            return conn.isOpen();
        }
        return false;
    }

    public boolean isStale() {
        OperatedClientConnection conn = getConnection();
        if (conn != null) {
            return conn.isStale();
        }
        return true;
    }

    public void setSocketTimeout(int timeout) {
        ensureConnection().setSocketTimeout(timeout);
    }

    public int getSocketTimeout() {
        return ensureConnection().getSocketTimeout();
    }

    public HttpConnectionMetrics getMetrics() {
        return ensureConnection().getMetrics();
    }

    public void flush() throws IOException {
        ensureConnection().flush();
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        return ensureConnection().isResponseAvailable(timeout);
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        ensureConnection().receiveResponseEntity(response);
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        return ensureConnection().receiveResponseHeader();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        ensureConnection().sendRequestEntity(request);
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        ensureConnection().sendRequestHeader(request);
    }

    public InetAddress getLocalAddress() {
        return ensureConnection().getLocalAddress();
    }

    public int getLocalPort() {
        return ensureConnection().getLocalPort();
    }

    public InetAddress getRemoteAddress() {
        return ensureConnection().getRemoteAddress();
    }

    public int getRemotePort() {
        return ensureConnection().getRemotePort();
    }

    public boolean isSecure() {
        return ensureConnection().isSecure();
    }

    public void bind(Socket socket) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Socket getSocket() {
        return ensureConnection().getSocket();
    }

    public SSLSession getSSLSession() {
        Socket sock = ensureConnection().getSocket();
        if (sock instanceof SSLSocket) {
            return ((SSLSocket) sock).getSession();
        }
        return null;
    }

    public Object getAttribute(String id) {
        OperatedClientConnection conn = ensureConnection();
        if (conn instanceof HttpContext) {
            return ((HttpContext) conn).getAttribute(id);
        }
        return null;
    }

    public Object removeAttribute(String id) {
        OperatedClientConnection conn = ensureConnection();
        if (conn instanceof HttpContext) {
            return ((HttpContext) conn).removeAttribute(id);
        }
        return null;
    }

    public void setAttribute(String id, Object obj) {
        OperatedClientConnection conn = ensureConnection();
        if (conn instanceof HttpContext) {
            ((HttpContext) conn).setAttribute(id, obj);
        }
    }

    public HttpRoute getRoute() {
        return ensurePoolEntry().getEffectiveRoute();
    }

    public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
        OperatedClientConnection conn;
        Args.notNull(route, "Route");
        Args.notNull(params, "HTTP parameters");
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(!tracker.isConnected(), "Connection already open");
            conn = (OperatedClientConnection) this.poolEntry.getConnection();
        }
        HttpHost proxy = route.getProxyHost();
        this.operator.openConnection(conn, proxy != null ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            RouteTracker tracker2 = this.poolEntry.getTracker();
            if (proxy == null) {
                tracker2.connectTarget(conn.isSecure());
            } else {
                tracker2.connectProxy(proxy, conn.isSecure());
            }
        }
    }

    public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
        HttpHost target;
        OperatedClientConnection conn;
        Args.notNull(params, "HTTP parameters");
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(tracker.isConnected(), "Connection not open");
            Asserts.check(!tracker.isTunnelled(), "Connection is already tunnelled");
            target = tracker.getTargetHost();
            conn = (OperatedClientConnection) this.poolEntry.getConnection();
        }
        conn.update((Socket) null, target, secure, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            this.poolEntry.getTracker().tunnelTarget(secure);
        }
    }

    public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
        OperatedClientConnection conn;
        Args.notNull(next, "Next proxy");
        Args.notNull(params, "HTTP parameters");
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(tracker.isConnected(), "Connection not open");
            conn = (OperatedClientConnection) this.poolEntry.getConnection();
        }
        conn.update((Socket) null, next, secure, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            this.poolEntry.getTracker().tunnelProxy(next, secure);
        }
    }

    public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        HttpHost target;
        OperatedClientConnection conn;
        Args.notNull(params, "HTTP parameters");
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(tracker.isConnected(), "Connection not open");
            Asserts.check(tracker.isTunnelled(), "Protocol layering without a tunnel not supported");
            Asserts.check(!tracker.isLayered(), "Multiple protocol layering not supported");
            target = tracker.getTargetHost();
            conn = (OperatedClientConnection) this.poolEntry.getConnection();
        }
        this.operator.updateSecureConnection(conn, target, context, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            this.poolEntry.getTracker().layerProtocol(conn.isSecure());
        }
    }

    public Object getState() {
        return ensurePoolEntry().getState();
    }

    public void setState(Object state) {
        ensurePoolEntry().setState(state);
    }

    public void markReusable() {
        this.reusable = true;
    }

    public void unmarkReusable() {
        this.reusable = false;
    }

    public boolean isMarkedReusable() {
        return this.reusable;
    }

    public void setIdleDuration(long duration2, TimeUnit unit) {
        if (duration2 > 0) {
            this.duration = unit.toMillis(duration2);
        } else {
            this.duration = -1;
        }
    }

    public void releaseConnection() {
        synchronized (this) {
            if (this.poolEntry != null) {
                this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
                this.poolEntry = null;
            }
        }
    }

    public void abortConnection() {
        synchronized (this) {
            if (this.poolEntry != null) {
                this.reusable = false;
                try {
                    ((OperatedClientConnection) this.poolEntry.getConnection()).shutdown();
                } catch (IOException e) {
                }
                this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
                this.poolEntry = null;
            }
        }
    }
}
