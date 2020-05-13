package com.baidu.location.c;

import com.baidu.location.Jni;
import com.baidu.location.h.c;
import com.baidu.location.h.h;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class f {
    public static final String a = (h.a + "/llin.dat");
    private static volatile f b = null;
    private static String c = "LogSDK";
    private static int d = 5;
    private static int e = 1024;
    private static final String f = (h.a + "/llg.dat");
    private static final String g = (h.a + "/ller.dat");
    private SimpleDateFormat h = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private g i = null;
    private g j = null;
    private a k = null;
    /* access modifiers changed from: private */
    public long l = 0;

    private class a extends com.baidu.location.h.f {
        private String b = null;
        private boolean c = false;

        a() {
            this.k = new HashMap();
        }

        public void a() {
            this.k.clear();
            this.k.put("qt", "stat");
            this.k.put("req", this.b);
            this.h = "http://loc.map.baidu.com/statloc";
        }

        public void a(String str) {
            this.b = str;
            if (this.b != null) {
                e();
                this.c = true;
            }
        }

        public void a(boolean z) {
            this.c = false;
            if (!z || this.j == null) {
                long unused = f.this.l = System.currentTimeMillis();
                return;
            }
            try {
                String str = this.j;
            } catch (Exception e) {
            }
        }

        public boolean b() {
            return this.c;
        }
    }

    private f() {
        if (this.i == null) {
            this.i = new g();
        }
    }

    public static f a() {
        if (b == null) {
            synchronized (f.class) {
                if (b == null) {
                    b = new f();
                }
            }
        }
        return b;
    }

    public static synchronized void a(String str, String str2) {
        int i2;
        synchronized (f.class) {
            File file = new File(str);
            if (!file.exists()) {
                b(str);
            }
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(4);
                int readInt = randomAccessFile.readInt();
                int readInt2 = randomAccessFile.readInt();
                int readInt3 = randomAccessFile.readInt();
                int readInt4 = randomAccessFile.readInt();
                int readInt5 = randomAccessFile.readInt();
                if (readInt3 < readInt) {
                    randomAccessFile.seek((long) ((readInt2 * readInt3) + 128));
                    byte[] bytes = (str2 + 0).getBytes();
                    randomAccessFile.writeInt(bytes.length);
                    randomAccessFile.write(bytes, 0, bytes.length);
                    i2 = readInt3 + 1;
                } else {
                    randomAccessFile.seek((long) ((readInt4 * readInt2) + 128));
                    byte[] bytes2 = (str2 + 0).getBytes();
                    randomAccessFile.writeInt(bytes2.length);
                    randomAccessFile.write(bytes2, 0, bytes2.length);
                    readInt4++;
                    if (readInt4 > readInt3) {
                        readInt4 = 0;
                        i2 = readInt3;
                    } else {
                        i2 = readInt3;
                    }
                }
                randomAccessFile.seek(12);
                randomAccessFile.writeInt(i2);
                randomAccessFile.writeInt(readInt4);
                randomAccessFile.writeInt(readInt5);
                randomAccessFile.close();
            } catch (Exception e2) {
            }
        }
    }

    public static boolean a(String str, List<String> list) {
        File file = new File(str);
        if (!file.exists()) {
            return false;
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(8);
            int readInt = randomAccessFile.readInt();
            int readInt2 = randomAccessFile.readInt();
            int readInt3 = randomAccessFile.readInt();
            byte[] bArr = new byte[e];
            int i2 = readInt2;
            int i3 = d + 1;
            boolean z = false;
            while (i3 > 0 && i2 > 0) {
                if (i2 < readInt3) {
                    readInt3 = 0;
                }
                try {
                    randomAccessFile.seek((long) (((i2 - 1) * readInt) + 128));
                    int readInt4 = randomAccessFile.readInt();
                    if (readInt4 > 0 && readInt4 < readInt) {
                        randomAccessFile.read(bArr, 0, readInt4);
                        if (bArr[readInt4 - 1] == 0) {
                            list.add(0, new String(bArr, 0, readInt4 - 1));
                            z = true;
                        }
                    }
                    i3--;
                    i2--;
                } catch (Exception e2) {
                    return z;
                }
            }
            randomAccessFile.seek(12);
            randomAccessFile.writeInt(i2);
            randomAccessFile.writeInt(readInt3);
            randomAccessFile.close();
            return z;
        } catch (Exception e3) {
            return false;
        }
    }

    private static void b(String str) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                File file2 = new File(h.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (!file.createNewFile()) {
                    file = null;
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(0);
                randomAccessFile.writeInt(32);
                randomAccessFile.writeInt(1000);
                randomAccessFile.writeInt(1040);
                randomAccessFile.writeInt(0);
                randomAccessFile.writeInt(0);
                randomAccessFile.writeInt(0);
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
    }

    public void a(g gVar) {
        if (gVar != null) {
            a(f, Jni.encode(gVar.b()));
        }
    }

    public void a(String str) {
        if (str != null) {
            try {
                StringBuffer stringBuffer = new StringBuffer();
                String format = this.h.format(new Date());
                stringBuffer.append("&time=");
                stringBuffer.append(format);
                stringBuffer.append("&err=");
                stringBuffer.append(str);
                stringBuffer.append(c.a().a(false));
                stringBuffer.append(com.baidu.location.a.a.a().c());
                a(g, Jni.encode(stringBuffer.toString()));
            } catch (Exception e2) {
            }
        }
    }

    public g b() {
        return this.i;
    }

    public void c() {
        if (this.i != null) {
            a(f, Jni.encode(this.i.b()));
            this.i.a();
        }
    }

    public void d() {
        boolean z;
        boolean z2;
        if (this.k == null) {
            this.k = new a();
        }
        if (System.currentTimeMillis() - this.l >= 3600000 && !this.k.b()) {
            try {
                ArrayList arrayList = new ArrayList();
                a(g, (List<String>) arrayList);
                if (arrayList.size() > 0) {
                    z = false;
                    z2 = true;
                } else {
                    a(f, (List<String>) arrayList);
                    if (arrayList.size() == 0) {
                        a(a, (List<String>) arrayList);
                        z = true;
                        z2 = false;
                    } else {
                        z = false;
                        z2 = false;
                    }
                }
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                if (arrayList.size() > 0) {
                    int size = arrayList.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        jSONArray.put((String) arrayList.get(i2));
                    }
                    if (z2) {
                        jSONObject.put("locpt", jSONArray);
                    } else if (z) {
                        jSONObject.put("locup", jSONArray);
                    } else {
                        jSONObject.put("loctc", jSONArray);
                    }
                    this.k.a(jSONObject.toString());
                }
            } catch (Exception e2) {
            }
        }
    }
}
