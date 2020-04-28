package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import com.android.launcher3.R;
import com.android.launcher3.util.Themes;

public class IconPalette {
    private static final boolean DEBUG = false;
    private static final float MIN_PRELOAD_COLOR_LIGHTNESS = 0.6f;
    private static final float MIN_PRELOAD_COLOR_SATURATION = 0.2f;
    private static final String TAG = "IconPalette";

    public static int getPreloadProgressColor(Context context, int dominantColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(dominantColor, hsv);
        if (hsv[1] < 0.2f) {
            return Themes.getColorAccent(context);
        }
        hsv[2] = Math.max(MIN_PRELOAD_COLOR_LIGHTNESS, hsv[2]);
        return Color.HSVToColor(hsv);
    }

    public static int resolveContrastColor(Context context, int color, int background) {
        return ensureTextContrast(resolveColor(context, color), background);
    }

    private static int resolveColor(Context context, int color) {
        if (color == 0) {
            return context.getColor(R.color.notification_icon_default_color);
        }
        return color;
    }

    private static String contrastChange(int colorOld, int colorNew, int bg) {
        return String.format("from %.2f:1 to %.2f:1", new Object[]{Double.valueOf(ColorUtils.calculateContrast(colorOld, bg)), Double.valueOf(ColorUtils.calculateContrast(colorNew, bg))});
    }

    private static int ensureTextContrast(int color, int bg) {
        return findContrastColor(color, bg, 4.5d);
    }

    private static int findContrastColor(int fg, int bg, double minRatio) {
        double d;
        int i = fg;
        int i2 = bg;
        if (ColorUtils.calculateContrast(fg, bg) >= minRatio) {
            return i;
        }
        double[] lab = new double[3];
        ColorUtils.colorToLAB(i2, lab);
        double bgL = lab[0];
        ColorUtils.colorToLAB(i, lab);
        double fgL = lab[0];
        boolean isBgDark = bgL < 50.0d;
        double low = isBgDark ? fgL : 0.0d;
        double high = isBgDark ? 100.0d : fgL;
        double a = lab[1];
        double b = lab[2];
        for (int i3 = 0; i3 < 15 && high - low > 1.0E-5d; i3++) {
            double l = (low + high) / 2.0d;
            if (ColorUtils.calculateContrast(ColorUtils.LABToColor(l, a, b), i2) > minRatio) {
                if (isBgDark) {
                    high = l;
                } else {
                    d = l;
                }
            } else if (isBgDark) {
                d = l;
            } else {
                high = l;
            }
            low = d;
        }
        return ColorUtils.LABToColor(low, a, b);
    }

    public static int getMutedColor(int color, float whiteScrimAlpha) {
        return ColorUtils.compositeColors(ColorUtils.setAlphaComponent(-1, (int) (255.0f * whiteScrimAlpha)), color);
    }
}
