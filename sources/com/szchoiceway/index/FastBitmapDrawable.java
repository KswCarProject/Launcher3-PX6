package com.szchoiceway.index;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class FastBitmapDrawable extends Drawable {
    private int mAlpha = 255;
    private Bitmap mBitmap;
    private int mHeight;
    private final Paint mPaint = new Paint(2);
    private int mWidth;

    FastBitmapDrawable(Bitmap b) {
        this.mBitmap = b;
        if (b != null) {
            this.mWidth = this.mBitmap.getWidth();
            this.mHeight = this.mBitmap.getHeight();
            return;
        }
        this.mHeight = 0;
        this.mWidth = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, (Rect) null, getBounds(), this.mPaint);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        this.mPaint.setAlpha(alpha);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        this.mPaint.setFilterBitmap(filterBitmap);
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public int getIntrinsicWidth() {
        return this.mWidth;
    }

    public int getIntrinsicHeight() {
        return this.mHeight;
    }

    public int getMinimumWidth() {
        return this.mWidth;
    }

    public int getMinimumHeight() {
        return this.mHeight;
    }

    public void setBitmap(Bitmap b) {
        this.mBitmap = b;
        if (b != null) {
            this.mWidth = this.mBitmap.getWidth();
            this.mHeight = this.mBitmap.getHeight();
            return;
        }
        this.mHeight = 0;
        this.mWidth = 0;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }
}
