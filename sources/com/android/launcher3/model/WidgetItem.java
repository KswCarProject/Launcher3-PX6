package com.android.launcher3.model;

import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.util.ComponentKey;
import java.text.Collator;

public class WidgetItem extends ComponentKey implements Comparable<WidgetItem> {
    private static Collator sCollator;
    private static UserHandle sMyUserHandle;
    public final ShortcutConfigActivityInfo activityInfo;
    public final String label;
    public final int spanX;
    public final int spanY;
    public final LauncherAppWidgetProviderInfo widgetInfo;

    public WidgetItem(LauncherAppWidgetProviderInfo info, PackageManager pm, InvariantDeviceProfile idp) {
        super(info.provider, info.getProfile());
        this.label = Utilities.trim(info.getLabel(pm));
        this.widgetInfo = info;
        this.activityInfo = null;
        this.spanX = Math.min(info.spanX, idp.numColumns);
        this.spanY = Math.min(info.spanY, idp.numRows);
    }

    public WidgetItem(ShortcutConfigActivityInfo info) {
        super(info.getComponent(), info.getUser());
        this.label = Utilities.trim(info.getLabel());
        this.widgetInfo = null;
        this.activityInfo = info;
        this.spanY = 1;
        this.spanX = 1;
    }

    public int compareTo(WidgetItem another) {
        if (sMyUserHandle == null) {
            sMyUserHandle = Process.myUserHandle();
            sCollator = Collator.getInstance();
        }
        boolean thisWorkProfile = !sMyUserHandle.equals(this.user);
        if (!(thisWorkProfile ^ (!sMyUserHandle.equals(another.user)))) {
            int labelCompare = sCollator.compare(this.label, another.label);
            if (labelCompare != 0) {
                return labelCompare;
            }
            int thisArea = this.spanX * this.spanY;
            int otherArea = another.spanX * another.spanY;
            if (thisArea == otherArea) {
                return Integer.compare(this.spanY, another.spanY);
            }
            return Integer.compare(thisArea, otherArea);
        } else if (thisWorkProfile) {
            return 1;
        } else {
            return -1;
        }
    }
}
