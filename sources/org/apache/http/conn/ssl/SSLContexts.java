package org.apache.http.conn.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class SSLContexts {
    public static SSLContext createDefault() throws SSLInitializationException {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init((KeyManager[]) null, (TrustManager[]) null, (SecureRandom) null);
            return sslcontext;
        } catch (NoSuchAlgorithmException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        } catch (KeyManagementException ex2) {
            throw new SSLInitializationException(ex2.getMessage(), ex2);
        }
    }

    public static SSLContext createSystemDefault() throws SSLInitializationException {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            return createDefault();
        }
    }

    public static SSLContextBuilder custom() {
        return new SSLContextBuilder();
    }
}
