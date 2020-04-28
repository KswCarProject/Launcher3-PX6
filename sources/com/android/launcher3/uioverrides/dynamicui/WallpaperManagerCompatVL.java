package com.android.launcher3.uioverrides.dynamicui;

import android.app.WallpaperManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.Utilities;
import com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompat;
import java.util.ArrayList;
import java.util.Iterator;

public class WallpaperManagerCompatVL extends WallpaperManagerCompat {
    private static final String ACTION_EXTRACTION_COMPLETE = "com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompatVL.EXTRACTION_COMPLETE";
    private static final String KEY_COLORS = "wallpaper_parsed_colors";
    private static final String TAG = "WMCompatVL";
    private static final String VERSION_PREFIX = "1,";
    private WallpaperColorsCompat mColorsCompat;
    private final Context mContext;
    private final ArrayList<WallpaperManagerCompat.OnColorsChangedListenerCompat> mListeners = new ArrayList<>();

    WallpaperManagerCompatVL(Context context) {
        this.mContext = context;
        String colors = Utilities.getDevicePrefs(this.mContext).getString(KEY_COLORS, "");
        int wallpaperId = -1;
        if (colors.startsWith(VERSION_PREFIX)) {
            Pair<Integer, WallpaperColorsCompat> storedValue = parseValue(colors);
            wallpaperId = ((Integer) storedValue.first).intValue();
            this.mColorsCompat = (WallpaperColorsCompat) storedValue.second;
        }
        if (wallpaperId == -1 || wallpaperId != getWallpaperId(context)) {
            reloadColors();
        }
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                WallpaperManagerCompatVL.this.reloadColors();
            }
        }, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
        String permission = null;
        try {
            for (PermissionInfo info : context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).permissions) {
                if ((info.protectionLevel & 2) != 0) {
                    permission = info.name;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Unable to get permission info", e);
        }
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                WallpaperManagerCompatVL.this.handleResult(intent.getStringExtra(WallpaperManagerCompatVL.KEY_COLORS));
            }
        }, new IntentFilter(ACTION_EXTRACTION_COMPLETE), permission, new Handler());
    }

    @Nullable
    public WallpaperColorsCompat getWallpaperColors(int which) {
        if (which == 1) {
            return this.mColorsCompat;
        }
        return null;
    }

    public void addOnColorsChangedListener(WallpaperManagerCompat.OnColorsChangedListenerCompat listener) {
        this.mListeners.add(listener);
    }

    /* access modifiers changed from: private */
    public void reloadColors() {
        ((JobScheduler) this.mContext.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(2, new ComponentName(this.mContext, ColorExtractionService.class)).setMinimumLatency(0).build());
    }

    /* access modifiers changed from: private */
    public void handleResult(String result) {
        Utilities.getDevicePrefs(this.mContext).edit().putString(KEY_COLORS, result).apply();
        this.mColorsCompat = (WallpaperColorsCompat) parseValue(result).second;
        Iterator<WallpaperManagerCompat.OnColorsChangedListenerCompat> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onColorsChanged(this.mColorsCompat, 1);
        }
    }

    /* access modifiers changed from: private */
    public static final int getWallpaperId(Context context) {
        if (!Utilities.ATLEAST_NOUGAT) {
            return -1;
        }
        return ((WallpaperManager) context.getSystemService(WallpaperManager.class)).getWallpaperId(1);
    }

    private static Pair<Integer, WallpaperColorsCompat> parseValue(String value) {
        String[] parts = value.split(",");
        Integer wallpaperId = Integer.valueOf(Integer.parseInt(parts[1]));
        if (parts.length == 2) {
            return Pair.create(wallpaperId, (Object) null);
        }
        return Pair.create(wallpaperId, new WallpaperColorsCompat(parts.length > 2 ? Integer.parseInt(parts[2]) : 0, parts.length > 3 ? Integer.parseInt(parts[3]) : 0, parts.length > 4 ? Integer.parseInt(parts[4]) : 0, 0));
    }

    public static class ColorExtractionService extends JobService implements Runnable {
        private static final int MAX_WALLPAPER_EXTRACTION_AREA = 12544;
        private Handler mWorkerHandler;
        private HandlerThread mWorkerThread;

        public void onCreate() {
            super.onCreate();
            this.mWorkerThread = new HandlerThread("ColorExtractionService");
            this.mWorkerThread.start();
            this.mWorkerHandler = new Handler(this.mWorkerThread.getLooper());
        }

        public void onDestroy() {
            super.onDestroy();
            this.mWorkerThread.quit();
        }

        public boolean onStartJob(JobParameters jobParameters) {
            this.mWorkerHandler.post(this);
            return true;
        }

        public boolean onStopJob(JobParameters jobParameters) {
            this.mWorkerHandler.removeCallbacksAndMessages((Object) null);
            return true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a3, code lost:
            r0 = e;
         */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x00a3 A[ExcHandler: IOException | NullPointerException (e java.lang.Throwable), Splitter:B:33:0x009b] */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00b1  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00b7  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x010d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r23 = this;
                int r1 = com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompatVL.getWallpaperId(r23)
                r2 = 0
                r3 = 0
                android.app.WallpaperManager r4 = android.app.WallpaperManager.getInstance(r23)
                android.app.WallpaperInfo r5 = r4.getWallpaperInfo()
                r6 = 4668121751257874432(0x40c8800000000000, double:12544.0)
                r8 = 12544(0x3100, float:1.7578E-41)
                r9 = 0
                if (r5 == 0) goto L_0x0022
                android.content.pm.PackageManager r0 = r23.getPackageManager()
                android.graphics.drawable.Drawable r3 = r5.loadThumbnail(r0)
                goto L_0x00b5
            L_0x0022:
                boolean r0 = com.android.launcher3.Utilities.ATLEAST_NOUGAT
                if (r0 == 0) goto L_0x00af
                r0 = 1
                android.os.ParcelFileDescriptor r0 = r4.getWallpaperFile(r0)     // Catch:{ IOException | NullPointerException -> 0x00a5 }
                r10 = r0
                java.io.FileDescriptor r0 = r10.getFileDescriptor()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                android.graphics.BitmapRegionDecoder r0 = android.graphics.BitmapRegionDecoder.newInstance(r0, r9)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r12 = r0.getWidth()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r13 = r0.getHeight()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r12 = r12 * r13
                android.graphics.BitmapFactory$Options r13 = new android.graphics.BitmapFactory$Options     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r13.<init>()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                if (r12 <= r8) goto L_0x0069
                double r14 = (double) r12     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                double r14 = r14 / r6
                double r16 = java.lang.Math.log(r14)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r18 = r12
                r11 = 4611686018427387904(0x4000000000000000, double:2.0)
                double r19 = java.lang.Math.log(r11)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                double r19 = r19 * r11
                double r16 = r16 / r19
                double r16 = java.lang.Math.floor(r16)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r21 = r16
                r6 = r21
                double r11 = java.lang.Math.pow(r11, r6)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r11 = (int) r11     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r13.inSampleSize = r11     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                goto L_0x006b
            L_0x0069:
                r18 = r12
            L_0x006b:
                android.graphics.Rect r6 = new android.graphics.Rect     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r7 = r0.getWidth()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r11 = r0.getHeight()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r6.<init>(r9, r9, r7, r11)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                android.graphics.Bitmap r7 = r0.decodeRegion(r6, r13)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r2 = r7
                r0.recycle()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                if (r10 == 0) goto L_0x0085
                r10.close()     // Catch:{ IOException | NullPointerException -> 0x00a5 }
            L_0x0085:
                goto L_0x00af
            L_0x0086:
                r0 = move-exception
                r6 = r2
                r11 = 0
            L_0x0089:
                r2 = r0
                goto L_0x0091
            L_0x008b:
                r0 = move-exception
                r11 = r0
                throw r11     // Catch:{ all -> 0x008e }
            L_0x008e:
                r0 = move-exception
                r6 = r2
                goto L_0x0089
            L_0x0091:
                if (r10 == 0) goto L_0x00a2
                if (r11 == 0) goto L_0x009f
                r10.close()     // Catch:{ Throwable -> 0x0099 }
                goto L_0x00a2
            L_0x0099:
                r0 = move-exception
                r7 = r0
                r11.addSuppressed(r7)     // Catch:{ IOException | NullPointerException -> 0x00a3, IOException | NullPointerException -> 0x00a3 }
                goto L_0x00a2
            L_0x009f:
                r10.close()     // Catch:{ IOException | NullPointerException -> 0x00a3, IOException | NullPointerException -> 0x00a3 }
            L_0x00a2:
                throw r2     // Catch:{ IOException | NullPointerException -> 0x00a3, IOException | NullPointerException -> 0x00a3 }
            L_0x00a3:
                r0 = move-exception
                goto L_0x00a7
            L_0x00a5:
                r0 = move-exception
                r6 = r2
            L_0x00a7:
                java.lang.String r2 = "WMCompatVL"
                java.lang.String r7 = "Fetching partial bitmap failed, trying old method"
                android.util.Log.e(r2, r7, r0)
                r2 = r6
            L_0x00af:
                if (r2 != 0) goto L_0x00b5
                android.graphics.drawable.Drawable r3 = r4.getDrawable()
            L_0x00b5:
                if (r3 == 0) goto L_0x00fa
                int r0 = r3.getIntrinsicWidth()
                int r6 = r3.getIntrinsicHeight()
                int r0 = r0 * r6
                r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                if (r0 <= r8) goto L_0x00d1
                double r10 = (double) r0
                r12 = 4668121751257874432(0x40c8800000000000, double:12544.0)
                double r10 = r12 / r10
                double r6 = java.lang.Math.sqrt(r10)
            L_0x00d1:
                int r10 = r3.getIntrinsicWidth()
                double r10 = (double) r10
                double r10 = r10 * r6
                int r10 = (int) r10
                int r11 = r3.getIntrinsicHeight()
                double r11 = (double) r11
                double r11 = r11 * r6
                int r11 = (int) r11
                android.graphics.Bitmap$Config r12 = android.graphics.Bitmap.Config.ARGB_8888
                android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r10, r11, r12)
                android.graphics.Canvas r10 = new android.graphics.Canvas
                r10.<init>(r2)
                int r11 = r2.getWidth()
                int r12 = r2.getHeight()
                r3.setBounds(r9, r9, r11, r12)
                r3.draw(r10)
            L_0x00fa:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r6 = "1,"
                r0.append(r6)
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                if (r2 == 0) goto L_0x0125
                int r6 = com.android.launcher3.graphics.ColorExtractor.findDominantColorByHue(r2, r8)
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r0)
                java.lang.String r8 = ","
                r7.append(r8)
                r7.append(r6)
                java.lang.String r0 = r7.toString()
            L_0x0125:
                android.content.Intent r6 = new android.content.Intent
                java.lang.String r7 = "com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompatVL.EXTRACTION_COMPLETE"
                r6.<init>(r7)
                java.lang.String r7 = r23.getPackageName()
                android.content.Intent r6 = r6.setPackage(r7)
                java.lang.String r7 = "wallpaper_parsed_colors"
                android.content.Intent r6 = r6.putExtra(r7, r0)
                r7 = r23
                r7.sendBroadcast(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompatVL.ColorExtractionService.run():void");
        }
    }
}
