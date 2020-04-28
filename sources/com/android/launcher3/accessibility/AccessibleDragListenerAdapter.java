package com.android.launcher3.accessibility;

import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;

public class AccessibleDragListenerAdapter implements DragController.DragListener {
    private final int mDragType;
    private final ViewGroup mViewGroup;

    public AccessibleDragListenerAdapter(ViewGroup parent, int dragType) {
        this.mViewGroup = parent;
        this.mDragType = dragType;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        enableAccessibleDrag(true);
    }

    public void onDragEnd() {
        enableAccessibleDrag(false);
        Launcher.getLauncher(this.mViewGroup.getContext()).getDragController().removeDragListener(this);
    }

    /* access modifiers changed from: protected */
    public void enableAccessibleDrag(boolean enable) {
        for (int i = 0; i < this.mViewGroup.getChildCount(); i++) {
            setEnableForLayout((CellLayout) this.mViewGroup.getChildAt(i), enable);
        }
    }

    /* access modifiers changed from: protected */
    public final void setEnableForLayout(CellLayout layout, boolean enable) {
        layout.enableAccessibleDrag(enable, this.mDragType);
    }
}
