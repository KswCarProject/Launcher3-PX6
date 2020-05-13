package org.apache.http.client.params;

import java.net.InetAddress;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

@Deprecated
public final class HttpClientParamConfig {
    private HttpClientParamConfig() {
    }

    public static RequestConfig getRequestConfig(HttpParams params) {
        return getRequestConfig(params, RequestConfig.DEFAULT);
    }

    public static RequestConfig getRequestConfig(HttpParams params, RequestConfig defaultConfig) {
        boolean z;
        boolean z2 = true;
        RequestConfig.Builder redirectsEnabled = RequestConfig.copy(defaultConfig).setSocketTimeout(params.getIntParameter(CoreConnectionPNames.SO_TIMEOUT, defaultConfig.getSocketTimeout())).setStaleConnectionCheckEnabled(params.getBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, defaultConfig.isStaleConnectionCheckEnabled())).setConnectTimeout(params.getIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, defaultConfig.getConnectTimeout())).setExpectContinueEnabled(params.getBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, defaultConfig.isExpectContinueEnabled())).setAuthenticationEnabled(params.getBooleanParameter(ClientPNames.HANDLE_AUTHENTICATION, defaultConfig.isAuthenticationEnabled())).setCircularRedirectsAllowed(params.getBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, defaultConfig.isCircularRedirectsAllowed())).setConnectionRequestTimeout((int) params.getLongParameter("http.conn-manager.timeout", (long) defaultConfig.getConnectionRequestTimeout())).setMaxRedirects(params.getIntParameter(ClientPNames.MAX_REDIRECTS, defaultConfig.getMaxRedirects())).setRedirectsEnabled(params.getBooleanParameter(ClientPNames.HANDLE_REDIRECTS, defaultConfig.isRedirectsEnabled()));
        if (!defaultConfig.isRelativeRedirectsAllowed()) {
            z = true;
        } else {
            z = false;
        }
        if (params.getBooleanParameter(ClientPNames.REJECT_RELATIVE_REDIRECT, z)) {
            z2 = false;
        }
        RequestConfig.Builder builder = redirectsEnabled.setRelativeRedirectsAllowed(z2);
        HttpHost proxy = (HttpHost) params.getParameter(ConnRoutePNames.DEFAULT_PROXY);
        if (proxy != null) {
            builder.setProxy(proxy);
        }
        InetAddress localAddress = (InetAddress) params.getParameter(ConnRoutePNames.LOCAL_ADDRESS);
        if (localAddress != null) {
            builder.setLocalAddress(localAddress);
        }
        Collection<String> targetAuthPrefs = (Collection) params.getParameter(AuthPNames.TARGET_AUTH_PREF);
        if (targetAuthPrefs != null) {
            builder.setTargetPreferredAuthSchemes(targetAuthPrefs);
        }
        Collection<String> proxySuthPrefs = (Collection) params.getParameter(AuthPNames.PROXY_AUTH_PREF);
        if (proxySuthPrefs != null) {
            builder.setProxyPreferredAuthSchemes(proxySuthPrefs);
        }
        String cookiePolicy = (String) params.getParameter(ClientPNames.COOKIE_POLICY);
        if (cookiePolicy != null) {
            builder.setCookieSpec(cookiePolicy);
        }
        return builder.build();
    }
}
