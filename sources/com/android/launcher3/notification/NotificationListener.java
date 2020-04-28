package com.android.launcher3.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.SettingsActivity;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.SettingsObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TargetApi(26)
public class NotificationListener extends NotificationListenerService {
    private static final int MSG_NOTIFICATION_FULL_REFRESH = 3;
    private static final int MSG_NOTIFICATION_POSTED = 1;
    private static final int MSG_NOTIFICATION_REMOVED = 2;
    public static final String TAG = "NotificationListener";
    /* access modifiers changed from: private */
    public static boolean sIsConnected;
    private static boolean sIsCreated;
    private static NotificationListener sNotificationListenerInstance = null;
    /* access modifiers changed from: private */
    public static NotificationsChangedListener sNotificationsChangedListener;
    private static StatusBarNotificationsChangedListener sStatusBarNotificationsChangedListener;
    private String mLastKeyDismissedByLauncher;
    private SettingsObserver mNotificationBadgingObserver;
    private final Map<String, String> mNotificationGroupKeyMap = new HashMap();
    private final Map<String, NotificationGroup> mNotificationGroupMap = new HashMap();
    private final NotificationListenerService.Ranking mTempRanking = new NotificationListenerService.Ranking();
    private final Handler.Callback mUiCallback = new Handler.Callback() {
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (NotificationListener.sNotificationsChangedListener == null) {
                        return true;
                    }
                    NotificationPostedMsg msg = (NotificationPostedMsg) message.obj;
                    NotificationListener.sNotificationsChangedListener.onNotificationPosted(msg.packageUserKey, msg.notificationKey, msg.shouldBeFilteredOut);
                    return true;
                case 2:
                    if (NotificationListener.sNotificationsChangedListener == null) {
                        return true;
                    }
                    Pair<PackageUserKey, NotificationKeyData> pair = (Pair) message.obj;
                    NotificationListener.sNotificationsChangedListener.onNotificationRemoved((PackageUserKey) pair.first, (NotificationKeyData) pair.second);
                    return true;
                case 3:
                    if (NotificationListener.sNotificationsChangedListener == null) {
                        return true;
                    }
                    NotificationListener.sNotificationsChangedListener.onNotificationFullRefresh((List) message.obj);
                    return true;
                default:
                    return true;
            }
        }
    };
    /* access modifiers changed from: private */
    public final Handler mUiHandler = new Handler(Looper.getMainLooper(), this.mUiCallback);
    private final Handler.Callback mWorkerCallback = new Handler.Callback() {
        public boolean handleMessage(Message message) {
            List<StatusBarNotification> activeNotifications;
            switch (message.what) {
                case 1:
                    NotificationListener.this.mUiHandler.obtainMessage(message.what, message.obj).sendToTarget();
                    return true;
                case 2:
                    NotificationListener.this.mUiHandler.obtainMessage(message.what, message.obj).sendToTarget();
                    return true;
                case 3:
                    if (NotificationListener.sIsConnected) {
                        try {
                            activeNotifications = NotificationListener.this.filterNotifications(NotificationListener.this.getActiveNotifications());
                        } catch (SecurityException e) {
                            Log.e(NotificationListener.TAG, "SecurityException: failed to fetch notifications");
                            activeNotifications = new ArrayList<>();
                        }
                    } else {
                        activeNotifications = new ArrayList<>();
                    }
                    NotificationListener.this.mUiHandler.obtainMessage(message.what, activeNotifications).sendToTarget();
                    return true;
                default:
                    return true;
            }
        }
    };
    private final Handler mWorkerHandler = new Handler(LauncherModel.getWorkerLooper(), this.mWorkerCallback);

    public interface NotificationsChangedListener {
        void onNotificationFullRefresh(List<StatusBarNotification> list);

        void onNotificationPosted(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData, boolean z);

        void onNotificationRemoved(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData);
    }

    public interface StatusBarNotificationsChangedListener {
        void onNotificationPosted(StatusBarNotification statusBarNotification);

        void onNotificationRemoved(StatusBarNotification statusBarNotification);
    }

    public NotificationListener() {
        sNotificationListenerInstance = this;
    }

    public void onCreate() {
        super.onCreate();
        sIsCreated = true;
    }

    public void onDestroy() {
        super.onDestroy();
        sIsCreated = false;
    }

    @Nullable
    public static NotificationListener getInstanceIfConnected() {
        if (sIsConnected) {
            return sNotificationListenerInstance;
        }
        return null;
    }

    public static void setNotificationsChangedListener(NotificationsChangedListener listener) {
        sNotificationsChangedListener = listener;
        NotificationListener notificationListener = getInstanceIfConnected();
        if (notificationListener != null) {
            notificationListener.onNotificationFullRefresh();
        } else if (!sIsCreated && sNotificationsChangedListener != null) {
            sNotificationsChangedListener.onNotificationFullRefresh(Collections.emptyList());
        }
    }

    public static void setStatusBarNotificationsChangedListener(StatusBarNotificationsChangedListener listener) {
        sStatusBarNotificationsChangedListener = listener;
    }

    public static void removeNotificationsChangedListener() {
        sNotificationsChangedListener = null;
    }

    public static void removeStatusBarNotificationsChangedListener() {
        sStatusBarNotificationsChangedListener = null;
    }

    public void onListenerConnected() {
        super.onListenerConnected();
        sIsConnected = true;
        this.mNotificationBadgingObserver = new SettingsObserver.Secure(getContentResolver()) {
            public void onSettingChanged(boolean isNotificationBadgingEnabled) {
                if (!isNotificationBadgingEnabled) {
                    NotificationListener.this.requestUnbind();
                }
            }
        };
        this.mNotificationBadgingObserver.register(SettingsActivity.NOTIFICATION_BADGING, new String[0]);
        onNotificationFullRefresh();
    }

    private void onNotificationFullRefresh() {
        this.mWorkerHandler.obtainMessage(3).sendToTarget();
    }

    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        sIsConnected = false;
        this.mNotificationBadgingObserver.unregister();
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn != null) {
            this.mWorkerHandler.obtainMessage(1, new NotificationPostedMsg(sbn)).sendToTarget();
            if (sStatusBarNotificationsChangedListener != null) {
                sStatusBarNotificationsChangedListener.onNotificationPosted(sbn);
            }
        }
    }

    private class NotificationPostedMsg {
        final NotificationKeyData notificationKey;
        final PackageUserKey packageUserKey;
        final boolean shouldBeFilteredOut;

        NotificationPostedMsg(StatusBarNotification sbn) {
            this.packageUserKey = PackageUserKey.fromNotification(sbn);
            this.notificationKey = NotificationKeyData.fromNotification(sbn);
            this.shouldBeFilteredOut = NotificationListener.this.shouldBeFilteredOut(sbn);
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (sbn != null) {
            this.mWorkerHandler.obtainMessage(2, new Pair<>(PackageUserKey.fromNotification(sbn), NotificationKeyData.fromNotification(sbn))).sendToTarget();
            if (sStatusBarNotificationsChangedListener != null) {
                sStatusBarNotificationsChangedListener.onNotificationRemoved(sbn);
            }
            NotificationGroup notificationGroup = this.mNotificationGroupMap.get(sbn.getGroupKey());
            String key = sbn.getKey();
            if (notificationGroup != null) {
                notificationGroup.removeChildKey(key);
                if (notificationGroup.isEmpty()) {
                    if (key.equals(this.mLastKeyDismissedByLauncher)) {
                        cancelNotification(notificationGroup.getGroupSummaryKey());
                    }
                    this.mNotificationGroupMap.remove(sbn.getGroupKey());
                }
            }
            if (key.equals(this.mLastKeyDismissedByLauncher)) {
                this.mLastKeyDismissedByLauncher = null;
            }
        }
    }

    public void cancelNotificationFromLauncher(String key) {
        this.mLastKeyDismissedByLauncher = key;
        cancelNotification(key);
    }

    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
        for (StatusBarNotification sbn : getActiveNotifications(rankingMap.getOrderedKeys())) {
            updateGroupKeyIfNecessary(sbn);
        }
    }

    private void updateGroupKeyIfNecessary(StatusBarNotification sbn) {
        String childKey = sbn.getKey();
        String oldGroupKey = this.mNotificationGroupKeyMap.get(childKey);
        String newGroupKey = sbn.getGroupKey();
        if (oldGroupKey == null || !oldGroupKey.equals(newGroupKey)) {
            this.mNotificationGroupKeyMap.put(childKey, newGroupKey);
            if (oldGroupKey != null && this.mNotificationGroupMap.containsKey(oldGroupKey)) {
                NotificationGroup oldGroup = this.mNotificationGroupMap.get(oldGroupKey);
                oldGroup.removeChildKey(childKey);
                if (oldGroup.isEmpty()) {
                    this.mNotificationGroupMap.remove(oldGroupKey);
                }
            }
        }
        if (sbn.isGroup() && newGroupKey != null) {
            NotificationGroup notificationGroup = this.mNotificationGroupMap.get(newGroupKey);
            if (notificationGroup == null) {
                notificationGroup = new NotificationGroup();
                this.mNotificationGroupMap.put(newGroupKey, notificationGroup);
            }
            if ((sbn.getNotification().flags & 512) != 0) {
                notificationGroup.setGroupSummaryKey(childKey);
            } else {
                notificationGroup.addChildKey(childKey);
            }
        }
    }

    public List<StatusBarNotification> getNotificationsForKeys(List<NotificationKeyData> keys) {
        StatusBarNotification[] notifications = getActiveNotifications((String[]) NotificationKeyData.extractKeysOnly(keys).toArray(new String[keys.size()]));
        return notifications == null ? Collections.emptyList() : Arrays.asList(notifications);
    }

    /* access modifiers changed from: private */
    public List<StatusBarNotification> filterNotifications(StatusBarNotification[] notifications) {
        if (notifications == null) {
            return null;
        }
        Set<Integer> removedNotifications = new ArraySet<>();
        for (int i = 0; i < notifications.length; i++) {
            if (shouldBeFilteredOut(notifications[i])) {
                removedNotifications.add(Integer.valueOf(i));
            }
        }
        List<StatusBarNotification> filteredNotifications = new ArrayList<>(notifications.length - removedNotifications.size());
        for (int i2 = 0; i2 < notifications.length; i2++) {
            if (!removedNotifications.contains(Integer.valueOf(i2))) {
                filteredNotifications.add(notifications[i2]);
            }
        }
        return filteredNotifications;
    }

    /* access modifiers changed from: private */
    public boolean shouldBeFilteredOut(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        updateGroupKeyIfNecessary(sbn);
        getCurrentRanking().getRanking(sbn.getKey(), this.mTempRanking);
        if (!this.mTempRanking.canShowBadge()) {
            return true;
        }
        if (this.mTempRanking.getChannel().getId().equals("miscellaneous") && (notification.flags & 2) != 0) {
            return true;
        }
        boolean missingTitleAndText = TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE)) && TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
        if (((notification.flags & 512) != 0) || missingTitleAndText) {
            return true;
        }
        return false;
    }
}
