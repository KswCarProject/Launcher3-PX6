package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.widget.TextView;
import com.android.launcher3.IconCache;
import com.android.launcher3.Launcher;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.badge.BadgeRenderer;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.model.PackageItemInfo;
import java.text.NumberFormat;

public class BubbleTextView extends TextView implements IconCache.ItemInfoUpdateReceiver, Launcher.OnResumeCallback {
    private static final Property<BubbleTextView, Float> BADGE_SCALE_PROPERTY = new Property<BubbleTextView, Float>(Float.TYPE, "badgeScale") {
        public Float get(BubbleTextView bubbleTextView) {
            return Float.valueOf(bubbleTextView.mBadgeScale);
        }

        public void set(BubbleTextView bubbleTextView, Float value) {
            float unused = bubbleTextView.mBadgeScale = value.floatValue();
            bubbleTextView.invalidate();
        }
    };
    private static final int DISPLAY_ALL_APPS = 1;
    private static final int DISPLAY_FOLDER = 2;
    private static final int DISPLAY_WORKSPACE = 0;
    private static final int[] STATE_PRESSED = {16842919};
    public static final Property<BubbleTextView, Float> TEXT_ALPHA_PROPERTY = new Property<BubbleTextView, Float>(Float.class, "textAlpha") {
        public Float get(BubbleTextView bubbleTextView) {
            return Float.valueOf(bubbleTextView.mTextAlpha);
        }

        public void set(BubbleTextView bubbleTextView, Float alpha) {
            bubbleTextView.setTextAlpha(alpha.floatValue());
        }
    };
    private final BaseDraggingActivity mActivity;
    private int mBadgeColor;
    private BadgeInfo mBadgeInfo;
    private BadgeRenderer mBadgeRenderer;
    /* access modifiers changed from: private */
    public float mBadgeScale;
    private final boolean mCenterVertically;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mDisableRelayout;
    private boolean mForceHideBadge;
    private Drawable mIcon;
    private IconCache.IconLoadRequest mIconLoadRequest;
    private final int mIconSize;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mIgnorePressedStateChange;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mIsIconVisible;
    private final boolean mLayoutHorizontal;
    private final CheckLongPressHelper mLongPressHelper;
    private final float mSlop;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mStayPressed;
    private final StylusEventHelper mStylusEventHelper;
    private Rect mTempIconBounds;
    private Point mTempSpaceForBadgeOffset;
    /* access modifiers changed from: private */
    @ViewDebug.ExportedProperty(category = "launcher")
    public float mTextAlpha;
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mTextColor;

    public BubbleTextView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsIconVisible = true;
        this.mTextAlpha = 1.0f;
        this.mTempSpaceForBadgeOffset = new Point();
        this.mTempIconBounds = new Rect();
        this.mDisableRelayout = false;
        this.mActivity = BaseDraggingActivity.fromContext(context);
        DeviceProfile grid = this.mActivity.getDeviceProfile();
        this.mSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleTextView, defStyle, 0);
        this.mLayoutHorizontal = a.getBoolean(3, false);
        int display = a.getInteger(1, 0);
        int defaultIconSize = grid.iconSizePx;
        if (display == 0) {
            setTextSize(0, (float) grid.iconTextSizePx);
            setCompoundDrawablePadding(grid.iconDrawablePaddingPx);
        } else if (display == 1) {
            setTextSize(0, grid.allAppsIconTextSizePx);
            setCompoundDrawablePadding(grid.allAppsIconDrawablePaddingPx);
            defaultIconSize = grid.allAppsIconSizePx;
        } else if (display == 2) {
            setTextSize(0, (float) grid.folderChildTextSizePx);
            setCompoundDrawablePadding(grid.folderChildDrawablePaddingPx);
            defaultIconSize = grid.folderChildIconSizePx;
        }
        this.mCenterVertically = a.getBoolean(0, false);
        this.mIconSize = a.getDimensionPixelSize(2, defaultIconSize);
        a.recycle();
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        setEllipsize(TextUtils.TruncateAt.END);
        setAccessibilityDelegate(this.mActivity.getAccessibilityDelegate());
        setTextAlpha(1.0f);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        setEllipsize(focused ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    public void reset() {
        this.mBadgeInfo = null;
        this.mBadgeColor = 0;
        this.mBadgeScale = 0.0f;
        this.mForceHideBadge = false;
    }

    public void applyFromShortcutInfo(ShortcutInfo info) {
        applyFromShortcutInfo(info, false);
    }

    public void applyFromShortcutInfo(ShortcutInfo info, boolean promiseStateChanged) {
        applyIconAndLabel(info);
        setTag(info);
        if (promiseStateChanged || info.hasPromiseIconUi()) {
            applyPromiseState(promiseStateChanged);
        }
        applyBadgeState(info, false);
    }

    public void applyFromApplicationInfo(AppInfo info) {
        applyIconAndLabel(info);
        super.setTag(info);
        verifyHighRes();
        if (info instanceof PromiseAppInfo) {
            applyProgressLevel(((PromiseAppInfo) info).level);
        }
        applyBadgeState(info, false);
    }

    public void applyFromPackageItemInfo(PackageItemInfo info) {
        applyIconAndLabel(info);
        super.setTag(info);
        verifyHighRes();
    }

    private void applyIconAndLabel(ItemInfoWithIcon info) {
        CharSequence charSequence;
        FastBitmapDrawable iconDrawable = DrawableFactory.get(getContext()).newIcon(info);
        this.mBadgeColor = IconPalette.getMutedColor(info.iconColor, 0.54f);
        setIcon(iconDrawable);
        setText(info.title);
        if (info.contentDescription != null) {
            if (info.isDisabled()) {
                charSequence = getContext().getString(R.string.disabled_app_label, new Object[]{info.contentDescription});
            } else {
                charSequence = info.contentDescription;
            }
            setContentDescription(charSequence);
        }
    }

    public void setLongPressTimeout(int longPressTimeout) {
        this.mLongPressHelper.setLongPressTimeout(longPressTimeout);
    }

    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    public void refreshDrawableState() {
        if (!this.mIgnorePressedStateChange) {
            super.refreshDrawableState();
        }
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.mStayPressed) {
            mergeDrawableStates(drawableState, STATE_PRESSED);
        }
        return drawableState;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (this.mStylusEventHelper.onMotionEvent(event)) {
            this.mLongPressHelper.cancelLongPress();
            result = true;
        }
        switch (event.getAction()) {
            case 0:
                if (!this.mStylusEventHelper.inStylusButtonPressed()) {
                    this.mLongPressHelper.postCheckForLongPress();
                    break;
                }
                break;
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                break;
            case 2:
                if (!Utilities.pointInView(this, event.getX(), event.getY(), this.mSlop)) {
                    this.mLongPressHelper.cancelLongPress();
                    break;
                }
                break;
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public void setStayPressed(boolean stayPressed) {
        this.mStayPressed = stayPressed;
        refreshDrawableState();
    }

    public void onLauncherResume() {
        setStayPressed(false);
    }

    /* access modifiers changed from: package-private */
    public void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.mIgnorePressedStateChange = true;
        boolean result = super.onKeyUp(keyCode, event);
        this.mIgnorePressedStateChange = false;
        refreshDrawableState();
        return result;
    }

    /* access modifiers changed from: protected */
    public void drawWithoutBadge(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBadgeIfNecessary(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawBadgeIfNecessary(Canvas canvas) {
        if (this.mForceHideBadge) {
            return;
        }
        if (hasBadge() || this.mBadgeScale > 0.0f) {
            getIconBounds(this.mTempIconBounds);
            this.mTempSpaceForBadgeOffset.set((getWidth() - this.mIconSize) / 2, getPaddingTop());
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            canvas.translate((float) scrollX, (float) scrollY);
            this.mBadgeRenderer.draw(canvas, this.mBadgeColor, this.mTempIconBounds, this.mBadgeScale, this.mTempSpaceForBadgeOffset);
            canvas.translate((float) (-scrollX), (float) (-scrollY));
        }
    }

    public void forceHideBadge(boolean forceHideBadge) {
        if (this.mForceHideBadge != forceHideBadge) {
            this.mForceHideBadge = forceHideBadge;
            if (forceHideBadge) {
                invalidate();
            } else if (hasBadge()) {
                ObjectAnimator.ofFloat(this, BADGE_SCALE_PROPERTY, new float[]{0.0f, 1.0f}).start();
            }
        }
    }

    private boolean hasBadge() {
        return this.mBadgeInfo != null;
    }

    public void getIconBounds(Rect outBounds) {
        int top = getPaddingTop();
        int left = (getWidth() - this.mIconSize) / 2;
        outBounds.set(left, top, this.mIconSize + left, this.mIconSize + top);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mCenterVertically) {
            Paint.FontMetrics fm = getPaint().getFontMetrics();
            int cellHeightPx = this.mIconSize + getCompoundDrawablePadding() + ((int) Math.ceil((double) (fm.bottom - fm.top)));
            setPadding(getPaddingLeft(), (View.MeasureSpec.getSize(heightMeasureSpec) - cellHeightPx) / 2, getPaddingRight(), getPaddingBottom());
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        super.setTextColor(getModifiedColor());
    }

    public void setTextColor(ColorStateList colors) {
        this.mTextColor = colors.getDefaultColor();
        if (Float.compare(this.mTextAlpha, 1.0f) == 0) {
            super.setTextColor(colors);
        } else {
            super.setTextColor(getModifiedColor());
        }
    }

    public boolean shouldTextBeVisible() {
        Object tag = getParent() instanceof FolderIcon ? ((View) getParent()).getTag() : getTag();
        ItemInfo info = tag instanceof ItemInfo ? (ItemInfo) tag : null;
        return info == null || info.container != -101;
    }

    public void setTextVisibility(boolean visible) {
        setTextAlpha(visible ? 1.0f : 0.0f);
    }

    /* access modifiers changed from: private */
    public void setTextAlpha(float alpha) {
        this.mTextAlpha = alpha;
        super.setTextColor(getModifiedColor());
    }

    private int getModifiedColor() {
        if (this.mTextAlpha == 0.0f) {
            return 0;
        }
        return ColorUtils.setAlphaComponent(this.mTextColor, Math.round(((float) Color.alpha(this.mTextColor)) * this.mTextAlpha));
    }

    public ObjectAnimator createTextAlphaAnimator(boolean fadeIn) {
        return ObjectAnimator.ofFloat(this, TEXT_ALPHA_PROPERTY, new float[]{(!shouldTextBeVisible() || !fadeIn) ? 0.0f : 1.0f});
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void applyPromiseState(boolean promiseStateChanged) {
        if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo info = (ShortcutInfo) getTag();
            PreloadIconDrawable preloadDrawable = applyProgressLevel(info.hasPromiseIconUi() ? info.hasStatusFlag(4) ? info.getInstallProgress() : 0 : 100);
            if (preloadDrawable != null && promiseStateChanged) {
                preloadDrawable.maybePerformFinishedAnimation();
            }
        }
    }

    public PreloadIconDrawable applyProgressLevel(int progressLevel) {
        if (!(getTag() instanceof ItemInfoWithIcon)) {
            return null;
        }
        ItemInfoWithIcon info = (ItemInfoWithIcon) getTag();
        if (progressLevel >= 100) {
            setContentDescription(info.contentDescription != null ? info.contentDescription : "");
        } else if (progressLevel > 0) {
            setContentDescription(getContext().getString(R.string.app_downloading_title, new Object[]{info.title, NumberFormat.getPercentInstance().format(((double) progressLevel) * 0.01d)}));
        } else {
            setContentDescription(getContext().getString(R.string.app_waiting_download_title, new Object[]{info.title}));
        }
        if (this.mIcon == null) {
            return null;
        }
        if (this.mIcon instanceof PreloadIconDrawable) {
            PreloadIconDrawable preloadDrawable = (PreloadIconDrawable) this.mIcon;
            preloadDrawable.setLevel(progressLevel);
            return preloadDrawable;
        }
        PreloadIconDrawable preloadDrawable2 = DrawableFactory.get(getContext()).newPendingIcon(info, getContext());
        preloadDrawable2.setLevel(progressLevel);
        setIcon(preloadDrawable2);
        return preloadDrawable2;
    }

    public void applyBadgeState(ItemInfo itemInfo, boolean animate) {
        if (this.mIcon instanceof FastBitmapDrawable) {
            boolean wasBadged = this.mBadgeInfo != null;
            this.mBadgeInfo = this.mActivity.getBadgeInfoForItem(itemInfo);
            boolean isBadged = this.mBadgeInfo != null;
            float newBadgeScale = isBadged ? 1.0f : 0.0f;
            this.mBadgeRenderer = this.mActivity.getDeviceProfile().mBadgeRenderer;
            if (wasBadged || isBadged) {
                if (!animate || !(wasBadged ^ isBadged) || !isShown()) {
                    this.mBadgeScale = newBadgeScale;
                    invalidate();
                } else {
                    ObjectAnimator.ofFloat(this, BADGE_SCALE_PROPERTY, new float[]{newBadgeScale}).start();
                }
            }
            if (itemInfo.contentDescription == null) {
                return;
            }
            if (hasBadge()) {
                int count = this.mBadgeInfo.getNotificationCount();
                setContentDescription(getContext().getResources().getQuantityString(R.plurals.badged_app_label, count, new Object[]{itemInfo.contentDescription, Integer.valueOf(count)}));
                return;
            }
            setContentDescription(itemInfo.contentDescription);
        }
    }

    private void setIcon(Drawable icon) {
        if (this.mIsIconVisible) {
            applyCompoundDrawables(icon);
        }
        this.mIcon = icon;
    }

    public void setIconVisible(boolean visible) {
        this.mIsIconVisible = visible;
        applyCompoundDrawables(visible ? this.mIcon : new ColorDrawable(0));
    }

    /* access modifiers changed from: protected */
    public void applyCompoundDrawables(Drawable icon) {
        this.mDisableRelayout = this.mIcon != null;
        icon.setBounds(0, 0, this.mIconSize, this.mIconSize);
        if (this.mLayoutHorizontal) {
            setCompoundDrawablesRelative(icon, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            setCompoundDrawables((Drawable) null, icon, (Drawable) null, (Drawable) null);
        }
        this.mDisableRelayout = false;
    }

    public void requestLayout() {
        if (!this.mDisableRelayout) {
            super.requestLayout();
        }
    }

    public void reapplyItemInfo(ItemInfoWithIcon info) {
        if (getTag() == info) {
            this.mIconLoadRequest = null;
            this.mDisableRelayout = true;
            info.iconBitmap.prepareToDraw();
            if (info instanceof AppInfo) {
                applyFromApplicationInfo((AppInfo) info);
            } else if (info instanceof ShortcutInfo) {
                applyFromShortcutInfo((ShortcutInfo) info);
                this.mActivity.invalidateParent(info);
            } else if (info instanceof PackageItemInfo) {
                applyFromPackageItemInfo((PackageItemInfo) info);
            }
            this.mDisableRelayout = false;
        }
    }

    public void verifyHighRes() {
        if (this.mIconLoadRequest != null) {
            this.mIconLoadRequest.cancel();
            this.mIconLoadRequest = null;
        }
        if (getTag() instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon info = (ItemInfoWithIcon) getTag();
            if (info.usingLowResIcon) {
                this.mIconLoadRequest = LauncherAppState.getInstance(getContext()).getIconCache().updateIconInBackground(this, info);
            }
        }
    }

    public int getIconSize() {
        return this.mIconSize;
    }
}
