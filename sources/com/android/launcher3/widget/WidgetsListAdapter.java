package com.android.launcher3.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.IconCache;
import com.android.launcher3.R;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.util.LabelComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WidgetsListAdapter extends RecyclerView.Adapter<WidgetsRowViewHolder> {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsListAdapter";
    private boolean mApplyBitmapDeferred;
    private final WidgetsDiffReporter mDiffReporter;
    private ArrayList<WidgetListRowEntry> mEntries = new ArrayList<>();
    private final View.OnClickListener mIconClickListener;
    private final View.OnLongClickListener mIconLongClickListener;
    private final int mIndent;
    private final LayoutInflater mLayoutInflater;
    private final WidgetPreviewLoader mWidgetPreviewLoader;

    public WidgetsListAdapter(Context context, LayoutInflater layoutInflater, WidgetPreviewLoader widgetPreviewLoader, IconCache iconCache, View.OnClickListener iconClickListener, View.OnLongClickListener iconLongClickListener) {
        this.mLayoutInflater = layoutInflater;
        this.mWidgetPreviewLoader = widgetPreviewLoader;
        this.mIconClickListener = iconClickListener;
        this.mIconLongClickListener = iconLongClickListener;
        this.mIndent = context.getResources().getDimensionPixelSize(R.dimen.widget_section_indent);
        this.mDiffReporter = new WidgetsDiffReporter(iconCache, this);
    }

    public void setApplyBitmapDeferred(boolean isDeferred, RecyclerView rv) {
        this.mApplyBitmapDeferred = isDeferred;
        for (int i = rv.getChildCount() - 1; i >= 0; i--) {
            WidgetsRowViewHolder holder = (WidgetsRowViewHolder) rv.getChildViewHolder(rv.getChildAt(i));
            for (int j = holder.cellContainer.getChildCount() - 1; j >= 0; j--) {
                View v = holder.cellContainer.getChildAt(j);
                if (v instanceof WidgetCell) {
                    ((WidgetCell) v).setApplyBitmapDeferred(this.mApplyBitmapDeferred);
                }
            }
        }
    }

    public void setWidgets(ArrayList<WidgetListRowEntry> tempEntries) {
        WidgetListRowEntryComparator rowComparator = new WidgetListRowEntryComparator();
        Collections.sort(tempEntries, rowComparator);
        this.mDiffReporter.process(this.mEntries, tempEntries, rowComparator);
    }

    public int getItemCount() {
        return this.mEntries.size();
    }

    public String getSectionName(int pos) {
        return this.mEntries.get(pos).titleSectionName;
    }

    public void onBindViewHolder(WidgetsRowViewHolder holder, int pos) {
        WidgetListRowEntry entry = this.mEntries.get(pos);
        List<WidgetItem> infoList = entry.widgets;
        ViewGroup row = holder.cellContainer;
        int expectedChildCount = infoList.size() + Math.max(0, infoList.size() - 1);
        int childCount = row.getChildCount();
        if (expectedChildCount > childCount) {
            for (int i = childCount; i < expectedChildCount; i++) {
                if ((i & 1) == 1) {
                    this.mLayoutInflater.inflate(R.layout.widget_list_divider, row);
                } else {
                    WidgetCell widget = (WidgetCell) this.mLayoutInflater.inflate(R.layout.widget_cell, row, false);
                    widget.setOnClickListener(this.mIconClickListener);
                    widget.setOnLongClickListener(this.mIconLongClickListener);
                    row.addView(widget);
                }
            }
        } else if (expectedChildCount < childCount) {
            for (int i2 = expectedChildCount; i2 < childCount; i2++) {
                row.getChildAt(i2).setVisibility(8);
            }
        }
        holder.title.applyFromPackageItemInfo(entry.pkgItem);
        for (int i3 = 0; i3 < infoList.size(); i3++) {
            WidgetCell widget2 = (WidgetCell) row.getChildAt(i3 * 2);
            widget2.applyFromCellItem(infoList.get(i3), this.mWidgetPreviewLoader);
            widget2.setApplyBitmapDeferred(this.mApplyBitmapDeferred);
            widget2.ensurePreview();
            widget2.setVisibility(0);
            if (i3 > 0) {
                row.getChildAt((i3 * 2) - 1).setVisibility(0);
            }
        }
    }

    public WidgetsRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup container = (ViewGroup) this.mLayoutInflater.inflate(R.layout.widgets_list_row_view, parent, false);
        container.findViewById(R.id.widgets_cell_list).setPaddingRelative(this.mIndent, 0, 1, 0);
        return new WidgetsRowViewHolder(container);
    }

    public void onViewRecycled(WidgetsRowViewHolder holder) {
        int total = holder.cellContainer.getChildCount();
        for (int i = 0; i < total; i += 2) {
            ((WidgetCell) holder.cellContainer.getChildAt(i)).clear();
        }
    }

    public boolean onFailedToRecycleView(WidgetsRowViewHolder holder) {
        return true;
    }

    public long getItemId(int pos) {
        return (long) pos;
    }

    public static class WidgetListRowEntryComparator implements Comparator<WidgetListRowEntry> {
        private final LabelComparator mComparator = new LabelComparator();

        public int compare(WidgetListRowEntry a, WidgetListRowEntry b) {
            return this.mComparator.compare(a.pkgItem.title.toString(), b.pkgItem.title.toString());
        }
    }
}
