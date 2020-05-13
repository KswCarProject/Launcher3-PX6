package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@Deprecated
class LayeredSocketFactoryAdaptor extends SocketFactoryAdaptor implements LayeredSocketFactory {
    private final LayeredSchemeSocketFactory factory;

    LayeredSocketFactoryAdaptor(LayeredSchemeSocketFactory factory2) {
        super(factory2);
        this.factory = factory2;
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return this.factory.createLayeredSocket(socket, host, port, autoClose);
    }
}
