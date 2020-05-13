package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import com.szchoiceway.index.AppsCustomizePagedView;
import java.util.ArrayList;

public class AppsCustomizeTabHost extends TabHost implements LauncherTransitionable, TabHost.OnTabChangeListener {
    private static final String APPS_TAB_TAG = "APPS";
    static final String TAG = "AppsCustomizeTabHost";
    private static final String WIDGETS_TAB_TAG = "WIDGETS";
    /* access modifiers changed from: private */
    public FrameLayout mAnimationBuffer;
    /* access modifiers changed from: private */
    public AppsCustomizePagedView mAppsCustomizePane;
    public int mAppsIconIndex = 0;
    private LinearLayout mContent;
    private boolean mInTransition;
    private final LayoutInflater mLayoutInflater;
    private Runnable mRelayoutAndMakeVisible;
    private boolean mResetAfterTransition;
    private SysProviderOpt mSysProviderOpt = null;
    /* access modifiers changed from: private */
    public ViewGroup mTabs;
    /* access modifiers changed from: private */
    public ViewGroup mTabsContainer;
    private boolean mTransitioningToWorkspace;
    private int m_iModeSet = 0;
    private int m_iUITypeVer = 0;
    public String xml_client;

    public AppsCustomizeTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSysProviderOpt = ((LauncherApplication) context.getApplicationContext()).getProvider();
        this.m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, this.m_iUITypeVer);
        this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
        this.xml_client = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_FACTORY_SET_CLIENT);
        this.mAppsIconIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_APPS_ICON_SELECT_INDEX, 0);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRelayoutAndMakeVisible = new Runnable() {
            public void run() {
                AppsCustomizeTabHost.this.mTabs.requestLayout();
                AppsCustomizeTabHost.this.mTabsContainer.setAlpha(1.0f);
            }
        };
    }

    /* access modifiers changed from: package-private */
    public void setContentTypeImmediate(AppsCustomizePagedView.ContentType type) {
        setOnTabChangedListener((TabHost.OnTabChangeListener) null);
        onTabChangedStart();
        onTabChangedEnd(type);
        setCurrentTabByTag(getTabTagForContentType(type));
        setOnTabChangedListener(this);
    }

    /* access modifiers changed from: package-private */
    public void selectAppsTab() {
        setContentTypeImmediate(AppsCustomizePagedView.ContentType.Applications);
    }

    /* access modifiers changed from: package-private */
    public void selectWidgetsTab() {
        setContentTypeImmediate(AppsCustomizePagedView.ContentType.Widgets);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        TextView tabView;
        setup();
        TabWidget tabs = getTabWidget();
        final AppsCustomizePagedView appsCustomizePane = (AppsCustomizePagedView) findViewById(R.id.apps_customize_pane_content);
        this.mTabs = tabs;
        this.mTabsContainer = (ViewGroup) findViewById(R.id.tabs_container);
        this.mAppsCustomizePane = appsCustomizePane;
        this.mAnimationBuffer = (FrameLayout) findViewById(R.id.animation_buffer);
        this.mContent = (LinearLayout) findViewById(R.id.apps_customize_content);
        if (tabs == null || this.mAppsCustomizePane == null) {
            throw new Resources.NotFoundException();
        }
        TabHost.TabContentFactory contentFactory = new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return appsCustomizePane;
            }
        };
        String label = getContext().getString(R.string.all_apps_button_label);
        if (this.m_iUITypeVer == 41) {
            tabView = (TextView) this.mLayoutInflater.inflate(R.layout.tab_widget_indicator_ksw, tabs, false);
        } else {
            tabView = (TextView) this.mLayoutInflater.inflate(R.layout.tab_widget_indicator, tabs, false);
        }
        tabView.setText(label);
        if (this.m_iUITypeVer == 41) {
            if (this.m_iModeSet == 13) {
                tabView.setTextSize(26.0f);
            } else {
                tabView.setTextSize(16.0f);
            }
        } else if (this.m_iUITypeVer == 101) {
            tabView.setTextSize(26.0f);
        } else {
            tabView.setTextSize(16.0f);
        }
        tabView.setContentDescription(label);
        addTab(newTabSpec(APPS_TAB_TAG).setIndicator(tabView).setContent(contentFactory));
        String label2 = getContext().getString(R.string.widgets_tab_label);
        TextView tabView2 = (TextView) this.mLayoutInflater.inflate(R.layout.tab_widget_indicator, tabs, false);
        tabView2.setText(label2);
        tabView2.setTextSize(16.0f);
        if (!(this.m_iUITypeVer == 36 || this.m_iUITypeVer == 35 || this.m_iUITypeVer == 37 || this.m_iUITypeVer == 40 || this.m_iUITypeVer == 42 || this.m_iUITypeVer == 38 || this.m_iUITypeVer == 43 || this.m_iUITypeVer == 39 || this.m_iUITypeVer == 44 || this.m_iUITypeVer == 41 || this.m_iUITypeVer == 45 || this.m_iUITypeVer == 101)) {
            tabView2.setContentDescription(label2);
            addTab(newTabSpec(WIDGETS_TAB_TAG).setIndicator(tabView2).setContent(contentFactory));
            setOnTabChangedListener(this);
        }
        if (!(this.m_iUITypeVer == 41 || this.m_iUITypeVer == 101)) {
            AppsCustomizeTabKeyEventListener keyListener = new AppsCustomizeTabKeyEventListener();
            tabs.getChildTabViewAt(tabs.getTabCount() - 1).setOnKeyListener(keyListener);
            findViewById(R.id.market_button).setOnKeyListener(keyListener);
        }
        this.mTabsContainer.setAlpha(0.0f);
        if ("ALS".equalsIgnoreCase(this.xml_client) && this.mAppsIconIndex == 1) {
            this.mTabsContainer.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean remeasureTabWidth = this.mTabs.getLayoutParams().width <= 0;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (remeasureTabWidth) {
            int contentWidth = this.mAppsCustomizePane.getPageContentWidth();
            Log.i(TAG, "onMeasure: contentWidth = " + contentWidth);
            if (contentWidth > 0 && this.mTabs.getLayoutParams().width != contentWidth) {
                this.mTabs.getLayoutParams().width = contentWidth;
                this.mRelayoutAndMakeVisible.run();
            }
            Log.i(TAG, "onMeasure: widthMeasureSpec = " + widthMeasureSpec + ",heightMeasureSpec = " + heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.mInTransition || !this.mTransitioningToWorkspace) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mInTransition && this.mTransitioningToWorkspace) {
            return super.onTouchEvent(event);
        }
        if (event.getY() < ((float) this.mAppsCustomizePane.getBottom())) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    /* access modifiers changed from: private */
    public void onTabChangedStart() {
        this.mAppsCustomizePane.hideScrollingIndicator(false);
    }

    /* access modifiers changed from: private */
    public void reloadCurrentPage() {
        if (!LauncherApplication.isScreenLarge()) {
            this.mAppsCustomizePane.flashScrollingIndicator(true);
        }
        this.mAppsCustomizePane.loadAssociatedPages(this.mAppsCustomizePane.getCurrentPage());
        this.mAppsCustomizePane.requestFocus();
    }

    /* access modifiers changed from: private */
    public void onTabChangedEnd(AppsCustomizePagedView.ContentType type) {
        this.mAppsCustomizePane.setContentType(type);
    }

    public void onTabChanged(String tabId) {
        Log.i(TAG, "onTabChanged: " + tabId);
        final AppsCustomizePagedView.ContentType type = getContentTypeForTabTag(tabId);
        final int duration = getResources().getInteger(R.integer.config_tabTransitionDuration);
        post(new Runnable() {
            public void run() {
                if (AppsCustomizeTabHost.this.mAppsCustomizePane.getMeasuredWidth() <= 0 || AppsCustomizeTabHost.this.mAppsCustomizePane.getMeasuredHeight() <= 0) {
                    AppsCustomizeTabHost.this.reloadCurrentPage();
                    return;
                }
                int[] visiblePageRange = new int[2];
                AppsCustomizeTabHost.this.mAppsCustomizePane.getVisiblePages(visiblePageRange);
                if (visiblePageRange[0] == -1 && visiblePageRange[1] == -1) {
                    AppsCustomizeTabHost.this.reloadCurrentPage();
                    return;
                }
                ArrayList<View> visiblePages = new ArrayList<>();
                for (int i = visiblePageRange[0]; i <= visiblePageRange[1]; i++) {
                    visiblePages.add(AppsCustomizeTabHost.this.mAppsCustomizePane.getPageAt(i));
                }
                AppsCustomizeTabHost.this.mAnimationBuffer.scrollTo(AppsCustomizeTabHost.this.mAppsCustomizePane.getScrollX(), 0);
                for (int i2 = visiblePages.size() - 1; i2 >= 0; i2--) {
                    View child = visiblePages.get(i2);
                    if (child instanceof PagedViewCellLayout) {
                        ((PagedViewCellLayout) child).resetChildrenOnKeyListeners();
                    } else if (child instanceof PagedViewGridLayout) {
                        ((PagedViewGridLayout) child).resetChildrenOnKeyListeners();
                    }
                    PagedViewWidget.setDeletePreviewsWhenDetachedFromWindow(false);
                    AppsCustomizeTabHost.this.mAppsCustomizePane.removeView(child);
                    PagedViewWidget.setDeletePreviewsWhenDetachedFromWindow(true);
                    AppsCustomizeTabHost.this.mAnimationBuffer.setAlpha(1.0f);
                    AppsCustomizeTabHost.this.mAnimationBuffer.setVisibility(0);
                    FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(child.getMeasuredWidth(), child.getMeasuredHeight());
                    p.setMargins(child.getLeft(), child.getTop(), 0, 0);
                    AppsCustomizeTabHost.this.mAnimationBuffer.addView(child, p);
                }
                AppsCustomizeTabHost.this.onTabChangedStart();
                AppsCustomizeTabHost.this.onTabChangedEnd(type);
                ObjectAnimator outAnim = LauncherAnimUtils.ofFloat(AppsCustomizeTabHost.this.mAnimationBuffer, "alpha", 0.0f);
                outAnim.addListener(new AnimatorListenerAdapter() {
                    private void clearAnimationBuffer() {
                        AppsCustomizeTabHost.this.mAnimationBuffer.setVisibility(8);
                        PagedViewWidget.setRecyclePreviewsWhenDetachedFromWindow(false);
                        AppsCustomizeTabHost.this.mAnimationBuffer.removeAllViews();
                        PagedViewWidget.setRecyclePreviewsWhenDetachedFromWindow(true);
                    }

                    public void onAnimationEnd(Animator animation) {
                        clearAnimationBuffer();
                    }

                    public void onAnimationCancel(Animator animation) {
                        clearAnimationBuffer();
                    }
                });
                ObjectAnimator inAnim = LauncherAnimUtils.ofFloat(AppsCustomizeTabHost.this.mAppsCustomizePane, "alpha", 1.0f);
                inAnim.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AppsCustomizeTabHost.this.reloadCurrentPage();
                        AppsCustomizeTabHost.this.invalidate();
                    }
                });
                AnimatorSet animSet = LauncherAnimUtils.createAnimatorSet();
                animSet.playTogether(new Animator[]{outAnim, inAnim});
                animSet.setDuration((long) duration);
                animSet.start();
            }
        });
    }

    public void setCurrentTabFromContent(AppsCustomizePagedView.ContentType type) {
        setOnTabChangedListener((TabHost.OnTabChangeListener) null);
        setCurrentTabByTag(getTabTagForContentType(type));
        setOnTabChangedListener(this);
    }

    public AppsCustomizePagedView.ContentType getContentTypeForTabTag(String tag) {
        if (tag.equals(APPS_TAB_TAG)) {
            return AppsCustomizePagedView.ContentType.Applications;
        }
        if (tag.equals(WIDGETS_TAB_TAG)) {
            return AppsCustomizePagedView.ContentType.Widgets;
        }
        return AppsCustomizePagedView.ContentType.Applications;
    }

    public String getTabTagForContentType(AppsCustomizePagedView.ContentType type) {
        if (type != AppsCustomizePagedView.ContentType.Applications && type == AppsCustomizePagedView.ContentType.Widgets) {
            return WIDGETS_TAB_TAG;
        }
        return APPS_TAB_TAG;
    }

    public int getDescendantFocusability() {
        if (getVisibility() != 0) {
            return 393216;
        }
        return super.getDescendantFocusability();
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        if (this.mInTransition) {
            this.mResetAfterTransition = true;
        } else {
            this.mAppsCustomizePane.reset();
        }
    }

    private void enableAndBuildHardwareLayer() {
        if (isHardwareAccelerated()) {
            setLayerType(2, (Paint) null);
            buildLayer();
        }
    }

    public View getContent() {
        return this.mContent;
    }

    public void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace) {
        this.mAppsCustomizePane.onLauncherTransitionPrepare(l, animated, toWorkspace);
        this.mInTransition = true;
        this.mTransitioningToWorkspace = toWorkspace;
        if (toWorkspace) {
            setVisibilityOfSiblingsWithLowerZOrder(0);
            this.mAppsCustomizePane.cancelScrollingIndicatorAnimations();
        } else {
            this.mContent.setVisibility(0);
            this.mAppsCustomizePane.loadAssociatedPages(this.mAppsCustomizePane.getCurrentPage(), true);
            if (!LauncherApplication.isScreenLarge()) {
                this.mAppsCustomizePane.showScrollingIndicator(true);
            }
        }
        if (this.mResetAfterTransition) {
            this.mAppsCustomizePane.reset();
            this.mResetAfterTransition = false;
        }
    }

    public void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace) {
        if (animated) {
            enableAndBuildHardwareLayer();
        }
    }

    public void onLauncherTransitionStep(Launcher l, float t) {
    }

    public void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace) {
        this.mAppsCustomizePane.onLauncherTransitionEnd(l, animated, toWorkspace);
        this.mInTransition = false;
        if (animated) {
            setLayerType(0, (Paint) null);
        }
        if (!toWorkspace) {
            l.dismissWorkspaceCling((View) null);
            this.mAppsCustomizePane.showAllAppsCling();
            this.mAppsCustomizePane.loadAssociatedPages(this.mAppsCustomizePane.getCurrentPage());
            if (!LauncherApplication.isScreenLarge()) {
                this.mAppsCustomizePane.hideScrollingIndicator(false);
            }
            setVisibilityOfSiblingsWithLowerZOrder(4);
        }
    }

    private void setVisibilityOfSiblingsWithLowerZOrder(int visibility) {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            int count = parent.getChildCount();
            if (!isChildrenDrawingOrderEnabled()) {
                int i = 0;
                while (i < count) {
                    View child = parent.getChildAt(i);
                    if (child != this) {
                        if (child.getVisibility() != 8) {
                            child.setVisibility(visibility);
                        }
                        i++;
                    } else {
                        return;
                    }
                }
                return;
            }
            throw new RuntimeException("Failed; can't get z-order of views");
        }
    }

    public void onWindowVisible() {
        if (getVisibility() == 0) {
            this.mContent.setVisibility(0);
            this.mAppsCustomizePane.loadAssociatedPages(this.mAppsCustomizePane.getCurrentPage(), true);
            this.mAppsCustomizePane.loadAssociatedPages(this.mAppsCustomizePane.getCurrentPage());
        }
    }

    public void onTrimMemory() {
        this.mContent.setVisibility(8);
        this.mAppsCustomizePane.clearAllWidgetPages();
    }

    /* access modifiers changed from: package-private */
    public boolean isTransitioning() {
        return this.mInTransition;
    }
}
