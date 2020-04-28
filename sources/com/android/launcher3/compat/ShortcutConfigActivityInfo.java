package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;
import com.android.launcher3.IconCache;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;

public abstract class ShortcutConfigActivityInfo {
    private static final String TAG = "SCActivityInfo";
    private final ComponentName mCn;
    private final UserHandle mUser;

    public abstract Drawable getFullResIcon(IconCache iconCache);

    public abstract CharSequence getLabel();

    protected ShortcutConfigActivityInfo(ComponentName cn, UserHandle user) {
        this.mCn = cn;
        this.mUser = user;
    }

    public ComponentName getComponent() {
        return this.mCn;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public int getItemType() {
        return 1;
    }

    public ShortcutInfo createShortcutInfo() {
        return null;
    }

    public boolean startConfigActivity(Activity activity, int requestCode) {
        Intent intent = new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(getComponent());
        try {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            return false;
        } catch (SecurityException e2) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e2);
            return false;
        }
    }

    public boolean isPersistable() {
        return true;
    }

    static class ShortcutConfigActivityInfoVL extends ShortcutConfigActivityInfo {
        private final ActivityInfo mInfo;
        private final PackageManager mPm;

        public ShortcutConfigActivityInfoVL(ActivityInfo info, PackageManager pm) {
            super(new ComponentName(info.packageName, info.name), Process.myUserHandle());
            this.mInfo = info;
            this.mPm = pm;
        }

        public CharSequence getLabel() {
            return this.mInfo.loadLabel(this.mPm);
        }

        public Drawable getFullResIcon(IconCache cache) {
            return cache.getFullResIcon(this.mInfo);
        }
    }

    @TargetApi(26)
    public static class ShortcutConfigActivityInfoVO extends ShortcutConfigActivityInfo {
        private final LauncherActivityInfo mInfo;

        public ShortcutConfigActivityInfoVO(LauncherActivityInfo info) {
            super(info.getComponentName(), info.getUser());
            this.mInfo = info;
        }

        public CharSequence getLabel() {
            return this.mInfo.getLabel();
        }

        public Drawable getFullResIcon(IconCache cache) {
            return cache.getFullResIcon(this.mInfo);
        }

        public boolean startConfigActivity(Activity activity, int requestCode) {
            if (getUser().equals(Process.myUserHandle())) {
                return ShortcutConfigActivityInfo.super.startConfigActivity(activity, requestCode);
            }
            try {
                activity.startIntentSenderForResult(((LauncherApps) activity.getSystemService(LauncherApps.class)).getShortcutConfigActivityIntent(this.mInfo), requestCode, (Intent) null, 0, 0, 0);
                return true;
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(activity, R.string.activity_not_found, 0).show();
                return false;
            }
        }
    }
}
