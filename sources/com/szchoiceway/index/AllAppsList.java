package com.szchoiceway.index;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class AllAppsList {
    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;
    static final String TAG = "AllAppsList class";
    public ArrayList<ApplicationInfo> added = new ArrayList<>(42);
    private boolean bXingshuoForwardView = false;
    private boolean bXingshuoRightView = false;
    public ArrayList<ApplicationInfo> data = new ArrayList<>(42);
    private Context mContext = null;
    private IconCache mIconCache;
    private SysProviderOpt mSysProviderOpt = null;
    private boolean m_b_DVR_Xingshuo = false;
    private boolean m_b_canbusinfo_Xingshuo = false;
    private boolean m_b_googleplay_Maisiluo = false;
    private boolean m_b_googleplay_Xingshuo = false;
    private boolean m_b_have_AUX = false;
    private int m_b_have_DVD = 0;
    private boolean m_b_have_TV = false;
    private boolean m_b_ksw_FM_launch = false;
    private boolean m_b_ksw_Support_dashboard = true;
    private boolean m_b_voice_switch_KeSaiWei = false;
    private boolean m_b_voice_switch_Xingshuo = false;
    private int m_iCanbustype = 0;
    private int m_iCarCanbusName_ID = 0;
    private int m_iCarstype_ID = 0;
    private int m_iHaveCMMB = 1;
    private int m_iModeSet = 0;
    private int m_iUITypeVer = 0;
    private int m_iUSBDvr = 1;
    private int m_i_have_DVR_index = 0;
    private int m_i_ksw_evo_main_interface_index = 0;
    public ArrayList<ApplicationInfo> modified = new ArrayList<>();
    public ArrayList<ApplicationInfo> removed = new ArrayList<>();
    private final String xml_client;

    public AllAppsList(IconCache iconCache, Context context) {
        this.mIconCache = iconCache;
        this.mContext = context;
        this.mSysProviderOpt = ((LauncherApplication) context.getApplicationContext()).getProvider();
        this.m_iCanbustype = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_Canbustype_KEY, this.m_iCanbustype);
        this.m_iCarstype_ID = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_Carstype_ID_KEY, this.m_iCarstype_ID);
        this.m_iCarCanbusName_ID = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_CarCanbusName_ID_KEY, this.m_iCarCanbusName_ID);
        Log.i(TAG, "m_iCanbustype  = " + this.m_iCanbustype);
        Log.i(TAG, "m_iCarstype_ID  = " + this.m_iCarstype_ID);
        Log.i(TAG, "m_iCarCanbusName_ID  = " + this.m_iCarCanbusName_ID);
        this.m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, this.m_iUITypeVer);
        this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
        this.m_iUSBDvr = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USB_DVR_MODE, this.m_iUSBDvr);
        this.m_iHaveCMMB = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SYS_CMMB_ONOFF_KEY, this.m_iHaveCMMB);
        this.m_b_googleplay_Maisiluo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.MAISILUO_SYS_GOOGLEPLAY, this.m_b_googleplay_Maisiluo);
        this.bXingshuoForwardView = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KEY_XINGSHUO_FORWARD_VIEW, this.bXingshuoForwardView);
        this.bXingshuoRightView = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KEY_XINGSHUO_RIGHT_VIEW, this.bXingshuoRightView);
        this.m_b_googleplay_Xingshuo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.XINGSHUO_SYS_GOOGLEPLAY, this.m_b_googleplay_Xingshuo);
        this.m_b_canbusinfo_Xingshuo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.XINGSHUO_SYS_CANBUSINFO, this.m_b_canbusinfo_Xingshuo);
        this.m_b_DVR_Xingshuo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.XINGSHUO_SYS_DVR, this.m_b_DVR_Xingshuo);
        this.m_b_voice_switch_Xingshuo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.XINGSHUO_SYS_VOICE_SWITCH, this.m_b_voice_switch_Xingshuo);
        this.m_b_have_AUX = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_HAVE_AUX, this.m_b_have_AUX);
        this.m_b_have_DVD = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_HAVE_DVD, this.m_b_have_DVD);
        this.m_b_have_TV = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_HAVE_TV, this.m_b_have_TV);
        this.m_i_have_DVR_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_RECORD_DVR, this.m_i_have_DVR_index);
        this.m_b_voice_switch_KeSaiWei = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_CHK_VOICE_SWITCH, this.m_b_voice_switch_KeSaiWei);
        this.m_b_ksw_Support_dashboard = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_SUPPORT_DASHBOARD, this.m_b_ksw_Support_dashboard);
        this.m_b_ksw_FM_launch = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_FM_LAUNCH, this.m_b_ksw_FM_launch);
        this.m_i_ksw_evo_main_interface_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_EVO_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_main_interface_index);
        this.xml_client = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_FACTORY_SET_CLIENT);
    }

    public void add(ApplicationInfo info) {
        if (!findActivity(this.data, info.componentName) && !info.componentName.getPackageName().startsWith(BuildConfig.APPLICATION_ID)) {
            if (this.mContext != null) {
                try {
                    int appFlags = this.mContext.getPackageManager().getApplicationInfo(info.componentName.getPackageName(), 0).flags;
                    if (this.m_iUITypeVer == 41 && info.componentName.getPackageName().startsWith("com.szchoiceway.appwidget")) {
                        return;
                    }
                    if ((this.m_iUITypeVer != 41 || (!info.componentName.getClassName().startsWith(EventUtils.BTMUSIC_MODE_CLASS_NAME) && !info.componentName.getClassName().startsWith("com.didi365.miudrive.navi.MainActivity"))) && !info.componentName.getPackageName().startsWith("com.szchoiceway.standbyunlock") && !info.componentName.getPackageName().startsWith("com.szchoiceway.fatset") && !info.componentName.getPackageName().startsWith(EventUtils.KSW_OLD_BMW_X1_ORIGINAL_PACKAGE)) {
                        if (this.m_iCanbustype == 9 && this.m_iCarstype_ID == 7 && this.m_iCarCanbusName_ID == 0) {
                            if (info.componentName.getPackageName().startsWith(EventUtils.CANBUS_MODE_PACKAGE_NAME)) {
                                return;
                            }
                        } else if (this.m_iCanbustype == 0 && this.m_iCarstype_ID == 0 && this.m_iCarCanbusName_ID == 0) {
                            if (info.componentName.getPackageName().startsWith(EventUtils.CANBUS_MODE_PACKAGE_NAME)) {
                                return;
                            }
                            if (info.componentName.getPackageName().startsWith("com.szchoiceway.FocusCanbus")) {
                                return;
                            }
                        } else if ((this.m_iCanbustype == 55 && this.m_iCarstype_ID == 20 && this.m_iCarCanbusName_ID == 0) || (this.m_iCanbustype == 56 && this.m_iCarstype_ID == 21 && this.m_iCarCanbusName_ID == 0)) {
                            if (info.componentName.getPackageName().startsWith(EventUtils.CANBUS_MODE_PACKAGE_NAME)) {
                                return;
                            }
                        } else if (info.componentName.getPackageName().startsWith("com.szchoiceway.FocusCanbus")) {
                            return;
                        } else {
                            if (this.m_iCanbustype == 8) {
                                if (info.componentName.getClassName().startsWith("com.szchoiceway.Jeep.CherokeeCDActivity")) {
                                    return;
                                }
                            } else if (this.m_iCanbustype != 47) {
                                if (info.componentName.getClassName().startsWith("com.szchoiceway.Peugeot.Peugeot408AirSetActivity")) {
                                    return;
                                }
                                if (info.componentName.getClassName().startsWith("com.szchoiceway.Jeep.CherokeeCDActivity")) {
                                    return;
                                }
                            }
                        }
                        if (this.m_iUSBDvr == 0) {
                            if (info.componentName.getPackageName().startsWith(EventUtils.USB_DVR_MODE_PACKAGE_NAME) || info.componentName.getPackageName().startsWith("com.sinosmart")) {
                                return;
                            }
                        } else if (this.m_iUSBDvr == 1) {
                            if (info.componentName.getPackageName().startsWith(EventUtils.DVR_MODE_PACKAGE_NAME)) {
                                return;
                            }
                            if (info.componentName.getPackageName().startsWith("com.sinosmart")) {
                                return;
                            }
                        } else if (info.componentName.getPackageName().startsWith(" com.szchoiceway.usbdvrplayer")) {
                            return;
                        } else {
                            if (info.componentName.getPackageName().startsWith(EventUtils.DVR_MODE_PACKAGE_NAME)) {
                                return;
                            }
                        }
                        if (this.m_iUITypeVer == 41) {
                            if (!this.m_b_have_AUX && info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_aux")) {
                                return;
                            }
                            if (this.m_b_have_DVD != 1 && info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_dvd")) {
                                return;
                            }
                            if (this.m_b_have_DVD != 2 && info.componentName.getPackageName().startsWith("com.szchoiceway.dvdplayer")) {
                                return;
                            }
                            if (!this.m_b_have_TV && info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_cmmb")) {
                                return;
                            }
                            if (this.m_i_have_DVR_index == 1 || !info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_dvr")) {
                                Log.i(TAG, "--->>> ksw com.anwensoft.cardvr 000 m_i_have_DVR_index = " + this.m_i_have_DVR_index);
                                if (this.m_i_have_DVR_index != 2) {
                                    Log.i(TAG, "--->>> ksw com.anwensoft.cardvr 111 m_i_have_DVR_index = " + this.m_i_have_DVR_index);
                                    if (info.componentName.getPackageName().startsWith("com.anwensoft.cardvr") || info.componentName.getPackageName().startsWith("com.ankai.cardvr")) {
                                        Log.i(TAG, "--->>> ksw com.anwensoft.cardvr 222 m_i_have_DVR_index = " + this.m_i_have_DVR_index);
                                        return;
                                    }
                                }
                                if (this.m_b_voice_switch_KeSaiWei || (!info.componentName.getPackageName().startsWith("com.txznet.webchat") && !info.componentName.getPackageName().startsWith("com.txznet.music"))) {
                                    if (this.m_b_googleplay_Maisiluo) {
                                        if (info.componentName.getPackageName().startsWith("com.hiapk.marketpho")) {
                                            return;
                                        }
                                    } else if (info.componentName.getPackageName().startsWith("com.google.android.googlequicksearchbox")) {
                                        return;
                                    }
                                    if ((this.m_iModeSet != 8 && this.m_iModeSet != 9) || !info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_dashboard")) {
                                        if (this.m_b_ksw_Support_dashboard || !info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_dashboard")) {
                                            if (this.m_b_ksw_FM_launch || !info.componentName.getPackageName().startsWith("com.szchoiceway.ksw_fm")) {
                                                if (this.m_i_ksw_evo_main_interface_index == 5 && info.componentName.getPackageName().startsWith(EventUtils.PHONEAPP_MODE_PACKAGE_NAME)) {
                                                    return;
                                                }
                                                if ("XinCheng".equalsIgnoreCase(this.xml_client) && info.componentName.getPackageName().startsWith("com.szchoiceway.ambientlight_ksw")) {
                                                    return;
                                                }
                                            } else {
                                                return;
                                            }
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                        if (this.m_iUITypeVer == 101) {
                            if (!info.componentName.getClassName().startsWith("com.szchoiceway.Mazida.BNRMazidaCDActivity") && !info.componentName.getClassName().startsWith("com.szchoiceway.Mazida.Mazida6CDActivity") && !info.componentName.getClassName().contains("QiChenT90CDActivity") && !info.componentName.getClassName().contains("ZoyteT600PMActivity") && !info.componentName.getClassName().contains("ParkingImageActivity")) {
                                if (info.componentName.getClassName().contains("CanMainActivity")) {
                                }
                            } else {
                                return;
                            }
                        }
                        if (info.componentName.getPackageName().startsWith("com.android.rk")) {
                            return;
                        }
                        if (this.m_iUITypeVer != 41 || !this.m_b_googleplay_Maisiluo) {
                            if ((appFlags & 1) != 0 && !info.componentName.getPackageName().startsWith("com.szchoiceway") && !info.componentName.getPackageName().startsWith("com.glsx") && !info.componentName.getPackageName().startsWith("com.android.rk") && !info.componentName.getPackageName().startsWith(EventUtils.EXPLORER_MODE_PACKAGE_NAME) && !info.componentName.getPackageName().startsWith(EventUtils.EXPLORER_MODE_PACKAGE_NAME2) && !info.componentName.getPackageName().startsWith("com.estrongs.android.pop") && !info.componentName.getPackageName().startsWith(EventUtils.PHONEAPP_MODE_PACKAGE_NAME) && !info.componentName.getPackageName().startsWith("com.adobe.reader") && !info.componentName.getPackageName().startsWith("com.togic.livevideo") && !info.componentName.getPackageName().startsWith("com.sinosmart") && !info.componentName.getPackageName().startsWith("com.example.administrator") && !info.componentName.getPackageName().startsWith("com.coagent.ecar") && !info.componentName.getPackageName().startsWith("com.coagent.voip") && !info.componentName.getPackageName().startsWith("cn.kuwo.kwmusiccar") && !info.componentName.getPackageName().startsWith("com.tencent.mtt.x86") && !info.componentName.getPackageName().startsWith("com.aispeech.aios") && !info.componentName.getPackageName().startsWith("com.AnywheeBt") && !info.componentName.getPackageName().startsWith("com.chartcross.gpstest") && !info.componentName.getPackageName().startsWith("com.anwensoft.cardvr") && !info.componentName.getPackageName().startsWith("com.ankai.cardvr") && !info.componentName.getPackageName().startsWith("com.txznet.webchat") && !info.componentName.getPackageName().startsWith("com.txznet.music")) {
                                return;
                            }
                        } else if ((appFlags & 1) != 0 && !info.componentName.getPackageName().startsWith("com.szchoiceway") && !info.componentName.getPackageName().startsWith("com.glsx") && !info.componentName.getPackageName().startsWith("com.android.rk") && !info.componentName.getPackageName().startsWith(EventUtils.EXPLORER_MODE_PACKAGE_NAME) && !info.componentName.getPackageName().startsWith(EventUtils.EXPLORER_MODE_PACKAGE_NAME2) && !info.componentName.getPackageName().startsWith("com.estrongs.android.pop") && !info.componentName.getPackageName().startsWith(EventUtils.PHONEAPP_MODE_PACKAGE_NAME) && !info.componentName.getPackageName().startsWith("com.google") && !info.componentName.getPackageName().startsWith("com.android.vending") && !info.componentName.getPackageName().startsWith("com.adobe.reader") && !info.componentName.getPackageName().startsWith("com.togic.livevideo") && !info.componentName.getPackageName().startsWith("com.sinosmart") && !info.componentName.getPackageName().startsWith("com.example.administrator") && !info.componentName.getPackageName().startsWith("com.coagent.ecar") && !info.componentName.getPackageName().startsWith("com.coagent.voip") && !info.componentName.getPackageName().startsWith("cn.kuwo.kwmusiccar") && !info.componentName.getPackageName().startsWith("com.tencent.mtt.x86") && !info.componentName.getPackageName().startsWith("com.aispeech.aios") && !info.componentName.getPackageName().startsWith("com.AnywheeBt") && !info.componentName.getPackageName().startsWith("com.chartcross.gpstest") && !info.componentName.getPackageName().startsWith("com.anwensoft.cardvr") && !info.componentName.getPackageName().startsWith("com.ankai.cardvr") && !info.componentName.getPackageName().startsWith("com.txznet.webchat") && !info.componentName.getPackageName().startsWith("com.txznet.music")) {
                            Log.i("ADD", "**return**" + info.componentName.getPackageName());
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.data.add(info);
            this.added.add(info);
        }
    }

    public void clear() {
        this.data.clear();
        this.added.clear();
        this.removed.clear();
        this.modified.clear();
    }

    public int size() {
        return this.data.size();
    }

    public ApplicationInfo get(int index) {
        return this.data.get(index);
    }

    public void addPackage(Context context, String packageName) {
        List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        if (matches.size() > 0) {
            for (ResolveInfo info : matches) {
                add(new ApplicationInfo(context.getPackageManager(), info, this.mIconCache, (HashMap<Object, CharSequence>) null));
            }
        }
    }

    public void removePackage(String packageName) {
        List<ApplicationInfo> data2 = this.data;
        for (int i = data2.size() - 1; i >= 0; i--) {
            ApplicationInfo info = data2.get(i);
            if (packageName.equals(info.intent.getComponent().getPackageName())) {
                this.removed.add(info);
                data2.remove(i);
            }
        }
        this.mIconCache.flush();
    }

    public void updatePackage(Context context, String packageName) {
        List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        if (matches.size() > 0) {
            for (int i = this.data.size() - 1; i >= 0; i--) {
                ApplicationInfo applicationInfo = this.data.get(i);
                ComponentName component = applicationInfo.intent.getComponent();
                if (packageName.equals(component.getPackageName()) && !findActivity(matches, component)) {
                    this.removed.add(applicationInfo);
                    this.mIconCache.remove(component);
                    this.data.remove(i);
                }
            }
            int count = matches.size();
            for (int i2 = 0; i2 < count; i2++) {
                ResolveInfo info = matches.get(i2);
                ApplicationInfo applicationInfo2 = findApplicationInfoLocked(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
                if (applicationInfo2 == null) {
                    add(new ApplicationInfo(context.getPackageManager(), info, this.mIconCache, (HashMap<Object, CharSequence>) null));
                } else {
                    this.mIconCache.remove(applicationInfo2.componentName);
                    this.mIconCache.getTitleAndIcon(applicationInfo2, info, (HashMap<Object, CharSequence>) null);
                    this.modified.add(applicationInfo2);
                }
            }
            return;
        }
        for (int i3 = this.data.size() - 1; i3 >= 0; i3--) {
            ApplicationInfo applicationInfo3 = this.data.get(i3);
            ComponentName component2 = applicationInfo3.intent.getComponent();
            if (packageName.equals(component2.getPackageName())) {
                this.removed.add(applicationInfo3);
                this.mIconCache.remove(component2);
                this.data.remove(i3);
            }
        }
    }

    private static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        mainIntent.setPackage(packageName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<>();
    }

    private static boolean findActivity(List<ResolveInfo> apps, ComponentName component) {
        String className = component.getClassName();
        for (ResolveInfo info : apps) {
            if (info.activityInfo.name.equals(className)) {
                return true;
            }
        }
        return false;
    }

    private static boolean findActivity(ArrayList<ApplicationInfo> apps, ComponentName component) {
        int N = apps.size();
        for (int i = 0; i < N; i++) {
            if (apps.get(i).componentName.equals(component)) {
                return true;
            }
        }
        return false;
    }

    private ApplicationInfo findApplicationInfoLocked(String packageName, String className) {
        Iterator<ApplicationInfo> it = this.data.iterator();
        while (it.hasNext()) {
            ApplicationInfo info = it.next();
            ComponentName component = info.intent.getComponent();
            if (packageName.equals(component.getPackageName()) && className.equals(component.getClassName())) {
                return info;
            }
        }
        return null;
    }
}
