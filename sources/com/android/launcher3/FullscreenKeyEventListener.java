package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;

/* compiled from: FocusHelper */
class FullscreenKeyEventListener implements View.OnKeyListener {
    FullscreenKeyEventListener() {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == 21 || keyCode == 22 || keyCode == 93 || keyCode == 92) {
            return FocusHelper.handleIconKeyEvent(v, keyCode, event);
        }
        return false;
    }
}
