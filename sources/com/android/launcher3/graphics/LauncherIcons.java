package com.android.launcher3.graphics;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.AppInfo;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.IconCache;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.BitmapRenderer;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.Themes;

public class LauncherIcons implements AutoCloseable {
    private static final int DEFAULT_WRAPPER_BACKGROUND = -1;
    private static LauncherIcons sPool;
    public static final Object sPoolSync = new Object();
    private final Canvas mCanvas;
    private final Context mContext;
    private final int mFillResIconDpi;
    private final int mIconBitmapSize;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds = new Rect();
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private int mWrapperBackgroundColor = -1;
    private Drawable mWrapperIcon;
    private LauncherIcons next;

    public static LauncherIcons obtain(Context context) {
        synchronized (sPoolSync) {
            if (sPool == null) {
                return new LauncherIcons(context);
            }
            LauncherIcons m = sPool;
            sPool = m.next;
            m.next = null;
            return m;
        }
    }

    public void recycle() {
        synchronized (sPoolSync) {
            this.mWrapperBackgroundColor = -1;
            this.next = sPool;
            sPool = this;
        }
    }

    public void close() {
        recycle();
    }

    private LauncherIcons(Context context) {
        this.mContext = context.getApplicationContext();
        this.mPm = this.mContext.getPackageManager();
        InvariantDeviceProfile idp = LauncherAppState.getIDP(this.mContext);
        this.mFillResIconDpi = idp.fillResIconDpi;
        this.mIconBitmapSize = idp.iconBitmapSize;
        this.mCanvas = new Canvas();
        this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
    }

    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mContext);
        }
        return this.mShadowGenerator;
    }

    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext);
        }
        return this.mNormalizer;
    }

    public BitmapInfo createIconBitmap(Intent.ShortcutIconResource iconRes) {
        try {
            Resources resources = this.mPm.getResourcesForApplication(iconRes.packageName);
            if (resources != null) {
                return createBadgedIconBitmap(resources.getDrawableForDensity(resources.getIdentifier(iconRes.resourceName, (String) null, (String) null), this.mFillResIconDpi), Process.myUserHandle(), 0);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public BitmapInfo createIconBitmap(Bitmap icon) {
        if (this.mIconBitmapSize == icon.getWidth() && this.mIconBitmapSize == icon.getHeight()) {
            return BitmapInfo.fromBitmap(icon);
        }
        return BitmapInfo.fromBitmap(createIconBitmap(new BitmapDrawable(this.mContext.getResources(), icon), 1.0f));
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk) {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, false, (float[]) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk, boolean isInstantApp) {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, isInstantApp, (float[]) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk, boolean isInstantApp, float[] scale) {
        Bitmap result;
        Bitmap result2;
        if (scale == null) {
            scale = new float[1];
        }
        Drawable icon2 = normalizeAndWrapToAdaptiveIcon(icon, iconAppTargetSdk, (RectF) null, scale);
        Bitmap bitmap = createIconBitmap(icon2, scale[0]);
        if (Utilities.ATLEAST_OREO && (icon2 instanceof AdaptiveIconDrawable)) {
            this.mCanvas.setBitmap(bitmap);
            getShadowGenerator().recreateIcon(Bitmap.createBitmap(bitmap), this.mCanvas);
            this.mCanvas.setBitmap((Bitmap) null);
        }
        if (user != null && !Process.myUserHandle().equals(user)) {
            Drawable badged = this.mPm.getUserBadgedIcon(new FixedSizeBitmapDrawable(bitmap), user);
            if (badged instanceof BitmapDrawable) {
                result2 = ((BitmapDrawable) badged).getBitmap();
            } else {
                result2 = createIconBitmap(badged, 1.0f);
            }
            result = result2;
        } else if (isInstantApp) {
            badgeWithDrawable(bitmap, this.mContext.getDrawable(R.drawable.ic_instant_app_badge));
            result = bitmap;
        } else {
            result = bitmap;
        }
        return BitmapInfo.fromBitmap(result);
    }

    public Bitmap createScaledBitmapWithoutShadow(Drawable icon, int iconAppTargetSdk) {
        RectF iconBounds = new RectF();
        float[] scale = new float[1];
        return createIconBitmap(normalizeAndWrapToAdaptiveIcon(icon, iconAppTargetSdk, iconBounds, scale), Math.min(scale[0], ShadowGenerator.getScaleForBounds(iconBounds)));
    }

    public void setWrapperBackgroundColor(int color) {
        this.mWrapperBackgroundColor = Color.alpha(color) < 255 ? -1 : color;
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(Drawable icon, int iconAppTargetSdk, RectF outIconBounds, float[] outScale) {
        float scale;
        if ((!Utilities.ATLEAST_OREO || iconAppTargetSdk < 26) && !Utilities.ATLEAST_P) {
            scale = getNormalizer().getScale(icon, outIconBounds, (Path) null, (boolean[]) null);
        } else {
            boolean[] outShape = new boolean[1];
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            AdaptiveIconDrawable dr = (AdaptiveIconDrawable) this.mWrapperIcon;
            dr.setBounds(0, 0, 1, 1);
            scale = getNormalizer().getScale(icon, outIconBounds, dr.getIconMask(), outShape);
            if (Utilities.ATLEAST_OREO && !outShape[0] && !(icon instanceof AdaptiveIconDrawable)) {
                FixedScaleDrawable fsd = (FixedScaleDrawable) dr.getForeground();
                fsd.setDrawable(icon);
                fsd.setScale(scale);
                icon = dr;
                scale = getNormalizer().getScale(icon, outIconBounds, (Path) null, (boolean[]) null);
                ((ColorDrawable) dr.getBackground()).setColor(this.mWrapperBackgroundColor);
            }
        }
        outScale[0] = scale;
        return icon;
    }

    public void badgeWithDrawable(Bitmap target, Drawable badge) {
        this.mCanvas.setBitmap(target);
        badgeWithDrawable(this.mCanvas, badge);
        this.mCanvas.setBitmap((Bitmap) null);
    }

    private void badgeWithDrawable(Canvas target, Drawable badge) {
        int badgeSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.profile_badge_size);
        badge.setBounds(this.mIconBitmapSize - badgeSize, this.mIconBitmapSize - badgeSize, this.mIconBitmapSize, this.mIconBitmapSize);
        badge.draw(target);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0016, code lost:
        r2 = (android.graphics.drawable.BitmapDrawable) r14;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap createIconBitmap(android.graphics.drawable.Drawable r14, float r15) {
        /*
            r13 = this;
            int r0 = r13.mIconBitmapSize
            int r1 = r13.mIconBitmapSize
            boolean r2 = r14 instanceof android.graphics.drawable.PaintDrawable
            if (r2 == 0) goto L_0x0012
            r2 = r14
            android.graphics.drawable.PaintDrawable r2 = (android.graphics.drawable.PaintDrawable) r2
            r2.setIntrinsicWidth(r0)
            r2.setIntrinsicHeight(r1)
            goto L_0x0032
        L_0x0012:
            boolean r2 = r14 instanceof android.graphics.drawable.BitmapDrawable
            if (r2 == 0) goto L_0x0032
            r2 = r14
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2
            android.graphics.Bitmap r3 = r2.getBitmap()
            if (r3 == 0) goto L_0x0032
            int r4 = r3.getDensity()
            if (r4 != 0) goto L_0x0032
            android.content.Context r4 = r13.mContext
            android.content.res.Resources r4 = r4.getResources()
            android.util.DisplayMetrics r4 = r4.getDisplayMetrics()
            r2.setTargetDensity(r4)
        L_0x0032:
            int r2 = r14.getIntrinsicWidth()
            int r3 = r14.getIntrinsicHeight()
            if (r2 <= 0) goto L_0x004d
            if (r3 <= 0) goto L_0x004d
            float r4 = (float) r2
            float r5 = (float) r3
            float r4 = r4 / r5
            if (r2 <= r3) goto L_0x0047
            float r5 = (float) r0
            float r5 = r5 / r4
            int r1 = (int) r5
            goto L_0x004d
        L_0x0047:
            if (r3 <= r2) goto L_0x004d
            float r5 = (float) r1
            float r5 = r5 * r4
            int r0 = (int) r5
        L_0x004d:
            int r4 = r13.mIconBitmapSize
            int r5 = r13.mIconBitmapSize
            android.graphics.Bitmap$Config r6 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r6 = android.graphics.Bitmap.createBitmap(r4, r5, r6)
            android.graphics.Canvas r7 = r13.mCanvas
            r7.setBitmap(r6)
            int r7 = r4 - r0
            int r7 = r7 / 2
            int r8 = r5 - r1
            int r8 = r8 / 2
            android.graphics.Rect r9 = r13.mOldBounds
            android.graphics.Rect r10 = r14.getBounds()
            r9.set(r10)
            boolean r9 = com.android.launcher3.Utilities.ATLEAST_OREO
            if (r9 == 0) goto L_0x0095
            boolean r9 = r14 instanceof android.graphics.drawable.AdaptiveIconDrawable
            if (r9 == 0) goto L_0x0095
            r9 = 1009429163(0x3c2aaaab, float:0.010416667)
            float r10 = (float) r4
            float r10 = r10 * r9
            double r9 = (double) r10
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r10 = java.lang.Math.max(r7, r8)
            int r9 = java.lang.Math.max(r9, r10)
            int r10 = java.lang.Math.max(r0, r1)
            int r11 = r10 - r9
            int r12 = r10 - r9
            r14.setBounds(r9, r9, r11, r12)
            goto L_0x009c
        L_0x0095:
            int r9 = r7 + r0
            int r10 = r8 + r1
            r14.setBounds(r7, r8, r9, r10)
        L_0x009c:
            android.graphics.Canvas r9 = r13.mCanvas
            r9.save()
            android.graphics.Canvas r9 = r13.mCanvas
            int r10 = r4 / 2
            float r10 = (float) r10
            int r11 = r5 / 2
            float r11 = (float) r11
            r9.scale(r15, r15, r10, r11)
            android.graphics.Canvas r9 = r13.mCanvas
            r14.draw(r9)
            android.graphics.Canvas r9 = r13.mCanvas
            r9.restore()
            android.graphics.Rect r9 = r13.mOldBounds
            r14.setBounds(r9)
            android.graphics.Canvas r9 = r13.mCanvas
            r10 = 0
            r9.setBitmap(r10)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.LauncherIcons.createIconBitmap(android.graphics.drawable.Drawable, float):android.graphics.Bitmap");
    }

    public BitmapInfo createShortcutIcon(ShortcutInfoCompat shortcutInfo) {
        return createShortcutIcon(shortcutInfo, true);
    }

    public BitmapInfo createShortcutIcon(ShortcutInfoCompat shortcutInfo, boolean badged) {
        return createShortcutIcon(shortcutInfo, badged, (Provider<Bitmap>) null);
    }

    public BitmapInfo createShortcutIcon(ShortcutInfoCompat shortcutInfo, boolean badged, @Nullable Provider<Bitmap> fallbackIconProvider) {
        Bitmap unbadgedBitmap;
        Bitmap fullIcon;
        Drawable unbadgedDrawable = DeepShortcutManager.getInstance(this.mContext).getShortcutIconDrawable(shortcutInfo, this.mFillResIconDpi);
        IconCache cache = LauncherAppState.getInstance(this.mContext).getIconCache();
        if (unbadgedDrawable != null) {
            unbadgedBitmap = createScaledBitmapWithoutShadow(unbadgedDrawable, 0);
        } else if (fallbackIconProvider != null && (fullIcon = fallbackIconProvider.get()) != null) {
            return createIconBitmap(fullIcon);
        } else {
            unbadgedBitmap = cache.getDefaultIcon(Process.myUserHandle()).icon;
        }
        BitmapInfo result = new BitmapInfo();
        if (!badged) {
            result.color = Themes.getColorAccent(this.mContext);
            result.icon = unbadgedBitmap;
            return result;
        }
        ItemInfoWithIcon badge = getShortcutInfoBadge(shortcutInfo, cache);
        result.color = badge.iconColor;
        result.icon = BitmapRenderer.createHardwareBitmap(this.mIconBitmapSize, this.mIconBitmapSize, new BitmapRenderer.Renderer(unbadgedBitmap, badge) {
            private final /* synthetic */ Bitmap f$1;
            private final /* synthetic */ ItemInfoWithIcon f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void draw(Canvas canvas) {
                LauncherIcons.lambda$createShortcutIcon$0(LauncherIcons.this, this.f$1, this.f$2, canvas);
            }
        });
        return result;
    }

    public static /* synthetic */ void lambda$createShortcutIcon$0(LauncherIcons launcherIcons, Bitmap unbadgedfinal, ItemInfoWithIcon badge, Canvas c) {
        launcherIcons.getShadowGenerator().recreateIcon(unbadgedfinal, c);
        launcherIcons.badgeWithDrawable(c, (Drawable) new FastBitmapDrawable(badge));
    }

    public ItemInfoWithIcon getShortcutInfoBadge(ShortcutInfoCompat shortcutInfo, IconCache cache) {
        ComponentName cn = shortcutInfo.getActivity();
        String badgePkg = shortcutInfo.getBadgePackage(this.mContext);
        boolean hasBadgePkgSet = !badgePkg.equals(shortcutInfo.getPackage());
        if (cn == null || hasBadgePkgSet) {
            PackageItemInfo pkgInfo = new PackageItemInfo(badgePkg);
            cache.getTitleAndIconForApp(pkgInfo, false);
            return pkgInfo;
        }
        AppInfo appInfo = new AppInfo();
        appInfo.user = shortcutInfo.getUserHandle();
        appInfo.componentName = cn;
        appInfo.intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(cn);
        cache.getTitleAndIcon(appInfo, false);
        return appInfo;
    }

    private static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }
}
