package com.baidu.location.h;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.location.f;
import com.baidu.location.f.a;
import com.baidu.location.f.b;
import com.baidu.location.f.c;
import com.baidu.location.f.e;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import org.apache.http.HttpStatus;

public class i {
    public static int A = 3;
    public static int B = 10;
    public static int C = 2;
    public static int D = 7;
    public static int E = 20;
    public static int F = 70;
    public static int G = 120;
    public static float H = 2.0f;
    public static float I = 10.0f;
    public static float J = 50.0f;
    public static float K = 200.0f;
    public static int L = 16;
    public static float M = 0.9f;
    public static int N = LocationClientOption.MIN_AUTO_NOTIFY_INTERVAL;
    public static float O = 0.5f;
    public static float P = 0.0f;
    public static float Q = 0.1f;
    public static int R = 30;
    public static int S = 100;
    public static int T = 0;
    public static int U = 0;
    public static int V = 0;
    public static int W = 420000;
    public static boolean X = true;
    public static boolean Y = true;
    public static int Z = 20;
    public static boolean a = false;
    public static int aa = HttpStatus.SC_MULTIPLE_CHOICES;
    public static int ab = 1000;
    public static long ac = 900000;
    public static long ad = 420000;
    public static long ae = 180000;
    public static long af = 0;
    public static long ag = 15;
    public static long ah = 300000;
    public static int ai = 1000;
    public static int aj = 0;
    public static int ak = 30000;
    public static int al = 30000;
    public static float am = 10.0f;
    public static float an = 6.0f;
    public static float ao = 10.0f;
    public static int ap = 60;
    public static int aq = 70;
    public static int ar = 6;
    private static String as = "http://loc.map.baidu.com/sdk.php";
    private static String at = "http://loc.map.baidu.com/user_err.php";
    private static String au = "http://loc.map.baidu.com/oqur.php";
    private static String av = "http://loc.map.baidu.com/tcu.php";
    private static String aw = "http://loc.map.baidu.com/rtbu.php";
    private static String ax = "http://loc.map.baidu.com/iofd.php";
    private static String ay = "https://sapi.skyhookwireless.com/wps2/location";
    private static String az = "http://loc.map.baidu.com/wloc";
    public static boolean b = false;
    public static boolean c = false;
    public static int d = 0;
    public static String e = "http://loc.map.baidu.com/sdk_ep.php";
    public static String f = "no";
    public static boolean g = false;
    public static boolean h = false;
    public static boolean i = false;
    public static boolean j = false;
    public static boolean k = false;
    public static String l = "gcj02";
    public static boolean m = true;
    public static int n = 3;
    public static double o = 0.0d;
    public static double p = 0.0d;
    public static double q = 0.0d;
    public static double r = 0.0d;
    public static int s = 0;
    public static byte[] t = null;
    public static boolean u = false;
    public static int v = 0;
    public static float w = 1.1f;
    public static float x = 2.2f;
    public static float y = 2.3f;
    public static float z = 3.8f;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0014, code lost:
        r1 = r1 + r5.length();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int a(java.lang.String r4, java.lang.String r5, java.lang.String r6) {
        /*
            r3 = -1
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r4 == 0) goto L_0x000d
            java.lang.String r1 = ""
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x000e
        L_0x000d:
            return r0
        L_0x000e:
            int r1 = r4.indexOf(r5)
            if (r1 == r3) goto L_0x000d
            int r2 = r5.length()
            int r1 = r1 + r2
            int r2 = r4.indexOf(r6, r1)
            if (r2 == r3) goto L_0x000d
            java.lang.String r1 = r4.substring(r1, r2)
            if (r1 == 0) goto L_0x000d
            java.lang.String r2 = ""
            boolean r2 = r1.equals(r2)
            if (r2 != 0) goto L_0x000d
            int r0 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x0032 }
            goto L_0x000d
        L_0x0032:
            r1 = move-exception
            goto L_0x000d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.h.i.a(java.lang.String, java.lang.String, java.lang.String):int");
    }

    public static Object a(Context context, String str) {
        if (context == null) {
            return null;
        }
        try {
            return context.getApplicationContext().getSystemService(str);
        } catch (Throwable th) {
            return null;
        }
    }

    public static Object a(Object obj, String str, Object... objArr) throws Exception {
        Class<?> cls = obj.getClass();
        Class<Integer>[] clsArr = new Class[objArr.length];
        int length = objArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            clsArr[i2] = objArr[i2].getClass();
            if (clsArr[i2] == Integer.class) {
                clsArr[i2] = Integer.TYPE;
            }
        }
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        if (!declaredMethod.isAccessible()) {
            declaredMethod.setAccessible(true);
        }
        return declaredMethod.invoke(obj, objArr);
    }

    public static String a() {
        Calendar instance = Calendar.getInstance();
        int i2 = instance.get(5);
        int i3 = instance.get(1);
        int i4 = instance.get(11);
        int i5 = instance.get(12);
        int i6 = instance.get(13);
        return String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(i2), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
    }

    public static String a(a aVar, com.baidu.location.f.i iVar, Location location, String str, int i2) {
        String a2;
        String b2;
        StringBuffer stringBuffer = new StringBuffer(1024);
        if (!(aVar == null || (b2 = b.a().b(aVar)) == null)) {
            stringBuffer.append(b2);
        }
        if (iVar != null) {
            String b3 = i2 == 0 ? iVar.b() : iVar.c();
            if (b3 != null) {
                stringBuffer.append(b3);
            }
        }
        if (location != null) {
            String b4 = (d == 0 || i2 == 0) ? e.b(location) : e.c(location);
            if (b4 != null) {
                stringBuffer.append(b4);
            }
        }
        boolean z2 = false;
        if (i2 == 0) {
            z2 = true;
        }
        String a3 = c.a().a(z2);
        if (a3 != null) {
            stringBuffer.append(a3);
        }
        if (str != null) {
            stringBuffer.append(str);
        }
        String d2 = com.baidu.location.c.b.a().d();
        if (!TextUtils.isEmpty(d2)) {
            stringBuffer.append("&bc=").append(d2);
        }
        if (i2 == 0) {
        }
        if (!(aVar == null || (a2 = c.a().a(aVar)) == null || a2.length() + stringBuffer.length() >= 750)) {
            stringBuffer.append(a2);
        }
        String stringBuffer2 = stringBuffer.toString();
        if (location == null || iVar == null) {
            n = 3;
        } else {
            try {
                float speed = location.getSpeed();
                int i3 = d;
                int d3 = iVar.d();
                int a4 = iVar.a();
                boolean e2 = iVar.e();
                if (speed < an && ((i3 == 1 || i3 == 0) && (d3 < ap || e2))) {
                    n = 1;
                } else if (speed >= ao || (!(i3 == 1 || i3 == 0 || i3 == 3) || (d3 >= aq && a4 <= ar))) {
                    n = 3;
                } else {
                    n = 2;
                }
            } catch (Exception e3) {
                n = 3;
            }
        }
        return stringBuffer2;
    }

    public static String a(File file) {
        if (!file.isFile()) {
            return null;
        }
        byte[] bArr = new byte[1024];
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(file);
            while (true) {
                int read = fileInputStream.read(bArr, 0, 1024);
                if (read != -1) {
                    instance.update(bArr, 0, read);
                } else {
                    fileInputStream.close();
                    return new BigInteger(1, instance.digest()).toString(16);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static boolean a(Context context) {
        NetworkInfo[] allNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (allNetworkInfo = connectivityManager.getAllNetworkInfo()) == null) {
            return false;
        }
        for (NetworkInfo state : allNetworkInfo) {
            if (state.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static boolean a(BDLocation bDLocation) {
        int locType = bDLocation.getLocType();
        return locType > 100 && locType < 200;
    }

    public static int b(Object obj, String str, Object... objArr) throws Exception {
        Class<?> cls = obj.getClass();
        Class<Integer>[] clsArr = new Class[objArr.length];
        int length = objArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            clsArr[i2] = objArr[i2].getClass();
            if (clsArr[i2] == Integer.class) {
                clsArr[i2] = Integer.TYPE;
            }
        }
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        if (!declaredMethod.isAccessible()) {
            declaredMethod.setAccessible(true);
        }
        return ((Integer) declaredMethod.invoke(obj, objArr)).intValue();
    }

    public static String b() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (true) {
                    if (inetAddresses.hasMoreElements()) {
                        InetAddress nextElement = inetAddresses.nextElement();
                        if (!nextElement.isLoopbackAddress() && (nextElement instanceof Inet4Address)) {
                            byte[] address = nextElement.getAddress();
                            String str = "";
                            int i2 = 0;
                            while (true) {
                                int i3 = i2;
                                String str2 = str;
                                if (i3 >= address.length) {
                                    return str2;
                                }
                                String hexString = Integer.toHexString(address[i3] & 255);
                                if (hexString.length() == 1) {
                                    hexString = '0' + hexString;
                                }
                                str = str2 + hexString;
                                i2 = i3 + 1;
                            }
                        }
                    }
                }
            }
        } catch (Exception e2) {
        }
        return null;
    }

    public static String c() {
        return as;
    }

    public static String d() {
        return av;
    }

    public static String e() {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return null;
        }
        try {
            String path = Environment.getExternalStorageDirectory().getPath();
            File file = new File(path + "/baidu/tempdata");
            if (file.exists()) {
                return path;
            }
            file.mkdirs();
            return path;
        } catch (Exception e2) {
            return null;
        }
    }

    public static String f() {
        String e2 = e();
        if (e2 == null) {
            return null;
        }
        return e2 + "/baidu/tempdata";
    }

    public static String g() {
        try {
            File file = new File(f.getServiceContext().getFilesDir() + File.separator + "lldt");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } catch (Exception e2) {
            return null;
        }
    }
}
