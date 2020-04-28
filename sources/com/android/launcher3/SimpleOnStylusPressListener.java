package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.StylusEventHelper;

public class SimpleOnStylusPressListener implements StylusEventHelper.StylusButtonListener {
    private View mView;

    public SimpleOnStylusPressListener(View view) {
        this.mView = view;
    }

    public boolean onPressed(MotionEvent event) {
        return this.mView.isLongClickable() && this.mView.performLongClick();
    }

    public boolean onReleased(MotionEvent event) {
        return false;
    }
}
