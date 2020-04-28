package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.launcher3.compat.LauncherAppsCompat;
import java.util.List;

@TargetApi(26)
public class SessionCommitReceiver extends BroadcastReceiver {
    public static final String ADD_ICON_PREFERENCE_INITIALIZED_KEY = "pref_add_icon_to_home_initialized";
    public static final String ADD_ICON_PREFERENCE_KEY = "pref_add_icon_to_home";
    private static final String MARKER_PROVIDER_PREFIX = ".addtohomescreen";
    private static final String TAG = "SessionCommitReceiver";

    public void onReceive(Context context, Intent intent) {
        if (isEnabled(context) && Utilities.ATLEAST_OREO) {
            PackageInstaller.SessionInfo info = (PackageInstaller.SessionInfo) intent.getParcelableExtra("android.content.pm.extra.SESSION");
            UserHandle user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (!TextUtils.isEmpty(info.getAppPackageName()) && info.getInstallReason() == 4) {
                queueAppIconAddition(context, info.getAppPackageName(), user);
            }
        }
    }

    public static void queueAppIconAddition(Context context, String packageName, UserHandle user) {
        List<LauncherActivityInfo> activities = LauncherAppsCompat.getInstance(context).getActivityList(packageName, user);
        if (activities != null && !activities.isEmpty()) {
            InstallShortcutReceiver.queueActivityInfo(activities.get(0), context);
        }
    }

    public static boolean isEnabled(Context context) {
        return Utilities.getPrefs(context).getBoolean(ADD_ICON_PREFERENCE_KEY, true);
    }

    public static void applyDefaultUserPrefs(Context context) {
        if (Utilities.ATLEAST_OREO) {
            SharedPreferences prefs = Utilities.getPrefs(context);
            if (prefs.getAll().isEmpty()) {
                prefs.edit().putBoolean(ADD_ICON_PREFERENCE_KEY, true).apply();
            } else if (!prefs.contains(ADD_ICON_PREFERENCE_INITIALIZED_KEY)) {
                new PrefInitTask(context).executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    }

    private static class PrefInitTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;

        PrefInitTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voids) {
            Utilities.getPrefs(this.mContext).edit().putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY, readValueFromMarketApp()).putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_INITIALIZED_KEY, true).apply();
            return null;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0065, code lost:
            if (r2 != null) goto L_0x0067;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0067, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0075, code lost:
            if (r2 == null) goto L_0x0078;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0078, code lost:
            return true;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean readValueFromMarketApp() {
            /*
                r10 = this;
                android.content.Context r0 = r10.mContext
                android.content.pm.PackageManager r0 = r0.getPackageManager()
                android.content.Intent r1 = new android.content.Intent
                java.lang.String r2 = "android.intent.action.MAIN"
                r1.<init>(r2)
                java.lang.String r2 = "android.intent.category.APP_MARKET"
                android.content.Intent r1 = r1.addCategory(r2)
                r2 = 1114112(0x110000, float:1.561203E-39)
                android.content.pm.ResolveInfo r0 = r0.resolveActivity(r1, r2)
                r1 = 1
                if (r0 != 0) goto L_0x001d
                return r1
            L_0x001d:
                r2 = 0
                android.content.Context r3 = r10.mContext     // Catch:{ Exception -> 0x006d }
                android.content.ContentResolver r4 = r3.getContentResolver()     // Catch:{ Exception -> 0x006d }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006d }
                r3.<init>()     // Catch:{ Exception -> 0x006d }
                java.lang.String r5 = "content://"
                r3.append(r5)     // Catch:{ Exception -> 0x006d }
                android.content.pm.ActivityInfo r5 = r0.activityInfo     // Catch:{ Exception -> 0x006d }
                java.lang.String r5 = r5.packageName     // Catch:{ Exception -> 0x006d }
                r3.append(r5)     // Catch:{ Exception -> 0x006d }
                java.lang.String r5 = ".addtohomescreen"
                r3.append(r5)     // Catch:{ Exception -> 0x006d }
                java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x006d }
                android.net.Uri r5 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x006d }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                android.database.Cursor r3 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x006d }
                r2 = r3
                boolean r3 = r2.moveToNext()     // Catch:{ Exception -> 0x006d }
                if (r3 == 0) goto L_0x0065
                java.lang.String r3 = "value"
                int r3 = r2.getColumnIndexOrThrow(r3)     // Catch:{ Exception -> 0x006d }
                int r3 = r2.getInt(r3)     // Catch:{ Exception -> 0x006d }
                if (r3 == 0) goto L_0x005e
                goto L_0x005f
            L_0x005e:
                r1 = 0
            L_0x005f:
                if (r2 == 0) goto L_0x0064
                r2.close()
            L_0x0064:
                return r1
            L_0x0065:
                if (r2 == 0) goto L_0x0078
            L_0x0067:
                r2.close()
                goto L_0x0078
            L_0x006b:
                r1 = move-exception
                goto L_0x0079
            L_0x006d:
                r3 = move-exception
                java.lang.String r4 = "SessionCommitReceiver"
                java.lang.String r5 = "Error reading add to homescreen preference"
                android.util.Log.d(r4, r5, r3)     // Catch:{ all -> 0x006b }
                if (r2 == 0) goto L_0x0078
                goto L_0x0067
            L_0x0078:
                return r1
            L_0x0079:
                if (r2 == 0) goto L_0x007e
                r2.close()
            L_0x007e:
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.SessionCommitReceiver.PrefInitTask.readValueFromMarketApp():boolean");
        }
    }
}
