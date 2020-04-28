package com.android.launcher3.popup;

import android.content.ComponentName;
import android.os.Handler;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PopupPopulator {
    public static final int MAX_SHORTCUTS = 4;
    public static final int MAX_SHORTCUTS_IF_NOTIFICATIONS = 2;
    @VisibleForTesting
    static final int NUM_DYNAMIC = 2;
    private static final Comparator<ShortcutInfoCompat> SHORTCUT_RANK_COMPARATOR = new Comparator<ShortcutInfoCompat>() {
        public int compare(ShortcutInfoCompat a, ShortcutInfoCompat b) {
            if (a.isDeclaredInManifest() && !b.isDeclaredInManifest()) {
                return -1;
            }
            if (a.isDeclaredInManifest() || !b.isDeclaredInManifest()) {
                return Integer.compare(a.getRank(), b.getRank());
            }
            return 1;
        }
    };

    public static List<ShortcutInfoCompat> sortAndFilterShortcuts(List<ShortcutInfoCompat> shortcuts, @Nullable String shortcutIdToRemoveFirst) {
        if (shortcutIdToRemoveFirst != null) {
            Iterator<ShortcutInfoCompat> shortcutIterator = shortcuts.iterator();
            while (true) {
                if (shortcutIterator.hasNext()) {
                    if (shortcutIterator.next().getId().equals(shortcutIdToRemoveFirst)) {
                        shortcutIterator.remove();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        Collections.sort(shortcuts, SHORTCUT_RANK_COMPARATOR);
        if (shortcuts.size() <= 4) {
            return shortcuts;
        }
        List<ShortcutInfoCompat> filteredShortcuts = new ArrayList<>(4);
        int numDynamic = 0;
        int size = shortcuts.size();
        for (int i = 0; i < size; i++) {
            ShortcutInfoCompat shortcut = shortcuts.get(i);
            int filteredSize = filteredShortcuts.size();
            if (filteredSize < 4) {
                filteredShortcuts.add(shortcut);
                if (shortcut.isDynamic()) {
                    numDynamic++;
                }
            } else if (shortcut.isDynamic() && numDynamic < 2) {
                numDynamic++;
                filteredShortcuts.remove(filteredSize - numDynamic);
                filteredShortcuts.add(shortcut);
            }
        }
        return filteredShortcuts;
    }

    public static Runnable createUpdateRunnable(Launcher launcher, ItemInfo originalInfo, Handler uiHandler, PopupContainerWithArrow container, List<String> shortcutIds, List<DeepShortcutView> shortcutViews, List<NotificationKeyData> notificationKeys) {
        return new Runnable(notificationKeys, launcher, uiHandler, container, originalInfo.getTargetComponent(), shortcutIds, originalInfo.user, shortcutViews, originalInfo) {
            private final /* synthetic */ List f$0;
            private final /* synthetic */ Launcher f$1;
            private final /* synthetic */ Handler f$2;
            private final /* synthetic */ PopupContainerWithArrow f$3;
            private final /* synthetic */ ComponentName f$4;
            private final /* synthetic */ List f$5;
            private final /* synthetic */ UserHandle f$6;
            private final /* synthetic */ List f$7;
            private final /* synthetic */ ItemInfo f$8;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                PopupPopulator.lambda$createUpdateRunnable$3(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        };
    }

    static /* synthetic */ void lambda$createUpdateRunnable$3(List notificationKeys, Launcher launcher, Handler uiHandler, PopupContainerWithArrow container, ComponentName activity, List shortcutIds, UserHandle user, List shortcutViews, ItemInfo originalInfo) {
        String shortcutIdToDeDupe;
        List list = notificationKeys;
        Launcher launcher2 = launcher;
        Handler handler = uiHandler;
        PopupContainerWithArrow popupContainerWithArrow = container;
        boolean z = false;
        if (!notificationKeys.isEmpty()) {
            List<StatusBarNotification> notifications = launcher.getPopupDataProvider().getStatusBarNotificationsForKeys(list);
            List<NotificationInfo> infos = new ArrayList<>(notifications.size());
            for (int i = 0; i < notifications.size(); i++) {
                infos.add(new NotificationInfo(launcher2, notifications.get(i)));
            }
            handler.post(new Runnable(infos) {
                private final /* synthetic */ List f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PopupContainerWithArrow.this.applyNotificationInfos(this.f$1);
                }
            });
        }
        List<ShortcutInfoCompat> shortcuts = DeepShortcutManager.getInstance(launcher).queryForShortcutsContainer(activity, shortcutIds, user);
        if (notificationKeys.isEmpty()) {
            shortcutIdToDeDupe = null;
        } else {
            shortcutIdToDeDupe = ((NotificationKeyData) list.get(0)).shortcutId;
        }
        List<ShortcutInfoCompat> shortcuts2 = sortAndFilterShortcuts(shortcuts, shortcutIdToDeDupe);
        int i2 = 0;
        while (i2 < shortcuts2.size() && i2 < shortcutViews.size()) {
            ShortcutInfoCompat shortcut = shortcuts2.get(i2);
            ShortcutInfo si = new ShortcutInfo(shortcut, launcher2);
            LauncherIcons li = LauncherIcons.obtain(launcher);
            li.createShortcutIcon(shortcut, z).applyTo((ItemInfoWithIcon) si);
            li.recycle();
            si.rank = i2;
            handler.post(new Runnable(si, shortcut, popupContainerWithArrow) {
                private final /* synthetic */ ShortcutInfo f$1;
                private final /* synthetic */ ShortcutInfoCompat f$2;
                private final /* synthetic */ PopupContainerWithArrow f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    DeepShortcutView.this.applyShortcutInfo(this.f$1, this.f$2, this.f$3);
                }
            });
            i2++;
            z = false;
        }
        List list2 = shortcutViews;
        handler.post(new Runnable(originalInfo) {
            private final /* synthetic */ ItemInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Launcher.this.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromItemInfo(this.f$1));
            }
        });
    }
}
