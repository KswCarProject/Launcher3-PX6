package org.apache.http.conn.params;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE)
@Deprecated
public final class ConnPerRouteBean implements ConnPerRoute {
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
    private volatile int defaultMax;
    private final ConcurrentHashMap<HttpRoute, Integer> maxPerHostMap;

    public ConnPerRouteBean(int defaultMax2) {
        this.maxPerHostMap = new ConcurrentHashMap<>();
        setDefaultMaxPerRoute(defaultMax2);
    }

    public ConnPerRouteBean() {
        this(2);
    }

    public int getDefaultMax() {
        return this.defaultMax;
    }

    public int getDefaultMaxPerRoute() {
        return this.defaultMax;
    }

    public void setDefaultMaxPerRoute(int max) {
        Args.positive(max, "Default max per route");
        this.defaultMax = max;
    }

    public void setMaxForRoute(HttpRoute route, int max) {
        Args.notNull(route, "HTTP route");
        Args.positive(max, "Max per route");
        this.maxPerHostMap.put(route, Integer.valueOf(max));
    }

    public int getMaxForRoute(HttpRoute route) {
        Args.notNull(route, "HTTP route");
        Integer max = this.maxPerHostMap.get(route);
        if (max != null) {
            return max.intValue();
        }
        return this.defaultMax;
    }

    public void setMaxForRoutes(Map<HttpRoute, Integer> map) {
        if (map != null) {
            this.maxPerHostMap.clear();
            this.maxPerHostMap.putAll(map);
        }
    }

    public String toString() {
        return this.maxPerHostMap.toString();
    }
}
