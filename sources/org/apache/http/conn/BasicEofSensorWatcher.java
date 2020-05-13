package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.Args;

@Deprecated
public class BasicEofSensorWatcher implements EofSensorWatcher {
    protected final boolean attemptReuse;
    protected final ManagedClientConnection managedConn;

    public BasicEofSensorWatcher(ManagedClientConnection conn, boolean reuse) {
        Args.notNull(conn, "Connection");
        this.managedConn = conn;
        this.attemptReuse = reuse;
    }

    /* JADX INFO: finally extract failed */
    public boolean eofDetected(InputStream wrapped) throws IOException {
        try {
            if (this.attemptReuse) {
                wrapped.close();
                this.managedConn.markReusable();
            }
            this.managedConn.releaseConnection();
            return false;
        } catch (Throwable th) {
            this.managedConn.releaseConnection();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean streamClosed(InputStream wrapped) throws IOException {
        try {
            if (this.attemptReuse) {
                wrapped.close();
                this.managedConn.markReusable();
            }
            this.managedConn.releaseConnection();
            return false;
        } catch (Throwable th) {
            this.managedConn.releaseConnection();
            throw th;
        }
    }

    public boolean streamAbort(InputStream wrapped) throws IOException {
        this.managedConn.abortConnection();
        return false;
    }
}
