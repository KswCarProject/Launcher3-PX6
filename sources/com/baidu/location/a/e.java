package com.baidu.location.a;

import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.a.d;
import com.baidu.location.c.g;
import com.baidu.location.e.d;
import com.baidu.location.f.c;
import com.baidu.location.f.f;
import com.baidu.location.f.i;
import com.baidu.location.f.k;
import java.util.List;

public class e extends d {
    public static boolean h = false;
    private static e i = null;
    private double A;
    private boolean B;
    private long C;
    private long D;
    private a E;
    /* access modifiers changed from: private */
    public boolean F;
    /* access modifiers changed from: private */
    public boolean G;
    private boolean H;
    private boolean I;
    private b J;
    /* access modifiers changed from: private */
    public boolean K;
    final int e;
    public d.b f;
    public final Handler g;
    private boolean j;
    private String k;
    private BDLocation l;
    private BDLocation m;
    private i n;
    private com.baidu.location.f.a o;
    private i p;
    private com.baidu.location.f.a q;
    private boolean r;
    private volatile boolean s;
    /* access modifiers changed from: private */
    public boolean t;
    private long u;
    private long v;
    private Address w;
    private String x;
    private List<Poi> y;
    private double z;

    private class a implements Runnable {
        private a() {
        }

        public void run() {
            if (e.this.F) {
                boolean unused = e.this.F = false;
                if (!e.this.G) {
                    e.this.a(true);
                }
            }
        }
    }

    private class b implements Runnable {
        private b() {
        }

        public void run() {
            if (e.this.K) {
                boolean unused = e.this.K = false;
            }
            if (e.this.t) {
                boolean unused2 = e.this.t = false;
                e.this.g((Message) null);
            }
        }
    }

    private e() {
        this.e = 1000;
        this.j = true;
        this.f = null;
        this.k = null;
        this.l = null;
        this.m = null;
        this.n = null;
        this.o = null;
        this.p = null;
        this.q = null;
        this.r = true;
        this.s = false;
        this.t = false;
        this.u = 0;
        this.v = 0;
        this.w = null;
        this.x = null;
        this.y = null;
        this.B = false;
        this.C = 0;
        this.D = 0;
        this.E = null;
        this.F = false;
        this.G = false;
        this.H = true;
        this.g = new d.a();
        this.I = false;
        this.J = null;
        this.K = false;
        this.f = new d.b();
    }

    private boolean a(com.baidu.location.f.a aVar) {
        this.b = c.a().f();
        if (this.b == aVar) {
            return false;
        }
        return this.b == null || aVar == null || !aVar.a(this.b);
    }

    private boolean a(i iVar) {
        this.a = k.a().j();
        if (iVar == this.a) {
            return false;
        }
        return this.a == null || iVar == null || !iVar.c(this.a);
    }

    public static synchronized e b() {
        e eVar;
        synchronized (e.class) {
            if (i == null) {
                i = new e();
            }
            eVar = i;
        }
        return eVar;
    }

    private boolean b(com.baidu.location.f.a aVar) {
        if (aVar == null) {
            return false;
        }
        return this.q == null || !aVar.a(this.q);
    }

    private void c(Message message) {
        boolean z2 = message.getData().getBoolean("isWaitingLocTag", false);
        if (z2) {
            h = true;
        }
        if (z2) {
        }
        int d = a.a().d(message);
        f.a().d();
        switch (d) {
            case 1:
                d(message);
                return;
            case 2:
                f(message);
                return;
            case 3:
                if (f.a().i()) {
                    e(message);
                    return;
                }
                return;
            default:
                throw new IllegalArgumentException(String.format("this type %d is illegal", new Object[]{Integer.valueOf(d)}));
        }
    }

    private void d(Message message) {
        if (f.a().i()) {
            e(message);
            f.a().c();
            return;
        }
        f(message);
        f.a().b();
    }

    private void e(Message message) {
        BDLocation bDLocation = new BDLocation(f.a().f());
        if (com.baidu.location.h.i.f.equals("all") || com.baidu.location.h.i.g || com.baidu.location.h.i.h) {
            float[] fArr = new float[2];
            Location.distanceBetween(this.A, this.z, bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
            if (fArr[0] < 100.0f) {
                if (this.w != null) {
                    bDLocation.setAddr(this.w);
                }
                if (this.x != null) {
                    bDLocation.setLocationDescribe(this.x);
                }
                if (this.y != null) {
                    bDLocation.setPoiList(this.y);
                }
            } else {
                this.B = true;
                f((Message) null);
            }
        }
        this.l = bDLocation;
        this.m = null;
        a.a().a(bDLocation);
    }

    private void f(Message message) {
        if (this.r) {
            this.D = SystemClock.uptimeMillis();
            g(message);
        } else if (!this.s) {
            this.D = SystemClock.uptimeMillis();
            if (k.a().e()) {
                this.t = true;
                if (this.J == null) {
                    this.J = new b();
                }
                if (this.K && this.J != null) {
                    this.g.removeCallbacks(this.J);
                }
                this.g.postDelayed(this.J, 3500);
                this.K = true;
                return;
            }
            g(message);
        }
    }

    /* access modifiers changed from: private */
    public void g(Message message) {
        if (!this.s) {
            if (System.currentTimeMillis() - this.u >= 1000 || this.l == null) {
                if (this.D > 0) {
                    com.baidu.location.c.f.a().b().a(this.D);
                } else {
                    com.baidu.location.c.f.a().b().a(SystemClock.uptimeMillis());
                }
                this.s = true;
                this.j = a(this.o);
                if (a(this.n) || this.j || this.l == null || this.B) {
                    this.u = System.currentTimeMillis();
                    String a2 = a((String) null);
                    if (a2 != null) {
                        if (this.k != null) {
                            a2 = a2 + this.k;
                            this.k = null;
                        }
                        com.baidu.location.c.f.a().b().b(SystemClock.uptimeMillis());
                        this.f.a(a2);
                        this.o = this.b;
                        this.n = this.a;
                        if (j()) {
                            this.o = this.b;
                            this.n = this.a;
                        }
                        if (com.baidu.location.e.d.a().h()) {
                            if (this.E == null) {
                                this.E = new a();
                            }
                            this.g.postDelayed(this.E, com.baidu.location.e.d.a().a(c.a(c.a().e())));
                            this.F = true;
                        }
                        if (this.r) {
                            this.r = false;
                            if (k.a().g() && message != null && a.a().e(message) < 1000 && com.baidu.location.e.d.a().d()) {
                                com.baidu.location.e.d.a().i();
                            }
                            com.baidu.location.c.a.a().b();
                        }
                    } else if (this.l != null) {
                        a.a().a(this.l);
                        k();
                    } else {
                        BDLocation bDLocation = new BDLocation();
                        bDLocation.setLocType(62);
                        a.a().a(bDLocation);
                        k();
                        long currentTimeMillis = System.currentTimeMillis();
                        if (currentTimeMillis - this.C > 60000) {
                            this.C = currentTimeMillis;
                            com.baidu.location.c.f.a().a("TypeCriteriaException");
                        }
                    }
                } else {
                    if (this.m != null && System.currentTimeMillis() - this.v > 30000) {
                        this.l = this.m;
                        this.m = null;
                    }
                    if (f.a().f()) {
                        this.l.setDirection(f.a().h());
                    }
                    a.a().a(this.l);
                    k();
                }
            } else {
                a.a().a(this.l);
                k();
            }
        }
    }

    private boolean j() {
        BDLocation bDLocation = null;
        double random = Math.random();
        long uptimeMillis = SystemClock.uptimeMillis();
        com.baidu.location.f.a f2 = c.a().f();
        i i2 = k.a().i();
        boolean z2 = f2 != null && f2.e() && (i2 == null || i2.a() == 0);
        if (com.baidu.location.e.d.a().d() && com.baidu.location.e.d.a().f() && (z2 || (0.0d < random && random < com.baidu.location.e.d.a().o()))) {
            bDLocation = com.baidu.location.e.d.a().a(c.a().f(), k.a().i(), (BDLocation) null, d.b.IS_MIX_MODE, d.a.NEED_TO_LOG);
        }
        if (bDLocation == null || bDLocation.getLocType() != 66 || !this.s) {
            return false;
        }
        BDLocation bDLocation2 = new BDLocation(bDLocation);
        bDLocation2.setLocType(BDLocation.TypeNetWorkLocation);
        if (!this.s) {
            return false;
        }
        g gVar = new g();
        gVar.a(this.D);
        gVar.b(uptimeMillis);
        gVar.c(uptimeMillis);
        gVar.d(SystemClock.uptimeMillis());
        gVar.a("ofs");
        if (this.o != null) {
            gVar.b(this.o.h());
            gVar.b("&offtag=1");
        }
        com.baidu.location.c.f.a().a(gVar);
        this.G = true;
        a.a().a(bDLocation2);
        this.l = bDLocation2;
        return true;
    }

    private void k() {
        this.s = false;
        this.G = false;
        this.H = false;
        this.B = false;
        l();
    }

    private void l() {
        if (this.l != null) {
            j.a().c();
        }
    }

    public Address a(BDLocation bDLocation) {
        if (com.baidu.location.h.i.f.equals("all") || com.baidu.location.h.i.g || com.baidu.location.h.i.h) {
            float[] fArr = new float[2];
            Location.distanceBetween(this.A, this.z, bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
            if (fArr[0] >= 100.0f) {
                this.x = null;
                this.y = null;
                this.B = true;
                f((Message) null);
            } else if (this.w != null) {
                return this.w;
            }
        }
        return null;
    }

    public void a() {
        BDLocation bDLocation;
        if (this.E != null && this.F) {
            this.F = false;
            this.g.removeCallbacks(this.E);
        }
        if (f.a().i()) {
            BDLocation bDLocation2 = new BDLocation(f.a().f());
            if (com.baidu.location.h.i.f.equals("all") || com.baidu.location.h.i.g || com.baidu.location.h.i.h) {
                float[] fArr = new float[2];
                Location.distanceBetween(this.A, this.z, bDLocation2.getLatitude(), bDLocation2.getLongitude(), fArr);
                if (fArr[0] < 100.0f) {
                    if (this.w != null) {
                        bDLocation2.setAddr(this.w);
                    }
                    if (this.x != null) {
                        bDLocation2.setLocationDescribe(this.x);
                    }
                    if (this.y != null) {
                        bDLocation2.setPoiList(this.y);
                    }
                }
            }
            a.a().a(bDLocation2);
            k();
        } else if (this.G) {
            k();
        } else {
            com.baidu.location.c.f.a().b().c(SystemClock.uptimeMillis());
            if (!com.baidu.location.e.d.a().d() || !com.baidu.location.e.d.a().e()) {
                bDLocation = null;
            } else {
                bDLocation = com.baidu.location.e.d.a().a(c.a().f(), k.a().i(), (BDLocation) null, d.b.IS_NOT_MIX_MODE, d.a.NEED_TO_LOG);
                if (bDLocation != null && bDLocation.getLocType() == 66) {
                    a.a().a(bDLocation);
                }
            }
            if (bDLocation == null || bDLocation.getLocType() == 67) {
                if (this.j || this.l == null) {
                    BDLocation a2 = com.baidu.location.e.a.a().a(false);
                    a.a().a(a2);
                    boolean z2 = true;
                    if (com.baidu.location.h.i.f.equals("all") && a2.getAddrStr() == null) {
                        z2 = false;
                    }
                    if (com.baidu.location.h.i.g && a2.getLocationDescribe() == null) {
                        z2 = false;
                    }
                    if (com.baidu.location.h.i.h && a2.getPoiList() == null) {
                        z2 = false;
                    }
                    if (!z2) {
                        a2.setLocType(67);
                    }
                    bDLocation = a2;
                } else {
                    a.a().a(this.l);
                }
            }
            com.baidu.location.c.f.a().b().d(SystemClock.uptimeMillis());
            if (bDLocation == null || bDLocation.getLocType() == 67) {
                this.l = null;
                com.baidu.location.c.f.a().b().a("off");
                if (this.o != null) {
                    com.baidu.location.c.f.a().b().b(this.o.h());
                }
                com.baidu.location.c.f.a().c();
            } else {
                this.l = bDLocation;
                com.baidu.location.c.f.a().b().a("ofs");
                if (this.o != null) {
                    com.baidu.location.c.f.a().b().b(this.o.h());
                }
                com.baidu.location.c.f.a().c();
            }
            this.m = null;
            k();
        }
    }

    public void a(Message message) {
        boolean z2;
        if (this.E != null && this.F) {
            this.F = false;
            this.g.removeCallbacks(this.E);
        }
        BDLocation bDLocation = (BDLocation) message.obj;
        BDLocation bDLocation2 = new BDLocation(bDLocation);
        if (bDLocation.hasAddr()) {
            this.w = bDLocation.getAddress();
            this.z = bDLocation.getLongitude();
            this.A = bDLocation.getLatitude();
        }
        if (bDLocation.getLocationDescribe() != null) {
            this.x = bDLocation.getLocationDescribe();
            this.z = bDLocation.getLongitude();
            this.A = bDLocation.getLatitude();
        }
        if (bDLocation.getPoiList() != null) {
            this.y = bDLocation.getPoiList();
            this.z = bDLocation.getLongitude();
            this.A = bDLocation.getLatitude();
        }
        if (f.a().i()) {
            BDLocation bDLocation3 = new BDLocation(f.a().f());
            if (com.baidu.location.h.i.f.equals("all") || com.baidu.location.h.i.g || com.baidu.location.h.i.h) {
                float[] fArr = new float[2];
                Location.distanceBetween(this.A, this.z, bDLocation3.getLatitude(), bDLocation3.getLongitude(), fArr);
                if (fArr[0] < 100.0f) {
                    if (this.w != null) {
                        bDLocation3.setAddr(this.w);
                    }
                    if (this.x != null) {
                        bDLocation3.setLocationDescribe(this.x);
                    }
                    if (this.y != null) {
                        bDLocation3.setPoiList(this.y);
                    }
                }
            }
            a.a().a(bDLocation3);
            k();
        } else if (bDLocation.getNetworkLocationType() != null && bDLocation.getNetworkLocationType().equals("sky")) {
            bDLocation.setNetworkLocationType("wf");
            a.a().a(bDLocation);
            this.v = System.currentTimeMillis();
            this.l = bDLocation;
        } else if (this.G) {
            float[] fArr2 = new float[2];
            if (this.l != null) {
                Location.distanceBetween(this.l.getLatitude(), this.l.getLongitude(), bDLocation.getLatitude(), bDLocation.getLongitude(), fArr2);
            }
            if (fArr2[0] > 10.0f) {
                this.l = bDLocation;
                if (!this.H) {
                    this.H = false;
                    a.a().a(bDLocation);
                }
            }
            k();
        } else {
            com.baidu.location.c.f.a().b().c(SystemClock.uptimeMillis());
            this.m = null;
            if (bDLocation.getLocType() != 161 || !"cl".equals(bDLocation.getNetworkLocationType()) || this.l == null || this.l.getLocType() != 161 || !"wf".equals(this.l.getNetworkLocationType()) || System.currentTimeMillis() - this.v >= 30000) {
                z2 = false;
            } else {
                z2 = true;
                this.m = bDLocation;
            }
            if (z2) {
                a.a().a(this.l);
            } else {
                a.a().a(bDLocation);
                this.v = System.currentTimeMillis();
                com.baidu.location.c.f.a().b().d(SystemClock.uptimeMillis());
                if (bDLocation.getLocType() == 161) {
                    com.baidu.location.c.f.a().b().a("ons");
                    if (this.o != null) {
                        com.baidu.location.c.f.a().b().b(this.o.h());
                    }
                } else {
                    com.baidu.location.c.f.a().b().a("onf");
                    if (this.o != null) {
                        com.baidu.location.c.f.a().b().b(this.o.h());
                    }
                    com.baidu.location.c.f.a().c();
                }
            }
            if (!com.baidu.location.h.i.a(bDLocation)) {
                this.l = null;
            } else if (!z2) {
                this.l = bDLocation;
            }
            int a2 = com.baidu.location.h.i.a(c, "ssid\":\"", "\"");
            if (a2 == Integer.MIN_VALUE || this.n == null) {
                this.k = null;
            } else {
                this.k = this.n.c(a2);
            }
            if (com.baidu.location.e.d.a().d() && bDLocation.getLocType() == 161 && "cl".equals(bDLocation.getNetworkLocationType()) && b(this.o)) {
                com.baidu.location.e.d.a().a(this.o, (i) null, bDLocation2, d.b.IS_NOT_MIX_MODE, d.a.NO_NEED_TO_LOG);
                this.q = this.o;
            }
            if (com.baidu.location.e.d.a().d() && bDLocation.getLocType() == 161 && "wf".equals(bDLocation.getNetworkLocationType())) {
                com.baidu.location.e.d.a().a((com.baidu.location.f.a) null, this.n, bDLocation2, d.b.IS_NOT_MIX_MODE, d.a.NO_NEED_TO_LOG);
                this.p = this.n;
            }
            if (this.o != null) {
                com.baidu.location.e.a.a().a(c, this.o, this.n, bDLocation2);
            }
            if (k.a().g()) {
                com.baidu.location.e.d.a().i();
                com.baidu.location.e.d.a().m();
            }
            k();
        }
    }

    public void a(boolean z2) {
        BDLocation bDLocation = null;
        if (com.baidu.location.e.d.a().d() && com.baidu.location.e.d.a().g()) {
            bDLocation = com.baidu.location.e.d.a().a(c.a().f(), k.a().i(), (BDLocation) null, d.b.IS_NOT_MIX_MODE, d.a.NEED_TO_LOG);
            if ((bDLocation == null || bDLocation.getLocType() == 67) && z2) {
                bDLocation = com.baidu.location.e.a.a().a(false);
            }
        } else if (z2) {
            bDLocation = com.baidu.location.e.a.a().a(false);
        }
        if (bDLocation != null && bDLocation.getLocType() == 66) {
            boolean z3 = true;
            if (com.baidu.location.h.i.f.equals("all") && bDLocation.getAddrStr() == null) {
                z3 = false;
            }
            if (com.baidu.location.h.i.g && bDLocation.getLocationDescribe() == null) {
                z3 = false;
            }
            if (com.baidu.location.h.i.h && bDLocation.getPoiList() == null) {
                z3 = false;
            }
            if (z3) {
                a.a().a(bDLocation);
            }
        }
    }

    public void b(Message message) {
        if (this.I) {
            c(message);
        }
    }

    public void c() {
        this.r = true;
        this.s = false;
        this.I = true;
    }

    public void d() {
        this.s = false;
        this.t = false;
        this.G = false;
        this.H = true;
        i();
        this.I = false;
    }

    public String e() {
        return this.x;
    }

    public List<Poi> f() {
        return this.y;
    }

    public boolean g() {
        return this.j;
    }

    public void h() {
        if (this.t) {
            g((Message) null);
            this.t = false;
            return;
        }
        com.baidu.location.c.a.a().d();
    }

    public void i() {
        this.l = null;
    }
}
