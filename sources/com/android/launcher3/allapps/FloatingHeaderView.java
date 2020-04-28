package com.android.launcher3.allapps;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.android.launcher3.R;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.anim.PropertySetter;

public class FloatingHeaderView extends LinearLayout implements ValueAnimator.AnimatorUpdateListener {
    private boolean mAllowTouchForwarding;
    /* access modifiers changed from: private */
    public final ValueAnimator mAnimator;
    private final Rect mClip;
    /* access modifiers changed from: private */
    public AllAppsRecyclerView mCurrentRV;
    private boolean mForwardToRecyclerView;
    private boolean mHeaderCollapsed;
    private AllAppsRecyclerView mMainRV;
    private boolean mMainRVActive;
    protected int mMaxTranslation;
    private final RecyclerView.OnScrollListener mOnScrollListener;
    private ViewGroup mParent;
    private int mSnappedScrolledY;
    protected ViewGroup mTabLayout;
    protected boolean mTabsHidden;
    private final Point mTempOffset;
    private int mTranslationY;
    private AllAppsRecyclerView mWorkRV;

    public FloatingHeaderView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatingHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mClip = new Rect(0, 0, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        this.mAnimator = ValueAnimator.ofInt(new int[]{0, 0});
        this.mTempOffset = new Point();
        this.mOnScrollListener = new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            public void onScrolled(RecyclerView rv, int dx, int dy) {
                if (rv == FloatingHeaderView.this.mCurrentRV) {
                    if (FloatingHeaderView.this.mAnimator.isStarted()) {
                        FloatingHeaderView.this.mAnimator.cancel();
                    }
                    FloatingHeaderView.this.moved(-FloatingHeaderView.this.mCurrentRV.getCurrentScrollY());
                    FloatingHeaderView.this.apply();
                }
            }
        };
        this.mMainRVActive = true;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTabLayout = (ViewGroup) findViewById(R.id.tabs);
    }

    public void setup(AllAppsContainerView.AdapterHolder[] mAH, boolean tabsHidden) {
        this.mTabsHidden = tabsHidden;
        this.mTabLayout.setVisibility(tabsHidden ? 8 : 0);
        this.mMainRV = setupRV(this.mMainRV, mAH[0].recyclerView);
        boolean z = true;
        this.mWorkRV = setupRV(this.mWorkRV, mAH[1].recyclerView);
        this.mParent = (ViewGroup) this.mMainRV.getParent();
        if (!this.mMainRVActive && this.mWorkRV != null) {
            z = false;
        }
        setMainActive(z);
        reset(false);
    }

    private AllAppsRecyclerView setupRV(AllAppsRecyclerView old, AllAppsRecyclerView updated) {
        if (!(old == updated || updated == null)) {
            updated.addOnScrollListener(this.mOnScrollListener);
        }
        return updated;
    }

    public void setMainActive(boolean active) {
        this.mCurrentRV = active ? this.mMainRV : this.mWorkRV;
        this.mMainRVActive = active;
    }

    public int getMaxTranslation() {
        if (this.mMaxTranslation == 0 && this.mTabsHidden) {
            return getResources().getDimensionPixelSize(R.dimen.all_apps_search_bar_bottom_padding);
        }
        if (this.mMaxTranslation <= 0 || !this.mTabsHidden) {
            return this.mMaxTranslation;
        }
        return this.mMaxTranslation + getPaddingTop();
    }

    private boolean canSnapAt(int currentScrollY) {
        return Math.abs(currentScrollY) <= this.mMaxTranslation;
    }

    /* access modifiers changed from: private */
    public void moved(int currentScrollY) {
        if (this.mHeaderCollapsed) {
            if (currentScrollY > this.mSnappedScrolledY) {
                this.mHeaderCollapsed = false;
            } else if (canSnapAt(currentScrollY)) {
                this.mSnappedScrolledY = currentScrollY;
            }
            this.mTranslationY = currentScrollY;
        } else if (!this.mHeaderCollapsed) {
            this.mTranslationY = (currentScrollY - this.mSnappedScrolledY) - this.mMaxTranslation;
            if (this.mTranslationY >= 0) {
                this.mTranslationY = 0;
                this.mSnappedScrolledY = currentScrollY - this.mMaxTranslation;
            } else if (this.mTranslationY <= (-this.mMaxTranslation)) {
                this.mHeaderCollapsed = true;
                this.mSnappedScrolledY = -this.mMaxTranslation;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void applyScroll(int uncappedY, int currentY) {
    }

    /* access modifiers changed from: protected */
    public void apply() {
        int uncappedTranslationY = this.mTranslationY;
        this.mTranslationY = Math.max(this.mTranslationY, -this.mMaxTranslation);
        applyScroll(uncappedTranslationY, this.mTranslationY);
        this.mTabLayout.setTranslationY((float) this.mTranslationY);
        this.mClip.top = this.mMaxTranslation + this.mTranslationY;
        this.mMainRV.setClipBounds(this.mClip);
        if (this.mWorkRV != null) {
            this.mWorkRV.setClipBounds(this.mClip);
        }
    }

    public void reset(boolean animate) {
        if (this.mAnimator.isStarted()) {
            this.mAnimator.cancel();
        }
        if (animate) {
            this.mAnimator.setIntValues(new int[]{this.mTranslationY, 0});
            this.mAnimator.addUpdateListener(this);
            this.mAnimator.setDuration(150);
            this.mAnimator.start();
        } else {
            this.mTranslationY = 0;
            apply();
        }
        this.mHeaderCollapsed = false;
        this.mSnappedScrolledY = -this.mMaxTranslation;
        this.mCurrentRV.scrollToTop();
    }

    public boolean isExpanded() {
        return !this.mHeaderCollapsed;
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        this.mTranslationY = ((Integer) animation.getAnimatedValue()).intValue();
        apply();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.mAllowTouchForwarding) {
            this.mForwardToRecyclerView = false;
            return super.onInterceptTouchEvent(ev);
        }
        calcOffset(this.mTempOffset);
        ev.offsetLocation((float) this.mTempOffset.x, (float) this.mTempOffset.y);
        this.mForwardToRecyclerView = this.mCurrentRV.onInterceptTouchEvent(ev);
        ev.offsetLocation((float) (-this.mTempOffset.x), (float) (-this.mTempOffset.y));
        if (this.mForwardToRecyclerView || super.onInterceptTouchEvent(ev)) {
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mForwardToRecyclerView) {
            return super.onTouchEvent(event);
        }
        calcOffset(this.mTempOffset);
        event.offsetLocation((float) this.mTempOffset.x, (float) this.mTempOffset.y);
        try {
            return this.mCurrentRV.onTouchEvent(event);
        } finally {
            event.offsetLocation((float) (-this.mTempOffset.x), (float) (-this.mTempOffset.y));
        }
    }

    private void calcOffset(Point p) {
        p.x = (getLeft() - this.mCurrentRV.getLeft()) - this.mParent.getLeft();
        p.y = (getTop() - this.mCurrentRV.getTop()) - this.mParent.getTop();
    }

    public void setContentVisibility(boolean hasHeader, boolean hasContent, PropertySetter setter, Interpolator fadeInterpolator) {
        setter.setViewAlpha(this, hasContent ? 1.0f : 0.0f, fadeInterpolator);
        allowTouchForwarding(hasContent);
    }

    /* access modifiers changed from: protected */
    public void allowTouchForwarding(boolean allow) {
        this.mAllowTouchForwarding = allow;
    }

    public boolean hasVisibleContent() {
        return false;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
