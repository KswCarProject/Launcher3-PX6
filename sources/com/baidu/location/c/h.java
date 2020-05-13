package com.baidu.location.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import com.baidu.location.e.d;
import com.baidu.location.f;
import com.baidu.location.h.i;

public class h {
    private static h b = null;
    final Handler a = new Handler();
    private a c = null;
    /* access modifiers changed from: private */
    public boolean d = false;
    private boolean e = false;
    /* access modifiers changed from: private */
    public boolean f = false;
    /* access modifiers changed from: private */
    public boolean g = true;
    private boolean h = false;

    private class a extends BroadcastReceiver {
        private a() {
        }

        public void onReceive(Context context, Intent intent) {
            if (context != null && h.this.a != null) {
                h.this.f();
            }
        }
    }

    private class b implements Runnable {
        private b() {
        }

        public void run() {
            if (h.this.d && b.a().e() && d.a().d()) {
                new Thread() {
                    public void run() {
                        super.run();
                        d.a().m();
                        d.a().i();
                    }
                }.start();
            }
            if (h.this.d && b.a().e()) {
                f.a().d();
            }
            if (!h.this.d || !h.this.g) {
                boolean unused = h.this.f = false;
                return;
            }
            h.this.a.postDelayed(this, (long) i.N);
            boolean unused2 = h.this.f = true;
        }
    }

    private h() {
    }

    public static synchronized h a() {
        h hVar;
        synchronized (h.class) {
            if (b == null) {
                b = new h();
            }
            hVar = b;
        }
        return hVar;
    }

    /* access modifiers changed from: private */
    public void f() {
        NetworkInfo.State state;
        NetworkInfo.State state2 = NetworkInfo.State.UNKNOWN;
        try {
            state = ((ConnectivityManager) f.getServiceContext().getSystemService("connectivity")).getNetworkInfo(1).getState();
        } catch (Exception e2) {
            state = state2;
        }
        if (NetworkInfo.State.CONNECTED != state) {
            this.d = false;
        } else if (!this.d) {
            this.d = true;
            this.a.postDelayed(new b(), (long) i.N);
            this.f = true;
        }
    }

    public synchronized void b() {
        if (f.isServing) {
            if (!this.h) {
                try {
                    this.c = new a();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                    f.getServiceContext().registerReceiver(this.c, intentFilter);
                    this.e = true;
                    f();
                } catch (Exception e2) {
                }
                this.g = true;
                this.h = true;
            }
        }
    }

    public synchronized void c() {
        if (this.h) {
            try {
                f.getServiceContext().unregisterReceiver(this.c);
            } catch (Exception e2) {
            }
            this.g = false;
            this.h = false;
            this.f = false;
            this.c = null;
        }
    }

    public void d() {
        if (this.h) {
            this.g = true;
            if (!this.f && this.g) {
                this.a.postDelayed(new b(), (long) i.N);
                this.f = true;
            }
        }
    }

    public void e() {
        this.g = false;
    }
}
