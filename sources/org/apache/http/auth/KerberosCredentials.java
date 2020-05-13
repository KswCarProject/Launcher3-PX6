package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.ietf.jgss.GSSCredential;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosCredentials implements Credentials, Serializable {
    private static final long serialVersionUID = 487421613855550713L;
    private final GSSCredential gssCredential;

    public KerberosCredentials(GSSCredential gssCredential2) {
        this.gssCredential = gssCredential2;
    }

    public GSSCredential getGSSCredential() {
        return this.gssCredential;
    }

    public Principal getUserPrincipal() {
        return null;
    }

    public String getPassword() {
        return null;
    }
}
