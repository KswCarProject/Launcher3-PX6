package com.android.launcher3.model;

import android.os.UserHandle;

public class PackageUpdatedTask extends BaseModelUpdateTask {
    private static final boolean DEBUG = false;
    public static final int OP_ADD = 1;
    public static final int OP_NONE = 0;
    public static final int OP_REMOVE = 3;
    public static final int OP_SUSPEND = 5;
    public static final int OP_UNAVAILABLE = 4;
    public static final int OP_UNSUSPEND = 6;
    public static final int OP_UPDATE = 2;
    public static final int OP_USER_AVAILABILITY_CHANGE = 7;
    private static final String TAG = "PackageUpdatedTask";
    private final int mOp;
    private final String[] mPackages;
    private final UserHandle mUser;

    public PackageUpdatedTask(int op, UserHandle user, String... packages) {
        this.mOp = op;
        this.mUser = user;
        this.mPackages = packages;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0452, code lost:
        bindUpdatedShortcuts(r6, r1.mUser);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x045b, code lost:
        if (r12.isEmpty() != false) goto L_0x046a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x045d, code lost:
        deleteAndBindComponentsRemoved(com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r12, false));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x046f, code lost:
        if (r7.isEmpty() != false) goto L_0x0479;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0471, code lost:
        scheduleCallbackTask(new com.android.launcher3.model.PackageUpdatedTask.AnonymousClass1(r1));
     */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0351 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x035e A[Catch:{ all -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01ed A[SYNTHETIC, Splitter:B:85:0x01ed] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(com.android.launcher3.LauncherAppState r38, com.android.launcher3.model.BgDataModel r39, com.android.launcher3.AllAppsList r40) {
        /*
            r37 = this;
            r1 = r37
            r2 = r39
            r3 = r40
            android.content.Context r4 = r38.getContext()
            com.android.launcher3.IconCache r5 = r38.getIconCache()
            java.lang.String[] r6 = r1.mPackages
            int r7 = r6.length
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
            java.util.HashSet r8 = new java.util.HashSet
            java.util.List r9 = java.util.Arrays.asList(r6)
            r8.<init>(r9)
            android.os.UserHandle r9 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r9 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r8, r9)
            int r10 = r1.mOp
            r11 = 4
            r12 = 2
            switch(r10) {
                case 1: goto L_0x00b2;
                case 2: goto L_0x008d;
                case 3: goto L_0x0062;
                case 4: goto L_0x006f;
                case 5: goto L_0x004f;
                case 6: goto L_0x004f;
                case 7: goto L_0x002d;
                default: goto L_0x0029;
            }
        L_0x0029:
            r10 = r9
            r9 = r0
            goto L_0x00e3
        L_0x002d:
            com.android.launcher3.compat.UserManagerCompat r10 = com.android.launcher3.compat.UserManagerCompat.getInstance(r4)
            android.os.UserHandle r14 = r1.mUser
            boolean r10 = r10.isQuietModeEnabled(r14)
            r14 = 8
            if (r10 == 0) goto L_0x0040
            com.android.launcher3.util.FlagOp r10 = com.android.launcher3.util.FlagOp.addFlag(r14)
            goto L_0x0044
        L_0x0040:
            com.android.launcher3.util.FlagOp r10 = com.android.launcher3.util.FlagOp.removeFlag(r14)
        L_0x0044:
            r0 = r10
            android.os.UserHandle r10 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r9 = com.android.launcher3.util.ItemInfoMatcher.ofUser(r10)
            r3.updateDisabledFlags(r9, r0)
            goto L_0x0029
        L_0x004f:
            int r10 = r1.mOp
            r14 = 5
            if (r10 != r14) goto L_0x0059
            com.android.launcher3.util.FlagOp r10 = com.android.launcher3.util.FlagOp.addFlag(r11)
            goto L_0x005d
        L_0x0059:
            com.android.launcher3.util.FlagOp r10 = com.android.launcher3.util.FlagOp.removeFlag(r11)
        L_0x005d:
            r0 = r10
            r3.updateDisabledFlags(r9, r0)
            goto L_0x0029
        L_0x0062:
            r10 = 0
        L_0x0063:
            if (r10 >= r7) goto L_0x006f
            r14 = r6[r10]
            android.os.UserHandle r15 = r1.mUser
            r5.removeIconsForPkg(r14, r15)
            int r10 = r10 + 1
            goto L_0x0063
        L_0x006f:
            r10 = 0
        L_0x0070:
            if (r10 >= r7) goto L_0x0088
            r14 = r6[r10]
            android.os.UserHandle r15 = r1.mUser
            r3.removePackage(r14, r15)
            com.android.launcher3.WidgetPreviewLoader r14 = r38.getWidgetCache()
            r15 = r6[r10]
            android.os.UserHandle r11 = r1.mUser
            r14.removePackage(r15, r11)
            int r10 = r10 + 1
            r11 = 4
            goto L_0x0070
        L_0x0088:
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.addFlag(r12)
            goto L_0x0029
        L_0x008d:
            r10 = 0
        L_0x008e:
            if (r10 >= r7) goto L_0x00ac
            r11 = r6[r10]
            android.os.UserHandle r14 = r1.mUser
            r5.updateIconsForPkg(r11, r14)
            r11 = r6[r10]
            android.os.UserHandle r14 = r1.mUser
            r3.updatePackage(r4, r11, r14)
            com.android.launcher3.WidgetPreviewLoader r11 = r38.getWidgetCache()
            r14 = r6[r10]
            android.os.UserHandle r15 = r1.mUser
            r11.removePackage(r14, r15)
            int r10 = r10 + 1
            goto L_0x008e
        L_0x00ac:
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.removeFlag(r12)
            goto L_0x0029
        L_0x00b2:
            r10 = 0
        L_0x00b3:
            if (r10 >= r7) goto L_0x00dd
            r11 = r6[r10]
            android.os.UserHandle r14 = r1.mUser
            r5.updateIconsForPkg(r11, r14)
            r11 = r6[r10]
            android.os.UserHandle r14 = r1.mUser
            r3.addPackage(r4, r11, r14)
            boolean r11 = com.android.launcher3.Utilities.ATLEAST_OREO
            if (r11 != 0) goto L_0x00da
            android.os.UserHandle r11 = android.os.Process.myUserHandle()
            android.os.UserHandle r14 = r1.mUser
            boolean r11 = r11.equals(r14)
            if (r11 != 0) goto L_0x00da
            r11 = r6[r10]
            android.os.UserHandle r14 = r1.mUser
            com.android.launcher3.SessionCommitReceiver.queueAppIconAddition(r4, r11, r14)
        L_0x00da:
            int r10 = r10 + 1
            goto L_0x00b3
        L_0x00dd:
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.removeFlag(r12)
            goto L_0x0029
        L_0x00e3:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            java.util.ArrayList<com.android.launcher3.AppInfo> r0 = r3.added
            r11.addAll(r0)
            java.util.ArrayList<com.android.launcher3.AppInfo> r0 = r3.added
            r0.clear()
            java.util.ArrayList<com.android.launcher3.AppInfo> r0 = r3.modified
            r11.addAll(r0)
            java.util.ArrayList<com.android.launcher3.AppInfo> r0 = r3.modified
            r0.clear()
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.ArrayList<com.android.launcher3.AppInfo> r14 = r3.removed
            r0.<init>(r14)
            r14 = r0
            java.util.ArrayList<com.android.launcher3.AppInfo> r0 = r3.removed
            r0.clear()
            android.util.ArrayMap r0 = new android.util.ArrayMap
            r0.<init>()
            r15 = r0
            boolean r0 = r11.isEmpty()
            if (r0 != 0) goto L_0x0138
            com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$yMYESDBbq0fzV9nU1ZnxQ9utjqg r0 = new com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$yMYESDBbq0fzV9nU1ZnxQ9utjqg
            r0.<init>(r11)
            r1.scheduleCallbackTask(r0)
            java.util.Iterator r0 = r11.iterator()
        L_0x0122:
            boolean r16 = r0.hasNext()
            if (r16 == 0) goto L_0x0138
            java.lang.Object r16 = r0.next()
            r13 = r16
            com.android.launcher3.AppInfo r13 = (com.android.launcher3.AppInfo) r13
            android.content.ComponentName r12 = r13.componentName
            r15.put(r12, r13)
            r12 = 2
            goto L_0x0122
        L_0x0138:
            com.android.launcher3.util.LongArrayMap r0 = new com.android.launcher3.util.LongArrayMap
            r0.<init>()
            r12 = r0
            int r0 = r1.mOp
            r13 = 1
            if (r0 == r13) goto L_0x0155
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
            if (r9 == r0) goto L_0x0148
            goto L_0x0155
        L_0x0148:
            r27 = r6
            r28 = r7
            r26 = r10
            r23 = r11
            r25 = r14
            r10 = 0
            goto L_0x0479
        L_0x0155:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r18 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r19 = r0
            int r0 = r1.mOp
            if (r0 == r13) goto L_0x016f
            int r0 = r1.mOp
            r13 = 2
            if (r0 != r13) goto L_0x016d
            goto L_0x016f
        L_0x016d:
            r0 = 0
            goto L_0x0170
        L_0x016f:
            r0 = 1
        L_0x0170:
            r13 = r0
            monitor-enter(r39)
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r0 = r2.itemsIdMap     // Catch:{ all -> 0x0556 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0556 }
        L_0x0178:
            boolean r16 = r0.hasNext()     // Catch:{ all -> 0x0556 }
            if (r16 == 0) goto L_0x0443
            java.lang.Object r16 = r0.next()     // Catch:{ all -> 0x042d }
            com.android.launcher3.ItemInfo r16 = (com.android.launcher3.ItemInfo) r16     // Catch:{ all -> 0x042d }
            r21 = r16
            r22 = r0
            r0 = r21
            boolean r3 = r0 instanceof com.android.launcher3.ShortcutInfo     // Catch:{ all -> 0x042d }
            if (r3 == 0) goto L_0x03b9
            android.os.UserHandle r3 = r1.mUser     // Catch:{ all -> 0x03a6 }
            r23 = r11
            android.os.UserHandle r11 = r0.user     // Catch:{ all -> 0x0395 }
            boolean r3 = r3.equals(r11)     // Catch:{ all -> 0x0395 }
            if (r3 == 0) goto L_0x038a
            r3 = r0
            com.android.launcher3.ShortcutInfo r3 = (com.android.launcher3.ShortcutInfo) r3     // Catch:{ all -> 0x0395 }
            r11 = 0
            r16 = 0
            r24 = r11
            android.content.Intent$ShortcutIconResource r11 = r3.iconResource     // Catch:{ all -> 0x0395 }
            if (r11 == 0) goto L_0x01e5
            android.content.Intent$ShortcutIconResource r11 = r3.iconResource     // Catch:{ all -> 0x01d7 }
            java.lang.String r11 = r11.packageName     // Catch:{ all -> 0x01d7 }
            boolean r11 = r8.contains(r11)     // Catch:{ all -> 0x01d7 }
            if (r11 == 0) goto L_0x01e5
            com.android.launcher3.graphics.LauncherIcons r11 = com.android.launcher3.graphics.LauncherIcons.obtain(r4)     // Catch:{ all -> 0x01d7 }
            r25 = r14
            android.content.Intent$ShortcutIconResource r14 = r3.iconResource     // Catch:{ all -> 0x01c8 }
            com.android.launcher3.graphics.BitmapInfo r14 = r11.createIconBitmap((android.content.Intent.ShortcutIconResource) r14)     // Catch:{ all -> 0x01c8 }
            r11.recycle()     // Catch:{ all -> 0x01c8 }
            if (r14 == 0) goto L_0x01e7
            r14.applyTo((com.android.launcher3.ItemInfoWithIcon) r3)     // Catch:{ all -> 0x01c8 }
            r11 = 1
            r24 = r11
            goto L_0x01e7
        L_0x01c8:
            r0 = move-exception
            r3 = r38
            r11 = r6
            r14 = r7
            r26 = r10
            r6 = r18
            r7 = r19
            r10 = r25
            goto L_0x0564
        L_0x01d7:
            r0 = move-exception
            r3 = r38
            r11 = r6
            r26 = r10
            r10 = r14
            r6 = r18
            r14 = r7
            r7 = r19
            goto L_0x0564
        L_0x01e5:
            r25 = r14
        L_0x01e7:
            android.content.ComponentName r11 = r3.getTargetComponent()     // Catch:{ all -> 0x0378 }
            if (r11 == 0) goto L_0x0349
            boolean r14 = r10.matches(r3, r11)     // Catch:{ all -> 0x033a }
            if (r14 == 0) goto L_0x0349
            java.lang.Object r14 = r15.get(r11)     // Catch:{ all -> 0x033a }
            com.android.launcher3.AppInfo r14 = (com.android.launcher3.AppInfo) r14     // Catch:{ all -> 0x033a }
            r26 = r10
            r10 = 16
            boolean r10 = r3.hasStatusFlag(r10)     // Catch:{ all -> 0x032d }
            if (r10 == 0) goto L_0x0221
            r27 = r6
            r28 = r7
            long r6 = r3.id     // Catch:{ all -> 0x021a }
            r29 = r14
            r10 = 0
            java.lang.Boolean r14 = java.lang.Boolean.valueOf(r10)     // Catch:{ all -> 0x021a }
            r12.put(r6, r14)     // Catch:{ all -> 0x021a }
            int r6 = r1.mOp     // Catch:{ all -> 0x021a }
            r7 = 3
            if (r6 != r7) goto L_0x0227
            goto L_0x0410
        L_0x021a:
            r0 = move-exception
            r3 = r38
            r6 = r18
            goto L_0x036e
        L_0x0221:
            r27 = r6
            r28 = r7
            r29 = r14
        L_0x0227:
            boolean r6 = r3.isPromise()     // Catch:{ all -> 0x021a }
            if (r6 == 0) goto L_0x030e
            if (r13 == 0) goto L_0x030e
            r6 = 1
            int r7 = r3.itemType     // Catch:{ all -> 0x021a }
            r10 = 6
            if (r7 != r10) goto L_0x026e
            com.android.launcher3.shortcuts.DeepShortcutManager r7 = com.android.launcher3.shortcuts.DeepShortcutManager.getInstance(r4)     // Catch:{ all -> 0x021a }
            java.lang.String r10 = r11.getPackageName()     // Catch:{ all -> 0x021a }
            r30 = r6
            r14 = 1
            java.lang.String[] r6 = new java.lang.String[r14]     // Catch:{ all -> 0x021a }
            java.lang.String r14 = r3.getDeepShortcutId()     // Catch:{ all -> 0x021a }
            r17 = 0
            r6[r17] = r14     // Catch:{ all -> 0x021a }
            java.util.List r6 = java.util.Arrays.asList(r6)     // Catch:{ all -> 0x021a }
            android.os.UserHandle r14 = r1.mUser     // Catch:{ all -> 0x021a }
            java.util.List r6 = r7.queryForPinnedShortcuts(r10, r6, r14)     // Catch:{ all -> 0x021a }
            boolean r7 = r6.isEmpty()     // Catch:{ all -> 0x021a }
            if (r7 == 0) goto L_0x025e
            r7 = 0
            r6 = r7
            goto L_0x026d
        L_0x025e:
            r7 = 0
            java.lang.Object r10 = r6.get(r7)     // Catch:{ all -> 0x021a }
            com.android.launcher3.shortcuts.ShortcutInfoCompat r10 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r10     // Catch:{ all -> 0x021a }
            r3.updateFromDeepShortcutInfo(r10, r4)     // Catch:{ all -> 0x021a }
            r6 = 1
            r24 = r6
            r6 = r30
        L_0x026d:
            goto L_0x0289
        L_0x026e:
            r30 = r6
            java.lang.String r6 = r11.getClassName()     // Catch:{ all -> 0x021a }
            java.lang.String r7 = "."
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x021a }
            if (r6 != 0) goto L_0x0287
            com.android.launcher3.compat.LauncherAppsCompat r6 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r4)     // Catch:{ all -> 0x021a }
            android.os.UserHandle r7 = r1.mUser     // Catch:{ all -> 0x021a }
            boolean r6 = r6.isActivityEnabledForProfile(r11, r7)     // Catch:{ all -> 0x021a }
            goto L_0x0289
        L_0x0287:
            r6 = r30
        L_0x0289:
            r7 = 2
            boolean r10 = r3.hasStatusFlag(r7)     // Catch:{ all -> 0x021a }
            if (r10 == 0) goto L_0x02df
            if (r6 != 0) goto L_0x030e
            com.android.launcher3.util.PackageManagerHelper r7 = new com.android.launcher3.util.PackageManagerHelper     // Catch:{ all -> 0x021a }
            r7.<init>(r4)     // Catch:{ all -> 0x021a }
            java.lang.String r10 = r11.getPackageName()     // Catch:{ all -> 0x021a }
            android.os.UserHandle r14 = r1.mUser     // Catch:{ all -> 0x021a }
            android.content.Intent r7 = r7.getAppLaunchIntent(r10, r14)     // Catch:{ all -> 0x021a }
            if (r7 == 0) goto L_0x02b0
            android.content.ComponentName r10 = r7.getComponent()     // Catch:{ all -> 0x021a }
            r11 = r10
            java.lang.Object r10 = r15.get(r11)     // Catch:{ all -> 0x021a }
            com.android.launcher3.AppInfo r10 = (com.android.launcher3.AppInfo) r10     // Catch:{ all -> 0x021a }
            r14 = r10
            goto L_0x02b2
        L_0x02b0:
            r14 = r29
        L_0x02b2:
            if (r7 == 0) goto L_0x02c1
            if (r14 == 0) goto L_0x02c1
            r3.intent = r7     // Catch:{ all -> 0x021a }
            r10 = 0
            r3.status = r10     // Catch:{ all -> 0x021a }
            r10 = 1
            r24 = r10
            r31 = r11
            goto L_0x02da
        L_0x02c1:
            boolean r10 = r3.hasPromiseIconUi()     // Catch:{ all -> 0x021a }
            if (r10 == 0) goto L_0x02d8
            r31 = r11
            long r10 = r3.id     // Catch:{ all -> 0x021a }
            r32 = r7
            r20 = 1
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r20)     // Catch:{ all -> 0x021a }
            r12.put(r10, r7)     // Catch:{ all -> 0x021a }
            goto L_0x0410
        L_0x02d8:
            r31 = r11
        L_0x02da:
            r29 = r14
            r11 = r31
            goto L_0x030e
        L_0x02df:
            if (r6 != 0) goto L_0x0307
            r33 = r6
            long r6 = r3.id     // Catch:{ all -> 0x021a }
            r10 = 1
            java.lang.Boolean r14 = java.lang.Boolean.valueOf(r10)     // Catch:{ all -> 0x021a }
            r12.put(r6, r14)     // Catch:{ all -> 0x021a }
            java.lang.String r6 = "PackageUpdatedTask"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x021a }
            r7.<init>()     // Catch:{ all -> 0x021a }
            java.lang.String r10 = "Restored shortcut no longer valid "
            r7.append(r10)     // Catch:{ all -> 0x021a }
            android.content.Intent r10 = r3.intent     // Catch:{ all -> 0x021a }
            r7.append(r10)     // Catch:{ all -> 0x021a }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x021a }
            com.android.launcher3.logging.FileLog.e(r6, r7)     // Catch:{ all -> 0x021a }
            goto L_0x0410
        L_0x0307:
            r33 = r6
            r6 = 0
            r3.status = r6     // Catch:{ all -> 0x021a }
            r24 = 1
        L_0x030e:
            if (r13 == 0) goto L_0x031c
            int r6 = r3.itemType     // Catch:{ all -> 0x021a }
            if (r6 != 0) goto L_0x031c
            boolean r6 = r3.usingLowResIcon     // Catch:{ all -> 0x021a }
            r5.getTitleAndIcon(r3, r6)     // Catch:{ all -> 0x021a }
            r6 = 1
            r24 = r6
        L_0x031c:
            int r6 = r3.runtimeStatusFlags     // Catch:{ all -> 0x021a }
            int r7 = r3.runtimeStatusFlags     // Catch:{ all -> 0x021a }
            int r7 = r9.apply(r7)     // Catch:{ all -> 0x021a }
            r3.runtimeStatusFlags = r7     // Catch:{ all -> 0x021a }
            int r7 = r3.runtimeStatusFlags     // Catch:{ all -> 0x021a }
            if (r7 == r6) goto L_0x034f
            r16 = 1
            goto L_0x034f
        L_0x032d:
            r0 = move-exception
            r3 = r38
            r11 = r6
            r14 = r7
            r6 = r18
            r7 = r19
            r10 = r25
            goto L_0x0564
        L_0x033a:
            r0 = move-exception
            r26 = r10
            r3 = r38
            r11 = r6
            r14 = r7
            r6 = r18
            r7 = r19
            r10 = r25
            goto L_0x0564
        L_0x0349:
            r27 = r6
            r28 = r7
            r26 = r10
        L_0x034f:
            if (r24 != 0) goto L_0x0357
            if (r16 == 0) goto L_0x0354
            goto L_0x0357
        L_0x0354:
            r6 = r18
            goto L_0x035c
        L_0x0357:
            r6 = r18
            r6.add(r3)     // Catch:{ all -> 0x036b }
        L_0x035c:
            if (r24 == 0) goto L_0x0365
            com.android.launcher3.model.ModelWriter r7 = r37.getModelWriter()     // Catch:{ all -> 0x036b }
            r7.updateItemInDatabase(r3)     // Catch:{ all -> 0x036b }
        L_0x0365:
            r7 = r19
            r10 = 4
            goto L_0x040c
        L_0x036b:
            r0 = move-exception
            r3 = r38
        L_0x036e:
            r7 = r19
        L_0x0370:
            r10 = r25
            r11 = r27
            r14 = r28
            goto L_0x0564
        L_0x0378:
            r0 = move-exception
            r27 = r6
            r26 = r10
            r6 = r18
            r3 = r38
            r14 = r7
            r7 = r19
            r10 = r25
            r11 = r27
            goto L_0x0564
        L_0x038a:
            r27 = r6
            r28 = r7
            r26 = r10
            r25 = r14
            r6 = r18
            goto L_0x03c5
        L_0x0395:
            r0 = move-exception
            r27 = r6
            r26 = r10
            r6 = r18
            r3 = r38
            r10 = r14
            r11 = r27
            r14 = r7
            r7 = r19
            goto L_0x0564
        L_0x03a6:
            r0 = move-exception
            r27 = r6
            r26 = r10
            r23 = r11
            r6 = r18
            r3 = r38
            r10 = r14
            r11 = r27
            r14 = r7
            r7 = r19
            goto L_0x0564
        L_0x03b9:
            r27 = r6
            r28 = r7
            r26 = r10
            r23 = r11
            r25 = r14
            r6 = r18
        L_0x03c5:
            boolean r3 = r0 instanceof com.android.launcher3.LauncherAppWidgetInfo     // Catch:{ all -> 0x0420 }
            if (r3 == 0) goto L_0x0409
            if (r13 == 0) goto L_0x0409
            r3 = r0
            com.android.launcher3.LauncherAppWidgetInfo r3 = (com.android.launcher3.LauncherAppWidgetInfo) r3     // Catch:{ all -> 0x0420 }
            android.os.UserHandle r7 = r1.mUser     // Catch:{ all -> 0x0420 }
            android.os.UserHandle r10 = r3.user     // Catch:{ all -> 0x0420 }
            boolean r7 = r7.equals(r10)     // Catch:{ all -> 0x0420 }
            if (r7 == 0) goto L_0x0409
            r7 = 2
            boolean r10 = r3.hasRestoreFlag(r7)     // Catch:{ all -> 0x0420 }
            if (r10 == 0) goto L_0x0409
            android.content.ComponentName r7 = r3.providerName     // Catch:{ all -> 0x0420 }
            java.lang.String r7 = r7.getPackageName()     // Catch:{ all -> 0x0420 }
            boolean r7 = r8.contains(r7)     // Catch:{ all -> 0x0420 }
            if (r7 == 0) goto L_0x0409
            int r7 = r3.restoreStatus     // Catch:{ all -> 0x0420 }
            r7 = r7 & -11
            r3.restoreStatus = r7     // Catch:{ all -> 0x0420 }
            int r7 = r3.restoreStatus     // Catch:{ all -> 0x0420 }
            r10 = 4
            r7 = r7 | r10
            r3.restoreStatus = r7     // Catch:{ all -> 0x0420 }
            r7 = r19
            r7.add(r3)     // Catch:{ all -> 0x0404 }
            com.android.launcher3.model.ModelWriter r11 = r37.getModelWriter()     // Catch:{ all -> 0x0404 }
            r11.updateItemInDatabase(r3)     // Catch:{ all -> 0x0404 }
            goto L_0x040c
        L_0x0404:
            r0 = move-exception
            r3 = r38
            goto L_0x0370
        L_0x0409:
            r7 = r19
            r10 = 4
        L_0x040c:
            r18 = r6
            r19 = r7
        L_0x0410:
            r0 = r22
            r11 = r23
            r14 = r25
            r10 = r26
            r6 = r27
            r7 = r28
            r3 = r40
            goto L_0x0178
        L_0x0420:
            r0 = move-exception
            r7 = r19
            r3 = r38
            r10 = r25
            r11 = r27
            r14 = r28
            goto L_0x0564
        L_0x042d:
            r0 = move-exception
            r27 = r6
            r28 = r7
            r26 = r10
            r23 = r11
            r6 = r18
            r7 = r19
            r3 = r38
            r10 = r14
            r11 = r27
            r14 = r28
            goto L_0x0564
        L_0x0443:
            r27 = r6
            r28 = r7
            r26 = r10
            r23 = r11
            r25 = r14
            r6 = r18
            r7 = r19
            monitor-exit(r39)     // Catch:{ all -> 0x054c }
            android.os.UserHandle r0 = r1.mUser
            r1.bindUpdatedShortcuts(r6, r0)
            boolean r0 = r12.isEmpty()
            if (r0 != 0) goto L_0x046a
            r10 = 0
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r10)
            com.android.launcher3.util.ItemInfoMatcher r0 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r12, r0)
            r1.deleteAndBindComponentsRemoved(r0)
            goto L_0x046b
        L_0x046a:
            r10 = 0
        L_0x046b:
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x0479
            com.android.launcher3.model.PackageUpdatedTask$1 r0 = new com.android.launcher3.model.PackageUpdatedTask$1
            r0.<init>(r7)
            r1.scheduleCallbackTask(r0)
        L_0x0479:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.HashSet r3 = new java.util.HashSet
            r3.<init>()
            int r6 = r1.mOp
            r7 = 3
            if (r6 != r7) goto L_0x0490
            r11 = r27
            java.util.Collections.addAll(r0, r11)
            r14 = r28
            goto L_0x04cd
        L_0x0490:
            r11 = r27
            int r6 = r1.mOp
            r7 = 2
            if (r6 != r7) goto L_0x04cb
            com.android.launcher3.compat.LauncherAppsCompat r6 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r4)
            r7 = 0
        L_0x049c:
            r14 = r28
            if (r7 >= r14) goto L_0x04b5
            r13 = r11[r7]
            android.os.UserHandle r10 = r1.mUser
            boolean r10 = r6.isPackageEnabledForProfile(r13, r10)
            if (r10 != 0) goto L_0x04af
            r10 = r11[r7]
            r0.add(r10)
        L_0x04af:
            int r7 = r7 + 1
            r28 = r14
            r10 = 0
            goto L_0x049c
        L_0x04b5:
            java.util.Iterator r7 = r25.iterator()
        L_0x04b9:
            boolean r10 = r7.hasNext()
            if (r10 == 0) goto L_0x04cd
            java.lang.Object r10 = r7.next()
            com.android.launcher3.AppInfo r10 = (com.android.launcher3.AppInfo) r10
            android.content.ComponentName r13 = r10.componentName
            r3.add(r13)
            goto L_0x04b9
        L_0x04cb:
            r14 = r28
        L_0x04cd:
            boolean r6 = r0.isEmpty()
            if (r6 == 0) goto L_0x04d9
            boolean r6 = r3.isEmpty()
            if (r6 != 0) goto L_0x04fe
        L_0x04d9:
            android.os.UserHandle r6 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r6 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r0, r6)
            android.os.UserHandle r7 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r7 = com.android.launcher3.util.ItemInfoMatcher.ofComponents(r3, r7)
            com.android.launcher3.util.ItemInfoMatcher r6 = r6.or(r7)
            r7 = 1
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r7)
            com.android.launcher3.util.ItemInfoMatcher r7 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r12, r10)
            com.android.launcher3.util.ItemInfoMatcher r6 = r6.and(r7)
            r1.deleteAndBindComponentsRemoved(r6)
            android.os.UserHandle r7 = r1.mUser
            com.android.launcher3.InstallShortcutReceiver.removeFromInstallQueue(r4, r0, r7)
        L_0x04fe:
            boolean r6 = r25.isEmpty()
            if (r6 != 0) goto L_0x050f
            com.android.launcher3.model.PackageUpdatedTask$2 r6 = new com.android.launcher3.model.PackageUpdatedTask$2
            r10 = r25
            r6.<init>(r10)
            r1.scheduleCallbackTask(r6)
            goto L_0x0511
        L_0x050f:
            r10 = r25
        L_0x0511:
            boolean r6 = com.android.launcher3.Utilities.ATLEAST_OREO
            if (r6 == 0) goto L_0x0545
            int r6 = r1.mOp
            r7 = 1
            if (r6 != r7) goto L_0x0545
            r34 = 0
        L_0x051c:
            r6 = r34
            if (r6 >= r14) goto L_0x053b
            com.android.launcher3.model.WidgetsModel r7 = r2.widgetsModel
            com.android.launcher3.util.PackageUserKey r13 = new com.android.launcher3.util.PackageUserKey
            r35 = r0
            r0 = r11[r6]
            r36 = r3
            android.os.UserHandle r3 = r1.mUser
            r13.<init>(r0, r3)
            r3 = r38
            r7.update(r3, r13)
            int r34 = r6 + 1
            r0 = r35
            r3 = r36
            goto L_0x051c
        L_0x053b:
            r35 = r0
            r36 = r3
            r3 = r38
            r1.bindUpdatedWidgets(r2)
            goto L_0x054b
        L_0x0545:
            r35 = r0
            r36 = r3
            r3 = r38
        L_0x054b:
            return
        L_0x054c:
            r0 = move-exception
            r3 = r38
            r10 = r25
            r11 = r27
            r14 = r28
            goto L_0x0564
        L_0x0556:
            r0 = move-exception
            r3 = r38
            r26 = r10
            r23 = r11
            r10 = r14
            r11 = r6
            r14 = r7
            r6 = r18
            r7 = r19
        L_0x0564:
            monitor-exit(r39)     // Catch:{ all -> 0x0566 }
            throw r0
        L_0x0566:
            r0 = move-exception
            goto L_0x0564
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.PackageUpdatedTask.execute(com.android.launcher3.LauncherAppState, com.android.launcher3.model.BgDataModel, com.android.launcher3.AllAppsList):void");
    }
}
