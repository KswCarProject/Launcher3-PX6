package org.apache.http.client.config;

import java.net.InetAddress;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestConfig implements Cloneable {
    public static final RequestConfig DEFAULT = new Builder().build();
    private final boolean authenticationEnabled;
    private final boolean circularRedirectsAllowed;
    private final int connectTimeout;
    private final int connectionRequestTimeout;
    private final boolean contentCompressionEnabled;
    private final String cookieSpec;
    private final boolean expectContinueEnabled;
    private final InetAddress localAddress;
    private final int maxRedirects;
    private final HttpHost proxy;
    private final Collection<String> proxyPreferredAuthSchemes;
    private final boolean redirectsEnabled;
    private final boolean relativeRedirectsAllowed;
    private final int socketTimeout;
    private final boolean staleConnectionCheckEnabled;
    private final Collection<String> targetPreferredAuthSchemes;

    protected RequestConfig() {
        this(false, (HttpHost) null, (InetAddress) null, false, (String) null, false, false, false, 0, false, (Collection<String>) null, (Collection<String>) null, 0, 0, 0, true);
    }

    RequestConfig(boolean expectContinueEnabled2, HttpHost proxy2, InetAddress localAddress2, boolean staleConnectionCheckEnabled2, String cookieSpec2, boolean redirectsEnabled2, boolean relativeRedirectsAllowed2, boolean circularRedirectsAllowed2, int maxRedirects2, boolean authenticationEnabled2, Collection<String> targetPreferredAuthSchemes2, Collection<String> proxyPreferredAuthSchemes2, int connectionRequestTimeout2, int connectTimeout2, int socketTimeout2, boolean contentCompressionEnabled2) {
        this.expectContinueEnabled = expectContinueEnabled2;
        this.proxy = proxy2;
        this.localAddress = localAddress2;
        this.staleConnectionCheckEnabled = staleConnectionCheckEnabled2;
        this.cookieSpec = cookieSpec2;
        this.redirectsEnabled = redirectsEnabled2;
        this.relativeRedirectsAllowed = relativeRedirectsAllowed2;
        this.circularRedirectsAllowed = circularRedirectsAllowed2;
        this.maxRedirects = maxRedirects2;
        this.authenticationEnabled = authenticationEnabled2;
        this.targetPreferredAuthSchemes = targetPreferredAuthSchemes2;
        this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes2;
        this.connectionRequestTimeout = connectionRequestTimeout2;
        this.connectTimeout = connectTimeout2;
        this.socketTimeout = socketTimeout2;
        this.contentCompressionEnabled = contentCompressionEnabled2;
    }

    public boolean isExpectContinueEnabled() {
        return this.expectContinueEnabled;
    }

    public HttpHost getProxy() {
        return this.proxy;
    }

    public InetAddress getLocalAddress() {
        return this.localAddress;
    }

    @Deprecated
    public boolean isStaleConnectionCheckEnabled() {
        return this.staleConnectionCheckEnabled;
    }

    public String getCookieSpec() {
        return this.cookieSpec;
    }

    public boolean isRedirectsEnabled() {
        return this.redirectsEnabled;
    }

    public boolean isRelativeRedirectsAllowed() {
        return this.relativeRedirectsAllowed;
    }

    public boolean isCircularRedirectsAllowed() {
        return this.circularRedirectsAllowed;
    }

    public int getMaxRedirects() {
        return this.maxRedirects;
    }

    public boolean isAuthenticationEnabled() {
        return this.authenticationEnabled;
    }

    public Collection<String> getTargetPreferredAuthSchemes() {
        return this.targetPreferredAuthSchemes;
    }

    public Collection<String> getProxyPreferredAuthSchemes() {
        return this.proxyPreferredAuthSchemes;
    }

    public int getConnectionRequestTimeout() {
        return this.connectionRequestTimeout;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    @Deprecated
    public boolean isDecompressionEnabled() {
        return this.contentCompressionEnabled;
    }

    public boolean isContentCompressionEnabled() {
        return this.contentCompressionEnabled;
    }

    /* access modifiers changed from: protected */
    public RequestConfig clone() throws CloneNotSupportedException {
        return (RequestConfig) super.clone();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("expectContinueEnabled=").append(this.expectContinueEnabled);
        builder.append(", proxy=").append(this.proxy);
        builder.append(", localAddress=").append(this.localAddress);
        builder.append(", cookieSpec=").append(this.cookieSpec);
        builder.append(", redirectsEnabled=").append(this.redirectsEnabled);
        builder.append(", relativeRedirectsAllowed=").append(this.relativeRedirectsAllowed);
        builder.append(", maxRedirects=").append(this.maxRedirects);
        builder.append(", circularRedirectsAllowed=").append(this.circularRedirectsAllowed);
        builder.append(", authenticationEnabled=").append(this.authenticationEnabled);
        builder.append(", targetPreferredAuthSchemes=").append(this.targetPreferredAuthSchemes);
        builder.append(", proxyPreferredAuthSchemes=").append(this.proxyPreferredAuthSchemes);
        builder.append(", connectionRequestTimeout=").append(this.connectionRequestTimeout);
        builder.append(", connectTimeout=").append(this.connectTimeout);
        builder.append(", socketTimeout=").append(this.socketTimeout);
        builder.append(", contentCompressionEnabled=").append(this.contentCompressionEnabled);
        builder.append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(RequestConfig config) {
        return new Builder().setExpectContinueEnabled(config.isExpectContinueEnabled()).setProxy(config.getProxy()).setLocalAddress(config.getLocalAddress()).setStaleConnectionCheckEnabled(config.isStaleConnectionCheckEnabled()).setCookieSpec(config.getCookieSpec()).setRedirectsEnabled(config.isRedirectsEnabled()).setRelativeRedirectsAllowed(config.isRelativeRedirectsAllowed()).setCircularRedirectsAllowed(config.isCircularRedirectsAllowed()).setMaxRedirects(config.getMaxRedirects()).setAuthenticationEnabled(config.isAuthenticationEnabled()).setTargetPreferredAuthSchemes(config.getTargetPreferredAuthSchemes()).setProxyPreferredAuthSchemes(config.getProxyPreferredAuthSchemes()).setConnectionRequestTimeout(config.getConnectionRequestTimeout()).setConnectTimeout(config.getConnectTimeout()).setSocketTimeout(config.getSocketTimeout()).setDecompressionEnabled(config.isDecompressionEnabled()).setContentCompressionEnabled(config.isContentCompressionEnabled());
    }

    public static class Builder {
        private boolean authenticationEnabled = true;
        private boolean circularRedirectsAllowed;
        private int connectTimeout = -1;
        private int connectionRequestTimeout = -1;
        private boolean contentCompressionEnabled = true;
        private String cookieSpec;
        private boolean expectContinueEnabled;
        private InetAddress localAddress;
        private int maxRedirects = 50;
        private HttpHost proxy;
        private Collection<String> proxyPreferredAuthSchemes;
        private boolean redirectsEnabled = true;
        private boolean relativeRedirectsAllowed = true;
        private int socketTimeout = -1;
        private boolean staleConnectionCheckEnabled = false;
        private Collection<String> targetPreferredAuthSchemes;

        Builder() {
        }

        public Builder setExpectContinueEnabled(boolean expectContinueEnabled2) {
            this.expectContinueEnabled = expectContinueEnabled2;
            return this;
        }

        public Builder setProxy(HttpHost proxy2) {
            this.proxy = proxy2;
            return this;
        }

        public Builder setLocalAddress(InetAddress localAddress2) {
            this.localAddress = localAddress2;
            return this;
        }

        @Deprecated
        public Builder setStaleConnectionCheckEnabled(boolean staleConnectionCheckEnabled2) {
            this.staleConnectionCheckEnabled = staleConnectionCheckEnabled2;
            return this;
        }

        public Builder setCookieSpec(String cookieSpec2) {
            this.cookieSpec = cookieSpec2;
            return this;
        }

        public Builder setRedirectsEnabled(boolean redirectsEnabled2) {
            this.redirectsEnabled = redirectsEnabled2;
            return this;
        }

        public Builder setRelativeRedirectsAllowed(boolean relativeRedirectsAllowed2) {
            this.relativeRedirectsAllowed = relativeRedirectsAllowed2;
            return this;
        }

        public Builder setCircularRedirectsAllowed(boolean circularRedirectsAllowed2) {
            this.circularRedirectsAllowed = circularRedirectsAllowed2;
            return this;
        }

        public Builder setMaxRedirects(int maxRedirects2) {
            this.maxRedirects = maxRedirects2;
            return this;
        }

        public Builder setAuthenticationEnabled(boolean authenticationEnabled2) {
            this.authenticationEnabled = authenticationEnabled2;
            return this;
        }

        public Builder setTargetPreferredAuthSchemes(Collection<String> targetPreferredAuthSchemes2) {
            this.targetPreferredAuthSchemes = targetPreferredAuthSchemes2;
            return this;
        }

        public Builder setProxyPreferredAuthSchemes(Collection<String> proxyPreferredAuthSchemes2) {
            this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes2;
            return this;
        }

        public Builder setConnectionRequestTimeout(int connectionRequestTimeout2) {
            this.connectionRequestTimeout = connectionRequestTimeout2;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout2) {
            this.connectTimeout = connectTimeout2;
            return this;
        }

        public Builder setSocketTimeout(int socketTimeout2) {
            this.socketTimeout = socketTimeout2;
            return this;
        }

        @Deprecated
        public Builder setDecompressionEnabled(boolean decompressionEnabled) {
            this.contentCompressionEnabled = decompressionEnabled;
            return this;
        }

        public Builder setContentCompressionEnabled(boolean contentCompressionEnabled2) {
            this.contentCompressionEnabled = contentCompressionEnabled2;
            return this;
        }

        public RequestConfig build() {
            return new RequestConfig(this.expectContinueEnabled, this.proxy, this.localAddress, this.staleConnectionCheckEnabled, this.cookieSpec, this.redirectsEnabled, this.relativeRedirectsAllowed, this.circularRedirectsAllowed, this.maxRedirects, this.authenticationEnabled, this.targetPreferredAuthSchemes, this.proxyPreferredAuthSchemes, this.connectionRequestTimeout, this.connectTimeout, this.socketTimeout, this.contentCompressionEnabled);
        }
    }
}
