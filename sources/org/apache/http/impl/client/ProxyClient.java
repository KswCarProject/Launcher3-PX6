package org.apache.http.impl.client;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.HttpAuthenticator;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.execchain.TunnelRefusedException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

public class ProxyClient {
    private final AuthSchemeRegistry authSchemeRegistry;
    private final HttpAuthenticator authenticator;
    private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
    private final ConnectionConfig connectionConfig;
    private final HttpProcessor httpProcessor;
    private final AuthState proxyAuthState;
    private final ProxyAuthenticationStrategy proxyAuthStrategy;
    private final RequestConfig requestConfig;
    private final HttpRequestExecutor requestExec;
    private final ConnectionReuseStrategy reuseStrategy;

    public ProxyClient(HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory2, ConnectionConfig connectionConfig2, RequestConfig requestConfig2) {
        this.connFactory = connFactory2 == null ? ManagedHttpClientConnectionFactory.INSTANCE : connFactory2;
        this.connectionConfig = connectionConfig2 == null ? ConnectionConfig.DEFAULT : connectionConfig2;
        this.requestConfig = requestConfig2 == null ? RequestConfig.DEFAULT : requestConfig2;
        this.httpProcessor = new ImmutableHttpProcessor(new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent());
        this.requestExec = new HttpRequestExecutor();
        this.proxyAuthStrategy = new ProxyAuthenticationStrategy();
        this.authenticator = new HttpAuthenticator();
        this.proxyAuthState = new AuthState();
        this.authSchemeRegistry = new AuthSchemeRegistry();
        this.authSchemeRegistry.register("Basic", new BasicSchemeFactory());
        this.authSchemeRegistry.register("Digest", new DigestSchemeFactory());
        this.authSchemeRegistry.register("NTLM", new NTLMSchemeFactory());
        this.authSchemeRegistry.register("Negotiate", new SPNegoSchemeFactory());
        this.authSchemeRegistry.register("Kerberos", new KerberosSchemeFactory());
        this.reuseStrategy = new DefaultConnectionReuseStrategy();
    }

    @Deprecated
    public ProxyClient(HttpParams params) {
        this((HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>) null, HttpParamConfig.getConnectionConfig(params), HttpClientParamConfig.getRequestConfig(params));
    }

    public ProxyClient(RequestConfig requestConfig2) {
        this((HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>) null, (ConnectionConfig) null, requestConfig2);
    }

    public ProxyClient() {
        this((HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>) null, (ConnectionConfig) null, (RequestConfig) null);
    }

    @Deprecated
    public HttpParams getParams() {
        return new BasicHttpParams();
    }

    @Deprecated
    public AuthSchemeRegistry getAuthSchemeRegistry() {
        return this.authSchemeRegistry;
    }

    public Socket tunnel(HttpHost proxy, HttpHost target, Credentials credentials) throws IOException, HttpException {
        HttpResponse response;
        Args.notNull(proxy, "Proxy host");
        Args.notNull(target, "Target host");
        Args.notNull(credentials, "Credentials");
        HttpHost host = target;
        if (host.getPort() <= 0) {
            host = new HttpHost(host.getHostName(), 80, host.getSchemeName());
        }
        HttpRoute route = new HttpRoute(host, this.requestConfig.getLocalAddress(), proxy, false, RouteInfo.TunnelType.TUNNELLED, RouteInfo.LayerType.PLAIN);
        ManagedHttpClientConnection conn = this.connFactory.create(route, this.connectionConfig);
        HttpContext context = new BasicHttpContext();
        HttpRequest connect = new BasicHttpRequest("CONNECT", host.toHostString(), HttpVersion.HTTP_1_1);
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxy), credentials);
        context.setAttribute("http.target_host", target);
        context.setAttribute("http.connection", conn);
        context.setAttribute("http.request", connect);
        context.setAttribute("http.route", route);
        context.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
        context.setAttribute("http.auth.credentials-provider", credsProvider);
        context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
        context.setAttribute("http.request-config", this.requestConfig);
        this.requestExec.preProcess(connect, this.httpProcessor, context);
        while (true) {
            if (!conn.isOpen()) {
                conn.bind(new Socket(proxy.getHostName(), proxy.getPort()));
            }
            this.authenticator.generateAuthResponse(connect, this.proxyAuthState, context);
            response = this.requestExec.execute(connect, conn, context);
            if (response.getStatusLine().getStatusCode() >= 200) {
                if (!this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                    break;
                }
                if (!this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                    break;
                }
                if (this.reuseStrategy.keepAlive(response, context)) {
                    EntityUtils.consume(response.getEntity());
                } else {
                    conn.close();
                }
                connect.removeHeaders("Proxy-Authorization");
            } else {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
        }
        if (response.getStatusLine().getStatusCode() <= 299) {
            return conn.getSocket();
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            response.setEntity(new BufferedHttpEntity(entity));
        }
        conn.close();
        throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
    }
}
