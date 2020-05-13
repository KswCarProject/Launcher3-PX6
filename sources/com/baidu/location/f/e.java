package com.baidu.location.f;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.baidu.location.Jni;
import com.baidu.location.a.h;
import com.baidu.location.a.j;
import com.baidu.location.f;
import com.baidu.location.h.i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.HttpStatus;

public class e extends g {
    private static e c = null;
    /* access modifiers changed from: private */
    public static int k = 0;
    /* access modifiers changed from: private */
    public static String r = null;
    private final long a = 1000;
    private final long b = 9000;
    private Context d;
    /* access modifiers changed from: private */
    public LocationManager e = null;
    private Location f;
    private b g = null;
    private c h = null;
    /* access modifiers changed from: private */
    public GpsStatus i;
    /* access modifiers changed from: private */
    public a j = null;
    /* access modifiers changed from: private */
    public long l = 0;
    /* access modifiers changed from: private */
    public boolean m = false;
    /* access modifiers changed from: private */
    public boolean n = false;
    private String o = null;
    private boolean p = false;
    /* access modifiers changed from: private */
    public long q = 0;
    /* access modifiers changed from: private */
    public Handler s = null;
    private final int t = 1;
    private final int u = 2;
    private final int v = 3;
    private final int w = 4;
    /* access modifiers changed from: private */
    public int x;
    /* access modifiers changed from: private */
    public int y;
    /* access modifiers changed from: private */
    public HashMap<Integer, List<GpsSatellite>> z;

    private class a implements GpsStatus.Listener, GpsStatus.NmeaListener {
        long a;
        private long c;
        private final int d;
        private boolean e;
        private List<String> f;
        private String g;
        private String h;
        private String i;

        private a() {
            this.a = 0;
            this.c = 0;
            this.d = HttpStatus.SC_BAD_REQUEST;
            this.e = false;
            this.f = new ArrayList();
            this.g = null;
            this.h = null;
            this.i = null;
        }

        public void a(String str) {
            if (System.currentTimeMillis() - this.c > 400 && this.e && this.f.size() > 0) {
                try {
                    h hVar = new h(this.f, this.g, this.h, this.i);
                    if (hVar.a()) {
                        i.d = e.this.a(hVar, e.this.y);
                        if (i.d > 0) {
                            String unused = e.r = String.format(Locale.CHINA, "&nmea=%.1f|%.1f&g_tp=%d", new Object[]{Double.valueOf(hVar.c()), Double.valueOf(hVar.b()), Integer.valueOf(i.d)});
                        }
                    } else {
                        i.d = 0;
                    }
                } catch (Exception e2) {
                    i.d = 0;
                }
                this.f.clear();
                this.i = null;
                this.h = null;
                this.g = null;
                this.e = false;
            }
            if (str.startsWith("$GPGGA")) {
                this.e = true;
                this.g = str.trim();
            } else if (str.startsWith("$GPGSV")) {
                this.f.add(str.trim());
            } else if (str.startsWith("$GPGSA")) {
                this.i = str.trim();
            }
            this.c = System.currentTimeMillis();
        }

        public void onGpsStatusChanged(int i2) {
            if (e.this.e != null) {
                switch (i2) {
                    case 2:
                        e.this.d((Location) null);
                        e.this.b(false);
                        int unused = e.k = 0;
                        return;
                    case 4:
                        if (e.this.n) {
                            try {
                                if (e.this.i == null) {
                                    GpsStatus unused2 = e.this.i = e.this.e.getGpsStatus((GpsStatus) null);
                                } else {
                                    e.this.e.getGpsStatus(e.this.i);
                                }
                                int unused3 = e.this.x = 0;
                                int unused4 = e.this.y = 0;
                                HashMap unused5 = e.this.z = new HashMap();
                                int i3 = 0;
                                for (GpsSatellite next : e.this.i.getSatellites()) {
                                    if (next.usedInFix()) {
                                        i3++;
                                        if (next.getSnr() >= ((float) i.E)) {
                                            e.f(e.this);
                                        }
                                        String unused6 = e.this.a(next, (HashMap<Integer, List<GpsSatellite>>) e.this.z);
                                    }
                                }
                                int unused7 = e.k = i3;
                                return;
                            } catch (Exception e2) {
                                return;
                            }
                        } else {
                            return;
                        }
                    default:
                        return;
                }
            }
        }

        public void onNmeaReceived(long j, String str) {
            if (e.this.n) {
                if (!com.baidu.location.c.c.a().g) {
                    i.d = 0;
                } else if (str != null && !str.equals("") && str.length() >= 9 && str.length() <= 150 && e.this.i()) {
                    e.this.s.sendMessage(e.this.s.obtainMessage(2, str));
                }
            }
        }
    }

    private class b implements LocationListener {
        private b() {
        }

        public void onLocationChanged(Location location) {
            long unused = e.this.q = System.currentTimeMillis();
            e.this.b(true);
            e.this.d(location);
            boolean unused2 = e.this.m = false;
        }

        public void onProviderDisabled(String str) {
            e.this.d((Location) null);
            e.this.b(false);
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
            switch (i) {
                case 0:
                    e.this.d((Location) null);
                    e.this.b(false);
                    return;
                case 1:
                    long unused = e.this.l = System.currentTimeMillis();
                    boolean unused2 = e.this.m = true;
                    e.this.b(false);
                    return;
                case 2:
                    boolean unused3 = e.this.m = false;
                    return;
                default:
                    return;
            }
        }
    }

    private class c implements LocationListener {
        private long b;

        private c() {
            this.b = 0;
        }

        public void onLocationChanged(Location location) {
            if (!e.this.n && location != null && location.getProvider() == "gps" && System.currentTimeMillis() - this.b >= 10000 && j.a(location, false)) {
                this.b = System.currentTimeMillis();
                e.this.s.sendMessage(e.this.s.obtainMessage(4, location));
            }
        }

        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }
    }

    private e() {
    }

    /* access modifiers changed from: private */
    public int a(h hVar, int i2) {
        if (k >= i.B) {
            return 1;
        }
        if (k <= i.A) {
            return 4;
        }
        double c2 = hVar.c();
        if (c2 <= ((double) i.w)) {
            return 1;
        }
        if (c2 >= ((double) i.x)) {
            return 4;
        }
        double b2 = hVar.b();
        if (b2 <= ((double) i.y)) {
            return 1;
        }
        if (b2 >= ((double) i.z)) {
            return 4;
        }
        if (i2 >= i.D) {
            return 1;
        }
        if (i2 <= i.C) {
            return 4;
        }
        if (this.z != null) {
            return a(this.z);
        }
        return 3;
    }

    private int a(HashMap<Integer, List<GpsSatellite>> hashMap) {
        int i2;
        double[] a2;
        if (this.x > 4) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i3 = 0;
            for (Map.Entry<Integer, List<GpsSatellite>> value : hashMap.entrySet()) {
                List list = (List) value.getValue();
                if (list == null || (a2 = a((List<GpsSatellite>) list)) == null) {
                    i2 = i3;
                } else {
                    arrayList.add(a2);
                    i2 = i3 + 1;
                    arrayList2.add(Integer.valueOf(i3));
                }
                i3 = i2;
            }
            if (!arrayList.isEmpty()) {
                double[] dArr = new double[2];
                int size = arrayList.size();
                for (int i4 = 0; i4 < size; i4++) {
                    double[] dArr2 = (double[]) arrayList.get(i4);
                    int intValue = ((Integer) arrayList2.get(i4)).intValue();
                    dArr2[0] = dArr2[0] * ((double) intValue);
                    dArr2[1] = dArr2[1] * ((double) intValue);
                    dArr[0] = dArr[0] + dArr2[0];
                    dArr[1] = dArr2[1] + dArr[1];
                }
                dArr[0] = dArr[0] / ((double) size);
                dArr[1] = dArr[1] / ((double) size);
                double[] b2 = b(dArr[0], dArr[1]);
                if (b2[0] <= ((double) i.F)) {
                    return 1;
                }
                if (b2[0] >= ((double) i.G)) {
                    return 4;
                }
            }
        }
        return 3;
    }

    public static synchronized e a() {
        e eVar;
        synchronized (e.class) {
            if (c == null) {
                c = new e();
            }
            eVar = c;
        }
        return eVar;
    }

    /* access modifiers changed from: private */
    public String a(GpsSatellite gpsSatellite, HashMap<Integer, List<GpsSatellite>> hashMap) {
        int floor = (int) Math.floor((double) (gpsSatellite.getAzimuth() / 6.0f));
        float elevation = gpsSatellite.getElevation();
        int floor2 = (int) Math.floor(((double) elevation) / 1.5d);
        float snr = gpsSatellite.getSnr();
        int round = Math.round(snr / 5.0f);
        int prn = gpsSatellite.getPrn();
        int i2 = prn >= 65 ? prn - 32 : prn;
        if (snr >= 10.0f && elevation >= 1.0f) {
            List list = hashMap.get(Integer.valueOf(round));
            if (list == null) {
                list = new ArrayList();
            }
            list.add(gpsSatellite);
            hashMap.put(Integer.valueOf(round), list);
            this.x++;
        }
        if (floor >= 64) {
        }
        if (floor2 >= 64) {
        }
        if (i2 >= 65) {
        }
        return null;
    }

    public static String a(Location location) {
        float f2 = -1.0f;
        if (location == null) {
            return null;
        }
        float speed = (float) (((double) location.getSpeed()) * 3.6d);
        if (!location.hasSpeed()) {
            speed = -1.0f;
        }
        int accuracy = (int) (location.hasAccuracy() ? location.getAccuracy() : -1.0f);
        double altitude = location.hasAltitude() ? location.getAltitude() : 555.0d;
        if (location.hasBearing()) {
            f2 = location.getBearing();
        }
        return String.format(Locale.CHINA, "&ll=%.5f|%.5f&s=%.1f&d=%.1f&ll_r=%d&ll_n=%d&ll_h=%.2f&ll_t=%d", new Object[]{Double.valueOf(location.getLongitude()), Double.valueOf(location.getLatitude()), Float.valueOf(speed), Float.valueOf(f2), Integer.valueOf(accuracy), Integer.valueOf(k), Double.valueOf(altitude), Long.valueOf(location.getTime() / 1000)});
    }

    private void a(double d2, double d3, float f2) {
        int i2 = 0;
        if (com.baidu.location.c.c.a().f) {
            if (d2 >= 73.146973d && d2 <= 135.252686d && d3 <= 54.258807d && d3 >= 14.604847d && f2 <= 18.0f) {
                int i3 = (int) ((d2 - i.q) * 1000.0d);
                int i4 = (int) ((i.r - d3) * 1000.0d);
                if (i3 <= 0 || i3 >= 50 || i4 <= 0 || i4 >= 50) {
                    i.o = d2;
                    i.p = d3;
                    com.baidu.location.c.c.a().a(String.format(Locale.CHINA, "&ll=%.5f|%.5f", new Object[]{Double.valueOf(d2), Double.valueOf(d3)}) + "&im=" + com.baidu.location.h.c.a().b());
                } else {
                    int i5 = i3 + (i4 * 50);
                    int i6 = i5 >> 2;
                    int i7 = i5 & 3;
                    if (i.u) {
                        i2 = (i.t[i6] >> (i7 * 2)) & 3;
                    }
                }
            }
            if (i.s != i2) {
                i.s = i2;
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(String str, Location location) {
        if (location != null) {
            String str2 = str + com.baidu.location.a.a.a().c();
            boolean d2 = j.a().d();
            h.a(new a(b.a().f()));
            h.a(System.currentTimeMillis());
            h.a(new Location(location));
            h.a(str2);
            if (!d2) {
                j.a(h.c(), (i) null, h.d(), str2);
            }
        }
    }

    public static boolean a(Location location, Location location2, boolean z2) {
        if (location == location2) {
            return false;
        }
        if (location == null || location2 == null) {
            return true;
        }
        float speed = location2.getSpeed();
        if (z2 && ((i.s == 3 || !com.baidu.location.h.e.a().a(location2.getLongitude(), location2.getLatitude())) && speed < 5.0f)) {
            return true;
        }
        float distanceTo = location2.distanceTo(location);
        return speed > i.I ? distanceTo > i.K : speed > i.H ? distanceTo > i.J : distanceTo > 5.0f;
    }

    private double[] a(double d2, double d3) {
        return new double[]{Math.sin(Math.toRadians(d3)) * d2, Math.cos(Math.toRadians(d3)) * d2};
    }

    private double[] a(List<GpsSatellite> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        double[] dArr = new double[2];
        for (GpsSatellite next : list) {
            if (next != null) {
                double[] a2 = a((double) (90.0f - next.getElevation()), (double) next.getAzimuth());
                dArr[0] = dArr[0] + a2[0];
                dArr[1] = dArr[1] + a2[1];
            }
        }
        int size = list.size();
        dArr[0] = dArr[0] / ((double) size);
        dArr[1] = dArr[1] / ((double) size);
        return dArr;
    }

    public static String b(Location location) {
        String a2 = a(location);
        return a2 != null ? a2 + "&g_tp=0" : a2;
    }

    /* access modifiers changed from: private */
    public void b(boolean z2) {
        this.p = z2;
        if (!z2 || !i()) {
        }
    }

    private double[] b(double d2, double d3) {
        double d4 = 0.0d;
        if (d3 != 0.0d) {
            d4 = Math.toDegrees(Math.atan(d2 / d3));
        } else if (d2 > 0.0d) {
            d4 = 90.0d;
        } else if (d2 < 0.0d) {
            d4 = 270.0d;
        }
        return new double[]{Math.sqrt((d2 * d2) + (d3 * d3)), d4};
    }

    public static String c(Location location) {
        String a2 = a(location);
        return a2 != null ? a2 + r : a2;
    }

    /* access modifiers changed from: private */
    public void d(Location location) {
        this.s.sendMessage(this.s.obtainMessage(1, location));
    }

    /* access modifiers changed from: private */
    public void e(Location location) {
        if (location != null) {
            int i2 = k;
            if (i2 == 0) {
                try {
                    i2 = location.getExtras().getInt("satellites");
                } catch (Exception e2) {
                }
            }
            if (i2 != 0 || i.k) {
                this.f = location;
                if (this.f == null) {
                    this.o = null;
                } else {
                    long currentTimeMillis = System.currentTimeMillis();
                    this.f.setTime(currentTimeMillis);
                    float speed = (float) (((double) this.f.getSpeed()) * 3.6d);
                    if (!this.f.hasSpeed()) {
                        speed = -1.0f;
                    }
                    int i3 = k;
                    if (i3 == 0) {
                        try {
                            i3 = this.f.getExtras().getInt("satellites");
                        } catch (Exception e3) {
                        }
                    }
                    this.o = String.format(Locale.CHINA, "&ll=%.5f|%.5f&s=%.1f&d=%.1f&ll_n=%d&ll_t=%d", new Object[]{Double.valueOf(this.f.getLongitude()), Double.valueOf(this.f.getLatitude()), Float.valueOf(speed), Float.valueOf(this.f.getBearing()), Integer.valueOf(i3), Long.valueOf(currentTimeMillis)});
                    a(this.f.getLongitude(), this.f.getLatitude(), speed);
                }
                try {
                    com.baidu.location.a.c.a().a(this.f);
                } catch (Exception e4) {
                }
                if (this.f != null) {
                    com.baidu.location.c.e.a().a(this.f);
                }
                if (i() && this.f != null) {
                    com.baidu.location.a.a.a().a(f());
                    if (k > 2 && j.a(this.f, true)) {
                        boolean d2 = j.a().d();
                        h.a(new a(b.a().f()));
                        h.a(System.currentTimeMillis());
                        h.a(new Location(this.f));
                        h.a(com.baidu.location.a.a.a().c());
                        if (!d2) {
                            j.a(h.c(), (i) null, h.d(), com.baidu.location.a.a.a().c());
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        this.f = null;
    }

    static /* synthetic */ int f(e eVar) {
        int i2 = eVar.y;
        eVar.y = i2 + 1;
        return i2;
    }

    public void a(boolean z2) {
        if (z2) {
            c();
        } else {
            d();
        }
    }

    public synchronized void b() {
        if (f.isServing) {
            this.d = f.getServiceContext();
            try {
                this.e = (LocationManager) this.d.getSystemService("location");
                this.j = new a();
                this.e.addGpsStatusListener(this.j);
                this.h = new c();
                this.e.requestLocationUpdates("passive", 9000, 0.0f, this.h);
            } catch (Exception e2) {
            }
            this.s = new Handler() {
                public void handleMessage(Message message) {
                    if (f.isServing) {
                        switch (message.what) {
                            case 1:
                                e.this.e((Location) message.obj);
                                return;
                            case 2:
                                if (e.this.j != null) {
                                    e.this.j.a((String) message.obj);
                                    return;
                                }
                                return;
                            case 3:
                                e.this.a("&og=1", (Location) message.obj);
                                return;
                            case 4:
                                e.this.a("&og=2", (Location) message.obj);
                                return;
                            default:
                                return;
                        }
                    }
                }
            };
        }
    }

    public void c() {
        b();
        if (!this.n) {
            try {
                this.g = new b();
                this.e.requestLocationUpdates("gps", 1000, 0.0f, this.g);
                this.e.addNmeaListener(this.j);
                this.n = true;
            } catch (Exception e2) {
            }
        }
    }

    public void d() {
        if (this.n) {
            if (this.e != null) {
                try {
                    if (this.g != null) {
                        this.e.removeUpdates(this.g);
                    }
                    if (this.j != null) {
                        this.e.removeNmeaListener(this.j);
                    }
                } catch (Exception e2) {
                }
            }
            i.d = 0;
            i.s = 0;
            this.g = null;
            this.n = false;
            b(false);
        }
    }

    public synchronized void e() {
        d();
        if (this.e != null) {
            try {
                if (this.j != null) {
                    this.e.removeGpsStatusListener(this.j);
                }
                this.e.removeUpdates(this.h);
            } catch (Exception e2) {
            }
            this.j = null;
            this.e = null;
        }
    }

    public String f() {
        double[] dArr;
        boolean z2;
        if (this.f == null) {
            return null;
        }
        String str = "{\"result\":{\"time\":\"" + i.a() + "\",\"error\":\"61\"},\"content\":{\"point\":{\"x\":" + "\"%f\",\"y\":\"%f\"},\"radius\":\"%d\",\"d\":\"%f\"," + "\"s\":\"%f\",\"n\":\"%d\"";
        int accuracy = (int) (this.f.hasAccuracy() ? this.f.getAccuracy() : 10.0f);
        float speed = (float) (((double) this.f.getSpeed()) * 3.6d);
        if (!this.f.hasSpeed()) {
            speed = -1.0f;
        }
        double[] dArr2 = new double[2];
        if (com.baidu.location.h.e.a().a(this.f.getLongitude(), this.f.getLatitude())) {
            double[] coorEncrypt = Jni.coorEncrypt(this.f.getLongitude(), this.f.getLatitude(), "gps2gcj");
            if (coorEncrypt[0] > 0.0d || coorEncrypt[1] > 0.0d) {
                dArr = coorEncrypt;
                z2 = true;
            } else {
                coorEncrypt[0] = this.f.getLongitude();
                coorEncrypt[1] = this.f.getLatitude();
                dArr = coorEncrypt;
                z2 = true;
            }
        } else {
            dArr2[0] = this.f.getLongitude();
            dArr2[1] = this.f.getLatitude();
            dArr = dArr2;
            z2 = false;
        }
        String format = String.format(Locale.CHINA, str, new Object[]{Double.valueOf(dArr[0]), Double.valueOf(dArr[1]), Integer.valueOf(accuracy), Float.valueOf(this.f.getBearing()), Float.valueOf(speed), Integer.valueOf(k)});
        if (!z2) {
            format = format + ",\"in_cn\":\"0\"";
        }
        if (!this.f.hasAltitude()) {
            return format + "}}";
        }
        return format + String.format(Locale.CHINA, ",\"h\":%.2f}}", new Object[]{Double.valueOf(this.f.getAltitude())});
    }

    public Location g() {
        if (this.f != null && Math.abs(System.currentTimeMillis() - this.f.getTime()) <= 60000) {
            return this.f;
        }
        return null;
    }

    public boolean h() {
        return (this.f == null || this.f.getLatitude() == 0.0d || this.f.getLongitude() == 0.0d) ? false : true;
    }

    public boolean i() {
        if (!h() || System.currentTimeMillis() - this.q > 10000) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (!this.m || currentTimeMillis - this.l >= 3000) {
            return this.p;
        }
        return true;
    }
}
