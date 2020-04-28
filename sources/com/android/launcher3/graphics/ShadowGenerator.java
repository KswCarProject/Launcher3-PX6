package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.v4.graphics.ColorUtils;
import com.android.launcher3.LauncherAppState;

public class ShadowGenerator {
    private static final int AMBIENT_SHADOW_ALPHA = 30;
    public static final float BLUR_FACTOR = 0.010416667f;
    private static final float HALF_DISTANCE = 0.5f;
    private static final int KEY_SHADOW_ALPHA = 61;
    public static final float KEY_SHADOW_DISTANCE = 0.020833334f;
    private final Paint mBlurPaint = new Paint(3);
    private final BlurMaskFilter mDefaultBlurMaskFilter = new BlurMaskFilter(((float) this.mIconSize) * 0.010416667f, BlurMaskFilter.Blur.NORMAL);
    private final Paint mDrawPaint = new Paint(3);
    private final int mIconSize;

    public ShadowGenerator(Context context) {
        this.mIconSize = LauncherAppState.getIDP(context).iconBitmapSize;
    }

    public synchronized void recreateIcon(Bitmap icon, Canvas out) {
        recreateIcon(icon, this.mDefaultBlurMaskFilter, 30, 61, out);
    }

    public synchronized void recreateIcon(Bitmap icon, BlurMaskFilter blurMaskFilter, int ambientAlpha, int keyAlpha, Canvas out) {
        int[] offset = new int[2];
        this.mBlurPaint.setMaskFilter(blurMaskFilter);
        Bitmap shadow = icon.extractAlpha(this.mBlurPaint, offset);
        this.mDrawPaint.setAlpha(ambientAlpha);
        out.drawBitmap(shadow, (float) offset[0], (float) offset[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(keyAlpha);
        out.drawBitmap(shadow, (float) offset[0], ((float) offset[1]) + (((float) this.mIconSize) * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        out.drawBitmap(icon, 0.0f, 0.0f, this.mDrawPaint);
    }

    public static float getScaleForBounds(RectF bounds) {
        float scale = 1.0f;
        float minSide = Math.min(Math.min(bounds.left, bounds.right), bounds.top);
        if (minSide < 0.010416667f) {
            scale = 0.48958334f / (0.5f - minSide);
        }
        if (bounds.bottom < 0.03125f) {
            return Math.min(scale, (0.5f - 0.03125f) / (0.5f - bounds.bottom));
        }
        return scale;
    }

    public static class Builder {
        public int ambientShadowAlpha = 30;
        public final RectF bounds = new RectF();
        public final int color;
        public int keyShadowAlpha = 61;
        public float keyShadowDistance;
        public float radius;
        public float shadowBlur;

        public Builder(int color2) {
            this.color = color2;
        }

        public Builder setupBlurForSize(int height) {
            this.shadowBlur = (((float) height) * 1.0f) / 32.0f;
            this.keyShadowDistance = (((float) height) * 1.0f) / 16.0f;
            return this;
        }

        public Bitmap createPill(int width, int height) {
            this.radius = ((float) height) / 2.0f;
            int center = Math.max(Math.round((((float) width) / 2.0f) + this.shadowBlur), Math.round(this.radius + this.shadowBlur + this.keyShadowDistance));
            this.bounds.set(0.0f, 0.0f, (float) width, (float) height);
            this.bounds.offsetTo(((float) center) - (((float) width) / 2.0f), ((float) center) - (((float) height) / 2.0f));
            int size = center * 2;
            Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            drawShadow(new Canvas(result));
            return result;
        }

        public void drawShadow(Canvas c) {
            Paint p = new Paint(3);
            p.setColor(this.color);
            p.setShadowLayer(this.shadowBlur, 0.0f, this.keyShadowDistance, ColorUtils.setAlphaComponent(-16777216, this.keyShadowAlpha));
            c.drawRoundRect(this.bounds, this.radius, this.radius, p);
            p.setShadowLayer(this.shadowBlur, 0.0f, 0.0f, ColorUtils.setAlphaComponent(-16777216, this.ambientShadowAlpha));
            c.drawRoundRect(this.bounds, this.radius, this.radius, p);
            if (Color.alpha(this.color) < 255) {
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                p.clearShadowLayer();
                p.setColor(-16777216);
                c.drawRoundRect(this.bounds, this.radius, this.radius, p);
                p.setXfermode((Xfermode) null);
                p.setColor(this.color);
                c.drawRoundRect(this.bounds, this.radius, this.radius, p);
            }
        }
    }
}
