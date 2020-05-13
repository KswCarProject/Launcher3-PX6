package com.szchoiceway.index;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import com.szchoiceway.index.DragController;

public interface DropTarget {
    public static final String TAG = "DropTarget";

    public static class DragObject {
        public boolean cancelled = false;
        public boolean deferDragViewCleanupPostAnimation = true;
        public boolean dragComplete = false;
        public Object dragInfo = null;
        public DragSource dragSource = null;
        public DragView dragView = null;
        public Runnable postAnimationRunnable = null;
        public int x = -1;
        public int xOffset = -1;
        public int y = -1;
        public int yOffset = -1;
    }

    boolean acceptDrop(DragObject dragObject);

    DropTarget getDropTargetDelegate(DragObject dragObject);

    void getHitRect(Rect rect);

    int getLeft();

    void getLocationInDragLayer(int[] iArr);

    int getTop();

    boolean isDropEnabled();

    void onDragEnter(DragObject dragObject);

    void onDragExit(DragObject dragObject);

    void onDragOver(DragObject dragObject);

    void onDrop(DragObject dragObject);

    void onFlingToDelete(DragObject dragObject, int i, int i2, PointF pointF);

    public static class DragEnforcer implements DragController.DragListener {
        int dragParity = 0;

        public DragEnforcer(Context context) {
            ((Launcher) context).getDragController().addDragListener(this);
        }

        /* access modifiers changed from: package-private */
        public void onDragEnter() {
            this.dragParity++;
            if (this.dragParity != 1) {
                Log.e(DropTarget.TAG, "onDragEnter: Drag contract violated: " + this.dragParity);
            }
        }

        /* access modifiers changed from: package-private */
        public void onDragExit() {
            this.dragParity--;
            if (this.dragParity != 0) {
                Log.e(DropTarget.TAG, "onDragExit: Drag contract violated: " + this.dragParity);
            }
        }

        public void onDragStart(DragSource source, Object info, int dragAction) {
            if (this.dragParity != 0) {
                Log.e(DropTarget.TAG, "onDragEnter: Drag contract violated: " + this.dragParity);
            }
        }

        public void onDragEnd() {
            if (this.dragParity != 0) {
                Log.e(DropTarget.TAG, "onDragExit: Drag contract violated: " + this.dragParity);
            }
        }
    }
}
