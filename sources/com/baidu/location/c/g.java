package com.baidu.location.c;

import com.baidu.location.a.a;
import com.baidu.location.f.b;
import com.baidu.location.f.j;
import com.baidu.location.h.c;
import java.util.Locale;

public class g {
    private long a = 0;
    private long b = 0;
    private long c = 0;
    private long d = 0;
    private int e = 0;
    private String f = null;
    private String g = null;
    private String h = null;

    public void a() {
        this.a = 0;
        this.b = 0;
        this.c = 0;
        this.d = 0;
        this.e = 0;
        this.f = null;
        this.g = null;
        this.h = null;
    }

    public void a(long j) {
        this.a = j;
    }

    public void a(String str) {
        this.g = str;
    }

    public void a(boolean z) {
        if (z) {
            this.e = 1;
        } else {
            this.e = 0;
        }
    }

    public String b() {
        StringBuffer stringBuffer = new StringBuffer();
        if (j.a().g()) {
            this.f = "&cn=32";
        } else {
            this.f = String.format(Locale.CHINA, "&cn=%d", new Object[]{Integer.valueOf(b.a().e())});
        }
        stringBuffer.append(this.f);
        stringBuffer.append(String.format(Locale.CHINA, "&fir=%d&tim=%d&dsc=%d&det=%d&ded=%d&typ=%s", new Object[]{Integer.valueOf(this.e), Long.valueOf(this.a), Long.valueOf(this.b - this.a), Long.valueOf(this.c - this.b), Long.valueOf(this.d - this.c), this.g}));
        if (this.h != null) {
            stringBuffer.append(this.h);
        }
        stringBuffer.append(c.a().a(false));
        stringBuffer.append(a.a().c());
        return stringBuffer.toString();
    }

    public void b(long j) {
        this.b = j;
    }

    public void b(String str) {
        if (this.h == null) {
            this.h = str;
            return;
        }
        this.h = String.format("%s%s", new Object[]{this.h, str});
    }

    public void c(long j) {
        this.c = j;
    }

    public void d(long j) {
        this.d = j;
    }
}
