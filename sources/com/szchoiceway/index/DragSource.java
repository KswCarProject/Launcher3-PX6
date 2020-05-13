package com.szchoiceway.index;

import android.view.View;
import com.szchoiceway.index.DropTarget;

public interface DragSource {
    void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z, boolean z2);

    void onFlingToDeleteCompleted();

    boolean supportsFlingToDelete();
}
