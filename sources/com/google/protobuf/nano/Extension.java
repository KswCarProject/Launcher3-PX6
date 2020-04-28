package com.google.protobuf.nano;

import com.google.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Extension<M extends ExtendableMessageNano<M>, T> {
    public static final int TYPE_BOOL = 8;
    public static final int TYPE_BYTES = 12;
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_ENUM = 14;
    public static final int TYPE_FIXED32 = 7;
    public static final int TYPE_FIXED64 = 6;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_GROUP = 10;
    public static final int TYPE_INT32 = 5;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_MESSAGE = 11;
    public static final int TYPE_SFIXED32 = 15;
    public static final int TYPE_SFIXED64 = 16;
    public static final int TYPE_SINT32 = 17;
    public static final int TYPE_SINT64 = 18;
    public static final int TYPE_STRING = 9;
    public static final int TYPE_UINT32 = 13;
    public static final int TYPE_UINT64 = 4;
    protected final Class<T> clazz;
    protected final boolean repeated;
    public final int tag;
    protected final int type;

    @Deprecated
    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(int type2, Class<T> clazz2, int tag2) {
        return new Extension<>(type2, clazz2, tag2, false);
    }

    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(int type2, Class<T> clazz2, long tag2) {
        return new Extension<>(type2, clazz2, (int) tag2, false);
    }

    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T[]> createRepeatedMessageTyped(int type2, Class<T[]> clazz2, long tag2) {
        return new Extension<>(type2, clazz2, (int) tag2, true);
    }

    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createPrimitiveTyped(int type2, Class<T> clazz2, long tag2) {
        return new PrimitiveExtension(type2, clazz2, (int) tag2, false, 0, 0);
    }

    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createRepeatedPrimitiveTyped(int type2, Class<T> clazz2, long tag2, long nonPackedTag, long packedTag) {
        return new PrimitiveExtension(type2, clazz2, (int) tag2, true, (int) nonPackedTag, (int) packedTag);
    }

    private Extension(int type2, Class<T> clazz2, int tag2, boolean repeated2) {
        this.type = type2;
        this.clazz = clazz2;
        this.tag = tag2;
        this.repeated = repeated2;
    }

    /* access modifiers changed from: package-private */
    public final T getValueFrom(List<UnknownFieldData> unknownFields) {
        if (unknownFields == null) {
            return null;
        }
        return this.repeated ? getRepeatedValueFrom(unknownFields) : getSingularValueFrom(unknownFields);
    }

    private T getRepeatedValueFrom(List<UnknownFieldData> unknownFields) {
        List<Object> resultList = new ArrayList<>();
        for (int i = 0; i < unknownFields.size(); i++) {
            UnknownFieldData data = unknownFields.get(i);
            if (data.bytes.length != 0) {
                readDataInto(data, resultList);
            }
        }
        int resultSize = resultList.size();
        if (resultSize == 0) {
            return null;
        }
        T result = this.clazz.cast(Array.newInstance(this.clazz.getComponentType(), resultSize));
        for (int i2 = 0; i2 < resultSize; i2++) {
            Array.set(result, i2, resultList.get(i2));
        }
        return result;
    }

    private T getSingularValueFrom(List<UnknownFieldData> unknownFields) {
        if (unknownFields.isEmpty()) {
            return null;
        }
        return this.clazz.cast(readData(CodedInputByteBufferNano.newInstance(unknownFields.get(unknownFields.size() - 1).bytes)));
    }

    /* access modifiers changed from: protected */
    public Object readData(CodedInputByteBufferNano input) {
        Class componentType = this.repeated ? this.clazz.getComponentType() : this.clazz;
        try {
            switch (this.type) {
                case 10:
                    MessageNano group = (MessageNano) componentType.newInstance();
                    input.readGroup(group, WireFormatNano.getTagFieldNumber(this.tag));
                    return group;
                case 11:
                    MessageNano message = (MessageNano) componentType.newInstance();
                    input.readMessage(message);
                    return message;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e);
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e2);
        } catch (IOException e3) {
            throw new IllegalArgumentException("Error reading extension field", e3);
        }
    }

    /* access modifiers changed from: protected */
    public void readDataInto(UnknownFieldData data, List<Object> resultList) {
        resultList.add(readData(CodedInputByteBufferNano.newInstance(data.bytes)));
    }

    /* access modifiers changed from: package-private */
    public void writeTo(Object value, CodedOutputByteBufferNano output) throws IOException {
        if (this.repeated) {
            writeRepeatedData(value, output);
        } else {
            writeSingularData(value, output);
        }
    }

    /* access modifiers changed from: protected */
    public void writeSingularData(Object value, CodedOutputByteBufferNano out) {
        try {
            out.writeRawVarint32(this.tag);
            switch (this.type) {
                case 10:
                    int fieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
                    out.writeGroupNoTag((MessageNano) value);
                    out.writeTag(fieldNumber, 4);
                    return;
                case 11:
                    out.writeMessageNoTag((MessageNano) value);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* access modifiers changed from: protected */
    public void writeRepeatedData(Object array, CodedOutputByteBufferNano output) {
        int arrayLength = Array.getLength(array);
        for (int i = 0; i < arrayLength; i++) {
            Object element = Array.get(array, i);
            if (element != null) {
                writeSingularData(element, output);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int computeSerializedSize(Object value) {
        if (this.repeated) {
            return computeRepeatedSerializedSize(value);
        }
        return computeSingularSerializedSize(value);
    }

    /* access modifiers changed from: protected */
    public int computeRepeatedSerializedSize(Object array) {
        int size = 0;
        int arrayLength = Array.getLength(array);
        for (int i = 0; i < arrayLength; i++) {
            if (Array.get(array, i) != null) {
                size += computeSingularSerializedSize(Array.get(array, i));
            }
        }
        return size;
    }

    /* access modifiers changed from: protected */
    public int computeSingularSerializedSize(Object value) {
        int fieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
        switch (this.type) {
            case 10:
                return CodedOutputByteBufferNano.computeGroupSize(fieldNumber, (MessageNano) value);
            case 11:
                return CodedOutputByteBufferNano.computeMessageSize(fieldNumber, (MessageNano) value);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    private static class PrimitiveExtension<M extends ExtendableMessageNano<M>, T> extends Extension<M, T> {
        private final int nonPackedTag;
        private final int packedTag;

        public PrimitiveExtension(int type, Class<T> clazz, int tag, boolean repeated, int nonPackedTag2, int packedTag2) {
            super(type, clazz, tag, repeated);
            this.nonPackedTag = nonPackedTag2;
            this.packedTag = packedTag2;
        }

        /* access modifiers changed from: protected */
        public Object readData(CodedInputByteBufferNano input) {
            try {
                return input.readPrimitiveField(this.type);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        /* access modifiers changed from: protected */
        public void readDataInto(UnknownFieldData data, List<Object> resultList) {
            if (data.tag == this.nonPackedTag) {
                resultList.add(readData(CodedInputByteBufferNano.newInstance(data.bytes)));
                return;
            }
            CodedInputByteBufferNano buffer = CodedInputByteBufferNano.newInstance(data.bytes);
            try {
                buffer.pushLimit(buffer.readRawVarint32());
                while (!buffer.isAtEnd()) {
                    resultList.add(readData(buffer));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        /* access modifiers changed from: protected */
        public final void writeSingularData(Object value, CodedOutputByteBufferNano output) {
            try {
                output.writeRawVarint32(this.tag);
                switch (this.type) {
                    case 1:
                        output.writeDoubleNoTag(((Double) value).doubleValue());
                        return;
                    case 2:
                        output.writeFloatNoTag(((Float) value).floatValue());
                        return;
                    case 3:
                        output.writeInt64NoTag(((Long) value).longValue());
                        return;
                    case 4:
                        output.writeUInt64NoTag(((Long) value).longValue());
                        return;
                    case 5:
                        output.writeInt32NoTag(((Integer) value).intValue());
                        return;
                    case 6:
                        output.writeFixed64NoTag(((Long) value).longValue());
                        return;
                    case 7:
                        output.writeFixed32NoTag(((Integer) value).intValue());
                        return;
                    case 8:
                        output.writeBoolNoTag(((Boolean) value).booleanValue());
                        return;
                    case 9:
                        output.writeStringNoTag((String) value);
                        return;
                    case 12:
                        output.writeBytesNoTag((byte[]) value);
                        return;
                    case 13:
                        output.writeUInt32NoTag(((Integer) value).intValue());
                        return;
                    case 14:
                        output.writeEnumNoTag(((Integer) value).intValue());
                        return;
                    case 15:
                        output.writeSFixed32NoTag(((Integer) value).intValue());
                        return;
                    case 16:
                        output.writeSFixed64NoTag(((Long) value).longValue());
                        return;
                    case 17:
                        output.writeSInt32NoTag(((Integer) value).intValue());
                        return;
                    case 18:
                        output.writeSInt64NoTag(((Long) value).longValue());
                        return;
                    default:
                        throw new IllegalArgumentException("Unknown type " + this.type);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:100:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:101:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0044, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0045, code lost:
            if (r2 >= r0) goto L_0x0051;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0047, code lost:
            r7.writeSInt64NoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0054, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0055, code lost:
            if (r2 >= r0) goto L_0x0061;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0057, code lost:
            r7.writeSInt32NoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0064, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0065, code lost:
            if (r2 >= r0) goto L_0x0071;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0067, code lost:
            r7.writeSFixed64NoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0074, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0075, code lost:
            if (r2 >= r0) goto L_0x0081;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0077, code lost:
            r7.writeSFixed32NoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0084, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0085, code lost:
            if (r2 >= r0) goto L_0x0091;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0087, code lost:
            r7.writeEnumNoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x0094, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0095, code lost:
            if (r2 >= r0) goto L_0x00a1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0097, code lost:
            r7.writeUInt32NoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a4, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a5, code lost:
            if (r2 >= r0) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a7, code lost:
            r7.writeBoolNoTag(java.lang.reflect.Array.getBoolean(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b4, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b5, code lost:
            if (r2 >= r0) goto L_0x00c1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b7, code lost:
            r7.writeFixed32NoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x00c4, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x00c5, code lost:
            if (r2 >= r0) goto L_0x00d1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c7, code lost:
            r7.writeFixed64NoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d3, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x00d4, code lost:
            if (r2 >= r0) goto L_0x00e0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d6, code lost:
            r7.writeInt32NoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00e2, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e3, code lost:
            if (r2 >= r0) goto L_0x00ef;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e5, code lost:
            r7.writeUInt64NoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x00f1, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x00f2, code lost:
            if (r2 >= r0) goto L_0x00fe;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00f4, code lost:
            r7.writeInt64NoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x0100, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x0101, code lost:
            if (r2 >= r0) goto L_0x010d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x0103, code lost:
            r7.writeFloatNoTag(java.lang.reflect.Array.getFloat(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x010f, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x0110, code lost:
            if (r2 >= r0) goto L_0x011f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:66:0x0112, code lost:
            r7.writeDoubleNoTag(java.lang.reflect.Array.getDouble(r6, r2));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x0119, code lost:
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:89:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:90:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:92:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:99:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void writeRepeatedData(java.lang.Object r6, com.google.protobuf.nano.CodedOutputByteBufferNano r7) {
            /*
                r5 = this;
                int r0 = r5.tag
                int r1 = r5.nonPackedTag
                if (r0 != r1) goto L_0x000b
                com.google.protobuf.nano.Extension.super.writeRepeatedData(r6, r7)
                goto L_0x011f
            L_0x000b:
                int r0 = r5.tag
                int r1 = r5.packedTag
                if (r0 != r1) goto L_0x0127
                int r0 = java.lang.reflect.Array.getLength(r6)
                int r1 = r5.computePackedDataSize(r6)
                int r2 = r5.tag     // Catch:{ IOException -> 0x0120 }
                r7.writeRawVarint32(r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeRawVarint32(r1)     // Catch:{ IOException -> 0x0120 }
                int r2 = r5.type     // Catch:{ IOException -> 0x0120 }
                r3 = 0
                switch(r2) {
                    case 1: goto L_0x010e;
                    case 2: goto L_0x00ff;
                    case 3: goto L_0x00f0;
                    case 4: goto L_0x00e1;
                    case 5: goto L_0x00d2;
                    case 6: goto L_0x00c3;
                    case 7: goto L_0x00b3;
                    case 8: goto L_0x00a3;
                    default: goto L_0x0027;
                }     // Catch:{ IOException -> 0x0120 }
            L_0x0027:
                switch(r2) {
                    case 13: goto L_0x0093;
                    case 14: goto L_0x0083;
                    case 15: goto L_0x0073;
                    case 16: goto L_0x0063;
                    case 17: goto L_0x0053;
                    case 18: goto L_0x0043;
                    default: goto L_0x002a;
                }     // Catch:{ IOException -> 0x0120 }
            L_0x002a:
                java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ IOException -> 0x0120 }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0120 }
                r3.<init>()     // Catch:{ IOException -> 0x0120 }
                java.lang.String r4 = "Unpackable type "
                r3.append(r4)     // Catch:{ IOException -> 0x0120 }
                int r4 = r5.type     // Catch:{ IOException -> 0x0120 }
                r3.append(r4)     // Catch:{ IOException -> 0x0120 }
                java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x0120 }
                r2.<init>(r3)     // Catch:{ IOException -> 0x0120 }
                throw r2     // Catch:{ IOException -> 0x0120 }
            L_0x0043:
            L_0x0044:
                r2 = r3
                if (r2 >= r0) goto L_0x0051
                long r3 = java.lang.reflect.Array.getLong(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeSInt64NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0044
            L_0x0051:
                goto L_0x011d
            L_0x0053:
            L_0x0054:
                r2 = r3
                if (r2 >= r0) goto L_0x0061
                int r3 = java.lang.reflect.Array.getInt(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeSInt32NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0054
            L_0x0061:
                goto L_0x011d
            L_0x0063:
            L_0x0064:
                r2 = r3
                if (r2 >= r0) goto L_0x0071
                long r3 = java.lang.reflect.Array.getLong(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeSFixed64NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0064
            L_0x0071:
                goto L_0x011d
            L_0x0073:
            L_0x0074:
                r2 = r3
                if (r2 >= r0) goto L_0x0081
                int r3 = java.lang.reflect.Array.getInt(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeSFixed32NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0074
            L_0x0081:
                goto L_0x011d
            L_0x0083:
            L_0x0084:
                r2 = r3
                if (r2 >= r0) goto L_0x0091
                int r3 = java.lang.reflect.Array.getInt(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeEnumNoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0084
            L_0x0091:
                goto L_0x011d
            L_0x0093:
            L_0x0094:
                r2 = r3
                if (r2 >= r0) goto L_0x00a1
                int r3 = java.lang.reflect.Array.getInt(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeUInt32NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0094
            L_0x00a1:
                goto L_0x011d
            L_0x00a3:
            L_0x00a4:
                r2 = r3
                if (r2 >= r0) goto L_0x00b1
                boolean r3 = java.lang.reflect.Array.getBoolean(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeBoolNoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x00a4
            L_0x00b1:
                goto L_0x011d
            L_0x00b3:
            L_0x00b4:
                r2 = r3
                if (r2 >= r0) goto L_0x00c1
                int r3 = java.lang.reflect.Array.getInt(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeFixed32NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x00b4
            L_0x00c1:
                goto L_0x011d
            L_0x00c3:
            L_0x00c4:
                r2 = r3
                if (r2 >= r0) goto L_0x00d1
                long r3 = java.lang.reflect.Array.getLong(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeFixed64NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x00c4
            L_0x00d1:
                goto L_0x011d
            L_0x00d2:
            L_0x00d3:
                r2 = r3
                if (r2 >= r0) goto L_0x00e0
                int r3 = java.lang.reflect.Array.getInt(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeInt32NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x00d3
            L_0x00e0:
                goto L_0x011d
            L_0x00e1:
            L_0x00e2:
                r2 = r3
                if (r2 >= r0) goto L_0x00ef
                long r3 = java.lang.reflect.Array.getLong(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeUInt64NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x00e2
            L_0x00ef:
                goto L_0x011d
            L_0x00f0:
            L_0x00f1:
                r2 = r3
                if (r2 >= r0) goto L_0x00fe
                long r3 = java.lang.reflect.Array.getLong(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeInt64NoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x00f1
            L_0x00fe:
                goto L_0x011d
            L_0x00ff:
            L_0x0100:
                r2 = r3
                if (r2 >= r0) goto L_0x010d
                float r3 = java.lang.reflect.Array.getFloat(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeFloatNoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x0100
            L_0x010d:
                goto L_0x011d
            L_0x010e:
            L_0x010f:
                r2 = r3
                if (r2 >= r0) goto L_0x011c
                double r3 = java.lang.reflect.Array.getDouble(r6, r2)     // Catch:{ IOException -> 0x0120 }
                r7.writeDoubleNoTag(r3)     // Catch:{ IOException -> 0x0120 }
                int r3 = r2 + 1
                goto L_0x010f
            L_0x011c:
            L_0x011d:
            L_0x011f:
                return
            L_0x0120:
                r2 = move-exception
                java.lang.IllegalStateException r3 = new java.lang.IllegalStateException
                r3.<init>(r2)
                throw r3
            L_0x0127:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "Unexpected repeated extension tag "
                r1.append(r2)
                int r2 = r5.tag
                r1.append(r2)
                java.lang.String r2 = ", unequal to both non-packed variant "
                r1.append(r2)
                int r2 = r5.nonPackedTag
                r1.append(r2)
                java.lang.String r2 = " and packed variant "
                r1.append(r2)
                int r2 = r5.packedTag
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.nano.Extension.PrimitiveExtension.writeRepeatedData(java.lang.Object, com.google.protobuf.nano.CodedOutputByteBufferNano):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x003e, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeSInt32SizeNoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x004e, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x004f, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0051, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeEnumSizeNoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0060, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0061, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0063, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeUInt32SizeNoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0074, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0075, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0077, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeInt32SizeNoTag(java.lang.reflect.Array.getInt(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0086, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0087, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0089, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeUInt64SizeNoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0098, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0099, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x009b, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeInt64SizeNoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
            return r1 * 8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
            return r1 * 4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x0028, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0029, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x002b, code lost:
            r0 = r0 + com.google.protobuf.nano.CodedOutputByteBufferNano.computeSInt64SizeNoTag(java.lang.reflect.Array.getLong(r6, r2));
            r3 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x003b, code lost:
            r2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x003c, code lost:
            if (r2 >= r1) goto L_0x00af;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int computePackedDataSize(java.lang.Object r6) {
            /*
                r5 = this;
                r0 = 0
                int r1 = java.lang.reflect.Array.getLength(r6)
                int r2 = r5.type
                r3 = 0
                switch(r2) {
                    case 1: goto L_0x00ac;
                    case 2: goto L_0x00a9;
                    case 3: goto L_0x0097;
                    case 4: goto L_0x0085;
                    case 5: goto L_0x0073;
                    case 6: goto L_0x00ac;
                    case 7: goto L_0x00a9;
                    case 8: goto L_0x0071;
                    default: goto L_0x000b;
                }
            L_0x000b:
                switch(r2) {
                    case 13: goto L_0x005f;
                    case 14: goto L_0x004d;
                    case 15: goto L_0x00a9;
                    case 16: goto L_0x00ac;
                    case 17: goto L_0x003a;
                    case 18: goto L_0x0027;
                    default: goto L_0x000e;
                }
            L_0x000e:
                java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Unexpected non-packable type "
                r3.append(r4)
                int r4 = r5.type
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                r2.<init>(r3)
                throw r2
            L_0x0027:
            L_0x0028:
                r2 = r3
                if (r2 >= r1) goto L_0x0038
                long r3 = java.lang.reflect.Array.getLong(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeSInt64SizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x0028
            L_0x0038:
                goto L_0x00af
            L_0x003a:
            L_0x003b:
                r2 = r3
                if (r2 >= r1) goto L_0x004b
                int r3 = java.lang.reflect.Array.getInt(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeSInt32SizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x003b
            L_0x004b:
                goto L_0x00af
            L_0x004d:
            L_0x004e:
                r2 = r3
                if (r2 >= r1) goto L_0x005e
                int r3 = java.lang.reflect.Array.getInt(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeEnumSizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x004e
            L_0x005e:
                goto L_0x00af
            L_0x005f:
            L_0x0060:
                r2 = r3
                if (r2 >= r1) goto L_0x0070
                int r3 = java.lang.reflect.Array.getInt(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeUInt32SizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x0060
            L_0x0070:
                goto L_0x00af
            L_0x0071:
                r0 = r1
                goto L_0x00af
            L_0x0073:
            L_0x0074:
                r2 = r3
                if (r2 >= r1) goto L_0x0084
                int r3 = java.lang.reflect.Array.getInt(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeInt32SizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x0074
            L_0x0084:
                goto L_0x00af
            L_0x0085:
            L_0x0086:
                r2 = r3
                if (r2 >= r1) goto L_0x0096
                long r3 = java.lang.reflect.Array.getLong(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeUInt64SizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x0086
            L_0x0096:
                goto L_0x00af
            L_0x0097:
            L_0x0098:
                r2 = r3
                if (r2 >= r1) goto L_0x00a8
                long r3 = java.lang.reflect.Array.getLong(r6, r2)
                int r3 = com.google.protobuf.nano.CodedOutputByteBufferNano.computeInt64SizeNoTag(r3)
                int r0 = r0 + r3
                int r3 = r2 + 1
                goto L_0x0098
            L_0x00a8:
                goto L_0x00af
            L_0x00a9:
                int r0 = r1 * 4
                goto L_0x00af
            L_0x00ac:
                int r0 = r1 * 8
            L_0x00af:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.nano.Extension.PrimitiveExtension.computePackedDataSize(java.lang.Object):int");
        }

        /* access modifiers changed from: protected */
        public int computeRepeatedSerializedSize(Object array) {
            if (this.tag == this.nonPackedTag) {
                return Extension.super.computeRepeatedSerializedSize(array);
            }
            if (this.tag == this.packedTag) {
                int dataSize = computePackedDataSize(array);
                return CodedOutputByteBufferNano.computeRawVarint32Size(this.tag) + CodedOutputByteBufferNano.computeRawVarint32Size(dataSize) + dataSize;
            }
            throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
        }

        /* access modifiers changed from: protected */
        public final int computeSingularSerializedSize(Object value) {
            int fieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
            switch (this.type) {
                case 1:
                    return CodedOutputByteBufferNano.computeDoubleSize(fieldNumber, ((Double) value).doubleValue());
                case 2:
                    return CodedOutputByteBufferNano.computeFloatSize(fieldNumber, ((Float) value).floatValue());
                case 3:
                    return CodedOutputByteBufferNano.computeInt64Size(fieldNumber, ((Long) value).longValue());
                case 4:
                    return CodedOutputByteBufferNano.computeUInt64Size(fieldNumber, ((Long) value).longValue());
                case 5:
                    return CodedOutputByteBufferNano.computeInt32Size(fieldNumber, ((Integer) value).intValue());
                case 6:
                    return CodedOutputByteBufferNano.computeFixed64Size(fieldNumber, ((Long) value).longValue());
                case 7:
                    return CodedOutputByteBufferNano.computeFixed32Size(fieldNumber, ((Integer) value).intValue());
                case 8:
                    return CodedOutputByteBufferNano.computeBoolSize(fieldNumber, ((Boolean) value).booleanValue());
                case 9:
                    return CodedOutputByteBufferNano.computeStringSize(fieldNumber, (String) value);
                case 12:
                    return CodedOutputByteBufferNano.computeBytesSize(fieldNumber, (byte[]) value);
                case 13:
                    return CodedOutputByteBufferNano.computeUInt32Size(fieldNumber, ((Integer) value).intValue());
                case 14:
                    return CodedOutputByteBufferNano.computeEnumSize(fieldNumber, ((Integer) value).intValue());
                case 15:
                    return CodedOutputByteBufferNano.computeSFixed32Size(fieldNumber, ((Integer) value).intValue());
                case 16:
                    return CodedOutputByteBufferNano.computeSFixed64Size(fieldNumber, ((Long) value).longValue());
                case 17:
                    return CodedOutputByteBufferNano.computeSInt32Size(fieldNumber, ((Integer) value).intValue());
                case 18:
                    return CodedOutputByteBufferNano.computeSInt64Size(fieldNumber, ((Long) value).longValue());
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        }
    }
}
