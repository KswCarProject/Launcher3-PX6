package org.apache.http;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class HttpHost implements Cloneable, Serializable {
    public static final String DEFAULT_SCHEME_NAME = "http";
    private static final long serialVersionUID = -7529410654042457626L;
    protected final InetAddress address;
    protected final String hostname;
    protected final String lcHostname;
    protected final int port;
    protected final String schemeName;

    public HttpHost(String hostname2, int port2, String scheme) {
        this.hostname = (String) Args.containsNoBlanks(hostname2, "Host name");
        this.lcHostname = hostname2.toLowerCase(Locale.ROOT);
        if (scheme != null) {
            this.schemeName = scheme.toLowerCase(Locale.ROOT);
        } else {
            this.schemeName = DEFAULT_SCHEME_NAME;
        }
        this.port = port2;
        this.address = null;
    }

    public HttpHost(String hostname2, int port2) {
        this(hostname2, port2, (String) null);
    }

    public static HttpHost create(String s) {
        Args.containsNoBlanks(s, "HTTP Host");
        String text = s;
        String scheme = null;
        int schemeIdx = text.indexOf("://");
        if (schemeIdx > 0) {
            scheme = text.substring(0, schemeIdx);
            text = text.substring(schemeIdx + 3);
        }
        int port2 = -1;
        int portIdx = text.lastIndexOf(":");
        if (portIdx > 0) {
            try {
                port2 = Integer.parseInt(text.substring(portIdx + 1));
                text = text.substring(0, portIdx);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid HTTP host: " + text);
            }
        }
        return new HttpHost(text, port2, scheme);
    }

    public HttpHost(String hostname2) {
        this(hostname2, -1, (String) null);
    }

    public HttpHost(InetAddress address2, int port2, String scheme) {
        this((InetAddress) Args.notNull(address2, "Inet address"), address2.getHostName(), port2, scheme);
    }

    public HttpHost(InetAddress address2, String hostname2, int port2, String scheme) {
        this.address = (InetAddress) Args.notNull(address2, "Inet address");
        this.hostname = (String) Args.notNull(hostname2, "Hostname");
        this.lcHostname = this.hostname.toLowerCase(Locale.ROOT);
        if (scheme != null) {
            this.schemeName = scheme.toLowerCase(Locale.ROOT);
        } else {
            this.schemeName = DEFAULT_SCHEME_NAME;
        }
        this.port = port2;
    }

    public HttpHost(InetAddress address2, int port2) {
        this(address2, port2, (String) null);
    }

    public HttpHost(InetAddress address2) {
        this(address2, -1, (String) null);
    }

    public HttpHost(HttpHost httphost) {
        Args.notNull(httphost, "HTTP host");
        this.hostname = httphost.hostname;
        this.lcHostname = httphost.lcHostname;
        this.schemeName = httphost.schemeName;
        this.port = httphost.port;
        this.address = httphost.address;
    }

    public String getHostName() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public String toURI() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.schemeName);
        buffer.append("://");
        buffer.append(this.hostname);
        if (this.port != -1) {
            buffer.append(':');
            buffer.append(Integer.toString(this.port));
        }
        return buffer.toString();
    }

    public String toHostString() {
        if (this.port == -1) {
            return this.hostname;
        }
        StringBuilder buffer = new StringBuilder(this.hostname.length() + 6);
        buffer.append(this.hostname);
        buffer.append(":");
        buffer.append(Integer.toString(this.port));
        return buffer.toString();
    }

    public String toString() {
        return toURI();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HttpHost)) {
            return false;
        }
        HttpHost that = (HttpHost) obj;
        if (this.lcHostname.equals(that.lcHostname) && this.port == that.port && this.schemeName.equals(that.schemeName)) {
            if (this.address == null) {
                if (that.address == null) {
                    return true;
                }
            } else if (this.address.equals(that.address)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int hash = LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, (Object) this.lcHostname), this.port), (Object) this.schemeName);
        if (this.address != null) {
            return LangUtils.hashCode(hash, (Object) this.address);
        }
        return hash;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
