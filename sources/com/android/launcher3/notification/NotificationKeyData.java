package com.android.launcher3.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class NotificationKeyData {
    public int count;
    public final String notificationKey;
    public final String shortcutId;

    private NotificationKeyData(String notificationKey2, String shortcutId2, int count2) {
        this.notificationKey = notificationKey2;
        this.shortcutId = shortcutId2;
        this.count = Math.max(1, count2);
    }

    public static NotificationKeyData fromNotification(StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        return new NotificationKeyData(sbn.getKey(), notif.getShortcutId(), notif.number);
    }

    public static List<String> extractKeysOnly(@NonNull List<NotificationKeyData> notificationKeys) {
        List<String> keysOnly = new ArrayList<>(notificationKeys.size());
        for (NotificationKeyData notificationKeyData : notificationKeys) {
            keysOnly.add(notificationKeyData.notificationKey);
        }
        return keysOnly;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationKeyData)) {
            return false;
        }
        return ((NotificationKeyData) obj).notificationKey.equals(this.notificationKey);
    }
}
