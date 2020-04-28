package com.android.launcher3.util;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;

public class FlingAnimation implements ValueAnimator.AnimatorUpdateListener, Runnable {
    private static final int DRAG_END_DELAY = 300;
    private static final float MAX_ACCELERATION = 0.5f;
    protected float mAX;
    protected float mAY;
    protected final TimeInterpolator mAlphaInterpolator = new DecelerateInterpolator(0.75f);
    protected float mAnimationTimeFraction;
    protected final DragLayer mDragLayer;
    protected final DropTarget.DragObject mDragObject;
    /* access modifiers changed from: private */
    public final ButtonDropTarget mDropTarget;
    protected int mDuration;
    protected Rect mFrom;
    protected Rect mIconRect;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    protected final float mUX;
    protected final float mUY;

    public FlingAnimation(DropTarget.DragObject d, PointF vel, ButtonDropTarget dropTarget, Launcher launcher) {
        this.mDropTarget = dropTarget;
        this.mLauncher = launcher;
        this.mDragObject = d;
        this.mUX = vel.x / 1000.0f;
        this.mUY = vel.y / 1000.0f;
        this.mDragLayer = this.mLauncher.getDragLayer();
    }

    public void run() {
        this.mIconRect = this.mDropTarget.getIconRect(this.mDragObject);
        this.mFrom = new Rect();
        this.mDragLayer.getViewRectRelativeToSelf(this.mDragObject.dragView, this.mFrom);
        float scale = this.mDragObject.dragView.getScaleX();
        float xOffset = ((scale - 1.0f) * ((float) this.mDragObject.dragView.getMeasuredWidth())) / 2.0f;
        float yOffset = ((scale - 1.0f) * ((float) this.mDragObject.dragView.getMeasuredHeight())) / 2.0f;
        Rect rect = this.mFrom;
        rect.left = (int) (((float) rect.left) + xOffset);
        Rect rect2 = this.mFrom;
        rect2.right = (int) (((float) rect2.right) - xOffset);
        Rect rect3 = this.mFrom;
        rect3.top = (int) (((float) rect3.top) + yOffset);
        Rect rect4 = this.mFrom;
        rect4.bottom = (int) (((float) rect4.bottom) - yOffset);
        this.mDuration = Math.abs(this.mUY) > Math.abs(this.mUX) ? initFlingUpDuration() : initFlingLeftDuration();
        this.mAnimationTimeFraction = ((float) this.mDuration) / ((float) (this.mDuration + 300));
        this.mDragObject.dragView.setColor(0);
        final int duration = this.mDuration + 300;
        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDragLayer.animateView(this.mDragObject.dragView, this, duration, new TimeInterpolator() {
            private int mCount = -1;
            private float mOffset = 0.0f;

            public float getInterpolation(float t) {
                if (this.mCount < 0) {
                    this.mCount++;
                } else if (this.mCount == 0) {
                    this.mOffset = Math.min(0.5f, ((float) (AnimationUtils.currentAnimationTimeMillis() - startTime)) / ((float) duration));
                    this.mCount++;
                }
                return Math.min(1.0f, this.mOffset + t);
            }
        }, new Runnable() {
            public void run() {
                FlingAnimation.this.mLauncher.getStateManager().goToState(LauncherState.NORMAL);
                FlingAnimation.this.mDropTarget.completeDrop(FlingAnimation.this.mDragObject);
            }
        }, 0, (View) null);
    }

    /* access modifiers changed from: protected */
    public int initFlingUpDuration() {
        float sY = (float) (-this.mFrom.bottom);
        float d = (this.mUY * this.mUY) + (sY * 2.0f * 0.5f);
        if (d >= 0.0f) {
            this.mAY = 0.5f;
        } else {
            d = 0.0f;
            this.mAY = (this.mUY * this.mUY) / ((-sY) * 2.0f);
        }
        double t = (((double) (-this.mUY)) - Math.sqrt((double) d)) / ((double) this.mAY);
        this.mAX = (float) (((((double) ((-this.mFrom.exactCenterX()) + this.mIconRect.exactCenterX())) - (((double) this.mUX) * t)) * 2.0d) / (t * t));
        return (int) Math.round(t);
    }

    /* access modifiers changed from: protected */
    public int initFlingLeftDuration() {
        float sX = (float) (-this.mFrom.right);
        float d = (this.mUX * this.mUX) + (sX * 2.0f * 0.5f);
        if (d >= 0.0f) {
            this.mAX = 0.5f;
        } else {
            d = 0.0f;
            this.mAX = (this.mUX * this.mUX) / ((-sX) * 2.0f);
        }
        double t = (((double) (-this.mUX)) - Math.sqrt((double) d)) / ((double) this.mAX);
        this.mAY = (float) (((((double) ((-this.mFrom.exactCenterY()) + this.mIconRect.exactCenterY())) - (((double) this.mUY) * t)) * 2.0d) / (t * t));
        return (int) Math.round(t);
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        float t;
        float t2 = animation.getAnimatedFraction();
        if (t2 > this.mAnimationTimeFraction) {
            t = 1.0f;
        } else {
            t = t2 / this.mAnimationTimeFraction;
        }
        DragView dragView = (DragView) this.mDragLayer.getAnimatedView();
        float time = ((float) this.mDuration) * t;
        dragView.setTranslationX((this.mUX * time) + ((float) this.mFrom.left) + (((this.mAX * time) * time) / 2.0f));
        dragView.setTranslationY((this.mUY * time) + ((float) this.mFrom.top) + (((this.mAY * time) * time) / 2.0f));
        dragView.setAlpha(1.0f - this.mAlphaInterpolator.getInterpolation(t));
    }
}
