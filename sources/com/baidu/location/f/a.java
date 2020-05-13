package com.baidu.location.f;

import java.util.Locale;

public class a {
    public int a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public long g;
    public int h;
    public char i;
    private boolean j;

    public a() {
        this.a = -1;
        this.b = -1;
        this.c = -1;
        this.d = -1;
        this.e = Integer.MAX_VALUE;
        this.f = Integer.MAX_VALUE;
        this.g = 0;
        this.h = -1;
        this.i = 0;
        this.j = false;
        this.g = System.currentTimeMillis();
    }

    public a(int i2, int i3, int i4, int i5, int i6, char c2) {
        this.a = -1;
        this.b = -1;
        this.c = -1;
        this.d = -1;
        this.e = Integer.MAX_VALUE;
        this.f = Integer.MAX_VALUE;
        this.g = 0;
        this.h = -1;
        this.i = 0;
        this.j = false;
        this.a = i2;
        this.b = i3;
        this.c = i4;
        this.d = i5;
        this.h = i6;
        this.i = c2;
        this.g = System.currentTimeMillis();
    }

    public a(a aVar) {
        this(aVar.a, aVar.b, aVar.c, aVar.d, aVar.h, aVar.i);
        this.g = aVar.g;
    }

    public boolean a() {
        return System.currentTimeMillis() - this.g < 3000;
    }

    public boolean a(a aVar) {
        return this.a == aVar.a && this.b == aVar.b && this.d == aVar.d && this.c == aVar.c;
    }

    public boolean b() {
        return this.a > -1 && this.b > 0;
    }

    public boolean c() {
        return this.a == -1 && this.b == -1 && this.d == -1 && this.c == -1;
    }

    public boolean d() {
        return this.a > -1 && this.b > -1 && this.d == -1 && this.c == -1;
    }

    public boolean e() {
        return this.a > -1 && this.b > -1 && this.d > -1 && this.c > -1;
    }

    public void f() {
        this.j = true;
    }

    public String g() {
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append(this.b + 23);
        stringBuffer.append("H");
        stringBuffer.append(this.a + 45);
        stringBuffer.append("K");
        stringBuffer.append(this.d + 54);
        stringBuffer.append("Q");
        stringBuffer.append(this.c + 203);
        return stringBuffer.toString();
    }

    public String h() {
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&nw=");
        stringBuffer.append(this.i);
        stringBuffer.append(String.format(Locale.CHINA, "&cl=%d|%d|%d|%d&cl_s=%d", new Object[]{Integer.valueOf(this.c), Integer.valueOf(this.d), Integer.valueOf(this.a), Integer.valueOf(this.b), Integer.valueOf(this.h)}));
        if (this.j) {
            stringBuffer.append("&newcl=1");
        }
        return stringBuffer.toString();
    }
}
