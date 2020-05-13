package com.szchoiceway.index;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Bitmap;

public abstract class CoverFlowAdapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public abstract int getCount();

    public abstract Bitmap getImage(int i);

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        this.mDataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        this.mDataSetObservable.notifyInvalidated();
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }
}
