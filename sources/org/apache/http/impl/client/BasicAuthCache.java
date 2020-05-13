package org.apache.http.impl.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE)
public class BasicAuthCache implements AuthCache {
    private final Log log;
    private final Map<HttpHost, byte[]> map;
    private final SchemePortResolver schemePortResolver;

    public BasicAuthCache(SchemePortResolver schemePortResolver2) {
        this.log = LogFactory.getLog(getClass());
        this.map = new ConcurrentHashMap();
        this.schemePortResolver = schemePortResolver2 == null ? DefaultSchemePortResolver.INSTANCE : schemePortResolver2;
    }

    public BasicAuthCache() {
        this((SchemePortResolver) null);
    }

    /* access modifiers changed from: protected */
    public HttpHost getKey(HttpHost host) {
        if (host.getPort() > 0) {
            return host;
        }
        try {
            return new HttpHost(host.getHostName(), this.schemePortResolver.resolve(host), host.getSchemeName());
        } catch (UnsupportedSchemeException e) {
            return host;
        }
    }

    public void put(HttpHost host, AuthScheme authScheme) {
        Args.notNull(host, "HTTP host");
        if (authScheme != null) {
            if (authScheme instanceof Serializable) {
                try {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(buf);
                    out.writeObject(authScheme);
                    out.close();
                    this.map.put(getKey(host), buf.toByteArray());
                } catch (IOException ex) {
                    if (this.log.isWarnEnabled()) {
                        this.log.warn("Unexpected I/O error while serializing auth scheme", ex);
                    }
                }
            } else if (this.log.isDebugEnabled()) {
                this.log.debug("Auth scheme " + authScheme.getClass() + " is not serializable");
            }
        }
    }

    public AuthScheme get(HttpHost host) {
        Args.notNull(host, "HTTP host");
        byte[] bytes = this.map.get(getKey(host));
        if (bytes == null) {
            return null;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            AuthScheme authScheme = (AuthScheme) in.readObject();
            in.close();
            return authScheme;
        } catch (IOException ex) {
            if (this.log.isWarnEnabled()) {
                this.log.warn("Unexpected I/O error while de-serializing auth scheme", ex);
            }
            return null;
        } catch (ClassNotFoundException ex2) {
            if (this.log.isWarnEnabled()) {
                this.log.warn("Unexpected error while de-serializing auth scheme", ex2);
            }
            return null;
        }
    }

    public void remove(HttpHost host) {
        Args.notNull(host, "HTTP host");
        this.map.remove(getKey(host));
    }

    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }
}
