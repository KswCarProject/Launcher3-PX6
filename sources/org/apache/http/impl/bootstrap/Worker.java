package org.apache.http.impl.bootstrap;

import java.io.IOException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpService;

class Worker implements Runnable {
    private final HttpServerConnection conn;
    private final ExceptionLogger exceptionLogger;
    private final HttpService httpservice;

    Worker(HttpService httpservice2, HttpServerConnection conn2, ExceptionLogger exceptionLogger2) {
        this.httpservice = httpservice2;
        this.conn = conn2;
        this.exceptionLogger = exceptionLogger2;
    }

    public HttpServerConnection getConnection() {
        return this.conn;
    }

    public void run() {
        try {
            BasicHttpContext localContext = new BasicHttpContext();
            HttpCoreContext context = HttpCoreContext.adapt(localContext);
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
                localContext.clear();
            }
            this.conn.close();
            try {
                this.conn.shutdown();
            } catch (IOException ex) {
                this.exceptionLogger.log(ex);
            }
        } catch (Exception ex2) {
            this.exceptionLogger.log(ex2);
            try {
                this.conn.shutdown();
            } catch (IOException ex3) {
                this.exceptionLogger.log(ex3);
            }
        } catch (Throwable th) {
            try {
                this.conn.shutdown();
            } catch (IOException ex4) {
                this.exceptionLogger.log(ex4);
            }
            throw th;
        }
    }
}
