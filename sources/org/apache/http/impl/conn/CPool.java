package org.apache.http.impl.conn;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.AbstractConnPool;
import org.apache.http.pool.ConnFactory;
import org.apache.http.pool.PoolEntryCallback;

@Contract(threading = ThreadingBehavior.SAFE)
class CPool extends AbstractConnPool<HttpRoute, ManagedHttpClientConnection, CPoolEntry> {
    private static final AtomicLong COUNTER = new AtomicLong();
    private final Log log = LogFactory.getLog(CPool.class);
    private final long timeToLive;
    private final TimeUnit tunit;

    public CPool(ConnFactory<HttpRoute, ManagedHttpClientConnection> connFactory, int defaultMaxPerRoute, int maxTotal, long timeToLive2, TimeUnit tunit2) {
        super(connFactory, defaultMaxPerRoute, maxTotal);
        this.timeToLive = timeToLive2;
        this.tunit = tunit2;
    }

    /* access modifiers changed from: protected */
    public CPoolEntry createEntry(HttpRoute route, ManagedHttpClientConnection conn) {
        return new CPoolEntry(this.log, Long.toString(COUNTER.getAndIncrement()), route, conn, this.timeToLive, this.tunit);
    }

    /* access modifiers changed from: protected */
    public boolean validate(CPoolEntry entry) {
        return !((ManagedHttpClientConnection) entry.getConnection()).isStale();
    }

    /* access modifiers changed from: protected */
    public void enumAvailable(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        super.enumAvailable(callback);
    }

    /* access modifiers changed from: protected */
    public void enumLeased(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        super.enumLeased(callback);
    }
}
