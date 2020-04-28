package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.util.FocusLogic;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.LauncherAppWidgetHostView;

public class AppWidgetResizeFrame extends AbstractFloatingView implements View.OnKeyListener {
    private static final float DIMMED_HANDLE_ALPHA = 0.0f;
    private static final int HANDLE_COUNT = 4;
    private static final int INDEX_BOTTOM = 3;
    private static final int INDEX_LEFT = 0;
    private static final int INDEX_RIGHT = 2;
    private static final int INDEX_TOP = 1;
    private static final float RESIZE_THRESHOLD = 0.66f;
    private static final int SNAP_DURATION = 150;
    private static Point[] sCellSize;
    private static final Rect sTmpRect = new Rect();
    private final int mBackgroundPadding;
    private final IntRange mBaselineX;
    private final IntRange mBaselineY;
    private boolean mBottomBorderActive;
    private int mBottomTouchRegionAdjustment;
    private CellLayout mCellLayout;
    private int mDeltaX;
    private int mDeltaXAddOn;
    private final IntRange mDeltaXRange;
    private int mDeltaY;
    private int mDeltaYAddOn;
    private final IntRange mDeltaYRange;
    private final int[] mDirectionVector;
    private final View[] mDragHandles;
    private DragLayer mDragLayer;
    private final int[] mLastDirectionVector;
    private final Launcher mLauncher;
    private boolean mLeftBorderActive;
    private int mMinHSpan;
    private int mMinVSpan;
    private int mResizeMode;
    private boolean mRightBorderActive;
    private int mRunningHInc;
    private int mRunningVInc;
    private final DragViewStateAnnouncer mStateAnnouncer;
    private final IntRange mTempRange1;
    private final IntRange mTempRange2;
    private boolean mTopBorderActive;
    private int mTopTouchRegionAdjustment;
    private final int mTouchTargetWidth;
    private Rect mWidgetPadding;
    private LauncherAppWidgetHostView mWidgetView;
    private int mXDown;
    private int mYDown;

    public AppWidgetResizeFrame(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppWidgetResizeFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppWidgetResizeFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDragHandles = new View[4];
        this.mDirectionVector = new int[2];
        this.mLastDirectionVector = new int[2];
        this.mTempRange1 = new IntRange();
        this.mTempRange2 = new IntRange();
        this.mDeltaXRange = new IntRange();
        this.mBaselineX = new IntRange();
        this.mDeltaYRange = new IntRange();
        this.mBaselineY = new IntRange();
        this.mTopTouchRegionAdjustment = 0;
        this.mBottomTouchRegionAdjustment = 0;
        this.mLauncher = Launcher.getLauncher(context);
        this.mStateAnnouncer = DragViewStateAnnouncer.createFor(this);
        this.mBackgroundPadding = getResources().getDimensionPixelSize(R.dimen.resize_frame_background_padding);
        this.mTouchTargetWidth = this.mBackgroundPadding * 2;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ViewGroup content = (ViewGroup) getChildAt(0);
        for (int i = 0; i < 4; i++) {
            this.mDragHandles[i] = content.getChildAt(i);
        }
    }

    public static void showForWidget(LauncherAppWidgetHostView widget, CellLayout cellLayout) {
        Launcher launcher = Launcher.getLauncher(cellLayout.getContext());
        AbstractFloatingView.closeAllOpenViews(launcher);
        DragLayer dl = launcher.getDragLayer();
        AppWidgetResizeFrame frame = (AppWidgetResizeFrame) launcher.getLayoutInflater().inflate(R.layout.app_widget_resize_frame, dl, false);
        frame.setupForWidget(widget, cellLayout, dl);
        ((BaseDragLayer.LayoutParams) frame.getLayoutParams()).customPosition = true;
        dl.addView(frame);
        frame.mIsOpen = true;
        frame.snapToWidget(false);
    }

    private void setupForWidget(LauncherAppWidgetHostView widgetView, CellLayout cellLayout, DragLayer dragLayer) {
        this.mCellLayout = cellLayout;
        this.mWidgetView = widgetView;
        LauncherAppWidgetProviderInfo info = (LauncherAppWidgetProviderInfo) widgetView.getAppWidgetInfo();
        this.mResizeMode = info.resizeMode;
        this.mDragLayer = dragLayer;
        this.mMinHSpan = info.minSpanX;
        this.mMinVSpan = info.minSpanY;
        this.mWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(getContext(), widgetView.getAppWidgetInfo().provider, (Rect) null);
        if (this.mResizeMode == 1) {
            this.mDragHandles[1].setVisibility(8);
            this.mDragHandles[3].setVisibility(8);
        } else if (this.mResizeMode == 2) {
            this.mDragHandles[0].setVisibility(8);
            this.mDragHandles[2].setVisibility(8);
        }
        this.mCellLayout.markCellsAsUnoccupiedForView(this.mWidgetView);
        setOnKeyListener(this);
    }

    public boolean beginResizeIfPointInRegion(int x, int y) {
        boolean horizontalActive = (this.mResizeMode & 1) != 0;
        boolean verticalActive = (this.mResizeMode & 2) != 0;
        this.mLeftBorderActive = x < this.mTouchTargetWidth && horizontalActive;
        this.mRightBorderActive = x > getWidth() - this.mTouchTargetWidth && horizontalActive;
        this.mTopBorderActive = y < this.mTouchTargetWidth + this.mTopTouchRegionAdjustment && verticalActive;
        this.mBottomBorderActive = y > (getHeight() - this.mTouchTargetWidth) + this.mBottomTouchRegionAdjustment && verticalActive;
        boolean anyBordersActive = this.mLeftBorderActive || this.mRightBorderActive || this.mTopBorderActive || this.mBottomBorderActive;
        if (anyBordersActive) {
            float f = 0.0f;
            this.mDragHandles[0].setAlpha(this.mLeftBorderActive ? 1.0f : 0.0f);
            this.mDragHandles[2].setAlpha(this.mRightBorderActive ? 1.0f : 0.0f);
            this.mDragHandles[1].setAlpha(this.mTopBorderActive ? 1.0f : 0.0f);
            View view = this.mDragHandles[3];
            if (this.mBottomBorderActive) {
                f = 1.0f;
            }
            view.setAlpha(f);
        }
        if (this.mLeftBorderActive) {
            this.mDeltaXRange.set(-getLeft(), getWidth() - (this.mTouchTargetWidth * 2));
        } else if (this.mRightBorderActive) {
            this.mDeltaXRange.set((this.mTouchTargetWidth * 2) - getWidth(), this.mDragLayer.getWidth() - getRight());
        } else {
            this.mDeltaXRange.set(0, 0);
        }
        this.mBaselineX.set(getLeft(), getRight());
        if (this.mTopBorderActive) {
            this.mDeltaYRange.set(-getTop(), getHeight() - (this.mTouchTargetWidth * 2));
        } else if (this.mBottomBorderActive) {
            this.mDeltaYRange.set((this.mTouchTargetWidth * 2) - getHeight(), this.mDragLayer.getHeight() - getBottom());
        } else {
            this.mDeltaYRange.set(0, 0);
        }
        this.mBaselineY.set(getTop(), getBottom());
        return anyBordersActive;
    }

    public void visualizeResizeForDelta(int deltaX, int deltaY) {
        this.mDeltaX = this.mDeltaXRange.clamp(deltaX);
        this.mDeltaY = this.mDeltaYRange.clamp(deltaY);
        BaseDragLayer.LayoutParams lp = (BaseDragLayer.LayoutParams) getLayoutParams();
        this.mDeltaX = this.mDeltaXRange.clamp(deltaX);
        this.mBaselineX.applyDelta(this.mLeftBorderActive, this.mRightBorderActive, this.mDeltaX, this.mTempRange1);
        lp.x = this.mTempRange1.start;
        lp.width = this.mTempRange1.size();
        this.mDeltaY = this.mDeltaYRange.clamp(deltaY);
        this.mBaselineY.applyDelta(this.mTopBorderActive, this.mBottomBorderActive, this.mDeltaY, this.mTempRange1);
        lp.y = this.mTempRange1.start;
        lp.height = this.mTempRange1.size();
        resizeWidgetIfNeeded(false);
        getSnappedRectRelativeToDragLayer(sTmpRect);
        if (this.mLeftBorderActive) {
            lp.width = (sTmpRect.width() + sTmpRect.left) - lp.x;
        }
        if (this.mTopBorderActive) {
            lp.height = (sTmpRect.height() + sTmpRect.top) - lp.y;
        }
        if (this.mRightBorderActive) {
            lp.x = sTmpRect.left;
        }
        if (this.mBottomBorderActive) {
            lp.y = sTmpRect.top;
        }
        requestLayout();
    }

    private static int getSpanIncrement(float deltaFrac) {
        if (Math.abs(deltaFrac) > RESIZE_THRESHOLD) {
            return Math.round(deltaFrac);
        }
        return 0;
    }

    private void resizeWidgetIfNeeded(boolean onDismiss) {
        float xThreshold = (float) this.mCellLayout.getCellWidth();
        float yThreshold = (float) this.mCellLayout.getCellHeight();
        int hSpanInc = getSpanIncrement((((float) (this.mDeltaX + this.mDeltaXAddOn)) / xThreshold) - ((float) this.mRunningHInc));
        int vSpanInc = getSpanIncrement((((float) (this.mDeltaY + this.mDeltaYAddOn)) / yThreshold) - ((float) this.mRunningVInc));
        if (onDismiss || hSpanInc != 0 || vSpanInc != 0) {
            this.mDirectionVector[0] = 0;
            this.mDirectionVector[1] = 0;
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) this.mWidgetView.getLayoutParams();
            int spanX = lp.cellHSpan;
            int spanY = lp.cellVSpan;
            int cellX = lp.useTmpCoords ? lp.tmpCellX : lp.cellX;
            int cellY = lp.useTmpCoords ? lp.tmpCellY : lp.cellY;
            this.mTempRange1.set(cellX, spanX + cellX);
            CellLayout.LayoutParams lp2 = lp;
            float f = xThreshold;
            int cellY2 = cellY;
            int i = cellX;
            int spanY2 = spanY;
            int i2 = spanX;
            int hSpanDelta = this.mTempRange1.applyDeltaAndBound(this.mLeftBorderActive, this.mRightBorderActive, hSpanInc, this.mMinHSpan, this.mCellLayout.getCountX(), this.mTempRange2);
            int cellX2 = this.mTempRange2.start;
            int spanX2 = this.mTempRange2.size();
            int i3 = -1;
            if (hSpanDelta != 0) {
                this.mDirectionVector[0] = this.mLeftBorderActive ? -1 : 1;
            }
            this.mTempRange1.set(cellY2, spanY2 + cellY2);
            int vSpanDelta = this.mTempRange1.applyDeltaAndBound(this.mTopBorderActive, this.mBottomBorderActive, vSpanInc, this.mMinVSpan, this.mCellLayout.getCountY(), this.mTempRange2);
            int cellY3 = this.mTempRange2.start;
            int spanY3 = this.mTempRange2.size();
            if (vSpanDelta != 0) {
                int[] iArr = this.mDirectionVector;
                if (!this.mTopBorderActive) {
                    i3 = 1;
                }
                iArr[1] = i3;
            }
            if (onDismiss || vSpanDelta != 0 || hSpanDelta != 0) {
                if (onDismiss) {
                    this.mDirectionVector[0] = this.mLastDirectionVector[0];
                    this.mDirectionVector[1] = this.mLastDirectionVector[1];
                } else {
                    this.mLastDirectionVector[0] = this.mDirectionVector[0];
                    this.mLastDirectionVector[1] = this.mDirectionVector[1];
                }
                int cellX3 = cellX2;
                float f2 = yThreshold;
                int cellX4 = cellX3;
                int i4 = hSpanInc;
                CellLayout.LayoutParams lp3 = lp2;
                if (this.mCellLayout.createAreaForResize(cellX3, cellY3, spanX2, spanY3, this.mWidgetView, this.mDirectionVector, onDismiss)) {
                    if (!(this.mStateAnnouncer == null || (lp3.cellHSpan == spanX2 && lp3.cellVSpan == spanY3))) {
                        this.mStateAnnouncer.announce(this.mLauncher.getString(R.string.widget_resized, new Object[]{Integer.valueOf(spanX2), Integer.valueOf(spanY3)}));
                    }
                    lp3.tmpCellX = cellX4;
                    lp3.tmpCellY = cellY3;
                    lp3.cellHSpan = spanX2;
                    lp3.cellVSpan = spanY3;
                    this.mRunningVInc += vSpanDelta;
                    this.mRunningHInc += hSpanDelta;
                    if (!onDismiss) {
                        updateWidgetSizeRanges(this.mWidgetView, this.mLauncher, spanX2, spanY3);
                    }
                }
                this.mWidgetView.requestLayout();
            }
        }
    }

    static void updateWidgetSizeRanges(AppWidgetHostView widgetView, Launcher launcher, int spanX, int spanY) {
        getWidgetSizeRanges(launcher, spanX, spanY, sTmpRect);
        widgetView.updateAppWidgetSize((Bundle) null, sTmpRect.left, sTmpRect.top, sTmpRect.right, sTmpRect.bottom);
    }

    public static Rect getWidgetSizeRanges(Context context, int spanX, int spanY, Rect rect) {
        if (sCellSize == null) {
            InvariantDeviceProfile inv = LauncherAppState.getIDP(context);
            sCellSize = new Point[2];
            sCellSize[0] = inv.landscapeProfile.getCellSize();
            sCellSize[1] = inv.portraitProfile.getCellSize();
        }
        if (rect == null) {
            rect = new Rect();
        }
        float density = context.getResources().getDisplayMetrics().density;
        int landHeight = (int) (((float) (sCellSize[0].y * spanY)) / density);
        rect.set((int) (((float) (sCellSize[1].x * spanX)) / density), landHeight, (int) (((float) (sCellSize[0].x * spanX)) / density), (int) (((float) (sCellSize[1].y * spanY)) / density));
        return rect;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resizeWidgetIfNeeded(true);
    }

    private void onTouchUp() {
        int xThreshold = this.mCellLayout.getCellWidth();
        int yThreshold = this.mCellLayout.getCellHeight();
        this.mDeltaXAddOn = this.mRunningHInc * xThreshold;
        this.mDeltaYAddOn = this.mRunningVInc * yThreshold;
        this.mDeltaX = 0;
        this.mDeltaY = 0;
        post(new Runnable() {
            public void run() {
                AppWidgetResizeFrame.this.snapToWidget(true);
            }
        });
    }

    private void getSnappedRectRelativeToDragLayer(Rect out) {
        float scale = this.mWidgetView.getScaleToFit();
        this.mDragLayer.getViewRectRelativeToSelf(this.mWidgetView, out);
        int width = (this.mBackgroundPadding * 2) + ((int) (((float) ((out.width() - this.mWidgetPadding.left) - this.mWidgetPadding.right)) * scale));
        int height = (this.mBackgroundPadding * 2) + ((int) (((float) ((out.height() - this.mWidgetPadding.top) - this.mWidgetPadding.bottom)) * scale));
        out.left = (int) (((float) (out.left - this.mBackgroundPadding)) + (((float) this.mWidgetPadding.left) * scale));
        out.top = (int) (((float) (out.top - this.mBackgroundPadding)) + (((float) this.mWidgetPadding.top) * scale));
        out.right = out.left + width;
        out.bottom = out.top + height;
    }

    /* access modifiers changed from: private */
    public void snapToWidget(boolean animate) {
        getSnappedRectRelativeToDragLayer(sTmpRect);
        int newWidth = sTmpRect.width();
        int newHeight = sTmpRect.height();
        int newX = sTmpRect.left;
        int newY = sTmpRect.top;
        if (newY < 0) {
            this.mTopTouchRegionAdjustment = -newY;
        } else {
            this.mTopTouchRegionAdjustment = 0;
        }
        if (newY + newHeight > this.mDragLayer.getHeight()) {
            this.mBottomTouchRegionAdjustment = -((newY + newHeight) - this.mDragLayer.getHeight());
        } else {
            this.mBottomTouchRegionAdjustment = 0;
        }
        BaseDragLayer.LayoutParams lp = (BaseDragLayer.LayoutParams) getLayoutParams();
        int i = 4;
        if (!animate) {
            lp.width = newWidth;
            lp.height = newHeight;
            lp.x = newX;
            lp.y = newY;
            for (int i2 = 0; i2 < 4; i2++) {
                this.mDragHandles[i2].setAlpha(1.0f);
            }
            requestLayout();
            int i3 = newWidth;
            int i4 = newHeight;
        } else {
            ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(lp, this, PropertyValuesHolder.ofInt("width", new int[]{lp.width, newWidth}), PropertyValuesHolder.ofInt("height", new int[]{lp.height, newHeight}), PropertyValuesHolder.ofInt("x", new int[]{lp.x, newX}), PropertyValuesHolder.ofInt("y", new int[]{lp.y, newY}));
            oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    AppWidgetResizeFrame.this.requestLayout();
                }
            });
            AnimatorSet set = LauncherAnimUtils.createAnimatorSet();
            set.play(oa);
            int i5 = 0;
            while (true) {
                int i6 = i5;
                if (i6 >= i) {
                    break;
                }
                int newWidth2 = newWidth;
                int newHeight2 = newHeight;
                set.play(LauncherAnimUtils.ofFloat(this.mDragHandles[i6], ALPHA, 1.0f));
                i5 = i6 + 1;
                newWidth = newWidth2;
                newHeight = newHeight2;
                i = 4;
            }
            int i7 = newHeight;
            set.setDuration(150);
            set.start();
        }
        setFocusableInTouchMode(true);
        requestFocus();
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (!FocusLogic.shouldConsume(keyCode)) {
            return false;
        }
        close(false);
        this.mWidgetView.requestFocus();
        return true;
    }

    private boolean handleTouchDown(MotionEvent ev) {
        Rect hitRect = new Rect();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        getHitRect(hitRect);
        if (!hitRect.contains(x, y) || !beginResizeIfPointInRegion(x - getLeft(), y - getTop())) {
            return false;
        }
        this.mXDown = x;
        this.mYDown = y;
        return true;
    }

    public boolean onControllerTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action) {
            case 0:
                return handleTouchDown(ev);
            case 1:
            case 3:
                visualizeResizeForDelta(x - this.mXDown, y - this.mYDown);
                onTouchUp();
                this.mYDown = 0;
                this.mXDown = 0;
                return true;
            case 2:
                visualizeResizeForDelta(x - this.mXDown, y - this.mYDown);
                return true;
            default:
                return true;
        }
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && handleTouchDown(ev)) {
            return true;
        }
        close(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        this.mDragLayer.removeView(this);
    }

    public void logActionCommand(int command) {
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 8) != 0;
    }

    private static class IntRange {
        public int end;
        public int start;

        private IntRange() {
        }

        public int clamp(int value) {
            return Utilities.boundToRange(value, this.start, this.end);
        }

        public void set(int s, int e) {
            this.start = s;
            this.end = e;
        }

        public int size() {
            return this.end - this.start;
        }

        public void applyDelta(boolean moveStart, boolean moveEnd, int delta, IntRange out) {
            out.start = moveStart ? this.start + delta : this.start;
            out.end = moveEnd ? this.end + delta : this.end;
        }

        public int applyDeltaAndBound(boolean moveStart, boolean moveEnd, int delta, int minSize, int maxEnd, IntRange out) {
            int size;
            int size2;
            applyDelta(moveStart, moveEnd, delta, out);
            if (out.start < 0) {
                out.start = 0;
            }
            if (out.end > maxEnd) {
                out.end = maxEnd;
            }
            if (out.size() < minSize) {
                if (moveStart) {
                    out.start = out.end - minSize;
                } else if (moveEnd) {
                    out.end = out.start + minSize;
                }
            }
            if (moveEnd) {
                size = out.size();
                size2 = size();
            } else {
                size = size();
                size2 = out.size();
            }
            return size - size2;
        }
    }
}
