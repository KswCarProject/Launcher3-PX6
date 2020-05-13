package com.szchoiceway.index;

import android.view.KeyEvent;
import android.view.View;

/* compiled from: FocusHelper */
class HotseatIconKeyEventListener implements View.OnKeyListener {
    HotseatIconKeyEventListener() {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleHotseatButtonKeyEvent(v, keyCode, event, v.getResources().getConfiguration().orientation);
    }
}
