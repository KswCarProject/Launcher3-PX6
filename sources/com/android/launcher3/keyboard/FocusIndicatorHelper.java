package com.android.launcher3.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Property;
import android.view.View;
import com.android.launcher3.R;

public abstract class FocusIndicatorHelper implements View.OnFocusChangeListener, ValueAnimator.AnimatorUpdateListener {
    public static final Property<FocusIndicatorHelper, Float> ALPHA = new Property<FocusIndicatorHelper, Float>(Float.TYPE, "alpha") {
        public void set(FocusIndicatorHelper object, Float value) {
            object.setAlpha(value.floatValue());
        }

        public Float get(FocusIndicatorHelper object) {
            return Float.valueOf(object.mAlpha);
        }
    };
    private static final long ANIM_DURATION = 150;
    private static final float MIN_VISIBLE_ALPHA = 0.2f;
    private static final RectEvaluator RECT_EVALUATOR = new RectEvaluator(new Rect());
    public static final Property<FocusIndicatorHelper, Float> SHIFT = new Property<FocusIndicatorHelper, Float>(Float.TYPE, "shift") {
        public void set(FocusIndicatorHelper object, Float value) {
            float unused = object.mShift = value.floatValue();
        }

        public Float get(FocusIndicatorHelper object) {
            return Float.valueOf(object.mShift);
        }
    };
    private static final Rect sTempRect1 = new Rect();
    private static final Rect sTempRect2 = new Rect();
    /* access modifiers changed from: private */
    public float mAlpha;
    private final View mContainer;
    private ObjectAnimator mCurrentAnimation;
    private View mCurrentView;
    private final Rect mDirtyRect = new Rect();
    private boolean mIsDirty = false;
    private View mLastFocusedView;
    private final int mMaxAlpha;
    private final Paint mPaint;
    /* access modifiers changed from: private */
    public float mShift;
    private View mTargetView;

    public abstract void viewToRect(View view, Rect rect);

    public FocusIndicatorHelper(View container) {
        this.mContainer = container;
        this.mPaint = new Paint(1);
        int color = container.getResources().getColor(R.color.focused_background);
        this.mMaxAlpha = Color.alpha(color);
        this.mPaint.setColor(-16777216 | color);
        setAlpha(0.0f);
        this.mShift = 0.0f;
    }

    /* access modifiers changed from: protected */
    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
        this.mPaint.setAlpha((int) (this.mAlpha * ((float) this.mMaxAlpha)));
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        invalidateDirty();
    }

    /* access modifiers changed from: protected */
    public void invalidateDirty() {
        if (this.mIsDirty) {
            this.mContainer.invalidate(this.mDirtyRect);
            this.mIsDirty = false;
        }
        Rect newRect = getDrawRect();
        if (newRect != null) {
            this.mContainer.invalidate(newRect);
        }
    }

    public void draw(Canvas c) {
        Rect newRect;
        if (this.mAlpha > 0.0f && (newRect = getDrawRect()) != null) {
            this.mDirtyRect.set(newRect);
            c.drawRect(this.mDirtyRect, this.mPaint);
            this.mIsDirty = true;
        }
    }

    private Rect getDrawRect() {
        if (this.mCurrentView == null || !this.mCurrentView.isAttachedToWindow()) {
            return null;
        }
        viewToRect(this.mCurrentView, sTempRect1);
        if (this.mShift <= 0.0f || this.mTargetView == null) {
            return sTempRect1;
        }
        viewToRect(this.mTargetView, sTempRect2);
        return RECT_EVALUATOR.evaluate(this.mShift, sTempRect1, sTempRect2);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        View view = null;
        if (hasFocus) {
            endCurrentAnimation();
            if (this.mAlpha > 0.2f) {
                this.mTargetView = v;
                this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{1.0f}), PropertyValuesHolder.ofFloat(SHIFT, new float[]{1.0f})});
                this.mCurrentAnimation.addListener(new ViewSetListener(v, true));
            } else {
                setCurrentView(v);
                this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{1.0f})});
            }
            this.mLastFocusedView = v;
        } else if (this.mLastFocusedView == v) {
            this.mLastFocusedView = null;
            endCurrentAnimation();
            this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{0.0f})});
            this.mCurrentAnimation.addListener(new ViewSetListener((View) null, false));
        }
        invalidateDirty();
        if (hasFocus) {
            view = v;
        }
        this.mLastFocusedView = view;
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.addUpdateListener(this);
            this.mCurrentAnimation.setDuration(ANIM_DURATION).start();
        }
    }

    /* access modifiers changed from: protected */
    public void endCurrentAnimation() {
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.cancel();
            this.mCurrentAnimation = null;
        }
    }

    /* access modifiers changed from: protected */
    public void setCurrentView(View v) {
        this.mCurrentView = v;
        this.mShift = 0.0f;
        this.mTargetView = null;
    }

    private class ViewSetListener extends AnimatorListenerAdapter {
        private final boolean mCallOnCancel;
        private boolean mCalled = false;
        private final View mViewToSet;

        public ViewSetListener(View v, boolean callOnCancel) {
            this.mViewToSet = v;
            this.mCallOnCancel = callOnCancel;
        }

        public void onAnimationCancel(Animator animation) {
            if (!this.mCallOnCancel) {
                this.mCalled = true;
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.mCalled) {
                FocusIndicatorHelper.this.setCurrentView(this.mViewToSet);
                this.mCalled = true;
            }
        }
    }

    public static class SimpleFocusIndicatorHelper extends FocusIndicatorHelper {
        public SimpleFocusIndicatorHelper(View container) {
            super(container);
        }

        public void viewToRect(View v, Rect outRect) {
            outRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        }
    }
}
