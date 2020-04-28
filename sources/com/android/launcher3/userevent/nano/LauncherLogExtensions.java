package com.android.launcher3.userevent.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;

public interface LauncherLogExtensions {

    public static final class LauncherEventExtension extends MessageNano {
        private static volatile LauncherEventExtension[] _emptyArray;

        public static LauncherEventExtension[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherEventExtension[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherEventExtension() {
            clear();
        }

        public LauncherEventExtension clear() {
            this.cachedSize = -1;
            return this;
        }

        /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: 
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
            	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
            */
        public com.android.launcher3.userevent.nano.LauncherLogExtensions.LauncherEventExtension mergeFrom(com.google.protobuf.nano.CodedInputByteBufferNano r3) throws java.io.IOException {
            /*
                r2 = this;
            L_0x0000:
                int r0 = r3.readTag()
                if (r0 == 0) goto L_0x000e
                boolean r1 = com.google.protobuf.nano.WireFormatNano.parseUnknownField(r3, r0)
                if (r1 != 0) goto L_0x000d
                return r2
            L_0x000d:
                goto L_0x0000
            L_0x000e:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.userevent.nano.LauncherLogExtensions.LauncherEventExtension.mergeFrom(com.google.protobuf.nano.CodedInputByteBufferNano):com.android.launcher3.userevent.nano.LauncherLogExtensions$LauncherEventExtension");
        }

        public static LauncherEventExtension parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LauncherEventExtension) MessageNano.mergeFrom(new LauncherEventExtension(), data);
        }

        public static LauncherEventExtension parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LauncherEventExtension().mergeFrom(input);
        }
    }

    public static final class TargetExtension extends MessageNano {
        private static volatile TargetExtension[] _emptyArray;

        public static TargetExtension[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TargetExtension[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TargetExtension() {
            clear();
        }

        public TargetExtension clear() {
            this.cachedSize = -1;
            return this;
        }

        /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: 
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
            	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
            */
        public com.android.launcher3.userevent.nano.LauncherLogExtensions.TargetExtension mergeFrom(com.google.protobuf.nano.CodedInputByteBufferNano r3) throws java.io.IOException {
            /*
                r2 = this;
            L_0x0000:
                int r0 = r3.readTag()
                if (r0 == 0) goto L_0x000e
                boolean r1 = com.google.protobuf.nano.WireFormatNano.parseUnknownField(r3, r0)
                if (r1 != 0) goto L_0x000d
                return r2
            L_0x000d:
                goto L_0x0000
            L_0x000e:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.userevent.nano.LauncherLogExtensions.TargetExtension.mergeFrom(com.google.protobuf.nano.CodedInputByteBufferNano):com.android.launcher3.userevent.nano.LauncherLogExtensions$TargetExtension");
        }

        public static TargetExtension parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (TargetExtension) MessageNano.mergeFrom(new TargetExtension(), data);
        }

        public static TargetExtension parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new TargetExtension().mergeFrom(input);
        }
    }
}
