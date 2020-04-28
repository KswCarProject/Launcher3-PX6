package com.android.launcher3.model;

import com.android.launcher3.ItemInfoWithIcon;

public class PackageItemInfo extends ItemInfoWithIcon {
    public String packageName;

    public PackageItemInfo(String packageName2) {
        this.packageName = packageName2;
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " packageName=" + this.packageName;
    }
}
