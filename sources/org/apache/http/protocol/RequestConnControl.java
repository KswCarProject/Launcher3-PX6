package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestConnControl implements HttpRequestInterceptor {
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (!request.getRequestLine().getMethod().equalsIgnoreCase("CONNECT") && !request.containsHeader("Connection")) {
            request.addHeader("Connection", HTTP.CONN_KEEP_ALIVE);
        }
    }
}
