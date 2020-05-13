package org.apache.http.conn;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.http.HttpHost;
import org.apache.http.util.Args;

@Deprecated
public class HttpInetSocketAddress extends InetSocketAddress {
    private static final long serialVersionUID = -6650701828361907957L;
    private final HttpHost httphost;

    public HttpInetSocketAddress(HttpHost httphost2, InetAddress addr, int port) {
        super(addr, port);
        Args.notNull(httphost2, "HTTP host");
        this.httphost = httphost2;
    }

    public HttpHost getHttpHost() {
        return this.httphost;
    }

    public String toString() {
        return this.httphost.getHostName() + ":" + getPort();
    }
}
