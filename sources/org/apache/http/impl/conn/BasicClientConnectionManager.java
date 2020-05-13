package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading = ThreadingBehavior.SAFE)
@Deprecated
public class BasicClientConnectionManager implements ClientConnectionManager {
    private static final AtomicLong COUNTER = new AtomicLong();
    public static final String MISUSE_MESSAGE = "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
    private ManagedClientConnectionImpl conn;
    private final ClientConnectionOperator connOperator;
    private final Log log;
    private HttpPoolEntry poolEntry;
    private final SchemeRegistry schemeRegistry;
    private volatile boolean shutdown;

    public BasicClientConnectionManager(SchemeRegistry schreg) {
        this.log = LogFactory.getLog(getClass());
        Args.notNull(schreg, "Scheme registry");
        this.schemeRegistry = schreg;
        this.connOperator = createConnectionOperator(schreg);
    }

    public BasicClientConnectionManager() {
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

    public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
        return new ClientConnectionRequest() {
            public void abortRequest() {
            }

            public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) {
                return BasicClientConnectionManager.this.getConnection(route, state);
            }
        };
    }

    private void assertNotShutdown() {
        Asserts.check(!this.shutdown, "Connection manager has been shut down");
    }

    /* access modifiers changed from: package-private */
    public ManagedClientConnection getConnection(HttpRoute route, Object state) {
        ManagedClientConnectionImpl managedClientConnectionImpl;
        Args.notNull(route, "Route");
        synchronized (this) {
            assertNotShutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Get connection for route " + route);
            }
            Asserts.check(this.conn == null, MISUSE_MESSAGE);
            if (this.poolEntry != null && !this.poolEntry.getPlannedRoute().equals(route)) {
                this.poolEntry.close();
                this.poolEntry = null;
            }
            if (this.poolEntry == null) {
                this.poolEntry = new HttpPoolEntry(this.log, Long.toString(COUNTER.getAndIncrement()), route, this.connOperator.createConnection(), 0, TimeUnit.MILLISECONDS);
            }
            if (this.poolEntry.isExpired(System.currentTimeMillis())) {
                this.poolEntry.close();
                this.poolEntry.getTracker().reset();
            }
            this.conn = new ManagedClientConnectionImpl(this, this.connOperator, this.poolEntry);
            managedClientConnectionImpl = this.conn;
        }
        return managedClientConnectionImpl;
    }

    private void shutdownConnection(HttpClientConnection conn2) {
        try {
            conn2.shutdown();
        } catch (IOException iox) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("I/O exception shutting down connection", iox);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        return;
     */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseConnection(org.apache.http.conn.ManagedClientConnection r7, long r8, java.util.concurrent.TimeUnit r10) {
        /*
            r6 = this;
            boolean r3 = r7 instanceof org.apache.http.impl.conn.ManagedClientConnectionImpl
            java.lang.String r4 = "Connection class mismatch, connection not obtained from this manager"
            org.apache.http.util.Args.check(r3, r4)
            r0 = r7
            org.apache.http.impl.conn.ManagedClientConnectionImpl r0 = (org.apache.http.impl.conn.ManagedClientConnectionImpl) r0
            monitor-enter(r0)
            org.apache.commons.logging.Log r3 = r6.log     // Catch:{ all -> 0x004a }
            boolean r3 = r3.isDebugEnabled()     // Catch:{ all -> 0x004a }
            if (r3 == 0) goto L_0x002b
            org.apache.commons.logging.Log r3 = r6.log     // Catch:{ all -> 0x004a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x004a }
            r4.<init>()     // Catch:{ all -> 0x004a }
            java.lang.String r5 = "Releasing connection "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x004a }
            java.lang.StringBuilder r4 = r4.append(r7)     // Catch:{ all -> 0x004a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x004a }
            r3.debug(r4)     // Catch:{ all -> 0x004a }
        L_0x002b:
            org.apache.http.impl.conn.HttpPoolEntry r3 = r0.getPoolEntry()     // Catch:{ all -> 0x004a }
            if (r3 != 0) goto L_0x0033
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
        L_0x0032:
            return
        L_0x0033:
            org.apache.http.conn.ClientConnectionManager r1 = r0.getManager()     // Catch:{ all -> 0x004a }
            if (r1 != r6) goto L_0x004d
            r3 = 1
        L_0x003a:
            java.lang.String r4 = "Connection not obtained from this manager"
            org.apache.http.util.Asserts.check(r3, r4)     // Catch:{ all -> 0x004a }
            monitor-enter(r6)     // Catch:{ all -> 0x004a }
            boolean r3 = r6.shutdown     // Catch:{ all -> 0x00dd }
            if (r3 == 0) goto L_0x004f
            r6.shutdownConnection(r0)     // Catch:{ all -> 0x00dd }
            monitor-exit(r6)     // Catch:{ all -> 0x00dd }
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            goto L_0x0032
        L_0x004a:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            throw r3
        L_0x004d:
            r3 = 0
            goto L_0x003a
        L_0x004f:
            boolean r3 = r0.isOpen()     // Catch:{ all -> 0x00ca }
            if (r3 == 0) goto L_0x005e
            boolean r3 = r0.isMarkedReusable()     // Catch:{ all -> 0x00ca }
            if (r3 != 0) goto L_0x005e
            r6.shutdownConnection(r0)     // Catch:{ all -> 0x00ca }
        L_0x005e:
            boolean r3 = r0.isMarkedReusable()     // Catch:{ all -> 0x00ca }
            if (r3 == 0) goto L_0x00af
            org.apache.http.impl.conn.HttpPoolEntry r4 = r6.poolEntry     // Catch:{ all -> 0x00ca }
            if (r10 == 0) goto L_0x00c4
            r3 = r10
        L_0x0069:
            r4.updateExpiry(r8, r3)     // Catch:{ all -> 0x00ca }
            org.apache.commons.logging.Log r3 = r6.log     // Catch:{ all -> 0x00ca }
            boolean r3 = r3.isDebugEnabled()     // Catch:{ all -> 0x00ca }
            if (r3 == 0) goto L_0x00af
            r4 = 0
            int r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x00c7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ca }
            r3.<init>()     // Catch:{ all -> 0x00ca }
            java.lang.String r4 = "for "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x00ca }
            java.lang.StringBuilder r3 = r3.append(r8)     // Catch:{ all -> 0x00ca }
            java.lang.String r4 = " "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x00ca }
            java.lang.StringBuilder r3 = r3.append(r10)     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = r3.toString()     // Catch:{ all -> 0x00ca }
        L_0x0097:
            org.apache.commons.logging.Log r3 = r6.log     // Catch:{ all -> 0x00ca }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ca }
            r4.<init>()     // Catch:{ all -> 0x00ca }
            java.lang.String r5 = "Connection can be kept alive "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x00ca }
            java.lang.StringBuilder r4 = r4.append(r2)     // Catch:{ all -> 0x00ca }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00ca }
            r3.debug(r4)     // Catch:{ all -> 0x00ca }
        L_0x00af:
            r0.detach()     // Catch:{ all -> 0x00dd }
            r3 = 0
            r6.conn = r3     // Catch:{ all -> 0x00dd }
            org.apache.http.impl.conn.HttpPoolEntry r3 = r6.poolEntry     // Catch:{ all -> 0x00dd }
            boolean r3 = r3.isClosed()     // Catch:{ all -> 0x00dd }
            if (r3 == 0) goto L_0x00c0
            r3 = 0
            r6.poolEntry = r3     // Catch:{ all -> 0x00dd }
        L_0x00c0:
            monitor-exit(r6)     // Catch:{ all -> 0x00dd }
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            goto L_0x0032
        L_0x00c4:
            java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ all -> 0x00ca }
            goto L_0x0069
        L_0x00c7:
            java.lang.String r2 = "indefinitely"
            goto L_0x0097
        L_0x00ca:
            r3 = move-exception
            r0.detach()     // Catch:{ all -> 0x00dd }
            r4 = 0
            r6.conn = r4     // Catch:{ all -> 0x00dd }
            org.apache.http.impl.conn.HttpPoolEntry r4 = r6.poolEntry     // Catch:{ all -> 0x00dd }
            boolean r4 = r4.isClosed()     // Catch:{ all -> 0x00dd }
            if (r4 == 0) goto L_0x00dc
            r4 = 0
            r6.poolEntry = r4     // Catch:{ all -> 0x00dd }
        L_0x00dc:
            throw r3     // Catch:{ all -> 0x00dd }
        L_0x00dd:
            r3 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00dd }
            throw r3     // Catch:{ all -> 0x004a }
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.BasicClientConnectionManager.releaseConnection(org.apache.http.conn.ManagedClientConnection, long, java.util.concurrent.TimeUnit):void");
    }

    public void closeExpiredConnections() {
        synchronized (this) {
            assertNotShutdown();
            long now = System.currentTimeMillis();
            if (this.poolEntry != null && this.poolEntry.isExpired(now)) {
                this.poolEntry.close();
                this.poolEntry.getTracker().reset();
            }
        }
    }

    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        synchronized (this) {
            assertNotShutdown();
            long time = tunit.toMillis(idletime);
            if (time < 0) {
                time = 0;
            }
            long deadline = System.currentTimeMillis() - time;
            if (this.poolEntry != null && this.poolEntry.getUpdated() <= deadline) {
                this.poolEntry.close();
                this.poolEntry.getTracker().reset();
            }
        }
    }

    public void shutdown() {
        synchronized (this) {
            this.shutdown = true;
            try {
                if (this.poolEntry != null) {
                    this.poolEntry.close();
                }
                this.poolEntry = null;
                this.conn = null;
            } catch (Throwable th) {
                this.poolEntry = null;
                this.conn = null;
                throw th;
            }
        }
    }
}
