package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Deprecated
public class DefaultRequestDirector implements RequestDirector {
    private final HttpAuthenticator authenticator;
    protected final ClientConnectionManager connManager;
    private int execCount;
    protected final HttpProcessor httpProcessor;
    protected final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final Log log;
    protected ManagedClientConnection managedConn;
    private final int maxRedirects;
    protected final HttpParams params;
    @Deprecated
    protected final AuthenticationHandler proxyAuthHandler;
    protected final AuthState proxyAuthState;
    protected final AuthenticationStrategy proxyAuthStrategy;
    private int redirectCount;
    @Deprecated
    protected final RedirectHandler redirectHandler;
    protected final RedirectStrategy redirectStrategy;
    protected final HttpRequestExecutor requestExec;
    protected final HttpRequestRetryHandler retryHandler;
    protected final ConnectionReuseStrategy reuseStrategy;
    protected final HttpRoutePlanner routePlanner;
    @Deprecated
    protected final AuthenticationHandler targetAuthHandler;
    protected final AuthState targetAuthState;
    protected final AuthenticationStrategy targetAuthStrategy;
    protected final UserTokenHandler userTokenHandler;
    private HttpHost virtualHost;

    @Deprecated
    public DefaultRequestDirector(HttpRequestExecutor requestExec2, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor2, HttpRequestRetryHandler retryHandler2, RedirectHandler redirectHandler2, AuthenticationHandler targetAuthHandler2, AuthenticationHandler proxyAuthHandler2, UserTokenHandler userTokenHandler2, HttpParams params2) {
        this(LogFactory.getLog(DefaultRequestDirector.class), requestExec2, conman, reustrat, kastrat, rouplan, httpProcessor2, retryHandler2, (RedirectStrategy) new DefaultRedirectStrategyAdaptor(redirectHandler2), (AuthenticationStrategy) new AuthenticationStrategyAdaptor(targetAuthHandler2), (AuthenticationStrategy) new AuthenticationStrategyAdaptor(proxyAuthHandler2), userTokenHandler2, params2);
    }

    @Deprecated
    public DefaultRequestDirector(Log log2, HttpRequestExecutor requestExec2, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor2, HttpRequestRetryHandler retryHandler2, RedirectStrategy redirectStrategy2, AuthenticationHandler targetAuthHandler2, AuthenticationHandler proxyAuthHandler2, UserTokenHandler userTokenHandler2, HttpParams params2) {
        this(LogFactory.getLog(DefaultRequestDirector.class), requestExec2, conman, reustrat, kastrat, rouplan, httpProcessor2, retryHandler2, redirectStrategy2, (AuthenticationStrategy) new AuthenticationStrategyAdaptor(targetAuthHandler2), (AuthenticationStrategy) new AuthenticationStrategyAdaptor(proxyAuthHandler2), userTokenHandler2, params2);
    }

    public DefaultRequestDirector(Log log2, HttpRequestExecutor requestExec2, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor2, HttpRequestRetryHandler retryHandler2, RedirectStrategy redirectStrategy2, AuthenticationStrategy targetAuthStrategy2, AuthenticationStrategy proxyAuthStrategy2, UserTokenHandler userTokenHandler2, HttpParams params2) {
        Args.notNull(log2, "Log");
        Args.notNull(requestExec2, "Request executor");
        Args.notNull(conman, "Client connection manager");
        Args.notNull(reustrat, "Connection reuse strategy");
        Args.notNull(kastrat, "Connection keep alive strategy");
        Args.notNull(rouplan, "Route planner");
        Args.notNull(httpProcessor2, "HTTP protocol processor");
        Args.notNull(retryHandler2, "HTTP request retry handler");
        Args.notNull(redirectStrategy2, "Redirect strategy");
        Args.notNull(targetAuthStrategy2, "Target authentication strategy");
        Args.notNull(proxyAuthStrategy2, "Proxy authentication strategy");
        Args.notNull(userTokenHandler2, "User token handler");
        Args.notNull(params2, "HTTP parameters");
        this.log = log2;
        this.authenticator = new HttpAuthenticator(log2);
        this.requestExec = requestExec2;
        this.connManager = conman;
        this.reuseStrategy = reustrat;
        this.keepAliveStrategy = kastrat;
        this.routePlanner = rouplan;
        this.httpProcessor = httpProcessor2;
        this.retryHandler = retryHandler2;
        this.redirectStrategy = redirectStrategy2;
        this.targetAuthStrategy = targetAuthStrategy2;
        this.proxyAuthStrategy = proxyAuthStrategy2;
        this.userTokenHandler = userTokenHandler2;
        this.params = params2;
        if (redirectStrategy2 instanceof DefaultRedirectStrategyAdaptor) {
            this.redirectHandler = ((DefaultRedirectStrategyAdaptor) redirectStrategy2).getHandler();
        } else {
            this.redirectHandler = null;
        }
        if (targetAuthStrategy2 instanceof AuthenticationStrategyAdaptor) {
            this.targetAuthHandler = ((AuthenticationStrategyAdaptor) targetAuthStrategy2).getHandler();
        } else {
            this.targetAuthHandler = null;
        }
        if (proxyAuthStrategy2 instanceof AuthenticationStrategyAdaptor) {
            this.proxyAuthHandler = ((AuthenticationStrategyAdaptor) proxyAuthStrategy2).getHandler();
        } else {
            this.proxyAuthHandler = null;
        }
        this.managedConn = null;
        this.execCount = 0;
        this.redirectCount = 0;
        this.targetAuthState = new AuthState();
        this.proxyAuthState = new AuthState();
        this.maxRedirects = this.params.getIntParameter(ClientPNames.MAX_REDIRECTS, 100);
    }

    private RequestWrapper wrapRequest(HttpRequest request) throws ProtocolException {
        if (request instanceof HttpEntityEnclosingRequest) {
            return new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest) request);
        }
        return new RequestWrapper(request);
    }

    /* access modifiers changed from: protected */
    public void rewriteRequestURI(RequestWrapper request, HttpRoute route) throws ProtocolException {
        URI uri;
        try {
            URI uri2 = request.getURI();
            if (route.getProxyHost() == null || route.isTunnelled()) {
                if (uri2.isAbsolute()) {
                    uri = URIUtils.rewriteURI(uri2, (HttpHost) null, true);
                } else {
                    uri = URIUtils.rewriteURI(uri2);
                }
            } else if (!uri2.isAbsolute()) {
                uri = URIUtils.rewriteURI(uri2, route.getTargetHost(), true);
            } else {
                uri = URIUtils.rewriteURI(uri2);
            }
            request.setURI(uri);
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), ex);
        }
    }

    public HttpResponse execute(HttpHost targetHost, HttpRequest request, HttpContext context) throws HttpException, IOException {
        String s;
        context.setAttribute("http.auth.target-scope", this.targetAuthState);
        context.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
        HttpHost target = targetHost;
        HttpRequest orig = request;
        RequestWrapper origWrapper = wrapRequest(orig);
        origWrapper.setParams(this.params);
        HttpRoute origRoute = determineRoute(target, origWrapper, context);
        this.virtualHost = (HttpHost) origWrapper.getParams().getParameter(ClientPNames.VIRTUAL_HOST);
        if (this.virtualHost != null && this.virtualHost.getPort() == -1) {
            int port = (target != null ? target : origRoute.getTargetHost()).getPort();
            if (port != -1) {
                this.virtualHost = new HttpHost(this.virtualHost.getHostName(), port, this.virtualHost.getSchemeName());
            }
        }
        RoutedRequest roureq = new RoutedRequest(origWrapper, origRoute);
        boolean reuse = false;
        boolean done = false;
        HttpResponse response = null;
        while (!done) {
            try {
                RequestWrapper wrapper = roureq.getRequest();
                HttpRoute route = roureq.getRoute();
                Object userToken = context.getAttribute("http.user-token");
                if (this.managedConn == null) {
                    ClientConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
                    if (orig instanceof AbortableHttpRequest) {
                        ((AbortableHttpRequest) orig).setConnectionRequest(connRequest);
                    }
                    this.managedConn = connRequest.getConnection(HttpClientParams.getConnectionManagerTimeout(this.params), TimeUnit.MILLISECONDS);
                    if (HttpConnectionParams.isStaleCheckingEnabled(this.params) && this.managedConn.isOpen()) {
                        this.log.debug("Stale connection check");
                        if (this.managedConn.isStale()) {
                            this.log.debug("Stale connection detected");
                            this.managedConn.close();
                        }
                    }
                }
                if (orig instanceof AbortableHttpRequest) {
                    ((AbortableHttpRequest) orig).setReleaseTrigger(this.managedConn);
                }
                try {
                    tryConnect(roureq, context);
                    String userinfo = wrapper.getURI().getUserInfo();
                    if (userinfo != null) {
                        this.targetAuthState.update(new BasicScheme(), new UsernamePasswordCredentials(userinfo));
                    }
                    if (this.virtualHost != null) {
                        target = this.virtualHost;
                    } else {
                        URI requestURI = wrapper.getURI();
                        if (requestURI.isAbsolute()) {
                            target = URIUtils.extractHost(requestURI);
                        }
                    }
                    if (target == null) {
                        target = route.getTargetHost();
                    }
                    wrapper.resetHeaders();
                    rewriteRequestURI(wrapper, route);
                    context.setAttribute("http.target_host", target);
                    context.setAttribute("http.route", route);
                    context.setAttribute("http.connection", this.managedConn);
                    this.requestExec.preProcess(wrapper, this.httpProcessor, context);
                    response = tryExecute(roureq, context);
                    if (response != null) {
                        response.setParams(this.params);
                        this.requestExec.postProcess(response, this.httpProcessor, context);
                        reuse = this.reuseStrategy.keepAlive(response, context);
                        if (reuse) {
                            long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
                            if (this.log.isDebugEnabled()) {
                                if (duration > 0) {
                                    s = "for " + duration + " " + TimeUnit.MILLISECONDS;
                                } else {
                                    s = "indefinitely";
                                }
                                this.log.debug("Connection can be kept alive " + s);
                            }
                            this.managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);
                        }
                        RoutedRequest followup = handleResponse(roureq, response, context);
                        if (followup == null) {
                            done = true;
                        } else {
                            if (reuse) {
                                EntityUtils.consume(response.getEntity());
                                this.managedConn.markReusable();
                            } else {
                                this.managedConn.close();
                                if (this.proxyAuthState.getState().compareTo(AuthProtocolState.CHALLENGED) > 0 && this.proxyAuthState.getAuthScheme() != null && this.proxyAuthState.getAuthScheme().isConnectionBased()) {
                                    this.log.debug("Resetting proxy auth state");
                                    this.proxyAuthState.reset();
                                }
                                if (this.targetAuthState.getState().compareTo(AuthProtocolState.CHALLENGED) > 0 && this.targetAuthState.getAuthScheme() != null && this.targetAuthState.getAuthScheme().isConnectionBased()) {
                                    this.log.debug("Resetting target auth state");
                                    this.targetAuthState.reset();
                                }
                            }
                            if (!followup.getRoute().equals(roureq.getRoute())) {
                                releaseConnection();
                            }
                            roureq = followup;
                        }
                        if (this.managedConn != null) {
                            if (userToken == null) {
                                userToken = this.userTokenHandler.getUserToken(context);
                                context.setAttribute("http.user-token", userToken);
                            }
                            if (userToken != null) {
                                this.managedConn.setState(userToken);
                            }
                        }
                    }
                } catch (TunnelRefusedException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage());
                    }
                    response = ex.getResponse();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            } catch (ConnectionShutdownException ex2) {
                InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
                ioex.initCause(ex2);
                throw ioex;
            } catch (HttpException ex3) {
                abortConnection();
                throw ex3;
            } catch (IOException ex4) {
                abortConnection();
                throw ex4;
            } catch (RuntimeException ex5) {
                abortConnection();
                throw ex5;
            }
        }
        if (response == null || response.getEntity() == null || !response.getEntity().isStreaming()) {
            if (reuse) {
                this.managedConn.markReusable();
            }
            releaseConnection();
        } else {
            response.setEntity(new BasicManagedEntity(response.getEntity(), this.managedConn, reuse));
        }
        return response;
    }

    private void tryConnect(RoutedRequest req, HttpContext context) throws HttpException, IOException {
        HttpRoute route = req.getRoute();
        HttpRequest wrapper = req.getRequest();
        int connectCount = 0;
        while (true) {
            context.setAttribute("http.request", wrapper);
            connectCount++;
            try {
                if (!this.managedConn.isOpen()) {
                    this.managedConn.open(route, context, this.params);
                } else {
                    this.managedConn.setSocketTimeout(HttpConnectionParams.getSoTimeout(this.params));
                }
                establishRoute(route, context);
                return;
            } catch (IOException ex) {
                try {
                    this.managedConn.close();
                } catch (IOException e) {
                }
                if (!this.retryHandler.retryRequest(ex, connectCount, context)) {
                    throw ex;
                } else if (this.log.isInfoEnabled()) {
                    this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when connecting to " + route + ": " + ex.getMessage());
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                    this.log.info("Retrying connect to " + route);
                }
            }
        }
    }

    private HttpResponse tryExecute(RoutedRequest req, HttpContext context) throws HttpException, IOException {
        RequestWrapper wrapper = req.getRequest();
        HttpRoute route = req.getRoute();
        Exception retryReason = null;
        while (true) {
            this.execCount++;
            wrapper.incrementExecCount();
            if (!wrapper.isRepeatable()) {
                this.log.debug("Cannot retry non-repeatable request");
                if (retryReason != null) {
                    throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.  The cause lists the reason the original request failed.", retryReason);
                }
                throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
            }
            try {
                if (!this.managedConn.isOpen()) {
                    if (!route.isTunnelled()) {
                        this.log.debug("Reopening the direct connection.");
                        this.managedConn.open(route, context, this.params);
                    } else {
                        this.log.debug("Proxied connection. Need to start over.");
                        return null;
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Attempt " + this.execCount + " to execute request");
                }
                return this.requestExec.execute(wrapper, this.managedConn, context);
            } catch (IOException ex) {
                this.log.debug("Closing the connection.");
                try {
                    this.managedConn.close();
                } catch (IOException e) {
                }
                if (this.retryHandler.retryRequest(ex, wrapper.getExecCount(), context)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when processing request to " + route + ": " + ex.getMessage());
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                    if (this.log.isInfoEnabled()) {
                        this.log.info("Retrying request to " + route);
                    }
                    retryReason = ex;
                } else if (ex instanceof NoHttpResponseException) {
                    NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
                    updatedex.setStackTrace(ex.getStackTrace());
                    throw updatedex;
                } else {
                    throw ex;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void releaseConnection() {
        try {
            this.managedConn.releaseConnection();
        } catch (IOException ignored) {
            this.log.debug("IOException releasing connection", ignored);
        }
        this.managedConn = null;
    }

    /* access modifiers changed from: protected */
    public HttpRoute determineRoute(HttpHost targetHost, HttpRequest request, HttpContext context) throws HttpException {
        HttpRoutePlanner httpRoutePlanner = this.routePlanner;
        if (targetHost == null) {
            targetHost = (HttpHost) request.getParams().getParameter(ClientPNames.DEFAULT_HOST);
        }
        return httpRoutePlanner.determineRoute(targetHost, request, context);
    }

    /* access modifiers changed from: protected */
    public void establishRoute(HttpRoute route, HttpContext context) throws HttpException, IOException {
        int step;
        HttpRouteDirector rowdy = new BasicRouteDirector();
        do {
            HttpRoute fact = this.managedConn.getRoute();
            step = rowdy.nextStep(route, fact);
            switch (step) {
                case -1:
                    throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                case 0:
                    break;
                case 1:
                case 2:
                    this.managedConn.open(route, context, this.params);
                    continue;
                case 3:
                    boolean secure = createTunnelToTarget(route, context);
                    this.log.debug("Tunnel to target created.");
                    this.managedConn.tunnelTarget(secure, this.params);
                    continue;
                case 4:
                    int hop = fact.getHopCount() - 1;
                    boolean secure2 = createTunnelToProxy(route, hop, context);
                    this.log.debug("Tunnel to proxy created.");
                    this.managedConn.tunnelProxy(route.getHopTarget(hop), secure2, this.params);
                    continue;
                case 5:
                    this.managedConn.layerProtocol(context, this.params);
                    continue;
                default:
                    throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
            }
        } while (step > 0);
    }

    /* access modifiers changed from: protected */
    public boolean createTunnelToTarget(HttpRoute route, HttpContext context) throws HttpException, IOException {
        HttpResponse response;
        HttpHost proxy = route.getProxyHost();
        HttpHost target = route.getTargetHost();
        while (true) {
            if (!this.managedConn.isOpen()) {
                this.managedConn.open(route, context, this.params);
            }
            HttpRequest connect = createConnectRequest(route, context);
            connect.setParams(this.params);
            context.setAttribute("http.target_host", target);
            context.setAttribute("http.route", route);
            context.setAttribute(ExecutionContext.HTTP_PROXY_HOST, proxy);
            context.setAttribute("http.connection", this.managedConn);
            context.setAttribute("http.request", connect);
            this.requestExec.preProcess(connect, this.httpProcessor, context);
            response = this.requestExec.execute(connect, this.managedConn, context);
            response.setParams(this.params);
            this.requestExec.postProcess(response, this.httpProcessor, context);
            if (response.getStatusLine().getStatusCode() < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            } else if (HttpClientParams.isAuthenticating(this.params)) {
                if (this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context) && this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                    if (this.reuseStrategy.keepAlive(response, context)) {
                        this.log.debug("Connection kept alive");
                        EntityUtils.consume(response.getEntity());
                    } else {
                        this.managedConn.close();
                    }
                }
            }
        }
        if (response.getStatusLine().getStatusCode() > 299) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                response.setEntity(new BufferedHttpEntity(entity));
            }
            this.managedConn.close();
            throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
        }
        this.managedConn.markReusable();
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean createTunnelToProxy(HttpRoute route, int hop, HttpContext context) throws HttpException, IOException {
        throw new HttpException("Proxy chains are not supported.");
    }

    /* access modifiers changed from: protected */
    public HttpRequest createConnectRequest(HttpRoute route, HttpContext context) {
        HttpHost target = route.getTargetHost();
        String host = target.getHostName();
        int port = target.getPort();
        if (port < 0) {
            port = this.connManager.getSchemeRegistry().getScheme(target.getSchemeName()).getDefaultPort();
        }
        StringBuilder buffer = new StringBuilder(host.length() + 6);
        buffer.append(host);
        buffer.append(':');
        buffer.append(Integer.toString(port));
        return new BasicHttpRequest("CONNECT", buffer.toString(), HttpProtocolParams.getVersion(this.params));
    }

    /* access modifiers changed from: protected */
    public RoutedRequest handleResponse(RoutedRequest roureq, HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRoute route = roureq.getRoute();
        RequestWrapper request = roureq.getRequest();
        HttpParams params2 = request.getParams();
        if (HttpClientParams.isAuthenticating(params2)) {
            HttpHost target = (HttpHost) context.getAttribute("http.target_host");
            if (target == null) {
                target = route.getTargetHost();
            }
            if (target.getPort() < 0) {
                target = new HttpHost(target.getHostName(), this.connManager.getSchemeRegistry().getScheme(target).getDefaultPort(), target.getSchemeName());
            }
            boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, this.targetAuthState, context);
            HttpHost proxy = route.getProxyHost();
            if (proxy == null) {
                proxy = route.getTargetHost();
            }
            boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context);
            if (targetAuthRequested) {
                if (this.authenticator.authenticate(target, response, this.targetAuthStrategy, this.targetAuthState, context)) {
                    return roureq;
                }
            }
            if (proxyAuthRequested) {
                if (this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                    return roureq;
                }
            }
        }
        if (!HttpClientParams.isRedirecting(params2) || !this.redirectStrategy.isRedirected(request, response, context)) {
            return null;
        }
        if (this.redirectCount >= this.maxRedirects) {
            throw new RedirectException("Maximum redirects (" + this.maxRedirects + ") exceeded");
        }
        this.redirectCount++;
        this.virtualHost = null;
        HttpUriRequest redirect = this.redirectStrategy.getRedirect(request, response, context);
        redirect.setHeaders(request.getOriginal().getAllHeaders());
        URI uri = redirect.getURI();
        HttpHost newTarget = URIUtils.extractHost(uri);
        if (newTarget == null) {
            throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
        }
        if (!route.getTargetHost().equals(newTarget)) {
            this.log.debug("Resetting target auth state");
            this.targetAuthState.reset();
            AuthScheme authScheme = this.proxyAuthState.getAuthScheme();
            if (authScheme != null && authScheme.isConnectionBased()) {
                this.log.debug("Resetting proxy auth state");
                this.proxyAuthState.reset();
            }
        }
        RequestWrapper wrapper = wrapRequest(redirect);
        wrapper.setParams(params2);
        HttpRoute newRoute = determineRoute(newTarget, wrapper, context);
        RoutedRequest newRequest = new RoutedRequest(wrapper, newRoute);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Redirecting to '" + uri + "' via " + newRoute);
        }
        return newRequest;
    }

    private void abortConnection() {
        ManagedClientConnection mcc = this.managedConn;
        if (mcc != null) {
            this.managedConn = null;
            try {
                mcc.abortConnection();
            } catch (IOException ex) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(ex.getMessage(), ex);
                }
            }
            try {
                mcc.releaseConnection();
            } catch (IOException ignored) {
                this.log.debug("Error releasing connection", ignored);
            }
        }
    }
}
