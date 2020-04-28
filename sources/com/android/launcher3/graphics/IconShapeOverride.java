package com.android.launcher3.graphics;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Process;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.LooperExecutor;
import java.lang.reflect.Field;

@TargetApi(26)
public class IconShapeOverride {
    public static final String KEY_PREFERENCE = "pref_override_icon_shape";
    private static final long PROCESS_KILL_DELAY_MS = 1000;
    private static final int RESTART_REQUEST_CODE = 42;
    private static final String TAG = "IconShapeOverride";

    public static boolean isSupported(Context context) {
        if (!Utilities.ATLEAST_OREO || Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 1) {
            return false;
        }
        try {
            if (getSystemResField().get((Object) null) == Resources.getSystem() && getConfigResId() != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void apply(Context context) {
        if (Utilities.ATLEAST_OREO) {
            String path = getAppliedValue(context);
            if (!TextUtils.isEmpty(path) && isSupported(context)) {
                try {
                    getSystemResField().set((Object) null, new ResourcesOverride(Resources.getSystem(), getConfigResId(), path));
                } catch (Exception e) {
                    Log.e(TAG, "Unable to override icon shape", e);
                    Utilities.getDevicePrefs(context).edit().remove(KEY_PREFERENCE).apply();
                }
            }
        }
    }

    private static Field getSystemResField() throws Exception {
        Field staticField = Resources.class.getDeclaredField("mSystem");
        staticField.setAccessible(true);
        return staticField;
    }

    private static int getConfigResId() {
        return Resources.getSystem().getIdentifier("config_icon_mask", "string", "android");
    }

    /* access modifiers changed from: private */
    public static String getAppliedValue(Context context) {
        return Utilities.getDevicePrefs(context).getString(KEY_PREFERENCE, "");
    }

    public static void handlePreferenceUi(ListPreference preference) {
        Context context = preference.getContext();
        preference.setValue(getAppliedValue(context));
        preference.setOnPreferenceChangeListener(new PreferenceChangeHandler(context));
    }

    private static class ResourcesOverride extends Resources {
        private final int mOverrideId;
        private final String mOverrideValue;

        public ResourcesOverride(Resources parent, int overrideId, String overrideValue) {
            super(parent.getAssets(), parent.getDisplayMetrics(), parent.getConfiguration());
            this.mOverrideId = overrideId;
            this.mOverrideValue = overrideValue;
        }

        @NonNull
        public String getString(int id) throws Resources.NotFoundException {
            if (id == this.mOverrideId) {
                return this.mOverrideValue;
            }
            return super.getString(id);
        }
    }

    private static class PreferenceChangeHandler implements Preference.OnPreferenceChangeListener {
        private final Context mContext;

        private PreferenceChangeHandler(Context context) {
            this.mContext = context;
        }

        public boolean onPreferenceChange(Preference preference, Object o) {
            String newValue = (String) o;
            if (!IconShapeOverride.getAppliedValue(this.mContext).equals(newValue)) {
                ProgressDialog.show(this.mContext, (CharSequence) null, this.mContext.getString(R.string.icon_shape_override_progress), true, false);
                new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new OverrideApplyHandler(this.mContext, newValue));
            }
            return false;
        }
    }

    private static class OverrideApplyHandler implements Runnable {
        private final Context mContext;
        private final String mValue;

        private OverrideApplyHandler(Context context, String value) {
            this.mContext = context;
            this.mValue = value;
        }

        public void run() {
            Utilities.getDevicePrefs(this.mContext).edit().putString(IconShapeOverride.KEY_PREFERENCE, this.mValue).commit();
            LauncherAppState.getInstance(this.mContext).getIconCache().clear();
            try {
                Thread.sleep(IconShapeOverride.PROCESS_KILL_DELAY_MS);
            } catch (Exception e) {
                Log.e(IconShapeOverride.TAG, "Error waiting", e);
            }
            ((AlarmManager) this.mContext.getSystemService(AlarmManager.class)).setExact(3, SystemClock.elapsedRealtime() + 50, PendingIntent.getActivity(this.mContext, 42, new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setPackage(this.mContext.getPackageName()).addFlags(268435456), 1342177280));
            Process.killProcess(Process.myPid());
        }
    }
}
