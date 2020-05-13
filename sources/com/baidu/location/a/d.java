package com.baidu.location.a;

import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.f;
import com.baidu.location.f.c;
import com.baidu.location.f.i;
import com.baidu.location.f.k;
import java.util.HashMap;
import java.util.Locale;

public abstract class d {
    public static String c = null;
    public i a = null;
    public com.baidu.location.f.a b = null;
    final Handler d = new a();
    private boolean e = true;
    private boolean f = false;

    public class a extends Handler {
        public a() {
        }

        public void handleMessage(Message message) {
            if (f.isServing) {
                switch (message.what) {
                    case 21:
                        d.this.a(message);
                        return;
                    case BDLocation.TypeCriteriaException:
                    case BDLocation.TypeNetWorkException:
                        d.this.a();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    class b extends com.baidu.location.h.f {
        String a = null;
        String b = null;

        public b() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = com.baidu.location.h.i.c();
            String b2 = com.baidu.location.c.d.a().b();
            if (g == com.baidu.location.h.b.e || g == com.baidu.location.h.b.f) {
                this.h = "http://" + b2 + "/sdk.php";
            }
            if (b2 != null) {
                com.baidu.location.c.f.a().b().b("&host=" + b2);
            }
            String encodeTp4 = Jni.encodeTp4(this.b);
            this.b = null;
            if (this.a == null) {
                this.a = j.b();
            }
            this.k.put("bloc", encodeTp4);
            if (this.a != null) {
                this.k.put("up", this.a);
            }
            StringBuffer stringBuffer = new StringBuffer(512);
            stringBuffer.append(String.format(Locale.CHINA, "&ki=%s&sn=%s", new Object[]{com.baidu.location.h.a.b(f.getServiceContext()), com.baidu.location.h.a.a(f.getServiceContext())}));
            if (stringBuffer.length() > 0) {
                this.k.put("ext", Jni.encode(stringBuffer.toString()));
            }
            this.k.put("trtm", String.format(Locale.CHINA, "%d", new Object[]{Long.valueOf(System.currentTimeMillis())}));
        }

        public void a(String str) {
            this.b = str;
            e();
        }

        public void a(boolean z) {
            BDLocation bDLocation;
            if (!z || this.j == null) {
                Message obtainMessage = d.this.d.obtainMessage(63);
                obtainMessage.obj = "HttpStatus error";
                obtainMessage.sendToTarget();
            } else {
                try {
                    String str = this.j;
                    d.c = str;
                    try {
                        bDLocation = new BDLocation(str);
                        bDLocation.setOperators(c.a().g());
                        if (f.a().f()) {
                            bDLocation.setDirection(f.a().h());
                        }
                    } catch (Exception e) {
                        bDLocation = new BDLocation();
                        bDLocation.setLocType(0);
                    }
                    this.a = null;
                    if (bDLocation.getLocType() == 0 && bDLocation.getLatitude() == Double.MIN_VALUE && bDLocation.getLongitude() == Double.MIN_VALUE) {
                        Message obtainMessage2 = d.this.d.obtainMessage(63);
                        obtainMessage2.obj = "HttpStatus error";
                        obtainMessage2.sendToTarget();
                    } else {
                        Message obtainMessage3 = d.this.d.obtainMessage(21);
                        obtainMessage3.obj = bDLocation;
                        obtainMessage3.sendToTarget();
                    }
                } catch (Exception e2) {
                    Message obtainMessage4 = d.this.d.obtainMessage(63);
                    obtainMessage4.obj = "HttpStatus error";
                    obtainMessage4.sendToTarget();
                }
            }
            if (this.k != null) {
                this.k.clear();
            }
        }
    }

    public String a(String str) {
        if (this.b == null || !this.b.a()) {
            this.b = c.a().f();
        }
        if (this.a == null || !this.a.f()) {
            this.a = k.a().j();
        }
        Location g = com.baidu.location.f.f.a().i() ? com.baidu.location.f.f.a().g() : null;
        if ((this.b == null || this.b.c()) && ((this.a == null || this.a.a() == 0) && g == null)) {
            return null;
        }
        String c2 = a.a().c();
        String format = k.a().g() ? "&cn=32" : String.format(Locale.CHINA, "&cn=%d", new Object[]{Integer.valueOf(c.a().e())});
        if (this.e) {
            this.e = false;
            com.baidu.location.c.f.a().b().a(true);
            String l = k.a().l();
            if (!TextUtils.isEmpty(l)) {
                format = String.format(Locale.CHINA, "%s&mac=%s", new Object[]{format, l.replace(":", "")});
            }
            if (Build.VERSION.SDK_INT > 17) {
            }
        } else if (!this.f) {
            String f2 = j.f();
            if (f2 != null) {
                format = format + f2;
            }
            this.f = true;
        }
        String str2 = format + c2;
        if (str != null) {
            str2 = str + str2;
        }
        return com.baidu.location.h.i.a(this.b, this.a, g, str2, 0);
    }

    public abstract void a();

    public abstract void a(Message message);
}
