package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class BasicUserPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = -2266305184969850467L;
    private final String username;

    public BasicUserPrincipal(String username2) {
        Args.notNull(username2, "User name");
        this.username = username2;
    }

    public String getName() {
        return this.username;
    }

    public int hashCode() {
        return LangUtils.hashCode(17, (Object) this.username);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicUserPrincipal) || !LangUtils.equals((Object) this.username, (Object) ((BasicUserPrincipal) o).username)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "[principal: " + this.username + "]";
    }
}
