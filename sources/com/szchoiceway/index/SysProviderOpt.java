package com.szchoiceway.index;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SysProviderOpt {
    public static final String ADO_LastLoopMode_KEY = "Ado_LastLoopMode";
    public static final String ADO_LastLrcMode_KEY = "Ado_LastLrcMode";
    public static final String ADO_LastPlayFilePath_KEY = "Ado_LastPlayFilePath";
    public static final String ADO_LastPlayFilePos_KEY = "Ado_LastPlayFilePos";
    public static final String ADO_LastPlayFileSize_KEY = "Ado_LastPlayFileSize";
    public static final String ADO_StorageType_KEY = "Ado_StorageType";
    public static final int BTTYPE_FEIYITONG = 5;
    public static final int BTTYPE_IVT_BC5 = 6;
    public static final int BTTYPE_SUDING_BC5 = 1;
    public static final int BTTYPE_SUDING_BC8 = 2;
    public static final int BTTYPE_WENQIANG_BC6 = 3;
    public static final int BTTYPE_WENQIANG_BC9 = 4;
    public static final String BT_AutoAnswer_KEY = "BT_AutoAnswer";
    public static final String CHEKU_SHOW_RADAR_P = "CHEKU_SHOW_RADAR_P";
    private static final String CONTENT_NAME = "content://com.szchoiceway.eventcenter.SysVarProvider/SysVar";
    public static final String KESAIWEI_RECORD_BT_CONNECT_MENU = "KESAIWEI_RECORD_BT_CONNECT_MENU";
    public static final String KESAIWEI_RECORD_BT_OFF = "KESAIWEI_RECORD_BT_OFF";
    public static final String KESAIWEI_RECORD_DVR = "KESAIWEI_RECORD_DVR";
    public static final String KESAIWEI_SYS_MODE_SELECTION = "KESAIWEI_SYS_MODE_SELECTION";
    public static final String KEY_XINGSHUO_FORWARD_VIEW = "KEY_XINGSHUO_FORWARD_VIEW";
    public static final String KEY_XINGSHUO_RIGHT_VIEW = "KEY_XINGSHUO_RIGHT_VIEW";
    public static final String KSW_ACC_ON_FOCUS = "KSW_ACC_ON_FOCUS";
    public static final String KSW_APPS_ICON_SELECT_INDEX = "KSW_APPS_ICON_SELECT_INDEX";
    public static final String KSW_ARL_LEFT_SHOW_INDEX = "KSW_ARL_LEFT_SHOW_INDEX";
    public static final String KSW_ARL_RIGHT_SHOW_INDEX = "KSW_ARL_RIGHT_SHOW_INDEX";
    public static final String KSW_AUDIO_MAIN_SCROLL = "KSW_AUDIO_MAIN_SCROLL";
    public static final String KSW_CHK_VOICE_SWITCH = "KSW_CHK_VOICE_SWITCH";
    public static final String KSW_DVR_APK_PACKAGENAME = "KSW_DVR_APK_PACKAGENAME";
    public static final String KSW_EVO_ID6_MAIN_INTERFACE_INDEX = "KSW_EVO_ID6_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_MAIN_INTERFACE_INDEX = "KSW_EVO_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM = "KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM";
    public static final String KSW_FACTORY_SET_CLIENT = "KSW_FACTORY_SET_CLIENT";
    public static final String KSW_FM_LAUNCH = "KSW_FM_LAUNCH";
    public static final String KSW_FOREIGN_BROWSER = "KSW_FOREIGN_BROWSER";
    public static final String KSW_HAVE_AUX = "KSW_HAVE_AUX";
    public static final String KSW_HAVE_DVD = "KSW_HAVE_DVD";
    public static final String KSW_HAVE_TV = "KSW_HAVE_TV";
    public static final String KSW_INIT_ARM_UPGRADE_GOOGLE_APP = "KSW_INIT_ARM_UPGRADE_GOOGLE_APP";
    public static final String KSW_JLR_MAIN_INTERFACE_INDEX = "KSW_JLR_MAIN_INTERFACE_INDEX";
    public static final String KSW_ORIGINAL_CAR_VIDEO_DISPLAY = "KSW_ORIGINAL_CAR_VIDEO_DISPLAY";
    public static final String KSW_SUPPORT_DASHBOARD = "KSW_SUPPORT_DASHBOARD";
    public static final String LAUNCHER_APPS_CUSTOMIZE_RESUM = "LAUNCHER_APPS_CUSTOMIZE_RESUM";
    public static final String MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM = "MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM";
    public static final String MAISILUO_SYS_GOOGLEPLAY = "MAISILUO_SYS_GOOGLEPLAY";
    public static final String RESOLUTION = "RESOLUTION";
    public static final String SET_AUTO_RUN_GPS_KEY = "Set_AutoRunGPS";
    public static final String SET_BACK_TRACK_KEY = "Set_BackTrack";
    public static final String SET_BAL_FA_KEY = "Set_BalanaceFA";
    public static final String SET_BAL_LR_KEY = "Set_BalanaceLR";
    public static final String SET_BASS_FREQ_KEY = "Set_Bass_Freq";
    public static final String SET_BASS_KEY = "Set_Bass_Val";
    public static final String SET_BRAKE_DETECT_KEY = "Set_BrakeDetection";
    public static final String SET_Canbustype_KEY = "Set_Canbustype";
    public static final String SET_CarCanbusName_ID_KEY = "Set_CarCanbusName_ID";
    public static final String SET_Carstype_ID_KEY = "Set_Carstype_ID";
    public static final String SET_DAY_LIGHT_KEY = "Set_Day_Light";
    public static final String SET_DIM_LIGHT_KEY = "Set_DIM_Light";
    public static final String SET_DVD_MODE = "Set_Dvd_Mode";
    public static final String SET_EQMODE_KEY = "Set_Eq_Mode";
    public static final String SET_LOCAL_NAV_KEY = "Set_LocalNavi";
    public static final String SET_LOUDNESS_KEY = "Set_Loudness";
    public static final String SET_MIDDLE_FREQ_KEY = "Set_Middle_Freq";
    public static final String SET_MIDDLE_KEY = "Set_Middle_Val";
    public static final String SET_NIGHT_LIGHT_KEY = "Set_Night_Light";
    public static final String SET_NavClassName_KEY = "Set_NavClassName";
    public static final String SET_NavPackageName_KEY = "Set_NavPackageName";
    public static final String SET_NavSoundVolume_KEY = "Set_NavSoundVolume";
    public static final String SET_RIGHT_SIGN_DETECT_KEY = "Set_RightSignDetect";
    public static final String SET_SUBWOOFER_KEY = "Set_Subwoofer";
    public static final String SET_TOUCH_BEEP_KEY = "Set_TouchBeep";
    public static final String SET_TREBLE_FREQ_KEY = "Set_Treble_Freq";
    public static final String SET_TREBLE_KEY = "Set_Treble_Val";
    public static final int SET_UI_KESHANG_UI1 = 1;
    public static final int SET_UI_KESHANG_UI2 = 2;
    public static final int SET_UI_KESHANG_UI3 = 3;
    public static final int SET_UI_KESHANG_UI4 = 5;
    public static final int SET_UI_KESHANG_UI5_800x480 = 6;
    public static final int SET_UI_NORMAL = 0;
    public static final int SET_UI_PUSIRUI_UI = 4;
    public static final String SET_USB_DVR_MODE = "Set_Usb_Dvr_Mode";
    public static final String SET_USER_BASS_KEY = "Set_User_Bass";
    public static final String SET_USER_MIDDLE_KEY = "Set_User_Middle";
    public static final String SET_USER_TREBLE_KEY = "Set_User_Treble";
    public static final String SET_USER_UI_TYPE = "Set_User_UI_Type";
    public static final String SET_USER_UI_TYPE_INDEX = "Set_User_UI_Type_index";
    public static final String SYS_8825_UI_NUM_KEY = "Sys_8825UINumber";
    public static final String SYS_APP_VERSION = "Sys_AppVersion";
    public static final String SYS_BT_TYPE_KEY = "Sys_BTDeviceType";
    public static final String SYS_CAR_TYPE_KEY = "Sys_CarType";
    public static final String SYS_CMMB_ONOFF_KEY = "SYS_CMMB_ONOFF_KEY";
    public static final String SYS_COUNTRY = "Sys_Country";
    public static final String SYS_LANDSCAPE_KEY = "Sys_Landscape";
    public static final String SYS_LAST_MODE_KEY = "Sys_Last_Mode";
    public static final String SYS_LAST_MODE_OFFSET = "Sys_LastModeOffset";
    public static final String SYS_MCU_SET_KEY = "Sys_McuSet";
    public static final String SYS_SETDEFAULT_WALLPAPER = "SYS_SETDEFAULT_WALLPAPER";
    public static final String SYS_SETLOGO_INDEX = "SYS_SETLOGO_INDEX";
    public static final String SYS_SET_UI_TYPE = "Sys_Set_UI_Type";
    public static final String SYS_TOUCH_ORGIN_KEY = "Sys_TouchOrgin";
    public static final String SYS_UI_NUMBER_KEY = "Sys_UINumber";
    public static final String SYS_WND_IN_TOP = "Sys_Wnd_In_Top";
    public static final String SYS_ZHTY_UI_SELECT = "SYS_ZHTY_UI_SELECT";
    private static final String TAG = "SysProviderOpt";
    public static final int UI_ANCHANGXING = 2;
    public static final int UI_AOCHEKAI = 32;
    public static final int UI_BORUIZENGHENG = 36;
    public static final int UI_CHEKU_1280X480 = 44;
    public static final int UI_HANGFEI_1280X480 = 38;
    public static final int UI_HANGRUN = 4;
    public static final int UI_HUANGRUN_800X4800 = 35;
    public static final int UI_KANGHUI_800X480 = 43;
    public static final int UI_KESAIWEI_1280X480 = 41;
    public static final int UI_KESHANG = 1;
    public static final int UI_KESHANG_1280X480 = 40;
    public static final int UI_MAIRUIWEI_800x480 = 45;
    public static final int UI_MAISILUO_1280X480 = 37;
    public static final int UI_NORMAL = 0;
    public static final int UI_NORMAL_1920x720 = 101;
    public static final int UI_PUSIRUI = 3;
    public static final int UI_XINGSHUO_800X480 = 42;
    public static final int UI_ZHONGHANGTIANYI_800x480 = 39;
    public static final String VDO_LastLoopMode_KEY = "Vdo_LastLoopMode";
    public static final String VDO_LastPlayFilePath_KEY = "Vdo_LastPlayFilePath";
    public static final String VDO_LastPlayFilePos_KEY = "Vdo_LastPlayFilePos";
    public static final String VDO_LastPlayFileSize_KEY = "Vdo_LastPlayFileSize";
    public static final String VDO_StorageType_KEY = "Vdo_StorageType";
    public static final String XINGSHUO_SYS_CANBUSINFO = "XINGSHUO_SYS_CANBUSINFO";
    public static final String XINGSHUO_SYS_DVR = "XINGSHUO_SYS_DVR";
    public static final String XINGSHUO_SYS_GOOGLEPLAY = "XINGSHUO_SYS_GOOGLEPLAY";
    public static final String XINGSHUO_SYS_MAIN_INTERFACE = "XINGSHUO_SYS_MAIN_INTERFACE";
    public static final String XINGSHUO_SYS_VOICE_SWITCH = "XINGSHUO_SYS_VOICE_SWITCH";
    public static final String ZXW_IS_HAVE_DVD_APK = "ZXW_IS_HAVE_DVD_APK";
    private ContentResolver mCntResolver;
    private Context mContext;
    private Uri mUri = Uri.parse(CONTENT_NAME);

    public SysProviderOpt(Context context) {
        this.mContext = context;
        this.mCntResolver = this.mContext.getContentResolver();
    }

    public Uri insertRecord(String keyName, String keyValue) {
        ContentValues values = new ContentValues();
        values.put("keyname", keyName);
        values.put("keyvalue", keyValue);
        try {
            return this.mCntResolver.insert(this.mUri, values);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public String getRecordValue(String keyName) {
        return getRecordValue(keyName, "");
    }

    public String getRecordValue(String keyName, String defValue) {
        String strValue = "";
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return strValue;
    }

    public int getRecordInteger(String keyName, int defaultValue) {
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                String strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
                if (strValue.length() > 0) {
                    defaultValue = Integer.valueOf(strValue).intValue();
                }
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return defaultValue;
    }

    public long getRecordLong(String keyName, long defaultValue) {
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                String strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
                if (strValue.length() > 0) {
                    defaultValue = Long.valueOf(strValue).longValue();
                }
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return defaultValue;
    }

    public float getRecordFloat(String keyName, float defaultValue) {
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                String strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
                if (strValue.length() > 0) {
                    defaultValue = Float.valueOf(strValue).floatValue();
                }
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return defaultValue;
    }

    public boolean getRecordBoolean(String keyName, boolean defaultValue) {
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                String strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
                if (strValue.length() > 0) {
                    defaultValue = Integer.valueOf(strValue).intValue() == 1;
                }
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return defaultValue;
    }

    public byte getRecordByte(String keyName, byte defaultValue) {
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                String strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
                if (strValue.length() > 0) {
                    defaultValue = Byte.valueOf(strValue).byteValue();
                }
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return defaultValue;
    }

    public double getRecordDouble(String keyName, double defaultValue) {
        String strValue = getRecordValue(keyName, "");
        if (strValue.length() > 0) {
            return Double.valueOf(strValue).doubleValue();
        }
        return defaultValue;
    }

    public void updateRecord(String keyName, String keyValue) {
        updateRecord(keyName, keyValue, true);
    }

    public void updateRecord(String keyName, String keyValue, boolean insertIfNotRecord) {
        String[] selectionArgs = {keyName};
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, new String[]{"keyvalue"}, "keyname=?", selectionArgs, (String) null);
            if (cr2 != null && cr2.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("keyvalue", keyValue);
                if (cr2 != null) {
                    cr2.close();
                    cr2 = null;
                }
                this.mCntResolver.update(this.mUri, values, "keyname=?", selectionArgs);
            } else if (insertIfNotRecord) {
                insertRecord(keyName, keyValue);
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
    }

    public void setRecordDefaultValue(String keyName, String keyValue) {
        if (!checkRecordExist(keyName)) {
            insertRecord(keyName, keyValue);
        }
    }

    public boolean checkRecordExist(String keyName) {
        boolean ret = false;
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, new String[]{"keyvalue"}, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0) {
                ret = true;
            }
            if (cr2 != null) {
                cr2.close();
            }
        } catch (Exception e) {
            if (cr != null) {
                cr.close();
            }
            Log.e(TAG, e.toString());
        }
        return ret;
    }
}
