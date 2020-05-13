package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class BrowserCompatHostnameVerifier extends AbstractVerifier {
    public static final BrowserCompatHostnameVerifier INSTANCE = new BrowserCompatHostnameVerifier();

    public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        verify(host, cns, subjectAlts, false);
    }

    public final String toString() {
        return "BROWSER_COMPATIBLE";
    }
}
