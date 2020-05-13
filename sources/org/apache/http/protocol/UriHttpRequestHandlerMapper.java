package org.apache.http.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE)
public class UriHttpRequestHandlerMapper implements HttpRequestHandlerMapper {
    private final UriPatternMatcher<HttpRequestHandler> matcher;

    /* JADX WARNING: type inference failed for: r2v0, types: [java.lang.Object, org.apache.http.protocol.UriPatternMatcher<org.apache.http.protocol.HttpRequestHandler>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected UriHttpRequestHandlerMapper(org.apache.http.protocol.UriPatternMatcher<org.apache.http.protocol.HttpRequestHandler> r2) {
        /*
            r1 = this;
            r1.<init>()
            java.lang.String r0 = "Pattern matcher"
            java.lang.Object r0 = org.apache.http.util.Args.notNull(r2, r0)
            org.apache.http.protocol.UriPatternMatcher r0 = (org.apache.http.protocol.UriPatternMatcher) r0
            r1.matcher = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.protocol.UriHttpRequestHandlerMapper.<init>(org.apache.http.protocol.UriPatternMatcher):void");
    }

    public UriHttpRequestHandlerMapper() {
        this(new UriPatternMatcher());
    }

    public void register(String pattern, HttpRequestHandler handler) {
        Args.notNull(pattern, "Pattern");
        Args.notNull(handler, "Handler");
        this.matcher.register(pattern, handler);
    }

    public void unregister(String pattern) {
        this.matcher.unregister(pattern);
    }

    /* access modifiers changed from: protected */
    public String getRequestPath(HttpRequest request) {
        String uriPath = request.getRequestLine().getUri();
        int index = uriPath.indexOf("?");
        if (index != -1) {
            return uriPath.substring(0, index);
        }
        int index2 = uriPath.indexOf("#");
        if (index2 != -1) {
            return uriPath.substring(0, index2);
        }
        return uriPath;
    }

    public HttpRequestHandler lookup(HttpRequest request) {
        Args.notNull(request, "HTTP request");
        return this.matcher.lookup(getRequestPath(request));
    }
}
