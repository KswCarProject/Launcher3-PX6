package org.apache.http.impl.execchain;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BackoffStrategyExec implements ClientExecChain {
    private final BackoffManager backoffManager;
    private final ConnectionBackoffStrategy connectionBackoffStrategy;
    private final ClientExecChain requestExecutor;

    public BackoffStrategyExec(ClientExecChain requestExecutor2, ConnectionBackoffStrategy connectionBackoffStrategy2, BackoffManager backoffManager2) {
        Args.notNull(requestExecutor2, "HTTP client request executor");
        Args.notNull(connectionBackoffStrategy2, "Connection backoff strategy");
        Args.notNull(backoffManager2, "Backoff manager");
        this.requestExecutor = requestExecutor2;
        this.connectionBackoffStrategy = connectionBackoffStrategy2;
        this.backoffManager = backoffManager2;
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        CloseableHttpResponse out = null;
        try {
            CloseableHttpResponse out2 = this.requestExecutor.execute(route, request, context, execAware);
            if (this.connectionBackoffStrategy.shouldBackoff((HttpResponse) out2)) {
                this.backoffManager.backOff(route);
            } else {
                this.backoffManager.probe(route);
            }
            return out2;
        } catch (Exception ex) {
            if (out != null) {
                out.close();
            }
            if (this.connectionBackoffStrategy.shouldBackoff((Throwable) ex)) {
                this.backoffManager.backOff(route);
            }
            if (ex instanceof RuntimeException) {
                throw ((RuntimeException) ex);
            } else if (ex instanceof HttpException) {
                throw ((HttpException) ex);
            } else if (ex instanceof IOException) {
                throw ((IOException) ex);
            } else {
                throw new UndeclaredThrowableException(ex);
            }
        }
    }
}
