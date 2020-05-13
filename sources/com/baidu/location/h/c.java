package com.baidu.location.h;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.baidu.location.b.b.a;
import com.baidu.location.f;
import java.util.Locale;

public class c {
    public static String c = null;
    public static String d = null;
    public static String e = null;
    public static String f = null;
    private static c g = null;
    public String a = null;
    public String b = null;
    private boolean h = false;

    private c() {
        if (f.getServiceContext() != null) {
            a(f.getServiceContext());
        }
    }

    public static c a() {
        if (g == null) {
            g = new c();
        }
        return g;
    }

    public String a(boolean z) {
        return a(z, (String) null);
    }

    public String a(boolean z, String str) {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append("&sdk=");
        stringBuffer.append(6.23f);
        if (z) {
            if (i.f.equals("all")) {
                stringBuffer.append("&addr=all");
            }
            if (i.g || i.i || i.j || i.h) {
                stringBuffer.append("&sema=");
                if (i.g) {
                    stringBuffer.append("aptag|");
                }
                if (i.h) {
                    stringBuffer.append("aptagd|");
                }
                if (i.i) {
                    stringBuffer.append("poiregion|");
                }
                if (i.j) {
                    stringBuffer.append("regular");
                }
            }
        }
        if (z) {
            if (str == null) {
                stringBuffer.append("&coor=gcj02");
            } else {
                stringBuffer.append("&coor=");
                stringBuffer.append(str);
            }
        }
        if (this.b == null) {
            stringBuffer.append("&im=");
            stringBuffer.append(this.a);
        } else {
            stringBuffer.append("&cu=");
            stringBuffer.append(this.b);
        }
        stringBuffer.append("&fw=");
        stringBuffer.append(f.getFrameVersion());
        stringBuffer.append("&lt=1");
        stringBuffer.append("&mb=");
        stringBuffer.append(Build.MODEL);
        String b2 = i.b();
        if (b2 != null) {
            stringBuffer.append("&laip=");
            stringBuffer.append(b2);
        }
        float e2 = com.baidu.location.a.f.a().e();
        if (e2 != 0.0f) {
            stringBuffer.append("&altv=");
            stringBuffer.append(String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(e2)}));
        }
        stringBuffer.append("&resid=");
        stringBuffer.append("12");
        stringBuffer.append("&os=A");
        stringBuffer.append(Build.VERSION.SDK);
        if (z) {
            stringBuffer.append("&sv=");
            String str2 = Build.VERSION.RELEASE;
            if (str2 != null && str2.length() > 6) {
                str2 = str2.substring(0, 6);
            }
            stringBuffer.append(str2);
        }
        return stringBuffer.toString();
    }

    public void a(Context context) {
        if (context != null && !this.h) {
            try {
                this.a = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
            } catch (Exception e2) {
                this.a = "NULL";
            }
            try {
                this.b = a.a(context);
            } catch (Exception e3) {
                this.b = null;
            }
            try {
                c = context.getPackageName();
            } catch (Exception e4) {
                c = null;
            }
            this.h = true;
        }
    }

    public void a(String str, String str2) {
        d = str;
        c = str2;
    }

    public String b() {
        return this.b != null ? "v6.23|" + this.b + "|" + Build.MODEL : "v6.23|" + this.a + "|" + Build.MODEL;
    }

    public String c() {
        return c != null ? b() + "|" + c : b();
    }

    public String d() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.b == null) {
            stringBuffer.append("&im=");
            stringBuffer.append(this.a);
        } else {
            stringBuffer.append("&cu=");
            stringBuffer.append(this.b);
        }
        stringBuffer.append("&mb=");
        stringBuffer.append(Build.MODEL);
        stringBuffer.append("&os=A");
        stringBuffer.append(Build.VERSION.SDK);
        stringBuffer.append("&prod=");
        stringBuffer.append(d + ":" + c);
        stringBuffer.append("&resid=");
        stringBuffer.append("12");
        return stringBuffer.toString();
    }
}
