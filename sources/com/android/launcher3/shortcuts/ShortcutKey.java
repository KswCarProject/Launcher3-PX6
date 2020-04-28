package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Intent;
import android.os.UserHandle;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.util.ComponentKey;

public class ShortcutKey extends ComponentKey {
    public ShortcutKey(String packageName, UserHandle user, String id) {
        super(new ComponentName(packageName, id), user);
    }

    public ShortcutKey(ComponentName componentName, UserHandle user) {
        super(componentName, user);
    }

    public String getId() {
        return this.componentName.getClassName();
    }

    public static ShortcutKey fromInfo(ShortcutInfoCompat shortcutInfo) {
        return new ShortcutKey(shortcutInfo.getPackage(), shortcutInfo.getUserHandle(), shortcutInfo.getId());
    }

    public static ShortcutKey fromIntent(Intent intent, UserHandle user) {
        return new ShortcutKey(intent.getPackage(), user, intent.getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID));
    }

    public static ShortcutKey fromItemInfo(ItemInfo info) {
        return fromIntent(info.getIntent(), info.user);
    }
}
