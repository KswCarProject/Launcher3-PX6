package com.android.launcher3.shortcuts;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeepShortcutManager {
    private static final int FLAG_GET_ALL = 11;
    private static final String TAG = "DeepShortcutManager";
    private static DeepShortcutManager sInstance;
    private static final Object sInstanceLock = new Object();
    private final LauncherApps mLauncherApps;
    private boolean mWasLastCallSuccess;

    public static DeepShortcutManager getInstance(Context context) {
        DeepShortcutManager deepShortcutManager;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new DeepShortcutManager(context.getApplicationContext());
            }
            deepShortcutManager = sInstance;
        }
        return deepShortcutManager;
    }

    private DeepShortcutManager(Context context) {
        this.mLauncherApps = (LauncherApps) context.getSystemService("launcherapps");
    }

    public static boolean supportsShortcuts(ItemInfo info) {
        boolean isItemPromise = (info instanceof ShortcutInfo) && ((ShortcutInfo) info).hasPromiseIconUi();
        if (info.itemType != 0 || info.isDisabled() || isItemPromise) {
            return false;
        }
        return true;
    }

    public boolean wasLastCallSuccess() {
        return this.mWasLastCallSuccess;
    }

    public void onShortcutsChanged(List<ShortcutInfoCompat> list) {
    }

    public List<ShortcutInfoCompat> queryForFullDetails(String packageName, List<String> shortcutIds, UserHandle user) {
        return query(11, packageName, (ComponentName) null, shortcutIds, user);
    }

    public List<ShortcutInfoCompat> queryForShortcutsContainer(ComponentName activity, List<String> ids, UserHandle user) {
        return query(9, activity.getPackageName(), activity, ids, user);
    }

    @TargetApi(25)
    public void unpinShortcut(ShortcutKey key) {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            String packageName = key.componentName.getPackageName();
            String id = key.getId();
            UserHandle user = key.user;
            List<String> pinnedIds = extractIds(queryForPinnedShortcuts(packageName, user));
            pinnedIds.remove(id);
            try {
                this.mLauncherApps.pinShortcuts(packageName, pinnedIds, user);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.w(TAG, "Failed to unpin shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        }
    }

    @TargetApi(25)
    public void pinShortcut(ShortcutKey key) {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            String packageName = key.componentName.getPackageName();
            String id = key.getId();
            UserHandle user = key.user;
            List<String> pinnedIds = extractIds(queryForPinnedShortcuts(packageName, user));
            pinnedIds.add(id);
            try {
                this.mLauncherApps.pinShortcuts(packageName, pinnedIds, user);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.w(TAG, "Failed to pin shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        }
    }

    @TargetApi(25)
    public void startShortcut(String packageName, String id, Rect sourceBounds, Bundle startActivityOptions, UserHandle user) {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            try {
                this.mLauncherApps.startShortcut(packageName, id, sourceBounds, startActivityOptions, user);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.e(TAG, "Failed to start shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        }
    }

    @TargetApi(25)
    public Drawable getShortcutIconDrawable(ShortcutInfoCompat shortcutInfo, int density) {
        if (!Utilities.ATLEAST_NOUGAT_MR1) {
            return null;
        }
        try {
            Drawable icon = this.mLauncherApps.getShortcutIconDrawable(shortcutInfo.getShortcutInfo(), density);
            this.mWasLastCallSuccess = true;
            return icon;
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to get shortcut icon", e);
            this.mWasLastCallSuccess = false;
            return null;
        }
    }

    public List<ShortcutInfoCompat> queryForPinnedShortcuts(String packageName, UserHandle user) {
        return queryForPinnedShortcuts(packageName, (List<String>) null, user);
    }

    public List<ShortcutInfoCompat> queryForPinnedShortcuts(String packageName, List<String> shortcutIds, UserHandle user) {
        return query(2, packageName, (ComponentName) null, shortcutIds, user);
    }

    public List<ShortcutInfoCompat> queryForAllShortcuts(UserHandle user) {
        return query(11, (String) null, (ComponentName) null, (List<String>) null, user);
    }

    private List<String> extractIds(List<ShortcutInfoCompat> shortcuts) {
        List<String> shortcutIds = new ArrayList<>(shortcuts.size());
        for (ShortcutInfoCompat shortcut : shortcuts) {
            shortcutIds.add(shortcut.getId());
        }
        return shortcutIds;
    }

    @TargetApi(25)
    private List<ShortcutInfoCompat> query(int flags, String packageName, ComponentName activity, List<String> shortcutIds, UserHandle user) {
        if (!Utilities.ATLEAST_NOUGAT_MR1) {
            return Collections.EMPTY_LIST;
        }
        LauncherApps.ShortcutQuery q = new LauncherApps.ShortcutQuery();
        q.setQueryFlags(flags);
        if (packageName != null) {
            q.setPackage(packageName);
            q.setActivity(activity);
            q.setShortcutIds(shortcutIds);
        }
        List<android.content.pm.ShortcutInfo> shortcutInfos = null;
        try {
            shortcutInfos = this.mLauncherApps.getShortcuts(q, user);
            this.mWasLastCallSuccess = true;
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to query for shortcuts", e);
            this.mWasLastCallSuccess = false;
        }
        if (shortcutInfos == null) {
            return Collections.EMPTY_LIST;
        }
        List<ShortcutInfoCompat> shortcutInfoCompats = new ArrayList<>(shortcutInfos.size());
        for (android.content.pm.ShortcutInfo shortcutInfo : shortcutInfos) {
            shortcutInfoCompats.add(new ShortcutInfoCompat(shortcutInfo));
        }
        return shortcutInfoCompats;
    }

    @TargetApi(25)
    public boolean hasHostPermission() {
        if (!Utilities.ATLEAST_NOUGAT_MR1) {
            return false;
        }
        try {
            return this.mLauncherApps.hasShortcutHostPermission();
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to make shortcut manager call", e);
            return false;
        }
    }
}
