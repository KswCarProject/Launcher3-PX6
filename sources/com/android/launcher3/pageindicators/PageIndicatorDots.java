package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Themes;

public class PageIndicatorDots extends View implements PageIndicator {
    private static final long ANIMATION_DURATION = 150;
    private static final Property<PageIndicatorDots, Float> CURRENT_POSITION = new Property<PageIndicatorDots, Float>(Float.TYPE, "current_position") {
        public Float get(PageIndicatorDots obj) {
            return Float.valueOf(obj.mCurrentPosition);
        }

        public void set(PageIndicatorDots obj, Float pos) {
            float unused = obj.mCurrentPosition = pos.floatValue();
            obj.invalidate();
            obj.invalidateOutline();
        }
    };
    private static final int ENTER_ANIMATION_DURATION = 400;
    private static final float ENTER_ANIMATION_OVERSHOOT_TENSION = 4.9f;
    private static final int ENTER_ANIMATION_STAGGERED_DELAY = 150;
    private static final int ENTER_ANIMATION_START_DELAY = 300;
    private static final float SHIFT_PER_ANIMATION = 0.5f;
    private static final float SHIFT_THRESHOLD = 0.1f;
    private static final RectF sTempRect = new RectF();
    private final int mActiveColor;
    private int mActivePage;
    /* access modifiers changed from: private */
    public ObjectAnimator mAnimator;
    private final Paint mCirclePaint;
    /* access modifiers changed from: private */
    public float mCurrentPosition;
    /* access modifiers changed from: private */
    public final float mDotRadius;
    /* access modifiers changed from: private */
    public float[] mEntryAnimationRadiusFactors;
    /* access modifiers changed from: private */
    public float mFinalPosition;
    private final int mInActiveColor;
    private final boolean mIsRtl;
    private int mNumPages;

    public PageIndicatorDots(Context context) {
        this(context, (AttributeSet) null);
    }

    public PageIndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCirclePaint = new Paint(1);
        this.mCirclePaint.setStyle(Paint.Style.FILL);
        this.mDotRadius = getResources().getDimension(R.dimen.page_indicator_dot_size) / 2.0f;
        setOutlineProvider(new MyOutlineProver());
        this.mActiveColor = Themes.getColorAccent(context);
        this.mInActiveColor = Themes.getAttrColor(context, 16843820);
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    public void setScroll(int currentScroll, int totalScroll) {
        if (this.mNumPages > 1) {
            if (this.mIsRtl) {
                currentScroll = totalScroll - currentScroll;
            }
            int scrollPerPage = totalScroll / (this.mNumPages - 1);
            int pageToLeft = currentScroll / scrollPerPage;
            int pageToLeftScroll = pageToLeft * scrollPerPage;
            int pageToRightScroll = pageToLeftScroll + scrollPerPage;
            float scrollThreshold = ((float) scrollPerPage) * 0.1f;
            if (((float) currentScroll) < ((float) pageToLeftScroll) + scrollThreshold) {
                animateToPosition((float) pageToLeft);
            } else if (((float) currentScroll) > ((float) pageToRightScroll) - scrollThreshold) {
                animateToPosition((float) (pageToLeft + 1));
            } else {
                animateToPosition(((float) pageToLeft) + 0.5f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateToPosition(float position) {
        this.mFinalPosition = position;
        if (Math.abs(this.mCurrentPosition - this.mFinalPosition) < 0.1f) {
            this.mCurrentPosition = this.mFinalPosition;
        }
        if (this.mAnimator == null && Float.compare(this.mCurrentPosition, this.mFinalPosition) != 0) {
            this.mAnimator = ObjectAnimator.ofFloat(this, CURRENT_POSITION, new float[]{this.mCurrentPosition > this.mFinalPosition ? this.mCurrentPosition - 0.5f : this.mCurrentPosition + 0.5f});
            this.mAnimator.addListener(new AnimationCycleListener());
            this.mAnimator.setDuration(ANIMATION_DURATION);
            this.mAnimator.start();
        }
    }

    public void stopAllAnimations() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
            this.mAnimator = null;
        }
        this.mFinalPosition = (float) this.mActivePage;
        CURRENT_POSITION.set(this, Float.valueOf(this.mFinalPosition));
    }

    public void prepareEntryAnimation() {
        this.mEntryAnimationRadiusFactors = new float[this.mNumPages];
        invalidate();
    }

    public void playEntryAnimation() {
        int count = this.mEntryAnimationRadiusFactors.length;
        if (count == 0) {
            this.mEntryAnimationRadiusFactors = null;
            invalidate();
            return;
        }
        Interpolator interpolator = new OvershootInterpolator(ENTER_ANIMATION_OVERSHOOT_TENSION);
        AnimatorSet animSet = new AnimatorSet();
        for (int i = 0; i < count; i++) {
            ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(400);
            final int index = i;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    PageIndicatorDots.this.mEntryAnimationRadiusFactors[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    PageIndicatorDots.this.invalidate();
                }
            });
            anim.setInterpolator(interpolator);
            anim.setStartDelay((long) ((i * 150) + 300));
            animSet.play(anim);
        }
        animSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                float[] unused = PageIndicatorDots.this.mEntryAnimationRadiusFactors = null;
                PageIndicatorDots.this.invalidateOutline();
                PageIndicatorDots.this.invalidate();
            }
        });
        animSet.start();
    }

    public void setActiveMarker(int activePage) {
        if (this.mActivePage != activePage) {
            this.mActivePage = activePage;
        }
    }

    public void setMarkersCount(int numMarkers) {
        this.mNumPages = numMarkers;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824 ? View.MeasureSpec.getSize(widthMeasureSpec) : (int) (((float) ((this.mNumPages * 3) + 2)) * this.mDotRadius), View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824 ? View.MeasureSpec.getSize(heightMeasureSpec) : (int) (this.mDotRadius * 4.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float circleGap = this.mDotRadius * 3.0f;
        float x = this.mDotRadius + (((((float) getWidth()) - (((float) this.mNumPages) * circleGap)) + this.mDotRadius) / 2.0f);
        float y = (float) (canvas.getHeight() / 2);
        int i = 0;
        if (this.mEntryAnimationRadiusFactors != null) {
            if (this.mIsRtl) {
                x = ((float) getWidth()) - x;
                circleGap = -circleGap;
            }
            while (true) {
                int i2 = i;
                if (i2 < this.mEntryAnimationRadiusFactors.length) {
                    this.mCirclePaint.setColor(i2 == this.mActivePage ? this.mActiveColor : this.mInActiveColor);
                    canvas.drawCircle(x, y, this.mDotRadius * this.mEntryAnimationRadiusFactors[i2], this.mCirclePaint);
                    x += circleGap;
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        } else {
            this.mCirclePaint.setColor(this.mInActiveColor);
            while (true) {
                int i3 = i;
                if (i3 < this.mNumPages) {
                    canvas.drawCircle(x, y, this.mDotRadius, this.mCirclePaint);
                    x += circleGap;
                    i = i3 + 1;
                } else {
                    this.mCirclePaint.setColor(this.mActiveColor);
                    canvas.drawRoundRect(getActiveRect(), this.mDotRadius, this.mDotRadius, this.mCirclePaint);
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public RectF getActiveRect() {
        float startCircle = (float) ((int) this.mCurrentPosition);
        float delta = this.mCurrentPosition - startCircle;
        float circleGap = this.mDotRadius * 3.0f;
        sTempRect.top = (((float) getHeight()) * 0.5f) - this.mDotRadius;
        sTempRect.bottom = (((float) getHeight()) * 0.5f) + this.mDotRadius;
        sTempRect.left = (startCircle * circleGap) + (((((float) getWidth()) - (((float) this.mNumPages) * circleGap)) + this.mDotRadius) / 2.0f);
        sTempRect.right = sTempRect.left + (this.mDotRadius * 2.0f);
        if (delta < 0.5f) {
            sTempRect.right += delta * circleGap * 2.0f;
        } else {
            sTempRect.right += circleGap;
            sTempRect.left += (delta - 0.5f) * circleGap * 2.0f;
        }
        if (this.mIsRtl) {
            float rectWidth = sTempRect.width();
            sTempRect.right = ((float) getWidth()) - sTempRect.left;
            sTempRect.left = sTempRect.right - rectWidth;
        }
        return sTempRect;
    }

    private class MyOutlineProver extends ViewOutlineProvider {
        private MyOutlineProver() {
        }

        public void getOutline(View view, Outline outline) {
            if (PageIndicatorDots.this.mEntryAnimationRadiusFactors == null) {
                RectF activeRect = PageIndicatorDots.this.getActiveRect();
                outline.setRoundRect((int) activeRect.left, (int) activeRect.top, (int) activeRect.right, (int) activeRect.bottom, PageIndicatorDots.this.mDotRadius);
            }
        }
    }

    private class AnimationCycleListener extends AnimatorListenerAdapter {
        private boolean mCancelled;

        private AnimationCycleListener() {
            this.mCancelled = false;
        }

        public void onAnimationCancel(Animator animation) {
            this.mCancelled = true;
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.mCancelled) {
                ObjectAnimator unused = PageIndicatorDots.this.mAnimator = null;
                PageIndicatorDots.this.animateToPosition(PageIndicatorDots.this.mFinalPosition);
            }
        }
    }
}
