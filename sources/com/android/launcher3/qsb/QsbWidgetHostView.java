package com.android.launcher3.qsb;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;

public class QsbWidgetHostView extends AppWidgetHostView {
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mPreviousOrientation;

    public QsbWidgetHostView(Context context) {
        super(context);
    }

    public void setAppWidget(int appWidgetId, AppWidgetProviderInfo info) {
        QsbContainerView.updateDefaultLayout(getContext(), info);
        super.setAppWidget(appWidgetId, info);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        this.mPreviousOrientation = getResources().getConfiguration().orientation;
        super.updateAppWidget(remoteViews);
    }

    public boolean isReinflateRequired(int orientation) {
        return this.mPreviousOrientation != orientation;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            super.onLayout(changed, left, top, right, bottom);
        } catch (RuntimeException e) {
            post(new Runnable() {
                public final void run() {
                    QsbWidgetHostView.this.updateAppWidget(new RemoteViews(QsbWidgetHostView.this.getAppWidgetInfo().provider.getPackageName(), 0));
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return getDefaultView(this);
    }

    /* access modifiers changed from: protected */
    public View getDefaultView() {
        View v = super.getDefaultView();
        v.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                Launcher.getLauncher(QsbWidgetHostView.this.getContext()).startSearch("", false, (Bundle) null, true);
            }
        });
        return v;
    }

    public static View getDefaultView(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qsb_default_view, parent, false);
        v.findViewById(R.id.btn_qsb_search).setOnClickListener($$Lambda$QsbWidgetHostView$t6LAnczKLhKhNN52AyyrsfztE0.INSTANCE);
        return v;
    }
}
