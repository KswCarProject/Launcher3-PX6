package com.android.launcher3.widget;

import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;

public class PendingAddShortcutInfo extends PendingAddItemInfo {
    public ShortcutConfigActivityInfo activityInfo;

    public PendingAddShortcutInfo(ShortcutConfigActivityInfo activityInfo2) {
        this.activityInfo = activityInfo2;
        this.componentName = activityInfo2.getComponent();
        this.user = activityInfo2.getUser();
        this.itemType = activityInfo2.getItemType();
    }
}
