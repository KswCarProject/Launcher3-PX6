package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.entity.HttpEntityWrapper;

class ResponseEntityProxy extends HttpEntityWrapper implements EofSensorWatcher {
    private final ConnectionHolder connHolder;

    public static void enchance(HttpResponse response, ConnectionHolder connHolder2) {
        HttpEntity entity = response.getEntity();
        if (entity != null && entity.isStreaming() && connHolder2 != null) {
            response.setEntity(new ResponseEntityProxy(entity, connHolder2));
        }
    }

    ResponseEntityProxy(HttpEntity entity, ConnectionHolder connHolder2) {
        super(entity);
        this.connHolder = connHolder2;
    }

    private void cleanup() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.close();
        }
    }

    private void abortConnection() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.abortConnection();
        }
    }

    public void releaseConnection() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.releaseConnection();
        }
    }

    public boolean isRepeatable() {
        return false;
    }

    public InputStream getContent() throws IOException {
        return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
    }

    @Deprecated
    public void consumeContent() throws IOException {
        releaseConnection();
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream != null) {
            try {
                this.wrappedEntity.writeTo(outstream);
            } catch (IOException ex) {
                abortConnection();
                throw ex;
            } catch (RuntimeException ex2) {
                abortConnection();
                throw ex2;
            } catch (Throwable th) {
                cleanup();
                throw th;
            }
        }
        releaseConnection();
        cleanup();
    }

    public boolean eofDetected(InputStream wrapped) throws IOException {
        if (wrapped != null) {
            try {
                wrapped.close();
            } catch (IOException ex) {
                abortConnection();
                throw ex;
            } catch (RuntimeException ex2) {
                abortConnection();
                throw ex2;
            } catch (Throwable th) {
                cleanup();
                throw th;
            }
        }
        releaseConnection();
        cleanup();
        return false;
    }

    public boolean streamClosed(InputStream wrapped) throws IOException {
        boolean open;
        try {
            if (this.connHolder == null || this.connHolder.isReleased()) {
                open = false;
            } else {
                open = true;
            }
            if (wrapped != null) {
                wrapped.close();
            }
            releaseConnection();
        } catch (SocketException ex) {
            if (open) {
                throw ex;
            }
        } catch (IOException ex2) {
            try {
                abortConnection();
                throw ex2;
            } catch (Throwable th) {
                cleanup();
                throw th;
            }
        } catch (RuntimeException ex3) {
            abortConnection();
            throw ex3;
        }
        cleanup();
        return false;
    }

    public boolean streamAbort(InputStream wrapped) throws IOException {
        cleanup();
        return false;
    }

    public String toString() {
        return "ResponseEntityProxy{" + this.wrappedEntity + '}';
    }
}
