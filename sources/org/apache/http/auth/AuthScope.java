package org.apache.http.auth;

import java.util.Locale;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.TokenParser;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class AuthScope {
    public static final AuthScope ANY = new AuthScope(ANY_HOST, -1, ANY_REALM, ANY_SCHEME);
    public static final String ANY_HOST = null;
    public static final int ANY_PORT = -1;
    public static final String ANY_REALM = null;
    public static final String ANY_SCHEME = null;
    private final String host;
    private final HttpHost origin;
    private final int port;
    private final String realm;
    private final String scheme;

    public AuthScope(String host2, int port2, String realm2, String schemeName) {
        this.host = host2 == null ? ANY_HOST : host2.toLowerCase(Locale.ROOT);
        this.port = port2 < 0 ? -1 : port2;
        this.realm = realm2 == null ? ANY_REALM : realm2;
        this.scheme = schemeName == null ? ANY_SCHEME : schemeName.toUpperCase(Locale.ROOT);
        this.origin = null;
    }

    public AuthScope(HttpHost origin2, String realm2, String schemeName) {
        Args.notNull(origin2, "Host");
        this.host = origin2.getHostName().toLowerCase(Locale.ROOT);
        this.port = origin2.getPort() < 0 ? -1 : origin2.getPort();
        this.realm = realm2 == null ? ANY_REALM : realm2;
        this.scheme = schemeName == null ? ANY_SCHEME : schemeName.toUpperCase(Locale.ROOT);
        this.origin = origin2;
    }

    public AuthScope(HttpHost origin2) {
        this(origin2, ANY_REALM, ANY_SCHEME);
    }

    public AuthScope(String host2, int port2, String realm2) {
        this(host2, port2, realm2, ANY_SCHEME);
    }

    public AuthScope(String host2, int port2) {
        this(host2, port2, ANY_REALM, ANY_SCHEME);
    }

    public AuthScope(AuthScope authscope) {
        Args.notNull(authscope, "Scope");
        this.host = authscope.getHost();
        this.port = authscope.getPort();
        this.realm = authscope.getRealm();
        this.scheme = authscope.getScheme();
        this.origin = authscope.getOrigin();
    }

    public HttpHost getOrigin() {
        return this.origin;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getRealm() {
        return this.realm;
    }

    public String getScheme() {
        return this.scheme;
    }

    public int match(AuthScope that) {
        int factor = 0;
        if (LangUtils.equals((Object) this.scheme, (Object) that.scheme)) {
            factor = 0 + 1;
        } else if (!(this.scheme == ANY_SCHEME || that.scheme == ANY_SCHEME)) {
            return -1;
        }
        if (LangUtils.equals((Object) this.realm, (Object) that.realm)) {
            factor += 2;
        } else if (!(this.realm == ANY_REALM || that.realm == ANY_REALM)) {
            return -1;
        }
        if (this.port == that.port) {
            factor += 4;
        } else if (!(this.port == -1 || that.port == -1)) {
            return -1;
        }
        if (LangUtils.equals((Object) this.host, (Object) that.host)) {
            factor += 8;
        } else if (!(this.host == ANY_HOST || that.host == ANY_HOST)) {
            return -1;
        }
        return factor;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthScope)) {
            return super.equals(o);
        }
        AuthScope that = (AuthScope) o;
        if (!LangUtils.equals((Object) this.host, (Object) that.host) || this.port != that.port || !LangUtils.equals((Object) this.realm, (Object) that.realm) || !LangUtils.equals((Object) this.scheme, (Object) that.scheme)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.scheme != null) {
            buffer.append(this.scheme.toUpperCase(Locale.ROOT));
            buffer.append(TokenParser.SP);
        }
        if (this.realm != null) {
            buffer.append('\'');
            buffer.append(this.realm);
            buffer.append('\'');
        } else {
            buffer.append("<any realm>");
        }
        if (this.host != null) {
            buffer.append('@');
            buffer.append(this.host);
            if (this.port >= 0) {
                buffer.append(':');
                buffer.append(this.port);
            }
        }
        return buffer.toString();
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, (Object) this.host), this.port), (Object) this.realm), (Object) this.scheme);
    }
}
