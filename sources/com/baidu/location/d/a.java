package com.baidu.location.d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.Jni;
import com.baidu.location.LocationClient;
import java.util.ArrayList;
import java.util.Iterator;

public class a {
    /* access modifiers changed from: private */
    public ArrayList<BDNotifyListener> a = null;
    private float b = Float.MAX_VALUE;
    private BDLocation c = null;
    private long d = 0;
    /* access modifiers changed from: private */
    public LocationClient e = null;
    private Context f = null;
    private int g = 0;
    private long h = 0;
    private boolean i = false;
    private PendingIntent j = null;
    private AlarmManager k = null;
    private C0003a l = null;
    private b m = new b();
    private boolean n = false;

    /* renamed from: com.baidu.location.d.a$a  reason: collision with other inner class name */
    public class C0003a extends BroadcastReceiver {
        public C0003a() {
        }

        public void onReceive(Context context, Intent intent) {
            if (a.this.a != null && !a.this.a.isEmpty()) {
                a.this.e.requestNotifyLocation();
            }
        }
    }

    public class b implements BDLocationListener {
        public b() {
        }

        public void onReceiveLocation(BDLocation bDLocation) {
            if (a.this.a != null && a.this.a.size() > 0) {
                a.this.a(bDLocation);
            }
        }
    }

    public a(Context context, LocationClient locationClient) {
        this.f = context;
        this.e = locationClient;
        this.e.registerNotifyLocationListener(this.m);
        this.k = (AlarmManager) this.f.getSystemService("alarm");
        this.l = new C0003a();
        this.n = false;
    }

    private void a(long j2) {
        try {
            if (this.j != null) {
                this.k.cancel(this.j);
            }
            this.j = PendingIntent.getBroadcast(this.f, 0, new Intent("android.com.baidu.location.TIMER.NOTIFY"), 134217728);
            if (this.j != null) {
                this.k.set(0, System.currentTimeMillis() + j2, this.j);
            }
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: private */
    public void a(BDLocation bDLocation) {
        float f2;
        if (bDLocation.getLocType() != 61 && bDLocation.getLocType() != 161 && bDLocation.getLocType() != 65) {
            a(120000);
        } else if (System.currentTimeMillis() - this.d >= 5000 && this.a != null) {
            this.c = bDLocation;
            this.d = System.currentTimeMillis();
            float[] fArr = new float[1];
            float f3 = Float.MAX_VALUE;
            Iterator<BDNotifyListener> it = this.a.iterator();
            while (true) {
                f2 = f3;
                if (!it.hasNext()) {
                    break;
                }
                BDNotifyListener next = it.next();
                Location.distanceBetween(bDLocation.getLatitude(), bDLocation.getLongitude(), next.mLatitudeC, next.mLongitudeC, fArr);
                f3 = (fArr[0] - next.mRadius) - bDLocation.getRadius();
                if (f3 > 0.0f) {
                    if (f3 < f2) {
                    }
                    f3 = f2;
                } else {
                    if (next.Notified < 3) {
                        next.Notified++;
                        next.onNotify(bDLocation, fArr[0]);
                        if (next.Notified < 3) {
                            this.i = true;
                        }
                    }
                    f3 = f2;
                }
            }
            if (f2 < this.b) {
                this.b = f2;
            }
            this.g = 0;
            c();
        }
    }

    private boolean b() {
        boolean z = false;
        if (this.a == null || this.a.isEmpty()) {
            return false;
        }
        Iterator<BDNotifyListener> it = this.a.iterator();
        while (true) {
            boolean z2 = z;
            if (!it.hasNext()) {
                return z2;
            }
            z = it.next().Notified < 3 ? true : z2;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c() {
        /*
            r8 = this;
            r1 = 10000(0x2710, float:1.4013E-41)
            r2 = 0
            r3 = 1
            boolean r0 = r8.b()
            if (r0 != 0) goto L_0x000b
        L_0x000a:
            return
        L_0x000b:
            float r0 = r8.b
            r4 = 1167867904(0x459c4000, float:5000.0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0043
            r0 = 600000(0x927c0, float:8.40779E-40)
        L_0x0017:
            boolean r4 = r8.i
            if (r4 == 0) goto L_0x005f
            r8.i = r2
        L_0x001d:
            int r0 = r8.g
            if (r0 == 0) goto L_0x005d
            long r4 = r8.h
            int r0 = r8.g
            long r6 = (long) r0
            long r4 = r4 + r6
            long r6 = java.lang.System.currentTimeMillis()
            long r4 = r4 - r6
            long r6 = (long) r1
            int r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x005d
            r0 = r2
        L_0x0032:
            if (r0 == 0) goto L_0x000a
            r8.g = r1
            long r0 = java.lang.System.currentTimeMillis()
            r8.h = r0
            int r0 = r8.g
            long r0 = (long) r0
            r8.a((long) r0)
            goto L_0x000a
        L_0x0043:
            float r0 = r8.b
            r4 = 1148846080(0x447a0000, float:1000.0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x004f
            r0 = 120000(0x1d4c0, float:1.68156E-40)
            goto L_0x0017
        L_0x004f:
            float r0 = r8.b
            r4 = 1140457472(0x43fa0000, float:500.0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x005b
            r0 = 60000(0xea60, float:8.4078E-41)
            goto L_0x0017
        L_0x005b:
            r0 = r1
            goto L_0x0017
        L_0x005d:
            r0 = r3
            goto L_0x0032
        L_0x005f:
            r1 = r0
            goto L_0x001d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.d.a.c():void");
    }

    public int a(BDNotifyListener bDNotifyListener) {
        if (this.a == null) {
            this.a = new ArrayList<>();
        }
        this.a.add(bDNotifyListener);
        bDNotifyListener.isAdded = true;
        bDNotifyListener.mNotifyCache = this;
        if (!this.n) {
            this.f.registerReceiver(this.l, new IntentFilter("android.com.baidu.location.TIMER.NOTIFY"));
            this.n = true;
        }
        if (bDNotifyListener.mCoorType != null) {
            if (!bDNotifyListener.mCoorType.equals("gcj02")) {
                double[] coorEncrypt = Jni.coorEncrypt(bDNotifyListener.mLongitude, bDNotifyListener.mLatitude, bDNotifyListener.mCoorType + "2gcj");
                bDNotifyListener.mLongitudeC = coorEncrypt[0];
                bDNotifyListener.mLatitudeC = coorEncrypt[1];
            }
            if (this.c == null || System.currentTimeMillis() - this.d > 30000) {
                this.e.requestNotifyLocation();
            } else {
                float[] fArr = new float[1];
                Location.distanceBetween(this.c.getLatitude(), this.c.getLongitude(), bDNotifyListener.mLatitudeC, bDNotifyListener.mLongitudeC, fArr);
                float radius = (fArr[0] - bDNotifyListener.mRadius) - this.c.getRadius();
                if (radius > 0.0f) {
                    if (radius < this.b) {
                        this.b = radius;
                    }
                } else if (bDNotifyListener.Notified < 3) {
                    bDNotifyListener.Notified++;
                    bDNotifyListener.onNotify(this.c, fArr[0]);
                    if (bDNotifyListener.Notified < 3) {
                        this.i = true;
                    }
                }
            }
            c();
        }
        return 1;
    }

    public void a() {
        if (this.j != null) {
            this.k.cancel(this.j);
        }
        this.c = null;
        this.d = 0;
        if (this.n) {
            this.f.unregisterReceiver(this.l);
        }
        this.n = false;
    }

    public void b(BDNotifyListener bDNotifyListener) {
        if (bDNotifyListener.mCoorType != null) {
            if (!bDNotifyListener.mCoorType.equals("gcj02")) {
                double[] coorEncrypt = Jni.coorEncrypt(bDNotifyListener.mLongitude, bDNotifyListener.mLatitude, bDNotifyListener.mCoorType + "2gcj");
                bDNotifyListener.mLongitudeC = coorEncrypt[0];
                bDNotifyListener.mLatitudeC = coorEncrypt[1];
            }
            if (this.c == null || System.currentTimeMillis() - this.d > 300000) {
                this.e.requestNotifyLocation();
            } else {
                float[] fArr = new float[1];
                Location.distanceBetween(this.c.getLatitude(), this.c.getLongitude(), bDNotifyListener.mLatitudeC, bDNotifyListener.mLongitudeC, fArr);
                float radius = (fArr[0] - bDNotifyListener.mRadius) - this.c.getRadius();
                if (radius > 0.0f) {
                    if (radius < this.b) {
                        this.b = radius;
                    }
                } else if (bDNotifyListener.Notified < 3) {
                    bDNotifyListener.Notified++;
                    bDNotifyListener.onNotify(this.c, fArr[0]);
                    if (bDNotifyListener.Notified < 3) {
                        this.i = true;
                    }
                }
            }
            c();
        }
    }

    public int c(BDNotifyListener bDNotifyListener) {
        if (this.a == null) {
            return 0;
        }
        if (this.a.contains(bDNotifyListener)) {
            this.a.remove(bDNotifyListener);
        }
        if (this.a.size() == 0 && this.j != null) {
            this.k.cancel(this.j);
        }
        return 1;
    }
}
