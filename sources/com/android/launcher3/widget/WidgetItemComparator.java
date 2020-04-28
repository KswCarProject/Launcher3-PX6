package com.android.launcher3.widget;

import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.model.WidgetItem;
import java.text.Collator;
import java.util.Comparator;

public class WidgetItemComparator implements Comparator<WidgetItem> {
    private final Collator mCollator = Collator.getInstance();
    private final UserHandle mMyUserHandle = Process.myUserHandle();

    public int compare(WidgetItem a, WidgetItem b) {
        boolean thisWorkProfile = !this.mMyUserHandle.equals(a.user);
        if (!(thisWorkProfile ^ (!this.mMyUserHandle.equals(b.user)))) {
            int labelCompare = this.mCollator.compare(a.label, b.label);
            if (labelCompare != 0) {
                return labelCompare;
            }
            int thisArea = a.spanX * a.spanY;
            int otherArea = b.spanX * b.spanY;
            if (thisArea == otherArea) {
                return Integer.compare(a.spanY, b.spanY);
            }
            return Integer.compare(thisArea, otherArea);
        } else if (thisWorkProfile) {
            return 1;
        } else {
            return -1;
        }
    }
}
