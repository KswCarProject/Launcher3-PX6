package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthState;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
class InternalHttpClient extends CloseableHttpClient implements Configurable {
    private final Lookup<AuthSchemeProvider> authSchemeRegistry;
    private final List<Closeable> closeables;
    /* access modifiers changed from: private */
    public final HttpClientConnectionManager connManager;
    private final Lookup<CookieSpecProvider> cookieSpecRegistry;
    private final CookieStore cookieStore;
    private final CredentialsProvider credentialsProvider;
    private final RequestConfig defaultConfig;
    private final ClientExecChain execChain;
    private final Log log = LogFactory.getLog(getClass());
    private final HttpRoutePlanner routePlanner;

    public InternalHttpClient(ClientExecChain execChain2, HttpClientConnectionManager connManager2, HttpRoutePlanner routePlanner2, Lookup<CookieSpecProvider> cookieSpecRegistry2, Lookup<AuthSchemeProvider> authSchemeRegistry2, CookieStore cookieStore2, CredentialsProvider credentialsProvider2, RequestConfig defaultConfig2, List<Closeable> closeables2) {
        Args.notNull(execChain2, "HTTP client exec chain");
        Args.notNull(connManager2, "HTTP connection manager");
        Args.notNull(routePlanner2, "HTTP route planner");
        this.execChain = execChain2;
        this.connManager = connManager2;
        this.routePlanner = routePlanner2;
        this.cookieSpecRegistry = cookieSpecRegistry2;
        this.authSchemeRegistry = authSchemeRegistry2;
        this.cookieStore = cookieStore2;
        this.credentialsProvider = credentialsProvider2;
        this.defaultConfig = defaultConfig2;
        this.closeables = closeables2;
    }

    private HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        HttpHost host = target;
        if (host == null) {
            host = (HttpHost) request.getParams().getParameter(ClientPNames.DEFAULT_HOST);
        }
        return this.routePlanner.determineRoute(host, request, context);
    }

    private void setupContext(HttpClientContext context) {
        if (context.getAttribute("http.auth.target-scope") == null) {
            context.setAttribute("http.auth.target-scope", new AuthState());
        }
        if (context.getAttribute("http.auth.proxy-scope") == null) {
            context.setAttribute("http.auth.proxy-scope", new AuthState());
        }
        if (context.getAttribute("http.authscheme-registry") == null) {
            context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
        }
        if (context.getAttribute("http.cookiespec-registry") == null) {
            context.setAttribute("http.cookiespec-registry", this.cookieSpecRegistry);
        }
        if (context.getAttribute("http.cookie-store") == null) {
            context.setAttribute("http.cookie-store", this.cookieStore);
        }
        if (context.getAttribute("http.auth.credentials-provider") == null) {
            context.setAttribute("http.auth.credentials-provider", this.credentialsProvider);
        }
        if (context.getAttribute("http.request-config") == null) {
            context.setAttribute("http.request-config", this.defaultConfig);
        }
    }

    /* access modifiers changed from: protected */
    public CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(request, "HTTP request");
        HttpExecutionAware execAware = null;
        if (request instanceof HttpExecutionAware) {
            execAware = (HttpExecutionAware) request;
        }
        try {
            HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request, target);
            if (context == null) {
                context = new BasicHttpContext();
            }
            HttpClientContext localcontext = HttpClientContext.adapt(context);
            RequestConfig config = null;
            if (request instanceof Configurable) {
                config = ((Configurable) request).getConfig();
            }
            if (config == null) {
                HttpParams params = request.getParams();
                if (!(params instanceof HttpParamsNames)) {
                    config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
                } else if (!((HttpParamsNames) params).getNames().isEmpty()) {
                    config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
                }
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            setupContext(localcontext);
            return this.execChain.execute(determineRoute(target, wrapper, localcontext), wrapper, localcontext, execAware);
        } catch (HttpException httpException) {
            throw new ClientProtocolException((Throwable) httpException);
        }
    }

    public RequestConfig getConfig() {
        return this.defaultConfig;
    }

    public void close() {
        if (this.closeables != null) {
            for (Closeable closeable : this.closeables) {
                try {
                    closeable.close();
                } catch (IOException ex) {
                    this.log.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public HttpParams getParams() {
        throw new UnsupportedOperationException();
    }

    public ClientConnectionManager getConnectionManager() {
        return new ClientConnectionManager() {
            public void shutdown() {
                InternalHttpClient.this.connManager.shutdown();
            }

            public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
                throw new UnsupportedOperationException();
            }

            public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
                throw new UnsupportedOperationException();
            }

            public SchemeRegistry getSchemeRegistry() {
                throw new UnsupportedOperationException();
            }

            public void closeIdleConnections(long idletime, TimeUnit tunit) {
                InternalHttpClient.this.connManager.closeIdleConnections(idletime, tunit);
            }

            public void closeExpiredConnections() {
                InternalHttpClient.this.connManager.closeExpiredConnections();
            }
        };
    }
}
