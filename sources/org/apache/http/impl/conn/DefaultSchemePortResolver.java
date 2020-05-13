package org.apache.http.impl.conn;

import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultSchemePortResolver implements SchemePortResolver {
    public static final DefaultSchemePortResolver INSTANCE = new DefaultSchemePortResolver();

    public int resolve(HttpHost host) throws UnsupportedSchemeException {
        Args.notNull(host, "HTTP host");
        int port = host.getPort();
        if (port > 0) {
            return port;
        }
        String name = host.getSchemeName();
        if (name.equalsIgnoreCase(HttpHost.DEFAULT_SCHEME_NAME)) {
            return 80;
        }
        if (name.equalsIgnoreCase("https")) {
            return 443;
        }
        throw new UnsupportedSchemeException(name + " protocol is not supported");
    }
}
