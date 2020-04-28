package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import java.util.List;

public abstract class DragAndDropAccessibilityDelegate extends ExploreByTouchHelper implements View.OnClickListener {
    protected static final int INVALID_POSITION = -1;
    private static final int[] sTempArray = new int[2];
    protected final Context mContext;
    protected final LauncherAccessibilityDelegate mDelegate;
    private final Rect mTempRect = new Rect();
    protected final CellLayout mView;

    /* access modifiers changed from: protected */
    public abstract String getConfirmationForIconDrop(int i);

    /* access modifiers changed from: protected */
    public abstract String getLocationDescriptionForIconDrop(int i);

    /* access modifiers changed from: protected */
    public abstract int intersectsValidDropTarget(int i);

    public DragAndDropAccessibilityDelegate(CellLayout forView) {
        super(forView);
        this.mView = forView;
        this.mContext = this.mView.getContext();
        this.mDelegate = Launcher.getLauncher(this.mContext).getAccessibilityDelegate();
    }

    /* access modifiers changed from: protected */
    public int getVirtualViewAt(float x, float y) {
        if (x < 0.0f || y < 0.0f || x > ((float) this.mView.getMeasuredWidth()) || y > ((float) this.mView.getMeasuredHeight())) {
            return Integer.MIN_VALUE;
        }
        this.mView.pointToCellExact((int) x, (int) y, sTempArray);
        return intersectsValidDropTarget(sTempArray[0] + (sTempArray[1] * this.mView.getCountX()));
    }

    /* access modifiers changed from: protected */
    public void getVisibleVirtualViews(List<Integer> virtualViews) {
        int nCells = this.mView.getCountX() * this.mView.getCountY();
        for (int i = 0; i < nCells; i++) {
            if (intersectsValidDropTarget(i) == i) {
                virtualViews.add(Integer.valueOf(i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onPerformActionForVirtualView(int viewId, int action, Bundle args) {
        if (action != 16 || viewId == Integer.MIN_VALUE) {
            return false;
        }
        this.mDelegate.handleAccessibleDrop(this.mView, getItemBounds(viewId), getConfirmationForIconDrop(viewId));
        return true;
    }

    public void onClick(View v) {
        onPerformActionForVirtualView(getFocusedVirtualView(), 16, (Bundle) null);
    }

    /* access modifiers changed from: protected */
    public void onPopulateEventForVirtualView(int id, AccessibilityEvent event) {
        if (id != Integer.MIN_VALUE) {
            event.setContentDescription(this.mContext.getString(R.string.action_move_here));
            return;
        }
        throw new IllegalArgumentException("Invalid virtual view id");
    }

    /* access modifiers changed from: protected */
    public void onPopulateNodeForVirtualView(int id, AccessibilityNodeInfoCompat node) {
        if (id != Integer.MIN_VALUE) {
            node.setContentDescription(getLocationDescriptionForIconDrop(id));
            node.setBoundsInParent(getItemBounds(id));
            node.addAction(16);
            node.setClickable(true);
            node.setFocusable(true);
            return;
        }
        throw new IllegalArgumentException("Invalid virtual view id");
    }

    private Rect getItemBounds(int id) {
        int cellY = id / this.mView.getCountX();
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        this.mView.cellToRect(id % this.mView.getCountX(), cellY, dragInfo.info.spanX, dragInfo.info.spanY, this.mTempRect);
        return this.mTempRect;
    }
}
