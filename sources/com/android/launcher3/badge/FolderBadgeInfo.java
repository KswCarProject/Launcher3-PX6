package com.android.launcher3.badge;

import com.android.launcher3.Utilities;
import com.android.launcher3.util.PackageUserKey;

public class FolderBadgeInfo extends BadgeInfo {
    private static final int MIN_COUNT = 0;
    private int mNumNotifications;

    public FolderBadgeInfo() {
        super((PackageUserKey) null);
    }

    public void addBadgeInfo(BadgeInfo badgeToAdd) {
        if (badgeToAdd != null) {
            this.mNumNotifications += badgeToAdd.getNotificationKeys().size();
            this.mNumNotifications = Utilities.boundToRange(this.mNumNotifications, 0, (int) BadgeInfo.MAX_COUNT);
        }
    }

    public void subtractBadgeInfo(BadgeInfo badgeToSubtract) {
        if (badgeToSubtract != null) {
            this.mNumNotifications -= badgeToSubtract.getNotificationKeys().size();
            this.mNumNotifications = Utilities.boundToRange(this.mNumNotifications, 0, (int) BadgeInfo.MAX_COUNT);
        }
    }

    public int getNotificationCount() {
        return 0;
    }

    public boolean hasBadge() {
        return this.mNumNotifications > 0;
    }
}
