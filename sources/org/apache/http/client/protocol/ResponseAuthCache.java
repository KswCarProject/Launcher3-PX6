package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthCache;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class ResponseAuthCache implements HttpResponseInterceptor {
    private final Log log = LogFactory.getLog(getClass());

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP request");
        Args.notNull(context, "HTTP context");
        AuthCache authCache = (AuthCache) context.getAttribute("http.auth.auth-cache");
        HttpHost target = (HttpHost) context.getAttribute("http.target_host");
        AuthState targetState = (AuthState) context.getAttribute("http.auth.target-scope");
        if (!(target == null || targetState == null)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Target auth state: " + targetState.getState());
            }
            if (isCachable(targetState)) {
                SchemeRegistry schemeRegistry = (SchemeRegistry) context.getAttribute(ClientContext.SCHEME_REGISTRY);
                if (target.getPort() < 0) {
                    target = new HttpHost(target.getHostName(), schemeRegistry.getScheme(target).resolvePort(target.getPort()), target.getSchemeName());
                }
                if (authCache == null) {
                    authCache = new BasicAuthCache();
                    context.setAttribute("http.auth.auth-cache", authCache);
                }
                switch (targetState.getState()) {
                    case CHALLENGED:
                        cache(authCache, target, targetState.getAuthScheme());
                        break;
                    case FAILURE:
                        uncache(authCache, target, targetState.getAuthScheme());
                        break;
                }
            }
        }
        HttpHost proxy = (HttpHost) context.getAttribute(ExecutionContext.HTTP_PROXY_HOST);
        AuthState proxyState = (AuthState) context.getAttribute("http.auth.proxy-scope");
        if (proxy != null && proxyState != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Proxy auth state: " + proxyState.getState());
            }
            if (isCachable(proxyState)) {
                if (authCache == null) {
                    authCache = new BasicAuthCache();
                    context.setAttribute("http.auth.auth-cache", authCache);
                }
                switch (proxyState.getState()) {
                    case CHALLENGED:
                        cache(authCache, proxy, proxyState.getAuthScheme());
                        return;
                    case FAILURE:
                        uncache(authCache, proxy, proxyState.getAuthScheme());
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private boolean isCachable(AuthState authState) {
        AuthScheme authScheme = authState.getAuthScheme();
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        if (schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest")) {
            return true;
        }
        return false;
    }

    private void cache(AuthCache authCache, HttpHost host, AuthScheme authScheme) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + host);
        }
        authCache.put(host, authScheme);
    }

    private void uncache(AuthCache authCache, HttpHost host, AuthScheme authScheme) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + host);
        }
        authCache.remove(host);
    }
}
