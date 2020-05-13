package com.szchoiceway.index;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RemoteViews;

public class LauncherAppWidgetHostView extends AppWidgetHostView {
    private Context mContext;
    private LayoutInflater mInflater;
    private CheckLongPressHelper mLongPressHelper = new CheckLongPressHelper(this);
    private int mPreviousOrientation;

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return this.mInflater.inflate(R.layout.appwidget_error, this, false);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        this.mPreviousOrientation = this.mContext.getResources().getConfiguration().orientation;
        super.updateAppWidget(remoteViews);
    }

    public boolean orientationChangedSincedInflation() {
        if (this.mPreviousOrientation != this.mContext.getResources().getConfiguration().orientation) {
            return true;
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mLongPressHelper.hasPerformedLongPress()) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        }
        switch (ev.getAction()) {
            case 0:
                this.mLongPressHelper.postCheckForLongPress();
                break;
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                break;
        }
        return false;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public int getDescendantFocusability() {
        return 393216;
    }
}
