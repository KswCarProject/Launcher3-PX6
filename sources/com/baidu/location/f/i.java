package com.baidu.location.f;

import android.net.wifi.ScanResult;
import android.text.TextUtils;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class i {
    public List<ScanResult> a = null;
    private long b = 0;
    private long c = 0;
    private boolean d = false;
    private boolean e;

    public i(List<ScanResult> list, long j) {
        this.b = j;
        this.a = list;
        this.c = System.currentTimeMillis();
        i();
    }

    private boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return Pattern.compile("wpa|wep", 2).matcher(str).find();
    }

    private void i() {
        boolean z;
        if (a() >= 1) {
            boolean z2 = true;
            for (int size = this.a.size() - 1; size >= 1 && z2; size--) {
                int i = 0;
                z2 = false;
                while (i < size) {
                    if (this.a.get(i).level < this.a.get(i + 1).level) {
                        this.a.set(i + 1, this.a.get(i));
                        this.a.set(i, this.a.get(i + 1));
                        z = true;
                    } else {
                        z = z2;
                    }
                    i++;
                    z2 = z;
                }
            }
        }
    }

    public int a() {
        if (this.a == null) {
            return 0;
        }
        return this.a.size();
    }

    public String a(int i) {
        return a(i, false);
    }

    /* JADX WARNING: type inference failed for: r2v67 */
    /* JADX WARNING: type inference failed for: r2v86 */
    /* JADX WARNING: type inference failed for: r2v87 */
    /* JADX WARNING: type inference failed for: r2v104 */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x02fc, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a0, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0187, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0195, code lost:
        return null;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02f8 A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030c  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0310  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x007d A[Catch:{ Error -> 0x0194, Exception -> 0x02fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0194 A[ExcHandler: Error (e java.lang.Error), Splitter:B:4:0x000a] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01e8 A[SYNTHETIC, Splitter:B:94:0x01e8] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String a(int r26, boolean r27) {
        /*
            r25 = this;
            int r2 = r25.a()
            r3 = 1
            if (r2 >= r3) goto L_0x0009
            r2 = 0
        L_0x0008:
            return r2
        L_0x0009:
            r3 = 0
            java.util.Random r18 = new java.util.Random     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r18.<init>()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.StringBuffer r19 = new java.lang.StringBuffer     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r2 = 512(0x200, float:7.175E-43)
            r0 = r19
            r0.<init>(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.util.ArrayList r20 = new java.util.ArrayList     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r20.<init>()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            com.baidu.location.f.l r2 = com.baidu.location.f.k.a()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            android.net.wifi.WifiInfo r5 = r2.h()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r4 = 0
            r2 = -1
            if (r5 == 0) goto L_0x031c
            java.lang.String r6 = r5.getBSSID()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r6 == 0) goto L_0x031c
            java.lang.String r2 = r5.getBSSID()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r4 = ":"
            java.lang.String r6 = ""
            java.lang.String r4 = r2.replace(r4, r6)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            int r2 = r5.getRssi()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 >= 0) goto L_0x0316
            int r2 = -r2
            r16 = r2
            r17 = r4
        L_0x0046:
            r4 = 0
            r8 = 0
            r2 = 0
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r7 = 17
            if (r6 < r7) goto L_0x0313
            long r4 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x009f, Exception -> 0x02fb }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r6
        L_0x0058:
            r6 = 0
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x0313
            r2 = 1
            r14 = r4
        L_0x0060:
            if (r2 == 0) goto L_0x0310
            if (r2 == 0) goto L_0x00a3
            if (r27 == 0) goto L_0x00a3
            r2 = 1
        L_0x0067:
            r13 = r2
        L_0x0068:
            r6 = 0
            r5 = 0
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            int r2 = r2.size()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r4 = 1
            r0 = r26
            if (r2 <= r0) goto L_0x030c
        L_0x0077:
            r2 = 0
            r12 = r2
        L_0x0079:
            r0 = r26
            if (r12 >= r0) goto L_0x01e6
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            int r2 = r2.level     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 != 0) goto L_0x00a5
            r2 = r4
            r4 = r6
            r6 = r8
            r24 = r5
            r5 = r3
            r3 = r24
        L_0x0093:
            int r8 = r12 + 1
            r12 = r8
            r8 = r6
            r6 = r4
            r4 = r2
            r24 = r3
            r3 = r5
            r5 = r24
            goto L_0x0079
        L_0x009f:
            r4 = move-exception
            r4 = 0
            goto L_0x0058
        L_0x00a3:
            r2 = 0
            goto L_0x0067
        L_0x00a5:
            if (r13 == 0) goto L_0x00c8
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x0186, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x0186, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x0186, Error -> 0x0194 }
            long r10 = r2.timestamp     // Catch:{ Exception -> 0x0186, Error -> 0x0194 }
            long r10 = r14 - r10
            r22 = 1000000(0xf4240, double:4.940656E-318)
            long r10 = r10 / r22
        L_0x00ba:
            java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r20
            r0.add(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            int r2 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r2 <= 0) goto L_0x00c8
            r8 = r10
        L_0x00c8:
            if (r4 == 0) goto L_0x018b
            r4 = 0
            java.lang.String r2 = "&wf="
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
        L_0x00d2:
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = r2.BSSID     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 == 0) goto L_0x02ff
            java.lang.String r7 = ":"
            java.lang.String r10 = ""
            java.lang.String r7 = r2.replace(r7, r10)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r7)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            int r2 = r2.level     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 >= 0) goto L_0x00fc
            int r2 = -r2
        L_0x00fc:
            java.util.Locale r10 = java.util.Locale.CHINA     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r11 = ";%d;"
            r21 = 1
            r0 = r21
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r21 = r0
            r22 = 0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r21[r22] = r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r21
            java.lang.String r2 = java.lang.String.format(r10, r11, r0)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            int r6 = r6 + 1
            if (r17 == 0) goto L_0x013e
            r0 = r17
            boolean r2 = r0.equals(r7)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 == 0) goto L_0x013e
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = r2.capabilities     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r25
            boolean r2 = r0.a((java.lang.String) r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r25
            r0.e = r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r5 = r6
        L_0x013e:
            if (r3 != 0) goto L_0x0198
            r2 = 10
            r0 = r18
            int r2 = r0.nextInt(r2)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r7 = 2
            if (r2 != r7) goto L_0x0309
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            if (r2 == 0) goto L_0x0309
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r7 = 30
            if (r2 >= r7) goto L_0x0309
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r0 = r19
            r0.append(r2)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r2 = 1
        L_0x017f:
            r3 = r5
            r5 = r2
            r2 = r4
            r4 = r6
            r6 = r8
            goto L_0x0093
        L_0x0186:
            r2 = move-exception
            r10 = 0
            goto L_0x00ba
        L_0x018b:
            java.lang.String r2 = "|"
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            goto L_0x00d2
        L_0x0194:
            r2 = move-exception
            r2 = 0
            goto L_0x0008
        L_0x0198:
            r2 = 1
            if (r3 != r2) goto L_0x0309
            r2 = 20
            r0 = r18
            int r2 = r0.nextInt(r2)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r7 = 1
            if (r2 != r7) goto L_0x0309
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            if (r2 == 0) goto L_0x0309
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r7 = 30
            if (r2 >= r7) goto L_0x0309
            r0 = r25
            java.util.List<android.net.wifi.ScanResult> r2 = r0.a     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r0 = r19
            r0.append(r2)     // Catch:{ Exception -> 0x01db, Error -> 0x0194 }
            r2 = 2
            goto L_0x017f
        L_0x01db:
            r2 = move-exception
            r2 = r4
            r4 = r6
            r6 = r8
            r24 = r5
            r5 = r3
            r3 = r24
            goto L_0x0093
        L_0x01e6:
            if (r4 != 0) goto L_0x02f8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r2.<init>()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r3 = "&wf_n="
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = r2.toString()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r17 == 0) goto L_0x0221
            r2 = -1
            r0 = r16
            if (r0 == r2) goto L_0x0221
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r2.<init>()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r3 = "&wf_rs="
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r16
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = r2.toString()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
        L_0x0221:
            r2 = 10
            int r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x02a8
            int r2 = r20.size()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 <= 0) goto L_0x02a8
            r2 = 0
            r0 = r20
            java.lang.Object r2 = r0.get(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            long r2 = r2.longValue()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r6 = 0
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 <= 0) goto L_0x02a8
            java.lang.StringBuffer r6 = new java.lang.StringBuffer     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r2 = 128(0x80, float:1.794E-43)
            r6.<init>(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = "&wf_ut="
            r6.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r4 = 1
            r2 = 0
            r0 = r20
            java.lang.Object r2 = r0.get(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.util.Iterator r7 = r20.iterator()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
        L_0x025a:
            boolean r3 = r7.hasNext()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r3 == 0) goto L_0x029f
            java.lang.Object r3 = r7.next()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.Long r3 = (java.lang.Long) r3     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r4 == 0) goto L_0x0278
            r4 = 0
            long r8 = r3.longValue()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r6.append(r8)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r3 = r4
        L_0x0271:
            java.lang.String r4 = "|"
            r6.append(r4)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r4 = r3
            goto L_0x025a
        L_0x0278:
            long r8 = r3.longValue()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            long r10 = r2.longValue()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            long r8 = r8 - r10
            r10 = 0
            int r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r3 == 0) goto L_0x029d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r3.<init>()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r10 = ""
            java.lang.StringBuilder r3 = r3.append(r10)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.StringBuilder r3 = r3.append(r8)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r3 = r3.toString()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r6.append(r3)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
        L_0x029d:
            r3 = r4
            goto L_0x0271
        L_0x029f:
            java.lang.String r2 = r6.toString()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
        L_0x02a8:
            java.lang.String r2 = "&wf_st="
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r25
            long r2 = r0.b     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = "&wf_et="
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r25
            long r2 = r0.c     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = "&wf_vt="
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            long r2 = com.baidu.location.f.j.a     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r5 <= 0) goto L_0x02f0
            r2 = 1
            r0 = r25
            r0.d = r2     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            java.lang.String r2 = "&wf_en="
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            r0 = r25
            boolean r2 = r0.e     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            if (r2 == 0) goto L_0x02f6
            r2 = 1
        L_0x02eb:
            r0 = r19
            r0.append(r2)     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
        L_0x02f0:
            java.lang.String r2 = r19.toString()     // Catch:{ Error -> 0x0194, Exception -> 0x02fb }
            goto L_0x0008
        L_0x02f6:
            r2 = 0
            goto L_0x02eb
        L_0x02f8:
            r2 = 0
            goto L_0x0008
        L_0x02fb:
            r2 = move-exception
            r2 = 0
            goto L_0x0008
        L_0x02ff:
            r2 = r4
            r4 = r6
            r6 = r8
            r24 = r5
            r5 = r3
            r3 = r24
            goto L_0x0093
        L_0x0309:
            r2 = r3
            goto L_0x017f
        L_0x030c:
            r26 = r2
            goto L_0x0077
        L_0x0310:
            r13 = r2
            goto L_0x0068
        L_0x0313:
            r14 = r4
            goto L_0x0060
        L_0x0316:
            r16 = r2
            r17 = r4
            goto L_0x0046
        L_0x031c:
            r16 = r2
            r17 = r4
            goto L_0x0046
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.f.i.a(int, boolean):java.lang.String");
    }

    public boolean a(i iVar) {
        if (this.a == null || iVar == null || iVar.a == null) {
            return false;
        }
        int size = this.a.size() < iVar.a.size() ? this.a.size() : iVar.a.size();
        for (int i = 0; i < size; i++) {
            if (!this.a.get(i).BSSID.equals(iVar.a.get(i).BSSID)) {
                return false;
            }
        }
        return true;
    }

    public String b() {
        try {
            return a(com.baidu.location.h.i.L, true);
        } catch (Exception e2) {
            return null;
        }
    }

    public String b(int i) {
        if (a() < 1) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(512);
        int size = this.a.size();
        if (size <= i) {
            i = size;
        }
        int i2 = 0;
        boolean z = true;
        while (i2 < i) {
            if (!(this.a.get(i2).level == 0 || this.a.get(i2).BSSID == null)) {
                if (z) {
                    z = false;
                } else {
                    stringBuffer.append("|");
                }
                stringBuffer.append(this.a.get(i2).BSSID.replace(":", ""));
                int i3 = this.a.get(i2).level;
                if (i3 < 0) {
                    i3 = -i3;
                }
                stringBuffer.append(String.format(Locale.CHINA, ";%d;", new Object[]{Integer.valueOf(i3)}));
            }
            i2++;
            z = z;
        }
        if (!z) {
            return stringBuffer.toString();
        }
        return null;
    }

    public boolean b(i iVar) {
        if (this.a == null || iVar == null || iVar.a == null) {
            return false;
        }
        int size = this.a.size() < iVar.a.size() ? this.a.size() : iVar.a.size();
        for (int i = 0; i < size; i++) {
            String str = this.a.get(i).BSSID;
            int i2 = this.a.get(i).level;
            String str2 = iVar.a.get(i).BSSID;
            int i3 = iVar.a.get(i).level;
            if (!str.equals(str2) || i2 != i3) {
                return false;
            }
        }
        return true;
    }

    public String c() {
        try {
            return a(15);
        } catch (Exception e2) {
            return null;
        }
    }

    public String c(int i) {
        int i2;
        int i3 = 0;
        if (i == 0 || a() < 1) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(256);
        int size = this.a.size();
        int i4 = size > com.baidu.location.h.i.L ? com.baidu.location.h.i.L : size;
        int i5 = 1;
        int i6 = 0;
        while (i6 < i4) {
            if ((i5 & i) == 0 || this.a.get(i6).BSSID == null) {
                i2 = i3;
            } else {
                if (i3 == 0) {
                    stringBuffer.append("&ssid=");
                } else {
                    stringBuffer.append("|");
                }
                stringBuffer.append(this.a.get(i6).BSSID.replace(":", ""));
                stringBuffer.append(";");
                stringBuffer.append(this.a.get(i6).SSID);
                i2 = i3 + 1;
            }
            i5 <<= 1;
            i6++;
            i3 = i2;
        }
        return stringBuffer.toString();
    }

    public boolean c(i iVar) {
        return j.a(iVar, this, com.baidu.location.h.i.O);
    }

    public int d() {
        for (int i = 0; i < a(); i++) {
            int i2 = -this.a.get(i).level;
            if (i2 > 0) {
                return i2;
            }
        }
        return 0;
    }

    public boolean e() {
        return this.d;
    }

    public boolean f() {
        return System.currentTimeMillis() - this.c < 5000;
    }

    public boolean g() {
        return System.currentTimeMillis() - this.c < 5000;
    }

    public boolean h() {
        return System.currentTimeMillis() - this.b < 5000;
    }
}
