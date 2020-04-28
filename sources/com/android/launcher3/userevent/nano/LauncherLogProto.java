package com.android.launcher3.userevent.nano;

import com.android.launcher3.userevent.nano.LauncherLogExtensions;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface LauncherLogProto {

    public interface ContainerType {
        public static final int ALLAPPS = 4;
        public static final int APP = 13;
        public static final int DEEPSHORTCUTS = 9;
        public static final int DEFAULT_CONTAINERTYPE = 0;
        public static final int FOLDER = 3;
        public static final int HOTSEAT = 2;
        public static final int NAVBAR = 11;
        public static final int OVERVIEW = 6;
        public static final int PINITEM = 10;
        public static final int PREDICTION = 7;
        public static final int SEARCHRESULT = 8;
        public static final int SIDELOADED_LAUNCHER = 15;
        public static final int TASKSWITCHER = 12;
        public static final int TIP = 14;
        public static final int WIDGETS = 5;
        public static final int WORKSPACE = 1;
    }

    public interface ControlType {
        public static final int ALL_APPS_BUTTON = 1;
        public static final int APPINFO_TARGET = 7;
        public static final int BACK_BUTTON = 11;
        public static final int CANCEL_TARGET = 14;
        public static final int CLEAR_ALL_BUTTON = 13;
        public static final int DEFAULT_CONTROLTYPE = 0;
        public static final int HOME_INTENT = 10;
        public static final int QUICK_SCRUB_BUTTON = 12;
        public static final int REMOVE_TARGET = 5;
        public static final int RESIZE_HANDLE = 8;
        public static final int SETTINGS_BUTTON = 4;
        public static final int SPLIT_SCREEN_TARGET = 16;
        public static final int TASK_PREVIEW = 15;
        public static final int UNINSTALL_TARGET = 6;
        public static final int VERTICAL_SCROLL = 9;
        public static final int WALLPAPER_BUTTON = 3;
        public static final int WIDGETS_BUTTON = 2;
    }

    public interface ItemType {
        public static final int APP_ICON = 1;
        public static final int DEEPSHORTCUT = 5;
        public static final int DEFAULT_ITEMTYPE = 0;
        public static final int EDITTEXT = 7;
        public static final int FOLDER_ICON = 4;
        public static final int NOTIFICATION = 8;
        public static final int SEARCHBOX = 6;
        public static final int SHORTCUT = 2;
        public static final int TASK = 9;
        public static final int WEB_APP = 10;
        public static final int WIDGET = 3;
    }

    public interface TipType {
        public static final int BOUNCE = 1;
        public static final int DEFAULT_NONE = 0;
        public static final int PREDICTION_TEXT = 4;
        public static final int QUICK_SCRUB_TEXT = 3;
        public static final int SWIPE_UP_TEXT = 2;
    }

    public static final class Target extends MessageNano {
        private static volatile Target[] _emptyArray;
        public int cardinality;
        public int componentHash;
        public int containerType;
        public int controlType;
        public LauncherLogExtensions.TargetExtension extension;
        public int gridX;
        public int gridY;
        public int intentHash;
        public int itemType;
        public int packageNameHash;
        public int pageIndex;
        public int predictedRank;
        public int rank;
        public int spanX;
        public int spanY;
        public int tipType;
        public int type;

        public interface Type {
            public static final int CONTAINER = 3;
            public static final int CONTROL = 2;
            public static final int ITEM = 1;
            public static final int NONE = 0;
        }

        public static Target[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Target[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Target() {
            clear();
        }

        public Target clear() {
            this.type = 0;
            this.pageIndex = 0;
            this.rank = 0;
            this.gridX = 0;
            this.gridY = 0;
            this.containerType = 0;
            this.cardinality = 0;
            this.controlType = 0;
            this.itemType = 0;
            this.packageNameHash = 0;
            this.componentHash = 0;
            this.intentHash = 0;
            this.spanX = 1;
            this.spanY = 1;
            this.predictedRank = 0;
            this.extension = null;
            this.tipType = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.type != 0) {
                output.writeInt32(1, this.type);
            }
            if (this.pageIndex != 0) {
                output.writeInt32(2, this.pageIndex);
            }
            if (this.rank != 0) {
                output.writeInt32(3, this.rank);
            }
            if (this.gridX != 0) {
                output.writeInt32(4, this.gridX);
            }
            if (this.gridY != 0) {
                output.writeInt32(5, this.gridY);
            }
            if (this.containerType != 0) {
                output.writeInt32(6, this.containerType);
            }
            if (this.cardinality != 0) {
                output.writeInt32(7, this.cardinality);
            }
            if (this.controlType != 0) {
                output.writeInt32(8, this.controlType);
            }
            if (this.itemType != 0) {
                output.writeInt32(9, this.itemType);
            }
            if (this.packageNameHash != 0) {
                output.writeInt32(10, this.packageNameHash);
            }
            if (this.componentHash != 0) {
                output.writeInt32(11, this.componentHash);
            }
            if (this.intentHash != 0) {
                output.writeInt32(12, this.intentHash);
            }
            if (this.spanX != 1) {
                output.writeInt32(13, this.spanX);
            }
            if (this.spanY != 1) {
                output.writeInt32(14, this.spanY);
            }
            if (this.predictedRank != 0) {
                output.writeInt32(15, this.predictedRank);
            }
            if (this.extension != null) {
                output.writeMessage(16, this.extension);
            }
            if (this.tipType != 0) {
                output.writeInt32(17, this.tipType);
            }
            super.writeTo(output);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.type != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            }
            if (this.pageIndex != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, this.pageIndex);
            }
            if (this.rank != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, this.rank);
            }
            if (this.gridX != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, this.gridX);
            }
            if (this.gridY != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(5, this.gridY);
            }
            if (this.containerType != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(6, this.containerType);
            }
            if (this.cardinality != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(7, this.cardinality);
            }
            if (this.controlType != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(8, this.controlType);
            }
            if (this.itemType != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(9, this.itemType);
            }
            if (this.packageNameHash != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(10, this.packageNameHash);
            }
            if (this.componentHash != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(11, this.componentHash);
            }
            if (this.intentHash != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(12, this.intentHash);
            }
            if (this.spanX != 1) {
                size += CodedOutputByteBufferNano.computeInt32Size(13, this.spanX);
            }
            if (this.spanY != 1) {
                size += CodedOutputByteBufferNano.computeInt32Size(14, this.spanY);
            }
            if (this.predictedRank != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(15, this.predictedRank);
            }
            if (this.extension != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(16, this.extension);
            }
            if (this.tipType != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(17, this.tipType);
            }
            return size;
        }

        public Target mergeFrom(CodedInputByteBufferNano input) throws IOException {
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
                            case 3:
                                this.type = value;
                                break;
                        }
                    case 16:
                        this.pageIndex = input.readInt32();
                        break;
                    case 24:
                        this.rank = input.readInt32();
                        break;
                    case 32:
                        this.gridX = input.readInt32();
                        break;
                    case 40:
                        this.gridY = input.readInt32();
                        break;
                    case 48:
                        int value2 = input.readInt32();
                        switch (value2) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                                this.containerType = value2;
                                break;
                        }
                    case 56:
                        this.cardinality = input.readInt32();
                        break;
                    case 64:
                        int value3 = input.readInt32();
                        switch (value3) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                                this.controlType = value3;
                                break;
                        }
                    case 72:
                        int value4 = input.readInt32();
                        switch (value4) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                                this.itemType = value4;
                                break;
                        }
                    case 80:
                        this.packageNameHash = input.readInt32();
                        break;
                    case 88:
                        this.componentHash = input.readInt32();
                        break;
                    case 96:
                        this.intentHash = input.readInt32();
                        break;
                    case 104:
                        this.spanX = input.readInt32();
                        break;
                    case 112:
                        this.spanY = input.readInt32();
                        break;
                    case 120:
                        this.predictedRank = input.readInt32();
                        break;
                    case 130:
                        if (this.extension == null) {
                            this.extension = new LauncherLogExtensions.TargetExtension();
                        }
                        input.readMessage(this.extension);
                        break;
                    case 136:
                        int value5 = input.readInt32();
                        switch (value5) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.tipType = value5;
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

        public static Target parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Target) MessageNano.mergeFrom(new Target(), data);
        }

        public static Target parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Target().mergeFrom(input);
        }
    }

    public static final class Action extends MessageNano {
        private static volatile Action[] _emptyArray;
        public int command;
        public int dir;
        public boolean isOutside;
        public boolean isStateChange;
        public int touch;
        public int type;

        public interface Command {
            public static final int BACK = 1;
            public static final int CANCEL = 3;
            public static final int CONFIRM = 4;
            public static final int ENTRY = 2;
            public static final int HOME_INTENT = 0;
            public static final int RECENTS_BUTTON = 6;
            public static final int RESUME = 7;
            public static final int STOP = 5;
        }

        public interface Direction {
            public static final int DOWN = 2;
            public static final int LEFT = 3;
            public static final int NONE = 0;
            public static final int RIGHT = 4;
            public static final int UP = 1;
        }

        public interface Touch {
            public static final int DRAGDROP = 2;
            public static final int FLING = 4;
            public static final int LONGPRESS = 1;
            public static final int PINCH = 5;
            public static final int SWIPE = 3;
            public static final int TAP = 0;
        }

        public interface Type {
            public static final int AUTOMATED = 1;
            public static final int COMMAND = 2;
            public static final int TIP = 3;
            public static final int TOUCH = 0;
        }

        public static Action[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Action[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Action() {
            clear();
        }

        public Action clear() {
            this.type = 0;
            this.touch = 0;
            this.dir = 0;
            this.command = 0;
            this.isOutside = false;
            this.isStateChange = false;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.type != 0) {
                output.writeInt32(1, this.type);
            }
            if (this.touch != 0) {
                output.writeInt32(2, this.touch);
            }
            if (this.dir != 0) {
                output.writeInt32(3, this.dir);
            }
            if (this.command != 0) {
                output.writeInt32(4, this.command);
            }
            if (this.isOutside) {
                output.writeBool(5, this.isOutside);
            }
            if (this.isStateChange) {
                output.writeBool(6, this.isStateChange);
            }
            super.writeTo(output);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.type != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            }
            if (this.touch != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, this.touch);
            }
            if (this.dir != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, this.dir);
            }
            if (this.command != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, this.command);
            }
            if (this.isOutside) {
                size += CodedOutputByteBufferNano.computeBoolSize(5, this.isOutside);
            }
            if (this.isStateChange) {
                return size + CodedOutputByteBufferNano.computeBoolSize(6, this.isStateChange);
            }
            return size;
        }

        public Action mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag != 0) {
                    if (tag != 8) {
                        if (tag != 16) {
                            if (tag != 24) {
                                if (tag == 32) {
                                    int value = input.readInt32();
                                    switch (value) {
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 7:
                                            this.command = value;
                                            break;
                                    }
                                } else if (tag == 40) {
                                    this.isOutside = input.readBool();
                                } else if (tag == 48) {
                                    this.isStateChange = input.readBool();
                                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                                    return this;
                                }
                            } else {
                                int value2 = input.readInt32();
                                switch (value2) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                        this.dir = value2;
                                        break;
                                }
                            }
                        } else {
                            int value3 = input.readInt32();
                            switch (value3) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                    this.touch = value3;
                                    break;
                            }
                        }
                    } else {
                        int value4 = input.readInt32();
                        switch (value4) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.type = value4;
                                break;
                        }
                    }
                } else {
                    return this;
                }
            }
        }

        public static Action parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Action) MessageNano.mergeFrom(new Action(), data);
        }

        public static Action parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Action().mergeFrom(input);
        }
    }

    public static final class LauncherEvent extends MessageNano {
        private static volatile LauncherEvent[] _emptyArray;
        public Action action;
        public long actionDurationMillis;
        public Target[] destTarget;
        public long elapsedContainerMillis;
        public long elapsedSessionMillis;
        public LauncherLogExtensions.LauncherEventExtension extension;
        public boolean isInLandscapeMode;
        public boolean isInMultiWindowMode;
        public Target[] srcTarget;

        public static LauncherEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherEvent() {
            clear();
        }

        public LauncherEvent clear() {
            this.action = null;
            this.srcTarget = Target.emptyArray();
            this.destTarget = Target.emptyArray();
            this.actionDurationMillis = 0;
            this.elapsedContainerMillis = 0;
            this.elapsedSessionMillis = 0;
            this.isInMultiWindowMode = false;
            this.isInLandscapeMode = false;
            this.extension = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.action != null) {
                output.writeMessage(1, this.action);
            }
            int i = 0;
            if (this.srcTarget != null && this.srcTarget.length > 0) {
                for (Target element : this.srcTarget) {
                    if (element != null) {
                        output.writeMessage(2, element);
                    }
                }
            }
            if (this.destTarget != null && this.destTarget.length > 0) {
                while (true) {
                    int i2 = i;
                    if (i2 >= this.destTarget.length) {
                        break;
                    }
                    Target element2 = this.destTarget[i2];
                    if (element2 != null) {
                        output.writeMessage(3, element2);
                    }
                    i = i2 + 1;
                }
            }
            if (this.actionDurationMillis != 0) {
                output.writeInt64(4, this.actionDurationMillis);
            }
            if (this.elapsedContainerMillis != 0) {
                output.writeInt64(5, this.elapsedContainerMillis);
            }
            if (this.elapsedSessionMillis != 0) {
                output.writeInt64(6, this.elapsedSessionMillis);
            }
            if (this.isInMultiWindowMode) {
                output.writeBool(7, this.isInMultiWindowMode);
            }
            if (this.isInLandscapeMode) {
                output.writeBool(8, this.isInLandscapeMode);
            }
            if (this.extension != null) {
                output.writeMessage(9, this.extension);
            }
            super.writeTo(output);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.action != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(1, this.action);
            }
            int i = 0;
            if (this.srcTarget != null && this.srcTarget.length > 0) {
                int size2 = size;
                for (Target element : this.srcTarget) {
                    if (element != null) {
                        size2 += CodedOutputByteBufferNano.computeMessageSize(2, element);
                    }
                }
                size = size2;
            }
            if (this.destTarget != null && this.destTarget.length > 0) {
                while (true) {
                    int i2 = i;
                    if (i2 >= this.destTarget.length) {
                        break;
                    }
                    Target element2 = this.destTarget[i2];
                    if (element2 != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(3, element2);
                    }
                    i = i2 + 1;
                }
            }
            if (this.actionDurationMillis != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(4, this.actionDurationMillis);
            }
            if (this.elapsedContainerMillis != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(5, this.elapsedContainerMillis);
            }
            if (this.elapsedSessionMillis != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(6, this.elapsedSessionMillis);
            }
            if (this.isInMultiWindowMode) {
                size += CodedOutputByteBufferNano.computeBoolSize(7, this.isInMultiWindowMode);
            }
            if (this.isInLandscapeMode) {
                size += CodedOutputByteBufferNano.computeBoolSize(8, this.isInLandscapeMode);
            }
            if (this.extension != null) {
                return size + CodedOutputByteBufferNano.computeMessageSize(9, this.extension);
            }
            return size;
        }

        public LauncherEvent mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    if (this.action == null) {
                        this.action = new Action();
                    }
                    input.readMessage(this.action);
                } else if (tag == 18) {
                    int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                    int i = this.srcTarget == null ? 0 : this.srcTarget.length;
                    Target[] newArray = new Target[(i + arrayLength)];
                    if (i != 0) {
                        System.arraycopy(this.srcTarget, 0, newArray, 0, i);
                    }
                    while (i < newArray.length - 1) {
                        newArray[i] = new Target();
                        input.readMessage(newArray[i]);
                        input.readTag();
                        i++;
                    }
                    newArray[i] = new Target();
                    input.readMessage(newArray[i]);
                    this.srcTarget = newArray;
                } else if (tag == 26) {
                    int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 26);
                    int i2 = this.destTarget == null ? 0 : this.destTarget.length;
                    Target[] newArray2 = new Target[(i2 + arrayLength2)];
                    if (i2 != 0) {
                        System.arraycopy(this.destTarget, 0, newArray2, 0, i2);
                    }
                    while (i2 < newArray2.length - 1) {
                        newArray2[i2] = new Target();
                        input.readMessage(newArray2[i2]);
                        input.readTag();
                        i2++;
                    }
                    newArray2[i2] = new Target();
                    input.readMessage(newArray2[i2]);
                    this.destTarget = newArray2;
                } else if (tag == 32) {
                    this.actionDurationMillis = input.readInt64();
                } else if (tag == 40) {
                    this.elapsedContainerMillis = input.readInt64();
                } else if (tag == 48) {
                    this.elapsedSessionMillis = input.readInt64();
                } else if (tag == 56) {
                    this.isInMultiWindowMode = input.readBool();
                } else if (tag == 64) {
                    this.isInLandscapeMode = input.readBool();
                } else if (tag == 74) {
                    if (this.extension == null) {
                        this.extension = new LauncherLogExtensions.LauncherEventExtension();
                    }
                    input.readMessage(this.extension);
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static LauncherEvent parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LauncherEvent) MessageNano.mergeFrom(new LauncherEvent(), data);
        }

        public static LauncherEvent parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LauncherEvent().mergeFrom(input);
        }
    }
}
