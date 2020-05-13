package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE)
public class ResponseDate implements HttpResponseInterceptor {
    private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        if (response.getStatusLine().getStatusCode() >= 200 && !response.containsHeader("Date")) {
            response.setHeader("Date", DATE_GENERATOR.getCurrentDate());
        }
    }
}
