package com.android.launcher3;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SysProviderOpt {
    public static final int BTTYPE_FEIYITONG = 5;
    public static final int BTTYPE_IVT_BC5 = 6;
    public static final int BTTYPE_SUDING_BC5 = 1;
    public static final int BTTYPE_SUDING_BC8 = 2;
    public static final int BTTYPE_WENQIANG_BC6 = 3;
    public static final int BTTYPE_WENQIANG_BC9 = 4;
    private static final String CONTENT_NAME = "content://com.szchoiceway.eventcenter.SysVarProvider/SysVar";
    public static final String KESAIWEI_RECORD_BT_OFF = "KESAIWEI_RECORD_BT_OFF";
    public static final String KESAIWEI_RECORD_DVR = "KESAIWEI_RECORD_DVR";
    public static final String KESAIWEI_SYS_MODE_SELECTION = "KESAIWEI_SYS_MODE_SELECTION";
    public static final String KSW_APPS_ICON_SELECT_INDEX = "KSW_APPS_ICON_SELECT_INDEX";
    public static final String KSW_ARL_LEFT_SHOW_INDEX = "KSW_ARL_LEFT_SHOW_INDEX";
    public static final String KSW_ARL_RIGHT_SHOW_INDEX = "KSW_ARL_RIGHT_SHOW_INDEX";
    public static final String KSW_DVR_APK_PACKAGENAME = "KSW_DVR_APK_PACKAGENAME";
    public static final String KSW_EVO_ID6_MAIN_INTERFACE_INDEX = "KSW_EVO_ID6_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_MAIN_INTERFACE_INDEX = "KSW_EVO_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM = "KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM";
    public static final String KSW_FACTORY_SET_CLIENT = "KSW_FACTORY_SET_CLIENT";
    public static final String KSW_HAVE_AUX = "KSW_HAVE_AUX";
    public static final String KSW_HAVE_DVD = "KSW_HAVE_DVD";
    public static final String KSW_HAVE_TV = "KSW_HAVE_TV";
    public static final String KSW_SUPPORT_DASHBOARD = "KSW_SUPPORT_DASHBOARD";
    public static final String MAISILUO_SYS_GOOGLEPLAY = "MAISILUO_SYS_GOOGLEPLAY";
    public static final String SET_USER_UI_TYPE = "Set_User_UI_Type";
    public static final String SET_USER_UI_TYPE_INDEX = "Set_User_UI_Type_index";
    public static final String SYS_BT_TYPE_KEY = "Sys_BTDeviceType";
    public static final String SYS_LAST_MODE_OFFSET = "Sys_LastModeOffset";
    private static final String TAG = "SysProviderOpt";
    public static final int UI_KESAIWEI_1280X480 = 41;
    public static final int UI_NORMAL_1920X720 = 101;
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
        boolean z = true;
        Cursor cr = null;
        try {
            Cursor cr2 = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", new String[]{keyName}, (String) null);
            if (cr2 != null && cr2.getCount() > 0 && cr2.moveToNext()) {
                String strValue = cr2.getString(cr2.getColumnIndex("keyvalue"));
                if (strValue.length() > 0) {
                    if (Integer.valueOf(strValue).intValue() != 1) {
                        z = false;
                    }
                    defaultValue = z;
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
