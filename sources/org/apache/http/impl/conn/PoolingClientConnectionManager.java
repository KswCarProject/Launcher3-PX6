package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Deprecated
public class PoolingClientConnectionManager implements ClientConnectionManager, ConnPoolControl<HttpRoute> {
    private final DnsResolver dnsResolver;
    private final Log log;
    private final ClientConnectionOperator operator;
    private final HttpConnPool pool;
    private final SchemeRegistry schemeRegistry;

    public PoolingClientConnectionManager(SchemeRegistry schreg) {
        this(schreg, -1, TimeUnit.MILLISECONDS);
    }

    public PoolingClientConnectionManager(SchemeRegistry schreg, DnsResolver dnsResolver2) {
        this(schreg, -1, TimeUnit.MILLISECONDS, dnsResolver2);
    }

    public PoolingClientConnectionManager() {
        this(SchemeRegistryFactory.createDefault());
    }

    public PoolingClientConnectionManager(SchemeRegistry schemeRegistry2, long timeToLive, TimeUnit tunit) {
        this(schemeRegistry2, timeToLive, tunit, new SystemDefaultDnsResolver());
    }

    public PoolingClientConnectionManager(SchemeRegistry schemeRegistry2, long timeToLive, TimeUnit tunit, DnsResolver dnsResolver2) {
        this.log = LogFactory.getLog(getClass());
        Args.notNull(schemeRegistry2, "Scheme registry");
        Args.notNull(dnsResolver2, "DNS resolver");
        this.schemeRegistry = schemeRegistry2;
        this.dnsResolver = dnsResolver2;
        this.operator = createConnectionOperator(schemeRegistry2);
        this.pool = new HttpConnPool(this.log, this.operator, 2, 20, timeToLive, tunit);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            shutdown();
        } finally {
            super.finalize();
        }
    }

    /* access modifiers changed from: protected */
    public ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg, this.dnsResolver);
    }

    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    private String format(HttpRoute route, Object state) {
        StringBuilder buf = new StringBuilder();
        buf.append("[route: ").append(route).append("]");
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }

    private String formatStats(HttpRoute route) {
        StringBuilder buf = new StringBuilder();
        PoolStats totals = this.pool.getTotalStats();
        PoolStats stats = this.pool.getStats(route);
        buf.append("[total kept alive: ").append(totals.getAvailable()).append("; ");
        buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
        buf.append(" of ").append(stats.getMax()).append("; ");
        buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
        buf.append(" of ").append(totals.getMax()).append("]");
        return buf.toString();
    }

    private String format(HttpPoolEntry entry) {
        StringBuilder buf = new StringBuilder();
        buf.append("[id: ").append(entry.getId()).append("]");
        buf.append("[route: ").append(entry.getRoute()).append("]");
        Object state = entry.getState();
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }

    public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
        Args.notNull(route, "HTTP route");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Connection request: " + format(route, state) + formatStats(route));
        }
        final Future<HttpPoolEntry> future = this.pool.lease(route, state);
        return new ClientConnectionRequest() {
            public void abortRequest() {
                future.cancel(true);
            }

            public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
                return PoolingClientConnectionManager.this.leaseConnection(future, timeout, tunit);
            }
        };
    }

    /* access modifiers changed from: package-private */
    public ManagedClientConnection leaseConnection(Future<HttpPoolEntry> future, long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
        try {
            HttpPoolEntry entry = future.get(timeout, tunit);
            if (entry == null || future.isCancelled()) {
                throw new InterruptedException();
            }
            Asserts.check(entry.getConnection() != null, "Pool entry with no connection");
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection leased: " + format(entry) + formatStats((HttpRoute) entry.getRoute()));
            }
            return new ManagedClientConnectionImpl(this, this.operator, entry);
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
            this.log.error("Unexpected exception leasing connection from pool", cause);
            throw new InterruptedException();
        } catch (TimeoutException e) {
            throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseConnection(org.apache.http.conn.ManagedClientConnection r9, long r10, java.util.concurrent.TimeUnit r12) {
        /*
            r8 = this;
            boolean r4 = r9 instanceof org.apache.http.impl.conn.ManagedClientConnectionImpl
            java.lang.String r5 = "Connection class mismatch, connection not obtained from this manager"
            org.apache.http.util.Args.check(r4, r5)
            r2 = r9
            org.apache.http.impl.conn.ManagedClientConnectionImpl r2 = (org.apache.http.impl.conn.ManagedClientConnectionImpl) r2
            org.apache.http.conn.ClientConnectionManager r4 = r2.getManager()
            if (r4 != r8) goto L_0x001f
            r4 = 1
        L_0x0011:
            java.lang.String r5 = "Connection not obtained from this manager"
            org.apache.http.util.Asserts.check(r4, r5)
            monitor-enter(r2)
            org.apache.http.impl.conn.HttpPoolEntry r0 = r2.detach()     // Catch:{ all -> 0x00cb }
            if (r0 != 0) goto L_0x0021
            monitor-exit(r2)     // Catch:{ all -> 0x00cb }
        L_0x001e:
            return
        L_0x001f:
            r4 = 0
            goto L_0x0011
        L_0x0021:
            boolean r4 = r2.isOpen()     // Catch:{ all -> 0x00e0 }
            if (r4 == 0) goto L_0x0030
            boolean r4 = r2.isMarkedReusable()     // Catch:{ all -> 0x00e0 }
            if (r4 != 0) goto L_0x0030
            r2.shutdown()     // Catch:{ IOException -> 0x00ce }
        L_0x0030:
            boolean r4 = r2.isMarkedReusable()     // Catch:{ all -> 0x00e0 }
            if (r4 == 0) goto L_0x008d
            if (r12 == 0) goto L_0x00eb
            r4 = r12
        L_0x0039:
            r0.updateExpiry(r10, r4)     // Catch:{ all -> 0x00e0 }
            org.apache.commons.logging.Log r4 = r8.log     // Catch:{ all -> 0x00e0 }
            boolean r4 = r4.isDebugEnabled()     // Catch:{ all -> 0x00e0 }
            if (r4 == 0) goto L_0x008d
            r4 = 0
            int r4 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x00ef
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e0 }
            r4.<init>()     // Catch:{ all -> 0x00e0 }
            java.lang.String r5 = "for "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x00e0 }
            java.lang.StringBuilder r4 = r4.append(r10)     // Catch:{ all -> 0x00e0 }
            java.lang.String r5 = " "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x00e0 }
            java.lang.StringBuilder r4 = r4.append(r12)     // Catch:{ all -> 0x00e0 }
            java.lang.String r3 = r4.toString()     // Catch:{ all -> 0x00e0 }
        L_0x0067:
            org.apache.commons.logging.Log r4 = r8.log     // Catch:{ all -> 0x00e0 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e0 }
            r5.<init>()     // Catch:{ all -> 0x00e0 }
            java.lang.String r6 = "Connection "
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x00e0 }
            java.lang.String r6 = r8.format(r0)     // Catch:{ all -> 0x00e0 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x00e0 }
            java.lang.String r6 = " can be kept alive "
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x00e0 }
            java.lang.StringBuilder r5 = r5.append(r3)     // Catch:{ all -> 0x00e0 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00e0 }
            r4.debug(r5)     // Catch:{ all -> 0x00e0 }
        L_0x008d:
            org.apache.http.impl.conn.HttpConnPool r4 = r8.pool     // Catch:{ all -> 0x00cb }
            boolean r5 = r2.isMarkedReusable()     // Catch:{ all -> 0x00cb }
            r4.release(r0, (boolean) r5)     // Catch:{ all -> 0x00cb }
            org.apache.commons.logging.Log r4 = r8.log     // Catch:{ all -> 0x00cb }
            boolean r4 = r4.isDebugEnabled()     // Catch:{ all -> 0x00cb }
            if (r4 == 0) goto L_0x00c8
            org.apache.commons.logging.Log r5 = r8.log     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00cb }
            r4.<init>()     // Catch:{ all -> 0x00cb }
            java.lang.String r6 = "Connection released: "
            java.lang.StringBuilder r4 = r4.append(r6)     // Catch:{ all -> 0x00cb }
            java.lang.String r6 = r8.format(r0)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r6 = r4.append(r6)     // Catch:{ all -> 0x00cb }
            java.lang.Object r4 = r0.getRoute()     // Catch:{ all -> 0x00cb }
            org.apache.http.conn.routing.HttpRoute r4 = (org.apache.http.conn.routing.HttpRoute) r4     // Catch:{ all -> 0x00cb }
            java.lang.String r4 = r8.formatStats(r4)     // Catch:{ all -> 0x00cb }
            java.lang.StringBuilder r4 = r6.append(r4)     // Catch:{ all -> 0x00cb }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00cb }
            r5.debug(r4)     // Catch:{ all -> 0x00cb }
        L_0x00c8:
            monitor-exit(r2)     // Catch:{ all -> 0x00cb }
            goto L_0x001e
        L_0x00cb:
            r4 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00cb }
            throw r4
        L_0x00ce:
            r1 = move-exception
            org.apache.commons.logging.Log r4 = r8.log     // Catch:{ all -> 0x00e0 }
            boolean r4 = r4.isDebugEnabled()     // Catch:{ all -> 0x00e0 }
            if (r4 == 0) goto L_0x0030
            org.apache.commons.logging.Log r4 = r8.log     // Catch:{ all -> 0x00e0 }
            java.lang.String r5 = "I/O exception shutting down released connection"
            r4.debug(r5, r1)     // Catch:{ all -> 0x00e0 }
            goto L_0x0030
        L_0x00e0:
            r4 = move-exception
            org.apache.http.impl.conn.HttpConnPool r5 = r8.pool     // Catch:{ all -> 0x00cb }
            boolean r6 = r2.isMarkedReusable()     // Catch:{ all -> 0x00cb }
            r5.release(r0, (boolean) r6)     // Catch:{ all -> 0x00cb }
            throw r4     // Catch:{ all -> 0x00cb }
        L_0x00eb:
            java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ all -> 0x00e0 }
            goto L_0x0039
        L_0x00ef:
            java.lang.String r3 = "indefinitely"
            goto L_0x0067
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.PoolingClientConnectionManager.releaseConnection(org.apache.http.conn.ManagedClientConnection, long, java.util.concurrent.TimeUnit):void");
    }

    public void shutdown() {
        this.log.debug("Connection manager is shutting down");
        try {
            this.pool.shutdown();
        } catch (IOException ex) {
            this.log.debug("I/O exception shutting down connection manager", ex);
        }
        this.log.debug("Connection manager shut down");
    }

    public void closeIdleConnections(long idleTimeout, TimeUnit tunit) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Closing connections idle longer than " + idleTimeout + " " + tunit);
        }
        this.pool.closeIdle(idleTimeout, tunit);
    }

    public void closeExpiredConnections() {
        this.log.debug("Closing expired connections");
        this.pool.closeExpired();
    }

    public int getMaxTotal() {
        return this.pool.getMaxTotal();
    }

    public void setMaxTotal(int max) {
        this.pool.setMaxTotal(max);
    }

    public int getDefaultMaxPerRoute() {
        return this.pool.getDefaultMaxPerRoute();
    }

    public void setDefaultMaxPerRoute(int max) {
        this.pool.setDefaultMaxPerRoute(max);
    }

    public int getMaxPerRoute(HttpRoute route) {
        return this.pool.getMaxPerRoute(route);
    }

    public void setMaxPerRoute(HttpRoute route, int max) {
        this.pool.setMaxPerRoute(route, max);
    }

    public PoolStats getTotalStats() {
        return this.pool.getTotalStats();
    }

    public PoolStats getStats(HttpRoute route) {
        return this.pool.getStats(route);
    }
}
