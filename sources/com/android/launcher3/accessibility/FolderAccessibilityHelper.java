package com.android.launcher3.accessibility;

import com.android.launcher3.CellLayout;
import com.android.launcher3.R;
import com.android.launcher3.folder.FolderPagedView;

public class FolderAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    private final FolderPagedView mParent;
    private final int mStartPosition;

    public FolderAccessibilityHelper(CellLayout layout) {
        super(layout);
        this.mParent = (FolderPagedView) layout.getParent();
        this.mStartPosition = layout.getCountX() * this.mParent.indexOfChild(layout) * layout.getCountY();
    }

    /* access modifiers changed from: protected */
    public int intersectsValidDropTarget(int id) {
        return Math.min(id, (this.mParent.getAllocatedContentSize() - this.mStartPosition) - 1);
    }

    /* access modifiers changed from: protected */
    public String getLocationDescriptionForIconDrop(int id) {
        return this.mContext.getString(R.string.move_to_position, new Object[]{Integer.valueOf(this.mStartPosition + id + 1)});
    }

    /* access modifiers changed from: protected */
    public String getConfirmationForIconDrop(int id) {
        return this.mContext.getString(R.string.item_moved);
    }
}
