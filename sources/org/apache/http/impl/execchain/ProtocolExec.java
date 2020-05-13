package org.apache.http.impl.execchain;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ProtocolExec implements ClientExecChain {
    private final HttpProcessor httpProcessor;
    private final Log log = LogFactory.getLog(getClass());
    private final ClientExecChain requestExecutor;

    public ProtocolExec(ClientExecChain requestExecutor2, HttpProcessor httpProcessor2) {
        Args.notNull(requestExecutor2, "HTTP client request executor");
        Args.notNull(httpProcessor2, "HTTP protocol processor");
        this.requestExecutor = requestExecutor2;
        this.httpProcessor = httpProcessor2;
    }

    /* access modifiers changed from: package-private */
    public void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route) throws ProtocolException {
        URI uri = request.getURI();
        if (uri != null) {
            try {
                request.setURI(URIUtils.rewriteURIForRoute(uri, route));
            } catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid URI: " + uri, ex);
            }
        }
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        String userinfo;
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        HttpRequest original = request.getOriginal();
        URI uri = null;
        if (original instanceof HttpUriRequest) {
            uri = ((HttpUriRequest) original).getURI();
        } else {
            String uriString = original.getRequestLine().getUri();
            try {
                uri = URI.create(uriString);
            } catch (IllegalArgumentException ex) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Unable to parse '" + uriString + "' as a valid URI; " + "request URI and Host header may be inconsistent", ex);
                }
            }
        }
        request.setURI(uri);
        rewriteRequestURI(request, route);
        HttpHost virtualHost = (HttpHost) request.getParams().getParameter(ClientPNames.VIRTUAL_HOST);
        if (virtualHost != null && virtualHost.getPort() == -1) {
            int port = route.getTargetHost().getPort();
            if (port != -1) {
                virtualHost = new HttpHost(virtualHost.getHostName(), port, virtualHost.getSchemeName());
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Using virtual host" + virtualHost);
            }
        }
        HttpHost target = null;
        if (virtualHost != null) {
            target = virtualHost;
        } else if (!(uri == null || !uri.isAbsolute() || uri.getHost() == null)) {
            target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        if (target == null) {
            target = request.getTarget();
        }
        if (target == null) {
            target = route.getTargetHost();
        }
        if (!(uri == null || (userinfo = uri.getUserInfo()) == null)) {
            CredentialsProvider credsProvider = context.getCredentialsProvider();
            if (credsProvider == null) {
                credsProvider = new BasicCredentialsProvider();
                context.setCredentialsProvider(credsProvider);
            }
            credsProvider.setCredentials(new AuthScope(target), new UsernamePasswordCredentials(userinfo));
        }
        context.setAttribute("http.target_host", target);
        context.setAttribute("http.route", route);
        context.setAttribute("http.request", request);
        this.httpProcessor.process(request, context);
        CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
        try {
            context.setAttribute("http.response", response);
            this.httpProcessor.process(response, context);
            return response;
        } catch (RuntimeException ex2) {
            response.close();
            throw ex2;
        } catch (IOException ex3) {
            response.close();
            throw ex3;
        } catch (HttpException ex4) {
            response.close();
            throw ex4;
        }
    }
}
