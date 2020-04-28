package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;

public class LauncherAppWidgetProviderInfo extends AppWidgetProviderInfo {
    public static final String CLS_CUSTOM_WIDGET_PREFIX = "#custom-widget-";
    public int minSpanX;
    public int minSpanY;
    public int spanX;
    public int spanY;

    public static LauncherAppWidgetProviderInfo fromProviderInfo(Context context, AppWidgetProviderInfo info) {
        LauncherAppWidgetProviderInfo launcherInfo;
        if (info instanceof LauncherAppWidgetProviderInfo) {
            launcherInfo = (LauncherAppWidgetProviderInfo) info;
        } else {
            Parcel p = Parcel.obtain();
            info.writeToParcel(p, 0);
            p.setDataPosition(0);
            LauncherAppWidgetProviderInfo launcherInfo2 = new LauncherAppWidgetProviderInfo(p);
            p.recycle();
            launcherInfo = launcherInfo2;
        }
        launcherInfo.initSpans(context);
        return launcherInfo;
    }

    protected LauncherAppWidgetProviderInfo() {
    }

    protected LauncherAppWidgetProviderInfo(Parcel in) {
        super(in);
    }

    public void initSpans(Context context) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        Point paddingLand = idp.landscapeProfile.getTotalWorkspacePadding();
        Point paddingPort = idp.portraitProfile.getTotalWorkspacePadding();
        float smallestCellWidth = (float) DeviceProfile.calculateCellWidth(Math.min(idp.landscapeProfile.widthPx - paddingLand.x, idp.portraitProfile.widthPx - paddingPort.x), idp.numColumns);
        float smallestCellHeight = (float) DeviceProfile.calculateCellWidth(Math.min(idp.landscapeProfile.heightPx - paddingLand.y, idp.portraitProfile.heightPx - paddingPort.y), idp.numRows);
        Rect widgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context, this.provider, (Rect) null);
        this.spanX = Math.max(1, (int) Math.ceil((double) (((float) ((this.minWidth + widgetPadding.left) + widgetPadding.right)) / smallestCellWidth)));
        this.spanY = Math.max(1, (int) Math.ceil((double) (((float) ((this.minHeight + widgetPadding.top) + widgetPadding.bottom)) / smallestCellHeight)));
        this.minSpanX = Math.max(1, (int) Math.ceil((double) (((float) ((this.minResizeWidth + widgetPadding.left) + widgetPadding.right)) / smallestCellWidth)));
        this.minSpanY = Math.max(1, (int) Math.ceil((double) (((float) ((this.minResizeHeight + widgetPadding.top) + widgetPadding.bottom)) / smallestCellHeight)));
    }

    public String getLabel(PackageManager packageManager) {
        return super.loadLabel(packageManager);
    }

    public Point getMinSpans() {
        int i = -1;
        int i2 = (this.resizeMode & 1) != 0 ? this.minSpanX : -1;
        if ((this.resizeMode & 2) != 0) {
            i = this.minSpanY;
        }
        return new Point(i2, i);
    }

    public boolean isCustomWidget() {
        return this.provider.getClassName().startsWith(CLS_CUSTOM_WIDGET_PREFIX);
    }

    public int getWidgetFeatures() {
        if (Utilities.ATLEAST_P) {
            return this.widgetFeatures;
        }
        return 0;
    }
}
