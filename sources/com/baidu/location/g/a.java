package com.baidu.location.g;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;
import com.baidu.location.LLSInterface;
import com.baidu.location.a.e;
import com.baidu.location.a.g;
import com.baidu.location.a.i;
import com.baidu.location.a.j;
import com.baidu.location.c.b;
import com.baidu.location.c.c;
import com.baidu.location.c.h;
import com.baidu.location.e.d;
import com.baidu.location.f;
import com.baidu.location.f.k;
import org.apache.http.HttpStatus;

public class a extends Service implements LLSInterface {
    static C0006a a = null;
    private static long f = 0;
    Messenger b = null;
    private Looper c;
    private HandlerThread d;
    private boolean e = false;

    /* renamed from: com.baidu.location.g.a$a  reason: collision with other inner class name */
    public class C0006a extends Handler {
        public C0006a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            if (f.isServing) {
                switch (message.what) {
                    case 11:
                        a.this.a(message);
                        break;
                    case 12:
                        a.this.b(message);
                        break;
                    case 15:
                        a.this.c(message);
                        break;
                    case 22:
                        e.b().b(message);
                        break;
                    case 28:
                        e.b().a(true);
                        break;
                    case 41:
                        e.b().h();
                        break;
                    case HttpStatus.SC_UNAUTHORIZED /*401*/:
                        try {
                            message.getData();
                            break;
                        } catch (Exception e) {
                            break;
                        }
                }
            }
            if (message.what == 1) {
                a.this.c();
            }
            if (message.what == 0) {
                a.this.b();
            }
            super.handleMessage(message);
        }
    }

    public static Handler a() {
        return a;
    }

    /* access modifiers changed from: private */
    public void a(Message message) {
        Log.d("baidu_location_service", "baidu location service register ...");
        com.baidu.location.a.a.a().a(message);
        d.a();
        c.a().d();
        g.b().c();
    }

    /* access modifiers changed from: private */
    public void b() {
        com.baidu.location.f.c.a().b();
        k.a().b();
        com.baidu.location.h.c.a();
        e.b().c();
        com.baidu.location.e.a.a().b();
        b.a().b();
        c.a().b();
    }

    /* access modifiers changed from: private */
    public void b(Message message) {
        com.baidu.location.a.a.a().b(message);
    }

    /* access modifiers changed from: private */
    public void c() {
        k.a().c();
        d.a().n();
        com.baidu.location.f.f.a().e();
        h.a().c();
        c.a().c();
        b.a().c();
        com.baidu.location.c.a.a().c();
        com.baidu.location.f.c.a().c();
        e.b().d();
        j.e();
        com.baidu.location.a.a.a().b();
        com.baidu.location.c.e.a().b();
        Log.d("baidu_location_service", "baidu location service has stoped ...");
        if (!this.e) {
            Process.killProcess(Process.myPid());
        }
    }

    /* access modifiers changed from: private */
    public void c(Message message) {
        com.baidu.location.a.a.a().c(message);
    }

    public double getVersion() {
        return 6.230000019073486d;
    }

    public IBinder onBind(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            com.baidu.location.h.c.f = extras.getString("key");
            com.baidu.location.h.c.e = extras.getString("sign");
            this.e = extras.getBoolean("kill_process");
        }
        return this.b.getBinder();
    }

    public void onCreate(Context context) {
        f = System.currentTimeMillis();
        this.d = i.a();
        this.c = this.d.getLooper();
        a = new C0006a(this.c);
        this.b = new Messenger(a);
        a.sendEmptyMessage(0);
        Log.d("baidu_location_service", "baidu location service start1 ..." + Process.myPid());
    }

    public void onDestroy() {
        a.sendEmptyMessage(1);
        Log.d("baidu_location_service", "baidu location service stop ...");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    public boolean onUnBind(Intent intent) {
        return false;
    }
}
