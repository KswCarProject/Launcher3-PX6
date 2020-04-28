package com.android.launcher3.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Insettable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetHost;
import com.android.launcher3.R;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.RecyclerViewFastScroller;
import com.android.launcher3.views.TopRoundedCornerView;

public class WidgetsFullSheet extends BaseWidgetSheet implements Insettable, LauncherAppWidgetHost.ProviderChangedListener {
    private static final long DEFAULT_OPEN_DURATION = 267;
    private static final long FADE_IN_DURATION = 150;
    private static final float VERTICAL_START_POSITION = 0.3f;
    /* access modifiers changed from: private */
    public final WidgetsListAdapter mAdapter;
    private final Rect mInsets;
    /* access modifiers changed from: private */
    public WidgetsRecyclerView mRecyclerView;

    public /* bridge */ /* synthetic */ void fillInLogContainerData(View view, ItemInfo itemInfo, LauncherLogProto.Target target, LauncherLogProto.Target target2) {
        super.fillInLogContainerData(view, itemInfo, target, target2);
    }

    public /* bridge */ /* synthetic */ void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
        super.onDropCompleted(view, dragObject, z);
    }

    public WidgetsFullSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInsets = new Rect();
        LauncherAppState apps = LauncherAppState.getInstance(context);
        this.mAdapter = new WidgetsListAdapter(context, LayoutInflater.from(context), apps.getWidgetCache(), apps.getIconCache(), this, this);
    }

    public WidgetsFullSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = findViewById(R.id.container);
        this.mRecyclerView = (WidgetsRecyclerView) findViewById(R.id.widgets_list_view);
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mAdapter.setApplyBitmapDeferred(true, this.mRecyclerView);
        TopRoundedCornerView springLayout = (TopRoundedCornerView) this.mContent;
        springLayout.addSpringView(R.id.widgets_list_view);
        this.mRecyclerView.setEdgeEffectFactory(springLayout.createEdgeEffectFactory());
        onWidgetsBound();
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return Pair.create(this.mRecyclerView, getContext().getString(this.mIsOpen ? R.string.widgets_list : R.string.widgets_list_closed));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLauncher.getAppWidgetHost().addProviderChangeListener(this);
        notifyWidgetProvidersChanged();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mLauncher.getAppWidgetHost().removeProviderChangeListener(this);
    }

    public void setInsets(Rect insets) {
        this.mInsets.set(insets);
        this.mRecyclerView.setPadding(this.mRecyclerView.getPaddingLeft(), this.mRecyclerView.getPaddingTop(), this.mRecyclerView.getPaddingRight(), insets.bottom);
        if (insets.bottom > 0) {
            setupNavBarColor();
        } else {
            clearNavBarColor();
        }
        ((TopRoundedCornerView) this.mContent).setNavBarScrimHeight(this.mInsets.bottom);
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int max;
        if (this.mInsets.bottom > 0) {
            max = 0;
        } else {
            Rect padding = this.mLauncher.getDeviceProfile().workspacePadding;
            max = Math.max(padding.left + padding.right, (this.mInsets.left + this.mInsets.right) * 2);
        }
        int widthUsed = max;
        measureChildWithMargins(this.mContent, widthMeasureSpec, widthUsed, heightMeasureSpec, this.mInsets.top + this.mLauncher.getDeviceProfile().edgeMarginPx);
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = b - t;
        int contentWidth = this.mContent.getMeasuredWidth();
        int contentLeft = ((r - l) - contentWidth) / 2;
        this.mContent.layout(contentLeft, height - this.mContent.getMeasuredHeight(), contentLeft + contentWidth, height);
        setTranslationShift(this.mTranslationShift);
    }

    public void notifyWidgetProvidersChanged() {
        this.mLauncher.refreshAndBindWidgetsForPackageUser((PackageUserKey) null);
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
        this.mAdapter.setWidgets(this.mLauncher.getPopupDataProvider().getAllWidgets());
    }

    private void open(boolean animate) {
        if (animate) {
            if (this.mLauncher.getDragLayer().getInsets().bottom > 0) {
                this.mContent.setAlpha(0.0f);
                setTranslationShift(VERTICAL_START_POSITION);
            }
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setDuration(DEFAULT_OPEN_DURATION).setInterpolator(AnimationUtils.loadInterpolator(getContext(), AndroidResources.LINEAR_OUT_SLOW_IN));
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    WidgetsFullSheet.this.mRecyclerView.setLayoutFrozen(false);
                    WidgetsFullSheet.this.mAdapter.setApplyBitmapDeferred(false, WidgetsFullSheet.this.mRecyclerView);
                    WidgetsFullSheet.this.mOpenCloseAnimator.removeListener(this);
                }
            });
            post(new Runnable() {
                public final void run() {
                    WidgetsFullSheet.lambda$open$0(WidgetsFullSheet.this);
                }
            });
            return;
        }
        setTranslationShift(0.0f);
        this.mAdapter.setApplyBitmapDeferred(false, this.mRecyclerView);
        post(new Runnable() {
            public final void run() {
                WidgetsFullSheet.this.announceAccessibilityChanges();
            }
        });
    }

    public static /* synthetic */ void lambda$open$0(WidgetsFullSheet widgetsFullSheet) {
        widgetsFullSheet.mRecyclerView.setLayoutFrozen(true);
        widgetsFullSheet.mOpenCloseAnimator.start();
        widgetsFullSheet.mContent.animate().alpha(1.0f).setDuration(FADE_IN_DURATION);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        handleClose(animate, DEFAULT_OPEN_DURATION);
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 16) != 0;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mNoIntercept = false;
            RecyclerViewFastScroller scroller = this.mRecyclerView.getScrollbar();
            if (scroller.getThumbOffsetY() >= 0 && this.mLauncher.getDragLayer().isEventOverView(scroller, ev)) {
                this.mNoIntercept = true;
            } else if (this.mLauncher.getDragLayer().isEventOverView(this.mContent, ev)) {
                this.mNoIntercept = !this.mRecyclerView.shouldContainerScroll(ev, this.mLauncher.getDragLayer());
            }
        }
        return super.onControllerInterceptTouchEvent(ev);
    }

    public static WidgetsFullSheet show(Launcher launcher, boolean animate) {
        WidgetsFullSheet sheet = (WidgetsFullSheet) launcher.getLayoutInflater().inflate(R.layout.widgets_full_sheet, launcher.getDragLayer(), false);
        sheet.mIsOpen = true;
        launcher.getDragLayer().addView(sheet);
        sheet.open(animate);
        return sheet;
    }

    /* access modifiers changed from: protected */
    public int getElementsRowCount() {
        return this.mAdapter.getItemCount();
    }
}
