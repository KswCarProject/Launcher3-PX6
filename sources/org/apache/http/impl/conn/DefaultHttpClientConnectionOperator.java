package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionOperator;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpClientConnectionOperator implements HttpClientConnectionOperator {
    static final String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
    private final DnsResolver dnsResolver;
    private final Log log = LogFactory.getLog(getClass());
    private final SchemePortResolver schemePortResolver;
    private final Lookup<ConnectionSocketFactory> socketFactoryRegistry;

    public DefaultHttpClientConnectionOperator(Lookup<ConnectionSocketFactory> socketFactoryRegistry2, SchemePortResolver schemePortResolver2, DnsResolver dnsResolver2) {
        Args.notNull(socketFactoryRegistry2, "Socket factory registry");
        this.socketFactoryRegistry = socketFactoryRegistry2;
        this.schemePortResolver = schemePortResolver2 == null ? DefaultSchemePortResolver.INSTANCE : schemePortResolver2;
        this.dnsResolver = dnsResolver2 == null ? SystemDefaultDnsResolver.INSTANCE : dnsResolver2;
    }

    private Lookup<ConnectionSocketFactory> getSocketFactoryRegistry(HttpContext context) {
        Lookup<ConnectionSocketFactory> reg = (Lookup) context.getAttribute("http.socket-factory-registry");
        if (reg == null) {
            return this.socketFactoryRegistry;
        }
        return reg;
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0176 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(org.apache.http.conn.ManagedHttpClientConnection r19, org.apache.http.HttpHost r20, java.net.InetSocketAddress r21, int r22, org.apache.http.config.SocketConfig r23, org.apache.http.protocol.HttpContext r24) throws java.io.IOException {
        /*
            r18 = this;
            r0 = r18
            r1 = r24
            org.apache.http.config.Lookup r17 = r0.getSocketFactoryRegistry(r1)
            java.lang.String r3 = r20.getSchemeName()
            r0 = r17
            java.lang.Object r2 = r0.lookup(r3)
            org.apache.http.conn.socket.ConnectionSocketFactory r2 = (org.apache.http.conn.socket.ConnectionSocketFactory) r2
            if (r2 != 0) goto L_0x0033
            org.apache.http.conn.UnsupportedSchemeException r3 = new org.apache.http.conn.UnsupportedSchemeException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = r20.getSchemeName()
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.String r7 = " protocol is not supported"
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.String r5 = r5.toString()
            r3.<init>(r5)
            throw r3
        L_0x0033:
            java.net.InetAddress r3 = r20.getAddress()
            if (r3 == 0) goto L_0x0107
            r3 = 1
            java.net.InetAddress[] r10 = new java.net.InetAddress[r3]
            r3 = 0
            java.net.InetAddress r5 = r20.getAddress()
            r10[r3] = r5
        L_0x0043:
            r0 = r18
            org.apache.http.conn.SchemePortResolver r3 = r0.schemePortResolver
            r0 = r20
            int r16 = r3.resolve(r0)
            r12 = 0
        L_0x004e:
            int r3 = r10.length
            if (r12 >= r3) goto L_0x0106
            r9 = r10[r12]
            int r3 = r10.length
            int r3 = r3 + -1
            if (r12 != r3) goto L_0x0115
            r13 = 1
        L_0x0059:
            r0 = r24
            java.net.Socket r4 = r2.createSocket(r0)
            int r3 = r23.getSoTimeout()
            r4.setSoTimeout(r3)
            boolean r3 = r23.isSoReuseAddress()
            r4.setReuseAddress(r3)
            boolean r3 = r23.isTcpNoDelay()
            r4.setTcpNoDelay(r3)
            boolean r3 = r23.isSoKeepAlive()
            r4.setKeepAlive(r3)
            int r3 = r23.getRcvBufSize()
            if (r3 <= 0) goto L_0x0088
            int r3 = r23.getRcvBufSize()
            r4.setReceiveBufferSize(r3)
        L_0x0088:
            int r3 = r23.getSndBufSize()
            if (r3 <= 0) goto L_0x0095
            int r3 = r23.getSndBufSize()
            r4.setSendBufferSize(r3)
        L_0x0095:
            int r14 = r23.getSoLinger()
            if (r14 < 0) goto L_0x009f
            r3 = 1
            r4.setSoLinger(r3, r14)
        L_0x009f:
            r0 = r19
            r0.bind(r4)
            java.net.InetSocketAddress r6 = new java.net.InetSocketAddress
            r0 = r16
            r6.<init>(r9, r0)
            r0 = r18
            org.apache.commons.logging.Log r3 = r0.log
            boolean r3 = r3.isDebugEnabled()
            if (r3 == 0) goto L_0x00cf
            r0 = r18
            org.apache.commons.logging.Log r3 = r0.log
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Connecting to "
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            r3.debug(r5)
        L_0x00cf:
            r3 = r22
            r5 = r20
            r7 = r21
            r8 = r24
            java.net.Socket r4 = r2.connectSocket(r3, r4, r5, r6, r7, r8)     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            r0 = r19
            r0.bind(r4)     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            r0 = r18
            org.apache.commons.logging.Log r3 = r0.log     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            boolean r3 = r3.isDebugEnabled()     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            if (r3 == 0) goto L_0x0106
            r0 = r18
            org.apache.commons.logging.Log r3 = r0.log     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            r5.<init>()     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            java.lang.String r7 = "Connection established "
            java.lang.StringBuilder r5 = r5.append(r7)     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            r0 = r19
            java.lang.StringBuilder r5 = r5.append(r0)     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            java.lang.String r5 = r5.toString()     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
            r3.debug(r5)     // Catch:{ SocketTimeoutException -> 0x0118, ConnectException -> 0x0123, NoRouteToHostException -> 0x0142 }
        L_0x0106:
            return
        L_0x0107:
            r0 = r18
            org.apache.http.conn.DnsResolver r3 = r0.dnsResolver
            java.lang.String r5 = r20.getHostName()
            java.net.InetAddress[] r10 = r3.resolve(r5)
            goto L_0x0043
        L_0x0115:
            r13 = 0
            goto L_0x0059
        L_0x0118:
            r11 = move-exception
            if (r13 == 0) goto L_0x0146
            org.apache.http.conn.ConnectTimeoutException r3 = new org.apache.http.conn.ConnectTimeoutException
            r0 = r20
            r3.<init>(r11, r0, r10)
            throw r3
        L_0x0123:
            r11 = move-exception
            if (r13 == 0) goto L_0x0146
            java.lang.String r15 = r11.getMessage()
            java.lang.String r3 = "Connection timed out"
            boolean r3 = r3.equals(r15)
            if (r3 == 0) goto L_0x013a
            org.apache.http.conn.ConnectTimeoutException r3 = new org.apache.http.conn.ConnectTimeoutException
            r0 = r20
            r3.<init>(r11, r0, r10)
            throw r3
        L_0x013a:
            org.apache.http.conn.HttpHostConnectException r3 = new org.apache.http.conn.HttpHostConnectException
            r0 = r20
            r3.<init>(r11, r0, r10)
            throw r3
        L_0x0142:
            r11 = move-exception
            if (r13 == 0) goto L_0x0146
            throw r11
        L_0x0146:
            r0 = r18
            org.apache.commons.logging.Log r3 = r0.log
            boolean r3 = r3.isDebugEnabled()
            if (r3 == 0) goto L_0x0176
            r0 = r18
            org.apache.commons.logging.Log r3 = r0.log
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Connect to "
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r7 = " timed out. "
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.String r7 = "Connection will be retried using another IP address"
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.String r5 = r5.toString()
            r3.debug(r5)
        L_0x0176:
            int r12 = r12 + 1
            goto L_0x004e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(org.apache.http.conn.ManagedHttpClientConnection, org.apache.http.HttpHost, java.net.InetSocketAddress, int, org.apache.http.config.SocketConfig, org.apache.http.protocol.HttpContext):void");
    }

    public void upgrade(ManagedHttpClientConnection conn, HttpHost host, HttpContext context) throws IOException {
        ConnectionSocketFactory sf = getSocketFactoryRegistry(HttpClientContext.adapt(context)).lookup(host.getSchemeName());
        if (sf == null) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
        } else if (!(sf instanceof LayeredConnectionSocketFactory)) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol does not support connection upgrade");
        } else {
            conn.bind(((LayeredConnectionSocketFactory) sf).createLayeredSocket(conn.getSocket(), host.getHostName(), this.schemePortResolver.resolve(host), context));
        }
    }
}
