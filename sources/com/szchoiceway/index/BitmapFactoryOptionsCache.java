package com.szchoiceway.index;

import android.graphics.BitmapFactory;

/* compiled from: WidgetPreviewLoader */
class BitmapFactoryOptionsCache extends SoftReferenceThreadLocal<BitmapFactory.Options> {
    BitmapFactoryOptionsCache() {
    }

    /* access modifiers changed from: protected */
    public BitmapFactory.Options initialValue() {
        return new BitmapFactory.Options();
    }
}
