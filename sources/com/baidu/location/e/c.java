package com.baidu.location.e;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.os.EnvironmentCompat;
import com.baidu.location.Jni;
import com.baidu.location.h.f;
import com.baidu.location.h.i;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import org.json.JSONObject;

final class c {
    /* access modifiers changed from: private */
    public final d a;
    private final SQLiteDatabase b;
    private final a c;
    /* access modifiers changed from: private */
    public boolean d;
    /* access modifiers changed from: private */
    public boolean e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public boolean h;
    /* access modifiers changed from: private */
    public String[] i;
    /* access modifiers changed from: private */
    public boolean j;
    /* access modifiers changed from: private */
    public int k;
    /* access modifiers changed from: private */
    public int l;
    /* access modifiers changed from: private */
    public int m;
    /* access modifiers changed from: private */
    public double n;
    /* access modifiers changed from: private */
    public double o;
    /* access modifiers changed from: private */
    public double p;
    /* access modifiers changed from: private */
    public double q;
    /* access modifiers changed from: private */
    public double r;
    /* access modifiers changed from: private */
    public int s;
    private boolean t = true;
    /* access modifiers changed from: private */
    public long u = 8000;
    /* access modifiers changed from: private */
    public long v = 5000;
    /* access modifiers changed from: private */
    public long w = 5000;
    /* access modifiers changed from: private */
    public long x = 5000;
    /* access modifiers changed from: private */
    public long y = 5000;

    private final class a extends f {
        private int b;
        private long c;
        private long d;
        private boolean e;
        private final String f;

        private a() {
            this.b = 0;
            this.e = false;
            this.c = -1;
            this.d = -1;
            this.k = new HashMap();
            this.f = Jni.encodeOfflineLocationUpdateRequest(String.format(Locale.US, "&ver=%s&cuid=%s&prod=%s:%s&sdk=%.2f", new Object[]{"1", com.baidu.location.h.c.a().b, com.baidu.location.h.c.d, com.baidu.location.h.c.c, Float.valueOf(6.23f)}));
        }

        /* access modifiers changed from: private */
        public void b() {
            if (!this.e) {
                boolean z = false;
                try {
                    File file = new File(c.this.a.c(), "ofl.config");
                    if (this.d == -1 && file.exists()) {
                        Scanner scanner = new Scanner(file);
                        String next = scanner.next();
                        scanner.close();
                        JSONObject jSONObject = new JSONObject(next);
                        boolean unused = c.this.d = jSONObject.getBoolean("ol");
                        boolean unused2 = c.this.e = jSONObject.getBoolean("fl");
                        boolean unused3 = c.this.f = jSONObject.getBoolean("on");
                        boolean unused4 = c.this.g = jSONObject.getBoolean("wn");
                        boolean unused5 = c.this.h = jSONObject.getBoolean("oc");
                        this.d = jSONObject.getLong("t");
                        if (jSONObject.has("cplist")) {
                            String[] unused6 = c.this.i = jSONObject.getString("cplist").split(";");
                        }
                        if (jSONObject.has("rgcgp")) {
                            int unused7 = c.this.k = jSONObject.getInt("rgcgp");
                        }
                        if (jSONObject.has("rgcon")) {
                            boolean unused8 = c.this.j = jSONObject.getBoolean("rgcon");
                        }
                        if (jSONObject.has("addrup")) {
                            int unused9 = c.this.m = jSONObject.getInt("addrup");
                        }
                        if (jSONObject.has("poiup")) {
                            int unused10 = c.this.l = jSONObject.getInt("poiup");
                        }
                        if (jSONObject.has("oflp")) {
                            JSONObject jSONObject2 = jSONObject.getJSONObject("oflp");
                            if (jSONObject2.has("0")) {
                                double unused11 = c.this.n = jSONObject2.getDouble("0");
                            }
                            if (jSONObject2.has("1")) {
                                double unused12 = c.this.o = jSONObject2.getDouble("1");
                            }
                            if (jSONObject2.has("2")) {
                                double unused13 = c.this.p = jSONObject2.getDouble("2");
                            }
                            if (jSONObject2.has("3")) {
                                double unused14 = c.this.q = jSONObject2.getDouble("3");
                            }
                            if (jSONObject2.has("4")) {
                                double unused15 = c.this.r = jSONObject2.getDouble("4");
                            }
                        }
                        if (jSONObject.has("onlt")) {
                            JSONObject jSONObject3 = jSONObject.getJSONObject("onlt");
                            if (jSONObject3.has("0")) {
                                long unused16 = c.this.y = jSONObject3.getLong("0");
                            }
                            if (jSONObject3.has("1")) {
                                long unused17 = c.this.x = jSONObject3.getLong("1");
                            }
                            if (jSONObject3.has("2")) {
                                long unused18 = c.this.u = jSONObject3.getLong("2");
                            }
                            if (jSONObject3.has("3")) {
                                long unused19 = c.this.v = jSONObject3.getLong("3");
                            }
                            if (jSONObject3.has("4")) {
                                long unused20 = c.this.w = jSONObject3.getLong("4");
                            }
                        }
                        if (jSONObject.has("minapn")) {
                            int unused21 = c.this.s = jSONObject.getInt("minapn");
                        }
                    }
                    if (this.d != -1 || !file.exists()) {
                    }
                    if (this.d != -1 && this.d + 86400000 <= System.currentTimeMillis()) {
                        z = true;
                    }
                } catch (Exception e2) {
                }
                if ((this.d == -1 || z) && c() && i.a(c.this.a.b())) {
                    this.e = true;
                    e();
                }
            }
        }

        private boolean c() {
            boolean z = true;
            if (this.b >= 2) {
                if (this.c + 86400000 < System.currentTimeMillis()) {
                    this.b = 0;
                    this.c = -1;
                } else {
                    z = false;
                }
            }
            if (!z) {
            }
            return z;
        }

        public void a() {
            this.k.clear();
            this.k.put("qt", "conf");
            this.k.put("req", this.f);
            this.h = d.a;
        }

        public void a(boolean z) {
            if (!z || this.j == null) {
                this.b++;
                this.c = System.currentTimeMillis();
            } else {
                try {
                    JSONObject jSONObject = new JSONObject(this.j);
                    String str = "1";
                    long j = 0;
                    if (jSONObject.has("ofl")) {
                        j = jSONObject.getLong("ofl");
                    }
                    if (jSONObject.has("ver")) {
                        str = jSONObject.getString("ver");
                    }
                    if ((j & 1) == 1) {
                        boolean unused = c.this.d = true;
                    }
                    if ((j & 2) == 2) {
                        boolean unused2 = c.this.e = true;
                    }
                    if ((j & 4) == 4) {
                        boolean unused3 = c.this.f = true;
                    }
                    if ((j & 8) == 8) {
                        boolean unused4 = c.this.g = true;
                    }
                    if ((16 & j) == 16) {
                        boolean unused5 = c.this.h = true;
                    }
                    if ((j & 32) == 32) {
                        boolean unused6 = c.this.j = true;
                    }
                    JSONObject jSONObject2 = new JSONObject();
                    if (jSONObject.has("cplist")) {
                        String[] unused7 = c.this.i = jSONObject.getString("cplist").split(";");
                        jSONObject2.put("cplist", jSONObject.getString("cplist"));
                    }
                    if (jSONObject.has("bklist")) {
                        c.this.a(jSONObject.getString("bklist").split(";"));
                    }
                    if (jSONObject.has("para")) {
                        JSONObject jSONObject3 = jSONObject.getJSONObject("para");
                        if (jSONObject3.has("rgcgp")) {
                            int unused8 = c.this.k = jSONObject3.getInt("rgcgp");
                        }
                        if (jSONObject3.has("addrup")) {
                            int unused9 = c.this.m = jSONObject3.getInt("addrup");
                        }
                        if (jSONObject3.has("poiup")) {
                            int unused10 = c.this.l = jSONObject3.getInt("poiup");
                        }
                        if (jSONObject3.has("oflp")) {
                            JSONObject jSONObject4 = jSONObject3.getJSONObject("oflp");
                            if (jSONObject4.has("0")) {
                                double unused11 = c.this.n = jSONObject4.getDouble("0");
                            }
                            if (jSONObject4.has("1")) {
                                double unused12 = c.this.o = jSONObject4.getDouble("1");
                            }
                            if (jSONObject4.has("2")) {
                                double unused13 = c.this.p = jSONObject4.getDouble("2");
                            }
                            if (jSONObject4.has("3")) {
                                double unused14 = c.this.q = jSONObject4.getDouble("3");
                            }
                            if (jSONObject4.has("4")) {
                                double unused15 = c.this.r = jSONObject4.getDouble("4");
                            }
                        }
                        if (jSONObject3.has("onlt")) {
                            JSONObject jSONObject5 = jSONObject3.getJSONObject("onlt");
                            if (jSONObject5.has("0")) {
                                long unused16 = c.this.y = jSONObject5.getLong("0");
                            }
                            if (jSONObject5.has("1")) {
                                long unused17 = c.this.x = jSONObject5.getLong("1");
                            }
                            if (jSONObject5.has("2")) {
                                long unused18 = c.this.u = jSONObject5.getLong("2");
                            }
                            if (jSONObject5.has("3")) {
                                long unused19 = c.this.v = jSONObject5.getLong("3");
                            }
                            if (jSONObject5.has("4")) {
                                long unused20 = c.this.w = jSONObject5.getLong("4");
                            }
                        }
                        if (jSONObject3.has("minapn")) {
                            int unused21 = c.this.s = jSONObject3.getInt("minapn");
                        }
                    }
                    jSONObject2.put("ol", c.this.d);
                    jSONObject2.put("fl", c.this.e);
                    jSONObject2.put("on", c.this.f);
                    jSONObject2.put("wn", c.this.g);
                    jSONObject2.put("oc", c.this.h);
                    this.d = System.currentTimeMillis();
                    jSONObject2.put("t", this.d);
                    jSONObject2.put("ver", str);
                    jSONObject2.put("rgcon", c.this.j);
                    jSONObject2.put("rgcgp", c.this.k);
                    JSONObject jSONObject6 = new JSONObject();
                    jSONObject6.put("0", c.this.n);
                    jSONObject6.put("1", c.this.o);
                    jSONObject6.put("2", c.this.p);
                    jSONObject6.put("3", c.this.q);
                    jSONObject6.put("4", c.this.r);
                    jSONObject2.put("oflp", jSONObject6);
                    JSONObject jSONObject7 = new JSONObject();
                    jSONObject7.put("0", c.this.y);
                    jSONObject7.put("1", c.this.x);
                    jSONObject7.put("2", c.this.u);
                    jSONObject7.put("3", c.this.v);
                    jSONObject7.put("4", c.this.w);
                    jSONObject2.put("onlt", jSONObject7);
                    jSONObject2.put("addrup", c.this.m);
                    jSONObject2.put("poiup", c.this.l);
                    jSONObject2.put("minapn", c.this.s);
                    File file = new File(c.this.a.c(), "ofl.config");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(jSONObject2.toString());
                    fileWriter.close();
                } catch (Exception e2) {
                    this.b++;
                    this.c = System.currentTimeMillis();
                }
            }
            this.e = false;
        }
    }

    c(d dVar, SQLiteDatabase sQLiteDatabase) {
        this.a = dVar;
        this.d = false;
        this.e = false;
        this.f = false;
        this.g = false;
        this.h = false;
        this.j = false;
        this.k = 6;
        this.l = 30;
        this.m = 30;
        this.n = 0.0d;
        this.o = 0.0d;
        this.p = 0.0d;
        this.q = 0.0d;
        this.r = 0.0d;
        this.s = 8;
        this.i = new String[0];
        this.b = sQLiteDatabase;
        this.c = new a();
        if (this.b != null && this.b.isOpen()) {
            try {
                this.b.execSQL("CREATE TABLE IF NOT EXISTS BLACK (name VARCHAR(100) PRIMARY KEY);");
            } catch (Exception e2) {
            }
        }
        g();
    }

    /* access modifiers changed from: package-private */
    public int a() {
        return this.s;
    }

    /* access modifiers changed from: package-private */
    public long a(String str) {
        if (str.equals("2G")) {
            return this.u;
        }
        if (str.equals("3G")) {
            return this.v;
        }
        if (str.equals("4G")) {
            return this.w;
        }
        if (str.equals("WIFI")) {
            return this.x;
        }
        if (str.equals(EnvironmentCompat.MEDIA_UNKNOWN)) {
            return this.y;
        }
        return 5000;
    }

    /* access modifiers changed from: package-private */
    public void a(String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (i2 > 0) {
                stringBuffer.append(",");
            }
            stringBuffer.append("(\"");
            stringBuffer.append(strArr[i2]);
            stringBuffer.append("\")");
        }
        if (this.b != null && this.b.isOpen() && stringBuffer.length() > 0) {
            try {
                this.b.execSQL(String.format(Locale.US, "INSERT OR IGNORE INTO BLACK VALUES %s;", new Object[]{stringBuffer.toString()}));
            } catch (Exception e2) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public double b() {
        return this.n;
    }

    /* access modifiers changed from: package-private */
    public double c() {
        return this.o;
    }

    /* access modifiers changed from: package-private */
    public double d() {
        return this.p;
    }

    /* access modifiers changed from: package-private */
    public double e() {
        return this.q;
    }

    /* access modifiers changed from: package-private */
    public double f() {
        return this.r;
    }

    /* access modifiers changed from: package-private */
    public void g() {
        this.c.b();
    }

    /* access modifiers changed from: package-private */
    public boolean h() {
        return this.d;
    }

    /* access modifiers changed from: package-private */
    public boolean i() {
        return this.f;
    }

    /* access modifiers changed from: package-private */
    public boolean j() {
        return this.g;
    }

    /* access modifiers changed from: package-private */
    public boolean k() {
        return this.e;
    }

    /* access modifiers changed from: package-private */
    public boolean l() {
        return this.j;
    }

    /* access modifiers changed from: package-private */
    public boolean m() {
        return this.t;
    }

    /* access modifiers changed from: package-private */
    public int n() {
        return this.k;
    }

    /* access modifiers changed from: package-private */
    public String[] o() {
        return this.i;
    }

    /* access modifiers changed from: package-private */
    public int p() {
        return this.m;
    }

    /* access modifiers changed from: package-private */
    public int q() {
        return this.l;
    }
}
