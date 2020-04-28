package com.android.launcher3.popup;

import android.content.ComponentName;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.WidgetListRowEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PopupDataProvider implements NotificationListener.NotificationsChangedListener {
    private static final boolean LOGD = false;
    private static final SystemShortcut[] SYSTEM_SHORTCUTS = {new SystemShortcut.AppInfo(), new SystemShortcut.Widgets(), new SystemShortcut.Install()};
    private static final String TAG = "PopupDataProvider";
    private ArrayList<WidgetListRowEntry> mAllWidgets = new ArrayList<>();
    private MultiHashMap<ComponentKey, String> mDeepShortcutMap = new MultiHashMap<>();
    private final Launcher mLauncher;
    private Map<PackageUserKey, BadgeInfo> mPackageUserToBadgeInfos = new HashMap();

    public PopupDataProvider(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public void onNotificationPosted(PackageUserKey postedPackageUserKey, NotificationKeyData notificationKey, boolean shouldBeFilteredOut) {
        boolean badgeShouldBeRefreshed;
        BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(postedPackageUserKey);
        if (badgeInfo != null) {
            if (shouldBeFilteredOut) {
                badgeShouldBeRefreshed = badgeInfo.removeNotificationKey(notificationKey);
            } else {
                badgeShouldBeRefreshed = badgeInfo.addOrUpdateNotificationKey(notificationKey);
            }
            if (badgeInfo.getNotificationKeys().size() == 0) {
                this.mPackageUserToBadgeInfos.remove(postedPackageUserKey);
            }
        } else if (!shouldBeFilteredOut) {
            BadgeInfo newBadgeInfo = new BadgeInfo(postedPackageUserKey);
            newBadgeInfo.addOrUpdateNotificationKey(notificationKey);
            this.mPackageUserToBadgeInfos.put(postedPackageUserKey, newBadgeInfo);
            badgeShouldBeRefreshed = true;
        } else {
            badgeShouldBeRefreshed = false;
        }
        if (badgeShouldBeRefreshed) {
            this.mLauncher.updateIconBadges(Utilities.singletonHashSet(postedPackageUserKey));
        }
    }

    public void onNotificationRemoved(PackageUserKey removedPackageUserKey, NotificationKeyData notificationKey) {
        BadgeInfo oldBadgeInfo = this.mPackageUserToBadgeInfos.get(removedPackageUserKey);
        if (oldBadgeInfo != null && oldBadgeInfo.removeNotificationKey(notificationKey)) {
            if (oldBadgeInfo.getNotificationKeys().size() == 0) {
                this.mPackageUserToBadgeInfos.remove(removedPackageUserKey);
            }
            this.mLauncher.updateIconBadges(Utilities.singletonHashSet(removedPackageUserKey));
            trimNotifications(this.mPackageUserToBadgeInfos);
        }
    }

    public void onNotificationFullRefresh(List<StatusBarNotification> activeNotifications) {
        if (activeNotifications != null) {
            HashMap<PackageUserKey, BadgeInfo> updatedBadges = new HashMap<>(this.mPackageUserToBadgeInfos);
            this.mPackageUserToBadgeInfos.clear();
            for (StatusBarNotification notification : activeNotifications) {
                PackageUserKey packageUserKey = PackageUserKey.fromNotification(notification);
                BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(packageUserKey);
                if (badgeInfo == null) {
                    badgeInfo = new BadgeInfo(packageUserKey);
                    this.mPackageUserToBadgeInfos.put(packageUserKey, badgeInfo);
                }
                badgeInfo.addOrUpdateNotificationKey(NotificationKeyData.fromNotification(notification));
            }
            for (PackageUserKey packageUserKey2 : this.mPackageUserToBadgeInfos.keySet()) {
                BadgeInfo prevBadge = updatedBadges.get(packageUserKey2);
                BadgeInfo newBadge = this.mPackageUserToBadgeInfos.get(packageUserKey2);
                if (prevBadge == null) {
                    updatedBadges.put(packageUserKey2, newBadge);
                } else if (!prevBadge.shouldBeInvalidated(newBadge)) {
                    updatedBadges.remove(packageUserKey2);
                }
            }
            if (!updatedBadges.isEmpty()) {
                this.mLauncher.updateIconBadges(updatedBadges.keySet());
            }
            trimNotifications(updatedBadges);
        }
    }

    private void trimNotifications(Map<PackageUserKey, BadgeInfo> updatedBadges) {
        PopupContainerWithArrow openContainer = PopupContainerWithArrow.getOpen(this.mLauncher);
        if (openContainer != null) {
            openContainer.trimNotifications(updatedBadges);
        }
    }

    public void setDeepShortcutMap(MultiHashMap<ComponentKey, String> deepShortcutMapCopy) {
        this.mDeepShortcutMap = deepShortcutMapCopy;
    }

    public List<String> getShortcutIdsForItem(ItemInfo info) {
        if (!DeepShortcutManager.supportsShortcuts(info)) {
            return Collections.EMPTY_LIST;
        }
        ComponentName component = info.getTargetComponent();
        if (component == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> ids = (List) this.mDeepShortcutMap.get(new ComponentKey(component, info.user));
        return ids == null ? Collections.EMPTY_LIST : ids;
    }

    public BadgeInfo getBadgeInfoForItem(ItemInfo info) {
        if (!DeepShortcutManager.supportsShortcuts(info)) {
            return null;
        }
        return this.mPackageUserToBadgeInfos.get(PackageUserKey.fromItemInfo(info));
    }

    @NonNull
    public List<NotificationKeyData> getNotificationKeysForItem(ItemInfo info) {
        BadgeInfo badgeInfo = getBadgeInfoForItem(info);
        return badgeInfo == null ? Collections.EMPTY_LIST : badgeInfo.getNotificationKeys();
    }

    @NonNull
    public List<StatusBarNotification> getStatusBarNotificationsForKeys(List<NotificationKeyData> notificationKeys) {
        NotificationListener notificationListener = NotificationListener.getInstanceIfConnected();
        if (notificationListener == null) {
            return Collections.EMPTY_LIST;
        }
        return notificationListener.getNotificationsForKeys(notificationKeys);
    }

    @NonNull
    public List<SystemShortcut> getEnabledSystemShortcutsForItem(ItemInfo info) {
        List<SystemShortcut> systemShortcuts = new ArrayList<>();
        for (SystemShortcut systemShortcut : SYSTEM_SHORTCUTS) {
            if (systemShortcut.getOnClickListener(this.mLauncher, info) != null) {
                systemShortcuts.add(systemShortcut);
            }
        }
        return systemShortcuts;
    }

    public void cancelNotification(String notificationKey) {
        NotificationListener notificationListener = NotificationListener.getInstanceIfConnected();
        if (notificationListener != null) {
            notificationListener.cancelNotificationFromLauncher(notificationKey);
        }
    }

    public void setAllWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        this.mAllWidgets = allWidgets;
    }

    public ArrayList<WidgetListRowEntry> getAllWidgets() {
        return this.mAllWidgets;
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        Iterator<WidgetListRowEntry> it = this.mAllWidgets.iterator();
        while (it.hasNext()) {
            WidgetListRowEntry entry = it.next();
            if (entry.pkgItem.packageName.equals(packageUserKey.mPackageName)) {
                ArrayList<WidgetItem> widgets = new ArrayList<>(entry.widgets);
                Iterator<WidgetItem> iterator = widgets.iterator();
                while (iterator.hasNext()) {
                    if (!iterator.next().user.equals(packageUserKey.mUser)) {
                        iterator.remove();
                    }
                }
                if (widgets.isEmpty()) {
                    return null;
                }
                return widgets;
            }
        }
        return null;
    }
}
