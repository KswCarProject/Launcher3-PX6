package org.apache.http.impl.client;

import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class DefaultProxyAuthenticationHandler extends AbstractAuthenticationHandler {
    public boolean isAuthenticationRequested(HttpResponse response, HttpContext context) {
        Args.notNull(response, "HTTP response");
        return response.getStatusLine().getStatusCode() == 407;
    }

    public Map<String, Header> getChallenges(HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Args.notNull(response, "HTTP response");
        return parseChallenges(response.getHeaders("Proxy-Authenticate"));
    }

    /* access modifiers changed from: protected */
    public List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
        List<String> authpref = (List) response.getParams().getParameter(AuthPNames.PROXY_AUTH_PREF);
        return authpref != null ? authpref : super.getAuthPreferences(response, context);
    }
}
