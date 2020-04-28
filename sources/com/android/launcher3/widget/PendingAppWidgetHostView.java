package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.R;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.util.Themes;

public class PendingAppWidgetHostView extends LauncherAppWidgetHostView implements View.OnClickListener, IconCache.ItemInfoUpdateReceiver {
    private static final float MIN_SATUNATION = 0.7f;
    private static final float SETUP_ICON_SIZE_FACTOR = 0.4f;
    private Drawable mCenterDrawable;
    private View.OnClickListener mClickListener;
    private View mDefaultView;
    private final boolean mDisabledForSafeMode;
    private boolean mDrawableSizeChanged;
    private final LauncherAppWidgetInfo mInfo;
    private final TextPaint mPaint;
    private final Rect mRect = new Rect();
    private Drawable mSettingIconDrawable;
    private Layout mSetupTextLayout;
    private final int mStartState;

    public PendingAppWidgetHostView(Context context, LauncherAppWidgetInfo info, IconCache cache, boolean disabledForSafeMode) {
        super(new ContextThemeWrapper(context, R.style.WidgetContainerTheme));
        this.mInfo = info;
        this.mStartState = info.restoreStatus;
        this.mDisabledForSafeMode = disabledForSafeMode;
        this.mPaint = new TextPaint();
        this.mPaint.setColor(Themes.getAttrColor(getContext(), 16842806));
        this.mPaint.setTextSize(TypedValue.applyDimension(0, (float) this.mLauncher.getDeviceProfile().iconTextSizePx, getResources().getDisplayMetrics()));
        setBackgroundResource(R.drawable.pending_widget_bg);
        setWillNotDraw(false);
        setElevation(getResources().getDimension(R.dimen.pending_widget_elevation));
        updateAppWidget((RemoteViews) null);
        setOnClickListener(ItemClickHandler.INSTANCE);
        if (info.pendingItemInfo == null) {
            info.pendingItemInfo = new PackageItemInfo(info.providerName.getPackageName());
            info.pendingItemInfo.user = info.user;
            cache.updateIconInBackground(this, info.pendingItemInfo);
            return;
        }
        reapplyItemInfo(info.pendingItemInfo);
    }

    public void updateAppWidgetSize(Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight) {
    }

    /* access modifiers changed from: protected */
    public View getDefaultView() {
        if (this.mDefaultView == null) {
            this.mDefaultView = this.mInflater.inflate(R.layout.appwidget_not_ready, this, false);
            this.mDefaultView.setOnClickListener(this);
            applyState();
        }
        return this.mDefaultView;
    }

    public void setOnClickListener(View.OnClickListener l) {
        this.mClickListener = l;
    }

    public boolean isReinflateIfNeeded() {
        return this.mStartState != this.mInfo.restoreStatus;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mDrawableSizeChanged = true;
    }

    public void reapplyItemInfo(ItemInfoWithIcon info) {
        if (this.mCenterDrawable != null) {
            this.mCenterDrawable.setCallback((Drawable.Callback) null);
            this.mCenterDrawable = null;
        }
        if (info.iconBitmap != null) {
            DrawableFactory drawableFactory = DrawableFactory.get(getContext());
            if (this.mDisabledForSafeMode) {
                FastBitmapDrawable disabledIcon = drawableFactory.newIcon(info);
                disabledIcon.setIsDisabled(true);
                this.mCenterDrawable = disabledIcon;
                this.mSettingIconDrawable = null;
            } else if (isReadyForClickSetup()) {
                this.mCenterDrawable = drawableFactory.newIcon(info);
                this.mSettingIconDrawable = getResources().getDrawable(R.drawable.ic_setting).mutate();
                updateSettingColor(info.iconColor);
            } else {
                this.mCenterDrawable = DrawableFactory.get(getContext()).newPendingIcon(info, getContext());
                this.mSettingIconDrawable = null;
                applyState();
            }
            this.mCenterDrawable.setCallback(this);
            this.mDrawableSizeChanged = true;
        }
        invalidate();
    }

    private void updateSettingColor(int dominantColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(dominantColor, hsv);
        hsv[1] = Math.min(hsv[1], MIN_SATUNATION);
        hsv[2] = 1.0f;
        this.mSettingIconDrawable.setColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return who == this.mCenterDrawable || super.verifyDrawable(who);
    }

    public void applyState() {
        if (this.mCenterDrawable != null) {
            this.mCenterDrawable.setLevel(Math.max(this.mInfo.installProgress, 0));
        }
    }

    public void onClick(View v) {
        if (this.mClickListener != null) {
            this.mClickListener.onClick(this);
        }
    }

    public boolean isReadyForClickSetup() {
        if (this.mInfo.hasRestoreFlag(2) || (!this.mInfo.hasRestoreFlag(4) && !this.mInfo.hasRestoreFlag(1))) {
            return false;
        }
        return true;
    }

    private void updateDrawableBounds() {
        int actualIconSize;
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int minPadding = getResources().getDimensionPixelSize(R.dimen.pending_widget_min_padding);
        int availableWidth = ((getWidth() - paddingLeft) - paddingRight) - (minPadding * 2);
        int availableHeight = ((getHeight() - paddingTop) - paddingBottom) - (minPadding * 2);
        if (this.mSettingIconDrawable == null) {
            int size = Math.min(grid.iconSizePx, Math.min(availableWidth, availableHeight));
            this.mRect.set(0, 0, size, size);
            this.mRect.offsetTo((getWidth() - this.mRect.width()) / 2, (getHeight() - this.mRect.height()) / 2);
            this.mCenterDrawable.setBounds(this.mRect);
            return;
        }
        float iconSize = (float) Math.max(0, Math.min(availableWidth, availableHeight));
        int maxSize = Math.max(availableWidth, availableHeight);
        if (iconSize * 1.8f > ((float) maxSize)) {
            iconSize = ((float) maxSize) / 1.8f;
        }
        float iconSize2 = iconSize;
        int actualIconSize2 = (int) Math.min(iconSize2, (float) grid.iconSizePx);
        int iconTop = (getHeight() - actualIconSize2) / 2;
        this.mSetupTextLayout = null;
        if (availableWidth > 0) {
            StaticLayout staticLayout = r9;
            float f = iconSize2;
            int i = maxSize;
            StaticLayout staticLayout2 = new StaticLayout(getResources().getText(R.string.gadget_setup_text), this.mPaint, availableWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            this.mSetupTextLayout = staticLayout;
            int textHeight = this.mSetupTextLayout.getHeight();
            actualIconSize = actualIconSize2;
            if (((float) textHeight) + (((float) actualIconSize) * 1.8f) + ((float) grid.iconDrawablePaddingPx) < ((float) availableHeight)) {
                iconTop = (((getHeight() - textHeight) - grid.iconDrawablePaddingPx) - actualIconSize) / 2;
            } else {
                this.mSetupTextLayout = null;
            }
        } else {
            actualIconSize = actualIconSize2;
            float f2 = iconSize2;
            int i2 = maxSize;
        }
        this.mRect.set(0, 0, actualIconSize, actualIconSize);
        this.mRect.offset((getWidth() - actualIconSize) / 2, iconTop);
        this.mCenterDrawable.setBounds(this.mRect);
        this.mRect.left = paddingLeft + minPadding;
        this.mRect.right = this.mRect.left + ((int) (((float) actualIconSize) * SETUP_ICON_SIZE_FACTOR));
        this.mRect.top = paddingTop + minPadding;
        this.mRect.bottom = this.mRect.top + ((int) (((float) actualIconSize) * SETUP_ICON_SIZE_FACTOR));
        this.mSettingIconDrawable.setBounds(this.mRect);
        if (this.mSetupTextLayout != null) {
            this.mRect.left = paddingLeft + minPadding;
            this.mRect.top = this.mCenterDrawable.getBounds().bottom + grid.iconDrawablePaddingPx;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mCenterDrawable != null) {
            if (this.mDrawableSizeChanged) {
                updateDrawableBounds();
                this.mDrawableSizeChanged = false;
            }
            this.mCenterDrawable.draw(canvas);
            if (this.mSettingIconDrawable != null) {
                this.mSettingIconDrawable.draw(canvas);
            }
            if (this.mSetupTextLayout != null) {
                canvas.save();
                canvas.translate((float) this.mRect.left, (float) this.mRect.top);
                this.mSetupTextLayout.draw(canvas);
                canvas.restore();
            }
        }
    }
}
