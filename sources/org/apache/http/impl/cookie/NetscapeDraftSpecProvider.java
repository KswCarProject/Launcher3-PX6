package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Obsolete
public class NetscapeDraftSpecProvider implements CookieSpecProvider {
    private volatile CookieSpec cookieSpec;
    private final String[] datepatterns;

    public NetscapeDraftSpecProvider(String[] datepatterns2) {
        this.datepatterns = datepatterns2;
    }

    public NetscapeDraftSpecProvider() {
        this((String[]) null);
    }

    public CookieSpec create(HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new NetscapeDraftSpec(this.datepatterns);
                }
            }
        }
        return this.cookieSpec;
    }
}
