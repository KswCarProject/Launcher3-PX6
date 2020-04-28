package com.android.launcher3.allapps;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Process;
import android.support.animation.DynamicAnimation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.AppInfo;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.keyboard.FocusedItemDecorator;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BottomUserEducationView;
import com.android.launcher3.views.RecyclerViewFastScroller;
import com.android.launcher3.views.SpringRelativeLayout;
import java.util.Iterator;

public class AllAppsContainerView extends SpringRelativeLayout implements DragSource, Insettable, DeviceProfile.OnDeviceProfileChangeListener {
    private static final float FLING_ANIMATION_THRESHOLD = 0.55f;
    private static final float FLING_VELOCITY_MULTIPLIER = 135.0f;
    /* access modifiers changed from: private */
    public final AdapterHolder[] mAH;
    /* access modifiers changed from: private */
    public final AllAppsStore mAllAppsStore;
    private final Point mFastScrollerOffset;
    private FloatingHeaderView mHeader;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    private int mNavBarScrimHeight;
    private final Paint mNavBarScrimPaint;
    private final ItemInfoMatcher mPersonalMatcher;
    private View mSearchContainer;
    private boolean mSearchModeWhileUsingTabs;
    private SpannableStringBuilder mSearchQueryBuilder;
    private SearchUiManager mSearchUiManager;
    private RecyclerViewFastScroller mTouchHandler;
    /* access modifiers changed from: private */
    public boolean mUsingTabs;
    private AllAppsPagedView mViewPager;
    private final ItemInfoMatcher mWorkMatcher;

    public AllAppsContainerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPersonalMatcher = ItemInfoMatcher.ofUser(Process.myUserHandle());
        this.mWorkMatcher = ItemInfoMatcher.not(this.mPersonalMatcher);
        this.mAllAppsStore = new AllAppsStore();
        this.mNavBarScrimHeight = 0;
        this.mSearchQueryBuilder = null;
        this.mSearchModeWhileUsingTabs = false;
        this.mFastScrollerOffset = new Point();
        this.mLauncher = Launcher.getLauncher(context);
        this.mLauncher.addOnDeviceProfileChangeListener(this);
        this.mSearchQueryBuilder = new SpannableStringBuilder();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
        this.mAH = new AdapterHolder[2];
        this.mAH[0] = new AdapterHolder(false);
        this.mAH[1] = new AdapterHolder(true);
        this.mNavBarScrimPaint = new Paint();
        this.mNavBarScrimPaint.setColor(Themes.getAttrColor(context, R.attr.allAppsNavBarScrimColor));
        this.mAllAppsStore.addUpdateListener(new AllAppsStore.OnUpdateListener() {
            public final void onAppsUpdated() {
                AllAppsContainerView.this.onAppsUpdated();
            }
        });
        addSpringView(R.id.all_apps_header);
        addSpringView(R.id.apps_list_view);
        addSpringView(R.id.all_apps_tabs_view_pager);
    }

    public AllAppsStore getAppsStore() {
        return this.mAllAppsStore;
    }

    /* access modifiers changed from: protected */
    public void setDampedScrollShift(float shift) {
        float maxShift = ((float) getSearchView().getHeight()) / 2.0f;
        super.setDampedScrollShift(Utilities.boundToRange(shift, -maxShift, maxShift));
    }

    public void onDeviceProfileChanged(DeviceProfile dp) {
        for (AdapterHolder holder : this.mAH) {
            if (holder.recyclerView != null) {
                holder.recyclerView.swapAdapter(holder.recyclerView.getAdapter(), true);
                holder.recyclerView.getRecycledViewPool().clear();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onAppsUpdated() {
        boolean hasWorkApps = false;
        Iterator<AppInfo> it = this.mAllAppsStore.getApps().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            if (this.mWorkMatcher.matches(it.next(), (ComponentName) null)) {
                hasWorkApps = true;
                break;
            }
        }
        rebindAdapters(hasWorkApps);
    }

    public boolean shouldContainerScroll(MotionEvent ev) {
        AllAppsRecyclerView rv;
        if (this.mLauncher.getDragLayer().isEventOverView(this.mSearchContainer, ev) || (rv = getActiveRecyclerView()) == null) {
            return true;
        }
        if (rv.getScrollbar().getThumbOffsetY() < 0 || !this.mLauncher.getDragLayer().isEventOverView(rv.getScrollbar(), ev)) {
            return rv.shouldContainerScroll(ev, this.mLauncher.getDragLayer());
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        AllAppsRecyclerView rv;
        if (ev.getAction() == 0 && (rv = getActiveRecyclerView()) != null && rv.getScrollbar().isHitInParent(ev.getX(), ev.getY(), this.mFastScrollerOffset)) {
            this.mTouchHandler = rv.getScrollbar();
        }
        if (this.mTouchHandler != null) {
            return this.mTouchHandler.handleTouchEvent(ev, this.mFastScrollerOffset);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mTouchHandler == null) {
            return false;
        }
        this.mTouchHandler.handleTouchEvent(ev, this.mFastScrollerOffset);
        return true;
    }

    public String getDescription() {
        int descriptionRes;
        if (this.mUsingTabs) {
            descriptionRes = this.mViewPager.getNextPage() == 0 ? R.string.all_apps_button_personal_label : R.string.all_apps_button_work_label;
        } else {
            descriptionRes = R.string.all_apps_button_label;
        }
        return getContext().getString(descriptionRes);
    }

    public AllAppsRecyclerView getActiveRecyclerView() {
        if (!this.mUsingTabs || this.mViewPager.getNextPage() == 0) {
            return this.mAH[0].recyclerView;
        }
        return this.mAH[1].recyclerView;
    }

    public void reset(boolean animate) {
        for (int i = 0; i < this.mAH.length; i++) {
            if (this.mAH[i].recyclerView != null) {
                this.mAH[i].recyclerView.scrollToTop();
            }
        }
        if (isHeaderVisible() != 0) {
            this.mHeader.reset(animate);
        }
        this.mSearchUiManager.resetSearch();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public final void onFocusChange(View view, boolean z) {
                AllAppsContainerView.lambda$onFinishInflate$0(AllAppsContainerView.this, view, z);
            }
        });
        this.mHeader = (FloatingHeaderView) findViewById(R.id.all_apps_header);
        rebindAdapters(this.mUsingTabs, true);
        this.mSearchContainer = findViewById(R.id.search_container_all_apps);
        this.mSearchUiManager = (SearchUiManager) this.mSearchContainer;
        this.mSearchUiManager.initialize(this);
    }

    public static /* synthetic */ void lambda$onFinishInflate$0(AllAppsContainerView allAppsContainerView, View v, boolean hasFocus) {
        if (hasFocus && allAppsContainerView.getActiveRecyclerView() != null) {
            allAppsContainerView.getActiveRecyclerView().requestFocus();
        }
    }

    public SearchUiManager getSearchUiManager() {
        return this.mSearchUiManager;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        this.mSearchUiManager.preDispatchKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
    }

    public void setInsets(Rect insets) {
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        int leftRightPadding = grid.desiredWorkspaceLeftRightMarginPx + grid.cellLayoutPaddingLeftRightPx;
        for (int i = 0; i < this.mAH.length; i++) {
            this.mAH[i].padding.bottom = insets.bottom;
            Rect rect = this.mAH[i].padding;
            this.mAH[i].padding.right = leftRightPadding;
            rect.left = leftRightPadding;
            this.mAH[i].applyPadding();
        }
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) getLayoutParams();
        if (grid.isVerticalBarLayout()) {
            mlp.leftMargin = insets.left;
            mlp.rightMargin = insets.right;
            setPadding(grid.workspacePadding.left, 0, grid.workspacePadding.right, 0);
        } else {
            mlp.rightMargin = 0;
            mlp.leftMargin = 0;
            setPadding(0, 0, 0, 0);
        }
        setLayoutParams(mlp);
        this.mNavBarScrimHeight = insets.bottom;
        InsettableFrameLayout.dispatchInsets(this, insets);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mNavBarScrimHeight > 0) {
            canvas.drawRect(0.0f, (float) (getHeight() - this.mNavBarScrimHeight), (float) getWidth(), (float) getHeight(), this.mNavBarScrimPaint);
        }
    }

    public int getCanvasClipTopForOverscroll() {
        if (this.mSpringViews.get(getSearchView().getId())) {
            return 0;
        }
        return this.mHeader.getTop();
    }

    private void rebindAdapters(boolean showTabs) {
        rebindAdapters(showTabs, false);
    }

    private void rebindAdapters(boolean showTabs, boolean force) {
        if (showTabs != this.mUsingTabs || force) {
            replaceRVContainer(showTabs);
            this.mUsingTabs = showTabs;
            this.mAllAppsStore.unregisterIconContainer(this.mAH[0].recyclerView);
            this.mAllAppsStore.unregisterIconContainer(this.mAH[1].recyclerView);
            if (this.mUsingTabs) {
                this.mAH[0].setup(this.mViewPager.getChildAt(0), this.mPersonalMatcher);
                this.mAH[1].setup(this.mViewPager.getChildAt(1), this.mWorkMatcher);
                onTabChanged(this.mViewPager.getNextPage());
            } else {
                this.mAH[0].setup(findViewById(R.id.apps_list_view), (ItemInfoMatcher) null);
                this.mAH[1].recyclerView = null;
            }
            setupHeader();
            this.mAllAppsStore.registerIconContainer(this.mAH[0].recyclerView);
            this.mAllAppsStore.registerIconContainer(this.mAH[1].recyclerView);
        }
    }

    private void replaceRVContainer(boolean showTabs) {
        for (int i = 0; i < this.mAH.length; i++) {
            if (this.mAH[i].recyclerView != null) {
                this.mAH[i].recyclerView.setLayoutManager((RecyclerView.LayoutManager) null);
            }
        }
        View oldView = getRecyclerViewContainer();
        int index = indexOfChild(oldView);
        removeView(oldView);
        View newView = LayoutInflater.from(getContext()).inflate(showTabs ? R.layout.all_apps_tabs : R.layout.all_apps_rv_layout, this, false);
        addView(newView, index);
        if (showTabs) {
            this.mViewPager = (AllAppsPagedView) newView;
            this.mViewPager.initParentViews(this);
            ((PersonalWorkSlidingTabStrip) this.mViewPager.getPageIndicator()).setContainerView(this);
            return;
        }
        this.mViewPager = null;
    }

    public View getRecyclerViewContainer() {
        return this.mViewPager != null ? this.mViewPager : findViewById(R.id.apps_list_view);
    }

    public void onTabChanged(int pos) {
        this.mHeader.setMainActive(pos == 0);
        reset(true);
        if (this.mAH[pos].recyclerView != null) {
            this.mAH[pos].recyclerView.bindFastScrollbar();
            findViewById(R.id.tab_personal).setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    AllAppsContainerView.this.mViewPager.snapToPage(0);
                }
            });
            findViewById(R.id.tab_work).setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    AllAppsContainerView.this.mViewPager.snapToPage(1);
                }
            });
        }
        if (pos == 1) {
            BottomUserEducationView.showIfNeeded(this.mLauncher);
        }
    }

    public AlphabeticalAppsList getApps() {
        return this.mAH[0].appsList;
    }

    public FloatingHeaderView getFloatingHeaderView() {
        return this.mHeader;
    }

    public View getSearchView() {
        return this.mSearchContainer;
    }

    public View getContentView() {
        return this.mViewPager == null ? getActiveRecyclerView() : this.mViewPager;
    }

    public RecyclerViewFastScroller getScrollBar() {
        AllAppsRecyclerView rv = getActiveRecyclerView();
        if (rv == null) {
            return null;
        }
        return rv.getScrollbar();
    }

    public void setupHeader() {
        this.mHeader.setVisibility(0);
        FloatingHeaderView floatingHeaderView = this.mHeader;
        AdapterHolder[] adapterHolderArr = this.mAH;
        boolean z = true;
        if (this.mAH[1].recyclerView != null) {
            z = false;
        }
        floatingHeaderView.setup(adapterHolderArr, z);
        int padding = this.mHeader.getMaxTranslation();
        for (int i = 0; i < this.mAH.length; i++) {
            this.mAH[i].padding.top = padding;
            this.mAH[i].applyPadding();
        }
    }

    public void setLastSearchQuery(String query) {
        for (AdapterHolder adapterHolder : this.mAH) {
            adapterHolder.adapter.setLastSearchQuery(query);
        }
        if (this.mUsingTabs != 0) {
            this.mSearchModeWhileUsingTabs = true;
            rebindAdapters(false);
        }
    }

    public void onClearSearchResult() {
        if (this.mSearchModeWhileUsingTabs) {
            rebindAdapters(true);
            this.mSearchModeWhileUsingTabs = false;
        }
    }

    public void onSearchResultsChanged() {
        for (int i = 0; i < this.mAH.length; i++) {
            if (this.mAH[i].recyclerView != null) {
                this.mAH[i].recyclerView.onSearchResultsChanged();
            }
        }
    }

    public void setRecyclerViewVerticalFadingEdgeEnabled(boolean enabled) {
        for (AdapterHolder applyVerticalFadingEdgeEnabled : this.mAH) {
            applyVerticalFadingEdgeEnabled.applyVerticalFadingEdgeEnabled(enabled);
        }
    }

    public void addElevationController(RecyclerView.OnScrollListener scrollListener) {
        if (!this.mUsingTabs) {
            this.mAH[0].recyclerView.addOnScrollListener(scrollListener);
        }
    }

    public boolean isHeaderVisible() {
        return this.mHeader != null && this.mHeader.getVisibility() == 0;
    }

    public void onScrollUpEnd() {
        if (this.mUsingTabs) {
            ((PersonalWorkSlidingTabStrip) findViewById(R.id.tabs)).highlightWorkTabIfNecessary();
        }
    }

    public void addSpringFromFlingUpdateListener(ValueAnimator animator, final float velocity) {
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean shouldSpring = true;

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (this.shouldSpring && valueAnimator.getAnimatedFraction() >= AllAppsContainerView.FLING_ANIMATION_THRESHOLD) {
                    final int searchViewId = AllAppsContainerView.this.getSearchView().getId();
                    AllAppsContainerView.this.addSpringView(searchViewId);
                    AllAppsContainerView.this.finishWithShiftAndVelocity(1.0f, velocity * AllAppsContainerView.FLING_VELOCITY_MULTIPLIER, new DynamicAnimation.OnAnimationEndListener() {
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            AllAppsContainerView.this.removeSpringView(searchViewId);
                        }
                    });
                    this.shouldSpring = false;
                }
            }
        });
    }

    public void getDrawingRect(Rect outRect) {
        super.getDrawingRect(outRect);
        outRect.offset(0, (int) getTranslationY());
    }

    public class AdapterHolder {
        public static final int MAIN = 0;
        public static final int WORK = 1;
        public final AllAppsGridAdapter adapter;
        final AlphabeticalAppsList appsList;
        final LinearLayoutManager layoutManager;
        final Rect padding = new Rect();
        AllAppsRecyclerView recyclerView;
        boolean verticalFadingEdge;

        AdapterHolder(boolean isWork) {
            this.appsList = new AlphabeticalAppsList(AllAppsContainerView.this.mLauncher, AllAppsContainerView.this.mAllAppsStore, isWork);
            this.adapter = new AllAppsGridAdapter(AllAppsContainerView.this.mLauncher, this.appsList);
            this.appsList.setAdapter(this.adapter);
            this.layoutManager = this.adapter.getLayoutManager();
        }

        /* access modifiers changed from: package-private */
        public void setup(@NonNull View rv, @Nullable ItemInfoMatcher matcher) {
            this.appsList.updateItemFilter(matcher);
            this.recyclerView = (AllAppsRecyclerView) rv;
            this.recyclerView.setEdgeEffectFactory(AllAppsContainerView.this.createEdgeEffectFactory());
            this.recyclerView.setApps(this.appsList, AllAppsContainerView.this.mUsingTabs);
            this.recyclerView.setLayoutManager(this.layoutManager);
            this.recyclerView.setAdapter(this.adapter);
            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
            FocusedItemDecorator focusedItemDecorator = new FocusedItemDecorator(this.recyclerView);
            this.recyclerView.addItemDecoration(focusedItemDecorator);
            this.adapter.setIconFocusListener(focusedItemDecorator.getFocusListener());
            applyVerticalFadingEdgeEnabled(this.verticalFadingEdge);
            applyPadding();
        }

        /* access modifiers changed from: package-private */
        public void applyPadding() {
            if (this.recyclerView != null) {
                this.recyclerView.setPadding(this.padding.left, this.padding.top, this.padding.right, this.padding.bottom);
            }
        }

        public void applyVerticalFadingEdgeEnabled(boolean enabled) {
            this.verticalFadingEdge = enabled;
            boolean z = false;
            AllAppsRecyclerView allAppsRecyclerView = AllAppsContainerView.this.mAH[0].recyclerView;
            if (!AllAppsContainerView.this.mUsingTabs && this.verticalFadingEdge) {
                z = true;
            }
            allAppsRecyclerView.setVerticalFadingEdgeEnabled(z);
        }
    }
}
