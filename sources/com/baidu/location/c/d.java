package com.baidu.location.c;

import com.baidu.location.f.c;
import com.baidu.location.h.f;
import com.baidu.location.h.h;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import org.json.JSONArray;
import org.json.JSONObject;

public class d {
    private static d a = null;
    /* access modifiers changed from: private */
    public long b = 0;
    private long c = 0;
    /* access modifiers changed from: private */
    public long d = 0;
    /* access modifiers changed from: private */
    public String e = null;
    /* access modifiers changed from: private */
    public String f = null;
    /* access modifiers changed from: private */
    public String g = "loc.map.baidu.com";
    /* access modifiers changed from: private */
    public String h = "dns.map.baidu.com";
    /* access modifiers changed from: private */
    public int i = 0;
    private a j = new a();

    private class a extends f {
        private boolean b = false;

        public a() {
        }

        public void a() {
            if (d.this.h.equals("dns.map.baidu.com") || System.currentTimeMillis() - d.this.b > 720000) {
                switch (c.a().g()) {
                    case 1:
                        String unused = d.this.h = "111.13.100.247";
                        break;
                    case 2:
                        String unused2 = d.this.h = "111.206.37.190";
                        break;
                    case 3:
                        String unused3 = d.this.h = "180.97.33.196";
                        break;
                    default:
                        String unused4 = d.this.h = "dns.map.baidu.com";
                        break;
                }
            }
            this.h = "http://" + d.this.h + ":80/remotedns?pid=lbs-geolocation";
        }

        public void a(boolean z) {
            String str = null;
            if (z && this.j != null) {
                try {
                    JSONObject jSONObject = new JSONObject(this.j);
                    if (jSONObject.getInt("errno") == 0 && jSONObject.has("data")) {
                        JSONArray jSONArray = jSONObject.getJSONArray("data");
                        JSONObject jSONObject2 = jSONArray.getJSONObject(0);
                        JSONObject jSONObject3 = jSONArray.getJSONObject(1);
                        String string = jSONObject2.has("loc.map.baidu.com") ? jSONObject2.getJSONArray("loc.map.baidu.com").getJSONObject(0).getString("ip") : null;
                        if (jSONObject3.has("dns.map.baidu.com")) {
                            str = jSONObject3.getJSONArray("dns.map.baidu.com").getJSONObject(0).getString("ip");
                        }
                        if (!(string == null || str == null)) {
                            String unused = d.this.h = str;
                            String unused2 = d.this.g = string;
                        }
                        if (jSONObject.has("switch")) {
                            int unused3 = d.this.i = jSONObject.getInt("switch");
                        }
                        long unused4 = d.this.b = System.currentTimeMillis();
                        c();
                    }
                } catch (Exception e) {
                }
            }
            this.b = false;
        }

        /* access modifiers changed from: package-private */
        public void b() {
            if (!this.b) {
                this.b = true;
                d();
            }
        }

        /* access modifiers changed from: package-private */
        public void c() {
            String str;
            InetAddress inetAddress;
            if (System.currentTimeMillis() - d.this.d < 1200000) {
                d.this.d();
                return;
            }
            try {
                str = (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{"net.dns1"});
            } catch (Exception e) {
                str = null;
            }
            try {
                inetAddress = InetAddress.getByName("loc.map.baidu.com");
            } catch (Exception e2) {
                inetAddress = null;
            }
            if (inetAddress != null && inetAddress.getHostAddress() != null && str != null && !"".equals(str)) {
                String unused = d.this.e = str;
                String unused2 = d.this.f = inetAddress.getHostAddress();
                long unused3 = d.this.d = System.currentTimeMillis();
                d.this.d();
            }
        }
    }

    private d() {
        e();
    }

    public static d a() {
        if (a == null) {
            a = new d();
        }
        return a;
    }

    private void a(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("dnsServer")) {
                this.e = jSONObject.getString("dnsServer");
            }
            if (jSONObject.has("locServer")) {
                this.f = jSONObject.getString("locServer");
            }
            if (jSONObject.has("address")) {
                this.g = jSONObject.getString("address");
            }
            if (jSONObject.has("locServer")) {
                this.h = jSONObject.getString("dnsServerIp");
            }
            if (jSONObject.has("DnsProxyTime")) {
                this.b = jSONObject.getLong("DnsProxyTime");
            }
            if (jSONObject.has("DnsExtraTime")) {
                this.c = jSONObject.getLong("DnsExtraTime");
            }
            if (jSONObject.has("DnsExtraUpdateTime")) {
                this.d = jSONObject.getLong("DnsExtraUpdateTime");
            }
            if (jSONObject.has("enable")) {
                this.i = jSONObject.getInt("enable");
            }
        } catch (Exception e2) {
        }
    }

    private String c() {
        try {
            JSONObject jSONObject = new JSONObject();
            if (this.e != null) {
                jSONObject.put("dnsServer", this.e);
            }
            if (this.f != null) {
                jSONObject.put("locServer", this.f);
            }
            if (this.g != null) {
                jSONObject.put("address", this.g);
            }
            if (this.h != null) {
                jSONObject.put("dnsServerIp", this.h);
            }
            jSONObject.put("DnsProxyTime", this.b);
            jSONObject.put("DnsExtraTime", this.c);
            jSONObject.put("DnsExtraUpdateTime", this.d);
            jSONObject.put("enable", this.i);
            return jSONObject.toString();
        } catch (Exception e2) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void d() {
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
            randomAccessFile2.seek(800);
            String c2 = c();
            if (c2 != null) {
                randomAccessFile2.writeBoolean(true);
                byte[] bytes2 = c2.getBytes();
                randomAccessFile2.writeInt(bytes2.length);
                randomAccessFile2.write(bytes2);
            } else {
                randomAccessFile2.writeBoolean(false);
            }
            randomAccessFile2.close();
        } catch (Error | Exception e2) {
        }
    }

    private void e() {
        try {
            File file = new File(h.a + "/grtcf.dat");
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(800);
                if (randomAccessFile.readBoolean()) {
                    int readInt = randomAccessFile.readInt();
                    byte[] bArr = new byte[readInt];
                    randomAccessFile.read(bArr, 0, readInt);
                    a(new String(bArr));
                }
                randomAccessFile.close();
            }
        } catch (Error | Exception e2) {
        }
    }

    public String b() {
        String str = "loc.map.baidu.com";
        if (this.i == 1 && System.currentTimeMillis() - this.b < 360000) {
            str = this.g;
        }
        if (System.currentTimeMillis() - this.b > 300000) {
            this.j.b();
        }
        return str;
    }
}
