package com.szchoiceway.index;

import android.graphics.Canvas;

/* compiled from: WidgetPreviewLoader */
class CanvasCache extends SoftReferenceThreadLocal<Canvas> {
    CanvasCache() {
    }

    /* access modifiers changed from: protected */
    public Canvas initialValue() {
        return new Canvas();
    }
}
