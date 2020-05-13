package org.apache.http.impl.client;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.TextUtils;

public class HttpClientBuilder {
    private boolean authCachingDisabled;
    private Lookup<AuthSchemeProvider> authSchemeRegistry;
    private boolean automaticRetriesDisabled;
    private BackoffManager backoffManager;
    private List<Closeable> closeables;
    private HttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private long connTimeToLive = -1;
    private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;
    private ConnectionBackoffStrategy connectionBackoffStrategy;
    private boolean connectionStateDisabled;
    private boolean contentCompressionDisabled;
    private Map<String, InputStreamFactory> contentDecoderMap;
    private boolean cookieManagementDisabled;
    private Lookup<CookieSpecProvider> cookieSpecRegistry;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private ConnectionConfig defaultConnectionConfig;
    private Collection<? extends Header> defaultHeaders;
    private RequestConfig defaultRequestConfig;
    private SocketConfig defaultSocketConfig;
    private DnsResolver dnsResolver;
    private boolean evictExpiredConnections;
    private boolean evictIdleConnections;
    private HostnameVerifier hostnameVerifier;
    private HttpProcessor httpprocessor;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private int maxConnPerRoute = 0;
    private int maxConnTotal = 0;
    private long maxIdleTime;
    private TimeUnit maxIdleTimeUnit;
    private HttpHost proxy;
    private AuthenticationStrategy proxyAuthStrategy;
    private PublicSuffixMatcher publicSuffixMatcher;
    private boolean redirectHandlingDisabled;
    private RedirectStrategy redirectStrategy;
    private HttpRequestExecutor requestExec;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private HttpRequestRetryHandler retryHandler;
    private ConnectionReuseStrategy reuseStrategy;
    private HttpRoutePlanner routePlanner;
    private SchemePortResolver schemePortResolver;
    private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
    private SSLContext sslContext;
    private LayeredConnectionSocketFactory sslSocketFactory;
    private boolean systemProperties;
    private AuthenticationStrategy targetAuthStrategy;
    private String userAgent;
    private UserTokenHandler userTokenHandler;

    public static HttpClientBuilder create() {
        return new HttpClientBuilder();
    }

    protected HttpClientBuilder() {
    }

    public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor requestExec2) {
        this.requestExec = requestExec2;
        return this;
    }

    @Deprecated
    public final HttpClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier2) {
        this.hostnameVerifier = hostnameVerifier2;
        return this;
    }

    public final HttpClientBuilder setSSLHostnameVerifier(HostnameVerifier hostnameVerifier2) {
        this.hostnameVerifier = hostnameVerifier2;
        return this;
    }

    public final HttpClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher publicSuffixMatcher2) {
        this.publicSuffixMatcher = publicSuffixMatcher2;
        return this;
    }

    @Deprecated
    public final HttpClientBuilder setSslcontext(SSLContext sslcontext) {
        return setSSLContext(sslcontext);
    }

    public final HttpClientBuilder setSSLContext(SSLContext sslContext2) {
        this.sslContext = sslContext2;
        return this;
    }

    public final HttpClientBuilder setSSLSocketFactory(LayeredConnectionSocketFactory sslSocketFactory2) {
        this.sslSocketFactory = sslSocketFactory2;
        return this;
    }

    public final HttpClientBuilder setMaxConnTotal(int maxConnTotal2) {
        this.maxConnTotal = maxConnTotal2;
        return this;
    }

    public final HttpClientBuilder setMaxConnPerRoute(int maxConnPerRoute2) {
        this.maxConnPerRoute = maxConnPerRoute2;
        return this;
    }

    public final HttpClientBuilder setDefaultSocketConfig(SocketConfig config) {
        this.defaultSocketConfig = config;
        return this;
    }

    public final HttpClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
        this.defaultConnectionConfig = config;
        return this;
    }

    public final HttpClientBuilder setConnectionTimeToLive(long connTimeToLive2, TimeUnit connTimeToLiveTimeUnit2) {
        this.connTimeToLive = connTimeToLive2;
        this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit2;
        return this;
    }

    public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager connManager2) {
        this.connManager = connManager2;
        return this;
    }

    public final HttpClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy2) {
        this.reuseStrategy = reuseStrategy2;
        return this;
    }

    public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy2) {
        this.keepAliveStrategy = keepAliveStrategy2;
        return this;
    }

    public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy2) {
        this.targetAuthStrategy = targetAuthStrategy2;
        return this;
    }

    public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy2) {
        this.proxyAuthStrategy = proxyAuthStrategy2;
        return this;
    }

    public final HttpClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler2) {
        this.userTokenHandler = userTokenHandler2;
        return this;
    }

    public final HttpClientBuilder disableConnectionState() {
        this.connectionStateDisabled = true;
        return this;
    }

    public final HttpClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver2) {
        this.schemePortResolver = schemePortResolver2;
        return this;
    }

    public final HttpClientBuilder setUserAgent(String userAgent2) {
        this.userAgent = userAgent2;
        return this;
    }

    public final HttpClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders2) {
        this.defaultHeaders = defaultHeaders2;
        return this;
    }

    public final HttpClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseFirst == null) {
                this.responseFirst = new LinkedList<>();
            }
            this.responseFirst.addFirst(itcp);
        }
        return this;
    }

    public final HttpClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseLast == null) {
                this.responseLast = new LinkedList<>();
            }
            this.responseLast.addLast(itcp);
        }
        return this;
    }

    public final HttpClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestFirst == null) {
                this.requestFirst = new LinkedList<>();
            }
            this.requestFirst.addFirst(itcp);
        }
        return this;
    }

    public final HttpClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestLast == null) {
                this.requestLast = new LinkedList<>();
            }
            this.requestLast.addLast(itcp);
        }
        return this;
    }

    public final HttpClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public final HttpClientBuilder disableContentCompression() {
        this.contentCompressionDisabled = true;
        return this;
    }

    public final HttpClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    public final HttpClientBuilder setHttpProcessor(HttpProcessor httpprocessor2) {
        this.httpprocessor = httpprocessor2;
        return this;
    }

    public final HttpClientBuilder setDnsResolver(DnsResolver dnsResolver2) {
        this.dnsResolver = dnsResolver2;
        return this;
    }

    public final HttpClientBuilder setRetryHandler(HttpRequestRetryHandler retryHandler2) {
        this.retryHandler = retryHandler2;
        return this;
    }

    public final HttpClientBuilder disableAutomaticRetries() {
        this.automaticRetriesDisabled = true;
        return this;
    }

    public final HttpClientBuilder setProxy(HttpHost proxy2) {
        this.proxy = proxy2;
        return this;
    }

    public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner2) {
        this.routePlanner = routePlanner2;
        return this;
    }

    public final HttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy2) {
        this.redirectStrategy = redirectStrategy2;
        return this;
    }

    public final HttpClientBuilder disableRedirectHandling() {
        this.redirectHandlingDisabled = true;
        return this;
    }

    public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy connectionBackoffStrategy2) {
        this.connectionBackoffStrategy = connectionBackoffStrategy2;
        return this;
    }

    public final HttpClientBuilder setBackoffManager(BackoffManager backoffManager2) {
        this.backoffManager = backoffManager2;
        return this;
    }

    public final HttpClientBuilder setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy serviceUnavailStrategy2) {
        this.serviceUnavailStrategy = serviceUnavailStrategy2;
        return this;
    }

    public final HttpClientBuilder setDefaultCookieStore(CookieStore cookieStore2) {
        this.cookieStore = cookieStore2;
        return this;
    }

    public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider2) {
        this.credentialsProvider = credentialsProvider2;
        return this;
    }

    public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> authSchemeRegistry2) {
        this.authSchemeRegistry = authSchemeRegistry2;
        return this;
    }

    public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> cookieSpecRegistry2) {
        this.cookieSpecRegistry = cookieSpecRegistry2;
        return this;
    }

    public final HttpClientBuilder setContentDecoderRegistry(Map<String, InputStreamFactory> contentDecoderMap2) {
        this.contentDecoderMap = contentDecoderMap2;
        return this;
    }

    public final HttpClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final HttpClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public final HttpClientBuilder evictExpiredConnections() {
        this.evictExpiredConnections = true;
        return this;
    }

    @Deprecated
    public final HttpClientBuilder evictIdleConnections(Long maxIdleTime2, TimeUnit maxIdleTimeUnit2) {
        return evictIdleConnections(maxIdleTime2.longValue(), maxIdleTimeUnit2);
    }

    public final HttpClientBuilder evictIdleConnections(long maxIdleTime2, TimeUnit maxIdleTimeUnit2) {
        this.evictIdleConnections = true;
        this.maxIdleTime = maxIdleTime2;
        this.maxIdleTimeUnit = maxIdleTimeUnit2;
        return this;
    }

    /* access modifiers changed from: protected */
    public ClientExecChain createMainExec(HttpRequestExecutor requestExec2, HttpClientConnectionManager connManager2, ConnectionReuseStrategy reuseStrategy2, ConnectionKeepAliveStrategy keepAliveStrategy2, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy2, AuthenticationStrategy proxyAuthStrategy2, UserTokenHandler userTokenHandler2) {
        return new MainClientExec(requestExec2, connManager2, reuseStrategy2, keepAliveStrategy2, proxyHttpProcessor, targetAuthStrategy2, proxyAuthStrategy2, userTokenHandler2);
    }

    /* access modifiers changed from: protected */
    public ClientExecChain decorateMainExec(ClientExecChain mainExec) {
        return mainExec;
    }

    /* access modifiers changed from: protected */
    public ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
        return protocolExec;
    }

    /* access modifiers changed from: protected */
    public void addCloseable(Closeable closeable) {
        if (closeable != null) {
            if (this.closeables == null) {
                this.closeables = new ArrayList();
            }
            this.closeables.add(closeable);
        }
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v7, resolved type: org.apache.http.impl.execchain.BackoffStrategyExec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v8, resolved type: org.apache.http.impl.execchain.RedirectExec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v9, resolved type: org.apache.http.impl.execchain.ServiceUnavailableRetryExec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: org.apache.http.conn.socket.LayeredConnectionSocketFactory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: javax.net.ssl.HostnameVerifier} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: org.apache.http.conn.ssl.SSLConnectionSocketFactory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: org.apache.http.conn.ssl.DefaultHostnameVerifier} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v134, resolved type: org.apache.http.conn.ssl.SSLConnectionSocketFactory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v135, resolved type: org.apache.http.conn.ssl.SSLConnectionSocketFactory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v136, resolved type: org.apache.http.conn.ssl.SSLConnectionSocketFactory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v137, resolved type: org.apache.http.conn.ssl.DefaultHostnameVerifier} */
    /* JADX WARNING: type inference failed for: r18v1, types: [org.apache.http.conn.routing.HttpRoutePlanner] */
    /* JADX WARNING: type inference failed for: r43v1 */
    /* JADX WARNING: type inference failed for: r31v1 */
    /* JADX WARNING: type inference failed for: r18v2 */
    /* JADX WARNING: type inference failed for: r0v131, types: [org.apache.http.impl.conn.DefaultRoutePlanner] */
    /* JADX WARNING: type inference failed for: r0v132, types: [org.apache.http.impl.conn.SystemDefaultRoutePlanner] */
    /* JADX WARNING: type inference failed for: r0v133, types: [org.apache.http.impl.conn.DefaultProxyRoutePlanner] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.http.impl.client.CloseableHttpClient build() {
        /*
            r50 = this;
            r0 = r50
            org.apache.http.conn.util.PublicSuffixMatcher r0 = r0.publicSuffixMatcher
            r36 = r0
            if (r36 != 0) goto L_0x000c
            org.apache.http.conn.util.PublicSuffixMatcher r36 = org.apache.http.conn.util.PublicSuffixMatcherLoader.getDefault()
        L_0x000c:
            r0 = r50
            org.apache.http.protocol.HttpRequestExecutor r0 = r0.requestExec
            r38 = r0
            if (r38 != 0) goto L_0x0019
            org.apache.http.protocol.HttpRequestExecutor r38 = new org.apache.http.protocol.HttpRequestExecutor
            r38.<init>()
        L_0x0019:
            r0 = r50
            org.apache.http.conn.HttpClientConnectionManager r8 = r0.connManager
            if (r8 != 0) goto L_0x0105
            r0 = r50
            org.apache.http.conn.socket.LayeredConnectionSocketFactory r0 = r0.sslSocketFactory
            r43 = r0
            if (r43 != 0) goto L_0x006f
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x01c3
            java.lang.String r6 = "https.protocols"
            java.lang.String r6 = java.lang.System.getProperty(r6)
            java.lang.String[] r45 = split(r6)
        L_0x0037:
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x01c7
            java.lang.String r6 = "https.cipherSuites"
            java.lang.String r6 = java.lang.System.getProperty(r6)
            java.lang.String[] r44 = split(r6)
        L_0x0047:
            r0 = r50
            javax.net.ssl.HostnameVerifier r0 = r0.hostnameVerifier
            r31 = r0
            if (r31 != 0) goto L_0x0058
            org.apache.http.conn.ssl.DefaultHostnameVerifier r31 = new org.apache.http.conn.ssl.DefaultHostnameVerifier
            r0 = r31
            r1 = r36
            r0.<init>(r1)
        L_0x0058:
            r0 = r50
            javax.net.ssl.SSLContext r6 = r0.sslContext
            if (r6 == 0) goto L_0x01cb
            org.apache.http.conn.ssl.SSLConnectionSocketFactory r43 = new org.apache.http.conn.ssl.SSLConnectionSocketFactory
            r0 = r50
            javax.net.ssl.SSLContext r6 = r0.sslContext
            r0 = r43
            r1 = r45
            r2 = r44
            r3 = r31
            r0.<init>((javax.net.ssl.SSLContext) r6, (java.lang.String[]) r1, (java.lang.String[]) r2, (javax.net.ssl.HostnameVerifier) r3)
        L_0x006f:
            org.apache.http.impl.conn.PoolingHttpClientConnectionManager r5 = new org.apache.http.impl.conn.PoolingHttpClientConnectionManager
            org.apache.http.config.RegistryBuilder r6 = org.apache.http.config.RegistryBuilder.create()
            java.lang.String r7 = "http"
            org.apache.http.conn.socket.PlainConnectionSocketFactory r11 = org.apache.http.conn.socket.PlainConnectionSocketFactory.getSocketFactory()
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r11)
            java.lang.String r7 = "https"
            r0 = r43
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r0)
            org.apache.http.config.Registry r6 = r6.build()
            r7 = 0
            r8 = 0
            r0 = r50
            org.apache.http.conn.DnsResolver r9 = r0.dnsResolver
            r0 = r50
            long r10 = r0.connTimeToLive
            r0 = r50
            java.util.concurrent.TimeUnit r15 = r0.connTimeToLiveTimeUnit
            if (r15 == 0) goto L_0x01f5
            r0 = r50
            java.util.concurrent.TimeUnit r12 = r0.connTimeToLiveTimeUnit
        L_0x009f:
            r5.<init>(r6, r7, r8, r9, r10, r12)
            r0 = r50
            org.apache.http.config.SocketConfig r6 = r0.defaultSocketConfig
            if (r6 == 0) goto L_0x00af
            r0 = r50
            org.apache.http.config.SocketConfig r6 = r0.defaultSocketConfig
            r5.setDefaultSocketConfig(r6)
        L_0x00af:
            r0 = r50
            org.apache.http.config.ConnectionConfig r6 = r0.defaultConnectionConfig
            if (r6 == 0) goto L_0x00bc
            r0 = r50
            org.apache.http.config.ConnectionConfig r6 = r0.defaultConnectionConfig
            r5.setDefaultConnectionConfig(r6)
        L_0x00bc:
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x00ea
            java.lang.String r6 = "http.keepAlive"
            java.lang.String r7 = "true"
            java.lang.String r40 = java.lang.System.getProperty(r6, r7)
            java.lang.String r6 = "true"
            r0 = r40
            boolean r6 = r6.equalsIgnoreCase(r0)
            if (r6 == 0) goto L_0x00ea
            java.lang.String r6 = "http.maxConnections"
            java.lang.String r7 = "5"
            java.lang.String r40 = java.lang.System.getProperty(r6, r7)
            int r35 = java.lang.Integer.parseInt(r40)
            r0 = r35
            r5.setDefaultMaxPerRoute(r0)
            int r6 = r35 * 2
            r5.setMaxTotal(r6)
        L_0x00ea:
            r0 = r50
            int r6 = r0.maxConnTotal
            if (r6 <= 0) goto L_0x00f7
            r0 = r50
            int r6 = r0.maxConnTotal
            r5.setMaxTotal(r6)
        L_0x00f7:
            r0 = r50
            int r6 = r0.maxConnPerRoute
            if (r6 <= 0) goto L_0x0104
            r0 = r50
            int r6 = r0.maxConnPerRoute
            r5.setDefaultMaxPerRoute(r6)
        L_0x0104:
            r8 = r5
        L_0x0105:
            r0 = r50
            org.apache.http.ConnectionReuseStrategy r9 = r0.reuseStrategy
            if (r9 != 0) goto L_0x0125
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x01fd
            java.lang.String r6 = "http.keepAlive"
            java.lang.String r7 = "true"
            java.lang.String r40 = java.lang.System.getProperty(r6, r7)
            java.lang.String r6 = "true"
            r0 = r40
            boolean r6 = r6.equalsIgnoreCase(r0)
            if (r6 == 0) goto L_0x01f9
            org.apache.http.impl.client.DefaultClientConnectionReuseStrategy r9 = org.apache.http.impl.client.DefaultClientConnectionReuseStrategy.INSTANCE
        L_0x0125:
            r0 = r50
            org.apache.http.conn.ConnectionKeepAliveStrategy r10 = r0.keepAliveStrategy
            if (r10 != 0) goto L_0x012d
            org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy r10 = org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy.INSTANCE
        L_0x012d:
            r0 = r50
            org.apache.http.client.AuthenticationStrategy r12 = r0.targetAuthStrategy
            if (r12 != 0) goto L_0x0135
            org.apache.http.impl.client.TargetAuthenticationStrategy r12 = org.apache.http.impl.client.TargetAuthenticationStrategy.INSTANCE
        L_0x0135:
            r0 = r50
            org.apache.http.client.AuthenticationStrategy r13 = r0.proxyAuthStrategy
            if (r13 != 0) goto L_0x013d
            org.apache.http.impl.client.ProxyAuthenticationStrategy r13 = org.apache.http.impl.client.ProxyAuthenticationStrategy.INSTANCE
        L_0x013d:
            r0 = r50
            org.apache.http.client.UserTokenHandler r14 = r0.userTokenHandler
            if (r14 != 0) goto L_0x014b
            r0 = r50
            boolean r6 = r0.connectionStateDisabled
            if (r6 != 0) goto L_0x0201
            org.apache.http.impl.client.DefaultUserTokenHandler r14 = org.apache.http.impl.client.DefaultUserTokenHandler.INSTANCE
        L_0x014b:
            r0 = r50
            java.lang.String r0 = r0.userAgent
            r46 = r0
            if (r46 != 0) goto L_0x016d
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x015f
            java.lang.String r6 = "http.agent"
            java.lang.String r46 = java.lang.System.getProperty(r6)
        L_0x015f:
            if (r46 != 0) goto L_0x016d
            java.lang.String r6 = "Apache-HttpClient"
            java.lang.String r7 = "org.apache.http.client"
            java.lang.Class r11 = r50.getClass()
            java.lang.String r46 = org.apache.http.util.VersionInfo.getUserAgent(r6, r7, r11)
        L_0x016d:
            org.apache.http.protocol.ImmutableHttpProcessor r11 = new org.apache.http.protocol.ImmutableHttpProcessor
            r6 = 2
            org.apache.http.HttpRequestInterceptor[] r6 = new org.apache.http.HttpRequestInterceptor[r6]
            r7 = 0
            org.apache.http.protocol.RequestTargetHost r15 = new org.apache.http.protocol.RequestTargetHost
            r15.<init>()
            r6[r7] = r15
            r7 = 1
            org.apache.http.protocol.RequestUserAgent r15 = new org.apache.http.protocol.RequestUserAgent
            r0 = r46
            r15.<init>(r0)
            r6[r7] = r15
            r11.<init>((org.apache.http.HttpRequestInterceptor[]) r6)
            r6 = r50
            r7 = r38
            org.apache.http.impl.execchain.ClientExecChain r16 = r6.createMainExec(r7, r8, r9, r10, r11, r12, r13, r14)
            r0 = r50
            r1 = r16
            org.apache.http.impl.execchain.ClientExecChain r16 = r0.decorateMainExec(r1)
            r0 = r50
            org.apache.http.protocol.HttpProcessor r0 = r0.httpprocessor
            r32 = r0
            if (r32 != 0) goto L_0x034d
            org.apache.http.protocol.HttpProcessorBuilder r4 = org.apache.http.protocol.HttpProcessorBuilder.create()
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpRequestInterceptor> r6 = r0.requestFirst
            if (r6 == 0) goto L_0x0205
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpRequestInterceptor> r6 = r0.requestFirst
            java.util.Iterator r34 = r6.iterator()
        L_0x01b1:
            boolean r6 = r34.hasNext()
            if (r6 == 0) goto L_0x0205
            java.lang.Object r33 = r34.next()
            org.apache.http.HttpRequestInterceptor r33 = (org.apache.http.HttpRequestInterceptor) r33
            r0 = r33
            r4.addFirst((org.apache.http.HttpRequestInterceptor) r0)
            goto L_0x01b1
        L_0x01c3:
            r45 = 0
            goto L_0x0037
        L_0x01c7:
            r44 = 0
            goto L_0x0047
        L_0x01cb:
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x01e6
            org.apache.http.conn.ssl.SSLConnectionSocketFactory r43 = new org.apache.http.conn.ssl.SSLConnectionSocketFactory
            javax.net.SocketFactory r6 = javax.net.ssl.SSLSocketFactory.getDefault()
            javax.net.ssl.SSLSocketFactory r6 = (javax.net.ssl.SSLSocketFactory) r6
            r0 = r43
            r1 = r45
            r2 = r44
            r3 = r31
            r0.<init>((javax.net.ssl.SSLSocketFactory) r6, (java.lang.String[]) r1, (java.lang.String[]) r2, (javax.net.ssl.HostnameVerifier) r3)
            goto L_0x006f
        L_0x01e6:
            org.apache.http.conn.ssl.SSLConnectionSocketFactory r43 = new org.apache.http.conn.ssl.SSLConnectionSocketFactory
            javax.net.ssl.SSLContext r6 = org.apache.http.ssl.SSLContexts.createDefault()
            r0 = r43
            r1 = r31
            r0.<init>((javax.net.ssl.SSLContext) r6, (javax.net.ssl.HostnameVerifier) r1)
            goto L_0x006f
        L_0x01f5:
            java.util.concurrent.TimeUnit r12 = java.util.concurrent.TimeUnit.MILLISECONDS
            goto L_0x009f
        L_0x01f9:
            org.apache.http.impl.NoConnectionReuseStrategy r9 = org.apache.http.impl.NoConnectionReuseStrategy.INSTANCE
            goto L_0x0125
        L_0x01fd:
            org.apache.http.impl.client.DefaultClientConnectionReuseStrategy r9 = org.apache.http.impl.client.DefaultClientConnectionReuseStrategy.INSTANCE
            goto L_0x0125
        L_0x0201:
            org.apache.http.impl.client.NoopUserTokenHandler r14 = org.apache.http.impl.client.NoopUserTokenHandler.INSTANCE
            goto L_0x014b
        L_0x0205:
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpResponseInterceptor> r6 = r0.responseFirst
            if (r6 == 0) goto L_0x0225
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpResponseInterceptor> r6 = r0.responseFirst
            java.util.Iterator r34 = r6.iterator()
        L_0x0213:
            boolean r6 = r34.hasNext()
            if (r6 == 0) goto L_0x0225
            java.lang.Object r33 = r34.next()
            org.apache.http.HttpResponseInterceptor r33 = (org.apache.http.HttpResponseInterceptor) r33
            r0 = r33
            r4.addFirst((org.apache.http.HttpResponseInterceptor) r0)
            goto L_0x0213
        L_0x0225:
            r6 = 6
            org.apache.http.HttpRequestInterceptor[] r6 = new org.apache.http.HttpRequestInterceptor[r6]
            r7 = 0
            org.apache.http.client.protocol.RequestDefaultHeaders r11 = new org.apache.http.client.protocol.RequestDefaultHeaders
            r0 = r50
            java.util.Collection<? extends org.apache.http.Header> r15 = r0.defaultHeaders
            r11.<init>(r15)
            r6[r7] = r11
            r7 = 1
            org.apache.http.protocol.RequestContent r11 = new org.apache.http.protocol.RequestContent
            r11.<init>()
            r6[r7] = r11
            r7 = 2
            org.apache.http.protocol.RequestTargetHost r11 = new org.apache.http.protocol.RequestTargetHost
            r11.<init>()
            r6[r7] = r11
            r7 = 3
            org.apache.http.client.protocol.RequestClientConnControl r11 = new org.apache.http.client.protocol.RequestClientConnControl
            r11.<init>()
            r6[r7] = r11
            r7 = 4
            org.apache.http.protocol.RequestUserAgent r11 = new org.apache.http.protocol.RequestUserAgent
            r0 = r46
            r11.<init>(r0)
            r6[r7] = r11
            r7 = 5
            org.apache.http.client.protocol.RequestExpectContinue r11 = new org.apache.http.client.protocol.RequestExpectContinue
            r11.<init>()
            r6[r7] = r11
            r4.addAll((org.apache.http.HttpRequestInterceptor[]) r6)
            r0 = r50
            boolean r6 = r0.cookieManagementDisabled
            if (r6 != 0) goto L_0x026f
            org.apache.http.client.protocol.RequestAddCookies r6 = new org.apache.http.client.protocol.RequestAddCookies
            r6.<init>()
            r4.add((org.apache.http.HttpRequestInterceptor) r6)
        L_0x026f:
            r0 = r50
            boolean r6 = r0.contentCompressionDisabled
            if (r6 != 0) goto L_0x0297
            r0 = r50
            java.util.Map<java.lang.String, org.apache.http.client.entity.InputStreamFactory> r6 = r0.contentDecoderMap
            if (r6 == 0) goto L_0x02eb
            java.util.ArrayList r28 = new java.util.ArrayList
            r0 = r50
            java.util.Map<java.lang.String, org.apache.http.client.entity.InputStreamFactory> r6 = r0.contentDecoderMap
            java.util.Set r6 = r6.keySet()
            r0 = r28
            r0.<init>(r6)
            java.util.Collections.sort(r28)
            org.apache.http.client.protocol.RequestAcceptEncoding r6 = new org.apache.http.client.protocol.RequestAcceptEncoding
            r0 = r28
            r6.<init>(r0)
            r4.add((org.apache.http.HttpRequestInterceptor) r6)
        L_0x0297:
            r0 = r50
            boolean r6 = r0.authCachingDisabled
            if (r6 != 0) goto L_0x02a5
            org.apache.http.client.protocol.RequestAuthCache r6 = new org.apache.http.client.protocol.RequestAuthCache
            r6.<init>()
            r4.add((org.apache.http.HttpRequestInterceptor) r6)
        L_0x02a5:
            r0 = r50
            boolean r6 = r0.cookieManagementDisabled
            if (r6 != 0) goto L_0x02b3
            org.apache.http.client.protocol.ResponseProcessCookies r6 = new org.apache.http.client.protocol.ResponseProcessCookies
            r6.<init>()
            r4.add((org.apache.http.HttpResponseInterceptor) r6)
        L_0x02b3:
            r0 = r50
            boolean r6 = r0.contentCompressionDisabled
            if (r6 != 0) goto L_0x0300
            r0 = r50
            java.util.Map<java.lang.String, org.apache.http.client.entity.InputStreamFactory> r6 = r0.contentDecoderMap
            if (r6 == 0) goto L_0x0320
            org.apache.http.config.RegistryBuilder r25 = org.apache.http.config.RegistryBuilder.create()
            r0 = r50
            java.util.Map<java.lang.String, org.apache.http.client.entity.InputStreamFactory> r6 = r0.contentDecoderMap
            java.util.Set r6 = r6.entrySet()
            java.util.Iterator r34 = r6.iterator()
        L_0x02cf:
            boolean r6 = r34.hasNext()
            if (r6 == 0) goto L_0x02f4
            java.lang.Object r29 = r34.next()
            java.util.Map$Entry r29 = (java.util.Map.Entry) r29
            java.lang.Object r6 = r29.getKey()
            java.lang.String r6 = (java.lang.String) r6
            java.lang.Object r7 = r29.getValue()
            r0 = r25
            r0.register(r6, r7)
            goto L_0x02cf
        L_0x02eb:
            org.apache.http.client.protocol.RequestAcceptEncoding r6 = new org.apache.http.client.protocol.RequestAcceptEncoding
            r6.<init>()
            r4.add((org.apache.http.HttpRequestInterceptor) r6)
            goto L_0x0297
        L_0x02f4:
            org.apache.http.client.protocol.ResponseContentEncoding r6 = new org.apache.http.client.protocol.ResponseContentEncoding
            org.apache.http.config.Registry r7 = r25.build()
            r6.<init>((org.apache.http.config.Lookup<org.apache.http.client.entity.InputStreamFactory>) r7)
            r4.add((org.apache.http.HttpResponseInterceptor) r6)
        L_0x0300:
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpRequestInterceptor> r6 = r0.requestLast
            if (r6 == 0) goto L_0x0329
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpRequestInterceptor> r6 = r0.requestLast
            java.util.Iterator r34 = r6.iterator()
        L_0x030e:
            boolean r6 = r34.hasNext()
            if (r6 == 0) goto L_0x0329
            java.lang.Object r33 = r34.next()
            org.apache.http.HttpRequestInterceptor r33 = (org.apache.http.HttpRequestInterceptor) r33
            r0 = r33
            r4.addLast((org.apache.http.HttpRequestInterceptor) r0)
            goto L_0x030e
        L_0x0320:
            org.apache.http.client.protocol.ResponseContentEncoding r6 = new org.apache.http.client.protocol.ResponseContentEncoding
            r6.<init>()
            r4.add((org.apache.http.HttpResponseInterceptor) r6)
            goto L_0x0300
        L_0x0329:
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpResponseInterceptor> r6 = r0.responseLast
            if (r6 == 0) goto L_0x0349
            r0 = r50
            java.util.LinkedList<org.apache.http.HttpResponseInterceptor> r6 = r0.responseLast
            java.util.Iterator r34 = r6.iterator()
        L_0x0337:
            boolean r6 = r34.hasNext()
            if (r6 == 0) goto L_0x0349
            java.lang.Object r33 = r34.next()
            org.apache.http.HttpResponseInterceptor r33 = (org.apache.http.HttpResponseInterceptor) r33
            r0 = r33
            r4.addLast((org.apache.http.HttpResponseInterceptor) r0)
            goto L_0x0337
        L_0x0349:
            org.apache.http.protocol.HttpProcessor r32 = r4.build()
        L_0x034d:
            org.apache.http.impl.execchain.ProtocolExec r30 = new org.apache.http.impl.execchain.ProtocolExec
            r0 = r30
            r1 = r16
            r2 = r32
            r0.<init>(r1, r2)
            r0 = r50
            r1 = r30
            org.apache.http.impl.execchain.ClientExecChain r16 = r0.decorateProtocolExec(r1)
            r0 = r50
            boolean r6 = r0.automaticRetriesDisabled
            if (r6 != 0) goto L_0x037d
            r0 = r50
            org.apache.http.client.HttpRequestRetryHandler r0 = r0.retryHandler
            r39 = r0
            if (r39 != 0) goto L_0x0370
            org.apache.http.impl.client.DefaultHttpRequestRetryHandler r39 = org.apache.http.impl.client.DefaultHttpRequestRetryHandler.INSTANCE
        L_0x0370:
            org.apache.http.impl.execchain.RetryExec r30 = new org.apache.http.impl.execchain.RetryExec
            r0 = r30
            r1 = r16
            r2 = r39
            r0.<init>(r1, r2)
            r16 = r30
        L_0x037d:
            r0 = r50
            org.apache.http.conn.routing.HttpRoutePlanner r0 = r0.routePlanner
            r18 = r0
            if (r18 != 0) goto L_0x03a2
            r0 = r50
            org.apache.http.conn.SchemePortResolver r0 = r0.schemePortResolver
            r41 = r0
            if (r41 != 0) goto L_0x038f
            org.apache.http.impl.conn.DefaultSchemePortResolver r41 = org.apache.http.impl.conn.DefaultSchemePortResolver.INSTANCE
        L_0x038f:
            r0 = r50
            org.apache.http.HttpHost r6 = r0.proxy
            if (r6 == 0) goto L_0x04eb
            org.apache.http.impl.conn.DefaultProxyRoutePlanner r18 = new org.apache.http.impl.conn.DefaultProxyRoutePlanner
            r0 = r50
            org.apache.http.HttpHost r6 = r0.proxy
            r0 = r18
            r1 = r41
            r0.<init>(r6, r1)
        L_0x03a2:
            r0 = r50
            org.apache.http.client.ServiceUnavailableRetryStrategy r0 = r0.serviceUnavailStrategy
            r42 = r0
            if (r42 == 0) goto L_0x03b7
            org.apache.http.impl.execchain.ServiceUnavailableRetryExec r30 = new org.apache.http.impl.execchain.ServiceUnavailableRetryExec
            r0 = r30
            r1 = r16
            r2 = r42
            r0.<init>(r1, r2)
            r16 = r30
        L_0x03b7:
            r0 = r50
            boolean r6 = r0.redirectHandlingDisabled
            if (r6 != 0) goto L_0x03d6
            r0 = r50
            org.apache.http.client.RedirectStrategy r0 = r0.redirectStrategy
            r37 = r0
            if (r37 != 0) goto L_0x03c7
            org.apache.http.impl.client.DefaultRedirectStrategy r37 = org.apache.http.impl.client.DefaultRedirectStrategy.INSTANCE
        L_0x03c7:
            org.apache.http.impl.execchain.RedirectExec r30 = new org.apache.http.impl.execchain.RedirectExec
            r0 = r30
            r1 = r16
            r2 = r18
            r3 = r37
            r0.<init>(r1, r2, r3)
            r16 = r30
        L_0x03d6:
            r0 = r50
            org.apache.http.client.BackoffManager r6 = r0.backoffManager
            if (r6 == 0) goto L_0x03f5
            r0 = r50
            org.apache.http.client.ConnectionBackoffStrategy r6 = r0.connectionBackoffStrategy
            if (r6 == 0) goto L_0x03f5
            org.apache.http.impl.execchain.BackoffStrategyExec r30 = new org.apache.http.impl.execchain.BackoffStrategyExec
            r0 = r50
            org.apache.http.client.ConnectionBackoffStrategy r6 = r0.connectionBackoffStrategy
            r0 = r50
            org.apache.http.client.BackoffManager r7 = r0.backoffManager
            r0 = r30
            r1 = r16
            r0.<init>(r1, r6, r7)
            r16 = r30
        L_0x03f5:
            r0 = r50
            org.apache.http.config.Lookup<org.apache.http.auth.AuthSchemeProvider> r0 = r0.authSchemeRegistry
            r20 = r0
            if (r20 != 0) goto L_0x043c
            org.apache.http.config.RegistryBuilder r6 = org.apache.http.config.RegistryBuilder.create()
            java.lang.String r7 = "Basic"
            org.apache.http.impl.auth.BasicSchemeFactory r11 = new org.apache.http.impl.auth.BasicSchemeFactory
            r11.<init>()
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r11)
            java.lang.String r7 = "Digest"
            org.apache.http.impl.auth.DigestSchemeFactory r11 = new org.apache.http.impl.auth.DigestSchemeFactory
            r11.<init>()
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r11)
            java.lang.String r7 = "NTLM"
            org.apache.http.impl.auth.NTLMSchemeFactory r11 = new org.apache.http.impl.auth.NTLMSchemeFactory
            r11.<init>()
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r11)
            java.lang.String r7 = "Negotiate"
            org.apache.http.impl.auth.SPNegoSchemeFactory r11 = new org.apache.http.impl.auth.SPNegoSchemeFactory
            r11.<init>()
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r11)
            java.lang.String r7 = "Kerberos"
            org.apache.http.impl.auth.KerberosSchemeFactory r11 = new org.apache.http.impl.auth.KerberosSchemeFactory
            r11.<init>()
            org.apache.http.config.RegistryBuilder r6 = r6.register(r7, r11)
            org.apache.http.config.Registry r20 = r6.build()
        L_0x043c:
            r0 = r50
            org.apache.http.config.Lookup<org.apache.http.cookie.CookieSpecProvider> r0 = r0.cookieSpecRegistry
            r19 = r0
            if (r19 != 0) goto L_0x0448
            org.apache.http.config.Lookup r19 = org.apache.http.impl.client.CookieSpecRegistries.createDefault(r36)
        L_0x0448:
            r0 = r50
            org.apache.http.client.CookieStore r0 = r0.cookieStore
            r21 = r0
            if (r21 != 0) goto L_0x0455
            org.apache.http.impl.client.BasicCookieStore r21 = new org.apache.http.impl.client.BasicCookieStore
            r21.<init>()
        L_0x0455:
            r0 = r50
            org.apache.http.client.CredentialsProvider r0 = r0.credentialsProvider
            r22 = r0
            if (r22 != 0) goto L_0x0468
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x050b
            org.apache.http.impl.client.SystemDefaultCredentialsProvider r22 = new org.apache.http.impl.client.SystemDefaultCredentialsProvider
            r22.<init>()
        L_0x0468:
            r0 = r50
            java.util.List<java.io.Closeable> r6 = r0.closeables
            if (r6 == 0) goto L_0x0512
            java.util.ArrayList r24 = new java.util.ArrayList
            r0 = r50
            java.util.List<java.io.Closeable> r6 = r0.closeables
            r0 = r24
            r0.<init>(r6)
        L_0x0479:
            r0 = r50
            boolean r6 = r0.connManagerShared
            if (r6 != 0) goto L_0x04d7
            if (r24 != 0) goto L_0x0489
            java.util.ArrayList r24 = new java.util.ArrayList
            r6 = 1
            r0 = r24
            r0.<init>(r6)
        L_0x0489:
            r26 = r8
            r0 = r50
            boolean r6 = r0.evictExpiredConnections
            if (r6 != 0) goto L_0x0497
            r0 = r50
            boolean r6 = r0.evictIdleConnections
            if (r6 == 0) goto L_0x04c9
        L_0x0497:
            org.apache.http.impl.client.IdleConnectionEvictor r27 = new org.apache.http.impl.client.IdleConnectionEvictor
            r0 = r50
            long r6 = r0.maxIdleTime
            r48 = 0
            int r6 = (r6 > r48 ? 1 : (r6 == r48 ? 0 : -1))
            if (r6 <= 0) goto L_0x0516
            r0 = r50
            long r6 = r0.maxIdleTime
        L_0x04a7:
            r0 = r50
            java.util.concurrent.TimeUnit r11 = r0.maxIdleTimeUnit
            if (r11 == 0) goto L_0x0519
            r0 = r50
            java.util.concurrent.TimeUnit r11 = r0.maxIdleTimeUnit
        L_0x04b1:
            r0 = r27
            r1 = r26
            r0.<init>(r1, r6, r11)
            org.apache.http.impl.client.HttpClientBuilder$1 r6 = new org.apache.http.impl.client.HttpClientBuilder$1
            r0 = r50
            r1 = r27
            r6.<init>(r1)
            r0 = r24
            r0.add(r6)
            r27.start()
        L_0x04c9:
            org.apache.http.impl.client.HttpClientBuilder$2 r6 = new org.apache.http.impl.client.HttpClientBuilder$2
            r0 = r50
            r1 = r26
            r6.<init>(r1)
            r0 = r24
            r0.add(r6)
        L_0x04d7:
            org.apache.http.impl.client.InternalHttpClient r15 = new org.apache.http.impl.client.InternalHttpClient
            r0 = r50
            org.apache.http.client.config.RequestConfig r6 = r0.defaultRequestConfig
            if (r6 == 0) goto L_0x051c
            r0 = r50
            org.apache.http.client.config.RequestConfig r0 = r0.defaultRequestConfig
            r23 = r0
        L_0x04e5:
            r17 = r8
            r15.<init>(r16, r17, r18, r19, r20, r21, r22, r23, r24)
            return r15
        L_0x04eb:
            r0 = r50
            boolean r6 = r0.systemProperties
            if (r6 == 0) goto L_0x0500
            org.apache.http.impl.conn.SystemDefaultRoutePlanner r18 = new org.apache.http.impl.conn.SystemDefaultRoutePlanner
            java.net.ProxySelector r6 = java.net.ProxySelector.getDefault()
            r0 = r18
            r1 = r41
            r0.<init>(r1, r6)
            goto L_0x03a2
        L_0x0500:
            org.apache.http.impl.conn.DefaultRoutePlanner r18 = new org.apache.http.impl.conn.DefaultRoutePlanner
            r0 = r18
            r1 = r41
            r0.<init>(r1)
            goto L_0x03a2
        L_0x050b:
            org.apache.http.impl.client.BasicCredentialsProvider r22 = new org.apache.http.impl.client.BasicCredentialsProvider
            r22.<init>()
            goto L_0x0468
        L_0x0512:
            r24 = 0
            goto L_0x0479
        L_0x0516:
            r6 = 10
            goto L_0x04a7
        L_0x0519:
            java.util.concurrent.TimeUnit r11 = java.util.concurrent.TimeUnit.SECONDS
            goto L_0x04b1
        L_0x051c:
            org.apache.http.client.config.RequestConfig r23 = org.apache.http.client.config.RequestConfig.DEFAULT
            goto L_0x04e5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.client.HttpClientBuilder.build():org.apache.http.impl.client.CloseableHttpClient");
    }
}
