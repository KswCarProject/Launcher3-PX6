package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.AbstractConnPool;
import org.apache.http.pool.ConnFactory;

@Deprecated
class HttpConnPool extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry> {
    private static final AtomicLong COUNTER = new AtomicLong();
    private final Log log;
    private final long timeToLive;
    private final TimeUnit tunit;

    public HttpConnPool(Log log2, ClientConnectionOperator connOperator, int defaultMaxPerRoute, int maxTotal, long timeToLive2, TimeUnit tunit2) {
        super(new InternalConnFactory(connOperator), defaultMaxPerRoute, maxTotal);
        this.log = log2;
        this.timeToLive = timeToLive2;
        this.tunit = tunit2;
    }

    /* access modifiers changed from: protected */
    public HttpPoolEntry createEntry(HttpRoute route, OperatedClientConnection conn) {
        return new HttpPoolEntry(this.log, Long.toString(COUNTER.getAndIncrement()), route, conn, this.timeToLive, this.tunit);
    }

    static class InternalConnFactory implements ConnFactory<HttpRoute, OperatedClientConnection> {
        private final ClientConnectionOperator connOperator;

        InternalConnFactory(ClientConnectionOperator connOperator2) {
            this.connOperator = connOperator2;
        }

        public OperatedClientConnection create(HttpRoute route) throws IOException {
            return this.connOperator.createConnection();
        }
    }
}
