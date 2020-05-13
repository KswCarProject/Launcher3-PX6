package org.apache.http.impl.auth;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public abstract class GGSSchemeBase extends AuthSchemeBase {
    private final Base64 base64codec;
    private final Log log;
    private State state;
    private final boolean stripPort;
    private byte[] token;
    private final boolean useCanonicalHostname;

    enum State {
        UNINITIATED,
        CHALLENGE_RECEIVED,
        TOKEN_GENERATED,
        FAILED
    }

    GGSSchemeBase(boolean stripPort2, boolean useCanonicalHostname2) {
        this.log = LogFactory.getLog(getClass());
        this.base64codec = new Base64(0);
        this.stripPort = stripPort2;
        this.useCanonicalHostname = useCanonicalHostname2;
        this.state = State.UNINITIATED;
    }

    GGSSchemeBase(boolean stripPort2) {
        this(stripPort2, true);
    }

    GGSSchemeBase() {
        this(true, true);
    }

    /* access modifiers changed from: protected */
    public GSSManager getManager() {
        return GSSManager.getInstance();
    }

    /* access modifiers changed from: protected */
    public byte[] generateGSSToken(byte[] input, Oid oid, String authServer) throws GSSException {
        return generateGSSToken(input, oid, authServer, (Credentials) null);
    }

    /* access modifiers changed from: protected */
    public byte[] generateGSSToken(byte[] input, Oid oid, String authServer, Credentials credentials) throws GSSException {
        GSSCredential gssCredential;
        GSSManager manager = getManager();
        GSSName serverName = manager.createName("HTTP@" + authServer, GSSName.NT_HOSTBASED_SERVICE);
        if (credentials instanceof KerberosCredentials) {
            gssCredential = ((KerberosCredentials) credentials).getGSSCredential();
        } else {
            gssCredential = null;
        }
        GSSContext gssContext = createGSSContext(manager, oid, serverName, gssCredential);
        if (input != null) {
            return gssContext.initSecContext(input, 0, input.length);
        }
        return gssContext.initSecContext(new byte[0], 0, 0);
    }

    /* access modifiers changed from: package-private */
    public GSSContext createGSSContext(GSSManager manager, Oid oid, GSSName serverName, GSSCredential gssCredential) throws GSSException {
        GSSContext gssContext = manager.createContext(serverName.canonicalize(oid), oid, gssCredential, 0);
        gssContext.requestMutualAuth(true);
        return gssContext;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public byte[] generateToken(byte[] input, String authServer) throws GSSException {
        return null;
    }

    /* access modifiers changed from: protected */
    public byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
        return generateToken(input, authServer);
    }

    public boolean isComplete() {
        return this.state == State.TOKEN_GENERATED || this.state == State.FAILED;
    }

    @Deprecated
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        return authenticate(credentials, request, (HttpContext) null);
    }

    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        HttpHost host;
        String authServer;
        Args.notNull(request, "HTTP request");
        switch (this.state) {
            case UNINITIATED:
                throw new AuthenticationException(getSchemeName() + " authentication has not been initiated");
            case FAILED:
                throw new AuthenticationException(getSchemeName() + " authentication has failed");
            case CHALLENGE_RECEIVED:
                try {
                    HttpRoute route = (HttpRoute) context.getAttribute("http.route");
                    if (route != null) {
                        if (isProxy()) {
                            host = route.getProxyHost();
                            if (host == null) {
                                host = route.getTargetHost();
                            }
                        } else {
                            host = route.getTargetHost();
                        }
                        String hostname = host.getHostName();
                        if (this.useCanonicalHostname) {
                            try {
                                hostname = resolveCanonicalHostname(hostname);
                            } catch (UnknownHostException e) {
                            }
                        }
                        if (this.stripPort) {
                            authServer = hostname;
                        } else {
                            authServer = hostname + ":" + host.getPort();
                        }
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("init " + authServer);
                        }
                        this.token = generateToken(this.token, authServer, credentials);
                        this.state = State.TOKEN_GENERATED;
                        break;
                    } else {
                        throw new AuthenticationException("Connection route is not available");
                    }
                } catch (GSSException gsse) {
                    this.state = State.FAILED;
                    if (gsse.getMajor() == 9 || gsse.getMajor() == 8) {
                        throw new InvalidCredentialsException(gsse.getMessage(), gsse);
                    } else if (gsse.getMajor() == 13) {
                        throw new InvalidCredentialsException(gsse.getMessage(), gsse);
                    } else if (gsse.getMajor() == 10 || gsse.getMajor() == 19 || gsse.getMajor() == 20) {
                        throw new AuthenticationException(gsse.getMessage(), gsse);
                    } else {
                        throw new AuthenticationException(gsse.getMessage());
                    }
                }
                break;
            case TOKEN_GENERATED:
                break;
            default:
                throw new IllegalStateException("Illegal state: " + this.state);
        }
        String tokenstr = new String(this.base64codec.encode(this.token));
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending response '" + tokenstr + "' back to the auth server");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (isProxy()) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Negotiate ");
        buffer.append(tokenstr);
        return new BufferedHeader(buffer);
    }

    /* access modifiers changed from: protected */
    public void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
        String challenge = buffer.substringTrimmed(beginIndex, endIndex);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Received challenge '" + challenge + "' from the auth server");
        }
        if (this.state == State.UNINITIATED) {
            this.token = Base64.decodeBase64(challenge.getBytes());
            this.state = State.CHALLENGE_RECEIVED;
            return;
        }
        this.log.debug("Authentication already attempted");
        this.state = State.FAILED;
    }

    private String resolveCanonicalHostname(String host) throws UnknownHostException {
        InetAddress in = InetAddress.getByName(host);
        String canonicalServer = in.getCanonicalHostName();
        return in.getHostAddress().contentEquals(canonicalServer) ? host : canonicalServer;
    }
}
