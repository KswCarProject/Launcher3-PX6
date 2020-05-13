package com.szchoiceway.index;

import android.graphics.Rect;

/* compiled from: WidgetPreviewLoader */
class RectCache extends SoftReferenceThreadLocal<Rect> {
    RectCache() {
    }

    /* access modifiers changed from: protected */
    public Rect initialValue() {
        return new Rect();
    }
}
