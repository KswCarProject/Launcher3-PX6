package com.android.launcher3.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    private static final int BUF_SIZE = 4096;

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0012, code lost:
        r1 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000d, code lost:
        r1 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
        r2 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] toByteArray(java.io.File r4) throws java.io.IOException {
        /*
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r4)
            byte[] r1 = toByteArray((java.io.InputStream) r0)     // Catch:{ Throwable -> 0x0010, all -> 0x000d }
            r0.close()
            return r1
        L_0x000d:
            r1 = move-exception
            r2 = 0
            goto L_0x0013
        L_0x0010:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0012 }
        L_0x0012:
            r1 = move-exception
        L_0x0013:
            if (r2 == 0) goto L_0x001e
            r0.close()     // Catch:{ Throwable -> 0x0019 }
            goto L_0x0021
        L_0x0019:
            r3 = move-exception
            r2.addSuppressed(r3)
            goto L_0x0021
        L_0x001e:
            r0.close()
        L_0x0021:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.IOUtils.toByteArray(java.io.File):byte[]");
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    public static long copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[4096];
        long total = 0;
        while (true) {
            int read = from.read(buf);
            int r = read;
            if (read == -1) {
                return total;
            }
            to.write(buf, 0, r);
            total += (long) r;
        }
    }
}
