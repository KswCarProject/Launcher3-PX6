package com.android.launcher3.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.FastScrollThumbDrawable;
import com.android.launcher3.util.Themes;

public class RecyclerViewFastScroller extends View {
    private static final float FAST_SCROLL_OVERLAY_Y_OFFSET_FACTOR = 0.75f;
    private static final int MAX_TRACK_ALPHA = 30;
    private static final int SCROLL_BAR_VIS_DURATION = 150;
    private static final int SCROLL_DELTA_THRESHOLD_DP = 4;
    private static final Property<RecyclerViewFastScroller, Integer> TRACK_WIDTH = new Property<RecyclerViewFastScroller, Integer>(Integer.class, "width") {
        public Integer get(RecyclerViewFastScroller scrollBar) {
            return Integer.valueOf(scrollBar.mWidth);
        }

        public void set(RecyclerViewFastScroller scrollBar, Integer value) {
            scrollBar.setTrackWidth(value.intValue());
        }
    };
    private static final Rect sTempRect = new Rect();
    private final boolean mCanThumbDetach;
    private final ViewConfiguration mConfig;
    private final float mDeltaThreshold;
    private int mDownX;
    private int mDownY;
    /* access modifiers changed from: private */
    public int mDy;
    private boolean mIgnoreDragGesture;
    private boolean mIsDragging;
    private boolean mIsThumbDetached;
    private float mLastTouchY;
    private int mLastY;
    private final int mMaxWidth;
    private final int mMinWidth;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private String mPopupSectionName;
    private TextView mPopupView;
    private boolean mPopupVisible;
    protected BaseRecyclerView mRv;
    protected final int mThumbHeight;
    protected int mThumbOffsetY;
    private final int mThumbPadding;
    private final Paint mThumbPaint;
    protected int mTouchOffsetY;
    private final Paint mTrackPaint;
    /* access modifiers changed from: private */
    public int mWidth;
    private ObjectAnimator mWidthAnimator;

    public RecyclerViewFastScroller(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecyclerViewFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDy = 0;
        this.mTrackPaint = new Paint();
        this.mTrackPaint.setColor(Themes.getAttrColor(context, 16842806));
        this.mTrackPaint.setAlpha(30);
        this.mThumbPaint = new Paint();
        this.mThumbPaint.setAntiAlias(true);
        this.mThumbPaint.setColor(Themes.getColorAccent(context));
        this.mThumbPaint.setStyle(Paint.Style.FILL);
        Resources res = getResources();
        int dimensionPixelSize = res.getDimensionPixelSize(R.dimen.fastscroll_track_min_width);
        this.mMinWidth = dimensionPixelSize;
        this.mWidth = dimensionPixelSize;
        this.mMaxWidth = res.getDimensionPixelSize(R.dimen.fastscroll_track_max_width);
        this.mThumbPadding = res.getDimensionPixelSize(R.dimen.fastscroll_thumb_padding);
        this.mThumbHeight = res.getDimensionPixelSize(R.dimen.fastscroll_thumb_height);
        this.mConfig = ViewConfiguration.get(context);
        this.mDeltaThreshold = res.getDisplayMetrics().density * 4.0f;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewFastScroller, defStyleAttr, 0);
        this.mCanThumbDetach = ta.getBoolean(0, false);
        ta.recycle();
    }

    public void setRecyclerView(BaseRecyclerView rv, TextView popupView) {
        if (!(this.mRv == null || this.mOnScrollListener == null)) {
            this.mRv.removeOnScrollListener(this.mOnScrollListener);
        }
        this.mRv = rv;
        BaseRecyclerView baseRecyclerView = this.mRv;
        AnonymousClass2 r1 = new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int unused = RecyclerViewFastScroller.this.mDy = dy;
                RecyclerViewFastScroller.this.mRv.onUpdateScrollbar(dy);
            }
        };
        this.mOnScrollListener = r1;
        baseRecyclerView.addOnScrollListener(r1);
        this.mPopupView = popupView;
        this.mPopupView.setBackground(new FastScrollThumbDrawable(this.mThumbPaint, Utilities.isRtl(getResources())));
    }

    public void reattachThumbToScroll() {
        this.mIsThumbDetached = false;
    }

    public void setThumbOffsetY(int y) {
        if (this.mThumbOffsetY != y) {
            this.mThumbOffsetY = y;
            invalidate();
        }
    }

    public int getThumbOffsetY() {
        return this.mThumbOffsetY;
    }

    /* access modifiers changed from: private */
    public void setTrackWidth(int width) {
        if (this.mWidth != width) {
            this.mWidth = width;
            invalidate();
        }
    }

    public int getThumbHeight() {
        return this.mThumbHeight;
    }

    public boolean isDraggingThumb() {
        return this.mIsDragging;
    }

    public boolean isThumbDetached() {
        return this.mIsThumbDetached;
    }

    public boolean handleTouchEvent(MotionEvent ev, Point offset) {
        int x = ((int) ev.getX()) - offset.x;
        int y = ((int) ev.getY()) - offset.y;
        boolean z = false;
        switch (ev.getAction()) {
            case 0:
                this.mDownX = x;
                this.mLastY = y;
                this.mDownY = y;
                if (((float) Math.abs(this.mDy)) < this.mDeltaThreshold && this.mRv.getScrollState() != 0) {
                    this.mRv.stopScroll();
                }
                if (!isNearThumb(x, y)) {
                    if (this.mRv.supportsFastScrolling() && isNearScrollBar(this.mDownX)) {
                        calcTouchOffsetAndPrepToFastScroll(this.mDownY, this.mLastY);
                        updateFastScrollSectionNameAndThumbOffset(this.mLastY, y);
                        break;
                    }
                } else {
                    this.mTouchOffsetY = this.mDownY - this.mThumbOffsetY;
                    break;
                }
            case 1:
            case 3:
                this.mRv.onFastScrollCompleted();
                this.mTouchOffsetY = 0;
                this.mLastTouchY = 0.0f;
                this.mIgnoreDragGesture = false;
                if (this.mIsDragging) {
                    this.mIsDragging = false;
                    animatePopupVisibility(false);
                    showActiveScrollbar(false);
                    break;
                }
                break;
            case 2:
                this.mLastY = y;
                boolean z2 = this.mIgnoreDragGesture;
                if (Math.abs(y - this.mDownY) > this.mConfig.getScaledPagingTouchSlop()) {
                    z = true;
                }
                this.mIgnoreDragGesture = z2 | z;
                if (!this.mIsDragging && !this.mIgnoreDragGesture && this.mRv.supportsFastScrolling() && isNearThumb(this.mDownX, this.mLastY) && Math.abs(y - this.mDownY) > this.mConfig.getScaledTouchSlop()) {
                    calcTouchOffsetAndPrepToFastScroll(this.mDownY, this.mLastY);
                }
                if (this.mIsDragging) {
                    updateFastScrollSectionNameAndThumbOffset(this.mLastY, y);
                    break;
                }
                break;
        }
        return this.mIsDragging;
    }

    private void calcTouchOffsetAndPrepToFastScroll(int downY, int lastY) {
        this.mIsDragging = true;
        if (this.mCanThumbDetach) {
            this.mIsThumbDetached = true;
        }
        this.mTouchOffsetY += lastY - downY;
        animatePopupVisibility(true);
        showActiveScrollbar(true);
    }

    private void updateFastScrollSectionNameAndThumbOffset(int lastY, int y) {
        int bottom = this.mRv.getScrollbarTrackHeight() - this.mThumbHeight;
        float boundedY = (float) Math.max(0, Math.min(bottom, y - this.mTouchOffsetY));
        String sectionName = this.mRv.scrollToPositionAtProgress(boundedY / ((float) bottom));
        if (!sectionName.equals(this.mPopupSectionName)) {
            this.mPopupSectionName = sectionName;
            this.mPopupView.setText(sectionName);
        }
        animatePopupVisibility(!sectionName.isEmpty());
        updatePopupY(lastY);
        this.mLastTouchY = boundedY;
        setThumbOffsetY((int) this.mLastTouchY);
    }

    public void onDraw(Canvas canvas) {
        if (this.mThumbOffsetY >= 0) {
            int saveCount = canvas.save();
            canvas.translate((float) (getWidth() / 2), (float) this.mRv.getScrollBarTop());
            float halfW = (float) (this.mWidth / 2);
            canvas.drawRoundRect(-halfW, 0.0f, halfW, (float) this.mRv.getScrollbarTrackHeight(), (float) this.mWidth, (float) this.mWidth, this.mTrackPaint);
            canvas.translate(0.0f, (float) this.mThumbOffsetY);
            float halfW2 = halfW + ((float) this.mThumbPadding);
            float r = (float) (this.mWidth + this.mThumbPadding + this.mThumbPadding);
            canvas.drawRoundRect(-halfW2, 0.0f, halfW2, (float) this.mThumbHeight, r, r, this.mThumbPaint);
            canvas.restoreToCount(saveCount);
        }
    }

    private void showActiveScrollbar(boolean isScrolling) {
        if (this.mWidthAnimator != null) {
            this.mWidthAnimator.cancel();
        }
        Property<RecyclerViewFastScroller, Integer> property = TRACK_WIDTH;
        int[] iArr = new int[1];
        iArr[0] = isScrolling ? this.mMaxWidth : this.mMinWidth;
        this.mWidthAnimator = ObjectAnimator.ofInt(this, property, iArr);
        this.mWidthAnimator.setDuration(150);
        this.mWidthAnimator.start();
    }

    private boolean isNearThumb(int x, int y) {
        int offset = y - this.mThumbOffsetY;
        return x >= 0 && x < getWidth() && offset >= 0 && offset <= this.mThumbHeight;
    }

    public boolean shouldBlockIntercept(int x, int y) {
        return isNearThumb(x, y);
    }

    public boolean isNearScrollBar(int x) {
        return x >= (getWidth() - this.mMaxWidth) / 2 && x <= (getWidth() + this.mMaxWidth) / 2;
    }

    private void animatePopupVisibility(boolean visible) {
        if (this.mPopupVisible != visible) {
            this.mPopupVisible = visible;
            this.mPopupView.animate().cancel();
            this.mPopupView.animate().alpha(visible ? 1.0f : 0.0f).setDuration(visible ? 200 : 150).start();
        }
    }

    private void updatePopupY(int lastTouchY) {
        int height = this.mPopupView.getHeight();
        this.mPopupView.setTranslationY(Utilities.boundToRange((((float) lastTouchY) - (((float) height) * 0.75f)) + ((float) this.mRv.getScrollBarTop()), (float) this.mMaxWidth, (float) ((this.mRv.getScrollbarTrackHeight() - this.mMaxWidth) - height)));
    }

    public boolean isHitInParent(float x, float y, Point outOffset) {
        if (this.mThumbOffsetY < 0) {
            return false;
        }
        getHitRect(sTempRect);
        sTempRect.top += this.mRv.getScrollBarTop();
        if (outOffset != null) {
            outOffset.set(sTempRect.left, sTempRect.top);
        }
        return sTempRect.contains((int) x, (int) y);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
