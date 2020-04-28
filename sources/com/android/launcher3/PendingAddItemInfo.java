package com.android.launcher3;

import android.content.ComponentName;

public class PendingAddItemInfo extends ItemInfo {
    public ComponentName componentName;

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " componentName=" + this.componentName;
    }
}
