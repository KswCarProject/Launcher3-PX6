package com.android.launcher3.dragndrop;

import android.graphics.Point;
import com.android.launcher3.DropTarget;

public class DragOptions {
    public float intrinsicIconScaleFactor = 1.0f;
    public boolean isAccessibleDrag = false;
    public PreDragCondition preDragCondition = null;
    public Point systemDndStartPoint = null;

    public interface PreDragCondition {
        void onPreDragEnd(DropTarget.DragObject dragObject, boolean z);

        void onPreDragStart(DropTarget.DragObject dragObject);

        boolean shouldStartDrag(double d);
    }
}
