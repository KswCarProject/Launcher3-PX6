package com.android.launcher3.badge;

import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;

public class BadgeInfo {
    public static final int MAX_COUNT = 999;
    private List<NotificationKeyData> mNotificationKeys = new ArrayList();
    private PackageUserKey mPackageUserKey;
    private int mTotalCount;

    public BadgeInfo(PackageUserKey packageUserKey) {
        this.mPackageUserKey = packageUserKey;
    }

    public boolean addOrUpdateNotificationKey(NotificationKeyData notificationKey) {
        NotificationKeyData prevKey;
        int indexOfPrevKey = this.mNotificationKeys.indexOf(notificationKey);
        if (indexOfPrevKey == -1) {
            prevKey = null;
        } else {
            prevKey = this.mNotificationKeys.get(indexOfPrevKey);
        }
        if (prevKey == null) {
            boolean added = this.mNotificationKeys.add(notificationKey);
            if (added) {
                this.mTotalCount += notificationKey.count;
            }
            return added;
        } else if (prevKey.count == notificationKey.count) {
            return false;
        } else {
            this.mTotalCount -= prevKey.count;
            this.mTotalCount += notificationKey.count;
            prevKey.count = notificationKey.count;
            return true;
        }
    }

    public boolean removeNotificationKey(NotificationKeyData notificationKey) {
        boolean removed = this.mNotificationKeys.remove(notificationKey);
        if (removed) {
            this.mTotalCount -= notificationKey.count;
        }
        return removed;
    }

    public List<NotificationKeyData> getNotificationKeys() {
        return this.mNotificationKeys;
    }

    public int getNotificationCount() {
        return Math.min(this.mTotalCount, MAX_COUNT);
    }

    public boolean shouldBeInvalidated(BadgeInfo newBadge) {
        return this.mPackageUserKey.equals(newBadge.mPackageUserKey) && getNotificationCount() != newBadge.getNotificationCount();
    }
}
