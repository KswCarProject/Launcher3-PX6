package com.baidu.location.e;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.baidu.location.LocationClientOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class g {
    /* access modifiers changed from: private */
    public static final double[] b = {45.0d, 135.0d, 225.0d, 315.0d};
    private final d a;
    private final int c;
    private final SQLiteDatabase d;
    private int e = -1;
    private int f = -1;

    private static final class a {
        /* access modifiers changed from: private */
        public double a;
        /* access modifiers changed from: private */
        public double b;

        private a(double d, double d2) {
            this.a = d;
            this.b = d2;
        }
    }

    private enum b {
        AREA("RGCAREA", "area", "addrv", 0, 1000) {
            /* access modifiers changed from: package-private */
            public List<String> a(JSONObject jSONObject, String str, int i) {
                int i2 = 0;
                Iterator<String> keys = jSONObject.keys();
                StringBuffer stringBuffer = new StringBuffer();
                StringBuffer stringBuffer2 = new StringBuffer();
                ArrayList arrayList = new ArrayList();
                while (true) {
                    int i3 = i2;
                    if (!keys.hasNext()) {
                        break;
                    }
                    String str2 = null;
                    String str3 = null;
                    String str4 = null;
                    String str5 = null;
                    String str6 = null;
                    String str7 = null;
                    String next = keys.next();
                    try {
                        JSONObject jSONObject2 = jSONObject.getJSONObject(next);
                        if (jSONObject2.has("cy")) {
                            str2 = jSONObject2.getString("cy");
                        }
                        if (jSONObject2.has("cyc")) {
                            str3 = jSONObject2.getString("cyc");
                        }
                        if (jSONObject2.has("prov")) {
                            str4 = jSONObject2.getString("prov");
                        }
                        if (jSONObject2.has("ctc")) {
                            str5 = jSONObject2.getString("ctc");
                        }
                        if (jSONObject2.has("ct")) {
                            str6 = jSONObject2.getString("ct");
                        }
                        if (jSONObject2.has("dist")) {
                            str7 = jSONObject2.getString("dist");
                        }
                        if (stringBuffer.length() > 0) {
                            stringBuffer.append(",");
                        }
                        stringBuffer.append("(\"").append(next).append("\",\"").append(str2).append("\",\"").append(str3).append("\",\"").append(str4).append("\",\"").append(str6).append("\",\"").append(str5).append("\",\"").append(str7).append("\",").append(System.currentTimeMillis() / 1000).append(",\"\")");
                        b.b(stringBuffer2, next, str, 0);
                    } catch (JSONException e) {
                    }
                    if (i3 % 50 == 49 && stringBuffer.length() > 0) {
                        arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCAREA", stringBuffer}));
                        arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCUPDATE", stringBuffer2}));
                        stringBuffer.setLength(0);
                    }
                    i2 = i3 + 1;
                }
                if (stringBuffer.length() > 0) {
                    arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCAREA", stringBuffer}));
                    arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCUPDATE", stringBuffer2}));
                    stringBuffer.setLength(0);
                }
                arrayList.add(String.format(Locale.US, "DELETE FROM RGCAREA WHERE gridkey NOT IN (SELECT gridkey FROM RGCAREA LIMIT %d);", new Object[]{Integer.valueOf(i)}));
                return arrayList;
            }
        },
        ROAD("RGCROAD", "road", "addrv", 1000, LocationClientOption.MIN_AUTO_NOTIFY_INTERVAL) {
            /* access modifiers changed from: package-private */
            public List<String> a(JSONObject jSONObject, String str, int i) {
                JSONArray jSONArray;
                Iterator<String> keys = jSONObject.keys();
                ArrayList arrayList = new ArrayList();
                StringBuffer stringBuffer = new StringBuffer();
                while (keys.hasNext()) {
                    StringBuffer stringBuffer2 = new StringBuffer();
                    String next = keys.next();
                    b.b(stringBuffer, next, str, 0);
                    try {
                        jSONArray = jSONObject.getJSONArray(next);
                    } catch (JSONException e) {
                        jSONArray = null;
                    }
                    if (jSONArray != null) {
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            Double d = null;
                            Double d2 = null;
                            Double d3 = null;
                            Double d4 = null;
                            String str2 = null;
                            try {
                                JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                                if (jSONObject2.has("st")) {
                                    str2 = jSONObject2.getString("st");
                                }
                                if (jSONObject2.has("x1")) {
                                    d = Double.valueOf(jSONObject2.getDouble("x1"));
                                }
                                if (jSONObject2.has("y1")) {
                                    d2 = Double.valueOf(jSONObject2.getDouble("y1"));
                                }
                                if (jSONObject2.has("x2")) {
                                    d3 = Double.valueOf(jSONObject2.getDouble("x2"));
                                }
                                if (jSONObject2.has("y2")) {
                                    d4 = Double.valueOf(jSONObject2.getDouble("y2"));
                                }
                                if (!(str2 == null || d == null || d2 == null || d3 == null || d4 == null)) {
                                    if (stringBuffer2.length() > 0) {
                                        stringBuffer2.append(",");
                                    }
                                    stringBuffer2.append("(NULL,\"").append(next).append("\",\"").append(str2).append("\",").append(d).append(",").append(d2).append(",").append(d3).append(",").append(d4).append(")");
                                }
                            } catch (JSONException e2) {
                            }
                            if (i2 % 50 == 49 && stringBuffer2.length() > 0) {
                                arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCROAD", stringBuffer2.toString()}));
                                stringBuffer2.setLength(0);
                            }
                        }
                        if (stringBuffer2.length() > 0) {
                            arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCROAD", stringBuffer2.toString()}));
                        }
                    }
                }
                if (stringBuffer.length() > 0) {
                    arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCUPDATE", stringBuffer}));
                }
                arrayList.add(String.format(Locale.US, "DELETE FROM RGCROAD WHERE _id NOT IN (SELECT _id FROM RGCROAD LIMIT %d);", new Object[]{Integer.valueOf(i)}));
                return arrayList;
            }
        },
        SITE("RGCSITE", "site", "addrv", 100, 50000) {
            /* access modifiers changed from: package-private */
            public List<String> a(JSONObject jSONObject, String str, int i) {
                JSONArray jSONArray;
                Iterator<String> keys = jSONObject.keys();
                ArrayList arrayList = new ArrayList();
                StringBuffer stringBuffer = new StringBuffer();
                while (keys.hasNext()) {
                    StringBuffer stringBuffer2 = new StringBuffer();
                    String next = keys.next();
                    b.b(stringBuffer, next, str, 0);
                    try {
                        jSONArray = jSONObject.getJSONArray(next);
                    } catch (JSONException e) {
                        jSONArray = null;
                    }
                    if (jSONArray != null) {
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            Double d = null;
                            Double d2 = null;
                            String str2 = null;
                            String str3 = null;
                            try {
                                JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                                if (jSONObject2.has("st")) {
                                    str2 = jSONObject2.getString("st");
                                }
                                if (jSONObject2.has("stn")) {
                                    str3 = jSONObject2.getString("stn");
                                }
                                if (jSONObject2.has("x")) {
                                    d = Double.valueOf(jSONObject2.getDouble("x"));
                                }
                                if (jSONObject2.has("y")) {
                                    d2 = Double.valueOf(jSONObject2.getDouble("y"));
                                }
                                if (stringBuffer2.length() > 0) {
                                    stringBuffer2.append(",");
                                }
                                stringBuffer2.append("(NULL,\"").append(next).append("\",\"").append(str2).append("\",\"").append(str3).append("\",").append(d).append(",").append(d2).append(")");
                            } catch (JSONException e2) {
                            }
                            if (i2 % 50 == 49 && stringBuffer2.length() > 0) {
                                arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCSITE", stringBuffer2.toString()}));
                                stringBuffer2.setLength(0);
                            }
                        }
                        if (stringBuffer2.length() > 0) {
                            arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCSITE", stringBuffer2.toString()}));
                        }
                    }
                }
                if (stringBuffer.length() > 0) {
                    arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCUPDATE", stringBuffer}));
                }
                arrayList.add(String.format(Locale.US, "DELETE FROM RGCSITE WHERE _id NOT IN (SELECT _id FROM RGCSITE LIMIT %d);", new Object[]{Integer.valueOf(i)}));
                return arrayList;
            }
        },
        POI("RGCPOI", "poi", "poiv", 1000, 5000) {
            /* access modifiers changed from: package-private */
            public List<String> a(JSONObject jSONObject, String str, int i) {
                JSONArray jSONArray;
                Iterator<String> keys = jSONObject.keys();
                ArrayList arrayList = new ArrayList();
                StringBuffer stringBuffer = new StringBuffer();
                while (keys.hasNext()) {
                    StringBuffer stringBuffer2 = new StringBuffer();
                    String next = keys.next();
                    b.b(stringBuffer, next, str, 1);
                    try {
                        jSONArray = jSONObject.getJSONArray(next);
                    } catch (JSONException e) {
                        jSONArray = null;
                    }
                    if (jSONArray != null) {
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            Double d = null;
                            Double d2 = null;
                            String str2 = null;
                            String str3 = null;
                            String str4 = null;
                            Integer num = null;
                            try {
                                JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                                if (jSONObject2.has("pid")) {
                                    str2 = jSONObject2.getString("pid");
                                }
                                if (jSONObject2.has("ne")) {
                                    str3 = jSONObject2.getString("ne");
                                }
                                if (jSONObject2.has("tp")) {
                                    str4 = jSONObject2.getString("tp");
                                }
                                if (jSONObject2.has("rk")) {
                                    num = Integer.valueOf(jSONObject2.getInt("rk"));
                                }
                                if (jSONObject2.has("x")) {
                                    d = Double.valueOf(jSONObject2.getDouble("x"));
                                }
                                if (jSONObject2.has("y")) {
                                    d2 = Double.valueOf(jSONObject2.getDouble("y"));
                                }
                                if (stringBuffer2.length() > 0) {
                                    stringBuffer2.append(",");
                                }
                                stringBuffer2.append("(\"").append(str2).append("\",\"").append(next).append("\",\"").append(str3).append("\",\"").append(str4).append("\",").append(d).append(",").append(d2).append(",").append(num).append(")");
                            } catch (JSONException e2) {
                            }
                            if (i2 % 50 == 49) {
                                arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCPOI", stringBuffer2.toString()}));
                                stringBuffer2.setLength(0);
                            }
                        }
                        if (stringBuffer2.length() > 0) {
                            arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCPOI", stringBuffer2.toString()}));
                        }
                    }
                }
                if (stringBuffer.length() > 0) {
                    arrayList.add(String.format(Locale.US, "INSERT OR REPLACE INTO %s VALUES %s", new Object[]{"RGCUPDATE", stringBuffer}));
                }
                arrayList.add(String.format(Locale.US, "DELETE FROM RGCPOI WHERE pid NOT IN (SELECT pid FROM RGCPOI LIMIT %d);", new Object[]{Integer.valueOf(i)}));
                return arrayList;
            }
        };
        
        /* access modifiers changed from: private */
        public final int e;
        private final String f;
        /* access modifiers changed from: private */
        public final String g;
        /* access modifiers changed from: private */
        public final String h;
        /* access modifiers changed from: private */
        public final int i;

        private b(String str, String str2, String str3, int i2, int i3) {
            this.f = str;
            this.g = str2;
            this.h = str3;
            this.e = i2;
            this.i = i3;
        }

        /* access modifiers changed from: private */
        public String a(int i2, double d, double d2) {
            HashSet hashSet = new HashSet();
            hashSet.add(g.b(i2, d, d2));
            double d3 = ((double) this.e) * 1.414d;
            if (this.e > 0) {
                int i3 = 0;
                while (true) {
                    int i4 = i3;
                    if (i4 >= g.b.length) {
                        break;
                    }
                    double[] a = g.b(d2, d, d3, g.b[i4]);
                    hashSet.add(g.b(i2, a[1], a[0]));
                    i3 = i4 + 1;
                }
            }
            StringBuffer stringBuffer = new StringBuffer();
            Iterator it = hashSet.iterator();
            boolean z = true;
            while (it.hasNext()) {
                String str = (String) it.next();
                if (z) {
                    z = false;
                } else {
                    stringBuffer.append(',');
                }
                stringBuffer.append("\"").append(str).append("\"");
            }
            return String.format("SELECT * FROM %s WHERE gridkey IN (%s);", new Object[]{this.f, stringBuffer.toString()});
        }

        /* access modifiers changed from: private */
        public String a(JSONObject jSONObject) {
            Iterator<String> keys = jSONObject.keys();
            StringBuffer stringBuffer = new StringBuffer();
            while (keys.hasNext()) {
                String next = keys.next();
                if (stringBuffer.length() != 0) {
                    stringBuffer.append(",");
                }
                stringBuffer.append("\"").append(next).append("\"");
            }
            return String.format(Locale.US, "DELETE FROM %s WHERE gridkey IN (%s)", new Object[]{this.f, stringBuffer});
        }

        /* access modifiers changed from: private */
        public static void b(StringBuffer stringBuffer, String str, String str2, int i2) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(",");
            }
            stringBuffer.append("(\"").append(str).append("\",\"").append(str2).append("\",").append(i2).append(",").append(System.currentTimeMillis() / 86400000).append(")");
        }

        /* access modifiers changed from: package-private */
        public abstract List<String> a(JSONObject jSONObject, String str, int i2);
    }

    g(d dVar, SQLiteDatabase sQLiteDatabase, int i) {
        this.a = dVar;
        this.d = sQLiteDatabase;
        this.c = i;
        if (this.d != null && this.d.isOpen()) {
            try {
                this.d.execSQL("CREATE TABLE IF NOT EXISTS RGCAREA(gridkey VARCHAR(10) PRIMARY KEY, country VARCHAR(100),countrycode VARCHAR(100), province VARCHAR(100), city VARCHAR(100), citycode VARCHAR(100), district VARCHAR(100), timestamp INTEGER, version VARCHAR(50))");
                this.d.execSQL("CREATE TABLE IF NOT EXISTS RGCROAD(_id INTEGER PRIMARY KEY AUTOINCREMENT, gridkey VARCHAR(10), street VARCHAR(100), x1 DOUBLE, y1 DOUBLE, x2 DOUBLE, y2 DOUBLE)");
                this.d.execSQL("CREATE TABLE IF NOT EXISTS RGCSITE(_id INTEGER PRIMARY KEY AUTOINCREMENT, gridkey VARCHAR(10), street VARCHAR(100), streetnumber VARCHAR(100), x DOUBLE, y DOUBLE)");
                this.d.execSQL("CREATE TABLE IF NOT EXISTS RGCPOI(pid VARCHAR(50) PRIMARY KEY , gridkey VARCHAR(10), name VARCHAR(100), type VARCHAR(50), x DOUBLE, y DOUBLE, rank INTEGER)");
                this.d.execSQL("CREATE TABLE IF NOT EXISTS RGCUPDATE(gridkey VARCHAR(10), version VARCHAR(50), type INTEGER, timestamp INTEGER, PRIMARY KEY(gridkey, type))");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private double a(double d2, double d3, double d4, double d5, double d6, double d7) {
        double d8 = ((d6 - d4) * (d2 - d4)) + ((d7 - d5) * (d3 - d5));
        if (d8 <= 0.0d) {
            return Math.sqrt(((d2 - d4) * (d2 - d4)) + ((d3 - d5) * (d3 - d5)));
        }
        double d9 = ((d6 - d4) * (d6 - d4)) + ((d7 - d5) * (d7 - d5));
        if (d8 >= d9) {
            return Math.sqrt(((d2 - d6) * (d2 - d6)) + ((d3 - d7) * (d3 - d7)));
        }
        double d10 = d8 / d9;
        double d11 = ((d6 - d4) * d10) + d4;
        double d12 = (d10 * (d7 - d5)) + d5;
        return Math.sqrt(((d12 - d3) * (d12 - d3)) + ((d2 - d11) * (d2 - d11)));
    }

    private static int a(int i, int i2) {
        double d2;
        int i3;
        if (100 > i2) {
            d2 = -0.1d;
            i3 = 60000;
        } else if (500 > i2) {
            d2 = -0.75d;
            i3 = 55500;
        } else {
            d2 = -0.5d;
            i3 = 0;
        }
        return ((int) (((double) i3) + (d2 * ((double) i2)))) + i;
    }

    /* access modifiers changed from: private */
    public static String b(int i, double d2, double d3) {
        a aVar;
        double d4;
        int i2;
        int i3;
        int i4 = i * 5;
        char[] cArr = new char[(i + 1)];
        a aVar2 = new a(90.0d, -90.0d);
        a aVar3 = new a(180.0d, -180.0d);
        int i5 = 1;
        boolean z = true;
        int i6 = 0;
        while (i5 <= i4) {
            if (z) {
                aVar = aVar3;
                d4 = d2;
            } else {
                aVar = aVar2;
                d4 = d3;
            }
            double a2 = (aVar.b + aVar.a) / 2.0d;
            int i7 = i6 << 1;
            if (((int) (d4 * 1000000.0d)) > ((int) (1000000.0d * a2))) {
                double unused = aVar.b = a2;
                i2 = i7 | 1;
            } else {
                double unused2 = aVar.a = a2;
                i2 = i7;
            }
            if (i5 % 5 == 0) {
                cArr[(i5 / 5) - 1] = "0123456789bcdefghjkmnpqrstuvwxyz".charAt(i2);
                i3 = 0;
            } else {
                i3 = i2;
            }
            i5++;
            z = !z;
            i6 = i3;
        }
        cArr[i] = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i8 = 0; i8 < i; i8++) {
            stringBuffer.append(cArr[i8]);
        }
        return stringBuffer.toString();
    }

    /* access modifiers changed from: private */
    public static double[] b(double d2, double d3, double d4, double d5) {
        double radians = Math.toRadians(d2);
        double radians2 = Math.toRadians(d3);
        double radians3 = Math.toRadians(d5);
        double asin = Math.asin((Math.sin(radians) * Math.cos(d4 / 6378137.0d)) + (Math.cos(radians) * Math.sin(d4 / 6378137.0d) * Math.cos(radians3)));
        return new double[]{Math.toDegrees(asin), Math.toDegrees(Math.atan2(Math.sin(radians3) * Math.sin(d4 / 6378137.0d) * Math.cos(radians), Math.cos(d4 / 6378137.0d) - (Math.sin(radians) * Math.sin(asin))) + radians2)};
    }

    private double c(double d2, double d3, double d4, double d5) {
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

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x026e, code lost:
        r5 = null;
        r6 = null;
        r7 = null;
        r8 = null;
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x027d, code lost:
        r11 = r9;
        r9 = r8;
        r8 = r7;
        r7 = r6;
        r6 = r5;
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0287, code lost:
        r11 = r9;
        r9 = r8;
        r8 = r7;
        r7 = r6;
        r6 = r5;
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x02a6, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x02a7, code lost:
        r30 = r5;
        r5 = r4;
        r4 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x02ae, code lost:
        r5 = null;
        r6 = null;
        r7 = null;
        r8 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x02b8, code lost:
        r5 = null;
        r6 = null;
        r7 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x02c0, code lost:
        r5 = null;
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x02cb, code lost:
        r4 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02d6, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x02d7, code lost:
        r10 = r4;
        r4 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02e4, code lost:
        r5 = r14;
        r6 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02ec, code lost:
        r11 = r9;
        r9 = r8;
        r8 = r7;
        r7 = r6;
        r6 = r5;
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        r10.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01ea  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x025a A[SYNTHETIC, Splitter:B:115:0x025a] */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0269 A[SYNTHETIC, Splitter:B:123:0x0269] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x026d A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:65:0x014d] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x027a A[SYNTHETIC, Splitter:B:129:0x027a] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0298 A[SYNTHETIC, Splitter:B:137:0x0298] */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x02a6 A[ExcHandler: all (r5v23 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:67:0x0154] */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02cb A[ExcHandler: all (th java.lang.Throwable), Splitter:B:41:0x010d] */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x02d6 A[ExcHandler: all (r5v76 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:7:0x0035] */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x02ec  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0131 A[SYNTHETIC, Splitter:B:56:0x0131] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0160 A[Catch:{ Exception -> 0x026d, all -> 0x02a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01a5 A[SYNTHETIC, Splitter:B:90:0x01a5] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01aa  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.baidu.location.Address a(double r32, double r34) {
        /*
            r31 = this;
            r24 = 0
            r23 = 0
            r22 = 0
            r21 = 0
            r20 = 0
            r25 = 0
            r12 = 0
            r11 = 0
            r10 = 0
            com.baidu.location.e.g$b r4 = com.baidu.location.e.g.b.SITE     // Catch:{ Exception -> 0x011a, all -> 0x012e }
            r0 = r31
            int r5 = r0.c     // Catch:{ Exception -> 0x011a, all -> 0x012e }
            r6 = r32
            r8 = r34
            java.lang.String r4 = r4.a((int) r5, (double) r6, (double) r8)     // Catch:{ Exception -> 0x011a, all -> 0x012e }
            r0 = r31
            android.database.sqlite.SQLiteDatabase r5 = r0.d     // Catch:{ Exception -> 0x011a, all -> 0x012e }
            r6 = 0
            android.database.Cursor r4 = r5.rawQuery(r4, r6)     // Catch:{ Exception -> 0x011a, all -> 0x012e }
            boolean r5 = r4.moveToFirst()     // Catch:{ Exception -> 0x02db, all -> 0x02d6 }
            if (r5 == 0) goto L_0x0316
            r6 = 9218868437227405311(0x7fefffffffffffff, double:1.7976931348623157E308)
            r16 = r6
            r14 = r11
            r15 = r12
        L_0x0035:
            boolean r5 = r4.isAfterLast()     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            if (r5 != 0) goto L_0x0074
            r5 = 2
            java.lang.String r19 = r4.getString(r5)     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            r5 = 3
            java.lang.String r18 = r4.getString(r5)     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            r5 = 5
            double r10 = r4.getDouble(r5)     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            r5 = 4
            double r12 = r4.getDouble(r5)     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            r5 = r31
            r6 = r34
            r8 = r32
            double r8 = r5.c(r6, r8, r10, r12)     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            int r5 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r5 >= 0) goto L_0x0310
            com.baidu.location.e.g$b r5 = com.baidu.location.e.g.b.SITE     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            int r5 = r5.e     // Catch:{ Exception -> 0x02e3, all -> 0x02d6 }
            double r6 = (double) r5
            int r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r5 > 0) goto L_0x0310
            r5 = r18
            r6 = r19
        L_0x006c:
            r4.moveToNext()     // Catch:{ Exception -> 0x02e0, all -> 0x02d6 }
            r16 = r8
            r14 = r5
            r15 = r6
            goto L_0x0035
        L_0x0074:
            r5 = r14
            r6 = r15
        L_0x0076:
            if (r4 == 0) goto L_0x030b
            r4.close()     // Catch:{ Exception -> 0x0114 }
            r18 = r5
            r10 = r6
        L_0x007e:
            if (r18 != 0) goto L_0x013c
            r11 = 0
            com.baidu.location.e.g$b r4 = com.baidu.location.e.g.b.ROAD     // Catch:{ Exception -> 0x0255, all -> 0x0264 }
            r0 = r31
            int r5 = r0.c     // Catch:{ Exception -> 0x0255, all -> 0x0264 }
            r6 = r32
            r8 = r34
            java.lang.String r4 = r4.a((int) r5, (double) r6, (double) r8)     // Catch:{ Exception -> 0x0255, all -> 0x0264 }
            r0 = r31
            android.database.sqlite.SQLiteDatabase r5 = r0.d     // Catch:{ Exception -> 0x0255, all -> 0x0264 }
            r6 = 0
            android.database.Cursor r19 = r5.rawQuery(r4, r6)     // Catch:{ Exception -> 0x0255, all -> 0x0264 }
            boolean r4 = r19.moveToFirst()     // Catch:{ Exception -> 0x02cd, all -> 0x02cb }
            if (r4 == 0) goto L_0x0135
            r26 = 9218868437227405311(0x7fefffffffffffff, double:1.7976931348623157E308)
            java.lang.String r4 = "wgs842mc"
            r0 = r32
            r2 = r34
            double[] r29 = com.baidu.location.Jni.coorEncrypt(r0, r2, r4)     // Catch:{ Exception -> 0x02cd, all -> 0x02cb }
            r4 = r10
        L_0x00ae:
            boolean r5 = r19.isAfterLast()     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            if (r5 != 0) goto L_0x0136
            r5 = 2
            r0 = r19
            java.lang.String r28 = r0.getString(r5)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r5 = 3
            r0 = r19
            double r6 = r0.getDouble(r5)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r5 = 4
            r0 = r19
            double r8 = r0.getDouble(r5)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            java.lang.String r5 = "wgs842mc"
            double[] r5 = com.baidu.location.Jni.coorEncrypt(r6, r8, r5)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r6 = 5
            r0 = r19
            double r6 = r0.getDouble(r6)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r8 = 6
            r0 = r19
            double r8 = r0.getDouble(r8)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            java.lang.String r10 = "wgs842mc"
            double[] r16 = com.baidu.location.Jni.coorEncrypt(r6, r8, r10)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r6 = 0
            r6 = r29[r6]     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r8 = 1
            r8 = r29[r8]     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r10 = 0
            r10 = r5[r10]     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r12 = 1
            r12 = r5[r12]     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r5 = 0
            r14 = r16[r5]     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r5 = 1
            r16 = r16[r5]     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            r5 = r31
            double r6 = r5.a(r6, r8, r10, r12, r14, r16)     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            int r5 = (r6 > r26 ? 1 : (r6 == r26 ? 0 : -1))
            if (r5 >= 0) goto L_0x0306
            com.baidu.location.e.g$b r5 = com.baidu.location.e.g.b.ROAD     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            int r5 = r5.e     // Catch:{ Exception -> 0x02d2, all -> 0x02cb }
            double r8 = (double) r5
            int r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r5 > 0) goto L_0x0306
            r4 = r6
            r10 = r28
        L_0x010d:
            r19.moveToNext()     // Catch:{ Exception -> 0x02cd, all -> 0x02cb }
            r26 = r4
            r4 = r10
            goto L_0x00ae
        L_0x0114:
            r4 = move-exception
            r18 = r5
            r10 = r6
            goto L_0x007e
        L_0x011a:
            r4 = move-exception
            r4 = r10
            r5 = r11
            r6 = r12
        L_0x011e:
            if (r4 == 0) goto L_0x030b
            r4.close()     // Catch:{ Exception -> 0x0128 }
            r18 = r5
            r10 = r6
            goto L_0x007e
        L_0x0128:
            r4 = move-exception
            r18 = r5
            r10 = r6
            goto L_0x007e
        L_0x012e:
            r4 = move-exception
        L_0x012f:
            if (r10 == 0) goto L_0x0134
            r10.close()     // Catch:{ Exception -> 0x029c }
        L_0x0134:
            throw r4
        L_0x0135:
            r4 = r10
        L_0x0136:
            if (r19 == 0) goto L_0x0303
            r19.close()     // Catch:{ Exception -> 0x0251 }
            r10 = r4
        L_0x013c:
            com.baidu.location.e.g$b r4 = com.baidu.location.e.g.b.AREA
            r0 = r31
            int r5 = r0.c
            r6 = r32
            r8 = r34
            java.lang.String r5 = r4.a((int) r5, (double) r6, (double) r8)
            r4 = 0
            r0 = r31
            android.database.sqlite.SQLiteDatabase r6 = r0.d     // Catch:{ Exception -> 0x026d, all -> 0x0290 }
            r7 = 0
            android.database.Cursor r4 = r6.rawQuery(r5, r7)     // Catch:{ Exception -> 0x026d, all -> 0x0290 }
            boolean r5 = r4.moveToFirst()     // Catch:{ Exception -> 0x026d, all -> 0x02a6 }
            if (r5 == 0) goto L_0x02f5
            boolean r5 = r4.isAfterLast()     // Catch:{ Exception -> 0x026d, all -> 0x02a6 }
            if (r5 != 0) goto L_0x02f5
            java.lang.String r5 = "country"
            int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x026d, all -> 0x02a6 }
            java.lang.String r9 = r4.getString(r5)     // Catch:{ Exception -> 0x026d, all -> 0x02a6 }
            java.lang.String r5 = "countrycode"
            int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x02ad, all -> 0x02a6 }
            java.lang.String r8 = r4.getString(r5)     // Catch:{ Exception -> 0x02ad, all -> 0x02a6 }
            java.lang.String r5 = "province"
            int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x02b7, all -> 0x02a6 }
            java.lang.String r7 = r4.getString(r5)     // Catch:{ Exception -> 0x02b7, all -> 0x02a6 }
            java.lang.String r5 = "city"
            int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x02bf, all -> 0x02a6 }
            java.lang.String r6 = r4.getString(r5)     // Catch:{ Exception -> 0x02bf, all -> 0x02a6 }
            java.lang.String r5 = "citycode"
            int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x02c5, all -> 0x02a6 }
            java.lang.String r5 = r4.getString(r5)     // Catch:{ Exception -> 0x02c5, all -> 0x02a6 }
            java.lang.String r11 = "district"
            int r11 = r4.getColumnIndex(r11)     // Catch:{ Exception -> 0x02c9, all -> 0x02a6 }
            java.lang.String r25 = r4.getString(r11)     // Catch:{ Exception -> 0x02c9, all -> 0x02a6 }
            r11 = r9
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r25
        L_0x01a3:
            if (r4 == 0) goto L_0x01a8
            r4.close()     // Catch:{ Exception -> 0x02a1 }
        L_0x01a8:
            if (r11 == 0) goto L_0x01b8
            java.lang.String r4 = new java.lang.String
            byte[] r11 = r11.getBytes()
            byte[] r11 = com.baidu.location.b.a.b.a(r11)
            r4.<init>(r11)
            r11 = r4
        L_0x01b8:
            if (r9 == 0) goto L_0x01c8
            java.lang.String r4 = new java.lang.String
            byte[] r9 = r9.getBytes()
            byte[] r9 = com.baidu.location.b.a.b.a(r9)
            r4.<init>(r9)
            r9 = r4
        L_0x01c8:
            if (r8 == 0) goto L_0x01d8
            java.lang.String r4 = new java.lang.String
            byte[] r8 = r8.getBytes()
            byte[] r8 = com.baidu.location.b.a.b.a(r8)
            r4.<init>(r8)
            r8 = r4
        L_0x01d8:
            if (r7 == 0) goto L_0x01e8
            java.lang.String r4 = new java.lang.String
            byte[] r7 = r7.getBytes()
            byte[] r7 = com.baidu.location.b.a.b.a(r7)
            r4.<init>(r7)
            r7 = r4
        L_0x01e8:
            if (r6 == 0) goto L_0x01f8
            java.lang.String r4 = new java.lang.String
            byte[] r6 = r6.getBytes()
            byte[] r6 = com.baidu.location.b.a.b.a(r6)
            r4.<init>(r6)
            r6 = r4
        L_0x01f8:
            if (r5 == 0) goto L_0x0208
            java.lang.String r4 = new java.lang.String
            byte[] r5 = r5.getBytes()
            byte[] r5 = com.baidu.location.b.a.b.a(r5)
            r4.<init>(r5)
            r5 = r4
        L_0x0208:
            if (r10 == 0) goto L_0x0218
            java.lang.String r4 = new java.lang.String
            byte[] r10 = r10.getBytes()
            byte[] r10 = com.baidu.location.b.a.b.a(r10)
            r4.<init>(r10)
            r10 = r4
        L_0x0218:
            if (r18 == 0) goto L_0x02e8
            java.lang.String r4 = new java.lang.String
            byte[] r12 = r18.getBytes()
            byte[] r12 = com.baidu.location.b.a.b.a(r12)
            r4.<init>(r12)
        L_0x0227:
            com.baidu.location.Address$Builder r12 = new com.baidu.location.Address$Builder
            r12.<init>()
            com.baidu.location.Address$Builder r11 = r12.country(r11)
            com.baidu.location.Address$Builder r9 = r11.countryCode(r9)
            com.baidu.location.Address$Builder r8 = r9.province(r8)
            com.baidu.location.Address$Builder r7 = r8.city(r7)
            com.baidu.location.Address$Builder r6 = r7.cityCode(r6)
            com.baidu.location.Address$Builder r5 = r6.district(r5)
            com.baidu.location.Address$Builder r5 = r5.street(r10)
            com.baidu.location.Address$Builder r4 = r5.streetNumber(r4)
            com.baidu.location.Address r4 = r4.build()
            return r4
        L_0x0251:
            r5 = move-exception
            r10 = r4
            goto L_0x013c
        L_0x0255:
            r4 = move-exception
            r5 = r11
            r4 = r10
        L_0x0258:
            if (r5 == 0) goto L_0x0303
            r5.close()     // Catch:{ Exception -> 0x0260 }
            r10 = r4
            goto L_0x013c
        L_0x0260:
            r5 = move-exception
            r10 = r4
            goto L_0x013c
        L_0x0264:
            r4 = move-exception
            r19 = r11
        L_0x0267:
            if (r19 == 0) goto L_0x026c
            r19.close()     // Catch:{ Exception -> 0x029f }
        L_0x026c:
            throw r4
        L_0x026d:
            r5 = move-exception
            r5 = r20
            r6 = r21
            r7 = r22
            r8 = r23
            r9 = r24
        L_0x0278:
            if (r4 == 0) goto L_0x02ec
            r4.close()     // Catch:{ Exception -> 0x0286 }
            r11 = r9
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r25
            goto L_0x01a8
        L_0x0286:
            r4 = move-exception
            r11 = r9
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r25
            goto L_0x01a8
        L_0x0290:
            r5 = move-exception
            r30 = r5
            r5 = r4
            r4 = r30
        L_0x0296:
            if (r5 == 0) goto L_0x029b
            r5.close()     // Catch:{ Exception -> 0x02a4 }
        L_0x029b:
            throw r4
        L_0x029c:
            r5 = move-exception
            goto L_0x0134
        L_0x029f:
            r5 = move-exception
            goto L_0x026c
        L_0x02a1:
            r4 = move-exception
            goto L_0x01a8
        L_0x02a4:
            r5 = move-exception
            goto L_0x029b
        L_0x02a6:
            r5 = move-exception
            r30 = r5
            r5 = r4
            r4 = r30
            goto L_0x0296
        L_0x02ad:
            r5 = move-exception
            r5 = r20
            r6 = r21
            r7 = r22
            r8 = r23
            goto L_0x0278
        L_0x02b7:
            r5 = move-exception
            r5 = r20
            r6 = r21
            r7 = r22
            goto L_0x0278
        L_0x02bf:
            r5 = move-exception
            r5 = r20
            r6 = r21
            goto L_0x0278
        L_0x02c5:
            r5 = move-exception
            r5 = r20
            goto L_0x0278
        L_0x02c9:
            r11 = move-exception
            goto L_0x0278
        L_0x02cb:
            r4 = move-exception
            goto L_0x0267
        L_0x02cd:
            r4 = move-exception
            r5 = r19
            r4 = r10
            goto L_0x0258
        L_0x02d2:
            r5 = move-exception
            r5 = r19
            goto L_0x0258
        L_0x02d6:
            r5 = move-exception
            r10 = r4
            r4 = r5
            goto L_0x012f
        L_0x02db:
            r5 = move-exception
            r5 = r11
            r6 = r12
            goto L_0x011e
        L_0x02e0:
            r7 = move-exception
            goto L_0x011e
        L_0x02e3:
            r5 = move-exception
            r5 = r14
            r6 = r15
            goto L_0x011e
        L_0x02e8:
            r4 = r18
            goto L_0x0227
        L_0x02ec:
            r11 = r9
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r25
            goto L_0x01a8
        L_0x02f5:
            r5 = r25
            r6 = r20
            r7 = r21
            r8 = r22
            r9 = r23
            r11 = r24
            goto L_0x01a3
        L_0x0303:
            r10 = r4
            goto L_0x013c
        L_0x0306:
            r10 = r4
            r4 = r26
            goto L_0x010d
        L_0x030b:
            r18 = r5
            r10 = r6
            goto L_0x007e
        L_0x0310:
            r8 = r16
            r5 = r14
            r6 = r15
            goto L_0x006c
        L_0x0316:
            r5 = r11
            r6 = r12
            goto L_0x0076
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.g.a(double, double):com.baidu.location.Address");
    }

    /* access modifiers changed from: package-private */
    public void a(JSONObject jSONObject) {
        if (this.d != null && this.d.isOpen()) {
            try {
                this.d.beginTransaction();
                for (b bVar : b.values()) {
                    if (jSONObject.has(bVar.g)) {
                        String str = "";
                        if (jSONObject.has(bVar.h)) {
                            str = jSONObject.getString(bVar.h);
                        }
                        ArrayList<String> arrayList = new ArrayList<>();
                        JSONObject jSONObject2 = jSONObject.getJSONObject(bVar.g);
                        arrayList.add(bVar.a(jSONObject2));
                        arrayList.addAll(bVar.a(jSONObject2, str, bVar.i));
                        for (String execSQL : arrayList) {
                            this.d.execSQL(execSQL);
                        }
                    }
                }
                this.d.setTransactionSuccessful();
                this.e = -1;
                this.f = -1;
                try {
                    this.d.endTransaction();
                } catch (Exception e2) {
                }
            } catch (Exception e3) {
                try {
                    this.d.endTransaction();
                } catch (Exception e4) {
                }
            } catch (Throwable th) {
                try {
                    this.d.endTransaction();
                } catch (Exception e5) {
                }
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x005b A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0070 A[SYNTHETIC, Splitter:B:37:0x0070] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0075 A[SYNTHETIC, Splitter:B:40:0x0075] */
    /* JADX WARNING: Removed duplicated region for block: B:51:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a() {
        /*
            r6 = this;
            r3 = -1
            r0 = 0
            r1 = 0
            com.baidu.location.e.d r2 = r6.a
            com.baidu.location.e.c r2 = r2.l()
            boolean r2 = r2.l()
            if (r2 == 0) goto L_0x0053
            int r2 = r6.f
            if (r2 != r3) goto L_0x0053
            int r2 = r6.e
            if (r2 != r3) goto L_0x0053
            android.database.sqlite.SQLiteDatabase r2 = r6.d
            if (r2 == 0) goto L_0x0053
            android.database.sqlite.SQLiteDatabase r2 = r6.d
            boolean r2 = r2.isOpen()
            if (r2 == 0) goto L_0x0053
            android.database.sqlite.SQLiteDatabase r2 = r6.d     // Catch:{ Exception -> 0x005d, all -> 0x006c }
            java.lang.String r3 = "SELECT COUNT(*) FROM RGCSITE;"
            r4 = 0
            android.database.Cursor r2 = r2.rawQuery(r3, r4)     // Catch:{ Exception -> 0x005d, all -> 0x006c }
            r2.moveToFirst()     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            r3 = 0
            int r3 = r2.getInt(r3)     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            r6.f = r3     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            android.database.sqlite.SQLiteDatabase r3 = r6.d     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            java.lang.String r4 = "SELECT COUNT(*) FROM RGCAREA;"
            r5 = 0
            android.database.Cursor r1 = r3.rawQuery(r4, r5)     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            r1.moveToFirst()     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            r3 = 0
            int r3 = r1.getInt(r3)     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            r6.e = r3     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            if (r2 == 0) goto L_0x004e
            r2.close()     // Catch:{ Exception -> 0x0079 }
        L_0x004e:
            if (r1 == 0) goto L_0x0053
            r1.close()     // Catch:{ Exception -> 0x007b }
        L_0x0053:
            int r1 = r6.f
            if (r1 != 0) goto L_0x005c
            int r1 = r6.e
            if (r1 != 0) goto L_0x005c
            r0 = 1
        L_0x005c:
            return r0
        L_0x005d:
            r2 = move-exception
            r2 = r1
        L_0x005f:
            if (r2 == 0) goto L_0x0064
            r2.close()     // Catch:{ Exception -> 0x007d }
        L_0x0064:
            if (r1 == 0) goto L_0x0053
            r1.close()     // Catch:{ Exception -> 0x006a }
            goto L_0x0053
        L_0x006a:
            r1 = move-exception
            goto L_0x0053
        L_0x006c:
            r0 = move-exception
            r2 = r1
        L_0x006e:
            if (r2 == 0) goto L_0x0073
            r2.close()     // Catch:{ Exception -> 0x007f }
        L_0x0073:
            if (r1 == 0) goto L_0x0078
            r1.close()     // Catch:{ Exception -> 0x0081 }
        L_0x0078:
            throw r0
        L_0x0079:
            r2 = move-exception
            goto L_0x004e
        L_0x007b:
            r1 = move-exception
            goto L_0x0053
        L_0x007d:
            r2 = move-exception
            goto L_0x0064
        L_0x007f:
            r2 = move-exception
            goto L_0x0073
        L_0x0081:
            r1 = move-exception
            goto L_0x0078
        L_0x0083:
            r0 = move-exception
            goto L_0x006e
        L_0x0085:
            r3 = move-exception
            goto L_0x005f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.g.a():boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a7 A[SYNTHETIC, Splitter:B:30:0x00a7] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.baidu.location.Poi> b(double r20, double r22) {
        /*
            r19 = this;
            r8 = 0
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            com.baidu.location.e.g$b r2 = com.baidu.location.e.g.b.POI
            r0 = r19
            int r3 = r0.c
            r4 = r20
            r6 = r22
            java.lang.String r4 = r2.a((int) r3, (double) r4, (double) r6)
            r2 = 0
            r3 = 0
            r0 = r19
            android.database.sqlite.SQLiteDatabase r5 = r0.d     // Catch:{ Exception -> 0x0099, all -> 0x00a3 }
            r6 = 0
            android.database.Cursor r12 = r5.rawQuery(r4, r6)     // Catch:{ Exception -> 0x0099, all -> 0x00a3 }
            boolean r4 = r12.moveToFirst()     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            if (r4 == 0) goto L_0x008e
            r13 = r3
        L_0x0026:
            boolean r3 = r12.isAfterLast()     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            if (r3 != 0) goto L_0x008e
            r3 = 0
            java.lang.String r15 = r12.getString(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r3 = 2
            java.lang.String r16 = r12.getString(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r3 = 4
            double r10 = r12.getDouble(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r3 = 5
            double r8 = r12.getDouble(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r3 = 6
            int r17 = r12.getInt(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r3 = r19
            r4 = r22
            r6 = r20
            double r6 = r3.c(r4, r6, r8, r10)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            com.baidu.location.e.g$b r3 = com.baidu.location.e.g.b.POI     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            int r3 = r3.e     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            double r4 = (double) r3     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            int r3 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x00b4
            com.baidu.location.Poi r4 = new com.baidu.location.Poi     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            java.lang.String r3 = new java.lang.String     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            byte[] r5 = r15.getBytes()     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            byte[] r5 = com.baidu.location.b.a.b.a(r5)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r3.<init>(r5)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            java.lang.String r5 = new java.lang.String     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            byte[] r8 = r16.getBytes()     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            byte[] r8 = com.baidu.location.b.a.b.a(r8)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r5.<init>(r8)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r4.<init>(r3, r5, r8)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            float r3 = (float) r6     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            int r3 = java.lang.Math.round(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r0 = r17
            int r3 = a((int) r0, (int) r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            if (r3 <= r13) goto L_0x00b4
            r2 = r4
        L_0x0089:
            r12.moveToNext()     // Catch:{ Exception -> 0x00b1, all -> 0x00af }
            r13 = r3
            goto L_0x0026
        L_0x008e:
            if (r12 == 0) goto L_0x0093
            r12.close()     // Catch:{ Exception -> 0x00ab }
        L_0x0093:
            if (r2 == 0) goto L_0x0098
            r14.add(r2)
        L_0x0098:
            return r14
        L_0x0099:
            r3 = move-exception
            r3 = r8
        L_0x009b:
            if (r3 == 0) goto L_0x0093
            r3.close()     // Catch:{ Exception -> 0x00a1 }
            goto L_0x0093
        L_0x00a1:
            r3 = move-exception
            goto L_0x0093
        L_0x00a3:
            r2 = move-exception
            r12 = r8
        L_0x00a5:
            if (r12 == 0) goto L_0x00aa
            r12.close()     // Catch:{ Exception -> 0x00ad }
        L_0x00aa:
            throw r2
        L_0x00ab:
            r3 = move-exception
            goto L_0x0093
        L_0x00ad:
            r3 = move-exception
            goto L_0x00aa
        L_0x00af:
            r2 = move-exception
            goto L_0x00a5
        L_0x00b1:
            r3 = move-exception
            r3 = r12
            goto L_0x009b
        L_0x00b4:
            r3 = r13
            goto L_0x0089
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.g.b(double, double):java.util.List");
    }

    /* access modifiers changed from: package-private */
    public JSONObject b() {
        Cursor cursor = null;
        Cursor cursor2 = null;
        JSONObject jSONObject = new JSONObject();
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        int currentTimeMillis = (int) (System.currentTimeMillis() / 86400000);
        try {
            if (this.d != null && this.d.isOpen()) {
                JSONArray jSONArray = new JSONArray();
                JSONArray jSONArray2 = new JSONArray();
                JSONArray jSONArray3 = new JSONArray();
                JSONArray jSONArray4 = new JSONArray();
                cursor2 = this.d.rawQuery(String.format("SELECT * FROM RGCUPDATE WHERE type=%d AND %d > timestamp+%d ORDER BY gridkey", new Object[]{0, Integer.valueOf(currentTimeMillis), Integer.valueOf(this.a.l().p())}), (String[]) null);
                cursor = this.d.rawQuery(String.format("SELECT * FROM RGCUPDATE WHERE type=%d AND %d > timestamp+%d ORDER BY gridkey", new Object[]{1, Integer.valueOf(currentTimeMillis), Integer.valueOf(this.a.l().q())}), (String[]) null);
                if (cursor2.moveToFirst()) {
                    HashSet hashSet = new HashSet();
                    while (!cursor2.isAfterLast()) {
                        String string = cursor2.getString(0);
                        String string2 = cursor2.getString(1);
                        jSONArray3.put(string);
                        hashSet.add(string2);
                        if (stringBuffer2.length() > 0) {
                            stringBuffer2.append(",");
                        }
                        stringBuffer2.append("\"").append(string).append("\"");
                        cursor2.moveToNext();
                    }
                    String[] strArr = new String[hashSet.size()];
                    hashSet.toArray(strArr);
                    for (String put : strArr) {
                        jSONArray4.put(put);
                    }
                }
                if (cursor.moveToFirst()) {
                    HashSet hashSet2 = new HashSet();
                    while (!cursor.isAfterLast()) {
                        String string3 = cursor.getString(0);
                        String string4 = cursor.getString(1);
                        jSONArray.put(string3);
                        hashSet2.add(string4);
                        if (stringBuffer.length() > 0) {
                            stringBuffer.append(",");
                        }
                        stringBuffer.append("\"").append(string3).append("\"");
                        cursor.moveToNext();
                    }
                    String[] strArr2 = new String[hashSet2.size()];
                    hashSet2.toArray(strArr2);
                    for (String put2 : strArr2) {
                        jSONArray2.put(put2);
                    }
                }
                if (jSONArray3.length() != 0) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("gk", jSONArray3);
                    jSONObject2.put("ver", jSONArray4);
                    jSONObject.put("addr", jSONObject2);
                }
                if (jSONArray.length() != 0) {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("gk", jSONArray);
                    jSONObject3.put("ver", jSONArray2);
                    jSONObject.put("poi", jSONObject3);
                }
            }
            if (stringBuffer2.length() > 0) {
                this.d.execSQL(String.format(Locale.US, "UPDATE RGCUPDATE SET timestamp=timestamp+1 WHERE type = %d AND gridkey IN (%s)", new Object[]{0, stringBuffer2.toString()}));
            }
            if (stringBuffer.length() > 0) {
                this.d.execSQL(String.format(Locale.US, "UPDATE RGCUPDATE SET timestamp=timestamp+1 WHERE type = %d AND gridkey IN (%s)", new Object[]{1, stringBuffer.toString()}));
            }
            if (cursor2 != null) {
                try {
                    cursor2.close();
                } catch (Exception e2) {
                }
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
            if (cursor2 != null) {
                try {
                    cursor2.close();
                } catch (Exception e5) {
                }
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e6) {
                }
            }
        } catch (Throwable th) {
            if (cursor2 != null) {
                try {
                    cursor2.close();
                } catch (Exception e7) {
                }
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e8) {
                }
            }
            throw th;
        }
        if (jSONObject.has("poi") || jSONObject.has("addr")) {
            return jSONObject;
        }
        return null;
    }
}
