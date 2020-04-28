package com.android.launcher3.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.SysProviderOpt;
import java.util.ArrayList;
import java.util.List;

public class EventUtils {
    public static final String ACTION_SWITCH_ORIGINACAR = "com.szchoiceway.eventcenter.EventUtils.ACTION_SWITCH_ORIGINACAR";
    public static final String BT_MODE_CLASS_NAME = "com.szchoiceway.btsuite.BTMainActivity";
    public static final String BT_MODE_PACKAGE_NAME = "com.szchoiceway.btsuite";
    public static final String EXPLORER_MODE_CLASS_NAME2 = "com.google.android.apps.chrome.Main";
    public static final String EXPLORER_MODE_PACKAGE_NAME2 = "com.android.chrome";
    public static final String HBCP_EVT_HSHF_STATUS = "com.szchoiceway.btsuite.HBCP_EVT_HSHF_STATUS";
    public static final String INTENT_EXTRA_INT_KEYNAME = "com.szchoiceway.btsuite.DATA_INT";
    public static final byte MCU_KEY_NEXT = 2;
    public static final byte MCU_KEY_PLAY = 4;
    public static final byte MCU_KEY_PLAYPAUSE = 6;
    public static final byte MCU_KEY_PREV = 3;
    public static final byte MCU_KEY_STANDBY = -9;
    public static final byte MCU_KEY_STOP = 5;
    public static final String MOVIE_MODE_CLASS_NAME = "com.szchoiceway.videoplayer.MainActivity";
    public static final String MOVIE_MODE_PACKAGE_NAME = "com.szchoiceway.videoplayer";
    public static final String MUSIC_MODE_CLASS_NAME = "com.szchoiceway.musicplayer.MainActivity";
    public static final String MUSIC_MODE_PACKAGE_NAME = "com.szchoiceway.musicplayer";
    public static final String NAV_MODE_CLASS_NAME = "com.szchoiceway.navigation.MainActivity";
    public static final String NAV_MODE_PACKAGE_NAME = "com.szchoiceway.navigation";
    public static final String PHONEAPP_MODE_CLASS_NAME = "net.easyconn.WelcomeActivity";
    public static final String PHONEAPP_MODE_PACKAGE_NAME = "net.easyconn";
    public static final String RADIO_MODE_CLASS_NAME = "com.szchoiceway.radio.MainActivity";
    public static final String RADIO_MODE_PACKAGE_NAME = "com.szchoiceway.radio";
    public static final String REC_AUTONAVI_STANDARD = "AUTONAVI_STANDARD_BROADCAST_SEND";
    public static final String SET_MODE_CLASS_NAME = "com.szchoiceway.settings.MainActivity";
    public static final String SET_MODE_PACKAGE_NAME = "com.szchoiceway.settings";
    public static final String VALID_MODE_INFOR_CHANGE = "com.szchoiceway.eventcenter.EventUtils.VALID_MODE_INFOR_CHANGE";
    public static final String ZXW_ACTION_NOTIIFY_MEDIA_TYPE = "com.szchoiceway.eventcenter.EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE";
    public static final String ZXW_ACTION_NOTIIFY_MEDIA_TYPE_EXTRA = "com.szchoiceway.eventcenter.EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE_EXTRA";
    public static final String ZXW_CAN_KEY_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT";
    public static final String ZXW_CAN_KEY_EVT_EXTRA = "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT_EXTRA";
    public static final String ZXW_CAN_START_UP_APPS = "com.szchoiceway.eventcenter.EventUtils.ZXW_CAN_START_UP_APPS";
    public static final int ZXW_ORIGINAL_MCU_KEY_DOWN = 4;
    public static final int ZXW_ORIGINAL_MCU_KEY_ENTER = 5;
    public static final String ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA = "com.choiceway.eventcenter.EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA";
    public static final String ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT";
    public static final int ZXW_ORIGINAL_MCU_KEY_LEFT = 1;
    public static final int ZXW_ORIGINAL_MCU_KEY_LEFT_HANDED = 7;
    public static final int ZXW_ORIGINAL_MCU_KEY_RIGHT = 2;
    public static final int ZXW_ORIGINAL_MCU_KEY_RIGHT_HANDED = 8;
    public static final int ZXW_ORIGINAL_MCU_KEY_UP = 3;
    public static final String ZXW_SENDBROADCAST8902MOD = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD";
    public static final String ZXW_SENDBROADCAST8902MOD_EXTRA = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_EXTRA";
    public static final String ZXW_SENDBROADCAST8902MOD_ShunShiSuDu = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_ShunShiSuDu";
    public static final String ZXW_SENDBROADCAST8902MOD_SpeedUnit = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_SpeedUnit";
    public static final String ZXW_SENDBROADCAST8902MOD_anquandai = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_anquandai";
    public static final String ZXW_SENDBROADCAST8902MOD_fadongjizhuansu = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_fadongjizhuansu";
    public static final String ZXW_SENDBROADCAST8902MOD_huanjinwendu = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_huanjinwendu";
    public static final String ZXW_SENDBROADCAST8902MOD_shousha = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_shousha";
    public static final String ZXW_SENDBROADCAST8902MOD_xushilicheng = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_xushilicheng";
    public static final String ZXW_SENDBROADCAST8902MOD_youLiang = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_youLiang";

    public enum eSrcMode {
        SRC_NONE(0),
        SRC_RADIO(1),
        SRC_DVD(2),
        SRC_USB(3),
        SRC_CARD(4),
        SRC_IPOD(5),
        SRC_BT(6),
        SRC_BTMUSIC(7),
        SRC_CMMB(8),
        SRC_TV(9),
        SRC_MOVIE(10),
        SRC_MUSIC(11),
        SRC_EBOOK(12),
        SRC_IMAGE(13),
        SRC_ANDROID(14),
        SRC_VMCD(15),
        SRC_NETWORK(16),
        SRC_CARMEDIA(17),
        SRC_CAR_BT(18),
        SRC_DVR(19),
        SRC_MOBILE_APP(20),
        SRC_ATSL_AIRCONSOLE(30),
        SRC_TXZ_WEBCHAT(37),
        SRC_TXZ_MUSIC(38),
        SRC_PHONELINK(39),
        SRC_AUX(40),
        SRC_BACKCAR(41),
        SRC_GPS(42),
        SRC_HOME(43),
        SRC_REHOME(44),
        SRC_COMPASS(45),
        SRC_STANDBY(46),
        SRC_EQ(47),
        SRC_BACKLIGHT_SET(48),
        SRC_SETUP(49),
        SRC_FRONT_CAMERA(50),
        SRC_BCAM(51),
        SRC_LEFT_CAMERA(52),
        SRC_RIGHT_CAMERA(54),
        SRC_MCU_VERSION(80),
        SRC_TFT_VERSION(81),
        SRC_NULL(99),
        SRC_POWERON(100),
        SRC_POWEROFF(101),
        SRC_IDLE_MODE(103),
        SRC_IDLE_REST(104),
        SRC_MUSIC_NAVI(105),
        SRC_PHONE_APP(106),
        SRC_QUICK_ACCESS_1(107),
        SRC_QUICK_ACCESS_2(108),
        SRC_Original_TO_ARM(109);
        
        int value;

        private eSrcMode(int val) {
            this.value = val;
        }

        public byte getValue() {
            return (byte) (this.value & 255);
        }

        public int getIntValue() {
            return this.value;
        }

        public String toString() {
            return Integer.toString(this.value);
        }
    }

    @SuppressLint({"NewApi"})
    public static void startActivityIfNotRuning(Context context, String packageName, String className) {
        if (context != null && packageName != null && className != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(100, 0);
            ComponentName topAct = activityManager.getRunningTasks(1).get(0).topActivity;
            if (!topAct.getClassName().equals(className) || !topAct.getPackageName().equals(packageName)) {
                Intent intent = new Intent();
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.setComponent(new ComponentName(packageName, className));
                intent.setFlags(270532608);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAG", "startActivityIfNotRuning: e = " + e.toString());
                    try {
                        Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String mainAct = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setFlags(8388608);
        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, 1);
        int i = 0;
        while (true) {
            if (i >= list.size()) {
                break;
            }
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
            i++;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    public static void startActivityForPackage(Context context, String packageName) {
        try {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
        } catch (Exception e) {
            Log.e("TAG", "startActivityForPackage: e = " + e.toString());
            try {
                Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static boolean isActivityRuning(Context context, Class<?> cls) {
        if (!cls.getName().equals(((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0).topActivity.getClassName())) {
            return false;
        }
        return true;
    }

    public static boolean getInstallStatus(Context context, String strPackage) {
        boolean bIsInstall = false;
        PackageManager packageManager = context.getPackageManager();
        new ArrayList();
        List<PackageInfo> mAllPackages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < mAllPackages.size(); i++) {
            if (strPackage.equals(mAllPackages.get(i).packageName)) {
                bIsInstall = true;
            }
        }
        return bIsInstall;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getProgressFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            long m = (long) ((time / 60000) % 60);
            return String.format("%02d:%02d:%02d", new Object[]{Long.valueOf((long) ((time / 3600000) % 24)), Long.valueOf(m), Long.valueOf((long) ((time / 1000) % 60))});
        } else if ((time / 60) / 1000 > 0) {
            return String.format("00:%02d:%02d", new Object[]{Long.valueOf((long) ((time / 60000) % 60)), Long.valueOf((long) ((time / 1000) % 60))});
        } else {
            return String.format("00:00:%02d", new Object[]{Long.valueOf((long) ((time / 1000) % 60))});
        }
    }

    public static int getSecFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            int i = (time / 60000) % 60;
            int i2 = (time / 3600000) % 24;
            return (time / 1000) % 60;
        } else if ((time / 60) / 1000 <= 0) {
            return (time / 1000) % 60;
        } else {
            int i3 = (time / 60000) % 60;
            return (time / 1000) % 60;
        }
    }

    public static int getMinFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            int i = (time / 1000) % 60;
            int i2 = (time / 3600000) % 24;
            return (time / 60000) % 60;
        } else if ((time / 60) / 1000 > 0) {
            int i3 = (time / 1000) % 60;
            return (time / 60000) % 60;
        } else {
            int iMinute = (time / 1000) % 60;
            return 0;
        }
    }

    public static int getHourFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            int i = (time / 1000) % 60;
            int i2 = (time / 60000) % 60;
            return (time / 3600000) % 24;
        } else if ((time / 60) / 1000 > 0) {
            int i3 = (time / 1000) % 60;
            int i4 = (time / 60000) % 60;
            return 0;
        } else {
            int iHour = (time / 1000) % 60;
            return 0;
        }
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public static void onEnterDashBoard(Context context, boolean bIsSupport) {
        if (bIsSupport) {
            startActivityIfNotRuning(context, "com.szchoiceway.ksw_dashboard", "com.szchoiceway.ksw_dashboard.MainActivity");
            return;
        }
        try {
            Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onEnterPhoneLink(Context context) {
        if (getInstallStatus(context, PHONEAPP_MODE_PACKAGE_NAME)) {
            startActivityIfNotRuning(context, PHONEAPP_MODE_PACKAGE_NAME, PHONEAPP_MODE_CLASS_NAME);
            return;
        }
        try {
            Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onEnterDvd(Context context, int iHaveDvdType) {
        if (iHaveDvdType == 1) {
            startActivityIfNotRuning(context, "com.szchoiceway.ksw_dvd", "com.szchoiceway.ksw_dvd.MainActivity");
        } else if (iHaveDvdType == 2) {
            startActivityIfNotRuning(context, "com.szchoiceway.dvdplayer", "com.szchoiceway.dvdplayer.MainActivity");
        } else {
            try {
                Toast.makeText(context, context.getString(R.string.lb_no_device), 0).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void onEnterTv(Context context, boolean bHaveTv) {
        if (bHaveTv) {
            startActivityIfNotRuning(context, "com.szchoiceway.ksw_cmmb", "com.szchoiceway.ksw_cmmb.MainActivity");
            return;
        }
        try {
            Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onEnterAux(Context context, boolean bHaveAux) {
        if (((Launcher) context).m_iUITypeVer == 101) {
            if (getInstallStatus(context, "com.szchoiceway.auxplayer")) {
                startActivityIfNotRuning(context, "com.szchoiceway.auxplayer", "com.szchoiceway.auxplayer.MainActivity");
                return;
            }
            try {
                Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (bHaveAux) {
            startActivityIfNotRuning(context, "com.szchoiceway.ksw_aux", "com.szchoiceway.ksw_aux.MainActivity");
        } else {
            try {
                Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void onEnterDvr(Context context, int dvtType) {
        if (dvtType == 1) {
            startActivityIfNotRuning(context, "com.szchoiceway.ksw_dvr", "com.szchoiceway.ksw_dvr.MainActivity");
        } else if (dvtType == 2) {
            String xml_Dvr_apk_packagename = ((Launcher) context).mApp.getSysProviderOpt().getRecordValue(SysProviderOpt.KSW_DVR_APK_PACKAGENAME, "");
            if ("".equals(xml_Dvr_apk_packagename) || "com.anwensoft.cardvr".equals(xml_Dvr_apk_packagename)) {
                startActivityIfNotRuning(context, "com.anwensoft.cardvr", "com.anwensoft.cardvr.ui.GuideActivity");
            } else if ("".equals(xml_Dvr_apk_packagename) || "com.ankai.cardvr".equals(xml_Dvr_apk_packagename)) {
                startActivityIfNotRuning(context, "com.ankai.cardvr", "com.ankai.cardvr.ui.GuideActivity");
            } else {
                startActivityForPackage(context, xml_Dvr_apk_packagename);
            }
        } else {
            try {
                Toast.makeText(context, context.getString(R.string.lb_no_device), 1).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendBroadcastCanKeyExtra(Context context, String action, int iExtraData) {
        if (context != null) {
            Intent intt = new Intent(action);
            intt.putExtra(ZXW_CAN_KEY_EVT_EXTRA, iExtraData);
            Log.i("TAG", "---iExtraData---" + iExtraData);
            context.sendBroadcast(intt);
        }
    }
}
