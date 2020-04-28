package com.android.launcher3.widget;

import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import java.util.ArrayList;

public class WidgetListRowEntry {
    public final PackageItemInfo pkgItem;
    public String titleSectionName;
    public final ArrayList<WidgetItem> widgets;

    public WidgetListRowEntry(PackageItemInfo pkgItem2, ArrayList<WidgetItem> items) {
        this.pkgItem = pkgItem2;
        this.widgets = items;
    }

    public String toString() {
        return this.pkgItem.packageName + ":" + this.widgets.size();
    }
}
