package com.android.launcher3.allapps;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.AppInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.LabelComparator;
import java.util.Comparator;

public class AppInfoComparator implements Comparator<AppInfo> {
    private final LabelComparator mLabelComparator = new LabelComparator();
    private final UserHandle mMyUser = Process.myUserHandle();
    private final UserManagerCompat mUserManager;

    public AppInfoComparator(Context context) {
        this.mUserManager = UserManagerCompat.getInstance(context);
    }

    public int compare(AppInfo a, AppInfo b) {
        int result = this.mLabelComparator.compare(a.title.toString(), b.title.toString());
        if (result != 0) {
            return result;
        }
        int result2 = a.componentName.compareTo(b.componentName);
        if (result2 != 0) {
            return result2;
        }
        if (this.mMyUser.equals(a.user)) {
            return -1;
        }
        return Long.valueOf(this.mUserManager.getSerialNumberForUser(a.user)).compareTo(Long.valueOf(this.mUserManager.getSerialNumberForUser(b.user)));
    }
}
