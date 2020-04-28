package com.android.launcher3.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;

public class WidgetsRowViewHolder extends RecyclerView.ViewHolder {
    public final ViewGroup cellContainer;
    public final BubbleTextView title;

    public WidgetsRowViewHolder(ViewGroup v) {
        super(v);
        this.cellContainer = (ViewGroup) v.findViewById(R.id.widgets_cell_list);
        this.title = (BubbleTextView) v.findViewById(R.id.section);
        this.title.setAccessibilityDelegate((View.AccessibilityDelegate) null);
    }
}
