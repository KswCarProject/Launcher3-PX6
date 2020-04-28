package com.android.launcher3.uioverrides;

import android.content.Context;
import android.view.OrientationEventListener;

public class DisplayRotationListener extends OrientationEventListener {
    private final Runnable mCallback;

    public DisplayRotationListener(Context context, Runnable callback) {
        super(context);
        this.mCallback = callback;
    }

    public void onOrientationChanged(int i) {
        this.mCallback.run();
    }
}
