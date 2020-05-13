package org.apache.http.conn.routing;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class HttpRoute implements RouteInfo, Cloneable {
    private final RouteInfo.LayerType layered;
    private final InetAddress localAddress;
    private final List<HttpHost> proxyChain;
    private final boolean secure;
    private final HttpHost targetHost;
    private final RouteInfo.TunnelType tunnelled;

    private HttpRoute(HttpHost target, InetAddress local, List<HttpHost> proxies, boolean secure2, RouteInfo.TunnelType tunnelled2, RouteInfo.LayerType layered2) {
        Args.notNull(target, "Target host");
        this.targetHost = normalize(target);
        this.localAddress = local;
        if (proxies == null || proxies.isEmpty()) {
            this.proxyChain = null;
        } else {
            this.proxyChain = new ArrayList(proxies);
        }
        if (tunnelled2 == RouteInfo.TunnelType.TUNNELLED) {
            Args.check(this.proxyChain != null, "Proxy required if tunnelled");
        }
        this.secure = secure2;
        this.tunnelled = tunnelled2 == null ? RouteInfo.TunnelType.PLAIN : tunnelled2;
        this.layered = layered2 == null ? RouteInfo.LayerType.PLAIN : layered2;
    }

    private static int getDefaultPort(String schemeName) {
        if (HttpHost.DEFAULT_SCHEME_NAME.equalsIgnoreCase(schemeName)) {
            return 80;
        }
        if ("https".equalsIgnoreCase(schemeName)) {
            return 443;
        }
        return -1;
    }

    private static HttpHost normalize(HttpHost target) {
        if (target.getPort() >= 0) {
            return target;
        }
        InetAddress address = target.getAddress();
        String schemeName = target.getSchemeName();
        if (address != null) {
            return new HttpHost(address, getDefaultPort(schemeName), schemeName);
        }
        return new HttpHost(target.getHostName(), getDefaultPort(schemeName), schemeName);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure2, RouteInfo.TunnelType tunnelled2, RouteInfo.LayerType layered2) {
        this(target, local, (List<HttpHost>) proxies != null ? Arrays.asList(proxies) : null, secure2, tunnelled2, layered2);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure2, RouteInfo.TunnelType tunnelled2, RouteInfo.LayerType layered2) {
        this(target, local, (List<HttpHost>) proxy != null ? Collections.singletonList(proxy) : null, secure2, tunnelled2, layered2);
    }

    public HttpRoute(HttpHost target, InetAddress local, boolean secure2) {
        this(target, local, (List<HttpHost>) Collections.emptyList(), secure2, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
    }

    public HttpRoute(HttpHost target) {
        this(target, (InetAddress) null, (List<HttpHost>) Collections.emptyList(), false, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure2) {
        this(target, local, (List<HttpHost>) Collections.singletonList(Args.notNull(proxy, "Proxy host")), secure2, secure2 ? RouteInfo.TunnelType.TUNNELLED : RouteInfo.TunnelType.PLAIN, secure2 ? RouteInfo.LayerType.LAYERED : RouteInfo.LayerType.PLAIN);
    }

    public HttpRoute(HttpHost target, HttpHost proxy) {
        this(target, (InetAddress) null, proxy, false);
    }

    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    public final InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public final InetSocketAddress getLocalSocketAddress() {
        if (this.localAddress != null) {
            return new InetSocketAddress(this.localAddress, 0);
        }
        return null;
    }

    public final int getHopCount() {
        if (this.proxyChain != null) {
            return this.proxyChain.size() + 1;
        }
        return 1;
    }

    public final HttpHost getHopTarget(int hop) {
        Args.notNegative(hop, "Hop index");
        int hopcount = getHopCount();
        Args.check(hop < hopcount, "Hop index exceeds tracked route length");
        if (hop < hopcount - 1) {
            return this.proxyChain.get(hop);
        }
        return this.targetHost;
    }

    public final HttpHost getProxyHost() {
        if (this.proxyChain == null || this.proxyChain.isEmpty()) {
            return null;
        }
        return this.proxyChain.get(0);
    }

    public final RouteInfo.TunnelType getTunnelType() {
        return this.tunnelled;
    }

    public final boolean isTunnelled() {
        return this.tunnelled == RouteInfo.TunnelType.TUNNELLED;
    }

    public final RouteInfo.LayerType getLayerType() {
        return this.layered;
    }

    public final boolean isLayered() {
        return this.layered == RouteInfo.LayerType.LAYERED;
    }

    public final boolean isSecure() {
        return this.secure;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HttpRoute)) {
            return false;
        }
        HttpRoute that = (HttpRoute) obj;
        if (this.secure != that.secure || this.tunnelled != that.tunnelled || this.layered != that.layered || !LangUtils.equals((Object) this.targetHost, (Object) that.targetHost) || !LangUtils.equals((Object) this.localAddress, (Object) that.localAddress) || !LangUtils.equals((Object) this.proxyChain, (Object) that.proxyChain)) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int hash = LangUtils.hashCode(LangUtils.hashCode(17, (Object) this.targetHost), (Object) this.localAddress);
        if (this.proxyChain != null) {
            for (HttpHost element : this.proxyChain) {
                hash = LangUtils.hashCode(hash, (Object) element);
            }
        }
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(hash, this.secure), (Object) this.tunnelled), (Object) this.layered);
    }

    public final String toString() {
        StringBuilder cab = new StringBuilder((getHopCount() * 30) + 50);
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.tunnelled == RouteInfo.TunnelType.TUNNELLED) {
            cab.append('t');
        }
        if (this.layered == RouteInfo.LayerType.LAYERED) {
            cab.append('l');
        }
        if (this.secure) {
            cab.append('s');
        }
        cab.append("}->");
        if (this.proxyChain != null) {
            for (HttpHost aProxyChain : this.proxyChain) {
                cab.append(aProxyChain);
                cab.append("->");
            }
        }
        cab.append(this.targetHost);
        return cab.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
