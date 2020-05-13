package org.apache.http.impl.client;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.IgnoreSpecFactory;
import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
import org.apache.http.impl.cookie.RFC2109SpecFactory;
import org.apache.http.impl.cookie.RFC2965SpecFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Deprecated
public abstract class AbstractHttpClient extends CloseableHttpClient {
    private BackoffManager backoffManager;
    private ClientConnectionManager connManager;
    private ConnectionBackoffStrategy connectionBackoffStrategy;
    private CookieStore cookieStore;
    private CredentialsProvider credsProvider;
    private HttpParams defaultParams;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private final Log log = LogFactory.getLog(getClass());
    private BasicHttpProcessor mutableProcessor;
    private ImmutableHttpProcessor protocolProcessor;
    private AuthenticationStrategy proxyAuthStrategy;
    private RedirectStrategy redirectStrategy;
    private HttpRequestExecutor requestExec;
    private HttpRequestRetryHandler retryHandler;
    private ConnectionReuseStrategy reuseStrategy;
    private HttpRoutePlanner routePlanner;
    private AuthSchemeRegistry supportedAuthSchemes;
    private CookieSpecRegistry supportedCookieSpecs;
    private AuthenticationStrategy targetAuthStrategy;
    private UserTokenHandler userTokenHandler;

    /* access modifiers changed from: protected */
    public abstract HttpParams createHttpParams();

    /* access modifiers changed from: protected */
    public abstract BasicHttpProcessor createHttpProcessor();

    protected AbstractHttpClient(ClientConnectionManager conman, HttpParams params) {
        this.defaultParams = params;
        this.connManager = conman;
    }

    /* access modifiers changed from: protected */
    public HttpContext createHttpContext() {
        HttpContext context = new BasicHttpContext();
        context.setAttribute(ClientContext.SCHEME_REGISTRY, getConnectionManager().getSchemeRegistry());
        context.setAttribute("http.authscheme-registry", getAuthSchemes());
        context.setAttribute("http.cookiespec-registry", getCookieSpecs());
        context.setAttribute("http.cookie-store", getCookieStore());
        context.setAttribute("http.auth.credentials-provider", getCredentialsProvider());
        return context;
    }

    /* access modifiers changed from: protected */
    public ClientConnectionManager createClientConnectionManager() {
        Class<?> clazz;
        SchemeRegistry registry = SchemeRegistryFactory.createDefault();
        HttpParams params = getParams();
        ClientConnectionManagerFactory factory = null;
        String className = (String) params.getParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME);
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (className != null) {
            if (contextLoader != null) {
                try {
                    clazz = Class.forName(className, true, contextLoader);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Invalid class name: " + className);
                } catch (IllegalAccessException ex) {
                    throw new IllegalAccessError(ex.getMessage());
                } catch (InstantiationException ex2) {
                    throw new InstantiationError(ex2.getMessage());
                }
            } else {
                clazz = Class.forName(className);
            }
            factory = (ClientConnectionManagerFactory) clazz.newInstance();
        }
        if (factory != null) {
            return factory.newInstance(params, registry);
        }
        return new BasicClientConnectionManager(registry);
    }

    /* access modifiers changed from: protected */
    public AuthSchemeRegistry createAuthSchemeRegistry() {
        AuthSchemeRegistry registry = new AuthSchemeRegistry();
        registry.register("Basic", new BasicSchemeFactory());
        registry.register("Digest", new DigestSchemeFactory());
        registry.register("NTLM", new NTLMSchemeFactory());
        registry.register("Negotiate", new SPNegoSchemeFactory());
        registry.register("Kerberos", new KerberosSchemeFactory());
        return registry;
    }

    /* access modifiers changed from: protected */
    public CookieSpecRegistry createCookieSpecRegistry() {
        CookieSpecRegistry registry = new CookieSpecRegistry();
        registry.register(CookieSpecs.DEFAULT, new BestMatchSpecFactory());
        registry.register("best-match", new BestMatchSpecFactory());
        registry.register("compatibility", new BrowserCompatSpecFactory());
        registry.register("netscape", new NetscapeDraftSpecFactory());
        registry.register(CookiePolicy.RFC_2109, new RFC2109SpecFactory());
        registry.register(CookiePolicy.RFC_2965, new RFC2965SpecFactory());
        registry.register("ignoreCookies", new IgnoreSpecFactory());
        return registry;
    }

    /* access modifiers changed from: protected */
    public HttpRequestExecutor createRequestExecutor() {
        return new HttpRequestExecutor();
    }

    /* access modifiers changed from: protected */
    public ConnectionReuseStrategy createConnectionReuseStrategy() {
        return new DefaultConnectionReuseStrategy();
    }

    /* access modifiers changed from: protected */
    public ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
        return new DefaultConnectionKeepAliveStrategy();
    }

    /* access modifiers changed from: protected */
    public HttpRequestRetryHandler createHttpRequestRetryHandler() {
        return new DefaultHttpRequestRetryHandler();
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public RedirectHandler createRedirectHandler() {
        return new DefaultRedirectHandler();
    }

    /* access modifiers changed from: protected */
    public AuthenticationStrategy createTargetAuthenticationStrategy() {
        return new TargetAuthenticationStrategy();
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public AuthenticationHandler createTargetAuthenticationHandler() {
        return new DefaultTargetAuthenticationHandler();
    }

    /* access modifiers changed from: protected */
    public AuthenticationStrategy createProxyAuthenticationStrategy() {
        return new ProxyAuthenticationStrategy();
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public AuthenticationHandler createProxyAuthenticationHandler() {
        return new DefaultProxyAuthenticationHandler();
    }

    /* access modifiers changed from: protected */
    public CookieStore createCookieStore() {
        return new BasicCookieStore();
    }

    /* access modifiers changed from: protected */
    public CredentialsProvider createCredentialsProvider() {
        return new BasicCredentialsProvider();
    }

    /* access modifiers changed from: protected */
    public HttpRoutePlanner createHttpRoutePlanner() {
        return new DefaultHttpRoutePlanner(getConnectionManager().getSchemeRegistry());
    }

    /* access modifiers changed from: protected */
    public UserTokenHandler createUserTokenHandler() {
        return new DefaultUserTokenHandler();
    }

    public final synchronized HttpParams getParams() {
        if (this.defaultParams == null) {
            this.defaultParams = createHttpParams();
        }
        return this.defaultParams;
    }

    public synchronized void setParams(HttpParams params) {
        this.defaultParams = params;
    }

    public final synchronized ClientConnectionManager getConnectionManager() {
        if (this.connManager == null) {
            this.connManager = createClientConnectionManager();
        }
        return this.connManager;
    }

    public final synchronized HttpRequestExecutor getRequestExecutor() {
        if (this.requestExec == null) {
            this.requestExec = createRequestExecutor();
        }
        return this.requestExec;
    }

    public final synchronized AuthSchemeRegistry getAuthSchemes() {
        if (this.supportedAuthSchemes == null) {
            this.supportedAuthSchemes = createAuthSchemeRegistry();
        }
        return this.supportedAuthSchemes;
    }

    public synchronized void setAuthSchemes(AuthSchemeRegistry registry) {
        this.supportedAuthSchemes = registry;
    }

    public final synchronized ConnectionBackoffStrategy getConnectionBackoffStrategy() {
        return this.connectionBackoffStrategy;
    }

    public synchronized void setConnectionBackoffStrategy(ConnectionBackoffStrategy strategy) {
        this.connectionBackoffStrategy = strategy;
    }

    public final synchronized CookieSpecRegistry getCookieSpecs() {
        if (this.supportedCookieSpecs == null) {
            this.supportedCookieSpecs = createCookieSpecRegistry();
        }
        return this.supportedCookieSpecs;
    }

    public final synchronized BackoffManager getBackoffManager() {
        return this.backoffManager;
    }

    public synchronized void setBackoffManager(BackoffManager manager) {
        this.backoffManager = manager;
    }

    public synchronized void setCookieSpecs(CookieSpecRegistry registry) {
        this.supportedCookieSpecs = registry;
    }

    public final synchronized ConnectionReuseStrategy getConnectionReuseStrategy() {
        if (this.reuseStrategy == null) {
            this.reuseStrategy = createConnectionReuseStrategy();
        }
        return this.reuseStrategy;
    }

    public synchronized void setReuseStrategy(ConnectionReuseStrategy strategy) {
        this.reuseStrategy = strategy;
    }

    public final synchronized ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        if (this.keepAliveStrategy == null) {
            this.keepAliveStrategy = createConnectionKeepAliveStrategy();
        }
        return this.keepAliveStrategy;
    }

    public synchronized void setKeepAliveStrategy(ConnectionKeepAliveStrategy strategy) {
        this.keepAliveStrategy = strategy;
    }

    public final synchronized HttpRequestRetryHandler getHttpRequestRetryHandler() {
        if (this.retryHandler == null) {
            this.retryHandler = createHttpRequestRetryHandler();
        }
        return this.retryHandler;
    }

    public synchronized void setHttpRequestRetryHandler(HttpRequestRetryHandler handler) {
        this.retryHandler = handler;
    }

    @Deprecated
    public final synchronized RedirectHandler getRedirectHandler() {
        return createRedirectHandler();
    }

    @Deprecated
    public synchronized void setRedirectHandler(RedirectHandler handler) {
        this.redirectStrategy = new DefaultRedirectStrategyAdaptor(handler);
    }

    public final synchronized RedirectStrategy getRedirectStrategy() {
        if (this.redirectStrategy == null) {
            this.redirectStrategy = new DefaultRedirectStrategy();
        }
        return this.redirectStrategy;
    }

    public synchronized void setRedirectStrategy(RedirectStrategy strategy) {
        this.redirectStrategy = strategy;
    }

    @Deprecated
    public final synchronized AuthenticationHandler getTargetAuthenticationHandler() {
        return createTargetAuthenticationHandler();
    }

    @Deprecated
    public synchronized void setTargetAuthenticationHandler(AuthenticationHandler handler) {
        this.targetAuthStrategy = new AuthenticationStrategyAdaptor(handler);
    }

    public final synchronized AuthenticationStrategy getTargetAuthenticationStrategy() {
        if (this.targetAuthStrategy == null) {
            this.targetAuthStrategy = createTargetAuthenticationStrategy();
        }
        return this.targetAuthStrategy;
    }

    public synchronized void setTargetAuthenticationStrategy(AuthenticationStrategy strategy) {
        this.targetAuthStrategy = strategy;
    }

    @Deprecated
    public final synchronized AuthenticationHandler getProxyAuthenticationHandler() {
        return createProxyAuthenticationHandler();
    }

    @Deprecated
    public synchronized void setProxyAuthenticationHandler(AuthenticationHandler handler) {
        this.proxyAuthStrategy = new AuthenticationStrategyAdaptor(handler);
    }

    public final synchronized AuthenticationStrategy getProxyAuthenticationStrategy() {
        if (this.proxyAuthStrategy == null) {
            this.proxyAuthStrategy = createProxyAuthenticationStrategy();
        }
        return this.proxyAuthStrategy;
    }

    public synchronized void setProxyAuthenticationStrategy(AuthenticationStrategy strategy) {
        this.proxyAuthStrategy = strategy;
    }

    public final synchronized CookieStore getCookieStore() {
        if (this.cookieStore == null) {
            this.cookieStore = createCookieStore();
        }
        return this.cookieStore;
    }

    public synchronized void setCookieStore(CookieStore cookieStore2) {
        this.cookieStore = cookieStore2;
    }

    public final synchronized CredentialsProvider getCredentialsProvider() {
        if (this.credsProvider == null) {
            this.credsProvider = createCredentialsProvider();
        }
        return this.credsProvider;
    }

    public synchronized void setCredentialsProvider(CredentialsProvider credsProvider2) {
        this.credsProvider = credsProvider2;
    }

    public final synchronized HttpRoutePlanner getRoutePlanner() {
        if (this.routePlanner == null) {
            this.routePlanner = createHttpRoutePlanner();
        }
        return this.routePlanner;
    }

    public synchronized void setRoutePlanner(HttpRoutePlanner routePlanner2) {
        this.routePlanner = routePlanner2;
    }

    public final synchronized UserTokenHandler getUserTokenHandler() {
        if (this.userTokenHandler == null) {
            this.userTokenHandler = createUserTokenHandler();
        }
        return this.userTokenHandler;
    }

    public synchronized void setUserTokenHandler(UserTokenHandler handler) {
        this.userTokenHandler = handler;
    }

    /* access modifiers changed from: protected */
    public final synchronized BasicHttpProcessor getHttpProcessor() {
        if (this.mutableProcessor == null) {
            this.mutableProcessor = createHttpProcessor();
        }
        return this.mutableProcessor;
    }

    private synchronized HttpProcessor getProtocolProcessor() {
        if (this.protocolProcessor == null) {
            BasicHttpProcessor proc = getHttpProcessor();
            int reqc = proc.getRequestInterceptorCount();
            HttpRequestInterceptor[] reqinterceptors = new HttpRequestInterceptor[reqc];
            for (int i = 0; i < reqc; i++) {
                reqinterceptors[i] = proc.getRequestInterceptor(i);
            }
            int resc = proc.getResponseInterceptorCount();
            HttpResponseInterceptor[] resinterceptors = new HttpResponseInterceptor[resc];
            for (int i2 = 0; i2 < resc; i2++) {
                resinterceptors[i2] = proc.getResponseInterceptor(i2);
            }
            this.protocolProcessor = new ImmutableHttpProcessor(reqinterceptors, resinterceptors);
        }
        return this.protocolProcessor;
    }

    public synchronized int getResponseInterceptorCount() {
        return getHttpProcessor().getResponseInterceptorCount();
    }

    public synchronized HttpResponseInterceptor getResponseInterceptor(int index) {
        return getHttpProcessor().getResponseInterceptor(index);
    }

    public synchronized HttpRequestInterceptor getRequestInterceptor(int index) {
        return getHttpProcessor().getRequestInterceptor(index);
    }

    public synchronized int getRequestInterceptorCount() {
        return getHttpProcessor().getRequestInterceptorCount();
    }

    public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp) {
        getHttpProcessor().addInterceptor(itcp);
        this.protocolProcessor = null;
    }

    public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp, int index) {
        getHttpProcessor().addInterceptor(itcp, index);
        this.protocolProcessor = null;
    }

    public synchronized void clearResponseInterceptors() {
        getHttpProcessor().clearResponseInterceptors();
        this.protocolProcessor = null;
    }

    public synchronized void removeResponseInterceptorByClass(Class<? extends HttpResponseInterceptor> clazz) {
        getHttpProcessor().removeResponseInterceptorByClass(clazz);
        this.protocolProcessor = null;
    }

    public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp) {
        getHttpProcessor().addInterceptor(itcp);
        this.protocolProcessor = null;
    }

    public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp, int index) {
        getHttpProcessor().addInterceptor(itcp, index);
        this.protocolProcessor = null;
    }

    public synchronized void clearRequestInterceptors() {
        getHttpProcessor().clearRequestInterceptors();
        this.protocolProcessor = null;
    }

    public synchronized void removeRequestInterceptorByClass(Class<? extends HttpRequestInterceptor> clazz) {
        getHttpProcessor().removeRequestInterceptorByClass(clazz);
        this.protocolProcessor = null;
    }

    /* access modifiers changed from: protected */
    public final CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        HttpContext execContext;
        RequestDirector director;
        HttpRoutePlanner routePlanner2;
        ConnectionBackoffStrategy connectionBackoffStrategy2;
        BackoffManager backoffManager2;
        HttpRoute route;
        Args.notNull(request, "HTTP request");
        synchronized (this) {
            HttpContext defaultContext = createHttpContext();
            if (context == null) {
                execContext = defaultContext;
            } else {
                execContext = new DefaultedHttpContext(context, defaultContext);
            }
            HttpParams params = determineParams(request);
            execContext.setAttribute("http.request-config", HttpClientParamConfig.getRequestConfig(params));
            director = createClientRequestDirector(getRequestExecutor(), getConnectionManager(), getConnectionReuseStrategy(), getConnectionKeepAliveStrategy(), getRoutePlanner(), getProtocolProcessor(), getHttpRequestRetryHandler(), getRedirectStrategy(), getTargetAuthenticationStrategy(), getProxyAuthenticationStrategy(), getUserTokenHandler(), params);
            routePlanner2 = getRoutePlanner();
            connectionBackoffStrategy2 = getConnectionBackoffStrategy();
            backoffManager2 = getBackoffManager();
        }
        if (connectionBackoffStrategy2 == null || backoffManager2 == null) {
            return CloseableHttpResponseProxy.newProxy(director.execute(target, request, execContext));
        }
        try {
            route = routePlanner2.determineRoute(target != null ? target : (HttpHost) determineParams(request).getParameter(ClientPNames.DEFAULT_HOST), request, execContext);
            CloseableHttpResponse out = CloseableHttpResponseProxy.newProxy(director.execute(target, request, execContext));
            if (connectionBackoffStrategy2.shouldBackoff((HttpResponse) out)) {
                backoffManager2.backOff(route);
                return out;
            }
            backoffManager2.probe(route);
            return out;
        } catch (RuntimeException re) {
            if (connectionBackoffStrategy2.shouldBackoff((Throwable) re)) {
                backoffManager2.backOff(route);
            }
            throw re;
        } catch (Exception e) {
            if (connectionBackoffStrategy2.shouldBackoff((Throwable) e)) {
                backoffManager2.backOff(route);
            }
            if (e instanceof HttpException) {
                throw ((HttpException) e);
            } else if (e instanceof IOException) {
                throw ((IOException) e);
            } else {
                throw new UndeclaredThrowableException(e);
            }
        } catch (HttpException httpException) {
            throw new ClientProtocolException((Throwable) httpException);
        }
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec2, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler2, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler2, HttpParams params) {
        return new DefaultRequestDirector(requestExec2, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler2, redirectHandler, targetAuthHandler, proxyAuthHandler, userTokenHandler2, params);
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec2, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler2, RedirectStrategy redirectStrategy2, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler2, HttpParams params) {
        return new DefaultRequestDirector(this.log, requestExec2, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler2, redirectStrategy2, targetAuthHandler, proxyAuthHandler, userTokenHandler2, params);
    }

    /* access modifiers changed from: protected */
    public RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec2, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler2, RedirectStrategy redirectStrategy2, AuthenticationStrategy targetAuthStrategy2, AuthenticationStrategy proxyAuthStrategy2, UserTokenHandler userTokenHandler2, HttpParams params) {
        return new DefaultRequestDirector(this.log, requestExec2, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler2, redirectStrategy2, targetAuthStrategy2, proxyAuthStrategy2, userTokenHandler2, params);
    }

    /* access modifiers changed from: protected */
    public HttpParams determineParams(HttpRequest req) {
        return new ClientParamsStack((HttpParams) null, getParams(), req.getParams(), (HttpParams) null);
    }

    public void close() {
        getConnectionManager().shutdown();
    }
}
