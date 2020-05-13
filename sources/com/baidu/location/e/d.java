package com.baidu.location.e;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.baidu.location.BDLocation;
import com.baidu.location.c.f;
import com.baidu.location.f.i;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class d {
    static final String a = "http://loc.map.baidu.com/offline_loc";
    static final String b = "com.baidu.lbs.offlinelocationprovider";
    /* access modifiers changed from: private */
    public static Context c;
    private static volatile d d;
    private final File e;
    private final f f;
    /* access modifiers changed from: private */
    public final b g;
    private final g h;
    /* access modifiers changed from: private */
    public final c i;

    public enum a {
        NEED_TO_LOG,
        NO_NEED_TO_LOG
    }

    public enum b {
        IS_MIX_MODE,
        IS_NOT_MIX_MODE
    }

    private enum c {
        NETWORK_UNKNOWN,
        NETWORK_WIFI,
        NETWORK_2G,
        NETWORK_3G,
        NETWORK_4G
    }

    private d() {
        File file;
        try {
            file = new File(c.getFilesDir(), "ofld");
            try {
                if (!file.exists()) {
                    file.mkdir();
                }
            } catch (Exception e2) {
            }
        } catch (Exception e3) {
            file = null;
        }
        this.e = file;
        this.g = new b(this);
        this.f = new f(this.g.a());
        this.i = new c(this, this.g.a());
        this.h = new g(this, this.g.a(), this.i.n());
    }

    private BDLocation a(final String[] strArr) {
        new BDLocation();
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        FutureTask futureTask = (FutureTask) newSingleThreadExecutor.submit(new Callable<BDLocation>() {
            /* JADX WARNING: Removed duplicated region for block: B:35:0x0081 A[SYNTHETIC, Splitter:B:35:0x0081] */
            /* renamed from: a */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public com.baidu.location.BDLocation call() {
                /*
                    r8 = this;
                    r1 = 0
                    r7 = 0
                    com.baidu.location.BDLocation r6 = new com.baidu.location.BDLocation
                    r6.<init>()
                    java.lang.String[] r0 = r9
                    int r0 = r0.length
                    if (r0 <= 0) goto L_0x004f
                    android.content.Context r0 = com.baidu.location.e.d.c
                    android.content.pm.PackageManager r0 = r0.getPackageManager()
                    java.lang.String r2 = com.baidu.location.e.d.b
                    android.content.pm.ProviderInfo r0 = r0.resolveContentProvider(r2, r1)
                    if (r0 == 0) goto L_0x0050
                    r2 = r0
                L_0x001d:
                    if (r2 == 0) goto L_0x0085
                    android.content.Context r0 = com.baidu.location.e.d.c     // Catch:{ Exception -> 0x0072, all -> 0x007e }
                    android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ Exception -> 0x0072, all -> 0x007e }
                    java.lang.String r1 = r2.authority     // Catch:{ Exception -> 0x0072, all -> 0x007e }
                    android.net.Uri r1 = com.baidu.location.e.d.c(r1)     // Catch:{ Exception -> 0x0072, all -> 0x007e }
                    java.lang.String[] r2 = r9     // Catch:{ Exception -> 0x0072, all -> 0x007e }
                    r3 = 0
                    r4 = 0
                    r5 = 0
                    android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x0072, all -> 0x007e }
                    com.baidu.location.BDLocation r0 = com.baidu.location.e.e.a((android.database.Cursor) r1)     // Catch:{ Exception -> 0x00bb, all -> 0x00b8 }
                    if (r1 == 0) goto L_0x003f
                    r1.close()     // Catch:{ Exception -> 0x00b2 }
                L_0x003f:
                    r6 = r0
                L_0x0040:
                    if (r6 == 0) goto L_0x004f
                    int r0 = r6.getLocType()
                    r1 = 67
                    if (r0 == r1) goto L_0x004f
                    r0 = 66
                    r6.setLocType(r0)
                L_0x004f:
                    return r6
                L_0x0050:
                    com.baidu.location.e.d r2 = com.baidu.location.e.d.this
                    com.baidu.location.e.c r2 = r2.i
                    java.lang.String[] r3 = r2.o()
                    r2 = r0
                    r0 = r1
                L_0x005c:
                    int r4 = r3.length
                    if (r0 >= r4) goto L_0x001d
                    android.content.Context r2 = com.baidu.location.e.d.c
                    android.content.pm.PackageManager r2 = r2.getPackageManager()
                    r4 = r3[r0]
                    android.content.pm.ProviderInfo r2 = r2.resolveContentProvider(r4, r1)
                    if (r2 != 0) goto L_0x001d
                    int r0 = r0 + 1
                    goto L_0x005c
                L_0x0072:
                    r0 = move-exception
                    r0 = r7
                L_0x0074:
                    if (r0 == 0) goto L_0x00be
                    r0.close()     // Catch:{ Exception -> 0x007b }
                    r0 = r6
                    goto L_0x003f
                L_0x007b:
                    r0 = move-exception
                    r0 = r6
                    goto L_0x003f
                L_0x007e:
                    r0 = move-exception
                L_0x007f:
                    if (r7 == 0) goto L_0x0084
                    r7.close()     // Catch:{ Exception -> 0x00b4 }
                L_0x0084:
                    throw r0
                L_0x0085:
                    com.baidu.location.e.e$a r0 = new com.baidu.location.e.e$a
                    java.lang.String[] r1 = r9
                    r0.<init>(r1)
                    com.baidu.location.e.d r1 = com.baidu.location.e.d.this     // Catch:{ Exception -> 0x00a2, all -> 0x00ab }
                    com.baidu.location.e.b r1 = r1.g     // Catch:{ Exception -> 0x00a2, all -> 0x00ab }
                    android.database.Cursor r7 = r1.a((com.baidu.location.e.e.a) r0)     // Catch:{ Exception -> 0x00a2, all -> 0x00ab }
                    com.baidu.location.BDLocation r6 = com.baidu.location.e.e.a((android.database.Cursor) r7)     // Catch:{ Exception -> 0x00a2, all -> 0x00ab }
                    if (r7 == 0) goto L_0x0040
                    r7.close()     // Catch:{ Exception -> 0x00a0 }
                    goto L_0x0040
                L_0x00a0:
                    r0 = move-exception
                    goto L_0x0040
                L_0x00a2:
                    r0 = move-exception
                    if (r7 == 0) goto L_0x0040
                    r7.close()     // Catch:{ Exception -> 0x00a9 }
                    goto L_0x0040
                L_0x00a9:
                    r0 = move-exception
                    goto L_0x0040
                L_0x00ab:
                    r0 = move-exception
                    if (r7 == 0) goto L_0x00b1
                    r7.close()     // Catch:{ Exception -> 0x00b6 }
                L_0x00b1:
                    throw r0
                L_0x00b2:
                    r1 = move-exception
                    goto L_0x003f
                L_0x00b4:
                    r1 = move-exception
                    goto L_0x0084
                L_0x00b6:
                    r1 = move-exception
                    goto L_0x00b1
                L_0x00b8:
                    r0 = move-exception
                    r7 = r1
                    goto L_0x007f
                L_0x00bb:
                    r0 = move-exception
                    r0 = r1
                    goto L_0x0074
                L_0x00be:
                    r0 = r6
                    goto L_0x003f
                */
                throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.d.AnonymousClass1.call():com.baidu.location.BDLocation");
            }
        });
        try {
            BDLocation bDLocation = (BDLocation) futureTask.get(2000, TimeUnit.MILLISECONDS);
            newSingleThreadExecutor.shutdown();
            return bDLocation;
        } catch (InterruptedException e2) {
            futureTask.cancel(true);
            newSingleThreadExecutor.shutdown();
            return null;
        } catch (ExecutionException e3) {
            futureTask.cancel(true);
            newSingleThreadExecutor.shutdown();
            return null;
        } catch (TimeoutException e4) {
            f.a().a("offlineLocation Timeout Exception!");
            futureTask.cancel(true);
            newSingleThreadExecutor.shutdown();
            return null;
        } catch (Throwable th) {
            newSingleThreadExecutor.shutdown();
            throw th;
        }
    }

    public static d a() {
        if (d == null) {
            synchronized (d.class) {
                if (d == null) {
                    if (c == null) {
                        a(com.baidu.location.f.getServiceContext());
                    }
                    d = new d();
                }
            }
        }
        d.q();
        return d;
    }

    public static void a(Context context) {
        if (c == null) {
            c = context;
            com.baidu.location.h.c.a().a(c);
        }
    }

    /* access modifiers changed from: private */
    public static final Uri c(String str) {
        return Uri.parse(String.format("content://%s/", new Object[]{str}));
    }

    private void q() {
        this.i.g();
    }

    private boolean r() {
        ProviderInfo providerInfo;
        String packageName = c.getPackageName();
        ProviderInfo resolveContentProvider = c.getPackageManager().resolveContentProvider(b, 0);
        if (resolveContentProvider == null) {
            String[] o = this.i.o();
            providerInfo = resolveContentProvider;
            int i2 = 0;
            while (i2 < o.length && (providerInfo = c.getPackageManager().resolveContentProvider(o[i2], 0)) == null) {
                i2++;
            }
        } else {
            providerInfo = resolveContentProvider;
        }
        if (providerInfo == null) {
            return true;
        }
        return packageName.equals(providerInfo.packageName);
    }

    public long a(String str) {
        return this.i.a(str);
    }

    public BDLocation a(com.baidu.location.f.a aVar, i iVar, BDLocation bDLocation, b bVar, a aVar2) {
        String d2;
        int i2;
        if (bVar == b.IS_MIX_MODE) {
            i2 = this.i.a();
            d2 = com.baidu.location.h.c.a().d() + "&mixMode=1";
        } else {
            d2 = com.baidu.location.h.c.a().d();
            i2 = 0;
        }
        String[] a2 = e.a(aVar, iVar, bDLocation, d2, (aVar2 == a.NEED_TO_LOG).booleanValue(), i2);
        BDLocation bDLocation2 = null;
        if (a2.length <= 0 || (bDLocation2 = a(a2)) == null || bDLocation2.getLocType() != 67) {
        }
        return bDLocation2;
    }

    public Context b() {
        return c;
    }

    /* access modifiers changed from: package-private */
    public File c() {
        return this.e;
    }

    public boolean d() {
        return this.i.h();
    }

    public boolean e() {
        return this.i.i();
    }

    public boolean f() {
        return this.i.j();
    }

    public boolean g() {
        return this.i.k();
    }

    public boolean h() {
        return this.i.m();
    }

    public void i() {
        this.f.a();
    }

    /* access modifiers changed from: package-private */
    public f j() {
        return this.f;
    }

    /* access modifiers changed from: package-private */
    public g k() {
        return this.h;
    }

    /* access modifiers changed from: package-private */
    public c l() {
        return this.i;
    }

    public void m() {
        if (r()) {
            this.g.b();
        }
    }

    public void n() {
    }

    public double o() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) c.getSystemService("connectivity")).getActiveNetworkInfo();
        c cVar = c.NETWORK_UNKNOWN;
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == 1) {
                cVar = c.NETWORK_WIFI;
            }
            if (activeNetworkInfo.getType() == 0) {
                int subtype = activeNetworkInfo.getSubtype();
                if (subtype == 1 || subtype == 2 || subtype == 4 || subtype == 7 || subtype == 11) {
                    cVar = c.NETWORK_2G;
                } else if (subtype == 3 || subtype == 5 || subtype == 6 || subtype == 8 || subtype == 9 || subtype == 10 || subtype == 12 || subtype == 14 || subtype == 15) {
                    cVar = c.NETWORK_3G;
                } else if (subtype == 13) {
                    cVar = c.NETWORK_4G;
                }
            }
        }
        if (cVar == c.NETWORK_UNKNOWN) {
            return this.i.b();
        }
        if (cVar == c.NETWORK_WIFI) {
            return this.i.c();
        }
        if (cVar == c.NETWORK_2G) {
            return this.i.d();
        }
        if (cVar == c.NETWORK_3G) {
            return this.i.e();
        }
        if (cVar == c.NETWORK_4G) {
            return this.i.f();
        }
        return 0.0d;
    }
}
