package org.apache.http.client.protocol;

import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

@Deprecated
abstract class RequestAuthenticationBase implements HttpRequestInterceptor {
    final Log log = LogFactory.getLog(getClass());

    /* access modifiers changed from: package-private */
    public void process(AuthState authState, HttpRequest request, HttpContext context) {
        AuthScheme authScheme = authState.getAuthScheme();
        Credentials creds = authState.getCredentials();
        switch (authState.getState()) {
            case FAILURE:
                return;
            case SUCCESS:
                ensureAuthScheme(authScheme);
                if (authScheme.isConnectionBased()) {
                    return;
                }
                break;
            case CHALLENGED:
                Queue<AuthOption> authOptions = authState.getAuthOptions();
                if (authOptions == null) {
                    ensureAuthScheme(authScheme);
                    break;
                } else {
                    while (!authOptions.isEmpty()) {
                        AuthOption authOption = authOptions.remove();
                        AuthScheme authScheme2 = authOption.getAuthScheme();
                        Credentials creds2 = authOption.getCredentials();
                        authState.update(authScheme2, creds2);
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Generating response to an authentication challenge using " + authScheme2.getSchemeName() + " scheme");
                        }
                        try {
                            request.addHeader(authenticate(authScheme2, creds2, request, context));
                            return;
                        } catch (AuthenticationException ex) {
                            if (this.log.isWarnEnabled()) {
                                this.log.warn(authScheme2 + " authentication error: " + ex.getMessage());
                            }
                        }
                    }
                    return;
                }
        }
        if (authScheme != null) {
            try {
                request.addHeader(authenticate(authScheme, creds, request, context));
            } catch (AuthenticationException ex2) {
                if (this.log.isErrorEnabled()) {
                    this.log.error(authScheme + " authentication error: " + ex2.getMessage());
                }
            }
        }
    }

    private void ensureAuthScheme(AuthScheme authScheme) {
        Asserts.notNull(authScheme, "Auth scheme");
    }

    private Header authenticate(AuthScheme authScheme, Credentials creds, HttpRequest request, HttpContext context) throws AuthenticationException {
        Asserts.notNull(authScheme, "Auth scheme");
        if (authScheme instanceof ContextAwareAuthScheme) {
            return ((ContextAwareAuthScheme) authScheme).authenticate(creds, request, context);
        }
        return authScheme.authenticate(creds, request);
    }
}
