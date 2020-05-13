package org.apache.http.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public final class ImmutableHttpProcessor implements HttpProcessor {
    private final HttpRequestInterceptor[] requestInterceptors;
    private final HttpResponseInterceptor[] responseInterceptors;

    public ImmutableHttpProcessor(HttpRequestInterceptor[] requestInterceptors2, HttpResponseInterceptor[] responseInterceptors2) {
        if (requestInterceptors2 != null) {
            int l = requestInterceptors2.length;
            this.requestInterceptors = new HttpRequestInterceptor[l];
            System.arraycopy(requestInterceptors2, 0, this.requestInterceptors, 0, l);
        } else {
            this.requestInterceptors = new HttpRequestInterceptor[0];
        }
        if (responseInterceptors2 != null) {
            int l2 = responseInterceptors2.length;
            this.responseInterceptors = new HttpResponseInterceptor[l2];
            System.arraycopy(responseInterceptors2, 0, this.responseInterceptors, 0, l2);
            return;
        }
        this.responseInterceptors = new HttpResponseInterceptor[0];
    }

    public ImmutableHttpProcessor(List<HttpRequestInterceptor> requestInterceptors2, List<HttpResponseInterceptor> responseInterceptors2) {
        if (requestInterceptors2 != null) {
            this.requestInterceptors = (HttpRequestInterceptor[]) requestInterceptors2.toArray(new HttpRequestInterceptor[requestInterceptors2.size()]);
        } else {
            this.requestInterceptors = new HttpRequestInterceptor[0];
        }
        if (responseInterceptors2 != null) {
            this.responseInterceptors = (HttpResponseInterceptor[]) responseInterceptors2.toArray(new HttpResponseInterceptor[responseInterceptors2.size()]);
        } else {
            this.responseInterceptors = new HttpResponseInterceptor[0];
        }
    }

    @Deprecated
    public ImmutableHttpProcessor(HttpRequestInterceptorList requestInterceptors2, HttpResponseInterceptorList responseInterceptors2) {
        if (requestInterceptors2 != null) {
            int count = requestInterceptors2.getRequestInterceptorCount();
            this.requestInterceptors = new HttpRequestInterceptor[count];
            for (int i = 0; i < count; i++) {
                this.requestInterceptors[i] = requestInterceptors2.getRequestInterceptor(i);
            }
        } else {
            this.requestInterceptors = new HttpRequestInterceptor[0];
        }
        if (responseInterceptors2 != null) {
            int count2 = responseInterceptors2.getResponseInterceptorCount();
            this.responseInterceptors = new HttpResponseInterceptor[count2];
            for (int i2 = 0; i2 < count2; i2++) {
                this.responseInterceptors[i2] = responseInterceptors2.getResponseInterceptor(i2);
            }
            return;
        }
        this.responseInterceptors = new HttpResponseInterceptor[0];
    }

    public ImmutableHttpProcessor(HttpRequestInterceptor... requestInterceptors2) {
        this(requestInterceptors2, (HttpResponseInterceptor[]) null);
    }

    public ImmutableHttpProcessor(HttpResponseInterceptor... responseInterceptors2) {
        this((HttpRequestInterceptor[]) null, responseInterceptors2);
    }

    public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
        for (HttpRequestInterceptor requestInterceptor : this.requestInterceptors) {
            requestInterceptor.process(request, context);
        }
    }

    public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
        for (HttpResponseInterceptor responseInterceptor : this.responseInterceptors) {
            responseInterceptor.process(response, context);
        }
    }
}
