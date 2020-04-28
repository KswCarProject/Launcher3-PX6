package com.android.launcher3.dragndrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.WidgetCell;

public class LivePreviewWidgetCell extends WidgetCell {
    private RemoteViews mPreview;

    public LivePreviewWidgetCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public LivePreviewWidgetCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LivePreviewWidgetCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPreview(RemoteViews view) {
        this.mPreview = view;
    }

    public void ensurePreview() {
        Bitmap preview;
        if (this.mPreview == null || this.mActiveRequest != null || (preview = generateFromRemoteViews(this.mActivity, this.mPreview, this.mItem.widgetInfo, this.mPresetPreviewSize, new int[1])) == null) {
            super.ensurePreview();
        } else {
            applyPreview(preview);
        }
    }

    public static Bitmap generateFromRemoteViews(BaseActivity activity, RemoteViews views, LauncherAppWidgetProviderInfo info, int previewSize, int[] preScaledWidthOut) {
        int bitmapHeight;
        int bitmapWidth;
        float scale;
        DeviceProfile dp = activity.getDeviceProfile();
        int viewWidth = dp.cellWidthPx * info.spanX;
        int viewHeight = dp.cellHeightPx * info.spanY;
        try {
            View v = views.apply(activity, new FrameLayout(activity));
            v.measure(View.MeasureSpec.makeMeasureSpec(viewWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(viewHeight, 1073741824));
            int viewWidth2 = v.getMeasuredWidth();
            int viewHeight2 = v.getMeasuredHeight();
            v.layout(0, 0, viewWidth2, viewHeight2);
            preScaledWidthOut[0] = viewWidth2;
            if (viewWidth2 > previewSize) {
                scale = ((float) previewSize) / ((float) viewWidth2);
                bitmapWidth = previewSize;
                bitmapHeight = (int) (((float) viewHeight2) * scale);
            } else {
                scale = 1.0f;
                bitmapWidth = viewWidth2;
                bitmapHeight = viewHeight2;
            }
            Bitmap preview = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(preview);
            c.scale(scale, scale);
            v.draw(c);
            c.setBitmap((Bitmap) null);
            return preview;
        } catch (Exception e) {
            return null;
        }
    }
}
