package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.Toast;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.widget.DeferredAppWidgetHostView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import java.util.ArrayList;
import java.util.Iterator;

public class LauncherAppWidgetHost extends AppWidgetHost {
    public static final int APPWIDGET_HOST_ID = 1024;
    private static final int FLAG_LISTENING = 1;
    private static final int FLAG_LISTEN_IF_RESUMED = 4;
    private static final int FLAG_RESUMED = 2;
    private final Context mContext;
    private int mFlags = 2;
    private final ArrayList<ProviderChangedListener> mProviderChangeListeners = new ArrayList<>();
    private final SparseArray<LauncherAppWidgetHostView> mViews = new SparseArray<>();

    public interface ProviderChangedListener {
        void notifyWidgetProvidersChanged();
    }

    public LauncherAppWidgetHost(Context context) {
        super(context, 1024);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public LauncherAppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        LauncherAppWidgetHostView view = new LauncherAppWidgetHostView(context);
        this.mViews.put(appWidgetId, view);
        return view;
    }

    public void startListening() {
        this.mFlags |= 1;
        try {
            super.startListening();
        } catch (Exception e) {
            if (!Utilities.isBinderSizeError(e)) {
                throw new RuntimeException(e);
            }
        }
        for (int i = this.mViews.size() - 1; i >= 0; i--) {
            LauncherAppWidgetHostView view = this.mViews.valueAt(i);
            if (view instanceof DeferredAppWidgetHostView) {
                view.reInflate();
            }
        }
    }

    public void stopListening() {
        this.mFlags &= -2;
        super.stopListening();
    }

    public void setResumed(boolean isResumed) {
        if (isResumed != ((this.mFlags & 2) != 0)) {
            if (isResumed) {
                this.mFlags |= 2;
                if ((this.mFlags & 4) != 0 && (this.mFlags & 1) == 0) {
                    startListening();
                    return;
                }
                return;
            }
            this.mFlags &= -3;
        }
    }

    public void setListenIfResumed(boolean listenIfResumed) {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            if (listenIfResumed != ((this.mFlags & 4) != 0)) {
                if (listenIfResumed) {
                    this.mFlags |= 4;
                    if ((this.mFlags & 2) != 0) {
                        startListening();
                        return;
                    }
                    return;
                }
                this.mFlags &= -5;
                stopListening();
            }
        }
    }

    public int allocateAppWidgetId() {
        return super.allocateAppWidgetId();
    }

    public void addProviderChangeListener(ProviderChangedListener callback) {
        this.mProviderChangeListeners.add(callback);
    }

    public void removeProviderChangeListener(ProviderChangedListener callback) {
        this.mProviderChangeListeners.remove(callback);
    }

    /* access modifiers changed from: protected */
    public void onProvidersChanged() {
        if (!this.mProviderChangeListeners.isEmpty()) {
            Iterator it = new ArrayList(this.mProviderChangeListeners).iterator();
            while (it.hasNext()) {
                ((ProviderChangedListener) it.next()).notifyWidgetProvidersChanged();
            }
        }
    }

    public AppWidgetHostView createView(Context context, int appWidgetId, LauncherAppWidgetProviderInfo appWidget) {
        if (appWidget.isCustomWidget()) {
            LauncherAppWidgetHostView lahv = new LauncherAppWidgetHostView(context);
            ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(appWidget.initialLayout, lahv);
            lahv.setAppWidget(0, appWidget);
            return lahv;
        } else if ((this.mFlags & 1) == 0) {
            DeferredAppWidgetHostView view = new DeferredAppWidgetHostView(context);
            view.setAppWidget(appWidgetId, appWidget);
            this.mViews.put(appWidgetId, view);
            return view;
        } else {
            try {
                return super.createView(context, appWidgetId, appWidget);
            } catch (Exception e) {
                if (Utilities.isBinderSizeError(e)) {
                    LauncherAppWidgetHostView view2 = this.mViews.get(appWidgetId);
                    if (view2 == null) {
                        view2 = onCreateView(this.mContext, appWidgetId, (AppWidgetProviderInfo) appWidget);
                    }
                    view2.setAppWidget(appWidgetId, appWidget);
                    view2.switchToErrorView();
                    return view2;
                }
                throw new RuntimeException(e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onProviderChanged(int appWidgetId, AppWidgetProviderInfo appWidget) {
        LauncherAppWidgetProviderInfo info = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidget);
        super.onProviderChanged(appWidgetId, info);
        info.initSpans(this.mContext);
    }

    public void deleteAppWidgetId(int appWidgetId) {
        super.deleteAppWidgetId(appWidgetId);
        this.mViews.remove(appWidgetId);
    }

    public void clearViews() {
        super.clearViews();
        this.mViews.clear();
    }

    public void startBindFlow(BaseActivity activity, int appWidgetId, AppWidgetProviderInfo info, int requestCode) {
        activity.startActivityForResult(new Intent("android.appwidget.action.APPWIDGET_BIND").putExtra(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId).putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, info.provider).putExtra("appWidgetProviderProfile", info.getProfile()), requestCode);
    }

    public void startConfigActivity(BaseActivity activity, int widgetId, int requestCode) {
        try {
            startAppWidgetConfigureActivityForResult(activity, widgetId, 0, requestCode, (Bundle) null);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            sendActionCancelled(activity, requestCode);
        }
    }

    private void sendActionCancelled(BaseActivity activity, int requestCode) {
        new Handler().post(new Runnable(requestCode) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BaseActivity.this.onActivityResult(this.f$1, 0, (Intent) null);
            }
        });
    }
}
