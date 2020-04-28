package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(23)
public class UserManagerCompatVM extends UserManagerCompatVL {
    UserManagerCompatVM(Context context) {
        super(context);
    }
}
