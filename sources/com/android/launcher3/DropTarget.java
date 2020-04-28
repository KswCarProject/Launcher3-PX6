package com.android.launcher3;

import android.graphics.Rect;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;

public interface DropTarget {
    boolean acceptDrop(DragObject dragObject);

    void getHitRectRelativeToDragLayer(Rect rect);

    boolean isDropEnabled();

    void onDragEnter(DragObject dragObject);

    void onDragExit(DragObject dragObject);

    void onDragOver(DragObject dragObject);

    void onDrop(DragObject dragObject, DragOptions dragOptions);

    void prepareAccessibilityDrop();

    public static class DragObject {
        public boolean accessibleDrag;
        public boolean cancelled = false;
        public boolean deferDragViewCleanupPostAnimation = true;
        public boolean dragComplete = false;
        public ItemInfo dragInfo = null;
        public DragSource dragSource = null;
        public DragView dragView = null;
        public ItemInfo originalDragInfo = null;
        public DragViewStateAnnouncer stateAnnouncer;
        public int x = -1;
        public int xOffset = -1;
        public int y = -1;
        public int yOffset = -1;

        public final float[] getVisualCenter(float[] recycle) {
            float[] res = recycle == null ? new float[2] : recycle;
            res[0] = (float) ((this.dragView.getDragRegion().width() / 2) + (this.x - this.xOffset));
            res[1] = (float) ((this.dragView.getDragRegion().height() / 2) + (this.y - this.yOffset));
            return res;
        }
    }
}
