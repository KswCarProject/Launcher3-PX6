package com.baidu.location.c;

import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import com.baidu.location.Jni;
import com.baidu.location.LocationClientOption;
import com.baidu.location.h.c;
import com.baidu.location.h.f;
import com.baidu.location.h.h;
import com.szchoiceway.index.EventUtils;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

public class e {
    public static String f = "0";
    private static e j = null;
    private a A;
    private boolean B;
    private boolean C;
    private int D;
    private float E;
    private float F;
    private long G;
    private int H;
    private Handler I;
    private byte[] J;
    private byte[] K;
    private int L;
    private List<Byte> M;
    private boolean N;
    long a;
    Location b;
    Location c;
    StringBuilder d;
    long e;
    int g;
    double h;
    double i;
    private int k;
    private double l;
    private String m;
    private int n;
    private int o;
    private int p;
    private int q;
    private double r;
    private double s;
    private double t;
    private int u;
    private int v;
    private int w;
    private int x;
    private int y;
    private long z;

    class a extends f {
        String a = null;

        public a() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = "http://loc.map.baidu.com/cc.php";
            String encode = Jni.encode(this.a);
            this.a = null;
            this.k.put("q", encode);
        }

        public void a(String str) {
            this.a = str;
            e();
        }

        public void a(boolean z) {
            if (z && this.j != null) {
                try {
                    JSONObject jSONObject = new JSONObject(this.j);
                    jSONObject.put("prod", c.c);
                    jSONObject.put("uptime", System.currentTimeMillis());
                    e.this.e(jSONObject.toString());
                } catch (Exception e) {
                }
            }
            if (this.k != null) {
                this.k.clear();
            }
        }
    }

    private e() {
        this.k = 1;
        this.l = 0.699999988079071d;
        this.m = "3G|4G";
        this.n = 1;
        this.o = 307200;
        this.p = 15;
        this.q = 1;
        this.r = 3.5d;
        this.s = 3.0d;
        this.t = 0.5d;
        this.u = HttpStatus.SC_MULTIPLE_CHOICES;
        this.v = 60;
        this.w = 0;
        this.x = 60;
        this.y = 0;
        this.z = 0;
        this.A = null;
        this.B = false;
        this.C = false;
        this.D = 0;
        this.E = 0.0f;
        this.F = 0.0f;
        this.G = 0;
        this.H = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        this.a = 0;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = 0;
        this.I = null;
        this.J = new byte[4];
        this.K = null;
        this.L = 0;
        this.M = null;
        this.N = false;
        this.g = 0;
        this.h = 116.22345545d;
        this.i = 40.245667323d;
        this.I = new Handler();
    }

    public static e a() {
        if (j == null) {
            j = new e();
        }
        return j;
    }

    /* access modifiers changed from: private */
    public String a(File file, String str) {
        String uuid = UUID.randomUUID().toString();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setReadTimeout(LocationClientOption.MIN_AUTO_NOTIFY_INTERVAL);
            httpURLConnection.setConnectTimeout(LocationClientOption.MIN_AUTO_NOTIFY_INTERVAL);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod(HttpPost.METHOD_NAME);
            httpURLConnection.setRequestProperty("Charset", "utf-8");
            httpURLConnection.setRequestProperty("connection", "close");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + uuid);
            if (file != null && file.exists()) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("--");
                stringBuffer.append(uuid);
                stringBuffer.append("\r\n");
                stringBuffer.append("Content-Disposition: form-data; name=\"location_dat\"; filename=\"" + file.getName() + "\"" + "\r\n");
                stringBuffer.append("Content-Type: application/octet-stream; charset=utf-8" + "\r\n");
                stringBuffer.append("\r\n");
                dataOutputStream.write(stringBuffer.toString().getBytes());
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    dataOutputStream.write(bArr, 0, read);
                }
                fileInputStream.close();
                dataOutputStream.write("\r\n".getBytes());
                dataOutputStream.write(("--" + uuid + "--" + "\r\n").getBytes());
                dataOutputStream.flush();
                int responseCode = httpURLConnection.getResponseCode();
                outputStream.close();
                httpURLConnection.disconnect();
                this.y += HttpStatus.SC_BAD_REQUEST;
                c(this.y);
                if (responseCode == 200) {
                    return "1";
                }
            }
        } catch (IOException | MalformedURLException e2) {
        }
        return "0";
    }

    private boolean a(String str, Context context) {
        boolean z2;
        boolean z3 = false;
        try {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
            if (runningAppProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
                    if (next.processName.equals(str)) {
                        int i2 = next.importance;
                        if (i2 == 200 || i2 == 100) {
                            z2 = true;
                            z3 = z2;
                        }
                    }
                    z2 = z3;
                    z3 = z2;
                }
            }
        } catch (Exception e2) {
        }
        return z3;
    }

    private byte[] a(int i2) {
        return new byte[]{(byte) (i2 & 255), (byte) ((65280 & i2) >> 8), (byte) ((16711680 & i2) >> 16), (byte) ((-16777216 & i2) >> 24)};
    }

    private byte[] a(String str) {
        int i2 = 0;
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes();
        byte nextInt = (byte) new Random().nextInt(255);
        byte nextInt2 = (byte) new Random().nextInt(255);
        byte[] bArr = new byte[(bytes.length + 2)];
        int length = bytes.length;
        int i3 = 0;
        while (i2 < length) {
            bArr[i3] = (byte) (bytes[i2] ^ nextInt);
            i2++;
            i3++;
        }
        int i4 = i3 + 1;
        bArr[i3] = nextInt;
        int i5 = i4 + 1;
        bArr[i4] = nextInt2;
        return bArr;
    }

    private String b(String str) {
        Calendar instance = Calendar.getInstance();
        return String.format(str, new Object[]{Integer.valueOf(instance.get(1)), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(5))});
    }

    private void b(int i2) {
        byte[] a2 = a(i2);
        for (int i3 = 0; i3 < 4; i3++) {
            this.M.add(Byte.valueOf(a2[i3]));
        }
    }

    /* access modifiers changed from: private */
    public void b(Location location) {
        c(location);
        g();
    }

    private void c() {
        if (!this.N) {
            this.N = true;
            d(c.c);
            i();
            d();
        }
    }

    private void c(int i2) {
        if (i2 != 0) {
            try {
                File file = new File(h.a + "/grtcf.dat");
                if (!file.exists()) {
                    File file2 = new File(h.a);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file.createNewFile()) {
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                        randomAccessFile.seek(2);
                        randomAccessFile.writeInt(0);
                        randomAccessFile.seek(8);
                        byte[] bytes = "1980_01_01:0".getBytes();
                        randomAccessFile.writeInt(bytes.length);
                        randomAccessFile.write(bytes);
                        randomAccessFile.seek(200);
                        randomAccessFile.writeBoolean(false);
                        randomAccessFile.seek(800);
                        randomAccessFile.writeBoolean(false);
                        randomAccessFile.close();
                    } else {
                        return;
                    }
                }
                RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
                randomAccessFile2.seek(8);
                byte[] bytes2 = (b("%d_%02d_%02d") + ":" + i2).getBytes();
                randomAccessFile2.writeInt(bytes2.length);
                randomAccessFile2.write(bytes2);
                randomAccessFile2.close();
            } catch (Exception e2) {
            }
        }
    }

    private void c(Location location) {
        if (System.currentTimeMillis() - this.a >= ((long) this.H) && location != null) {
            if (location != null && location.hasSpeed() && location.getSpeed() > this.E) {
                this.E = location.getSpeed();
            }
            try {
                if (this.M == null) {
                    this.M = new ArrayList();
                    h();
                    d(location);
                } else {
                    e(location);
                }
            } catch (Exception e2) {
            }
            this.L++;
        }
    }

    private void c(String str) {
        if (str != null) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("on")) {
                    this.k = jSONObject.getInt("on");
                }
                if (jSONObject.has("bash")) {
                    this.l = jSONObject.getDouble("bash");
                }
                if (jSONObject.has("net")) {
                    this.m = jSONObject.getString("net");
                }
                if (jSONObject.has("tcon")) {
                    this.n = jSONObject.getInt("tcon");
                }
                if (jSONObject.has("tcsh")) {
                    this.o = jSONObject.getInt("tcsh");
                }
                if (jSONObject.has("per")) {
                    this.p = jSONObject.getInt("per");
                }
                if (jSONObject.has("chdron")) {
                    this.q = jSONObject.getInt("chdron");
                }
                if (jSONObject.has("spsh")) {
                    this.r = jSONObject.getDouble("spsh");
                }
                if (jSONObject.has("acsh")) {
                    this.s = jSONObject.getDouble("acsh");
                }
                if (jSONObject.has("stspsh")) {
                    this.t = jSONObject.getDouble("stspsh");
                }
                if (jSONObject.has("drstsh")) {
                    this.u = jSONObject.getInt("drstsh");
                }
                if (jSONObject.has("stper")) {
                    this.v = jSONObject.getInt("stper");
                }
                if (jSONObject.has("nondron")) {
                    this.w = jSONObject.getInt("nondron");
                }
                if (jSONObject.has("nondrper")) {
                    this.x = jSONObject.getInt("nondrper");
                }
                if (jSONObject.has("uptime")) {
                    this.z = jSONObject.getLong("uptime");
                }
                j();
            } catch (JSONException e2) {
            }
        }
    }

    private void d() {
        String str = null;
        if (0 == 0) {
            str = "6.2.3";
        }
        String[] split = str.split("\\.");
        int length = split.length;
        this.J[0] = 0;
        this.J[1] = 0;
        this.J[2] = 0;
        this.J[3] = 0;
        if (length >= 4) {
            length = 4;
        }
        int i2 = 0;
        while (i2 < length) {
            try {
                this.J[i2] = (byte) (Integer.valueOf(split[i2]).intValue() & 255);
                i2++;
            } catch (Exception e2) {
            }
        }
        this.K = a(c.c + ":" + c.a().b);
    }

    private void d(Location location) {
        char c2 = 0;
        this.e = System.currentTimeMillis();
        b((int) (this.e / 1000));
        b((int) (location.getLongitude() * 1000000.0d));
        b((int) (location.getLatitude() * 1000000.0d));
        char c3 = location.hasBearing() ? (char) 0 : 1;
        if (!location.hasSpeed()) {
            c2 = 1;
        }
        if (c3 > 0) {
            this.M.add((byte) 32);
        } else {
            this.M.add(Byte.valueOf((byte) (((byte) (((int) (location.getBearing() / 15.0f)) & 255)) & -33)));
        }
        if (c2 > 0) {
            this.M.add(Byte.valueOf(EventUtils.CMD_FREQ_SEL));
        } else {
            this.M.add(Byte.valueOf((byte) (((byte) (((int) ((((double) location.getSpeed()) * 3.6d) / 4.0d)) & 255)) & EventUtils.CMD_UPGRADE_ACK)));
        }
        this.b = location;
    }

    private void d(String str) {
        int i2 = 1;
        try {
            File file = new File(h.a + "/grtcf.dat");
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(2);
                int readInt = randomAccessFile.readInt();
                randomAccessFile.seek(8);
                int readInt2 = randomAccessFile.readInt();
                byte[] bArr = new byte[readInt2];
                randomAccessFile.read(bArr, 0, readInt2);
                String str2 = new String(bArr);
                if (str2.contains(b("%d_%02d_%02d")) && str2.contains(":")) {
                    try {
                        String[] split = str2.split(":");
                        if (split.length > 1) {
                            this.y = Integer.valueOf(split[1]).intValue();
                        }
                    } catch (Exception e2) {
                    }
                }
                while (true) {
                    if (i2 > readInt) {
                        break;
                    }
                    randomAccessFile.seek((long) (i2 * 2048));
                    int readInt3 = randomAccessFile.readInt();
                    byte[] bArr2 = new byte[readInt3];
                    randomAccessFile.read(bArr2, 0, readInt3);
                    String str3 = new String(bArr2);
                    if (str != null && str3.contains(str)) {
                        c(str3);
                        break;
                    }
                    i2++;
                }
                randomAccessFile.close();
            }
        } catch (Exception e3) {
        }
    }

    private void e(Location location) {
        if (location != null) {
            int longitude = (int) ((location.getLongitude() - this.b.getLongitude()) * 100000.0d);
            int latitude = (int) ((location.getLatitude() - this.b.getLatitude()) * 100000.0d);
            char c2 = location.hasBearing() ? (char) 0 : 1;
            char c3 = location.hasSpeed() ? (char) 0 : 1;
            char c4 = longitude > 0 ? (char) 0 : 1;
            int abs = Math.abs(longitude);
            char c5 = latitude > 0 ? (char) 0 : 1;
            int abs2 = Math.abs(latitude);
            if (this.L > 1) {
                this.c = null;
                this.c = this.b;
            }
            this.b = location;
            if (this.b != null && this.c != null && this.b.getTime() > this.c.getTime() && this.b.getTime() - this.c.getTime() < 5000) {
                long time = this.b.getTime() - this.c.getTime();
                float[] fArr = new float[2];
                Location.distanceBetween(this.b.getAltitude(), this.b.getLongitude(), this.c.getLatitude(), this.c.getLongitude(), fArr);
                double speed = (double) ((2.0f * (fArr[0] - (this.c.getSpeed() * ((float) time)))) / ((float) (time * time)));
                if (speed > ((double) this.F)) {
                    this.F = (float) speed;
                }
            }
            this.M.add(Byte.valueOf((byte) (abs & 255)));
            this.M.add(Byte.valueOf((byte) (abs2 & 255)));
            if (c2 > 0) {
                byte b2 = 32;
                if (c5 > 0) {
                    b2 = (byte) 96;
                }
                if (c4 > 0) {
                    b2 = (byte) (b2 | EventUtils.CMD_FREQ_SEL);
                }
                this.M.add(Byte.valueOf(b2));
            } else {
                byte bearing = (byte) (((byte) (((int) (location.getBearing() / 15.0f)) & 255)) & 31);
                if (c5 > 0) {
                    bearing = (byte) (bearing | EventUtils.MCU_KEY_NUM1_LONG);
                }
                if (c4 > 0) {
                    bearing = (byte) (bearing | EventUtils.CMD_FREQ_SEL);
                }
                this.M.add(Byte.valueOf(bearing));
            }
            if (c3 > 0) {
                this.M.add(Byte.valueOf(EventUtils.CMD_FREQ_SEL));
            } else {
                this.M.add(Byte.valueOf((byte) (((byte) (((int) ((((double) location.getSpeed()) * 3.6d) / 4.0d)) & 255)) & EventUtils.CMD_UPGRADE_ACK)));
            }
        }
    }

    /* access modifiers changed from: private */
    public void e(String str) {
        try {
            File file = new File(h.a + "/grtcf.dat");
            if (!file.exists()) {
                File file2 = new File(h.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(2);
                    randomAccessFile.writeInt(0);
                    randomAccessFile.seek(8);
                    byte[] bytes = "1980_01_01:0".getBytes();
                    randomAccessFile.writeInt(bytes.length);
                    randomAccessFile.write(bytes);
                    randomAccessFile.seek(200);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.seek(800);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(2);
            int readInt = randomAccessFile2.readInt();
            int i2 = 1;
            while (i2 <= readInt) {
                randomAccessFile2.seek((long) (i2 * 2048));
                int readInt2 = randomAccessFile2.readInt();
                byte[] bArr = new byte[readInt2];
                randomAccessFile2.read(bArr, 0, readInt2);
                if (new String(bArr).contains(c.c)) {
                    break;
                }
                i2++;
            }
            if (i2 >= readInt) {
                randomAccessFile2.seek(2);
                randomAccessFile2.writeInt(i2);
            }
            randomAccessFile2.seek((long) (i2 * 2048));
            byte[] bytes2 = str.getBytes();
            randomAccessFile2.writeInt(bytes2.length);
            randomAccessFile2.write(bytes2);
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    private boolean e() {
        if (this.B) {
            if (this.C) {
                if (((double) this.E) < this.t) {
                    this.D += this.p;
                    if (this.D <= this.u || System.currentTimeMillis() - this.G > ((long) (this.v * 1000))) {
                        return true;
                    }
                } else {
                    this.D = 0;
                    this.C = false;
                    return true;
                }
            } else if (((double) this.E) >= this.t) {
                return true;
            } else {
                this.C = true;
                this.D = 0;
                this.D += this.p;
                return true;
            }
        } else if (((double) this.E) >= this.r || ((double) this.F) >= this.s) {
            this.B = true;
            return true;
        } else if (this.w == 1 && System.currentTimeMillis() - this.G > ((long) (this.x * 1000))) {
            return true;
        }
        return false;
    }

    private void f() {
        this.M = null;
        this.e = 0;
        this.L = 0;
        this.b = null;
        this.c = null;
        this.E = 0.0f;
        this.F = 0.0f;
    }

    private void g() {
        if (this.e != 0 && System.currentTimeMillis() - this.e >= ((long) (this.p * 1000))) {
            if (com.baidu.location.f.getServiceContext().getSharedPreferences("loc_navi_mode", 4).getBoolean("is_navi_on", false)) {
                f();
            } else if (this.n == 1 && !e()) {
                f();
            } else if (!a(c.c, com.baidu.location.f.getServiceContext())) {
                f();
            } else if (this.M != null) {
                int size = this.M.size();
                this.M.set(0, Byte.valueOf((byte) (size & 255)));
                this.M.set(1, Byte.valueOf((byte) ((65280 & size) >> 8)));
                this.M.set(3, Byte.valueOf((byte) (this.L & 255)));
                byte[] bArr = new byte[size];
                for (int i2 = 0; i2 < size; i2++) {
                    bArr[i2] = this.M.get(i2).byteValue();
                }
                if (Environment.getExternalStorageState().equals("mounted")) {
                    File file = new File(Environment.getExternalStorageDirectory(), "baidu/tempdata");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (file.exists()) {
                        File file2 = new File(file, "intime.dat");
                        if (file2.exists()) {
                            file2.delete();
                        }
                        try {
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
                            bufferedOutputStream.write(bArr);
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                            new Thread() {
                                public void run() {
                                    String unused = e.this.a(new File(Environment.getExternalStorageDirectory() + "/baidu/tempdata", "intime.dat"), "http://itsdata.map.baidu.com/long-conn-gps/sdk.php");
                                }
                            }.start();
                        } catch (Exception e2) {
                        }
                    }
                }
                f();
                this.G = System.currentTimeMillis();
            }
        }
    }

    private void h() {
        this.M.add((byte) 0);
        this.M.add((byte) 0);
        if (f.equals("0")) {
            this.M.add(Byte.valueOf(EventUtils.MCU_KEY1_14));
        } else {
            this.M.add(Byte.valueOf(EventUtils.CMD_KEY_AD));
        }
        this.M.add((byte) 0);
        this.M.add(Byte.valueOf(this.J[0]));
        this.M.add(Byte.valueOf(this.J[1]));
        this.M.add(Byte.valueOf(this.J[2]));
        this.M.add(Byte.valueOf(this.J[3]));
        this.M.add(Byte.valueOf((byte) ((r1 + 1) & 255)));
        for (byte valueOf : this.K) {
            this.M.add(Byte.valueOf(valueOf));
        }
    }

    private void i() {
        if (System.currentTimeMillis() - this.z > 86400000) {
            if (this.A == null) {
                this.A = new a();
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(c.a().a(false));
            stringBuffer.append(com.baidu.location.a.a.a().c());
            this.A.a(stringBuffer.toString());
        }
        j();
    }

    private void j() {
    }

    public void a(final Location location) {
        if (!this.N) {
            c();
        }
        if (this.k == 1 && ((double) b.a().f()) < this.l * 100.0d && this.m.contains(com.baidu.location.f.c.a(com.baidu.location.f.c.a().e()))) {
            if (this.n != 1 || this.y <= this.o) {
                this.I.post(new Runnable() {
                    public void run() {
                        e.this.b(location);
                    }
                });
            }
        }
    }

    public void b() {
        if (this.N) {
            this.N = false;
            f();
        }
    }
}
