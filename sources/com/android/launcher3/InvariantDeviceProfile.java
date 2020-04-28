package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InvariantDeviceProfile {
    private static float DEFAULT_ICON_SIZE_DP = 60.0f;
    private static final float ICON_SIZE_DEFINED_IN_APP_DP = 48.0f;
    private static float KNEARESTNEIGHBOR = 3.0f;
    private static float WEIGHT_EFFICIENT = 100000.0f;
    private static float WEIGHT_POWER = 5.0f;
    int defaultLayoutId;
    public Point defaultWallpaperSize;
    int demoModeLayoutId;
    public int fillResIconDpi;
    public int iconBitmapSize;
    public float iconSize;
    public float iconTextSize;
    public float landscapeIconSize;
    public DeviceProfile landscapeProfile;
    float minHeightDps;
    float minWidthDps;
    String name;
    public int numColumns;
    public int numFolderColumns;
    public int numFolderRows;
    public int numHotseatIcons;
    public int numRows;
    public DeviceProfile portraitProfile;

    @VisibleForTesting
    public InvariantDeviceProfile() {
    }

    private InvariantDeviceProfile(InvariantDeviceProfile p) {
        this(p.name, p.minWidthDps, p.minHeightDps, p.numRows, p.numColumns, p.numFolderRows, p.numFolderColumns, p.iconSize, p.landscapeIconSize, p.iconTextSize, p.numHotseatIcons, p.defaultLayoutId, p.demoModeLayoutId);
    }

    private InvariantDeviceProfile(String n, float w, float h, int r, int c, int fr, int fc, float is, float lis, float its, int hs, int dlId, int dmlId) {
        this.name = n;
        this.minWidthDps = w;
        this.minHeightDps = h;
        this.numRows = r;
        this.numColumns = c;
        this.numFolderRows = fr;
        this.numFolderColumns = fc;
        this.iconSize = is;
        this.landscapeIconSize = lis;
        this.iconTextSize = its;
        this.numHotseatIcons = hs;
        this.defaultLayoutId = dlId;
        this.demoModeLayoutId = dmlId;
    }

    @TargetApi(23)
    public InvariantDeviceProfile(Context context) {
        Context context2 = context;
        Display display = ((WindowManager) context2.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        Point smallestSize = new Point();
        Point largestSize = new Point();
        display.getCurrentSizeRange(smallestSize, largestSize);
        this.minWidthDps = Utilities.dpiFromPx(Math.min(smallestSize.x, smallestSize.y), dm);
        this.minHeightDps = Utilities.dpiFromPx(Math.min(largestSize.x, largestSize.y), dm);
        ArrayList<InvariantDeviceProfile> closestProfiles = findClosestDeviceProfiles(this.minWidthDps, this.minHeightDps, getPredefinedDeviceProfiles(context));
        InvariantDeviceProfile interpolatedDeviceProfileOut = invDistWeightedInterpolate(this.minWidthDps, this.minHeightDps, closestProfiles);
        InvariantDeviceProfile closestProfile = closestProfiles.get(0);
        this.numRows = closestProfile.numRows;
        this.numColumns = closestProfile.numColumns;
        this.numHotseatIcons = closestProfile.numHotseatIcons;
        this.defaultLayoutId = closestProfile.defaultLayoutId;
        this.demoModeLayoutId = closestProfile.demoModeLayoutId;
        this.numFolderRows = closestProfile.numFolderRows;
        this.numFolderColumns = closestProfile.numFolderColumns;
        this.iconSize = interpolatedDeviceProfileOut.iconSize;
        this.landscapeIconSize = interpolatedDeviceProfileOut.landscapeIconSize;
        this.iconBitmapSize = Utilities.pxFromDp(this.iconSize, dm);
        this.iconTextSize = interpolatedDeviceProfileOut.iconTextSize;
        this.fillResIconDpi = getLauncherIconDensity(this.iconBitmapSize);
        applyPartnerDeviceProfileOverrides(context2, dm);
        Point realSize = new Point();
        display.getRealSize(realSize);
        int smallSide = Math.min(realSize.x, realSize.y);
        Context context3 = context;
        DeviceProfile deviceProfile = r0;
        int largeSide = Math.max(realSize.x, realSize.y);
        Point point = smallestSize;
        int smallSide2 = smallSide;
        Point point2 = largestSize;
        Point point3 = realSize;
        InvariantDeviceProfile invariantDeviceProfile = closestProfile;
        InvariantDeviceProfile invariantDeviceProfile2 = interpolatedDeviceProfileOut;
        ArrayList<InvariantDeviceProfile> arrayList = closestProfiles;
        DeviceProfile deviceProfile2 = new DeviceProfile(context3, this, point, point2, largeSide, smallSide2, true, false);
        this.landscapeProfile = deviceProfile;
        DeviceProfile deviceProfile3 = r0;
        DeviceProfile deviceProfile4 = new DeviceProfile(context3, this, point, point2, smallSide2, largeSide, false, false);
        this.portraitProfile = deviceProfile3;
        if (context.getResources().getConfiguration().smallestScreenWidthDp >= 720) {
            int largeSide2 = largeSide;
            this.defaultWallpaperSize = new Point((int) (((float) largeSide2) * wallpaperTravelToScreenWidthRatio(largeSide2, smallSide2)), largeSide2);
            return;
        }
        int largeSide3 = largeSide;
        this.defaultWallpaperSize = new Point(Math.max(smallSide2 * 2, largeSide3), largeSide3);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r4.addSuppressed(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:25:0x00ae, B:40:0x00c5] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d3 A[ExcHandler: IOException | XmlPullParserException (e java.lang.Throwable), Splitter:B:25:0x00ae] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.android.launcher3.InvariantDeviceProfile> getPredefinedDeviceProfiles(android.content.Context r27) {
        /*
            r26 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = r0
            android.content.res.Resources r0 = r27.getResources()     // Catch:{ IOException | XmlPullParserException -> 0x00d5 }
            r2 = 2132082694(0x7f150006, float:1.980551E38)
            android.content.res.XmlResourceParser r0 = r0.getXml(r2)     // Catch:{ IOException | XmlPullParserException -> 0x00d5 }
            r2 = r0
            int r0 = r2.getDepth()     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
        L_0x0016:
            int r4 = r2.next()     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
            r5 = r4
            r6 = 3
            if (r4 != r6) goto L_0x0024
            int r4 = r2.getDepth()     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
            if (r4 <= r0) goto L_0x00aa
        L_0x0024:
            r4 = 1
            if (r5 == r4) goto L_0x00aa
            r7 = 2
            if (r5 != r7) goto L_0x00a6
            java.lang.String r8 = "profile"
            java.lang.String r9 = r2.getName()     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
            boolean r8 = r8.equals(r9)     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
            if (r8 == 0) goto L_0x00a6
            android.util.AttributeSet r8 = android.util.Xml.asAttributeSet(r2)     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
            int[] r9 = com.android.launcher3.R.styleable.InvariantDeviceProfile     // Catch:{ Throwable -> 0x00b9, all -> 0x00b3 }
            r10 = r27
            android.content.res.TypedArray r8 = r10.obtainStyledAttributes(r8, r9)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r9 = 12
            r11 = 0
            int r9 = r8.getInt(r9, r11)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r12 = 8
            int r12 = r8.getInt(r12, r11)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r15 = r12
            r12 = 0
            float r7 = r8.getFloat(r7, r12)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            com.android.launcher3.InvariantDeviceProfile r14 = new com.android.launcher3.InvariantDeviceProfile     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r13 = 7
            java.lang.String r13 = r8.getString(r13)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r3 = 6
            float r3 = r8.getFloat(r3, r12)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r4 = 5
            float r4 = r8.getFloat(r4, r12)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r11 = 10
            int r18 = r8.getInt(r11, r9)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r11 = 9
            int r19 = r8.getInt(r11, r15)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r11 = 4
            float r21 = r8.getFloat(r11, r7)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            float r22 = r8.getFloat(r6, r12)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r6 = 11
            int r23 = r8.getInt(r6, r15)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r6 = 0
            int r24 = r8.getResourceId(r6, r6)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r11 = 1
            int r25 = r8.getResourceId(r11, r6)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r12 = r14
            r6 = r14
            r14 = r3
            r3 = r15
            r15 = r4
            r16 = r9
            r17 = r3
            r20 = r7
            r12.<init>(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r1.add(r6)     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            r8.recycle()     // Catch:{ Throwable -> 0x00a4, all -> 0x00a2 }
            goto L_0x0016
        L_0x00a2:
            r0 = move-exception
            goto L_0x00b6
        L_0x00a4:
            r0 = move-exception
            goto L_0x00bc
        L_0x00a6:
            r10 = r27
            goto L_0x0016
        L_0x00aa:
            r10 = r27
            if (r2 == 0) goto L_0x00b1
            r2.close()     // Catch:{ IOException | XmlPullParserException -> 0x00d3 }
        L_0x00b1:
            return r1
        L_0x00b3:
            r0 = move-exception
            r10 = r27
        L_0x00b6:
            r3 = r0
            r4 = 0
            goto L_0x00c1
        L_0x00b9:
            r0 = move-exception
            r10 = r27
        L_0x00bc:
            r3 = r0
            throw r3     // Catch:{ all -> 0x00be }
        L_0x00be:
            r0 = move-exception
            r4 = r3
            r3 = r0
        L_0x00c1:
            if (r2 == 0) goto L_0x00d2
            if (r4 == 0) goto L_0x00cf
            r2.close()     // Catch:{ Throwable -> 0x00c9, IOException | XmlPullParserException -> 0x00d3 }
            goto L_0x00d2
        L_0x00c9:
            r0 = move-exception
            r5 = r0
            r4.addSuppressed(r5)     // Catch:{ IOException | XmlPullParserException -> 0x00d3 }
            goto L_0x00d2
        L_0x00cf:
            r2.close()     // Catch:{ IOException | XmlPullParserException -> 0x00d3 }
        L_0x00d2:
            throw r3     // Catch:{ IOException | XmlPullParserException -> 0x00d3 }
        L_0x00d3:
            r0 = move-exception
            goto L_0x00d8
        L_0x00d5:
            r0 = move-exception
            r10 = r27
        L_0x00d8:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            r2.<init>(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.InvariantDeviceProfile.getPredefinedDeviceProfiles(android.content.Context):java.util.ArrayList");
    }

    private int getLauncherIconDensity(int requiredSize) {
        int[] densityBuckets = {120, 160, 213, 240, LauncherAnimUtils.ALL_APPS_TRANSITION_MS, 480, 640};
        int density = 640;
        for (int i = densityBuckets.length - 1; i >= 0; i--) {
            if ((((float) densityBuckets[i]) * ICON_SIZE_DEFINED_IN_APP_DP) / 160.0f >= ((float) requiredSize)) {
                density = densityBuckets[i];
            }
        }
        return density;
    }

    private void applyPartnerDeviceProfileOverrides(Context context, DisplayMetrics dm) {
        Partner p = Partner.get(context.getPackageManager());
        if (p != null) {
            p.applyInvariantDeviceProfileOverrides(this, dm);
        }
    }

    /* access modifiers changed from: package-private */
    public float dist(float x0, float y0, float x1, float y1) {
        return (float) Math.hypot((double) (x1 - x0), (double) (y1 - y0));
    }

    /* access modifiers changed from: package-private */
    public ArrayList<InvariantDeviceProfile> findClosestDeviceProfiles(final float width, final float height, ArrayList<InvariantDeviceProfile> points) {
        ArrayList<InvariantDeviceProfile> pointsByNearness = points;
        Collections.sort(pointsByNearness, new Comparator<InvariantDeviceProfile>() {
            public int compare(InvariantDeviceProfile a, InvariantDeviceProfile b) {
                return Float.compare(InvariantDeviceProfile.this.dist(width, height, a.minWidthDps, a.minHeightDps), InvariantDeviceProfile.this.dist(width, height, b.minWidthDps, b.minHeightDps));
            }
        });
        return pointsByNearness;
    }

    /* access modifiers changed from: package-private */
    public InvariantDeviceProfile invDistWeightedInterpolate(float width, float height, ArrayList<InvariantDeviceProfile> points) {
        float weights = 0.0f;
        int i = 0;
        InvariantDeviceProfile p = points.get(0);
        if (dist(width, height, p.minWidthDps, p.minHeightDps) == 0.0f) {
            return p;
        }
        InvariantDeviceProfile out = new InvariantDeviceProfile();
        while (i < points.size() && ((float) i) < KNEARESTNEIGHBOR) {
            InvariantDeviceProfile p2 = new InvariantDeviceProfile(points.get(i));
            float w = weight(width, height, p2.minWidthDps, p2.minHeightDps, WEIGHT_POWER);
            weights += w;
            out.add(p2.multiply(w));
            i++;
        }
        return out.multiply(1.0f / weights);
    }

    private void add(InvariantDeviceProfile p) {
        this.iconSize += p.iconSize;
        this.landscapeIconSize += p.landscapeIconSize;
        this.iconTextSize += p.iconTextSize;
    }

    private InvariantDeviceProfile multiply(float w) {
        this.iconSize *= w;
        this.landscapeIconSize *= w;
        this.iconTextSize *= w;
        return this;
    }

    public int getAllAppsButtonRank() {
        return this.numHotseatIcons / 2;
    }

    public boolean isAllAppsButtonRank(int rank) {
        return rank == getAllAppsButtonRank();
    }

    public DeviceProfile getDeviceProfile(Context context) {
        return context.getResources().getConfiguration().orientation == 2 ? this.landscapeProfile : this.portraitProfile;
    }

    private float weight(float x0, float y0, float x1, float y1, float pow) {
        float d = dist(x0, y0, x1, y1);
        if (Float.compare(d, 0.0f) == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) (((double) WEIGHT_EFFICIENT) / Math.pow((double) d, (double) pow));
    }

    private static float wallpaperTravelToScreenWidthRatio(int width, int height) {
        return (0.30769226f * (((float) width) / ((float) height))) + 1.0076923f;
    }
}
