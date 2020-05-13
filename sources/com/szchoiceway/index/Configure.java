package com.szchoiceway.index;

import android.app.Activity;
import android.util.DisplayMetrics;

public class Configure {
    public static int countPages = 0;
    public static int curentPage = 0;
    public static boolean isChangingPage = false;
    public static boolean isDatabaseOprating = false;
    public static boolean isDelDark = false;
    public static boolean isMove = false;
    public static int removeItem = 0;
    public static float screenDensity = 0.0f;
    public static int screenHeight = 0;
    public static int screenWidth = 0;

    public static void init(Activity context) {
        if (screenDensity == 0.0f || screenWidth == 0 || screenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenDensity = dm.density;
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;
        }
        curentPage = 0;
        countPages = 0;
    }

    public static int getScreenHeight(Activity context) {
        if (screenWidth == 0 || screenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenDensity = dm.density;
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;
        }
        return screenHeight;
    }

    public static int getScreenWidth(Activity context) {
        if (screenWidth == 0 || screenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenDensity = dm.density;
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;
        }
        return screenWidth;
    }

    public int[] ret(int[] intArray) {
        for (int i = intArray.length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (intArray[j] > intArray[j + 1]) {
                    int t = intArray[j];
                    intArray[j] = intArray[j + 1];
                    intArray[j + 1] = t;
                }
            }
        }
        return intArray;
    }
}
