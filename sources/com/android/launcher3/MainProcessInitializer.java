package com.android.launcher3;

import android.content.Context;
import com.android.launcher3.graphics.IconShapeOverride;
import com.android.launcher3.logging.FileLog;

public class MainProcessInitializer {
    public static void initialize(Context context) {
        ((MainProcessInitializer) Utilities.getOverrideObject(MainProcessInitializer.class, context, R.string.main_process_initializer_class)).init(context);
    }

    /* access modifiers changed from: protected */
    public void init(Context context) {
        FileLog.setDir(context.getApplicationContext().getFilesDir());
        IconShapeOverride.apply(context);
        SessionCommitReceiver.applyDefaultUserPrefs(context);
    }
}
