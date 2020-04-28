package com.android.launcher3.views;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ButtonPreference extends Preference {
    private boolean mWidgetFrameVisible = false;

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonPreference(Context context) {
        super(context);
    }

    public void setWidgetFrameVisible(boolean isVisible) {
        if (this.mWidgetFrameVisible != isVisible) {
            this.mWidgetFrameVisible = isVisible;
            notifyChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        ViewGroup widgetFrame = (ViewGroup) view.findViewById(16908312);
        if (widgetFrame != null) {
            widgetFrame.setVisibility(this.mWidgetFrameVisible ? 0 : 8);
        }
    }
}
