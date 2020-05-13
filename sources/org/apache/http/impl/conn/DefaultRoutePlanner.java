package org.apache.http.impl.conn;

import java.net.InetAddress;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultRoutePlanner implements HttpRoutePlanner {
    private final SchemePortResolver schemePortResolver;

    public DefaultRoutePlanner(SchemePortResolver schemePortResolver2) {
        this.schemePortResolver = schemePortResolver2 == null ? DefaultSchemePortResolver.INSTANCE : schemePortResolver2;
    }

    public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
        HttpHost target;
        Args.notNull(request, "Request");
        if (host == null) {
            throw new ProtocolException("Target host is not specified");
        }
        RequestConfig config = HttpClientContext.adapt(context).getRequestConfig();
        InetAddress local = config.getLocalAddress();
        HttpHost proxy = config.getProxy();
        if (proxy == null) {
            proxy = determineProxy(host, request, context);
        }
        if (host.getPort() <= 0) {
            try {
                target = new HttpHost(host.getHostName(), this.schemePortResolver.resolve(host), host.getSchemeName());
            } catch (UnsupportedSchemeException ex) {
                throw new HttpException(ex.getMessage());
            }
        } else {
            target = host;
        }
        boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        if (proxy == null) {
            return new HttpRoute(target, local, secure);
        }
        return new HttpRoute(target, local, proxy, secure);
    }

    /* access modifiers changed from: protected */
    public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        return null;
    }
}
