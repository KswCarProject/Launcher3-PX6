package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.anim.AlphaUpdateListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;

public class DropTargetBar extends FrameLayout implements DragController.DragListener, Insettable {
    protected static final int DEFAULT_DRAG_FADE_DURATION = 175;
    protected static final TimeInterpolator DEFAULT_INTERPOLATOR = Interpolators.ACCEL;
    private ViewPropertyAnimator mCurrentAnimation;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected boolean mDeferOnDragEnd;
    private ButtonDropTarget[] mDropTargets;
    private final Runnable mFadeAnimationEndRunnable = new Runnable() {
        public final void run() {
            AlphaUpdateListener.updateVisibility(DropTargetBar.this);
        }
    };
    private boolean mIsVertical = true;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected boolean mVisible = false;

    public DropTargetBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropTargetBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDropTargets = new ButtonDropTarget[getChildCount()];
        for (int i = 0; i < this.mDropTargets.length; i++) {
            this.mDropTargets[i] = (ButtonDropTarget) getChildAt(i);
            this.mDropTargets[i].setDropTargetBar(this);
        }
    }

    public void setInsets(Rect insets) {
        int gap;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile grid = Launcher.getLauncher(getContext()).getDeviceProfile();
        this.mIsVertical = grid.isVerticalBarLayout();
        lp.leftMargin = insets.left;
        lp.topMargin = insets.top;
        lp.bottomMargin = insets.bottom;
        lp.rightMargin = insets.right;
        int tooltipLocation = 0;
        int i = 1;
        if (grid.isVerticalBarLayout()) {
            lp.width = grid.dropTargetBarSizePx;
            lp.height = grid.availableHeightPx - (grid.edgeMarginPx * 2);
            lp.gravity = grid.isSeascape() ? 5 : 3;
            if (!grid.isSeascape()) {
                i = 2;
            }
            tooltipLocation = i;
        } else {
            if (grid.isTablet) {
                gap = (((grid.widthPx - (grid.edgeMarginPx * 2)) - (grid.inv.numColumns * grid.cellWidthPx)) / ((grid.inv.numColumns + 1) * 2)) + grid.edgeMarginPx;
            } else {
                gap = grid.desiredWorkspaceLeftRightMarginPx - grid.defaultWidgetPadding.right;
            }
            lp.width = grid.availableWidthPx - (gap * 2);
            lp.topMargin += grid.edgeMarginPx;
            lp.height = grid.dropTargetBarSizePx;
            lp.gravity = 49;
        }
        setLayoutParams(lp);
        for (ButtonDropTarget button : this.mDropTargets) {
            button.setToolTipLocation(tooltipLocation);
        }
    }

    public void setup(DragController dragController) {
        dragController.addDragListener(this);
        for (int i = 0; i < this.mDropTargets.length; i++) {
            dragController.addDragListener(this.mDropTargets[i]);
            dragController.addDropTarget(this.mDropTargets[i]);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.mIsVertical) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
            for (ButtonDropTarget button : this.mDropTargets) {
                if (button.getVisibility() != 8) {
                    button.setTextVisible(false);
                    button.measure(widthSpec, heightSpec);
                }
            }
        } else {
            int availableWidth = width / getVisibleButtonsCount();
            boolean textVisible = true;
            for (ButtonDropTarget buttons : this.mDropTargets) {
                if (buttons.getVisibility() != 8) {
                    textVisible = textVisible && !buttons.isTextTruncated(availableWidth);
                }
            }
            int widthSpec2 = View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE);
            int heightSpec2 = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
            for (ButtonDropTarget button2 : this.mDropTargets) {
                if (button2.getVisibility() != 8) {
                    button2.setTextVisible(textVisible);
                    button2.measure(widthSpec2, heightSpec2);
                }
            }
        }
        setMeasuredDimension(width, height);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.mIsVertical) {
            int gap = getResources().getDimensionPixelSize(R.dimen.drop_target_vertical_gap);
            int start = gap;
            for (ButtonDropTarget button : this.mDropTargets) {
                if (button.getVisibility() != 8) {
                    int end = button.getMeasuredHeight() + start;
                    button.layout(0, start, button.getMeasuredWidth(), end);
                    start = end + gap;
                }
            }
            return;
        }
        int frameSize = (right - left) / getVisibleButtonsCount();
        int start2 = frameSize / 2;
        for (ButtonDropTarget button2 : this.mDropTargets) {
            if (button2.getVisibility() != 8) {
                int halfWidth = button2.getMeasuredWidth() / 2;
                button2.layout(start2 - halfWidth, 0, start2 + halfWidth, button2.getMeasuredHeight());
                start2 += frameSize;
            }
        }
    }

    private int getVisibleButtonsCount() {
        int visibleCount = 0;
        for (ButtonDropTarget buttons : this.mDropTargets) {
            if (buttons.getVisibility() != 8) {
                visibleCount++;
            }
        }
        return visibleCount;
    }

    private void animateToVisibility(boolean isVisible) {
        if (this.mVisible != isVisible) {
            this.mVisible = isVisible;
            if (this.mCurrentAnimation != null) {
                this.mCurrentAnimation.cancel();
                this.mCurrentAnimation = null;
            }
            float finalAlpha = this.mVisible ? 1.0f : 0.0f;
            if (Float.compare(getAlpha(), finalAlpha) != 0) {
                setVisibility(0);
                this.mCurrentAnimation = animate().alpha(finalAlpha).setInterpolator(DEFAULT_INTERPOLATOR).setDuration(175).withEndAction(this.mFadeAnimationEndRunnable);
            }
        }
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        animateToVisibility(true);
    }

    /* access modifiers changed from: protected */
    public void deferOnDragEnd() {
        this.mDeferOnDragEnd = true;
    }

    public void onDragEnd() {
        if (!this.mDeferOnDragEnd) {
            animateToVisibility(false);
        } else {
            this.mDeferOnDragEnd = false;
        }
    }

    public ButtonDropTarget[] getDropTargets() {
        return this.mDropTargets;
    }
}
