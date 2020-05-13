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
public class NetscapeDraftSpecFactory implements CookieSpecFactory, CookieSpecProvider {
    private final CookieSpec cookieSpec;

    public NetscapeDraftSpecFactory(String[] datepatterns) {
        this.cookieSpec = new NetscapeDraftSpec(datepatterns);
    }

    public NetscapeDraftSpecFactory() {
        this((String[]) null);
    }

    public CookieSpec newInstance(HttpParams params) {
        if (params == null) {
            return new NetscapeDraftSpec();
        }
        String[] patterns = null;
        Collection<?> param = (Collection) params.getParameter(CookieSpecPNames.DATE_PATTERNS);
        if (param != null) {
            patterns = (String[]) param.toArray(new String[param.size()]);
        }
        return new NetscapeDraftSpec(patterns);
    }

    public CookieSpec create(HttpContext context) {
        return this.cookieSpec;
    }
}
