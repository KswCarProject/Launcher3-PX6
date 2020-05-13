package org.apache.http.impl.conn;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.protocol.HttpContext;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class SystemDefaultRoutePlanner extends DefaultRoutePlanner {
    private final ProxySelector proxySelector;

    public SystemDefaultRoutePlanner(SchemePortResolver schemePortResolver, ProxySelector proxySelector2) {
        super(schemePortResolver);
        this.proxySelector = proxySelector2;
    }

    public SystemDefaultRoutePlanner(ProxySelector proxySelector2) {
        this((SchemePortResolver) null, proxySelector2);
    }

    /* access modifiers changed from: protected */
    public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        try {
            URI targetURI = new URI(target.toURI());
            ProxySelector proxySelectorInstance = this.proxySelector;
            if (proxySelectorInstance == null) {
                proxySelectorInstance = ProxySelector.getDefault();
            }
            if (proxySelectorInstance == null) {
                return null;
            }
            Proxy p = chooseProxy(proxySelectorInstance.select(targetURI));
            if (p.type() != Proxy.Type.HTTP) {
                return null;
            }
            if (!(p.address() instanceof InetSocketAddress)) {
                throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
            }
            InetSocketAddress isa = (InetSocketAddress) p.address();
            return new HttpHost(getHost(isa), isa.getPort());
        } catch (URISyntaxException ex) {
            throw new HttpException("Cannot convert host to URI: " + target, ex);
        }
    }

    private String getHost(InetSocketAddress isa) {
        return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
    }

    private Proxy chooseProxy(List<Proxy> proxies) {
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

    /* renamed from: org.apache.http.impl.conn.SystemDefaultRoutePlanner$1  reason: invalid class name */
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
