package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.Advanceable;
import android.widget.RemoteViews;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.R;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.views.BaseDragLayer;

public class LauncherAppWidgetHostView extends AppWidgetHostView implements BaseDragLayer.TouchCompleteListener, View.OnLongClickListener {
    private static final long ADVANCE_INTERVAL = 20000;
    private static final long ADVANCE_STAGGER = 250;
    private static final SparseBooleanArray sAutoAdvanceWidgetIds = new SparseBooleanArray();
    private Runnable mAutoAdvanceRunnable;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mChildrenFocused;
    protected final LayoutInflater mInflater;
    private boolean mIsAttachedToWindow;
    private boolean mIsAutoAdvanceRegistered;
    private boolean mIsScrollable;
    protected final Launcher mLauncher;
    private final CheckLongPressHelper mLongPressHelper;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mReinflateOnConfigChange;
    private float mScaleToFit = 1.0f;
    private float mSlop;
    private final StylusEventHelper mStylusEventHelper;
    private final PointF mTranslationForCentering = new PointF(0.0f, 0.0f);

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        this.mLauncher = Launcher.getLauncher(context);
        this.mLongPressHelper = new CheckLongPressHelper(this, this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mInflater = LayoutInflater.from(context);
        setAccessibilityDelegate(this.mLauncher.getAccessibilityDelegate());
        setBackgroundResource(R.drawable.widget_internal_focus_bg);
        if (Utilities.ATLEAST_OREO) {
            setExecutor(Utilities.THREAD_POOL_EXECUTOR);
        }
    }

    public boolean onLongClick(View view) {
        if (this.mIsScrollable) {
            Launcher.getLauncher(getContext()).getDragLayer().requestDisallowInterceptTouchEvent(false);
        }
        view.performLongClick();
        return true;
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return this.mInflater.inflate(R.layout.appwidget_error, this, false);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        super.updateAppWidget(remoteViews);
        checkIfAutoAdvance();
        this.mReinflateOnConfigChange = !isSameOrientation();
    }

    private boolean isSameOrientation() {
        return this.mLauncher.getResources().getConfiguration().orientation == this.mLauncher.getOrientation();
    }

    private boolean checkScrollableRecursively(ViewGroup viewGroup) {
        if (viewGroup instanceof AdapterView) {
            return true;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if ((child instanceof ViewGroup) && checkScrollableRecursively((ViewGroup) child)) {
                return true;
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mLongPressHelper.cancelLongPress();
        }
        if (this.mLongPressHelper.hasPerformedLongPress()) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        } else if (this.mStylusEventHelper.onMotionEvent(ev)) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        } else {
            switch (ev.getAction()) {
                case 0:
                    DragLayer dragLayer = Launcher.getLauncher(getContext()).getDragLayer();
                    if (this.mIsScrollable) {
                        dragLayer.requestDisallowInterceptTouchEvent(true);
                    }
                    if (!this.mStylusEventHelper.inStylusButtonPressed()) {
                        this.mLongPressHelper.postCheckForLongPress();
                    }
                    dragLayer.setTouchCompleteListener(this);
                    return false;
                case 1:
                case 3:
                    this.mLongPressHelper.cancelLongPress();
                    return false;
                case 2:
                    if (Utilities.pointInView(this, ev.getX(), ev.getY(), this.mSlop)) {
                        return false;
                    }
                    this.mLongPressHelper.cancelLongPress();
                    return false;
                default:
                    return false;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                return false;
            case 2:
                if (Utilities.pointInView(this, ev.getX(), ev.getY(), this.mSlop)) {
                    return false;
                }
                this.mLongPressHelper.cancelLongPress();
                return false;
            default:
                return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mIsAttachedToWindow = true;
        checkIfAutoAdvance();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsAttachedToWindow = false;
        checkIfAutoAdvance();
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public AppWidgetProviderInfo getAppWidgetInfo() {
        AppWidgetProviderInfo info = super.getAppWidgetInfo();
        if (info == null || (info instanceof LauncherAppWidgetProviderInfo)) {
            return info;
        }
        throw new IllegalStateException("Launcher widget must have LauncherAppWidgetProviderInfo");
    }

    public void onTouchComplete() {
        if (!this.mLongPressHelper.hasPerformedLongPress()) {
            this.mLongPressHelper.cancelLongPress();
        }
    }

    public int getDescendantFocusability() {
        return this.mChildrenFocused ? 131072 : 393216;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!this.mChildrenFocused || event.getKeyCode() != 111 || event.getAction() != 1) {
            return super.dispatchKeyEvent(event);
        }
        this.mChildrenFocused = false;
        requestFocus();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mChildrenFocused || keyCode != 66) {
            return super.onKeyDown(keyCode, event);
        }
        event.startTracking();
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0047, code lost:
        r1.get(0).requestFocus();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0050, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyUp(int r7, android.view.KeyEvent r8) {
        /*
            r6 = this;
            boolean r0 = r8.isTracking()
            if (r0 == 0) goto L_0x0051
            boolean r0 = r6.mChildrenFocused
            if (r0 != 0) goto L_0x0051
            r0 = 66
            if (r7 != r0) goto L_0x0051
            r0 = 1
            r6.mChildrenFocused = r0
            r1 = 2
            java.util.ArrayList r1 = r6.getFocusables(r1)
            r1.remove(r6)
            int r2 = r1.size()
            r3 = 0
            switch(r2) {
                case 0: goto L_0x0044;
                case 1: goto L_0x0022;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0047
        L_0x0022:
            java.lang.Object r4 = r6.getTag()
            boolean r4 = r4 instanceof com.android.launcher3.ItemInfo
            if (r4 == 0) goto L_0x0047
            java.lang.Object r4 = r6.getTag()
            com.android.launcher3.ItemInfo r4 = (com.android.launcher3.ItemInfo) r4
            int r5 = r4.spanX
            if (r5 != r0) goto L_0x0047
            int r5 = r4.spanY
            if (r5 != r0) goto L_0x0047
            java.lang.Object r5 = r1.get(r3)
            android.view.View r5 = (android.view.View) r5
            r5.performClick()
            r6.mChildrenFocused = r3
            return r0
        L_0x0044:
            r6.mChildrenFocused = r3
            goto L_0x0051
        L_0x0047:
            java.lang.Object r3 = r1.get(r3)
            android.view.View r3 = (android.view.View) r3
            r3.requestFocus()
            return r0
        L_0x0051:
            boolean r0 = super.onKeyUp(r7, r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.widget.LauncherAppWidgetHostView.onKeyUp(int, android.view.KeyEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            this.mChildrenFocused = false;
            dispatchChildFocus(false);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        dispatchChildFocus(this.mChildrenFocused && focused != null);
        if (focused != null) {
            focused.setFocusableInTouchMode(false);
        }
    }

    public void clearChildFocus(View child) {
        super.clearChildFocus(child);
        dispatchChildFocus(false);
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return this.mChildrenFocused;
    }

    private void dispatchChildFocus(boolean childIsFocused) {
        setSelected(childIsFocused);
    }

    public void switchToErrorView() {
        updateAppWidget(new RemoteViews(getAppWidgetInfo().provider.getPackageName(), 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            super.onLayout(changed, left, top, right, bottom);
        } catch (RuntimeException e) {
            post(new Runnable() {
                public void run() {
                    LauncherAppWidgetHostView.this.switchToErrorView();
                }
            });
        }
        this.mIsScrollable = checkScrollableRecursively(this);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(getClass().getName());
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        maybeRegisterAutoAdvance();
    }

    private void checkIfAutoAdvance() {
        boolean isAutoAdvance = false;
        Advanceable target = getAdvanceable();
        if (target != null) {
            isAutoAdvance = true;
            target.fyiWillBeAdvancedByHostKThx();
        }
        if (isAutoAdvance != (sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId()) >= 0)) {
            if (isAutoAdvance) {
                sAutoAdvanceWidgetIds.put(getAppWidgetId(), true);
            } else {
                sAutoAdvanceWidgetIds.delete(getAppWidgetId());
            }
            maybeRegisterAutoAdvance();
        }
    }

    private Advanceable getAdvanceable() {
        AppWidgetProviderInfo info = getAppWidgetInfo();
        if (info == null || info.autoAdvanceViewId == -1 || !this.mIsAttachedToWindow) {
            return null;
        }
        View v = findViewById(info.autoAdvanceViewId);
        if (v instanceof Advanceable) {
            return (Advanceable) v;
        }
        return null;
    }

    private void maybeRegisterAutoAdvance() {
        Handler handler = getHandler();
        boolean shouldRegisterAutoAdvance = getWindowVisibility() == 0 && handler != null && sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId()) >= 0;
        if (shouldRegisterAutoAdvance != this.mIsAutoAdvanceRegistered) {
            this.mIsAutoAdvanceRegistered = shouldRegisterAutoAdvance;
            if (this.mAutoAdvanceRunnable == null) {
                this.mAutoAdvanceRunnable = new Runnable() {
                    public void run() {
                        LauncherAppWidgetHostView.this.runAutoAdvance();
                    }
                };
            }
            handler.removeCallbacks(this.mAutoAdvanceRunnable);
            scheduleNextAdvance();
        }
    }

    private void scheduleNextAdvance() {
        if (this.mIsAutoAdvanceRegistered) {
            long now = SystemClock.uptimeMillis();
            long advanceTime = (ADVANCE_INTERVAL - (now % ADVANCE_INTERVAL)) + now + (((long) sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId())) * ADVANCE_STAGGER);
            Handler handler = getHandler();
            if (handler != null) {
                handler.postAtTime(this.mAutoAdvanceRunnable, advanceTime);
            }
        }
    }

    /* access modifiers changed from: private */
    public void runAutoAdvance() {
        Advanceable target = getAdvanceable();
        if (target != null) {
            target.advance();
        }
        scheduleNextAdvance();
    }

    public void setScaleToFit(float scale) {
        this.mScaleToFit = scale;
        setScaleX(scale);
        setScaleY(scale);
    }

    public float getScaleToFit() {
        return this.mScaleToFit;
    }

    public void setTranslationForCentering(float x, float y) {
        this.mTranslationForCentering.set(x, y);
        setTranslationX(x);
        setTranslationY(y);
    }

    public PointF getTranslationForCentering() {
        return this.mTranslationForCentering;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mReinflateOnConfigChange && isSameOrientation()) {
            this.mReinflateOnConfigChange = false;
            reInflate();
        }
    }

    public void reInflate() {
        if (isAttachedToWindow()) {
            LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) getTag();
            this.mLauncher.removeItem(this, info, false);
            this.mLauncher.bindAppWidget(info);
        }
    }
}
