package com.baidu.location.f;

import android.os.Build;
import android.os.SystemClock;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import com.baidu.location.f;
import com.baidu.location.h.i;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class b extends d {
    public static int a = 0;
    public static int b = 0;
    private static b c = null;
    private static Method k = null;
    private static Method l = null;
    private static Method m = null;
    private static Method n = null;
    private static Method o = null;
    private static Class<?> p = null;
    private TelephonyManager d = null;
    private Object e = null;
    /* access modifiers changed from: private */
    public a f = new a();
    private a g = null;
    private List<a> h = null;
    private a i = null;
    private boolean j = false;
    private boolean q = false;

    private class a extends PhoneStateListener {
        public a() {
        }

        public void onCellLocationChanged(CellLocation cellLocation) {
            if (cellLocation != null) {
                try {
                    b.this.j();
                } catch (Exception e) {
                }
                com.baidu.location.c.a.a().e();
            }
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            if (b.this.f == null) {
                return;
            }
            if (b.this.f.i == 'g') {
                b.this.f.h = signalStrength.getGsmSignalStrength();
            } else if (b.this.f.i == 'c') {
                b.this.f.h = signalStrength.getCdmaDbm();
            }
        }
    }

    private b() {
    }

    private int a(int i2) {
        if (i2 == Integer.MAX_VALUE) {
            return -1;
        }
        return i2;
    }

    /* JADX WARNING: type inference failed for: r7v6 */
    /* JADX WARNING: type inference failed for: r7v7 */
    /* JADX WARNING: type inference failed for: r7v8 */
    /* JADX WARNING: type inference failed for: r7v9 */
    /* JADX WARNING: type inference failed for: r7v10 */
    /* JADX WARNING: type inference failed for: r7v11 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.telephony.CellLocation a(java.util.List<?> r14) {
        /*
            r13 = this;
            if (r14 == 0) goto L_0x0008
            boolean r0 = r14.isEmpty()
            if (r0 == 0) goto L_0x000a
        L_0x0008:
            r0 = 0
        L_0x0009:
            return r0
        L_0x000a:
            java.lang.ClassLoader r9 = java.lang.ClassLoader.getSystemClassLoader()
            r6 = 0
            r2 = 0
            r1 = 0
            r0 = 0
            r8 = r0
            r0 = r1
            r1 = r2
        L_0x0015:
            int r2 = r14.size()
            if (r8 >= r2) goto L_0x0124
            java.lang.Object r2 = r14.get(r8)
            if (r2 != 0) goto L_0x0025
        L_0x0021:
            int r2 = r8 + 1
            r8 = r2
            goto L_0x0015
        L_0x0025:
            java.lang.String r3 = "android.telephony.CellInfoGsm"
            java.lang.Class r3 = r9.loadClass(r3)     // Catch:{ Exception -> 0x010e }
            java.lang.String r4 = "android.telephony.CellInfoWcdma"
            java.lang.Class r4 = r9.loadClass(r4)     // Catch:{ Exception -> 0x010e }
            java.lang.String r5 = "android.telephony.CellInfoLte"
            java.lang.Class r5 = r9.loadClass(r5)     // Catch:{ Exception -> 0x010e }
            java.lang.String r7 = "android.telephony.CellInfoCdma"
            java.lang.Class r10 = r9.loadClass(r7)     // Catch:{ Exception -> 0x010e }
            boolean r7 = r3.isInstance(r2)     // Catch:{ Exception -> 0x010e }
            if (r7 == 0) goto L_0x005b
            r7 = 1
        L_0x0044:
            if (r7 <= 0) goto L_0x010b
            r0 = 0
            r11 = 1
            if (r7 != r11) goto L_0x0075
            java.lang.Object r0 = r3.cast(r2)     // Catch:{ Exception -> 0x0111 }
        L_0x004e:
            java.lang.String r2 = "getCellIdentity"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0111 }
            java.lang.Object r2 = com.baidu.location.h.i.a((java.lang.Object) r0, (java.lang.String) r2, (java.lang.Object[]) r3)     // Catch:{ Exception -> 0x0111 }
            if (r2 != 0) goto L_0x008d
            r0 = r7
            goto L_0x0021
        L_0x005b:
            boolean r7 = r4.isInstance(r2)     // Catch:{ Exception -> 0x010e }
            if (r7 == 0) goto L_0x0063
            r7 = 2
            goto L_0x0044
        L_0x0063:
            boolean r7 = r5.isInstance(r2)     // Catch:{ Exception -> 0x010e }
            if (r7 == 0) goto L_0x006b
            r7 = 3
            goto L_0x0044
        L_0x006b:
            boolean r0 = r10.isInstance(r2)     // Catch:{ Exception -> 0x010e }
            if (r0 == 0) goto L_0x0073
            r7 = 4
            goto L_0x0044
        L_0x0073:
            r7 = 0
            goto L_0x0044
        L_0x0075:
            r3 = 2
            if (r7 != r3) goto L_0x007d
            java.lang.Object r0 = r4.cast(r2)     // Catch:{ Exception -> 0x0111 }
            goto L_0x004e
        L_0x007d:
            r3 = 3
            if (r7 != r3) goto L_0x0085
            java.lang.Object r0 = r5.cast(r2)     // Catch:{ Exception -> 0x0111 }
            goto L_0x004e
        L_0x0085:
            r3 = 4
            if (r7 != r3) goto L_0x004e
            java.lang.Object r0 = r10.cast(r2)     // Catch:{ Exception -> 0x0111 }
            goto L_0x004e
        L_0x008d:
            r0 = 4
            if (r7 != r0) goto L_0x00cc
            android.telephony.cdma.CdmaCellLocation r0 = new android.telephony.cdma.CdmaCellLocation     // Catch:{ Exception -> 0x0111 }
            r0.<init>()     // Catch:{ Exception -> 0x0111 }
            java.lang.String r1 = "getSystemId"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0115 }
            int r4 = com.baidu.location.h.i.b(r2, r1, r3)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r1 = "getNetworkId"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0115 }
            int r5 = com.baidu.location.h.i.b(r2, r1, r3)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r1 = "getBasestationId"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0115 }
            int r1 = com.baidu.location.h.i.b(r2, r1, r3)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r3 = "getLongitude"
            r10 = 0
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x0115 }
            int r3 = com.baidu.location.h.i.b(r2, r3, r10)     // Catch:{ Exception -> 0x0115 }
            java.lang.String r10 = "getLatitude"
            r11 = 0
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x0115 }
            int r2 = com.baidu.location.h.i.b(r2, r10, r11)     // Catch:{ Exception -> 0x0115 }
            r0.setCellLocationData(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x0115 }
            r1 = r6
        L_0x00c6:
            r2 = 4
            if (r7 == r2) goto L_0x0009
            r0 = r1
            goto L_0x0009
        L_0x00cc:
            r0 = 3
            if (r7 != r0) goto L_0x00ed
            java.lang.String r0 = "getTac"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0111 }
            int r3 = com.baidu.location.h.i.b(r2, r0, r3)     // Catch:{ Exception -> 0x0111 }
            java.lang.String r0 = "getCi"
            r4 = 0
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0111 }
            int r2 = com.baidu.location.h.i.b(r2, r0, r4)     // Catch:{ Exception -> 0x0111 }
            android.telephony.gsm.GsmCellLocation r0 = new android.telephony.gsm.GsmCellLocation     // Catch:{ Exception -> 0x0111 }
            r0.<init>()     // Catch:{ Exception -> 0x0111 }
            r0.setLacAndCid(r3, r2)     // Catch:{ Exception -> 0x011a }
            r12 = r1
            r1 = r0
            r0 = r12
            goto L_0x00c6
        L_0x00ed:
            java.lang.String r0 = "getLac"
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0111 }
            int r3 = com.baidu.location.h.i.b(r2, r0, r3)     // Catch:{ Exception -> 0x0111 }
            java.lang.String r0 = "getCid"
            r4 = 0
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0111 }
            int r2 = com.baidu.location.h.i.b(r2, r0, r4)     // Catch:{ Exception -> 0x0111 }
            android.telephony.gsm.GsmCellLocation r0 = new android.telephony.gsm.GsmCellLocation     // Catch:{ Exception -> 0x0111 }
            r0.<init>()     // Catch:{ Exception -> 0x0111 }
            r0.setLacAndCid(r3, r2)     // Catch:{ Exception -> 0x011f }
            r12 = r1
            r1 = r0
            r0 = r12
            goto L_0x00c6
        L_0x010b:
            r0 = r7
            goto L_0x0021
        L_0x010e:
            r2 = move-exception
            goto L_0x0021
        L_0x0111:
            r0 = move-exception
            r0 = r7
            goto L_0x0021
        L_0x0115:
            r1 = move-exception
            r1 = r0
            r0 = r7
            goto L_0x0021
        L_0x011a:
            r2 = move-exception
            r6 = r0
            r0 = r7
            goto L_0x0021
        L_0x011f:
            r2 = move-exception
            r6 = r0
            r0 = r7
            goto L_0x0021
        L_0x0124:
            r7 = r0
            r0 = r1
            r1 = r6
            goto L_0x00c6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.f.b.a(java.util.List):android.telephony.CellLocation");
    }

    private a a(CellInfo cellInfo) {
        int intValue = Integer.valueOf(Build.VERSION.SDK_INT).intValue();
        if (intValue < 17) {
            return null;
        }
        a aVar = new a();
        boolean z = false;
        if (cellInfo instanceof CellInfoGsm) {
            CellIdentityGsm cellIdentity = ((CellInfoGsm) cellInfo).getCellIdentity();
            aVar.c = a(cellIdentity.getMcc());
            aVar.d = a(cellIdentity.getMnc());
            aVar.a = a(cellIdentity.getLac());
            aVar.b = a(cellIdentity.getCid());
            aVar.i = 'g';
            aVar.h = ((CellInfoGsm) cellInfo).getCellSignalStrength().getAsuLevel();
            z = true;
        } else if (cellInfo instanceof CellInfoCdma) {
            CellIdentityCdma cellIdentity2 = ((CellInfoCdma) cellInfo).getCellIdentity();
            aVar.e = cellIdentity2.getLatitude();
            aVar.f = cellIdentity2.getLongitude();
            aVar.d = a(cellIdentity2.getSystemId());
            aVar.a = a(cellIdentity2.getNetworkId());
            aVar.b = a(cellIdentity2.getBasestationId());
            aVar.i = 'c';
            aVar.h = ((CellInfoCdma) cellInfo).getCellSignalStrength().getCdmaDbm();
            z = true;
        } else if (cellInfo instanceof CellInfoLte) {
            CellIdentityLte cellIdentity3 = ((CellInfoLte) cellInfo).getCellIdentity();
            aVar.c = a(cellIdentity3.getMcc());
            aVar.d = a(cellIdentity3.getMnc());
            aVar.a = a(cellIdentity3.getTac());
            aVar.b = a(cellIdentity3.getCi());
            aVar.i = 'g';
            aVar.h = ((CellInfoLte) cellInfo).getCellSignalStrength().getAsuLevel();
            z = true;
        }
        if (intValue >= 18 && !z) {
            try {
                if (cellInfo instanceof CellInfoWcdma) {
                    CellIdentityWcdma cellIdentity4 = ((CellInfoWcdma) cellInfo).getCellIdentity();
                    aVar.c = a(cellIdentity4.getMcc());
                    aVar.d = a(cellIdentity4.getMnc());
                    aVar.a = a(cellIdentity4.getLac());
                    aVar.b = a(cellIdentity4.getCid());
                    aVar.i = 'g';
                    aVar.h = ((CellInfoWcdma) cellInfo).getCellSignalStrength().getAsuLevel();
                }
            } catch (Exception e2) {
            }
        }
        try {
            aVar.g = System.currentTimeMillis() - ((SystemClock.elapsedRealtimeNanos() - cellInfo.getTimeStamp()) / 1000000);
        } catch (Error e3) {
            aVar.g = System.currentTimeMillis();
        }
        return aVar;
    }

    private a a(CellLocation cellLocation) {
        return a(cellLocation, false);
    }

    private a a(CellLocation cellLocation, boolean z) {
        int i2 = 0;
        if (cellLocation == null || this.d == null) {
            return null;
        }
        a aVar = new a();
        if (z) {
            aVar.f();
        }
        aVar.g = System.currentTimeMillis();
        try {
            String networkOperator = this.d.getNetworkOperator();
            if (networkOperator != null && networkOperator.length() > 0) {
                if (networkOperator.length() >= 3) {
                    int intValue = Integer.valueOf(networkOperator.substring(0, 3)).intValue();
                    if (intValue < 0) {
                        intValue = this.f.c;
                    }
                    aVar.c = intValue;
                }
                String substring = networkOperator.substring(3);
                if (substring != null) {
                    char[] charArray = substring.toCharArray();
                    while (i2 < charArray.length && Character.isDigit(charArray[i2])) {
                        i2++;
                    }
                }
                int intValue2 = Integer.valueOf(substring.substring(0, i2)).intValue();
                if (intValue2 < 0) {
                    intValue2 = this.f.d;
                }
                aVar.d = intValue2;
            }
            a = this.d.getSimState();
        } catch (Exception e2) {
            b = 1;
        }
        if (cellLocation instanceof GsmCellLocation) {
            aVar.a = ((GsmCellLocation) cellLocation).getLac();
            aVar.b = ((GsmCellLocation) cellLocation).getCid();
            aVar.i = 'g';
        } else if (cellLocation instanceof CdmaCellLocation) {
            aVar.i = 'c';
            if (Integer.valueOf(Build.VERSION.SDK_INT).intValue() < 5) {
                return aVar;
            }
            if (p == null) {
                try {
                    p = Class.forName("android.telephony.cdma.CdmaCellLocation");
                    k = p.getMethod("getBaseStationId", new Class[0]);
                    l = p.getMethod("getNetworkId", new Class[0]);
                    m = p.getMethod("getSystemId", new Class[0]);
                    n = p.getMethod("getBaseStationLatitude", new Class[0]);
                    o = p.getMethod("getBaseStationLongitude", new Class[0]);
                } catch (Exception e3) {
                    p = null;
                    b = 2;
                    return aVar;
                }
            }
            if (p != null && p.isInstance(cellLocation)) {
                try {
                    int intValue3 = ((Integer) m.invoke(cellLocation, new Object[0])).intValue();
                    if (intValue3 < 0) {
                        intValue3 = this.f.d;
                    }
                    aVar.d = intValue3;
                    aVar.b = ((Integer) k.invoke(cellLocation, new Object[0])).intValue();
                    aVar.a = ((Integer) l.invoke(cellLocation, new Object[0])).intValue();
                    Object invoke = n.invoke(cellLocation, new Object[0]);
                    if (((Integer) invoke).intValue() < Integer.MAX_VALUE) {
                        aVar.e = ((Integer) invoke).intValue();
                    }
                    Object invoke2 = o.invoke(cellLocation, new Object[0]);
                    if (((Integer) invoke2).intValue() < Integer.MAX_VALUE) {
                        aVar.f = ((Integer) invoke2).intValue();
                    }
                } catch (Exception e4) {
                    b = 3;
                    return aVar;
                }
            }
        }
        c(aVar);
        return aVar;
    }

    public static synchronized b a() {
        b bVar;
        synchronized (b.class) {
            if (c == null) {
                c = new b();
            }
            bVar = c;
        }
        return bVar;
    }

    private void c(a aVar) {
        if (!aVar.b()) {
            return;
        }
        if (this.f == null || !this.f.a(aVar)) {
            this.f = aVar;
            if (aVar.b()) {
                int size = this.h.size();
                a aVar2 = size == 0 ? null : this.h.get(size - 1);
                if (aVar2 == null || aVar2.b != this.f.b || aVar2.a != this.f.a) {
                    this.h.add(this.f);
                    if (this.h.size() > 3) {
                        this.h.remove(0);
                    }
                    i();
                    this.q = false;
                }
            } else if (this.h != null) {
                this.h.clear();
            }
        }
    }

    private String d(a aVar) {
        a a2;
        StringBuilder sb = new StringBuilder();
        if (Integer.valueOf(Build.VERSION.SDK_INT).intValue() >= 17) {
            try {
                List<CellInfo> allCellInfo = this.d.getAllCellInfo();
                if (allCellInfo != null && allCellInfo.size() > 0) {
                    sb.append("&nc=");
                    for (CellInfo next : allCellInfo) {
                        if (!(next.isRegistered() || (a2 = a(next)) == null || a2.a == -1 || a2.b == -1)) {
                            if (aVar.a != a2.a) {
                                sb.append(a2.a + "|" + a2.b + "|" + a2.h + ";");
                            } else {
                                sb.append("|" + a2.b + "|" + a2.h + ";");
                            }
                        }
                    }
                }
            } catch (Exception | NoSuchMethodError e2) {
            }
        }
        return sb.toString();
    }

    private void h() {
        String f2 = i.f();
        if (f2 != null) {
            File file = new File(f2 + File.separator + "lcvif.dat");
            if (file.exists()) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    if (System.currentTimeMillis() - randomAccessFile.readLong() > 60000) {
                        randomAccessFile.close();
                        file.delete();
                        return;
                    }
                    randomAccessFile.readInt();
                    for (int i2 = 0; i2 < 3; i2++) {
                        long readLong = randomAccessFile.readLong();
                        int readInt = randomAccessFile.readInt();
                        int readInt2 = randomAccessFile.readInt();
                        int readInt3 = randomAccessFile.readInt();
                        int readInt4 = randomAccessFile.readInt();
                        int readInt5 = randomAccessFile.readInt();
                        char c2 = 0;
                        if (readInt5 == 1) {
                            c2 = 'g';
                        }
                        if (readInt5 == 2) {
                            c2 = 'c';
                        }
                        if (readLong != 0) {
                            a aVar = new a(readInt3, readInt4, readInt, readInt2, 0, c2);
                            aVar.g = readLong;
                            if (aVar.b()) {
                                this.q = true;
                                this.h.add(aVar);
                            }
                        }
                    }
                    randomAccessFile.close();
                } catch (Exception e2) {
                    file.delete();
                }
            }
        }
    }

    private void i() {
        if (this.h != null || this.g != null) {
            if (this.h == null && this.g != null) {
                this.h = new LinkedList();
                this.h.add(this.g);
            }
            String f2 = i.f();
            if (f2 != null) {
                File file = new File(f2 + File.separator + "lcvif.dat");
                int size = this.h.size();
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    randomAccessFile.writeLong(this.h.get(size - 1).g);
                    randomAccessFile.writeInt(size);
                    for (int i2 = 0; i2 < 3 - size; i2++) {
                        randomAccessFile.writeLong(0);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeInt(2);
                    }
                    for (int i3 = 0; i3 < size; i3++) {
                        randomAccessFile.writeLong(this.h.get(i3).g);
                        randomAccessFile.writeInt(this.h.get(i3).c);
                        randomAccessFile.writeInt(this.h.get(i3).d);
                        randomAccessFile.writeInt(this.h.get(i3).a);
                        randomAccessFile.writeInt(this.h.get(i3).b);
                        if (this.h.get(i3).i == 'g') {
                            randomAccessFile.writeInt(1);
                        } else if (this.h.get(i3).i == 'c') {
                            randomAccessFile.writeInt(2);
                        } else {
                            randomAccessFile.writeInt(3);
                        }
                    }
                    randomAccessFile.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void j() {
        CellLocation k2;
        a m2 = m();
        if (m2 != null) {
            c(m2);
        }
        if (m2 == null || !m2.b()) {
            a a2 = a(this.d.getCellLocation());
            if ((a2 == null || !a2.b()) && (k2 = k()) != null) {
                Log.i(com.baidu.location.h.b.a, "cell sim2 cell is valid");
                a(k2, true);
            }
        }
    }

    private CellLocation k() {
        CellLocation cellLocation;
        Object obj;
        List list;
        Object obj2 = this.e;
        if (obj2 == null) {
            return null;
        }
        try {
            Class<?> l2 = l();
            if (l2.isInstance(obj2)) {
                Object cast = l2.cast(obj2);
                try {
                    obj = i.a((Object) cast, "getCellLocation", new Object[0]);
                } catch (NoSuchMethodException e2) {
                    obj = null;
                } catch (Exception e3) {
                    obj = null;
                }
                if (obj == null) {
                    try {
                        obj = i.a((Object) cast, "getCellLocation", 1);
                    } catch (Exception | NoSuchMethodException e4) {
                    }
                }
                if (obj == null) {
                    try {
                        obj = i.a((Object) cast, "getCellLocationGemini", 1);
                    } catch (Exception | NoSuchMethodException e5) {
                    }
                }
                if (obj == null) {
                    try {
                        list = (List) i.a((Object) cast, "getAllCellInfo", new Object[0]);
                    } catch (NoSuchMethodException e6) {
                        list = null;
                    } catch (Exception e7) {
                        list = null;
                    }
                    obj = a((List<?>) list);
                    if (obj != null) {
                    }
                }
            } else {
                obj = null;
            }
            cellLocation = obj != null ? (CellLocation) obj : null;
        } catch (Exception e8) {
            cellLocation = null;
        }
        return cellLocation;
    }

    private Class<?> l() {
        String str;
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        switch (n()) {
            case 0:
                str = "android.telephony.TelephonyManager";
                break;
            case 1:
                str = "android.telephony.MSimTelephonyManager";
                break;
            case 2:
                str = "android.telephony.TelephonyManager2";
                break;
            default:
                str = null;
                break;
        }
        try {
            return systemClassLoader.loadClass(str);
        } catch (Exception e2) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        return null;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0046 A[ExcHandler: NoSuchMethodError (e java.lang.NoSuchMethodError), Splitter:B:2:0x0010] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.baidu.location.f.a m() {
        /*
            r5 = this;
            r1 = 0
            int r0 = android.os.Build.VERSION.SDK_INT
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            int r0 = r0.intValue()
            r2 = 17
            if (r0 >= r2) goto L_0x0010
        L_0x000f:
            return r1
        L_0x0010:
            android.telephony.TelephonyManager r0 = r5.d     // Catch:{ Exception -> 0x0048, NoSuchMethodError -> 0x0046 }
            java.util.List r0 = r0.getAllCellInfo()     // Catch:{ Exception -> 0x0048, NoSuchMethodError -> 0x0046 }
            if (r0 == 0) goto L_0x000f
            int r2 = r0.size()     // Catch:{ Exception -> 0x0048, NoSuchMethodError -> 0x0046 }
            if (r2 <= 0) goto L_0x000f
            java.util.Iterator r3 = r0.iterator()     // Catch:{ Exception -> 0x0048, NoSuchMethodError -> 0x0046 }
            r2 = r1
        L_0x0023:
            boolean r0 = r3.hasNext()     // Catch:{ Exception -> 0x004a, NoSuchMethodError -> 0x0046 }
            if (r0 == 0) goto L_0x0050
            java.lang.Object r0 = r3.next()     // Catch:{ Exception -> 0x004a, NoSuchMethodError -> 0x0046 }
            android.telephony.CellInfo r0 = (android.telephony.CellInfo) r0     // Catch:{ Exception -> 0x004a, NoSuchMethodError -> 0x0046 }
            boolean r4 = r0.isRegistered()     // Catch:{ Exception -> 0x004a, NoSuchMethodError -> 0x0046 }
            if (r4 == 0) goto L_0x0023
            com.baidu.location.f.a r0 = r5.a((android.telephony.CellInfo) r0)     // Catch:{ Exception -> 0x004a, NoSuchMethodError -> 0x0046 }
            if (r0 != 0) goto L_0x003d
            r2 = r0
            goto L_0x0023
        L_0x003d:
            boolean r2 = r0.b()     // Catch:{ Exception -> 0x004d, NoSuchMethodError -> 0x0046 }
            if (r2 != 0) goto L_0x0044
            r0 = r1
        L_0x0044:
            r1 = r0
            goto L_0x000f
        L_0x0046:
            r0 = move-exception
            goto L_0x000f
        L_0x0048:
            r0 = move-exception
            goto L_0x000f
        L_0x004a:
            r0 = move-exception
            r1 = r2
            goto L_0x000f
        L_0x004d:
            r1 = move-exception
            r1 = r0
            goto L_0x000f
        L_0x0050:
            r1 = r2
            goto L_0x000f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.f.b.m():com.baidu.location.f.a");
    }

    private int n() {
        int i2 = 0;
        try {
            Class.forName("android.telephony.MSimTelephonyManager");
            i2 = 1;
        } catch (Exception e2) {
        }
        if (i2 != 0) {
            return i2;
        }
        try {
            Class.forName("android.telephony.TelephonyManager2");
            return 2;
        } catch (Exception e3) {
            return i2;
        }
    }

    public String a(a aVar) {
        String str;
        try {
            str = d(aVar);
            if (str != null && !str.equals("") && !str.equals("&nc=")) {
                return str;
            }
            List neighboringCellInfo = this.d.getNeighboringCellInfo();
            if (neighboringCellInfo != null && !neighboringCellInfo.isEmpty()) {
                String str2 = "&nc=";
                Iterator it = neighboringCellInfo.iterator();
                int i2 = 0;
                while (true) {
                    if (!it.hasNext()) {
                        str = str2;
                        break;
                    }
                    NeighboringCellInfo neighboringCellInfo2 = (NeighboringCellInfo) it.next();
                    int lac = neighboringCellInfo2.getLac();
                    str = (lac == -1 || neighboringCellInfo2.getCid() == -1) ? str2 : aVar.a != lac ? str2 + lac + "|" + neighboringCellInfo2.getCid() + "|" + neighboringCellInfo2.getRssi() + ";" : str2 + "|" + neighboringCellInfo2.getCid() + "|" + neighboringCellInfo2.getRssi() + ";";
                    int i3 = i2 + 1;
                    if (i3 >= 8) {
                        break;
                    }
                    i2 = i3;
                    str2 = str;
                }
            }
            if (str == null || !str.equals("&nc=")) {
                return str;
            }
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            str = "";
        }
    }

    public String b(a aVar) {
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&nw=");
        stringBuffer.append(aVar.i);
        stringBuffer.append(String.format(Locale.CHINA, "&cl=%d|%d|%d|%d&cl_s=%d", new Object[]{Integer.valueOf(aVar.c), Integer.valueOf(aVar.d), Integer.valueOf(aVar.a), Integer.valueOf(aVar.b), Integer.valueOf(aVar.h)}));
        if (aVar.e < Integer.MAX_VALUE && aVar.f < Integer.MAX_VALUE) {
            stringBuffer.append(String.format(Locale.CHINA, "&cdmall=%.6f|%.6f", new Object[]{Double.valueOf(((double) aVar.f) / 14400.0d), Double.valueOf(((double) aVar.e) / 14400.0d)}));
        }
        stringBuffer.append("&cl_t=");
        stringBuffer.append(aVar.g);
        if (this.h != null && this.h.size() > 0) {
            int size = this.h.size();
            stringBuffer.append("&clt=");
            for (int i2 = 0; i2 < size; i2++) {
                a aVar2 = this.h.get(i2);
                if (aVar2.c != aVar.c) {
                    stringBuffer.append(aVar2.c);
                }
                stringBuffer.append("|");
                if (aVar2.d != aVar.d) {
                    stringBuffer.append(aVar2.d);
                }
                stringBuffer.append("|");
                if (aVar2.a != aVar.a) {
                    stringBuffer.append(aVar2.a);
                }
                stringBuffer.append("|");
                if (aVar2.b != aVar.b) {
                    stringBuffer.append(aVar2.b);
                }
                stringBuffer.append("|");
                stringBuffer.append((System.currentTimeMillis() - aVar2.g) / 1000);
                stringBuffer.append(";");
            }
        }
        if (a > 100) {
            a = 0;
        }
        stringBuffer.append("&cs=" + ((b << 8) + a));
        return stringBuffer.toString();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void b() {
        /*
            r3 = this;
            r1 = 1
            monitor-enter(r3)
            boolean r0 = r3.j     // Catch:{ all -> 0x0047 }
            if (r0 != r1) goto L_0x0008
        L_0x0006:
            monitor-exit(r3)
            return
        L_0x0008:
            boolean r0 = com.baidu.location.f.isServing     // Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0006
            android.content.Context r0 = com.baidu.location.f.getServiceContext()     // Catch:{ all -> 0x0047 }
            java.lang.String r1 = "phone"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ all -> 0x0047 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ all -> 0x0047 }
            r3.d = r0     // Catch:{ all -> 0x0047 }
            java.util.LinkedList r0 = new java.util.LinkedList     // Catch:{ all -> 0x0047 }
            r0.<init>()     // Catch:{ all -> 0x0047 }
            r3.h = r0     // Catch:{ all -> 0x0047 }
            com.baidu.location.f.b$a r0 = new com.baidu.location.f.b$a     // Catch:{ all -> 0x0047 }
            r0.<init>()     // Catch:{ all -> 0x0047 }
            r3.i = r0     // Catch:{ all -> 0x0047 }
            r3.h()     // Catch:{ all -> 0x0047 }
            android.telephony.TelephonyManager r0 = r3.d     // Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0006
            com.baidu.location.f.b$a r0 = r3.i     // Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0006
            android.telephony.TelephonyManager r0 = r3.d     // Catch:{ Exception -> 0x0076 }
            com.baidu.location.f.b$a r1 = r3.i     // Catch:{ Exception -> 0x0076 }
            r2 = 272(0x110, float:3.81E-43)
            r0.listen(r1, r2)     // Catch:{ Exception -> 0x0076 }
        L_0x003c:
            int r0 = r3.n()     // Catch:{ Throwable -> 0x0057 }
            switch(r0) {
                case 0: goto L_0x0069;
                case 1: goto L_0x004a;
                case 2: goto L_0x005c;
                default: goto L_0x0043;
            }
        L_0x0043:
            r0 = 1
            r3.j = r0     // Catch:{ all -> 0x0047 }
            goto L_0x0006
        L_0x0047:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x004a:
            android.content.Context r0 = com.baidu.location.f.getServiceContext()     // Catch:{ Throwable -> 0x0057 }
            java.lang.String r1 = "phone_msim"
            java.lang.Object r0 = com.baidu.location.h.i.a(r0, r1)     // Catch:{ Throwable -> 0x0057 }
            r3.e = r0     // Catch:{ Throwable -> 0x0057 }
            goto L_0x0043
        L_0x0057:
            r0 = move-exception
            r0 = 0
            r3.e = r0     // Catch:{ all -> 0x0047 }
            goto L_0x0043
        L_0x005c:
            android.content.Context r0 = com.baidu.location.f.getServiceContext()     // Catch:{ Throwable -> 0x0057 }
            java.lang.String r1 = "phone2"
            java.lang.Object r0 = com.baidu.location.h.i.a(r0, r1)     // Catch:{ Throwable -> 0x0057 }
            r3.e = r0     // Catch:{ Throwable -> 0x0057 }
            goto L_0x0043
        L_0x0069:
            android.content.Context r0 = com.baidu.location.f.getServiceContext()     // Catch:{ Throwable -> 0x0057 }
            java.lang.String r1 = "phone2"
            java.lang.Object r0 = com.baidu.location.h.i.a(r0, r1)     // Catch:{ Throwable -> 0x0057 }
            r3.e = r0     // Catch:{ Throwable -> 0x0057 }
            goto L_0x0043
        L_0x0076:
            r0 = move-exception
            goto L_0x003c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.f.b.b():void");
    }

    public synchronized void c() {
        if (this.j) {
            if (!(this.i == null || this.d == null)) {
                this.d.listen(this.i, 0);
            }
            this.i = null;
            this.d = null;
            this.h.clear();
            this.h = null;
            i();
            this.j = false;
        }
    }

    public boolean d() {
        return this.q;
    }

    public int e() {
        if (this.d == null) {
            return 0;
        }
        try {
            return this.d.getNetworkType();
        } catch (Exception e2) {
            return 0;
        }
    }

    public a f() {
        if ((this.f == null || !this.f.a() || !this.f.b()) && this.d != null) {
            try {
                j();
            } catch (Exception e2) {
            }
        }
        if (this.f.e()) {
            this.g = null;
            this.g = new a(this.f.a, this.f.b, this.f.c, this.f.d, this.f.h, this.f.i);
        }
        if (this.f.d() && this.g != null && this.f.i == 'g') {
            this.f.d = this.g.d;
            this.f.c = this.g.c;
        }
        return this.f;
    }

    public int g() {
        String str;
        try {
            str = ((TelephonyManager) f.getServiceContext().getSystemService("phone")).getSubscriberId();
        } catch (Exception e2) {
            str = null;
        }
        if (str != null) {
            if (str.startsWith("46000") || str.startsWith("46002") || str.startsWith("46007")) {
                return 1;
            }
            if (str.startsWith("46001")) {
                return 2;
            }
            if (str.startsWith("46003")) {
                return 3;
            }
        }
        return 0;
    }
}
