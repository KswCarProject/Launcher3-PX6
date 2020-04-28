package com.android.launcher3;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.szchoiceway.eventcenter.IEventService;

public class LauncherApplication extends Application {
    private static final String TAG = "LauncherApplication";
    public ServiceConnection mEvtSc = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            IEventService unused = LauncherApplication.this.mEvtService = IEventService.Stub.asInterface(service);
            Log.i(LauncherApplication.TAG, "onServiceConnected: mEvtService = " + LauncherApplication.this.mEvtService);
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i(LauncherApplication.TAG, "onServiceDisconnected: ");
            IEventService unused = LauncherApplication.this.mEvtService = null;
        }
    };
    /* access modifiers changed from: private */
    public IEventService mEvtService = null;
    private SysProviderOpt mSysProviderOpt;
    private int m_iUITypeVer = 41;

    public IEventService getEvtService() {
        return this.mEvtService;
    }

    public SysProviderOpt getSysProviderOpt() {
        return this.mSysProviderOpt;
    }

    public int getUITypeVer() {
        return this.m_iUITypeVer;
    }

    public void setUITypeVer(int m_iUITypeVer2) {
        this.m_iUITypeVer = m_iUITypeVer2;
        SysProviderOpt sysProviderOpt = this.mSysProviderOpt;
        sysProviderOpt.updateRecord(SysProviderOpt.SET_USER_UI_TYPE, m_iUITypeVer2 + "");
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        bindService(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"), this.mEvtSc, 1);
        this.mSysProviderOpt = new SysProviderOpt(this);
        this.m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, this.m_iUITypeVer);
    }

    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate: ");
        unbindService(this.mEvtSc);
    }
}
