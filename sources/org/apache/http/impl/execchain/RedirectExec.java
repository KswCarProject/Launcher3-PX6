package org.apache.http.impl.execchain;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RedirectExec implements ClientExecChain {
    private final Log log = LogFactory.getLog(getClass());
    private final RedirectStrategy redirectStrategy;
    private final ClientExecChain requestExecutor;
    private final HttpRoutePlanner routePlanner;

    public RedirectExec(ClientExecChain requestExecutor2, HttpRoutePlanner routePlanner2, RedirectStrategy redirectStrategy2) {
        Args.notNull(requestExecutor2, "HTTP client request executor");
        Args.notNull(routePlanner2, "HTTP route planner");
        Args.notNull(redirectStrategy2, "HTTP redirect strategy");
        this.requestExecutor = requestExecutor2;
        this.routePlanner = routePlanner2;
        this.redirectStrategy = redirectStrategy2;
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        CloseableHttpResponse response;
        AuthScheme authScheme;
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        List<URI> redirectLocations = context.getRedirectLocations();
        if (redirectLocations != null) {
            redirectLocations.clear();
        }
        RequestConfig config = context.getRequestConfig();
        int maxRedirects = config.getMaxRedirects() > 0 ? config.getMaxRedirects() : 50;
        HttpRoute currentRoute = route;
        HttpRequestWrapper currentRequest = request;
        int redirectCount = 0;
        while (true) {
            response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);
            try {
                if (!config.isRedirectsEnabled() || !this.redirectStrategy.isRedirected(currentRequest.getOriginal(), response, context)) {
                    return response;
                }
                if (redirectCount >= maxRedirects) {
                    throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
                }
                redirectCount++;
                HttpRequest redirect = this.redirectStrategy.getRedirect(currentRequest.getOriginal(), response, context);
                if (!redirect.headerIterator().hasNext()) {
                    redirect.setHeaders(request.getOriginal().getAllHeaders());
                }
                currentRequest = HttpRequestWrapper.wrap(redirect);
                if (currentRequest instanceof HttpEntityEnclosingRequest) {
                    RequestEntityProxy.enhance((HttpEntityEnclosingRequest) currentRequest);
                }
                URI uri = currentRequest.getURI();
                HttpHost newTarget = URIUtils.extractHost(uri);
                if (newTarget == null) {
                    throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
                }
                if (!currentRoute.getTargetHost().equals(newTarget)) {
                    AuthState targetAuthState = context.getTargetAuthState();
                    if (targetAuthState != null) {
                        this.log.debug("Resetting target auth state");
                        targetAuthState.reset();
                    }
                    AuthState proxyAuthState = context.getProxyAuthState();
                    if (!(proxyAuthState == null || (authScheme = proxyAuthState.getAuthScheme()) == null || !authScheme.isConnectionBased())) {
                        this.log.debug("Resetting proxy auth state");
                        proxyAuthState.reset();
                    }
                }
                currentRoute = this.routePlanner.determineRoute(newTarget, currentRequest, context);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Redirecting to '" + uri + "' via " + currentRoute);
                }
                EntityUtils.consume(response.getEntity());
                response.close();
            } catch (RuntimeException ex) {
                response.close();
                throw ex;
            } catch (IOException ex2) {
                response.close();
                throw ex2;
            } catch (HttpException ex3) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ioex) {
                    this.log.debug("I/O error while releasing connection", ioex);
                } finally {
                    response.close();
                }
                throw ex3;
            }
        }
        return response;
    }
}
