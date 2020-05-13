package com.baidu.location.h;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import java.util.Map;

public abstract class f {
    private static String a = "10.0.0.172";
    private static int b = 80;
    public static int g = b.g;
    protected static int o = 0;
    public String h = null;
    public int i = 3;
    public String j = null;
    public Map<String, Object> k = null;
    public String l = null;
    public byte[] m = null;
    public String n = null;

    private static int a(Context context, NetworkInfo networkInfo) {
        String lowerCase;
        if (!(networkInfo == null || networkInfo.getExtraInfo() == null || (lowerCase = networkInfo.getExtraInfo().toLowerCase()) == null)) {
            if (lowerCase.startsWith("cmwap") || lowerCase.startsWith("uniwap") || lowerCase.startsWith("3gwap")) {
                String defaultHost = Proxy.getDefaultHost();
                if (defaultHost == null || defaultHost.equals("") || defaultHost.equals("null")) {
                    defaultHost = "10.0.0.172";
                }
                a = defaultHost;
                return b.d;
            } else if (lowerCase.startsWith("ctwap")) {
                String defaultHost2 = Proxy.getDefaultHost();
                if (defaultHost2 == null || defaultHost2.equals("") || defaultHost2.equals("null")) {
                    defaultHost2 = "10.0.0.200";
                }
                a = defaultHost2;
                return b.d;
            } else if (lowerCase.startsWith("cmnet") || lowerCase.startsWith("uninet") || lowerCase.startsWith("ctnet") || lowerCase.startsWith("3gnet")) {
                return b.e;
            }
        }
        String defaultHost3 = Proxy.getDefaultHost();
        if (defaultHost3 != null && defaultHost3.length() > 0) {
            if ("10.0.0.172".equals(defaultHost3.trim())) {
                a = "10.0.0.172";
                return b.d;
            } else if ("10.0.0.200".equals(defaultHost3.trim())) {
                a = "10.0.0.200";
                return b.d;
            }
        }
        return b.e;
    }

    /* access modifiers changed from: private */
    public void b() {
        g = c();
    }

    private int c() {
        Context serviceContext = com.baidu.location.f.getServiceContext();
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) serviceContext.getSystemService("connectivity");
            if (connectivityManager == null) {
                return b.g;
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) {
                return b.g;
            }
            if (activeNetworkInfo.getType() != 1) {
                return a(serviceContext, activeNetworkInfo);
            }
            String defaultHost = Proxy.getDefaultHost();
            return (defaultHost == null || defaultHost.length() <= 0) ? b.f : b.h;
        } catch (Exception e) {
            return b.g;
        }
    }

    public abstract void a();

    public abstract void a(boolean z);

    public void b(final boolean z) {
        new Thread() {
            /* JADX WARNING: Removed duplicated region for block: B:12:0x0068  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x0162  */
            /* JADX WARNING: Removed duplicated region for block: B:54:0x016c  */
            /* JADX WARNING: Removed duplicated region for block: B:64:0x006b A[SYNTHETIC] */
            /* JADX WARNING: Removed duplicated region for block: B:67:0x006b A[SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r9 = this;
                    r3 = 0
                    r8 = 0
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    java.lang.String r1 = com.baidu.location.h.i.c()
                    r0.h = r1
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.b()
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.a()
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    int r0 = r0.i
                    r2 = r3
                    r4 = r0
                L_0x001a:
                    if (r4 <= 0) goto L_0x013c
                    java.net.URL r5 = new java.net.URL     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.String r0 = r0.h     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    r5.<init>(r0)     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.StringBuffer r6 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    r6.<init>()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.util.Map<java.lang.String, java.lang.Object> r0 = r0.k     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.util.Set r0 = r0.entrySet()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.util.Iterator r7 = r0.iterator()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                L_0x0036:
                    boolean r0 = r7.hasNext()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    if (r0 == 0) goto L_0x0070
                    java.lang.Object r0 = r7.next()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.Object r1 = r0.getKey()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    r6.append(r1)     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.String r1 = "="
                    r6.append(r1)     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.Object r0 = r0.getValue()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    r6.append(r0)     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.String r0 = "&"
                    r6.append(r0)     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    goto L_0x0036
                L_0x005d:
                    r0 = move-exception
                    r0 = r2
                L_0x005f:
                    java.lang.String r1 = com.baidu.location.h.b.a     // Catch:{ all -> 0x0167 }
                    java.lang.String r2 = "NetworkCommunicationException!"
                    android.util.Log.d(r1, r2)     // Catch:{ all -> 0x0167 }
                    if (r0 == 0) goto L_0x006b
                    r0.disconnect()
                L_0x006b:
                    int r1 = r4 + -1
                    r2 = r0
                    r4 = r1
                    goto L_0x001a
                L_0x0070:
                    int r0 = r6.length()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    if (r0 <= 0) goto L_0x007f
                    int r0 = r6.length()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    int r0 = r0 + -1
                    r6.deleteCharAt(r0)     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                L_0x007f:
                    java.net.URLConnection r0 = r5.openConnection()     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x005d, Error -> 0x0175, all -> 0x0173 }
                    java.lang.String r1 = "POST"
                    r0.setRequestMethod(r1)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1 = 1
                    r0.setDoInput(r1)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1 = 1
                    r0.setDoOutput(r1)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1 = 0
                    r0.setUseCaches(r1)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    int r1 = com.baidu.location.h.b.b     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r0.setConnectTimeout(r1)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    int r1 = com.baidu.location.h.b.b     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r0.setReadTimeout(r1)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r1 = "Content-Type"
                    java.lang.String r2 = "application/x-www-form-urlencoded; charset=utf-8"
                    r0.setRequestProperty(r1, r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r1 = "Accept-Charset"
                    java.lang.String r2 = "UTF-8"
                    r0.setRequestProperty(r1, r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r1 = "Accept-Encoding"
                    java.lang.String r2 = "gzip"
                    r0.setRequestProperty(r1, r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r1 = "Host"
                    java.lang.String r2 = "loc.map.baidu.com"
                    r0.setRequestProperty(r1, r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.io.OutputStream r1 = r0.getOutputStream()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r2 = r6.toString()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    byte[] r2 = r2.getBytes()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1.write(r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1.flush()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1.close()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    int r1 = r0.getResponseCode()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r2 = 200(0xc8, float:2.8E-43)
                    if (r1 != r2) goto L_0x014e
                    java.io.InputStream r2 = r0.getInputStream()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r1 = r0.getContentEncoding()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    if (r1 == 0) goto L_0x0178
                    java.lang.String r5 = "gzip"
                    boolean r1 = r1.contains(r5)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    if (r1 == 0) goto L_0x0178
                    java.util.zip.GZIPInputStream r1 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.io.BufferedInputStream r5 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r5.<init>(r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1.<init>(r5)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                L_0x00f5:
                    java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r2.<init>()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r5 = 1024(0x400, float:1.435E-42)
                    byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                L_0x00fe:
                    int r6 = r1.read(r5)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r7 = -1
                    if (r6 == r7) goto L_0x010d
                    r7 = 0
                    r2.write(r5, r7, r6)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    goto L_0x00fe
                L_0x010a:
                    r1 = move-exception
                    goto L_0x005f
                L_0x010d:
                    r1.close()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r2.close()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    com.baidu.location.h.f r1 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r5 = new java.lang.String     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    byte[] r6 = r2.toByteArray()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    java.lang.String r7 = "utf-8"
                    r5.<init>(r6, r7)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1.j = r5     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    boolean r1 = r2     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    if (r1 == 0) goto L_0x012e
                    com.baidu.location.h.f r1 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    byte[] r2 = r2.toByteArray()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r1.m = r2     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                L_0x012e:
                    com.baidu.location.h.f r1 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r2 = 1
                    r1.a((boolean) r2)     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    r0.disconnect()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    if (r0 == 0) goto L_0x013c
                    r0.disconnect()
                L_0x013c:
                    if (r4 > 0) goto L_0x0170
                    int r0 = com.baidu.location.h.f.o
                    int r0 = r0 + 1
                    com.baidu.location.h.f.o = r0
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.j = r3
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.a((boolean) r8)
                L_0x014d:
                    return
                L_0x014e:
                    r0.disconnect()     // Catch:{ Exception -> 0x010a, Error -> 0x0158 }
                    if (r0 == 0) goto L_0x006b
                    r0.disconnect()
                    goto L_0x006b
                L_0x0158:
                    r1 = move-exception
                L_0x0159:
                    java.lang.String r1 = com.baidu.location.h.b.a     // Catch:{ all -> 0x0167 }
                    java.lang.String r2 = "NetworkCommunicationError!"
                    android.util.Log.d(r1, r2)     // Catch:{ all -> 0x0167 }
                    if (r0 == 0) goto L_0x006b
                    r0.disconnect()
                    goto L_0x006b
                L_0x0167:
                    r1 = move-exception
                    r2 = r0
                    r0 = r1
                L_0x016a:
                    if (r2 == 0) goto L_0x016f
                    r2.disconnect()
                L_0x016f:
                    throw r0
                L_0x0170:
                    com.baidu.location.h.f.o = r8
                    goto L_0x014d
                L_0x0173:
                    r0 = move-exception
                    goto L_0x016a
                L_0x0175:
                    r0 = move-exception
                    r0 = r2
                    goto L_0x0159
                L_0x0178:
                    r1 = r2
                    goto L_0x00f5
                */
                throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.h.f.AnonymousClass2.run():void");
            }
        }.start();
    }

    public void d() {
        new Thread() {
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00cb  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r10 = this;
                    r2 = 0
                    r8 = 0
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    java.lang.String r1 = com.baidu.location.h.i.c()
                    r0.h = r1
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.b()
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.a()
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    int r0 = r0.i
                    r1 = r2
                    r3 = r0
                L_0x001a:
                    if (r3 <= 0) goto L_0x00aa
                    java.net.URL r0 = new java.net.URL     // Catch:{ Exception -> 0x00d4, all -> 0x00d2 }
                    com.baidu.location.h.f r4 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x00d4, all -> 0x00d2 }
                    java.lang.String r4 = r4.h     // Catch:{ Exception -> 0x00d4, all -> 0x00d2 }
                    r0.<init>(r4)     // Catch:{ Exception -> 0x00d4, all -> 0x00d2 }
                    java.net.URLConnection r0 = r0.openConnection()     // Catch:{ Exception -> 0x00d4, all -> 0x00d2 }
                    java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x00d4, all -> 0x00d2 }
                    java.lang.String r1 = "GET"
                    r0.setRequestMethod(r1)     // Catch:{ Exception -> 0x0075 }
                    r1 = 1
                    r0.setDoInput(r1)     // Catch:{ Exception -> 0x0075 }
                    r1 = 1
                    r0.setDoOutput(r1)     // Catch:{ Exception -> 0x0075 }
                    r1 = 0
                    r0.setUseCaches(r1)     // Catch:{ Exception -> 0x0075 }
                    int r1 = com.baidu.location.h.b.b     // Catch:{ Exception -> 0x0075 }
                    r0.setConnectTimeout(r1)     // Catch:{ Exception -> 0x0075 }
                    int r1 = com.baidu.location.h.b.b     // Catch:{ Exception -> 0x0075 }
                    r0.setReadTimeout(r1)     // Catch:{ Exception -> 0x0075 }
                    java.lang.String r1 = "Content-Type"
                    java.lang.String r4 = "application/x-www-form-urlencoded; charset=utf-8"
                    r0.setRequestProperty(r1, r4)     // Catch:{ Exception -> 0x0075 }
                    java.lang.String r1 = "Accept-Charset"
                    java.lang.String r4 = "UTF-8"
                    r0.setRequestProperty(r1, r4)     // Catch:{ Exception -> 0x0075 }
                    int r1 = r0.getResponseCode()     // Catch:{ Exception -> 0x0075 }
                    r4 = 200(0xc8, float:2.8E-43)
                    if (r1 != r4) goto L_0x00bc
                    java.io.InputStream r1 = r0.getInputStream()     // Catch:{ Exception -> 0x0075 }
                    java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0075 }
                    r4.<init>()     // Catch:{ Exception -> 0x0075 }
                    r5 = 1024(0x400, float:1.435E-42)
                    byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x0075 }
                L_0x0069:
                    int r6 = r1.read(r5)     // Catch:{ Exception -> 0x0075 }
                    r7 = -1
                    if (r6 == r7) goto L_0x0087
                    r7 = 0
                    r4.write(r5, r7, r6)     // Catch:{ Exception -> 0x0075 }
                    goto L_0x0069
                L_0x0075:
                    r1 = move-exception
                L_0x0076:
                    java.lang.String r1 = com.baidu.location.h.b.a     // Catch:{ all -> 0x00c5 }
                    java.lang.String r4 = "NetworkCommunicationException!"
                    android.util.Log.d(r1, r4)     // Catch:{ all -> 0x00c5 }
                    if (r0 == 0) goto L_0x0082
                    r0.disconnect()
                L_0x0082:
                    int r1 = r3 + -1
                    r3 = r1
                    r1 = r0
                    goto L_0x001a
                L_0x0087:
                    r1.close()     // Catch:{ Exception -> 0x0075 }
                    r4.close()     // Catch:{ Exception -> 0x0075 }
                    com.baidu.location.h.f r1 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x0075 }
                    java.lang.String r5 = new java.lang.String     // Catch:{ Exception -> 0x0075 }
                    byte[] r4 = r4.toByteArray()     // Catch:{ Exception -> 0x0075 }
                    java.lang.String r6 = "utf-8"
                    r5.<init>(r4, r6)     // Catch:{ Exception -> 0x0075 }
                    r1.j = r5     // Catch:{ Exception -> 0x0075 }
                    com.baidu.location.h.f r1 = com.baidu.location.h.f.this     // Catch:{ Exception -> 0x0075 }
                    r4 = 1
                    r1.a((boolean) r4)     // Catch:{ Exception -> 0x0075 }
                    r0.disconnect()     // Catch:{ Exception -> 0x0075 }
                    if (r0 == 0) goto L_0x00aa
                    r0.disconnect()
                L_0x00aa:
                    if (r3 > 0) goto L_0x00cf
                    int r0 = com.baidu.location.h.f.o
                    int r0 = r0 + 1
                    com.baidu.location.h.f.o = r0
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.j = r2
                    com.baidu.location.h.f r0 = com.baidu.location.h.f.this
                    r0.a((boolean) r8)
                L_0x00bb:
                    return
                L_0x00bc:
                    r0.disconnect()     // Catch:{ Exception -> 0x0075 }
                    if (r0 == 0) goto L_0x0082
                    r0.disconnect()
                    goto L_0x0082
                L_0x00c5:
                    r1 = move-exception
                    r9 = r1
                    r1 = r0
                    r0 = r9
                L_0x00c9:
                    if (r1 == 0) goto L_0x00ce
                    r1.disconnect()
                L_0x00ce:
                    throw r0
                L_0x00cf:
                    com.baidu.location.h.f.o = r8
                    goto L_0x00bb
                L_0x00d2:
                    r0 = move-exception
                    goto L_0x00c9
                L_0x00d4:
                    r0 = move-exception
                    r0 = r1
                    goto L_0x0076
                */
                throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.h.f.AnonymousClass1.run():void");
            }
        }.start();
    }

    public void e() {
        b(false);
    }
}
