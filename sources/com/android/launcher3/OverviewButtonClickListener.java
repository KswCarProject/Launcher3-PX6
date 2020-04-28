package com.android.launcher3;

import android.view.View;

public abstract class OverviewButtonClickListener implements View.OnClickListener, View.OnLongClickListener {
    private int mControlType;

    public abstract void handleViewClick(View view);

    public OverviewButtonClickListener(int controlType) {
        this.mControlType = controlType;
    }

    public void attachTo(View v) {
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    public void onClick(View view) {
        if (shouldPerformClick(view)) {
            handleViewClick(view, 0);
        }
    }

    public boolean onLongClick(View view) {
        if (shouldPerformClick(view)) {
            handleViewClick(view, 1);
        }
        return true;
    }

    private boolean shouldPerformClick(View view) {
        return !Launcher.getLauncher(view.getContext()).getWorkspace().isSwitchingState();
    }

    private void handleViewClick(View view, int action) {
        handleViewClick(view);
        Launcher.getLauncher(view.getContext()).getUserEventDispatcher().logActionOnControl(action, this.mControlType);
    }
}
