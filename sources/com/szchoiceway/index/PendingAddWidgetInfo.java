package com.szchoiceway.index;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import android.os.Parcelable;

/* compiled from: PendingAddItemInfo */
class PendingAddWidgetInfo extends PendingAddItemInfo {
    Bundle bindOptions = null;
    AppWidgetHostView boundWidget;
    Parcelable configurationData;
    int icon;
    AppWidgetProviderInfo info;
    String mimeType;
    int minHeight;
    int minResizeHeight;
    int minResizeWidth;
    int minWidth;
    int previewImage;

    public PendingAddWidgetInfo(AppWidgetProviderInfo i, String dataMimeType, Parcelable data) {
        this.itemType = 4;
        this.info = i;
        this.componentName = i.provider;
        this.minWidth = i.minWidth;
        this.minHeight = i.minHeight;
        this.minResizeWidth = i.minResizeWidth;
        this.minResizeHeight = i.minResizeHeight;
        this.previewImage = i.previewImage;
        this.icon = i.icon;
        if (dataMimeType != null && data != null) {
            this.mimeType = dataMimeType;
            this.configurationData = data;
        }
    }

    public PendingAddWidgetInfo(PendingAddWidgetInfo copy) {
        Bundle bundle = null;
        this.minWidth = copy.minWidth;
        this.minHeight = copy.minHeight;
        this.minResizeWidth = copy.minResizeWidth;
        this.minResizeHeight = copy.minResizeHeight;
        this.previewImage = copy.previewImage;
        this.icon = copy.icon;
        this.info = copy.info;
        this.boundWidget = copy.boundWidget;
        this.mimeType = copy.mimeType;
        this.configurationData = copy.configurationData;
        this.componentName = copy.componentName;
        this.itemType = copy.itemType;
        this.spanX = copy.spanX;
        this.spanY = copy.spanY;
        this.minSpanX = copy.minSpanX;
        this.minSpanY = copy.minSpanY;
        this.bindOptions = copy.bindOptions != null ? (Bundle) copy.bindOptions.clone() : bundle;
    }

    public String toString() {
        return "Widget: " + this.componentName.toShortString();
    }
}
