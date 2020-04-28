package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(25)
public class UserManagerCompatVNMr1 extends UserManagerCompatVN {
    UserManagerCompatVNMr1(Context context) {
        super(context);
    }

    public boolean isDemoUser() {
        return this.mUserManager.isDemoUser();
    }
}
