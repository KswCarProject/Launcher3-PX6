package org.apache.http.impl.conn;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionOperator;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicHttpClientConnectionManager implements HttpClientConnectionManager, Closeable {
    private ManagedHttpClientConnection conn;
    private ConnectionConfig connConfig;
    private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
    private final HttpClientConnectionOperator connectionOperator;
    private long expiry;
    private final AtomicBoolean isShutdown;
    private boolean leased;
    private final Log log;
    private HttpRoute route;
    private SocketConfig socketConfig;
    private Object state;
    private long updated;

    private static Registry<ConnectionSocketFactory> getDefaultRegistry() {
        return RegistryBuilder.create().register(HttpHost.DEFAULT_SCHEME_NAME, PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
    }

    public BasicHttpClientConnectionManager(Lookup<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory2, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
        this((HttpClientConnectionOperator) new DefaultHttpClientConnectionOperator(socketFactoryRegistry, schemePortResolver, dnsResolver), connFactory2);
    }

    public BasicHttpClientConnectionManager(HttpClientConnectionOperator httpClientConnectionOperator, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory2) {
        this.log = LogFactory.getLog(getClass());
        this.connectionOperator = (HttpClientConnectionOperator) Args.notNull(httpClientConnectionOperator, "Connection operator");
        this.connFactory = connFactory2 == null ? ManagedHttpClientConnectionFactory.INSTANCE : connFactory2;
        this.expiry = Long.MAX_VALUE;
        this.socketConfig = SocketConfig.DEFAULT;
        this.connConfig = ConnectionConfig.DEFAULT;
        this.isShutdown = new AtomicBoolean(false);
    }

    public BasicHttpClientConnectionManager(Lookup<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory2) {
        this(socketFactoryRegistry, connFactory2, (SchemePortResolver) null, (DnsResolver) null);
    }

    public BasicHttpClientConnectionManager(Lookup<ConnectionSocketFactory> socketFactoryRegistry) {
        this(socketFactoryRegistry, (HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>) null, (SchemePortResolver) null, (DnsResolver) null);
    }

    public BasicHttpClientConnectionManager() {
        this(getDefaultRegistry(), (HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>) null, (SchemePortResolver) null, (DnsResolver) null);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            shutdown();
        } finally {
            super.finalize();
        }
    }

    public void close() {
        shutdown();
    }

    /* access modifiers changed from: package-private */
    public HttpRoute getRoute() {
        return this.route;
    }

    /* access modifiers changed from: package-private */
    public Object getState() {
        return this.state;
    }

    public synchronized SocketConfig getSocketConfig() {
        return this.socketConfig;
    }

    public synchronized void setSocketConfig(SocketConfig socketConfig2) {
        if (socketConfig2 == null) {
            socketConfig2 = SocketConfig.DEFAULT;
        }
        this.socketConfig = socketConfig2;
    }

    public synchronized ConnectionConfig getConnectionConfig() {
        return this.connConfig;
    }

    public synchronized void setConnectionConfig(ConnectionConfig connConfig2) {
        if (connConfig2 == null) {
            connConfig2 = ConnectionConfig.DEFAULT;
        }
        this.connConfig = connConfig2;
    }

    public final ConnectionRequest requestConnection(final HttpRoute route2, final Object state2) {
        Args.notNull(route2, "Route");
        return new ConnectionRequest() {
            public boolean cancel() {
                return false;
            }

            public HttpClientConnection get(long timeout, TimeUnit tunit) {
                return BasicHttpClientConnectionManager.this.getConnection(route2, state2);
            }
        };
    }

    private void closeConnection() {
        if (this.conn != null) {
            this.log.debug("Closing connection");
            try {
                this.conn.close();
            } catch (IOException iox) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("I/O exception closing connection", iox);
                }
            }
            this.conn = null;
        }
    }

    private void shutdownConnection() {
        if (this.conn != null) {
            this.log.debug("Shutting down connection");
            try {
                this.conn.shutdown();
            } catch (IOException iox) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("I/O exception shutting down connection", iox);
                }
            }
            this.conn = null;
        }
    }

    private void checkExpiry() {
        if (this.conn != null && System.currentTimeMillis() >= this.expiry) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection expired @ " + new Date(this.expiry));
            }
            closeConnection();
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized HttpClientConnection getConnection(HttpRoute route2, Object state2) {
        ManagedHttpClientConnection managedHttpClientConnection;
        boolean z = true;
        synchronized (this) {
            Asserts.check(!this.isShutdown.get(), "Connection manager has been shut down");
            if (this.log.isDebugEnabled()) {
                this.log.debug("Get connection for route " + route2);
            }
            if (this.leased) {
                z = false;
            }
            Asserts.check(z, "Connection is still allocated");
            if (!LangUtils.equals((Object) this.route, (Object) route2) || !LangUtils.equals(this.state, state2)) {
                closeConnection();
            }
            this.route = route2;
            this.state = state2;
            checkExpiry();
            if (this.conn == null) {
                this.conn = this.connFactory.create(route2, this.connConfig);
            }
            this.leased = true;
            managedHttpClientConnection = this.conn;
        }
        return managedHttpClientConnection;
    }

    /* JADX INFO: finally extract failed */
    public synchronized void releaseConnection(HttpClientConnection conn2, Object state2, long keepalive, TimeUnit tunit) {
        String s;
        boolean z = false;
        synchronized (this) {
            Args.notNull(conn2, "Connection");
            if (conn2 == this.conn) {
                z = true;
            }
            Asserts.check(z, "Connection not obtained from this manager");
            if (this.log.isDebugEnabled()) {
                this.log.debug("Releasing connection " + conn2);
            }
            if (!this.isShutdown.get()) {
                try {
                    this.updated = System.currentTimeMillis();
                    if (!this.conn.isOpen()) {
                        this.conn = null;
                        this.route = null;
                        this.conn = null;
                        this.expiry = Long.MAX_VALUE;
                    } else {
                        this.state = state2;
                        if (this.log.isDebugEnabled()) {
                            if (keepalive > 0) {
                                s = "for " + keepalive + " " + tunit;
                            } else {
                                s = "indefinitely";
                            }
                            this.log.debug("Connection can be kept alive " + s);
                        }
                        if (keepalive > 0) {
                            this.expiry = this.updated + tunit.toMillis(keepalive);
                        } else {
                            this.expiry = Long.MAX_VALUE;
                        }
                    }
                    this.leased = false;
                } catch (Throwable th) {
                    this.leased = false;
                    throw th;
                }
            }
        }
    }

    public void connect(HttpClientConnection conn2, HttpRoute route2, int connectTimeout, HttpContext context) throws IOException {
        HttpHost host;
        Args.notNull(conn2, "Connection");
        Args.notNull(route2, "HTTP route");
        Asserts.check(conn2 == this.conn, "Connection not obtained from this manager");
        if (route2.getProxyHost() != null) {
            host = route2.getProxyHost();
        } else {
            host = route2.getTargetHost();
        }
        this.connectionOperator.connect(this.conn, host, route2.getLocalSocketAddress(), connectTimeout, this.socketConfig, context);
    }

    public void upgrade(HttpClientConnection conn2, HttpRoute route2, HttpContext context) throws IOException {
        Args.notNull(conn2, "Connection");
        Args.notNull(route2, "HTTP route");
        Asserts.check(conn2 == this.conn, "Connection not obtained from this manager");
        this.connectionOperator.upgrade(this.conn, route2.getTargetHost(), context);
    }

    public void routeComplete(HttpClientConnection conn2, HttpRoute route2, HttpContext context) throws IOException {
    }

    public synchronized void closeExpiredConnections() {
        if (!this.isShutdown.get()) {
            if (!this.leased) {
                checkExpiry();
            }
        }
    }

    public synchronized void closeIdleConnections(long idletime, TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        if (!this.isShutdown.get()) {
            if (!this.leased) {
                long time = tunit.toMillis(idletime);
                if (time < 0) {
                    time = 0;
                }
                if (this.updated <= System.currentTimeMillis() - time) {
                    closeConnection();
                }
            }
        }
    }

    public synchronized void shutdown() {
        if (this.isShutdown.compareAndSet(false, true)) {
            shutdownConnection();
        }
    }
}
