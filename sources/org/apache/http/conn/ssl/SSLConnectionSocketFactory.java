package org.apache.http.conn.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(threading = ThreadingBehavior.SAFE)
public class SSLConnectionSocketFactory implements LayeredConnectionSocketFactory {
    @Deprecated
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = AllowAllHostnameVerifier.INSTANCE;
    @Deprecated
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = BrowserCompatHostnameVerifier.INSTANCE;
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    @Deprecated
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = StrictHostnameVerifier.INSTANCE;
    public static final String TLS = "TLS";
    private final HostnameVerifier hostnameVerifier;
    private final Log log;
    private final SSLSocketFactory socketfactory;
    private final String[] supportedCipherSuites;
    private final String[] supportedProtocols;

    public static HostnameVerifier getDefaultHostnameVerifier() {
        return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
    }

    public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
        return new SSLConnectionSocketFactory(SSLContexts.createDefault(), getDefaultHostnameVerifier());
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    public static SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new SSLConnectionSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), getDefaultHostnameVerifier());
    }

    public SSLConnectionSocketFactory(SSLContext sslContext) {
        this(sslContext, getDefaultHostnameVerifier());
    }

    @Deprecated
    public SSLConnectionSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier2) {
        this(((SSLContext) Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[]) null, (String[]) null, hostnameVerifier2);
    }

    @Deprecated
    public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols2, String[] supportedCipherSuites2, X509HostnameVerifier hostnameVerifier2) {
        this(((SSLContext) Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols2, supportedCipherSuites2, hostnameVerifier2);
    }

    @Deprecated
    public SSLConnectionSocketFactory(SSLSocketFactory socketfactory2, X509HostnameVerifier hostnameVerifier2) {
        this(socketfactory2, (String[]) null, (String[]) null, hostnameVerifier2);
    }

    @Deprecated
    public SSLConnectionSocketFactory(SSLSocketFactory socketfactory2, String[] supportedProtocols2, String[] supportedCipherSuites2, X509HostnameVerifier hostnameVerifier2) {
        this(socketfactory2, supportedProtocols2, supportedCipherSuites2, (HostnameVerifier) hostnameVerifier2);
    }

    public SSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier2) {
        this(((SSLContext) Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[]) null, (String[]) null, hostnameVerifier2);
    }

    public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols2, String[] supportedCipherSuites2, HostnameVerifier hostnameVerifier2) {
        this(((SSLContext) Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols2, supportedCipherSuites2, hostnameVerifier2);
    }

    public SSLConnectionSocketFactory(SSLSocketFactory socketfactory2, HostnameVerifier hostnameVerifier2) {
        this(socketfactory2, (String[]) null, (String[]) null, hostnameVerifier2);
    }

    public SSLConnectionSocketFactory(SSLSocketFactory socketfactory2, String[] supportedProtocols2, String[] supportedCipherSuites2, HostnameVerifier hostnameVerifier2) {
        this.log = LogFactory.getLog(getClass());
        this.socketfactory = (SSLSocketFactory) Args.notNull(socketfactory2, "SSL socket factory");
        this.supportedProtocols = supportedProtocols2;
        this.supportedCipherSuites = supportedCipherSuites2;
        this.hostnameVerifier = hostnameVerifier2 == null ? getDefaultHostnameVerifier() : hostnameVerifier2;
    }

    /* access modifiers changed from: protected */
    public void prepareSocket(SSLSocket socket) throws IOException {
    }

    public Socket createSocket(HttpContext context) throws IOException {
        return SocketFactory.getDefault().createSocket();
    }

    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        Args.notNull(host, "HTTP host");
        Args.notNull(remoteAddress, "Remote address");
        Socket sock = socket != null ? socket : createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        if (connectTimeout > 0) {
            try {
                if (sock.getSoTimeout() == 0) {
                    sock.setSoTimeout(connectTimeout);
                }
            } catch (IOException ex) {
                try {
                    sock.close();
                } catch (IOException e) {
                }
                throw ex;
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Connecting socket to " + remoteAddress + " with timeout " + connectTimeout);
        }
        sock.connect(remoteAddress, connectTimeout);
        if (!(sock instanceof SSLSocket)) {
            return createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
        }
        SSLSocket sslsock = (SSLSocket) sock;
        this.log.debug("Starting handshake");
        sslsock.startHandshake();
        verifyHostname(sslsock, host.getHostName());
        return sock;
    }

    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
        SSLSocket sslsock = (SSLSocket) this.socketfactory.createSocket(socket, target, port, true);
        if (this.supportedProtocols != null) {
            sslsock.setEnabledProtocols(this.supportedProtocols);
        } else {
            String[] allProtocols = sslsock.getEnabledProtocols();
            List<String> enabledProtocols = new ArrayList<>(allProtocols.length);
            for (String protocol : allProtocols) {
                if (!protocol.startsWith("SSL")) {
                    enabledProtocols.add(protocol);
                }
            }
            if (!enabledProtocols.isEmpty()) {
                sslsock.setEnabledProtocols((String[]) enabledProtocols.toArray(new String[enabledProtocols.size()]));
            }
        }
        if (this.supportedCipherSuites != null) {
            sslsock.setEnabledCipherSuites(this.supportedCipherSuites);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Enabled protocols: " + Arrays.asList(sslsock.getEnabledProtocols()));
            this.log.debug("Enabled cipher suites:" + Arrays.asList(sslsock.getEnabledCipherSuites()));
        }
        prepareSocket(sslsock);
        this.log.debug("Starting handshake");
        sslsock.startHandshake();
        verifyHostname(sslsock, target);
        return sslsock;
    }

    private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
        try {
            SSLSession session = sslsock.getSession();
            if (session == null) {
                sslsock.getInputStream().available();
                session = sslsock.getSession();
                if (session == null) {
                    sslsock.startHandshake();
                    session = sslsock.getSession();
                }
            }
            if (session == null) {
                throw new SSLHandshakeException("SSL session not available");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Secure session established");
                this.log.debug(" negotiated protocol: " + session.getProtocol());
                this.log.debug(" negotiated cipher suite: " + session.getCipherSuite());
                try {
                    X509Certificate x509 = (X509Certificate) session.getPeerCertificates()[0];
                    this.log.debug(" peer principal: " + x509.getSubjectX500Principal().toString());
                    Collection<List<?>> altNames1 = x509.getSubjectAlternativeNames();
                    if (altNames1 != null) {
                        List<String> altNames = new ArrayList<>();
                        for (List<?> aC : altNames1) {
                            if (!aC.isEmpty()) {
                                altNames.add((String) aC.get(1));
                            }
                        }
                        this.log.debug(" peer alternative names: " + altNames);
                    }
                    this.log.debug(" issuer principal: " + x509.getIssuerX500Principal().toString());
                    Collection<List<?>> altNames2 = x509.getIssuerAlternativeNames();
                    if (altNames2 != null) {
                        List<String> altNames3 = new ArrayList<>();
                        for (List<?> aC2 : altNames2) {
                            if (!aC2.isEmpty()) {
                                altNames3.add((String) aC2.get(1));
                            }
                        }
                        this.log.debug(" issuer alternative names: " + altNames3);
                    }
                } catch (Exception e) {
                }
            }
            if (!this.hostnameVerifier.verify(hostname, session)) {
                throw new SSLPeerUnverifiedException("Certificate for <" + hostname + "> doesn't match any " + "of the subject alternative names: " + DefaultHostnameVerifier.getSubjectAltNames((X509Certificate) session.getPeerCertificates()[0]));
            }
        } catch (IOException iox) {
            try {
                sslsock.close();
            } catch (Exception e2) {
            }
            throw iox;
        }
    }
}
