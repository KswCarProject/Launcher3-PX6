package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Process;
import android.os.UserHandle;

@TargetApi(24)
public class UserManagerCompatVN extends UserManagerCompatVM {
    UserManagerCompatVN(Context context) {
        super(context);
    }

    public boolean isQuietModeEnabled(UserHandle user) {
        return this.mUserManager.isQuietModeEnabled(user);
    }

    public boolean isUserUnlocked(UserHandle user) {
        return this.mUserManager.isUserUnlocked(user);
    }

    public boolean isAnyProfileQuietModeEnabled() {
        for (UserHandle userProfile : getUserProfiles()) {
            if (!Process.myUserHandle().equals(userProfile) && isQuietModeEnabled(userProfile)) {
                return true;
            }
        }
        return false;
    }
}
