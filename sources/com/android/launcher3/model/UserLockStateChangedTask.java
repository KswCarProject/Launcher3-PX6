package com.android.launcher3.model;

import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class UserLockStateChangedTask extends BaseModelUpdateTask {
    private final UserHandle mUser;

    public UserLockStateChangedTask(UserHandle user) {
        this.mUser = user;
    }

    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
        BgDataModel bgDataModel = dataModel;
        Context context = app.getContext();
        boolean isUserUnlocked = UserManagerCompat.getInstance(context).isUserUnlocked(this.mUser);
        DeepShortcutManager deepShortcutManager = DeepShortcutManager.getInstance(context);
        HashMap<ShortcutKey, ShortcutInfoCompat> pinnedShortcuts = new HashMap<>();
        if (isUserUnlocked) {
            List<ShortcutInfoCompat> shortcuts = deepShortcutManager.queryForPinnedShortcuts((String) null, this.mUser);
            if (deepShortcutManager.wasLastCallSuccess()) {
                for (ShortcutInfoCompat shortcut : shortcuts) {
                    pinnedShortcuts.put(ShortcutKey.fromInfo(shortcut), shortcut);
                }
            } else {
                isUserUnlocked = false;
            }
        }
        ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
        HashSet<ShortcutKey> removedKeys = new HashSet<>();
        Iterator<ItemInfo> it = bgDataModel.itemsIdMap.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = it.next();
            if (itemInfo.itemType == 6 && this.mUser.equals(itemInfo.user)) {
                ShortcutInfo si = (ShortcutInfo) itemInfo;
                if (isUserUnlocked) {
                    ShortcutKey key = ShortcutKey.fromItemInfo(si);
                    ShortcutInfoCompat shortcut2 = pinnedShortcuts.get(key);
                    if (shortcut2 == null) {
                        removedKeys.add(key);
                    } else {
                        si.runtimeStatusFlags &= -33;
                        si.updateFromDeepShortcutInfo(shortcut2, context);
                        LauncherIcons li = LauncherIcons.obtain(context);
                        li.createShortcutIcon(shortcut2, true, Provider.of(si.iconBitmap)).applyTo((ItemInfoWithIcon) si);
                        li.recycle();
                    }
                } else {
                    si.runtimeStatusFlags |= 32;
                }
                updatedShortcutInfos.add(si);
            }
        }
        bindUpdatedShortcuts(updatedShortcutInfos, this.mUser);
        if (!removedKeys.isEmpty()) {
            deleteAndBindComponentsRemoved(ItemInfoMatcher.ofShortcutKeys(removedKeys));
        }
        Iterator<ComponentKey> keysIter = bgDataModel.deepShortcutMap.keySet().iterator();
        while (keysIter.hasNext()) {
            if (keysIter.next().user.equals(this.mUser)) {
                keysIter.remove();
            }
        }
        if (isUserUnlocked) {
            bgDataModel.updateDeepShortcutMap((String) null, this.mUser, deepShortcutManager.queryForAllShortcuts(this.mUser));
        }
        bindDeepShortcuts(bgDataModel);
    }
}
