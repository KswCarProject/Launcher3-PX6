package org.apache.http.impl.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.HttpService;

public class HttpServer {
    private final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
    private final ExceptionLogger exceptionLogger;
    private final HttpService httpService;
    private final InetAddress ifAddress;
    private final ThreadPoolExecutor listenerExecutorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue(), new ThreadFactoryImpl("HTTP-listener-" + this.port));
    private final int port;
    private volatile RequestListener requestListener;
    private volatile ServerSocket serverSocket;
    private final ServerSocketFactory serverSocketFactory;
    private final SocketConfig socketConfig;
    private final SSLServerSetupHandler sslSetupHandler;
    private final AtomicReference<Status> status = new AtomicReference<>(Status.READY);
    private final WorkerPoolExecutor workerExecutorService = new WorkerPoolExecutor(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactoryImpl("HTTP-worker", this.workerThreads));
    private final ThreadGroup workerThreads = new ThreadGroup("HTTP-workers");

    enum Status {
        READY,
        ACTIVE,
        STOPPING
    }

    HttpServer(int port2, InetAddress ifAddress2, SocketConfig socketConfig2, ServerSocketFactory serverSocketFactory2, HttpService httpService2, HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory2, SSLServerSetupHandler sslSetupHandler2, ExceptionLogger exceptionLogger2) {
        this.port = port2;
        this.ifAddress = ifAddress2;
        this.socketConfig = socketConfig2;
        this.serverSocketFactory = serverSocketFactory2;
        this.httpService = httpService2;
        this.connectionFactory = connectionFactory2;
        this.sslSetupHandler = sslSetupHandler2;
        this.exceptionLogger = exceptionLogger2;
    }

    public InetAddress getInetAddress() {
        ServerSocket localSocket = this.serverSocket;
        if (localSocket != null) {
            return localSocket.getInetAddress();
        }
        return null;
    }

    public int getLocalPort() {
        ServerSocket localSocket = this.serverSocket;
        if (localSocket != null) {
            return localSocket.getLocalPort();
        }
        return -1;
    }

    public void start() throws IOException {
        if (this.status.compareAndSet(Status.READY, Status.ACTIVE)) {
            this.serverSocket = this.serverSocketFactory.createServerSocket(this.port, this.socketConfig.getBacklogSize(), this.ifAddress);
            this.serverSocket.setReuseAddress(this.socketConfig.isSoReuseAddress());
            if (this.socketConfig.getRcvBufSize() > 0) {
                this.serverSocket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
            }
            if (this.sslSetupHandler != null && (this.serverSocket instanceof SSLServerSocket)) {
                this.sslSetupHandler.initialize((SSLServerSocket) this.serverSocket);
            }
            this.requestListener = new RequestListener(this.socketConfig, this.serverSocket, this.httpService, this.connectionFactory, this.exceptionLogger, this.workerExecutorService);
            this.listenerExecutorService.execute(this.requestListener);
        }
    }

    public void stop() {
        if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPING)) {
            this.listenerExecutorService.shutdown();
            this.workerExecutorService.shutdown();
            RequestListener local = this.requestListener;
            if (local != null) {
                try {
                    local.terminate();
                } catch (IOException ex) {
                    this.exceptionLogger.log(ex);
                }
            }
            this.workerThreads.interrupt();
        }
    }

    public void awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
        this.workerExecutorService.awaitTermination(timeout, timeUnit);
    }

    public void shutdown(long gracePeriod, TimeUnit timeUnit) {
        stop();
        if (gracePeriod > 0) {
            try {
                awaitTermination(gracePeriod, timeUnit);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (Worker worker : this.workerExecutorService.getWorkers()) {
            try {
                worker.getConnection().shutdown();
            } catch (IOException ex) {
                this.exceptionLogger.log(ex);
            }
        }
    }
}
