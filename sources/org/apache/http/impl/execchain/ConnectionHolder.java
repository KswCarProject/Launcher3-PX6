package org.apache.http.impl.execchain;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.HttpClientConnectionManager;

@Contract(threading = ThreadingBehavior.SAFE)
class ConnectionHolder implements ConnectionReleaseTrigger, Cancellable, Closeable {
    private final Log log;
    private final HttpClientConnection managedConn;
    private final HttpClientConnectionManager manager;
    private final AtomicBoolean released = new AtomicBoolean(false);
    private volatile boolean reusable;
    private volatile Object state;
    private volatile TimeUnit tunit;
    private volatile long validDuration;

    public ConnectionHolder(Log log2, HttpClientConnectionManager manager2, HttpClientConnection managedConn2) {
        this.log = log2;
        this.manager = manager2;
        this.managedConn = managedConn2;
    }

    public boolean isReusable() {
        return this.reusable;
    }

    public void markReusable() {
        this.reusable = true;
    }

    public void markNonReusable() {
        this.reusable = false;
    }

    public void setState(Object state2) {
        this.state = state2;
    }

    public void setValidFor(long duration, TimeUnit tunit2) {
        synchronized (this.managedConn) {
            this.validDuration = duration;
            this.tunit = tunit2;
        }
    }

    private void releaseConnection(boolean reusable2) {
        if (this.released.compareAndSet(false, true)) {
            synchronized (this.managedConn) {
                if (reusable2) {
                    this.manager.releaseConnection(this.managedConn, this.state, this.validDuration, this.tunit);
                } else {
                    try {
                        this.managedConn.close();
                        this.log.debug("Connection discarded");
                        this.manager.releaseConnection(this.managedConn, (Object) null, 0, TimeUnit.MILLISECONDS);
                    } catch (IOException ex) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(ex.getMessage(), ex);
                        }
                        this.manager.releaseConnection(this.managedConn, (Object) null, 0, TimeUnit.MILLISECONDS);
                    } catch (Throwable th) {
                        Throwable th2 = th;
                        this.manager.releaseConnection(this.managedConn, (Object) null, 0, TimeUnit.MILLISECONDS);
                        throw th2;
                    }
                }
            }
        }
    }

    public void releaseConnection() {
        releaseConnection(this.reusable);
    }

    public void abortConnection() {
        if (this.released.compareAndSet(false, true)) {
            synchronized (this.managedConn) {
                try {
                    this.managedConn.shutdown();
                    this.log.debug("Connection discarded");
                    this.manager.releaseConnection(this.managedConn, (Object) null, 0, TimeUnit.MILLISECONDS);
                } catch (IOException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                    this.manager.releaseConnection(this.managedConn, (Object) null, 0, TimeUnit.MILLISECONDS);
                } catch (Throwable th) {
                    Throwable th2 = th;
                    this.manager.releaseConnection(this.managedConn, (Object) null, 0, TimeUnit.MILLISECONDS);
                    throw th2;
                }
            }
        }
    }

    public boolean cancel() {
        boolean alreadyReleased = this.released.get();
        this.log.debug("Cancelling request execution");
        abortConnection();
        return !alreadyReleased;
    }

    public boolean isReleased() {
        return this.released.get();
    }

    public void close() throws IOException {
        releaseConnection(false);
    }
}