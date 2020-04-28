package com.android.launcher3.util;

import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import java.util.Arrays;

public class PackageUserKey {
    private int mHashCode;
    public String mPackageName;
    public UserHandle mUser;

    public static PackageUserKey fromItemInfo(ItemInfo info) {
        return new PackageUserKey(info.getTargetComponent().getPackageName(), info.user);
    }

    public static PackageUserKey fromNotification(StatusBarNotification notification) {
        return new PackageUserKey(notification.getPackageName(), notification.getUser());
    }

    public PackageUserKey(String packageName, UserHandle user) {
        update(packageName, user);
    }

    private void update(String packageName, UserHandle user) {
        this.mPackageName = packageName;
        this.mUser = user;
        this.mHashCode = Arrays.hashCode(new Object[]{packageName, user});
    }

    public boolean updateFromItemInfo(ItemInfo info) {
        if (!DeepShortcutManager.supportsShortcuts(info)) {
            return false;
        }
        update(info.getTargetComponent().getPackageName(), info.user);
        return true;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PackageUserKey)) {
            return false;
        }
        PackageUserKey otherKey = (PackageUserKey) obj;
        if (!this.mPackageName.equals(otherKey.mPackageName) || !this.mUser.equals(otherKey.mUser)) {
            return false;
        }
        return true;
    }
}
