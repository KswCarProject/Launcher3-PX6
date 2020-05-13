package com.szchoiceway.index;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.HashMap;

public class IconCache {
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
    private static final String TAG = "Index.IconCache";
    public static boolean ksw_m_b_easyconn = false;
    private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<>(50);
    private final LauncherApplication mContext;
    private final Bitmap mDefaultIcon;
    private int mIconDpi;
    private final PackageManager mPackageManager;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;

        private CacheEntry() {
        }
    }

    public IconCache(LauncherApplication context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mIconDpi = ((ActivityManager) context.getSystemService("activity")).getLauncherLargeIconDensity();
        this.mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), 17629184);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, this.mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }
        return d != null ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = this.mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources == null || iconId == 0) {
            return getFullResDefaultActivityIcon();
        }
        return getFullResIcon(resources, iconId);
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        int iconId;
        try {
            resources = this.mPackageManager.getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources == null || (iconId = info.getIconResource()) == 0) {
            return getFullResDefaultActivityIcon();
        }
        return getFullResIcon(resources, iconId);
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1), Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap((Bitmap) null);
        return b;
    }

    public void remove(ComponentName componentName) {
        synchronized (this.mCache) {
            this.mCache.remove(componentName);
        }
    }

    public void flush() {
        synchronized (this.mCache) {
            this.mCache.clear();
        }
    }

    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
        synchronized (this.mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info, labelCache);
            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        Bitmap bitmap;
        synchronized (this.mCache) {
            ResolveInfo resolveInfo = this.mPackageManager.resolveActivity(intent, 0);
            ComponentName component = intent.getComponent();
            if (resolveInfo == null || component == null) {
                bitmap = this.mDefaultIcon;
            } else {
                bitmap = cacheLocked(component, resolveInfo, (HashMap<Object, CharSequence>) null).icon;
            }
        }
        return bitmap;
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo, HashMap<Object, CharSequence> labelCache) {
        Bitmap bitmap;
        synchronized (this.mCache) {
            if (resolveInfo == null || component == null) {
                bitmap = null;
            } else {
                bitmap = cacheLocked(component, resolveInfo, labelCache).icon;
            }
        }
        return bitmap;
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return this.mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = this.mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();
            this.mCache.put(componentName, entry);
            ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
            if (labelCache == null || !labelCache.containsKey(key)) {
                entry.title = info.loadLabel(this.mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            } else {
                entry.title = labelCache.get(key).toString();
            }
            Log.i(TAG, "cacheLocked: entry.title = " + entry.title);
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }
            Utilities.SetUIType(this.mContext.m_iSetUIType, this.mContext);
            LauncherApplication launcherApplication = this.mContext;
            Utilities.SetUITypeVer(LauncherApplication.m_iUITypeVer, this.mContext);
            Log.i("bitmap", "packageName = " + info.activityInfo.packageName);
            LauncherApplication launcherApplication2 = this.mContext;
            if (LauncherApplication.m_iUITypeVer != 41 || (LauncherApplication.get_m_iModeSet() != 3 && LauncherApplication.get_m_iModeSet() != 5 && LauncherApplication.get_m_iModeSet() != 6)) {
                ksw_m_b_easyconn = false;
            } else if (info.activityInfo.packageName.startsWith(EventUtils.PHONEAPP_MODE_PACKAGE_NAME)) {
                ksw_m_b_easyconn = true;
            } else {
                ksw_m_b_easyconn = false;
            }
            if (info.activityInfo.packageName.startsWith("com.szchoiceway")) {
                if (info.activityInfo.packageName.startsWith(EventUtils.RADIO_MODE_PACKAGE_NAME)) {
                    int iconId = R.drawable.fm_n_keshang_ui_800x480_2;
                    LauncherApplication launcherApplication3 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        iconId = R.drawable.fm_n_keshang_ui_800x480_2;
                    } else {
                        LauncherApplication launcherApplication4 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId = R.drawable.normal_1920x720_icon_fm;
                        }
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith("com.szchoiceway.auxplayer") || info.activityInfo.packageName.startsWith("com.szchoiceway.ksw_aux")) {
                    int iconId2 = R.drawable.avin_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication5 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication6 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId2 = R.drawable.normal_1920x720_icon_aux;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        LauncherApplication launcherApplication7 = this.mContext;
                        if (LauncherApplication.m_iModeSet == 14) {
                            iconId2 = R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_aux_n;
                        } else {
                            iconId2 = R.drawable.avin_n_keshang_ui_800x480;
                        }
                    } else {
                        iconId2 = R.drawable.kesaiwei_1280x480_als_apps_aux_;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId2), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith("com.szchoiceway.dvdplayer") || info.activityInfo.packageName.startsWith("com.szchoiceway.ksw_dvd")) {
                    int iconId3 = R.drawable.dvd_n_800x480;
                    LauncherApplication launcherApplication8 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication9 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId3 = R.drawable.normal_1920x720_icon_dvd;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        iconId3 = R.drawable.dvd_n_800x480;
                    } else {
                        iconId3 = R.drawable.kesaiwei_1280x480_als_apps_dvd;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId3), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith(EventUtils.BT_MODE_PACKAGE_NAME)) {
                    int iconId4 = R.drawable.bt_n_keshang_ui_800x480;
                    if (info.activityInfo.name.startsWith(EventUtils.BT_MODE_CLASS_NAME)) {
                        LauncherApplication launcherApplication10 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer != 41) {
                            LauncherApplication launcherApplication11 = this.mContext;
                            if (LauncherApplication.m_iUITypeVer == 101) {
                                iconId4 = R.drawable.normal_1920x720_icon_bt;
                            }
                        } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                            LauncherApplication launcherApplication12 = this.mContext;
                            if (LauncherApplication.m_iModeSet == 14) {
                                iconId4 = R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_lanya_n;
                            } else {
                                iconId4 = R.drawable.bt_n_keshang_ui_800x480;
                            }
                        } else {
                            iconId4 = R.drawable.kesaiwei_1280x480_als_apps_lanya;
                        }
                        entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId4), this.mContext, true, false);
                    } else if (info.activityInfo.name.startsWith(EventUtils.BTMUSIC_MODE_CLASS_NAME)) {
                        LauncherApplication launcherApplication13 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            iconId4 = R.drawable.btmusic_n_keshang_ui_800x480;
                        } else {
                            LauncherApplication launcherApplication14 = this.mContext;
                            if (LauncherApplication.m_iUITypeVer == 101) {
                                iconId4 = R.drawable.normal_1920x720_icon_bt_music;
                            }
                        }
                        entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId4), this.mContext, true, false);
                    }
                } else if (info.activityInfo.packageName.startsWith(EventUtils.DVR_MODE_PACKAGE_NAME) || info.activityInfo.packageName.startsWith("com.szchoiceway.ksw_dvr")) {
                    int iconId5 = R.drawable.xingchejilu_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication15 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication16 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId5 = R.drawable.normal_1920x720_icon_dvr;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        iconId5 = R.drawable.xingchejilu_n_keshang_ui_800x480;
                    } else {
                        iconId5 = R.drawable.kesaiwei_1280x480_als_apps_dvr;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId5), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith(EventUtils.MUSIC_MODE_PACKAGE_NAME)) {
                    int iconId6 = R.drawable.yinyue_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication17 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                            LauncherApplication launcherApplication18 = this.mContext;
                            if (LauncherApplication.get_m_iModeSet() == 14) {
                                iconId6 = R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_yinyue_n;
                            } else {
                                iconId6 = R.drawable.yinyue_n_keshang_ui_800x480;
                            }
                        } else {
                            iconId6 = R.drawable.kesaiwei_1280x480_als_apps_yinyue;
                        }
                    }
                    LauncherApplication launcherApplication19 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer == 101) {
                        iconId6 = R.drawable.normal_1920x720_icon_music;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId6), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith(EventUtils.MOVIE_MODE_PACKAGE_NAME)) {
                    int iconId7 = R.drawable.shipin_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication20 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication21 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId7 = R.drawable.normal_1920x720_icon_voide;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        LauncherApplication launcherApplication22 = this.mContext;
                        if (LauncherApplication.get_m_iModeSet() == 14) {
                            iconId7 = R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_shipin_n;
                        } else {
                            iconId7 = R.drawable.shipin_n_keshang_ui_800x480;
                        }
                    } else {
                        iconId7 = R.drawable.kesaiwei_1280x480_als_apps_shipin;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId7), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith(EventUtils.SET_MODE_PACKAGE_NAME)) {
                    int iconId8 = R.drawable.set_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication23 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication24 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId8 = R.drawable.normal_1920x720_icon_setting;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        LauncherApplication launcherApplication25 = this.mContext;
                        if (LauncherApplication.get_m_iModeSet() == 14) {
                            iconId8 = R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_shezhi_n;
                        } else {
                            iconId8 = R.drawable.set_n_keshang_ui_800x480;
                        }
                    } else {
                        iconId8 = R.drawable.kesaiwei_1280x480_als_apps_shezhi;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId8), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith("com.szchoiceway.navigation")) {
                    int iconId9 = R.drawable.daohang_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication26 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication27 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId9 = R.drawable.normal_1920x720_icon_navi;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        LauncherApplication launcherApplication28 = this.mContext;
                        if (LauncherApplication.get_m_iModeSet() == 14) {
                            iconId9 = R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_daohang_n;
                        } else {
                            iconId9 = R.drawable.daohang_n_keshang_ui_800x480;
                        }
                    } else {
                        iconId9 = R.drawable.kesaiwei_1280x480_als_apps_daohang;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId9), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith(EventUtils.TV_MODE_PACKAGE_NAME) || info.activityInfo.packageName.startsWith("com.szchoiceway.ksw_cmmb")) {
                    int iconId10 = R.drawable.tv_n_keshang_ui_800x480;
                    LauncherApplication launcherApplication29 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                            iconId10 = R.drawable.tv_n_keshang_ui_800x480;
                        } else {
                            iconId10 = R.drawable.kesaiwei_1280x480_als_apps_shuzidianshi;
                        }
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId10), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith(EventUtils.CANBUS_MODE_PACKAGE_NAME) || info.activityInfo.packageName.startsWith("com.szchoiceway.FocusCanbus")) {
                    int iconId11 = R.drawable.canbus_keshang_ui_800x480;
                    LauncherApplication launcherApplication30 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        iconId11 = R.drawable.canbus_keshang_ui_800x480;
                    } else if (this.mContext.m_iSetUIType == 101) {
                        iconId11 = R.drawable.icon_cheliangxinxi_n;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId11), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith("com.szchoiceway.ksw_dashboard")) {
                    int iconId12 = R.drawable.kesaiwei_1280x480_apps_yibiaopan;
                    LauncherApplication launcherApplication31 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        LauncherApplication launcherApplication32 = this.mContext;
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            iconId12 = R.drawable.icon_yibiaopan;
                        }
                    } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                        LauncherApplication launcherApplication33 = this.mContext;
                        if (LauncherApplication.m_iModeSet == 13) {
                            iconId12 = R.drawable.icon_yibiaopan;
                        }
                    } else {
                        iconId12 = R.drawable.kesaiwei_1280x480_als_apps_yibiaopan;
                    }
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId12), this.mContext, true, false);
                } else if (info.activityInfo.packageName.startsWith("com.szchoiceway.ambientlight_ksw")) {
                    LauncherApplication launcherApplication34 = this.mContext;
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                            entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_led_n), this.mContext, true, false);
                        } else {
                            entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_led_n), this.mContext, false, false);
                        }
                    }
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.autonavi.amapauto")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_auto_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith(EventUtils.PHONEAPP_MODE_PACKAGE_NAME)) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_shoujihulian_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("cn.kuwo.kwmusiccar")) {
                entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.ic_kuwo_music), this.mContext, false, false);
            } else if (info.activityInfo.packageName.startsWith("com.estrongs.android.pop")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), (Context) this.mContext);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_esfile_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith(EventUtils.EXPLORER_MODE_PACKAGE_NAME2)) {
                entry.icon = Utilities.createIconBitmap(getFullResIcon(info), (Context) this.mContext);
            } else if (info.activityInfo.packageName.startsWith("com.google.android.youtube")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), (Context) this.mContext);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_youtube_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.skysoft.kkbox.android")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), (Context) this.mContext);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_kkbox_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.kingwaytek.naviking3d")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_naviking3d_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("mbinc12.mb32b")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), (Context) this.mContext);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_mixerbox_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.android.vending")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_android_vending_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.google.android.googlequicksearchbox")) {
                int iconId13 = R.drawable.inco_google_n;
                if ("com.google.android.googlequicksearchbox.SearchActivity".equals(info.activityInfo.name)) {
                    iconId13 = R.drawable.inco_google_n;
                } else if ("com.google.android.googlequicksearchbox.VoiceSearchActivity".equals(info.activityInfo.name)) {
                    iconId13 = R.drawable.inco_google_voice_n;
                }
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), iconId13), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.google.android.apps.maps")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_google_map), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.qiyi.video.pad")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_qiyi), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("com.papago.s1OBU")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_papago_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("cn.manstep.phonemirrorBox")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_autopaly_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("tw.chaozhuyin.paid")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_chaozhuyin_n), this.mContext, true, false);
                }
            } else if (info.activityInfo.packageName.startsWith("tv.fourgtv.fourgtv")) {
                if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
                } else {
                    entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_fourgtv_n), this.mContext, true, false);
                }
            } else if (!info.activityInfo.packageName.startsWith("com.facebook.katana")) {
                entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
            } else if (!"ALS".equalsIgnoreCase(this.mContext.xml_client) || this.mContext.mAppsIconIndex != 1) {
                entry.icon = Utilities.createIconBitmap(getFullResIcon(info), this.mContext, false, true);
            } else {
                entry.icon = Utilities.createIconBitmap(getFullResIcon(this.mContext.getResources(), (int) R.drawable.icon_facebook_n), this.mContext, true, false);
            }
        }
        return entry;
    }

    public HashMap<ComponentName, Bitmap> getAllIcons() {
        HashMap<ComponentName, Bitmap> set;
        synchronized (this.mCache) {
            set = new HashMap<>();
            for (ComponentName cn : this.mCache.keySet()) {
                set.put(cn, this.mCache.get(cn).icon);
            }
        }
        return set;
    }
}
