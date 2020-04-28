package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.UserHandle;

@TargetApi(28)
public class UserManagerCompatVP extends UserManagerCompatVNMr1 {
    UserManagerCompatVP(Context context) {
        super(context);
    }

    public boolean requestQuietModeEnabled(boolean enableQuietMode, UserHandle user) {
        return this.mUserManager.requestQuietModeEnabled(enableQuietMode, user);
    }
}
