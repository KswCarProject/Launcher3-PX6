package com.szchoiceway.index;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.ContentValues;

class LauncherAppWidgetInfo extends ItemInfo {
    static final int NO_ID = -1;
    int appWidgetId = -1;
    AppWidgetHostView hostView = null;
    private boolean mHasNotifiedInitialWidgetSizeChanged;
    int minHeight = -1;
    int minWidth = -1;
    ComponentName providerName;

    LauncherAppWidgetInfo(int appWidgetId2, ComponentName providerName2) {
        this.itemType = 4;
        this.appWidgetId = appWidgetId2;
        this.providerName = providerName2;
        this.spanX = -1;
        this.spanY = -1;
    }

    /* access modifiers changed from: package-private */
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put("appWidgetId", Integer.valueOf(this.appWidgetId));
    }

    /* access modifiers changed from: package-private */
    public void onBindAppWidget(Launcher launcher) {
        if (!this.mHasNotifiedInitialWidgetSizeChanged) {
            notifyWidgetSizeChanged(launcher);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyWidgetSizeChanged(Launcher launcher) {
        AppWidgetResizeFrame.updateWidgetSizeRanges(this.hostView, launcher, this.spanX, this.spanY);
        this.mHasNotifiedInitialWidgetSizeChanged = true;
    }

    public String toString() {
        return "AppWidget(id=" + Integer.toString(this.appWidgetId) + ")";
    }

    /* access modifiers changed from: package-private */
    public void unbind() {
        super.unbind();
        this.hostView = null;
    }
}
