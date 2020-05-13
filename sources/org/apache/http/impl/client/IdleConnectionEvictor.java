package org.apache.http.impl.client;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.util.Args;

public final class IdleConnectionEvictor {
    private final HttpClientConnectionManager connectionManager;
    /* access modifiers changed from: private */
    public volatile Exception exception;
    /* access modifiers changed from: private */
    public final long maxIdleTimeMs;
    /* access modifiers changed from: private */
    public final long sleepTimeMs;
    private final Thread thread;
    private final ThreadFactory threadFactory;

    public IdleConnectionEvictor(final HttpClientConnectionManager connectionManager2, ThreadFactory threadFactory2, long sleepTime, TimeUnit sleepTimeUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this.connectionManager = (HttpClientConnectionManager) Args.notNull(connectionManager2, "Connection manager");
        this.threadFactory = threadFactory2 == null ? new DefaultThreadFactory() : threadFactory2;
        this.sleepTimeMs = sleepTimeUnit != null ? sleepTimeUnit.toMillis(sleepTime) : sleepTime;
        this.maxIdleTimeMs = maxIdleTimeUnit != null ? maxIdleTimeUnit.toMillis(maxIdleTime) : maxIdleTime;
        this.thread = this.threadFactory.newThread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(IdleConnectionEvictor.this.sleepTimeMs);
                        connectionManager2.closeExpiredConnections();
                        if (IdleConnectionEvictor.this.maxIdleTimeMs > 0) {
                            connectionManager2.closeIdleConnections(IdleConnectionEvictor.this.maxIdleTimeMs, TimeUnit.MILLISECONDS);
                        }
                    } catch (Exception ex) {
                        Exception unused = IdleConnectionEvictor.this.exception = ex;
                        return;
                    }
                }
            }
        });
    }

    public IdleConnectionEvictor(HttpClientConnectionManager connectionManager2, long sleepTime, TimeUnit sleepTimeUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this(connectionManager2, (ThreadFactory) null, sleepTime, sleepTimeUnit, maxIdleTime, maxIdleTimeUnit);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public IdleConnectionEvictor(HttpClientConnectionManager connectionManager2, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this(connectionManager2, (ThreadFactory) null, maxIdleTime > 0 ? maxIdleTime : 5, maxIdleTimeUnit != null ? maxIdleTimeUnit : TimeUnit.SECONDS, maxIdleTime, maxIdleTimeUnit);
    }

    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.thread.interrupt();
    }

    public boolean isRunning() {
        return this.thread.isAlive();
    }

    public void awaitTermination(long time, TimeUnit tunit) throws InterruptedException {
        Thread thread2 = this.thread;
        if (tunit == null) {
            tunit = TimeUnit.MILLISECONDS;
        }
        thread2.join(tunit.toMillis(time));
    }

    static class DefaultThreadFactory implements ThreadFactory {
        DefaultThreadFactory() {
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Connection evictor");
            t.setDaemon(true);
            return t;
        }
    }
}
