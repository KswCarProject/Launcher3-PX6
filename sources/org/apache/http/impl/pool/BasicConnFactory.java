package org.apache.http.impl.pool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpClientConnectionFactory;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.ConnFactory;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BasicConnFactory implements ConnFactory<HttpHost, HttpClientConnection> {
    private final HttpConnectionFactory<? extends HttpClientConnection> connFactory;
    private final int connectTimeout;
    private final SocketFactory plainfactory;
    private final SocketConfig sconfig;
    private final SSLSocketFactory sslfactory;

    @Deprecated
    public BasicConnFactory(SSLSocketFactory sslfactory2, HttpParams params) {
        Args.notNull(params, "HTTP params");
        this.plainfactory = null;
        this.sslfactory = sslfactory2;
        this.connectTimeout = params.getIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 0);
        this.sconfig = HttpParamConfig.getSocketConfig(params);
        this.connFactory = new DefaultBHttpClientConnectionFactory(HttpParamConfig.getConnectionConfig(params));
    }

    @Deprecated
    public BasicConnFactory(HttpParams params) {
        this((SSLSocketFactory) null, params);
    }

    public BasicConnFactory(SocketFactory plainfactory2, SSLSocketFactory sslfactory2, int connectTimeout2, SocketConfig sconfig2, ConnectionConfig cconfig) {
        this.plainfactory = plainfactory2;
        this.sslfactory = sslfactory2;
        this.connectTimeout = connectTimeout2;
        this.sconfig = sconfig2 == null ? SocketConfig.DEFAULT : sconfig2;
        this.connFactory = new DefaultBHttpClientConnectionFactory(cconfig == null ? ConnectionConfig.DEFAULT : cconfig);
    }

    public BasicConnFactory(int connectTimeout2, SocketConfig sconfig2, ConnectionConfig cconfig) {
        this((SocketFactory) null, (SSLSocketFactory) null, connectTimeout2, sconfig2, cconfig);
    }

    public BasicConnFactory(SocketConfig sconfig2, ConnectionConfig cconfig) {
        this((SocketFactory) null, (SSLSocketFactory) null, 0, sconfig2, cconfig);
    }

    public BasicConnFactory() {
        this((SocketFactory) null, (SSLSocketFactory) null, 0, SocketConfig.DEFAULT, ConnectionConfig.DEFAULT);
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public HttpClientConnection create(Socket socket, HttpParams params) throws IOException {
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(params.getIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8192));
        conn.bind(socket);
        return conn;
    }

    public HttpClientConnection create(HttpHost host) throws IOException {
        String scheme = host.getSchemeName();
        Socket socket = null;
        if (HttpHost.DEFAULT_SCHEME_NAME.equalsIgnoreCase(scheme)) {
            socket = this.plainfactory != null ? this.plainfactory.createSocket() : new Socket();
        }
        if ("https".equalsIgnoreCase(scheme)) {
            socket = (this.sslfactory != null ? this.sslfactory : SSLSocketFactory.getDefault()).createSocket();
        }
        if (socket == null) {
            throw new IOException(scheme + " scheme is not supported");
        }
        String hostname = host.getHostName();
        int port = host.getPort();
        if (port == -1) {
            if (host.getSchemeName().equalsIgnoreCase(HttpHost.DEFAULT_SCHEME_NAME)) {
                port = 80;
            } else if (host.getSchemeName().equalsIgnoreCase("https")) {
                port = 443;
            }
        }
        socket.setSoTimeout(this.sconfig.getSoTimeout());
        if (this.sconfig.getSndBufSize() > 0) {
            socket.setSendBufferSize(this.sconfig.getSndBufSize());
        }
        if (this.sconfig.getRcvBufSize() > 0) {
            socket.setReceiveBufferSize(this.sconfig.getRcvBufSize());
        }
        socket.setTcpNoDelay(this.sconfig.isTcpNoDelay());
        int linger = this.sconfig.getSoLinger();
        if (linger >= 0) {
            socket.setSoLinger(true, linger);
        }
        socket.setKeepAlive(this.sconfig.isSoKeepAlive());
        socket.connect(new InetSocketAddress(hostname, port), this.connectTimeout);
        return (HttpClientConnection) this.connFactory.createConnection(socket);
    }
}
