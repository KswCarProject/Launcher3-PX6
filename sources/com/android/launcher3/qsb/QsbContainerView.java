package com.android.launcher3.qsb;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.qsb.QsbContainerView;

public class QsbContainerView extends FrameLayout {

    public interface WidgetViewFactory {
        QsbWidgetHostView newView(Context context);
    }

    public QsbContainerView(Context context) {
        super(context);
    }

    public QsbContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QsbContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    /* access modifiers changed from: protected */
    public void setPaddingUnchecked(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    public static class QsbFragment extends Fragment {
        public static final int QSB_WIDGET_HOST_ID = 1026;
        private static final int REQUEST_BIND_QSB = 1;
        protected String mKeyWidgetId = "qsb_widget_id";
        private int mOrientation;
        private QsbWidgetHostView mQsb;
        private QsbWidgetHost mQsbWidgetHost;
        private AppWidgetProviderInfo mWidgetInfo;
        private FrameLayout mWrapper;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.mQsbWidgetHost = createHost();
            this.mOrientation = getContext().getResources().getConfiguration().orientation;
        }

        /* access modifiers changed from: protected */
        public QsbWidgetHost createHost() {
            return new QsbWidgetHost(getActivity(), QSB_WIDGET_HOST_ID, $$Lambda$QsbContainerView$QsbFragment$mJH3DRYTtExJl2VCjYSPQ8HApZ8.INSTANCE);
        }

        static /* synthetic */ QsbWidgetHostView lambda$createHost$0(Context c) {
            return new QsbWidgetHostView(c);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.mWrapper = new FrameLayout(getActivity());
            if (isQsbEnabled()) {
                this.mWrapper.addView(createQsb(this.mWrapper));
            }
            return this.mWrapper;
        }

        private View createQsb(ViewGroup container) {
            this.mWidgetInfo = getSearchWidgetProvider();
            boolean isWidgetBound = false;
            if (this.mWidgetInfo == null) {
                return getDefaultView(container, false);
            }
            Bundle opts = createBindOptions();
            Activity activity = getActivity();
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(activity);
            Rect size = AppWidgetResizeFrame.getWidgetSizeRanges(activity, LauncherAppState.getIDP(activity).numColumns, 1, (Rect) null);
            opts.putInt("appWidgetMinWidth", size.left);
            opts.putInt("appWidgetMinHeight", size.top);
            opts.putInt("appWidgetMaxWidth", size.right);
            opts.putInt("appWidgetMaxHeight", size.bottom);
            opts.putString("attached-launcher-identifier", BuildConfig.APPLICATION_ID);
            opts.putString("requested-widget-style", "cqsb");
            int widgetId = Utilities.getPrefs(activity).getInt(this.mKeyWidgetId, -1);
            AppWidgetProviderInfo widgetInfo = widgetManager.getAppWidgetInfo(widgetId);
            if (widgetInfo != null && widgetInfo.provider.equals(this.mWidgetInfo.provider)) {
                isWidgetBound = true;
            }
            int oldWidgetId = widgetId;
            if (!isWidgetBound) {
                if (widgetId > -1) {
                    this.mQsbWidgetHost.deleteHost();
                }
                widgetId = this.mQsbWidgetHost.allocateAppWidgetId();
                isWidgetBound = widgetManager.bindAppWidgetIdIfAllowed(widgetId, this.mWidgetInfo.getProfile(), this.mWidgetInfo.provider, opts);
                if (!isWidgetBound) {
                    this.mQsbWidgetHost.deleteAppWidgetId(widgetId);
                    widgetId = -1;
                }
                if (oldWidgetId != widgetId) {
                    saveWidgetId(widgetId);
                }
            }
            if (!isWidgetBound) {
                return getDefaultView(container, true);
            }
            this.mQsb = (QsbWidgetHostView) this.mQsbWidgetHost.createView(activity, widgetId, this.mWidgetInfo);
            this.mQsb.setId(R.id.qsb_widget);
            if (!Utilities.containsAll(AppWidgetManager.getInstance(activity).getAppWidgetOptions(widgetId), opts)) {
                this.mQsb.updateAppWidgetOptions(opts);
            }
            this.mQsbWidgetHost.startListening();
            return this.mQsb;
        }

        private void saveWidgetId(int widgetId) {
            Utilities.getPrefs(getActivity()).edit().putInt(this.mKeyWidgetId, widgetId).apply();
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode != 1) {
                return;
            }
            if (resultCode == -1) {
                saveWidgetId(data.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, -1));
                rebindFragment();
                return;
            }
            this.mQsbWidgetHost.deleteHost();
        }

        public void onResume() {
            super.onResume();
            if (this.mQsb != null && this.mQsb.isReinflateRequired(this.mOrientation)) {
                rebindFragment();
            }
        }

        public void onDestroy() {
            this.mQsbWidgetHost.stopListening();
            super.onDestroy();
        }

        private void rebindFragment() {
            if (isQsbEnabled() && this.mWrapper != null && getActivity() != null) {
                this.mWrapper.removeAllViews();
                this.mWrapper.addView(createQsb(this.mWrapper));
            }
        }

        public boolean isQsbEnabled() {
            return true;
        }

        /* access modifiers changed from: protected */
        public Bundle createBindOptions() {
            InvariantDeviceProfile idp = LauncherAppState.getIDP(getActivity());
            Bundle opts = new Bundle();
            Rect size = AppWidgetResizeFrame.getWidgetSizeRanges(getActivity(), idp.numColumns, 1, (Rect) null);
            opts.putInt("appWidgetMinWidth", size.left);
            opts.putInt("appWidgetMinHeight", size.top);
            opts.putInt("appWidgetMaxWidth", size.right);
            opts.putInt("appWidgetMaxHeight", size.bottom);
            return opts;
        }

        /* access modifiers changed from: protected */
        public View getDefaultView(ViewGroup container, boolean showSetupIcon) {
            View v = QsbWidgetHostView.getDefaultView(container);
            if (showSetupIcon) {
                View setupButton = v.findViewById(R.id.btn_qsb_setup);
                setupButton.setVisibility(0);
                setupButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        QsbContainerView.QsbFragment.this.startActivityForResult(new Intent("android.appwidget.action.APPWIDGET_BIND").putExtra(LauncherSettings.Favorites.APPWIDGET_ID, QsbContainerView.QsbFragment.this.mQsbWidgetHost.allocateAppWidgetId()).putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, QsbContainerView.QsbFragment.this.mWidgetInfo.provider), 1);
                    }
                });
            }
            return v;
        }

        /* access modifiers changed from: protected */
        public AppWidgetProviderInfo getSearchWidgetProvider() {
            ComponentName searchComponent = ((SearchManager) getActivity().getSystemService("search")).getGlobalSearchActivity();
            if (searchComponent == null) {
                return null;
            }
            String providerPkg = searchComponent.getPackageName();
            AppWidgetProviderInfo defaultWidgetForSearchPackage = null;
            for (AppWidgetProviderInfo info : AppWidgetManager.getInstance(getActivity()).getInstalledProviders()) {
                if (info.provider.getPackageName().equals(providerPkg) && info.configure == null) {
                    if ((info.widgetCategory & 4) != 0) {
                        return info;
                    }
                    if (defaultWidgetForSearchPackage == null) {
                        defaultWidgetForSearchPackage = info;
                    }
                }
            }
            return defaultWidgetForSearchPackage;
        }
    }

    public static class QsbWidgetHost extends AppWidgetHost {
        private final WidgetViewFactory mViewFactory;

        public QsbWidgetHost(Context context, int hostId, WidgetViewFactory viewFactory) {
            super(context, hostId);
            this.mViewFactory = viewFactory;
        }

        /* access modifiers changed from: protected */
        public AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
            return this.mViewFactory.newView(context);
        }
    }

    public static void updateDefaultLayout(Context context, AppWidgetProviderInfo info) {
        ComponentName provider = info.provider;
        if (provider.getClassName().equals("com.google.android.googlequicksearchbox.SearchWidgetProvider")) {
            try {
                int resId = context.getPackageManager().getReceiverInfo(provider, 128).metaData.getInt("com.google.android.gsa.searchwidget.alt_initial_layout_cqsb", -1);
                if (resId != -1) {
                    info.initialLayout = resId;
                }
            } catch (Exception e) {
            }
        }
    }
}
