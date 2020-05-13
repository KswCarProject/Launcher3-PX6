package com.baidu.location.e;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.Poi;
import com.baidu.location.e.e;
import com.baidu.location.h.f;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.message.TokenParser;
import org.json.JSONObject;

final class b {
    /* access modifiers changed from: private */
    public final d a;
    private int b;
    private double c;
    private double d;
    private Long e;
    private final c f = new c(this, true);
    /* access modifiers changed from: private */
    public final c g = new c(this, false);
    /* access modifiers changed from: private */
    public final SQLiteDatabase h;
    /* access modifiers changed from: private */
    public final SQLiteDatabase i;
    /* access modifiers changed from: private */
    public StringBuffer j = null;
    /* access modifiers changed from: private */
    public StringBuffer k = null;
    private HashSet<Long> l = new HashSet<>();
    private ConcurrentHashMap<Long, Integer> m = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, String> n = new ConcurrentHashMap<>();
    private StringBuffer o = new StringBuffer();
    private boolean p = false;

    private static final class a {
        double a;
        double b;
        double c;

        private a(double d, double d2, double d3) {
            this.a = d;
            this.b = d2;
            this.c = d3;
        }
    }

    /* renamed from: com.baidu.location.e.b$b  reason: collision with other inner class name */
    private class C0005b extends Thread {
        private String a;
        private Long c;
        private BDLocation d;
        private BDLocation e;
        private BDLocation f;
        private String g;
        private LinkedHashMap<String, Integer> h;

        private C0005b(String str, Long l, BDLocation bDLocation, BDLocation bDLocation2, BDLocation bDLocation3, String str2, LinkedHashMap<String, Integer> linkedHashMap) {
            this.a = str;
            this.c = l;
            this.d = bDLocation;
            this.e = bDLocation2;
            this.f = bDLocation3;
            this.g = str2;
            this.h = linkedHashMap;
        }

        public void run() {
            try {
                b.this.a(this.a, this.c, this.d);
                StringBuffer unused = b.this.j = null;
                StringBuffer unused2 = b.this.k = null;
                b.this.a(this.h);
                b.this.a(this.f, this.d, this.e, this.a, this.c);
                if (this.g != null) {
                    b.this.a.j().a(this.g);
                }
            } catch (Exception e2) {
            }
            this.h = null;
            this.a = null;
            this.g = null;
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = null;
        }
    }

    private final class c extends f {
        private String b;
        private final String c;
        private String d;
        /* access modifiers changed from: private */
        public b e;
        /* access modifiers changed from: private */
        public boolean f = false;
        private int p = 0;
        private long q = -1;
        /* access modifiers changed from: private */
        public long r = -1;
        private long s = -1;
        private long t = -1;

        c(b bVar, boolean z) {
            this.e = bVar;
            if (z) {
                this.c = "load";
            } else {
                this.c = "update";
            }
            this.k = new HashMap();
            this.b = d.a;
        }

        /* access modifiers changed from: private */
        public void a(String str, String str2, String str3) {
            this.d = str3;
            this.b = String.format("http://%s/%s", new Object[]{str, str2});
            e();
        }

        /* access modifiers changed from: private */
        public void c() {
            this.p++;
            this.q = System.currentTimeMillis();
        }

        private boolean f() {
            if (this.p < 2) {
                return true;
            }
            if (this.q + 43200000 >= System.currentTimeMillis()) {
                return false;
            }
            this.p = 0;
            this.q = -1;
            return true;
        }

        private void g() {
            this.d = null;
            if (!l()) {
                this.d = i();
            } else if (this.r == -1 || this.r + 86400000 <= System.currentTimeMillis()) {
                this.d = h();
            }
            if (this.d == null && (this.s == -1 || this.s + 86400000 <= System.currentTimeMillis())) {
                if (b.this.a.k().a()) {
                    this.d = j();
                } else {
                    this.d = k();
                }
            }
            if (this.d != null) {
                e();
            }
        }

        private String h() {
            JSONObject jSONObject;
            try {
                jSONObject = new JSONObject();
                jSONObject.put("type", "0");
                jSONObject.put("cuid", com.baidu.location.h.c.a().b);
                jSONObject.put("ver", "1");
                jSONObject.put("prod", com.baidu.location.h.c.d + ":" + com.baidu.location.h.c.c);
            } catch (Exception e2) {
                jSONObject = null;
            }
            if (jSONObject != null) {
                return Jni.encodeOfflineLocationUpdateRequest(jSONObject.toString());
            }
            return null;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:64:0x015a, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x015b, code lost:
            r2 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0176, code lost:
            r0 = null;
            r2 = null;
            r3 = null;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x005d A[SYNTHETIC, Splitter:B:16:0x005d] */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0062 A[SYNTHETIC, Splitter:B:19:0x0062] */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x0086  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x009f  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00da A[SYNTHETIC, Splitter:B:39:0x00da] */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0147 A[SYNTHETIC, Splitter:B:54:0x0147] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x014c A[SYNTHETIC, Splitter:B:57:0x014c] */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x015a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:1:0x0002] */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x015e A[SYNTHETIC, Splitter:B:67:0x015e] */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0163 A[SYNTHETIC, Splitter:B:70:0x0163] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0180  */
        /* JADX WARNING: Removed duplicated region for block: B:91:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.lang.String i() {
            /*
                r11 = this;
                r4 = 0
                r1 = 0
                org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ Exception -> 0x0175, all -> 0x015a }
                r6.<init>()     // Catch:{ Exception -> 0x0175, all -> 0x015a }
                org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0175, all -> 0x015a }
                r0.<init>()     // Catch:{ Exception -> 0x0175, all -> 0x015a }
                com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                java.lang.String r3 = "SELECT * FROM %s WHERE frequency>%d ORDER BY frequency DESC LIMIT %d;"
                r5 = 3
                java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                r7 = 0
                java.lang.String r8 = "CL"
                r5[r7] = r8     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                r7 = 1
                r8 = 5
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                r5[r7] = r8     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                r7 = 2
                r8 = 50
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                r5[r7] = r8     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                r5 = 0
                android.database.Cursor r2 = r2.rawQuery(r3, r5)     // Catch:{ Exception -> 0x017b, all -> 0x015a }
                if (r2 == 0) goto L_0x0186
                boolean r3 = r2.moveToFirst()     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                if (r3 == 0) goto L_0x0186
                int r3 = r2.getCount()     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                org.json.JSONArray r5 = new org.json.JSONArray     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r5.<init>()     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
            L_0x0047:
                boolean r7 = r2.isAfterLast()     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                if (r7 != 0) goto L_0x00a8
                r7 = 1
                java.lang.String r7 = r2.getString(r7)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r5.put(r7)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r2.moveToNext()     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                goto L_0x0047
            L_0x0059:
                r3 = move-exception
                r3 = r1
            L_0x005b:
                if (r3 == 0) goto L_0x0060
                r3.close()     // Catch:{ Exception -> 0x0169 }
            L_0x0060:
                if (r2 == 0) goto L_0x0183
                r2.close()     // Catch:{ Exception -> 0x0156 }
                r2 = r0
            L_0x0066:
                if (r2 == 0) goto L_0x0180
                java.lang.String r0 = "model"
                boolean r0 = r2.has(r0)
                if (r0 != 0) goto L_0x0180
                long r4 = r11.t
                r6 = -1
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 == 0) goto L_0x0086
                long r4 = r11.t
                r6 = 86400000(0x5265c00, double:4.2687272E-316)
                long r4 = r4 + r6
                long r6 = java.lang.System.currentTimeMillis()
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 >= 0) goto L_0x0180
            L_0x0086:
                java.lang.String r0 = r2.toString()
                java.lang.String r1 = com.baidu.location.Jni.encodeOfflineLocationUpdateRequest(r0)
                long r4 = java.lang.System.currentTimeMillis()
                r11.t = r4
                r0 = r1
            L_0x0095:
                if (r2 == 0) goto L_0x00a7
                java.lang.String r1 = "model"
                boolean r1 = r2.has(r1)
                if (r1 == 0) goto L_0x00a7
                java.lang.String r0 = r2.toString()
                java.lang.String r0 = com.baidu.location.Jni.encodeOfflineLocationUpdateRequest(r0)
            L_0x00a7:
                return r0
            L_0x00a8:
                java.lang.String r7 = "cell"
                r6.put(r7, r5)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r5 = r3
            L_0x00ae:
                com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                android.database.sqlite.SQLiteDatabase r3 = r3.i     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                java.lang.String r7 = "SELECT * FROM %s WHERE frequency>%d ORDER BY frequency DESC LIMIT %d;"
                r8 = 3
                java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r9 = 0
                java.lang.String r10 = "AP"
                r8[r9] = r10     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r9 = 1
                r10 = 5
                java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r8[r9] = r10     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r9 = 2
                r10 = 50
                java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r8[r9] = r10     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                java.lang.String r7 = java.lang.String.format(r7, r8)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                r8 = 0
                android.database.Cursor r3 = r3.rawQuery(r7, r8)     // Catch:{ Exception -> 0x0059, all -> 0x0170 }
                if (r3 == 0) goto L_0x0103
                boolean r7 = r3.moveToFirst()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                if (r7 == 0) goto L_0x0103
                int r4 = r3.getCount()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                org.json.JSONArray r7 = new org.json.JSONArray     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                r7.<init>()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
            L_0x00e9:
                boolean r8 = r3.isAfterLast()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                if (r8 != 0) goto L_0x00fe
                r8 = 1
                java.lang.String r8 = r3.getString(r8)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                r7.put(r8)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                r3.moveToNext()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                goto L_0x00e9
            L_0x00fb:
                r4 = move-exception
                goto L_0x005b
            L_0x00fe:
                java.lang.String r8 = "ap"
                r6.put(r8, r7)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
            L_0x0103:
                java.lang.String r7 = "type"
                java.lang.String r8 = "1"
                r0.put(r7, r8)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r7 = "cuid"
                com.baidu.location.h.c r8 = com.baidu.location.h.c.a()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r8 = r8.b     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                r0.put(r7, r8)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r7 = "ver"
                java.lang.String r8 = "1"
                r0.put(r7, r8)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r7 = "prod"
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                r8.<init>()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r9 = com.baidu.location.h.c.d     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r9 = ":"
                java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r9 = com.baidu.location.h.c.c     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                r0.put(r7, r8)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
                if (r5 != 0) goto L_0x0140
                if (r4 == 0) goto L_0x0145
            L_0x0140:
                java.lang.String r4 = "model"
                r0.put(r4, r6)     // Catch:{ Exception -> 0x00fb, all -> 0x0172 }
            L_0x0145:
                if (r3 == 0) goto L_0x014a
                r3.close()     // Catch:{ Exception -> 0x0167 }
            L_0x014a:
                if (r2 == 0) goto L_0x0183
                r2.close()     // Catch:{ Exception -> 0x0152 }
                r2 = r0
                goto L_0x0066
            L_0x0152:
                r2 = move-exception
                r2 = r0
                goto L_0x0066
            L_0x0156:
                r2 = move-exception
                r2 = r0
                goto L_0x0066
            L_0x015a:
                r0 = move-exception
                r2 = r1
            L_0x015c:
                if (r1 == 0) goto L_0x0161
                r1.close()     // Catch:{ Exception -> 0x016c }
            L_0x0161:
                if (r2 == 0) goto L_0x0166
                r2.close()     // Catch:{ Exception -> 0x016e }
            L_0x0166:
                throw r0
            L_0x0167:
                r3 = move-exception
                goto L_0x014a
            L_0x0169:
                r3 = move-exception
                goto L_0x0060
            L_0x016c:
                r1 = move-exception
                goto L_0x0161
            L_0x016e:
                r1 = move-exception
                goto L_0x0166
            L_0x0170:
                r0 = move-exception
                goto L_0x015c
            L_0x0172:
                r0 = move-exception
                r1 = r3
                goto L_0x015c
            L_0x0175:
                r0 = move-exception
                r0 = r1
                r2 = r1
                r3 = r1
                goto L_0x005b
            L_0x017b:
                r2 = move-exception
                r2 = r1
                r3 = r1
                goto L_0x005b
            L_0x0180:
                r0 = r1
                goto L_0x0095
            L_0x0183:
                r2 = r0
                goto L_0x0066
            L_0x0186:
                r5 = r4
                goto L_0x00ae
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.c.i():java.lang.String");
        }

        private String j() {
            JSONObject jSONObject;
            try {
                jSONObject = new JSONObject();
                try {
                    jSONObject.put("type", "2");
                    jSONObject.put("ver", "1");
                    jSONObject.put("cuid", com.baidu.location.h.c.a().b);
                    jSONObject.put("prod", com.baidu.location.h.c.d + ":" + com.baidu.location.h.c.c);
                    this.s = System.currentTimeMillis();
                } catch (Exception e2) {
                }
            } catch (Exception e3) {
                jSONObject = null;
            }
            if (jSONObject != null) {
                return Jni.encodeOfflineLocationUpdateRequest(jSONObject.toString());
            }
            return null;
        }

        private String k() {
            JSONObject jSONObject;
            try {
                JSONObject b2 = b.this.a.k().b();
                if (b2 != null) {
                    jSONObject = new JSONObject();
                    try {
                        jSONObject.put("type", "3");
                        jSONObject.put("ver", "1");
                        jSONObject.put("cuid", com.baidu.location.h.c.a().b);
                        jSONObject.put("prod", com.baidu.location.h.c.d + ":" + com.baidu.location.h.c.c);
                        jSONObject.put("rgc", b2);
                        this.s = System.currentTimeMillis();
                    } catch (Exception e2) {
                    }
                } else {
                    jSONObject = null;
                }
            } catch (Exception e3) {
                jSONObject = null;
            }
            if (jSONObject != null) {
                return Jni.encodeOfflineLocationUpdateRequest(jSONObject.toString());
            }
            return null;
        }

        /* JADX WARNING: Removed duplicated region for block: B:35:0x005a A[SYNTHETIC, Splitter:B:35:0x005a] */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x005f A[SYNTHETIC, Splitter:B:38:0x005f] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean l() {
            /*
                r7 = this;
                r3 = 0
                r1 = 0
                r0 = 1
                com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0047, all -> 0x0056 }
                android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0047, all -> 0x0056 }
                java.lang.String r4 = "SELECT COUNT(*) FROM AP;"
                r5 = 0
                android.database.Cursor r2 = r2.rawQuery(r4, r5)     // Catch:{ Exception -> 0x0047, all -> 0x0056 }
                com.baidu.location.e.b r4 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                android.database.sqlite.SQLiteDatabase r4 = r4.h     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                java.lang.String r5 = "SELECT COUNT(*) FROM CL"
                r6 = 0
                android.database.Cursor r1 = r4.rawQuery(r5, r6)     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                if (r2 == 0) goto L_0x003c
                boolean r4 = r2.moveToFirst()     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                if (r4 == 0) goto L_0x003c
                if (r1 == 0) goto L_0x003c
                boolean r4 = r1.moveToFirst()     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                if (r4 == 0) goto L_0x003c
                r4 = 0
                int r4 = r2.getInt(r4)     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                if (r4 != 0) goto L_0x003b
                r4 = 0
                int r4 = r1.getInt(r4)     // Catch:{ Exception -> 0x006f, all -> 0x006d }
                if (r4 == 0) goto L_0x003c
            L_0x003b:
                r0 = r3
            L_0x003c:
                if (r2 == 0) goto L_0x0041
                r2.close()     // Catch:{ Exception -> 0x0063 }
            L_0x0041:
                if (r1 == 0) goto L_0x0046
                r1.close()     // Catch:{ Exception -> 0x0065 }
            L_0x0046:
                return r0
            L_0x0047:
                r2 = move-exception
                r2 = r1
            L_0x0049:
                if (r2 == 0) goto L_0x004e
                r2.close()     // Catch:{ Exception -> 0x0067 }
            L_0x004e:
                if (r1 == 0) goto L_0x0046
                r1.close()     // Catch:{ Exception -> 0x0054 }
                goto L_0x0046
            L_0x0054:
                r1 = move-exception
                goto L_0x0046
            L_0x0056:
                r0 = move-exception
                r2 = r1
            L_0x0058:
                if (r2 == 0) goto L_0x005d
                r2.close()     // Catch:{ Exception -> 0x0069 }
            L_0x005d:
                if (r1 == 0) goto L_0x0062
                r1.close()     // Catch:{ Exception -> 0x006b }
            L_0x0062:
                throw r0
            L_0x0063:
                r2 = move-exception
                goto L_0x0041
            L_0x0065:
                r1 = move-exception
                goto L_0x0046
            L_0x0067:
                r2 = move-exception
                goto L_0x004e
            L_0x0069:
                r2 = move-exception
                goto L_0x005d
            L_0x006b:
                r1 = move-exception
                goto L_0x0062
            L_0x006d:
                r0 = move-exception
                goto L_0x0058
            L_0x006f:
                r3 = move-exception
                goto L_0x0049
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.c.l():boolean");
        }

        public void a() {
            this.f = true;
            this.h = this.b;
            this.k.clear();
            this.k.put("qt", this.c);
            this.k.put("req", this.d);
        }

        public void a(boolean z) {
            if (!z || this.j == null) {
                this.f = false;
                c();
                return;
            }
            new Thread() {
                /* JADX WARNING: Removed duplicated region for block: B:115:0x033c A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:117:0x035b A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:119:0x037a A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:126:0x03c6 A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:150:0x04df A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:152:0x04fe A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:154:0x051d A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* JADX WARNING: Removed duplicated region for block: B:26:0x0092  */
                /* JADX WARNING: Removed duplicated region for block: B:49:0x014f A[Catch:{ Exception -> 0x0252, all -> 0x02c6 }] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r21 = this;
                        super.run()
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this
                        android.database.sqlite.SQLiteDatabase r2 = r2.h
                        if (r2 == 0) goto L_0x003b
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this
                        android.database.sqlite.SQLiteDatabase r2 = r2.i
                        if (r2 == 0) goto L_0x003b
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this
                        android.database.sqlite.SQLiteDatabase r2 = r2.h
                        boolean r2 = r2.isOpen()
                        if (r2 == 0) goto L_0x003b
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this
                        android.database.sqlite.SQLiteDatabase r2 = r2.i
                        boolean r2 = r2.isOpen()
                        if (r2 != 0) goto L_0x0044
                    L_0x003b:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        r3 = 0
                        boolean unused = r2.f = r3
                    L_0x0043:
                        return
                    L_0x0044:
                        r5 = 0
                        r4 = 0
                        r2 = 0
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x023d }
                        java.lang.String r3 = r3.j     // Catch:{ Exception -> 0x023d }
                        if (r3 == 0) goto L_0x068c
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x023d }
                        java.lang.String r6 = r3.j     // Catch:{ Exception -> 0x023d }
                        org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x023d }
                        r3.<init>(r6)     // Catch:{ Exception -> 0x023d }
                        java.lang.String r4 = "model"
                        boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x067e }
                        if (r4 == 0) goto L_0x0689
                        java.lang.String r4 = "model"
                        org.json.JSONObject r4 = r3.getJSONObject(r4)     // Catch:{ Exception -> 0x067e }
                    L_0x0068:
                        java.lang.String r5 = "rgc"
                        boolean r5 = r3.has(r5)     // Catch:{ Exception -> 0x0686 }
                        if (r5 == 0) goto L_0x0076
                        java.lang.String r5 = "rgc"
                        org.json.JSONObject r2 = r3.getJSONObject(r5)     // Catch:{ Exception -> 0x0686 }
                    L_0x0076:
                        r0 = r21
                        com.baidu.location.e.b$c r5 = com.baidu.location.e.b.c.this
                        com.baidu.location.e.b r5 = com.baidu.location.e.b.this
                        android.database.sqlite.SQLiteDatabase r5 = r5.h
                        r5.beginTransaction()
                        r0 = r21
                        com.baidu.location.e.b$c r5 = com.baidu.location.e.b.c.this
                        com.baidu.location.e.b r5 = com.baidu.location.e.b.this
                        android.database.sqlite.SQLiteDatabase r5 = r5.i
                        r5.beginTransaction()
                        if (r2 == 0) goto L_0x00a3
                        r0 = r21
                        com.baidu.location.e.b$c r5 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r5 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.d r5 = r5.a     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.g r5 = r5.k()     // Catch:{ Exception -> 0x0252 }
                        r5.a(r2)     // Catch:{ Exception -> 0x0252 }
                    L_0x00a3:
                        if (r3 == 0) goto L_0x00c6
                        java.lang.String r2 = "type"
                        boolean r2 = r3.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x00c6
                        java.lang.String r2 = "type"
                        java.lang.String r2 = r3.getString(r2)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r5 = "0"
                        boolean r2 = r2.equals(r5)     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x00c6
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0252 }
                        long unused = r2.r = r6     // Catch:{ Exception -> 0x0252 }
                    L_0x00c6:
                        if (r3 == 0) goto L_0x00e7
                        java.lang.String r2 = "bdlist"
                        boolean r2 = r3.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x00e7
                        java.lang.String r2 = "bdlist"
                        java.lang.String r2 = r3.getString(r2)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r5 = ";"
                        java.lang.String[] r2 = r2.split(r5)     // Catch:{ Exception -> 0x0252 }
                        r0 = r21
                        com.baidu.location.e.b$c r5 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r5 = r5.e     // Catch:{ Exception -> 0x0252 }
                        r5.a((java.lang.String[]) r2)     // Catch:{ Exception -> 0x0252 }
                    L_0x00e7:
                        if (r3 == 0) goto L_0x0120
                        java.lang.String r2 = "loadurl"
                        boolean r2 = r3.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x0120
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = r2.e     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r5 = "loadurl"
                        org.json.JSONObject r5 = r3.getJSONObject(r5)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r6 = "host"
                        java.lang.String r5 = r5.getString(r6)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r6 = "loadurl"
                        org.json.JSONObject r6 = r3.getJSONObject(r6)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r7 = "module"
                        java.lang.String r6 = r6.getString(r7)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r7 = "loadurl"
                        org.json.JSONObject r3 = r3.getJSONObject(r7)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r7 = "req"
                        java.lang.String r3 = r3.getString(r7)     // Catch:{ Exception -> 0x0252 }
                        r2.a((java.lang.String) r5, (java.lang.String) r6, (java.lang.String) r3)     // Catch:{ Exception -> 0x0252 }
                    L_0x0120:
                        if (r4 == 0) goto L_0x0397
                        java.lang.String r2 = "cell"
                        boolean r2 = r4.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x0397
                        java.lang.String r2 = "cell"
                        org.json.JSONObject r10 = r4.getJSONObject(r2)     // Catch:{ Exception -> 0x0252 }
                        java.util.Iterator r11 = r10.keys()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r12 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0252 }
                        r12.<init>()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r13 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0252 }
                        r13.<init>()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r14 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0252 }
                        r14.<init>()     // Catch:{ Exception -> 0x0252 }
                        r9 = 1
                        r8 = 1
                        r6 = 1
                        r3 = 0
                        r7 = 0
                        r5 = 0
                    L_0x0149:
                        boolean r2 = r11.hasNext()     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x033a
                        java.lang.Object r2 = r11.next()     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r15 = r10.getString(r2)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r16 = ","
                        java.lang.String[] r16 = r15.split(r16)     // Catch:{ Exception -> 0x0252 }
                        r17 = 3
                        r16 = r16[r17]     // Catch:{ Exception -> 0x0252 }
                        java.lang.Double r16 = java.lang.Double.valueOf(r16)     // Catch:{ Exception -> 0x0252 }
                        if (r8 == 0) goto L_0x0249
                        r8 = 0
                    L_0x016a:
                        r13.append(r2)     // Catch:{ Exception -> 0x0252 }
                        int r7 = r7 + 1
                        double r16 = r16.doubleValue()     // Catch:{ Exception -> 0x0252 }
                        r18 = 0
                        int r16 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
                        if (r16 <= 0) goto L_0x0329
                        if (r6 == 0) goto L_0x02bd
                        r6 = 0
                    L_0x017c:
                        r16 = 40
                        r0 = r16
                        java.lang.StringBuffer r16 = r14.append(r0)     // Catch:{ Exception -> 0x0252 }
                        r0 = r16
                        java.lang.StringBuffer r2 = r0.append(r2)     // Catch:{ Exception -> 0x0252 }
                        r16 = 44
                        r0 = r16
                        java.lang.StringBuffer r2 = r2.append(r0)     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r2 = r2.append(r15)     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0252 }
                        r15.<init>()     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r16 = ","
                        java.lang.StringBuilder r15 = r15.append(r16)     // Catch:{ Exception -> 0x0252 }
                        long r16 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0252 }
                        r18 = 1000(0x3e8, double:4.94E-321)
                        long r16 = r16 / r18
                        java.lang.StringBuilder r15 = r15.append(r16)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r15 = r15.toString()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r2 = r2.append(r15)     // Catch:{ Exception -> 0x0252 }
                        r15 = 41
                        r2.append(r15)     // Catch:{ Exception -> 0x0252 }
                        int r5 = r5 + 1
                        r2 = r3
                        r3 = r9
                    L_0x01be:
                        r9 = 100
                        if (r7 < r9) goto L_0x01e7
                        r0 = r21
                        com.baidu.location.e.b$c r8 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r8 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r8 = r8.i     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = "DELETE FROM CL WHERE id IN (%s);"
                        r15 = 1
                        java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x0252 }
                        r16 = 0
                        java.lang.String r17 = r13.toString()     // Catch:{ Exception -> 0x0252 }
                        r15[r16] = r17     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = java.lang.String.format(r9, r15)     // Catch:{ Exception -> 0x0252 }
                        r8.execSQL(r9)     // Catch:{ Exception -> 0x0252 }
                        r8 = 1
                        r9 = 0
                        r13.setLength(r9)     // Catch:{ Exception -> 0x0252 }
                        int r7 = r7 + -100
                    L_0x01e7:
                        r9 = 100
                        if (r5 < r9) goto L_0x0210
                        r0 = r21
                        com.baidu.location.e.b$c r6 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r6 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r6 = r6.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = "INSERT OR REPLACE INTO CL (id,x,y,r,cl,timestamp) VALUES %s;"
                        r15 = 1
                        java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x0252 }
                        r16 = 0
                        java.lang.String r17 = r14.toString()     // Catch:{ Exception -> 0x0252 }
                        r15[r16] = r17     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = java.lang.String.format(r9, r15)     // Catch:{ Exception -> 0x0252 }
                        r6.execSQL(r9)     // Catch:{ Exception -> 0x0252 }
                        r6 = 1
                        r9 = 0
                        r14.setLength(r9)     // Catch:{ Exception -> 0x0252 }
                        int r5 = r5 + -100
                    L_0x0210:
                        r9 = 100
                        if (r2 < r9) goto L_0x0239
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = "DELETE FROM CL WHERE id IN (%s);"
                        r15 = 1
                        java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x0252 }
                        r16 = 0
                        java.lang.String r17 = r12.toString()     // Catch:{ Exception -> 0x0252 }
                        r15[r16] = r17     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = java.lang.String.format(r9, r15)     // Catch:{ Exception -> 0x0252 }
                        r3.execSQL(r9)     // Catch:{ Exception -> 0x0252 }
                        r3 = 1
                        r9 = 0
                        r12.setLength(r9)     // Catch:{ Exception -> 0x0252 }
                        int r2 = r2 + -100
                    L_0x0239:
                        r9 = r3
                        r3 = r2
                        goto L_0x0149
                    L_0x023d:
                        r3 = move-exception
                        r20 = r3
                        r3 = r4
                        r4 = r5
                        r5 = r20
                    L_0x0244:
                        r5.printStackTrace()
                        goto L_0x0076
                    L_0x0249:
                        r17 = 44
                        r0 = r17
                        r13.append(r0)     // Catch:{ Exception -> 0x0252 }
                        goto L_0x016a
                    L_0x0252:
                        r2 = move-exception
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ all -> 0x02c6 }
                        r2.c()     // Catch:{ all -> 0x02c6 }
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0679 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0679 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0679 }
                        if (r2 == 0) goto L_0x0283
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0679 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0679 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0679 }
                        boolean r2 = r2.isOpen()     // Catch:{ Exception -> 0x0679 }
                        if (r2 == 0) goto L_0x0283
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0679 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0679 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0679 }
                        r2.endTransaction()     // Catch:{ Exception -> 0x0679 }
                    L_0x0283:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0679 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0679 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0679 }
                        if (r2 == 0) goto L_0x02ac
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0679 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0679 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0679 }
                        boolean r2 = r2.isOpen()     // Catch:{ Exception -> 0x0679 }
                        if (r2 == 0) goto L_0x02ac
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0679 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0679 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0679 }
                        r2.endTransaction()     // Catch:{ Exception -> 0x0679 }
                    L_0x02ac:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        r3 = 0
                        r2.j = r3
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        r3 = 0
                        boolean unused = r2.f = r3
                        goto L_0x0043
                    L_0x02bd:
                        r16 = 44
                        r0 = r16
                        r14.append(r0)     // Catch:{ Exception -> 0x0252 }
                        goto L_0x017c
                    L_0x02c6:
                        r2 = move-exception
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0676 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0676 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.h     // Catch:{ Exception -> 0x0676 }
                        if (r3 == 0) goto L_0x02f0
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0676 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0676 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.h     // Catch:{ Exception -> 0x0676 }
                        boolean r3 = r3.isOpen()     // Catch:{ Exception -> 0x0676 }
                        if (r3 == 0) goto L_0x02f0
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0676 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0676 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.h     // Catch:{ Exception -> 0x0676 }
                        r3.endTransaction()     // Catch:{ Exception -> 0x0676 }
                    L_0x02f0:
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0676 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0676 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.i     // Catch:{ Exception -> 0x0676 }
                        if (r3 == 0) goto L_0x0319
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0676 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0676 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.i     // Catch:{ Exception -> 0x0676 }
                        boolean r3 = r3.isOpen()     // Catch:{ Exception -> 0x0676 }
                        if (r3 == 0) goto L_0x0319
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0676 }
                        com.baidu.location.e.b r3 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0676 }
                        android.database.sqlite.SQLiteDatabase r3 = r3.i     // Catch:{ Exception -> 0x0676 }
                        r3.endTransaction()     // Catch:{ Exception -> 0x0676 }
                    L_0x0319:
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this
                        r4 = 0
                        r3.j = r4
                        r0 = r21
                        com.baidu.location.e.b$c r3 = com.baidu.location.e.b.c.this
                        r4 = 0
                        boolean unused = r3.f = r4
                        throw r2
                    L_0x0329:
                        if (r9 == 0) goto L_0x0334
                        r9 = 0
                    L_0x032c:
                        r12.append(r2)     // Catch:{ Exception -> 0x0252 }
                        int r2 = r3 + 1
                        r3 = r9
                        goto L_0x01be
                    L_0x0334:
                        r15 = 44
                        r12.append(r15)     // Catch:{ Exception -> 0x0252 }
                        goto L_0x032c
                    L_0x033a:
                        if (r7 <= 0) goto L_0x0359
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r6 = "DELETE FROM CL WHERE id IN (%s);"
                        r7 = 1
                        java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0252 }
                        r8 = 0
                        java.lang.String r9 = r13.toString()     // Catch:{ Exception -> 0x0252 }
                        r7[r8] = r9     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r6 = java.lang.String.format(r6, r7)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r6)     // Catch:{ Exception -> 0x0252 }
                    L_0x0359:
                        if (r5 <= 0) goto L_0x0378
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r5 = "INSERT OR REPLACE INTO CL (id,x,y,r,cl,timestamp) VALUES %s;"
                        r6 = 1
                        java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0252 }
                        r7 = 0
                        java.lang.String r8 = r14.toString()     // Catch:{ Exception -> 0x0252 }
                        r6[r7] = r8     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r5 = java.lang.String.format(r5, r6)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r5)     // Catch:{ Exception -> 0x0252 }
                    L_0x0378:
                        if (r3 <= 0) goto L_0x0397
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM CL WHERE id IN (%s);"
                        r5 = 1
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = r12.toString()     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                    L_0x0397:
                        if (r4 == 0) goto L_0x053a
                        java.lang.String r2 = "ap"
                        boolean r2 = r4.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x053a
                        java.lang.String r2 = "ap"
                        org.json.JSONObject r10 = r4.getJSONObject(r2)     // Catch:{ Exception -> 0x0252 }
                        java.util.Iterator r11 = r10.keys()     // Catch:{ Exception -> 0x0252 }
                        r9 = 0
                        r6 = 0
                        r8 = 0
                        r7 = 1
                        r5 = 1
                        r3 = 1
                        java.lang.StringBuffer r12 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0252 }
                        r12.<init>()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r13 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0252 }
                        r13.<init>()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r14 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0252 }
                        r14.<init>()     // Catch:{ Exception -> 0x0252 }
                    L_0x03c0:
                        boolean r2 = r11.hasNext()     // Catch:{ Exception -> 0x0252 }
                        if (r2 == 0) goto L_0x04dd
                        java.lang.Object r2 = r11.next()     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r15 = r10.getString(r2)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r16 = ","
                        java.lang.String[] r16 = r15.split(r16)     // Catch:{ Exception -> 0x0252 }
                        r17 = 3
                        r16 = r16[r17]     // Catch:{ Exception -> 0x0252 }
                        java.lang.Double r16 = java.lang.Double.valueOf(r16)     // Catch:{ Exception -> 0x0252 }
                        if (r5 == 0) goto L_0x04b5
                        r5 = 0
                    L_0x03e1:
                        r13.append(r2)     // Catch:{ Exception -> 0x0252 }
                        int r6 = r6 + 1
                        double r16 = r16.doubleValue()     // Catch:{ Exception -> 0x0252 }
                        r18 = 0
                        int r16 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
                        if (r16 <= 0) goto L_0x04c7
                        if (r3 == 0) goto L_0x04be
                        r3 = 0
                    L_0x03f3:
                        r16 = 40
                        r0 = r16
                        java.lang.StringBuffer r16 = r14.append(r0)     // Catch:{ Exception -> 0x0252 }
                        r0 = r16
                        java.lang.StringBuffer r2 = r0.append(r2)     // Catch:{ Exception -> 0x0252 }
                        r16 = 44
                        r0 = r16
                        java.lang.StringBuffer r2 = r2.append(r0)     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r2 = r2.append(r15)     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0252 }
                        r15.<init>()     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r16 = ","
                        java.lang.StringBuilder r15 = r15.append(r16)     // Catch:{ Exception -> 0x0252 }
                        long r16 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0252 }
                        r18 = 1000(0x3e8, double:4.94E-321)
                        long r16 = r16 / r18
                        java.lang.StringBuilder r15 = r15.append(r16)     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r15 = r15.toString()     // Catch:{ Exception -> 0x0252 }
                        java.lang.StringBuffer r2 = r2.append(r15)     // Catch:{ Exception -> 0x0252 }
                        r15 = 41
                        r2.append(r15)     // Catch:{ Exception -> 0x0252 }
                        int r2 = r8 + 1
                        r8 = r9
                        r20 = r2
                        r2 = r3
                        r3 = r20
                    L_0x0439:
                        r9 = 100
                        if (r6 < r9) goto L_0x0462
                        r0 = r21
                        com.baidu.location.e.b$c r5 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r5 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r5 = r5.i     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = "DELETE FROM AP WHERE id IN (%s);"
                        r15 = 1
                        java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x0252 }
                        r16 = 0
                        java.lang.String r17 = r13.toString()     // Catch:{ Exception -> 0x0252 }
                        r15[r16] = r17     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = java.lang.String.format(r9, r15)     // Catch:{ Exception -> 0x0252 }
                        r5.execSQL(r9)     // Catch:{ Exception -> 0x0252 }
                        r5 = 1
                        r9 = 0
                        r13.setLength(r9)     // Catch:{ Exception -> 0x0252 }
                        int r6 = r6 + -100
                    L_0x0462:
                        r9 = 100
                        if (r3 < r9) goto L_0x048b
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = "INSERT OR REPLACE INTO AP (id,x,y,r,cl,timestamp) VALUES %s;"
                        r15 = 1
                        java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x0252 }
                        r16 = 0
                        java.lang.String r17 = r14.toString()     // Catch:{ Exception -> 0x0252 }
                        r15[r16] = r17     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r9 = java.lang.String.format(r9, r15)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r9)     // Catch:{ Exception -> 0x0252 }
                        r2 = 1
                        r9 = 0
                        r14.setLength(r9)     // Catch:{ Exception -> 0x0252 }
                        int r3 = r3 + -100
                    L_0x048b:
                        if (r8 <= 0) goto L_0x04b0
                        r0 = r21
                        com.baidu.location.e.b$c r9 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r9 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r9 = r9.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r15 = "DELETE FROM AP WHERE id IN (%s);"
                        r16 = 1
                        r0 = r16
                        java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x0252 }
                        r16 = r0
                        r17 = 0
                        java.lang.String r18 = r12.toString()     // Catch:{ Exception -> 0x0252 }
                        r16[r17] = r18     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r15 = java.lang.String.format(r15, r16)     // Catch:{ Exception -> 0x0252 }
                        r9.execSQL(r15)     // Catch:{ Exception -> 0x0252 }
                    L_0x04b0:
                        r9 = r8
                        r8 = r3
                        r3 = r2
                        goto L_0x03c0
                    L_0x04b5:
                        r17 = 44
                        r0 = r17
                        r13.append(r0)     // Catch:{ Exception -> 0x0252 }
                        goto L_0x03e1
                    L_0x04be:
                        r16 = 44
                        r0 = r16
                        r14.append(r0)     // Catch:{ Exception -> 0x0252 }
                        goto L_0x03f3
                    L_0x04c7:
                        if (r7 == 0) goto L_0x04d7
                        r7 = 0
                    L_0x04ca:
                        r12.append(r2)     // Catch:{ Exception -> 0x0252 }
                        int r2 = r9 + 1
                        r20 = r3
                        r3 = r8
                        r8 = r2
                        r2 = r20
                        goto L_0x0439
                    L_0x04d7:
                        r15 = 44
                        r12.append(r15)     // Catch:{ Exception -> 0x0252 }
                        goto L_0x04ca
                    L_0x04dd:
                        if (r6 <= 0) goto L_0x04fc
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM AP WHERE id IN (%s);"
                        r5 = 1
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = r13.toString()     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                    L_0x04fc:
                        if (r8 <= 0) goto L_0x051b
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "INSERT OR REPLACE INTO AP (id,x,y,r,cl,timestamp) VALUES %s;"
                        r5 = 1
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = r14.toString()     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                    L_0x051b:
                        if (r9 <= 0) goto L_0x053a
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM AP WHERE id IN (%s);"
                        r5 = 1
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = r12.toString()     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                    L_0x053a:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM %s WHERE id NOT IN (SELECT id FROM %s ORDER BY timestamp DESC, frequency DESC LIMIT %d);"
                        r5 = 3
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = "AP"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 1
                        java.lang.String r7 = "AP"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 2
                        r7 = 200000(0x30d40, float:2.8026E-40)
                        java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM %s WHERE id NOT IN (SELECT id FROM %s ORDER BY timestamp DESC, frequency DESC LIMIT %d);"
                        r5 = 3
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = "CL"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 1
                        java.lang.String r7 = "CL"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 2
                        r7 = 200000(0x30d40, float:2.8026E-40)
                        java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM %s WHERE id NOT IN (SELECT id FROM %s ORDER BY frequency DESC LIMIT %d);"
                        r5 = 3
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = "AP"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 1
                        java.lang.String r7 = "AP"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 2
                        r7 = 10000(0x2710, float:1.4013E-41)
                        java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = "DELETE FROM %s WHERE id NOT IN (SELECT id FROM %s ORDER BY frequency DESC LIMIT %d);"
                        r5 = 3
                        java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0252 }
                        r6 = 0
                        java.lang.String r7 = "CL"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 1
                        java.lang.String r7 = "CL"
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        r6 = 2
                        r7 = 10000(0x2710, float:1.4013E-41)
                        java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0252 }
                        r5[r6] = r7     // Catch:{ Exception -> 0x0252 }
                        java.lang.String r3 = java.lang.String.format(r3, r5)     // Catch:{ Exception -> 0x0252 }
                        r2.execSQL(r3)     // Catch:{ Exception -> 0x0252 }
                        if (r4 == 0) goto L_0x05f9
                        java.lang.String r2 = "ap"
                        boolean r2 = r4.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 != 0) goto L_0x05f9
                        java.lang.String r2 = "cell"
                        boolean r2 = r4.has(r2)     // Catch:{ Exception -> 0x0252 }
                        if (r2 != 0) goto L_0x05f9
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        r2.c()     // Catch:{ Exception -> 0x0252 }
                    L_0x05f9:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x0252 }
                        r2.setTransactionSuccessful()     // Catch:{ Exception -> 0x0252 }
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x0252 }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x0252 }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x0252 }
                        r2.setTransactionSuccessful()     // Catch:{ Exception -> 0x0252 }
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x067c }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x067c }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x067c }
                        if (r2 == 0) goto L_0x063c
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x067c }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x067c }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x067c }
                        boolean r2 = r2.isOpen()     // Catch:{ Exception -> 0x067c }
                        if (r2 == 0) goto L_0x063c
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x067c }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x067c }
                        android.database.sqlite.SQLiteDatabase r2 = r2.h     // Catch:{ Exception -> 0x067c }
                        r2.endTransaction()     // Catch:{ Exception -> 0x067c }
                    L_0x063c:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x067c }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x067c }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x067c }
                        if (r2 == 0) goto L_0x0665
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x067c }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x067c }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x067c }
                        boolean r2 = r2.isOpen()     // Catch:{ Exception -> 0x067c }
                        if (r2 == 0) goto L_0x0665
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this     // Catch:{ Exception -> 0x067c }
                        com.baidu.location.e.b r2 = com.baidu.location.e.b.this     // Catch:{ Exception -> 0x067c }
                        android.database.sqlite.SQLiteDatabase r2 = r2.i     // Catch:{ Exception -> 0x067c }
                        r2.endTransaction()     // Catch:{ Exception -> 0x067c }
                    L_0x0665:
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        r3 = 0
                        r2.j = r3
                        r0 = r21
                        com.baidu.location.e.b$c r2 = com.baidu.location.e.b.c.this
                        r3 = 0
                        boolean unused = r2.f = r3
                        goto L_0x0043
                    L_0x0676:
                        r3 = move-exception
                        goto L_0x0319
                    L_0x0679:
                        r2 = move-exception
                        goto L_0x02ac
                    L_0x067c:
                        r2 = move-exception
                        goto L_0x0665
                    L_0x067e:
                        r4 = move-exception
                        r20 = r4
                        r4 = r5
                        r5 = r20
                        goto L_0x0244
                    L_0x0686:
                        r5 = move-exception
                        goto L_0x0244
                    L_0x0689:
                        r4 = r5
                        goto L_0x0068
                    L_0x068c:
                        r3 = r4
                        r4 = r5
                        goto L_0x0076
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.c.AnonymousClass1.run():void");
                }
            }.start();
        }

        /* access modifiers changed from: package-private */
        public void b() {
            if (f() && !this.f) {
                b.this.g.g();
            }
        }
    }

    b(d dVar) {
        SQLiteDatabase sQLiteDatabase;
        SQLiteDatabase sQLiteDatabase2 = null;
        this.a = dVar;
        try {
            File file = new File(this.a.c(), "ofl_location.db");
            if (!file.exists()) {
                file.createNewFile();
            }
            sQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file, (SQLiteDatabase.CursorFactory) null);
        } catch (Exception e2) {
            sQLiteDatabase = null;
        }
        this.h = sQLiteDatabase;
        if (this.h != null) {
            try {
                this.h.execSQL("CREATE TABLE IF NOT EXISTS AP (id LONG PRIMARY KEY,x DOUBLE,y DOUBLE,r INTEGER,cl DOUBLE,timestamp INTEGER, frequency INTEGER DEFAULT 0);");
                this.h.execSQL("CREATE TABLE IF NOT EXISTS CL (id LONG PRIMARY KEY,x DOUBLE,y DOUBLE,r INTEGER,cl DOUBLE,timestamp INTEGER, frequency INTEGER DEFAULT 0);");
            } catch (Exception e3) {
            }
        }
        try {
            File file2 = new File(this.a.c(), "ofl_statistics.db");
            if (!file2.exists()) {
                file2.createNewFile();
            }
            sQLiteDatabase2 = SQLiteDatabase.openOrCreateDatabase(file2, (SQLiteDatabase.CursorFactory) null);
        } catch (Exception e4) {
        }
        this.i = sQLiteDatabase2;
        if (this.i != null) {
            try {
                this.i.execSQL("CREATE TABLE IF NOT EXISTS AP (id LONG PRIMARY KEY, originid VARCHAR(15), frequency INTEGER DEFAULT 0);");
                this.i.execSQL("CREATE TABLE IF NOT EXISTS CL (id LONG PRIMARY KEY, originid VARCHAR(40), frequency INTEGER DEFAULT 0);");
            } catch (Exception e5) {
            }
        }
    }

    private double a(double d2, double d3, double d4, double d5) {
        double radians = Math.toRadians(d2);
        Math.toRadians(d3);
        double radians2 = Math.toRadians(d4);
        Math.toRadians(d5);
        double radians3 = Math.toRadians(d5 - d3);
        double radians4 = Math.toRadians(d4 - d2);
        double sin = Math.sin(radians4 / 2.0d);
        double sin2 = (Math.sin(radians3 / 2.0d) * Math.cos(radians) * Math.cos(radians2) * Math.sin(radians3 / 2.0d)) + (Math.sin(radians4 / 2.0d) * sin);
        return Math.atan2(Math.sqrt(sin2), Math.sqrt(1.0d - sin2)) * 2.0d * 6378137.0d;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0091 A[LOOP:0: B:4:0x000a->B:25:0x0091, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0007 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int a(java.util.ArrayList<com.baidu.location.e.b.a> r19, double r20) {
        /*
            r18 = this;
            int r2 = r19.size()
            if (r2 != 0) goto L_0x0008
            r2 = 0
        L_0x0007:
            return r2
        L_0x0008:
            r2 = 0
            r12 = r2
        L_0x000a:
            r15 = 0
            int r2 = r19.size()
            r3 = 3
            if (r2 < r3) goto L_0x0094
            r6 = 0
            r4 = 0
            r2 = 0
            r3 = r2
        L_0x0018:
            int r2 = r19.size()
            if (r3 >= r2) goto L_0x0038
            r0 = r19
            java.lang.Object r2 = r0.get(r3)
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2
            double r8 = r2.a
            double r6 = r6 + r8
            r0 = r19
            java.lang.Object r2 = r0.get(r3)
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2
            double r8 = r2.b
            double r4 = r4 + r8
            int r2 = r3 + 1
            r3 = r2
            goto L_0x0018
        L_0x0038:
            int r2 = r19.size()
            double r2 = (double) r2
            double r6 = r6 / r2
            int r2 = r19.size()
            double r2 = (double) r2
            double r4 = r4 / r2
            r8 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            r3 = -1
            r2 = 0
            r13 = r2
            r14 = r3
            r16 = r8
        L_0x004c:
            int r2 = r19.size()
            if (r13 >= r2) goto L_0x0078
            r0 = r19
            java.lang.Object r2 = r0.get(r13)
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2
            double r8 = r2.b
            r0 = r19
            java.lang.Object r2 = r0.get(r13)
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2
            double r10 = r2.a
            r3 = r18
            double r8 = r3.a((double) r4, (double) r6, (double) r8, (double) r10)
            int r2 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x0097
            r3 = r13
        L_0x0071:
            int r2 = r13 + 1
            r13 = r2
            r14 = r3
            r16 = r8
            goto L_0x004c
        L_0x0078:
            int r2 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1))
            if (r2 <= 0) goto L_0x0094
            if (r14 < 0) goto L_0x0094
            int r2 = r19.size()
            if (r14 >= r2) goto L_0x0094
            int r12 = r12 + 1
            r0 = r19
            r0.remove(r14)
            r2 = 1
            r3 = r2
            r2 = r12
        L_0x008e:
            r4 = 1
            if (r3 != r4) goto L_0x0007
            r12 = r2
            goto L_0x000a
        L_0x0094:
            r3 = r15
            r2 = r12
            goto L_0x008e
        L_0x0097:
            r3 = r14
            r8 = r16
            goto L_0x0071
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.a(java.util.ArrayList, double):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00f7, code lost:
        if (r2 != null) goto L_0x00f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0101, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0102, code lost:
        r18 = r3;
        r3 = r2;
        r2 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00f6 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:8:0x0070] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0109 A[SYNTHETIC, Splitter:B:38:0x0109] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.baidu.location.BDLocation a(java.lang.Long r20) {
        /*
            r19 = this;
            r2 = 0
            r0 = r19
            r0.p = r2
            r8 = 0
            r6 = 0
            r4 = 0
            r3 = 0
            r0 = r19
            java.lang.Long r2 = r0.e
            if (r2 == 0) goto L_0x0046
            r0 = r19
            java.lang.Long r2 = r0.e
            r0 = r20
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0046
            r3 = 1
            r0 = r19
            double r6 = r0.c
            r0 = r19
            double r4 = r0.d
            r0 = r19
            int r8 = r0.b
        L_0x002a:
            if (r3 == 0) goto L_0x010d
            com.baidu.location.BDLocation r2 = new com.baidu.location.BDLocation
            r2.<init>()
            float r3 = (float) r8
            r2.setRadius(r3)
            r2.setLatitude(r4)
            r2.setLongitude(r6)
            java.lang.String r3 = "cl"
            r2.setNetworkLocationType(r3)
            r3 = 66
            r2.setLocType(r3)
        L_0x0045:
            return r2
        L_0x0046:
            r2 = 0
            java.util.Locale r9 = java.util.Locale.US
            java.lang.String r10 = "SELECT * FROM CL WHERE id = %d AND timestamp + %d > %d;"
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]
            r12 = 0
            r11[r12] = r20
            r12 = 1
            r13 = 15552000(0xed4e00, float:2.1792994E-38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r11[r12] = r13
            r12 = 2
            long r14 = java.lang.System.currentTimeMillis()
            r16 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r16
            java.lang.Long r13 = java.lang.Long.valueOf(r14)
            r11[r12] = r13
            java.lang.String r9 = java.lang.String.format(r9, r10, r11)
            r0 = r19
            android.database.sqlite.SQLiteDatabase r10 = r0.h     // Catch:{ Exception -> 0x00f6, all -> 0x0101 }
            r11 = 0
            android.database.Cursor r2 = r10.rawQuery(r9, r11)     // Catch:{ Exception -> 0x00f6, all -> 0x0101 }
            if (r2 == 0) goto L_0x00e5
            boolean r9 = r2.moveToFirst()     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            if (r9 == 0) goto L_0x00e5
            java.lang.String r9 = "cl"
            int r9 = r2.getColumnIndex(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            double r10 = r2.getDouble(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            r12 = 0
            int r9 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r9 <= 0) goto L_0x00e5
            r3 = 1
            java.lang.String r9 = "x"
            int r9 = r2.getColumnIndex(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            double r6 = r2.getDouble(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            java.lang.String r9 = "y"
            int r9 = r2.getColumnIndex(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            double r4 = r2.getDouble(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            java.lang.String r9 = "r"
            int r9 = r2.getColumnIndex(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            int r8 = r2.getInt(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            java.lang.String r9 = "timestamp"
            int r9 = r2.getColumnIndex(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            int r9 = r2.getInt(r9)     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            r10 = 604800(0x93a80, float:8.47505E-40)
            int r9 = r9 + r10
            long r10 = (long) r9     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            r14 = 1000(0x3e8, double:4.94E-321)
            long r12 = r12 / r14
            int r9 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r9 >= 0) goto L_0x00cd
            r9 = 1
            r0 = r19
            r0.p = r9     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
        L_0x00cd:
            r9 = 300(0x12c, float:4.2E-43)
            if (r8 >= r9) goto L_0x00ef
            r8 = 300(0x12c, float:4.2E-43)
        L_0x00d3:
            r0 = r19
            r0.c = r6     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            r0 = r19
            r0.d = r4     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            r0 = r19
            r0.b = r8     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
            r0 = r20
            r1 = r19
            r1.e = r0     // Catch:{ Exception -> 0x00f6, all -> 0x0112 }
        L_0x00e5:
            if (r2 == 0) goto L_0x002a
            r2.close()     // Catch:{ Exception -> 0x00ec }
            goto L_0x002a
        L_0x00ec:
            r2 = move-exception
            goto L_0x002a
        L_0x00ef:
            r9 = 2000(0x7d0, float:2.803E-42)
            if (r9 >= r8) goto L_0x00d3
            r8 = 2000(0x7d0, float:2.803E-42)
            goto L_0x00d3
        L_0x00f6:
            r9 = move-exception
            if (r2 == 0) goto L_0x002a
            r2.close()     // Catch:{ Exception -> 0x00fe }
            goto L_0x002a
        L_0x00fe:
            r2 = move-exception
            goto L_0x002a
        L_0x0101:
            r3 = move-exception
            r18 = r3
            r3 = r2
            r2 = r18
        L_0x0107:
            if (r3 == 0) goto L_0x010c
            r3.close()     // Catch:{ Exception -> 0x0110 }
        L_0x010c:
            throw r2
        L_0x010d:
            r2 = 0
            goto L_0x0045
        L_0x0110:
            r3 = move-exception
            goto L_0x010c
        L_0x0112:
            r3 = move-exception
            r18 = r3
            r3 = r2
            r2 = r18
            goto L_0x0107
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.a(java.lang.Long):com.baidu.location.BDLocation");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:126:0x0357, code lost:
        r2 = r22;
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x01ae, code lost:
        r2 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        r22.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0327 A[SYNTHETIC, Splitter:B:112:0x0327] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x033c A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x017d  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01ae A[ExcHandler: all (th java.lang.Throwable), Splitter:B:50:0x01a9] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01b1 A[SYNTHETIC, Splitter:B:54:0x01b1] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.baidu.location.BDLocation a(java.util.LinkedHashMap<java.lang.String, java.lang.Integer> r34, com.baidu.location.BDLocation r35, int r36) {
        /*
            r33 = this;
            r0 = r33
            java.lang.StringBuffer r2 = r0.o
            r3 = 0
            r2.setLength(r3)
            r6 = 0
            r4 = 0
            r2 = 0
            if (r35 == 0) goto L_0x0382
            r2 = 1
            double r4 = r35.getLatitude()
            double r6 = r35.getLongitude()
            r21 = r2
        L_0x001a:
            r28 = 0
            r26 = 0
            r24 = 0
            r23 = 0
            java.lang.StringBuffer r10 = new java.lang.StringBuffer
            r10.<init>()
            r3 = 1
            java.util.Set r2 = r34.entrySet()
            java.util.Iterator r11 = r2.iterator()
            r2 = 0
            r8 = r2
            r9 = r3
        L_0x0033:
            int r2 = r34.size()
            r3 = 30
            int r2 = java.lang.Math.min(r2, r3)
            if (r8 >= r2) goto L_0x0088
            java.lang.Object r2 = r11.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r3 = r2.getKey()
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Object r2 = r2.getValue()
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r12 = r2.intValue()
            if (r12 >= 0) goto L_0x0060
            int r2 = r2.intValue()
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
        L_0x0060:
            java.lang.Long r12 = com.baidu.location.Jni.encode3(r3)
            if (r12 != 0) goto L_0x006c
            r3 = r9
        L_0x0067:
            int r2 = r8 + 1
            r8 = r2
            r9 = r3
            goto L_0x0033
        L_0x006c:
            r0 = r33
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.String> r13 = r0.n
            r13.put(r12, r3)
            if (r9 == 0) goto L_0x0082
            r9 = 0
        L_0x0076:
            r0 = r33
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r0.m
            r3.put(r12, r2)
            r10.append(r12)
            r3 = r9
            goto L_0x0067
        L_0x0082:
            r3 = 44
            r10.append(r3)
            goto L_0x0076
        L_0x0088:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.String r3 = "SELECT * FROM AP WHERE id IN (%s) AND timestamp+%d>%d;"
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            r8[r9] = r10
            r9 = 1
            r10 = 7776000(0x76a700, float:1.0896497E-38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r9] = r10
            r9 = 2
            long r10 = java.lang.System.currentTimeMillis()
            r12 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r12
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            r8[r9] = r10
            java.lang.String r3 = java.lang.String.format(r2, r3, r8)
            r2 = 0
            r0 = r33
            android.database.sqlite.SQLiteDatabase r8 = r0.h     // Catch:{ Exception -> 0x034b, all -> 0x0345 }
            r9 = 0
            android.database.Cursor r22 = r8.rawQuery(r3, r9)     // Catch:{ Exception -> 0x034b, all -> 0x0345 }
            if (r22 == 0) goto L_0x0379
            boolean r2 = r22.moveToFirst()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            if (r2 == 0) goto L_0x0379
            java.util.ArrayList r29 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r29.<init>()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
        L_0x00c5:
            boolean r2 = r22.isAfterLast()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            if (r2 != 0) goto L_0x022e
            r2 = 0
            r0 = r22
            long r2 = r0.getLong(r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 1
            r0 = r22
            double r10 = r0.getDouble(r3)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 2
            r0 = r22
            double r8 = r0.getDouble(r3)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 3
            r0 = r22
            int r12 = r0.getInt(r3)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 4
            r0 = r22
            double r14 = r0.getDouble(r3)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 5
            r0 = r22
            int r3 = r0.getInt(r3)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r0 = r33
            java.util.HashSet<java.lang.Long> r13 = r0.l     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r13.add(r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r13 = 604800(0x93a80, float:8.47505E-40)
            int r3 = r3 + r13
            long r0 = (long) r3     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r16 = r0
            long r18 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r30 = 1000(0x3e8, double:4.94E-321)
            long r18 = r18 / r30
            int r3 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r3 >= 0) goto L_0x0160
            r0 = r33
            java.lang.StringBuffer r3 = r0.o     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            int r3 = r3.length()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            if (r3 <= 0) goto L_0x0126
            r0 = r33
            java.lang.StringBuffer r3 = r0.o     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            java.lang.String r13 = ","
            r3.append(r13)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
        L_0x0126:
            r0 = r33
            java.lang.StringBuffer r3 = r0.o     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            java.util.Locale r13 = java.util.Locale.US     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            java.lang.String r16 = "(%d,\"%s\",%d)"
            r17 = 3
            r0 = r17
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r17 = r0
            r18 = 0
            r17[r18] = r2     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r18 = 1
            r0 = r33
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.String> r0 = r0.n     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r19 = r0
            r0 = r19
            java.lang.Object r19 = r0.get(r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r17[r18] = r19     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r18 = 2
            r19 = 100000(0x186a0, float:1.4013E-40)
            java.lang.Integer r19 = java.lang.Integer.valueOf(r19)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r17[r18] = r19     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r0 = r16
            r1 = r17
            java.lang.String r13 = java.lang.String.format(r13, r0, r1)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3.append(r13)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
        L_0x0160:
            r16 = 0
            int r3 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r3 > 0) goto L_0x0197
            r22.moveToNext()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            goto L_0x00c5
        L_0x016b:
            r2 = move-exception
            r2 = r22
            r3 = r23
            r12 = r24
            r10 = r26
            r4 = r28
        L_0x0176:
            if (r2 == 0) goto L_0x017b
            r2.close()     // Catch:{ Exception -> 0x033f }
        L_0x017b:
            if (r3 == 0) goto L_0x033c
            com.baidu.location.BDLocation r2 = new com.baidu.location.BDLocation
            r2.<init>()
            float r3 = (float) r4
            r2.setRadius(r3)
            r2.setLatitude(r12)
            r2.setLongitude(r10)
            java.lang.String r3 = "wf"
            r2.setNetworkLocationType(r3)
            r3 = 66
            r2.setLocType(r3)
        L_0x0196:
            return r2
        L_0x0197:
            r14 = 0
            int r3 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x01a9
            r14 = 0
            int r3 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x01a9
            if (r12 <= 0) goto L_0x01a9
            r3 = 1000(0x3e8, float:1.401E-42)
            if (r12 < r3) goto L_0x01b5
        L_0x01a9:
            r22.moveToNext()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            goto L_0x00c5
        L_0x01ae:
            r2 = move-exception
        L_0x01af:
            if (r22 == 0) goto L_0x01b4
            r22.close()     // Catch:{ Exception -> 0x0342 }
        L_0x01b4:
            throw r2
        L_0x01b5:
            r3 = 1
            r0 = r21
            if (r0 != r3) goto L_0x01ce
            r3 = r33
            double r14 = r3.a((double) r4, (double) r6, (double) r8, (double) r10)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r16 = 4666723172467343360(0x40c3880000000000, double:10000.0)
            int r3 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r3 <= 0) goto L_0x01ce
            r22.moveToNext()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            goto L_0x00c5
        L_0x01ce:
            r0 = r33
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r0.m     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            java.lang.Object r2 = r3.get(r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 30
            int r2 = java.lang.Math.max(r3, r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r3 = 100
            int r2 = java.lang.Math.min(r3, r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r14 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r3 = 70
            if (r2 <= r3) goto L_0x0225
            int r2 = r2 + -70
            double r2 = (double) r2     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r16 = 4629137466983448576(0x403e000000000000, double:30.0)
            double r2 = r2 / r16
            double r2 = r2 + r14
        L_0x01f6:
            r14 = 4632233691727265792(0x4049000000000000, double:50.0)
            double r12 = (double) r12     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            double r12 = java.lang.Math.max(r14, r12)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r14 = 4603579539098121011(0x3fe3333333333333, double:0.6)
            double r12 = java.lang.Math.pow(r12, r14)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r14 = -4634023872579145564(0xbfb0a3d70a3d70a4, double:-0.065)
            double r12 = r12 * r14
            double r2 = r2 * r12
            double r18 = java.lang.Math.exp(r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            com.baidu.location.e.b$a r13 = new com.baidu.location.e.b$a     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r20 = 0
            r14 = r10
            r16 = r8
            r13.<init>(r14, r16, r18)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r0 = r29
            r0.add(r13)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r22.moveToNext()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            goto L_0x00c5
        L_0x0225:
            int r2 = r2 + -70
            double r2 = (double) r2     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r16 = 4632233691727265792(0x4049000000000000, double:50.0)
            double r2 = r2 / r16
            double r2 = r2 + r14
            goto L_0x01f6
        L_0x022e:
            r2 = 4652007308841189376(0x408f400000000000, double:1000.0)
            r0 = r33
            r1 = r29
            r0.a((java.util.ArrayList<com.baidu.location.e.b.a>) r1, (double) r2)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r10 = 0
            r12 = 0
            r8 = 0
            r2 = 0
            r16 = r2
        L_0x0243:
            int r2 = r29.size()     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r0 = r16
            if (r0 >= r2) goto L_0x027e
            r0 = r29
            r1 = r16
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            double r14 = r2.c     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r18 = 0
            int r3 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
            if (r3 > 0) goto L_0x0266
            r2 = r8
            r8 = r12
        L_0x025f:
            int r12 = r16 + 1
            r16 = r12
            r12 = r8
            r8 = r2
            goto L_0x0243
        L_0x0266:
            double r14 = r2.a     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            double r0 = r2.c     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r18 = r0
            double r14 = r14 * r18
            double r14 = r14 + r10
            double r10 = r2.b     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            double r0 = r2.c     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            r18 = r0
            double r10 = r10 * r18
            double r10 = r10 + r12
            double r2 = r2.c     // Catch:{ Exception -> 0x016b, all -> 0x01ae }
            double r2 = r2 + r8
            r8 = r10
            r10 = r14
            goto L_0x025f
        L_0x027e:
            r2 = 0
            int r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x036f
            r2 = 0
            int r2 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x036f
            r2 = 0
            int r2 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x036f
            double r10 = r10 / r8
            double r12 = r12 / r8
            r3 = 1
            r8 = 0
            r2 = 0
            r32 = r2
            r2 = r8
            r8 = r32
        L_0x029a:
            int r9 = r29.size()     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            if (r8 >= r9) goto L_0x02c7
            double r0 = (double) r2     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            r18 = r0
            r0 = r29
            java.lang.Object r2 = r0.get(r8)     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            double r14 = r2.a     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            r0 = r29
            java.lang.Object r2 = r0.get(r8)     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            com.baidu.location.e.b$a r2 = (com.baidu.location.e.b.a) r2     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            double r0 = r2.b     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            r16 = r0
            r9 = r33
            double r14 = r9.a((double) r10, (double) r12, (double) r14, (double) r16)     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            double r14 = r14 + r18
            float r9 = (float) r14     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            int r2 = r8 + 1
            r8 = r2
            r2 = r9
            goto L_0x029a
        L_0x02c7:
            int r8 = r29.size()     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            float r8 = (float) r8     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            float r2 = r2 / r8
            int r28 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x0356, all -> 0x01ae }
            r2 = 30
            r0 = r28
            if (r0 >= r2) goto L_0x032f
            r28 = 30
            r2 = r3
            r8 = r12
            r12 = r28
        L_0x02dd:
            if (r21 != 0) goto L_0x02e7
            int r3 = r29.size()     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            r13 = 1
            if (r3 > r13) goto L_0x02e7
            r2 = 0
        L_0x02e7:
            int r3 = r29.size()     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            r0 = r36
            if (r3 >= r0) goto L_0x030a
            r14 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r3 = r29.size()     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            double r0 = (double) r3     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            r16 = r0
            double r14 = r14 * r16
            int r3 = r34.size()     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            double r0 = (double) r3     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            r16 = r0
            double r14 = r14 / r16
            r16 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            int r3 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r3 >= 0) goto L_0x030a
            r2 = 0
        L_0x030a:
            r3 = 1
            r0 = r21
            if (r0 != r3) goto L_0x0365
            r3 = 1
            if (r2 != r3) goto L_0x0365
            r3 = r33
            double r4 = r3.a((double) r4, (double) r6, (double) r8, (double) r10)     // Catch:{ Exception -> 0x035d, all -> 0x01ae }
            r6 = 4666723172467343360(0x40c3880000000000, double:10000.0)
            int r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0365
            r2 = 0
            r3 = r2
            r4 = r12
            r12 = r8
        L_0x0325:
            if (r22 == 0) goto L_0x017b
            r22.close()     // Catch:{ Exception -> 0x032c }
            goto L_0x017b
        L_0x032c:
            r2 = move-exception
            goto L_0x017b
        L_0x032f:
            r2 = 100
            r0 = r28
            if (r2 >= r0) goto L_0x0369
            r28 = 100
            r2 = r3
            r8 = r12
            r12 = r28
            goto L_0x02dd
        L_0x033c:
            r2 = 0
            goto L_0x0196
        L_0x033f:
            r2 = move-exception
            goto L_0x017b
        L_0x0342:
            r3 = move-exception
            goto L_0x01b4
        L_0x0345:
            r3 = move-exception
            r22 = r2
            r2 = r3
            goto L_0x01af
        L_0x034b:
            r3 = move-exception
            r3 = r23
            r12 = r24
            r10 = r26
            r4 = r28
            goto L_0x0176
        L_0x0356:
            r2 = move-exception
            r2 = r22
            r4 = r28
            goto L_0x0176
        L_0x035d:
            r3 = move-exception
            r3 = r2
            r4 = r12
            r12 = r8
            r2 = r22
            goto L_0x0176
        L_0x0365:
            r3 = r2
            r4 = r12
            r12 = r8
            goto L_0x0325
        L_0x0369:
            r2 = r3
            r8 = r12
            r12 = r28
            goto L_0x02dd
        L_0x036f:
            r2 = r23
            r8 = r24
            r10 = r26
            r12 = r28
            goto L_0x02dd
        L_0x0379:
            r3 = r23
            r12 = r24
            r10 = r26
            r4 = r28
            goto L_0x0325
        L_0x0382:
            r21 = r2
            goto L_0x001a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.a(java.util.LinkedHashMap, com.baidu.location.BDLocation, int):com.baidu.location.BDLocation");
    }

    /* access modifiers changed from: private */
    public void a(BDLocation bDLocation, BDLocation bDLocation2, BDLocation bDLocation3, String str, Long l2) {
        if (bDLocation != null && bDLocation.getLocType() == 161) {
            if (!(bDLocation2 == null || bDLocation.getNetworkLocationType() == null || !bDLocation.getNetworkLocationType().equals("cl"))) {
                if (a(bDLocation2.getLatitude(), bDLocation2.getLongitude(), bDLocation.getLatitude(), bDLocation.getLongitude()) > 300.0d) {
                    String format = String.format(Locale.US, "UPDATE CL SET cl = 0 WHERE id = %d;", new Object[]{l2});
                    String format2 = String.format(Locale.US, "INSERT OR REPLACE INTO CL VALUES (%d,\"%s\",%d);", new Object[]{l2, str, 100000});
                    try {
                        this.h.execSQL(format);
                        this.i.execSQL(format2);
                    } catch (Exception e2) {
                    }
                }
            }
            if (bDLocation3 != null && bDLocation.getNetworkLocationType() != null && bDLocation.getNetworkLocationType().equals("wf")) {
                if (a(bDLocation3.getLatitude(), bDLocation3.getLongitude(), bDLocation.getLatitude(), bDLocation.getLongitude()) > 100.0d) {
                    try {
                        String format3 = String.format("UPDATE AP SET cl = 0 WHERE id In (%s);", new Object[]{this.j.toString()});
                        String format4 = String.format("INSERT OR REPLACE INTO AP VALUES %s;", new Object[]{this.k.toString()});
                        this.h.execSQL(format3);
                        this.i.execSQL(format4);
                    } catch (Exception e3) {
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(String str, Long l2, BDLocation bDLocation) {
        if (str != null) {
            if (bDLocation != null) {
                try {
                    this.h.execSQL(String.format(Locale.US, "UPDATE CL SET frequency=frequency+1 WHERE id = %d;", new Object[]{l2}));
                } catch (Exception e2) {
                }
            } else {
                String format = String.format(Locale.US, "INSERT OR IGNORE INTO CL VALUES (%d,\"%s\",0);", new Object[]{l2, str});
                String format2 = String.format(Locale.US, "UPDATE CL SET frequency=frequency+1 WHERE id = %d;", new Object[]{l2});
                try {
                    this.i.execSQL(format);
                    this.i.execSQL(format2);
                } catch (Exception e3) {
                }
            }
            if (this.p) {
                try {
                    this.i.execSQL(String.format(Locale.US, "INSERT OR IGNORE INTO CL VALUES (%d,\"%s\",%d);", new Object[]{l2, str, 100000}));
                } catch (Exception e4) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(String str, String str2, String str3) {
        this.f.a(str, str2, str3);
    }

    /* access modifiers changed from: private */
    public void a(LinkedHashMap<String, Integer> linkedHashMap) {
        boolean z;
        boolean z2;
        if (linkedHashMap != null && linkedHashMap.size() > 0) {
            this.j = new StringBuffer();
            this.k = new StringBuffer();
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            if (!(this.m == null || this.m.keySet() == null)) {
                boolean z3 = true;
                boolean z4 = true;
                for (Long next : this.m.keySet()) {
                    try {
                        if (this.l.contains(next)) {
                            if (z4) {
                                z4 = false;
                            } else {
                                this.j.append(',');
                                this.k.append(',');
                            }
                            this.j.append(next);
                            this.k.append('(').append(next).append(',').append(TokenParser.DQUOTE).append(this.n.get(next)).append(TokenParser.DQUOTE).append(',').append(100000).append(')');
                            z = z3;
                            z2 = z4;
                        } else {
                            String str = this.n.get(next);
                            if (z3) {
                                z3 = false;
                            } else {
                                stringBuffer.append(',');
                                stringBuffer2.append(',');
                            }
                            stringBuffer.append(next);
                            stringBuffer2.append('(').append(next).append(',').append(TokenParser.DQUOTE).append(str).append(TokenParser.DQUOTE).append(",0)");
                            z = z3;
                            z2 = z4;
                        }
                        z3 = z;
                        z4 = z2;
                    } catch (Exception e2) {
                        z3 = z3;
                        z4 = z4;
                    }
                }
            }
            try {
                this.h.execSQL(String.format(Locale.US, "UPDATE AP SET frequency=frequency+1 WHERE id IN(%s)", new Object[]{this.j.toString()}));
            } catch (Exception e3) {
            }
            if (this.o != null && this.o.length() > 0) {
                if (stringBuffer2.length() > 0) {
                    stringBuffer2.append(",");
                }
                stringBuffer2.append(this.o);
            }
            try {
                String format = String.format("INSERT OR IGNORE INTO AP VALUES %s;", new Object[]{stringBuffer2.toString()});
                String format2 = String.format("UPDATE AP SET frequency=frequency+1 WHERE id in (%s);", new Object[]{stringBuffer.toString()});
                if (stringBuffer2.length() > 0) {
                    this.i.execSQL(format);
                }
                if (stringBuffer.length() > 0) {
                    this.i.execSQL(format2);
                }
            } catch (Exception e4) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(String[] strArr) {
        this.a.l().a(strArr);
    }

    /* access modifiers changed from: package-private */
    public Cursor a(e.a aVar) {
        BDLocation bDLocation;
        Double d2;
        List<Poi> list;
        int i2;
        BDLocation bDLocation2 = new BDLocation();
        bDLocation2.setLocType(67);
        int i3 = 0;
        if (aVar.c) {
            String str = aVar.b;
            LinkedHashMap<String, Integer> linkedHashMap = aVar.i;
            int i4 = aVar.f;
            BDLocation bDLocation3 = aVar.g;
            BDLocation bDLocation4 = null;
            long j2 = Long.MIN_VALUE;
            if (!(str == null || this.h == null || (j2 = Jni.encode3(str)) == null)) {
                bDLocation4 = a(j2);
            }
            BDLocation bDLocation5 = null;
            if (!(linkedHashMap == null || linkedHashMap.size() <= 0 || this.h == null)) {
                this.m.clear();
                this.l.clear();
                this.n.clear();
                bDLocation5 = a(linkedHashMap, bDLocation4, i4);
            }
            Double d3 = null;
            Double d4 = null;
            Double d5 = null;
            Double d6 = null;
            if (bDLocation4 != null) {
                d3 = Double.valueOf(bDLocation4.getLongitude());
                d4 = Double.valueOf(bDLocation4.getLatitude());
                double[] coorEncrypt = Jni.coorEncrypt(bDLocation4.getLongitude(), bDLocation4.getLatitude(), BDLocation.BDLOCATION_BD09LL_TO_GCJ02);
                bDLocation4.setCoorType("gcj");
                bDLocation4.setLatitude(coorEncrypt[1]);
                bDLocation4.setLongitude(coorEncrypt[0]);
                bDLocation4.setNetworkLocationType("cl");
            }
            if (bDLocation5 != null) {
                d5 = Double.valueOf(bDLocation5.getLongitude());
                d6 = Double.valueOf(bDLocation5.getLatitude());
                double[] coorEncrypt2 = Jni.coorEncrypt(bDLocation5.getLongitude(), bDLocation5.getLatitude(), BDLocation.BDLOCATION_BD09LL_TO_GCJ02);
                bDLocation5.setCoorType("gcj");
                bDLocation5.setLatitude(coorEncrypt2[1]);
                bDLocation5.setLongitude(coorEncrypt2[0]);
                bDLocation5.setNetworkLocationType("wf");
            }
            if (bDLocation4 != null && bDLocation5 == null) {
                i3 = 1;
            } else if (bDLocation4 == null && bDLocation5 != null) {
                i3 = 2;
            } else if (!(bDLocation4 == null || bDLocation5 == null)) {
                i3 = 4;
            }
            boolean z = aVar.f > 0;
            boolean z2 = linkedHashMap == null || linkedHashMap.size() <= 0;
            if (z) {
                if (bDLocation5 != null) {
                    d2 = d5;
                    bDLocation = bDLocation5;
                } else {
                    if (z2 && bDLocation4 != null) {
                        d6 = d4;
                        bDLocation = bDLocation4;
                        d2 = d3;
                    }
                    d6 = null;
                    d2 = null;
                    bDLocation = bDLocation2;
                }
            } else if (bDLocation5 != null) {
                d2 = d5;
                bDLocation = bDLocation5;
            } else {
                if (bDLocation4 != null) {
                    d6 = d4;
                    bDLocation = bDLocation4;
                    d2 = d3;
                }
                d6 = null;
                d2 = null;
                bDLocation = bDLocation2;
            }
            if (aVar.e && this.a.l().l() && d6 != null && d2 != null) {
                bDLocation.setAddr(this.a.k().a(d2.doubleValue(), d6.doubleValue()));
            }
            if (z && aVar.e && bDLocation.getAddrStr() == null) {
                d6 = null;
                d2 = null;
                i3 = 0;
                bDLocation = bDLocation2;
            }
            if ((!aVar.d && !aVar.h) || d6 == null || d2 == null) {
                list = null;
            } else {
                List<Poi> b2 = this.a.k().b(d2.doubleValue(), d6.doubleValue());
                if (aVar.d) {
                    bDLocation.setPoiList(b2);
                }
                list = b2;
            }
            if (!z || !aVar.d || (list != null && list.size() > 0)) {
                i2 = i3;
            } else {
                i2 = 0;
                bDLocation = bDLocation2;
            }
            String str2 = null;
            if (aVar.h && list != null && list.size() > 0) {
                str2 = String.format(Locale.CHINA, "在%s附近", new Object[]{list.get(0).getName()});
                bDLocation.setLocationDescribe(str2);
            }
            if (z && aVar.h && str2 == null) {
                i2 = 0;
                bDLocation = bDLocation2;
            }
            StringBuffer stringBuffer = new StringBuffer();
            String str3 = null;
            if (aVar.a != null) {
                stringBuffer.append(aVar.a);
                stringBuffer.append(e.a(bDLocation5, bDLocation4, aVar));
                stringBuffer.append(e.a(bDLocation, i2));
                str3 = stringBuffer.toString();
            }
            new C0005b(str, j2, bDLocation4, bDLocation5, bDLocation3, str3, linkedHashMap) {
            }.start();
        } else {
            bDLocation = bDLocation2;
        }
        return e.a(bDLocation);
    }

    /* access modifiers changed from: package-private */
    public SQLiteDatabase a() {
        return this.i;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        this.g.b();
    }
}
