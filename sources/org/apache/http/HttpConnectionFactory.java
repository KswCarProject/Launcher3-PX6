package org.apache.http;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpConnection;

public interface HttpConnectionFactory<T extends HttpConnection> {
    T createConnection(Socket socket) throws IOException;
}
