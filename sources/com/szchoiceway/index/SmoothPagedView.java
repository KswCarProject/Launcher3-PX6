package com.szchoiceway.index;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public abstract class SmoothPagedView extends PagedView {
    static final int DEFAULT_MODE = 0;
    private static final float SMOOTHING_CONSTANT = ((float) (0.016d / Math.log(0.75d)));
    private static final float SMOOTHING_SPEED = 0.75f;
    static final int X_LARGE_MODE = 1;
    private float mBaseLineFlingVelocity;
    private float mFlingVelocityInfluence;
    private Interpolator mScrollInterpolator;
    int mScrollMode;

    public static class OvershootInterpolator implements Interpolator {
        private static final float DEFAULT_TENSION = 1.3f;
        private float mTension = DEFAULT_TENSION;

        public void setDistance(int distance) {
            float f = DEFAULT_TENSION;
            if (distance > 0) {
                f = DEFAULT_TENSION / ((float) distance);
            }
            this.mTension = f;
        }

        public void disableSettle() {
            this.mTension = 0.0f;
        }

        public float getInterpolation(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * (((this.mTension + 1.0f) * t2) + this.mTension)) + 1.0f;
        }
    }

    public SmoothPagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SmoothPagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        boolean z = true;
        this.mUsePagingTouchSlop = false;
        this.mDeferScrollUpdate = this.mScrollMode == 1 ? false : z;
    }

    /* access modifiers changed from: protected */
    public int getScrollMode() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public void init() {
        super.init();
        this.mScrollMode = getScrollMode();
        if (this.mScrollMode == 0) {
            this.mBaseLineFlingVelocity = 2500.0f;
            this.mFlingVelocityInfluence = 0.4f;
            this.mScrollInterpolator = new OvershootInterpolator();
            this.mScroller = new Scroller(getContext(), this.mScrollInterpolator);
        }
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        if (this.mScrollMode == 1) {
            super.snapToDestination();
        } else {
            snapToPageWithVelocity(getPageNearestToCenterOfScreen(), 0);
        }
    }

    /* access modifiers changed from: protected */
    public void snapToPageWithVelocity(int whichPage, int velocity) {
        if (this.mScrollMode == 1) {
            super.snapToPageWithVelocity(whichPage, velocity);
        } else {
            snapToPageWithVelocity(whichPage, 0, true);
        }
    }

    private void snapToPageWithVelocity(int whichPage, int velocity, boolean settle) {
        int duration;
        int whichPage2 = Math.max(0, Math.min(whichPage, getChildCount() - 1));
        int screenDelta = Math.max(1, Math.abs(whichPage2 - this.mCurrentPage));
        int delta = (getChildOffset(whichPage2) - getRelativeChildOffset(whichPage2)) - this.mUnboundedScrollX;
        int duration2 = (screenDelta + 1) * 100;
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        if (settle) {
            ((OvershootInterpolator) this.mScrollInterpolator).setDistance(screenDelta);
        } else {
            ((OvershootInterpolator) this.mScrollInterpolator).disableSettle();
        }
        int velocity2 = Math.abs(velocity);
        if (velocity2 > 0) {
            duration = (int) (((float) duration2) + ((((float) duration2) / (((float) velocity2) / this.mBaseLineFlingVelocity)) * this.mFlingVelocityInfluence));
        } else {
            duration = duration2 + 100;
        }
        snapToPage(whichPage2, delta, duration);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage) {
        if (this.mScrollMode == 1) {
            super.snapToPage(whichPage);
        } else {
            snapToPageWithVelocity(whichPage, 0, false);
        }
    }

    public void computeScroll() {
        if (this.mScrollMode == 1) {
            super.computeScroll();
        } else if (!computeScrollHelper() && this.mTouchState == 1) {
            float now = ((float) System.nanoTime()) / 1.0E9f;
            float e = (float) Math.exp((double) ((now - this.mSmoothingTime) / SMOOTHING_CONSTANT));
            float dx = this.mTouchX - ((float) this.mUnboundedScrollX);
            scrollTo(Math.round(((float) this.mUnboundedScrollX) + (dx * e)), getScrollY());
            this.mSmoothingTime = now;
            if (dx > 1.0f || dx < -1.0f) {
                invalidate();
            }
        }
    }
}
