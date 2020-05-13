package com.baidu.location.h;

public class g {
    private String a;
    private String b;
    private boolean c;

    public g(String str, boolean z, String str2) {
        this.b = str;
        this.c = z;
        this.a = str2;
    }

    public String a() {
        return this.b;
    }

    public String toString() {
        return "SDCardInfo [label=" + this.a + ", mountPoint=" + this.b + ", isRemoveable=" + this.c + "]";
    }
}
