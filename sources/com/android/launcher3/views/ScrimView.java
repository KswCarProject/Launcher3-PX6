package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.Themes;
import java.util.List;

public class ScrimView extends View implements Insettable, WallpaperColorInfo.OnChangeListener, AccessibilityManager.AccessibilityStateChangeListener, LauncherStateManager.StateListener {
    public static final Property<ScrimView, Integer> DRAG_HANDLE_ALPHA = new Property<ScrimView, Integer>(Integer.TYPE, "dragHandleAlpha") {
        public Integer get(ScrimView scrimView) {
            return Integer.valueOf(scrimView.mDragHandleAlpha);
        }

        public void set(ScrimView scrimView, Integer value) {
            scrimView.setDragHandleAlpha(value.intValue());
        }
    };
    private static final int SETTINGS = 2131886284;
    private static final int WALLPAPERS = 2131886295;
    private static final int WIDGETS = 2131886298;
    private final AccessibilityManager mAM;
    private final AccessibilityHelper mAccessibilityHelper;
    protected int mCurrentFlatColor;
    @Nullable
    protected Drawable mDragHandle;
    /* access modifiers changed from: private */
    public int mDragHandleAlpha = 255;
    /* access modifiers changed from: private */
    public final Rect mDragHandleBounds;
    protected float mDragHandleOffset;
    protected final int mDragHandleSize;
    protected int mEndFlatColor;
    protected int mEndFlatColorAlpha;
    protected final int mEndScrim;
    private final RectF mHitRect = new RectF();
    protected final Launcher mLauncher;
    protected float mMaxScrimAlpha;
    protected float mProgress = 1.0f;
    protected int mScrimColor;
    /* access modifiers changed from: private */
    public final int[] mTempPos = new int[2];
    /* access modifiers changed from: private */
    public final Rect mTempRect = new Rect();
    private final WallpaperColorInfo mWallpaperColorInfo;

    public ScrimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLauncher = Launcher.getLauncher(context);
        this.mWallpaperColorInfo = WallpaperColorInfo.getInstance(context);
        this.mEndScrim = Themes.getAttrColor(context, R.attr.allAppsScrimColor);
        this.mMaxScrimAlpha = 0.7f;
        this.mDragHandleSize = context.getResources().getDimensionPixelSize(R.dimen.vertical_drag_handle_size);
        this.mDragHandleBounds = new Rect(0, 0, this.mDragHandleSize, this.mDragHandleSize);
        this.mAccessibilityHelper = createAccessibilityHelper();
        ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityHelper);
        this.mAM = (AccessibilityManager) context.getSystemService("accessibility");
        setFocusable(false);
    }

    /* access modifiers changed from: protected */
    @NonNull
    public AccessibilityHelper createAccessibilityHelper() {
        return new AccessibilityHelper();
    }

    public void setInsets(Rect insets) {
        updateDragHandleBounds();
        updateDragHandleVisibility((Drawable) null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateDragHandleBounds();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWallpaperColorInfo.addOnChangeListener(this);
        onExtractedColorsChanged(this.mWallpaperColorInfo);
        this.mAM.addAccessibilityStateChangeListener(this);
        onAccessibilityStateChanged(this.mAM.isEnabled());
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWallpaperColorInfo.removeOnChangeListener(this);
        this.mAM.removeAccessibilityStateChangeListener(this);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        this.mScrimColor = wallpaperColorInfo.getMainColor();
        this.mEndFlatColor = ColorUtils.compositeColors(this.mEndScrim, ColorUtils.setAlphaComponent(this.mScrimColor, Math.round(this.mMaxScrimAlpha * 255.0f)));
        this.mEndFlatColorAlpha = Color.alpha(this.mEndFlatColor);
        updateColors();
        invalidate();
    }

    public void setProgress(float progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            updateColors();
            updateDragHandleAlpha();
            invalidate();
        }
    }

    public void reInitUi() {
    }

    /* access modifiers changed from: protected */
    public void updateColors() {
        this.mCurrentFlatColor = this.mProgress >= 1.0f ? 0 : ColorUtils.setAlphaComponent(this.mEndFlatColor, Math.round((1.0f - this.mProgress) * ((float) this.mEndFlatColorAlpha)));
    }

    /* access modifiers changed from: protected */
    public void updateDragHandleAlpha() {
        if (this.mDragHandle != null) {
            this.mDragHandle.setAlpha(this.mDragHandleAlpha);
        }
    }

    /* access modifiers changed from: private */
    public void setDragHandleAlpha(int alpha) {
        if (alpha != this.mDragHandleAlpha) {
            this.mDragHandleAlpha = alpha;
            if (this.mDragHandle != null) {
                this.mDragHandle.setAlpha(this.mDragHandleAlpha);
                invalidate();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mCurrentFlatColor != 0) {
            canvas.drawColor(this.mCurrentFlatColor);
        }
        drawDragHandle(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawDragHandle(Canvas canvas) {
        if (this.mDragHandle != null) {
            canvas.translate(0.0f, -this.mDragHandleOffset);
            this.mDragHandle.draw(canvas);
            canvas.translate(0.0f, this.mDragHandleOffset);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        if (!value && this.mDragHandle != null && event.getAction() == 0 && this.mDragHandle.getAlpha() == 255 && this.mHitRect.contains(event.getX(), event.getY())) {
            final Drawable drawable = this.mDragHandle;
            this.mDragHandle = null;
            Rect bounds = new Rect(this.mDragHandleBounds);
            bounds.offset(0, -((int) this.mDragHandleOffset));
            drawable.setBounds(bounds);
            Rect topBounds = new Rect(bounds);
            topBounds.offset(0, (-bounds.height()) / 2);
            Rect invalidateRegion = new Rect(bounds);
            invalidateRegion.top = topBounds.top;
            Keyframe frameTop = Keyframe.ofObject(0.6f, topBounds);
            frameTop.setInterpolator(Interpolators.DEACCEL);
            Keyframe frameBot = Keyframe.ofObject(1.0f, bounds);
            frameBot.setInterpolator(Interpolators.ACCEL);
            PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("bounds", new Keyframe[]{Keyframe.ofObject(0.0f, bounds), frameTop, frameBot});
            holder.setEvaluator(new RectEvaluator());
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(drawable, new PropertyValuesHolder[]{holder});
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ScrimView.this.getOverlay().remove(drawable);
                    ScrimView.this.updateDragHandleVisibility(drawable);
                }
            });
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(invalidateRegion) {
                private final /* synthetic */ Rect f$1;

                {
                    this.f$1 = r2;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ScrimView.this.invalidate(this.f$1);
                }
            });
            getOverlay().add(drawable);
            anim.start();
        }
        return value;
    }

    /* access modifiers changed from: protected */
    public void updateDragHandleBounds() {
        int left;
        int topMargin;
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        int width = getMeasuredWidth();
        int top = (getMeasuredHeight() - this.mDragHandleSize) - grid.getInsets().bottom;
        if (grid.isVerticalBarLayout()) {
            topMargin = grid.workspacePadding.bottom;
            if (grid.isSeascape()) {
                left = (width - grid.getInsets().right) - this.mDragHandleSize;
            } else {
                left = this.mDragHandleSize + grid.getInsets().left;
            }
        } else {
            left = (width - this.mDragHandleSize) / 2;
            topMargin = grid.hotseatBarSizePx;
        }
        this.mDragHandleBounds.offsetTo(left, top - topMargin);
        this.mHitRect.set(this.mDragHandleBounds);
        float inset = (float) ((-this.mDragHandleSize) / 2);
        this.mHitRect.inset(inset, inset);
        if (this.mDragHandle != null) {
            this.mDragHandle.setBounds(this.mDragHandleBounds);
        }
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        LauncherStateManager stateManager = this.mLauncher.getStateManager();
        stateManager.removeStateListener(this);
        if (enabled) {
            stateManager.addStateListener(this);
            onStateSetImmediately(this.mLauncher.getStateManager().getState());
        } else {
            setImportantForAccessibility(4);
        }
        updateDragHandleVisibility((Drawable) null);
    }

    /* access modifiers changed from: private */
    public void updateDragHandleVisibility(Drawable recycle) {
        boolean wasVisible = false;
        boolean visible = this.mLauncher.getDeviceProfile().isVerticalBarLayout() || this.mAM.isEnabled();
        if (this.mDragHandle != null) {
            wasVisible = true;
        }
        if (visible != wasVisible) {
            if (visible) {
                this.mDragHandle = recycle != null ? recycle : this.mLauncher.getDrawable(R.drawable.drag_handle_indicator);
                this.mDragHandle.setBounds(this.mDragHandleBounds);
                updateDragHandleAlpha();
            } else {
                this.mDragHandle = null;
            }
            invalidate();
        }
    }

    public boolean dispatchHoverEvent(MotionEvent event) {
        return this.mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void onStateTransitionStart(LauncherState toState) {
    }

    public void onStateTransitionComplete(LauncherState finalState) {
        onStateSetImmediately(finalState);
    }

    public void onStateSetImmediately(LauncherState state) {
        setImportantForAccessibility(state == LauncherState.ALL_APPS ? 4 : 0);
    }

    protected class AccessibilityHelper extends ExploreByTouchHelper {
        private static final int DRAG_HANDLE_ID = 1;

        public AccessibilityHelper() {
            super(ScrimView.this);
        }

        /* access modifiers changed from: protected */
        public int getVirtualViewAt(float x, float y) {
            return ScrimView.this.mDragHandleBounds.contains((int) x, (int) y) ? 1 : Integer.MIN_VALUE;
        }

        /* access modifiers changed from: protected */
        public void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            virtualViewIds.add(1);
        }

        /* access modifiers changed from: protected */
        public void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            node.setContentDescription(ScrimView.this.getContext().getString(R.string.all_apps_button_label));
            node.setBoundsInParent(ScrimView.this.mDragHandleBounds);
            ScrimView.this.getLocationOnScreen(ScrimView.this.mTempPos);
            ScrimView.this.mTempRect.set(ScrimView.this.mDragHandleBounds);
            ScrimView.this.mTempRect.offset(ScrimView.this.mTempPos[0], ScrimView.this.mTempPos[1]);
            node.setBoundsInScreen(ScrimView.this.mTempRect);
            node.addAction(16);
            node.setClickable(true);
            node.setFocusable(true);
            if (ScrimView.this.mLauncher.isInState(LauncherState.NORMAL)) {
                Context context = ScrimView.this.getContext();
                if (Utilities.isWallpaperAllowed(context)) {
                    node.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.string.wallpaper_button_text, context.getText(R.string.wallpaper_button_text)));
                }
                node.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.string.widget_button_text, context.getText(R.string.widget_button_text)));
                node.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.string.settings_button_text, context.getText(R.string.settings_button_text)));
            }
        }

        /* access modifiers changed from: protected */
        public boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            if (action == 16) {
                ScrimView.this.mLauncher.getUserEventDispatcher().logActionOnControl(0, 1, ScrimView.this.mLauncher.getStateManager().getState().containerType);
                ScrimView.this.mLauncher.getStateManager().goToState(LauncherState.ALL_APPS);
                return true;
            } else if (action == R.string.wallpaper_button_text) {
                return OptionsPopupView.startWallpaperPicker(ScrimView.this);
            } else {
                if (action == R.string.widget_button_text) {
                    return OptionsPopupView.onWidgetsClicked(ScrimView.this);
                }
                if (action == R.string.settings_button_text) {
                    return OptionsPopupView.startSettings(ScrimView.this);
                }
                return false;
            }
        }
    }

    public int getDragHandleSize() {
        return this.mDragHandleSize;
    }
}
