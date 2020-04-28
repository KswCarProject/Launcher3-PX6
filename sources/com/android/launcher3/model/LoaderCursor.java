package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.BitmapInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.LongArrayMap;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class LoaderCursor extends CursorWrapper {
    private static final String TAG = "LoaderCursor";
    public final LongSparseArray<UserHandle> allUsers = new LongSparseArray<>();
    private final int cellXIndex;
    private final int cellYIndex;
    public long container;
    private final int containerIndex;
    private final int iconIndex;
    private final int iconPackageIndex;
    private final int iconResourceIndex;
    public long id;
    private final int idIndex;
    private final int intentIndex;
    public int itemType;
    private final int itemTypeIndex;
    private final ArrayList<Long> itemsToRemove = new ArrayList<>();
    private final Context mContext;
    private final InvariantDeviceProfile mIDP;
    private final IconCache mIconCache;
    private final UserManagerCompat mUserManager;
    private final LongArrayMap<GridOccupancy> occupied = new LongArrayMap<>();
    private final int profileIdIndex;
    public int restoreFlag;
    private final int restoredIndex;
    private final ArrayList<Long> restoredRows = new ArrayList<>();
    private final int screenIndex;
    public long serialNumber;
    public final int titleIndex;
    public UserHandle user;

    public LoaderCursor(Cursor c, LauncherAppState app) {
        super(c);
        this.mContext = app.getContext();
        this.mIconCache = app.getIconCache();
        this.mIDP = app.getInvariantDeviceProfile();
        this.mUserManager = UserManagerCompat.getInstance(this.mContext);
        this.iconIndex = getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON);
        this.iconPackageIndex = getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE);
        this.iconResourceIndex = getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE);
        this.titleIndex = getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.TITLE);
        this.idIndex = getColumnIndexOrThrow("_id");
        this.containerIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
        this.itemTypeIndex = getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
        this.screenIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
        this.cellXIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
        this.cellYIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
        this.profileIdIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.PROFILE_ID);
        this.restoredIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.RESTORED);
        this.intentIndex = getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
    }

    public boolean moveToNext() {
        boolean result = super.moveToNext();
        if (result) {
            this.itemType = getInt(this.itemTypeIndex);
            this.container = (long) getInt(this.containerIndex);
            this.id = getLong(this.idIndex);
            this.serialNumber = (long) getInt(this.profileIdIndex);
            this.user = this.allUsers.get(this.serialNumber);
            this.restoreFlag = getInt(this.restoredIndex);
        }
        return result;
    }

    public Intent parseIntent() {
        String intentDescription = getString(this.intentIndex);
        try {
            if (TextUtils.isEmpty(intentDescription)) {
                return null;
            }
            return Intent.parseUri(intentDescription, 0);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error parsing Intent");
            return null;
        }
    }

    public ShortcutInfo loadSimpleShortcut() {
        ShortcutInfo info = new ShortcutInfo();
        info.user = this.user;
        info.itemType = this.itemType;
        info.title = getTitle();
        if (!loadIcon(info)) {
            this.mIconCache.getDefaultIcon(info.user).applyTo((ItemInfoWithIcon) info);
        }
        return info;
    }

    /* access modifiers changed from: protected */
    public boolean loadIcon(ShortcutInfo info) {
        LauncherIcons li;
        Throwable th;
        if (this.itemType == 1) {
            String packageName = getString(this.iconPackageIndex);
            String resourceName = getString(this.iconResourceIndex);
            if (!TextUtils.isEmpty(packageName) || !TextUtils.isEmpty(resourceName)) {
                info.iconResource = new Intent.ShortcutIconResource();
                info.iconResource.packageName = packageName;
                info.iconResource.resourceName = resourceName;
                LauncherIcons li2 = LauncherIcons.obtain(this.mContext);
                BitmapInfo iconInfo = li2.createIconBitmap(info.iconResource);
                li2.recycle();
                if (iconInfo != null) {
                    iconInfo.applyTo((ItemInfoWithIcon) info);
                    return true;
                }
            }
        }
        byte[] data = getBlob(this.iconIndex);
        try {
            li = LauncherIcons.obtain(this.mContext);
            li.createIconBitmap(BitmapFactory.decodeByteArray(data, 0, data.length)).applyTo((ItemInfoWithIcon) info);
            if (li != null) {
                li.close();
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load icon for info " + info, e);
            return false;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    private String getTitle() {
        String title = getString(this.titleIndex);
        return TextUtils.isEmpty(title) ? "" : Utilities.trim(title);
    }

    public ShortcutInfo getRestoredItemInfo(Intent intent) {
        ShortcutInfo info = new ShortcutInfo();
        info.user = this.user;
        info.intent = intent;
        if (!loadIcon(info)) {
            this.mIconCache.getTitleAndIcon(info, false);
        }
        if (hasRestoreFlag(1)) {
            String title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                info.title = Utilities.trim(title);
            }
        } else if (!hasRestoreFlag(2)) {
            throw new InvalidParameterException("Invalid restoreType " + this.restoreFlag);
        } else if (TextUtils.isEmpty(info.title)) {
            info.title = getTitle();
        }
        info.contentDescription = this.mUserManager.getBadgedLabelForUser(info.title, info.user);
        info.itemType = this.itemType;
        info.status = this.restoreFlag;
        return info;
    }

    public ShortcutInfo getAppShortcutInfo(Intent intent, boolean allowMissingTarget, boolean useLowResIcon) {
        if (this.user == null) {
            Log.d(TAG, "Null user found in getShortcutInfo");
            return null;
        }
        ComponentName componentName = intent.getComponent();
        if (componentName == null) {
            Log.d(TAG, "Missing component found in getShortcutInfo");
            return null;
        }
        Intent newIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        newIntent.addCategory("android.intent.category.LAUNCHER");
        newIntent.setComponent(componentName);
        LauncherActivityInfo lai = LauncherAppsCompat.getInstance(this.mContext).resolveActivity(newIntent, this.user);
        if (lai != null || allowMissingTarget) {
            ShortcutInfo info = new ShortcutInfo();
            info.itemType = 0;
            info.user = this.user;
            info.intent = newIntent;
            this.mIconCache.getTitleAndIcon(info, lai, useLowResIcon);
            if (this.mIconCache.isDefaultIcon(info.iconBitmap, this.user)) {
                loadIcon(info);
            }
            if (lai != null) {
                AppInfo.updateRuntimeFlagsForActivityTarget(info, lai);
            }
            if (TextUtils.isEmpty(info.title)) {
                info.title = getTitle();
            }
            if (info.title == null) {
                info.title = componentName.getClassName();
            }
            info.contentDescription = this.mUserManager.getBadgedLabelForUser(info.title, info.user);
            return info;
        }
        Log.d(TAG, "Missing activity found in getShortcutInfo: " + componentName);
        return null;
    }

    public ContentWriter updater() {
        return new ContentWriter(this.mContext, new ContentWriter.CommitParams("_id= ?", new String[]{Long.toString(this.id)}));
    }

    public void markDeleted(String reason) {
        FileLog.e(TAG, reason);
        this.itemsToRemove.add(Long.valueOf(this.id));
    }

    public boolean commitDeleted() {
        if (this.itemsToRemove.size() <= 0) {
            return false;
        }
        this.mContext.getContentResolver().delete(LauncherSettings.Favorites.CONTENT_URI, Utilities.createDbSelectionQuery("_id", this.itemsToRemove), (String[]) null);
        return true;
    }

    public void markRestored() {
        if (this.restoreFlag != 0) {
            this.restoredRows.add(Long.valueOf(this.id));
            this.restoreFlag = 0;
        }
    }

    public boolean hasRestoreFlag(int flagMask) {
        return (this.restoreFlag & flagMask) != 0;
    }

    public void commitRestoredItems() {
        if (this.restoredRows.size() > 0) {
            ContentValues values = new ContentValues();
            values.put(LauncherSettings.Favorites.RESTORED, 0);
            this.mContext.getContentResolver().update(LauncherSettings.Favorites.CONTENT_URI, values, Utilities.createDbSelectionQuery("_id", this.restoredRows), (String[]) null);
        }
    }

    public boolean isOnWorkspaceOrHotseat() {
        return this.container == -100 || this.container == -101;
    }

    public void applyCommonProperties(ItemInfo info) {
        info.id = this.id;
        info.container = this.container;
        info.screenId = (long) getInt(this.screenIndex);
        info.cellX = getInt(this.cellXIndex);
        info.cellY = getInt(this.cellYIndex);
    }

    public void checkAndAddItem(ItemInfo info, BgDataModel dataModel) {
        if (checkItemPlacement(info, dataModel.workspaceScreens)) {
            dataModel.addItem(this.mContext, info, false);
        } else {
            markDeleted("Item position overlap");
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkItemPlacement(ItemInfo item, ArrayList<Long> workspaceScreens) {
        ItemInfo itemInfo = item;
        long containerIndex2 = itemInfo.screenId;
        if (itemInfo.container == -101) {
            GridOccupancy hotseatOccupancy = (GridOccupancy) this.occupied.get(-101);
            if (itemInfo.screenId >= ((long) this.mIDP.numHotseatIcons)) {
                Log.e(TAG, "Error loading shortcut " + itemInfo + " into hotseat position " + itemInfo.screenId + ", position out of bounds: (0 to " + (this.mIDP.numHotseatIcons - 1) + ")");
                return false;
            } else if (hotseatOccupancy == null) {
                GridOccupancy occupancy = new GridOccupancy(this.mIDP.numHotseatIcons, 1);
                occupancy.cells[(int) itemInfo.screenId][0] = true;
                this.occupied.put(-101, occupancy);
                return true;
            } else if (hotseatOccupancy.cells[(int) itemInfo.screenId][0]) {
                Log.e(TAG, "Error loading shortcut into hotseat " + itemInfo + " into position (" + itemInfo.screenId + ":" + itemInfo.cellX + "," + itemInfo.cellY + ") already occupied");
                return false;
            } else {
                hotseatOccupancy.cells[(int) itemInfo.screenId][0] = true;
                return true;
            }
        } else if (itemInfo.container == -100) {
            if (!workspaceScreens.contains(Long.valueOf(itemInfo.screenId))) {
                return false;
            }
            int countX = this.mIDP.numColumns;
            int countY = this.mIDP.numRows;
            if ((itemInfo.container != -100 || itemInfo.cellX >= 0) && itemInfo.cellY >= 0 && itemInfo.cellX + itemInfo.spanX <= countX && itemInfo.cellY + itemInfo.spanY <= countY) {
                if (!this.occupied.containsKey(itemInfo.screenId)) {
                    GridOccupancy screen = new GridOccupancy(countX + 1, countY + 1);
                    if (itemInfo.screenId == 0) {
                        screen.markCells(0, 0, countX + 1, 1, true);
                    }
                    this.occupied.put(itemInfo.screenId, screen);
                }
                GridOccupancy occupancy2 = (GridOccupancy) this.occupied.get(itemInfo.screenId);
                if (occupancy2.isRegionVacant(itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY)) {
                    occupancy2.markCells(itemInfo, true);
                    return true;
                }
                Log.e(TAG, "Error loading shortcut " + itemInfo + " into cell (" + containerIndex2 + "-" + itemInfo.screenId + ":" + itemInfo.cellX + "," + itemInfo.cellX + "," + itemInfo.spanX + "," + itemInfo.spanY + ") already occupied");
                return false;
            }
            Log.e(TAG, "Error loading shortcut " + itemInfo + " into cell (" + containerIndex2 + "-" + itemInfo.screenId + ":" + itemInfo.cellX + "," + itemInfo.cellY + ") out of screen bounds ( " + countX + "x" + countY + ")");
            return false;
        } else {
            ArrayList<Long> arrayList = workspaceScreens;
            return true;
        }
    }
}
