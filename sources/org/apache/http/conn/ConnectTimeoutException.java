package org.apache.http.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.util.Arrays;
import org.apache.http.HttpHost;

public class ConnectTimeoutException extends InterruptedIOException {
    private static final long serialVersionUID = -4816682903149535989L;
    private final HttpHost host;

    public ConnectTimeoutException() {
        this.host = null;
    }

    public ConnectTimeoutException(String message) {
        super(message);
        this.host = null;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ConnectTimeoutException(IOException cause, HttpHost host2, InetAddress... remoteAddresses) {
        super("Connect to " + (host2 != null ? host2.toHostString() : "remote host") + ((remoteAddresses == null || remoteAddresses.length <= 0) ? "" : " " + Arrays.asList(remoteAddresses)) + ((cause == null || cause.getMessage() == null) ? " timed out" : " failed: " + cause.getMessage()));
        this.host = host2;
        initCause(cause);
    }

    public HttpHost getHost() {
        return this.host;
    }
}
