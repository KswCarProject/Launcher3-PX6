package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.touch.SwipeDetector;

public abstract class AbstractSlideInView extends AbstractFloatingView implements SwipeDetector.Listener {
    protected static Property<AbstractSlideInView, Float> TRANSLATION_SHIFT = new Property<AbstractSlideInView, Float>(Float.class, "translationShift") {
        public Float get(AbstractSlideInView view) {
            return Float.valueOf(view.mTranslationShift);
        }

        public void set(AbstractSlideInView view, Float value) {
            view.setTranslationShift(value.floatValue());
        }
    };
    protected static final float TRANSLATION_SHIFT_CLOSED = 1.0f;
    protected static final float TRANSLATION_SHIFT_OPENED = 0.0f;
    protected View mContent;
    protected final Launcher mLauncher;
    protected boolean mNoIntercept;
    /* access modifiers changed from: protected */
    public final ObjectAnimator mOpenCloseAnimator;
    protected Interpolator mScrollInterpolator;
    protected final SwipeDetector mSwipeDetector;
    protected float mTranslationShift = 1.0f;

    public AbstractSlideInView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLauncher = Launcher.getLauncher(context);
        this.mScrollInterpolator = Interpolators.SCROLL_CUBIC;
        this.mSwipeDetector = new SwipeDetector(context, (SwipeDetector.Listener) this, SwipeDetector.VERTICAL);
        this.mOpenCloseAnimator = LauncherAnimUtils.ofPropertyValuesHolder(this, new PropertyValuesHolder[0]);
        this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AbstractSlideInView.this.mSwipeDetector.finishedScrolling();
                AbstractSlideInView.this.announceAccessibilityChanges();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setTranslationShift(float translationShift) {
        this.mTranslationShift = translationShift;
        this.mContent.setTranslationY(this.mTranslationShift * ((float) this.mContent.getHeight()));
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (this.mNoIntercept) {
            return false;
        }
        this.mSwipeDetector.setDetectableScrollConditions(this.mSwipeDetector.isIdleState() ? 2 : 0, false);
        this.mSwipeDetector.onTouchEvent(ev);
        if (this.mSwipeDetector.isDraggingOrSettling() || !this.mLauncher.getDragLayer().isEventOverView(this.mContent, ev)) {
            return true;
        }
        return false;
    }

    public boolean onControllerTouchEvent(MotionEvent ev) {
        this.mSwipeDetector.onTouchEvent(ev);
        if (ev.getAction() == 1 && this.mSwipeDetector.isIdleState() && !this.mLauncher.getDragLayer().isEventOverView(this.mContent, ev)) {
            close(true);
        }
        return true;
    }

    public void onDragStart(boolean start) {
    }

    public boolean onDrag(float displacement, float velocity) {
        float range = (float) this.mContent.getHeight();
        setTranslationShift(Utilities.boundToRange(displacement, 0.0f, range) / range);
        return true;
    }

    public void onDragEnd(float velocity, boolean fling) {
        if ((!fling || velocity <= 0.0f) && this.mTranslationShift <= 0.5f) {
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setDuration(SwipeDetector.calculateDuration(velocity, this.mTranslationShift)).setInterpolator(Interpolators.DEACCEL);
            this.mOpenCloseAnimator.start();
            return;
        }
        this.mScrollInterpolator = Interpolators.scrollInterpolatorForVelocity(velocity);
        this.mOpenCloseAnimator.setDuration(SwipeDetector.calculateDuration(velocity, 1.0f - this.mTranslationShift));
        close(true);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate, long defaultDuration) {
        if (this.mIsOpen && !animate) {
            this.mOpenCloseAnimator.cancel();
            setTranslationShift(1.0f);
            onCloseComplete();
        } else if (this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{1.0f})});
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AbstractSlideInView.this.onCloseComplete();
                }
            });
            if (this.mSwipeDetector.isIdleState()) {
                this.mOpenCloseAnimator.setDuration(defaultDuration).setInterpolator(Interpolators.ACCEL);
            } else {
                this.mOpenCloseAnimator.setInterpolator(this.mScrollInterpolator);
            }
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onCloseComplete() {
        this.mIsOpen = false;
        this.mLauncher.getDragLayer().removeView(this);
    }
}
