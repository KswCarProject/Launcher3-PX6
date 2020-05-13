package com.baidu.location.e;

import android.database.sqlite.SQLiteDatabase;
import com.baidu.location.Jni;
import java.util.HashMap;
import java.util.Locale;
import org.apache.http.protocol.HttpRequestExecutor;
import org.json.JSONObject;

final class f {
    private static final String d = String.format(Locale.US, "DELETE FROM LOG WHERE timestamp NOT IN (SELECT timestamp FROM LOG ORDER BY timestamp DESC LIMIT %d);", new Object[]{Integer.valueOf(HttpRequestExecutor.DEFAULT_WAIT_FOR_CONTINUE)});
    private static final String e = String.format(Locale.US, "SELECT * FROM LOG ORDER BY timestamp DESC LIMIT %d;", new Object[]{3});
    private String a = null;
    private final SQLiteDatabase b;
    private final a c;

    private class a extends com.baidu.location.h.f {
        private int b;
        private long c;
        private String d = null;
        private boolean e = false;
        /* access modifiers changed from: private */
        public boolean f = false;
        /* access modifiers changed from: private */
        public f p;

        a(f fVar) {
            this.p = fVar;
            this.k = new HashMap();
            this.b = 0;
            this.c = -1;
        }

        /* access modifiers changed from: private */
        public void b() {
            if (!this.e) {
                this.d = this.p.b();
                if (this.c != -1 && this.c + 86400000 <= System.currentTimeMillis()) {
                    this.b = 0;
                    this.c = -1;
                }
                if (this.d != null && this.b < 2) {
                    this.e = true;
                    e();
                }
            }
        }

        public void a() {
            this.k.clear();
            this.k.put("qt", "ofbh");
            this.k.put("req", this.d);
            this.h = d.a;
        }

        public void a(boolean z) {
            this.f = false;
            if (z && this.j != null) {
                try {
                    JSONObject jSONObject = new JSONObject(this.j);
                    if (jSONObject != null && jSONObject.has("error") && jSONObject.getInt("error") == 161) {
                        this.f = true;
                    }
                } catch (Exception e2) {
                }
            }
            this.e = false;
            if (!this.f) {
                this.b++;
                this.c = System.currentTimeMillis();
            }
            new Thread() {
                public void run() {
                    super.run();
                    a.this.p.a(a.this.f);
                }
            }.start();
        }
    }

    f(SQLiteDatabase sQLiteDatabase) {
        this.b = sQLiteDatabase;
        this.c = new a(this);
        if (this.b != null && this.b.isOpen()) {
            try {
                this.b.execSQL("CREATE TABLE IF NOT EXISTS LOG(timestamp LONG PRIMARY KEY, log VARCHAR(4000))");
            } catch (Exception e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        if (z && this.a != null) {
            String format = String.format("DELETE FROM LOG WHERE timestamp in (%s);", new Object[]{this.a});
            try {
                if (this.a.length() > 0) {
                    this.b.execSQL(format);
                }
            } catch (Exception e2) {
            }
        }
        this.a = null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x006d A[SYNTHETIC, Splitter:B:31:0x006d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String b() {
        /*
            r9 = this;
            r0 = 0
            org.json.JSONArray r2 = new org.json.JSONArray
            r2.<init>()
            org.json.JSONObject r3 = new org.json.JSONObject
            r3.<init>()
            android.database.sqlite.SQLiteDatabase r1 = r9.b     // Catch:{ Exception -> 0x0077, all -> 0x0067 }
            java.lang.String r4 = e     // Catch:{ Exception -> 0x0077, all -> 0x0067 }
            r5 = 0
            android.database.Cursor r1 = r1.rawQuery(r4, r5)     // Catch:{ Exception -> 0x0077, all -> 0x0067 }
            if (r1 == 0) goto L_0x005f
            int r4 = r1.getCount()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            if (r4 <= 0) goto L_0x005f
            java.lang.StringBuffer r4 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            r4.<init>()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            r1.moveToFirst()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
        L_0x0024:
            boolean r5 = r1.isAfterLast()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            if (r5 != 0) goto L_0x0050
            r5 = 1
            java.lang.String r5 = r1.getString(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            r2.put(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            int r5 = r4.length()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            if (r5 == 0) goto L_0x003d
            java.lang.String r5 = ","
            r4.append(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
        L_0x003d:
            r5 = 0
            long r6 = r1.getLong(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            r4.append(r6)     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            r1.moveToNext()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            goto L_0x0024
        L_0x0049:
            r2 = move-exception
        L_0x004a:
            if (r1 == 0) goto L_0x004f
            r1.close()     // Catch:{ Exception -> 0x0071 }
        L_0x004f:
            return r0
        L_0x0050:
            java.lang.String r5 = "ofloc"
            r3.put(r5, r2)     // Catch:{ JSONException -> 0x007a }
            java.lang.String r0 = r3.toString()     // Catch:{ JSONException -> 0x007a }
        L_0x0059:
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
            r9.a = r2     // Catch:{ Exception -> 0x0049, all -> 0x0075 }
        L_0x005f:
            if (r1 == 0) goto L_0x004f
            r1.close()     // Catch:{ Exception -> 0x0065 }
            goto L_0x004f
        L_0x0065:
            r1 = move-exception
            goto L_0x004f
        L_0x0067:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
        L_0x006b:
            if (r1 == 0) goto L_0x0070
            r1.close()     // Catch:{ Exception -> 0x0073 }
        L_0x0070:
            throw r0
        L_0x0071:
            r1 = move-exception
            goto L_0x004f
        L_0x0073:
            r1 = move-exception
            goto L_0x0070
        L_0x0075:
            r0 = move-exception
            goto L_0x006b
        L_0x0077:
            r1 = move-exception
            r1 = r0
            goto L_0x004a
        L_0x007a:
            r2 = move-exception
            goto L_0x0059
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.f.b():java.lang.String");
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.c.b();
    }

    /* access modifiers changed from: package-private */
    public void a(String str) {
        String encodeOfflineLocationUpdateRequest = Jni.encodeOfflineLocationUpdateRequest(str);
        try {
            this.b.execSQL(String.format(Locale.US, "INSERT OR IGNORE INTO LOG VALUES (%d,\"%s\");", new Object[]{Long.valueOf(System.currentTimeMillis()), encodeOfflineLocationUpdateRequest}));
            this.b.execSQL(d);
        } catch (Exception e2) {
        }
    }
}
