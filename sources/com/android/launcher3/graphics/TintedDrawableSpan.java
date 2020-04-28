package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

public class TintedDrawableSpan extends DynamicDrawableSpan {
    private final Drawable mDrawable;
    private int mOldTint = 0;

    public TintedDrawableSpan(Context context, int resourceId) {
        super(0);
        this.mDrawable = context.getDrawable(resourceId);
        this.mDrawable.setTint(0);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Paint.FontMetricsInt fm2 = fm == null ? paint.getFontMetricsInt() : fm;
        int iconSize = fm2.bottom - fm2.top;
        this.mDrawable.setBounds(0, 0, iconSize, iconSize);
        return super.getSize(paint, text, start, end, fm2);
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color = paint.getColor();
        if (this.mOldTint != color) {
            this.mOldTint = color;
            this.mDrawable.setTint(this.mOldTint);
        }
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }
}
