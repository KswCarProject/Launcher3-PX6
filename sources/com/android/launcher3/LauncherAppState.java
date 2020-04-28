package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.ConfigMonitor;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.SettingsObserver;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class LauncherAppState {
    public static final String ACTION_FORCE_ROLOAD = "force-reload-launcher";
    private static LauncherAppState INSTANCE;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final IconCache mIconCache;
    private final InvariantDeviceProfile mInvariantDeviceProfile;
    private final LauncherModel mModel;
    private final SettingsObserver mNotificationBadgingObserver;
    private final WidgetPreviewLoader mWidgetCache;

    public static LauncherAppState getInstance(final Context context) {
        if (INSTANCE == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                INSTANCE = new LauncherAppState(context.getApplicationContext());
            } else {
                try {
                    return (LauncherAppState) new MainThreadExecutor().submit(new Callable<LauncherAppState>() {
                        public LauncherAppState call() throws Exception {
                            return LauncherAppState.getInstance(context);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return INSTANCE;
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return this.mContext;
    }

    private LauncherAppState(Context context) {
        if (getLocalProvider(context) != null) {
            Log.v(Launcher.TAG, "LauncherAppState initiated");
            Preconditions.assertUIThread();
            this.mContext = context;
            this.mInvariantDeviceProfile = new InvariantDeviceProfile(this.mContext);
            this.mIconCache = new IconCache(this.mContext, this.mInvariantDeviceProfile);
            this.mWidgetCache = new WidgetPreviewLoader(this.mContext, this.mIconCache);
            this.mModel = new LauncherModel(this, this.mIconCache, AppFilter.newInstance(this.mContext));
            LauncherAppsCompat.getInstance(this.mContext).addOnAppsChangedCallback(this.mModel);
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.LOCALE_CHANGED");
            filter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            filter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            filter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
            filter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
            filter.addAction("android.intent.action.MANAGED_PROFILE_UNLOCKED");
            this.mContext.registerReceiver(this.mModel, filter);
            UserManagerCompat.getInstance(this.mContext).enableAndResetCache();
            new ConfigMonitor(this.mContext).register();
            if (!this.mContext.getResources().getBoolean(R.bool.notification_badging_enabled)) {
                this.mNotificationBadgingObserver = null;
                return;
            }
            this.mNotificationBadgingObserver = new SettingsObserver.Secure(this.mContext.getContentResolver()) {
                public void onSettingChanged(boolean isNotificationBadgingEnabled) {
                    if (isNotificationBadgingEnabled) {
                        NotificationListener.requestRebind(new ComponentName(LauncherAppState.this.mContext, NotificationListener.class));
                    }
                }
            };
            this.mNotificationBadgingObserver.register(SettingsActivity.NOTIFICATION_BADGING, new String[0]);
            return;
        }
        throw new RuntimeException("Initializing LauncherAppState in the absence of LauncherProvider");
    }

    public void onTerminate() {
        this.mContext.unregisterReceiver(this.mModel);
        LauncherAppsCompat.getInstance(this.mContext).removeOnAppsChangedCallback(this.mModel);
        PackageInstallerCompat.getInstance(this.mContext).onStop();
        if (this.mNotificationBadgingObserver != null) {
            this.mNotificationBadgingObserver.unregister();
        }
    }

    /* access modifiers changed from: package-private */
    public LauncherModel setLauncher(Launcher launcher) {
        getLocalProvider(this.mContext).setLauncherProviderChangeListener(launcher);
        this.mModel.initialize(launcher);
        return this.mModel;
    }

    public IconCache getIconCache() {
        return this.mIconCache;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public WidgetPreviewLoader getWidgetCache() {
        return this.mWidgetCache;
    }

    public InvariantDeviceProfile getInvariantDeviceProfile() {
        return this.mInvariantDeviceProfile;
    }

    public static InvariantDeviceProfile getIDP(Context context) {
        return getInstance(context).getInvariantDeviceProfile();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        if (r0 != null) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        if (r1 != null) goto L_0x0020;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.android.launcher3.LauncherProvider getLocalProvider(android.content.Context r4) {
        /*
            android.content.ContentResolver r0 = r4.getContentResolver()
            java.lang.String r1 = com.android.launcher3.LauncherProvider.AUTHORITY
            android.content.ContentProviderClient r0 = r0.acquireContentProviderClient(r1)
            r1 = 0
            android.content.ContentProvider r2 = r0.getLocalContentProvider()     // Catch:{ Throwable -> 0x001a }
            com.android.launcher3.LauncherProvider r2 = (com.android.launcher3.LauncherProvider) r2     // Catch:{ Throwable -> 0x001a }
            if (r0 == 0) goto L_0x0017
            r0.close()
        L_0x0017:
            return r2
        L_0x0018:
            r2 = move-exception
            goto L_0x001c
        L_0x001a:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0018 }
        L_0x001c:
            if (r0 == 0) goto L_0x002c
            if (r1 == 0) goto L_0x0029
            r0.close()     // Catch:{ Throwable -> 0x0024 }
            goto L_0x002c
        L_0x0024:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x002c
        L_0x0029:
            r0.close()
        L_0x002c:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherAppState.getLocalProvider(android.content.Context):com.android.launcher3.LauncherProvider");
    }
}
