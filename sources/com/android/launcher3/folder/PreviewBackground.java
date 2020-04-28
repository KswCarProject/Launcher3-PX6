package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.v4.graphics.ColorUtils;
import android.util.Property;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.util.Themes;

public class PreviewBackground {
    private static final float ACCEPT_COLOR_MULTIPLIER = 1.5f;
    private static final float ACCEPT_SCALE_FACTOR = 1.2f;
    private static final int BG_OPACITY = 160;
    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    private static final int MAX_BG_OPACITY = 225;
    private static final Property<PreviewBackground, Integer> SHADOW_ALPHA = new Property<PreviewBackground, Integer>(Integer.class, "shadowAlpha") {
        public Integer get(PreviewBackground previewBackground) {
            return Integer.valueOf(previewBackground.mShadowAlpha);
        }

        public void set(PreviewBackground previewBackground, Integer alpha) {
            int unused = previewBackground.mShadowAlpha = alpha.intValue();
            previewBackground.invalidate();
        }
    };
    private static final int SHADOW_OPACITY = 40;
    private static final Property<PreviewBackground, Integer> STROKE_ALPHA = new Property<PreviewBackground, Integer>(Integer.class, "strokeAlpha") {
        public Integer get(PreviewBackground previewBackground) {
            return Integer.valueOf(previewBackground.mStrokeAlpha);
        }

        public void set(PreviewBackground previewBackground, Integer alpha) {
            int unused = previewBackground.mStrokeAlpha = alpha.intValue();
            previewBackground.invalidate();
        }
    };
    int basePreviewOffsetX;
    int basePreviewOffsetY;
    public int delegateCellX;
    public int delegateCellY;
    public boolean isClipping = true;
    private int mBgColor;
    private final PorterDuffXfermode mClipPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private final RadialGradient mClipShader = new RadialGradient(0.0f, 0.0f, 1.0f, new int[]{-16777216, -16777216, 0}, new float[]{0.0f, 0.999f, 1.0f}, Shader.TileMode.CLAMP);
    /* access modifiers changed from: private */
    public float mColorMultiplier = 1.0f;
    private CellLayout mDrawingDelegate;
    private View mInvalidateDelegate;
    private final Paint mPaint = new Paint(1);
    private final Path mPath = new Path();
    float mScale = 1.0f;
    /* access modifiers changed from: private */
    public ValueAnimator mScaleAnimator;
    private final Matrix mShaderMatrix = new Matrix();
    /* access modifiers changed from: private */
    public int mShadowAlpha = 255;
    /* access modifiers changed from: private */
    public ObjectAnimator mShadowAnimator;
    private final PorterDuffXfermode mShadowPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private RadialGradient mShadowShader = null;
    /* access modifiers changed from: private */
    public int mStrokeAlpha = MAX_BG_OPACITY;
    /* access modifiers changed from: private */
    public ObjectAnimator mStrokeAlphaAnimator;
    private float mStrokeWidth;
    int previewSize;

    public void setup(Launcher launcher, View invalidateDelegate, int availableSpaceX, int topPadding) {
        this.mInvalidateDelegate = invalidateDelegate;
        this.mBgColor = Themes.getAttrColor(launcher, 16843827);
        DeviceProfile grid = launcher.getDeviceProfile();
        this.previewSize = grid.folderIconSizePx;
        this.basePreviewOffsetX = (availableSpaceX - this.previewSize) / 2;
        this.basePreviewOffsetY = topPadding + grid.folderIconOffsetYPx;
        this.mStrokeWidth = launcher.getResources().getDisplayMetrics().density;
        float radius = (float) getScaledRadius();
        int[] iArr = {Color.argb(40, 0, 0, 0), 0};
        RadialGradient radialGradient = r9;
        RadialGradient radialGradient2 = new RadialGradient(0.0f, 0.0f, 1.0f, iArr, new float[]{radius / (this.mStrokeWidth + radius), 1.0f}, Shader.TileMode.CLAMP);
        this.mShadowShader = radialGradient;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public int getRadius() {
        return this.previewSize / 2;
    }

    /* access modifiers changed from: package-private */
    public int getScaledRadius() {
        return (int) (this.mScale * ((float) getRadius()));
    }

    /* access modifiers changed from: package-private */
    public int getOffsetX() {
        return this.basePreviewOffsetX - (getScaledRadius() - getRadius());
    }

    /* access modifiers changed from: package-private */
    public int getOffsetY() {
        return this.basePreviewOffsetY - (getScaledRadius() - getRadius());
    }

    /* access modifiers changed from: package-private */
    public float getScaleProgress() {
        return (this.mScale - 1.0f) / 0.20000005f;
    }

    /* access modifiers changed from: package-private */
    public void invalidate() {
        if (this.mInvalidateDelegate != null) {
            this.mInvalidateDelegate.invalidate();
        }
        if (this.mDrawingDelegate != null) {
            this.mDrawingDelegate.invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void setInvalidateDelegate(View invalidateDelegate) {
        this.mInvalidateDelegate = invalidateDelegate;
        invalidate();
    }

    public int getBgColor() {
        return ColorUtils.setAlphaComponent(this.mBgColor, (int) Math.min(225.0f, this.mColorMultiplier * 160.0f));
    }

    public int getBadgeColor() {
        return this.mBgColor;
    }

    public void drawBackground(Canvas canvas) {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(getBgColor());
        drawCircle(canvas, 0.0f);
        drawShadow(canvas);
    }

    public void drawShadow(Canvas canvas) {
        int saveCount;
        if (this.mShadowShader != null) {
            float radius = (float) getScaledRadius();
            float shadowRadius = this.mStrokeWidth + radius;
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setColor(-16777216);
            int offsetX = getOffsetX();
            int offsetY = getOffsetY();
            if (canvas.isHardwareAccelerated()) {
                saveCount = canvas.saveLayer(((float) offsetX) - this.mStrokeWidth, (float) offsetY, ((float) offsetX) + radius + shadowRadius, ((float) offsetY) + shadowRadius + shadowRadius, (Paint) null);
            } else {
                saveCount = canvas.save();
                canvas.clipPath(getClipPath(), Region.Op.DIFFERENCE);
            }
            this.mShaderMatrix.setScale(shadowRadius, shadowRadius);
            this.mShaderMatrix.postTranslate(((float) offsetX) + radius, ((float) offsetY) + shadowRadius);
            this.mShadowShader.setLocalMatrix(this.mShaderMatrix);
            this.mPaint.setAlpha(this.mShadowAlpha);
            this.mPaint.setShader(this.mShadowShader);
            canvas.drawPaint(this.mPaint);
            this.mPaint.setAlpha(255);
            this.mPaint.setShader((Shader) null);
            if (canvas.isHardwareAccelerated()) {
                this.mPaint.setXfermode(this.mShadowPorterDuffXfermode);
                canvas.drawCircle(((float) offsetX) + radius, ((float) offsetY) + radius, radius, this.mPaint);
                this.mPaint.setXfermode((Xfermode) null);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    public void fadeInBackgroundShadow() {
        if (this.mShadowAnimator != null) {
            this.mShadowAnimator.cancel();
        }
        this.mShadowAnimator = ObjectAnimator.ofInt(this, SHADOW_ALPHA, new int[]{0, 255}).setDuration(100);
        this.mShadowAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator unused = PreviewBackground.this.mShadowAnimator = null;
            }
        });
        this.mShadowAnimator.start();
    }

    public void animateBackgroundStroke() {
        if (this.mStrokeAlphaAnimator != null) {
            this.mStrokeAlphaAnimator.cancel();
        }
        this.mStrokeAlphaAnimator = ObjectAnimator.ofInt(this, STROKE_ALPHA, new int[]{112, MAX_BG_OPACITY}).setDuration(100);
        this.mStrokeAlphaAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator unused = PreviewBackground.this.mStrokeAlphaAnimator = null;
            }
        });
        this.mStrokeAlphaAnimator.start();
    }

    public void drawBackgroundStroke(Canvas canvas) {
        this.mPaint.setColor(ColorUtils.setAlphaComponent(this.mBgColor, this.mStrokeAlpha));
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        drawCircle(canvas, 1.0f);
    }

    public void drawLeaveBehind(Canvas canvas) {
        float originalScale = this.mScale;
        this.mScale = 0.5f;
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.argb(BG_OPACITY, 245, 245, 245));
        drawCircle(canvas, 0.0f);
        this.mScale = originalScale;
    }

    private void drawCircle(Canvas canvas, float deltaRadius) {
        float radius = (float) getScaledRadius();
        canvas.drawCircle(((float) getOffsetX()) + radius, ((float) getOffsetY()) + radius, radius - deltaRadius, this.mPaint);
    }

    public Path getClipPath() {
        this.mPath.reset();
        float r = (float) getScaledRadius();
        this.mPath.addCircle(((float) getOffsetX()) + r, ((float) getOffsetY()) + r, r, Path.Direction.CW);
        return this.mPath;
    }

    /* access modifiers changed from: package-private */
    public void clipCanvasHardware(Canvas canvas) {
        this.mPaint.setColor(-16777216);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setXfermode(this.mClipPorterDuffXfermode);
        float radius = (float) getScaledRadius();
        this.mShaderMatrix.setScale(radius, radius);
        this.mShaderMatrix.postTranslate(((float) getOffsetX()) + radius, ((float) getOffsetY()) + radius);
        this.mClipShader.setLocalMatrix(this.mShaderMatrix);
        this.mPaint.setShader(this.mClipShader);
        canvas.drawPaint(this.mPaint);
        this.mPaint.setXfermode((Xfermode) null);
        this.mPaint.setShader((Shader) null);
    }

    /* access modifiers changed from: private */
    public void delegateDrawing(CellLayout delegate, int cellX, int cellY) {
        if (this.mDrawingDelegate != delegate) {
            delegate.addFolderBackground(this);
        }
        this.mDrawingDelegate = delegate;
        this.delegateCellX = cellX;
        this.delegateCellY = cellY;
        invalidate();
    }

    /* access modifiers changed from: private */
    public void clearDrawingDelegate() {
        if (this.mDrawingDelegate != null) {
            this.mDrawingDelegate.removeFolderBackground(this);
        }
        this.mDrawingDelegate = null;
        this.isClipping = true;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public boolean drawingDelegated() {
        return this.mDrawingDelegate != null;
    }

    private void animateScale(float finalScale, float finalMultiplier, final Runnable onStart, final Runnable onEnd) {
        float scale0 = this.mScale;
        final float scale1 = finalScale;
        float bgMultiplier0 = this.mColorMultiplier;
        final float bgMultiplier1 = finalMultiplier;
        if (this.mScaleAnimator != null) {
            this.mScaleAnimator.cancel();
        }
        this.mScaleAnimator = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        final float f = scale0;
        final float f2 = bgMultiplier0;
        this.mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float prog = animation.getAnimatedFraction();
                PreviewBackground.this.mScale = (scale1 * prog) + ((1.0f - prog) * f);
                float unused = PreviewBackground.this.mColorMultiplier = (bgMultiplier1 * prog) + ((1.0f - prog) * f2);
                PreviewBackground.this.invalidate();
            }
        });
        this.mScaleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                if (onStart != null) {
                    onStart.run();
                }
            }

            public void onAnimationEnd(Animator animation) {
                if (onEnd != null) {
                    onEnd.run();
                }
                ValueAnimator unused = PreviewBackground.this.mScaleAnimator = null;
            }
        });
        this.mScaleAnimator.setDuration(100);
        this.mScaleAnimator.start();
    }

    public void animateToAccept(final CellLayout cl, final int cellX, final int cellY) {
        animateScale(ACCEPT_SCALE_FACTOR, ACCEPT_COLOR_MULTIPLIER, new Runnable() {
            public void run() {
                PreviewBackground.this.delegateDrawing(cl, cellX, cellY);
            }
        }, (Runnable) null);
    }

    public void animateToRest() {
        final CellLayout cl = this.mDrawingDelegate;
        final int cellX = this.delegateCellX;
        final int cellY = this.delegateCellY;
        animateScale(1.0f, 1.0f, new Runnable() {
            public void run() {
                PreviewBackground.this.delegateDrawing(cl, cellX, cellY);
            }
        }, new Runnable() {
            public void run() {
                PreviewBackground.this.clearDrawingDelegate();
            }
        });
    }

    public int getBackgroundAlpha() {
        return (int) Math.min(225.0f, this.mColorMultiplier * 160.0f);
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }
}
