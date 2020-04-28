package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.views.RecyclerViewFastScroller;
import java.util.List;

public class AllAppsRecyclerView extends BaseRecyclerView implements UserEventDispatcher.LogContainerProvider {
    private AlphabeticalAppsList mApps;
    /* access modifiers changed from: private */
    public SparseIntArray mCachedScrollPositions;
    private AllAppsBackgroundDrawable mEmptySearchBackground;
    private int mEmptySearchBackgroundTopOffset;
    private AllAppsFastScrollHelper mFastScrollHelper;
    private final int mNumAppsPerRow;
    private SparseIntArray mViewHeights;

    public AllAppsRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.mViewHeights = new SparseIntArray();
        this.mCachedScrollPositions = new SparseIntArray();
        this.mEmptySearchBackgroundTopOffset = getResources().getDimensionPixelSize(R.dimen.all_apps_empty_search_bg_top_offset);
        this.mNumAppsPerRow = LauncherAppState.getIDP(context).numColumns;
    }

    public void setApps(AlphabeticalAppsList apps, boolean usingTabs) {
        this.mApps = apps;
        this.mFastScrollHelper = new AllAppsFastScrollHelper(this, apps);
    }

    public AlphabeticalAppsList getApps() {
        return this.mApps;
    }

    private void updatePoolSize() {
        DeviceProfile grid = Launcher.getLauncher(getContext()).getDeviceProfile();
        RecyclerView.RecycledViewPool pool = getRecycledViewPool();
        pool.setMaxRecycledViews(4, 1);
        pool.setMaxRecycledViews(16, 1);
        pool.setMaxRecycledViews(8, 1);
        pool.setMaxRecycledViews(2, this.mNumAppsPerRow * ((int) Math.ceil((double) (grid.availableHeightPx / grid.allAppsIconSizePx))));
        this.mViewHeights.clear();
        this.mViewHeights.put(2, grid.allAppsCellHeightPx);
    }

    public void scrollToTop() {
        if (this.mScrollbar != null) {
            this.mScrollbar.reattachThumbToScroll();
        }
        scrollToPosition(0);
    }

    public void onDraw(Canvas c) {
        if (this.mEmptySearchBackground != null && this.mEmptySearchBackground.getAlpha() > 0) {
            this.mEmptySearchBackground.draw(c);
        }
        super.onDraw(c);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return who == this.mEmptySearchBackground || super.verifyDrawable(who);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateEmptySearchBackgroundBounds();
        updatePoolSize();
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        if (this.mApps.hasFilter()) {
            targetParent.containerType = 8;
        } else {
            targetParent.containerType = 4;
        }
    }

    public void onSearchResultsChanged() {
        scrollToTop();
        if (this.mApps.hasNoFilteredResults()) {
            if (this.mEmptySearchBackground == null) {
                this.mEmptySearchBackground = DrawableFactory.get(getContext()).getAllAppsBackground(getContext());
                this.mEmptySearchBackground.setAlpha(0);
                this.mEmptySearchBackground.setCallback(this);
                updateEmptySearchBackgroundBounds();
            }
            this.mEmptySearchBackground.animateBgAlpha(1.0f, 150);
        } else if (this.mEmptySearchBackground != null) {
            this.mEmptySearchBackground.setBgAlpha(0.0f);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean result = super.onInterceptTouchEvent(e);
        if (!result && e.getAction() == 0 && this.mEmptySearchBackground != null && this.mEmptySearchBackground.getAlpha() > 0) {
            this.mEmptySearchBackground.setHotspot(e.getX(), e.getY());
        }
        return result;
    }

    public String scrollToPositionAtProgress(float touchFraction) {
        if (this.mApps.getNumAppRows() == 0) {
            return "";
        }
        stopScroll();
        List<AlphabeticalAppsList.FastScrollSectionInfo> fastScrollSections = this.mApps.getFastScrollerSections();
        AlphabeticalAppsList.FastScrollSectionInfo lastInfo = fastScrollSections.get(0);
        for (int i = 1; i < fastScrollSections.size(); i++) {
            AlphabeticalAppsList.FastScrollSectionInfo info = fastScrollSections.get(i);
            if (info.touchFraction > touchFraction) {
                break;
            }
            lastInfo = info;
        }
        this.mFastScrollHelper.smoothScrollToSection(getCurrentScrollY(), getAvailableScrollHeight(), lastInfo);
        return lastInfo.sectionName;
    }

    public void onFastScrollCompleted() {
        super.onFastScrollCompleted();
        this.mFastScrollHelper.onFastScrollCompleted();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                AllAppsRecyclerView.this.mCachedScrollPositions.clear();
            }
        });
        this.mFastScrollHelper.onSetAdapter((AllAppsGridAdapter) adapter);
    }

    /* access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public boolean isPaddingOffsetRequired() {
        return true;
    }

    /* access modifiers changed from: protected */
    public int getTopPaddingOffset() {
        return -getPaddingTop();
    }

    public void onUpdateScrollbar(int dy) {
        int thumbScrollY;
        if (this.mApps != null) {
            if (this.mApps.getAdapterItems().isEmpty() || this.mNumAppsPerRow == 0) {
                this.mScrollbar.setThumbOffsetY(-1);
                return;
            }
            int scrollY = getCurrentScrollY();
            if (scrollY < 0) {
                this.mScrollbar.setThumbOffsetY(-1);
                return;
            }
            int availableScrollBarHeight = getAvailableScrollBarHeight();
            int availableScrollHeight = getAvailableScrollHeight();
            if (availableScrollHeight <= 0) {
                this.mScrollbar.setThumbOffsetY(-1);
            } else if (!this.mScrollbar.isThumbDetached()) {
                synchronizeScrollBarThumbOffsetToViewScroll(scrollY, availableScrollHeight);
            } else if (!this.mScrollbar.isDraggingThumb()) {
                int scrollBarY = (int) ((((float) scrollY) / ((float) availableScrollHeight)) * ((float) availableScrollBarHeight));
                int thumbScrollY2 = this.mScrollbar.getThumbOffsetY();
                int diffScrollY = scrollBarY - thumbScrollY2;
                if (((float) (diffScrollY * dy)) > 0.0f) {
                    if (dy < 0) {
                        thumbScrollY = thumbScrollY2 + Math.max((int) (((float) (dy * thumbScrollY2)) / ((float) scrollBarY)), diffScrollY);
                    } else {
                        thumbScrollY = thumbScrollY2 + Math.min((int) (((float) ((availableScrollBarHeight - thumbScrollY2) * dy)) / ((float) (availableScrollBarHeight - scrollBarY))), diffScrollY);
                    }
                    int thumbScrollY3 = Math.max(0, Math.min(availableScrollBarHeight, thumbScrollY));
                    this.mScrollbar.setThumbOffsetY(thumbScrollY3);
                    if (scrollBarY == thumbScrollY3) {
                        this.mScrollbar.reattachThumbToScroll();
                        return;
                    }
                    return;
                }
                this.mScrollbar.setThumbOffsetY(thumbScrollY2);
            }
        }
    }

    public boolean supportsFastScrolling() {
        return !this.mApps.hasFilter();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        r1 = getChildAt(0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCurrentScrollY() {
        /*
            r5 = this;
            com.android.launcher3.allapps.AlphabeticalAppsList r0 = r5.mApps
            java.util.List r0 = r0.getAdapterItems()
            boolean r1 = r0.isEmpty()
            r2 = -1
            if (r1 != 0) goto L_0x0036
            int r1 = r5.mNumAppsPerRow
            if (r1 == 0) goto L_0x0036
            int r1 = r5.getChildCount()
            if (r1 != 0) goto L_0x0018
            goto L_0x0036
        L_0x0018:
            r1 = 0
            android.view.View r1 = r5.getChildAt(r1)
            int r3 = r5.getChildPosition(r1)
            if (r3 != r2) goto L_0x0024
            return r2
        L_0x0024:
            int r2 = r5.getPaddingTop()
            android.support.v7.widget.RecyclerView$LayoutManager r4 = r5.getLayoutManager()
            int r4 = r4.getDecoratedTop(r1)
            int r4 = r5.getCurrentScrollY(r3, r4)
            int r2 = r2 + r4
            return r2
        L_0x0036:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsRecyclerView.getCurrentScrollY():int");
    }

    public int getCurrentScrollY(int position, int offset) {
        List<AlphabeticalAppsList.AdapterItem> items = this.mApps.getAdapterItems();
        AlphabeticalAppsList.AdapterItem posItem = position < items.size() ? items.get(position) : null;
        int y = this.mCachedScrollPositions.get(position, -1);
        if (y < 0) {
            int y2 = 0;
            for (int i = 0; i < position; i++) {
                AlphabeticalAppsList.AdapterItem item = items.get(i);
                if (AllAppsGridAdapter.isIconViewType(item.viewType)) {
                    if (posItem != null && posItem.viewType == item.viewType && posItem.rowIndex == item.rowIndex) {
                        break;
                    } else if (item.rowAppIndex == 0) {
                        y2 += this.mViewHeights.get(item.viewType, 0);
                    }
                } else {
                    int elHeight = this.mViewHeights.get(item.viewType);
                    if (elHeight == 0) {
                        RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(i);
                        if (holder == null) {
                            RecyclerView.ViewHolder holder2 = getAdapter().createViewHolder(this, item.viewType);
                            getAdapter().onBindViewHolder(holder2, i);
                            holder2.itemView.measure(0, 0);
                            elHeight = holder2.itemView.getMeasuredHeight();
                            getRecycledViewPool().putRecycledView(holder2);
                        } else {
                            elHeight = holder.itemView.getMeasuredHeight();
                        }
                    }
                    y2 += elHeight;
                }
            }
            this.mCachedScrollPositions.put(position, y2);
            y = y2;
        }
        return y - offset;
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollHeight() {
        return ((getPaddingTop() + getCurrentScrollY(getAdapter().getItemCount(), 0)) - getHeight()) + getPaddingBottom();
    }

    public int getScrollBarTop() {
        return getResources().getDimensionPixelOffset(R.dimen.all_apps_header_top_padding);
    }

    public RecyclerViewFastScroller getScrollbar() {
        return this.mScrollbar;
    }

    private void updateEmptySearchBackgroundBounds() {
        if (this.mEmptySearchBackground != null) {
            int x = (getMeasuredWidth() - this.mEmptySearchBackground.getIntrinsicWidth()) / 2;
            int y = this.mEmptySearchBackgroundTopOffset;
            this.mEmptySearchBackground.setBounds(x, y, this.mEmptySearchBackground.getIntrinsicWidth() + x, this.mEmptySearchBackground.getIntrinsicHeight() + y);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
