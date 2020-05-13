package com.szchoiceway.index;

import android.view.KeyEvent;
import android.view.View;

/* compiled from: FocusHelper */
class FolderKeyEventListener implements View.OnKeyListener {
    FolderKeyEventListener() {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleFolderKeyEvent(v, keyCode, event);
    }
}
