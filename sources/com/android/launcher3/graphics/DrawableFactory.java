package com.android.launcher3.graphics;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.UiThread;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsBackgroundDrawable;

public class DrawableFactory {
    private static final Object LOCK = new Object();
    private static final String TAG = "DrawableFactory";
    private static DrawableFactory sInstance;
    protected final UserHandle mMyUser = Process.myUserHandle();
    private Path mPreloadProgressPath;
    protected final ArrayMap<UserHandle, Bitmap> mUserBadges = new ArrayMap<>();

    public static DrawableFactory get(Context context) {
        DrawableFactory drawableFactory;
        synchronized (LOCK) {
            if (sInstance == null) {
                sInstance = (DrawableFactory) Utilities.getOverrideObject(DrawableFactory.class, context.getApplicationContext(), R.string.drawable_factory_class);
            }
            drawableFactory = sInstance;
        }
        return drawableFactory;
    }

    public FastBitmapDrawable newIcon(ItemInfoWithIcon info) {
        FastBitmapDrawable drawable = new FastBitmapDrawable(info);
        drawable.setIsDisabled(info.isDisabled());
        return drawable;
    }

    public FastBitmapDrawable newIcon(BitmapInfo info, ActivityInfo target) {
        return new FastBitmapDrawable(info);
    }

    public PreloadIconDrawable newPendingIcon(ItemInfoWithIcon info, Context context) {
        if (this.mPreloadProgressPath == null) {
            this.mPreloadProgressPath = getPreloadProgressPath(context);
        }
        return new PreloadIconDrawable(info, this.mPreloadProgressPath, context);
    }

    /* access modifiers changed from: protected */
    public Path getPreloadProgressPath(Context context) {
        if (Utilities.ATLEAST_OREO) {
            try {
                Drawable icon = context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper);
                icon.setBounds(0, 0, 100, 100);
                return (Path) icon.getClass().getMethod("getIconMask", new Class[0]).invoke(icon, new Object[0]);
            } catch (Exception e) {
                Log.e(TAG, "Error loading mask icon", e);
            }
        }
        Path p = new Path();
        p.moveTo(50.0f, 0.0f);
        p.addArc(0.0f, 0.0f, 100.0f, 100.0f, -90.0f, 360.0f);
        return p;
    }

    public AllAppsBackgroundDrawable getAllAppsBackground(Context context) {
        return new AllAppsBackgroundDrawable(context);
    }

    @UiThread
    public Drawable getBadgeForUser(UserHandle user, Context context) {
        if (this.mMyUser.equals(user)) {
            return null;
        }
        Bitmap badgeBitmap = getUserBadge(user, context);
        FastBitmapDrawable d = new FastBitmapDrawable(badgeBitmap);
        d.setFilterBitmap(true);
        d.setBounds(0, 0, badgeBitmap.getWidth(), badgeBitmap.getHeight());
        return d;
    }

    /* access modifiers changed from: protected */
    public synchronized Bitmap getUserBadge(UserHandle user, Context context) {
        Bitmap badgeBitmap = this.mUserBadges.get(user);
        if (badgeBitmap != null) {
            return badgeBitmap;
        }
        Resources res = context.getApplicationContext().getResources();
        int badgeSize = res.getDimensionPixelSize(R.dimen.profile_badge_size);
        Bitmap badgeBitmap2 = Bitmap.createBitmap(badgeSize, badgeSize, Bitmap.Config.ARGB_8888);
        Drawable drawable = context.getPackageManager().getUserBadgedDrawableForDensity(new BitmapDrawable(res, badgeBitmap2), user, new Rect(0, 0, badgeSize, badgeSize), 0);
        if (drawable instanceof BitmapDrawable) {
            badgeBitmap2 = ((BitmapDrawable) drawable).getBitmap();
        } else {
            badgeBitmap2.eraseColor(0);
            Canvas c = new Canvas(badgeBitmap2);
            drawable.setBounds(0, 0, badgeSize, badgeSize);
            drawable.draw(c);
            c.setBitmap((Bitmap) null);
        }
        this.mUserBadges.put(user, badgeBitmap2);
        return badgeBitmap2;
    }
}
