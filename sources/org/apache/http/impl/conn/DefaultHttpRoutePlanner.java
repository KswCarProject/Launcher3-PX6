package org.apache.http.impl.conn;

import java.net.InetAddress;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading = ThreadingBehavior.SAFE)
@Deprecated
public class DefaultHttpRoutePlanner implements HttpRoutePlanner {
    protected final SchemeRegistry schemeRegistry;

    public DefaultHttpRoutePlanner(SchemeRegistry schreg) {
        Args.notNull(schreg, "Scheme registry");
        this.schemeRegistry = schreg;
    }

    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        HttpRoute route;
        Args.notNull(request, "HTTP request");
        HttpRoute route2 = ConnRouteParams.getForcedRoute(request.getParams());
        if (route2 != null) {
            return route2;
        }
        Asserts.notNull(target, "Target host");
        InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
        HttpHost proxy = ConnRouteParams.getDefaultProxy(request.getParams());
        try {
            boolean secure = this.schemeRegistry.getScheme(target.getSchemeName()).isLayered();
            if (proxy == null) {
                route = new HttpRoute(target, local, secure);
            } else {
                route = new HttpRoute(target, local, proxy, secure);
            }
            return route;
        } catch (IllegalStateException ex) {
            throw new HttpException(ex.getMessage());
        }
    }
}
