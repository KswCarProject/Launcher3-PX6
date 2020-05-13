package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public class ConnPoolByRoute extends AbstractConnPool {
    protected final ConnPerRoute connPerRoute;
    private final long connTTL;
    private final TimeUnit connTTLTimeUnit;
    protected final Queue<BasicPoolEntry> freeConnections;
    protected final Set<BasicPoolEntry> leasedConnections;
    private final Log log;
    protected volatile int maxTotalConnections;
    protected volatile int numConnections;
    protected final ClientConnectionOperator operator;
    /* access modifiers changed from: private */
    public final Lock poolLock;
    protected final Map<HttpRoute, RouteSpecificPool> routeToPool;
    protected volatile boolean shutdown;
    protected final Queue<WaitingThread> waitingThreads;

    public ConnPoolByRoute(ClientConnectionOperator operator2, ConnPerRoute connPerRoute2, int maxTotalConnections2) {
        this(operator2, connPerRoute2, maxTotalConnections2, -1, TimeUnit.MILLISECONDS);
    }

    public ConnPoolByRoute(ClientConnectionOperator operator2, ConnPerRoute connPerRoute2, int maxTotalConnections2, long connTTL2, TimeUnit connTTLTimeUnit2) {
        this.log = LogFactory.getLog(getClass());
        Args.notNull(operator2, "Connection operator");
        Args.notNull(connPerRoute2, "Connections per route");
        this.poolLock = this.poolLock;
        this.leasedConnections = this.leasedConnections;
        this.operator = operator2;
        this.connPerRoute = connPerRoute2;
        this.maxTotalConnections = maxTotalConnections2;
        this.freeConnections = createFreeConnQueue();
        this.waitingThreads = createWaitingThreadQueue();
        this.routeToPool = createRouteToPoolMap();
        this.connTTL = connTTL2;
        this.connTTLTimeUnit = connTTLTimeUnit2;
    }

    /* access modifiers changed from: protected */
    public Lock getLock() {
        return this.poolLock;
    }

    @Deprecated
    public ConnPoolByRoute(ClientConnectionOperator operator2, HttpParams params) {
        this(operator2, ConnManagerParams.getMaxConnectionsPerRoute(params), ConnManagerParams.getMaxTotalConnections(params));
    }

    /* access modifiers changed from: protected */
    public Queue<BasicPoolEntry> createFreeConnQueue() {
        return new LinkedList();
    }

    /* access modifiers changed from: protected */
    public Queue<WaitingThread> createWaitingThreadQueue() {
        return new LinkedList();
    }

    /* access modifiers changed from: protected */
    public Map<HttpRoute, RouteSpecificPool> createRouteToPoolMap() {
        return new HashMap();
    }

    /* access modifiers changed from: protected */
    public RouteSpecificPool newRouteSpecificPool(HttpRoute route) {
        return new RouteSpecificPool(route, this.connPerRoute);
    }

    /* access modifiers changed from: protected */
    public WaitingThread newWaitingThread(Condition cond, RouteSpecificPool rospl) {
        return new WaitingThread(cond, rospl);
    }

    private void closeConnection(BasicPoolEntry entry) {
        OperatedClientConnection conn = entry.getConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException ex) {
                this.log.debug("I/O error closing connection", ex);
            }
        }
    }

    /* access modifiers changed from: protected */
    public RouteSpecificPool getRoutePool(HttpRoute route, boolean create) {
        this.poolLock.lock();
        try {
            RouteSpecificPool rospl = this.routeToPool.get(route);
            if (rospl == null && create) {
                rospl = newRouteSpecificPool(route);
                this.routeToPool.put(route, rospl);
            }
            return rospl;
        } finally {
            this.poolLock.unlock();
        }
    }

    public int getConnectionsInPool(HttpRoute route) {
        int i = 0;
        this.poolLock.lock();
        try {
            RouteSpecificPool rospl = getRoutePool(route, false);
            if (rospl != null) {
                i = rospl.getEntryCount();
            }
            return i;
        } finally {
            this.poolLock.unlock();
        }
    }

    public int getConnectionsInPool() {
        this.poolLock.lock();
        try {
            return this.numConnections;
        } finally {
            this.poolLock.unlock();
        }
    }

    public PoolEntryRequest requestPoolEntry(final HttpRoute route, final Object state) {
        final WaitingThreadAborter aborter = new WaitingThreadAborter();
        return new PoolEntryRequest() {
            public void abortRequest() {
                ConnPoolByRoute.this.poolLock.lock();
                try {
                    aborter.abort();
                } finally {
                    ConnPoolByRoute.this.poolLock.unlock();
                }
            }

            public BasicPoolEntry getPoolEntry(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
                return ConnPoolByRoute.this.getEntryBlocking(route, state, timeout, tunit, aborter);
            }
        };
    }

    /* access modifiers changed from: protected */
    public BasicPoolEntry getEntryBlocking(HttpRoute route, Object state, long timeout, TimeUnit tunit, WaitingThreadAborter aborter) throws ConnectionPoolTimeoutException, InterruptedException {
        RouteSpecificPool rospl;
        WaitingThread waitingThread;
        Date deadline = null;
        if (timeout > 0) {
            deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
        }
        BasicPoolEntry entry = null;
        this.poolLock.lock();
        try {
            rospl = getRoutePool(route, true);
            waitingThread = null;
            while (entry == null) {
                Asserts.check(!this.shutdown, "Connection pool shut down");
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[" + route + "] total kept alive: " + this.freeConnections.size() + ", total issued: " + this.leasedConnections.size() + ", total allocated: " + this.numConnections + " out of " + this.maxTotalConnections);
                }
                entry = getFreeEntry(rospl, state);
                if (entry != null) {
                    break;
                }
                boolean hasCapacity = rospl.getCapacity() > 0;
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Available capacity: " + rospl.getCapacity() + " out of " + rospl.getMaxEntries() + " [" + route + "][" + state + "]");
                }
                if (hasCapacity && this.numConnections < this.maxTotalConnections) {
                    entry = createEntry(rospl, this.operator);
                } else if (!hasCapacity || this.freeConnections.isEmpty()) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Need to wait for connection [" + route + "][" + state + "]");
                    }
                    if (waitingThread == null) {
                        waitingThread = newWaitingThread(this.poolLock.newCondition(), rospl);
                        aborter.setWaitingThread(waitingThread);
                    }
                    rospl.queueThread(waitingThread);
                    this.waitingThreads.add(waitingThread);
                    boolean success = waitingThread.await(deadline);
                    rospl.removeThread(waitingThread);
                    this.waitingThreads.remove(waitingThread);
                    if (!success && deadline != null && deadline.getTime() <= System.currentTimeMillis()) {
                        throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
                    }
                } else {
                    deleteLeastUsedEntry();
                    rospl = getRoutePool(route, true);
                    entry = createEntry(rospl, this.operator);
                }
            }
            this.poolLock.unlock();
            return entry;
        } catch (Throwable th) {
            this.poolLock.unlock();
            throw th;
        }
    }

    public void freeEntry(BasicPoolEntry entry, boolean reusable, long validDuration, TimeUnit timeUnit) {
        String s;
        HttpRoute route = entry.getPlannedRoute();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Releasing connection [" + route + "][" + entry.getState() + "]");
        }
        this.poolLock.lock();
        try {
            if (this.shutdown) {
                closeConnection(entry);
                return;
            }
            this.leasedConnections.remove(entry);
            RouteSpecificPool rospl = getRoutePool(route, true);
            if (!reusable || rospl.getCapacity() < 0) {
                closeConnection(entry);
                rospl.dropEntry();
                this.numConnections--;
            } else {
                if (this.log.isDebugEnabled()) {
                    if (validDuration > 0) {
                        s = "for " + validDuration + " " + timeUnit;
                    } else {
                        s = "indefinitely";
                    }
                    this.log.debug("Pooling connection [" + route + "][" + entry.getState() + "]; keep alive " + s);
                }
                rospl.freeEntry(entry);
                entry.updateExpiry(validDuration, timeUnit);
                this.freeConnections.add(entry);
            }
            notifyWaitingThread(rospl);
            this.poolLock.unlock();
        } finally {
            this.poolLock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public BasicPoolEntry getFreeEntry(RouteSpecificPool rospl, Object state) {
        BasicPoolEntry entry = null;
        this.poolLock.lock();
        boolean done = false;
        while (!done) {
            try {
                entry = rospl.allocEntry(state);
                if (entry != null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Getting free connection [" + rospl.getRoute() + "][" + state + "]");
                    }
                    this.freeConnections.remove(entry);
                    if (entry.isExpired(System.currentTimeMillis())) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Closing expired free connection [" + rospl.getRoute() + "][" + state + "]");
                        }
                        closeConnection(entry);
                        rospl.dropEntry();
                        this.numConnections--;
                    } else {
                        this.leasedConnections.add(entry);
                        done = true;
                    }
                } else {
                    done = true;
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("No free connections [" + rospl.getRoute() + "][" + state + "]");
                    }
                }
            } catch (Throwable th) {
                this.poolLock.unlock();
                throw th;
            }
        }
        this.poolLock.unlock();
        return entry;
    }

    /* access modifiers changed from: protected */
    public BasicPoolEntry createEntry(RouteSpecificPool rospl, ClientConnectionOperator op) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Creating new connection [" + rospl.getRoute() + "]");
        }
        BasicPoolEntry entry = new BasicPoolEntry(op, rospl.getRoute(), this.connTTL, this.connTTLTimeUnit);
        this.poolLock.lock();
        try {
            rospl.createdEntry(entry);
            this.numConnections++;
            this.leasedConnections.add(entry);
            return entry;
        } finally {
            this.poolLock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public void deleteEntry(BasicPoolEntry entry) {
        HttpRoute route = entry.getPlannedRoute();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Deleting connection [" + route + "][" + entry.getState() + "]");
        }
        this.poolLock.lock();
        try {
            closeConnection(entry);
            RouteSpecificPool rospl = getRoutePool(route, true);
            rospl.deleteEntry(entry);
            this.numConnections--;
            if (rospl.isUnused()) {
                this.routeToPool.remove(route);
            }
        } finally {
            this.poolLock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public void deleteLeastUsedEntry() {
        this.poolLock.lock();
        try {
            BasicPoolEntry entry = this.freeConnections.remove();
            if (entry != null) {
                deleteEntry(entry);
            } else if (this.log.isDebugEnabled()) {
                this.log.debug("No free connection to delete");
            }
        } finally {
            this.poolLock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public void handleLostEntry(HttpRoute route) {
        this.poolLock.lock();
        try {
            RouteSpecificPool rospl = getRoutePool(route, true);
            rospl.dropEntry();
            if (rospl.isUnused()) {
                this.routeToPool.remove(route);
            }
            this.numConnections--;
            notifyWaitingThread(rospl);
        } finally {
            this.poolLock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x003e A[Catch:{ all -> 0x0079 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyWaitingThread(org.apache.http.impl.conn.tsccm.RouteSpecificPool r6) {
        /*
            r5 = this;
            r1 = 0
            java.util.concurrent.locks.Lock r2 = r5.poolLock
            r2.lock()
            if (r6 == 0) goto L_0x0047
            boolean r2 = r6.hasThread()     // Catch:{ all -> 0x0079 }
            if (r2 == 0) goto L_0x0047
            org.apache.commons.logging.Log r2 = r5.log     // Catch:{ all -> 0x0079 }
            boolean r2 = r2.isDebugEnabled()     // Catch:{ all -> 0x0079 }
            if (r2 == 0) goto L_0x0038
            org.apache.commons.logging.Log r2 = r5.log     // Catch:{ all -> 0x0079 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0079 }
            r3.<init>()     // Catch:{ all -> 0x0079 }
            java.lang.String r4 = "Notifying thread waiting on pool ["
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x0079 }
            org.apache.http.conn.routing.HttpRoute r4 = r6.getRoute()     // Catch:{ all -> 0x0079 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x0079 }
            java.lang.String r4 = "]"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x0079 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0079 }
            r2.debug(r3)     // Catch:{ all -> 0x0079 }
        L_0x0038:
            org.apache.http.impl.conn.tsccm.WaitingThread r1 = r6.nextThread()     // Catch:{ all -> 0x0079 }
        L_0x003c:
            if (r1 == 0) goto L_0x0041
            r1.wakeup()     // Catch:{ all -> 0x0079 }
        L_0x0041:
            java.util.concurrent.locks.Lock r2 = r5.poolLock
            r2.unlock()
            return
        L_0x0047:
            java.util.Queue<org.apache.http.impl.conn.tsccm.WaitingThread> r2 = r5.waitingThreads     // Catch:{ all -> 0x0079 }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0079 }
            if (r2 != 0) goto L_0x0069
            org.apache.commons.logging.Log r2 = r5.log     // Catch:{ all -> 0x0079 }
            boolean r2 = r2.isDebugEnabled()     // Catch:{ all -> 0x0079 }
            if (r2 == 0) goto L_0x005e
            org.apache.commons.logging.Log r2 = r5.log     // Catch:{ all -> 0x0079 }
            java.lang.String r3 = "Notifying thread waiting on any pool"
            r2.debug(r3)     // Catch:{ all -> 0x0079 }
        L_0x005e:
            java.util.Queue<org.apache.http.impl.conn.tsccm.WaitingThread> r2 = r5.waitingThreads     // Catch:{ all -> 0x0079 }
            java.lang.Object r2 = r2.remove()     // Catch:{ all -> 0x0079 }
            r0 = r2
            org.apache.http.impl.conn.tsccm.WaitingThread r0 = (org.apache.http.impl.conn.tsccm.WaitingThread) r0     // Catch:{ all -> 0x0079 }
            r1 = r0
            goto L_0x003c
        L_0x0069:
            org.apache.commons.logging.Log r2 = r5.log     // Catch:{ all -> 0x0079 }
            boolean r2 = r2.isDebugEnabled()     // Catch:{ all -> 0x0079 }
            if (r2 == 0) goto L_0x003c
            org.apache.commons.logging.Log r2 = r5.log     // Catch:{ all -> 0x0079 }
            java.lang.String r3 = "Notifying no-one, there are no waiting threads"
            r2.debug(r3)     // Catch:{ all -> 0x0079 }
            goto L_0x003c
        L_0x0079:
            r2 = move-exception
            java.util.concurrent.locks.Lock r3 = r5.poolLock
            r3.unlock()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.tsccm.ConnPoolByRoute.notifyWaitingThread(org.apache.http.impl.conn.tsccm.RouteSpecificPool):void");
    }

    public void deleteClosedConnections() {
        this.poolLock.lock();
        try {
            Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                BasicPoolEntry entry = iter.next();
                if (!entry.getConnection().isOpen()) {
                    iter.remove();
                    deleteEntry(entry);
                }
            }
        } finally {
            this.poolLock.unlock();
        }
    }

    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        long t = 0;
        Args.notNull(tunit, "Time unit");
        if (idletime > 0) {
            t = idletime;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Closing connections idle longer than " + t + " " + tunit);
        }
        long deadline = System.currentTimeMillis() - tunit.toMillis(t);
        this.poolLock.lock();
        try {
            Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                BasicPoolEntry entry = iter.next();
                if (entry.getUpdated() <= deadline) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Closing connection last used @ " + new Date(entry.getUpdated()));
                    }
                    iter.remove();
                    deleteEntry(entry);
                }
            }
        } finally {
            this.poolLock.unlock();
        }
    }

    public void closeExpiredConnections() {
        this.log.debug("Closing expired connections");
        long now = System.currentTimeMillis();
        this.poolLock.lock();
        try {
            Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                BasicPoolEntry entry = iter.next();
                if (entry.isExpired(now)) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Closing connection expired @ " + new Date(entry.getExpiry()));
                    }
                    iter.remove();
                    deleteEntry(entry);
                }
            }
        } finally {
            this.poolLock.unlock();
        }
    }

    public void shutdown() {
        this.poolLock.lock();
        try {
            if (!this.shutdown) {
                this.shutdown = true;
                Iterator<BasicPoolEntry> iter1 = this.leasedConnections.iterator();
                while (iter1.hasNext()) {
                    iter1.remove();
                    closeConnection(iter1.next());
                }
                Iterator<BasicPoolEntry> iter2 = this.freeConnections.iterator();
                while (iter2.hasNext()) {
                    BasicPoolEntry entry = iter2.next();
                    iter2.remove();
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Closing connection [" + entry.getPlannedRoute() + "][" + entry.getState() + "]");
                    }
                    closeConnection(entry);
                }
                Iterator<WaitingThread> iwth = this.waitingThreads.iterator();
                while (iwth.hasNext()) {
                    iwth.remove();
                    iwth.next().wakeup();
                }
                this.routeToPool.clear();
                this.poolLock.unlock();
            }
        } finally {
            this.poolLock.unlock();
        }
    }

    public void setMaxTotalConnections(int max) {
        this.poolLock.lock();
        try {
            this.maxTotalConnections = max;
        } finally {
            this.poolLock.unlock();
        }
    }

    public int getMaxTotalConnections() {
        return this.maxTotalConnections;
    }
}
