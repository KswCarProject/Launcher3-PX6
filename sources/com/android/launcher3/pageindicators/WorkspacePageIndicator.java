package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.uioverrides.WallpaperColorInfo;

public class WorkspacePageIndicator extends View implements Insettable, PageIndicator {
    private static final int ANIMATOR_COUNT = 3;
    public static final int BLACK_ALPHA = 165;
    private static final int LINE_ALPHA_ANIMATOR_INDEX = 0;
    private static final int LINE_ANIMATE_DURATION = ViewConfiguration.getScrollBarFadeDuration();
    private static final int LINE_FADE_DELAY = ViewConfiguration.getScrollDefaultDelay();
    private static final Property<WorkspacePageIndicator, Float> NUM_PAGES = new Property<WorkspacePageIndicator, Float>(Float.class, "num_pages") {
        public Float get(WorkspacePageIndicator obj) {
            return Float.valueOf(obj.mNumPagesFloat);
        }

        public void set(WorkspacePageIndicator obj, Float numPages) {
            float unused = obj.mNumPagesFloat = numPages.floatValue();
            obj.invalidate();
        }
    };
    private static final int NUM_PAGES_ANIMATOR_INDEX = 1;
    private static final Property<WorkspacePageIndicator, Integer> PAINT_ALPHA = new Property<WorkspacePageIndicator, Integer>(Integer.class, "paint_alpha") {
        public Integer get(WorkspacePageIndicator obj) {
            return Integer.valueOf(obj.mLinePaint.getAlpha());
        }

        public void set(WorkspacePageIndicator obj, Integer alpha) {
            obj.mLinePaint.setAlpha(alpha.intValue());
            obj.invalidate();
        }
    };
    private static final Property<WorkspacePageIndicator, Integer> TOTAL_SCROLL = new Property<WorkspacePageIndicator, Integer>(Integer.class, "total_scroll") {
        public Integer get(WorkspacePageIndicator obj) {
            return Integer.valueOf(obj.mTotalScroll);
        }

        public void set(WorkspacePageIndicator obj, Integer totalScroll) {
            int unused = obj.mTotalScroll = totalScroll.intValue();
            obj.invalidate();
        }
    };
    private static final int TOTAL_SCROLL_ANIMATOR_INDEX = 2;
    public static final int WHITE_ALPHA = 178;
    private int mActiveAlpha;
    /* access modifiers changed from: private */
    public ValueAnimator[] mAnimators;
    private int mCurrentScroll;
    private final Handler mDelayedLineFadeHandler;
    private Runnable mHideLineRunnable;
    private final Launcher mLauncher;
    private final int mLineHeight;
    /* access modifiers changed from: private */
    public Paint mLinePaint;
    /* access modifiers changed from: private */
    public float mNumPagesFloat;
    private boolean mShouldAutoHide;
    private int mToAlpha;
    /* access modifiers changed from: private */
    public int mTotalScroll;

    public WorkspacePageIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public WorkspacePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorkspacePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimators = new ValueAnimator[3];
        this.mDelayedLineFadeHandler = new Handler(Looper.getMainLooper());
        this.mShouldAutoHide = true;
        this.mActiveAlpha = 0;
        this.mHideLineRunnable = new Runnable() {
            public final void run() {
                WorkspacePageIndicator.this.animateLineToAlpha(0);
            }
        };
        Resources res = context.getResources();
        this.mLinePaint = new Paint();
        this.mLinePaint.setAlpha(0);
        this.mLauncher = Launcher.getLauncher(context);
        this.mLineHeight = res.getDimensionPixelSize(R.dimen.dynamic_grid_page_indicator_line_height);
        boolean darkText = WallpaperColorInfo.getInstance(context).supportsDarkText();
        this.mActiveAlpha = darkText ? BLACK_ALPHA : WHITE_ALPHA;
        this.mLinePaint.setColor(darkText ? -16777216 : -1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mTotalScroll != 0 && this.mNumPagesFloat != 0.0f) {
            float progress = Utilities.boundToRange(((float) this.mCurrentScroll) / ((float) this.mTotalScroll), 0.0f, 1.0f);
            int availableWidth = getWidth();
            int lineWidth = (int) (((float) availableWidth) / this.mNumPagesFloat);
            int lineLeft = (int) (((float) (availableWidth - lineWidth)) * progress);
            canvas.drawRoundRect((float) lineLeft, (float) ((getHeight() / 2) - (this.mLineHeight / 2)), (float) (lineLeft + lineWidth), (float) ((getHeight() / 2) + (this.mLineHeight / 2)), (float) this.mLineHeight, (float) this.mLineHeight, this.mLinePaint);
        }
    }

    public void setScroll(int currentScroll, int totalScroll) {
        if (getAlpha() != 0.0f) {
            animateLineToAlpha(this.mActiveAlpha);
            this.mCurrentScroll = currentScroll;
            if (this.mTotalScroll == 0) {
                this.mTotalScroll = totalScroll;
            } else if (this.mTotalScroll != totalScroll) {
                animateToTotalScroll(totalScroll);
            } else {
                invalidate();
            }
            if (this.mShouldAutoHide) {
                hideAfterDelay();
            }
        }
    }

    private void hideAfterDelay() {
        this.mDelayedLineFadeHandler.removeCallbacksAndMessages((Object) null);
        this.mDelayedLineFadeHandler.postDelayed(this.mHideLineRunnable, (long) LINE_FADE_DELAY);
    }

    public void setActiveMarker(int activePage) {
    }

    public void setMarkersCount(int numMarkers) {
        if (Float.compare((float) numMarkers, this.mNumPagesFloat) != 0) {
            setupAndRunAnimation(ObjectAnimator.ofFloat(this, NUM_PAGES, new float[]{(float) numMarkers}), 1);
        } else if (this.mAnimators[1] != null) {
            this.mAnimators[1].cancel();
            this.mAnimators[1] = null;
        }
    }

    public void setShouldAutoHide(boolean shouldAutoHide) {
        this.mShouldAutoHide = shouldAutoHide;
        if (shouldAutoHide && this.mLinePaint.getAlpha() > 0) {
            hideAfterDelay();
        } else if (!shouldAutoHide) {
            this.mDelayedLineFadeHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    /* access modifiers changed from: private */
    public void animateLineToAlpha(int alpha) {
        if (alpha != this.mToAlpha) {
            this.mToAlpha = alpha;
            setupAndRunAnimation(ObjectAnimator.ofInt(this, PAINT_ALPHA, new int[]{alpha}), 0);
        }
    }

    private void animateToTotalScroll(int totalScroll) {
        setupAndRunAnimation(ObjectAnimator.ofInt(this, TOTAL_SCROLL, new int[]{totalScroll}), 2);
    }

    private void setupAndRunAnimation(ValueAnimator animator, final int animatorIndex) {
        if (this.mAnimators[animatorIndex] != null) {
            this.mAnimators[animatorIndex].cancel();
        }
        this.mAnimators[animatorIndex] = animator;
        this.mAnimators[animatorIndex].addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                WorkspacePageIndicator.this.mAnimators[animatorIndex] = null;
            }
        });
        this.mAnimators[animatorIndex].setDuration((long) LINE_ANIMATE_DURATION);
        this.mAnimators[animatorIndex].start();
    }

    public void pauseAnimations() {
        for (int i = 0; i < 3; i++) {
            if (this.mAnimators[i] != null) {
                this.mAnimators[i].pause();
            }
        }
    }

    public void skipAnimationsToEnd() {
        for (int i = 0; i < 3; i++) {
            if (this.mAnimators[i] != null) {
                this.mAnimators[i].end();
            }
        }
    }

    public void setInsets(Rect insets) {
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        if (grid.isVerticalBarLayout()) {
            Rect padding = grid.workspacePadding;
            lp.leftMargin = padding.left + grid.workspaceCellPaddingXPx;
            lp.rightMargin = padding.right + grid.workspaceCellPaddingXPx;
            lp.bottomMargin = padding.bottom;
        } else {
            lp.rightMargin = 0;
            lp.leftMargin = 0;
            lp.gravity = 81;
            lp.bottomMargin = grid.hotseatBarSizePx + insets.bottom;
        }
        setLayoutParams(lp);
    }
}
