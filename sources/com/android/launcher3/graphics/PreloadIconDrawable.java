package com.android.launcher3.graphics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.Property;
import android.util.SparseArray;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.anim.Interpolators;
import java.lang.ref.WeakReference;

public class PreloadIconDrawable extends FastBitmapDrawable {
    private static final int COLOR_SHADOW = 1426063360;
    private static final int COLOR_TRACK = 2012147438;
    private static final float COMPLETE_ANIM_FRACTION = 0.3f;
    private static final long DURATION_SCALE = 500;
    private static final Property<PreloadIconDrawable, Float> INTERNAL_STATE = new Property<PreloadIconDrawable, Float>(Float.TYPE, "internalStateProgress") {
        public Float get(PreloadIconDrawable object) {
            return Float.valueOf(object.mInternalStateProgress);
        }

        public void set(PreloadIconDrawable object, Float value) {
            object.setInternalProgress(value.floatValue());
        }
    };
    private static final int MAX_PAINT_ALPHA = 255;
    public static final int PATH_SIZE = 100;
    private static final float PROGRESS_GAP = 2.0f;
    private static final float PROGRESS_WIDTH = 7.0f;
    private static final float SMALL_SCALE = 0.6f;
    private static final SparseArray<WeakReference<Bitmap>> sShadowCache = new SparseArray<>();
    private ObjectAnimator mCurrentAnim;
    private float mIconScale;
    private final int mIndicatorColor;
    /* access modifiers changed from: private */
    public float mInternalStateProgress;
    private final ItemInfoWithIcon mItem;
    private final PathMeasure mPathMeasure = new PathMeasure();
    private final Paint mProgressPaint;
    private final Path mProgressPath;
    /* access modifiers changed from: private */
    public boolean mRanFinishAnimation;
    private final Path mScaledProgressPath;
    private final Path mScaledTrackPath;
    private Bitmap mShadowBitmap;
    private final Matrix mTmpMatrix = new Matrix();
    private int mTrackAlpha;
    private float mTrackLength;

    public PreloadIconDrawable(ItemInfoWithIcon info, Path progressPath, Context context) {
        super(info);
        this.mItem = info;
        this.mProgressPath = progressPath;
        this.mScaledTrackPath = new Path();
        this.mScaledProgressPath = new Path();
        this.mProgressPaint = new Paint(3);
        this.mProgressPaint.setStyle(Paint.Style.STROKE);
        this.mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mIndicatorColor = IconPalette.getPreloadProgressColor(context, this.mIconColor);
        setInternalProgress(0.0f);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mTmpMatrix.setScale(((((float) bounds.width()) - 14.0f) - 4.0f) / 100.0f, ((((float) bounds.height()) - 14.0f) - 4.0f) / 100.0f);
        this.mTmpMatrix.postTranslate(((float) bounds.left) + PROGRESS_WIDTH + PROGRESS_GAP, ((float) bounds.top) + PROGRESS_WIDTH + PROGRESS_GAP);
        this.mProgressPath.transform(this.mTmpMatrix, this.mScaledTrackPath);
        float scale = (float) (bounds.width() / 100);
        this.mProgressPaint.setStrokeWidth(PROGRESS_WIDTH * scale);
        this.mShadowBitmap = getShadowBitmap(bounds.width(), bounds.height(), PROGRESS_GAP * scale);
        this.mPathMeasure.setPath(this.mScaledTrackPath, true);
        this.mTrackLength = this.mPathMeasure.getLength();
        setInternalProgress(this.mInternalStateProgress);
    }

    private Bitmap getShadowBitmap(int width, int height, float shadowRadius) {
        int key = (width << 16) | height;
        WeakReference<Bitmap> shadowRef = sShadowCache.get(key);
        Bitmap shadow = shadowRef != null ? (Bitmap) shadowRef.get() : null;
        if (shadow != null) {
            return shadow;
        }
        Bitmap shadow2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(shadow2);
        this.mProgressPaint.setShadowLayer(shadowRadius, 0.0f, 0.0f, COLOR_SHADOW);
        this.mProgressPaint.setColor(COLOR_TRACK);
        this.mProgressPaint.setAlpha(255);
        c.drawPath(this.mScaledTrackPath, this.mProgressPaint);
        this.mProgressPaint.clearShadowLayer();
        c.setBitmap((Bitmap) null);
        sShadowCache.put(key, new WeakReference(shadow2));
        return shadow2;
    }

    public void drawInternal(Canvas canvas, Rect bounds) {
        if (this.mRanFinishAnimation) {
            super.drawInternal(canvas, bounds);
            return;
        }
        this.mProgressPaint.setColor(this.mIndicatorColor);
        this.mProgressPaint.setAlpha(this.mTrackAlpha);
        if (this.mShadowBitmap != null) {
            canvas.drawBitmap(this.mShadowBitmap, (float) bounds.left, (float) bounds.top, this.mProgressPaint);
        }
        canvas.drawPath(this.mScaledProgressPath, this.mProgressPaint);
        int saveCount = canvas.save();
        canvas.scale(this.mIconScale, this.mIconScale, bounds.exactCenterX(), bounds.exactCenterY());
        super.drawInternal(canvas, bounds);
        canvas.restoreToCount(saveCount);
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int level) {
        updateInternalState(((float) level) * 0.01f, getBounds().width() > 0, false);
        return true;
    }

    public void maybePerformFinishedAnimation() {
        if (this.mInternalStateProgress == 0.0f) {
            this.mInternalStateProgress = 1.0f;
        }
        updateInternalState(1.3f, true, true);
    }

    public boolean hasNotCompleted() {
        return !this.mRanFinishAnimation;
    }

    private void updateInternalState(float finalProgress, boolean shouldAnimate, boolean isFinish) {
        if (this.mCurrentAnim != null) {
            this.mCurrentAnim.cancel();
            this.mCurrentAnim = null;
        }
        if (Float.compare(finalProgress, this.mInternalStateProgress) != 0) {
            if (finalProgress < this.mInternalStateProgress) {
                shouldAnimate = false;
            }
            if (!shouldAnimate || this.mRanFinishAnimation) {
                setInternalProgress(finalProgress);
                return;
            }
            this.mCurrentAnim = ObjectAnimator.ofFloat(this, INTERNAL_STATE, new float[]{finalProgress});
            this.mCurrentAnim.setDuration((long) ((finalProgress - this.mInternalStateProgress) * 500.0f));
            this.mCurrentAnim.setInterpolator(Interpolators.LINEAR);
            if (isFinish) {
                this.mCurrentAnim.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        boolean unused = PreloadIconDrawable.this.mRanFinishAnimation = true;
                    }
                });
            }
            this.mCurrentAnim.start();
        }
    }

    /* access modifiers changed from: private */
    public void setInternalProgress(float progress) {
        this.mInternalStateProgress = progress;
        if (progress <= 0.0f) {
            this.mIconScale = SMALL_SCALE;
            this.mScaledTrackPath.reset();
            this.mTrackAlpha = 255;
            setIsDisabled(true);
        }
        if (progress < 1.0f && progress > 0.0f) {
            this.mPathMeasure.getSegment(0.0f, this.mTrackLength * progress, this.mScaledProgressPath, true);
            this.mIconScale = SMALL_SCALE;
            this.mTrackAlpha = 255;
            setIsDisabled(true);
        } else if (progress >= 1.0f) {
            setIsDisabled(this.mItem.isDisabled());
            this.mScaledTrackPath.set(this.mScaledProgressPath);
            float fraction = (progress - 1.0f) / COMPLETE_ANIM_FRACTION;
            if (fraction >= 1.0f) {
                this.mIconScale = 1.0f;
                this.mTrackAlpha = 0;
            } else {
                this.mTrackAlpha = Math.round((1.0f - fraction) * 255.0f);
                this.mIconScale = (0.39999998f * fraction) + SMALL_SCALE;
            }
        }
        invalidateSelf();
    }
}
