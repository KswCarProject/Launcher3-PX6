package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.BitmapInfo;
import com.android.launcher3.graphics.BitmapRenderer;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.SQLiteCacheHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class IconCache {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_IGNORE_CACHE = false;
    public static final String EMPTY_CLASS_NAME = ".";
    static final Object ICON_UPDATE_TOKEN = new Object();
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
    private static final int LOW_RES_SCALE_FACTOR = 5;
    private static final String TAG = "Launcher.IconCache";
    private final HashMap<ComponentKey, CacheEntry> mCache = new HashMap<>(50);
    /* access modifiers changed from: private */
    public final Context mContext;
    private final HashMap<UserHandle, BitmapInfo> mDefaultIcons = new HashMap<>();
    private final BitmapFactory.Options mHighResOptions;
    final IconDB mIconDb;
    private final int mIconDpi;
    private final IconProvider mIconProvider;
    private final InstantAppResolver mInstantAppResolver;
    /* access modifiers changed from: private */
    public final LauncherAppsCompat mLauncherApps;
    private final BitmapFactory.Options mLowResOptions;
    final MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();
    private final PackageManager mPackageManager;
    private int mPendingIconRequestCount = 0;
    final UserManagerCompat mUserManager;
    final Handler mWorkerHandler;

    public static class CacheEntry extends BitmapInfo {
        public CharSequence contentDescription = "";
        public boolean isLowResIcon;
        public CharSequence title = "";
    }

    public interface ItemInfoUpdateReceiver {
        void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon);
    }

    public IconCache(Context context, InvariantDeviceProfile inv) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = UserManagerCompat.getInstance(this.mContext);
        this.mLauncherApps = LauncherAppsCompat.getInstance(this.mContext);
        this.mInstantAppResolver = InstantAppResolver.newInstance(this.mContext);
        this.mIconDpi = inv.fillResIconDpi;
        this.mIconDb = new IconDB(context, inv.iconBitmapSize);
        this.mIconProvider = IconProvider.newInstance(context);
        this.mWorkerHandler = new Handler(LauncherModel.getWorkerLooper());
        this.mLowResOptions = new BitmapFactory.Options();
        this.mLowResOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        if (BitmapRenderer.USE_HARDWARE_BITMAP) {
            this.mHighResOptions = new BitmapFactory.Options();
            this.mHighResOptions.inPreferredConfig = Bitmap.Config.HARDWARE;
            return;
        }
        this.mHighResOptions = null;
    }

    private Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), Utilities.ATLEAST_OREO ? 17301651 : 17629184);
    }

    private Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, this.mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }
        return d != null ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = this.mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources == null || iconId == 0) {
            return getFullResDefaultActivityIcon();
        }
        return getFullResIcon(resources, iconId);
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        int iconId;
        try {
            resources = this.mPackageManager.getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources == null || (iconId = info.getIconResource()) == 0) {
            return getFullResDefaultActivityIcon();
        }
        return getFullResIcon(resources, iconId);
    }

    public Drawable getFullResIcon(LauncherActivityInfo info) {
        return getFullResIcon(info, true);
    }

    public Drawable getFullResIcon(LauncherActivityInfo info, boolean flattenDrawable) {
        return this.mIconProvider.getIcon(info, this.mIconDpi, flattenDrawable);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        if (r0 != null) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        if (r1 != null) goto L_0x0020;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.launcher3.graphics.BitmapInfo makeDefaultIcon(android.os.UserHandle r5) {
        /*
            r4 = this;
            android.content.Context r0 = r4.mContext
            com.android.launcher3.graphics.LauncherIcons r0 = com.android.launcher3.graphics.LauncherIcons.obtain(r0)
            r1 = 0
            android.graphics.drawable.Drawable r2 = r4.getFullResDefaultActivityIcon()     // Catch:{ Throwable -> 0x001a }
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Throwable -> 0x001a }
            com.android.launcher3.graphics.BitmapInfo r2 = r0.createBadgedIconBitmap(r2, r5, r3)     // Catch:{ Throwable -> 0x001a }
            if (r0 == 0) goto L_0x0017
            r0.close()
        L_0x0017:
            return r2
        L_0x0018:
            r2 = move-exception
            goto L_0x001c
        L_0x001a:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0018 }
        L_0x001c:
            if (r0 == 0) goto L_0x002c
            if (r1 == 0) goto L_0x0029
            r0.close()     // Catch:{ Throwable -> 0x0024 }
            goto L_0x002c
        L_0x0024:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x002c
        L_0x0029:
            r0.close()
        L_0x002c:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.IconCache.makeDefaultIcon(android.os.UserHandle):com.android.launcher3.graphics.BitmapInfo");
    }

    public synchronized void remove(ComponentName componentName, UserHandle user) {
        this.mCache.remove(new ComponentKey(componentName, user));
    }

    private void removeFromMemCacheLocked(String packageName, UserHandle user) {
        HashSet<ComponentKey> forDeletion = new HashSet<>();
        for (ComponentKey key : this.mCache.keySet()) {
            if (key.componentName.getPackageName().equals(packageName) && key.user.equals(user)) {
                forDeletion.add(key);
            }
        }
        Iterator<ComponentKey> it = forDeletion.iterator();
        while (it.hasNext()) {
            this.mCache.remove(it.next());
        }
    }

    public synchronized void updateIconsForPkg(String packageName, UserHandle user) {
        removeIconsForPkg(packageName, user);
        try {
            PackageInfo info = this.mPackageManager.getPackageInfo(packageName, 8192);
            long userSerial = this.mUserManager.getSerialNumberForUser(user);
            for (LauncherActivityInfo app : this.mLauncherApps.getActivityList(packageName, user)) {
                addIconToDBAndMemCache(app, info, userSerial, false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package not found", e);
        }
        return;
    }

    public synchronized void removeIconsForPkg(String packageName, UserHandle user) {
        removeFromMemCacheLocked(packageName, user);
        long userSerial = this.mUserManager.getSerialNumberForUser(user);
        IconDB iconDB = this.mIconDb;
        iconDB.delete("componentName LIKE ? AND profileId = ?", new String[]{packageName + "/%", Long.toString(userSerial)});
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x001e, code lost:
        r1 = r0.next();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateDbIcons(java.util.Set<java.lang.String> r5) {
        /*
            r4 = this;
            android.os.Handler r0 = r4.mWorkerHandler
            java.lang.Object r1 = ICON_UPDATE_TOKEN
            r0.removeCallbacksAndMessages(r1)
            com.android.launcher3.IconProvider r0 = r4.mIconProvider
            android.content.Context r1 = r4.mContext
            r0.updateSystemStateString(r1)
            com.android.launcher3.compat.UserManagerCompat r0 = r4.mUserManager
            java.util.List r0 = r0.getUserProfiles()
            java.util.Iterator r0 = r0.iterator()
        L_0x0018:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0049
            java.lang.Object r1 = r0.next()
            android.os.UserHandle r1 = (android.os.UserHandle) r1
            com.android.launcher3.compat.LauncherAppsCompat r2 = r4.mLauncherApps
            r3 = 0
            java.util.List r2 = r2.getActivityList(r3, r1)
            if (r2 == 0) goto L_0x0048
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x0034
            goto L_0x0048
        L_0x0034:
            android.os.UserHandle r3 = android.os.Process.myUserHandle()
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L_0x0040
            r3 = r5
            goto L_0x0044
        L_0x0040:
            java.util.Set r3 = java.util.Collections.emptySet()
        L_0x0044:
            r4.updateDBIcons(r1, r2, r3)
            goto L_0x0018
        L_0x0048:
            return
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.IconCache.updateDbIcons(java.util.Set):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:61:0x017c, code lost:
        if (r2 != null) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0193, code lost:
        if (r2 == null) goto L_0x0198;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0195, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0198, code lost:
        r0 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x019d, code lost:
        if (r15.isEmpty() != false) goto L_0x01ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x019f, code lost:
        r8.mIconDb.delete(com.android.launcher3.Utilities.createDbSelectionQuery("rowid", r15), (java.lang.String[]) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x01af, code lost:
        if (r14.isEmpty() == false) goto L_0x01bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01b5, code lost:
        if (r7.isEmpty() != false) goto L_0x01b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01b8, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01bb, code lost:
        r12 = new java.util.Stack<>();
        r12.addAll(r14.values());
        r16 = r7;
        new com.android.launcher3.IconCache.SerializedIconUpdateTask(r27, r10, r25, r12, r7).scheduleNext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateDBIcons(android.os.UserHandle r28, java.util.List<android.content.pm.LauncherActivityInfo> r29, java.util.Set<java.lang.String> r30) {
        /*
            r27 = this;
            r8 = r27
            r9 = r28
            com.android.launcher3.compat.UserManagerCompat r0 = r8.mUserManager
            long r10 = r0.getSerialNumberForUser(r9)
            android.content.Context r0 = r8.mContext
            android.content.pm.PackageManager r12 = r0.getPackageManager()
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r13 = r0
            r0 = 8192(0x2000, float:1.14794E-41)
            java.util.List r0 = r12.getInstalledPackages(r0)
            java.util.Iterator r0 = r0.iterator()
        L_0x0020:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0032
            java.lang.Object r1 = r0.next()
            android.content.pm.PackageInfo r1 = (android.content.pm.PackageInfo) r1
            java.lang.String r2 = r1.packageName
            r13.put(r2, r1)
            goto L_0x0020
        L_0x0032:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r14 = r0
            java.util.Iterator r0 = r29.iterator()
        L_0x003c:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0050
            java.lang.Object r1 = r0.next()
            android.content.pm.LauncherActivityInfo r1 = (android.content.pm.LauncherActivityInfo) r1
            android.content.ComponentName r2 = r1.getComponentName()
            r14.put(r2, r1)
            goto L_0x003c
        L_0x0050:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r15 = r0
            java.util.Stack r0 = new java.util.Stack
            r0.<init>()
            r7 = r0
            r1 = 0
            r2 = r1
            com.android.launcher3.IconCache$IconDB r0 = r8.mIconDb     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.String r3 = "rowid"
            java.lang.String r4 = "componentName"
            java.lang.String r5 = "lastUpdated"
            java.lang.String r6 = "version"
            java.lang.String r1 = "system_state"
            java.lang.String[] r1 = new java.lang.String[]{r3, r4, r5, r6, r1}     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.String r3 = "profileId = ? "
            r4 = 1
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            r5 = 0
            java.lang.String r6 = java.lang.Long.toString(r10)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            r4[r5] = r6     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            android.database.Cursor r0 = r0.query(r1, r3, r4)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            r2 = r0
            java.lang.String r0 = "componentName"
            int r0 = r2.getColumnIndex(r0)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.String r1 = "lastUpdated"
            int r1 = r2.getColumnIndex(r1)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.String r3 = "version"
            int r3 = r2.getColumnIndex(r3)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.String r4 = "rowid"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.String r5 = "system_state"
            int r5 = r2.getColumnIndex(r5)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
        L_0x009d:
            boolean r6 = r2.moveToNext()     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            if (r6 == 0) goto L_0x0178
            java.lang.String r6 = r2.getString(r0)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            android.content.ComponentName r17 = android.content.ComponentName.unflattenFromString(r6)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            r18 = r17
            r19 = r0
            java.lang.String r0 = r18.getPackageName()     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            java.lang.Object r0 = r13.get(r0)     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            android.content.pm.PackageInfo r0 = (android.content.pm.PackageInfo) r0     // Catch:{ SQLiteException -> 0x0187, all -> 0x017f }
            if (r0 != 0) goto L_0x00fd
            r20 = r6
            java.lang.String r6 = r18.getPackageName()     // Catch:{ SQLiteException -> 0x00f6, all -> 0x00ed }
            r21 = r12
            r12 = r30
            boolean r6 = r12.contains(r6)     // Catch:{ SQLiteException -> 0x00e8, all -> 0x00e1 }
            if (r6 != 0) goto L_0x00db
            r6 = r18
            r8.remove(r6, r9)     // Catch:{ SQLiteException -> 0x00e8, all -> 0x00e1 }
            int r17 = r2.getInt(r4)     // Catch:{ SQLiteException -> 0x00e8, all -> 0x00e1 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r17)     // Catch:{ SQLiteException -> 0x00e8, all -> 0x00e1 }
            r15.add(r12)     // Catch:{ SQLiteException -> 0x00e8, all -> 0x00e1 }
        L_0x00db:
            r24 = r1
            r25 = r13
            goto L_0x0163
        L_0x00e1:
            r0 = move-exception
            r16 = r7
            r25 = r13
            goto L_0x01dd
        L_0x00e8:
            r0 = move-exception
            r25 = r13
            goto L_0x018c
        L_0x00ed:
            r0 = move-exception
            r21 = r12
            r16 = r7
            r25 = r13
            goto L_0x01dd
        L_0x00f6:
            r0 = move-exception
            r21 = r12
            r25 = r13
            goto L_0x018c
        L_0x00fd:
            r20 = r6
            r21 = r12
            r6 = r18
            android.content.pm.ApplicationInfo r12 = r0.applicationInfo     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            int r12 = r12.flags     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            r17 = 16777216(0x1000000, float:2.3509887E-38)
            r12 = r12 & r17
            if (r12 == 0) goto L_0x010e
            goto L_0x00db
        L_0x010e:
            long r17 = r2.getLong(r1)     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            int r12 = r2.getInt(r3)     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            java.lang.Object r22 = r14.remove(r6)     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            android.content.pm.LauncherActivityInfo r22 = (android.content.pm.LauncherActivityInfo) r22     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            r23 = r22
            r24 = r1
            int r1 = r0.versionCode     // Catch:{ SQLiteException -> 0x0174, all -> 0x016d }
            if (r12 != r1) goto L_0x0148
            r26 = r12
            r25 = r13
            long r12 = r0.lastUpdateTime     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            int r1 = (r17 > r12 ? 1 : (r17 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x014c
            java.lang.String r1 = r2.getString(r5)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            com.android.launcher3.IconProvider r12 = r8.mIconProvider     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            java.lang.String r13 = r0.packageName     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            java.lang.String r12 = r12.getIconSystemState(r13)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            boolean r1 = android.text.TextUtils.equals(r1, r12)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            if (r1 == 0) goto L_0x014c
            goto L_0x0163
        L_0x0141:
            r0 = move-exception
            r16 = r7
            goto L_0x01dd
        L_0x0146:
            r0 = move-exception
            goto L_0x018c
        L_0x0148:
            r26 = r12
            r25 = r13
        L_0x014c:
            r1 = r23
            if (r1 != 0) goto L_0x015f
            r8.remove(r6, r9)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            int r12 = r2.getInt(r4)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            r15.add(r12)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
            goto L_0x0162
        L_0x015f:
            r7.add(r1)     // Catch:{ SQLiteException -> 0x0146, all -> 0x0141 }
        L_0x0162:
        L_0x0163:
            r0 = r19
            r12 = r21
            r1 = r24
            r13 = r25
            goto L_0x009d
        L_0x016d:
            r0 = move-exception
            r25 = r13
            r16 = r7
            goto L_0x01dd
        L_0x0174:
            r0 = move-exception
            r25 = r13
            goto L_0x018c
        L_0x0178:
            r21 = r12
            r25 = r13
            if (r2 == 0) goto L_0x0198
            goto L_0x0195
        L_0x017f:
            r0 = move-exception
            r21 = r12
            r25 = r13
            r16 = r7
            goto L_0x01dd
        L_0x0187:
            r0 = move-exception
            r21 = r12
            r25 = r13
        L_0x018c:
            java.lang.String r1 = "Launcher.IconCache"
            java.lang.String r3 = "Error reading icon cache"
            android.util.Log.d(r1, r3, r0)     // Catch:{ all -> 0x01da }
            if (r2 == 0) goto L_0x0198
        L_0x0195:
            r2.close()
        L_0x0198:
            r0 = r2
            boolean r1 = r15.isEmpty()
            if (r1 != 0) goto L_0x01ab
            com.android.launcher3.IconCache$IconDB r1 = r8.mIconDb
            java.lang.String r2 = "rowid"
            java.lang.String r2 = com.android.launcher3.Utilities.createDbSelectionQuery(r2, r15)
            r3 = 0
            r1.delete(r2, r3)
        L_0x01ab:
            boolean r1 = r14.isEmpty()
            if (r1 == 0) goto L_0x01bb
            boolean r1 = r7.isEmpty()
            if (r1 != 0) goto L_0x01b8
            goto L_0x01bb
        L_0x01b8:
            r16 = r7
            goto L_0x01d9
        L_0x01bb:
            java.util.Stack r1 = new java.util.Stack
            r1.<init>()
            r12 = r1
            java.util.Collection r1 = r14.values()
            r12.addAll(r1)
            com.android.launcher3.IconCache$SerializedIconUpdateTask r13 = new com.android.launcher3.IconCache$SerializedIconUpdateTask
            r1 = r13
            r2 = r27
            r3 = r10
            r5 = r25
            r6 = r12
            r16 = r7
            r1.<init>(r3, r5, r6, r7)
            r13.scheduleNext()
        L_0x01d9:
            return
        L_0x01da:
            r0 = move-exception
            r16 = r7
        L_0x01dd:
            if (r2 == 0) goto L_0x01e2
            r2.close()
        L_0x01e2:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.IconCache.updateDBIcons(android.os.UserHandle, java.util.List, java.util.Set):void");
    }

    /* access modifiers changed from: package-private */
    public synchronized void addIconToDBAndMemCache(LauncherActivityInfo app, PackageInfo info, long userSerial, boolean replaceExisting) {
        synchronized (this) {
            ComponentKey key = new ComponentKey(app.getComponentName(), app.getUser());
            CacheEntry entry = null;
            if (!replaceExisting && ((entry = this.mCache.get(key)) == null || entry.isLowResIcon || entry.icon == null)) {
                entry = null;
            }
            if (entry == null) {
                entry = new CacheEntry();
                LauncherIcons li = LauncherIcons.obtain(this.mContext);
                li.createBadgedIconBitmap(getFullResIcon(app), app.getUser(), app.getApplicationInfo().targetSdkVersion).applyTo((BitmapInfo) entry);
                li.recycle();
            }
            CacheEntry entry2 = entry;
            entry2.title = app.getLabel();
            entry2.contentDescription = this.mUserManager.getBadgedLabelForUser(entry2.title, app.getUser());
            this.mCache.put(key, entry2);
            Bitmap lowResIcon = generateLowResIcon(entry2.icon);
            addIconToDB(newContentValues(entry2.icon, lowResIcon, entry2.color, entry2.title.toString(), app.getApplicationInfo().packageName), app.getComponentName(), info, userSerial);
        }
    }

    private void addIconToDB(ContentValues values, ComponentName key, PackageInfo info, long userSerial) {
        values.put("componentName", key.flattenToString());
        values.put(LauncherSettings.Favorites.PROFILE_ID, Long.valueOf(userSerial));
        values.put("lastUpdated", Long.valueOf(info.lastUpdateTime));
        values.put("version", Integer.valueOf(info.versionCode));
        this.mIconDb.insertOrReplace(values);
    }

    public IconLoadRequest updateIconInBackground(ItemInfoUpdateReceiver caller, ItemInfoWithIcon info) {
        Preconditions.assertUIThread();
        if (this.mPendingIconRequestCount <= 0) {
            LauncherModel.setWorkerPriority(-2);
        }
        this.mPendingIconRequestCount++;
        final ItemInfoWithIcon itemInfoWithIcon = info;
        final ItemInfoUpdateReceiver itemInfoUpdateReceiver = caller;
        AnonymousClass1 r1 = new IconLoadRequest(this.mWorkerHandler, new Runnable() {
            public final void run() {
                IconCache.this.onIconRequestEnd();
            }
        }) {
            public void run() {
                if ((itemInfoWithIcon instanceof AppInfo) || (itemInfoWithIcon instanceof ShortcutInfo)) {
                    IconCache.this.getTitleAndIcon(itemInfoWithIcon, false);
                } else if (itemInfoWithIcon instanceof PackageItemInfo) {
                    IconCache.this.getTitleAndIconForApp((PackageItemInfo) itemInfoWithIcon, false);
                }
                IconCache.this.mMainThreadExecutor.execute(new Runnable(itemInfoUpdateReceiver, itemInfoWithIcon) {
                    private final /* synthetic */ IconCache.ItemInfoUpdateReceiver f$1;
                    private final /* synthetic */ ItemInfoWithIcon f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        IconCache.AnonymousClass1.lambda$run$0(IconCache.AnonymousClass1.this, this.f$1, this.f$2);
                    }
                });
            }

            public static /* synthetic */ void lambda$run$0(AnonymousClass1 r0, ItemInfoUpdateReceiver caller, ItemInfoWithIcon info) {
                caller.reapplyItemInfo(info);
                r0.onEnd();
            }
        };
        Utilities.postAsyncCallback(this.mWorkerHandler, r1);
        return r1;
    }

    /* access modifiers changed from: private */
    public void onIconRequestEnd() {
        this.mPendingIconRequestCount--;
        if (this.mPendingIconRequestCount <= 0) {
            LauncherModel.setWorkerPriority(10);
        }
    }

    public synchronized void updateTitleAndIcon(AppInfo application) {
        CacheEntry entry = cacheLocked(application.componentName, Provider.of(null), application.user, false, application.usingLowResIcon);
        if (entry.icon != null && !isDefaultIcon(entry.icon, application.user)) {
            applyCacheEntry(entry, application);
        }
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon info, LauncherActivityInfo activityInfo, boolean useLowResIcon) {
        getTitleAndIcon(info, Provider.of(activityInfo), false, useLowResIcon);
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon info, boolean useLowResIcon) {
        if (info.getTargetComponent() == null) {
            getDefaultIcon(info.user).applyTo(info);
            info.title = "";
            info.contentDescription = "";
            info.usingLowResIcon = false;
        } else {
            getTitleAndIcon(info, new ActivityInfoProvider(info.getIntent(), info.user), true, useLowResIcon);
        }
    }

    private synchronized void getTitleAndIcon(@NonNull ItemInfoWithIcon infoInOut, @NonNull Provider<LauncherActivityInfo> activityInfoProvider, boolean usePkgIcon, boolean useLowResIcon) {
        applyCacheEntry(cacheLocked(infoInOut.getTargetComponent(), activityInfoProvider, infoInOut.user, usePkgIcon, useLowResIcon), infoInOut);
    }

    public synchronized void getTitleAndIconForApp(PackageItemInfo infoInOut, boolean useLowResIcon) {
        applyCacheEntry(getEntryForPackageLocked(infoInOut.packageName, infoInOut.user, useLowResIcon), infoInOut);
    }

    private void applyCacheEntry(CacheEntry entry, ItemInfoWithIcon info) {
        info.title = Utilities.trim(entry.title);
        info.contentDescription = entry.contentDescription;
        info.usingLowResIcon = entry.isLowResIcon;
        (entry.icon == null ? getDefaultIcon(info.user) : entry).applyTo(info);
    }

    public synchronized BitmapInfo getDefaultIcon(UserHandle user) {
        if (!this.mDefaultIcons.containsKey(user)) {
            this.mDefaultIcons.put(user, makeDefaultIcon(user));
        }
        return this.mDefaultIcons.get(user);
    }

    public boolean isDefaultIcon(Bitmap icon, UserHandle user) {
        return getDefaultIcon(user).icon == icon;
    }

    /* access modifiers changed from: protected */
    public CacheEntry cacheLocked(@NonNull ComponentName componentName, @NonNull Provider<LauncherActivityInfo> infoProvider, UserHandle user, boolean usePackageIcon, boolean useLowResIcon) {
        CacheEntry packageEntry;
        Preconditions.assertWorkerThread();
        ComponentKey cacheKey = new ComponentKey(componentName, user);
        CacheEntry entry = this.mCache.get(cacheKey);
        if (entry == null || (entry.isLowResIcon && !useLowResIcon)) {
            entry = new CacheEntry();
            this.mCache.put(cacheKey, entry);
            LauncherActivityInfo info = null;
            boolean providerFetchedOnce = false;
            if (!getEntryFromDB(cacheKey, entry, useLowResIcon)) {
                info = infoProvider.get();
                providerFetchedOnce = true;
                if (info != null) {
                    LauncherIcons li = LauncherIcons.obtain(this.mContext);
                    li.createBadgedIconBitmap(getFullResIcon(info), info.getUser(), info.getApplicationInfo().targetSdkVersion).applyTo((BitmapInfo) entry);
                    li.recycle();
                } else {
                    if (usePackageIcon && (packageEntry = getEntryForPackageLocked(componentName.getPackageName(), user, false)) != null) {
                        packageEntry.applyTo((BitmapInfo) entry);
                        entry.title = packageEntry.title;
                        entry.contentDescription = packageEntry.contentDescription;
                    }
                    if (entry.icon == null) {
                        getDefaultIcon(user).applyTo((BitmapInfo) entry);
                    }
                }
            }
            if (TextUtils.isEmpty(entry.title)) {
                if (info == null && !providerFetchedOnce) {
                    info = infoProvider.get();
                }
                if (info != null) {
                    entry.title = info.getLabel();
                    entry.contentDescription = this.mUserManager.getBadgedLabelForUser(entry.title, user);
                }
            }
        }
        return entry;
    }

    public synchronized void clear() {
        Preconditions.assertWorkerThread();
        this.mIconDb.clear();
    }

    public synchronized void cachePackageInstallInfo(String packageName, UserHandle user, Bitmap icon, CharSequence title) {
        removeFromMemCacheLocked(packageName, user);
        ComponentKey cacheKey = getPackageKey(packageName, user);
        CacheEntry entry = this.mCache.get(cacheKey);
        if (entry == null) {
            entry = new CacheEntry();
        }
        if (!TextUtils.isEmpty(title)) {
            entry.title = title;
        }
        if (icon != null) {
            LauncherIcons li = LauncherIcons.obtain(this.mContext);
            li.createIconBitmap(icon).applyTo((BitmapInfo) entry);
            li.recycle();
        }
        if (!TextUtils.isEmpty(title) && entry.icon != null) {
            this.mCache.put(cacheKey, entry);
        }
    }

    private static ComponentKey getPackageKey(String packageName, UserHandle user) {
        return new ComponentKey(new ComponentName(packageName, packageName + EMPTY_CLASS_NAME), user);
    }

    private CacheEntry getEntryForPackageLocked(String packageName, UserHandle user, boolean useLowResIcon) {
        UserHandle userHandle = user;
        boolean z = useLowResIcon;
        Preconditions.assertWorkerThread();
        ComponentKey cacheKey = getPackageKey(packageName, user);
        CacheEntry entry = this.mCache.get(cacheKey);
        if (entry != null && (!entry.isLowResIcon || z)) {
            return entry;
        }
        CacheEntry entry2 = new CacheEntry();
        boolean entryUpdated = true;
        if (!getEntryFromDB(cacheKey, entry2, z)) {
            try {
                PackageInfo info = this.mPackageManager.getPackageInfo(packageName, Process.myUserHandle().equals(userHandle) ? 0 : 8192);
                ApplicationInfo appInfo = info.applicationInfo;
                if (appInfo != null) {
                    LauncherIcons li = LauncherIcons.obtain(this.mContext);
                    BitmapInfo iconInfo = li.createBadgedIconBitmap(appInfo.loadIcon(this.mPackageManager), userHandle, appInfo.targetSdkVersion, this.mInstantAppResolver.isInstantApp(appInfo));
                    li.recycle();
                    Bitmap lowResIcon = generateLowResIcon(iconInfo.icon);
                    entry2.title = appInfo.loadLabel(this.mPackageManager);
                    entry2.contentDescription = this.mUserManager.getBadgedLabelForUser(entry2.title, userHandle);
                    entry2.icon = z ? lowResIcon : iconInfo.icon;
                    entry2.color = iconInfo.color;
                    entry2.isLowResIcon = z;
                    BitmapInfo bitmapInfo = iconInfo;
                    LauncherIcons launcherIcons = li;
                    ApplicationInfo applicationInfo = appInfo;
                    PackageInfo packageInfo = info;
                    addIconToDB(newContentValues(iconInfo.icon, lowResIcon, entry2.color, entry2.title.toString(), packageName), cacheKey.componentName, info, this.mUserManager.getSerialNumberForUser(userHandle));
                } else {
                    ApplicationInfo applicationInfo2 = appInfo;
                    PackageInfo packageInfo2 = info;
                    throw new PackageManager.NameNotFoundException("ApplicationInfo is null");
                }
            } catch (PackageManager.NameNotFoundException e) {
                entryUpdated = false;
            }
        }
        if (!entryUpdated) {
            return entry2;
        }
        this.mCache.put(cacheKey, entry2);
        return entry2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0080, code lost:
        if (r0 != null) goto L_0x0082;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0082, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0090, code lost:
        if (r0 == null) goto L_0x0093;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0093, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getEntryFromDB(com.android.launcher3.util.ComponentKey r11, com.android.launcher3.IconCache.CacheEntry r12, boolean r13) {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            com.android.launcher3.IconCache$IconDB r2 = r10.mIconDb     // Catch:{ SQLiteException -> 0x0088 }
            r3 = 3
            java.lang.String[] r3 = new java.lang.String[r3]     // Catch:{ SQLiteException -> 0x0088 }
            if (r13 == 0) goto L_0x000c
            java.lang.String r4 = "icon_low_res"
            goto L_0x000e
        L_0x000c:
            java.lang.String r4 = "icon"
        L_0x000e:
            r3[r1] = r4     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r4 = "icon_color"
            r5 = 1
            r3[r5] = r4     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r4 = "label"
            r6 = 2
            r3[r6] = r4     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r4 = "componentName = ? AND profileId = ?"
            java.lang.String[] r7 = new java.lang.String[r6]     // Catch:{ SQLiteException -> 0x0088 }
            android.content.ComponentName r8 = r11.componentName     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r8 = r8.flattenToString()     // Catch:{ SQLiteException -> 0x0088 }
            r7[r1] = r8     // Catch:{ SQLiteException -> 0x0088 }
            com.android.launcher3.compat.UserManagerCompat r8 = r10.mUserManager     // Catch:{ SQLiteException -> 0x0088 }
            android.os.UserHandle r9 = r11.user     // Catch:{ SQLiteException -> 0x0088 }
            long r8 = r8.getSerialNumberForUser(r9)     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r8 = java.lang.Long.toString(r8)     // Catch:{ SQLiteException -> 0x0088 }
            r7[r5] = r8     // Catch:{ SQLiteException -> 0x0088 }
            android.database.Cursor r2 = r2.query(r3, r4, r7)     // Catch:{ SQLiteException -> 0x0088 }
            r0 = r2
            boolean r2 = r0.moveToNext()     // Catch:{ SQLiteException -> 0x0088 }
            if (r2 == 0) goto L_0x0080
            if (r13 == 0) goto L_0x0044
            android.graphics.BitmapFactory$Options r2 = r10.mLowResOptions     // Catch:{ SQLiteException -> 0x0088 }
            goto L_0x0046
        L_0x0044:
            android.graphics.BitmapFactory$Options r2 = r10.mHighResOptions     // Catch:{ SQLiteException -> 0x0088 }
        L_0x0046:
            android.graphics.Bitmap r2 = loadIconNoResize(r0, r1, r2)     // Catch:{ SQLiteException -> 0x0088 }
            r12.icon = r2     // Catch:{ SQLiteException -> 0x0088 }
            int r2 = r0.getInt(r5)     // Catch:{ SQLiteException -> 0x0088 }
            r3 = 255(0xff, float:3.57E-43)
            int r2 = android.support.v4.graphics.ColorUtils.setAlphaComponent(r2, r3)     // Catch:{ SQLiteException -> 0x0088 }
            r12.color = r2     // Catch:{ SQLiteException -> 0x0088 }
            r12.isLowResIcon = r13     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r2 = r0.getString(r6)     // Catch:{ SQLiteException -> 0x0088 }
            r12.title = r2     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.CharSequence r2 = r12.title     // Catch:{ SQLiteException -> 0x0088 }
            if (r2 != 0) goto L_0x006d
            java.lang.String r2 = ""
            r12.title = r2     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.String r2 = ""
            r12.contentDescription = r2     // Catch:{ SQLiteException -> 0x0088 }
            goto L_0x0079
        L_0x006d:
            com.android.launcher3.compat.UserManagerCompat r2 = r10.mUserManager     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.CharSequence r3 = r12.title     // Catch:{ SQLiteException -> 0x0088 }
            android.os.UserHandle r4 = r11.user     // Catch:{ SQLiteException -> 0x0088 }
            java.lang.CharSequence r2 = r2.getBadgedLabelForUser(r3, r4)     // Catch:{ SQLiteException -> 0x0088 }
            r12.contentDescription = r2     // Catch:{ SQLiteException -> 0x0088 }
        L_0x0079:
            if (r0 == 0) goto L_0x007f
            r0.close()
        L_0x007f:
            return r5
        L_0x0080:
            if (r0 == 0) goto L_0x0093
        L_0x0082:
            r0.close()
            goto L_0x0093
        L_0x0086:
            r1 = move-exception
            goto L_0x0094
        L_0x0088:
            r2 = move-exception
            java.lang.String r3 = "Launcher.IconCache"
            java.lang.String r4 = "Error reading icon cache"
            android.util.Log.d(r3, r4, r2)     // Catch:{ all -> 0x0086 }
            if (r0 == 0) goto L_0x0093
            goto L_0x0082
        L_0x0093:
            return r1
        L_0x0094:
            if (r0 == 0) goto L_0x0099
            r0.close()
        L_0x0099:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.IconCache.getEntryFromDB(com.android.launcher3.util.ComponentKey, com.android.launcher3.IconCache$CacheEntry, boolean):boolean");
    }

    public static abstract class IconLoadRequest implements Runnable {
        private final Runnable mEndRunnable;
        private boolean mEnded = false;
        private final Handler mHandler;

        IconLoadRequest(Handler handler, Runnable endRunnable) {
            this.mHandler = handler;
            this.mEndRunnable = endRunnable;
        }

        public void cancel() {
            this.mHandler.removeCallbacks(this);
            onEnd();
        }

        public void onEnd() {
            if (!this.mEnded) {
                this.mEnded = true;
                this.mEndRunnable.run();
            }
        }
    }

    class SerializedIconUpdateTask implements Runnable {
        private final Stack<LauncherActivityInfo> mAppsToAdd;
        private final Stack<LauncherActivityInfo> mAppsToUpdate;
        private final HashMap<String, PackageInfo> mPkgInfoMap;
        private final HashSet<String> mUpdatedPackages = new HashSet<>();
        private final long mUserSerial;

        SerializedIconUpdateTask(long userSerial, HashMap<String, PackageInfo> pkgInfoMap, Stack<LauncherActivityInfo> appsToAdd, Stack<LauncherActivityInfo> appsToUpdate) {
            this.mUserSerial = userSerial;
            this.mPkgInfoMap = pkgInfoMap;
            this.mAppsToAdd = appsToAdd;
            this.mAppsToUpdate = appsToUpdate;
        }

        public void run() {
            if (!this.mAppsToUpdate.isEmpty()) {
                LauncherActivityInfo app = this.mAppsToUpdate.pop();
                String pkg = app.getComponentName().getPackageName();
                IconCache.this.addIconToDBAndMemCache(app, this.mPkgInfoMap.get(pkg), this.mUserSerial, true);
                this.mUpdatedPackages.add(pkg);
                if (this.mAppsToUpdate.isEmpty() && !this.mUpdatedPackages.isEmpty()) {
                    LauncherAppState.getInstance(IconCache.this.mContext).getModel().onPackageIconsUpdated(this.mUpdatedPackages, IconCache.this.mUserManager.getUserForSerialNumber(this.mUserSerial));
                }
                scheduleNext();
            } else if (!this.mAppsToAdd.isEmpty()) {
                LauncherActivityInfo app2 = this.mAppsToAdd.pop();
                PackageInfo info = this.mPkgInfoMap.get(app2.getComponentName().getPackageName());
                if (info != null) {
                    IconCache.this.addIconToDBAndMemCache(app2, info, this.mUserSerial, false);
                }
                if (!this.mAppsToAdd.isEmpty()) {
                    scheduleNext();
                }
            }
        }

        public void scheduleNext() {
            IconCache.this.mWorkerHandler.postAtTime(this, IconCache.ICON_UPDATE_TOKEN, SystemClock.uptimeMillis() + 1);
        }
    }

    private static final class IconDB extends SQLiteCacheHelper {
        private static final String COLUMN_COMPONENT = "componentName";
        private static final String COLUMN_ICON = "icon";
        private static final String COLUMN_ICON_COLOR = "icon_color";
        private static final String COLUMN_ICON_LOW_RES = "icon_low_res";
        private static final String COLUMN_LABEL = "label";
        private static final String COLUMN_LAST_UPDATED = "lastUpdated";
        private static final String COLUMN_ROWID = "rowid";
        private static final String COLUMN_SYSTEM_STATE = "system_state";
        private static final String COLUMN_USER = "profileId";
        private static final String COLUMN_VERSION = "version";
        private static final int RELEASE_VERSION = 24;
        private static final String TABLE_NAME = "icons";

        public IconDB(Context context, int iconPixelSize) {
            super(context, LauncherFiles.APP_ICONS_DB, 1572864 + iconPixelSize, TABLE_NAME);
        }

        /* access modifiers changed from: protected */
        public void onCreateTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS icons (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, icon BLOB, icon_low_res BLOB, icon_color INTEGER NOT NULL DEFAULT 0, label TEXT, system_state TEXT, PRIMARY KEY (componentName, profileId) );");
        }
    }

    private ContentValues newContentValues(Bitmap icon, Bitmap lowResIcon, int iconColor, String label, String packageName) {
        ContentValues values = new ContentValues();
        values.put(LauncherSettings.BaseLauncherColumns.ICON, Utilities.flattenBitmap(icon));
        values.put("icon_low_res", Utilities.flattenBitmap(lowResIcon));
        values.put("icon_color", Integer.valueOf(iconColor));
        values.put("label", label);
        values.put("system_state", this.mIconProvider.getIconSystemState(packageName));
        return values;
    }

    private Bitmap generateLowResIcon(Bitmap icon) {
        return Bitmap.createScaledBitmap(icon, icon.getWidth() / 5, icon.getHeight() / 5, true);
    }

    private static Bitmap loadIconNoResize(Cursor c, int iconIndex, BitmapFactory.Options options) {
        byte[] data = c.getBlob(iconIndex);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Exception e) {
            return null;
        }
    }

    private class ActivityInfoProvider extends Provider<LauncherActivityInfo> {
        private final Intent mIntent;
        private final UserHandle mUser;

        public ActivityInfoProvider(Intent intent, UserHandle user) {
            this.mIntent = intent;
            this.mUser = user;
        }

        public LauncherActivityInfo get() {
            return IconCache.this.mLauncherApps.resolveActivity(this.mIntent, this.mUser);
        }
    }
}
