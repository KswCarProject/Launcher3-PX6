package com.szchoiceway.index;

import android.content.pm.ActivityInfo;

/* compiled from: PendingAddItemInfo */
class PendingAddShortcutInfo extends PendingAddItemInfo {
    ActivityInfo shortcutActivityInfo;

    public PendingAddShortcutInfo(ActivityInfo activityInfo) {
        this.shortcutActivityInfo = activityInfo;
    }

    public String toString() {
        return "Shortcut: " + this.shortcutActivityInfo.packageName;
    }
}
