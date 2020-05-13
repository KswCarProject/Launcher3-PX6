package com.szchoiceway.index;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.view.ViewCompat;

public class HolographicOutlineHelper {
    private static final int EXTRA_THICK = 2;
    public static final int MAX_OUTER_BLUR_RADIUS;
    private static final int MEDIUM = 1;
    public static final int MIN_OUTER_BLUR_RADIUS;
    private static final int THICK = 0;
    private static final BlurMaskFilter sExtraThickInnerBlurMaskFilter;
    private static final BlurMaskFilter sExtraThickOuterBlurMaskFilter;
    private static final BlurMaskFilter sMediumInnerBlurMaskFilter;
    private static final BlurMaskFilter sMediumOuterBlurMaskFilter;
    private static final BlurMaskFilter sThickInnerBlurMaskFilter;
    private static final BlurMaskFilter sThickOuterBlurMaskFilter;
    private static final BlurMaskFilter sThinOuterBlurMaskFilter;
    private final Paint mBlurPaint = new Paint();
    private final Paint mErasePaint = new Paint();
    private final Paint mHolographicPaint = new Paint();

    static {
        float scale = LauncherApplication.getScreenDensity();
        MIN_OUTER_BLUR_RADIUS = (int) (scale * 1.0f);
        MAX_OUTER_BLUR_RADIUS = (int) (scale * 12.0f);
        sExtraThickOuterBlurMaskFilter = new BlurMaskFilter(12.0f * scale, BlurMaskFilter.Blur.OUTER);
        sThickOuterBlurMaskFilter = new BlurMaskFilter(scale * 6.0f, BlurMaskFilter.Blur.OUTER);
        sMediumOuterBlurMaskFilter = new BlurMaskFilter(scale * 2.0f, BlurMaskFilter.Blur.OUTER);
        sThinOuterBlurMaskFilter = new BlurMaskFilter(scale * 1.0f, BlurMaskFilter.Blur.OUTER);
        sExtraThickInnerBlurMaskFilter = new BlurMaskFilter(scale * 6.0f, BlurMaskFilter.Blur.NORMAL);
        sThickInnerBlurMaskFilter = new BlurMaskFilter(4.0f * scale, BlurMaskFilter.Blur.NORMAL);
        sMediumInnerBlurMaskFilter = new BlurMaskFilter(scale * 2.0f, BlurMaskFilter.Blur.NORMAL);
    }

    HolographicOutlineHelper() {
        this.mHolographicPaint.setFilterBitmap(true);
        this.mHolographicPaint.setAntiAlias(true);
        this.mBlurPaint.setFilterBitmap(true);
        this.mBlurPaint.setAntiAlias(true);
        this.mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.mErasePaint.setFilterBitmap(true);
        this.mErasePaint.setAntiAlias(true);
    }

    public static float highlightAlphaInterpolator(float r) {
        return (float) Math.pow((double) ((1.0f - r) * 0.6f), 1.5d);
    }

    public static float viewAlphaInterpolator(float r) {
        if (r < 0.95f) {
            return (float) Math.pow((double) (r / 0.95f), 1.5d);
        }
        return 1.0f;
    }

    /* access modifiers changed from: package-private */
    public void applyExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor, int thickness) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, true, thickness);
    }

    /* access modifiers changed from: package-private */
    public void applyExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor, boolean clipAlpha, int thickness) {
        BlurMaskFilter outerBlurMaskFilter;
        BlurMaskFilter innerBlurMaskFilter;
        if (clipAlpha) {
            int[] srcBuffer = new int[(srcDst.getWidth() * srcDst.getHeight())];
            srcDst.getPixels(srcBuffer, 0, srcDst.getWidth(), 0, 0, srcDst.getWidth(), srcDst.getHeight());
            for (int i = 0; i < srcBuffer.length; i++) {
                if ((srcBuffer[i] >>> 24) < 188) {
                    srcBuffer[i] = 0;
                }
            }
            srcDst.setPixels(srcBuffer, 0, srcDst.getWidth(), 0, 0, srcDst.getWidth(), srcDst.getHeight());
        }
        Bitmap glowShape = srcDst.extractAlpha();
        switch (thickness) {
            case 0:
                outerBlurMaskFilter = sThickOuterBlurMaskFilter;
                break;
            case 1:
                outerBlurMaskFilter = sMediumOuterBlurMaskFilter;
                break;
            case 2:
                outerBlurMaskFilter = sExtraThickOuterBlurMaskFilter;
                break;
            default:
                throw new RuntimeException("Invalid blur thickness");
        }
        this.mBlurPaint.setMaskFilter(outerBlurMaskFilter);
        int[] outerBlurOffset = new int[2];
        Bitmap thickOuterBlur = glowShape.extractAlpha(this.mBlurPaint, outerBlurOffset);
        if (thickness == 2) {
            this.mBlurPaint.setMaskFilter(sMediumOuterBlurMaskFilter);
        } else {
            this.mBlurPaint.setMaskFilter(sThinOuterBlurMaskFilter);
        }
        int[] brightOutlineOffset = new int[2];
        Bitmap brightOutline = glowShape.extractAlpha(this.mBlurPaint, brightOutlineOffset);
        srcDstCanvas.setBitmap(glowShape);
        srcDstCanvas.drawColor(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_OUT);
        switch (thickness) {
            case 0:
                innerBlurMaskFilter = sThickInnerBlurMaskFilter;
                break;
            case 1:
                innerBlurMaskFilter = sMediumInnerBlurMaskFilter;
                break;
            case 2:
                innerBlurMaskFilter = sExtraThickInnerBlurMaskFilter;
                break;
            default:
                throw new RuntimeException("Invalid blur thickness");
        }
        this.mBlurPaint.setMaskFilter(innerBlurMaskFilter);
        int[] thickInnerBlurOffset = new int[2];
        Bitmap thickInnerBlur = glowShape.extractAlpha(this.mBlurPaint, thickInnerBlurOffset);
        srcDstCanvas.setBitmap(thickInnerBlur);
        Canvas canvas = srcDstCanvas;
        canvas.drawBitmap(glowShape, (float) (-thickInnerBlurOffset[0]), (float) (-thickInnerBlurOffset[1]), this.mErasePaint);
        srcDstCanvas.drawRect(0.0f, 0.0f, (float) (-thickInnerBlurOffset[0]), (float) thickInnerBlur.getHeight(), this.mErasePaint);
        srcDstCanvas.drawRect(0.0f, 0.0f, (float) thickInnerBlur.getWidth(), (float) (-thickInnerBlurOffset[1]), this.mErasePaint);
        srcDstCanvas.setBitmap(srcDst);
        srcDstCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        this.mHolographicPaint.setColor(color);
        Canvas canvas2 = srcDstCanvas;
        Bitmap bitmap = thickInnerBlur;
        canvas2.drawBitmap(bitmap, (float) thickInnerBlurOffset[0], (float) thickInnerBlurOffset[1], this.mHolographicPaint);
        Canvas canvas3 = srcDstCanvas;
        Bitmap bitmap2 = thickOuterBlur;
        canvas3.drawBitmap(bitmap2, (float) outerBlurOffset[0], (float) outerBlurOffset[1], this.mHolographicPaint);
        this.mHolographicPaint.setColor(outlineColor);
        Canvas canvas4 = srcDstCanvas;
        canvas4.drawBitmap(brightOutline, (float) brightOutlineOffset[0], (float) brightOutlineOffset[1], this.mHolographicPaint);
        srcDstCanvas.setBitmap((Bitmap) null);
        brightOutline.recycle();
        thickOuterBlur.recycle();
        thickInnerBlur.recycle();
        glowShape.recycle();
    }

    /* access modifiers changed from: package-private */
    public void applyExtraThickExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, 2);
    }

    /* access modifiers changed from: package-private */
    public void applyThickExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, 0);
    }

    /* access modifiers changed from: package-private */
    public void applyMediumExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor, boolean clipAlpha) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, clipAlpha, 1);
    }

    /* access modifiers changed from: package-private */
    public void applyMediumExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, 1);
    }
}
