package org.apache.http.auth;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class AuthOption {
    private final AuthScheme authScheme;
    private final Credentials creds;

    public AuthOption(AuthScheme authScheme2, Credentials creds2) {
        Args.notNull(authScheme2, "Auth scheme");
        Args.notNull(creds2, "User credentials");
        this.authScheme = authScheme2;
        this.creds = creds2;
    }

    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }

    public Credentials getCredentials() {
        return this.creds;
    }

    public String toString() {
        return this.authScheme.toString();
    }
}
