package com.baidu.location.f;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import com.baidu.location.a.e;
import com.baidu.location.a.h;
import com.baidu.location.f;
import java.lang.reflect.Field;
import java.util.List;

public class j extends l {
    public static long a = 0;
    private static j b = null;
    private WifiManager c = null;
    private a d = null;
    private i e = null;
    private long f = 0;
    private long g = 0;
    private boolean h = false;
    private Object i = null;
    private boolean j = true;
    private Handler k = new Handler();

    private class a extends BroadcastReceiver {
        private long b;
        private boolean c;

        private a() {
            this.b = 0;
            this.c = false;
        }

        public void onReceive(Context context, Intent intent) {
            if (context != null) {
                String action = intent.getAction();
                if (action.equals("android.net.wifi.SCAN_RESULTS")) {
                    j.a = System.currentTimeMillis() / 1000;
                    j.this.m();
                    e.b().h();
                    if (System.currentTimeMillis() - h.b() <= 5000) {
                        com.baidu.location.a.j.a(h.c(), j.this.i(), h.d(), h.a());
                    }
                } else if (action.equals("android.net.wifi.STATE_CHANGE") && ((NetworkInfo) intent.getParcelableExtra("networkInfo")).getState().equals(NetworkInfo.State.CONNECTED) && System.currentTimeMillis() - this.b >= 5000) {
                    this.b = System.currentTimeMillis();
                    if (!this.c) {
                        this.c = true;
                    }
                }
            }
        }
    }

    private j() {
    }

    public static synchronized j a() {
        j jVar;
        synchronized (j.class) {
            if (b == null) {
                b = new j();
            }
            jVar = b;
        }
        return jVar;
    }

    public static boolean a(i iVar, i iVar2, float f2) {
        int i2;
        if (iVar == null || iVar2 == null) {
            return false;
        }
        List<ScanResult> list = iVar.a;
        List<ScanResult> list2 = iVar2.a;
        if (list == list2) {
            return true;
        }
        if (list == null || list2 == null) {
            return false;
        }
        int size = list.size();
        int size2 = list2.size();
        float f3 = (float) (size + size2);
        if (size == 0 && size2 == 0) {
            return true;
        }
        if (size == 0 || size2 == 0) {
            return false;
        }
        int i3 = 0;
        int i4 = 0;
        while (i3 < size) {
            String str = list.get(i3).BSSID;
            if (str != null) {
                int i5 = 0;
                while (true) {
                    if (i5 >= size2) {
                        i2 = i4;
                        break;
                    } else if (str.equals(list2.get(i5).BSSID)) {
                        i2 = i4 + 1;
                        break;
                    } else {
                        i5++;
                    }
                }
            } else {
                i2 = i4;
            }
            i3++;
            i4 = i2;
        }
        return ((float) (i4 * 2)) > f3 * f2;
    }

    /* access modifiers changed from: private */
    public void m() {
        if (this.c != null) {
            try {
                i iVar = new i(this.c.getScanResults(), System.currentTimeMillis());
                if (this.e == null || !iVar.a(this.e)) {
                    this.e = iVar;
                }
            } catch (Exception e2) {
            }
        }
    }

    public synchronized void b() {
        if (!this.h) {
            if (f.isServing) {
                this.c = (WifiManager) f.getServiceContext().getSystemService("wifi");
                this.d = new a();
                try {
                    f.getServiceContext().registerReceiver(this.d, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
                } catch (Exception e2) {
                }
                this.h = true;
                try {
                    Field declaredField = Class.forName("android.net.wifi.WifiManager").getDeclaredField("mService");
                    if (declaredField != null) {
                        declaredField.setAccessible(true);
                        this.i = declaredField.get(this.c);
                        this.i.getClass();
                    }
                } catch (Exception e3) {
                }
            }
        }
    }

    public synchronized void c() {
        if (this.h) {
            try {
                f.getServiceContext().unregisterReceiver(this.d);
                a = 0;
            } catch (Exception e2) {
            }
            this.d = null;
            this.c = null;
            this.h = false;
        }
    }

    public boolean d() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.g <= 5000) {
            return false;
        }
        this.g = currentTimeMillis;
        return e();
    }

    public boolean e() {
        if (this.c == null) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.f <= 5000 || currentTimeMillis - (a * 1000) <= 5000) {
            return false;
        }
        if (!g() || currentTimeMillis - this.f > 10000) {
            return f();
        }
        return false;
    }

    public boolean f() {
        try {
            if (!this.c.isWifiEnabled() && (Build.VERSION.SDK_INT <= 17 || !this.c.isScanAlwaysAvailable())) {
                return false;
            }
            this.c.startScan();
            this.f = System.currentTimeMillis();
            return true;
        } catch (Exception | NoSuchMethodError e2) {
            return false;
        }
    }

    public boolean g() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) f.getServiceContext().getSystemService("connectivity")).getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.getType() == 1;
        } catch (Exception e2) {
            return false;
        }
    }

    public WifiInfo h() {
        if (this.c == null) {
            return null;
        }
        try {
            WifiInfo connectionInfo = this.c.getConnectionInfo();
            if (connectionInfo == null || connectionInfo.getBSSID() == null) {
                return null;
            }
            String bssid = connectionInfo.getBSSID();
            if (bssid != null) {
                String replace = bssid.replace(":", "");
                if ("000000000000".equals(replace) || "".equals(replace)) {
                    return null;
                }
            }
            return connectionInfo;
        } catch (Exception e2) {
            return null;
        }
    }

    public i i() {
        return (this.e == null || !this.e.f()) ? k() : this.e;
    }

    public i j() {
        return (this.e == null || !this.e.g()) ? k() : this.e;
    }

    public i k() {
        if (this.c != null) {
            try {
                return new i(this.c.getScanResults(), this.f);
            } catch (Exception e2) {
            }
        }
        return new i((List<ScanResult>) null, 0);
    }

    public String l() {
        try {
            WifiInfo connectionInfo = this.c.getConnectionInfo();
            if (connectionInfo != null) {
                return connectionInfo.getMacAddress();
            }
            return null;
        } catch (Exception e2) {
            return null;
        }
    }
}
