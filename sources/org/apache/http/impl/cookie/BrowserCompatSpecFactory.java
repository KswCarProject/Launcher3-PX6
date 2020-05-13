package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class BrowserCompatSpecFactory implements CookieSpecFactory, CookieSpecProvider {
    private final CookieSpec cookieSpec;
    private final SecurityLevel securityLevel;

    public enum SecurityLevel {
        SECURITYLEVEL_DEFAULT,
        SECURITYLEVEL_IE_MEDIUM
    }

    public BrowserCompatSpecFactory(String[] datepatterns, SecurityLevel securityLevel2) {
        this.securityLevel = securityLevel2;
        this.cookieSpec = new BrowserCompatSpec(datepatterns, securityLevel2);
    }

    public BrowserCompatSpecFactory(String[] datepatterns) {
        this((String[]) null, SecurityLevel.SECURITYLEVEL_DEFAULT);
    }

    public BrowserCompatSpecFactory() {
        this((String[]) null, SecurityLevel.SECURITYLEVEL_DEFAULT);
    }

    public CookieSpec newInstance(HttpParams params) {
        if (params == null) {
            return new BrowserCompatSpec((String[]) null, this.securityLevel);
        }
        String[] patterns = null;
        Collection<?> param = (Collection) params.getParameter(CookieSpecPNames.DATE_PATTERNS);
        if (param != null) {
            patterns = (String[]) param.toArray(new String[param.size()]);
        }
        return new BrowserCompatSpec(patterns, this.securityLevel);
    }

    public CookieSpec create(HttpContext context) {
        return this.cookieSpec;
    }
}
