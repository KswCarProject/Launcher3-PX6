package com.android.launcher3.uioverrides.dynamicui;

import android.annotation.TargetApi;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompat;
import java.lang.reflect.Method;

@TargetApi(27)
public class WallpaperManagerCompatVOMR1 extends WallpaperManagerCompat {
    private static final String TAG = "WMCompatVOMR1";
    private Method mWCColorHintsMethod;
    private final WallpaperManager mWm;

    WallpaperManagerCompatVOMR1(Context context) throws Throwable {
        this.mWm = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        String name = WallpaperColors.class.getName();
        try {
            this.mWCColorHintsMethod = WallpaperColors.class.getDeclaredMethod("getColorHints", new Class[0]);
        } catch (Exception exc) {
            Log.e(TAG, "getColorHints not available", exc);
        }
    }

    @Nullable
    public WallpaperColorsCompat getWallpaperColors(int which) {
        return convertColorsObject(this.mWm.getWallpaperColors(which));
    }

    public void addOnColorsChangedListener(final WallpaperManagerCompat.OnColorsChangedListenerCompat listener) {
        this.mWm.addOnColorsChangedListener(new WallpaperManager.OnColorsChangedListener() {
            public void onColorsChanged(WallpaperColors colors, int which) {
                listener.onColorsChanged(WallpaperManagerCompatVOMR1.this.convertColorsObject(colors), which);
            }
        }, (Handler) null);
    }

    /* access modifiers changed from: private */
    public WallpaperColorsCompat convertColorsObject(WallpaperColors colors) {
        if (colors == null) {
            return null;
        }
        Color primary = colors.getPrimaryColor();
        Color secondary = colors.getSecondaryColor();
        Color tertiary = colors.getTertiaryColor();
        int primaryVal = primary != null ? primary.toArgb() : 0;
        int secondaryVal = secondary != null ? secondary.toArgb() : 0;
        int tertiaryVal = tertiary != null ? tertiary.toArgb() : 0;
        int colorHints = 0;
        try {
            if (this.mWCColorHintsMethod != null) {
                colorHints = ((Integer) this.mWCColorHintsMethod.invoke(colors, new Object[0])).intValue();
            }
        } catch (Exception exc) {
            Log.e(TAG, "error calling color hints", exc);
        }
        return new WallpaperColorsCompat(primaryVal, secondaryVal, tertiaryVal, colorHints);
    }
}
