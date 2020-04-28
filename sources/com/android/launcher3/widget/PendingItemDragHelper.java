package com.android.launcher3.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.LivePreviewWidgetCell;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.graphics.LauncherIcons;

public class PendingItemDragHelper extends DragPreviewProvider {
    private static final float MAX_WIDGET_SCALE = 1.25f;
    private final PendingAddItemInfo mAddInfo;
    private int[] mEstimatedCellSize;
    private RemoteViews mPreview;

    public PendingItemDragHelper(View view) {
        super(view);
        this.mAddInfo = (PendingAddItemInfo) view.getTag();
    }

    public void setPreview(RemoteViews preview) {
        this.mPreview = preview;
    }

    public void startDrag(Rect previewBounds, int previewBitmapWidth, int previewViewWidth, Point screenPos, DragSource source, DragOptions options) {
        Bitmap preview;
        Rect dragRegion;
        Point dragOffset;
        float scale;
        int[] previewSizeBeforeScale;
        Rect rect = previewBounds;
        int i = previewBitmapWidth;
        int i2 = previewViewWidth;
        Point point = screenPos;
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        LauncherAppState app = LauncherAppState.getInstance(launcher);
        Bitmap preview2 = null;
        this.mEstimatedCellSize = launcher.getWorkspace().estimateItemSize(this.mAddInfo);
        if (this.mAddInfo instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo createWidgetInfo = (PendingAddWidgetInfo) this.mAddInfo;
            int maxWidth = Math.min((int) (((float) i) * MAX_WIDGET_SCALE), this.mEstimatedCellSize[0]);
            int[] previewSizeBeforeScale2 = new int[1];
            if (this.mPreview != null) {
                preview2 = LivePreviewWidgetCell.generateFromRemoteViews(launcher, this.mPreview, createWidgetInfo.info, maxWidth, previewSizeBeforeScale2);
            }
            preview = preview2;
            if (preview == null) {
                previewSizeBeforeScale = previewSizeBeforeScale2;
                preview = app.getWidgetCache().generateWidgetPreview(launcher, createWidgetInfo.info, maxWidth, (Bitmap) null, previewSizeBeforeScale2);
            } else {
                previewSizeBeforeScale = previewSizeBeforeScale2;
            }
            if (previewSizeBeforeScale[0] < i) {
                int padding = (i - previewSizeBeforeScale[0]) / 2;
                if (i > i2) {
                    padding = (padding * i2) / i;
                }
                rect.left += padding;
                rect.right -= padding;
            }
            scale = ((float) previewBounds.width()) / ((float) preview.getWidth());
            launcher.getDragController().addDragListener(new WidgetHostViewLoader(launcher, this.mView));
            dragOffset = null;
            dragRegion = null;
        } else {
            Drawable icon = ((PendingAddShortcutInfo) this.mAddInfo).activityInfo.getFullResIcon(app.getIconCache());
            LauncherIcons li = LauncherIcons.obtain(launcher);
            preview = li.createScaledBitmapWithoutShadow(icon, 0);
            li.recycle();
            scale = ((float) launcher.getDeviceProfile().iconSizePx) / ((float) preview.getWidth());
            dragOffset = new Point(this.previewPadding / 2, this.previewPadding / 2);
            DeviceProfile dp = launcher.getDeviceProfile();
            int iconSize = dp.iconSizePx;
            int padding2 = launcher.getResources().getDimensionPixelSize(R.dimen.widget_preview_shortcut_padding);
            rect.left += padding2;
            rect.top += padding2;
            dragRegion = new Rect();
            dragRegion.left = (this.mEstimatedCellSize[0] - iconSize) / 2;
            dragRegion.right = dragRegion.left + iconSize;
            dragRegion.top = (((this.mEstimatedCellSize[1] - iconSize) - dp.iconTextSizePx) - dp.iconDrawablePaddingPx) / 2;
            dragRegion.bottom = dragRegion.top + iconSize;
        }
        launcher.getWorkspace().prepareDragWithProvider(this);
        launcher.getDragController().startDrag(preview, point.x + rect.left + ((int) (((((float) preview.getWidth()) * scale) - ((float) preview.getWidth())) / 2.0f)), point.y + rect.top + ((int) (((((float) preview.getHeight()) * scale) - ((float) preview.getHeight())) / 2.0f)), source, this.mAddInfo, dragOffset, dragRegion, scale, scale, options);
    }

    /* access modifiers changed from: protected */
    public Bitmap convertPreviewToAlphaBitmap(Bitmap preview) {
        if ((this.mAddInfo instanceof PendingAddShortcutInfo) || this.mEstimatedCellSize == null) {
            return super.convertPreviewToAlphaBitmap(preview);
        }
        int w = this.mEstimatedCellSize[0];
        int h = this.mEstimatedCellSize[1];
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        Rect src = new Rect(0, 0, preview.getWidth(), preview.getHeight());
        float scaleFactor = Math.min(((float) (w - this.blurSizeOutline)) / ((float) preview.getWidth()), ((float) (h - this.blurSizeOutline)) / ((float) preview.getHeight()));
        int scaledWidth = (int) (((float) preview.getWidth()) * scaleFactor);
        int scaledHeight = (int) (((float) preview.getHeight()) * scaleFactor);
        Rect dst = new Rect(0, 0, scaledWidth, scaledHeight);
        dst.offset((w - scaledWidth) / 2, (h - scaledHeight) / 2);
        new Canvas(b).drawBitmap(preview, src, dst, new Paint(2));
        return b;
    }
}
