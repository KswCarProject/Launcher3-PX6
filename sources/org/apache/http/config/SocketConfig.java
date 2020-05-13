package org.apache.http.config;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class SocketConfig implements Cloneable {
    public static final SocketConfig DEFAULT = new Builder().build();
    private final int backlogSize;
    private final int rcvBufSize;
    private final int sndBufSize;
    private final boolean soKeepAlive;
    private final int soLinger;
    private final boolean soReuseAddress;
    private final int soTimeout;
    private final boolean tcpNoDelay;

    SocketConfig(int soTimeout2, boolean soReuseAddress2, int soLinger2, boolean soKeepAlive2, boolean tcpNoDelay2, int sndBufSize2, int rcvBufSize2, int backlogSize2) {
        this.soTimeout = soTimeout2;
        this.soReuseAddress = soReuseAddress2;
        this.soLinger = soLinger2;
        this.soKeepAlive = soKeepAlive2;
        this.tcpNoDelay = tcpNoDelay2;
        this.sndBufSize = sndBufSize2;
        this.rcvBufSize = rcvBufSize2;
        this.backlogSize = backlogSize2;
    }

    public int getSoTimeout() {
        return this.soTimeout;
    }

    public boolean isSoReuseAddress() {
        return this.soReuseAddress;
    }

    public int getSoLinger() {
        return this.soLinger;
    }

    public boolean isSoKeepAlive() {
        return this.soKeepAlive;
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public int getSndBufSize() {
        return this.sndBufSize;
    }

    public int getRcvBufSize() {
        return this.rcvBufSize;
    }

    public int getBacklogSize() {
        return this.backlogSize;
    }

    /* access modifiers changed from: protected */
    public SocketConfig clone() throws CloneNotSupportedException {
        return (SocketConfig) super.clone();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[soTimeout=").append(this.soTimeout).append(", soReuseAddress=").append(this.soReuseAddress).append(", soLinger=").append(this.soLinger).append(", soKeepAlive=").append(this.soKeepAlive).append(", tcpNoDelay=").append(this.tcpNoDelay).append(", sndBufSize=").append(this.sndBufSize).append(", rcvBufSize=").append(this.rcvBufSize).append(", backlogSize=").append(this.backlogSize).append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(SocketConfig config) {
        Args.notNull(config, "Socket config");
        return new Builder().setSoTimeout(config.getSoTimeout()).setSoReuseAddress(config.isSoReuseAddress()).setSoLinger(config.getSoLinger()).setSoKeepAlive(config.isSoKeepAlive()).setTcpNoDelay(config.isTcpNoDelay()).setSndBufSize(config.getSndBufSize()).setRcvBufSize(config.getRcvBufSize()).setBacklogSize(config.getBacklogSize());
    }

    public static class Builder {
        private int backlogSize;
        private int rcvBufSize;
        private int sndBufSize;
        private boolean soKeepAlive;
        private int soLinger = -1;
        private boolean soReuseAddress;
        private int soTimeout;
        private boolean tcpNoDelay = true;

        Builder() {
        }

        public Builder setSoTimeout(int soTimeout2) {
            this.soTimeout = soTimeout2;
            return this;
        }

        public Builder setSoReuseAddress(boolean soReuseAddress2) {
            this.soReuseAddress = soReuseAddress2;
            return this;
        }

        public Builder setSoLinger(int soLinger2) {
            this.soLinger = soLinger2;
            return this;
        }

        public Builder setSoKeepAlive(boolean soKeepAlive2) {
            this.soKeepAlive = soKeepAlive2;
            return this;
        }

        public Builder setTcpNoDelay(boolean tcpNoDelay2) {
            this.tcpNoDelay = tcpNoDelay2;
            return this;
        }

        public Builder setSndBufSize(int sndBufSize2) {
            this.sndBufSize = sndBufSize2;
            return this;
        }

        public Builder setRcvBufSize(int rcvBufSize2) {
            this.rcvBufSize = rcvBufSize2;
            return this;
        }

        public Builder setBacklogSize(int backlogSize2) {
            this.backlogSize = backlogSize2;
            return this;
        }

        public SocketConfig build() {
            return new SocketConfig(this.soTimeout, this.soReuseAddress, this.soLinger, this.soKeepAlive, this.tcpNoDelay, this.sndBufSize, this.rcvBufSize, this.backlogSize);
        }
    }
}
