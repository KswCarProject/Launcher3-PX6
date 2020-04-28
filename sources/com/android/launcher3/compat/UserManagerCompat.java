package com.android.launcher3.compat;

import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.Utilities;
import java.util.List;

public abstract class UserManagerCompat {
    private static UserManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public abstract void enableAndResetCache();

    public abstract CharSequence getBadgedLabelForUser(CharSequence charSequence, UserHandle userHandle);

    public abstract long getSerialNumberForUser(UserHandle userHandle);

    public abstract UserHandle getUserForSerialNumber(long j);

    public abstract List<UserHandle> getUserProfiles();

    public abstract boolean isAnyProfileQuietModeEnabled();

    public abstract boolean isDemoUser();

    public abstract boolean isQuietModeEnabled(UserHandle userHandle);

    public abstract boolean isUserUnlocked(UserHandle userHandle);

    public abstract boolean requestQuietModeEnabled(boolean z, UserHandle userHandle);

    protected UserManagerCompat() {
    }

    public static UserManagerCompat getInstance(Context context) {
        UserManagerCompat userManagerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.ATLEAST_P) {
                    sInstance = new UserManagerCompatVP(context.getApplicationContext());
                } else if (Utilities.ATLEAST_NOUGAT_MR1) {
                    sInstance = new UserManagerCompatVNMr1(context.getApplicationContext());
                } else if (Utilities.ATLEAST_NOUGAT) {
                    sInstance = new UserManagerCompatVN(context.getApplicationContext());
                } else if (Utilities.ATLEAST_MARSHMALLOW) {
                    sInstance = new UserManagerCompatVM(context.getApplicationContext());
                } else {
                    sInstance = new UserManagerCompatVL(context.getApplicationContext());
                }
            }
            userManagerCompat = sInstance;
        }
        return userManagerCompat;
    }
}
