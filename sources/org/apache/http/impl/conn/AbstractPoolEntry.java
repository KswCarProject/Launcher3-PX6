package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public abstract class AbstractPoolEntry {
    protected final ClientConnectionOperator connOperator;
    protected final OperatedClientConnection connection;
    protected volatile HttpRoute route;
    protected volatile Object state;
    protected volatile RouteTracker tracker = null;

    protected AbstractPoolEntry(ClientConnectionOperator connOperator2, HttpRoute route2) {
        Args.notNull(connOperator2, "Connection operator");
        this.connOperator = connOperator2;
        this.connection = connOperator2.createConnection();
        this.route = route2;
    }

    public Object getState() {
        return this.state;
    }

    public void setState(Object state2) {
        this.state = state2;
    }

    public void open(HttpRoute route2, HttpContext context, HttpParams params) throws IOException {
        Args.notNull(route2, "Route");
        Args.notNull(params, "HTTP parameters");
        if (this.tracker != null) {
            Asserts.check(!this.tracker.isConnected(), "Connection already open");
        }
        this.tracker = new RouteTracker(route2);
        HttpHost proxy = route2.getProxyHost();
        this.connOperator.openConnection(this.connection, proxy != null ? proxy : route2.getTargetHost(), route2.getLocalAddress(), context, params);
        RouteTracker localTracker = this.tracker;
        if (localTracker == null) {
            throw new InterruptedIOException("Request aborted");
        } else if (proxy == null) {
            localTracker.connectTarget(this.connection.isSecure());
        } else {
            localTracker.connectProxy(proxy, this.connection.isSecure());
        }
    }

    public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
        Args.notNull(params, "HTTP parameters");
        Asserts.notNull(this.tracker, "Route tracker");
        Asserts.check(this.tracker.isConnected(), "Connection not open");
        Asserts.check(!this.tracker.isTunnelled(), "Connection is already tunnelled");
        this.connection.update((Socket) null, this.tracker.getTargetHost(), secure, params);
        this.tracker.tunnelTarget(secure);
    }

    public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
        Args.notNull(next, "Next proxy");
        Args.notNull(params, "Parameters");
        Asserts.notNull(this.tracker, "Route tracker");
        Asserts.check(this.tracker.isConnected(), "Connection not open");
        this.connection.update((Socket) null, next, secure, params);
        this.tracker.tunnelProxy(next, secure);
    }

    public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        Args.notNull(params, "HTTP parameters");
        Asserts.notNull(this.tracker, "Route tracker");
        Asserts.check(this.tracker.isConnected(), "Connection not open");
        Asserts.check(this.tracker.isTunnelled(), "Protocol layering without a tunnel not supported");
        Asserts.check(!this.tracker.isLayered(), "Multiple protocol layering not supported");
        this.connOperator.updateSecureConnection(this.connection, this.tracker.getTargetHost(), context, params);
        this.tracker.layerProtocol(this.connection.isSecure());
    }

    /* access modifiers changed from: protected */
    public void shutdownEntry() {
        this.tracker = null;
        this.state = null;
    }
}
