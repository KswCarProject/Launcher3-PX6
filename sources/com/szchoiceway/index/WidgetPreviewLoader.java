package com.szchoiceway.index;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class WidgetPreviewLoader {
    static final String ANDROID_INCREMENTAL_VERSION_NAME_KEY = "android.incremental.version";
    private static final String SHORTCUT_PREFIX = "Shortcut:";
    static final String TAG = "WidgetPreviewLoader";
    private static final String WIDGET_PREFIX = "Widget:";
    /* access modifiers changed from: private */
    public static HashSet<String> sInvalidPackages = new HashSet<>();
    private int mAppIconSize;
    private CanvasCache mCachedAppWidgetPreviewCanvas = new CanvasCache();
    private RectCache mCachedAppWidgetPreviewDestRect = new RectCache();
    private PaintCache mCachedAppWidgetPreviewPaint = new PaintCache();
    private RectCache mCachedAppWidgetPreviewSrcRect = new RectCache();
    private BitmapFactoryOptionsCache mCachedBitmapFactoryOptions = new BitmapFactoryOptionsCache();
    private String mCachedSelectQuery;
    private BitmapCache mCachedShortcutPreviewBitmap = new BitmapCache();
    private CanvasCache mCachedShortcutPreviewCanvas = new CanvasCache();
    private PaintCache mCachedShortcutPreviewPaint = new PaintCache();
    private Context mContext;
    private CacheDb mDb;
    private IconCache mIconCache;
    private Launcher mLauncher;
    private HashMap<String, WeakReference<Bitmap>> mLoadedPreviews;
    private PackageManager mPackageManager;
    private int mPreviewBitmapHeight;
    private int mPreviewBitmapWidth;
    private String mSize;
    private ArrayList<SoftReference<Bitmap>> mUnusedBitmaps;
    private PagedViewCellLayout mWidgetSpacingLayout;
    private final float sWidgetPreviewIconPaddingPercentage = 0.25f;

    public WidgetPreviewLoader(Launcher launcher) {
        this.mLauncher = launcher;
        this.mContext = launcher;
        this.mPackageManager = this.mContext.getPackageManager();
        this.mAppIconSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
        LauncherApplication app = (LauncherApplication) launcher.getApplicationContext();
        this.mIconCache = app.getIconCache();
        this.mDb = app.getWidgetPreviewCacheDb();
        this.mLoadedPreviews = new HashMap<>();
        this.mUnusedBitmaps = new ArrayList<>();
        SharedPreferences sp = launcher.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0);
        String lastVersionName = sp.getString(ANDROID_INCREMENTAL_VERSION_NAME_KEY, (String) null);
        String versionName = Build.VERSION.INCREMENTAL;
        if (!versionName.equals(lastVersionName)) {
            clearDb();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ANDROID_INCREMENTAL_VERSION_NAME_KEY, versionName);
            editor.commit();
        }
    }

    public void setPreviewSize(int previewWidth, int previewHeight, PagedViewCellLayout widgetSpacingLayout) {
        this.mPreviewBitmapWidth = previewWidth;
        this.mPreviewBitmapHeight = previewHeight;
        this.mSize = previewWidth + "x" + previewHeight;
        this.mWidgetSpacingLayout = widgetSpacingLayout;
    }

    public Bitmap getPreview(final Object o) {
        boolean packageValid;
        String name = getObjectName(o);
        synchronized (sInvalidPackages) {
            packageValid = !sInvalidPackages.contains(getObjectPackage(o));
        }
        if (!packageValid) {
            return null;
        }
        if (packageValid) {
            synchronized (this.mLoadedPreviews) {
                if (this.mLoadedPreviews.containsKey(name) && this.mLoadedPreviews.get(name).get() != null) {
                    Bitmap bitmap = (Bitmap) this.mLoadedPreviews.get(name).get();
                    return bitmap;
                }
            }
        }
        Bitmap unusedBitmap = null;
        synchronized (this.mUnusedBitmaps) {
            while (true) {
                if (unusedBitmap != null) {
                    if (unusedBitmap.isMutable() && unusedBitmap.getWidth() == this.mPreviewBitmapWidth && unusedBitmap.getHeight() == this.mPreviewBitmapHeight) {
                        break;
                    }
                }
                if (this.mUnusedBitmaps.size() <= 0) {
                    break;
                }
                unusedBitmap = (Bitmap) this.mUnusedBitmaps.remove(0).get();
            }
            if (unusedBitmap != null) {
                Canvas c = (Canvas) this.mCachedAppWidgetPreviewCanvas.get();
                c.setBitmap(unusedBitmap);
                c.drawColor(0, PorterDuff.Mode.CLEAR);
                c.setBitmap((Bitmap) null);
            }
        }
        if (unusedBitmap == null) {
            unusedBitmap = Bitmap.createBitmap(this.mPreviewBitmapWidth, this.mPreviewBitmapHeight, Bitmap.Config.ARGB_8888);
        }
        Bitmap preview = null;
        if (packageValid) {
            preview = readFromDb(name, unusedBitmap);
        }
        if (preview != null) {
            synchronized (this.mLoadedPreviews) {
                this.mLoadedPreviews.put(name, new WeakReference(preview));
            }
            return preview;
        }
        final Bitmap generatedPreview = generatePreview(o, unusedBitmap);
        Bitmap preview2 = generatedPreview;
        if (preview2 != unusedBitmap) {
            throw new RuntimeException("generatePreview is not recycling the bitmap " + o);
        }
        synchronized (this.mLoadedPreviews) {
            this.mLoadedPreviews.put(name, new WeakReference(preview2));
        }
        new AsyncTask<Void, Void, Void>() {
            public Void doInBackground(Void... args) {
                WidgetPreviewLoader.this.writeToDb(o, generatedPreview);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
        return preview2;
    }

    public void recycleBitmap(Object o, Bitmap bitmapToRecycle) {
        String name = getObjectName(o);
        synchronized (this.mLoadedPreviews) {
            if (this.mLoadedPreviews.containsKey(name)) {
                Bitmap b = (Bitmap) this.mLoadedPreviews.get(name).get();
                if (b == bitmapToRecycle) {
                    this.mLoadedPreviews.remove(name);
                    if (bitmapToRecycle.isMutable()) {
                        synchronized (this.mUnusedBitmaps) {
                            this.mUnusedBitmaps.add(new SoftReference(b));
                        }
                    }
                } else {
                    throw new RuntimeException("Bitmap passed in doesn't match up");
                }
            }
        }
    }

    static class CacheDb extends SQLiteOpenHelper {
        static final String COLUMN_NAME = "name";
        static final String COLUMN_PREVIEW_BITMAP = "preview_bitmap";
        static final String COLUMN_SIZE = "size";
        static final String DB_NAME = "widgetpreviews.db";
        static final int DB_VERSION = 2;
        static final String TABLE_NAME = "shortcut_and_widget_previews";
        Context mContext;

        public CacheDb(Context context) {
            super(context, new File(context.getCacheDir(), DB_NAME).getPath(), (SQLiteDatabase.CursorFactory) null, 2);
            this.mContext = context;
        }

        public void onCreate(SQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS shortcut_and_widget_previews (name TEXT NOT NULL, size TEXT NOT NULL, preview_bitmap BLOB NOT NULL, PRIMARY KEY (name, size) );");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                db.execSQL("DELETE FROM shortcut_and_widget_previews");
            }
        }
    }

    private static String getObjectName(Object o) {
        StringBuilder sb = new StringBuilder();
        if (o instanceof AppWidgetProviderInfo) {
            sb.append(WIDGET_PREFIX);
            sb.append(((AppWidgetProviderInfo) o).provider.flattenToString());
            String output = sb.toString();
            sb.setLength(0);
            return output;
        }
        sb.append(SHORTCUT_PREFIX);
        ResolveInfo info = (ResolveInfo) o;
        sb.append(new ComponentName(info.activityInfo.packageName, info.activityInfo.name).flattenToString());
        String output2 = sb.toString();
        sb.setLength(0);
        return output2;
    }

    private String getObjectPackage(Object o) {
        if (o instanceof AppWidgetProviderInfo) {
            return ((AppWidgetProviderInfo) o).provider.getPackageName();
        }
        return ((ResolveInfo) o).activityInfo.packageName;
    }

    /* access modifiers changed from: private */
    public void writeToDb(Object o, Bitmap preview) {
        String name = getObjectName(o);
        SQLiteDatabase db = this.mDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InstallShortcutReceiver.NAME_KEY, name);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview.compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put("preview_bitmap", stream.toByteArray());
        values.put("size", this.mSize);
        db.insert("shortcut_and_widget_previews", (String) null, values);
    }

    private void clearDb() {
        this.mDb.getWritableDatabase().delete("shortcut_and_widget_previews", (String) null, (String[]) null);
    }

    public static void removeFromDb(final CacheDb cacheDb, final String packageName) {
        synchronized (sInvalidPackages) {
            sInvalidPackages.add(packageName);
        }
        new AsyncTask<Void, Void, Void>() {
            public Void doInBackground(Void... args) {
                cacheDb.getWritableDatabase().delete("shortcut_and_widget_previews", "name LIKE ? OR name LIKE ?", new String[]{WidgetPreviewLoader.WIDGET_PREFIX + packageName + "/%", WidgetPreviewLoader.SHORTCUT_PREFIX + packageName + "/%"});
                synchronized (WidgetPreviewLoader.sInvalidPackages) {
                    WidgetPreviewLoader.sInvalidPackages.remove(packageName);
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
    }

    private Bitmap readFromDb(String name, Bitmap b) {
        if (this.mCachedSelectQuery == null) {
            this.mCachedSelectQuery = "name = ? AND size = ?";
        }
        Cursor result = this.mDb.getReadableDatabase().query("shortcut_and_widget_previews", new String[]{"preview_bitmap"}, this.mCachedSelectQuery, new String[]{name, this.mSize}, (String) null, (String) null, (String) null, (String) null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            byte[] blob = result.getBlob(0);
            result.close();
            BitmapFactory.Options opts = (BitmapFactory.Options) this.mCachedBitmapFactoryOptions.get();
            opts.inBitmap = b;
            opts.inSampleSize = 1;
            return BitmapFactory.decodeByteArray(blob, 0, blob.length, opts);
        }
        result.close();
        return null;
    }

    public Bitmap generatePreview(Object info, Bitmap preview) {
        if (preview != null && (preview.getWidth() != this.mPreviewBitmapWidth || preview.getHeight() != this.mPreviewBitmapHeight)) {
            throw new RuntimeException("Improperly sized bitmap passed as argument");
        } else if (info instanceof AppWidgetProviderInfo) {
            return generateWidgetPreview((AppWidgetProviderInfo) info, preview);
        } else {
            return generateShortcutPreview((ResolveInfo) info, this.mPreviewBitmapWidth, this.mPreviewBitmapHeight, preview);
        }
    }

    public Bitmap generateWidgetPreview(AppWidgetProviderInfo info, Bitmap preview) {
        int[] cellSpans = Launcher.getSpanForWidget((Context) this.mLauncher, info);
        return generateWidgetPreview(info.provider, info.previewImage, info.icon, cellSpans[0], cellSpans[1], maxWidthForWidgetPreview(cellSpans[0]), maxHeightForWidgetPreview(cellSpans[1]), preview, (int[]) null);
    }

    public int maxWidthForWidgetPreview(int spanX) {
        return Math.min(this.mPreviewBitmapWidth, this.mWidgetSpacingLayout.estimateCellWidth(spanX));
    }

    public int maxHeightForWidgetPreview(int spanY) {
        return Math.min(this.mPreviewBitmapHeight, this.mWidgetSpacingLayout.estimateCellHeight(spanY));
    }

    public Bitmap generateWidgetPreview(ComponentName provider, int previewImage, int iconId, int cellHSpan, int cellVSpan, int maxPreviewWidth, int maxPreviewHeight, Bitmap preview, int[] preScaledWidthOut) {
        int previewWidth;
        int previewHeight;
        String packageName = provider.getPackageName();
        if (maxPreviewWidth < 0) {
            maxPreviewWidth = Integer.MAX_VALUE;
        }
        if (maxPreviewHeight < 0) {
        }
        Drawable drawable = null;
        if (previewImage != 0 && (drawable = this.mPackageManager.getDrawable(packageName, previewImage, (ApplicationInfo) null)) == null) {
            Log.w(TAG, "Can't load widget preview drawable 0x" + Integer.toHexString(previewImage) + " for provider: " + provider);
        }
        Bitmap defaultPreview = null;
        boolean widgetPreviewExists = drawable != null;
        if (widgetPreviewExists) {
            previewWidth = drawable.getIntrinsicWidth();
            previewHeight = drawable.getIntrinsicHeight();
        } else {
            if (cellHSpan < 1) {
                cellHSpan = 1;
            }
            if (cellVSpan < 1) {
                cellVSpan = 1;
            }
            BitmapDrawable previewDrawable = (BitmapDrawable) this.mContext.getResources().getDrawable(R.drawable.widget_preview_tile);
            int previewDrawableWidth = previewDrawable.getIntrinsicWidth();
            int previewDrawableHeight = previewDrawable.getIntrinsicHeight();
            previewWidth = previewDrawableWidth * cellHSpan;
            previewHeight = previewDrawableHeight * cellVSpan;
            defaultPreview = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
            Canvas c = (Canvas) this.mCachedAppWidgetPreviewCanvas.get();
            c.setBitmap(defaultPreview);
            previewDrawable.setBounds(0, 0, previewWidth, previewHeight);
            previewDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            previewDrawable.draw(c);
            c.setBitmap((Bitmap) null);
            float iconScale = Math.min(((float) Math.min(previewWidth, previewHeight)) / ((float) (this.mAppIconSize + (((int) (((float) this.mAppIconSize) * 0.25f)) * 2))), 1.0f);
            Drawable icon = null;
            try {
                int hoffset = (int) ((((float) previewDrawableWidth) - (((float) this.mAppIconSize) * iconScale)) / 2.0f);
                int yoffset = (int) ((((float) previewDrawableHeight) - (((float) this.mAppIconSize) * iconScale)) / 2.0f);
                if (iconId > 0) {
                    icon = this.mIconCache.getFullResIcon(packageName, iconId);
                }
                if (icon != null) {
                    renderDrawableToBitmap(icon, defaultPreview, hoffset, yoffset, (int) (((float) this.mAppIconSize) * iconScale), (int) (((float) this.mAppIconSize) * iconScale));
                }
            } catch (Resources.NotFoundException e) {
            }
        }
        float scale = 1.0f;
        if (preScaledWidthOut != null) {
            preScaledWidthOut[0] = previewWidth;
        }
        if (previewWidth > maxPreviewWidth) {
            scale = ((float) maxPreviewWidth) / ((float) previewWidth);
        }
        if (scale != 1.0f) {
            previewWidth = (int) (((float) previewWidth) * scale);
            previewHeight = (int) (((float) previewHeight) * scale);
        }
        if (preview == null) {
            preview = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        }
        int x = (preview.getWidth() - previewWidth) / 2;
        if (widgetPreviewExists) {
            renderDrawableToBitmap(drawable, preview, x, 0, previewWidth, previewHeight);
        } else {
            Canvas c2 = (Canvas) this.mCachedAppWidgetPreviewCanvas.get();
            Rect src = (Rect) this.mCachedAppWidgetPreviewSrcRect.get();
            Rect dest = (Rect) this.mCachedAppWidgetPreviewDestRect.get();
            c2.setBitmap(preview);
            src.set(0, 0, defaultPreview.getWidth(), defaultPreview.getHeight());
            dest.set(x, 0, x + previewWidth, previewHeight);
            Paint p = (Paint) this.mCachedAppWidgetPreviewPaint.get();
            if (p == null) {
                p = new Paint();
                p.setFilterBitmap(true);
                this.mCachedAppWidgetPreviewPaint.set(p);
            }
            c2.drawBitmap(defaultPreview, src, dest, p);
            c2.setBitmap((Bitmap) null);
        }
        return preview;
    }

    private Bitmap generateShortcutPreview(ResolveInfo info, int maxWidth, int maxHeight, Bitmap preview) {
        Bitmap tempBitmap = (Bitmap) this.mCachedShortcutPreviewBitmap.get();
        Canvas c = (Canvas) this.mCachedShortcutPreviewCanvas.get();
        if (tempBitmap != null && tempBitmap.getWidth() == maxWidth && tempBitmap.getHeight() == maxHeight) {
            c.setBitmap(tempBitmap);
            c.drawColor(0, PorterDuff.Mode.CLEAR);
            c.setBitmap((Bitmap) null);
        } else {
            tempBitmap = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
            this.mCachedShortcutPreviewBitmap.set(tempBitmap);
        }
        Drawable icon = this.mIconCache.getFullResIcon(info);
        int paddingTop = this.mContext.getResources().getDimensionPixelOffset(R.dimen.shortcut_preview_padding_top);
        int paddingLeft = this.mContext.getResources().getDimensionPixelOffset(R.dimen.shortcut_preview_padding_left);
        int scaledIconWidth = (maxWidth - paddingLeft) - this.mContext.getResources().getDimensionPixelOffset(R.dimen.shortcut_preview_padding_right);
        renderDrawableToBitmap(icon, tempBitmap, paddingLeft, paddingTop, scaledIconWidth, scaledIconWidth);
        if (preview == null || (preview.getWidth() == maxWidth && preview.getHeight() == maxHeight)) {
            if (preview == null) {
                preview = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
            }
            c.setBitmap(preview);
            Paint p = (Paint) this.mCachedShortcutPreviewPaint.get();
            if (p == null) {
                p = new Paint();
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0.0f);
                p.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                p.setAlpha(15);
                this.mCachedShortcutPreviewPaint.set(p);
            }
            c.drawBitmap(tempBitmap, 0.0f, 0.0f, p);
            c.setBitmap((Bitmap) null);
            renderDrawableToBitmap(icon, preview, 0, 0, this.mAppIconSize, this.mAppIconSize);
            return preview;
        }
        throw new RuntimeException("Improperly sized bitmap passed as argument");
    }

    public static void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x, int y, int w, int h) {
        renderDrawableToBitmap(d, bitmap, x, y, w, h, 1.0f);
    }

    private static void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x, int y, int w, int h, float scale) {
        if (bitmap != null) {
            Canvas c = new Canvas(bitmap);
            c.scale(scale, scale);
            Rect oldBounds = d.copyBounds();
            d.setBounds(x, y, x + w, y + h);
            d.draw(c);
            d.setBounds(oldBounds);
            c.setBitmap((Bitmap) null);
        }
    }
}
