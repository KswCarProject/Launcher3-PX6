package com.szchoiceway.index;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.szchoiceway.index.DragLayer;

public class DragView extends View {
    /* access modifiers changed from: private */
    public static float sDragAlpha = 1.0f;
    ValueAnimator mAnim;
    private Bitmap mBitmap;
    private Bitmap mCrossFadeBitmap;
    /* access modifiers changed from: private */
    public float mCrossFadeProgress = 0.0f;
    private DragLayer mDragLayer = null;
    private Rect mDragRegion = null;
    private Point mDragVisualizeOffset = null;
    private boolean mHasDrawn = false;
    private float mInitialScale = 1.0f;
    /* access modifiers changed from: private */
    public float mOffsetX = 0.0f;
    /* access modifiers changed from: private */
    public float mOffsetY = 0.0f;
    private Paint mPaint;
    private int mRegistrationX;
    private int mRegistrationY;

    public DragView(Launcher launcher, Bitmap bitmap, int registrationX, int registrationY, int left, int top, int width, int height, float initialScale) {
        super(launcher);
        this.mDragLayer = launcher.getDragLayer();
        this.mInitialScale = initialScale;
        Resources res = getResources();
        final float offsetX = (float) res.getDimensionPixelSize(R.dimen.dragViewOffsetX);
        final float offsetY = (float) res.getDimensionPixelSize(R.dimen.dragViewOffsetY);
        final float scale = (((float) width) + ((float) res.getDimensionPixelSize(R.dimen.dragViewScale))) / ((float) width);
        setScaleX(initialScale);
        setScaleY(initialScale);
        this.mAnim = LauncherAnimUtils.ofFloat(this, 0.0f, 1.0f);
        this.mAnim.setDuration(150);
        final float f = initialScale;
        this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                int deltaX = (int) ((offsetX * value) - DragView.this.mOffsetX);
                int deltaY = (int) ((offsetY * value) - DragView.this.mOffsetY);
                float unused = DragView.this.mOffsetX = DragView.this.mOffsetX + ((float) deltaX);
                float unused2 = DragView.this.mOffsetY = DragView.this.mOffsetY + ((float) deltaY);
                DragView.this.setScaleX(f + ((scale - f) * value));
                DragView.this.setScaleY(f + ((scale - f) * value));
                if (DragView.sDragAlpha != 1.0f) {
                    DragView.this.setAlpha((DragView.sDragAlpha * value) + (1.0f - value));
                }
                if (DragView.this.getParent() == null) {
                    animation.cancel();
                    return;
                }
                DragView.this.setTranslationX(DragView.this.getTranslationX() + ((float) deltaX));
                DragView.this.setTranslationY(DragView.this.getTranslationY() + ((float) deltaY));
            }
        });
        this.mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
        setDragRegion(new Rect(0, 0, width, height));
        this.mRegistrationX = registrationX;
        this.mRegistrationY = registrationY;
        int ms = View.MeasureSpec.makeMeasureSpec(0, 0);
        measure(ms, ms);
        this.mPaint = new Paint(2);
    }

    public float getOffsetY() {
        return this.mOffsetY;
    }

    public int getDragRegionLeft() {
        return this.mDragRegion.left;
    }

    public int getDragRegionTop() {
        return this.mDragRegion.top;
    }

    public int getDragRegionWidth() {
        return this.mDragRegion.width();
    }

    public int getDragRegionHeight() {
        return this.mDragRegion.height();
    }

    public void setDragVisualizeOffset(Point p) {
        this.mDragVisualizeOffset = p;
    }

    public Point getDragVisualizeOffset() {
        return this.mDragVisualizeOffset;
    }

    public void setDragRegion(Rect r) {
        this.mDragRegion = r;
    }

    public Rect getDragRegion() {
        return this.mDragRegion;
    }

    public float getInitialScale() {
        return this.mInitialScale;
    }

    public void updateInitialScaleToCurrentScale() {
        this.mInitialScale = getScaleX();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.mBitmap.getWidth(), this.mBitmap.getHeight());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean crossFade = true;
        this.mHasDrawn = true;
        if (this.mCrossFadeProgress <= 0.0f || this.mCrossFadeBitmap == null) {
            crossFade = false;
        }
        if (crossFade) {
            this.mPaint.setAlpha(crossFade ? (int) ((1.0f - this.mCrossFadeProgress) * 255.0f) : 255);
        }
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
        if (crossFade) {
            this.mPaint.setAlpha((int) (this.mCrossFadeProgress * 255.0f));
            canvas.save();
            canvas.scale((((float) this.mBitmap.getWidth()) * 1.0f) / ((float) this.mCrossFadeBitmap.getWidth()), (((float) this.mBitmap.getHeight()) * 1.0f) / ((float) this.mCrossFadeBitmap.getHeight()));
            canvas.drawBitmap(this.mCrossFadeBitmap, 0.0f, 0.0f, this.mPaint);
            canvas.restore();
        }
    }

    public void setCrossFadeBitmap(Bitmap crossFadeBitmap) {
        this.mCrossFadeBitmap = crossFadeBitmap;
    }

    public void crossFade(int duration) {
        ValueAnimator va = LauncherAnimUtils.ofFloat(this, 0.0f, 1.0f);
        va.setDuration((long) duration);
        va.setInterpolator(new DecelerateInterpolator(1.5f));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float unused = DragView.this.mCrossFadeProgress = animation.getAnimatedFraction();
            }
        });
        va.start();
    }

    public void setColor(int color) {
        if (this.mPaint == null) {
            this.mPaint = new Paint(2);
        }
        if (color != 0) {
            this.mPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        } else {
            this.mPaint.setColorFilter((ColorFilter) null);
        }
        invalidate();
    }

    public boolean hasDrawn() {
        return this.mHasDrawn;
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.mPaint.setAlpha((int) (255.0f * alpha));
        invalidate();
    }

    public void show(int touchX, int touchY) {
        this.mDragLayer.addView(this);
        DragLayer.LayoutParams lp = new DragLayer.LayoutParams(0, 0);
        lp.width = this.mBitmap.getWidth();
        lp.height = this.mBitmap.getHeight();
        lp.customPosition = true;
        setLayoutParams(lp);
        setTranslationX((float) (touchX - this.mRegistrationX));
        setTranslationY((float) (touchY - this.mRegistrationY));
        post(new Runnable() {
            public void run() {
                DragView.this.mAnim.start();
            }
        });
    }

    public void cancelAnimation() {
        if (this.mAnim != null && this.mAnim.isRunning()) {
            this.mAnim.cancel();
        }
    }

    public void resetLayoutParams() {
        this.mOffsetY = 0.0f;
        this.mOffsetX = 0.0f;
        requestLayout();
    }

    /* access modifiers changed from: package-private */
    public void move(int touchX, int touchY) {
        setTranslationX((float) ((touchX - this.mRegistrationX) + ((int) this.mOffsetX)));
        setTranslationY((float) ((touchY - this.mRegistrationY) + ((int) this.mOffsetY)));
    }

    /* access modifiers changed from: package-private */
    public void remove() {
        if (getParent() != null) {
            this.mDragLayer.removeView(this);
        }
    }
}
