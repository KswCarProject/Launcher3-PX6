package org.apache.http.impl.client;

import java.net.ProxySelector;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpParams;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Deprecated
public class SystemDefaultHttpClient extends DefaultHttpClient {
    public SystemDefaultHttpClient(HttpParams params) {
        super((ClientConnectionManager) null, params);
    }

    public SystemDefaultHttpClient() {
        super((ClientConnectionManager) null, (HttpParams) null);
    }

    /* access modifiers changed from: protected */
    public ClientConnectionManager createClientConnectionManager() {
        PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
        if ("true".equalsIgnoreCase(System.getProperty("http.keepAlive", "true"))) {
            int max = Integer.parseInt(System.getProperty("http.maxConnections", "5"));
            connmgr.setDefaultMaxPerRoute(max);
            connmgr.setMaxTotal(max * 2);
        }
        return connmgr;
    }

    /* access modifiers changed from: protected */
    public HttpRoutePlanner createHttpRoutePlanner() {
        return new ProxySelectorRoutePlanner(getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
    }

    /* access modifiers changed from: protected */
    public ConnectionReuseStrategy createConnectionReuseStrategy() {
        if ("true".equalsIgnoreCase(System.getProperty("http.keepAlive", "true"))) {
            return new DefaultConnectionReuseStrategy();
        }
        return new NoConnectionReuseStrategy();
    }
}
