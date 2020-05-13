package com.szchoiceway.index;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

public class LauncherAppWidgetHost extends AppWidgetHost {
    Launcher mLauncher;

    public LauncherAppWidgetHost(Launcher launcher, int hostId) {
        super(launcher, hostId);
        this.mLauncher = launcher;
    }

    /* access modifiers changed from: protected */
    public AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        return new LauncherAppWidgetHostView(context);
    }

    public void stopListening() {
        super.stopListening();
        clearViews();
    }

    /* access modifiers changed from: protected */
    public void onProvidersChanged() {
        this.mLauncher.bindPackagesUpdated(LauncherModel.getSortedWidgetsAndShortcuts(this.mLauncher));
    }
}
