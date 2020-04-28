package com.android.launcher3.model;

import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.Provider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ShortcutsChangedTask extends BaseModelUpdateTask {
    private final String mPackageName;
    private final List<ShortcutInfoCompat> mShortcuts;
    private final boolean mUpdateIdMap;
    private final UserHandle mUser;

    public ShortcutsChangedTask(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user, boolean updateIdMap) {
        this.mPackageName = packageName;
        this.mShortcuts = shortcuts;
        this.mUser = user;
        this.mUpdateIdMap = updateIdMap;
    }

    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
        BgDataModel bgDataModel = dataModel;
        Context context = app.getContext();
        DeepShortcutManager deepShortcutManager = DeepShortcutManager.getInstance(context);
        deepShortcutManager.onShortcutsChanged(this.mShortcuts);
        HashSet<ShortcutKey> removedKeys = new HashSet<>();
        MultiHashMap<ShortcutKey, ShortcutInfo> keyToShortcutInfo = new MultiHashMap<>();
        HashSet<String> allIds = new HashSet<>();
        Iterator<ItemInfo> it = bgDataModel.itemsIdMap.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = it.next();
            if (itemInfo.itemType == 6) {
                ShortcutInfo si = (ShortcutInfo) itemInfo;
                if (si.getIntent().getPackage().equals(this.mPackageName) && si.user.equals(this.mUser)) {
                    keyToShortcutInfo.addToList(ShortcutKey.fromItemInfo(si), si);
                    allIds.add(si.getDeepShortcutId());
                }
            }
        }
        ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
        if (!keyToShortcutInfo.isEmpty()) {
            for (ShortcutInfoCompat fullDetails : deepShortcutManager.queryForFullDetails(this.mPackageName, new ArrayList(allIds), this.mUser)) {
                ShortcutKey key = ShortcutKey.fromInfo(fullDetails);
                List<ShortcutInfo> shortcutInfos = (List) keyToShortcutInfo.remove(key);
                if (!fullDetails.isPinned()) {
                    removedKeys.add(key);
                } else {
                    for (ShortcutInfo shortcutInfo : shortcutInfos) {
                        shortcutInfo.updateFromDeepShortcutInfo(fullDetails, context);
                        LauncherIcons li = LauncherIcons.obtain(context);
                        li.createShortcutIcon(fullDetails, true, Provider.of(shortcutInfo.iconBitmap)).applyTo((ItemInfoWithIcon) shortcutInfo);
                        li.recycle();
                        updatedShortcutInfos.add(shortcutInfo);
                        context = context;
                        deepShortcutManager = deepShortcutManager;
                    }
                    DeepShortcutManager deepShortcutManager2 = deepShortcutManager;
                }
            }
        }
        DeepShortcutManager deepShortcutManager3 = deepShortcutManager;
        removedKeys.addAll(keyToShortcutInfo.keySet());
        bindUpdatedShortcuts(updatedShortcutInfos, this.mUser);
        if (!keyToShortcutInfo.isEmpty()) {
            deleteAndBindComponentsRemoved(ItemInfoMatcher.ofShortcutKeys(removedKeys));
        }
        if (this.mUpdateIdMap) {
            bgDataModel.updateDeepShortcutMap(this.mPackageName, this.mUser, this.mShortcuts);
            bindDeepShortcuts(bgDataModel);
        }
    }
}
