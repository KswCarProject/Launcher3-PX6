package org.apache.http.impl.conn.tsccm;

import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Deprecated
public class ThreadSafeClientConnManager implements ClientConnectionManager {
    protected final ClientConnectionOperator connOperator;
    protected final ConnPerRouteBean connPerRoute;
    protected final AbstractConnPool connectionPool;
    /* access modifiers changed from: private */
    public final Log log;
    protected final ConnPoolByRoute pool;
    protected final SchemeRegistry schemeRegistry;

    public ThreadSafeClientConnManager(SchemeRegistry schreg) {
        this(schreg, -1, TimeUnit.MILLISECONDS);
    }

    public ThreadSafeClientConnManager() {
        this(SchemeRegistryFactory.createDefault());
    }

    public ThreadSafeClientConnManager(SchemeRegistry schreg, long connTTL, TimeUnit connTTLTimeUnit) {
        this(schreg, connTTL, connTTLTimeUnit, new ConnPerRouteBean());
    }

    public ThreadSafeClientConnManager(SchemeRegistry schreg, long connTTL, TimeUnit connTTLTimeUnit, ConnPerRouteBean connPerRoute2) {
        Args.notNull(schreg, "Scheme registry");
        this.log = LogFactory.getLog(getClass());
        this.schemeRegistry = schreg;
        this.connPerRoute = connPerRoute2;
        this.connOperator = createConnectionOperator(schreg);
        this.pool = createConnectionPool(connTTL, connTTLTimeUnit);
        this.connectionPool = this.pool;
    }

    @Deprecated
    public ThreadSafeClientConnManager(HttpParams params, SchemeRegistry schreg) {
        Args.notNull(schreg, "Scheme registry");
        this.log = LogFactory.getLog(getClass());
        this.schemeRegistry = schreg;
        this.connPerRoute = new ConnPerRouteBean();
        this.connOperator = createConnectionOperator(schreg);
        this.pool = (ConnPoolByRoute) createConnectionPool(params);
        this.connectionPool = this.pool;
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
    @Deprecated
    public AbstractConnPool createConnectionPool(HttpParams params) {
        return new ConnPoolByRoute(this.connOperator, params);
    }

    /* access modifiers changed from: protected */
    public ConnPoolByRoute createConnectionPool(long connTTL, TimeUnit connTTLTimeUnit) {
        return new ConnPoolByRoute(this.connOperator, this.connPerRoute, 20, connTTL, connTTLTimeUnit);
    }

    /* access modifiers changed from: protected */
    public ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg);
    }

    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    public ClientConnectionRequest requestConnection(final HttpRoute route, Object state) {
        final PoolEntryRequest poolRequest = this.pool.requestPoolEntry(route, state);
        return new ClientConnectionRequest() {
            public void abortRequest() {
                poolRequest.abortRequest();
            }

            public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
                Args.notNull(route, "Route");
                if (ThreadSafeClientConnManager.this.log.isDebugEnabled()) {
                    ThreadSafeClientConnManager.this.log.debug("Get connection: " + route + ", timeout = " + timeout);
                }
                return new BasicPooledConnAdapter(ThreadSafeClientConnManager.this, poolRequest.getPoolEntry(timeout, tunit));
            }
        };
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:36:0x0074=Splitter:B:36:0x0074, B:18:0x0038=Splitter:B:18:0x0038} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseConnection(org.apache.http.conn.ManagedClientConnection r11, long r12, java.util.concurrent.TimeUnit r14) {
        /*
            r10 = this;
            boolean r1 = r11 instanceof org.apache.http.impl.conn.tsccm.BasicPooledConnAdapter
            java.lang.String r4 = "Connection class mismatch, connection not obtained from this manager"
            org.apache.http.util.Args.check(r1, r4)
            r0 = r11
            org.apache.http.impl.conn.tsccm.BasicPooledConnAdapter r0 = (org.apache.http.impl.conn.tsccm.BasicPooledConnAdapter) r0
            org.apache.http.impl.conn.AbstractPoolEntry r1 = r0.getPoolEntry()
            if (r1 == 0) goto L_0x001c
            org.apache.http.conn.ClientConnectionManager r1 = r0.getManager()
            if (r1 != r10) goto L_0x0027
            r1 = 1
        L_0x0017:
            java.lang.String r4 = "Connection not obtained from this manager"
            org.apache.http.util.Asserts.check(r1, r4)
        L_0x001c:
            monitor-enter(r0)
            org.apache.http.impl.conn.AbstractPoolEntry r2 = r0.getPoolEntry()     // Catch:{ all -> 0x0059 }
            org.apache.http.impl.conn.tsccm.BasicPoolEntry r2 = (org.apache.http.impl.conn.tsccm.BasicPoolEntry) r2     // Catch:{ all -> 0x0059 }
            if (r2 != 0) goto L_0x0029
            monitor-exit(r0)     // Catch:{ all -> 0x0059 }
        L_0x0026:
            return
        L_0x0027:
            r1 = 0
            goto L_0x0017
        L_0x0029:
            boolean r1 = r0.isOpen()     // Catch:{ IOException -> 0x0064 }
            if (r1 == 0) goto L_0x0038
            boolean r1 = r0.isMarkedReusable()     // Catch:{ IOException -> 0x0064 }
            if (r1 != 0) goto L_0x0038
            r0.shutdown()     // Catch:{ IOException -> 0x0064 }
        L_0x0038:
            boolean r3 = r0.isMarkedReusable()     // Catch:{ all -> 0x0059 }
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            boolean r1 = r1.isDebugEnabled()     // Catch:{ all -> 0x0059 }
            if (r1 == 0) goto L_0x004d
            if (r3 == 0) goto L_0x005c
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = "Released connection is reusable."
            r1.debug(r4)     // Catch:{ all -> 0x0059 }
        L_0x004d:
            r0.detach()     // Catch:{ all -> 0x0059 }
            org.apache.http.impl.conn.tsccm.ConnPoolByRoute r1 = r10.pool     // Catch:{ all -> 0x0059 }
            r4 = r12
            r6 = r14
            r1.freeEntry(r2, r3, r4, r6)     // Catch:{ all -> 0x0059 }
        L_0x0057:
            monitor-exit(r0)     // Catch:{ all -> 0x0059 }
            goto L_0x0026
        L_0x0059:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0059 }
            throw r1
        L_0x005c:
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = "Released connection is not reusable."
            r1.debug(r4)     // Catch:{ all -> 0x0059 }
            goto L_0x004d
        L_0x0064:
            r7 = move-exception
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x009c }
            boolean r1 = r1.isDebugEnabled()     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x0074
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x009c }
            java.lang.String r4 = "Exception shutting down released connection."
            r1.debug(r4, r7)     // Catch:{ all -> 0x009c }
        L_0x0074:
            boolean r3 = r0.isMarkedReusable()     // Catch:{ all -> 0x0059 }
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            boolean r1 = r1.isDebugEnabled()     // Catch:{ all -> 0x0059 }
            if (r1 == 0) goto L_0x0089
            if (r3 == 0) goto L_0x0094
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = "Released connection is reusable."
            r1.debug(r4)     // Catch:{ all -> 0x0059 }
        L_0x0089:
            r0.detach()     // Catch:{ all -> 0x0059 }
            org.apache.http.impl.conn.tsccm.ConnPoolByRoute r1 = r10.pool     // Catch:{ all -> 0x0059 }
            r4 = r12
            r6 = r14
            r1.freeEntry(r2, r3, r4, r6)     // Catch:{ all -> 0x0059 }
            goto L_0x0057
        L_0x0094:
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = "Released connection is not reusable."
            r1.debug(r4)     // Catch:{ all -> 0x0059 }
            goto L_0x0089
        L_0x009c:
            r1 = move-exception
            r8 = r1
            boolean r3 = r0.isMarkedReusable()     // Catch:{ all -> 0x0059 }
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            boolean r1 = r1.isDebugEnabled()     // Catch:{ all -> 0x0059 }
            if (r1 == 0) goto L_0x00b3
            if (r3 == 0) goto L_0x00be
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = "Released connection is reusable."
            r1.debug(r4)     // Catch:{ all -> 0x0059 }
        L_0x00b3:
            r0.detach()     // Catch:{ all -> 0x0059 }
            org.apache.http.impl.conn.tsccm.ConnPoolByRoute r1 = r10.pool     // Catch:{ all -> 0x0059 }
            r4 = r12
            r6 = r14
            r1.freeEntry(r2, r3, r4, r6)     // Catch:{ all -> 0x0059 }
            throw r8     // Catch:{ all -> 0x0059 }
        L_0x00be:
            org.apache.commons.logging.Log r1 = r10.log     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = "Released connection is not reusable."
            r1.debug(r4)     // Catch:{ all -> 0x0059 }
            goto L_0x00b3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager.releaseConnection(org.apache.http.conn.ManagedClientConnection, long, java.util.concurrent.TimeUnit):void");
    }

    public void shutdown() {
        this.log.debug("Shutting down");
        this.pool.shutdown();
    }

    public int getConnectionsInPool(HttpRoute route) {
        return this.pool.getConnectionsInPool(route);
    }

    public int getConnectionsInPool() {
        return this.pool.getConnectionsInPool();
    }

    public void closeIdleConnections(long idleTimeout, TimeUnit tunit) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Closing connections idle longer than " + idleTimeout + " " + tunit);
        }
        this.pool.closeIdleConnections(idleTimeout, tunit);
    }

    public void closeExpiredConnections() {
        this.log.debug("Closing expired connections");
        this.pool.closeExpiredConnections();
    }

    public int getMaxTotal() {
        return this.pool.getMaxTotalConnections();
    }

    public void setMaxTotal(int max) {
        this.pool.setMaxTotalConnections(max);
    }

    public int getDefaultMaxPerRoute() {
        return this.connPerRoute.getDefaultMaxPerRoute();
    }

    public void setDefaultMaxPerRoute(int max) {
        this.connPerRoute.setDefaultMaxPerRoute(max);
    }

    public int getMaxForRoute(HttpRoute route) {
        return this.connPerRoute.getMaxForRoute(route);
    }

    public void setMaxForRoute(HttpRoute route, int max) {
        this.connPerRoute.setMaxForRoute(route, max);
    }
}
