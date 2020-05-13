package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.szchoiceway.index.CellLayout;
import com.szchoiceway.index.DragLayer;

public class AppWidgetResizeFrame extends FrameLayout {
    public static final int BOTTOM = 3;
    public static final int LEFT = 0;
    public static final int RIGHT = 2;
    public static final int TOP = 1;
    private static Rect mTmpRect = new Rect();
    final int BACKGROUND_PADDING = 24;
    final float DIMMED_HANDLE_ALPHA = 0.0f;
    final float RESIZE_THRESHOLD = 0.66f;
    final int SNAP_DURATION = 150;
    private int mBackgroundPadding;
    private int mBaselineHeight;
    private int mBaselineWidth;
    private int mBaselineX;
    private int mBaselineY;
    private boolean mBottomBorderActive;
    private ImageView mBottomHandle;
    private int mBottomTouchRegionAdjustment = 0;
    private CellLayout mCellLayout;
    private int mDeltaX;
    private int mDeltaXAddOn;
    private int mDeltaY;
    private int mDeltaYAddOn;
    int[] mDirectionVector = new int[2];
    private DragLayer mDragLayer;
    int[] mLastDirectionVector = new int[2];
    private Launcher mLauncher;
    private boolean mLeftBorderActive;
    private ImageView mLeftHandle;
    private int mMinHSpan;
    private int mMinVSpan;
    private int mResizeMode;
    private boolean mRightBorderActive;
    private ImageView mRightHandle;
    private int mRunningHInc;
    private int mRunningVInc;
    private boolean mTopBorderActive;
    private ImageView mTopHandle;
    private int mTopTouchRegionAdjustment = 0;
    private int mTouchTargetWidth;
    private int mWidgetPaddingBottom;
    private int mWidgetPaddingLeft;
    private int mWidgetPaddingRight;
    private int mWidgetPaddingTop;
    private LauncherAppWidgetHostView mWidgetView;
    private Workspace mWorkspace;

    public AppWidgetResizeFrame(Context context, LauncherAppWidgetHostView widgetView, CellLayout cellLayout, DragLayer dragLayer) {
        super(context);
        this.mLauncher = (Launcher) context;
        this.mCellLayout = cellLayout;
        this.mWidgetView = widgetView;
        this.mResizeMode = widgetView.getAppWidgetInfo().resizeMode;
        this.mDragLayer = dragLayer;
        this.mWorkspace = (Workspace) dragLayer.findViewById(R.id.workspace);
        int[] result = Launcher.getMinSpanForWidget((Context) this.mLauncher, widgetView.getAppWidgetInfo());
        this.mMinHSpan = result[0];
        this.mMinVSpan = result[1];
        setBackgroundResource(R.drawable.widget_resize_frame_holo);
        setPadding(0, 0, 0, 0);
        this.mLeftHandle = new ImageView(context);
        this.mLeftHandle.setImageResource(R.drawable.widget_resize_handle_left);
        addView(this.mLeftHandle, new FrameLayout.LayoutParams(-2, -2, 8388627));
        this.mRightHandle = new ImageView(context);
        this.mRightHandle.setImageResource(R.drawable.widget_resize_handle_right);
        addView(this.mRightHandle, new FrameLayout.LayoutParams(-2, -2, 8388629));
        this.mTopHandle = new ImageView(context);
        this.mTopHandle.setImageResource(R.drawable.widget_resize_handle_top);
        addView(this.mTopHandle, new FrameLayout.LayoutParams(-2, -2, 49));
        this.mBottomHandle = new ImageView(context);
        this.mBottomHandle.setImageResource(R.drawable.widget_resize_handle_bottom);
        addView(this.mBottomHandle, new FrameLayout.LayoutParams(-2, -2, 81));
        Rect p = AppWidgetHostView.getDefaultPaddingForWidget(context, widgetView.getAppWidgetInfo().provider, (Rect) null);
        this.mWidgetPaddingLeft = p.left;
        this.mWidgetPaddingTop = p.top;
        this.mWidgetPaddingRight = p.right;
        this.mWidgetPaddingBottom = p.bottom;
        if (this.mResizeMode == 1) {
            this.mTopHandle.setVisibility(8);
            this.mBottomHandle.setVisibility(8);
        } else if (this.mResizeMode == 2) {
            this.mLeftHandle.setVisibility(8);
            this.mRightHandle.setVisibility(8);
        }
        this.mBackgroundPadding = (int) Math.ceil((double) (24.0f * this.mLauncher.getResources().getDisplayMetrics().density));
        this.mTouchTargetWidth = this.mBackgroundPadding * 2;
        this.mCellLayout.markCellsAsUnoccupiedForView(this.mWidgetView);
    }

    public boolean beginResizeIfPointInRegion(int x, int y) {
        boolean horizontalActive;
        boolean verticalActive;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean anyBordersActive;
        float f;
        float f2;
        float f3;
        float f4 = 1.0f;
        if ((this.mResizeMode & 1) != 0) {
            horizontalActive = true;
        } else {
            horizontalActive = false;
        }
        if ((this.mResizeMode & 2) != 0) {
            verticalActive = true;
        } else {
            verticalActive = false;
        }
        if (x >= this.mTouchTargetWidth || !horizontalActive) {
            z = false;
        } else {
            z = true;
        }
        this.mLeftBorderActive = z;
        if (x <= getWidth() - this.mTouchTargetWidth || !horizontalActive) {
            z2 = false;
        } else {
            z2 = true;
        }
        this.mRightBorderActive = z2;
        if (y >= this.mTouchTargetWidth + this.mTopTouchRegionAdjustment || !verticalActive) {
            z3 = false;
        } else {
            z3 = true;
        }
        this.mTopBorderActive = z3;
        if (y <= (getHeight() - this.mTouchTargetWidth) + this.mBottomTouchRegionAdjustment || !verticalActive) {
            z4 = false;
        } else {
            z4 = true;
        }
        this.mBottomBorderActive = z4;
        if (this.mLeftBorderActive || this.mRightBorderActive || this.mTopBorderActive || this.mBottomBorderActive) {
            anyBordersActive = true;
        } else {
            anyBordersActive = false;
        }
        this.mBaselineWidth = getMeasuredWidth();
        this.mBaselineHeight = getMeasuredHeight();
        this.mBaselineX = getLeft();
        this.mBaselineY = getTop();
        if (anyBordersActive) {
            ImageView imageView = this.mLeftHandle;
            if (this.mLeftBorderActive) {
                f = 1.0f;
            } else {
                f = 0.0f;
            }
            imageView.setAlpha(f);
            ImageView imageView2 = this.mRightHandle;
            if (this.mRightBorderActive) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            imageView2.setAlpha(f2);
            ImageView imageView3 = this.mTopHandle;
            if (this.mTopBorderActive) {
                f3 = 1.0f;
            } else {
                f3 = 0.0f;
            }
            imageView3.setAlpha(f3);
            ImageView imageView4 = this.mBottomHandle;
            if (!this.mBottomBorderActive) {
                f4 = 0.0f;
            }
            imageView4.setAlpha(f4);
        }
        return anyBordersActive;
    }

    public void updateDeltas(int deltaX, int deltaY) {
        if (this.mLeftBorderActive) {
            this.mDeltaX = Math.max(-this.mBaselineX, deltaX);
            this.mDeltaX = Math.min(this.mBaselineWidth - (this.mTouchTargetWidth * 2), this.mDeltaX);
        } else if (this.mRightBorderActive) {
            this.mDeltaX = Math.min(this.mDragLayer.getWidth() - (this.mBaselineX + this.mBaselineWidth), deltaX);
            this.mDeltaX = Math.max((-this.mBaselineWidth) + (this.mTouchTargetWidth * 2), this.mDeltaX);
        }
        if (this.mTopBorderActive) {
            this.mDeltaY = Math.max(-this.mBaselineY, deltaY);
            this.mDeltaY = Math.min(this.mBaselineHeight - (this.mTouchTargetWidth * 2), this.mDeltaY);
        } else if (this.mBottomBorderActive) {
            this.mDeltaY = Math.min(this.mDragLayer.getHeight() - (this.mBaselineY + this.mBaselineHeight), deltaY);
            this.mDeltaY = Math.max((-this.mBaselineHeight) + (this.mTouchTargetWidth * 2), this.mDeltaY);
        }
    }

    public void visualizeResizeForDelta(int deltaX, int deltaY) {
        visualizeResizeForDelta(deltaX, deltaY, false);
    }

    private void visualizeResizeForDelta(int deltaX, int deltaY, boolean onDismiss) {
        updateDeltas(deltaX, deltaY);
        DragLayer.LayoutParams lp = (DragLayer.LayoutParams) getLayoutParams();
        if (this.mLeftBorderActive) {
            lp.x = this.mBaselineX + this.mDeltaX;
            lp.width = this.mBaselineWidth - this.mDeltaX;
        } else if (this.mRightBorderActive) {
            lp.width = this.mBaselineWidth + this.mDeltaX;
        }
        if (this.mTopBorderActive) {
            lp.y = this.mBaselineY + this.mDeltaY;
            lp.height = this.mBaselineHeight - this.mDeltaY;
        } else if (this.mBottomBorderActive) {
            lp.height = this.mBaselineHeight + this.mDeltaY;
        }
        resizeWidgetIfNeeded(onDismiss);
        requestLayout();
    }

    private void resizeWidgetIfNeeded(boolean onDismiss) {
        int cellY;
        int xThreshold = this.mCellLayout.getCellWidth() + this.mCellLayout.getWidthGap();
        int yThreshold = this.mCellLayout.getCellHeight() + this.mCellLayout.getHeightGap();
        int deltaX = this.mDeltaX + this.mDeltaXAddOn;
        int deltaY = this.mDeltaY + this.mDeltaYAddOn;
        float hSpanIncF = ((1.0f * ((float) deltaX)) / ((float) xThreshold)) - ((float) this.mRunningHInc);
        float vSpanIncF = ((1.0f * ((float) deltaY)) / ((float) yThreshold)) - ((float) this.mRunningVInc);
        int hSpanInc = 0;
        int vSpanInc = 0;
        int cellXInc = 0;
        int cellYInc = 0;
        int countX = this.mCellLayout.getCountX();
        int countY = this.mCellLayout.getCountY();
        if (Math.abs(hSpanIncF) > 0.66f) {
            hSpanInc = Math.round(hSpanIncF);
        }
        if (Math.abs(vSpanIncF) > 0.66f) {
            vSpanInc = Math.round(vSpanIncF);
        }
        if (onDismiss || hSpanInc != 0 || vSpanInc != 0) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) this.mWidgetView.getLayoutParams();
            int spanX = lp.cellHSpan;
            int spanY = lp.cellVSpan;
            int cellX = lp.useTmpCoords ? lp.tmpCellX : lp.cellX;
            if (lp.useTmpCoords) {
                cellY = lp.tmpCellY;
            } else {
                cellY = lp.cellY;
            }
            int hSpanDelta = 0;
            int vSpanDelta = 0;
            if (this.mLeftBorderActive) {
                cellXInc = Math.min(lp.cellHSpan - this.mMinHSpan, Math.max(-cellX, hSpanInc));
                hSpanInc = Math.max(-(lp.cellHSpan - this.mMinHSpan), Math.min(cellX, hSpanInc * -1));
                hSpanDelta = -hSpanInc;
            } else if (this.mRightBorderActive) {
                hSpanInc = Math.max(-(lp.cellHSpan - this.mMinHSpan), Math.min(countX - (cellX + spanX), hSpanInc));
                hSpanDelta = hSpanInc;
            }
            if (this.mTopBorderActive) {
                cellYInc = Math.min(lp.cellVSpan - this.mMinVSpan, Math.max(-cellY, vSpanInc));
                vSpanInc = Math.max(-(lp.cellVSpan - this.mMinVSpan), Math.min(cellY, vSpanInc * -1));
                vSpanDelta = -vSpanInc;
            } else if (this.mBottomBorderActive) {
                vSpanInc = Math.max(-(lp.cellVSpan - this.mMinVSpan), Math.min(countY - (cellY + spanY), vSpanInc));
                vSpanDelta = vSpanInc;
            }
            this.mDirectionVector[0] = 0;
            this.mDirectionVector[1] = 0;
            if (this.mLeftBorderActive || this.mRightBorderActive) {
                spanX += hSpanInc;
                cellX += cellXInc;
                if (hSpanDelta != 0) {
                    this.mDirectionVector[0] = this.mLeftBorderActive ? -1 : 1;
                }
            }
            if (this.mTopBorderActive || this.mBottomBorderActive) {
                spanY += vSpanInc;
                cellY += cellYInc;
                if (vSpanDelta != 0) {
                    this.mDirectionVector[1] = this.mTopBorderActive ? -1 : 1;
                }
            }
            if (onDismiss || vSpanDelta != 0 || hSpanDelta != 0) {
                if (onDismiss) {
                    this.mDirectionVector[0] = this.mLastDirectionVector[0];
                    this.mDirectionVector[1] = this.mLastDirectionVector[1];
                } else {
                    this.mLastDirectionVector[0] = this.mDirectionVector[0];
                    this.mLastDirectionVector[1] = this.mDirectionVector[1];
                }
                if (this.mCellLayout.createAreaForResize(cellX, cellY, spanX, spanY, this.mWidgetView, this.mDirectionVector, onDismiss)) {
                    lp.tmpCellX = cellX;
                    lp.tmpCellY = cellY;
                    lp.cellHSpan = spanX;
                    lp.cellVSpan = spanY;
                    this.mRunningVInc += vSpanDelta;
                    this.mRunningHInc += hSpanDelta;
                    if (!onDismiss) {
                        updateWidgetSizeRanges(this.mWidgetView, this.mLauncher, spanX, spanY);
                    }
                }
                this.mWidgetView.requestLayout();
            }
        }
    }

    static void updateWidgetSizeRanges(AppWidgetHostView widgetView, Launcher launcher, int spanX, int spanY) {
        getWidgetSizeRanges(launcher, spanX, spanY, mTmpRect);
        widgetView.updateAppWidgetSize((Bundle) null, mTmpRect.left, mTmpRect.top, mTmpRect.right, mTmpRect.bottom);
    }

    static Rect getWidgetSizeRanges(Launcher launcher, int spanX, int spanY, Rect rect) {
        if (rect == null) {
            rect = new Rect();
        }
        Rect landMetrics = Workspace.getCellLayoutMetrics(launcher, 0);
        Rect portMetrics = Workspace.getCellLayoutMetrics(launcher, 1);
        float density = launcher.getResources().getDisplayMetrics().density;
        int cellWidth = landMetrics.left;
        int cellHeight = landMetrics.top;
        int landWidth = (int) (((float) ((spanX * cellWidth) + ((spanX - 1) * landMetrics.right))) / density);
        int landHeight = (int) (((float) ((spanY * cellHeight) + ((spanY - 1) * landMetrics.bottom))) / density);
        int cellWidth2 = portMetrics.left;
        int cellHeight2 = portMetrics.top;
        Rect rect2 = rect;
        rect2.set((int) (((float) ((spanX * cellWidth2) + ((spanX - 1) * portMetrics.right))) / density), landHeight, landWidth, (int) (((float) ((spanY * cellHeight2) + ((spanY - 1) * portMetrics.bottom))) / density));
        return rect;
    }

    public void commitResize() {
        resizeWidgetIfNeeded(true);
        requestLayout();
    }

    public void onTouchUp() {
        int xThreshold = this.mCellLayout.getCellWidth() + this.mCellLayout.getWidthGap();
        int yThreshold = this.mCellLayout.getCellHeight() + this.mCellLayout.getHeightGap();
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

    public void snapToWidget(boolean animate) {
        DragLayer.LayoutParams lp = (DragLayer.LayoutParams) getLayoutParams();
        int xOffset = ((this.mCellLayout.getLeft() + this.mCellLayout.getPaddingLeft()) + this.mDragLayer.getPaddingLeft()) - this.mWorkspace.getScrollX();
        int yOffset = ((this.mCellLayout.getTop() + this.mCellLayout.getPaddingTop()) + this.mDragLayer.getPaddingTop()) - this.mWorkspace.getScrollY();
        int newWidth = ((this.mWidgetView.getWidth() + (this.mBackgroundPadding * 2)) - this.mWidgetPaddingLeft) - this.mWidgetPaddingRight;
        int newHeight = ((this.mWidgetView.getHeight() + (this.mBackgroundPadding * 2)) - this.mWidgetPaddingTop) - this.mWidgetPaddingBottom;
        int newX = (this.mWidgetView.getLeft() - this.mBackgroundPadding) + xOffset + this.mWidgetPaddingLeft;
        int newY = (this.mWidgetView.getTop() - this.mBackgroundPadding) + yOffset + this.mWidgetPaddingTop;
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
        if (!animate) {
            lp.width = newWidth;
            lp.height = newHeight;
            lp.x = newX;
            lp.y = newY;
            this.mLeftHandle.setAlpha(1.0f);
            this.mRightHandle.setAlpha(1.0f);
            this.mTopHandle.setAlpha(1.0f);
            this.mBottomHandle.setAlpha(1.0f);
            requestLayout();
            return;
        }
        ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(lp, this, PropertyValuesHolder.ofInt("width", new int[]{lp.width, newWidth}), PropertyValuesHolder.ofInt("height", new int[]{lp.height, newHeight}), PropertyValuesHolder.ofInt("x", new int[]{lp.x, newX}), PropertyValuesHolder.ofInt("y", new int[]{lp.y, newY}));
        ObjectAnimator leftOa = LauncherAnimUtils.ofFloat(this.mLeftHandle, "alpha", 1.0f);
        ObjectAnimator rightOa = LauncherAnimUtils.ofFloat(this.mRightHandle, "alpha", 1.0f);
        ObjectAnimator topOa = LauncherAnimUtils.ofFloat(this.mTopHandle, "alpha", 1.0f);
        ObjectAnimator bottomOa = LauncherAnimUtils.ofFloat(this.mBottomHandle, "alpha", 1.0f);
        oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                AppWidgetResizeFrame.this.requestLayout();
            }
        });
        AnimatorSet set = LauncherAnimUtils.createAnimatorSet();
        if (this.mResizeMode == 2) {
            set.playTogether(new Animator[]{oa, topOa, bottomOa});
        } else if (this.mResizeMode == 1) {
            set.playTogether(new Animator[]{oa, leftOa, rightOa});
        } else {
            set.playTogether(new Animator[]{oa, leftOa, rightOa, topOa, bottomOa});
        }
        set.setDuration(150);
        set.start();
    }
}
