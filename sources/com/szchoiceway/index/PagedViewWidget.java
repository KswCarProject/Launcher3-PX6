package com.szchoiceway.index;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PagedViewWidget extends LinearLayout {
    static final String TAG = "PagedViewWidgetLayout";
    private static boolean sDeletePreviewsWhenDetachedFromWindow = true;
    private static boolean sRecyclePreviewsWhenDetachedFromWindow = true;
    static PagedViewWidget sShortpressTarget = null;
    private String mDimensionsFormatString;
    private Object mInfo;
    boolean mIsAppWidget;
    private final Rect mOriginalImagePadding;
    CheckForShortPress mPendingCheckForShortPress;
    ShortPressListener mShortPressListener;
    boolean mShortPressTriggered;
    private WidgetPreviewLoader mWidgetPreviewLoader;

    interface ShortPressListener {
        void cleanUpShortPress(View view);

        void onShortPress(View view);
    }

    public PagedViewWidget(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagedViewWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPendingCheckForShortPress = null;
        this.mShortPressListener = null;
        this.mShortPressTriggered = false;
        this.mOriginalImagePadding = new Rect();
        this.mDimensionsFormatString = context.getResources().getString(R.string.widget_dims_format);
        setWillNotDraw(false);
        setClipToPadding(false);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ImageView image = (ImageView) findViewById(R.id.widget_preview);
        this.mOriginalImagePadding.left = image.getPaddingLeft();
        this.mOriginalImagePadding.top = image.getPaddingTop();
        this.mOriginalImagePadding.right = image.getPaddingRight();
        this.mOriginalImagePadding.bottom = image.getPaddingBottom();
    }

    public static void setDeletePreviewsWhenDetachedFromWindow(boolean value) {
        sDeletePreviewsWhenDetachedFromWindow = value;
    }

    public static void setRecyclePreviewsWhenDetachedFromWindow(boolean value) {
        sRecyclePreviewsWhenDetachedFromWindow = value;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        ImageView image;
        super.onDetachedFromWindow();
        if (sDeletePreviewsWhenDetachedFromWindow && (image = (ImageView) findViewById(R.id.widget_preview)) != null) {
            FastBitmapDrawable preview = (FastBitmapDrawable) image.getDrawable();
            if (!(!sRecyclePreviewsWhenDetachedFromWindow || this.mInfo == null || preview == null || preview.getBitmap() == null)) {
                this.mWidgetPreviewLoader.recycleBitmap(this.mInfo, preview.getBitmap());
            }
            image.setImageDrawable((Drawable) null);
        }
    }

    public void applyFromAppWidgetProviderInfo(AppWidgetProviderInfo info, int maxWidth, int[] cellSpan, WidgetPreviewLoader loader) {
        this.mIsAppWidget = true;
        this.mInfo = info;
        ImageView image = (ImageView) findViewById(R.id.widget_preview);
        if (maxWidth > -1) {
            image.setMaxWidth(maxWidth);
        }
        ((TextView) findViewById(R.id.widget_name)).setText(info.label);
        TextView dims = (TextView) findViewById(R.id.widget_dims);
        if (dims != null) {
            int hSpan = Math.min(cellSpan[0], LauncherModel.getCellCountX());
            int vSpan = Math.min(cellSpan[1], LauncherModel.getCellCountY());
            dims.setText(String.format(this.mDimensionsFormatString, new Object[]{Integer.valueOf(hSpan), Integer.valueOf(vSpan)}));
        }
        this.mWidgetPreviewLoader = loader;
    }

    public void applyFromResolveInfo(PackageManager pm, ResolveInfo info, WidgetPreviewLoader loader) {
        this.mIsAppWidget = false;
        this.mInfo = info;
        ((TextView) findViewById(R.id.widget_name)).setText(info.loadLabel(pm));
        TextView dims = (TextView) findViewById(R.id.widget_dims);
        if (dims != null) {
            dims.setText(String.format(this.mDimensionsFormatString, new Object[]{1, 1}));
        }
        this.mWidgetPreviewLoader = loader;
    }

    public int[] getPreviewSize() {
        ImageView i = (ImageView) findViewById(R.id.widget_preview);
        return new int[]{(i.getWidth() - this.mOriginalImagePadding.left) - this.mOriginalImagePadding.right, i.getHeight() - this.mOriginalImagePadding.top};
    }

    /* access modifiers changed from: package-private */
    public void applyPreview(FastBitmapDrawable preview, int index) {
        PagedViewWidgetImageView image = (PagedViewWidgetImageView) findViewById(R.id.widget_preview);
        if (preview != null) {
            image.mAllowRequestLayout = false;
            image.setImageDrawable(preview);
            if (this.mIsAppWidget) {
                image.setPadding(this.mOriginalImagePadding.left + ((getPreviewSize()[0] - preview.getIntrinsicWidth()) / 2), this.mOriginalImagePadding.top, this.mOriginalImagePadding.right, this.mOriginalImagePadding.bottom);
            }
            image.setAlpha(1.0f);
            image.mAllowRequestLayout = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void setShortPressListener(ShortPressListener listener) {
        this.mShortPressListener = listener;
    }

    class CheckForShortPress implements Runnable {
        CheckForShortPress() {
        }

        public void run() {
            if (PagedViewWidget.sShortpressTarget == null) {
                if (PagedViewWidget.this.mShortPressListener != null) {
                    PagedViewWidget.this.mShortPressListener.onShortPress(PagedViewWidget.this);
                    PagedViewWidget.sShortpressTarget = PagedViewWidget.this;
                }
                PagedViewWidget.this.mShortPressTriggered = true;
            }
        }
    }

    private void checkForShortPress() {
        if (sShortpressTarget == null) {
            if (this.mPendingCheckForShortPress == null) {
                this.mPendingCheckForShortPress = new CheckForShortPress();
            }
            postDelayed(this.mPendingCheckForShortPress, 120);
        }
    }

    private void removeShortPressCallback() {
        if (this.mPendingCheckForShortPress != null) {
            removeCallbacks(this.mPendingCheckForShortPress);
        }
    }

    private void cleanUpShortPress() {
        removeShortPressCallback();
        if (this.mShortPressTriggered) {
            if (this.mShortPressListener != null) {
                this.mShortPressListener.cleanUpShortPress(this);
            }
            this.mShortPressTriggered = false;
        }
    }

    static void resetShortPressTarget() {
        sShortpressTarget = null;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case 0:
                checkForShortPress();
                return true;
            case 1:
                cleanUpShortPress();
                return true;
            case 3:
                cleanUpShortPress();
                return true;
            default:
                return true;
        }
    }
}
