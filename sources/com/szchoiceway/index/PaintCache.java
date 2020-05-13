package com.szchoiceway.index;

import android.graphics.Paint;

/* compiled from: WidgetPreviewLoader */
class PaintCache extends SoftReferenceThreadLocal<Paint> {
    PaintCache() {
    }

    /* access modifiers changed from: protected */
    public Paint initialValue() {
        return null;
    }
}
