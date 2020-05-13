package org.apache.http.conn;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Arrays;
import org.apache.http.HttpHost;

public class HttpHostConnectException extends ConnectException {
    private static final long serialVersionUID = -3194482710275220224L;
    private final HttpHost host;

    @Deprecated
    public HttpHostConnectException(HttpHost host2, ConnectException cause) {
        this(cause, host2, (InetAddress[]) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HttpHostConnectException(IOException cause, HttpHost host2, InetAddress... remoteAddresses) {
        super("Connect to " + (host2 != null ? host2.toHostString() : "remote host") + ((remoteAddresses == null || remoteAddresses.length <= 0) ? "" : " " + Arrays.asList(remoteAddresses)) + ((cause == null || cause.getMessage() == null) ? " refused" : " failed: " + cause.getMessage()));
        this.host = host2;
        initCause(cause);
    }

    public HttpHost getHost() {
        return this.host;
    }
}
