package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.SAFE)
@Deprecated
public class BestMatchSpec extends DefaultCookieSpec {
    public BestMatchSpec(String[] datepatterns, boolean oneHeader) {
        super(datepatterns, oneHeader);
    }

    public BestMatchSpec() {
        this((String[]) null, false);
    }

    public String toString() {
        return "best-match";
    }
}
