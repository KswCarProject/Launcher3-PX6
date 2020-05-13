package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.Args;

public class EofSensorInputStream extends InputStream implements ConnectionReleaseTrigger {
    private final EofSensorWatcher eofWatcher;
    private boolean selfClosed = false;
    protected InputStream wrappedStream;

    public EofSensorInputStream(InputStream in, EofSensorWatcher watcher) {
        Args.notNull(in, "Wrapped stream");
        this.wrappedStream = in;
        this.eofWatcher = watcher;
    }

    /* access modifiers changed from: package-private */
    public boolean isSelfClosed() {
        return this.selfClosed;
    }

    /* access modifiers changed from: package-private */
    public InputStream getWrappedStream() {
        return this.wrappedStream;
    }

    /* access modifiers changed from: protected */
    public boolean isReadAllowed() throws IOException {
        if (!this.selfClosed) {
            return this.wrappedStream != null;
        }
        throw new IOException("Attempted read on closed stream.");
    }

    public int read() throws IOException {
        if (!isReadAllowed()) {
            return -1;
        }
        try {
            int l = this.wrappedStream.read();
            checkEOF(l);
            return l;
        } catch (IOException ex) {
            checkAbort();
            throw ex;
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (!isReadAllowed()) {
            return -1;
        }
        try {
            int l = this.wrappedStream.read(b, off, len);
            checkEOF(l);
            return l;
        } catch (IOException ex) {
            checkAbort();
            throw ex;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int available() throws IOException {
        if (!isReadAllowed()) {
            return 0;
        }
        try {
            return this.wrappedStream.available();
        } catch (IOException ex) {
            checkAbort();
            throw ex;
        }
    }

    public void close() throws IOException {
        this.selfClosed = true;
        checkClose();
    }

    /* access modifiers changed from: protected */
    public void checkEOF(int eof) throws IOException {
        InputStream toCheckStream = this.wrappedStream;
        if (toCheckStream != null && eof < 0) {
            boolean scws = true;
            try {
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.eofDetected(toCheckStream);
                }
                if (scws) {
                    toCheckStream.close();
                }
            } finally {
                this.wrappedStream = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkClose() throws IOException {
        InputStream toCloseStream = this.wrappedStream;
        if (toCloseStream != null) {
            boolean scws = true;
            try {
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.streamClosed(toCloseStream);
                }
                if (scws) {
                    toCloseStream.close();
                }
            } finally {
                this.wrappedStream = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkAbort() throws IOException {
        InputStream toAbortStream = this.wrappedStream;
        if (toAbortStream != null) {
            boolean scws = true;
            try {
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.streamAbort(toAbortStream);
                }
                if (scws) {
                    toAbortStream.close();
                }
            } finally {
                this.wrappedStream = null;
            }
        }
    }

    public void releaseConnection() throws IOException {
        close();
    }

    public void abortConnection() throws IOException {
        this.selfClosed = true;
        checkAbort();
    }
}
