package com.android.launcher3;

import android.view.View;
import com.android.launcher3.DropTarget;
import com.android.launcher3.logging.UserEventDispatcher;

public interface DragSource extends UserEventDispatcher.LogContainerProvider {
    void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z);
}
