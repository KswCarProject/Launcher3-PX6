package org.apache.http.client.utils;

import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
    private HttpClientUtils() {
    }

    public static void closeQuietly(HttpResponse response) {
        HttpEntity entity;
        if (response != null && (entity = response.getEntity()) != null) {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
            }
        }
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void closeQuietly(org.apache.http.client.methods.CloseableHttpResponse r1) {
        /*
            if (r1 == 0) goto L_0x000c
            org.apache.http.HttpEntity r0 = r1.getEntity()     // Catch:{ all -> 0x000d }
            org.apache.http.util.EntityUtils.consume(r0)     // Catch:{ all -> 0x000d }
            r1.close()     // Catch:{ IOException -> 0x0012 }
        L_0x000c:
            return
        L_0x000d:
            r0 = move-exception
            r1.close()     // Catch:{ IOException -> 0x0012 }
            throw r0     // Catch:{ IOException -> 0x0012 }
        L_0x0012:
            r0 = move-exception
            goto L_0x000c
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.client.utils.HttpClientUtils.closeQuietly(org.apache.http.client.methods.CloseableHttpResponse):void");
    }

    public static void closeQuietly(HttpClient httpClient) {
        if (httpClient != null && (httpClient instanceof Closeable)) {
            try {
                ((Closeable) httpClient).close();
            } catch (IOException e) {
            }
        }
    }
}
