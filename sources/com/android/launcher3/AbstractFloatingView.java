package com.android.launcher3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.BaseDragLayer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AbstractFloatingView extends LinearLayout implements TouchController {
    public static final int TYPE_ACCESSIBLE = 447;
    public static final int TYPE_ACTION_POPUP = 2;
    public static final int TYPE_ALL = 511;
    public static final int TYPE_DISCOVERY_BOUNCE = 64;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_HIDE_BACK_BUTTON = 96;
    public static final int TYPE_ON_BOARD_POPUP = 32;
    public static final int TYPE_OPTIONS_POPUP = 256;
    public static final int TYPE_QUICKSTEP_PREVIEW = 64;
    public static final int TYPE_REBIND_SAFE = 112;
    public static final int TYPE_TASK_MENU = 128;
    public static final int TYPE_WIDGETS_BOTTOM_SHEET = 4;
    public static final int TYPE_WIDGETS_FULL_SHEET = 16;
    public static final int TYPE_WIDGET_RESIZE_FRAME = 8;
    protected boolean mIsOpen;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FloatingViewType {
    }

    /* access modifiers changed from: protected */
    public abstract void handleClose(boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean isOfType(int i);

    public abstract void logActionCommand(int i);

    public AbstractFloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    public final void close(boolean animate) {
        boolean animate2 = animate & (!Utilities.isPowerSaverPreventingAnimation(getContext()));
        if (this.mIsOpen) {
            BaseActivity.fromContext(getContext()).getUserEventDispatcher().resetElapsedContainerMillis("container closed");
        }
        handleClose(animate2);
        this.mIsOpen = false;
    }

    public final boolean isOpen() {
        return this.mIsOpen;
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
    }

    public boolean onBackPressed() {
        logActionCommand(1);
        close(true);
        return true;
    }

    public boolean onControllerTouchEvent(MotionEvent ev) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void announceAccessibilityChanges() {
        Pair<View, String> targetInfo = getAccessibilityTarget();
        if (targetInfo != null && AccessibilityManagerCompat.isAccessibilityEnabled(getContext())) {
            AccessibilityManagerCompat.sendCustomAccessibilityEvent((View) targetInfo.first, 32, (String) targetInfo.second);
            if (this.mIsOpen) {
                sendAccessibilityEvent(8);
            }
            BaseDraggingActivity.fromContext(getContext()).getDragLayer().sendAccessibilityEvent(2048);
        }
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return null;
    }

    protected static <T extends AbstractFloatingView> T getOpenView(BaseDraggingActivity activity, int type) {
        BaseDragLayer dragLayer = activity.getDragLayer();
        for (int i = dragLayer.getChildCount() - 1; i >= 0; i--) {
            T childAt = dragLayer.getChildAt(i);
            if (childAt instanceof AbstractFloatingView) {
                AbstractFloatingView view = childAt;
                if (view.isOfType(type) && view.isOpen()) {
                    return view;
                }
            }
        }
        return null;
    }

    public static void closeOpenContainer(BaseDraggingActivity activity, int type) {
        AbstractFloatingView view = getOpenView(activity, type);
        if (view != null) {
            view.close(true);
        }
    }

    public static void closeOpenViews(BaseDraggingActivity activity, boolean animate, int type) {
        BaseDragLayer dragLayer = activity.getDragLayer();
        for (int i = dragLayer.getChildCount() - 1; i >= 0; i--) {
            View child = dragLayer.getChildAt(i);
            if (child instanceof AbstractFloatingView) {
                AbstractFloatingView abs = (AbstractFloatingView) child;
                if (abs.isOfType(type)) {
                    abs.close(animate);
                }
            }
        }
    }

    public static void closeAllOpenViews(BaseDraggingActivity activity, boolean animate) {
        closeOpenViews(activity, animate, 511);
        activity.finishAutoCancelActionMode();
    }

    public static void closeAllOpenViews(BaseDraggingActivity activity) {
        closeAllOpenViews(activity, true);
    }

    public static AbstractFloatingView getTopOpenView(BaseDraggingActivity activity) {
        return getTopOpenViewWithType(activity, 511);
    }

    public static AbstractFloatingView getTopOpenViewWithType(BaseDraggingActivity activity, int type) {
        return getOpenView(activity, type);
    }
}
