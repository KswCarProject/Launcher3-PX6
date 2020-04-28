package com.android.launcher3.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.uioverrides.WallpaperColorInfo;

public class ColorScrim extends ViewScrim {
    private final int mColor;
    private int mCurrentColor;
    private final Interpolator mInterpolator;

    public ColorScrim(View view, int color, Interpolator interpolator) {
        super(view);
        this.mColor = color;
        this.mInterpolator = interpolator;
    }

    /* access modifiers changed from: protected */
    public void onProgressChanged() {
        this.mCurrentColor = ColorUtils.setAlphaComponent(this.mColor, Math.round(this.mInterpolator.getInterpolation(this.mProgress) * ((float) Color.alpha(this.mColor))));
    }

    public void draw(Canvas canvas, int width, int height) {
        if (this.mProgress > 0.0f) {
            canvas.drawColor(this.mCurrentColor);
        }
    }

    public static ColorScrim createExtractedColorScrim(View view) {
        WallpaperColorInfo colors = WallpaperColorInfo.getInstance(view.getContext());
        ColorScrim scrim = new ColorScrim(view, ColorUtils.setAlphaComponent(colors.getSecondaryColor(), view.getResources().getInteger(R.integer.extracted_color_gradient_alpha)), Interpolators.LINEAR);
        scrim.attach();
        return scrim;
    }
}
