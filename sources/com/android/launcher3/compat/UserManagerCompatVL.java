package com.android.launcher3.compat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import com.android.launcher3.util.LongArrayMap;
import java.util.List;

public class UserManagerCompatVL extends UserManagerCompat {
    private static final String USER_CREATION_TIME_KEY = "user_creation_time_";
    private final Context mContext;
    private final PackageManager mPm;
    protected final UserManager mUserManager;
    protected ArrayMap<UserHandle, Long> mUserToSerialMap;
    protected LongArrayMap<UserHandle> mUsers;

    UserManagerCompatVL(Context context) {
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mPm = context.getPackageManager();
        this.mContext = context;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0017, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getSerialNumberForUser(android.os.UserHandle r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r0 = r3.mUserToSerialMap     // Catch:{ all -> 0x0020 }
            if (r0 == 0) goto L_0x0018
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r0 = r3.mUserToSerialMap     // Catch:{ all -> 0x0020 }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x0020 }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ all -> 0x0020 }
            if (r0 != 0) goto L_0x0012
            r1 = 0
            goto L_0x0016
        L_0x0012:
            long r1 = r0.longValue()     // Catch:{ all -> 0x0020 }
        L_0x0016:
            monitor-exit(r3)     // Catch:{ all -> 0x0020 }
            return r1
        L_0x0018:
            monitor-exit(r3)     // Catch:{ all -> 0x0020 }
            android.os.UserManager r0 = r3.mUserManager
            long r0 = r0.getSerialNumberForUser(r4)
            return r0
        L_0x0020:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0020 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.UserManagerCompatVL.getSerialNumberForUser(android.os.UserHandle):long");
    }

    public UserHandle getUserForSerialNumber(long serialNumber) {
        synchronized (this) {
            if (this.mUsers == null) {
                return this.mUserManager.getUserForSerialNumber(serialNumber);
            }
            UserHandle userHandle = (UserHandle) this.mUsers.get(serialNumber);
            return userHandle;
        }
    }

    public boolean isQuietModeEnabled(UserHandle user) {
        return false;
    }

    public boolean isUserUnlocked(UserHandle user) {
        return true;
    }

    public boolean isDemoUser() {
        return false;
    }

    public boolean requestQuietModeEnabled(boolean enableQuietMode, UserHandle user) {
        return false;
    }

    public boolean isAnyProfileQuietModeEnabled() {
        return false;
    }

    public void enableAndResetCache() {
        synchronized (this) {
            this.mUsers = new LongArrayMap<>();
            this.mUserToSerialMap = new ArrayMap<>();
            List<UserHandle> users = this.mUserManager.getUserProfiles();
            if (users != null) {
                for (UserHandle user : users) {
                    long serial = this.mUserManager.getSerialNumberForUser(user);
                    this.mUsers.put(serial, user);
                    this.mUserToSerialMap.put(user, Long.valueOf(serial));
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        return java.util.Collections.emptyList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r0 = r2.mUserManager.getUserProfiles();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        if (r0 != null) goto L_0x0020;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.os.UserHandle> getUserProfiles() {
        /*
            r2 = this;
            monitor-enter(r2)
            com.android.launcher3.util.LongArrayMap<android.os.UserHandle> r0 = r2.mUsers     // Catch:{ all -> 0x0022 }
            if (r0 == 0) goto L_0x0012
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0022 }
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r1 = r2.mUserToSerialMap     // Catch:{ all -> 0x0022 }
            java.util.Set r1 = r1.keySet()     // Catch:{ all -> 0x0022 }
            r0.<init>(r1)     // Catch:{ all -> 0x0022 }
            monitor-exit(r2)     // Catch:{ all -> 0x0022 }
            return r0
        L_0x0012:
            monitor-exit(r2)     // Catch:{ all -> 0x0022 }
            android.os.UserManager r0 = r2.mUserManager
            java.util.List r0 = r0.getUserProfiles()
            if (r0 != 0) goto L_0x0020
            java.util.List r1 = java.util.Collections.emptyList()
            goto L_0x0021
        L_0x0020:
            r1 = r0
        L_0x0021:
            return r1
        L_0x0022:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0022 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.UserManagerCompatVL.getUserProfiles():java.util.List");
    }

    public CharSequence getBadgedLabelForUser(CharSequence label, UserHandle user) {
        if (user == null) {
            return label;
        }
        return this.mPm.getUserBadgedLabel(label, user);
    }
}
