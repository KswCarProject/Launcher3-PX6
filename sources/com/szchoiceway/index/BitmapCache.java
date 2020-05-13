package com.szchoiceway.index;

import android.graphics.Bitmap;

/* compiled from: WidgetPreviewLoader */
class BitmapCache extends SoftReferenceThreadLocal<Bitmap> {
    BitmapCache() {
    }

    /* access modifiers changed from: protected */
    public Bitmap initialValue() {
        return null;
    }
}
