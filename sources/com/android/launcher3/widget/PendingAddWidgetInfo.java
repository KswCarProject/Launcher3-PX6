package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PendingAddItemInfo;

public class PendingAddWidgetInfo extends PendingAddItemInfo {
    public Bundle bindOptions = null;
    public AppWidgetHostView boundWidget;
    public int icon;
    public LauncherAppWidgetProviderInfo info;
    public int previewImage;

    public PendingAddWidgetInfo(LauncherAppWidgetProviderInfo i) {
        if (i.isCustomWidget()) {
            this.itemType = 5;
        } else {
            this.itemType = 4;
        }
        this.info = i;
        this.user = i.getProfile();
        this.componentName = i.provider;
        this.previewImage = i.previewImage;
        this.icon = i.icon;
        this.spanX = i.spanX;
        this.spanY = i.spanY;
        this.minSpanX = i.minSpanX;
        this.minSpanY = i.minSpanY;
    }

    public WidgetAddFlowHandler getHandler() {
        return new WidgetAddFlowHandler((AppWidgetProviderInfo) this.info);
    }
}
