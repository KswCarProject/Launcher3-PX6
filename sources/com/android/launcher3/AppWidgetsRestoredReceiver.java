package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.ContentWriter;

public class AppWidgetsRestoredReceiver extends BroadcastReceiver {
    private static final String TAG = "AWRestoredReceiver";

    public void onReceive(Context context, Intent intent) {
        if ("android.appwidget.action.APPWIDGET_HOST_RESTORED".equals(intent.getAction())) {
            int hostId = intent.getIntExtra("hostId", 0);
            Log.d(TAG, "Widget ID map received for host:" + hostId);
            if (hostId == 1024) {
                int[] oldIds = intent.getIntArrayExtra("appWidgetOldIds");
                int[] newIds = intent.getIntArrayExtra("appWidgetIds");
                if (oldIds.length == newIds.length) {
                    final Context context2 = context;
                    final int[] iArr = oldIds;
                    final int[] iArr2 = newIds;
                    final BroadcastReceiver.PendingResult goAsync = goAsync();
                    new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(new Runnable() {
                        public void run() {
                            AppWidgetsRestoredReceiver.restoreAppWidgetIds(context2, iArr, iArr2);
                            goAsync.finish();
                        }
                    });
                    return;
                }
                Log.e(TAG, "Invalid host restored received");
            }
        }
    }

    @WorkerThread
    static void restoreAppWidgetIds(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        int state;
        Context context2 = context;
        int[] iArr = oldWidgetIds;
        int[] iArr2 = newWidgetIds;
        AppWidgetHost appWidgetHost = new LauncherAppWidgetHost(context2);
        if (!RestoreDbTask.isPending(context)) {
            Log.e(TAG, "Skipping widget ID remap as DB already in use");
            for (int widgetId : iArr2) {
                Log.d(TAG, "Deleting widgetId: " + widgetId);
                appWidgetHost.deleteAppWidgetId(widgetId);
            }
            return;
        }
        ContentResolver cr = context.getContentResolver();
        AppWidgetManager widgets = AppWidgetManager.getInstance(context);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= iArr.length) {
                break;
            }
            Log.i(TAG, "Widget state restore id " + iArr[i2] + " => " + iArr2[i2]);
            if (LoaderTask.isValidProvider(widgets.getAppWidgetInfo(iArr2[i2]))) {
                state = 4;
            } else {
                state = 2;
            }
            int state2 = state;
            String[] widgetIdParams = {Integer.toString(iArr[i2])};
            if (new ContentWriter(context2, new ContentWriter.CommitParams("appWidgetId=? and (restored & 1) = 1", widgetIdParams)).put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(iArr2[i2])).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(state2)).commit() == 0) {
                String[] strArr = widgetIdParams;
                Cursor cursor = cr.query(LauncherSettings.Favorites.CONTENT_URI, new String[]{LauncherSettings.Favorites.APPWIDGET_ID}, "appWidgetId=?", widgetIdParams, (String) null);
                try {
                    if (!cursor.moveToFirst()) {
                        appWidgetHost.deleteAppWidgetId(iArr2[i2]);
                    }
                } finally {
                    cursor.close();
                }
            }
            i = i2 + 1;
        }
        LauncherAppState app = LauncherAppState.getInstanceNoCreate();
        if (app != null) {
            app.getModel().forceReload();
        }
    }
}
