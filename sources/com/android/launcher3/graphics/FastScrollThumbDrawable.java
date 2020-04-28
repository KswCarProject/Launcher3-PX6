package com.android.launcher3.graphics;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class FastScrollThumbDrawable extends Drawable {
    private static final Matrix sMatrix = new Matrix();
    private final boolean mIsRtl;
    private final Paint mPaint;
    private final Path mPath = new Path();

    public FastScrollThumbDrawable(Paint paint, boolean isRtl) {
        this.mPaint = paint;
        this.mIsRtl = isRtl;
    }

    public void getOutline(Outline outline) {
        if (this.mPath.isConvex()) {
            outline.setConvexPath(this.mPath);
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        this.mPath.reset();
        float r = ((float) bounds.height()) * 0.5f;
        float diameter = 2.0f * r;
        float r2 = r / 5.0f;
        this.mPath.addRoundRect((float) bounds.left, (float) bounds.top, ((float) bounds.left) + diameter, ((float) bounds.top) + diameter, new float[]{r, r, r, r, r2, r2, r, r}, Path.Direction.CCW);
        sMatrix.setRotate(-45.0f, ((float) bounds.left) + r, ((float) bounds.top) + r);
        if (this.mIsRtl) {
            sMatrix.postTranslate((float) bounds.width(), 0.0f);
            sMatrix.postScale(-1.0f, 1.0f, (float) bounds.width(), 0.0f);
        }
        this.mPath.transform(sMatrix);
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(this.mPath, this.mPaint);
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -3;
    }
}
