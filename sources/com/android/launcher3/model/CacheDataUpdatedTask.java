package com.android.launcher3.model;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CacheDataUpdatedTask extends BaseModelUpdateTask {
    public static final int OP_CACHE_UPDATE = 1;
    public static final int OP_SESSION_UPDATE = 2;
    private final int mOp;
    private final HashSet<String> mPackages;
    private final UserHandle mUser;

    public CacheDataUpdatedTask(int op, UserHandle user, HashSet<String> packages) {
        this.mOp = op;
        this.mUser = user;
        this.mPackages = packages;
    }

    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
        IconCache iconCache = app.getIconCache();
        final ArrayList<AppInfo> updatedApps = new ArrayList<>();
        ArrayList<ShortcutInfo> updatedShortcuts = new ArrayList<>();
        synchronized (dataModel) {
            Iterator<ItemInfo> it = dataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo info = it.next();
                if ((info instanceof ShortcutInfo) && this.mUser.equals(info.user)) {
                    ShortcutInfo si = (ShortcutInfo) info;
                    ComponentName cn = si.getTargetComponent();
                    if (si.itemType == 0 && isValidShortcut(si) && cn != null && this.mPackages.contains(cn.getPackageName())) {
                        iconCache.getTitleAndIcon(si, si.usingLowResIcon);
                        updatedShortcuts.add(si);
                    }
                }
            }
            apps.updateIconsAndLabels(this.mPackages, this.mUser, updatedApps);
        }
        bindUpdatedShortcuts(updatedShortcuts, this.mUser);
        if (!updatedApps.isEmpty()) {
            scheduleCallbackTask(new LauncherModel.CallbackTask() {
                public void execute(LauncherModel.Callbacks callbacks) {
                    callbacks.bindAppsAddedOrUpdated(updatedApps);
                }
            });
        }
    }

    public boolean isValidShortcut(ShortcutInfo si) {
        switch (this.mOp) {
            case 1:
                return true;
            case 2:
                return si.hasPromiseIconUi();
            default:
                return false;
        }
    }
}
