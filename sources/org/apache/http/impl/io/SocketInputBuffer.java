package org.apache.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.io.EofSensor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class SocketInputBuffer extends AbstractSessionInputBuffer implements EofSensor {
    private boolean eof = false;
    private final Socket socket;

    public SocketInputBuffer(Socket socket2, int buffersize, HttpParams params) throws IOException {
        Args.notNull(socket2, "Socket");
        this.socket = socket2;
        int n = buffersize;
        n = n < 0 ? socket2.getReceiveBufferSize() : n;
        init(socket2.getInputStream(), n < 1024 ? 1024 : n, params);
    }

    /* access modifiers changed from: protected */
    public int fillBuffer() throws IOException {
        int i = super.fillBuffer();
        this.eof = i == -1;
        return i;
    }

    /* JADX INFO: finally extract failed */
    public boolean isDataAvailable(int timeout) throws IOException {
        boolean result = hasBufferedData();
        if (!result) {
            int oldtimeout = this.socket.getSoTimeout();
            try {
                this.socket.setSoTimeout(timeout);
                fillBuffer();
                result = hasBufferedData();
                this.socket.setSoTimeout(oldtimeout);
            } catch (Throwable th) {
                this.socket.setSoTimeout(oldtimeout);
                throw th;
            }
        }
        return result;
    }

    public boolean isEof() {
        return this.eof;
    }
}
