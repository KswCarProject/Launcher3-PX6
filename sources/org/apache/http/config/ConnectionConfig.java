package org.apache.http.config;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import org.apache.http.Consts;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ConnectionConfig implements Cloneable {
    public static final ConnectionConfig DEFAULT = new Builder().build();
    private final int bufferSize;
    private final Charset charset;
    private final int fragmentSizeHint;
    private final CodingErrorAction malformedInputAction;
    private final MessageConstraints messageConstraints;
    private final CodingErrorAction unmappableInputAction;

    ConnectionConfig(int bufferSize2, int fragmentSizeHint2, Charset charset2, CodingErrorAction malformedInputAction2, CodingErrorAction unmappableInputAction2, MessageConstraints messageConstraints2) {
        this.bufferSize = bufferSize2;
        this.fragmentSizeHint = fragmentSizeHint2;
        this.charset = charset2;
        this.malformedInputAction = malformedInputAction2;
        this.unmappableInputAction = unmappableInputAction2;
        this.messageConstraints = messageConstraints2;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public int getFragmentSizeHint() {
        return this.fragmentSizeHint;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public CodingErrorAction getMalformedInputAction() {
        return this.malformedInputAction;
    }

    public CodingErrorAction getUnmappableInputAction() {
        return this.unmappableInputAction;
    }

    public MessageConstraints getMessageConstraints() {
        return this.messageConstraints;
    }

    /* access modifiers changed from: protected */
    public ConnectionConfig clone() throws CloneNotSupportedException {
        return (ConnectionConfig) super.clone();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[bufferSize=").append(this.bufferSize).append(", fragmentSizeHint=").append(this.fragmentSizeHint).append(", charset=").append(this.charset).append(", malformedInputAction=").append(this.malformedInputAction).append(", unmappableInputAction=").append(this.unmappableInputAction).append(", messageConstraints=").append(this.messageConstraints).append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(ConnectionConfig config) {
        Args.notNull(config, "Connection config");
        return new Builder().setBufferSize(config.getBufferSize()).setCharset(config.getCharset()).setFragmentSizeHint(config.getFragmentSizeHint()).setMalformedInputAction(config.getMalformedInputAction()).setUnmappableInputAction(config.getUnmappableInputAction()).setMessageConstraints(config.getMessageConstraints());
    }

    public static class Builder {
        private int bufferSize;
        private Charset charset;
        private int fragmentSizeHint = -1;
        private CodingErrorAction malformedInputAction;
        private MessageConstraints messageConstraints;
        private CodingErrorAction unmappableInputAction;

        Builder() {
        }

        public Builder setBufferSize(int bufferSize2) {
            this.bufferSize = bufferSize2;
            return this;
        }

        public Builder setFragmentSizeHint(int fragmentSizeHint2) {
            this.fragmentSizeHint = fragmentSizeHint2;
            return this;
        }

        public Builder setCharset(Charset charset2) {
            this.charset = charset2;
            return this;
        }

        public Builder setMalformedInputAction(CodingErrorAction malformedInputAction2) {
            this.malformedInputAction = malformedInputAction2;
            if (malformedInputAction2 != null && this.charset == null) {
                this.charset = Consts.ASCII;
            }
            return this;
        }

        public Builder setUnmappableInputAction(CodingErrorAction unmappableInputAction2) {
            this.unmappableInputAction = unmappableInputAction2;
            if (unmappableInputAction2 != null && this.charset == null) {
                this.charset = Consts.ASCII;
            }
            return this;
        }

        public Builder setMessageConstraints(MessageConstraints messageConstraints2) {
            this.messageConstraints = messageConstraints2;
            return this;
        }

        public ConnectionConfig build() {
            int fragmentHintSize;
            Charset cs = this.charset;
            if (cs == null && !(this.malformedInputAction == null && this.unmappableInputAction == null)) {
                cs = Consts.ASCII;
            }
            int bufSize = this.bufferSize > 0 ? this.bufferSize : 8192;
            if (this.fragmentSizeHint >= 0) {
                fragmentHintSize = this.fragmentSizeHint;
            } else {
                fragmentHintSize = bufSize;
            }
            return new ConnectionConfig(bufSize, fragmentHintSize, cs, this.malformedInputAction, this.unmappableInputAction, this.messageConstraints);
        }
    }
}
