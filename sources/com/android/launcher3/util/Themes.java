package com.android.launcher3.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;

public class Themes {
    public static int getColorAccent(Context context) {
        return getAttrColor(context, 16843829);
    }

    public static int getAttrColor(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int colorAccent = ta.getColor(0, 0);
        ta.recycle();
        return colorAccent;
    }

    public static boolean getAttrBoolean(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        boolean value = ta.getBoolean(0, false);
        ta.recycle();
        return value;
    }

    public static Drawable getAttrDrawable(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        Drawable value = ta.getDrawable(0);
        ta.recycle();
        return value;
    }

    public static int getAttrInteger(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int value = ta.getInteger(0, 0);
        ta.recycle();
        return value;
    }

    public static int getAlpha(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        float alpha = ta.getFloat(0, 0.0f);
        ta.recycle();
        return (int) ((255.0f * alpha) + 0.5f);
    }

    public static void setColorScaleOnMatrix(int color, ColorMatrix target) {
        target.setScale(((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f, ((float) Color.alpha(color)) / 255.0f);
    }

    public static void setColorChangeOnMatrix(int srcColor, int dstColor, ColorMatrix target) {
        target.reset();
        target.getArray()[4] = (float) (Color.red(dstColor) - Color.red(srcColor));
        target.getArray()[9] = (float) (Color.green(dstColor) - Color.green(srcColor));
        target.getArray()[14] = (float) (Color.blue(dstColor) - Color.blue(srcColor));
        target.getArray()[19] = (float) (Color.alpha(dstColor) - Color.alpha(srcColor));
    }
}
