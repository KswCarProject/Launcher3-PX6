package com.baidu.location.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.baidu.location.f;

public class b {
    private static b d = null;
    /* access modifiers changed from: private */
    public boolean a = false;
    /* access modifiers changed from: private */
    public String b = null;
    private a c = null;
    /* access modifiers changed from: private */
    public int e = -1;

    public class a extends BroadcastReceiver {
        public a() {
        }

        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                    boolean unused = b.this.a = false;
                    int intExtra = intent.getIntExtra("status", 0);
                    int intExtra2 = intent.getIntExtra("plugged", 0);
                    int intExtra3 = intent.getIntExtra("level", -1);
                    int intExtra4 = intent.getIntExtra("scale", -1);
                    if (intExtra3 <= 0 || intExtra4 <= 0) {
                        int unused2 = b.this.e = -1;
                    } else {
                        int unused3 = b.this.e = (intExtra3 * 100) / intExtra4;
                    }
                    switch (intExtra) {
                        case 2:
                            String unused4 = b.this.b = "4";
                            break;
                        case 3:
                        case 4:
                            String unused5 = b.this.b = "3";
                            break;
                        default:
                            String unused6 = b.this.b = null;
                            break;
                    }
                    switch (intExtra2) {
                        case 1:
                            String unused7 = b.this.b = "6";
                            boolean unused8 = b.this.a = true;
                            return;
                        case 2:
                            String unused9 = b.this.b = "5";
                            boolean unused10 = b.this.a = true;
                            return;
                        default:
                            return;
                    }
                }
            } catch (Exception e) {
                String unused11 = b.this.b = null;
            }
        }
    }

    private b() {
    }

    public static synchronized b a() {
        b bVar;
        synchronized (b.class) {
            if (d == null) {
                d = new b();
            }
            bVar = d;
        }
        return bVar;
    }

    public void b() {
        this.c = new a();
        f.getServiceContext().registerReceiver(this.c, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public void c() {
        if (this.c != null) {
            try {
                f.getServiceContext().unregisterReceiver(this.c);
            } catch (Exception e2) {
            }
        }
        this.c = null;
    }

    public String d() {
        return this.b;
    }

    public boolean e() {
        return this.a;
    }

    public int f() {
        return this.e;
    }
}
