package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {
    private final int maxRetries;
    private final long retryInterval;

    public DefaultServiceUnavailableRetryStrategy(int maxRetries2, int retryInterval2) {
        Args.positive(maxRetries2, "Max retries");
        Args.positive(retryInterval2, "Retry interval");
        this.maxRetries = maxRetries2;
        this.retryInterval = (long) retryInterval2;
    }

    public DefaultServiceUnavailableRetryStrategy() {
        this(1, 1000);
    }

    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        return executionCount <= this.maxRetries && response.getStatusLine().getStatusCode() == 503;
    }

    public long getRetryInterval() {
        return this.retryInterval;
    }
}
