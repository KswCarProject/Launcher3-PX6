package com.baidu.location.a;

import android.location.Location;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.c.h;
import com.baidu.location.f.f;
import com.baidu.location.h.c;
import com.baidu.location.h.i;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class a {
    private static a b = null;
    public boolean a;
    private ArrayList<C0000a> c;
    private boolean d;
    private BDLocation e;

    /* renamed from: com.baidu.location.a.a$a  reason: collision with other inner class name */
    private class C0000a {
        public String a = null;
        public Messenger b = null;
        public LocationClientOption c = new LocationClientOption();
        public int d = 0;
        final /* synthetic */ a e;

        public C0000a(a aVar, Message message) {
            boolean z = true;
            this.e = aVar;
            this.b = message.replyTo;
            this.a = message.getData().getString("packName");
            this.c.prodName = message.getData().getString("prodName");
            c.a().a(this.c.prodName, this.a);
            this.c.coorType = message.getData().getString("coorType");
            this.c.addrType = message.getData().getString("addrType");
            this.c.enableSimulateGps = message.getData().getBoolean("enableSimulateGps", false);
            i.k = i.k || this.c.enableSimulateGps;
            if (!i.f.equals("all")) {
                i.f = this.c.addrType;
            }
            this.c.openGps = message.getData().getBoolean("openGPS");
            this.c.scanSpan = message.getData().getInt("scanSpan");
            this.c.timeOut = message.getData().getInt("timeOut");
            this.c.priority = message.getData().getInt("priority");
            this.c.location_change_notify = message.getData().getBoolean("location_change_notify");
            this.c.mIsNeedDeviceDirect = message.getData().getBoolean("needDirect", false);
            this.c.isNeedAltitude = message.getData().getBoolean("isneedaltitude", false);
            i.g = i.g || message.getData().getBoolean("isneedaptag", false);
            if (!i.h && !message.getData().getBoolean("isneedaptagd", false)) {
                z = false;
            }
            i.h = z;
            i.O = message.getData().getFloat("autoNotifyLocSensitivity", 0.5f);
            int i = message.getData().getInt("autoNotifyMaxInterval", 0);
            if (i >= i.T) {
                i.T = i;
            }
            int i2 = message.getData().getInt("autoNotifyMinDistance", 0);
            if (i2 >= i.V) {
                i.V = i2;
            }
            int i3 = message.getData().getInt("autoNotifyMinTimeInterval", 0);
            if (i3 >= i.U) {
                i.U = i3;
            }
            if (this.c.scanSpan >= 1000) {
                h.a().b();
            }
            if (this.c.mIsNeedDeviceDirect || this.c.isNeedAltitude) {
                f.a().a(this.c.mIsNeedDeviceDirect);
                f.a().b(this.c.isNeedAltitude);
                f.a().b();
            }
        }

        /* access modifiers changed from: private */
        public void a(int i) {
            Message obtain = Message.obtain((Handler) null, i);
            try {
                if (this.b != null) {
                    this.b.send(obtain);
                }
                this.d = 0;
            } catch (Exception e2) {
                if (e2 instanceof DeadObjectException) {
                    this.d++;
                }
            }
        }

        private void a(int i, String str, BDLocation bDLocation) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(str, bDLocation);
            bundle.setClassLoader(BDLocation.class.getClassLoader());
            Message obtain = Message.obtain((Handler) null, i);
            obtain.setData(bundle);
            try {
                if (this.b != null) {
                    this.b.send(obtain);
                }
                this.d = 0;
            } catch (Exception e2) {
                if (e2 instanceof DeadObjectException) {
                    this.d++;
                }
            }
        }

        public void a() {
            if (!this.c.location_change_notify) {
                return;
            }
            if (i.b) {
                a(54);
            } else {
                a(55);
            }
        }

        public void a(BDLocation bDLocation) {
            a(bDLocation, 21);
        }

        public void a(BDLocation bDLocation, int i) {
            BDLocation bDLocation2 = new BDLocation(bDLocation);
            if (f.a().g() && (bDLocation2.getLocType() == 161 || bDLocation2.getLocType() == 66)) {
                bDLocation2.setAltitude(f.a().i());
            }
            if (i == 21) {
                a(27, "locStr", bDLocation2);
            }
            if (this.c.coorType != null && !this.c.coorType.equals("gcj02")) {
                double longitude = bDLocation2.getLongitude();
                double latitude = bDLocation2.getLatitude();
                if (!(longitude == Double.MIN_VALUE || latitude == Double.MIN_VALUE)) {
                    if ((bDLocation2.getCoorType() != null && bDLocation2.getCoorType().equals("gcj02")) || bDLocation2.getCoorType() == null) {
                        double[] coorEncrypt = Jni.coorEncrypt(longitude, latitude, this.c.coorType);
                        bDLocation2.setLongitude(coorEncrypt[0]);
                        bDLocation2.setLatitude(coorEncrypt[1]);
                        bDLocation2.setCoorType(this.c.coorType);
                    } else if (bDLocation2.getCoorType() != null && bDLocation2.getCoorType().equals("wgs84") && !this.c.coorType.equals(BDLocation.BDLOCATION_GCJ02_TO_BD09LL)) {
                        double[] coorEncrypt2 = Jni.coorEncrypt(longitude, latitude, "wgs842mc");
                        bDLocation2.setLongitude(coorEncrypt2[0]);
                        bDLocation2.setLatitude(coorEncrypt2[1]);
                        bDLocation2.setCoorType("wgs84mc");
                    }
                }
            }
            a(i, "locStr", bDLocation2);
        }
    }

    private a() {
        this.c = null;
        this.d = false;
        this.a = false;
        this.e = null;
        this.c = new ArrayList<>();
    }

    private C0000a a(Messenger messenger) {
        if (this.c == null) {
            return null;
        }
        Iterator<C0000a> it = this.c.iterator();
        while (it.hasNext()) {
            C0000a next = it.next();
            if (next.b.equals(messenger)) {
                return next;
            }
        }
        return null;
    }

    public static a a() {
        if (b == null) {
            b = new a();
        }
        return b;
    }

    private void a(C0000a aVar) {
        if (aVar != null) {
            if (a(aVar.b) != null) {
                aVar.a(14);
                return;
            }
            this.c.add(aVar);
            aVar.a(13);
        }
    }

    private void e() {
        f();
        d();
    }

    private void f() {
        Iterator<C0000a> it = this.c.iterator();
        boolean z = false;
        boolean z2 = false;
        while (it.hasNext()) {
            C0000a next = it.next();
            if (next.c.openGps) {
                z2 = true;
            }
            z = next.c.location_change_notify ? true : z;
        }
        i.a = z;
        if (this.d != z2) {
            this.d = z2;
            f.a().a(this.d);
        }
    }

    public void a(Message message) {
        if (message != null && message.replyTo != null) {
            this.a = true;
            a(new C0000a(this, message));
            e();
        }
    }

    public void a(BDLocation bDLocation) {
        boolean z = e.h;
        if (z) {
            e.h = false;
        }
        if (i.T >= 10000 && (bDLocation.getLocType() == 61 || bDLocation.getLocType() == 161 || bDLocation.getLocType() == 66)) {
            if (this.e != null) {
                float[] fArr = new float[1];
                Location.distanceBetween(this.e.getLatitude(), this.e.getLongitude(), bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
                if (fArr[0] > ((float) i.V) || z) {
                    this.e = null;
                    this.e = new BDLocation(bDLocation);
                } else {
                    return;
                }
            } else {
                this.e = new BDLocation(bDLocation);
            }
        }
        Iterator<C0000a> it = this.c.iterator();
        while (it.hasNext()) {
            try {
                C0000a next = it.next();
                next.a(bDLocation);
                if (next.d > 4) {
                    it.remove();
                }
            } catch (Exception e2) {
                return;
            }
        }
    }

    public void a(String str) {
        BDLocation bDLocation = new BDLocation(str);
        Address a2 = e.b().a(bDLocation);
        String e2 = e.b().e();
        List<Poi> f = e.b().f();
        if (a2 != null) {
            bDLocation.setAddr(a2);
        }
        if (e2 != null) {
            bDLocation.setLocationDescribe(e2);
        }
        if (f != null) {
            bDLocation.setPoiList(f);
        }
        a(bDLocation);
    }

    public void b() {
        this.c.clear();
        this.e = null;
        e();
    }

    public void b(Message message) {
        C0000a a2 = a(message.replyTo);
        if (a2 != null) {
            this.c.remove(a2);
        }
        h.a().c();
        f.a().c();
        e();
    }

    public String c() {
        StringBuffer stringBuffer = new StringBuffer(256);
        if (this.c.isEmpty()) {
            return "&prod=" + c.d + ":" + c.c;
        }
        C0000a aVar = this.c.get(0);
        if (aVar.c.prodName != null) {
            stringBuffer.append(aVar.c.prodName);
        }
        if (aVar.a != null) {
            stringBuffer.append(":");
            stringBuffer.append(aVar.a);
            stringBuffer.append("|");
        }
        String stringBuffer2 = stringBuffer.toString();
        if (stringBuffer2 == null || stringBuffer2.equals("")) {
            return null;
        }
        return "&prod=" + stringBuffer2;
    }

    public boolean c(Message message) {
        boolean z = true;
        C0000a a2 = a(message.replyTo);
        if (a2 == null) {
            return false;
        }
        int i = a2.c.scanSpan;
        a2.c.scanSpan = message.getData().getInt("scanSpan", a2.c.scanSpan);
        if (a2.c.scanSpan < 1000) {
            h.a().e();
            f.a().c();
            this.a = false;
        } else {
            h.a().d();
            this.a = true;
        }
        if (a2.c.scanSpan <= 999 || i >= 1000) {
            z = false;
        } else if (a2.c.mIsNeedDeviceDirect || a2.c.isNeedAltitude) {
            f.a().a(a2.c.mIsNeedDeviceDirect);
            f.a().b(a2.c.isNeedAltitude);
            f.a().b();
        }
        a2.c.openGps = message.getData().getBoolean("openGPS", a2.c.openGps);
        String string = message.getData().getString("coorType");
        LocationClientOption locationClientOption = a2.c;
        if (string == null || string.equals("")) {
            string = a2.c.coorType;
        }
        locationClientOption.coorType = string;
        String string2 = message.getData().getString("addrType");
        LocationClientOption locationClientOption2 = a2.c;
        if (string2 == null || string2.equals("")) {
            string2 = a2.c.addrType;
        }
        locationClientOption2.addrType = string2;
        if (!i.f.equals(a2.c.addrType)) {
            e.b().i();
        }
        a2.c.timeOut = message.getData().getInt("timeOut", a2.c.timeOut);
        a2.c.location_change_notify = message.getData().getBoolean("location_change_notify", a2.c.location_change_notify);
        a2.c.priority = message.getData().getInt("priority", a2.c.priority);
        e();
        return z;
    }

    public int d(Message message) {
        C0000a a2;
        if (message == null || message.replyTo == null || (a2 = a(message.replyTo)) == null || a2.c == null) {
            return 1;
        }
        return a2.c.priority;
    }

    public void d() {
        Iterator<C0000a> it = this.c.iterator();
        while (it.hasNext()) {
            it.next().a();
        }
    }

    public int e(Message message) {
        C0000a a2;
        if (message == null || message.replyTo == null || (a2 = a(message.replyTo)) == null || a2.c == null) {
            return 1000;
        }
        return a2.c.scanSpan;
    }
}
