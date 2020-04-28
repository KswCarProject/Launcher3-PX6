package com.android.launcher3.folder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.touch.ItemClickHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FolderPagedView extends PagedView<PageIndicatorDots> {
    private static final int REORDER_ANIMATION_DURATION = 230;
    private static final float SCROLL_HINT_FRACTION = 0.07f;
    private static final int START_VIEW_REORDER_DELAY = 30;
    private static final String TAG = "FolderPagedView";
    private static final float VIEW_REORDER_DELAY_FACTOR = 0.9f;
    private static final int[] sTmpArray = new int[2];
    private int mAllocatedContentSize;
    private final ViewGroupFocusHelper mFocusIndicatorHelper;
    private Folder mFolder;
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mGridCountX;
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mGridCountY;
    private final LayoutInflater mInflater;
    public final boolean mIsRtl;
    @ViewDebug.ExportedProperty(category = "launcher")
    private final int mMaxCountX;
    @ViewDebug.ExportedProperty(category = "launcher")
    private final int mMaxCountY;
    @ViewDebug.ExportedProperty(category = "launcher")
    private final int mMaxItemsPerPage;
    final ArrayMap<View, Runnable> mPendingAnimations = new ArrayMap<>();

    public FolderPagedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InvariantDeviceProfile profile = LauncherAppState.getIDP(context);
        this.mMaxCountX = profile.numFolderColumns;
        this.mMaxCountY = profile.numFolderRows;
        this.mMaxItemsPerPage = this.mMaxCountX * this.mMaxCountY;
        this.mInflater = LayoutInflater.from(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        setImportantForAccessibility(1);
        this.mFocusIndicatorHelper = new ViewGroupFocusHelper(this);
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        this.mPageIndicator = folder.findViewById(R.id.folder_page_indicator);
        initParentViews(folder);
    }

    public static void calculateGridSize(int count, int countX, int countY, int maxCountX, int maxCountY, int maxItemsPerPage, int[] out) {
        boolean done;
        int gridCountX = countX;
        int gridCountY = countY;
        if (count >= maxItemsPerPage) {
            gridCountX = maxCountX;
            gridCountY = maxCountY;
            done = true;
        } else {
            done = false;
        }
        while (true) {
            boolean z = true;
            if (!done) {
                int oldCountX = gridCountX;
                int oldCountY = gridCountY;
                if (gridCountX * gridCountY < count) {
                    if ((gridCountX <= gridCountY || gridCountY == maxCountY) && gridCountX < maxCountX) {
                        gridCountX++;
                    } else if (gridCountY < maxCountY) {
                        gridCountY++;
                    }
                    if (gridCountY == 0) {
                        gridCountY++;
                    }
                } else if ((gridCountY - 1) * gridCountX >= count && gridCountY >= gridCountX) {
                    gridCountY = Math.max(0, gridCountY - 1);
                } else if ((gridCountX - 1) * gridCountY >= count) {
                    gridCountX = Math.max(0, gridCountX - 1);
                }
                if (gridCountX != oldCountX || gridCountY != oldCountY) {
                    z = false;
                }
                done = z;
            } else {
                out[0] = gridCountX;
                out[1] = gridCountY;
                return;
            }
        }
    }

    public void setupContentDimensions(int count) {
        this.mAllocatedContentSize = count;
        calculateGridSize(count, this.mGridCountX, this.mGridCountY, this.mMaxCountX, this.mMaxCountY, this.mMaxItemsPerPage, sTmpArray);
        this.mGridCountX = sTmpArray[0];
        this.mGridCountY = sTmpArray[1];
        for (int i = getPageCount() - 1; i >= 0; i--) {
            getPageAt(i).setGridSize(this.mGridCountX, this.mGridCountY);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mFocusIndicatorHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public void bindItems(ArrayList<ShortcutInfo> items) {
        ArrayList<View> icons = new ArrayList<>();
        Iterator<ShortcutInfo> it = items.iterator();
        while (it.hasNext()) {
            icons.add(createNewView(it.next()));
        }
        arrangeChildren(icons, icons.size(), false);
    }

    public void allocateSpaceForRank(int rank) {
        ArrayList<View> views = new ArrayList<>(this.mFolder.getItemsInReadingOrder());
        views.add(rank, (Object) null);
        arrangeChildren(views, views.size(), false);
    }

    public int allocateRankForNewItem() {
        int rank = getItemCount();
        allocateSpaceForRank(rank);
        setCurrentPage(rank / this.mMaxItemsPerPage);
        return rank;
    }

    public View createAndAddViewForRank(ShortcutInfo item, int rank) {
        View icon = createNewView(item);
        allocateSpaceForRank(rank);
        addViewForRank(icon, item, rank);
        return icon;
    }

    public void addViewForRank(View view, ShortcutInfo item, int rank) {
        int pagePos = rank % this.mMaxItemsPerPage;
        item.rank = rank;
        item.cellX = pagePos % this.mGridCountX;
        item.cellY = pagePos / this.mGridCountX;
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) view.getLayoutParams();
        lp.cellX = item.cellX;
        lp.cellY = item.cellY;
        getPageAt(rank / this.mMaxItemsPerPage).addViewToCellLayout(view, -1, this.mFolder.mLauncher.getViewIdForItem(item), lp, true);
    }

    @SuppressLint({"InflateParams"})
    public View createNewView(ShortcutInfo item) {
        BubbleTextView textView = (BubbleTextView) this.mInflater.inflate(R.layout.folder_application, (ViewGroup) null, false);
        textView.applyFromShortcutInfo(item);
        textView.setHapticFeedbackEnabled(false);
        textView.setOnClickListener(ItemClickHandler.INSTANCE);
        textView.setOnLongClickListener(this.mFolder);
        textView.setOnFocusChangeListener(this.mFocusIndicatorHelper);
        textView.setLayoutParams(new CellLayout.LayoutParams(item.cellX, item.cellY, item.spanX, item.spanY));
        return textView;
    }

    public CellLayout getPageAt(int index) {
        return (CellLayout) getChildAt(index);
    }

    public CellLayout getCurrentCellLayout() {
        return getPageAt(getNextPage());
    }

    private CellLayout createAndAddNewPage() {
        DeviceProfile grid = Launcher.getLauncher(getContext()).getDeviceProfile();
        CellLayout page = (CellLayout) this.mInflater.inflate(R.layout.folder_page, this, false);
        page.setCellDimensions(grid.folderCellWidthPx, grid.folderCellHeightPx);
        page.getShortcutsAndWidgets().setMotionEventSplittingEnabled(false);
        page.setInvertIfRtl(true);
        page.setGridSize(this.mGridCountX, this.mGridCountY);
        addView(page, -1, generateDefaultLayoutParams());
        return page;
    }

    /* access modifiers changed from: protected */
    public int getChildGap() {
        return getPaddingLeft() + getPaddingRight();
    }

    public void setFixedSize(int width, int height) {
        int width2 = width - (getPaddingLeft() + getPaddingRight());
        int height2 = height - (getPaddingTop() + getPaddingBottom());
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((CellLayout) getChildAt(i)).setFixedSize(width2, height2);
        }
    }

    public void removeItem(View v) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getPageAt(i).removeView(v);
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ((PageIndicatorDots) this.mPageIndicator).setScroll(l, this.mMaxScrollX);
    }

    public void arrangeChildren(ArrayList<View> list, int itemCount) {
        arrangeChildren(list, itemCount, true);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: com.android.launcher3.CellLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    @android.annotation.SuppressLint({"RtlHardcoded"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void arrangeChildren(java.util.ArrayList<android.view.View> r26, int r27, boolean r28) {
        /*
            r25 = this;
            r0 = r25
            r1 = r27
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r4 = 0
        L_0x000a:
            int r5 = r25.getChildCount()
            if (r4 >= r5) goto L_0x001f
            android.view.View r5 = r0.getChildAt(r4)
            com.android.launcher3.CellLayout r5 = (com.android.launcher3.CellLayout) r5
            r5.removeAllViews()
            r2.add(r5)
            int r4 = r4 + 1
            goto L_0x000a
        L_0x001f:
            r0.setupContentDimensions(r1)
            java.util.Iterator r4 = r2.iterator()
            r5 = 0
            r6 = 0
            com.android.launcher3.folder.FolderIconPreviewVerifier r7 = new com.android.launcher3.folder.FolderIconPreviewVerifier
            android.content.Context r8 = r25.getContext()
            com.android.launcher3.Launcher r8 = com.android.launcher3.Launcher.getLauncher(r8)
            com.android.launcher3.DeviceProfile r8 = r8.getDeviceProfile()
            com.android.launcher3.InvariantDeviceProfile r8 = r8.inv
            r7.<init>(r8)
            r8 = 0
            r9 = r8
            r8 = r6
            r6 = r5
            r5 = 0
        L_0x0040:
            if (r5 >= r1) goto L_0x0100
            int r10 = r26.size()
            if (r10 <= r5) goto L_0x0051
            r10 = r26
            java.lang.Object r11 = r10.get(r5)
            android.view.View r11 = (android.view.View) r11
            goto L_0x0054
        L_0x0051:
            r10 = r26
            r11 = 0
        L_0x0054:
            if (r6 == 0) goto L_0x005a
            int r12 = r0.mMaxItemsPerPage
            if (r8 < r12) goto L_0x006d
        L_0x005a:
            boolean r12 = r4.hasNext()
            if (r12 == 0) goto L_0x0068
            java.lang.Object r12 = r4.next()
            r6 = r12
            com.android.launcher3.CellLayout r6 = (com.android.launcher3.CellLayout) r6
            goto L_0x006c
        L_0x0068:
            com.android.launcher3.CellLayout r6 = r25.createAndAddNewPage()
        L_0x006c:
            r8 = 0
        L_0x006d:
            if (r11 == 0) goto L_0x00f2
            android.view.ViewGroup$LayoutParams r12 = r11.getLayoutParams()
            r15 = r12
            com.android.launcher3.CellLayout$LayoutParams r15 = (com.android.launcher3.CellLayout.LayoutParams) r15
            int r12 = r0.mGridCountX
            int r14 = r8 % r12
            int r12 = r0.mGridCountX
            int r13 = r8 / r12
            java.lang.Object r12 = r11.getTag()
            com.android.launcher3.ItemInfo r12 = (com.android.launcher3.ItemInfo) r12
            int r3 = r12.cellX
            if (r3 != r14) goto L_0x0094
            int r3 = r12.cellY
            if (r3 != r13) goto L_0x0094
            int r3 = r12.rank
            if (r3 == r9) goto L_0x0091
            goto L_0x0094
        L_0x0091:
            r24 = r2
            goto L_0x00c0
        L_0x0094:
            r12.cellX = r14
            r12.cellY = r13
            r12.rank = r9
            if (r28 == 0) goto L_0x00be
            com.android.launcher3.folder.Folder r1 = r0.mFolder
            com.android.launcher3.Launcher r1 = r1.mLauncher
            com.android.launcher3.model.ModelWriter r16 = r1.getModelWriter()
            com.android.launcher3.folder.Folder r1 = r0.mFolder
            com.android.launcher3.FolderInfo r1 = r1.mInfo
            r24 = r2
            long r1 = r1.id
            r20 = 0
            int r3 = r12.cellX
            int r10 = r12.cellY
            r17 = r12
            r18 = r1
            r22 = r3
            r23 = r10
            r16.addOrMoveItemInDatabase(r17, r18, r20, r22, r23)
            goto L_0x00c0
        L_0x00be:
            r24 = r2
        L_0x00c0:
            int r1 = r12.cellX
            r15.cellX = r1
            int r1 = r12.cellY
            r15.cellY = r1
            r1 = -1
            com.android.launcher3.folder.Folder r2 = r0.mFolder
            com.android.launcher3.Launcher r2 = r2.mLauncher
            int r2 = r2.getViewIdForItem(r12)
            r17 = 1
            r3 = r12
            r12 = r6
            r10 = r13
            r13 = r11
            r18 = r14
            r14 = r1
            r1 = r15
            r15 = r2
            r16 = r1
            r12.addViewToCellLayout(r13, r14, r15, r16, r17)
            boolean r2 = r7.isItemInPreview(r9)
            if (r2 == 0) goto L_0x00f4
            boolean r2 = r11 instanceof com.android.launcher3.BubbleTextView
            if (r2 == 0) goto L_0x00f4
            r2 = r11
            com.android.launcher3.BubbleTextView r2 = (com.android.launcher3.BubbleTextView) r2
            r2.verifyHighRes()
            goto L_0x00f4
        L_0x00f2:
            r24 = r2
        L_0x00f4:
            int r9 = r9 + 1
            int r8 = r8 + 1
            int r5 = r5 + 1
            r2 = r24
            r1 = r27
            goto L_0x0040
        L_0x0100:
            r24 = r2
            r1 = 0
        L_0x0103:
            boolean r2 = r4.hasNext()
            if (r2 == 0) goto L_0x0114
            java.lang.Object r2 = r4.next()
            android.view.View r2 = (android.view.View) r2
            r0.removeView(r2)
            r1 = 1
            goto L_0x0103
        L_0x0114:
            if (r1 == 0) goto L_0x011b
            r2 = 0
            r0.setCurrentPage(r2)
            goto L_0x011c
        L_0x011b:
            r2 = 0
        L_0x011c:
            int r3 = r25.getPageCount()
            r5 = 1
            if (r3 <= r5) goto L_0x0125
            r3 = 1
            goto L_0x0126
        L_0x0125:
            r3 = 0
        L_0x0126:
            r0.setEnableOverscroll(r3)
            android.view.View r3 = r0.mPageIndicator
            com.android.launcher3.pageindicators.PageIndicatorDots r3 = (com.android.launcher3.pageindicators.PageIndicatorDots) r3
            int r10 = r25.getPageCount()
            if (r10 <= r5) goto L_0x0134
            goto L_0x0136
        L_0x0134:
            r2 = 8
        L_0x0136:
            r3.setVisibility(r2)
            com.android.launcher3.folder.Folder r2 = r0.mFolder
            com.android.launcher3.ExtendedEditText r2 = r2.mFolderName
            int r3 = r25.getPageCount()
            if (r3 <= r5) goto L_0x014b
            boolean r3 = r0.mIsRtl
            if (r3 == 0) goto L_0x0149
            r5 = 5
            goto L_0x014b
        L_0x0149:
            r5 = 3
        L_0x014b:
            r2.setGravity(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderPagedView.arrangeChildren(java.util.ArrayList, int, boolean):void");
    }

    public int getDesiredWidth() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingRight() + getPageAt(0).getDesiredWidth() + getPaddingLeft();
    }

    public int getDesiredHeight() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingBottom() + getPageAt(0).getDesiredHeight() + getPaddingTop();
    }

    public int getItemCount() {
        int lastPageIndex = getChildCount() - 1;
        if (lastPageIndex < 0) {
            return 0;
        }
        return getPageAt(lastPageIndex).getShortcutsAndWidgets().getChildCount() + (this.mMaxItemsPerPage * lastPageIndex);
    }

    public int findNearestArea(int pixelX, int pixelY) {
        int pageIndex = getNextPage();
        CellLayout page = getPageAt(pageIndex);
        page.findNearestArea(pixelX, pixelY, 1, 1, sTmpArray);
        if (this.mFolder.isLayoutRtl()) {
            sTmpArray[0] = (page.getCountX() - sTmpArray[0]) - 1;
        }
        return Math.min(this.mAllocatedContentSize - 1, (this.mMaxItemsPerPage * pageIndex) + (sTmpArray[1] * this.mGridCountX) + sTmpArray[0]);
    }

    public View getFirstItem() {
        if (getChildCount() < 1) {
            return null;
        }
        ShortcutAndWidgetContainer currContainer = getCurrentCellLayout().getShortcutsAndWidgets();
        if (this.mGridCountX > 0) {
            return currContainer.getChildAt(0, 0);
        }
        return currContainer.getChildAt(0);
    }

    public View getLastItem() {
        if (getChildCount() < 1) {
            return null;
        }
        ShortcutAndWidgetContainer currContainer = getCurrentCellLayout().getShortcutsAndWidgets();
        int lastRank = currContainer.getChildCount() - 1;
        if (this.mGridCountX > 0) {
            return currContainer.getChildAt(lastRank % this.mGridCountX, lastRank / this.mGridCountX);
        }
        return currContainer.getChildAt(lastRank);
    }

    public View iterateOverItems(Workspace.ItemOperator op) {
        for (int k = 0; k < getChildCount(); k++) {
            CellLayout page = getPageAt(k);
            for (int j = 0; j < page.getCountY(); j++) {
                for (int i = 0; i < page.getCountX(); i++) {
                    View v = page.getChildAt(i, j);
                    if (v != null && op.evaluate((ItemInfo) v.getTag(), v)) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    public String getAccessibilityDescription() {
        return getContext().getString(R.string.folder_opened, new Object[]{Integer.valueOf(this.mGridCountX), Integer.valueOf(this.mGridCountY)});
    }

    public void setFocusOnFirstChild() {
        View firstChild = getCurrentCellLayout().getChildAt(0, 0);
        if (firstChild != null) {
            firstChild.requestFocus();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int prevPage) {
        super.notifyPageSwitchListener(prevPage);
        if (this.mFolder != null) {
            this.mFolder.updateTextViewFocus();
        }
    }

    public void showScrollHint(int direction) {
        int delta = (getScrollForPage(getNextPage()) + ((int) (((float) getWidth()) * ((direction == 0) ^ this.mIsRtl ? -0.07f : SCROLL_HINT_FRACTION)))) - getScrollX();
        if (delta != 0) {
            this.mScroller.setInterpolator(Interpolators.DEACCEL);
            this.mScroller.startScroll(getScrollX(), 0, delta, 0, 500);
            invalidate();
        }
    }

    public void clearScrollHint() {
        if (getScrollX() != getScrollForPage(getNextPage())) {
            snapToPage(getNextPage());
        }
    }

    public void completePendingPageChanges() {
        if (!this.mPendingAnimations.isEmpty()) {
            for (Map.Entry<View, Runnable> e : new ArrayMap<>(this.mPendingAnimations).entrySet()) {
                e.getKey().animate().cancel();
                e.getValue().run();
            }
        }
    }

    public boolean rankOnCurrentPage(int rank) {
        return rank / this.mMaxItemsPerPage == getNextPage();
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        verifyVisibleHighResIcons(getCurrentPage() - 1);
        verifyVisibleHighResIcons(getCurrentPage() + 1);
    }

    public void verifyVisibleHighResIcons(int pageNo) {
        CellLayout page = getPageAt(pageNo);
        if (page != null) {
            ShortcutAndWidgetContainer parent = page.getShortcutsAndWidgets();
            for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                BubbleTextView icon = (BubbleTextView) parent.getChildAt(i);
                icon.verifyHighRes();
                Drawable d = icon.getCompoundDrawables()[1];
                if (d != null) {
                    d.setCallback(icon);
                }
            }
        }
    }

    public int getAllocatedContentSize() {
        return this.mAllocatedContentSize;
    }

    public void realTimeReorder(int empty, int target) {
        int endPos;
        int startPos;
        int moveEnd;
        int direction;
        int pagePosT;
        int pageT;
        int moveEnd2;
        int startPos2;
        int i = empty;
        int i2 = target;
        completePendingPageChanges();
        int delay = 0;
        float delayAmount = 30.0f;
        int pageToAnimate = getNextPage();
        int pageT2 = i2 / this.mMaxItemsPerPage;
        int pagePosT2 = i2 % this.mMaxItemsPerPage;
        if (pageT2 != pageToAnimate) {
            Log.e(TAG, "Cannot animate when the target cell is invisible");
        }
        int pagePosE = i % this.mMaxItemsPerPage;
        int pageE = i / this.mMaxItemsPerPage;
        if (i2 != i) {
            int moveStart = -1;
            if (i2 > i) {
                direction = 1;
                if (pageE < pageToAnimate) {
                    moveStart = empty;
                    moveEnd = this.mMaxItemsPerPage * pageToAnimate;
                    startPos = 0;
                } else {
                    moveEnd = -1;
                    startPos = pagePosE;
                }
                endPos = pagePosT2;
            } else {
                direction = -1;
                if (pageE > pageToAnimate) {
                    moveStart = empty;
                    moveEnd2 = ((pageToAnimate + 1) * this.mMaxItemsPerPage) - 1;
                    startPos2 = this.mMaxItemsPerPage - 1;
                } else {
                    moveEnd2 = -1;
                    startPos2 = pagePosE;
                }
                endPos = pagePosT2;
            }
            while (moveStart != moveEnd) {
                int rankToMove = moveStart + direction;
                int p = rankToMove / this.mMaxItemsPerPage;
                int pagePos = rankToMove % this.mMaxItemsPerPage;
                int x = pagePos % this.mGridCountX;
                int delay2 = delay;
                int y = pagePos / this.mGridCountX;
                int i3 = pagePos;
                CellLayout page = getPageAt(p);
                float delayAmount2 = delayAmount;
                final View v = page.getChildAt(x, y);
                if (v == null) {
                    int i4 = x;
                    int i5 = y;
                    pageT = pageT2;
                    pagePosT = pagePosT2;
                } else if (pageToAnimate != p) {
                    page.removeView(v);
                    CellLayout cellLayout = page;
                    addViewForRank(v, (ShortcutInfo) v.getTag(), moveStart);
                    int i6 = x;
                    int i7 = y;
                    pageT = pageT2;
                    pagePosT = pagePosT2;
                } else {
                    final int newRank = moveStart;
                    int i8 = x;
                    final float oldTranslateX = v.getTranslationX();
                    int i9 = y;
                    Runnable endAction = new Runnable() {
                        public void run() {
                            FolderPagedView.this.mPendingAnimations.remove(v);
                            v.setTranslationX(oldTranslateX);
                            ((CellLayout) v.getParent().getParent()).removeView(v);
                            FolderPagedView.this.addViewForRank(v, (ShortcutInfo) v.getTag(), newRank);
                        }
                    };
                    int i10 = newRank;
                    float f = oldTranslateX;
                    pageT = pageT2;
                    pagePosT = pagePosT2;
                    v.animate().translationXBy((float) ((direction > 0) ^ this.mIsRtl ? -v.getWidth() : v.getWidth())).setDuration(230).setStartDelay(0).withEndAction(endAction);
                    this.mPendingAnimations.put(v, endAction);
                }
                moveStart = rankToMove;
                delay = delay2;
                delayAmount = delayAmount2;
                pageT2 = pageT;
                pagePosT2 = pagePosT;
                int i11 = empty;
                int i12 = target;
            }
            int delay3 = delay;
            float delayAmount3 = delayAmount;
            int i13 = pageT2;
            int i14 = pagePosT2;
            if ((endPos - startPos) * direction > 0) {
                CellLayout page2 = getPageAt(pageToAnimate);
                int delay4 = delay3;
                for (int i15 = startPos; i15 != endPos; i15 += direction) {
                    int nextPos = i15 + direction;
                    View v2 = page2.getChildAt(nextPos % this.mGridCountX, nextPos / this.mGridCountX);
                    if (v2 != null) {
                        ((ItemInfo) v2.getTag()).rank -= direction;
                    }
                    if (page2.animateChildToPosition(v2, i15 % this.mGridCountX, i15 / this.mGridCountX, REORDER_ANIMATION_DURATION, delay4, true, true)) {
                        delay4 = (int) (((float) delay4) + delayAmount3);
                        delayAmount3 *= VIEW_REORDER_DELAY_FACTOR;
                    }
                }
            }
        }
    }

    public int itemsPerPage() {
        return this.mMaxItemsPerPage;
    }
}
