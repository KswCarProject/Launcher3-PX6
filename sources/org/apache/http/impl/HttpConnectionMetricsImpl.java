package org.apache.http.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.io.HttpTransportMetrics;

public class HttpConnectionMetricsImpl implements HttpConnectionMetrics {
    public static final String RECEIVED_BYTES_COUNT = "http.received-bytes-count";
    public static final String REQUEST_COUNT = "http.request-count";
    public static final String RESPONSE_COUNT = "http.response-count";
    public static final String SENT_BYTES_COUNT = "http.sent-bytes-count";
    private final HttpTransportMetrics inTransportMetric;
    private Map<String, Object> metricsCache;
    private final HttpTransportMetrics outTransportMetric;
    private long requestCount = 0;
    private long responseCount = 0;

    public HttpConnectionMetricsImpl(HttpTransportMetrics inTransportMetric2, HttpTransportMetrics outTransportMetric2) {
        this.inTransportMetric = inTransportMetric2;
        this.outTransportMetric = outTransportMetric2;
    }

    public long getReceivedBytesCount() {
        if (this.inTransportMetric != null) {
            return this.inTransportMetric.getBytesTransferred();
        }
        return -1;
    }

    public long getSentBytesCount() {
        if (this.outTransportMetric != null) {
            return this.outTransportMetric.getBytesTransferred();
        }
        return -1;
    }

    public long getRequestCount() {
        return this.requestCount;
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }

    public long getResponseCount() {
        return this.responseCount;
    }

    public void incrementResponseCount() {
        this.responseCount++;
    }

    public Object getMetric(String metricName) {
        Object value = null;
        if (this.metricsCache != null) {
            value = this.metricsCache.get(metricName);
        }
        if (value != null) {
            return value;
        }
        if (REQUEST_COUNT.equals(metricName)) {
            return Long.valueOf(this.requestCount);
        }
        if (RESPONSE_COUNT.equals(metricName)) {
            return Long.valueOf(this.responseCount);
        }
        if (RECEIVED_BYTES_COUNT.equals(metricName)) {
            if (this.inTransportMetric != null) {
                return Long.valueOf(this.inTransportMetric.getBytesTransferred());
            }
            return null;
        } else if (!SENT_BYTES_COUNT.equals(metricName)) {
            return value;
        } else {
            if (this.outTransportMetric != null) {
                return Long.valueOf(this.outTransportMetric.getBytesTransferred());
            }
            return null;
        }
    }

    public void setMetric(String metricName, Object obj) {
        if (this.metricsCache == null) {
            this.metricsCache = new HashMap();
        }
        this.metricsCache.put(metricName, obj);
    }

    public void reset() {
        if (this.outTransportMetric != null) {
            this.outTransportMetric.reset();
        }
        if (this.inTransportMetric != null) {
            this.inTransportMetric.reset();
        }
        this.requestCount = 0;
        this.responseCount = 0;
        this.metricsCache = null;
    }
}
