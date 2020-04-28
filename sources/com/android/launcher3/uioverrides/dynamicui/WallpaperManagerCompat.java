package com.android.launcher3.uioverrides.dynamicui;

import android.content.Context;
import android.support.annotation.Nullable;
import com.android.launcher3.Utilities;

public abstract class WallpaperManagerCompat {
    private static WallpaperManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public interface OnColorsChangedListenerCompat {
        void onColorsChanged(WallpaperColorsCompat wallpaperColorsCompat, int i);
    }

    public abstract void addOnColorsChangedListener(OnColorsChangedListenerCompat onColorsChangedListenerCompat);

    @Nullable
    public abstract WallpaperColorsCompat getWallpaperColors(int i);

    public static WallpaperManagerCompat getInstance(Context context) {
        WallpaperManagerCompat wallpaperManagerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                Context context2 = context.getApplicationContext();
                if (Utilities.ATLEAST_OREO_MR1) {
                    try {
                        sInstance = new WallpaperManagerCompatVOMR1(context2);
                    } catch (Throwable th) {
                    }
                }
                if (sInstance == null) {
                    sInstance = new WallpaperManagerCompatVL(context2);
                }
            }
            wallpaperManagerCompat = sInstance;
        }
        return wallpaperManagerCompat;
    }
}
