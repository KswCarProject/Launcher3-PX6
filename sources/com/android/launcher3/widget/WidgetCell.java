package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.CancellationSignal;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.R;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.model.WidgetItem;

public class WidgetCell extends LinearLayout implements View.OnLayoutChangeListener {
    private static final boolean DEBUG = false;
    private static final int FADE_IN_DURATION_MS = 90;
    private static final float PREVIEW_SCALE = 0.8f;
    private static final String TAG = "WidgetCell";
    private static final float WIDTH_SCALE = 2.6f;
    protected CancellationSignal mActiveRequest;
    protected final BaseActivity mActivity;
    private boolean mAnimatePreview;
    private boolean mApplyBitmapDeferred;
    private int mCellSize;
    private Bitmap mDeferredBitmap;
    protected WidgetItem mItem;
    protected int mPresetPreviewSize;
    private StylusEventHelper mStylusEventHelper;
    private TextView mWidgetDims;
    private WidgetImageView mWidgetImage;
    private TextView mWidgetName;
    private WidgetPreviewLoader mWidgetPreviewLoader;

    public WidgetCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimatePreview = true;
        this.mApplyBitmapDeferred = false;
        this.mActivity = BaseActivity.fromContext(context);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        setContainerWidth();
        setWillNotDraw(false);
        setClipToPadding(false);
        setAccessibilityDelegate(this.mActivity.getAccessibilityDelegate());
    }

    private void setContainerWidth() {
        this.mCellSize = (int) (((float) this.mActivity.getDeviceProfile().cellWidthPx) * WIDTH_SCALE);
        this.mPresetPreviewSize = (int) (((float) this.mCellSize) * PREVIEW_SCALE);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mWidgetImage = (WidgetImageView) findViewById(R.id.widget_preview);
        this.mWidgetName = (TextView) findViewById(R.id.widget_name);
        this.mWidgetDims = (TextView) findViewById(R.id.widget_dims);
    }

    public void clear() {
        this.mWidgetImage.animate().cancel();
        this.mWidgetImage.setBitmap((Bitmap) null, (Drawable) null);
        this.mWidgetName.setText((CharSequence) null);
        this.mWidgetDims.setText((CharSequence) null);
        if (this.mActiveRequest != null) {
            this.mActiveRequest.cancel();
            this.mActiveRequest = null;
        }
    }

    public void applyFromCellItem(WidgetItem item, WidgetPreviewLoader loader) {
        this.mItem = item;
        this.mWidgetName.setText(this.mItem.label);
        this.mWidgetDims.setText(getContext().getString(R.string.widget_dims_format, new Object[]{Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)}));
        this.mWidgetDims.setContentDescription(getContext().getString(R.string.widget_accessible_dims_format, new Object[]{Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)}));
        this.mWidgetPreviewLoader = loader;
        if (item.activityInfo != null) {
            setTag(new PendingAddShortcutInfo(item.activityInfo));
        } else {
            setTag(new PendingAddWidgetInfo(item.widgetInfo));
        }
    }

    public WidgetImageView getWidgetView() {
        return this.mWidgetImage;
    }

    public void setApplyBitmapDeferred(boolean isDeferred) {
        if (this.mApplyBitmapDeferred != isDeferred) {
            this.mApplyBitmapDeferred = isDeferred;
            if (!this.mApplyBitmapDeferred && this.mDeferredBitmap != null) {
                applyPreview(this.mDeferredBitmap);
                this.mDeferredBitmap = null;
            }
        }
    }

    public void setAnimatePreview(boolean shouldAnimate) {
        this.mAnimatePreview = shouldAnimate;
    }

    public void applyPreview(Bitmap bitmap) {
        if (this.mApplyBitmapDeferred) {
            this.mDeferredBitmap = bitmap;
        } else if (bitmap != null) {
            this.mWidgetImage.setBitmap(bitmap, DrawableFactory.get(getContext()).getBadgeForUser(this.mItem.user, getContext()));
            if (this.mAnimatePreview) {
                this.mWidgetImage.setAlpha(0.0f);
                this.mWidgetImage.animate().alpha(1.0f).setDuration(90);
                return;
            }
            this.mWidgetImage.setAlpha(1.0f);
        }
    }

    public void ensurePreview() {
        if (this.mActiveRequest == null) {
            this.mActiveRequest = this.mWidgetPreviewLoader.getPreview(this.mItem, this.mPresetPreviewSize, this.mPresetPreviewSize, this);
        }
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        removeOnLayoutChangeListener(this);
        ensurePreview();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = super.onTouchEvent(ev);
        if (this.mStylusEventHelper.onMotionEvent(ev)) {
            return true;
        }
        return handled;
    }

    private String getTagToString() {
        if ((getTag() instanceof PendingAddWidgetInfo) || (getTag() instanceof PendingAddShortcutInfo)) {
            return getTag().toString();
        }
        return "";
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        int i = this.mCellSize;
        params.height = i;
        params.width = i;
        super.setLayoutParams(params);
    }

    public CharSequence getAccessibilityClassName() {
        return WidgetCell.class.getName();
    }
}
