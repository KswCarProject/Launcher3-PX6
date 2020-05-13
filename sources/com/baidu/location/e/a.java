package com.baidu.location.e;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.a.e;
import com.baidu.location.f;
import com.baidu.location.f.c;
import com.baidu.location.f.i;
import com.baidu.location.f.k;
import java.io.File;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;

public final class a {
    private static a a = null;
    private static final String k = (Environment.getExternalStorageDirectory().getPath() + "/baidu/tempdata/");
    /* access modifiers changed from: private */
    public static final String l = (Environment.getExternalStorageDirectory().getPath() + "/baidu/tempdata" + "/ls.db");
    private String b = null;
    private boolean c = false;
    private boolean d = false;
    private double e = 0.0d;
    private double f = 0.0d;
    private double g = 0.0d;
    private double h = 0.0d;
    private double i = 0.0d;
    /* access modifiers changed from: private */
    public volatile boolean j = false;
    private Handler m = new Handler();

    /* renamed from: com.baidu.location.e.a$a  reason: collision with other inner class name */
    private class C0004a extends AsyncTask<Boolean, Void, Boolean> {
        private C0004a() {
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Boolean... boolArr) {
            SQLiteDatabase sQLiteDatabase = null;
            if (boolArr.length != 4) {
                return false;
            }
            try {
                sQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(a.l, (SQLiteDatabase.CursorFactory) null);
            } catch (Exception e) {
            }
            if (sQLiteDatabase == null) {
                return false;
            }
            int currentTimeMillis = (int) (System.currentTimeMillis() >> 28);
            try {
                sQLiteDatabase.beginTransaction();
                if (boolArr[0].booleanValue()) {
                    try {
                        sQLiteDatabase.execSQL("delete from wof where ac < " + (currentTimeMillis - 35));
                    } catch (Exception e2) {
                    }
                }
                if (boolArr[1].booleanValue()) {
                    try {
                        sQLiteDatabase.execSQL("delete from bdcltb09 where ac is NULL or ac < " + (currentTimeMillis - 130));
                    } catch (Exception e3) {
                    }
                }
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                sQLiteDatabase.close();
            } catch (Exception e4) {
            }
            return true;
        }
    }

    private class b extends AsyncTask<Object, Void, Boolean> {
        private b() {
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Object... objArr) {
            SQLiteDatabase sQLiteDatabase;
            if (objArr.length != 4) {
                boolean unused = a.this.j = false;
                return false;
            }
            try {
                sQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(a.l, (SQLiteDatabase.CursorFactory) null);
            } catch (Exception e) {
                sQLiteDatabase = null;
            }
            if (sQLiteDatabase == null) {
                boolean unused2 = a.this.j = false;
                return false;
            }
            try {
                sQLiteDatabase.beginTransaction();
                a.this.a(objArr[0], objArr[1], sQLiteDatabase);
                a.this.a(objArr[2], objArr[3], sQLiteDatabase);
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                sQLiteDatabase.close();
            } catch (Exception e2) {
            }
            boolean unused3 = a.this.j = false;
            return true;
        }
    }

    private a() {
        try {
            File file = new File(k);
            File file2 = new File(l);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file2.exists()) {
                file2.createNewFile();
            }
            if (file2.exists()) {
                SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(file2, (SQLiteDatabase.CursorFactory) null);
                openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS bdcltb09(id CHAR(40) PRIMARY KEY,time DOUBLE,tag DOUBLE, type DOUBLE , ac INT);");
                openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS wof(id CHAR(15) PRIMARY KEY,mktime DOUBLE,time DOUBLE, ac INT, bc INT, cc INT);");
                openOrCreateDatabase.setVersion(1);
                openOrCreateDatabase.close();
            }
        } catch (Exception e2) {
        }
    }

    public static synchronized a a() {
        a aVar;
        synchronized (a.class) {
            if (a == null) {
                a = new a();
            }
            aVar = a;
        }
        return aVar;
    }

    /* access modifiers changed from: private */
    public void a(i iVar, BDLocation bDLocation, SQLiteDatabase sQLiteDatabase) {
        int i2;
        int i3;
        double d2;
        boolean z;
        double d3;
        if (bDLocation != null && bDLocation.getLocType() == 161) {
            if (("wf".equals(bDLocation.getNetworkLocationType()) || bDLocation.getRadius() < 300.0f) && iVar.a != null) {
                int currentTimeMillis = (int) (System.currentTimeMillis() >> 28);
                System.currentTimeMillis();
                int i4 = 0;
                for (ScanResult next : iVar.a) {
                    if (next.level != 0) {
                        int i5 = i4 + 1;
                        if (i5 <= 6) {
                            ContentValues contentValues = new ContentValues();
                            String encode2 = Jni.encode2(next.BSSID.replace(":", ""));
                            try {
                                Cursor rawQuery = sQLiteDatabase.rawQuery("select * from wof where id = \"" + encode2 + "\";", (String[]) null);
                                if (rawQuery == null || !rawQuery.moveToFirst()) {
                                    i2 = 0;
                                    i3 = 0;
                                    d2 = 0.0d;
                                    z = false;
                                    d3 = 0.0d;
                                } else {
                                    double d4 = rawQuery.getDouble(1) - 113.2349d;
                                    int i6 = rawQuery.getInt(4);
                                    i2 = rawQuery.getInt(5);
                                    i3 = i6;
                                    d2 = d4;
                                    z = true;
                                    d3 = rawQuery.getDouble(2) - 432.1238d;
                                }
                                if (rawQuery != null) {
                                    rawQuery.close();
                                }
                                if (!z) {
                                    contentValues.put("mktime", Double.valueOf(bDLocation.getLongitude() + 113.2349d));
                                    contentValues.put("time", Double.valueOf(bDLocation.getLatitude() + 432.1238d));
                                    contentValues.put("bc", 1);
                                    contentValues.put("cc", 1);
                                    contentValues.put("ac", Integer.valueOf(currentTimeMillis));
                                    contentValues.put("id", encode2);
                                    sQLiteDatabase.insert("wof", (String) null, contentValues);
                                } else if (i2 == 0) {
                                    i4 = i5;
                                } else {
                                    float[] fArr = new float[1];
                                    Location.distanceBetween(d3, d2, bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
                                    if (fArr[0] > 1500.0f) {
                                        int i7 = i2 + 1;
                                        if (i7 <= 10 || i7 <= i3 * 3) {
                                            contentValues.put("cc", Integer.valueOf(i7));
                                        } else {
                                            contentValues.put("mktime", Double.valueOf(bDLocation.getLongitude() + 113.2349d));
                                            contentValues.put("time", Double.valueOf(bDLocation.getLatitude() + 432.1238d));
                                            contentValues.put("bc", 1);
                                            contentValues.put("cc", 1);
                                            contentValues.put("ac", Integer.valueOf(currentTimeMillis));
                                        }
                                    } else {
                                        contentValues.put("mktime", Double.valueOf((((d2 * ((double) i3)) + bDLocation.getLongitude()) / ((double) (i3 + 1))) + 113.2349d));
                                        contentValues.put("time", Double.valueOf((((d3 * ((double) i3)) + bDLocation.getLatitude()) / ((double) (i3 + 1))) + 432.1238d));
                                        contentValues.put("bc", Integer.valueOf(i3 + 1));
                                        contentValues.put("ac", Integer.valueOf(currentTimeMillis));
                                    }
                                    try {
                                        if (sQLiteDatabase.update("wof", contentValues, "id = \"" + encode2 + "\"", (String[]) null) <= 0) {
                                        }
                                    } catch (Exception e2) {
                                    }
                                }
                            } catch (Exception e3) {
                            }
                            i4 = i5;
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0068, code lost:
        if (r0 != null) goto L_0x006a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x007c, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007d, code lost:
        r6 = r1;
        r1 = r0;
        r0 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0067 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:5:0x000f] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0076 A[SYNTHETIC, Splitter:B:23:0x0076] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(java.lang.String r8, android.database.sqlite.SQLiteDatabase r9) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L_0x000b
            java.lang.String r1 = r7.b
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000c
        L_0x000b:
            return
        L_0x000c:
            r1 = 0
            r7.c = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            r1.<init>()     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            java.lang.String r2 = "select * from bdcltb09 where id = \""
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            java.lang.String r2 = "\";"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            r2 = 0
            android.database.Cursor r0 = r9.rawQuery(r1, r2)     // Catch:{ Exception -> 0x0067, all -> 0x0070 }
            r7.b = r8     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            boolean r1 = r0.moveToFirst()     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            if (r1 == 0) goto L_0x005f
            r1 = 1
            double r2 = r0.getDouble(r1)     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            r4 = 4653148304163072062(0x40934dbaacd9e83e, double:1235.4323)
            double r2 = r2 - r4
            r7.f = r2     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            r1 = 2
            double r2 = r0.getDouble(r1)     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            r4 = 4661478502002851840(0x40b0e60000000000, double:4326.0)
            double r2 = r2 - r4
            r7.e = r2     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            r1 = 3
            double r2 = r0.getDouble(r1)     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            r4 = 4657424210545395263(0x40a27ea4b5dcc63f, double:2367.3217)
            double r2 = r2 - r4
            r7.g = r2     // Catch:{ Exception -> 0x0067, all -> 0x007c }
            r1 = 1
            r7.c = r1     // Catch:{ Exception -> 0x0067, all -> 0x007c }
        L_0x005f:
            if (r0 == 0) goto L_0x000b
            r0.close()     // Catch:{ Exception -> 0x0065 }
            goto L_0x000b
        L_0x0065:
            r0 = move-exception
            goto L_0x000b
        L_0x0067:
            r1 = move-exception
            if (r0 == 0) goto L_0x000b
            r0.close()     // Catch:{ Exception -> 0x006e }
            goto L_0x000b
        L_0x006e:
            r0 = move-exception
            goto L_0x000b
        L_0x0070:
            r1 = move-exception
            r6 = r1
            r1 = r0
            r0 = r6
        L_0x0074:
            if (r1 == 0) goto L_0x0079
            r1.close()     // Catch:{ Exception -> 0x007a }
        L_0x0079:
            throw r0
        L_0x007a:
            r1 = move-exception
            goto L_0x0079
        L_0x007c:
            r1 = move-exception
            r6 = r1
            r1 = r0
            r0 = r6
            goto L_0x0074
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.a.a(java.lang.String, android.database.sqlite.SQLiteDatabase):void");
    }

    /* access modifiers changed from: private */
    public void a(String str, com.baidu.location.f.a aVar, SQLiteDatabase sQLiteDatabase) {
        float f2;
        double d2;
        boolean z = false;
        double d3 = 0.0d;
        if (aVar.b() && e.b().g()) {
            System.currentTimeMillis();
            int currentTimeMillis = (int) (System.currentTimeMillis() >> 28);
            String g2 = aVar.g();
            try {
                JSONObject jSONObject = new JSONObject(str);
                int parseInt = Integer.parseInt(jSONObject.getJSONObject("result").getString("error"));
                if (parseInt == 161) {
                    JSONObject jSONObject2 = jSONObject.getJSONObject("content");
                    if (jSONObject2.has("clf")) {
                        String string = jSONObject2.getString("clf");
                        if (string.equals("0")) {
                            JSONObject jSONObject3 = jSONObject2.getJSONObject("point");
                            d3 = Double.parseDouble(jSONObject3.getString("x"));
                            d2 = Double.parseDouble(jSONObject3.getString("y"));
                            f2 = Float.parseFloat(jSONObject2.getString("radius"));
                        } else {
                            String[] split = string.split("\\|");
                            d3 = Double.parseDouble(split[0]);
                            d2 = Double.parseDouble(split[1]);
                            f2 = Float.parseFloat(split[2]);
                        }
                    }
                    z = true;
                    f2 = 0.0f;
                    d2 = 0.0d;
                } else {
                    if (parseInt == 167) {
                        sQLiteDatabase.delete("bdcltb09", "id = \"" + g2 + "\"", (String[]) null);
                        return;
                    }
                    z = true;
                    f2 = 0.0f;
                    d2 = 0.0d;
                }
                if (!z) {
                    float f3 = 4326.0f + f2;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("time", Double.valueOf(d3 + 1235.4323d));
                    contentValues.put("tag", Float.valueOf(f3));
                    contentValues.put("type", Double.valueOf(d2 + 2367.3217d));
                    contentValues.put("ac", Integer.valueOf(currentTimeMillis));
                    try {
                        if (sQLiteDatabase.update("bdcltb09", contentValues, "id = \"" + g2 + "\"", (String[]) null) <= 0) {
                            contentValues.put("id", g2);
                            sQLiteDatabase.insert("bdcltb09", (String) null, contentValues);
                        }
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(String str, List<ScanResult> list) {
        SQLiteDatabase sQLiteDatabase;
        if (str == null || str.equals(this.b)) {
            sQLiteDatabase = null;
        } else {
            sQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(l, (SQLiteDatabase.CursorFactory) null);
            a(str, sQLiteDatabase);
        }
        if (list != null) {
            if (sQLiteDatabase == null) {
                sQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(l, (SQLiteDatabase.CursorFactory) null);
            }
            a(list, sQLiteDatabase);
        }
        if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
            sQLiteDatabase.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f9 A[SYNTHETIC, Splitter:B:39:0x00f9] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(java.util.List<android.net.wifi.ScanResult> r25, android.database.sqlite.SQLiteDatabase r26) {
        /*
            r24 = this;
            java.lang.System.currentTimeMillis()
            r2 = 0
            r0 = r24
            r0.d = r2
            if (r25 != 0) goto L_0x000b
        L_0x000a:
            return
        L_0x000b:
            if (r26 == 0) goto L_0x000a
            if (r25 == 0) goto L_0x000a
            r2 = 0
            r16 = 0
            r14 = 0
            r12 = 0
            r11 = 0
            r3 = 8
            double[] r0 = new double[r3]
            r21 = r0
            r19 = 0
            r18 = 0
            java.lang.StringBuffer r4 = new java.lang.StringBuffer
            r4.<init>()
            java.util.Iterator r5 = r25.iterator()
            r3 = r2
        L_0x002a:
            boolean r2 = r5.hasNext()
            if (r2 == 0) goto L_0x003a
            java.lang.Object r2 = r5.next()
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2
            r6 = 10
            if (r3 <= r6) goto L_0x00a2
        L_0x003a:
            r2 = 0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            r3.<init>()     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            java.lang.String r5 = "select * from wof where id in ("
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            java.lang.String r4 = ");"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            r4 = 0
            r0 = r26
            android.database.Cursor r13 = r0.rawQuery(r3, r4)     // Catch:{ Exception -> 0x01bd, all -> 0x01b8 }
            boolean r2 = r13.moveToFirst()     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            if (r2 == 0) goto L_0x011f
        L_0x0065:
            boolean r2 = r13.isAfterLast()     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            if (r2 != 0) goto L_0x010a
            r2 = 1
            double r2 = r13.getDouble(r2)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r4 = 4637668614646953253(0x405c4f089a027525, double:113.2349)
            double r4 = r2 - r4
            r2 = 2
            double r2 = r13.getDouble(r2)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r6 = 4646309618475430891(0x407b01fb15b573eb, double:432.1238)
            double r2 = r2 - r6
            r6 = 4
            int r6 = r13.getInt(r6)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r7 = 5
            int r7 = r13.getInt(r7)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r8 = 8
            if (r7 <= r8) goto L_0x00ca
            if (r7 <= r6) goto L_0x00ca
            r13.moveToNext()     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            goto L_0x0065
        L_0x0096:
            r2 = move-exception
            r2 = r13
        L_0x0098:
            if (r2 == 0) goto L_0x000a
            r2.close()     // Catch:{ Exception -> 0x009f }
            goto L_0x000a
        L_0x009f:
            r2 = move-exception
            goto L_0x000a
        L_0x00a2:
            if (r3 <= 0) goto L_0x00a9
            java.lang.String r6 = ","
            r4.append(r6)
        L_0x00a9:
            int r3 = r3 + 1
            java.lang.String r2 = r2.BSSID
            java.lang.String r6 = ":"
            java.lang.String r7 = ""
            java.lang.String r2 = r2.replace(r6, r7)
            java.lang.String r2 = com.baidu.location.Jni.encode2(r2)
            java.lang.String r6 = "\""
            java.lang.StringBuffer r6 = r4.append(r6)
            java.lang.StringBuffer r2 = r6.append(r2)
            java.lang.String r6 = "\""
            r2.append(r6)
            goto L_0x002a
        L_0x00ca:
            r0 = r24
            boolean r6 = r0.c     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            if (r6 == 0) goto L_0x0129
            r6 = 1
            float[] r10 = new float[r6]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r0 = r24
            double r6 = r0.g     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r0 = r24
            double r8 = r0.f     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            android.location.Location.distanceBetween(r2, r4, r6, r8, r10)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r6 = 0
            r6 = r10[r6]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r6 = (double) r6     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r0 = r24
            double r8 = r0.e     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r22 = 4656510908468559872(0x409f400000000000, double:2000.0)
            double r8 = r8 + r22
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x00fd
            r13.moveToNext()     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            goto L_0x0065
        L_0x00f6:
            r2 = move-exception
        L_0x00f7:
            if (r13 == 0) goto L_0x00fc
            r13.close()     // Catch:{ Exception -> 0x01b5 }
        L_0x00fc:
            throw r2
        L_0x00fd:
            r11 = 1
            double r16 = r16 + r4
            double r14 = r14 + r2
            int r12 = r12 + 1
            r2 = r18
            r3 = r19
        L_0x0107:
            r4 = 4
            if (r12 <= r4) goto L_0x01ac
        L_0x010a:
            if (r12 <= 0) goto L_0x011f
            r2 = 1
            r0 = r24
            r0.d = r2     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r2 = (double) r12     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r2 = r16 / r2
            r0 = r24
            r0.h = r2     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r2 = (double) r12     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r2 = r14 / r2
            r0 = r24
            r0.i = r2     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
        L_0x011f:
            if (r13 == 0) goto L_0x000a
            r13.close()     // Catch:{ Exception -> 0x0126 }
            goto L_0x000a
        L_0x0126:
            r2 = move-exception
            goto L_0x000a
        L_0x0129:
            if (r11 == 0) goto L_0x014a
            r6 = 1
            float[] r10 = new float[r6]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r6 = (double) r12     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r6 = r14 / r6
            double r8 = (double) r12     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r8 = r16 / r8
            android.location.Location.distanceBetween(r2, r4, r6, r8, r10)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r2 = 0
            r2 = r10[r2]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r3 = 1148846080(0x447a0000, float:1000.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0145
            r13.moveToNext()     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            goto L_0x0065
        L_0x0145:
            r2 = r18
            r3 = r19
            goto L_0x0107
        L_0x014a:
            if (r19 != 0) goto L_0x0158
            int r6 = r18 + 1
            r21[r18] = r4     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            int r4 = r6 + 1
            r21[r6] = r2     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r2 = 1
            r3 = r2
            r2 = r4
            goto L_0x0107
        L_0x0158:
            r6 = 0
            r20 = r6
        L_0x015b:
            r0 = r20
            r1 = r18
            if (r0 >= r1) goto L_0x018c
            r6 = 1
            float[] r10 = new float[r6]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            int r6 = r20 + 1
            r6 = r21[r6]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r8 = r21[r20]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            android.location.Location.distanceBetween(r2, r4, r6, r8, r10)     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r6 = 0
            r6 = r10[r6]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r7 = 1148846080(0x447a0000, float:1000.0)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 >= 0) goto L_0x01c0
            r6 = 1
            r8 = r21[r20]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r10 = r16 + r8
            int r7 = r20 + 1
            r8 = r21[r7]     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            double r8 = r8 + r14
            int r7 = r12 + 1
        L_0x0182:
            int r12 = r20 + 2
            r20 = r12
            r14 = r8
            r16 = r10
            r11 = r6
            r12 = r7
            goto L_0x015b
        L_0x018c:
            if (r11 == 0) goto L_0x0199
            double r16 = r16 + r4
            double r14 = r14 + r2
            int r12 = r12 + 1
            r2 = r18
            r3 = r19
            goto L_0x0107
        L_0x0199:
            r6 = 8
            r0 = r18
            if (r0 >= r6) goto L_0x010a
            int r6 = r18 + 1
            r21[r18] = r4     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            int r4 = r6 + 1
            r21[r6] = r2     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r2 = r4
            r3 = r19
            goto L_0x0107
        L_0x01ac:
            r13.moveToNext()     // Catch:{ Exception -> 0x0096, all -> 0x00f6 }
            r18 = r2
            r19 = r3
            goto L_0x0065
        L_0x01b5:
            r3 = move-exception
            goto L_0x00fc
        L_0x01b8:
            r3 = move-exception
            r13 = r2
            r2 = r3
            goto L_0x00f7
        L_0x01bd:
            r3 = move-exception
            goto L_0x0098
        L_0x01c0:
            r6 = r11
            r7 = r12
            r8 = r14
            r10 = r16
            goto L_0x0182
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.a.a(java.util.List, android.database.sqlite.SQLiteDatabase):void");
    }

    /* access modifiers changed from: private */
    public String b(boolean z) {
        boolean z2;
        boolean z3;
        double d2;
        double d3;
        double d4 = 0.0d;
        if (this.d) {
            d3 = this.h;
            d2 = this.i;
            d4 = 246.4d;
            z2 = true;
            z3 = true;
        } else if (this.c) {
            d3 = this.f;
            d2 = this.g;
            d4 = this.e;
            z2 = e.b().g();
            z3 = true;
        } else {
            z2 = false;
            z3 = false;
            d2 = 0.0d;
            d3 = 0.0d;
        }
        if (!z3) {
            return z ? "{\"result\":{\"time\":\"" + com.baidu.location.h.i.a() + "\",\"error\":\"67\"}}" : "{\"result\":{\"time\":\"" + com.baidu.location.h.i.a() + "\",\"error\":\"63\"}}";
        }
        if (z) {
            return String.format(Locale.CHINA, "{\"result\":{\"time\":\"" + com.baidu.location.h.i.a() + "\",\"error\":\"66\"},\"content\":{\"point\":{\"x\":" + "\"%f\",\"y\":\"%f\"},\"radius\":\"%f\",\"isCellChanged\":\"%b\"}}", new Object[]{Double.valueOf(d3), Double.valueOf(d2), Double.valueOf(d4), true});
        }
        return String.format(Locale.CHINA, "{\"result\":{\"time\":\"" + com.baidu.location.h.i.a() + "\",\"error\":\"66\"},\"content\":{\"point\":{\"x\":" + "\"%f\",\"y\":\"%f\"},\"radius\":\"%f\",\"isCellChanged\":\"%b\"}}", new Object[]{Double.valueOf(d3), Double.valueOf(d2), Double.valueOf(d4), Boolean.valueOf(z2)});
    }

    /* access modifiers changed from: private */
    public void d() {
        SQLiteDatabase sQLiteDatabase;
        boolean z = true;
        try {
            sQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(l, (SQLiteDatabase.CursorFactory) null);
        } catch (Exception e2) {
            sQLiteDatabase = null;
        }
        if (sQLiteDatabase != null) {
            try {
                long queryNumEntries = DatabaseUtils.queryNumEntries(sQLiteDatabase, "wof");
                long queryNumEntries2 = DatabaseUtils.queryNumEntries(sQLiteDatabase, "bdcltb09");
                boolean z2 = queryNumEntries > 10000;
                if (queryNumEntries2 <= 10000) {
                    z = false;
                }
                if (z2 || z) {
                    new C0004a().execute(new Boolean[]{Boolean.valueOf(z2), Boolean.valueOf(z)});
                }
                sQLiteDatabase.close();
            } catch (Exception e3) {
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.baidu.location.BDLocation a(final java.lang.String r9, final java.util.List<android.net.wifi.ScanResult> r10, boolean r11) {
        /*
            r8 = this;
            java.lang.String r4 = "{\"result\":\"null\"}"
            java.util.concurrent.ExecutorService r5 = java.util.concurrent.Executors.newSingleThreadExecutor()
            com.baidu.location.e.a$2 r2 = new com.baidu.location.e.a$2
            r2.<init>(r9, r10)
            java.util.concurrent.Future r2 = r5.submit(r2)
            java.util.concurrent.FutureTask r2 = (java.util.concurrent.FutureTask) r2
            r6 = 2000(0x7d0, double:9.88E-321)
            java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ InterruptedException -> 0x0026, ExecutionException -> 0x0030, TimeoutException -> 0x003a }
            java.lang.Object r3 = r2.get(r6, r3)     // Catch:{ InterruptedException -> 0x0026, ExecutionException -> 0x0030, TimeoutException -> 0x003a }
            r0 = r3
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ InterruptedException -> 0x0026, ExecutionException -> 0x0030, TimeoutException -> 0x003a }
            r2 = r0
            r5.shutdown()
        L_0x0020:
            com.baidu.location.BDLocation r3 = new com.baidu.location.BDLocation
            r3.<init>((java.lang.String) r2)
            return r3
        L_0x0026:
            r3 = move-exception
            r3 = 1
            r2.cancel(r3)     // Catch:{ all -> 0x004f }
            r5.shutdown()
            r2 = r4
            goto L_0x0020
        L_0x0030:
            r3 = move-exception
            r3 = 1
            r2.cancel(r3)     // Catch:{ all -> 0x004f }
            r5.shutdown()
            r2 = r4
            goto L_0x0020
        L_0x003a:
            r3 = move-exception
            if (r11 == 0) goto L_0x0046
            com.baidu.location.c.f r3 = com.baidu.location.c.f.a()     // Catch:{ all -> 0x004f }
            java.lang.String r6 = "old offlineLocation Timeout Exception!"
            r3.a((java.lang.String) r6)     // Catch:{ all -> 0x004f }
        L_0x0046:
            r3 = 1
            r2.cancel(r3)     // Catch:{ all -> 0x004f }
            r5.shutdown()
            r2 = r4
            goto L_0x0020
        L_0x004f:
            r2 = move-exception
            r5.shutdown()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.a.a(java.lang.String, java.util.List, boolean):com.baidu.location.BDLocation");
    }

    public BDLocation a(boolean z) {
        BDLocation bDLocation = null;
        com.baidu.location.f.a f2 = c.a().f();
        String g2 = f2 != null ? f2.g() : null;
        i i2 = k.a().i();
        if (i2 != null) {
            bDLocation = a(g2, i2.a, true);
        }
        if (bDLocation != null && bDLocation.getLocType() == 66) {
            StringBuffer stringBuffer = new StringBuffer(1024);
            stringBuffer.append(String.format(Locale.CHINA, "&ofl=%f|%f|%f", new Object[]{Double.valueOf(bDLocation.getLatitude()), Double.valueOf(bDLocation.getLongitude()), Float.valueOf(bDLocation.getRadius())}));
            if (i2 != null && i2.a() > 0) {
                stringBuffer.append("&wf=");
                stringBuffer.append(i2.b(15));
            }
            if (f2 != null) {
                stringBuffer.append(f2.h());
            }
            stringBuffer.append("&uptype=oldoff");
            stringBuffer.append(com.baidu.location.h.c.a().a(false));
            stringBuffer.append(com.baidu.location.a.a.a().c());
            stringBuffer.toString();
        }
        return bDLocation;
    }

    public void a(String str, com.baidu.location.f.a aVar, i iVar, BDLocation bDLocation) {
        boolean z = !aVar.b() || !e.b().g();
        boolean z2 = bDLocation == null || bDLocation.getLocType() != 161 || (!"wf".equals(bDLocation.getNetworkLocationType()) && bDLocation.getRadius() >= 300.0f);
        if (iVar.a == null) {
            z2 = true;
        }
        if ((!z || !z2) && !this.j) {
            this.j = true;
            new b().execute(new Object[]{str, aVar, iVar, bDLocation});
        }
    }

    public void b() {
        this.m.postDelayed(new Runnable() {
            public void run() {
                if (f.isServing) {
                    a.this.d();
                }
            }
        }, 3000);
    }
}
