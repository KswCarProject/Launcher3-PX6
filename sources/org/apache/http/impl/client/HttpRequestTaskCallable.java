package org.apache.http.impl.client;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.protocol.HttpContext;

class HttpRequestTaskCallable<V> implements Callable<V> {
    private final FutureCallback<V> callback;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final HttpContext context;
    private long ended = -1;
    private final HttpClient httpclient;
    private final FutureRequestExecutionMetrics metrics;
    private final HttpUriRequest request;
    private final ResponseHandler<V> responseHandler;
    private final long scheduled = System.currentTimeMillis();
    private long started = -1;

    HttpRequestTaskCallable(HttpClient httpClient, HttpUriRequest request2, HttpContext context2, ResponseHandler<V> responseHandler2, FutureCallback<V> callback2, FutureRequestExecutionMetrics metrics2) {
        this.httpclient = httpClient;
        this.responseHandler = responseHandler2;
        this.request = request2;
        this.context = context2;
        this.callback = callback2;
        this.metrics = metrics2;
    }

    public long getScheduled() {
        return this.scheduled;
    }

    public long getStarted() {
        return this.started;
    }

    public long getEnded() {
        return this.ended;
    }

    public V call() throws Exception {
        if (!this.cancelled.get()) {
            try {
                this.metrics.getActiveConnections().incrementAndGet();
                this.started = System.currentTimeMillis();
                this.metrics.getScheduledConnections().decrementAndGet();
                V result = this.httpclient.execute(this.request, this.responseHandler, this.context);
                this.ended = System.currentTimeMillis();
                this.metrics.getSuccessfulConnections().increment(this.started);
                if (this.callback != null) {
                    this.callback.completed(result);
                }
                this.metrics.getRequests().increment(this.started);
                this.metrics.getTasks().increment(this.started);
                this.metrics.getActiveConnections().decrementAndGet();
                return result;
            } catch (Exception e) {
                this.metrics.getFailedConnections().increment(this.started);
                this.ended = System.currentTimeMillis();
                if (this.callback != null) {
                    this.callback.failed(e);
                }
                throw e;
            } catch (Throwable th) {
                this.metrics.getRequests().increment(this.started);
                this.metrics.getTasks().increment(this.started);
                this.metrics.getActiveConnections().decrementAndGet();
                throw th;
            }
        } else {
            throw new IllegalStateException("call has been cancelled for request " + this.request.getURI());
        }
    }

    public void cancel() {
        this.cancelled.set(true);
        if (this.callback != null) {
            this.callback.cancelled();
        }
    }
}
