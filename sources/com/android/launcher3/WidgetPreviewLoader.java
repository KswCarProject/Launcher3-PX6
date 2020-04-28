package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.graphics.ShadowGenerator;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.SQLiteCacheHelper;
import com.android.launcher3.widget.WidgetCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class WidgetPreviewLoader {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetPreviewLoader";
    private final Context mContext;
    private final CacheDb mDb;
    private final IconCache mIconCache;
    private final MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();
    private final HashMap<String, long[]> mPackageVersions = new HashMap<>();
    final Set<Bitmap> mUnusedBitmaps = Collections.newSetFromMap(new WeakHashMap());
    private final UserManagerCompat mUserManager;
    private final AppWidgetManagerCompat mWidgetManager;
    final Handler mWorkerHandler;

    public WidgetPreviewLoader(Context context, IconCache iconCache) {
        this.mContext = context;
        this.mIconCache = iconCache;
        this.mWidgetManager = AppWidgetManagerCompat.getInstance(context);
        this.mUserManager = UserManagerCompat.getInstance(context);
        this.mDb = new CacheDb(context);
        this.mWorkerHandler = new Handler(LauncherModel.getWorkerLooper());
    }

    public CancellationSignal getPreview(WidgetItem item, int previewWidth, int previewHeight, WidgetCell caller) {
        PreviewLoadTask task = new PreviewLoadTask(new WidgetCacheKey(item.componentName, item.user, previewWidth + "x" + previewHeight), item, previewWidth, previewHeight, caller);
        task.executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
        CancellationSignal signal = new CancellationSignal();
        signal.setOnCancelListener(task);
        return signal;
    }

    private static class CacheDb extends SQLiteCacheHelper {
        private static final String COLUMN_COMPONENT = "componentName";
        private static final String COLUMN_LAST_UPDATED = "lastUpdated";
        private static final String COLUMN_PACKAGE = "packageName";
        private static final String COLUMN_PREVIEW_BITMAP = "preview_bitmap";
        private static final String COLUMN_SIZE = "size";
        private static final String COLUMN_USER = "profileId";
        private static final String COLUMN_VERSION = "version";
        private static final int DB_VERSION = 9;
        private static final String TABLE_NAME = "shortcut_and_widget_previews";

        public CacheDb(Context context) {
            super(context, LauncherFiles.WIDGET_PREVIEWS_DB, 9, TABLE_NAME);
        }

        public void onCreateTable(SQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS shortcut_and_widget_previews (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, size TEXT NOT NULL, packageName TEXT NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, preview_bitmap BLOB, PRIMARY KEY (componentName, profileId, size) );");
        }
    }

    /* access modifiers changed from: package-private */
    public void writeToDb(WidgetCacheKey key, long[] versions, Bitmap preview) {
        ContentValues values = new ContentValues();
        values.put("componentName", key.componentName.flattenToShortString());
        values.put(LauncherSettings.Favorites.PROFILE_ID, Long.valueOf(this.mUserManager.getSerialNumberForUser(key.user)));
        values.put("size", key.size);
        values.put("packageName", key.componentName.getPackageName());
        values.put("version", Long.valueOf(versions[0]));
        values.put("lastUpdated", Long.valueOf(versions[1]));
        values.put("preview_bitmap", Utilities.flattenBitmap(preview));
        this.mDb.insertOrReplace(values);
    }

    public void removePackage(String packageName, UserHandle user) {
        removePackage(packageName, user, this.mUserManager.getSerialNumberForUser(user));
    }

    private void removePackage(String packageName, UserHandle user, long userSerial) {
        synchronized (this.mPackageVersions) {
            this.mPackageVersions.remove(packageName);
        }
        this.mDb.delete("packageName = ? AND profileId = ?", new String[]{packageName, Long.toString(userSerial)});
    }

    public void removeObsoletePreviews(ArrayList<? extends ComponentKey> list, @Nullable PackageUserKey packageUser) {
        long passedUserId;
        int i;
        PackageUserKey packageUserKey = packageUser;
        Preconditions.assertWorkerThread();
        LongSparseArray longSparseArray = new LongSparseArray();
        Iterator<? extends ComponentKey> it = list.iterator();
        while (it.hasNext()) {
            ComponentKey key = (ComponentKey) it.next();
            long userId = this.mUserManager.getSerialNumberForUser(key.user);
            HashSet<String> packages = (HashSet) longSparseArray.get(userId);
            if (packages == null) {
                packages = new HashSet<>();
                longSparseArray.put(userId, packages);
            }
            packages.add(key.componentName.getPackageName());
        }
        LongSparseArray longSparseArray2 = new LongSparseArray();
        if (packageUserKey == null) {
            passedUserId = 0;
        } else {
            passedUserId = this.mUserManager.getSerialNumberForUser(packageUserKey.mUser);
        }
        Cursor c = null;
        try {
            c = this.mDb.query(new String[]{LauncherSettings.Favorites.PROFILE_ID, "packageName", "lastUpdated", "version"}, (String) null, (String[]) null);
            while (true) {
                i = 0;
                if (!c.moveToNext()) {
                    break;
                }
                long userId2 = c.getLong(0);
                String pkg = c.getString(1);
                long lastUpdated = c.getLong(2);
                long version = c.getLong(3);
                if (packageUserKey != null) {
                    if (pkg.equals(packageUserKey.mPackageName)) {
                        if (userId2 != passedUserId) {
                        }
                    }
                }
                HashSet<String> packages2 = (HashSet) longSparseArray.get(userId2);
                if (packages2 != null && packages2.contains(pkg)) {
                    long[] versions = getPackageVersion(pkg);
                    if (versions[0] == version && versions[1] == lastUpdated) {
                    }
                }
                HashSet<String> packages3 = (HashSet) longSparseArray2.get(userId2);
                if (packages3 == null) {
                    packages3 = new HashSet<>();
                    longSparseArray2.put(userId2, packages3);
                }
                packages3.add(pkg);
            }
            while (true) {
                int i2 = i;
                if (i2 >= longSparseArray2.size()) {
                    break;
                }
                long userId3 = longSparseArray2.keyAt(i2);
                UserHandle user = this.mUserManager.getUserForSerialNumber(userId3);
                Iterator it2 = ((HashSet) longSparseArray2.valueAt(i2)).iterator();
                while (it2.hasNext()) {
                    removePackage((String) it2.next(), user, userId3);
                }
                i = i2 + 1;
            }
            if (c == null) {
                return;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating widget previews", e);
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0069, code lost:
        if (r1 != null) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006b, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0079, code lost:
        if (r1 == null) goto L_0x007c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007c, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap readFromDb(com.android.launcher3.WidgetPreviewLoader.WidgetCacheKey r11, android.graphics.Bitmap r12, com.android.launcher3.WidgetPreviewLoader.PreviewLoadTask r13) {
        /*
            r10 = this;
            r0 = 0
            r1 = r0
            com.android.launcher3.WidgetPreviewLoader$CacheDb r2 = r10.mDb     // Catch:{ SQLException -> 0x0071 }
            java.lang.String r3 = "preview_bitmap"
            java.lang.String[] r3 = new java.lang.String[]{r3}     // Catch:{ SQLException -> 0x0071 }
            java.lang.String r4 = "componentName = ? AND profileId = ? AND size = ?"
            r5 = 3
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ SQLException -> 0x0071 }
            android.content.ComponentName r6 = r11.componentName     // Catch:{ SQLException -> 0x0071 }
            java.lang.String r6 = r6.flattenToShortString()     // Catch:{ SQLException -> 0x0071 }
            r7 = 0
            r5[r7] = r6     // Catch:{ SQLException -> 0x0071 }
            r6 = 1
            com.android.launcher3.compat.UserManagerCompat r8 = r10.mUserManager     // Catch:{ SQLException -> 0x0071 }
            android.os.UserHandle r9 = r11.user     // Catch:{ SQLException -> 0x0071 }
            long r8 = r8.getSerialNumberForUser(r9)     // Catch:{ SQLException -> 0x0071 }
            java.lang.String r8 = java.lang.Long.toString(r8)     // Catch:{ SQLException -> 0x0071 }
            r5[r6] = r8     // Catch:{ SQLException -> 0x0071 }
            r6 = 2
            java.lang.String r8 = r11.size     // Catch:{ SQLException -> 0x0071 }
            r5[r6] = r8     // Catch:{ SQLException -> 0x0071 }
            android.database.Cursor r2 = r2.query(r3, r4, r5)     // Catch:{ SQLException -> 0x0071 }
            r1 = r2
            boolean r2 = r13.isCancelled()     // Catch:{ SQLException -> 0x0071 }
            if (r2 == 0) goto L_0x003e
            if (r1 == 0) goto L_0x003d
            r1.close()
        L_0x003d:
            return r0
        L_0x003e:
            boolean r2 = r1.moveToNext()     // Catch:{ SQLException -> 0x0071 }
            if (r2 == 0) goto L_0x0069
            byte[] r2 = r1.getBlob(r7)     // Catch:{ SQLException -> 0x0071 }
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ SQLException -> 0x0071 }
            r3.<init>()     // Catch:{ SQLException -> 0x0071 }
            r3.inBitmap = r12     // Catch:{ SQLException -> 0x0071 }
            boolean r4 = r13.isCancelled()     // Catch:{ Exception -> 0x0061 }
            if (r4 != 0) goto L_0x0060
            int r4 = r2.length     // Catch:{ Exception -> 0x0061 }
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeByteArray(r2, r7, r4, r3)     // Catch:{ Exception -> 0x0061 }
            if (r1 == 0) goto L_0x005f
            r1.close()
        L_0x005f:
            return r4
        L_0x0060:
            goto L_0x0069
        L_0x0061:
            r4 = move-exception
            if (r1 == 0) goto L_0x0068
            r1.close()
        L_0x0068:
            return r0
        L_0x0069:
            if (r1 == 0) goto L_0x007c
        L_0x006b:
            r1.close()
            goto L_0x007c
        L_0x006f:
            r0 = move-exception
            goto L_0x007d
        L_0x0071:
            r2 = move-exception
            java.lang.String r3 = "WidgetPreviewLoader"
            java.lang.String r4 = "Error loading preview from DB"
            android.util.Log.w(r3, r4, r2)     // Catch:{ all -> 0x006f }
            if (r1 == 0) goto L_0x007c
            goto L_0x006b
        L_0x007c:
            return r0
        L_0x007d:
            if (r1 == 0) goto L_0x0082
            r1.close()
        L_0x0082:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.WidgetPreviewLoader.readFromDb(com.android.launcher3.WidgetPreviewLoader$WidgetCacheKey, android.graphics.Bitmap, com.android.launcher3.WidgetPreviewLoader$PreviewLoadTask):android.graphics.Bitmap");
    }

    /* access modifiers changed from: private */
    public Bitmap generatePreview(BaseActivity launcher, WidgetItem item, Bitmap recycle, int previewWidth, int previewHeight) {
        if (item.widgetInfo != null) {
            return generateWidgetPreview(launcher, item.widgetInfo, previewWidth, recycle, (int[]) null);
        }
        return generateShortcutPreview(launcher, item.activityInfo, previewWidth, previewHeight, recycle);
    }

    public Bitmap generateWidgetPreview(BaseActivity launcher, LauncherAppWidgetProviderInfo info, int maxPreviewWidth, Bitmap preview, int[] preScaledWidthOut) {
        int tileSize;
        int previewWidth;
        Drawable drawable;
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = info;
        Bitmap preview2 = preview;
        int maxPreviewWidth2 = maxPreviewWidth < 0 ? Integer.MAX_VALUE : maxPreviewWidth;
        Drawable drawable2 = null;
        if (launcherAppWidgetProviderInfo.previewImage != 0) {
            try {
                drawable = launcherAppWidgetProviderInfo.loadPreviewImage(this.mContext, 0);
            } catch (OutOfMemoryError e) {
                Log.w(TAG, "Error loading widget preview for: " + launcherAppWidgetProviderInfo.provider, e);
                drawable = null;
            }
            drawable2 = drawable;
            if (drawable2 != null) {
                drawable2 = mutateOnMainThread(drawable2);
            } else {
                Log.w(TAG, "Can't load widget preview drawable 0x" + Integer.toHexString(launcherAppWidgetProviderInfo.previewImage) + " for provider: " + launcherAppWidgetProviderInfo.provider);
            }
        }
        boolean widgetPreviewExists = drawable2 != null;
        int spanX = launcherAppWidgetProviderInfo.spanX;
        int spanY = launcherAppWidgetProviderInfo.spanY;
        if (!widgetPreviewExists || drawable2.getIntrinsicWidth() <= 0 || drawable2.getIntrinsicHeight() <= 0) {
            DeviceProfile dp = launcher.getDeviceProfile();
            int tileSize2 = Math.min(dp.cellWidthPx, dp.cellHeightPx);
            int previewWidth2 = tileSize2 * spanX;
            tileSize = tileSize2 * spanY;
            previewWidth = previewWidth2;
        } else {
            previewWidth = drawable2.getIntrinsicWidth();
            tileSize = drawable2.getIntrinsicHeight();
        }
        float scale = 1.0f;
        if (preScaledWidthOut != null) {
            preScaledWidthOut[0] = previewWidth;
        }
        if (previewWidth > maxPreviewWidth2) {
            scale = ((float) maxPreviewWidth2) / ((float) previewWidth);
        }
        if (scale != 1.0f) {
            previewWidth = Math.max((int) (((float) previewWidth) * scale), 1);
            tileSize = Math.max((int) (((float) tileSize) * scale), 1);
        }
        Canvas c = new Canvas();
        if (preview2 == null) {
            preview2 = Bitmap.createBitmap(previewWidth, tileSize, Bitmap.Config.ARGB_8888);
            c.setBitmap(preview2);
        } else {
            if (preview.getHeight() > tileSize) {
                preview2.reconfigure(preview.getWidth(), tileSize, preview.getConfig());
            }
            c.setBitmap(preview2);
            c.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        int x = (preview2.getWidth() - previewWidth) / 2;
        if (widgetPreviewExists) {
            drawable2.setBounds(x, 0, x + previewWidth, tileSize);
            drawable2.draw(c);
            int i = maxPreviewWidth2;
            Drawable drawable3 = drawable2;
            int i2 = x;
        } else {
            RectF boxRect = drawBoxWithShadow(c, previewWidth, tileSize);
            Paint p = new Paint(1);
            p.setStyle(Paint.Style.STROKE);
            int i3 = maxPreviewWidth2;
            p.setStrokeWidth(this.mContext.getResources().getDimension(R.dimen.widget_preview_cell_divider_width));
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            float t = boxRect.left;
            float tileSize3 = boxRect.width() / ((float) spanX);
            int i4 = 1;
            while (i4 < spanX) {
                t += tileSize3;
                c.drawLine(t, 0.0f, t, (float) tileSize, p);
                i4++;
                x = x;
                tileSize3 = tileSize3;
            }
            int i5 = x;
            float t2 = boxRect.top;
            float tileSize4 = boxRect.height() / ((float) spanY);
            float t3 = t2;
            int i6 = 1;
            while (true) {
                int i7 = i6;
                if (i7 < spanY) {
                    float t4 = t3 + tileSize4;
                    c.drawLine(0.0f, t4, (float) previewWidth, t4, p);
                    i6 = i7 + 1;
                    t3 = t4;
                } else {
                    try {
                        break;
                    } catch (Resources.NotFoundException e2) {
                        Paint paint = p;
                        float f = tileSize4;
                        Drawable drawable4 = drawable2;
                    }
                }
            }
            Paint paint2 = p;
            try {
                float f2 = tileSize4;
                try {
                    Drawable icon = this.mIconCache.getFullResIcon(launcherAppWidgetProviderInfo.provider.getPackageName(), launcherAppWidgetProviderInfo.icon);
                    if (icon != null) {
                        int appIconSize = launcher.getDeviceProfile().iconSizePx;
                        int i8 = appIconSize;
                        int iconSize = (int) Math.min(((float) appIconSize) * scale, Math.min(boxRect.width(), boxRect.height()));
                        Drawable icon2 = mutateOnMainThread(icon);
                        int hoffset = (previewWidth - iconSize) / 2;
                        int yoffset = (tileSize - iconSize) / 2;
                        Drawable drawable5 = drawable2;
                        try {
                            icon2.setBounds(hoffset, yoffset, hoffset + iconSize, yoffset + iconSize);
                            icon2.draw(c);
                        } catch (Resources.NotFoundException e3) {
                        }
                    }
                } catch (Resources.NotFoundException e4) {
                    Drawable drawable6 = drawable2;
                }
            } catch (Resources.NotFoundException e5) {
                float f3 = tileSize4;
                Drawable drawable7 = drawable2;
            }
            c.setBitmap((Bitmap) null);
        }
        return preview2;
    }

    private RectF drawBoxWithShadow(Canvas c, int width, int height) {
        Resources res = this.mContext.getResources();
        ShadowGenerator.Builder builder = new ShadowGenerator.Builder(-1);
        builder.shadowBlur = res.getDimension(R.dimen.widget_preview_shadow_blur);
        builder.radius = res.getDimension(R.dimen.widget_preview_corner_radius);
        builder.keyShadowDistance = res.getDimension(R.dimen.widget_preview_key_shadow_distance);
        builder.bounds.set(builder.shadowBlur, builder.shadowBlur, ((float) width) - builder.shadowBlur, (((float) height) - builder.shadowBlur) - builder.keyShadowDistance);
        builder.drawShadow(c);
        return builder.bounds;
    }

    private Bitmap generateShortcutPreview(BaseActivity launcher, ShortcutConfigActivityInfo info, int maxWidth, int maxHeight, Bitmap preview) {
        Bitmap preview2 = preview;
        int iconSize = launcher.getDeviceProfile().iconSizePx;
        int padding = launcher.getResources().getDimensionPixelSize(R.dimen.widget_preview_shortcut_padding);
        int size = (padding * 2) + iconSize;
        if (maxHeight < size) {
            ShortcutConfigActivityInfo shortcutConfigActivityInfo = info;
            int i = maxWidth;
        } else if (maxWidth >= size) {
            Canvas c = new Canvas();
            if (preview2 == null || preview.getWidth() < size || preview.getHeight() < size) {
                preview2 = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                c.setBitmap(preview2);
            } else {
                if (preview.getWidth() > size || preview.getHeight() > size) {
                    preview2.reconfigure(size, size, preview.getConfig());
                }
                c.setBitmap(preview2);
                c.drawColor(0, PorterDuff.Mode.CLEAR);
            }
            RectF boxRect = drawBoxWithShadow(c, size, size);
            LauncherIcons li = LauncherIcons.obtain(this.mContext);
            Bitmap icon = li.createScaledBitmapWithoutShadow(mutateOnMainThread(info.getFullResIcon(this.mIconCache)), 0);
            li.recycle();
            Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
            boxRect.set(0.0f, 0.0f, (float) iconSize, (float) iconSize);
            boxRect.offset((float) padding, (float) padding);
            c.drawBitmap(icon, src, boxRect, new Paint(3));
            c.setBitmap((Bitmap) null);
            return preview2;
        } else {
            ShortcutConfigActivityInfo shortcutConfigActivityInfo2 = info;
        }
        throw new RuntimeException("Max size is too small for preview");
    }

    private Drawable mutateOnMainThread(final Drawable drawable) {
        try {
            return (Drawable) this.mMainThreadExecutor.submit(new Callable<Drawable>() {
                public Drawable call() throws Exception {
                    return drawable.mutate();
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* access modifiers changed from: package-private */
    public long[] getPackageVersion(String packageName) {
        long[] versions;
        synchronized (this.mPackageVersions) {
            versions = this.mPackageVersions.get(packageName);
            if (versions == null) {
                versions = new long[2];
                try {
                    PackageInfo info = this.mContext.getPackageManager().getPackageInfo(packageName, 8192);
                    versions[0] = (long) info.versionCode;
                    versions[1] = info.lastUpdateTime;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "PackageInfo not found", e);
                }
                this.mPackageVersions.put(packageName, versions);
            }
        }
        return versions;
    }

    public class PreviewLoadTask extends AsyncTask<Void, Void, Bitmap> implements CancellationSignal.OnCancelListener {
        private final BaseActivity mActivity = BaseActivity.fromContext(this.mCaller.getContext());
        Bitmap mBitmapToRecycle;
        private final WidgetCell mCaller;
        private final WidgetItem mInfo;
        final WidgetCacheKey mKey;
        private final int mPreviewHeight;
        private final int mPreviewWidth;
        long[] mVersions;

        PreviewLoadTask(WidgetCacheKey key, WidgetItem info, int previewWidth, int previewHeight, WidgetCell caller) {
            this.mKey = key;
            this.mInfo = info;
            this.mPreviewHeight = previewHeight;
            this.mPreviewWidth = previewWidth;
            this.mCaller = caller;
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(Void... params) {
            Bitmap unusedBitmap = null;
            long[] jArr = null;
            if (isCancelled()) {
                return null;
            }
            synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                Iterator<Bitmap> it = WidgetPreviewLoader.this.mUnusedBitmaps.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Bitmap candidate = it.next();
                        if (candidate != null && candidate.isMutable() && candidate.getWidth() == this.mPreviewWidth && candidate.getHeight() == this.mPreviewHeight) {
                            unusedBitmap = candidate;
                            WidgetPreviewLoader.this.mUnusedBitmaps.remove(unusedBitmap);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (unusedBitmap == null) {
                unusedBitmap = Bitmap.createBitmap(this.mPreviewWidth, this.mPreviewHeight, Bitmap.Config.ARGB_8888);
            }
            if (isCancelled()) {
                return unusedBitmap;
            }
            Bitmap preview = WidgetPreviewLoader.this.readFromDb(this.mKey, unusedBitmap, this);
            if (isCancelled() || preview != null) {
                return preview;
            }
            if (this.mInfo.activityInfo == null || this.mInfo.activityInfo.isPersistable()) {
                jArr = WidgetPreviewLoader.this.getPackageVersion(this.mKey.componentName.getPackageName());
            }
            this.mVersions = jArr;
            return WidgetPreviewLoader.this.generatePreview(this.mActivity, this.mInfo, unusedBitmap, this.mPreviewWidth, this.mPreviewHeight);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(final Bitmap preview) {
            this.mCaller.applyPreview(preview);
            if (this.mVersions != null) {
                WidgetPreviewLoader.this.mWorkerHandler.post(new Runnable() {
                    public void run() {
                        if (!PreviewLoadTask.this.isCancelled()) {
                            WidgetPreviewLoader.this.writeToDb(PreviewLoadTask.this.mKey, PreviewLoadTask.this.mVersions, preview);
                            PreviewLoadTask.this.mBitmapToRecycle = preview;
                            return;
                        }
                        synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                            WidgetPreviewLoader.this.mUnusedBitmaps.add(preview);
                        }
                    }
                });
            } else {
                this.mBitmapToRecycle = preview;
            }
        }

        /* access modifiers changed from: protected */
        public void onCancelled(final Bitmap preview) {
            if (preview != null) {
                WidgetPreviewLoader.this.mWorkerHandler.post(new Runnable() {
                    public void run() {
                        synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                            WidgetPreviewLoader.this.mUnusedBitmaps.add(preview);
                        }
                    }
                });
            }
        }

        public void onCancel() {
            cancel(true);
            if (this.mBitmapToRecycle != null) {
                WidgetPreviewLoader.this.mWorkerHandler.post(new Runnable() {
                    public void run() {
                        synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                            WidgetPreviewLoader.this.mUnusedBitmaps.add(PreviewLoadTask.this.mBitmapToRecycle);
                        }
                        PreviewLoadTask.this.mBitmapToRecycle = null;
                    }
                });
            }
        }
    }

    private static final class WidgetCacheKey extends ComponentKey {
        final String size;

        public WidgetCacheKey(ComponentName componentName, UserHandle user, String size2) {
            super(componentName, user);
            this.size = size2;
        }

        public int hashCode() {
            return super.hashCode() ^ this.size.hashCode();
        }

        public boolean equals(Object o) {
            return super.equals(o) && ((WidgetCacheKey) o).size.equals(this.size);
        }
    }
}
