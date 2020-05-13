package org.apache.http.impl.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.params.HttpParams;

@Deprecated
public class NegotiateSchemeFactory implements AuthSchemeFactory {
    private final SpnegoTokenGenerator spengoGenerator;
    private final boolean stripPort;

    public NegotiateSchemeFactory(SpnegoTokenGenerator spengoGenerator2, boolean stripPort2) {
        this.spengoGenerator = spengoGenerator2;
        this.stripPort = stripPort2;
    }

    public NegotiateSchemeFactory(SpnegoTokenGenerator spengoGenerator2) {
        this(spengoGenerator2, false);
    }

    public NegotiateSchemeFactory() {
        this((SpnegoTokenGenerator) null, false);
    }

    public AuthScheme newInstance(HttpParams params) {
        return new NegotiateScheme(this.spengoGenerator, this.stripPort);
    }

    public boolean isStripPort() {
        return this.stripPort;
    }

    public SpnegoTokenGenerator getSpengoGenerator() {
        return this.spengoGenerator;
    }
}
