package com.android.launcher3.folder;

import android.graphics.drawable.Drawable;

class PreviewItemDrawingParams {
    FolderPreviewItemAnim anim;
    Drawable drawable;
    public boolean hidden;
    float overlayAlpha;
    float scale;
    float transX;
    float transY;

    PreviewItemDrawingParams(float transX2, float transY2, float scale2, float overlayAlpha2) {
        this.transX = transX2;
        this.transY = transY2;
        this.scale = scale2;
        this.overlayAlpha = overlayAlpha2;
    }

    public void update(float transX2, float transY2, float scale2) {
        if (this.anim != null) {
            if (this.anim.finalTransX != transX2 && this.anim.finalTransY != transY2 && this.anim.finalScale != scale2) {
                this.anim.cancel();
            } else {
                return;
            }
        }
        this.transX = transX2;
        this.transY = transY2;
        this.scale = scale2;
    }
}
