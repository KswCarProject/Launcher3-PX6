package org.apache.http.conn.scheme;

import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public final class Scheme {
    private final int defaultPort;
    private final boolean layered;
    private final String name;
    private final SchemeSocketFactory socketFactory;
    private String stringRep;

    public Scheme(String name2, int port, SchemeSocketFactory factory) {
        boolean z;
        Args.notNull(name2, "Scheme name");
        if (port <= 0 || port > 65535) {
            z = false;
        } else {
            z = true;
        }
        Args.check(z, "Port is invalid");
        Args.notNull(factory, "Socket factory");
        this.name = name2.toLowerCase(Locale.ENGLISH);
        this.defaultPort = port;
        if (factory instanceof SchemeLayeredSocketFactory) {
            this.layered = true;
            this.socketFactory = factory;
        } else if (factory instanceof LayeredSchemeSocketFactory) {
            this.layered = true;
            this.socketFactory = new SchemeLayeredSocketFactoryAdaptor2((LayeredSchemeSocketFactory) factory);
        } else {
            this.layered = false;
            this.socketFactory = factory;
        }
    }

    @Deprecated
    public Scheme(String name2, SocketFactory factory, int port) {
        boolean z;
        Args.notNull(name2, "Scheme name");
        Args.notNull(factory, "Socket factory");
        if (port <= 0 || port > 65535) {
            z = false;
        } else {
            z = true;
        }
        Args.check(z, "Port is invalid");
        this.name = name2.toLowerCase(Locale.ENGLISH);
        if (factory instanceof LayeredSocketFactory) {
            this.socketFactory = new SchemeLayeredSocketFactoryAdaptor((LayeredSocketFactory) factory);
            this.layered = true;
        } else {
            this.socketFactory = new SchemeSocketFactoryAdaptor(factory);
            this.layered = false;
        }
        this.defaultPort = port;
    }

    public final int getDefaultPort() {
        return this.defaultPort;
    }

    @Deprecated
    public final SocketFactory getSocketFactory() {
        if (this.socketFactory instanceof SchemeSocketFactoryAdaptor) {
            return ((SchemeSocketFactoryAdaptor) this.socketFactory).getFactory();
        }
        if (this.layered) {
            return new LayeredSocketFactoryAdaptor((LayeredSchemeSocketFactory) this.socketFactory);
        }
        return new SocketFactoryAdaptor(this.socketFactory);
    }

    public final SchemeSocketFactory getSchemeSocketFactory() {
        return this.socketFactory;
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isLayered() {
        return this.layered;
    }

    public final int resolvePort(int port) {
        return port <= 0 ? this.defaultPort : port;
    }

    public final String toString() {
        if (this.stringRep == null) {
            this.stringRep = this.name + ':' + Integer.toString(this.defaultPort);
        }
        return this.stringRep;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Scheme)) {
            return false;
        }
        Scheme that = (Scheme) obj;
        if (this.name.equals(that.name) && this.defaultPort == that.defaultPort && this.layered == that.layered) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, this.defaultPort), (Object) this.name), this.layered);
    }
}
