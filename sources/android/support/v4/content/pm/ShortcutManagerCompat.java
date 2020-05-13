package android.support.v4.content.pm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.BuildCompat;

public class ShortcutManagerCompat {
    @VisibleForTesting
    static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    @VisibleForTesting
    static final String INSTALL_SHORTCUT_PERMISSION = "com.android.launcher.permission.INSTALL_SHORTCUT";

    /* JADX WARNING: Removed duplicated region for block: B:8:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isRequestPinShortcutSupported(@android.support.annotation.NonNull android.content.Context r6) {
        /*
            r2 = 0
            boolean r3 = android.support.v4.os.BuildCompat.isAtLeastO()
            if (r3 == 0) goto L_0x000c
            boolean r2 = android.support.v4.content.pm.ShortcutManagerCompatApi26.isRequestPinShortcutSupported(r6)
        L_0x000b:
            return r2
        L_0x000c:
            java.lang.String r3 = "com.android.launcher.permission.INSTALL_SHORTCUT"
            int r3 = android.support.v4.content.ContextCompat.checkSelfPermission(r6, r3)
            if (r3 != 0) goto L_0x000b
            android.content.pm.PackageManager r3 = r6.getPackageManager()
            android.content.Intent r4 = new android.content.Intent
            java.lang.String r5 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r4.<init>(r5)
            java.util.List r3 = r3.queryBroadcastReceivers(r4, r2)
            java.util.Iterator r3 = r3.iterator()
        L_0x0027:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x000b
            java.lang.Object r0 = r3.next()
            android.content.pm.ResolveInfo r0 = (android.content.pm.ResolveInfo) r0
            android.content.pm.ActivityInfo r4 = r0.activityInfo
            java.lang.String r1 = r4.permission
            boolean r4 = android.text.TextUtils.isEmpty(r1)
            if (r4 != 0) goto L_0x0045
            java.lang.String r4 = "com.android.launcher.permission.INSTALL_SHORTCUT"
            boolean r4 = r4.equals(r1)
            if (r4 == 0) goto L_0x0027
        L_0x0045:
            r2 = 1
            goto L_0x000b
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.pm.ShortcutManagerCompat.isRequestPinShortcutSupported(android.content.Context):boolean");
    }

    public static boolean requestPinShortcut(@NonNull Context context, @NonNull ShortcutInfoCompat shortcut, @Nullable final IntentSender callback) {
        if (BuildCompat.isAtLeastO()) {
            return ShortcutManagerCompatApi26.requestPinShortcut(context, shortcut, callback);
        }
        if (!isRequestPinShortcutSupported(context)) {
            return false;
        }
        Intent intent = shortcut.addToIntent(new Intent(ACTION_INSTALL_SHORTCUT));
        if (callback == null) {
            context.sendBroadcast(intent);
            return true;
        }
        context.sendOrderedBroadcast(intent, (String) null, new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    callback.sendIntent(context, 0, (Intent) null, (IntentSender.OnFinished) null, (Handler) null);
                } catch (IntentSender.SendIntentException e) {
                }
            }
        }, (Handler) null, -1, (String) null, (Bundle) null);
        return true;
    }

    @NonNull
    public static Intent createShortcutResultIntent(@NonNull Context context, @NonNull ShortcutInfoCompat shortcut) {
        Intent result = null;
        if (BuildCompat.isAtLeastO()) {
            result = ShortcutManagerCompatApi26.createShortcutResultIntent(context, shortcut);
        }
        if (result == null) {
            result = new Intent();
        }
        return shortcut.addToIntent(result);
    }
}
