package com.szchoiceway.index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageChangedReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getSchemeSpecificPart();
        if (packageName != null && packageName.length() != 0) {
            WidgetPreviewLoader.removeFromDb(((LauncherApplication) context.getApplicationContext()).getWidgetPreviewCacheDb(), packageName);
        }
    }
}
