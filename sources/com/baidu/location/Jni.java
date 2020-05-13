package com.baidu.location;

import com.baidu.location.b.a.b;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.protocol.HTTP;

public class Jni {
    private static int a = 0;
    private static int b = 1;
    private static int c = 2;
    private static int d = 11;
    private static int e = 12;
    private static int f = 13;
    private static int g = 14;
    private static int h = 15;
    private static int i = 1024;
    private static boolean j;

    static {
        j = false;
        try {
            System.loadLibrary("locSDK6a");
        } catch (UnsatisfiedLinkError e2) {
            e2.printStackTrace();
            j = true;
        }
    }

    public static String Encrypt(String str) {
        if (j) {
            return null;
        }
        try {
            return URLEncoder.encode(a(encrypt(str.getBytes())), HTTP.UTF_8);
        } catch (Exception e2) {
            return "";
        }
    }

    private static String a(byte[] bArr) {
        try {
            return b.a(bArr, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e2) {
            return "";
        }
    }

    private static native String a(byte[] bArr, int i2);

    private static native String b(double d2, double d3, int i2, int i3);

    private static native String c(byte[] bArr, int i2);

    public static double[] coorEncrypt(double d2, double d3, String str) {
        double[] dArr = {0.0d, 0.0d};
        if (j) {
            return dArr;
        }
        int i2 = -1;
        if (str.equals(BDLocation.BDLOCATION_GCJ02_TO_BD09)) {
            i2 = a;
        } else if (str.equals(BDLocation.BDLOCATION_GCJ02_TO_BD09LL)) {
            i2 = b;
        } else if (str.equals("gcj02")) {
            i2 = c;
        } else if (str.equals("gps2gcj")) {
            i2 = d;
        } else if (str.equals(BDLocation.BDLOCATION_BD09_TO_GCJ02)) {
            i2 = e;
        } else if (str.equals(BDLocation.BDLOCATION_BD09LL_TO_GCJ02)) {
            i2 = f;
        } else if (str.equals("wgs842mc")) {
            i2 = h;
        }
        try {
            String[] split = b(d2, d3, i2, 132456).split(":");
            dArr[0] = Double.parseDouble(split[0]);
            dArr[1] = Double.parseDouble(split[1]);
        } catch (UnsatisfiedLinkError e2) {
        }
        return dArr;
    }

    public static String decodeIBeacon(byte[] bArr, byte[] bArr2) {
        if (j) {
            return null;
        }
        return ib(bArr, bArr2);
    }

    private static native String ee(String str, int i2);

    public static String en1(String str) {
        int i2 = 740;
        if (j) {
            return "err!";
        }
        if (str == null) {
            return "null";
        }
        byte[] bytes = str.getBytes();
        byte[] bArr = new byte[i];
        int length = bytes.length;
        if (length <= 740) {
            i2 = length;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            if (bytes[i4] != 0) {
                bArr[i3] = bytes[i4];
                i3++;
            }
        }
        try {
            return a(bArr, 132456);
        } catch (UnsatisfiedLinkError e2) {
            e2.printStackTrace();
            return "err!";
        }
    }

    public static String encode(String str) {
        return j ? "err!" : en1(str) + "|tp=3";
    }

    public static String encode2(String str) {
        if (j) {
            return "err!";
        }
        if (str == null) {
            return "null";
        }
        try {
            return c(str.getBytes(), 132456);
        } catch (UnsatisfiedLinkError e2) {
            e2.printStackTrace();
            return "err!";
        }
    }

    public static Long encode3(String str) {
        String str2;
        if (j) {
            return null;
        }
        try {
            str2 = new String(str.getBytes(), HTTP.UTF_8);
        } catch (Exception e2) {
            str2 = "";
        }
        try {
            return Long.valueOf(murmur(str2));
        } catch (UnsatisfiedLinkError e3) {
            e3.printStackTrace();
            return null;
        }
    }

    private static native String encodeNotLimit(String str, int i2);

    public static String encodeOfflineLocationUpdateRequest(String str) {
        String str2;
        String str3;
        if (j) {
            return "err!";
        }
        try {
            str2 = new String(str.getBytes(), HTTP.UTF_8);
        } catch (Exception e2) {
            str2 = "";
        }
        try {
            str3 = encodeNotLimit(str2, 132456);
        } catch (UnsatisfiedLinkError e3) {
            e3.printStackTrace();
            str3 = "err!";
        }
        return str3 + "|tp=3";
    }

    public static String encodeTp4(String str) {
        String str2;
        String str3;
        if (j) {
            return "err!";
        }
        try {
            str2 = new String(str.getBytes(), HTTP.UTF_8);
        } catch (Exception e2) {
            str2 = "";
        }
        try {
            str3 = ee(str2, 132456);
        } catch (UnsatisfiedLinkError e3) {
            e3.printStackTrace();
            str3 = "err!";
        }
        return str3 + "|tp=4";
    }

    private static native byte[] encrypt(byte[] bArr);

    private static native void f(byte[] bArr, byte[] bArr2);

    private static native String g(byte[] bArr);

    public static String getSkyKey() {
        if (j) {
            return "err!";
        }
        try {
            return sky();
        } catch (UnsatisfiedLinkError e2) {
            e2.printStackTrace();
            return "err!";
        }
    }

    public static String gtr2(String str) {
        if (j) {
            return null;
        }
        try {
            String g2 = g(str.getBytes());
            if (g2 == null || g2.length() < 2 || "no".equals(g2)) {
                return null;
            }
            return g2;
        } catch (UnsatisfiedLinkError e2) {
            return null;
        }
    }

    private static native String ib(byte[] bArr, byte[] bArr2);

    private static native long murmur(String str);

    private static native String sky();

    public static void tr2(String str, String str2) {
        if (!j) {
            try {
                f(str.getBytes(), str2.getBytes());
            } catch (UnsatisfiedLinkError e2) {
                e2.printStackTrace();
            }
        }
    }
}
