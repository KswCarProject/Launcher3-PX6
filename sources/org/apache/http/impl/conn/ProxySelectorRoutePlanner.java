package org.apache.http.impl.conn;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public class ProxySelectorRoutePlanner implements HttpRoutePlanner {
    protected ProxySelector proxySelector;
    protected final SchemeRegistry schemeRegistry;

    public ProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel) {
        Args.notNull(schreg, "SchemeRegistry");
        this.schemeRegistry = schreg;
        this.proxySelector = prosel;
    }

    public ProxySelector getProxySelector() {
        return this.proxySelector;
    }

    public void setProxySelector(ProxySelector prosel) {
        this.proxySelector = prosel;
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
        HttpHost proxy = determineProxy(target, request, context);
        boolean secure = this.schemeRegistry.getScheme(target.getSchemeName()).isLayered();
        if (proxy == null) {
            route = new HttpRoute(target, local, secure);
        } else {
            route = new HttpRoute(target, local, proxy, secure);
        }
        return route;
    }

    /* access modifiers changed from: protected */
    public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        ProxySelector psel = this.proxySelector;
        if (psel == null) {
            psel = ProxySelector.getDefault();
        }
        if (psel == null) {
            return null;
        }
        try {
            Proxy p = chooseProxy(psel.select(new URI(target.toURI())), target, request, context);
            if (p.type() != Proxy.Type.HTTP) {
                return null;
            }
            if (!(p.address() instanceof InetSocketAddress)) {
                throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
            }
            InetSocketAddress isa = (InetSocketAddress) p.address();
            return new HttpHost(getHost(isa), isa.getPort());
        } catch (URISyntaxException usx) {
            throw new HttpException("Cannot convert host to URI: " + target, usx);
        }
    }

    /* access modifiers changed from: protected */
    public String getHost(InetSocketAddress isa) {
        return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
    }

    /* access modifiers changed from: protected */
    public Proxy chooseProxy(List<Proxy> proxies, HttpHost target, HttpRequest request, HttpContext context) {
        Args.notEmpty(proxies, "List of proxies");
        Proxy result = null;
        int i = 0;
        while (result == null && i < proxies.size()) {
            Proxy p = proxies.get(i);
            switch (AnonymousClass1.$SwitchMap$java$net$Proxy$Type[p.type().ordinal()]) {
                case 1:
                case 2:
                    result = p;
                    break;
            }
            i++;
        }
        if (result == null) {
            return Proxy.NO_PROXY;
        }
        return result;
    }

    /* renamed from: org.apache.http.impl.conn.ProxySelectorRoutePlanner$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$net$Proxy$Type = new int[Proxy.Type.values().length];

        static {
            try {
                $SwitchMap$java$net$Proxy$Type[Proxy.Type.DIRECT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$net$Proxy$Type[Proxy.Type.HTTP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$net$Proxy$Type[Proxy.Type.SOCKS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
