package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class UsernamePasswordCredentials implements Credentials, Serializable {
    private static final long serialVersionUID = 243343858802739403L;
    private final String password;
    private final BasicUserPrincipal principal;

    @Deprecated
    public UsernamePasswordCredentials(String usernamePassword) {
        Args.notNull(usernamePassword, "Username:password string");
        int atColon = usernamePassword.indexOf(58);
        if (atColon >= 0) {
            this.principal = new BasicUserPrincipal(usernamePassword.substring(0, atColon));
            this.password = usernamePassword.substring(atColon + 1);
            return;
        }
        this.principal = new BasicUserPrincipal(usernamePassword);
        this.password = null;
    }

    public UsernamePasswordCredentials(String userName, String password2) {
        Args.notNull(userName, "Username");
        this.principal = new BasicUserPrincipal(userName);
        this.password = password2;
    }

    public Principal getUserPrincipal() {
        return this.principal;
    }

    public String getUserName() {
        return this.principal.getName();
    }

    public String getPassword() {
        return this.password;
    }

    public int hashCode() {
        return this.principal.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsernamePasswordCredentials) || !LangUtils.equals((Object) this.principal, (Object) ((UsernamePasswordCredentials) o).principal)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.principal.toString();
    }
}
