package com.baidu.location.h;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import com.baidu.location.f;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class h {
    public static String a = (a().b() + "/baidu/tempdata");
    private static volatile h c = null;
    private final List<g> b = new ArrayList();
    private Context d;

    private h(Context context) {
        this.d = context;
    }

    public static h a() {
        if (c == null) {
            synchronized (h.class) {
                if (c == null) {
                    c = new h(f.getServiceContext());
                }
            }
        }
        return c;
    }

    private boolean a(String str) {
        Exception e;
        boolean z;
        try {
            File file = new File(str, "test.0");
            if (file.exists()) {
                file.delete();
            }
            z = file.createNewFile();
            try {
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e2) {
                e = e2;
                e.printStackTrace();
                return z;
            }
        } catch (Exception e3) {
            Exception exc = e3;
            z = false;
            e = exc;
            e.printStackTrace();
            return z;
        }
        return z;
    }

    private List<g> d() {
        boolean z;
        try {
            StorageManager storageManager = (StorageManager) this.d.getSystemService("storage");
            Method method = storageManager.getClass().getMethod("getVolumeList", new Class[0]);
            Method method2 = storageManager.getClass().getMethod("getVolumeState", new Class[]{String.class});
            Class<?> cls = Class.forName("android.os.storage.StorageVolume");
            Method method3 = cls.getMethod("isRemovable", new Class[0]);
            Method method4 = cls.getMethod("getPath", new Class[0]);
            Object[] objArr = (Object[]) method.invoke(storageManager, new Object[0]);
            if (objArr != null) {
                for (Object obj : objArr) {
                    String str = (String) method4.invoke(obj, new Object[0]);
                    if (str != null && str.length() > 0) {
                        if ("mounted".equals(method2.invoke(storageManager, new Object[]{str}))) {
                            boolean z2 = !((Boolean) method3.invoke(obj, new Object[0])).booleanValue();
                            if (Build.VERSION.SDK_INT <= 19 && a(str)) {
                                this.b.add(new g(str, !z2, z2 ? "Internal Storage" : "External Storage"));
                            }
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    File[] externalFilesDirs = this.d.getExternalFilesDirs((String) null);
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(this.b);
                    int i = 0;
                    while (i < externalFilesDirs.length && externalFilesDirs[i] != null) {
                        String absolutePath = externalFilesDirs[i].getAbsolutePath();
                        Iterator<g> it = this.b.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (absolutePath.startsWith(it.next().a())) {
                                    z = true;
                                    break;
                                }
                            } else {
                                z = false;
                                break;
                            }
                        }
                        if (!z && absolutePath.indexOf(this.d.getPackageName()) != -1) {
                            arrayList.add(new g(absolutePath, true, "External Storage"));
                        }
                        i++;
                    }
                    this.b.clear();
                    this.b.addAll(arrayList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.b;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0049, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0117, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0118, code lost:
        r2 = r1;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0049 A[ExcHandler: Exception (e java.lang.Exception), PHI: r1 
      PHI: (r1v8 java.util.Scanner) = (r1v7 java.util.Scanner), (r1v7 java.util.Scanner), (r1v7 java.util.Scanner), (r1v9 java.util.Scanner), (r1v9 java.util.Scanner), (r1v9 java.util.Scanner) binds: [B:28:0x006a, B:46:0x00b1, B:47:?, B:5:0x001d, B:22:0x0055, B:23:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:5:0x001d] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ad  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.baidu.location.h.g> e() {
        /*
            r9 = this;
            r2 = 0
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            java.lang.String r1 = "/proc/mounts"
            r0.<init>(r1)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            boolean r1 = r0.exists()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r1 == 0) goto L_0x0058
            java.util.Scanner r1 = new java.util.Scanner     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
        L_0x001d:
            boolean r0 = r1.hasNext()     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            if (r0 == 0) goto L_0x0055
            java.lang.String r0 = r1.nextLine()     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            java.lang.String r5 = "/dev/block/vold/"
            boolean r5 = r0.startsWith(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            if (r5 == 0) goto L_0x001d
            r5 = 9
            r6 = 32
            java.lang.String r0 = r0.replace(r5, r6)     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            java.lang.String r5 = " "
            java.lang.String[] r0 = r0.split(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            if (r0 == 0) goto L_0x001d
            int r5 = r0.length     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            if (r5 <= 0) goto L_0x001d
            r5 = 1
            r0 = r0[r5]     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            r3.add(r0)     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
            goto L_0x001d
        L_0x0049:
            r0 = move-exception
        L_0x004a:
            r0.printStackTrace()     // Catch:{ all -> 0x011a }
            if (r1 == 0) goto L_0x0052
            r1.close()
        L_0x0052:
            java.util.List<com.baidu.location.h.g> r0 = r9.b
            return r0
        L_0x0055:
            r1.close()     // Catch:{ Exception -> 0x0049, all -> 0x0117 }
        L_0x0058:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            java.lang.String r1 = "/system/etc/vold.fstab"
            r0.<init>(r1)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            boolean r1 = r0.exists()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r1 == 0) goto L_0x00b4
            java.util.Scanner r1 = new java.util.Scanner     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
        L_0x006a:
            boolean r0 = r1.hasNext()     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            if (r0 == 0) goto L_0x00b1
            java.lang.String r0 = r1.nextLine()     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            java.lang.String r5 = "dev_mount"
            boolean r5 = r0.startsWith(r5)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            if (r5 == 0) goto L_0x006a
            r5 = 9
            r6 = 32
            java.lang.String r0 = r0.replace(r5, r6)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            java.lang.String r5 = " "
            java.lang.String[] r0 = r0.split(r5)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            if (r0 == 0) goto L_0x006a
            int r5 = r0.length     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            if (r5 <= 0) goto L_0x006a
            r5 = 2
            r0 = r0[r5]     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            java.lang.String r5 = ":"
            boolean r5 = r0.contains(r5)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            if (r5 == 0) goto L_0x00a5
            r5 = 0
            java.lang.String r6 = ":"
            int r6 = r0.indexOf(r6)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            java.lang.String r0 = r0.substring(r5, r6)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
        L_0x00a5:
            r4.add(r0)     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
            goto L_0x006a
        L_0x00a9:
            r0 = move-exception
            r2 = r1
        L_0x00ab:
            if (r2 == 0) goto L_0x00b0
            r2.close()
        L_0x00b0:
            throw r0
        L_0x00b1:
            r1.close()     // Catch:{ Exception -> 0x0049, all -> 0x00a9 }
        L_0x00b4:
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            java.lang.String r1 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            java.util.List<com.baidu.location.h.g> r0 = r9.b     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            com.baidu.location.h.g r5 = new com.baidu.location.h.g     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r6 = 0
            java.lang.String r7 = "Auto"
            r5.<init>(r1, r6, r7)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r0.add(r5)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
        L_0x00cd:
            boolean r0 = r3.hasNext()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r0 == 0) goto L_0x010e
            java.lang.Object r0 = r3.next()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            boolean r5 = r4.contains(r0)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r5 == 0) goto L_0x00cd
            boolean r5 = r0.equals(r1)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r5 != 0) goto L_0x00cd
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r5.<init>(r0)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            boolean r6 = r5.exists()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r6 == 0) goto L_0x00cd
            boolean r6 = r5.isDirectory()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r6 == 0) goto L_0x00cd
            boolean r5 = r5.canWrite()     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            if (r5 == 0) goto L_0x00cd
            java.util.List<com.baidu.location.h.g> r5 = r9.b     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            com.baidu.location.h.g r6 = new com.baidu.location.h.g     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r7 = 0
            java.lang.String r8 = "Auto"
            r6.<init>(r0, r7, r8)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            r5.add(r6)     // Catch:{ Exception -> 0x010a, all -> 0x0115 }
            goto L_0x00cd
        L_0x010a:
            r0 = move-exception
            r1 = r2
            goto L_0x004a
        L_0x010e:
            if (r2 == 0) goto L_0x0052
            r2.close()
            goto L_0x0052
        L_0x0115:
            r0 = move-exception
            goto L_0x00ab
        L_0x0117:
            r0 = move-exception
            r2 = r1
            goto L_0x00ab
        L_0x011a:
            r0 = move-exception
            r2 = r1
            goto L_0x00ab
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.h.h.e():java.util.List");
    }

    public String b() {
        List<g> c2 = c();
        if (c2 == null || c2.size() == 0) {
            return null;
        }
        return c2.get(0).a();
    }

    public List<g> c() {
        List<g> list = null;
        if (Build.VERSION.SDK_INT >= 14) {
            list = d();
        }
        return (list == null || list.size() <= 0) ? e() : list;
    }
}
