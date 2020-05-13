package com.szchoiceway.index;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.szchoiceway.index.DragController;
import com.szchoiceway.index.DropTarget;

public class ButtonDropTarget extends TextView implements DropTarget, DragController.DragListener {
    protected boolean mActive;
    private int mBottomDragPadding;
    protected int mHoverColor;
    protected Launcher mLauncher;
    protected SearchDropTargetBar mSearchDropTargetBar;
    protected TextView mText;
    protected final int mTransitionDuration;

    public ButtonDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHoverColor = 0;
        Resources r = getResources();
        this.mTransitionDuration = r.getInteger(R.integer.config_dropTargetBgTransitionDuration);
        this.mBottomDragPadding = r.getDimensionPixelSize(R.dimen.drop_target_drag_padding);
    }

    /* access modifiers changed from: package-private */
    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        return false;
    }

    public void setSearchDropTargetBar(SearchDropTargetBar searchDropTargetBar) {
        this.mSearchDropTargetBar = searchDropTargetBar;
    }

    /* access modifiers changed from: protected */
    public Drawable getCurrentDrawable() {
        Drawable[] drawables = getCompoundDrawablesRelative();
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                return drawables[i];
            }
        }
        return null;
    }

    public void onDrop(DropTarget.DragObject d) {
    }

    public void onFlingToDelete(DropTarget.DragObject d, int x, int y, PointF vec) {
    }

    public void onDragEnter(DropTarget.DragObject d) {
        d.dragView.setColor(this.mHoverColor);
    }

    public void onDragOver(DropTarget.DragObject d) {
    }

    public void onDragExit(DropTarget.DragObject d) {
        d.dragView.setColor(0);
    }

    public void onDragStart(DragSource source, Object info, int dragAction) {
    }

    public boolean isDropEnabled() {
        return this.mActive;
    }

    public void onDragEnd() {
    }

    public void getHitRect(Rect outRect) {
        super.getHitRect(outRect);
        outRect.bottom += this.mBottomDragPadding;
    }

    private boolean isRtl() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: package-private */
    public Rect getIconRect(int viewWidth, int viewHeight, int drawableWidth, int drawableHeight) {
        int left;
        int right;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect to = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, to);
        int width = drawableWidth;
        int height = drawableHeight;
        if (isRtl()) {
            right = to.right - getPaddingRight();
            left = right - width;
        } else {
            left = to.left + getPaddingLeft();
            right = left + width;
        }
        int top = to.top + ((getMeasuredHeight() - height) / 2);
        to.set(left, top, right, top + height);
        to.offset((-(viewWidth - width)) / 2, (-(viewHeight - height)) / 2);
        return to;
    }

    public DropTarget getDropTargetDelegate(DropTarget.DragObject d) {
        return null;
    }

    public void getLocationInDragLayer(int[] loc) {
        this.mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }
}
