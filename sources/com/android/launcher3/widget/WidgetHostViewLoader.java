package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.views.BaseDragLayer;

public class WidgetHostViewLoader implements DragController.DragListener {
    private static final boolean LOGD = false;
    private static final String TAG = "WidgetHostViewLoader";
    private Runnable mBindWidgetRunnable = null;
    Handler mHandler;
    Runnable mInflateWidgetRunnable = null;
    final PendingAddWidgetInfo mInfo;
    Launcher mLauncher;
    final View mView;
    int mWidgetLoadingId = -1;

    public WidgetHostViewLoader(Launcher launcher, View view) {
        this.mLauncher = launcher;
        this.mHandler = new Handler();
        this.mView = view;
        this.mInfo = (PendingAddWidgetInfo) view.getTag();
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        preloadWidget();
    }

    public void onDragEnd() {
        this.mLauncher.getDragController().removeDragListener(this);
        this.mHandler.removeCallbacks(this.mBindWidgetRunnable);
        this.mHandler.removeCallbacks(this.mInflateWidgetRunnable);
        if (this.mWidgetLoadingId != -1) {
            this.mLauncher.getAppWidgetHost().deleteAppWidgetId(this.mWidgetLoadingId);
            this.mWidgetLoadingId = -1;
        }
        if (this.mInfo.boundWidget != null) {
            this.mLauncher.getDragLayer().removeView(this.mInfo.boundWidget);
            this.mLauncher.getAppWidgetHost().deleteAppWidgetId(this.mInfo.boundWidget.getAppWidgetId());
            this.mInfo.boundWidget = null;
        }
    }

    private boolean preloadWidget() {
        final LauncherAppWidgetProviderInfo pInfo = this.mInfo.info;
        if (pInfo.isCustomWidget()) {
            return false;
        }
        final Bundle options = getDefaultOptionsForWidget(this.mLauncher, this.mInfo);
        if (this.mInfo.getHandler().needsConfigure()) {
            this.mInfo.bindOptions = options;
            return false;
        }
        this.mBindWidgetRunnable = new Runnable() {
            public void run() {
                WidgetHostViewLoader.this.mWidgetLoadingId = WidgetHostViewLoader.this.mLauncher.getAppWidgetHost().allocateAppWidgetId();
                if (AppWidgetManagerCompat.getInstance(WidgetHostViewLoader.this.mLauncher).bindAppWidgetIdIfAllowed(WidgetHostViewLoader.this.mWidgetLoadingId, pInfo, options)) {
                    WidgetHostViewLoader.this.mHandler.post(WidgetHostViewLoader.this.mInflateWidgetRunnable);
                }
            }
        };
        this.mInflateWidgetRunnable = new Runnable() {
            public void run() {
                if (WidgetHostViewLoader.this.mWidgetLoadingId != -1) {
                    AppWidgetHostView hostView = WidgetHostViewLoader.this.mLauncher.getAppWidgetHost().createView(WidgetHostViewLoader.this.mLauncher, WidgetHostViewLoader.this.mWidgetLoadingId, pInfo);
                    WidgetHostViewLoader.this.mInfo.boundWidget = hostView;
                    WidgetHostViewLoader.this.mWidgetLoadingId = -1;
                    hostView.setVisibility(4);
                    int[] unScaledSize = WidgetHostViewLoader.this.mLauncher.getWorkspace().estimateItemSize(WidgetHostViewLoader.this.mInfo);
                    BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(unScaledSize[0], unScaledSize[1]);
                    lp.y = 0;
                    lp.x = 0;
                    lp.customPosition = true;
                    hostView.setLayoutParams(lp);
                    WidgetHostViewLoader.this.mLauncher.getDragLayer().addView(hostView);
                    WidgetHostViewLoader.this.mView.setTag(WidgetHostViewLoader.this.mInfo);
                }
            }
        };
        this.mHandler.post(this.mBindWidgetRunnable);
        return true;
    }

    public static Bundle getDefaultOptionsForWidget(Context context, PendingAddWidgetInfo info) {
        Rect rect = new Rect();
        AppWidgetResizeFrame.getWidgetSizeRanges(context, info.spanX, info.spanY, rect);
        Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(context, info.componentName, (Rect) null);
        float density = context.getResources().getDisplayMetrics().density;
        int xPaddingDips = (int) (((float) (padding.left + padding.right)) / density);
        int yPaddingDips = (int) (((float) (padding.top + padding.bottom)) / density);
        Bundle options = new Bundle();
        options.putInt("appWidgetMinWidth", rect.left - xPaddingDips);
        options.putInt("appWidgetMinHeight", rect.top - yPaddingDips);
        options.putInt("appWidgetMaxWidth", rect.right - xPaddingDips);
        options.putInt("appWidgetMaxHeight", rect.bottom - yPaddingDips);
        return options;
    }
}
