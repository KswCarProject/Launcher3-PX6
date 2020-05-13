package org.apache.http.impl.client;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxRedirectStrategy extends DefaultRedirectStrategy {
    public static final LaxRedirectStrategy INSTANCE = new LaxRedirectStrategy();
    private static final String[] REDIRECT_METHODS = {HttpGet.METHOD_NAME, HttpPost.METHOD_NAME, HttpHead.METHOD_NAME, HttpDelete.METHOD_NAME};

    /* access modifiers changed from: protected */
    public boolean isRedirectable(String method) {
        for (String m : REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
