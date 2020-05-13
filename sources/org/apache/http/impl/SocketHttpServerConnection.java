package org.apache.http.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.apache.http.HttpInetConnection;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public class SocketHttpServerConnection extends AbstractHttpServerConnection implements HttpInetConnection {
    private volatile boolean open;
    private volatile Socket socket = null;

    /* access modifiers changed from: protected */
    public void assertNotOpen() {
        Asserts.check(!this.open, "Connection is already open");
    }

    /* access modifiers changed from: protected */
    public void assertOpen() {
        Asserts.check(this.open, "Connection is not open");
    }

    /* access modifiers changed from: protected */
    public SessionInputBuffer createSessionInputBuffer(Socket socket2, int buffersize, HttpParams params) throws IOException {
        return new SocketInputBuffer(socket2, buffersize, params);
    }

    /* access modifiers changed from: protected */
    public SessionOutputBuffer createSessionOutputBuffer(Socket socket2, int buffersize, HttpParams params) throws IOException {
        return new SocketOutputBuffer(socket2, buffersize, params);
    }

    /* access modifiers changed from: protected */
    public void bind(Socket socket2, HttpParams params) throws IOException {
        Args.notNull(socket2, "Socket");
        Args.notNull(params, "HTTP parameters");
        this.socket = socket2;
        int buffersize = params.getIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, -1);
        init(createSessionInputBuffer(socket2, buffersize, params), createSessionOutputBuffer(socket2, buffersize, params), params);
        this.open = true;
    }

    /* access modifiers changed from: protected */
    public Socket getSocket() {
        return this.socket;
    }

    public boolean isOpen() {
        return this.open;
    }

    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }

    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }

    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }

    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }

    public void setSocketTimeout(int timeout) {
        assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            } catch (SocketException e) {
            }
        }
    }

    public int getSocketTimeout() {
        if (this.socket == null) {
            return -1;
        }
        try {
            return this.socket.getSoTimeout();
        } catch (SocketException e) {
            return -1;
        }
    }

    public void shutdown() throws IOException {
        this.open = false;
        Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }

    public void close() throws IOException {
        if (this.open) {
            this.open = false;
            this.open = false;
            Socket sock = this.socket;
            try {
                doFlush();
                try {
                    sock.shutdownOutput();
                } catch (IOException e) {
                }
                try {
                    sock.shutdownInput();
                } catch (IOException | UnsupportedOperationException e2) {
                }
            } finally {
                sock.close();
            }
        }
    }

    private static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress) socketAddress;
            buffer.append(addr.getAddress() != null ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
            return;
        }
        buffer.append(socketAddress);
    }

    public String toString() {
        if (this.socket == null) {
            return super.toString();
        }
        StringBuilder buffer = new StringBuilder();
        SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
        SocketAddress localAddress = this.socket.getLocalSocketAddress();
        if (!(remoteAddress == null || localAddress == null)) {
            formatAddress(buffer, localAddress);
            buffer.append("<->");
            formatAddress(buffer, remoteAddress);
        }
        return buffer.toString();
    }
}
