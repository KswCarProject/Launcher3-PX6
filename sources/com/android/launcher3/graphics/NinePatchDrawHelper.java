package com.android.launcher3.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class NinePatchDrawHelper {
    public static final int EXTENSION_PX = 20;
    private final RectF mDst = new RectF();
    private final Rect mSrc = new Rect();
    public final Paint paint = new Paint(1);

    public void draw(Bitmap bitmap, Canvas canvas, float left, float top, float right) {
        int height = bitmap.getHeight();
        this.mSrc.top = 0;
        this.mSrc.bottom = height;
        this.mDst.top = top;
        this.mDst.bottom = ((float) height) + top;
        draw3Patch(bitmap, canvas, left, right);
    }

    public void drawVerticallyStretched(Bitmap bitmap, Canvas canvas, float left, float top, float right, float bottom) {
        draw(bitmap, canvas, left, top, right);
        int height = bitmap.getHeight();
        this.mSrc.top = height - 5;
        this.mSrc.bottom = height;
        this.mDst.top = ((float) height) + top;
        this.mDst.bottom = bottom;
        draw3Patch(bitmap, canvas, left, right);
    }

    private void draw3Patch(Bitmap bitmap, Canvas canvas, float left, float right) {
        int width = bitmap.getWidth();
        int halfWidth = width / 2;
        Bitmap bitmap2 = bitmap;
        Canvas canvas2 = canvas;
        drawRegion(bitmap2, canvas2, 0, halfWidth, left, left + ((float) halfWidth));
        drawRegion(bitmap2, canvas2, halfWidth, width, right - ((float) halfWidth), right);
        drawRegion(bitmap, canvas, halfWidth - 5, halfWidth + 5, left + ((float) halfWidth), right - ((float) halfWidth));
    }

    private void drawRegion(Bitmap bitmap, Canvas c, int srcLeft, int srcRight, float dstLeft, float dstRight) {
        this.mSrc.left = srcLeft;
        this.mSrc.right = srcRight;
        this.mDst.left = dstLeft;
        this.mDst.right = dstRight;
        c.drawBitmap(bitmap, this.mSrc, this.mDst, this.paint);
    }
}
