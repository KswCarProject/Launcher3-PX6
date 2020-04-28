package com.android.launcher3.model.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface LauncherDumpProto {

    public interface ContainerType {
        public static final int FOLDER = 3;
        public static final int HOTSEAT = 2;
        public static final int UNKNOWN_CONTAINERTYPE = 0;
        public static final int WORKSPACE = 1;
    }

    public interface ItemType {
        public static final int APP_ICON = 1;
        public static final int SHORTCUT = 3;
        public static final int UNKNOWN_ITEMTYPE = 0;
        public static final int WIDGET = 2;
    }

    public interface UserType {
        public static final int DEFAULT = 0;
        public static final int WORK = 1;
    }

    public static final class DumpTarget extends MessageNano {
        private static volatile DumpTarget[] _emptyArray;
        public String component;
        public int containerType;
        public int gridX;
        public int gridY;
        public String itemId;
        public int itemType;
        public String packageName;
        public int pageId;
        public int spanX;
        public int spanY;
        public int type;
        public int userType;

        public interface Type {
            public static final int CONTAINER = 2;
            public static final int ITEM = 1;
            public static final int NONE = 0;
        }

        public static DumpTarget[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DumpTarget[0];
                    }
                }
            }
            return _emptyArray;
        }

        public DumpTarget() {
            clear();
        }

        public DumpTarget clear() {
            this.type = 0;
            this.pageId = 0;
            this.gridX = 0;
            this.gridY = 0;
            this.containerType = 0;
            this.itemType = 0;
            this.packageName = "";
            this.component = "";
            this.itemId = "";
            this.spanX = 1;
            this.spanY = 1;
            this.userType = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.type != 0) {
                output.writeInt32(1, this.type);
            }
            if (this.pageId != 0) {
                output.writeInt32(2, this.pageId);
            }
            if (this.gridX != 0) {
                output.writeInt32(3, this.gridX);
            }
            if (this.gridY != 0) {
                output.writeInt32(4, this.gridY);
            }
            if (this.containerType != 0) {
                output.writeInt32(5, this.containerType);
            }
            if (this.itemType != 0) {
                output.writeInt32(6, this.itemType);
            }
            if (!this.packageName.equals("")) {
                output.writeString(7, this.packageName);
            }
            if (!this.component.equals("")) {
                output.writeString(8, this.component);
            }
            if (!this.itemId.equals("")) {
                output.writeString(9, this.itemId);
            }
            if (this.spanX != 1) {
                output.writeInt32(10, this.spanX);
            }
            if (this.spanY != 1) {
                output.writeInt32(11, this.spanY);
            }
            if (this.userType != 0) {
                output.writeInt32(12, this.userType);
            }
            super.writeTo(output);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.type != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            }
            if (this.pageId != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, this.pageId);
            }
            if (this.gridX != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, this.gridX);
            }
            if (this.gridY != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, this.gridY);
            }
            if (this.containerType != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(5, this.containerType);
            }
            if (this.itemType != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(6, this.itemType);
            }
            if (!this.packageName.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(7, this.packageName);
            }
            if (!this.component.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(8, this.component);
            }
            if (!this.itemId.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(9, this.itemId);
            }
            if (this.spanX != 1) {
                size += CodedOutputByteBufferNano.computeInt32Size(10, this.spanX);
            }
            if (this.spanY != 1) {
                size += CodedOutputByteBufferNano.computeInt32Size(11, this.spanY);
            }
            if (this.userType != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(12, this.userType);
            }
            return size;
        }

        public DumpTarget mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                                this.type = value;
                                break;
                        }
                    case 16:
                        this.pageId = input.readInt32();
                        break;
                    case 24:
                        this.gridX = input.readInt32();
                        break;
                    case 32:
                        this.gridY = input.readInt32();
                        break;
                    case 40:
                        int value2 = input.readInt32();
                        switch (value2) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.containerType = value2;
                                break;
                        }
                    case 48:
                        int value3 = input.readInt32();
                        switch (value3) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.itemType = value3;
                                break;
                        }
                    case 58:
                        this.packageName = input.readString();
                        break;
                    case 66:
                        this.component = input.readString();
                        break;
                    case 74:
                        this.itemId = input.readString();
                        break;
                    case 80:
                        this.spanX = input.readInt32();
                        break;
                    case 88:
                        this.spanY = input.readInt32();
                        break;
                    case 96:
                        int value4 = input.readInt32();
                        switch (value4) {
                            case 0:
                            case 1:
                                this.userType = value4;
                                break;
                        }
                    default:
                        if (WireFormatNano.parseUnknownField(input, tag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static DumpTarget parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (DumpTarget) MessageNano.mergeFrom(new DumpTarget(), data);
        }

        public static DumpTarget parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new DumpTarget().mergeFrom(input);
        }
    }

    public static final class LauncherImpression extends MessageNano {
        private static volatile LauncherImpression[] _emptyArray;
        public DumpTarget[] targets;

        public static LauncherImpression[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherImpression[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherImpression() {
            clear();
        }

        public LauncherImpression clear() {
            this.targets = DumpTarget.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.targets != null && this.targets.length > 0) {
                for (DumpTarget element : this.targets) {
                    if (element != null) {
                        output.writeMessage(1, element);
                    }
                }
            }
            super.writeTo(output);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.targets != null && this.targets.length > 0) {
                for (DumpTarget element : this.targets) {
                    if (element != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                    }
                }
            }
            return size;
        }

        public LauncherImpression mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                    int i = this.targets == null ? 0 : this.targets.length;
                    DumpTarget[] newArray = new DumpTarget[(i + arrayLength)];
                    if (i != 0) {
                        System.arraycopy(this.targets, 0, newArray, 0, i);
                    }
                    while (i < newArray.length - 1) {
                        newArray[i] = new DumpTarget();
                        input.readMessage(newArray[i]);
                        input.readTag();
                        i++;
                    }
                    newArray[i] = new DumpTarget();
                    input.readMessage(newArray[i]);
                    this.targets = newArray;
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static LauncherImpression parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LauncherImpression) MessageNano.mergeFrom(new LauncherImpression(), data);
        }

        public static LauncherImpression parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LauncherImpression().mergeFrom(input);
        }
    }
}
