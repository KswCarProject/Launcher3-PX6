package com.android.launcher3;

import android.animation.LayoutTransition;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.touch.OverScroll;
import java.util.ArrayList;

public abstract class PagedView<T extends View & PageIndicator> extends ViewGroup {
    private static final boolean DEBUG = false;
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    protected static final int INVALID_PAGE = -1;
    protected static final int INVALID_POINTER = -1;
    public static final int INVALID_RESTORE_PAGE = -1001;
    private static final float MAX_SCROLL_PROGRESS = 1.0f;
    private static final int MIN_FLING_VELOCITY = 250;
    private static final int MIN_SNAP_VELOCITY = 1500;
    private static final int OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION = 270;
    public static final int PAGE_SNAP_ANIMATION_DURATION = 750;
    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;
    protected static final ComputePageScrollsLogic SIMPLE_SCROLL_LOGIC = $$Lambda$PagedView$8WXZR5QBUDgMQgE75xbeR6qPZg.INSTANCE;
    public static final int SLOW_PAGE_SNAP_ANIMATION_DURATION = 950;
    private static final String TAG = "PagedView";
    protected static final int TOUCH_STATE_NEXT_PAGE = 3;
    protected static final int TOUCH_STATE_PREV_PAGE = 2;
    protected static final int TOUCH_STATE_REST = 0;
    protected static final int TOUCH_STATE_SCROLLING = 1;
    private static final Rect sTmpRect = new Rect();
    protected int mActivePointerId;
    protected boolean mAllowOverScroll;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected int mCurrentPage;
    private Interpolator mDefaultInterpolator;
    private float mDownMotionX;
    private float mDownMotionY;
    protected boolean mFirstLayout;
    protected int mFlingThresholdVelocity;
    private boolean mFreeScroll;
    protected final Rect mInsets;
    protected boolean mIsLayoutValid;
    protected boolean mIsPageInTransition;
    protected boolean mIsRtl;
    private float mLastMotionX;
    private float mLastMotionXRemainder;
    protected int mMaxScrollX;
    private int mMaximumVelocity;
    protected int mMinFlingVelocity;
    protected int mMinSnapVelocity;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected int mNextPage;
    protected int mOverScrollX;
    protected T mPageIndicator;
    int mPageIndicatorViewId;
    protected int[] mPageScrolls;
    protected int mPageSpacing;
    protected LauncherScroller mScroller;
    private boolean mSettleOnPageInFreeScroll;
    private int[] mTmpIntPair;
    private float mTotalMotionX;
    protected int mTouchSlop;
    protected int mTouchState;
    protected int mUnboundedScrollX;
    private VelocityTracker mVelocityTracker;
    protected boolean mWasInOverscroll;

    protected interface ComputePageScrollsLogic {
        boolean shouldIncludeView(View view);
    }

    static /* synthetic */ boolean lambda$static$0(View v) {
        return v.getVisibility() != 8;
    }

    public PagedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFreeScroll = false;
        this.mSettleOnPageInFreeScroll = false;
        this.mFirstLayout = true;
        this.mNextPage = -1;
        this.mPageSpacing = 0;
        this.mTouchState = 0;
        this.mAllowOverScroll = true;
        this.mActivePointerId = -1;
        this.mIsPageInTransition = false;
        this.mWasInOverscroll = false;
        this.mInsets = new Rect();
        this.mTmpIntPair = new int[2];
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagedView, defStyle, 0);
        this.mPageIndicatorViewId = a.getResourceId(0, -1);
        a.recycle();
        setHapticFeedbackEnabled(false);
        this.mIsRtl = Utilities.isRtl(getResources());
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mScroller = new LauncherScroller(getContext());
        setDefaultInterpolator(Interpolators.SCROLL);
        this.mCurrentPage = 0;
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledPagingTouchSlop();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        float density = getResources().getDisplayMetrics().density;
        this.mFlingThresholdVelocity = (int) (500.0f * density);
        this.mMinFlingVelocity = (int) (250.0f * density);
        this.mMinSnapVelocity = (int) (1500.0f * density);
        if (Utilities.ATLEAST_OREO) {
            setDefaultFocusHighlightEnabled(false);
        }
    }

    /* access modifiers changed from: protected */
    public void setDefaultInterpolator(Interpolator interpolator) {
        this.mDefaultInterpolator = interpolator;
        this.mScroller.setInterpolator(this.mDefaultInterpolator);
    }

    public void initParentViews(View parent) {
        if (this.mPageIndicatorViewId > -1) {
            this.mPageIndicator = parent.findViewById(this.mPageIndicatorViewId);
            ((PageIndicator) this.mPageIndicator).setMarkersCount(getChildCount());
        }
    }

    public T getPageIndicator() {
        return this.mPageIndicator;
    }

    public int getCurrentPage() {
        return this.mCurrentPage;
    }

    public int getNextPage() {
        return this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage;
    }

    public int getPageCount() {
        return getChildCount();
    }

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
            newX = getScrollForPage(this.mCurrentPage);
        }
        scrollTo(newX, 0);
        this.mScroller.setFinalX(newX);
        forceFinishScroller(true);
    }

    private void abortScrollerAnimation(boolean resetNextPage) {
        this.mScroller.abortAnimation();
        if (resetNextPage) {
            this.mNextPage = -1;
            pageEndTransition();
        }
    }

    private void forceFinishScroller(boolean resetNextPage) {
        this.mScroller.forceFinished(true);
        if (resetNextPage) {
            this.mNextPage = -1;
            pageEndTransition();
        }
    }

    private int validateNewPage(int newPage) {
        return Utilities.boundToRange(newPage, 0, getPageCount() - 1);
    }

    public void setCurrentPage(int currentPage) {
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(true);
        }
        if (getChildCount() != 0) {
            int prevPage = this.mCurrentPage;
            this.mCurrentPage = validateNewPage(currentPage);
            updateCurrentPageScroll();
            notifyPageSwitchListener(prevPage);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int prevPage) {
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        if (this.mPageIndicator != null) {
            ((PageIndicator) this.mPageIndicator).setActiveMarker(getNextPage());
        }
    }

    /* access modifiers changed from: protected */
    public void pageBeginTransition() {
        if (!this.mIsPageInTransition) {
            this.mIsPageInTransition = true;
            onPageBeginTransition();
        }
    }

    /* access modifiers changed from: protected */
    public void pageEndTransition() {
        if (this.mIsPageInTransition) {
            this.mIsPageInTransition = false;
            onPageEndTransition();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isPageInTransition() {
        return this.mIsPageInTransition;
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        this.mWasInOverscroll = false;
    }

    /* access modifiers changed from: protected */
    public int getUnboundedScrollX() {
        return this.mUnboundedScrollX;
    }

    public void scrollBy(int x, int y) {
        scrollTo(getUnboundedScrollX() + x, getScrollY() + y);
    }

    public void scrollTo(int x, int y) {
        int i = 0;
        if (this.mFreeScroll) {
            if (!this.mScroller.isFinished() && (x > this.mMaxScrollX || x < 0)) {
                forceFinishScroller(false);
            }
            x = Utilities.boundToRange(x, 0, this.mMaxScrollX);
        }
        this.mUnboundedScrollX = x;
        boolean isXBeforeFirstPage = !this.mIsRtl ? x < 0 : x > this.mMaxScrollX;
        boolean isXAfterLastPage = !this.mIsRtl ? x > this.mMaxScrollX : x < 0;
        if (isXBeforeFirstPage) {
            if (this.mIsRtl) {
                i = this.mMaxScrollX;
            }
            super.scrollTo(i, y);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                if (this.mIsRtl) {
                    overScroll((float) (x - this.mMaxScrollX));
                } else {
                    overScroll((float) x);
                }
            }
        } else if (isXAfterLastPage) {
            if (!this.mIsRtl) {
                i = this.mMaxScrollX;
            }
            super.scrollTo(i, y);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                if (this.mIsRtl) {
                    overScroll((float) x);
                } else {
                    overScroll((float) (x - this.mMaxScrollX));
                }
            }
        } else {
            if (this.mWasInOverscroll) {
                overScroll(0.0f);
                this.mWasInOverscroll = false;
            }
            this.mOverScrollX = x;
            super.scrollTo(x, y);
        }
    }

    private void sendScrollAccessibilityEvent() {
        if (AccessibilityManagerCompat.isObservedEventType(getContext(), 4096) && this.mCurrentPage != getNextPage()) {
            AccessibilityEvent ev = AccessibilityEvent.obtain(4096);
            ev.setScrollable(true);
            ev.setScrollX(getScrollX());
            ev.setScrollY(getScrollY());
            ev.setMaxScrollX(this.mMaxScrollX);
            ev.setMaxScrollY(0);
            sendAccessibilityEventUnchecked(ev);
        }
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper() {
        return computeScrollHelper(true);
    }

    /* access modifiers changed from: protected */
    public void announcePageForAccessibility() {
        if (AccessibilityManagerCompat.isAccessibilityEnabled(getContext())) {
            announceForAccessibility(getCurrentPageDescription());
        }
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper(boolean shouldInvalidate) {
        if (this.mScroller.computeScrollOffset()) {
            if (!(getUnboundedScrollX() == this.mScroller.getCurrX() && getScrollY() == this.mScroller.getCurrY() && this.mOverScrollX == this.mScroller.getCurrX())) {
                scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            }
            if (!shouldInvalidate) {
                return true;
            }
            invalidate();
            return true;
        } else if (this.mNextPage == -1 || !shouldInvalidate) {
            return false;
        } else {
            sendScrollAccessibilityEvent();
            int prevPage = this.mCurrentPage;
            this.mCurrentPage = validateNewPage(this.mNextPage);
            this.mNextPage = -1;
            notifyPageSwitchListener(prevPage);
            if (this.mTouchState == 0) {
                pageEndTransition();
            }
            if (!canAnnouncePageDescription()) {
                return false;
            }
            announcePageForAccessibility();
            return false;
        }
    }

    public void computeScroll() {
        computeScrollHelper();
    }

    public int getExpectedHeight() {
        return getMeasuredHeight();
    }

    public int getNormalChildHeight() {
        return (((getExpectedHeight() - getPaddingTop()) - getPaddingBottom()) - this.mInsets.top) - this.mInsets.bottom;
    }

    public int getExpectedWidth() {
        return getMeasuredWidth();
    }

    public int getNormalChildWidth() {
        return (((getExpectedWidth() - getPaddingLeft()) - getPaddingRight()) - this.mInsets.left) - this.mInsets.right;
    }

    public void requestLayout() {
        this.mIsLayoutValid = false;
        super.requestLayout();
    }

    public void forceLayout() {
        this.mIsLayoutValid = false;
        super.forceLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 0 || heightMode == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (widthSize <= 0 || heightSize <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureChildren(View.MeasureSpec.makeMeasureSpec((widthSize - this.mInsets.left) - this.mInsets.right, 1073741824), View.MeasureSpec.makeMeasureSpec((heightSize - this.mInsets.top) - this.mInsets.bottom, 1073741824));
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mIsLayoutValid = true;
        int childCount = getChildCount();
        boolean pageScrollChanged = false;
        if (this.mPageScrolls == null || childCount != this.mPageScrolls.length) {
            this.mPageScrolls = new int[childCount];
            pageScrollChanged = true;
        }
        if (childCount != 0) {
            if (getPageScrolls(this.mPageScrolls, true, SIMPLE_SCROLL_LOGIC)) {
                pageScrollChanged = true;
            }
            LayoutTransition transition = getLayoutTransition();
            if (transition == null || !transition.isRunning()) {
                updateMaxScrollX();
            } else {
                transition.addTransitionListener(new LayoutTransition.TransitionListener() {
                    public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    }

                    public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                        if (!transition.isRunning()) {
                            transition.removeTransitionListener(this);
                            PagedView.this.updateMaxScrollX();
                        }
                    }
                });
            }
            if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < childCount) {
                updateCurrentPageScroll();
                this.mFirstLayout = false;
            }
            if (this.mScroller.isFinished() && pageScrollChanged) {
                setCurrentPage(getNextPage());
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean getPageScrolls(int[] outPageScrolls, boolean layoutChildren, ComputePageScrollsLogic scrollLogic) {
        int startIndex;
        int childCount;
        int pageScroll;
        int childCount2 = getChildCount();
        int startIndex2 = this.mIsRtl ? childCount2 - 1 : 0;
        int delta = -1;
        int endIndex = this.mIsRtl ? -1 : childCount2;
        if (!this.mIsRtl) {
            delta = 1;
        }
        int verticalCenter = ((((getPaddingTop() + getMeasuredHeight()) + this.mInsets.top) - this.mInsets.bottom) - getPaddingBottom()) / 2;
        int scrollOffsetLeft = this.mInsets.left + getPaddingLeft();
        int scrollOffsetRight = (getWidth() - getPaddingRight()) - this.mInsets.right;
        int i = startIndex2;
        boolean pageScrollChanged = false;
        int childLeft = scrollOffsetLeft;
        while (i != endIndex) {
            View child = getPageAt(i);
            if (scrollLogic.shouldIncludeView(child)) {
                int childWidth = child.getMeasuredWidth();
                int childRight = childLeft + childWidth;
                if (layoutChildren) {
                    int childHeight = child.getMeasuredHeight();
                    childCount = childCount2;
                    int childTop = verticalCenter - (childHeight / 2);
                    startIndex = startIndex2;
                    child.layout(childLeft, childTop, childRight, childTop + childHeight);
                } else {
                    childCount = childCount2;
                    startIndex = startIndex2;
                }
                if (this.mIsRtl != 0) {
                    pageScroll = childLeft - scrollOffsetLeft;
                } else {
                    pageScroll = Math.max(0, childRight - scrollOffsetRight);
                }
                if (outPageScrolls[i] != pageScroll) {
                    pageScrollChanged = true;
                    outPageScrolls[i] = pageScroll;
                }
                childLeft += this.mPageSpacing + childWidth + getChildGap();
            } else {
                childCount = childCount2;
                startIndex = startIndex2;
            }
            i += delta;
            childCount2 = childCount;
            startIndex2 = startIndex;
        }
        ComputePageScrollsLogic computePageScrollsLogic = scrollLogic;
        int i2 = childCount2;
        int i3 = startIndex2;
        return pageScrollChanged;
    }

    /* access modifiers changed from: protected */
    public int getChildGap() {
        return 0;
    }

    /* access modifiers changed from: private */
    public void updateMaxScrollX() {
        this.mMaxScrollX = computeMaxScrollX();
    }

    /* access modifiers changed from: protected */
    public int computeMaxScrollX() {
        int childCount = getChildCount();
        int index = 0;
        if (childCount <= 0) {
            return 0;
        }
        if (!this.mIsRtl) {
            index = childCount - 1;
        }
        return getScrollForPage(index);
    }

    public void setPageSpacing(int pageSpacing) {
        this.mPageSpacing = pageSpacing;
        requestLayout();
    }

    private void dispatchPageCountChanged() {
        if (this.mPageIndicator != null) {
            ((PageIndicator) this.mPageIndicator).setMarkersCount(getChildCount());
        }
        invalidate();
    }

    public void onViewAdded(View child) {
        super.onViewAdded(child);
        dispatchPageCountChanged();
    }

    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        this.mCurrentPage = validateNewPage(this.mCurrentPage);
        dispatchPageCountChanged();
    }

    /* access modifiers changed from: protected */
    public int getChildOffset(int index) {
        if (index < 0 || index > getChildCount() - 1) {
            return 0;
        }
        return getPageAt(index).getLeft();
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int page = indexToPage(indexOfChild(child));
        if (page == this.mCurrentPage && this.mScroller.isFinished()) {
            return false;
        }
        if (immediate) {
            setCurrentPage(page);
            return true;
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
        if (super.dispatchUnhandledMove(focused, direction)) {
            return true;
        }
        if (this.mIsRtl) {
            if (direction == 17) {
                direction = 66;
            } else if (direction == 66) {
                direction = 17;
            }
        }
        if (direction == 17) {
            if (getCurrentPage() <= 0) {
                return false;
            }
            snapToPage(getCurrentPage() - 1);
            getChildAt(getCurrentPage() - 1).requestFocus(direction);
            return true;
        } else if (direction != 66 || getCurrentPage() >= getPageCount() - 1) {
            return false;
        } else {
            snapToPage(getCurrentPage() + 1);
            getChildAt(getCurrentPage() + 1).requestFocus(direction);
            return true;
        }
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (getDescendantFocusability() != 393216) {
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
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void focusableViewAvailable(android.view.View r5) {
        /*
            r4 = this;
            int r0 = r4.mCurrentPage
            android.view.View r0 = r4.getPageAt(r0)
            r1 = r5
        L_0x0007:
            if (r1 != r0) goto L_0x000d
            super.focusableViewAvailable(r5)
            return
        L_0x000d:
            if (r1 != r4) goto L_0x0010
            return
        L_0x0010:
            android.view.ViewParent r2 = r1.getParent()
            boolean r3 = r2 instanceof android.view.View
            if (r3 == 0) goto L_0x0020
            android.view.ViewParent r3 = r1.getParent()
            r1 = r3
            android.view.View r1 = (android.view.View) r1
            goto L_0x0007
        L_0x0020:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.PagedView.focusableViewAvailable(android.view.View):void");
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            getPageAt(this.mCurrentPage).cancelLongPress();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private boolean isTouchPointInViewportWithBuffer(int x, int y) {
        sTmpRect.set((-getMeasuredWidth()) / 2, 0, (getMeasuredWidth() * 3) / 2, getMeasuredHeight());
        return sTmpRect.contains(x, y);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        acquireVelocityTrackerAndAddMovement(ev);
        if (getChildCount() <= 0) {
            return super.onInterceptTouchEvent(ev);
        }
        int action = ev.getAction();
        if (action == 2 && this.mTouchState == 1) {
            return true;
        }
        int i = action & 255;
        if (i != 6) {
            switch (i) {
                case 0:
                    float x = ev.getX();
                    float y = ev.getY();
                    this.mDownMotionX = x;
                    this.mDownMotionY = y;
                    this.mLastMotionX = x;
                    this.mLastMotionXRemainder = 0.0f;
                    this.mTotalMotionX = 0.0f;
                    this.mActivePointerId = ev.getPointerId(0);
                    if (!(this.mScroller.isFinished() || Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) < this.mTouchSlop / 3)) {
                        if (!isTouchPointInViewportWithBuffer((int) this.mDownMotionX, (int) this.mDownMotionY)) {
                            this.mTouchState = 0;
                            break;
                        } else {
                            this.mTouchState = 1;
                            break;
                        }
                    } else {
                        this.mTouchState = 0;
                        if (!this.mScroller.isFinished() && !this.mFreeScroll) {
                            setCurrentPage(getNextPage());
                            pageEndTransition();
                            break;
                        }
                    }
                    break;
                case 1:
                case 3:
                    resetTouchState();
                    break;
                case 2:
                    if (this.mActivePointerId != -1) {
                        determineScrollingStart(ev);
                        break;
                    }
                    break;
            }
        } else {
            onSecondaryPointerUp(ev);
            releaseVelocityTracker();
        }
        if (this.mTouchState != 0) {
            return true;
        }
        return false;
    }

    public boolean isHandlingTouch() {
        return this.mTouchState != 0;
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev) {
        determineScrollingStart(ev, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (pointerIndex != -1) {
            float x = ev.getX(pointerIndex);
            if (isTouchPointInViewportWithBuffer((int) x, (int) ev.getY(pointerIndex))) {
                if (((int) Math.abs(x - this.mLastMotionX)) > Math.round(((float) this.mTouchSlop) * touchSlopScale)) {
                    this.mTouchState = 1;
                    this.mTotalMotionX += Math.abs(this.mLastMotionX - x);
                    this.mLastMotionX = x;
                    this.mLastMotionXRemainder = 0.0f;
                    onScrollInteractionBegin();
                    pageBeginTransition();
                    requestDisallowInterceptTouchEvent(true);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCurrentPageLongPress() {
        View currentPage = getPageAt(this.mCurrentPage);
        if (currentPage != null) {
            currentPage.cancelLongPress();
        }
    }

    /* access modifiers changed from: protected */
    public float getScrollProgress(int screenCenter, View v, int page) {
        int totalDistance;
        int delta = screenCenter - (getScrollForPage(page) + (getMeasuredWidth() / 2));
        int count = getChildCount();
        int adjacentPage = page + 1;
        if ((delta < 0 && !this.mIsRtl) || (delta > 0 && this.mIsRtl)) {
            adjacentPage = page - 1;
        }
        if (adjacentPage < 0 || adjacentPage > count - 1) {
            totalDistance = v.getMeasuredWidth() + this.mPageSpacing;
        } else {
            totalDistance = Math.abs(getScrollForPage(adjacentPage) - getScrollForPage(page));
        }
        return Math.max(Math.min(((float) delta) / (((float) totalDistance) * 1.0f), 1.0f), -1.0f);
    }

    public int getScrollForPage(int index) {
        if (this.mPageScrolls == null || index >= this.mPageScrolls.length || index < 0) {
            return 0;
        }
        return this.mPageScrolls[index];
    }

    public int getLayoutTransitionOffsetForPage(int index) {
        if (this.mPageScrolls == null || index >= this.mPageScrolls.length || index < 0) {
            return 0;
        }
        return (int) (getChildAt(index).getX() - ((float) (this.mPageScrolls[index] + (this.mIsRtl ? getPaddingRight() : getPaddingLeft()))));
    }

    /* access modifiers changed from: protected */
    public void dampedOverScroll(float amount) {
        if (Float.compare(amount, 0.0f) != 0) {
            int overScrollAmount = OverScroll.dampedScroll(amount, getMeasuredWidth());
            if (amount < 0.0f) {
                this.mOverScrollX = overScrollAmount;
                super.scrollTo(this.mOverScrollX, getScrollY());
            } else {
                this.mOverScrollX = this.mMaxScrollX + overScrollAmount;
                super.scrollTo(this.mOverScrollX, getScrollY());
            }
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void overScroll(float amount) {
        dampedOverScroll(amount);
    }

    /* access modifiers changed from: protected */
    public void enableFreeScroll(boolean settleOnPageInFreeScroll) {
        setEnableFreeScroll(true);
        this.mSettleOnPageInFreeScroll = settleOnPageInFreeScroll;
    }

    private void setEnableFreeScroll(boolean freeScroll) {
        boolean wasFreeScroll = this.mFreeScroll;
        this.mFreeScroll = freeScroll;
        if (this.mFreeScroll) {
            setCurrentPage(getNextPage());
        } else if (wasFreeScroll) {
            snapToPage(getNextPage());
        }
        setEnableOverscroll(!freeScroll);
    }

    /* access modifiers changed from: protected */
    public void setEnableOverscroll(boolean enable) {
        this.mAllowOverScroll = enable;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int finalX;
        MotionEvent motionEvent = ev;
        super.onTouchEvent(ev);
        if (getChildCount() <= 0) {
            return super.onTouchEvent(ev);
        }
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction();
        int i = action & 255;
        if (i != 6) {
            switch (i) {
                case 0:
                    if (!this.mScroller.isFinished()) {
                        abortScrollerAnimation(false);
                    }
                    float x = ev.getX();
                    this.mLastMotionX = x;
                    this.mDownMotionX = x;
                    this.mDownMotionY = ev.getY();
                    this.mLastMotionXRemainder = 0.0f;
                    this.mTotalMotionX = 0.0f;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    if (this.mTouchState != 1) {
                        return true;
                    }
                    onScrollInteractionBegin();
                    pageBeginTransition();
                    return true;
                case 1:
                    if (this.mTouchState == 1) {
                        int activePointerId = this.mActivePointerId;
                        float x2 = motionEvent.getX(motionEvent.findPointerIndex(activePointerId));
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                        int velocityX = (int) velocityTracker.getXVelocity(activePointerId);
                        int deltaX = (int) (x2 - this.mDownMotionX);
                        int pageWidth = getPageAt(this.mCurrentPage).getMeasuredWidth();
                        boolean isSignificantMove = ((float) Math.abs(deltaX)) > ((float) pageWidth) * SIGNIFICANT_MOVE_THRESHOLD;
                        this.mTotalMotionX += Math.abs((this.mLastMotionX + this.mLastMotionXRemainder) - x2);
                        boolean isFling = this.mTotalMotionX > ((float) this.mTouchSlop) && shouldFlingForVelocity(velocityX);
                        if (!this.mFreeScroll) {
                            boolean returnToOriginalPage = false;
                            if (((float) Math.abs(deltaX)) > ((float) pageWidth) * RETURN_TO_ORIGINAL_PAGE_THRESHOLD && Math.signum((float) velocityX) != Math.signum((float) deltaX) && isFling) {
                                returnToOriginalPage = true;
                            }
                            boolean isDeltaXLeft = !this.mIsRtl ? deltaX < 0 : deltaX > 0;
                            boolean isVelocityXLeft = !this.mIsRtl ? velocityX < 0 : velocityX > 0;
                            if (((!isSignificantMove || isDeltaXLeft || isFling) && (!isFling || isVelocityXLeft)) || this.mCurrentPage <= 0) {
                                if ((!isSignificantMove || !isDeltaXLeft || isFling) && (!isFling || !isVelocityXLeft)) {
                                } else {
                                    int i2 = action;
                                    if (this.mCurrentPage < getChildCount() - 1) {
                                        snapToPageWithVelocity(returnToOriginalPage ? this.mCurrentPage : this.mCurrentPage + 1, velocityX);
                                    }
                                }
                                snapToDestination();
                            } else {
                                snapToPageWithVelocity(returnToOriginalPage ? this.mCurrentPage : this.mCurrentPage - 1, velocityX);
                                int i3 = action;
                            }
                            int i4 = activePointerId;
                            boolean z = isFling;
                        } else {
                            if (!this.mScroller.isFinished()) {
                                abortScrollerAnimation(true);
                            }
                            float scaleX = getScaleX();
                            this.mScroller.setInterpolator(this.mDefaultInterpolator);
                            this.mScroller.fling((int) (((float) getScrollX()) * scaleX), getScrollY(), (int) (((float) (-velocityX)) * scaleX), 0, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 0, 0);
                            int unscaledScrollX = (int) (((float) this.mScroller.getFinalX()) / scaleX);
                            this.mNextPage = getPageNearestToCenterOfScreen(unscaledScrollX);
                            int firstPageScroll = getScrollForPage(!this.mIsRtl ? 0 : getPageCount() - 1);
                            float f = scaleX;
                            int lastPageScroll = getScrollForPage(!this.mIsRtl ? getPageCount() - 1 : 0);
                            int i5 = activePointerId;
                            if (this.mSettleOnPageInFreeScroll == 0 || unscaledScrollX <= 0 || unscaledScrollX >= this.mMaxScrollX) {
                                boolean z2 = isFling;
                            } else {
                                if (unscaledScrollX < firstPageScroll / 2) {
                                    finalX = 0;
                                } else {
                                    finalX = unscaledScrollX > (this.mMaxScrollX + lastPageScroll) / 2 ? this.mMaxScrollX : getScrollForPage(this.mNextPage);
                                }
                                int i6 = lastPageScroll;
                                boolean z3 = isFling;
                                this.mScroller.setFinalX((int) (((float) finalX) * getScaleX()));
                                int extraScrollDuration = 270 - this.mScroller.getDuration();
                                if (extraScrollDuration > 0) {
                                    this.mScroller.extendDuration(extraScrollDuration);
                                }
                            }
                            invalidate();
                        }
                        onScrollInteractionEnd();
                    } else {
                        if (this.mTouchState == 2) {
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
                        }
                    }
                    resetTouchState();
                    return true;
                case 2:
                    if (this.mTouchState != 1) {
                        determineScrollingStart(ev);
                        break;
                    } else {
                        int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (pointerIndex != -1) {
                            float x3 = motionEvent.getX(pointerIndex);
                            float deltaX2 = (this.mLastMotionX + this.mLastMotionXRemainder) - x3;
                            this.mTotalMotionX += Math.abs(deltaX2);
                            if (Math.abs(deltaX2) < 1.0f) {
                                awakenScrollBars();
                                break;
                            } else {
                                scrollBy((int) deltaX2, 0);
                                this.mLastMotionX = x3;
                                this.mLastMotionXRemainder = deltaX2 - ((float) ((int) deltaX2));
                                break;
                            }
                        } else {
                            return true;
                        }
                    }
                case 3:
                    if (this.mTouchState == 1) {
                        snapToDestination();
                        onScrollInteractionEnd();
                    }
                    resetTouchState();
                    break;
            }
            return true;
        }
        onSecondaryPointerUp(ev);
        releaseVelocityTracker();
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldFlingForVelocity(int velocityX) {
        return Math.abs(velocityX) > this.mFlingThresholdVelocity;
    }

    private void resetTouchState() {
        releaseVelocityTracker();
        this.mTouchState = 0;
        this.mActivePointerId = -1;
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionBegin() {
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionEnd() {
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        float hscroll;
        float vscroll;
        if ((event.getSource() & 2) != 0 && event.getAction() == 8) {
            if ((event.getMetaState() & 1) != 0) {
                vscroll = 0.0f;
                hscroll = event.getAxisValue(9);
            } else {
                vscroll = -event.getAxisValue(9);
                hscroll = event.getAxisValue(10);
            }
            if (!(hscroll == 0.0f && vscroll == 0.0f)) {
                boolean isForwardScroll = false;
                if (!this.mIsRtl ? hscroll > 0.0f || vscroll > 0.0f : hscroll < 0.0f || vscroll < 0.0f) {
                    isForwardScroll = true;
                }
                if (isForwardScroll) {
                    scrollRight();
                } else {
                    scrollLeft();
                }
                return true;
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
            this.mVelocityTracker.clear();
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
            this.mLastMotionXRemainder = 0.0f;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        int page = indexToPage(indexOfChild(child));
        if (page >= 0 && page != getCurrentPage() && !isInTouchMode()) {
            snapToPage(page);
        }
    }

    public int getPageNearestToCenterOfScreen() {
        return getPageNearestToCenterOfScreen(getScrollX());
    }

    private int getPageNearestToCenterOfScreen(int scaledScrollX) {
        int screenCenter = (getMeasuredWidth() / 2) + scaledScrollX;
        int minDistanceFromScreenCenter = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int minDistanceFromScreenCenterIndex = -1;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int distanceFromScreenCenter = Math.abs((getChildOffset(i) + (getPageAt(i).getMeasuredWidth() / 2)) - screenCenter);
            if (distanceFromScreenCenter < minDistanceFromScreenCenter) {
                minDistanceFromScreenCenter = distanceFromScreenCenter;
                minDistanceFromScreenCenterIndex = i;
            }
        }
        return minDistanceFromScreenCenterIndex;
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), getPageSnapDuration());
    }

    /* access modifiers changed from: protected */
    public boolean isInOverScroll() {
        return this.mOverScrollX > this.mMaxScrollX || this.mOverScrollX < 0;
    }

    /* access modifiers changed from: protected */
    public int getPageSnapDuration() {
        if (isInOverScroll()) {
            return OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION;
        }
        return PAGE_SNAP_ANIMATION_DURATION;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
    }

    /* access modifiers changed from: protected */
    public boolean snapToPageWithVelocity(int whichPage, int velocity) {
        int whichPage2 = validateNewPage(whichPage);
        int halfScreenSize = getMeasuredWidth() / 2;
        int delta = getScrollForPage(whichPage2) - getUnboundedScrollX();
        if (Math.abs(velocity) < this.mMinFlingVelocity) {
            return snapToPage(whichPage2, PAGE_SNAP_ANIMATION_DURATION);
        }
        return snapToPage(whichPage2, delta, Math.round(Math.abs((((float) halfScreenSize) + (((float) halfScreenSize) * distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) Math.abs(delta)) * 1.0f) / ((float) (halfScreenSize * 2)))))) / ((float) Math.max(this.mMinSnapVelocity, Math.abs(velocity)))) * 1000.0f) * 4);
    }

    public boolean snapToPage(int whichPage) {
        return snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
    }

    public boolean snapToPageImmediately(int whichPage) {
        return snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION, true, (TimeInterpolator) null);
    }

    public boolean snapToPage(int whichPage, int duration) {
        return snapToPage(whichPage, duration, false, (TimeInterpolator) null);
    }

    public boolean snapToPage(int whichPage, int duration, TimeInterpolator interpolator) {
        return snapToPage(whichPage, duration, false, interpolator);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPage(int whichPage, int duration, boolean immediate, TimeInterpolator interpolator) {
        int whichPage2 = validateNewPage(whichPage);
        return snapToPage(whichPage2, getScrollForPage(whichPage2) - getUnboundedScrollX(), duration, immediate, interpolator);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPage(int whichPage, int delta, int duration) {
        return snapToPage(whichPage, delta, duration, false, (TimeInterpolator) null);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPage(int whichPage, int delta, int duration, boolean immediate, TimeInterpolator interpolator) {
        if (this.mFirstLayout) {
            setCurrentPage(whichPage);
            return false;
        }
        this.mNextPage = validateNewPage(whichPage);
        awakenScrollBars(duration);
        if (immediate) {
            duration = 0;
        } else if (duration == 0) {
            duration = Math.abs(delta);
        }
        if (duration != 0) {
            pageBeginTransition();
        }
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }
        if (interpolator != null) {
            this.mScroller.setInterpolator(interpolator);
        } else {
            this.mScroller.setInterpolator(this.mDefaultInterpolator);
        }
        this.mScroller.startScroll(getUnboundedScrollX(), 0, delta, 0, duration);
        updatePageIndicator();
        if (immediate) {
            computeScroll();
            pageEndTransition();
        }
        invalidate();
        if (Math.abs(delta) > 0) {
            return true;
        }
        return false;
    }

    public boolean scrollLeft() {
        if (getNextPage() <= 0) {
            return false;
        }
        snapToPage(getNextPage() - 1);
        return true;
    }

    public boolean scrollRight() {
        if (getNextPage() >= getChildCount() - 1) {
            return false;
        }
        snapToPage(getNextPage() + 1);
        return true;
    }

    public CharSequence getAccessibilityClassName() {
        return ScrollView.class.getName();
    }

    /* access modifiers changed from: protected */
    public boolean isPageOrderFlipped() {
        return false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        boolean pagesFlipped = isPageOrderFlipped();
        info.setScrollable(getPageCount() > 1);
        int i = 4096;
        if (getCurrentPage() < getPageCount() - 1) {
            info.addAction(pagesFlipped ? 8192 : 4096);
        }
        if (getCurrentPage() > 0) {
            if (!pagesFlipped) {
                i = 8192;
            }
            info.addAction(i);
        }
        info.setLongClickable(false);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
    }

    public void sendAccessibilityEvent(int eventType) {
        if (eventType != 4096) {
            super.sendAccessibilityEvent(eventType);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        boolean z = true;
        if (getPageCount() <= 1) {
            z = false;
        }
        event.setScrollable(z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0024 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0034 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAccessibilityAction(int r4, android.os.Bundle r5) {
        /*
            r3 = this;
            boolean r0 = super.performAccessibilityAction(r4, r5)
            r1 = 1
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            boolean r0 = r3.isPageOrderFlipped()
            r2 = 4096(0x1000, float:5.74E-42)
            if (r4 == r2) goto L_0x0025
            r2 = 8192(0x2000, float:1.14794E-41)
            if (r4 == r2) goto L_0x0015
            goto L_0x0035
        L_0x0015:
            if (r0 == 0) goto L_0x001e
            boolean r2 = r3.scrollRight()
            if (r2 == 0) goto L_0x0035
            goto L_0x0024
        L_0x001e:
            boolean r2 = r3.scrollLeft()
            if (r2 == 0) goto L_0x0035
        L_0x0024:
            return r1
        L_0x0025:
            if (r0 == 0) goto L_0x002e
            boolean r2 = r3.scrollLeft()
            if (r2 == 0) goto L_0x0035
            goto L_0x0034
        L_0x002e:
            boolean r2 = r3.scrollRight()
            if (r2 == 0) goto L_0x0035
        L_0x0034:
            return r1
        L_0x0035:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.PagedView.performAccessibilityAction(int, android.os.Bundle):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean canAnnouncePageDescription() {
        return true;
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return getContext().getString(R.string.default_scroll_format, new Object[]{Integer.valueOf(getNextPage() + 1), Integer.valueOf(getChildCount())});
    }

    /* access modifiers changed from: protected */
    public float getDownMotionX() {
        return this.mDownMotionX;
    }

    /* access modifiers changed from: protected */
    public float getDownMotionY() {
        return this.mDownMotionY;
    }

    public int[] getVisibleChildrenRange() {
        float visibleLeft = 0.0f;
        float visibleRight = ((float) getMeasuredWidth()) + 0.0f;
        float scaleX = getScaleX();
        if (scaleX < 1.0f && scaleX > 0.0f) {
            float mid = (float) (getMeasuredWidth() / 2);
            visibleLeft = mid - ((mid - 0.0f) / scaleX);
            visibleRight = mid + ((visibleRight - mid) / scaleX);
        }
        int childCount = getChildCount();
        int rightChild = -1;
        int leftChild = -1;
        for (int i = 0; i < childCount; i++) {
            View child = getPageAt(i);
            float left = (((float) child.getLeft()) + child.getTranslationX()) - ((float) getScrollX());
            if (left <= visibleRight && ((float) child.getMeasuredWidth()) + left >= visibleLeft) {
                if (leftChild == -1) {
                    leftChild = i;
                }
                rightChild = i;
            }
        }
        this.mTmpIntPair[0] = leftChild;
        this.mTmpIntPair[1] = rightChild;
        return this.mTmpIntPair;
    }
}
