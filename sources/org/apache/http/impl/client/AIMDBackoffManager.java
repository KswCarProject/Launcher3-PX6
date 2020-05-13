package org.apache.http.impl.client;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.BackoffManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.util.Args;

public class AIMDBackoffManager implements BackoffManager {
    private double backoffFactor;
    private int cap;
    private final Clock clock;
    private final ConnPoolControl<HttpRoute> connPerRoute;
    private long coolDown;
    private final Map<HttpRoute, Long> lastRouteBackoffs;
    private final Map<HttpRoute, Long> lastRouteProbes;

    public AIMDBackoffManager(ConnPoolControl<HttpRoute> connPerRoute2) {
        this(connPerRoute2, new SystemClock());
    }

    AIMDBackoffManager(ConnPoolControl<HttpRoute> connPerRoute2, Clock clock2) {
        this.coolDown = 5000;
        this.backoffFactor = 0.5d;
        this.cap = 2;
        this.clock = clock2;
        this.connPerRoute = connPerRoute2;
        this.lastRouteProbes = new HashMap();
        this.lastRouteBackoffs = new HashMap();
    }

    public void backOff(HttpRoute route) {
        synchronized (this.connPerRoute) {
            int curr = this.connPerRoute.getMaxPerRoute(route);
            Long lastUpdate = getLastUpdate(this.lastRouteBackoffs, route);
            long now = this.clock.getCurrentTime();
            if (now - lastUpdate.longValue() >= this.coolDown) {
                this.connPerRoute.setMaxPerRoute(route, getBackedOffPoolSize(curr));
                this.lastRouteBackoffs.put(route, Long.valueOf(now));
            }
        }
    }

    private int getBackedOffPoolSize(int curr) {
        if (curr <= 1) {
            return 1;
        }
        return (int) Math.floor(this.backoffFactor * ((double) curr));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void probe(org.apache.http.conn.routing.HttpRoute r13) {
        /*
            r12 = this;
            org.apache.http.pool.ConnPoolControl<org.apache.http.conn.routing.HttpRoute> r7 = r12.connPerRoute
            monitor-enter(r7)
            org.apache.http.pool.ConnPoolControl<org.apache.http.conn.routing.HttpRoute> r6 = r12.connPerRoute     // Catch:{ all -> 0x004e }
            int r0 = r6.getMaxPerRoute(r13)     // Catch:{ all -> 0x004e }
            int r6 = r12.cap     // Catch:{ all -> 0x004e }
            if (r0 < r6) goto L_0x003b
            int r3 = r12.cap     // Catch:{ all -> 0x004e }
        L_0x000f:
            java.util.Map<org.apache.http.conn.routing.HttpRoute, java.lang.Long> r6 = r12.lastRouteProbes     // Catch:{ all -> 0x004e }
            java.lang.Long r2 = r12.getLastUpdate(r6, r13)     // Catch:{ all -> 0x004e }
            java.util.Map<org.apache.http.conn.routing.HttpRoute, java.lang.Long> r6 = r12.lastRouteBackoffs     // Catch:{ all -> 0x004e }
            java.lang.Long r1 = r12.getLastUpdate(r6, r13)     // Catch:{ all -> 0x004e }
            org.apache.http.impl.client.Clock r6 = r12.clock     // Catch:{ all -> 0x004e }
            long r4 = r6.getCurrentTime()     // Catch:{ all -> 0x004e }
            long r8 = r2.longValue()     // Catch:{ all -> 0x004e }
            long r8 = r4 - r8
            long r10 = r12.coolDown     // Catch:{ all -> 0x004e }
            int r6 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r6 < 0) goto L_0x0039
            long r8 = r1.longValue()     // Catch:{ all -> 0x004e }
            long r8 = r4 - r8
            long r10 = r12.coolDown     // Catch:{ all -> 0x004e }
            int r6 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r6 >= 0) goto L_0x003e
        L_0x0039:
            monitor-exit(r7)     // Catch:{ all -> 0x004e }
        L_0x003a:
            return
        L_0x003b:
            int r3 = r0 + 1
            goto L_0x000f
        L_0x003e:
            org.apache.http.pool.ConnPoolControl<org.apache.http.conn.routing.HttpRoute> r6 = r12.connPerRoute     // Catch:{ all -> 0x004e }
            r6.setMaxPerRoute(r13, r3)     // Catch:{ all -> 0x004e }
            java.util.Map<org.apache.http.conn.routing.HttpRoute, java.lang.Long> r6 = r12.lastRouteProbes     // Catch:{ all -> 0x004e }
            java.lang.Long r8 = java.lang.Long.valueOf(r4)     // Catch:{ all -> 0x004e }
            r6.put(r13, r8)     // Catch:{ all -> 0x004e }
            monitor-exit(r7)     // Catch:{ all -> 0x004e }
            goto L_0x003a
        L_0x004e:
            r6 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x004e }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.client.AIMDBackoffManager.probe(org.apache.http.conn.routing.HttpRoute):void");
    }

    private Long getLastUpdate(Map<HttpRoute, Long> updates, HttpRoute route) {
        Long lastUpdate = updates.get(route);
        if (lastUpdate == null) {
            return 0L;
        }
        return lastUpdate;
    }

    public void setBackoffFactor(double d) {
        Args.check(d > 0.0d && d < 1.0d, "Backoff factor must be 0.0 < f < 1.0");
        this.backoffFactor = d;
    }

    public void setCooldownMillis(long l) {
        Args.positive(this.coolDown, "Cool down");
        this.coolDown = l;
    }

    public void setPerHostConnectionCap(int cap2) {
        Args.positive(cap2, "Per host connection cap");
        this.cap = cap2;
    }
}
