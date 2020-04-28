package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.TransactionTooLargeException;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class Utilities {
    public static final boolean ATLEAST_LOLLIPOP_MR1 = (Build.VERSION.SDK_INT >= 22);
    public static final boolean ATLEAST_MARSHMALLOW = (Build.VERSION.SDK_INT >= 23);
    public static final boolean ATLEAST_NOUGAT = (Build.VERSION.SDK_INT >= 24);
    public static final boolean ATLEAST_NOUGAT_MR1 = (Build.VERSION.SDK_INT >= 25);
    public static final boolean ATLEAST_OREO = (Build.VERSION.SDK_INT >= 26);
    public static final boolean ATLEAST_OREO_MR1 = (Build.VERSION.SDK_INT >= 27);
    public static final boolean ATLEAST_P = (Build.VERSION.SDK_INT >= 28);
    public static final int COLOR_EXTRACTION_JOB_ID = 1;
    private static final int CORE_POOL_SIZE = (CPU_COUNT + 1);
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final String EXTRA_WALLPAPER_OFFSET = "com.android.launcher3.WALLPAPER_OFFSET";
    public static final boolean IS_DEBUG_DEVICE;
    private static final int KEEP_ALIVE = 1;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    public static final int SINGLE_FRAME_MS = 16;
    private static final String TAG = "Launcher.Utilities";
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, new LinkedBlockingQueue());
    public static final int WALLPAPER_COMPAT_JOB_ID = 2;
    private static final Matrix sInverseMatrix = new Matrix();
    private static final int[] sLoc0 = new int[2];
    private static final int[] sLoc1 = new int[2];
    private static final Matrix sMatrix = new Matrix();
    private static final float[] sPoint = new float[2];
    private static final Pattern sTrimPattern = Pattern.compile("^[\\s|\\p{javaSpaceChar}]*(.*)[\\s|\\p{javaSpaceChar}]*$");

    static {
        boolean z = false;
        if (Build.TYPE.toLowerCase().contains(BuildConfig.BUILD_TYPE) || Build.TYPE.toLowerCase().equals("eng")) {
            z = true;
        }
        IS_DEBUG_DEVICE = z;
    }

    public static boolean isPropertyEnabled(String propertyName) {
        return Log.isLoggable(propertyName, 2);
    }

    /* JADX WARNING: type inference failed for: r4v7, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static float getDescendantCoordRelativeToAncestor(android.view.View r7, android.view.View r8, int[] r9, boolean r10) {
        /*
            float[] r0 = sPoint
            r1 = 0
            r2 = r9[r1]
            float r2 = (float) r2
            r0[r1] = r2
            float[] r0 = sPoint
            r2 = 1
            r3 = r9[r2]
            float r3 = (float) r3
            r0[r2] = r3
            r0 = 1065353216(0x3f800000, float:1.0)
            r3 = r0
            r0 = r7
        L_0x0014:
            if (r0 == r8) goto L_0x0063
            if (r0 == 0) goto L_0x0063
            if (r0 != r7) goto L_0x001c
            if (r10 == 0) goto L_0x0034
        L_0x001c:
            float[] r4 = sPoint
            r5 = r4[r1]
            int r6 = r0.getScrollX()
            float r6 = (float) r6
            float r5 = r5 - r6
            r4[r1] = r5
            float[] r4 = sPoint
            r5 = r4[r2]
            int r6 = r0.getScrollY()
            float r6 = (float) r6
            float r5 = r5 - r6
            r4[r2] = r5
        L_0x0034:
            android.graphics.Matrix r4 = r0.getMatrix()
            float[] r5 = sPoint
            r4.mapPoints(r5)
            float[] r4 = sPoint
            r5 = r4[r1]
            int r6 = r0.getLeft()
            float r6 = (float) r6
            float r5 = r5 + r6
            r4[r1] = r5
            float[] r4 = sPoint
            r5 = r4[r2]
            int r6 = r0.getTop()
            float r6 = (float) r6
            float r5 = r5 + r6
            r4[r2] = r5
            float r4 = r0.getScaleX()
            float r3 = r3 * r4
            android.view.ViewParent r4 = r0.getParent()
            r0 = r4
            android.view.View r0 = (android.view.View) r0
            goto L_0x0014
        L_0x0063:
            float[] r4 = sPoint
            r4 = r4[r1]
            int r4 = java.lang.Math.round(r4)
            r9[r1] = r4
            float[] r1 = sPoint
            r1 = r1[r2]
            int r1 = java.lang.Math.round(r1)
            r9[r2] = r1
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Utilities.getDescendantCoordRelativeToAncestor(android.view.View, android.view.View, int[], boolean):float");
    }

    /* JADX WARNING: type inference failed for: r1v14, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void mapCoordInSelfToDescendant(android.view.View r5, android.view.View r6, int[] r7) {
        /*
            android.graphics.Matrix r0 = sMatrix
            r0.reset()
            r0 = r5
        L_0x0006:
            if (r0 == r6) goto L_0x0039
            android.graphics.Matrix r1 = sMatrix
            int r2 = r0.getScrollX()
            int r2 = -r2
            float r2 = (float) r2
            int r3 = r0.getScrollY()
            int r3 = -r3
            float r3 = (float) r3
            r1.postTranslate(r2, r3)
            android.graphics.Matrix r1 = sMatrix
            android.graphics.Matrix r2 = r0.getMatrix()
            r1.postConcat(r2)
            android.graphics.Matrix r1 = sMatrix
            int r2 = r0.getLeft()
            float r2 = (float) r2
            int r3 = r0.getTop()
            float r3 = (float) r3
            r1.postTranslate(r2, r3)
            android.view.ViewParent r1 = r0.getParent()
            r0 = r1
            android.view.View r0 = (android.view.View) r0
            goto L_0x0006
        L_0x0039:
            android.graphics.Matrix r1 = sMatrix
            int r2 = r0.getScrollX()
            int r2 = -r2
            float r2 = (float) r2
            int r3 = r0.getScrollY()
            int r3 = -r3
            float r3 = (float) r3
            r1.postTranslate(r2, r3)
            android.graphics.Matrix r1 = sMatrix
            android.graphics.Matrix r2 = sInverseMatrix
            r1.invert(r2)
            float[] r1 = sPoint
            r2 = 0
            r3 = r7[r2]
            float r3 = (float) r3
            r1[r2] = r3
            float[] r1 = sPoint
            r3 = 1
            r4 = r7[r3]
            float r4 = (float) r4
            r1[r3] = r4
            android.graphics.Matrix r1 = sInverseMatrix
            float[] r4 = sPoint
            r1.mapPoints(r4)
            float[] r1 = sPoint
            r1 = r1[r2]
            int r1 = java.lang.Math.round(r1)
            r7[r2] = r1
            float[] r1 = sPoint
            r1 = r1[r3]
            int r1 = java.lang.Math.round(r1)
            r7[r3] = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Utilities.mapCoordInSelfToDescendant(android.view.View, android.view.View, int[]):void");
    }

    public static boolean pointInView(View v, float localX, float localY, float slop) {
        return localX >= (-slop) && localY >= (-slop) && localX < ((float) v.getWidth()) + slop && localY < ((float) v.getHeight()) + slop;
    }

    public static int[] getCenterDeltaInScreenSpace(View v0, View v1) {
        v0.getLocationInWindow(sLoc0);
        v1.getLocationInWindow(sLoc1);
        int[] iArr = sLoc0;
        iArr[0] = (int) (((float) iArr[0]) + ((((float) v0.getMeasuredWidth()) * v0.getScaleX()) / 2.0f));
        int[] iArr2 = sLoc0;
        iArr2[1] = (int) (((float) iArr2[1]) + ((((float) v0.getMeasuredHeight()) * v0.getScaleY()) / 2.0f));
        int[] iArr3 = sLoc1;
        iArr3[0] = (int) (((float) iArr3[0]) + ((((float) v1.getMeasuredWidth()) * v1.getScaleX()) / 2.0f));
        int[] iArr4 = sLoc1;
        iArr4[1] = (int) (((float) iArr4[1]) + ((((float) v1.getMeasuredHeight()) * v1.getScaleY()) / 2.0f));
        return new int[]{sLoc1[0] - sLoc0[0], sLoc1[1] - sLoc0[1]};
    }

    public static void scaleRectFAboutCenter(RectF r, float scale) {
        if (scale != 1.0f) {
            float cx = r.centerX();
            float cy = r.centerY();
            r.offset(-cx, -cy);
            r.left *= scale;
            r.top *= scale;
            r.right *= scale;
            r.bottom *= scale;
            r.offset(cx, cy);
        }
    }

    public static void scaleRectAboutCenter(Rect r, float scale) {
        if (scale != 1.0f) {
            int cx = r.centerX();
            int cy = r.centerY();
            r.offset(-cx, -cy);
            scaleRect(r, scale);
            r.offset(cx, cy);
        }
    }

    public static void scaleRect(Rect r, float scale) {
        if (scale != 1.0f) {
            r.left = (int) ((((float) r.left) * scale) + 0.5f);
            r.top = (int) ((((float) r.top) * scale) + 0.5f);
            r.right = (int) ((((float) r.right) * scale) + 0.5f);
            r.bottom = (int) ((((float) r.bottom) * scale) + 0.5f);
        }
    }

    public static void insetRect(Rect r, Rect insets) {
        r.left = Math.min(r.right, r.left + insets.left);
        r.top = Math.min(r.bottom, r.top + insets.top);
        r.right = Math.max(r.left, r.right - insets.right);
        r.bottom = Math.max(r.top, r.bottom - insets.bottom);
    }

    public static float shrinkRect(Rect r, float scaleX, float scaleY) {
        float scale = Math.min(Math.min(scaleX, scaleY), 1.0f);
        if (scale < 1.0f) {
            int deltaX = (int) (((float) r.width()) * (scaleX - scale) * 0.5f);
            r.left += deltaX;
            r.right -= deltaX;
            int deltaY = (int) (((float) r.height()) * (scaleY - scale) * 0.5f);
            r.top += deltaY;
            r.bottom -= deltaY;
        }
        return scale;
    }

    public static float mapToRange(float t, float fromMin, float fromMax, float toMin, float toMax, Interpolator interpolator) {
        if (fromMin != fromMax && toMin != toMax) {
            return mapRange(interpolator.getInterpolation(Math.abs(t - fromMin) / Math.abs(fromMax - fromMin)), toMin, toMax);
        }
        Log.e(TAG, "mapToRange: range has 0 length");
        return toMin;
    }

    public static float mapRange(float value, float min, float max) {
        return ((max - min) * value) + min;
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        ComponentName cn = intent.getComponent();
        String packageName = null;
        if (cn == null) {
            ResolveInfo info = pm.resolveActivity(intent, 65536);
            if (!(info == null || info.activityInfo == null)) {
                packageName = info.activityInfo.packageName;
            }
        } else {
            packageName = cn.getPackageName();
        }
        if (packageName == null) {
            return false;
        }
        try {
            PackageInfo info2 = pm.getPackageInfo(packageName, 0);
            if (info2 == null || info2.applicationInfo == null || (info2.applicationInfo.flags & 1) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static Pair<String, Resources> findSystemApk(String action, PackageManager pm) {
        for (ResolveInfo info : pm.queryBroadcastReceivers(new Intent(action), 0)) {
            if (!(info.activityInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0)) {
                String packageName = info.activityInfo.packageName;
                try {
                    return Pair.create(packageName, pm.getResourcesForApplication(packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(TAG, "Failed to find resources for " + packageName);
                }
            }
        }
        return null;
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w(TAG, "Could not write bitmap");
            return null;
        }
    }

    public static String trim(CharSequence s) {
        if (s == null) {
            return null;
        }
        return sTrimPattern.matcher(s).replaceAll("$1");
    }

    public static int calculateTextHeight(float textSizePx) {
        Paint p = new Paint();
        p.setTextSize(textSizePx);
        Paint.FontMetrics fm = p.getFontMetrics();
        return (int) Math.ceil((double) (fm.bottom - fm.top));
    }

    public static void println(String key, Object... args) {
        StringBuilder b = new StringBuilder();
        b.append(key);
        b.append(": ");
        boolean isFirstArgument = true;
        for (Object arg : args) {
            if (isFirstArgument) {
                isFirstArgument = false;
            } else {
                b.append(", ");
            }
            b.append(arg);
        }
        System.out.println(b.toString());
    }

    public static boolean isRtl(Resources res) {
        return res.getConfiguration().getLayoutDirection() == 1;
    }

    public static boolean isLauncherAppTarget(Intent launchIntent) {
        if (launchIntent == null || !"android.intent.action.MAIN".equals(launchIntent.getAction()) || launchIntent.getComponent() == null || launchIntent.getCategories() == null || launchIntent.getCategories().size() != 1 || !launchIntent.hasCategory("android.intent.category.LAUNCHER") || !TextUtils.isEmpty(launchIntent.getDataString())) {
            return false;
        }
        Bundle extras = launchIntent.getExtras();
        if (extras == null || extras.keySet().isEmpty()) {
            return true;
        }
        return false;
    }

    public static float dpiFromPx(int size, DisplayMetrics metrics) {
        return ((float) size) / (((float) metrics.densityDpi) / 160.0f);
    }

    public static int pxFromDp(float size, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(1, size, metrics));
    }

    public static int pxFromSp(float size, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(2, size, metrics));
    }

    public static String createDbSelectionQuery(String columnName, Iterable<?> values) {
        return String.format(Locale.ENGLISH, "%s IN (%s)", new Object[]{columnName, TextUtils.join(", ", values)});
    }

    public static boolean isBootCompleted() {
        return "1".equals(getSystemProperty("sys.boot_completed", "1"));
    }

    public static String getSystemProperty(String property, String defaultValue) {
        try {
            String value = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{property});
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
            return defaultValue;
        } catch (Exception e) {
            Log.d(TAG, "Unable to read system properties");
        }
    }

    public static int boundToRange(int value, int lowerBound, int upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static float boundToRange(float value, float lowerBound, float upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static long boundToRange(long value, long lowerBound, long upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static CharSequence wrapForTts(CharSequence msg, String ttsMsg) {
        SpannableString spanned = new SpannableString(msg);
        spanned.setSpan(new TtsSpan.TextBuilder(ttsMsg).build(), 0, spanned.length(), 18);
        return spanned;
    }

    public static int longCompare(long lhs, long rhs) {
        if (lhs < rhs) {
            return -1;
        }
        return lhs == rhs ? 0 : 1;
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, 0);
    }

    public static SharedPreferences getDevicePrefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.DEVICE_PREFERENCES_KEY, 0);
    }

    public static boolean isPowerSaverPreventingAnimation(Context context) {
        if (ATLEAST_P) {
            return false;
        }
        return ((PowerManager) context.getSystemService("power")).isPowerSaveMode();
    }

    public static boolean isWallpaperAllowed(Context context) {
        if (!ATLEAST_NOUGAT) {
            return true;
        }
        try {
            WallpaperManager wm = (WallpaperManager) context.getSystemService(WallpaperManager.class);
            return ((Boolean) wm.getClass().getDeclaredMethod("isSetWallpaperAllowed", new Class[0]).invoke(wm, new Object[0])).booleanValue();
        } catch (Exception e) {
            return true;
        }
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }

    public static boolean containsAll(Bundle original, Bundle updates) {
        for (String key : updates.keySet()) {
            Object value1 = updates.get(key);
            Object value2 = original.get(key);
            if (value1 == null) {
                if (value2 != null) {
                    return false;
                }
            } else if (!value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isBinderSizeError(Exception e) {
        return (e.getCause() instanceof TransactionTooLargeException) || (e.getCause() instanceof DeadObjectException);
    }

    public static <T> T getOverrideObject(Class<T> clazz, Context context, int resId) {
        String className = context.getString(resId);
        if (!TextUtils.isEmpty(className)) {
            try {
                return Class.forName(className).getDeclaredConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
            } catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                Log.e(TAG, "Bad overriden class", e);
            }
        }
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static <T> HashSet<T> singletonHashSet(T elem) {
        HashSet<T> hashSet = new HashSet<>(1);
        hashSet.add(elem);
        return hashSet;
    }

    public static void postAsyncCallback(Handler handler, Runnable callback) {
        Message msg = Message.obtain(handler, callback);
        msg.setAsynchronous(true);
        handler.sendMessage(msg);
    }
}
