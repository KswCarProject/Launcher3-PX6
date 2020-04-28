package com.android.launcher3.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.android.launcher3.Launcher;
import java.util.ArrayList;
import java.util.List;

public class MyScannerAppsTask extends AsyncTask<Void, Void, List<ResolveInfo>> {
    private static final String TAG = "MyScannerAppsTask";
    private ScannerAppsCallback callback;
    @SuppressLint({"StaticFieldLeak"})
    private Context mContext;

    public interface ScannerAppsCallback {
        void ScannedApps(List<ResolveInfo> list);
    }

    public MyScannerAppsTask(Context mContext2, ScannerAppsCallback callback2) {
        this.mContext = mContext2;
        this.callback = callback2;
    }

    /* access modifiers changed from: protected */
    public List<ResolveInfo> doInBackground(Void... voids) {
        Log.i(TAG, "doInBackground: ");
        return filterApps(loadApps());
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(List<ResolveInfo> resolveInfos) {
        super.onPostExecute(resolveInfos);
        if (this.callback != null) {
            this.callback.ScannedApps(resolveInfos);
        }
    }

    private List<ResolveInfo> loadApps() {
        Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        return this.mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    private List<ResolveInfo> filterApps(List<ResolveInfo> resolveInfos) {
        List<ResolveInfo> mApps = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (!"acr.browser.barebones".equals(resolveInfo.activityInfo.packageName) && !"android.rk.RockVideoPlayer".equals(resolveInfo.activityInfo.packageName) && !"com.android.calendar".equals(resolveInfo.activityInfo.packageName) && !"com.android.camera2".equals(resolveInfo.activityInfo.packageName) && !"com.android.contacts".equals(resolveInfo.activityInfo.packageName) && !"com.android.deskclock".equals(resolveInfo.activityInfo.packageName) && !"com.android.email".equals(resolveInfo.activityInfo.packageName) && !"com.android.music".equals(resolveInfo.activityInfo.packageName) && !"com.android.settings".equals(resolveInfo.activityInfo.packageName) && !"com.android.soundrecorder".equals(resolveInfo.activityInfo.packageName) && !"com.alensw.PicFolder".equals(resolveInfo.activityInfo.packageName) && !"com.android.apkinstaller".equals(resolveInfo.activityInfo.packageName) && !"com.android.calculator2".equals(resolveInfo.activityInfo.packageName) && !"com.android.documentsui".equals(resolveInfo.activityInfo.packageName) && !"com.android.quicksearchbox".equals(resolveInfo.activityInfo.packageName) && !"com.android.rk".equals(resolveInfo.activityInfo.packageName) && !"com.android.gallery3d".equals(resolveInfo.activityInfo.packageName) && !"com.szchoiceway.ksw_old_bmw_x1_original".equals(resolveInfo.activityInfo.packageName) && !"com.szchoiceway.fatset".equals(resolveInfo.activityInfo.packageName)) {
                if (((Launcher) this.mContext).m_iUITypeVer != 41 ? ((Launcher) this.mContext).m_iUITypeVer != 101 || (!"com.szchoiceway.Jeep.CherokeeCDActivity".equals(resolveInfo.activityInfo.name) && !"com.szchoiceway.Mazida.BNRMazidaCDActivity".equals(resolveInfo.activityInfo.name) && !"com.szchoiceway.Mazida.Mazida6CDActivity".equals(resolveInfo.activityInfo.name) && !"com.szchoiceway.GeelyBorui.ParkingImageActivity".equals(resolveInfo.activityInfo.name) && !"com.szchoiceway.VScanbus.QiChenT90CDActivity".equals(resolveInfo.activityInfo.name) && !"com.szchoiceway.Zoyte_BNR.ZoyteT600PMActivity".equals(resolveInfo.activityInfo.name) && !"com.szchoiceway.Peugeot.Peugeot408AirSetActivity".equals(resolveInfo.activityInfo.name)) : !"com.szchoiceway.btsuite.BTMusicActivity".equals(resolveInfo.activityInfo.name) && ((!"com.android.vending".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bGoogleApps) && ((!"com.google.android.gms".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bGoogleApps) && ((!"com.google.android.apps.maps".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bGoogleApps) && ((!"com.google.android.gm".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bGoogleApps) && ((!"com.szchoiceway.ksw_aux".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bHaveAux) && ((!"com.szchoiceway.ksw_cmmb".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bHaveTv) && ((!"com.szchoiceway.ksw_dashboard".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).bSupportDashBoard) && ((!"com.szchoiceway.ksw_dvd".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).iHaveDvdType == 1) && ((!"com.szchoiceway.dvdplayer".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).iHaveDvdType == 2) && (!"com.szchoiceway.ksw_dvr".equals(resolveInfo.activityInfo.packageName) || ((Launcher) this.mContext).iHaveDvrType == 1))))))))))) {
                    mApps.add(resolveInfo);
                }
            }
        }
        return mApps;
    }
}
