package com.szchoiceway.index;

import android.view.KeyEvent;
import android.view.View;

/* compiled from: FocusHelper */
class IconKeyEventListener implements View.OnKeyListener {
    IconKeyEventListener() {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleIconKeyEvent(v, keyCode, event);
    }
}
