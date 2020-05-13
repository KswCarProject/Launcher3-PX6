package org.apache.http.client.params;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class HttpClientParams {
    private HttpClientParams() {
    }

    public static boolean isRedirecting(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
    }

    public static void setRedirecting(HttpParams params, boolean value) {
        Args.notNull(params, "HTTP parameters");
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, value);
    }

    public static boolean isAuthenticating(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getBooleanParameter(ClientPNames.HANDLE_AUTHENTICATION, true);
    }

    public static void setAuthenticating(HttpParams params, boolean value) {
        Args.notNull(params, "HTTP parameters");
        params.setBooleanParameter(ClientPNames.HANDLE_AUTHENTICATION, value);
    }

    public static String getCookiePolicy(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        String cookiePolicy = (String) params.getParameter(ClientPNames.COOKIE_POLICY);
        if (cookiePolicy == null) {
            return "best-match";
        }
        return cookiePolicy;
    }

    public static void setCookiePolicy(HttpParams params, String cookiePolicy) {
        Args.notNull(params, "HTTP parameters");
        params.setParameter(ClientPNames.COOKIE_POLICY, cookiePolicy);
    }

    public static void setConnectionManagerTimeout(HttpParams params, long timeout) {
        Args.notNull(params, "HTTP parameters");
        params.setLongParameter("http.conn-manager.timeout", timeout);
    }

    public static long getConnectionManagerTimeout(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        Long timeout = (Long) params.getParameter("http.conn-manager.timeout");
        if (timeout != null) {
            return timeout.longValue();
        }
        return (long) HttpConnectionParams.getConnectionTimeout(params);
    }
}
