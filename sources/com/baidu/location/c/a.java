package com.baidu.location.c;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.baidu.location.a.e;
import com.baidu.location.f;
import com.baidu.location.h.i;

public class a {
    private static a a = null;
    /* access modifiers changed from: private */
    public boolean b = false;
    /* access modifiers changed from: private */
    public Handler c = null;
    private AlarmManager d = null;
    private C0002a e = null;
    /* access modifiers changed from: private */
    public PendingIntent f = null;
    private long g = 0;

    /* renamed from: com.baidu.location.c.a$a  reason: collision with other inner class name */
    private class C0002a extends BroadcastReceiver {
        private C0002a() {
        }

        public void onReceive(Context context, Intent intent) {
            if (a.this.b && intent.getAction().equals("com.baidu.location.autonotifyloc_6.2.3") && a.this.c != null) {
                PendingIntent unused = a.this.f = null;
                a.this.c.sendEmptyMessage(1);
            }
        }
    }

    private a() {
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
    public void f() {
        if (System.currentTimeMillis() - this.g >= 1000) {
            if (this.f != null) {
                this.d.cancel(this.f);
                this.f = null;
            }
            if (this.f == null) {
                this.f = PendingIntent.getBroadcast(f.getServiceContext(), 0, new Intent("com.baidu.location.autonotifyloc_6.2.3"), 134217728);
                this.d.set(0, System.currentTimeMillis() + ((long) i.T), this.f);
            }
            Message message = new Message();
            message.what = 22;
            if (System.currentTimeMillis() - this.g >= ((long) i.U)) {
                this.g = System.currentTimeMillis();
                if (!com.baidu.location.f.f.a().i()) {
                    e.b().b(message);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void g() {
        if (this.b) {
            try {
                if (this.f != null) {
                    this.d.cancel(this.f);
                    this.f = null;
                }
                f.getServiceContext().unregisterReceiver(this.e);
            } catch (Exception e2) {
            }
            this.d = null;
            this.e = null;
            this.c = null;
            this.b = false;
        }
    }

    public void b() {
        if (!this.b && i.T >= 10000) {
            if (this.c == null) {
                this.c = new Handler() {
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case 1:
                                try {
                                    a.this.f();
                                    return;
                                } catch (Exception e) {
                                    return;
                                }
                            case 2:
                                try {
                                    a.this.g();
                                    return;
                                } catch (Exception e2) {
                                    return;
                                }
                            default:
                                return;
                        }
                    }
                };
            }
            this.d = (AlarmManager) f.getServiceContext().getSystemService("alarm");
            this.e = new C0002a();
            f.getServiceContext().registerReceiver(this.e, new IntentFilter("com.baidu.location.autonotifyloc_6.2.3"));
            this.f = PendingIntent.getBroadcast(f.getServiceContext(), 0, new Intent("com.baidu.location.autonotifyloc_6.2.3"), 134217728);
            this.d.set(0, System.currentTimeMillis() + ((long) i.T), this.f);
            this.b = true;
            this.g = System.currentTimeMillis();
        }
    }

    public void c() {
        if (this.b && this.c != null) {
            this.c.sendEmptyMessage(2);
        }
    }

    public void d() {
        if (this.b && this.c != null) {
            this.c.sendEmptyMessage(1);
        }
    }

    public void e() {
        if (this.b && this.c != null) {
            this.c.sendEmptyMessage(1);
        }
    }
}
