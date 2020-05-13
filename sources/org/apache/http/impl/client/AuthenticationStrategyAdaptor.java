package org.apache.http.impl.client;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
class AuthenticationStrategyAdaptor implements AuthenticationStrategy {
    private final AuthenticationHandler handler;
    private final Log log = LogFactory.getLog(getClass());

    public AuthenticationStrategyAdaptor(AuthenticationHandler handler2) {
        this.handler = handler2;
    }

    public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        return this.handler.isAuthenticationRequested(response, context);
    }

    public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        return this.handler.getChallenges(response, context);
    }

    public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Args.notNull(challenges, "Map of auth challenges");
        Args.notNull(authhost, "Host");
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        Queue<AuthOption> options = new LinkedList<>();
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute("http.auth.credentials-provider");
        if (credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
        } else {
            try {
                AuthScheme authScheme = this.handler.selectScheme(challenges, response, context);
                authScheme.processChallenge(challenges.get(authScheme.getSchemeName().toLowerCase(Locale.ROOT)));
                Credentials credentials = credsProvider.getCredentials(new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName()));
                if (credentials != null) {
                    options.add(new AuthOption(authScheme, credentials));
                }
            } catch (AuthenticationException ex) {
                if (this.log.isWarnEnabled()) {
                    this.log.warn(ex.getMessage(), ex);
                }
            }
        }
        return options;
    }

    public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        AuthCache authCache = (AuthCache) context.getAttribute("http.auth.auth-cache");
        if (isCachable(authScheme)) {
            if (authCache == null) {
                authCache = new BasicAuthCache();
                context.setAttribute("http.auth.auth-cache", authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
            }
            authCache.put(authhost, authScheme);
        }
    }

    public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        AuthCache authCache = (AuthCache) context.getAttribute("http.auth.auth-cache");
        if (authCache != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
            }
            authCache.remove(authhost);
        }
    }

    private boolean isCachable(AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        if (schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest")) {
            return true;
        }
        return false;
    }

    public AuthenticationHandler getHandler() {
        return this.handler;
    }
}
