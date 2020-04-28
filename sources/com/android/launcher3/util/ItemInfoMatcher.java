package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.util.HashSet;

public abstract class ItemInfoMatcher {
    public abstract boolean matches(ItemInfo itemInfo, ComponentName componentName);

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005a, code lost:
        r3 = (com.android.launcher3.LauncherAppWidgetInfo) r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.util.HashSet<com.android.launcher3.ItemInfo> filterItemInfos(java.lang.Iterable<com.android.launcher3.ItemInfo> r9) {
        /*
            r8 = this;
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r1 = r9.iterator()
        L_0x0009:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x006b
            java.lang.Object r2 = r1.next()
            com.android.launcher3.ItemInfo r2 = (com.android.launcher3.ItemInfo) r2
            boolean r3 = r2 instanceof com.android.launcher3.ShortcutInfo
            if (r3 == 0) goto L_0x002c
            r3 = r2
            com.android.launcher3.ShortcutInfo r3 = (com.android.launcher3.ShortcutInfo) r3
            android.content.ComponentName r4 = r3.getTargetComponent()
            if (r4 == 0) goto L_0x002b
            boolean r5 = r8.matches(r3, r4)
            if (r5 == 0) goto L_0x002b
            r0.add(r3)
        L_0x002b:
            goto L_0x006a
        L_0x002c:
            boolean r3 = r2 instanceof com.android.launcher3.FolderInfo
            if (r3 == 0) goto L_0x0056
            r3 = r2
            com.android.launcher3.FolderInfo r3 = (com.android.launcher3.FolderInfo) r3
            java.util.ArrayList<com.android.launcher3.ShortcutInfo> r4 = r3.contents
            java.util.Iterator r4 = r4.iterator()
        L_0x0039:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0055
            java.lang.Object r5 = r4.next()
            com.android.launcher3.ShortcutInfo r5 = (com.android.launcher3.ShortcutInfo) r5
            android.content.ComponentName r6 = r5.getTargetComponent()
            if (r6 == 0) goto L_0x0054
            boolean r7 = r8.matches(r5, r6)
            if (r7 == 0) goto L_0x0054
            r0.add(r5)
        L_0x0054:
            goto L_0x0039
        L_0x0055:
            goto L_0x006a
        L_0x0056:
            boolean r3 = r2 instanceof com.android.launcher3.LauncherAppWidgetInfo
            if (r3 == 0) goto L_0x006a
            r3 = r2
            com.android.launcher3.LauncherAppWidgetInfo r3 = (com.android.launcher3.LauncherAppWidgetInfo) r3
            android.content.ComponentName r4 = r3.providerName
            if (r4 == 0) goto L_0x006a
            boolean r5 = r8.matches(r3, r4)
            if (r5 == 0) goto L_0x006a
            r0.add(r3)
        L_0x006a:
            goto L_0x0009
        L_0x006b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.ItemInfoMatcher.filterItemInfos(java.lang.Iterable):java.util.HashSet");
    }

    public ItemInfoMatcher or(final ItemInfoMatcher matcher) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return this.matches(info, cn) || matcher.matches(info, cn);
            }
        };
    }

    public ItemInfoMatcher and(final ItemInfoMatcher matcher) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return this.matches(info, cn) && matcher.matches(info, cn);
            }
        };
    }

    public static ItemInfoMatcher not(ItemInfoMatcher matcher) {
        return new ItemInfoMatcher(matcher) {
            final /* synthetic */ ItemInfoMatcher val$matcher;

            {
                this.val$matcher = r1;
            }

            public boolean matches(ItemInfo info, ComponentName cn) {
                return !this.val$matcher.matches(info, cn);
            }
        };
    }

    public static ItemInfoMatcher ofUser(final UserHandle user) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return info.user.equals(user);
            }
        };
    }

    public static ItemInfoMatcher ofComponents(final HashSet<ComponentName> components, final UserHandle user) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return components.contains(cn) && info.user.equals(user);
            }
        };
    }

    public static ItemInfoMatcher ofPackages(final HashSet<String> packageNames, final UserHandle user) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return packageNames.contains(cn.getPackageName()) && info.user.equals(user);
            }
        };
    }

    public static ItemInfoMatcher ofShortcutKeys(final HashSet<ShortcutKey> keys) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return info.itemType == 6 && keys.contains(ShortcutKey.fromItemInfo(info));
            }
        };
    }

    public static ItemInfoMatcher ofItemIds(final LongArrayMap<Boolean> ids, final Boolean matchDefault) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo info, ComponentName cn) {
                return ((Boolean) ids.get(info.id, matchDefault)).booleanValue();
            }
        };
    }
}
