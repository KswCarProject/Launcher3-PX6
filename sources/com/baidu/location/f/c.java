package com.baidu.location.f;

import android.support.v4.os.EnvironmentCompat;

public final class c {
    public static d a() {
        return b.a();
    }

    public static String a(int i) {
        if (k.a().g()) {
            return "WIFI";
        }
        switch (i) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return "2G";
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return "3G";
            case 13:
                return "4G";
            default:
                return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }
}
