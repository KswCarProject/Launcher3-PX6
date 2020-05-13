package com.baidu.location.a;

import android.os.Build;
import android.os.Handler;
import com.baidu.location.Jni;
import com.baidu.location.f.k;
import com.baidu.location.h.c;
import com.baidu.location.h.d;
import com.baidu.location.h.f;
import com.baidu.location.h.h;
import com.baidu.location.h.i;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class g extends f {
    private static g p = null;
    String a;
    String b;
    String c;
    String d;
    int e;
    Handler f;

    private g() {
        this.a = null;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = 1;
        this.f = null;
        this.f = new Handler();
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0029  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(java.io.File r5, java.io.File r6) throws java.io.IOException {
        /*
            r2 = 0
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch:{ all -> 0x0043 }
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0043 }
            r0.<init>(r5)     // Catch:{ all -> 0x0043 }
            r3.<init>(r0)     // Catch:{ all -> 0x0043 }
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x0046 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0046 }
            r0.<init>(r6)     // Catch:{ all -> 0x0046 }
            r1.<init>(r0)     // Catch:{ all -> 0x0046 }
            r0 = 5120(0x1400, float:7.175E-42)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x0025 }
        L_0x0019:
            int r2 = r3.read(r0)     // Catch:{ all -> 0x0025 }
            r4 = -1
            if (r2 == r4) goto L_0x0032
            r4 = 0
            r1.write(r0, r4, r2)     // Catch:{ all -> 0x0025 }
            goto L_0x0019
        L_0x0025:
            r0 = move-exception
            r2 = r3
        L_0x0027:
            if (r2 == 0) goto L_0x002c
            r2.close()
        L_0x002c:
            if (r1 == 0) goto L_0x0031
            r1.close()
        L_0x0031:
            throw r0
        L_0x0032:
            r1.flush()     // Catch:{ all -> 0x0025 }
            r5.delete()     // Catch:{ all -> 0x0025 }
            if (r3 == 0) goto L_0x003d
            r3.close()
        L_0x003d:
            if (r1 == 0) goto L_0x0042
            r1.close()
        L_0x0042:
            return
        L_0x0043:
            r0 = move-exception
            r1 = r2
            goto L_0x0027
        L_0x0046:
            r0 = move-exception
            r1 = r2
            r2 = r3
            goto L_0x0027
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.a.g.a(java.io.File, java.io.File):void");
    }

    public static boolean a(String str, String str2) {
        File file = new File(i.g() + File.separator + "tmp");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[4096];
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            while (true) {
                int read = bufferedInputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            httpURLConnection.disconnect();
            fileOutputStream.close();
            if (file.length() < 10240) {
                file.delete();
                return false;
            }
            file.renameTo(new File(i.g() + File.separator + str2));
            return true;
        } catch (Exception e2) {
            file.delete();
            return false;
        }
    }

    public static g b() {
        if (p == null) {
            p = new g();
        }
        return p;
    }

    private Handler f() {
        return this.f;
    }

    private void g() {
        try {
            File file = new File(h.a + "/grtcf.dat");
            if (!file.exists()) {
                File file2 = new File(h.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(2);
                    randomAccessFile.writeInt(0);
                    randomAccessFile.seek(8);
                    byte[] bytes = "1980_01_01:0".getBytes();
                    randomAccessFile.writeInt(bytes.length);
                    randomAccessFile.write(bytes);
                    randomAccessFile.seek(200);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.seek(800);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(200);
            randomAccessFile2.writeBoolean(true);
            if (this.e == 1) {
                randomAccessFile2.writeBoolean(true);
            } else {
                randomAccessFile2.writeBoolean(false);
            }
            if (this.d != null) {
                byte[] bytes2 = this.d.getBytes();
                randomAccessFile2.writeInt(bytes2.length);
                randomAccessFile2.write(bytes2);
            } else {
                randomAccessFile2.writeInt(0);
            }
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        if (this.a != null && k.a().g()) {
            new Thread() {
                public void run() {
                    if (g.this.i()) {
                        g.this.j();
                    }
                }
            }.start();
        }
    }

    /* access modifiers changed from: private */
    public boolean i() {
        if (this.c != null && !new File(i.g() + File.separator + this.c).exists()) {
            return a("http://" + this.a + "/" + this.c, this.c);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void j() {
        if (this.b != null) {
            File file = new File(i.g() + File.separator + this.b);
            if (!file.exists() && a("http://" + this.a + "/" + this.b, this.b)) {
                String a2 = i.a(file);
                if (this.d != null && a2 != null && this.d.equals(a2)) {
                    File file2 = new File(i.g() + File.separator + com.baidu.location.f.replaceFileName);
                    if (file2.exists()) {
                        file2.delete();
                    }
                    try {
                        a(file, file2);
                    } catch (Exception e2) {
                        file2.delete();
                    }
                }
            }
        }
    }

    public void a() {
        String str;
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&sdk=");
        stringBuffer.append(6.23f);
        stringBuffer.append("&fw=");
        stringBuffer.append(com.baidu.location.f.getFrameVersion());
        stringBuffer.append("&suit=");
        stringBuffer.append(2);
        if (c.a().b == null) {
            stringBuffer.append("&im=");
            stringBuffer.append(c.a().a);
        } else {
            stringBuffer.append("&cu=");
            stringBuffer.append(c.a().b);
        }
        stringBuffer.append("&mb=");
        stringBuffer.append(Build.MODEL);
        stringBuffer.append("&sv=");
        String str2 = Build.VERSION.RELEASE;
        if (str2 != null && str2.length() > 10) {
            str2 = str2.substring(0, 10);
        }
        stringBuffer.append(str2);
        try {
            if (Build.VERSION.SDK_INT > 20) {
                String[] strArr = Build.SUPPORTED_ABIS;
                str = null;
                while (i < strArr.length) {
                    str = i == 0 ? strArr[i] + ";" : str + strArr[i] + ";";
                    i++;
                }
            } else {
                str = Build.CPU_ABI2;
            }
        } catch (Error e2) {
            str = null;
        } catch (Exception e3) {
            str = null;
        }
        if (str != null) {
            stringBuffer.append("&cpuabi=");
            stringBuffer.append(str);
        }
        stringBuffer.append("&pack=");
        stringBuffer.append(c.c);
        this.h = i.d() + "?&it=" + Jni.en1(stringBuffer.toString());
    }

    public void a(boolean z) {
        if (z) {
            try {
                JSONObject jSONObject = new JSONObject(this.j);
                if ("up".equals(jSONObject.getString("res"))) {
                    this.a = jSONObject.getString("upath");
                    if (jSONObject.has("u1")) {
                        this.b = jSONObject.getString("u1");
                    }
                    if (jSONObject.has("u2")) {
                        this.c = jSONObject.getString("u2");
                    }
                    if (jSONObject.has("u1_md5")) {
                        this.d = jSONObject.getString("u1_md5");
                    }
                    f().post(new Runnable() {
                        public void run() {
                            g.this.h();
                        }
                    });
                }
                if (jSONObject.has("ison")) {
                    this.e = jSONObject.getInt("ison");
                }
                g();
            } catch (Exception e2) {
            }
        }
        d.a().a(System.currentTimeMillis());
    }

    public void c() {
        if (System.currentTimeMillis() - d.a().b() > 86400000) {
            f().postDelayed(new Runnable() {
                public void run() {
                    if (k.a().g()) {
                        g.this.d();
                    }
                }
            }, 10000);
            f().postDelayed(new Runnable() {
                public void run() {
                    if (k.a().g()) {
                        com.baidu.location.e.d.a().m();
                    }
                }
            }, 5000);
        }
    }
}
