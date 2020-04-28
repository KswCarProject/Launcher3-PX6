package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;

@TargetApi(26)
public class LauncherAppsCompatVO extends LauncherAppsCompatVL {
    LauncherAppsCompatVO(Context context) {
        super(context);
    }

    public ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user) {
        try {
            ApplicationInfo info = this.mLauncherApps.getApplicationInfo(packageName, flags, user);
            if ((info.flags & 8388608) == 0 || !info.enabled) {
                return null;
            }
            return info;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUser) {
        String packageName;
        List<UserHandle> users;
        List<ShortcutConfigActivityInfo> result = new ArrayList<>();
        UserHandle myUser = Process.myUserHandle();
        if (packageUser == null) {
            users = UserManagerCompat.getInstance(this.mContext).getUserProfiles();
            packageName = null;
        } else {
            users = new ArrayList<>(1);
            users.add(packageUser.mUser);
            packageName = packageUser.mPackageName;
        }
        for (UserHandle user : users) {
            boolean ignoreTargetSdk = myUser.equals(user);
            for (LauncherActivityInfo activityInfo : this.mLauncherApps.getShortcutConfigActivityList(packageName, user)) {
                if (ignoreTargetSdk || activityInfo.getApplicationInfo().targetSdkVersion >= 26) {
                    result.add(new ShortcutConfigActivityInfo.ShortcutConfigActivityInfoVO(activityInfo));
                }
            }
        }
        return result;
    }

    @Nullable
    public static ShortcutInfo createShortcutInfoFromPinItemRequest(Context context, final LauncherApps.PinItemRequest request, final long acceptDelay) {
        if (request == null || request.getRequestType() != 1 || !request.isValid()) {
            return null;
        }
        if (acceptDelay > 0) {
            new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(acceptDelay);
                    } catch (InterruptedException e) {
                    }
                    if (request.isValid()) {
                        request.accept();
                    }
                }
            });
        } else if (!request.accept()) {
            return null;
        }
        ShortcutInfoCompat compat = new ShortcutInfoCompat(request.getShortcutInfo());
        ShortcutInfo info = new ShortcutInfo(compat, context);
        LauncherIcons li = LauncherIcons.obtain(context);
        li.createShortcutIcon(compat, false).applyTo((ItemInfoWithIcon) info);
        li.recycle();
        LauncherAppState.getInstance(context).getModel().updateAndBindShortcutInfo(info, compat);
        return info;
    }

    public static LauncherApps.PinItemRequest getPinItemRequest(Intent intent) {
        Parcelable extra = intent.getParcelableExtra("android.content.pm.extra.PIN_ITEM_REQUEST");
        if (extra instanceof LauncherApps.PinItemRequest) {
            return (LauncherApps.PinItemRequest) extra;
        }
        return null;
    }
}
