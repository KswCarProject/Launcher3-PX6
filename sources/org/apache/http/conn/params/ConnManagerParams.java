package org.apache.http.conn.params;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public final class ConnManagerParams implements ConnManagerPNames {
    private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE = new ConnPerRoute() {
        public int getMaxForRoute(HttpRoute route) {
            return 2;
        }
    };
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    @Deprecated
    public static long getTimeout(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getLongParameter("http.conn-manager.timeout", 0);
    }

    @Deprecated
    public static void setTimeout(HttpParams params, long timeout) {
        Args.notNull(params, "HTTP parameters");
        params.setLongParameter("http.conn-manager.timeout", timeout);
    }

    public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute) {
        Args.notNull(params, "HTTP parameters");
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, connPerRoute);
    }

    public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        ConnPerRoute connPerRoute = (ConnPerRoute) params.getParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE);
        if (connPerRoute == null) {
            return DEFAULT_CONN_PER_ROUTE;
        }
        return connPerRoute;
    }

    public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
        Args.notNull(params, "HTTP parameters");
        params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, maxTotalConnections);
    }

    public static int getMaxTotalConnections(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 20);
    }
}
