package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.TokenParser;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NTUserPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = -6870169797924406894L;
    private final String domain;
    private final String ntname;
    private final String username;

    public NTUserPrincipal(String domain2, String username2) {
        Args.notNull(username2, "User name");
        this.username = username2;
        if (domain2 != null) {
            this.domain = domain2.toUpperCase(Locale.ROOT);
        } else {
            this.domain = null;
        }
        if (this.domain == null || this.domain.isEmpty()) {
            this.ntname = this.username;
            return;
        }
        this.ntname = this.domain + TokenParser.ESCAPE + this.username;
    }

    public String getName() {
        return this.ntname;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getUsername() {
        return this.username;
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(17, (Object) this.username), (Object) this.domain);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof NTUserPrincipal) {
            NTUserPrincipal that = (NTUserPrincipal) o;
            if (!LangUtils.equals((Object) this.username, (Object) that.username) || !LangUtils.equals((Object) this.domain, (Object) that.domain)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return this.ntname;
    }
}
