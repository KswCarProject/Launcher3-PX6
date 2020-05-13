package com.baidu.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import com.baidu.location.a.b;
import com.szchoiceway.index.CanUtils;
import java.util.ArrayList;
import java.util.Iterator;

public final class LocationClient implements b.C0001b {
    private static final int MIN_REQUEST_SPAN = 1000;
    private static final int MSG_REG_LISTENER = 5;
    private static final int MSG_REG_NOTIFY_LISTENER = 8;
    private static final int MSG_REMOVE_NOTIFY = 10;
    private static final int MSG_REQ_LOC = 4;
    private static final int MSG_REQ_NOTIFY_LOC = 11;
    private static final int MSG_REQ_OFFLINE_LOC = 12;
    private static final int MSG_REQ_POI = 7;
    private static final int MSG_RIGSTER_NOTIFY = 9;
    private static final int MSG_SET_OPT = 3;
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_UNREG_LISTENER = 6;
    private static final String mTAG = "baidu_location_Client";
    private BDLocationListener NotifyLocationListenner = null;
    /* access modifiers changed from: private */
    public boolean clientFirst = false;
    /* access modifiers changed from: private */
    public Boolean firstConnected = true;
    private boolean inDoorState = false;
    /* access modifiers changed from: private */
    public boolean isScheduled = false;
    /* access modifiers changed from: private */
    public boolean isStop = true;
    private boolean isWaitingForLocation = false;
    /* access modifiers changed from: private */
    public boolean isWaitingLocTag = false;
    private long lastReceiveGpsTime = 0;
    private long lastReceiveLocationTime = 0;
    private Boolean mConfig_map = false;
    private Boolean mConfig_preimport = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Messenger unused = LocationClient.this.mServer = new Messenger(iBinder);
            if (LocationClient.this.mServer != null) {
                boolean unused2 = LocationClient.this.mIsStarted = true;
                Log.d("baidu_location_client", "baidu location connected ...");
                if (LocationClient.this.isStop) {
                    LocationClient.this.mHandler.obtainMessage(2).sendToTarget();
                    return;
                }
                try {
                    Message obtain = Message.obtain((Handler) null, 11);
                    obtain.replyTo = LocationClient.this.mMessenger;
                    obtain.setData(LocationClient.this.getOptionBundle());
                    LocationClient.this.mServer.send(obtain);
                    boolean unused3 = LocationClient.this.mIsStarted = true;
                    if (LocationClient.this.mOption != null) {
                        if (LocationClient.this.firstConnected.booleanValue()) {
                        }
                        LocationClient.this.mHandler.obtainMessage(4).sendToTarget();
                    }
                } catch (Exception e) {
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Messenger unused = LocationClient.this.mServer = null;
            boolean unused2 = LocationClient.this.mIsStarted = false;
        }
    };
    private Context mContext = null;
    private boolean mDebugByDev;
    /* access modifiers changed from: private */
    public boolean mGpsStatus = false;
    /* access modifiers changed from: private */
    public a mHandler = new a();
    /* access modifiers changed from: private */
    public boolean mIsStarted = false;
    private String mKey;
    private BDLocation mLastLocation = null;
    private long mLastRequestTime = 0;
    /* access modifiers changed from: private */
    public ArrayList<BDLocationListener> mLocationListeners = null;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final Messenger mMessenger = new Messenger(this.mHandler);
    private com.baidu.location.d.a mNotifyCache = null;
    /* access modifiers changed from: private */
    public LocationClientOption mOption = new LocationClientOption();
    private String mPackName = null;
    /* access modifiers changed from: private */
    public b mScheduledRequest = null;
    /* access modifiers changed from: private */
    public Messenger mServer = null;
    private com.baidu.location.a.b mloc = null;
    /* access modifiers changed from: private */
    public boolean serverFirst = false;
    private String serviceName = null;

    private class a extends Handler {
        private a() {
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    LocationClient.this.onStart();
                    return;
                case 2:
                    LocationClient.this.onStop();
                    return;
                case 3:
                    LocationClient.this.onSetOption(message);
                    return;
                case 4:
                    LocationClient.this.onRequestLocation();
                    return;
                case 5:
                    LocationClient.this.onRegisterListener(message);
                    return;
                case 6:
                    LocationClient.this.onUnRegisterListener(message);
                    return;
                case 7:
                    return;
                case 8:
                    LocationClient.this.onRegisterNotifyLocationListener(message);
                    return;
                case 9:
                    LocationClient.this.onRegisterNotify(message);
                    return;
                case 10:
                    LocationClient.this.onRemoveNotifyEvent(message);
                    return;
                case 11:
                    LocationClient.this.onRequestNotifyLocation();
                    return;
                case 12:
                    LocationClient.this.onRequestOffLineLocation();
                    return;
                case 21:
                    Bundle data = message.getData();
                    data.setClassLoader(BDLocation.class.getClassLoader());
                    BDLocation bDLocation = (BDLocation) data.getParcelable("locStr");
                    if (LocationClient.this.serverFirst || !LocationClient.this.clientFirst || bDLocation.getLocType() != 66) {
                        if (LocationClient.this.serverFirst || !LocationClient.this.clientFirst) {
                            if (!LocationClient.this.serverFirst) {
                                boolean unused = LocationClient.this.serverFirst = true;
                            }
                            LocationClient.this.onNewLocation(message, 21);
                            return;
                        }
                        boolean unused2 = LocationClient.this.serverFirst = true;
                        return;
                    }
                    return;
                case 26:
                    LocationClient.this.onNewLocation(message, 26);
                    return;
                case 27:
                    LocationClient.this.onNewNotifyLocation(message);
                    return;
                case 54:
                    if (LocationClient.this.mOption.location_change_notify) {
                        boolean unused3 = LocationClient.this.mGpsStatus = true;
                        return;
                    }
                    return;
                case CanUtils.CarGeelyBorui /*55*/:
                    if (LocationClient.this.mOption.location_change_notify) {
                        boolean unused4 = LocationClient.this.mGpsStatus = false;
                        return;
                    }
                    return;
                case 701:
                    LocationClient.this.sendFirstLoc((BDLocation) message.obj);
                    return;
                default:
                    super.handleMessage(message);
                    return;
            }
        }
    }

    private class b implements Runnable {
        private b() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r6 = this;
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this
                java.lang.Object r1 = r0.mLock
                monitor-enter(r1)
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                r2 = 0
                boolean unused = r0.isScheduled = r2     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                android.os.Messenger r0 = r0.mServer     // Catch:{ all -> 0x0036 }
                if (r0 == 0) goto L_0x001d
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                android.os.Messenger r0 = r0.mMessenger     // Catch:{ all -> 0x0036 }
                if (r0 != 0) goto L_0x001f
            L_0x001d:
                monitor-exit(r1)     // Catch:{ all -> 0x0036 }
            L_0x001e:
                return
            L_0x001f:
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                java.util.ArrayList r0 = r0.mLocationListeners     // Catch:{ all -> 0x0036 }
                if (r0 == 0) goto L_0x0034
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                java.util.ArrayList r0 = r0.mLocationListeners     // Catch:{ all -> 0x0036 }
                int r0 = r0.size()     // Catch:{ all -> 0x0036 }
                r2 = 1
                if (r0 >= r2) goto L_0x0039
            L_0x0034:
                monitor-exit(r1)     // Catch:{ all -> 0x0036 }
                goto L_0x001e
            L_0x0036:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0036 }
                throw r0
            L_0x0039:
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                boolean r0 = r0.isWaitingLocTag     // Catch:{ all -> 0x0036 }
                if (r0 == 0) goto L_0x006f
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient$b r0 = r0.mScheduledRequest     // Catch:{ all -> 0x0036 }
                if (r0 != 0) goto L_0x0055
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient$b r2 = new com.baidu.location.LocationClient$b     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient r3 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                r2.<init>()     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient.b unused = r0.mScheduledRequest = r2     // Catch:{ all -> 0x0036 }
            L_0x0055:
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient$a r0 = r0.mHandler     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient r2 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient$b r2 = r2.mScheduledRequest     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient r3 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClientOption r3 = r3.mOption     // Catch:{ all -> 0x0036 }
                int r3 = r3.scanSpan     // Catch:{ all -> 0x0036 }
                long r4 = (long) r3     // Catch:{ all -> 0x0036 }
                r0.postDelayed(r2, r4)     // Catch:{ all -> 0x0036 }
                monitor-exit(r1)     // Catch:{ all -> 0x0036 }
                goto L_0x001e
            L_0x006f:
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0036 }
                com.baidu.location.LocationClient$a r0 = r0.mHandler     // Catch:{ all -> 0x0036 }
                r2 = 4
                android.os.Message r0 = r0.obtainMessage(r2)     // Catch:{ all -> 0x0036 }
                r0.sendToTarget()     // Catch:{ all -> 0x0036 }
                monitor-exit(r1)     // Catch:{ all -> 0x0036 }
                goto L_0x001e
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.LocationClient.b.run():void");
        }
    }

    public LocationClient(Context context) {
        this.mContext = context;
        this.mOption = new LocationClientOption();
    }

    public LocationClient(Context context, LocationClientOption locationClientOption) {
        this.mContext = context;
        this.mOption = locationClientOption;
    }

    private void callListeners(int i) {
        if (this.mLastLocation.getCoorType() == null) {
            this.mLastLocation.setCoorType(this.mOption.coorType);
        }
        if (this.isWaitingForLocation || ((this.mOption.location_change_notify && this.mLastLocation.getLocType() == 61) || this.mLastLocation.getLocType() == 66 || this.mLastLocation.getLocType() == 67 || this.inDoorState || this.mLastLocation.getLocType() == 161)) {
            if (this.mLocationListeners != null) {
                Iterator<BDLocationListener> it = this.mLocationListeners.iterator();
                while (it.hasNext()) {
                    it.next().onReceiveLocation(this.mLastLocation);
                }
            }
            if (this.mLastLocation.getLocType() != 66 && this.mLastLocation.getLocType() != 67) {
                this.isWaitingForLocation = false;
                this.lastReceiveLocationTime = System.currentTimeMillis();
            }
        }
    }

    public static BDLocation getBDLocationInCoorType(BDLocation bDLocation, String str) {
        BDLocation bDLocation2 = new BDLocation(bDLocation);
        double[] coorEncrypt = Jni.coorEncrypt(bDLocation.getLongitude(), bDLocation.getLatitude(), str);
        bDLocation2.setLatitude(coorEncrypt[1]);
        bDLocation2.setLongitude(coorEncrypt[0]);
        return bDLocation2;
    }

    /* access modifiers changed from: private */
    public Bundle getOptionBundle() {
        if (this.mOption == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("packName", this.mPackName);
        bundle.putString("prodName", this.mOption.prodName);
        bundle.putString("coorType", this.mOption.coorType);
        bundle.putString("addrType", this.mOption.addrType);
        bundle.putBoolean("openGPS", this.mOption.openGps);
        bundle.putBoolean("location_change_notify", this.mOption.location_change_notify);
        bundle.putInt("scanSpan", this.mOption.scanSpan);
        bundle.putBoolean("enableSimulateGps", this.mOption.enableSimulateGps);
        bundle.putInt("timeOut", this.mOption.timeOut);
        bundle.putInt("priority", this.mOption.priority);
        bundle.putBoolean("map", this.mConfig_map.booleanValue());
        bundle.putBoolean("import", this.mConfig_preimport.booleanValue());
        bundle.putBoolean("needDirect", this.mOption.mIsNeedDeviceDirect);
        bundle.putBoolean("isneedaptag", this.mOption.isNeedAptag);
        bundle.putBoolean("isneedpoiregion", this.mOption.isNeedPoiRegion);
        bundle.putBoolean("isneedregular", this.mOption.isNeedRegular);
        bundle.putBoolean("isneedaptagd", this.mOption.isNeedAptagd);
        bundle.putBoolean("isneedaltitude", this.mOption.isNeedAltitude);
        bundle.putInt("autoNotifyMaxInterval", this.mOption.getAutoNotifyMaxInterval());
        bundle.putInt("autoNotifyMinTimeInterval", this.mOption.getAutoNotifyMinTimeInterval());
        bundle.putInt("autoNotifyMinDistance", this.mOption.getAutoNotifyMinDistance());
        bundle.putFloat("autoNotifyLocSensitivity", this.mOption.getAutoNotifyLocSensitivity());
        return bundle;
    }

    /* access modifiers changed from: private */
    public void onNewLocation(Message message, int i) {
        if (this.mIsStarted) {
            try {
                Bundle data = message.getData();
                data.setClassLoader(BDLocation.class.getClassLoader());
                this.mLastLocation = (BDLocation) data.getParcelable("locStr");
                if (this.mLastLocation.getLocType() == 61) {
                    this.lastReceiveGpsTime = System.currentTimeMillis();
                }
                callListeners(i);
            } catch (Exception e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void onNewNotifyLocation(Message message) {
        try {
            Bundle data = message.getData();
            data.setClassLoader(BDLocation.class.getClassLoader());
            BDLocation bDLocation = (BDLocation) data.getParcelable("locStr");
            if (this.NotifyLocationListenner == null) {
                return;
            }
            if (this.mOption == null || !this.mOption.isDisableCache() || bDLocation.getLocType() != 65) {
                this.NotifyLocationListenner.onReceiveLocation(bDLocation);
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void onRegisterListener(Message message) {
        if (message != null && message.obj != null) {
            BDLocationListener bDLocationListener = (BDLocationListener) message.obj;
            if (this.mLocationListeners == null) {
                this.mLocationListeners = new ArrayList<>();
            }
            if (!this.mLocationListeners.contains(bDLocationListener)) {
                this.mLocationListeners.add(bDLocationListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRegisterNotify(Message message) {
        if (message != null && message.obj != null) {
            BDNotifyListener bDNotifyListener = (BDNotifyListener) message.obj;
            if (this.mNotifyCache == null) {
                this.mNotifyCache = new com.baidu.location.d.a(this.mContext, this);
            }
            this.mNotifyCache.a(bDNotifyListener);
        }
    }

    /* access modifiers changed from: private */
    public void onRegisterNotifyLocationListener(Message message) {
        if (message != null && message.obj != null) {
            this.NotifyLocationListenner = (BDLocationListener) message.obj;
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveNotifyEvent(Message message) {
        if (message != null && message.obj != null) {
            BDNotifyListener bDNotifyListener = (BDNotifyListener) message.obj;
            if (this.mNotifyCache != null) {
                this.mNotifyCache.c(bDNotifyListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRequestLocation() {
        if (this.mServer != null) {
            if ((System.currentTimeMillis() - this.lastReceiveGpsTime > 3000 || !this.mOption.location_change_notify || this.isWaitingLocTag) && (!this.inDoorState || System.currentTimeMillis() - this.lastReceiveLocationTime > 20000 || this.isWaitingLocTag)) {
                Message obtain = Message.obtain((Handler) null, 22);
                if (this.isWaitingLocTag) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isWaitingLocTag", this.isWaitingLocTag);
                    this.isWaitingLocTag = false;
                    obtain.setData(bundle);
                }
                try {
                    obtain.replyTo = this.mMessenger;
                    this.mServer.send(obtain);
                    this.mLastRequestTime = System.currentTimeMillis();
                    this.isWaitingForLocation = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            synchronized (this.mLock) {
                if (this.mOption != null && this.mOption.scanSpan >= 1000 && !this.isScheduled) {
                    if (this.mScheduledRequest == null) {
                        this.mScheduledRequest = new b();
                    }
                    this.mHandler.postDelayed(this.mScheduledRequest, (long) this.mOption.scanSpan);
                    this.isScheduled = true;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRequestNotifyLocation() {
        if (this.mServer != null) {
            Message obtain = Message.obtain((Handler) null, 22);
            try {
                obtain.replyTo = this.mMessenger;
                this.mServer.send(obtain);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRequestOffLineLocation() {
        Message obtain = Message.obtain((Handler) null, 28);
        try {
            obtain.replyTo = this.mMessenger;
            this.mServer.send(obtain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void onSetOption(Message message) {
        this.isWaitingLocTag = false;
        if (message != null && message.obj != null) {
            LocationClientOption locationClientOption = (LocationClientOption) message.obj;
            if (!this.mOption.optionEquals(locationClientOption)) {
                if (this.mOption.scanSpan != locationClientOption.scanSpan) {
                    try {
                        synchronized (this.mLock) {
                            if (this.isScheduled) {
                                this.mHandler.removeCallbacks(this.mScheduledRequest);
                                this.isScheduled = false;
                            }
                            if (locationClientOption.scanSpan >= 1000 && !this.isScheduled) {
                                if (this.mScheduledRequest == null) {
                                    this.mScheduledRequest = new b();
                                }
                                this.mHandler.postDelayed(this.mScheduledRequest, (long) locationClientOption.scanSpan);
                                this.isScheduled = true;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                this.mOption = new LocationClientOption(locationClientOption);
                if (this.mServer != null) {
                    try {
                        Message obtain = Message.obtain((Handler) null, 15);
                        obtain.replyTo = this.mMessenger;
                        obtain.setData(getOptionBundle());
                        this.mServer.send(obtain);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onStart() {
        if (!this.mIsStarted) {
            if (this.firstConnected.booleanValue()) {
                if (this.mloc == null) {
                    this.mloc = new com.baidu.location.a.b(this.mContext, this.mOption, this);
                }
                this.mloc.c();
                this.firstConnected = false;
            }
            this.mPackName = this.mContext.getPackageName();
            this.serviceName = this.mPackName + "_bdls_v2.9";
            Intent intent = new Intent(this.mContext, f.class);
            try {
                intent.putExtra("debug_dev", this.mDebugByDev);
            } catch (Exception e) {
            }
            if (this.mOption == null) {
                this.mOption = new LocationClientOption();
            }
            intent.putExtra("cache_exception", this.mOption.isIgnoreCacheException);
            intent.putExtra("kill_process", this.mOption.isIgnoreKillProcess);
            try {
                this.mContext.bindService(intent, this.mConnection, 1);
            } catch (Exception e2) {
                e2.printStackTrace();
                this.mIsStarted = false;
            }
        }
    }

    /* access modifiers changed from: private */
    public void onStop() {
        if (this.mIsStarted && this.mServer != null) {
            Message obtain = Message.obtain((Handler) null, 12);
            obtain.replyTo = this.mMessenger;
            try {
                this.mServer.send(obtain);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                this.mContext.unbindService(this.mConnection);
            } catch (Exception e2) {
            }
            synchronized (this.mLock) {
                try {
                    if (this.isScheduled) {
                        this.mHandler.removeCallbacks(this.mScheduledRequest);
                        this.isScheduled = false;
                    }
                } catch (Exception e3) {
                }
            }
            if (this.mNotifyCache != null) {
                this.mNotifyCache.a();
            }
            this.mServer = null;
            this.isWaitingLocTag = false;
            this.inDoorState = false;
            this.mIsStarted = false;
            this.clientFirst = false;
            this.serverFirst = false;
        }
    }

    /* access modifiers changed from: private */
    public void onUnRegisterListener(Message message) {
        if (message != null && message.obj != null) {
            BDLocationListener bDLocationListener = (BDLocationListener) message.obj;
            if (this.mLocationListeners != null && this.mLocationListeners.contains(bDLocationListener)) {
                this.mLocationListeners.remove(bDLocationListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendFirstLoc(BDLocation bDLocation) {
        if (!this.isStop) {
            this.mLastLocation = bDLocation;
            if (!this.serverFirst && bDLocation.getLocType() == 161) {
                this.clientFirst = true;
            }
            if (this.mLocationListeners != null) {
                Iterator<BDLocationListener> it = this.mLocationListeners.iterator();
                while (it.hasNext()) {
                    it.next().onReceiveLocation(bDLocation);
                }
            }
        }
    }

    public String getAccessKey() {
        try {
            this.mKey = com.baidu.location.h.a.b(this.mContext);
            if (TextUtils.isEmpty(this.mKey)) {
                throw new IllegalStateException("please setting key from Manifest.xml");
            }
            return String.format("KEY=%s;SHA1=%s", new Object[]{this.mKey, com.baidu.location.h.a.a(this.mContext)});
        } catch (Exception e) {
            return null;
        }
    }

    public BDLocation getLastKnownLocation() {
        return this.mLastLocation;
    }

    public LocationClientOption getLocOption() {
        return this.mOption;
    }

    public String getVersion() {
        return "6.2.3";
    }

    public boolean isStarted() {
        return this.mIsStarted;
    }

    public void onReceiveLocation(BDLocation bDLocation) {
        if ((!this.serverFirst || this.clientFirst) && bDLocation != null) {
            Message obtainMessage = this.mHandler.obtainMessage(701);
            obtainMessage.obj = bDLocation;
            obtainMessage.sendToTarget();
        }
    }

    public void registerLocationListener(BDLocationListener bDLocationListener) {
        if (bDLocationListener == null) {
            throw new IllegalStateException("please set a non-null listener");
        }
        Message obtainMessage = this.mHandler.obtainMessage(5);
        obtainMessage.obj = bDLocationListener;
        obtainMessage.sendToTarget();
    }

    public void registerNotify(BDNotifyListener bDNotifyListener) {
        Message obtainMessage = this.mHandler.obtainMessage(9);
        obtainMessage.obj = bDNotifyListener;
        obtainMessage.sendToTarget();
    }

    public void registerNotifyLocationListener(BDLocationListener bDLocationListener) {
        Message obtainMessage = this.mHandler.obtainMessage(8);
        obtainMessage.obj = bDLocationListener;
        obtainMessage.sendToTarget();
    }

    public void removeNotifyEvent(BDNotifyListener bDNotifyListener) {
        Message obtainMessage = this.mHandler.obtainMessage(10);
        obtainMessage.obj = bDNotifyListener;
        obtainMessage.sendToTarget();
    }

    public int requestLocation() {
        if (this.mServer == null || this.mMessenger == null) {
            return 1;
        }
        if (this.mLocationListeners == null || this.mLocationListeners.size() < 1) {
            return 2;
        }
        if (System.currentTimeMillis() - this.mLastRequestTime < 1000) {
            return 6;
        }
        this.isWaitingLocTag = true;
        Message obtainMessage = this.mHandler.obtainMessage(4);
        obtainMessage.arg1 = 0;
        obtainMessage.sendToTarget();
        return 0;
    }

    public void requestNotifyLocation() {
        this.mHandler.obtainMessage(11).sendToTarget();
    }

    public int requestOfflineLocation() {
        if (this.mServer == null || this.mMessenger == null) {
            return 1;
        }
        if (this.mLocationListeners == null || this.mLocationListeners.size() < 1) {
            return 2;
        }
        this.mHandler.obtainMessage(12).sendToTarget();
        return 0;
    }

    public void setLocOption(LocationClientOption locationClientOption) {
        if (locationClientOption == null) {
            locationClientOption = new LocationClientOption();
        }
        if (locationClientOption.getAutoNotifyMaxInterval() > 0) {
            locationClientOption.setScanSpan(0);
            locationClientOption.setLocationNotify(true);
        }
        Message obtainMessage = this.mHandler.obtainMessage(3);
        obtainMessage.obj = locationClientOption;
        obtainMessage.sendToTarget();
    }

    public void start() {
        this.isStop = false;
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    public void stop() {
        this.isStop = true;
        this.mHandler.obtainMessage(2).sendToTarget();
        this.mloc = null;
    }

    public void unRegisterLocationListener(BDLocationListener bDLocationListener) {
        if (bDLocationListener == null) {
            throw new IllegalStateException("please set a non-null listener");
        }
        Message obtainMessage = this.mHandler.obtainMessage(6);
        obtainMessage.obj = bDLocationListener;
        obtainMessage.sendToTarget();
    }

    public boolean updateLocation(Location location) {
        if (this.mServer == null || this.mMessenger == null || location == null) {
            return false;
        }
        try {
            Message obtain = Message.obtain((Handler) null, 57);
            obtain.obj = location;
            this.mServer.send(obtain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
