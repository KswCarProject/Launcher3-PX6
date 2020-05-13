package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading = ThreadingBehavior.SAFE)
@Deprecated
public class SingleClientConnManager implements ClientConnectionManager {
    public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
    protected final boolean alwaysShutDown;
    protected final ClientConnectionOperator connOperator;
    protected volatile long connectionExpiresTime;
    protected volatile boolean isShutDown;
    protected volatile long lastReleaseTime;
    private final Log log;
    protected volatile ConnAdapter managedConn;
    protected final SchemeRegistry schemeRegistry;
    protected volatile PoolEntry uniquePoolEntry;

    @Deprecated
    public SingleClientConnManager(HttpParams params, SchemeRegistry schreg) {
        this(schreg);
    }

    public SingleClientConnManager(SchemeRegistry schreg) {
        this.log = LogFactory.getLog(getClass());
        Args.notNull(schreg, "Scheme registry");
        this.schemeRegistry = schreg;
        this.connOperator = createConnectionOperator(schreg);
        this.uniquePoolEntry = new PoolEntry();
        this.managedConn = null;
        this.lastReleaseTime = -1;
        this.alwaysShutDown = false;
        this.isShutDown = false;
    }

    public SingleClientConnManager() {
        this(SchemeRegistryFactory.createDefault());
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            shutdown();
        } finally {
            super.finalize();
        }
    }

    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    /* access modifiers changed from: protected */
    public ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg);
    }

    /* access modifiers changed from: protected */
    public final void assertStillUp() throws IllegalStateException {
        Asserts.check(!this.isShutDown, "Manager is shut down");
    }

    public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
        return new ClientConnectionRequest() {
            public void abortRequest() {
            }

            public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) {
                return SingleClientConnManager.this.getConnection(route, state);
            }
        };
    }

    public ManagedClientConnection getConnection(HttpRoute route, Object state) {
        boolean z;
        ConnAdapter connAdapter;
        Args.notNull(route, "Route");
        assertStillUp();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Get connection for route " + route);
        }
        synchronized (this) {
            if (this.managedConn == null) {
                z = true;
            } else {
                z = false;
            }
            Asserts.check(z, MISUSE_MESSAGE);
            boolean recreate = false;
            boolean shutdown = false;
            closeExpiredConnections();
            if (this.uniquePoolEntry.connection.isOpen()) {
                RouteTracker tracker = this.uniquePoolEntry.tracker;
                if (tracker == null || !tracker.toRoute().equals(route)) {
                    shutdown = true;
                } else {
                    shutdown = false;
                }
            } else {
                recreate = true;
            }
            if (shutdown) {
                recreate = true;
                try {
                    this.uniquePoolEntry.shutdown();
                } catch (IOException iox) {
                    this.log.debug("Problem shutting down connection.", iox);
                }
            }
            if (recreate) {
                this.uniquePoolEntry = new PoolEntry();
            }
            this.managedConn = new ConnAdapter(this.uniquePoolEntry, route);
            connAdapter = this.managedConn;
        }
        return connAdapter;
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:52:0x00a1=Splitter:B:52:0x00a1, B:24:0x0064=Splitter:B:24:0x0064, B:33:0x007f=Splitter:B:33:0x007f} */
    public void releaseConnection(org.apache.http.conn.ManagedClientConnection r9, long r10, java.util.concurrent.TimeUnit r12) {
        /*
            r8 = this;
            r6 = 0
            boolean r3 = r9 instanceof org.apache.http.impl.conn.SingleClientConnManager.ConnAdapter
            java.lang.String r4 = "Connection class mismatch, connection not obtained from this manager"
            org.apache.http.util.Args.check(r3, r4)
            r8.assertStillUp()
            org.apache.commons.logging.Log r3 = r8.log
            boolean r3 = r3.isDebugEnabled()
            if (r3 == 0) goto L_0x002c
            org.apache.commons.logging.Log r3 = r8.log
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Releasing connection "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r9)
            java.lang.String r4 = r4.toString()
            r3.debug(r4)
        L_0x002c:
            r2 = r9
            org.apache.http.impl.conn.SingleClientConnManager$ConnAdapter r2 = (org.apache.http.impl.conn.SingleClientConnManager.ConnAdapter) r2
            monitor-enter(r2)
            org.apache.http.impl.conn.AbstractPoolEntry r3 = r2.poolEntry     // Catch:{ all -> 0x0081 }
            if (r3 != 0) goto L_0x0036
            monitor-exit(r2)     // Catch:{ all -> 0x0081 }
        L_0x0035:
            return
        L_0x0036:
            org.apache.http.conn.ClientConnectionManager r1 = r2.getManager()     // Catch:{ all -> 0x0081 }
            if (r1 != r8) goto L_0x0084
            r3 = 1
        L_0x003d:
            java.lang.String r4 = "Connection not obtained from this manager"
            org.apache.http.util.Asserts.check(r3, r4)     // Catch:{ all -> 0x0081 }
            boolean r3 = r2.isOpen()     // Catch:{ IOException -> 0x0091 }
            if (r3 == 0) goto L_0x0064
            boolean r3 = r8.alwaysShutDown     // Catch:{ IOException -> 0x0091 }
            if (r3 != 0) goto L_0x0052
            boolean r3 = r2.isMarkedReusable()     // Catch:{ IOException -> 0x0091 }
            if (r3 != 0) goto L_0x0064
        L_0x0052:
            org.apache.commons.logging.Log r3 = r8.log     // Catch:{ IOException -> 0x0091 }
            boolean r3 = r3.isDebugEnabled()     // Catch:{ IOException -> 0x0091 }
            if (r3 == 0) goto L_0x0061
            org.apache.commons.logging.Log r3 = r8.log     // Catch:{ IOException -> 0x0091 }
            java.lang.String r4 = "Released connection open but not reusable."
            r3.debug(r4)     // Catch:{ IOException -> 0x0091 }
        L_0x0061:
            r2.shutdown()     // Catch:{ IOException -> 0x0091 }
        L_0x0064:
            r2.detach()     // Catch:{ all -> 0x0081 }
            monitor-enter(r8)     // Catch:{ all -> 0x0081 }
            r3 = 0
            r8.managedConn = r3     // Catch:{ all -> 0x008e }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x008e }
            r8.lastReleaseTime = r4     // Catch:{ all -> 0x008e }
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0086
            long r4 = r12.toMillis(r10)     // Catch:{ all -> 0x008e }
            long r6 = r8.lastReleaseTime     // Catch:{ all -> 0x008e }
            long r4 = r4 + r6
            r8.connectionExpiresTime = r4     // Catch:{ all -> 0x008e }
        L_0x007e:
            monitor-exit(r8)     // Catch:{ all -> 0x008e }
        L_0x007f:
            monitor-exit(r2)     // Catch:{ all -> 0x0081 }
            goto L_0x0035
        L_0x0081:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0081 }
            throw r3
        L_0x0084:
            r3 = 0
            goto L_0x003d
        L_0x0086:
            r4 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r8.connectionExpiresTime = r4     // Catch:{ all -> 0x008e }
            goto L_0x007e
        L_0x008e:
            r3 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x008e }
            throw r3     // Catch:{ all -> 0x0081 }
        L_0x0091:
            r0 = move-exception
            org.apache.commons.logging.Log r3 = r8.log     // Catch:{ all -> 0x00c8 }
            boolean r3 = r3.isDebugEnabled()     // Catch:{ all -> 0x00c8 }
            if (r3 == 0) goto L_0x00a1
            org.apache.commons.logging.Log r3 = r8.log     // Catch:{ all -> 0x00c8 }
            java.lang.String r4 = "Exception shutting down released connection."
            r3.debug(r4, r0)     // Catch:{ all -> 0x00c8 }
        L_0x00a1:
            r2.detach()     // Catch:{ all -> 0x0081 }
            monitor-enter(r8)     // Catch:{ all -> 0x0081 }
            r3 = 0
            r8.managedConn = r3     // Catch:{ all -> 0x00bd }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00bd }
            r8.lastReleaseTime = r4     // Catch:{ all -> 0x00bd }
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x00c0
            long r4 = r12.toMillis(r10)     // Catch:{ all -> 0x00bd }
            long r6 = r8.lastReleaseTime     // Catch:{ all -> 0x00bd }
            long r4 = r4 + r6
            r8.connectionExpiresTime = r4     // Catch:{ all -> 0x00bd }
        L_0x00bb:
            monitor-exit(r8)     // Catch:{ all -> 0x00bd }
            goto L_0x007f
        L_0x00bd:
            r3 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00bd }
            throw r3     // Catch:{ all -> 0x0081 }
        L_0x00c0:
            r4 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r8.connectionExpiresTime = r4     // Catch:{ all -> 0x00bd }
            goto L_0x00bb
        L_0x00c8:
            r3 = move-exception
            r2.detach()     // Catch:{ all -> 0x0081 }
            monitor-enter(r8)     // Catch:{ all -> 0x0081 }
            r4 = 0
            r8.managedConn = r4     // Catch:{ all -> 0x00ed }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00ed }
            r8.lastReleaseTime = r4     // Catch:{ all -> 0x00ed }
            int r4 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x00e5
            long r4 = r12.toMillis(r10)     // Catch:{ all -> 0x00ed }
            long r6 = r8.lastReleaseTime     // Catch:{ all -> 0x00ed }
            long r4 = r4 + r6
            r8.connectionExpiresTime = r4     // Catch:{ all -> 0x00ed }
        L_0x00e3:
            monitor-exit(r8)     // Catch:{ all -> 0x00ed }
            throw r3     // Catch:{ all -> 0x0081 }
        L_0x00e5:
            r4 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r8.connectionExpiresTime = r4     // Catch:{ all -> 0x00ed }
            goto L_0x00e3
        L_0x00ed:
            r3 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00ed }
            throw r3     // Catch:{ all -> 0x0081 }
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.SingleClientConnManager.releaseConnection(org.apache.http.conn.ManagedClientConnection, long, java.util.concurrent.TimeUnit):void");
    }

    public void closeExpiredConnections() {
        if (System.currentTimeMillis() >= this.connectionExpiresTime) {
            closeIdleConnections(0, TimeUnit.MILLISECONDS);
        }
    }

    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        assertStillUp();
        Args.notNull(tunit, "Time unit");
        synchronized (this) {
            if (this.managedConn == null && this.uniquePoolEntry.connection.isOpen()) {
                if (this.lastReleaseTime <= System.currentTimeMillis() - tunit.toMillis(idletime)) {
                    try {
                        this.uniquePoolEntry.close();
                    } catch (IOException iox) {
                        this.log.debug("Problem closing idle connection.", iox);
                    }
                }
            }
        }
    }

    public void shutdown() {
        this.isShutDown = true;
        synchronized (this) {
            try {
                if (this.uniquePoolEntry != null) {
                    this.uniquePoolEntry.shutdown();
                }
                this.uniquePoolEntry = null;
                this.managedConn = null;
            } catch (IOException iox) {
                this.log.debug("Problem while shutting down manager.", iox);
                this.uniquePoolEntry = null;
                this.managedConn = null;
            } catch (Throwable th) {
                this.uniquePoolEntry = null;
                this.managedConn = null;
                throw th;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void revokeConnection() {
        ConnAdapter conn = this.managedConn;
        if (conn != null) {
            conn.detach();
            synchronized (this) {
                try {
                    this.uniquePoolEntry.shutdown();
                } catch (IOException iox) {
                    this.log.debug("Problem while shutting down connection.", iox);
                }
            }
            return;
        }
        return;
    }

    protected class PoolEntry extends AbstractPoolEntry {
        protected PoolEntry() {
            super(SingleClientConnManager.this.connOperator, (HttpRoute) null);
        }

        /* access modifiers changed from: protected */
        public void close() throws IOException {
            shutdownEntry();
            if (this.connection.isOpen()) {
                this.connection.close();
            }
        }

        /* access modifiers changed from: protected */
        public void shutdown() throws IOException {
            shutdownEntry();
            if (this.connection.isOpen()) {
                this.connection.shutdown();
            }
        }
    }

    protected class ConnAdapter extends AbstractPooledConnAdapter {
        protected ConnAdapter(PoolEntry entry, HttpRoute route) {
            super(SingleClientConnManager.this, entry);
            markReusable();
            entry.route = route;
        }
    }
}
