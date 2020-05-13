package org.apache.http.impl.auth;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider {
    private final boolean stripPort;
    private final boolean useCanonicalHostname;

    public KerberosSchemeFactory(boolean stripPort2, boolean useCanonicalHostname2) {
        this.stripPort = stripPort2;
        this.useCanonicalHostname = useCanonicalHostname2;
    }

    public KerberosSchemeFactory(boolean stripPort2) {
        this.stripPort = stripPort2;
        this.useCanonicalHostname = true;
    }

    public KerberosSchemeFactory() {
        this(true, true);
    }

    public boolean isStripPort() {
        return this.stripPort;
    }

    public boolean isUseCanonicalHostname() {
        return this.useCanonicalHostname;
    }

    public AuthScheme newInstance(HttpParams params) {
        return new KerberosScheme(this.stripPort, this.useCanonicalHostname);
    }

    public AuthScheme create(HttpContext context) {
        return new KerberosScheme(this.stripPort, this.useCanonicalHostname);
    }
}
