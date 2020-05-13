package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Deprecated
public class DefaultClientConnectionOperator implements ClientConnectionOperator {
    protected final DnsResolver dnsResolver;
    private final Log log = LogFactory.getLog(getClass());
    protected final SchemeRegistry schemeRegistry;

    public DefaultClientConnectionOperator(SchemeRegistry schemes) {
        Args.notNull(schemes, "Scheme registry");
        this.schemeRegistry = schemes;
        this.dnsResolver = new SystemDefaultDnsResolver();
    }

    public DefaultClientConnectionOperator(SchemeRegistry schemes, DnsResolver dnsResolver2) {
        Args.notNull(schemes, "Scheme registry");
        Args.notNull(dnsResolver2, "DNS resolver");
        this.schemeRegistry = schemes;
        this.dnsResolver = dnsResolver2;
    }

    public OperatedClientConnection createConnection() {
        return new DefaultClientConnection();
    }

    private SchemeRegistry getSchemeRegistry(HttpContext context) {
        SchemeRegistry reg = (SchemeRegistry) context.getAttribute(ClientContext.SCHEME_REGISTRY);
        if (reg == null) {
            return this.schemeRegistry;
        }
        return reg;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x011f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void openConnection(org.apache.http.conn.OperatedClientConnection r20, org.apache.http.HttpHost r21, java.net.InetAddress r22, org.apache.http.protocol.HttpContext r23, org.apache.http.params.HttpParams r24) throws java.io.IOException {
        /*
            r19 = this;
            java.lang.String r16 = "Connection"
            r0 = r20
            r1 = r16
            org.apache.http.util.Args.notNull(r0, r1)
            java.lang.String r16 = "Target host"
            r0 = r21
            r1 = r16
            org.apache.http.util.Args.notNull(r0, r1)
            java.lang.String r16 = "HTTP parameters"
            r0 = r24
            r1 = r16
            org.apache.http.util.Args.notNull(r0, r1)
            boolean r16 = r20.isOpen()
            if (r16 != 0) goto L_0x00db
            r16 = 1
        L_0x0023:
            java.lang.String r17 = "Connection must not be open"
            org.apache.http.util.Asserts.check(r16, r17)
            r0 = r19
            r1 = r23
            org.apache.http.conn.scheme.SchemeRegistry r11 = r0.getSchemeRegistry(r1)
            java.lang.String r16 = r21.getSchemeName()
            r0 = r16
            org.apache.http.conn.scheme.Scheme r13 = r11.getScheme((java.lang.String) r0)
            org.apache.http.conn.scheme.SchemeSocketFactory r14 = r13.getSchemeSocketFactory()
            java.lang.String r16 = r21.getHostName()
            r0 = r19
            r1 = r16
            java.net.InetAddress[] r4 = r0.resolveHostname(r1)
            int r16 = r21.getPort()
            r0 = r16
            int r10 = r13.resolvePort(r0)
            r7 = 0
        L_0x0055:
            int r0 = r4.length
            r16 = r0
            r0 = r16
            if (r7 >= r0) goto L_0x00da
            r3 = r4[r7]
            int r0 = r4.length
            r16 = r0
            int r16 = r16 + -1
            r0 = r16
            if (r7 != r0) goto L_0x00df
            r8 = 1
        L_0x0068:
            r0 = r24
            java.net.Socket r15 = r14.createSocket(r0)
            r0 = r20
            r1 = r21
            r0.opening(r15, r1)
            org.apache.http.conn.HttpInetSocketAddress r12 = new org.apache.http.conn.HttpInetSocketAddress
            r0 = r21
            r12.<init>(r0, r3, r10)
            r9 = 0
            if (r22 == 0) goto L_0x008a
            java.net.InetSocketAddress r9 = new java.net.InetSocketAddress
            r16 = 0
            r0 = r22
            r1 = r16
            r9.<init>(r0, r1)
        L_0x008a:
            r0 = r19
            org.apache.commons.logging.Log r0 = r0.log
            r16 = r0
            boolean r16 = r16.isDebugEnabled()
            if (r16 == 0) goto L_0x00b4
            r0 = r19
            org.apache.commons.logging.Log r0 = r0.log
            r16 = r0
            java.lang.StringBuilder r17 = new java.lang.StringBuilder
            r17.<init>()
            java.lang.String r18 = "Connecting to "
            java.lang.StringBuilder r17 = r17.append(r18)
            r0 = r17
            java.lang.StringBuilder r17 = r0.append(r12)
            java.lang.String r17 = r17.toString()
            r16.debug(r17)
        L_0x00b4:
            r0 = r24
            java.net.Socket r5 = r14.connectSocket(r15, r12, r9, r0)     // Catch:{ ConnectException -> 0x00e1, ConnectTimeoutException -> 0x00e5 }
            if (r15 == r5) goto L_0x00c4
            r15 = r5
            r0 = r20
            r1 = r21
            r0.opening(r15, r1)     // Catch:{ ConnectException -> 0x00e1, ConnectTimeoutException -> 0x00e5 }
        L_0x00c4:
            r0 = r19
            r1 = r23
            r2 = r24
            r0.prepareSocket(r15, r1, r2)     // Catch:{ ConnectException -> 0x00e1, ConnectTimeoutException -> 0x00e5 }
            boolean r16 = r14.isSecure(r15)     // Catch:{ ConnectException -> 0x00e1, ConnectTimeoutException -> 0x00e5 }
            r0 = r20
            r1 = r16
            r2 = r24
            r0.openCompleted(r1, r2)     // Catch:{ ConnectException -> 0x00e1, ConnectTimeoutException -> 0x00e5 }
        L_0x00da:
            return
        L_0x00db:
            r16 = 0
            goto L_0x0023
        L_0x00df:
            r8 = 0
            goto L_0x0068
        L_0x00e1:
            r6 = move-exception
            if (r8 == 0) goto L_0x00e9
            throw r6
        L_0x00e5:
            r6 = move-exception
            if (r8 == 0) goto L_0x00e9
            throw r6
        L_0x00e9:
            r0 = r19
            org.apache.commons.logging.Log r0 = r0.log
            r16 = r0
            boolean r16 = r16.isDebugEnabled()
            if (r16 == 0) goto L_0x011f
            r0 = r19
            org.apache.commons.logging.Log r0 = r0.log
            r16 = r0
            java.lang.StringBuilder r17 = new java.lang.StringBuilder
            r17.<init>()
            java.lang.String r18 = "Connect to "
            java.lang.StringBuilder r17 = r17.append(r18)
            r0 = r17
            java.lang.StringBuilder r17 = r0.append(r12)
            java.lang.String r18 = " timed out. "
            java.lang.StringBuilder r17 = r17.append(r18)
            java.lang.String r18 = "Connection will be retried using another IP address"
            java.lang.StringBuilder r17 = r17.append(r18)
            java.lang.String r17 = r17.toString()
            r16.debug(r17)
        L_0x011f:
            int r7 = r7 + 1
            goto L_0x0055
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.DefaultClientConnectionOperator.openConnection(org.apache.http.conn.OperatedClientConnection, org.apache.http.HttpHost, java.net.InetAddress, org.apache.http.protocol.HttpContext, org.apache.http.params.HttpParams):void");
    }

    public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params) throws IOException {
        Args.notNull(conn, "Connection");
        Args.notNull(target, "Target host");
        Args.notNull(params, "Parameters");
        Asserts.check(conn.isOpen(), "Connection must be open");
        Scheme schm = getSchemeRegistry(context).getScheme(target.getSchemeName());
        Asserts.check(schm.getSchemeSocketFactory() instanceof SchemeLayeredSocketFactory, "Socket factory must implement SchemeLayeredSocketFactory");
        SchemeLayeredSocketFactory lsf = (SchemeLayeredSocketFactory) schm.getSchemeSocketFactory();
        Socket sock = lsf.createLayeredSocket(conn.getSocket(), target.getHostName(), schm.resolvePort(target.getPort()), params);
        prepareSocket(sock, context, params);
        conn.update(sock, target, lsf.isSecure(sock), params);
    }

    /* access modifiers changed from: protected */
    public void prepareSocket(Socket sock, HttpContext context, HttpParams params) throws IOException {
        sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
        sock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        int linger = HttpConnectionParams.getLinger(params);
        if (linger >= 0) {
            sock.setSoLinger(linger > 0, linger);
        }
    }

    /* access modifiers changed from: protected */
    public InetAddress[] resolveHostname(String host) throws UnknownHostException {
        return this.dnsResolver.resolve(host);
    }
}
