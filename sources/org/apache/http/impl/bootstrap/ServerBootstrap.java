package org.apache.http.impl.bootstrap;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.HttpExpectationVerifier;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;

public class ServerBootstrap {
    private ConnectionReuseStrategy connStrategy;
    private ConnectionConfig connectionConfig;
    private HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
    private ExceptionLogger exceptionLogger;
    private HttpExpectationVerifier expectationVerifier;
    private Map<String, HttpRequestHandler> handlerMap;
    private HttpRequestHandlerMapper handlerMapper;
    private HttpProcessor httpProcessor;
    private int listenerPort;
    private InetAddress localAddress;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private HttpResponseFactory responseFactory;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private String serverInfo;
    private ServerSocketFactory serverSocketFactory;
    private SocketConfig socketConfig;
    private SSLContext sslContext;
    private SSLServerSetupHandler sslSetupHandler;

    private ServerBootstrap() {
    }

    public static ServerBootstrap bootstrap() {
        return new ServerBootstrap();
    }

    public final ServerBootstrap setListenerPort(int listenerPort2) {
        this.listenerPort = listenerPort2;
        return this;
    }

    public final ServerBootstrap setLocalAddress(InetAddress localAddress2) {
        this.localAddress = localAddress2;
        return this;
    }

    public final ServerBootstrap setSocketConfig(SocketConfig socketConfig2) {
        this.socketConfig = socketConfig2;
        return this;
    }

    public final ServerBootstrap setConnectionConfig(ConnectionConfig connectionConfig2) {
        this.connectionConfig = connectionConfig2;
        return this;
    }

    public final ServerBootstrap setHttpProcessor(HttpProcessor httpProcessor2) {
        this.httpProcessor = httpProcessor2;
        return this;
    }

    public final ServerBootstrap addInterceptorFirst(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseFirst == null) {
                this.responseFirst = new LinkedList<>();
            }
            this.responseFirst.addFirst(itcp);
        }
        return this;
    }

    public final ServerBootstrap addInterceptorLast(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseLast == null) {
                this.responseLast = new LinkedList<>();
            }
            this.responseLast.addLast(itcp);
        }
        return this;
    }

    public final ServerBootstrap addInterceptorFirst(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestFirst == null) {
                this.requestFirst = new LinkedList<>();
            }
            this.requestFirst.addFirst(itcp);
        }
        return this;
    }

    public final ServerBootstrap addInterceptorLast(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestLast == null) {
                this.requestLast = new LinkedList<>();
            }
            this.requestLast.addLast(itcp);
        }
        return this;
    }

    public final ServerBootstrap setServerInfo(String serverInfo2) {
        this.serverInfo = serverInfo2;
        return this;
    }

    public final ServerBootstrap setConnectionReuseStrategy(ConnectionReuseStrategy connStrategy2) {
        this.connStrategy = connStrategy2;
        return this;
    }

    public final ServerBootstrap setResponseFactory(HttpResponseFactory responseFactory2) {
        this.responseFactory = responseFactory2;
        return this;
    }

    public final ServerBootstrap setHandlerMapper(HttpRequestHandlerMapper handlerMapper2) {
        this.handlerMapper = handlerMapper2;
        return this;
    }

    public final ServerBootstrap registerHandler(String pattern, HttpRequestHandler handler) {
        if (!(pattern == null || handler == null)) {
            if (this.handlerMap == null) {
                this.handlerMap = new HashMap();
            }
            this.handlerMap.put(pattern, handler);
        }
        return this;
    }

    public final ServerBootstrap setExpectationVerifier(HttpExpectationVerifier expectationVerifier2) {
        this.expectationVerifier = expectationVerifier2;
        return this;
    }

    public final ServerBootstrap setConnectionFactory(HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory2) {
        this.connectionFactory = connectionFactory2;
        return this;
    }

    public final ServerBootstrap setSslSetupHandler(SSLServerSetupHandler sslSetupHandler2) {
        this.sslSetupHandler = sslSetupHandler2;
        return this;
    }

    public final ServerBootstrap setServerSocketFactory(ServerSocketFactory serverSocketFactory2) {
        this.serverSocketFactory = serverSocketFactory2;
        return this;
    }

    public final ServerBootstrap setSslContext(SSLContext sslContext2) {
        this.sslContext = sslContext2;
        return this;
    }

    public final ServerBootstrap setExceptionLogger(ExceptionLogger exceptionLogger2) {
        this.exceptionLogger = exceptionLogger2;
        return this;
    }

    public HttpServer create() {
        int i;
        SocketConfig socketConfig2;
        HttpProcessor httpProcessorCopy = this.httpProcessor;
        if (httpProcessorCopy == null) {
            HttpProcessorBuilder b = HttpProcessorBuilder.create();
            if (this.requestFirst != null) {
                Iterator i$ = this.requestFirst.iterator();
                while (i$.hasNext()) {
                    b.addFirst((HttpRequestInterceptor) i$.next());
                }
            }
            if (this.responseFirst != null) {
                Iterator i$2 = this.responseFirst.iterator();
                while (i$2.hasNext()) {
                    b.addFirst((HttpResponseInterceptor) i$2.next());
                }
            }
            String serverInfoCopy = this.serverInfo;
            if (serverInfoCopy == null) {
                serverInfoCopy = "Apache-HttpCore/1.1";
            }
            b.addAll(new ResponseDate(), new ResponseServer(serverInfoCopy), new ResponseContent(), new ResponseConnControl());
            if (this.requestLast != null) {
                Iterator i$3 = this.requestLast.iterator();
                while (i$3.hasNext()) {
                    b.addLast((HttpRequestInterceptor) i$3.next());
                }
            }
            if (this.responseLast != null) {
                Iterator i$4 = this.responseLast.iterator();
                while (i$4.hasNext()) {
                    b.addLast((HttpResponseInterceptor) i$4.next());
                }
            }
            httpProcessorCopy = b.build();
        }
        HttpRequestHandlerMapper handlerMapperCopy = this.handlerMapper;
        if (handlerMapperCopy == null) {
            UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
            if (this.handlerMap != null) {
                for (Map.Entry<String, HttpRequestHandler> entry : this.handlerMap.entrySet()) {
                    reqistry.register(entry.getKey(), entry.getValue());
                }
            }
            handlerMapperCopy = reqistry;
        }
        ConnectionReuseStrategy connStrategyCopy = this.connStrategy;
        if (connStrategyCopy == null) {
            connStrategyCopy = DefaultConnectionReuseStrategy.INSTANCE;
        }
        HttpResponseFactory responseFactoryCopy = this.responseFactory;
        if (responseFactoryCopy == null) {
            responseFactoryCopy = DefaultHttpResponseFactory.INSTANCE;
        }
        HttpService httpService = new HttpService(httpProcessorCopy, connStrategyCopy, responseFactoryCopy, handlerMapperCopy, this.expectationVerifier);
        ServerSocketFactory serverSocketFactoryCopy = this.serverSocketFactory;
        if (serverSocketFactoryCopy == null) {
            if (this.sslContext != null) {
                serverSocketFactoryCopy = this.sslContext.getServerSocketFactory();
            } else {
                serverSocketFactoryCopy = ServerSocketFactory.getDefault();
            }
        }
        HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactoryCopy = this.connectionFactory;
        if (connectionFactoryCopy == null) {
            if (this.connectionConfig != null) {
                connectionFactoryCopy = new DefaultBHttpServerConnectionFactory(this.connectionConfig);
            } else {
                connectionFactoryCopy = DefaultBHttpServerConnectionFactory.INSTANCE;
            }
        }
        ExceptionLogger exceptionLoggerCopy = this.exceptionLogger;
        if (exceptionLoggerCopy == null) {
            exceptionLoggerCopy = ExceptionLogger.NO_OP;
        }
        if (this.listenerPort > 0) {
            i = this.listenerPort;
        } else {
            i = 0;
        }
        InetAddress inetAddress = this.localAddress;
        if (this.socketConfig != null) {
            socketConfig2 = this.socketConfig;
        } else {
            socketConfig2 = SocketConfig.DEFAULT;
        }
        return new HttpServer(i, inetAddress, socketConfig2, serverSocketFactoryCopy, httpService, connectionFactoryCopy, this.sslSetupHandler, exceptionLoggerCopy);
    }
}
