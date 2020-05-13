package com.baidu.location.h;

import java.io.File;
import java.io.RandomAccessFile;

public class d {
    static d c;
    String a = "firll.dat";
    int b = 3164;
    int d = 0;
    int e = 20;
    int f = 40;
    int g = 60;
    int h = 80;
    int i = 100;

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0058 A[SYNTHETIC, Splitter:B:25:0x0058] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long a(int r8) {
        /*
            r7 = this;
            r0 = -1
            java.lang.String r2 = com.baidu.location.h.i.g()
            if (r2 != 0) goto L_0x0009
        L_0x0008:
            return r0
        L_0x0009:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r2 = r3.append(r2)
            java.lang.String r3 = java.io.File.separator
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = r7.a
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = r2.toString()
            r2 = 0
            java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x004b, all -> 0x0054 }
            java.lang.String r5 = "rw"
            r4.<init>(r3, r5)     // Catch:{ Exception -> 0x004b, all -> 0x0054 }
            long r2 = (long) r8
            r4.seek(r2)     // Catch:{ Exception -> 0x0062, all -> 0x0060 }
            int r5 = r4.readInt()     // Catch:{ Exception -> 0x0062, all -> 0x0060 }
            long r2 = r4.readLong()     // Catch:{ Exception -> 0x0062, all -> 0x0060 }
            int r6 = r4.readInt()     // Catch:{ Exception -> 0x0062, all -> 0x0060 }
            if (r5 != r6) goto L_0x0043
            if (r4 == 0) goto L_0x0041
            r4.close()     // Catch:{ IOException -> 0x005c }
        L_0x0041:
            r0 = r2
            goto L_0x0008
        L_0x0043:
            if (r4 == 0) goto L_0x0008
            r4.close()     // Catch:{ IOException -> 0x0049 }
            goto L_0x0008
        L_0x0049:
            r2 = move-exception
            goto L_0x0008
        L_0x004b:
            r3 = move-exception
        L_0x004c:
            if (r2 == 0) goto L_0x0008
            r2.close()     // Catch:{ IOException -> 0x0052 }
            goto L_0x0008
        L_0x0052:
            r2 = move-exception
            goto L_0x0008
        L_0x0054:
            r0 = move-exception
            r4 = r2
        L_0x0056:
            if (r4 == 0) goto L_0x005b
            r4.close()     // Catch:{ IOException -> 0x005e }
        L_0x005b:
            throw r0
        L_0x005c:
            r0 = move-exception
            goto L_0x0041
        L_0x005e:
            r1 = move-exception
            goto L_0x005b
        L_0x0060:
            r0 = move-exception
            goto L_0x0056
        L_0x0062:
            r2 = move-exception
            r2 = r4
            goto L_0x004c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.h.d.a(int):long");
    }

    public static d a() {
        if (c == null) {
            c = new d();
        }
        return c;
    }

    private void a(int i2, long j) {
        String g2 = i.g();
        if (g2 != null) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(g2 + File.separator + this.a, "rw");
                randomAccessFile.seek((long) i2);
                randomAccessFile.writeInt(this.b);
                randomAccessFile.writeLong(j);
                randomAccessFile.writeInt(this.b);
                randomAccessFile.close();
            } catch (Exception e2) {
            }
        }
    }

    public void a(long j) {
        a(this.d, j);
    }

    public long b() {
        return a(this.d);
    }

    public void b(long j) {
        a(this.g, j);
    }

    public long c() {
        return a(this.g);
    }
}
