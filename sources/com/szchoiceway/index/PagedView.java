package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.util.ArrayList;

public abstract class PagedView extends ViewGroup implements ViewGroup.OnHierarchyChangeListener {
    protected static final float ALPHA_QUANTIZE_LEVEL = 1.0E-4f;
    static final int AUTOMATIC_PAGE_SPACING = -1;
    private static final boolean DEBUG = false;
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    protected static final int INVALID_PAGE = -1;
    protected static final int INVALID_POINTER = -1;
    protected static final int MAX_PAGE_SNAP_DURATION = 750;
    private static final int MIN_FLING_VELOCITY = 250;
    private static final int MIN_LENGTH_FOR_FLING = 25;
    private static final int MIN_SNAP_VELOCITY = 1500;
    protected static final float NANOTIME_DIV = 1.0E9f;
    private static final float OVERSCROLL_ACCELERATE_FACTOR = 2.0f;
    private static final float OVERSCROLL_DAMP_FACTOR = 0.14f;
    protected static final int PAGE_SNAP_ANIMATION_DURATION = 550;
    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;
    protected static final int SLOW_PAGE_SNAP_ANIMATION_DURATION = 950;
    private static final String TAG = "PagedView";
    protected static final int TOUCH_STATE_NEXT_PAGE = 3;
    protected static final int TOUCH_STATE_PREV_PAGE = 2;
    protected static final int TOUCH_STATE_REST = 0;
    protected static final int TOUCH_STATE_SCROLLING = 1;
    protected static final int sScrollIndicatorFadeInDuration = 150;
    protected static final int sScrollIndicatorFadeOutDuration = 650;
    protected static final int sScrollIndicatorFlashDuration = 650;
    Runnable hideScrollingIndicatorRunnable;
    protected int mActivePointerId;
    protected boolean mAllowLongPress;
    protected boolean mAllowOverScroll;
    protected int mCellCountX;
    protected int mCellCountY;
    protected boolean mCenterPagesVertically;
    private int[] mChildOffsets;
    private int[] mChildOffsetsWithLayoutScale;
    private int[] mChildRelativeOffsets;
    protected boolean mContentIsRefreshable;
    protected int mCurrentPage;
    private boolean mDeferLoadAssociatedPagesUntilScrollCompletes;
    protected boolean mDeferScrollUpdate;
    protected float mDensity;
    protected ArrayList<Boolean> mDirtyPageContent;
    private float mDownMotionX;
    protected boolean mFadeInAdjacentScreens;
    protected boolean mFirstLayout;
    protected int mFlingThresholdVelocity;
    protected boolean mForceDrawAllChildrenNextFrame;
    protected boolean mForceScreenScrolled;
    private boolean mHasScrollIndicator;
    protected boolean mIsDataReady;
    protected boolean mIsPageMoving;
    protected float mLastMotionX;
    protected float mLastMotionXRemainder;
    protected float mLastMotionY;
    private int mLastScreenCenter;
    protected float mLayoutScale;
    protected View.OnLongClickListener mLongClickListener;
    protected int mMaxScrollX;
    private int mMaximumVelocity;
    protected int mMinFlingVelocity;
    protected int mMinSnapVelocity;
    private int mMinimumWidth;
    protected int mNextPage;
    protected int mOverScrollX;
    protected int mPageLayoutHeightGap;
    protected int mPageLayoutPaddingBottom;
    protected int mPageLayoutPaddingLeft;
    protected int mPageLayoutPaddingRight;
    protected int mPageLayoutPaddingTop;
    protected int mPageLayoutWidthGap;
    protected int mPageSpacing;
    private PageSwitchListener mPageSwitchListener;
    private int mPagingTouchSlop;
    /* access modifiers changed from: private */
    public View mScrollIndicator;
    private ValueAnimator mScrollIndicatorAnimator;
    private int mScrollIndicatorPaddingLeft;
    private int mScrollIndicatorPaddingRight;
    protected Scroller mScroller;
    private boolean mScrollingPaused;
    private boolean mShouldShowScrollIndicator;
    private boolean mShouldShowScrollIndicatorImmediately;
    protected float mSmoothingTime;
    protected int[] mTempVisiblePagesRange;
    protected float mTotalMotionX;
    protected int mTouchSlop;
    protected int mTouchState;
    protected float mTouchX;
    protected int mUnboundedScrollX;
    protected boolean mUsePagingTouchSlop;
    private VelocityTracker mVelocityTracker;

    public interface PageSwitchListener {
        void onPageSwitch(View view, int i);
    }

    public abstract void syncPageItems(int i, boolean z);

    public abstract void syncPages();

    public PagedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFirstLayout = true;
        this.mNextPage = -1;
        this.mLastScreenCenter = -1;
        this.mTouchState = 0;
        this.mForceScreenScrolled = false;
        this.mAllowLongPress = true;
        this.mCellCountX = 0;
        this.mCellCountY = 0;
        this.mAllowOverScroll = true;
        this.mTempVisiblePagesRange = new int[2];
        this.mLayoutScale = 1.0f;
        this.mActivePointerId = -1;
        this.mContentIsRefreshable = true;
        this.mFadeInAdjacentScreens = true;
        this.mUsePagingTouchSlop = true;
        this.mDeferScrollUpdate = false;
        this.mIsPageMoving = false;
        this.mIsDataReady = false;
        this.mHasScrollIndicator = true;
        this.mShouldShowScrollIndicator = false;
        this.mShouldShowScrollIndicatorImmediately = false;
        this.mScrollingPaused = false;
        this.hideScrollingIndicatorRunnable = new Runnable() {
            public void run() {
                PagedView.this.hideScrollingIndicator(false);
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagedView, defStyle, 0);
        setPageSpacing(a.getDimensionPixelSize(6, 0));
        this.mPageLayoutPaddingTop = a.getDimensionPixelSize(2, 0);
        this.mPageLayoutPaddingBottom = a.getDimensionPixelSize(3, 0);
        this.mPageLayoutPaddingLeft = a.getDimensionPixelSize(4, 0);
        this.mPageLayoutPaddingRight = a.getDimensionPixelSize(5, 0);
        this.mPageLayoutWidthGap = a.getDimensionPixelSize(0, 0);
        this.mPageLayoutHeightGap = a.getDimensionPixelSize(1, 0);
        this.mScrollIndicatorPaddingLeft = a.getDimensionPixelSize(7, 0);
        this.mScrollIndicatorPaddingRight = a.getDimensionPixelSize(8, 0);
        a.recycle();
        setHapticFeedbackEnabled(false);
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mDirtyPageContent = new ArrayList<>();
        this.mDirtyPageContent.ensureCapacity(32);
        this.mScroller = new Scroller(getContext(), new ScrollInterpolator());
        this.mCurrentPage = 0;
        this.mCenterPagesVertically = true;
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mPagingTouchSlop = configuration.getScaledPagingTouchSlop();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mDensity = getResources().getDisplayMetrics().density;
        this.mFlingThresholdVelocity = (int) (500.0f * this.mDensity);
        this.mMinFlingVelocity = (int) (250.0f * this.mDensity);
        this.mMinSnapVelocity = (int) (1500.0f * this.mDensity);
        setOnHierarchyChangeListener(this);
    }

    public void setPageSwitchListener(PageSwitchListener pageSwitchListener) {
        this.mPageSwitchListener = pageSwitchListener;
        if (this.mPageSwitchListener != null) {
            this.mPageSwitchListener.onPageSwitch(getPageAt(this.mCurrentPage), this.mCurrentPage);
        }
    }

    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: protected */
    public void setDataIsReady() {
        this.mIsDataReady = true;
    }

    /* access modifiers changed from: protected */
    public boolean isDataReady() {
        return this.mIsDataReady;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentPage() {
        return this.mCurrentPage;
    }

    /* access modifiers changed from: package-private */
    public int getNextPage() {
        return this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage;
    }

    /* access modifiers changed from: package-private */
    public int getPageCount() {
        return getChildCount();
    }

    /* access modifiers changed from: package-private */
    public View getPageAt(int index) {
        return getChildAt(index);
    }

    /* access modifiers changed from: protected */
    public int indexToPage(int index) {
        return index;
    }

    /* access modifiers changed from: protected */
    public void updateCurrentPageScroll() {
        int newX = 0;
        if (this.mCurrentPage >= 0 && this.mCurrentPage < getPageCount()) {
            newX = getChildOffset(this.mCurrentPage) - getRelativeChildOffset(this.mCurrentPage);
        }
        scrollTo(newX, 0);
        this.mScroller.setFinalX(newX);
        this.mScroller.forceFinished(true);
    }

    /* access modifiers changed from: package-private */
    public void pauseScrolling() {
        this.mScroller.forceFinished(true);
        cancelScrollingIndicatorAnimations();
        this.mScrollingPaused = true;
    }

    /* access modifiers changed from: package-private */
    public void resumeScrolling() {
        this.mScrollingPaused = false;
    }

    /* access modifiers changed from: package-private */
    public void setCurrentPage(int currentPage) {
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        if (getChildCount() != 0) {
            this.mCurrentPage = Math.max(0, Math.min(currentPage, getPageCount() - 1));
            updateCurrentPageScroll();
            updateScrollingIndicator();
            notifyPageSwitchListener();
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener() {
        if (this.mPageSwitchListener != null) {
            this.mPageSwitchListener.onPageSwitch(getPageAt(this.mCurrentPage), this.mCurrentPage);
        }
    }

    /* access modifiers changed from: protected */
    public void pageBeginMoving() {
        if (!this.mIsPageMoving) {
            this.mIsPageMoving = true;
            onPageBeginMoving();
        }
    }

    /* access modifiers changed from: protected */
    public void pageEndMoving() {
        if (this.mIsPageMoving) {
            this.mIsPageMoving = false;
            onPageEndMoving();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isPageMoving() {
        return this.mIsPageMoving;
    }

    /* access modifiers changed from: protected */
    public void onPageBeginMoving() {
    }

    /* access modifiers changed from: protected */
    public void onPageEndMoving() {
    }

    public void setOnLongClickListener(View.OnLongClickListener l) {
        this.mLongClickListener = l;
        int count = getPageCount();
        for (int i = 0; i < count; i++) {
            getPageAt(i).setOnLongClickListener(l);
        }
    }

    public void scrollBy(int x, int y) {
        scrollTo(this.mUnboundedScrollX + x, getScrollY() + y);
    }

    public void scrollTo(int x, int y) {
        boolean isXAfterLastPage = true;
        boolean isRtl = isLayoutRtl();
        this.mUnboundedScrollX = x;
        boolean isXBeforeFirstPage = isRtl ? x > this.mMaxScrollX : x < 0;
        if (isRtl) {
            if (x >= 0) {
                isXAfterLastPage = false;
            }
        } else if (x <= this.mMaxScrollX) {
            isXAfterLastPage = false;
        }
        if (isXBeforeFirstPage) {
            super.scrollTo(0, y);
            if (this.mAllowOverScroll) {
                if (isRtl) {
                    overScroll((float) (x - this.mMaxScrollX));
                } else {
                    overScroll((float) x);
                }
            }
        } else if (isXAfterLastPage) {
            super.scrollTo(this.mMaxScrollX, y);
            if (this.mAllowOverScroll) {
                if (isRtl) {
                    overScroll((float) x);
                } else {
                    overScroll((float) (x - this.mMaxScrollX));
                }
            }
        } else {
            this.mOverScrollX = x;
            super.scrollTo(x, y);
        }
        this.mTouchX = (float) x;
        this.mSmoothingTime = ((float) System.nanoTime()) / NANOTIME_DIV;
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper() {
        if (this.mScroller.computeScrollOffset()) {
            if (!(getScrollX() == this.mScroller.getCurrX() && getScrollY() == this.mScroller.getCurrY() && this.mOverScrollX == this.mScroller.getCurrX())) {
                scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            }
            invalidate();
            return true;
        } else if (this.mNextPage == -1) {
            return false;
        } else {
            this.mCurrentPage = Math.max(0, Math.min(this.mNextPage, getPageCount() - 1));
            this.mNextPage = -1;
            notifyPageSwitchListener();
            if (this.mDeferLoadAssociatedPagesUntilScrollCompletes) {
                loadAssociatedPages(this.mCurrentPage);
                this.mDeferLoadAssociatedPagesUntilScrollCompletes = false;
            }
            if (this.mTouchState == 0) {
                pageEndMoving();
            }
            if (!((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
                return true;
            }
            AccessibilityEvent ev = AccessibilityEvent.obtain(4096);
            ev.getText().add(getCurrentPageDescription());
            sendAccessibilityEventUnchecked(ev);
            return true;
        }
    }

    public void computeScroll() {
        computeScrollHelper();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childWidthMode;
        int childHeightMode;
        if (!this.mIsDataReady) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 1073741824) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        } else if (widthSize <= 0 || heightSize <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int maxChildHeight = 0;
            int verticalPadding = getPaddingTop() + getPaddingBottom();
            int horizontalPadding = getPaddingLeft() + getPaddingRight();
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getPageAt(i);
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp.width == -2) {
                    childWidthMode = ExploreByTouchHelper.INVALID_ID;
                } else {
                    childWidthMode = 1073741824;
                }
                if (lp.height == -2) {
                    childHeightMode = ExploreByTouchHelper.INVALID_ID;
                } else {
                    childHeightMode = 1073741824;
                }
                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize - horizontalPadding, childWidthMode), View.MeasureSpec.makeMeasureSpec(heightSize - verticalPadding, childHeightMode));
                maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
            }
            if (heightMode == Integer.MIN_VALUE) {
                heightSize = maxChildHeight + verticalPadding;
            }
            setMeasuredDimension(widthSize, heightSize);
            invalidateCachedOffsets();
            if (childCount > 0 && this.mPageSpacing == -1) {
                int offset = getRelativeChildOffset(0);
                setPageSpacing(Math.max(offset, (widthSize - offset) - getChildAt(0).getMeasuredWidth()));
            }
            updateScrollingIndicatorPosition();
            if (childCount > 0) {
                int index = isLayoutRtl() ? 0 : childCount - 1;
                this.mMaxScrollX = getChildOffset(index) - getRelativeChildOffset(index);
                return;
            }
            this.mMaxScrollX = 0;
        }
    }

    /* access modifiers changed from: protected */
    public void scrollToNewPageWithoutMovingPages(int newCurrentPage) {
        int delta = (getChildOffset(newCurrentPage) - getRelativeChildOffset(newCurrentPage)) - getScrollX();
        int pageCount = getChildCount();
        for (int i = 0; i < pageCount; i++) {
            View page = getPageAt(i);
            page.setX(page.getX() + ((float) delta));
        }
        setCurrentPage(newCurrentPage);
    }

    public void setLayoutScale(float childrenScale) {
        this.mLayoutScale = childrenScale;
        invalidateCachedOffsets();
        int childCount = getChildCount();
        float[] childrenX = new float[childCount];
        float[] childrenY = new float[childCount];
        for (int i = 0; i < childCount; i++) {
            View child = getPageAt(i);
            childrenX[i] = child.getX();
            childrenY[i] = child.getY();
        }
        int widthSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        requestLayout();
        measure(widthSpec, heightSpec);
        layout(getLeft(), getTop(), getRight(), getBottom());
        for (int i2 = 0; i2 < childCount; i2++) {
            View child2 = getPageAt(i2);
            child2.setX(childrenX[i2]);
            child2.setY(childrenY[i2]);
        }
        scrollToNewPageWithoutMovingPages(this.mCurrentPage);
    }

    public void setPageSpacing(int pageSpacing) {
        this.mPageSpacing = pageSpacing;
        invalidateCachedOffsets();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int endIndex;
        if (this.mIsDataReady) {
            int verticalPadding = getPaddingTop() + getPaddingBottom();
            int childCount = getChildCount();
            boolean isRtl = isLayoutRtl();
            int startIndex = isRtl ? childCount - 1 : 0;
            if (isRtl) {
                endIndex = -1;
            } else {
                endIndex = childCount;
            }
            int delta = isRtl ? -1 : 1;
            int childLeft = getRelativeChildOffset(startIndex);
            for (int i = startIndex; i != endIndex; i += delta) {
                View child = getPageAt(i);
                if (child.getVisibility() != 8) {
                    int childWidth = getScaledMeasuredWidth(child);
                    int childHeight = child.getMeasuredHeight();
                    int childTop = getPaddingTop();
                    if (this.mCenterPagesVertically) {
                        childTop += ((getMeasuredHeight() - verticalPadding) - childHeight) / 2;
                    }
                    child.layout(childLeft, childTop, child.getMeasuredWidth() + childLeft, childTop + childHeight);
                    childLeft += this.mPageSpacing + childWidth;
                }
            }
            if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < getChildCount()) {
                setHorizontalScrollBarEnabled(false);
                updateCurrentPageScroll();
                setHorizontalScrollBarEnabled(true);
                this.mFirstLayout = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void screenScrolled(int screenCenter) {
        if (isScrollingIndicatorEnabled()) {
            updateScrollingIndicator();
        }
        boolean isInOverscroll = this.mOverScrollX < 0 || this.mOverScrollX > this.mMaxScrollX;
        if (this.mFadeInAdjacentScreens && !isInOverscroll) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null) {
                    child.setAlpha(1.0f - Math.abs(getScrollProgress(screenCenter, child, i)));
                }
            }
            invalidate();
        }
    }

    public void onChildViewAdded(View parent, View child) {
        this.mForceScreenScrolled = true;
        invalidate();
        invalidateCachedOffsets();
    }

    public void onChildViewRemoved(View parent, View child) {
    }

    /* access modifiers changed from: protected */
    public void invalidateCachedOffsets() {
        int count = getChildCount();
        if (count == 0) {
            this.mChildOffsets = null;
            this.mChildRelativeOffsets = null;
            this.mChildOffsetsWithLayoutScale = null;
            return;
        }
        this.mChildOffsets = new int[count];
        this.mChildRelativeOffsets = new int[count];
        this.mChildOffsetsWithLayoutScale = new int[count];
        for (int i = 0; i < count; i++) {
            this.mChildOffsets[i] = -1;
            this.mChildRelativeOffsets[i] = -1;
            this.mChildOffsetsWithLayoutScale[i] = -1;
        }
    }

    /* access modifiers changed from: protected */
    public int getChildOffset(int index) {
        int startIndex;
        int endIndex;
        int delta = -1;
        boolean isRtl = isLayoutRtl();
        int[] childOffsets = Float.compare(this.mLayoutScale, 1.0f) == 0 ? this.mChildOffsets : this.mChildOffsetsWithLayoutScale;
        if (childOffsets != null && childOffsets[index] != -1) {
            return childOffsets[index];
        }
        if (getChildCount() == 0) {
            return 0;
        }
        if (isRtl) {
            startIndex = getChildCount() - 1;
        } else {
            startIndex = 0;
        }
        if (isRtl) {
            endIndex = index;
        } else {
            endIndex = index;
        }
        if (!isRtl) {
            delta = 1;
        }
        int offset = getRelativeChildOffset(startIndex);
        for (int i = startIndex; i != endIndex; i += delta) {
            offset += getScaledMeasuredWidth(getPageAt(i)) + this.mPageSpacing;
        }
        if (childOffsets == null) {
            return offset;
        }
        childOffsets[index] = offset;
        return offset;
    }

    /* access modifiers changed from: protected */
    public int getRelativeChildOffset(int index) {
        if (this.mChildRelativeOffsets != null && this.mChildRelativeOffsets.length == 1 && index == 1) {
            index = 0;
        }
        if (this.mChildRelativeOffsets == null || this.mChildRelativeOffsets[index] == -1) {
            int offset = getPaddingLeft() + (((getMeasuredWidth() - (getPaddingLeft() + getPaddingRight())) - getChildWidth(index)) / 2);
            if (this.mChildRelativeOffsets == null) {
                return offset;
            }
            this.mChildRelativeOffsets[index] = offset;
            return offset;
        }
        Log.i(TAG, "getRelativeChildOffset: mChildRelativeOffsets.length = " + this.mChildRelativeOffsets.length);
        return this.mChildRelativeOffsets[index];
    }

    /* access modifiers changed from: protected */
    public int getScaledMeasuredWidth(View child) {
        int maxWidth;
        int measuredWidth = -1;
        if (child != null) {
            measuredWidth = child.getMeasuredWidth();
        }
        int minWidth = this.mMinimumWidth;
        if (minWidth > measuredWidth) {
            maxWidth = minWidth;
        } else {
            maxWidth = measuredWidth;
        }
        return (int) ((((float) maxWidth) * this.mLayoutScale) + 0.5f);
    }

    /* access modifiers changed from: protected */
    public void getVisiblePages(int[] range) {
        int leftScreen;
        int delta = -1;
        boolean isRtl = isLayoutRtl();
        int pageCount = getChildCount();
        if (pageCount > 0) {
            int screenWidth = getMeasuredWidth();
            if (isRtl) {
                leftScreen = pageCount - 1;
            } else {
                leftScreen = 0;
            }
            int endIndex = isRtl ? 0 : pageCount - 1;
            if (!isRtl) {
                delta = 1;
            }
            View currPage = getPageAt(leftScreen);
            while (leftScreen != endIndex && (currPage.getX() + ((float) currPage.getWidth())) - ((float) currPage.getPaddingRight()) < ((float) getScrollX())) {
                leftScreen += delta;
                currPage = getPageAt(leftScreen);
            }
            int rightScreen = leftScreen;
            View currPage2 = getPageAt(rightScreen + delta);
            while (rightScreen != endIndex && currPage2.getX() - ((float) currPage2.getPaddingLeft()) < ((float) (getScrollX() + screenWidth))) {
                rightScreen += delta;
                currPage2 = getPageAt(rightScreen + delta);
            }
            range[0] = Math.min(leftScreen, rightScreen);
            range[1] = Math.max(leftScreen, rightScreen);
            return;
        }
        range[0] = -1;
        range[1] = -1;
    }

    /* access modifiers changed from: protected */
    public boolean shouldDrawChild(View child) {
        return child.getAlpha() > 0.0f;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int screenCenter = this.mOverScrollX + (getMeasuredWidth() / 2);
        if (screenCenter != this.mLastScreenCenter || this.mForceScreenScrolled) {
            this.mForceScreenScrolled = false;
            screenScrolled(screenCenter);
            this.mLastScreenCenter = screenCenter;
        }
        if (getChildCount() > 0) {
            getVisiblePages(this.mTempVisiblePagesRange);
            int leftScreen = this.mTempVisiblePagesRange[0];
            int rightScreen = this.mTempVisiblePagesRange[1];
            if (leftScreen != -1 && rightScreen != -1) {
                long drawingTime = getDrawingTime();
                canvas.save();
                canvas.clipRect(getScrollX(), getScrollY(), (getScrollX() + getRight()) - getLeft(), (getScrollY() + getBottom()) - getTop());
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    View v = getPageAt(i);
                    if (this.mForceDrawAllChildrenNextFrame || (leftScreen <= i && i <= rightScreen && shouldDrawChild(v))) {
                        drawChild(canvas, v, drawingTime);
                    }
                }
                this.mForceDrawAllChildrenNextFrame = false;
                canvas.restore();
            }
        }
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int page = indexToPage(indexOfChild(child));
        if (page == this.mCurrentPage && this.mScroller.isFinished()) {
            return false;
        }
        snapToPage(page);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int focusablePage;
        if (this.mNextPage != -1) {
            focusablePage = this.mNextPage;
        } else {
            focusablePage = this.mCurrentPage;
        }
        View v = getPageAt(focusablePage);
        if (v != null) {
            return v.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == 17) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (direction == 66 && getCurrentPage() < getPageCount() - 1) {
            snapToPage(getCurrentPage() + 1);
            return true;
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (this.mCurrentPage >= 0 && this.mCurrentPage < getPageCount()) {
            getPageAt(this.mCurrentPage).addFocusables(views, direction, focusableMode);
        }
        if (direction == 17) {
            if (this.mCurrentPage > 0) {
                getPageAt(this.mCurrentPage - 1).addFocusables(views, direction, focusableMode);
            }
        } else if (direction == 66 && this.mCurrentPage < getPageCount() - 1) {
            getPageAt(this.mCurrentPage + 1).addFocusables(views, direction, focusableMode);
        }
    }

    public void focusableViewAvailable(View focused) {
        View current = getPageAt(this.mCurrentPage);
        View v = focused;
        while (v != current) {
            if (v != this && (v.getParent() instanceof View)) {
                v = (View) v.getParent();
            } else {
                return;
            }
        }
        super.focusableViewAvailable(focused);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            getPageAt(this.mCurrentPage).cancelLongPress();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /* access modifiers changed from: protected */
    public boolean hitsPreviousPage(float x, float y) {
        if (isLayoutRtl()) {
            if (x > ((float) ((getMeasuredWidth() - getRelativeChildOffset(this.mCurrentPage)) + this.mPageSpacing))) {
                return true;
            }
            return false;
        } else if (x >= ((float) (getRelativeChildOffset(this.mCurrentPage) - this.mPageSpacing))) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean hitsNextPage(float x, float y) {
        if (isLayoutRtl()) {
            if (x < ((float) (getRelativeChildOffset(this.mCurrentPage) - this.mPageSpacing))) {
                return true;
            }
            return false;
        } else if (x <= ((float) ((getMeasuredWidth() - getRelativeChildOffset(this.mCurrentPage)) + this.mPageSpacing))) {
            return false;
        } else {
            return true;
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r12) {
        /*
            r11 = this;
            r10 = -1
            r8 = 0
            r9 = 2
            r5 = 1
            r6 = 0
            r11.acquireVelocityTrackerAndAddMovement(r12)
            int r7 = r11.getChildCount()
            if (r7 > 0) goto L_0x0013
            boolean r5 = super.onInterceptTouchEvent(r12)
        L_0x0012:
            return r5
        L_0x0013:
            int r0 = r12.getAction()
            if (r0 != r9) goto L_0x001d
            int r7 = r11.mTouchState
            if (r7 == r5) goto L_0x0012
        L_0x001d:
            r7 = r0 & 255(0xff, float:3.57E-43)
            switch(r7) {
                case 0: goto L_0x0030;
                case 1: goto L_0x0098;
                case 2: goto L_0x0028;
                case 3: goto L_0x0098;
                case 4: goto L_0x0022;
                case 5: goto L_0x0022;
                case 6: goto L_0x00a2;
                default: goto L_0x0022;
            }
        L_0x0022:
            int r7 = r11.mTouchState
            if (r7 != 0) goto L_0x0012
            r5 = r6
            goto L_0x0012
        L_0x0028:
            int r7 = r11.mActivePointerId
            if (r7 == r10) goto L_0x0030
            r11.determineScrollingStart(r12)
            goto L_0x0022
        L_0x0030:
            float r2 = r12.getX()
            float r4 = r12.getY()
            r11.mDownMotionX = r2
            r11.mLastMotionX = r2
            r11.mLastMotionY = r4
            r11.mLastMotionXRemainder = r8
            r11.mTotalMotionX = r8
            int r7 = r12.getPointerId(r6)
            r11.mActivePointerId = r7
            r11.mAllowLongPress = r5
            android.widget.Scroller r7 = r11.mScroller
            int r7 = r7.getFinalX()
            android.widget.Scroller r8 = r11.mScroller
            int r8 = r8.getCurrX()
            int r7 = r7 - r8
            int r3 = java.lang.Math.abs(r7)
            android.widget.Scroller r7 = r11.mScroller
            boolean r7 = r7.isFinished()
            if (r7 != 0) goto L_0x0067
            int r7 = r11.mTouchSlop
            if (r3 >= r7) goto L_0x0089
        L_0x0067:
            r1 = r5
        L_0x0068:
            if (r1 == 0) goto L_0x008b
            r11.mTouchState = r6
            android.widget.Scroller r7 = r11.mScroller
            r7.abortAnimation()
        L_0x0071:
            int r7 = r11.mTouchState
            if (r7 == r9) goto L_0x0022
            int r7 = r11.mTouchState
            r8 = 3
            if (r7 == r8) goto L_0x0022
            int r7 = r11.getChildCount()
            if (r7 <= 0) goto L_0x0022
            boolean r7 = r11.hitsPreviousPage(r2, r4)
            if (r7 == 0) goto L_0x008e
            r11.mTouchState = r9
            goto L_0x0022
        L_0x0089:
            r1 = r6
            goto L_0x0068
        L_0x008b:
            r11.mTouchState = r5
            goto L_0x0071
        L_0x008e:
            boolean r7 = r11.hitsNextPage(r2, r4)
            if (r7 == 0) goto L_0x0022
            r7 = 3
            r11.mTouchState = r7
            goto L_0x0022
        L_0x0098:
            r11.mTouchState = r6
            r11.mAllowLongPress = r6
            r11.mActivePointerId = r10
            r11.releaseVelocityTracker()
            goto L_0x0022
        L_0x00a2:
            r11.onSecondaryPointerUp(r12)
            r11.releaseVelocityTracker()
            goto L_0x0022
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.PagedView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev) {
        determineScrollingStart(ev, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        boolean xPaged;
        boolean xMoved;
        boolean yMoved = false;
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (pointerIndex != -1) {
            float x = ev.getX(pointerIndex);
            float y = ev.getY(pointerIndex);
            int xDiff = (int) Math.abs(x - this.mLastMotionX);
            int yDiff = (int) Math.abs(y - this.mLastMotionY);
            int touchSlop = Math.round(((float) this.mTouchSlop) * touchSlopScale);
            if (xDiff > this.mPagingTouchSlop) {
                xPaged = true;
            } else {
                xPaged = false;
            }
            if (xDiff > touchSlop) {
                xMoved = true;
            } else {
                xMoved = false;
            }
            if (yDiff > touchSlop) {
                yMoved = true;
            }
            if (xMoved || xPaged || yMoved) {
                if (!this.mUsePagingTouchSlop ? xMoved : xPaged) {
                    this.mTouchState = 1;
                    this.mTotalMotionX += Math.abs(this.mLastMotionX - x);
                    this.mLastMotionX = x;
                    this.mLastMotionXRemainder = 0.0f;
                    this.mTouchX = (float) getScrollX();
                    this.mSmoothingTime = ((float) System.nanoTime()) / NANOTIME_DIV;
                    pageBeginMoving();
                }
                cancelCurrentPageLongPress();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCurrentPageLongPress() {
        if (this.mAllowLongPress) {
            this.mAllowLongPress = false;
            View currentPage = getPageAt(this.mCurrentPage);
            if (currentPage != null) {
                currentPage.cancelLongPress();
            }
        }
    }

    /* access modifiers changed from: protected */
    public float getScrollProgress(int screenCenter, View v, int page) {
        return Math.max(Math.min(((float) (screenCenter - ((getChildOffset(page) - getRelativeChildOffset(page)) + (getMeasuredWidth() / 2)))) / (((float) (getScaledMeasuredWidth(v) + this.mPageSpacing)) * 1.0f), 1.0f), -1.0f);
    }

    private float overScrollInfluenceCurve(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    /* access modifiers changed from: protected */
    public void acceleratedOverScroll(float amount) {
        int screenSize = getMeasuredWidth();
        float f = OVERSCROLL_ACCELERATE_FACTOR * (amount / ((float) screenSize));
        if (f != 0.0f) {
            if (Math.abs(f) >= 1.0f) {
                f /= Math.abs(f);
            }
            int overScrollAmount = Math.round(((float) screenSize) * f);
            if (amount < 0.0f) {
                this.mOverScrollX = overScrollAmount;
                super.scrollTo(0, getScrollY());
            } else {
                this.mOverScrollX = this.mMaxScrollX + overScrollAmount;
                super.scrollTo(this.mMaxScrollX, getScrollY());
            }
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void dampedOverScroll(float amount) {
        int screenSize = getMeasuredWidth();
        float f = amount / ((float) screenSize);
        if (f != 0.0f) {
            float f2 = (f / Math.abs(f)) * overScrollInfluenceCurve(Math.abs(f));
            if (Math.abs(f2) >= 1.0f) {
                f2 /= Math.abs(f2);
            }
            int overScrollAmount = Math.round(OVERSCROLL_DAMP_FACTOR * f2 * ((float) screenSize));
            if (amount < 0.0f) {
                this.mOverScrollX = overScrollAmount;
                super.scrollTo(0, getScrollY());
            } else {
                this.mOverScrollX = this.mMaxScrollX + overScrollAmount;
                super.scrollTo(this.mMaxScrollX, getScrollY());
            }
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void overScroll(float amount) {
        dampedOverScroll(amount);
    }

    /* access modifiers changed from: protected */
    public float maxOverScroll() {
        return OVERSCROLL_DAMP_FACTOR * (1.0f / Math.abs(1.0f)) * overScrollInfluenceCurve(Math.abs(1.0f));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() <= 0) {
            return super.onTouchEvent(ev);
        }
        acquireVelocityTrackerAndAddMovement(ev);
        switch (ev.getAction() & 255) {
            case 0:
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                float x = ev.getX();
                this.mLastMotionX = x;
                this.mDownMotionX = x;
                this.mLastMotionXRemainder = 0.0f;
                this.mTotalMotionX = 0.0f;
                this.mActivePointerId = ev.getPointerId(0);
                if (this.mTouchState == 1) {
                    pageBeginMoving();
                    break;
                }
                break;
            case 1:
                if (this.mTouchState == 1) {
                    int activePointerId = this.mActivePointerId;
                    float x2 = ev.getX(ev.findPointerIndex(activePointerId));
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity(activePointerId);
                    int deltaX = (int) (x2 - this.mDownMotionX);
                    int pageWidth = getScaledMeasuredWidth(getPageAt(this.mCurrentPage));
                    boolean isSignificantMove = ((float) Math.abs(deltaX)) > ((float) pageWidth) * SIGNIFICANT_MOVE_THRESHOLD;
                    this.mTotalMotionX += Math.abs((this.mLastMotionX + this.mLastMotionXRemainder) - x2);
                    boolean isFling = this.mTotalMotionX > 25.0f && Math.abs(velocityX) > this.mFlingThresholdVelocity;
                    boolean returnToOriginalPage = false;
                    if (((float) Math.abs(deltaX)) > ((float) pageWidth) * RETURN_TO_ORIGINAL_PAGE_THRESHOLD && Math.signum((float) velocityX) != Math.signum((float) deltaX) && isFling) {
                        returnToOriginalPage = true;
                    }
                    boolean isRtl = isLayoutRtl();
                    boolean isDeltaXLeft = isRtl ? deltaX > 0 : deltaX < 0;
                    boolean isVelocityXLeft = isRtl ? velocityX > 0 : velocityX < 0;
                    if (((isSignificantMove && !isDeltaXLeft && !isFling) || (isFling && !isVelocityXLeft)) && this.mCurrentPage > 0) {
                        snapToPageWithVelocity(returnToOriginalPage ? this.mCurrentPage : this.mCurrentPage - 1, velocityX);
                    } else if (((!isSignificantMove || !isDeltaXLeft || isFling) && (!isFling || !isVelocityXLeft)) || this.mCurrentPage >= getChildCount() - 1) {
                        snapToDestination();
                    } else {
                        snapToPageWithVelocity(returnToOriginalPage ? this.mCurrentPage : this.mCurrentPage + 1, velocityX);
                    }
                } else if (this.mTouchState == 2) {
                    int nextPage = Math.max(0, this.mCurrentPage - 1);
                    if (nextPage != this.mCurrentPage) {
                        snapToPage(nextPage);
                    } else {
                        snapToDestination();
                    }
                } else if (this.mTouchState == 3) {
                    int nextPage2 = Math.min(getChildCount() - 1, this.mCurrentPage + 1);
                    if (nextPage2 != this.mCurrentPage) {
                        snapToPage(nextPage2);
                    } else {
                        snapToDestination();
                    }
                } else {
                    onUnhandledTap(ev);
                }
                this.mTouchState = 0;
                this.mActivePointerId = -1;
                releaseVelocityTracker();
                break;
            case 2:
                if (this.mTouchState != 1) {
                    determineScrollingStart(ev);
                    break;
                } else {
                    float x3 = ev.getX(ev.findPointerIndex(this.mActivePointerId));
                    float deltaX2 = (this.mLastMotionX + this.mLastMotionXRemainder) - x3;
                    this.mTotalMotionX += Math.abs(deltaX2);
                    if (Math.abs(deltaX2) < 1.0f) {
                        awakenScrollBars();
                        break;
                    } else {
                        this.mTouchX += deltaX2;
                        this.mSmoothingTime = ((float) System.nanoTime()) / NANOTIME_DIV;
                        if (!this.mDeferScrollUpdate) {
                            scrollBy((int) deltaX2, 0);
                        } else {
                            invalidate();
                        }
                        this.mLastMotionX = x3;
                        this.mLastMotionXRemainder = deltaX2 - ((float) ((int) deltaX2));
                        break;
                    }
                }
            case 3:
                if (this.mTouchState == 1) {
                    snapToDestination();
                }
                this.mTouchState = 0;
                this.mActivePointerId = -1;
                releaseVelocityTracker();
                break;
            case 6:
                onSecondaryPointerUp(ev);
                break;
        }
        return true;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        float vscroll;
        float hscroll;
        boolean isForwardScroll = false;
        if ((event.getSource() & 2) != 0) {
            switch (event.getAction()) {
                case 8:
                    if ((event.getMetaState() & 1) != 0) {
                        vscroll = 0.0f;
                        hscroll = event.getAxisValue(9);
                    } else {
                        vscroll = -event.getAxisValue(9);
                        hscroll = event.getAxisValue(10);
                    }
                    if (!(hscroll == 0.0f && vscroll == 0.0f)) {
                        if (isLayoutRtl()) {
                            if (hscroll < 0.0f || vscroll < 0.0f) {
                                isForwardScroll = true;
                            }
                        } else if (hscroll > 0.0f || vscroll > 0.0f) {
                            isForwardScroll = true;
                        }
                        if (isForwardScroll) {
                            scrollRight();
                            return true;
                        }
                        scrollLeft();
                        return true;
                    }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            float x = ev.getX(newPointerIndex);
            this.mDownMotionX = x;
            this.mLastMotionX = x;
            this.mLastMotionY = ev.getY(newPointerIndex);
            this.mLastMotionXRemainder = 0.0f;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onUnhandledTap(MotionEvent ev) {
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        int page = indexToPage(indexOfChild(child));
        if (page >= 0 && page != getCurrentPage() && !isInTouchMode()) {
            snapToPage(page);
        }
    }

    /* access modifiers changed from: protected */
    public int getChildIndexForRelativeOffset(int relativeOffset) {
        int endIndex;
        boolean isRtl = isLayoutRtl();
        int childCount = getChildCount();
        int startIndex = isRtl ? childCount - 1 : 0;
        if (isRtl) {
            endIndex = -1;
        } else {
            endIndex = childCount;
        }
        int delta = isRtl ? -1 : 1;
        for (int i = startIndex; i != endIndex; i += delta) {
            int left = getRelativeChildOffset(i);
            int right = left + getScaledMeasuredWidth(getPageAt(i));
            if (left <= relativeOffset && relativeOffset <= right) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getChildWidth(int index) {
        int measuredWidth = getPageAt(index).getMeasuredWidth();
        int minWidth = this.mMinimumWidth;
        return minWidth > measuredWidth ? minWidth : measuredWidth;
    }

    /* access modifiers changed from: package-private */
    public int getPageNearestToCenterOfScreen() {
        int minDistanceFromScreenCenter = Integer.MAX_VALUE;
        int minDistanceFromScreenCenterIndex = -1;
        int screenCenter = getScrollX() + (getMeasuredWidth() / 2);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int distanceFromScreenCenter = Math.abs((getChildOffset(i) + (getScaledMeasuredWidth(getPageAt(i)) / 2)) - screenCenter);
            if (distanceFromScreenCenter < minDistanceFromScreenCenter) {
                minDistanceFromScreenCenter = distanceFromScreenCenter;
                minDistanceFromScreenCenterIndex = i;
            }
        }
        return minDistanceFromScreenCenterIndex;
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), PAGE_SNAP_ANIMATION_DURATION);
    }

    private static class ScrollInterpolator implements Interpolator {
        public float getInterpolation(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * t2 * t2 * t2) + 1.0f;
        }
    }

    /* access modifiers changed from: package-private */
    public float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
    }

    /* access modifiers changed from: protected */
    public void snapToPageWithVelocity(int whichPage, int velocity) {
        int whichPage2 = Math.max(0, Math.min(whichPage, getChildCount() - 1));
        int halfScreenSize = getMeasuredWidth() / 2;
        int delta = (getChildOffset(whichPage2) - getRelativeChildOffset(whichPage2)) - this.mUnboundedScrollX;
        if (Math.abs(velocity) < this.mMinFlingVelocity) {
            snapToPage(whichPage2, PAGE_SNAP_ANIMATION_DURATION);
            return;
        }
        snapToPage(whichPage2, delta, Math.min(Math.round(1000.0f * Math.abs((((float) halfScreenSize) + (((float) halfScreenSize) * distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) Math.abs(delta)) * 1.0f) / ((float) (halfScreenSize * 2)))))) / ((float) Math.max(this.mMinSnapVelocity, Math.abs(velocity))))) * 4, MAX_PAGE_SNAP_DURATION));
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage) {
        snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage, int duration) {
        int whichPage2 = Math.max(0, Math.min(whichPage, getPageCount() - 1));
        snapToPage(whichPage2, (getChildOffset(whichPage2) - getRelativeChildOffset(whichPage2)) - this.mUnboundedScrollX, duration);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage, int delta, int duration) {
        this.mNextPage = whichPage;
        View focusedChild = getFocusedChild();
        if (!(focusedChild == null || whichPage == this.mCurrentPage || focusedChild != getPageAt(this.mCurrentPage))) {
            focusedChild.clearFocus();
        }
        pageBeginMoving();
        awakenScrollBars(duration);
        if (duration == 0) {
            duration = Math.abs(delta);
        }
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        this.mScroller.startScroll(this.mUnboundedScrollX, 0, delta, 0, duration);
        if (this.mDeferScrollUpdate) {
            loadAssociatedPages(this.mNextPage);
        } else {
            this.mDeferLoadAssociatedPagesUntilScrollCompletes = true;
        }
        notifyPageSwitchListener();
        invalidate();
    }

    public void scrollLeft() {
        if (this.mScroller.isFinished()) {
            if (this.mCurrentPage > 0) {
                snapToPage(this.mCurrentPage - 1);
            }
        } else if (this.mNextPage > 0) {
            snapToPage(this.mNextPage - 1);
        }
    }

    public void scrollRight() {
        if (this.mScroller.isFinished()) {
            if (this.mCurrentPage < getChildCount() - 1) {
                snapToPage(this.mCurrentPage + 1);
            }
        } else if (this.mNextPage < getChildCount() - 1) {
            snapToPage(this.mNextPage + 1);
        }
    }

    public void scroll() {
        if (!this.mScroller.isFinished()) {
            return;
        }
        if (this.mCurrentPage < getChildCount() - 1) {
            snapToPage(this.mCurrentPage + 1);
        } else {
            snapToPage(0);
        }
    }

    public int getPageForView(View v) {
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getPageAt(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean allowLongPress() {
        return this.mAllowLongPress;
    }

    public void setAllowLongPress(boolean allowLongPress) {
        this.mAllowLongPress = allowLongPress;
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPage;

        SavedState(Parcelable superState) {
            super(superState);
            this.currentPage = -1;
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentPage = -1;
            this.currentPage = in.readInt();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentPage);
        }
    }

    /* access modifiers changed from: protected */
    public void loadAssociatedPages(int page) {
        loadAssociatedPages(page, false);
    }

    /* access modifiers changed from: protected */
    public void loadAssociatedPages(int page, boolean immediateAndOnly) {
        int count;
        boolean z;
        if (this.mContentIsRefreshable && page < (count = getChildCount())) {
            int lowerPageBound = getAssociatedLowerPageBound(page);
            int upperPageBound = getAssociatedUpperPageBound(page);
            for (int i = 0; i < count; i++) {
                Page layout = (Page) getPageAt(i);
                if (i < lowerPageBound || i > upperPageBound) {
                    if (layout.getPageChildCount() > 0) {
                        layout.removeAllViewsOnPage();
                    }
                    this.mDirtyPageContent.set(i, true);
                }
            }
            for (int i2 = 0; i2 < count; i2++) {
                if ((i2 == page || !immediateAndOnly) && lowerPageBound <= i2 && i2 <= upperPageBound && this.mDirtyPageContent.get(i2).booleanValue()) {
                    if (i2 != page || !immediateAndOnly) {
                        z = false;
                    } else {
                        z = true;
                    }
                    syncPageItems(i2, z);
                    this.mDirtyPageContent.set(i2, false);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getAssociatedLowerPageBound(int page) {
        return Math.max(0, page - 1);
    }

    /* access modifiers changed from: protected */
    public int getAssociatedUpperPageBound(int page) {
        return Math.min(page + 1, getChildCount() - 1);
    }

    /* access modifiers changed from: protected */
    public void invalidatePageData() {
        invalidatePageData(-1, false);
    }

    /* access modifiers changed from: protected */
    public void invalidatePageData(int currentPage) {
        invalidatePageData(currentPage, false);
    }

    /* access modifiers changed from: protected */
    public void invalidatePageData(int currentPage, boolean immediateAndOnly) {
        if (this.mIsDataReady && this.mContentIsRefreshable) {
            this.mScroller.forceFinished(true);
            this.mNextPage = -1;
            syncPages();
            measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
            if (currentPage > -1) {
                setCurrentPage(Math.min(getPageCount() - 1, currentPage));
            }
            int count = getChildCount();
            this.mDirtyPageContent.clear();
            for (int i = 0; i < count; i++) {
                this.mDirtyPageContent.add(true);
            }
            loadAssociatedPages(this.mCurrentPage, immediateAndOnly);
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public View getScrollingIndicator() {
        ViewGroup parent;
        boolean z;
        if (this.mHasScrollIndicator && this.mScrollIndicator == null && (parent = (ViewGroup) getParent()) != null) {
            this.mScrollIndicator = parent.findViewById(R.id.paged_view_indicator);
            if (this.mScrollIndicator != null) {
                z = true;
            } else {
                z = false;
            }
            this.mHasScrollIndicator = z;
            if (this.mHasScrollIndicator) {
                this.mScrollIndicator.setVisibility(0);
            }
        }
        return this.mScrollIndicator;
    }

    /* access modifiers changed from: protected */
    public boolean isScrollingIndicatorEnabled() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void flashScrollingIndicator(boolean animated) {
        removeCallbacks(this.hideScrollingIndicatorRunnable);
        showScrollingIndicator(!animated);
        postDelayed(this.hideScrollingIndicatorRunnable, 650);
    }

    /* access modifiers changed from: protected */
    public void showScrollingIndicator(boolean immediately) {
        this.mShouldShowScrollIndicator = true;
        this.mShouldShowScrollIndicatorImmediately = true;
        if (getChildCount() > 1 && isScrollingIndicatorEnabled()) {
            this.mShouldShowScrollIndicator = false;
            getScrollingIndicator();
            if (this.mScrollIndicator != null) {
                updateScrollingIndicatorPosition();
                this.mScrollIndicator.setVisibility(0);
                cancelScrollingIndicatorAnimations();
                if (immediately || this.mScrollingPaused) {
                    this.mScrollIndicator.setAlpha(1.0f);
                    return;
                }
                this.mScrollIndicatorAnimator = LauncherAnimUtils.ofFloat(this.mScrollIndicator, "alpha", 1.0f);
                this.mScrollIndicatorAnimator.setDuration(150);
                this.mScrollIndicatorAnimator.start();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void cancelScrollingIndicatorAnimations() {
        if (this.mScrollIndicatorAnimator != null) {
            this.mScrollIndicatorAnimator.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void hideScrollingIndicator(boolean immediately) {
        if (getChildCount() > 1 && isScrollingIndicatorEnabled()) {
            getScrollingIndicator();
            if (this.mScrollIndicator != null) {
                updateScrollingIndicatorPosition();
                cancelScrollingIndicatorAnimations();
                if (immediately || this.mScrollingPaused) {
                    this.mScrollIndicator.setVisibility(4);
                    this.mScrollIndicator.setAlpha(0.0f);
                    return;
                }
                this.mScrollIndicatorAnimator = LauncherAnimUtils.ofFloat(this.mScrollIndicator, "alpha", 0.0f);
                this.mScrollIndicatorAnimator.setDuration(650);
                this.mScrollIndicatorAnimator.addListener(new AnimatorListenerAdapter() {
                    private boolean cancelled = false;

                    public void onAnimationCancel(Animator animation) {
                        this.cancelled = true;
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (!this.cancelled) {
                            PagedView.this.mScrollIndicator.setVisibility(4);
                        }
                    }
                });
                this.mScrollIndicatorAnimator.start();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasElasticScrollIndicator() {
        return true;
    }

    private void updateScrollingIndicator() {
        if (getChildCount() > 1 && isScrollingIndicatorEnabled()) {
            getScrollingIndicator();
            if (this.mScrollIndicator != null) {
                updateScrollingIndicatorPosition();
            }
            if (this.mShouldShowScrollIndicator) {
                showScrollingIndicator(this.mShouldShowScrollIndicatorImmediately);
            }
        }
    }

    private void updateScrollingIndicatorPosition() {
        float scrollPos;
        boolean isRtl = isLayoutRtl();
        if (isScrollingIndicatorEnabled() && this.mScrollIndicator != null) {
            int numPages = getChildCount();
            int trackWidth = (getMeasuredWidth() - this.mScrollIndicatorPaddingLeft) - this.mScrollIndicatorPaddingRight;
            int indicatorWidth = (this.mScrollIndicator.getMeasuredWidth() - this.mScrollIndicator.getPaddingLeft()) - this.mScrollIndicator.getPaddingRight();
            if (isRtl) {
                scrollPos = (float) (this.mMaxScrollX - getScrollX());
            } else {
                scrollPos = (float) getScrollX();
            }
            float offset = Math.max(0.0f, Math.min(1.0f, scrollPos / ((float) this.mMaxScrollX)));
            if (isRtl) {
                offset = 1.0f - offset;
            }
            int indicatorSpace = trackWidth / numPages;
            int indicatorPos = ((int) (((float) (trackWidth - indicatorSpace)) * offset)) + this.mScrollIndicatorPaddingLeft;
            if (!hasElasticScrollIndicator()) {
                indicatorPos += (indicatorSpace / 2) - (indicatorWidth / 2);
            } else if (this.mScrollIndicator.getMeasuredWidth() != indicatorSpace) {
                this.mScrollIndicator.getLayoutParams().width = indicatorSpace;
                this.mScrollIndicator.requestLayout();
            }
            this.mScrollIndicator.setTranslationX((float) indicatorPos);
        }
    }

    public void showScrollIndicatorTrack() {
    }

    public void hideScrollIndicatorTrack() {
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        boolean z = true;
        super.onInitializeAccessibilityNodeInfo(info);
        if (getPageCount() <= 1) {
            z = false;
        }
        info.setScrollable(z);
        if (getCurrentPage() < getPageCount() - 1) {
            info.addAction(4096);
        }
        if (getCurrentPage() > 0) {
            info.addAction(8192);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setScrollable(true);
        if (event.getEventType() == 4096) {
            event.setFromIndex(this.mCurrentPage);
            event.setToIndex(this.mCurrentPage);
            event.setItemCount(getChildCount());
        }
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        switch (action) {
            case 4096:
                if (getCurrentPage() < getPageCount() - 1) {
                    scrollRight();
                    return true;
                }
                break;
            case 8192:
                if (getCurrentPage() > 0) {
                    scrollLeft();
                    return true;
                }
                break;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return String.format(getContext().getString(R.string.default_scroll_format), new Object[]{Integer.valueOf(getNextPage() + 1), Integer.valueOf(getChildCount())});
    }

    public boolean onHoverEvent(MotionEvent event) {
        return true;
    }
}
