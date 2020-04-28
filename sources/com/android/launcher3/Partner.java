package com.android.launcher3;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import java.io.File;

public class Partner {
    private static final String ACTION_PARTNER_CUSTOMIZATION = "com.android.launcher3.action.PARTNER_CUSTOMIZATION";
    public static final String RES_DEFAULT_LAYOUT = "partner_default_layout";
    public static final String RES_DEFAULT_WALLPAPER_HIDDEN = "default_wallpapper_hidden";
    public static final String RES_FOLDER = "partner_folder";
    public static final String RES_GRID_ICON_SIZE_DP = "grid_icon_size_dp";
    public static final String RES_GRID_NUM_COLUMNS = "grid_num_columns";
    public static final String RES_GRID_NUM_ROWS = "grid_num_rows";
    public static final String RES_REQUIRE_FIRST_RUN_FLOW = "requires_first_run_flow";
    public static final String RES_SYSTEM_WALLPAPER_DIR = "system_wallpaper_directory";
    public static final String RES_WALLPAPERS = "partner_wallpapers";
    static final String TAG = "Launcher.Partner";
    private static Partner sPartner;
    private static boolean sSearched = false;
    private final String mPackageName;
    private final Resources mResources;

    public static synchronized Partner get(PackageManager pm) {
        Partner partner;
        synchronized (Partner.class) {
            if (!sSearched) {
                Pair<String, Resources> apkInfo = Utilities.findSystemApk(ACTION_PARTNER_CUSTOMIZATION, pm);
                if (apkInfo != null) {
                    sPartner = new Partner((String) apkInfo.first, (Resources) apkInfo.second);
                }
                sSearched = true;
            }
            partner = sPartner;
        }
        return partner;
    }

    private Partner(String packageName, Resources res) {
        this.mPackageName = packageName;
        this.mResources = res;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public Resources getResources() {
        return this.mResources;
    }

    public boolean hasDefaultLayout() {
        return getResources().getIdentifier(RES_DEFAULT_LAYOUT, "xml", getPackageName()) != 0;
    }

    public boolean hasFolder() {
        return getResources().getIdentifier(RES_FOLDER, "xml", getPackageName()) != 0;
    }

    public boolean hideDefaultWallpaper() {
        int resId = getResources().getIdentifier(RES_DEFAULT_WALLPAPER_HIDDEN, "bool", getPackageName());
        return resId != 0 && getResources().getBoolean(resId);
    }

    public File getWallpaperDirectory() {
        int resId = getResources().getIdentifier(RES_SYSTEM_WALLPAPER_DIR, "string", getPackageName());
        if (resId != 0) {
            return new File(getResources().getString(resId));
        }
        return null;
    }

    public boolean requiresFirstRunFlow() {
        int resId = getResources().getIdentifier(RES_REQUIRE_FIRST_RUN_FLOW, "bool", getPackageName());
        return resId != 0 && getResources().getBoolean(resId);
    }

    public void applyInvariantDeviceProfileOverrides(InvariantDeviceProfile inv, DisplayMetrics dm) {
        int numRows = -1;
        int numColumns = -1;
        float iconSize = -1.0f;
        try {
            int resId = getResources().getIdentifier(RES_GRID_NUM_ROWS, "integer", getPackageName());
            if (resId > 0) {
                numRows = getResources().getInteger(resId);
            }
            int resId2 = getResources().getIdentifier(RES_GRID_NUM_COLUMNS, "integer", getPackageName());
            if (resId2 > 0) {
                numColumns = getResources().getInteger(resId2);
            }
            int resId3 = getResources().getIdentifier(RES_GRID_ICON_SIZE_DP, "dimen", getPackageName());
            if (resId3 > 0) {
                iconSize = Utilities.dpiFromPx(getResources().getDimensionPixelSize(resId3), dm);
            }
            if (numRows > 0 && numColumns > 0) {
                inv.numRows = numRows;
                inv.numColumns = numColumns;
            }
            if (iconSize > 0.0f) {
                inv.iconSize = iconSize;
            }
        } catch (Resources.NotFoundException ex) {
            Log.e(TAG, "Invalid Partner grid resource!", ex);
        }
    }
}
