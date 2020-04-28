package com.android.launcher3.widget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RemoteViews;
import com.android.launcher3.R;

public class DeferredAppWidgetHostView extends LauncherAppWidgetHostView {
    private final TextPaint mPaint = new TextPaint();
    private Layout mSetupTextLayout;

    public DeferredAppWidgetHostView(Context context) {
        super(context);
        setWillNotDraw(false);
        this.mPaint.setColor(-1);
        this.mPaint.setTextSize(TypedValue.applyDimension(0, (float) this.mLauncher.getDeviceProfile().getFullScreenProfile().iconTextSizePx, getResources().getDisplayMetrics()));
        setBackgroundResource(R.drawable.bg_deferred_app_widget);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        AppWidgetProviderInfo info = getAppWidgetInfo();
        if (info != null && !TextUtils.isEmpty(info.label)) {
            int availableWidth = getMeasuredWidth() - ((getPaddingLeft() + getPaddingRight()) * 2);
            if (this.mSetupTextLayout == null || !this.mSetupTextLayout.getText().equals(info.label) || this.mSetupTextLayout.getWidth() != availableWidth) {
                this.mSetupTextLayout = new StaticLayout(info.label, this.mPaint, availableWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mSetupTextLayout != null) {
            canvas.translate((float) (getPaddingLeft() * 2), (float) ((getHeight() - this.mSetupTextLayout.getHeight()) / 2));
            this.mSetupTextLayout.draw(canvas);
        }
    }
}
