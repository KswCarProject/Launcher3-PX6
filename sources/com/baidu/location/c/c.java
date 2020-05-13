package com.baidu.location.c;

import android.content.SharedPreferences;
import android.support.v4.media.session.PlaybackStateCompat;
import com.baidu.location.Jni;
import com.baidu.location.h.d;
import com.baidu.location.h.f;
import com.baidu.location.h.h;
import com.baidu.location.h.i;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONObject;

public class c {
    private static c i = null;
    private static final String k = (h.a + "/conlts.dat");
    private static int l = -1;
    private static int m = -1;
    private static int n = 0;
    public boolean a = true;
    public boolean b = true;
    public boolean c = false;
    public boolean d = true;
    public boolean e = true;
    public boolean f = true;
    public boolean g = true;
    public boolean h = false;
    private a j = null;

    class a extends f {
        String a = null;
        boolean b = false;
        boolean c = false;

        public a() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = i.c();
            this.i = 2;
            String encode = Jni.encode(this.a);
            this.a = null;
            if (this.b) {
                this.k.put("qt", "grid");
            } else {
                this.k.put("qt", "conf");
            }
            this.k.put("req", encode);
        }

        public void a(String str, boolean z) {
            if (!this.c) {
                this.c = true;
                this.a = str;
                this.b = z;
                if (z) {
                    b(true);
                } else {
                    e();
                }
            }
        }

        public void a(boolean z) {
            if (!z || this.j == null) {
                c.this.c((String) null);
            } else if (this.b) {
                c.this.a(this.m);
            } else {
                c.this.c(this.j);
            }
            if (this.k != null) {
                this.k.clear();
            }
            this.c = false;
        }
    }

    private c() {
    }

    public static c a() {
        if (i == null) {
            i = new c();
        }
        return i;
    }

    private void a(int i2) {
        boolean z = true;
        this.a = (i2 & 1) == 1;
        this.b = (i2 & 2) == 2;
        this.c = (i2 & 4) == 4;
        this.d = (i2 & 8) == 8;
        this.f = (i2 & 65536) == 65536;
        if ((i2 & 131072) != 131072) {
            z = false;
        }
        this.g = z;
        if ((i2 & 16) == 16) {
            this.e = false;
        }
    }

    private void a(JSONObject jSONObject) {
        boolean z = false;
        if (jSONObject != null) {
            int i2 = 14400000;
            int i3 = 10;
            try {
                if (!jSONObject.has("ipen") || jSONObject.getInt("ipen") != 0) {
                    z = true;
                }
                if (jSONObject.has("ipvt")) {
                    i2 = jSONObject.getInt("ipvt");
                }
                if (jSONObject.has("ipvn")) {
                    i3 = jSONObject.getInt("ipvn");
                }
                SharedPreferences.Editor edit = com.baidu.location.f.getServiceContext().getSharedPreferences("MapCoreServicePre", 0).edit();
                edit.putBoolean("ipLocInfoUpload", z);
                edit.putInt("ipValidTime", i2);
                edit.putInt("ipLocInfoUploadTimesPerDay", i3);
                edit.commit();
            } catch (Exception e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(byte[] bArr) {
        int i2 = 0;
        if (bArr != null) {
            if (bArr.length < 640) {
                i.u = false;
                i.r = i.p + 0.025d;
                i.q = i.o - 0.025d;
                i2 = 1;
            } else {
                i.u = true;
                i.q = Double.longBitsToDouble(((((long) bArr[7]) & 255) << 56) | ((((long) bArr[6]) & 255) << 48) | ((((long) bArr[5]) & 255) << 40) | ((((long) bArr[4]) & 255) << 32) | ((((long) bArr[3]) & 255) << 24) | ((((long) bArr[2]) & 255) << 16) | ((((long) bArr[1]) & 255) << 8) | (((long) bArr[0]) & 255));
                i.r = Double.longBitsToDouble(((((long) bArr[15]) & 255) << 56) | ((((long) bArr[14]) & 255) << 48) | ((((long) bArr[13]) & 255) << 40) | ((((long) bArr[12]) & 255) << 32) | ((((long) bArr[11]) & 255) << 24) | ((((long) bArr[10]) & 255) << 16) | ((((long) bArr[9]) & 255) << 8) | (((long) bArr[8]) & 255));
                i.t = new byte[625];
                while (i2 < 625) {
                    i.t[i2] = bArr[i2 + 16];
                    i2++;
                }
                i2 = 1;
            }
        }
        if (i2 != 0) {
            try {
                g();
            } catch (Exception e2) {
            }
        }
    }

    private void b(int i2) {
        File file = new File(k);
        if (!file.exists()) {
            i();
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(4);
            int readInt = randomAccessFile.readInt();
            int readInt2 = randomAccessFile.readInt();
            randomAccessFile.seek((long) ((readInt * n) + 128));
            byte[] bytes = (com.baidu.location.h.c.c + 0).getBytes();
            randomAccessFile.writeInt(bytes.length);
            randomAccessFile.write(bytes, 0, bytes.length);
            randomAccessFile.writeInt(i2);
            if (readInt2 == n) {
                randomAccessFile.seek(8);
                randomAccessFile.writeInt(readInt2 + 1);
            }
            randomAccessFile.close();
        } catch (Exception e2) {
        }
    }

    private boolean b(String str) {
        boolean z = true;
        if (str != null) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("ipconf")) {
                    try {
                        a(jSONObject.getJSONObject("ipconf"));
                    } catch (Exception e2) {
                    }
                }
                int parseInt = Integer.parseInt(jSONObject.getString("ver"));
                if (parseInt > i.v) {
                    i.v = parseInt;
                    if (jSONObject.has("gps")) {
                        String[] split = jSONObject.getString("gps").split("\\|");
                        if (split.length > 10) {
                            if (split[0] != null && !split[0].equals("")) {
                                i.w = Float.parseFloat(split[0]);
                            }
                            if (split[1] != null && !split[1].equals("")) {
                                i.x = Float.parseFloat(split[1]);
                            }
                            if (split[2] != null && !split[2].equals("")) {
                                i.y = Float.parseFloat(split[2]);
                            }
                            if (split[3] != null && !split[3].equals("")) {
                                i.z = Float.parseFloat(split[3]);
                            }
                            if (split[4] != null && !split[4].equals("")) {
                                i.A = Integer.parseInt(split[4]);
                            }
                            if (split[5] != null && !split[5].equals("")) {
                                i.B = Integer.parseInt(split[5]);
                            }
                            if (split[6] != null && !split[6].equals("")) {
                                i.C = Integer.parseInt(split[6]);
                            }
                            if (split[7] != null && !split[7].equals("")) {
                                i.D = Integer.parseInt(split[7]);
                            }
                            if (split[8] != null && !split[8].equals("")) {
                                i.E = Integer.parseInt(split[8]);
                            }
                            if (split[9] != null && !split[9].equals("")) {
                                i.F = Integer.parseInt(split[9]);
                            }
                            if (split[10] != null && !split[10].equals("")) {
                                i.G = Integer.parseInt(split[10]);
                            }
                        }
                    }
                    if (jSONObject.has("up")) {
                        String[] split2 = jSONObject.getString("up").split("\\|");
                        if (split2.length > 3) {
                            if (split2[0] != null && !split2[0].equals("")) {
                                i.H = Float.parseFloat(split2[0]);
                            }
                            if (split2[1] != null && !split2[1].equals("")) {
                                i.I = Float.parseFloat(split2[1]);
                            }
                            if (split2[2] != null && !split2[2].equals("")) {
                                i.J = Float.parseFloat(split2[2]);
                            }
                            if (split2[3] != null && !split2[3].equals("")) {
                                i.K = Float.parseFloat(split2[3]);
                            }
                        }
                    }
                    if (jSONObject.has("wf")) {
                        String[] split3 = jSONObject.getString("wf").split("\\|");
                        if (split3.length > 3) {
                            if (split3[0] != null && !split3[0].equals("")) {
                                i.L = Integer.parseInt(split3[0]);
                            }
                            if (split3[1] != null && !split3[1].equals("")) {
                                i.M = Float.parseFloat(split3[1]);
                            }
                            if (split3[2] != null && !split3[2].equals("")) {
                                i.N = Integer.parseInt(split3[2]);
                            }
                            if (split3[3] != null && !split3[3].equals("")) {
                                i.O = Float.parseFloat(split3[3]);
                            }
                        }
                    }
                    if (jSONObject.has("ab")) {
                        String[] split4 = jSONObject.getString("ab").split("\\|");
                        if (split4.length > 3) {
                            if (split4[0] != null && !split4[0].equals("")) {
                                i.P = Float.parseFloat(split4[0]);
                            }
                            if (split4[1] != null && !split4[1].equals("")) {
                                i.Q = Float.parseFloat(split4[1]);
                            }
                            if (split4[2] != null && !split4[2].equals("")) {
                                i.R = Integer.parseInt(split4[2]);
                            }
                            if (split4[3] != null && !split4[3].equals("")) {
                                i.S = Integer.parseInt(split4[3]);
                            }
                        }
                    }
                    if (jSONObject.has("zxd")) {
                        String[] split5 = jSONObject.getString("zxd").split("\\|");
                        if (split5.length > 4) {
                            if (split5[0] != null && !split5[0].equals("")) {
                                i.an = Float.parseFloat(split5[0]);
                            }
                            if (split5[1] != null && !split5[1].equals("")) {
                                i.ao = Float.parseFloat(split5[1]);
                            }
                            if (split5[2] != null && !split5[2].equals("")) {
                                i.ap = Integer.parseInt(split5[2]);
                            }
                            if (split5[3] != null && !split5[3].equals("")) {
                                i.aq = Integer.parseInt(split5[3]);
                            }
                            if (split5[4] != null && !split5[4].equals("")) {
                                i.ar = Integer.parseInt(split5[4]);
                            }
                        }
                    }
                    if (jSONObject.has("gpc")) {
                        String[] split6 = jSONObject.getString("gpc").split("\\|");
                        if (split6.length > 5) {
                            if (split6[0] != null && !split6[0].equals("")) {
                                if (Integer.parseInt(split6[0]) > 0) {
                                    i.X = true;
                                } else {
                                    i.X = false;
                                }
                            }
                            if (split6[1] != null && !split6[1].equals("")) {
                                if (Integer.parseInt(split6[1]) > 0) {
                                    i.Y = true;
                                } else {
                                    i.Y = false;
                                }
                            }
                            if (split6[2] != null && !split6[2].equals("")) {
                                i.Z = Integer.parseInt(split6[2]);
                            }
                            if (split6[3] != null && !split6[3].equals("")) {
                                i.ab = Integer.parseInt(split6[3]);
                            }
                            if (split6[4] != null && !split6[4].equals("")) {
                                int parseInt2 = Integer.parseInt(split6[4]);
                                if (parseInt2 > 0) {
                                    i.ag = (long) parseInt2;
                                    i.ac = i.ag * 1000 * 60;
                                    i.ah = i.ac >> 2;
                                } else {
                                    i.m = false;
                                }
                            }
                            if (split6[5] != null && !split6[5].equals("")) {
                                i.aj = Integer.parseInt(split6[5]);
                            }
                        }
                    }
                    if (jSONObject.has("shak")) {
                        String[] split7 = jSONObject.getString("shak").split("\\|");
                        if (split7.length > 2) {
                            if (split7[0] != null && !split7[0].equals("")) {
                                i.ak = Integer.parseInt(split7[0]);
                            }
                            if (split7[1] != null && !split7[1].equals("")) {
                                i.al = Integer.parseInt(split7[1]);
                            }
                            if (split7[2] != null && !split7[2].equals("")) {
                                i.am = Float.parseFloat(split7[2]);
                            }
                        }
                    }
                    if (jSONObject.has("dmx")) {
                        i.ai = jSONObject.getInt("dmx");
                    }
                    return z;
                }
            } catch (Exception e3) {
                return false;
            }
        }
        z = false;
        return z;
    }

    /* access modifiers changed from: private */
    public void c(String str) {
        int i2;
        m = -1;
        if (str != null) {
            try {
                if (b(str)) {
                    f();
                }
            } catch (Exception e2) {
            }
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("ctr")) {
                    m = Integer.parseInt(jSONObject.getString("ctr"));
                }
            } catch (Exception e3) {
            }
        }
        try {
            j();
            if (m != -1) {
                i2 = m;
                b(m);
            } else {
                i2 = l != -1 ? l : -1;
            }
            if (i2 != -1) {
                a(i2);
            }
            if (com.baidu.location.f.isServing) {
            }
        } catch (Exception e4) {
        }
    }

    private void e() {
        String str = "&ver=" + i.v + "&usr=" + com.baidu.location.h.c.a().b() + "&app=" + com.baidu.location.h.c.c + "&prod=" + com.baidu.location.h.c.d;
        if (this.j == null) {
            this.j = new a();
        }
        this.j.a(str, false);
    }

    private void f() {
        String str = h.a + "/config.dat";
        byte[] bytes = String.format(Locale.CHINA, "{\"ver\":\"%d\",\"gps\":\"%.1f|%.1f|%.1f|%.1f|%d|%d|%d|%d|%d|%d|%d\",\"up\":\"%.1f|%.1f|%.1f|%.1f\",\"wf\":\"%d|%.1f|%d|%.1f\",\"ab\":\"%.2f|%.2f|%d|%d\",\"gpc\":\"%d|%d|%d|%d|%d|%d\",\"zxd\":\"%.1f|%.1f|%d|%d|%d\",\"shak\":\"%d|%d|%.1f\",\"dmx\":%d}", new Object[]{Integer.valueOf(i.v), Float.valueOf(i.w), Float.valueOf(i.x), Float.valueOf(i.y), Float.valueOf(i.z), Integer.valueOf(i.A), Integer.valueOf(i.B), Integer.valueOf(i.C), Integer.valueOf(i.D), Integer.valueOf(i.E), Integer.valueOf(i.F), Integer.valueOf(i.G), Float.valueOf(i.H), Float.valueOf(i.I), Float.valueOf(i.J), Float.valueOf(i.K), Integer.valueOf(i.L), Float.valueOf(i.M), Integer.valueOf(i.N), Float.valueOf(i.O), Float.valueOf(i.P), Float.valueOf(i.Q), Integer.valueOf(i.R), Integer.valueOf(i.S), Integer.valueOf(i.X ? 1 : 0), Integer.valueOf(i.Y ? 1 : 0), Integer.valueOf(i.Z), Integer.valueOf(i.ab), Long.valueOf(i.ag), Integer.valueOf(i.aj), Float.valueOf(i.an), Float.valueOf(i.ao), Integer.valueOf(i.ap), Integer.valueOf(i.aq), Integer.valueOf(i.ar), Integer.valueOf(i.ak), Integer.valueOf(i.al), Float.valueOf(i.am), Integer.valueOf(i.ai)}).getBytes();
        try {
            File file = new File(str);
            if (!file.exists()) {
                File file2 = new File(h.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(0);
            randomAccessFile2.writeBoolean(true);
            randomAccessFile2.seek(2);
            randomAccessFile2.writeInt(bytes.length);
            randomAccessFile2.write(bytes);
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    private void g() {
        try {
            File file = new File(h.a + "/config.dat");
            if (!file.exists()) {
                File file2 = new File(h.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(1);
            randomAccessFile2.writeBoolean(true);
            randomAccessFile2.seek(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
            randomAccessFile2.writeDouble(i.q);
            randomAccessFile2.writeDouble(i.r);
            randomAccessFile2.writeBoolean(i.u);
            if (i.u && i.t != null) {
                randomAccessFile2.write(i.t);
            }
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    private void h() {
        try {
            File file = new File(h.a + "/config.dat");
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                if (randomAccessFile.readBoolean()) {
                    randomAccessFile.seek(2);
                    int readInt = randomAccessFile.readInt();
                    byte[] bArr = new byte[readInt];
                    randomAccessFile.read(bArr, 0, readInt);
                    b(new String(bArr));
                }
                randomAccessFile.seek(1);
                if (randomAccessFile.readBoolean()) {
                    randomAccessFile.seek(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
                    i.q = randomAccessFile.readDouble();
                    i.r = randomAccessFile.readDouble();
                    i.u = randomAccessFile.readBoolean();
                    if (i.u) {
                        i.t = new byte[625];
                        randomAccessFile.read(i.t, 0, 625);
                    }
                }
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
        c((String) null);
    }

    private void i() {
        try {
            File file = new File(k);
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
                randomAccessFile.writeInt(0);
                randomAccessFile.writeInt(128);
                randomAccessFile.writeInt(0);
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
    }

    private void j() {
        int i2 = 0;
        try {
            File file = new File(k);
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(4);
                int readInt = randomAccessFile.readInt();
                if (readInt > 3000) {
                    randomAccessFile.close();
                    n = 0;
                    i();
                    return;
                }
                int readInt2 = randomAccessFile.readInt();
                randomAccessFile.seek(128);
                byte[] bArr = new byte[readInt];
                while (true) {
                    if (i2 >= readInt2) {
                        break;
                    }
                    randomAccessFile.seek((long) ((readInt * i2) + 128));
                    int readInt3 = randomAccessFile.readInt();
                    if (readInt3 > 0 && readInt3 < readInt) {
                        randomAccessFile.read(bArr, 0, readInt3);
                        if (bArr[readInt3 - 1] == 0) {
                            String str = new String(bArr, 0, readInt3 - 1);
                            com.baidu.location.h.c.a();
                            if (str.equals(com.baidu.location.h.c.c)) {
                                l = randomAccessFile.readInt();
                                n = i2;
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                    i2++;
                }
                if (i2 == readInt2) {
                    n = readInt2;
                }
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
    }

    public void a(String str) {
        if (this.j == null) {
            this.j = new a();
        }
        this.j.a(str, true);
    }

    public void b() {
        h();
    }

    public void c() {
    }

    public void d() {
        if (System.currentTimeMillis() - d.a().c() > 86400000) {
            d.a().b(System.currentTimeMillis());
            e();
        }
    }
}
