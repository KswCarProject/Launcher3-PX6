package com.android.launcher3.widget;

import android.support.v7.widget.RecyclerView;
import com.android.launcher3.IconCache;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.widget.WidgetsListAdapter;
import java.util.ArrayList;
import java.util.Iterator;

public class WidgetsDiffReporter {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsDiffReporter";
    private final IconCache mIconCache;
    private final RecyclerView.Adapter mListener;

    public WidgetsDiffReporter(IconCache iconCache, RecyclerView.Adapter listener) {
        this.mIconCache = iconCache;
        this.mListener = listener;
    }

    public void process(ArrayList<WidgetListRowEntry> currentEntries, ArrayList<WidgetListRowEntry> newEntries, WidgetsListAdapter.WidgetListRowEntryComparator comparator) {
        int i;
        if (!currentEntries.isEmpty() && !newEntries.isEmpty()) {
            Iterator<WidgetListRowEntry> orgIter = ((ArrayList) currentEntries.clone()).iterator();
            Iterator<WidgetListRowEntry> newIter = newEntries.iterator();
            WidgetListRowEntry orgRowEntry = orgIter.next();
            WidgetListRowEntry newRowEntry = newIter.next();
            while (true) {
                int diff = comparePackageName(orgRowEntry, newRowEntry, comparator);
                WidgetListRowEntry widgetListRowEntry = null;
                if (diff < 0) {
                    int index = currentEntries.indexOf(orgRowEntry);
                    this.mListener.notifyItemRemoved(index);
                    currentEntries.remove(index);
                    if (orgIter.hasNext()) {
                        widgetListRowEntry = orgIter.next();
                    }
                    orgRowEntry = widgetListRowEntry;
                } else if (diff > 0) {
                    if (orgRowEntry != null) {
                        i = currentEntries.indexOf(orgRowEntry);
                    } else {
                        i = currentEntries.size();
                    }
                    int index2 = i;
                    currentEntries.add(index2, newRowEntry);
                    if (newIter.hasNext()) {
                        widgetListRowEntry = newIter.next();
                    }
                    newRowEntry = widgetListRowEntry;
                    this.mListener.notifyItemInserted(index2);
                } else {
                    if (!isSamePackageItemInfo(orgRowEntry.pkgItem, newRowEntry.pkgItem) || !orgRowEntry.widgets.equals(newRowEntry.widgets)) {
                        int index3 = currentEntries.indexOf(orgRowEntry);
                        currentEntries.set(index3, newRowEntry);
                        this.mListener.notifyItemChanged(index3);
                    }
                    orgRowEntry = orgIter.hasNext() ? orgIter.next() : null;
                    if (newIter.hasNext()) {
                        widgetListRowEntry = newIter.next();
                    }
                    newRowEntry = widgetListRowEntry;
                }
                if (orgRowEntry == null && newRowEntry == null) {
                    return;
                }
            }
        } else if (currentEntries.size() != newEntries.size()) {
            currentEntries.clear();
            currentEntries.addAll(newEntries);
            this.mListener.notifyDataSetChanged();
        }
    }

    private int comparePackageName(WidgetListRowEntry curRow, WidgetListRowEntry newRow, WidgetsListAdapter.WidgetListRowEntryComparator comparator) {
        if (curRow == null && newRow == null) {
            throw new IllegalStateException("Cannot compare PackageItemInfo if both rows are null.");
        } else if (curRow == null && newRow != null) {
            return 1;
        } else {
            if (curRow == null || newRow != null) {
                return comparator.compare(curRow, newRow);
            }
            return -1;
        }
    }

    private boolean isSamePackageItemInfo(PackageItemInfo curInfo, PackageItemInfo newInfo) {
        return curInfo.iconBitmap.equals(newInfo.iconBitmap) && !this.mIconCache.isDefaultIcon(curInfo.iconBitmap, curInfo.user);
    }
}
