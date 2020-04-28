package com.android.launcher3.shortcuts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.DragPreviewProvider;

public class ShortcutDragPreviewProvider extends DragPreviewProvider {
    private final Point mPositionShift;

    public ShortcutDragPreviewProvider(View icon, Point shift) {
        super(icon);
        this.mPositionShift = shift;
    }

    public Bitmap createDragBitmap() {
        Drawable d = this.mView.getBackground();
        Rect bounds = getDrawableBounds(d);
        int size = Launcher.getLauncher(this.mView.getContext()).getDeviceProfile().iconSizePx;
        Bitmap b = Bitmap.createBitmap(this.blurSizeOutline + size, this.blurSizeOutline + size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.translate((float) (this.blurSizeOutline / 2), (float) (this.blurSizeOutline / 2));
        canvas.scale(((float) size) / ((float) bounds.width()), ((float) size) / ((float) bounds.height()), 0.0f, 0.0f);
        canvas.translate((float) bounds.left, (float) bounds.top);
        d.draw(canvas);
        return b;
    }

    public float getScaleAndPosition(Bitmap preview, int[] outPos) {
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        int iconSize = getDrawableBounds(this.mView.getBackground()).width();
        float scale = launcher.getDragLayer().getLocationInDragLayer(this.mView, outPos);
        int iconLeft = this.mView.getPaddingStart();
        if (Utilities.isRtl(this.mView.getResources())) {
            iconLeft = (this.mView.getWidth() - iconSize) - iconLeft;
        }
        outPos[0] = outPos[0] + Math.round((((float) iconLeft) * scale) + (((((float) iconSize) * scale) - ((float) preview.getWidth())) / 2.0f) + ((float) this.mPositionShift.x));
        outPos[1] = outPos[1] + Math.round((((((float) this.mView.getHeight()) * scale) - ((float) preview.getHeight())) / 2.0f) + ((float) this.mPositionShift.y));
        return (((float) iconSize) * scale) / ((float) launcher.getDeviceProfile().iconSizePx);
    }
}
