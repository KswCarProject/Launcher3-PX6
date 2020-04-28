package com.android.launcher3.uioverrides.dynamicui;

public class WallpaperColorsCompat {
    public static final int HINT_SUPPORTS_DARK_TEXT = 1;
    public static final int HINT_SUPPORTS_DARK_THEME = 2;
    private final int mColorHints;
    private final int mPrimaryColor;
    private final int mSecondaryColor;
    private final int mTertiaryColor;

    public WallpaperColorsCompat(int primaryColor, int secondaryColor, int tertiaryColor, int colorHints) {
        this.mPrimaryColor = primaryColor;
        this.mSecondaryColor = secondaryColor;
        this.mTertiaryColor = tertiaryColor;
        this.mColorHints = colorHints;
    }

    public int getPrimaryColor() {
        return this.mPrimaryColor;
    }

    public int getSecondaryColor() {
        return this.mSecondaryColor;
    }

    public int getTertiaryColor() {
        return this.mTertiaryColor;
    }

    public int getColorHints() {
        return this.mColorHints;
    }
}
