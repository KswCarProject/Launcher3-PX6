package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHost;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.accessibility.WorkspaceAccessibilityHelper;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.badge.FolderBadgeInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.SpringLoadedDragController;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.pageindicators.WorkspacePageIndicator;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.touch.WorkspaceTouchListener;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.WallpaperOffsetInterpolator;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Workspace extends PagedView<WorkspacePageIndicator> implements DropTarget, DragSource, View.OnTouchListener, DragController.DragListener, Insettable, LauncherStateManager.StateHandler {
    private static final int ADJACENT_SCREEN_DROP_DURATION = 300;
    private static final float ALLOW_DROP_TRANSITION_PROGRESS = 0.25f;
    public static final int ANIMATE_INTO_POSITION_AND_DISAPPEAR = 0;
    public static final int ANIMATE_INTO_POSITION_AND_REMAIN = 1;
    public static final int ANIMATE_INTO_POSITION_AND_RESIZE = 2;
    public static final int CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION = 4;
    public static final int COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION = 3;
    private static final int DEFAULT_PAGE = 0;
    private static final int DRAG_MODE_ADD_TO_FOLDER = 2;
    private static final int DRAG_MODE_CREATE_FOLDER = 1;
    private static final int DRAG_MODE_NONE = 0;
    private static final int DRAG_MODE_REORDER = 3;
    private static final boolean ENFORCE_DRAG_EVENT_ORDER = false;
    public static final long EXTRA_EMPTY_SCREEN_ID = -201;
    private static final int FADE_EMPTY_SCREEN_DURATION = 150;
    private static final float FINISHED_SWITCHING_STATE_TRANSITION_PROGRESS = 0.5f;
    public static final long FIRST_SCREEN_ID = 0;
    private static final int FOLDER_CREATION_TIMEOUT = 0;
    private static final boolean MAP_NO_RECURSE = false;
    private static final boolean MAP_RECURSE = true;
    static final float MAX_SWIPE_ANGLE = 1.0471976f;
    public static final int REORDER_TIMEOUT = 650;
    private static final int SNAP_OFF_EMPTY_SCREEN_DURATION = 400;
    static final float START_DAMPING_TOUCH_SLOP_ANGLE = 0.5235988f;
    private static final String TAG = "Launcher.Workspace";
    static final float TOUCH_SLOP_DAMPING_FACTOR = 4.0f;
    private boolean mAddToExistingFolderOnDrop;
    boolean mChildrenLayersEnabled;
    private boolean mCreateUserFolderOnDrop;
    private float mCurrentScale;
    boolean mDeferRemoveExtraEmptyScreen;
    DragController mDragController;
    private CellLayout.CellInfo mDragInfo;
    private int mDragMode;
    private FolderIcon mDragOverFolderIcon;
    private int mDragOverX;
    private int mDragOverY;
    private CellLayout mDragOverlappingLayout;
    private ShortcutAndWidgetContainer mDragSourceInternal;
    CellLayout mDragTargetLayout;
    float[] mDragViewVisualCenter;
    private CellLayout mDropToLayout;
    /* access modifiers changed from: private */
    public PreviewBackground mFolderCreateBg;
    private final Alarm mFolderCreationAlarm;
    private boolean mForceDrawAdjacentPages;
    private boolean mIsSwitchingState;
    float mLastOverlayScroll;
    int mLastReorderX;
    int mLastReorderY;
    final Launcher mLauncher;
    Launcher.LauncherOverlay mLauncherOverlay;
    private LayoutTransition mLayoutTransition;
    private float mMaxDistanceForFolderCreation;
    private Runnable mOnOverlayHiddenCallback;
    /* access modifiers changed from: private */
    public DragPreviewProvider mOutlineProvider;
    boolean mOverlayShown;
    private float mOverlayTranslation;
    Runnable mRemoveEmptyScreenRunnable;
    private final Alarm mReorderAlarm;
    private final ArrayList<Integer> mRestoredPages;
    private SparseArray<Parcelable> mSavedStates;
    final ArrayList<Long> mScreenOrder;
    boolean mScrollInteractionBegan;
    private SpringLoadedDragController mSpringLoadedDragController;
    boolean mStartedSendingScrollEvents;
    private final WorkspaceStateTransitionAnimation mStateTransitionAnimation;
    private boolean mStripScreensOnPageStopMoving;
    int[] mTargetCell;
    private final float[] mTempTouchCoordinates;
    private final int[] mTempXY;
    /* access modifiers changed from: private */
    public float mTransitionProgress;
    private boolean mUnlockWallpaperFromDefaultPageOnLayout;
    final WallpaperManager mWallpaperManager;
    final WallpaperOffsetInterpolator mWallpaperOffset;
    private boolean mWorkspaceFadeInAdjacentScreens;
    final LongArrayMap<CellLayout> mWorkspaceScreens;
    private float mXDown;
    private float mYDown;

    public interface ItemOperator {
        boolean evaluate(ItemInfo itemInfo, View view);
    }

    public Workspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Workspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWorkspaceScreens = new LongArrayMap<>();
        this.mScreenOrder = new ArrayList<>();
        this.mDeferRemoveExtraEmptyScreen = false;
        this.mTargetCell = new int[2];
        this.mDragOverX = -1;
        this.mDragOverY = -1;
        this.mDragTargetLayout = null;
        this.mDragOverlappingLayout = null;
        this.mDropToLayout = null;
        this.mTempXY = new int[2];
        this.mDragViewVisualCenter = new float[2];
        this.mTempTouchCoordinates = new float[2];
        this.mIsSwitchingState = false;
        this.mChildrenLayersEnabled = true;
        this.mStripScreensOnPageStopMoving = false;
        this.mOutlineProvider = null;
        this.mFolderCreationAlarm = new Alarm();
        this.mReorderAlarm = new Alarm();
        this.mDragOverFolderIcon = null;
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDragMode = 0;
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
        this.mRestoredPages = new ArrayList<>();
        this.mLastOverlayScroll = 0.0f;
        this.mOverlayShown = false;
        this.mForceDrawAdjacentPages = false;
        this.mLauncher = Launcher.getLauncher(context);
        this.mStateTransitionAnimation = new WorkspaceStateTransitionAnimation(this.mLauncher, this);
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mWallpaperOffset = new WallpaperOffsetInterpolator(this);
        setHapticFeedbackEnabled(false);
        initWorkspace();
        setMotionEventSplittingEnabled(true);
        setOnTouchListener(new WorkspaceTouchListener(this.mLauncher, this));
    }

    public void setInsets(Rect insets) {
        this.mInsets.set(insets);
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        this.mMaxDistanceForFolderCreation = ((float) grid.iconSizePx) * 0.8f;
        this.mWorkspaceFadeInAdjacentScreens = grid.shouldFadeAdjacentWorkspaceScreens();
        Rect padding = grid.workspacePadding;
        setPadding(padding.left, padding.top, padding.right, padding.bottom);
        if (grid.shouldFadeAdjacentWorkspaceScreens()) {
            setPageSpacing(grid.defaultPageSpacingPx);
        } else {
            setPageSpacing(Math.max(grid.defaultPageSpacingPx, padding.left + 1));
        }
        int paddingLeftRight = grid.cellLayoutPaddingLeftRightPx;
        int paddingBottom = grid.cellLayoutBottomPaddingPx;
        for (int i = this.mWorkspaceScreens.size() - 1; i >= 0; i--) {
            ((CellLayout) this.mWorkspaceScreens.valueAt(i)).setPadding(paddingLeftRight, 0, paddingLeftRight, paddingBottom);
        }
    }

    public int[] estimateItemSize(ItemInfo itemInfo) {
        int[] size = new int[2];
        if (getChildCount() > 0) {
            CellLayout cl = (CellLayout) getChildAt(0);
            boolean isWidget = itemInfo.itemType == 4;
            Rect r = estimateItemPosition(cl, 0, 0, itemInfo.spanX, itemInfo.spanY);
            float scale = 1.0f;
            if (isWidget) {
                DeviceProfile profile = this.mLauncher.getDeviceProfile();
                scale = Utilities.shrinkRect(r, profile.appWidgetScale.x, profile.appWidgetScale.y);
            }
            size[0] = r.width();
            size[1] = r.height();
            if (isWidget) {
                size[0] = (int) (((float) size[0]) / scale);
                size[1] = (int) (((float) size[1]) / scale);
            }
            return size;
        }
        size[0] = Integer.MAX_VALUE;
        size[1] = Integer.MAX_VALUE;
        return size;
    }

    public float getWallpaperOffsetForCenterPage() {
        return this.mWallpaperOffset.wallpaperOffsetForScroll(getScrollForPage(getPageNearestToCenterOfScreen()));
    }

    public Rect estimateItemPosition(CellLayout cl, int hCell, int vCell, int hSpan, int vSpan) {
        Rect r = new Rect();
        cl.cellToRect(hCell, vCell, hSpan, vSpan, r);
        return r;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        if (!(this.mDragInfo == null || this.mDragInfo.cell == null)) {
            ((CellLayout) this.mDragInfo.cell.getParent().getParent()).markCellsAsUnoccupiedForView(this.mDragInfo.cell);
        }
        if (!(this.mOutlineProvider == null || dragObject.dragView == null)) {
            this.mOutlineProvider.generateDragOutline(dragObject.dragView.getPreviewBitmap());
        }
        updateChildrenLayersEnabled();
        if (!options.isAccessibleDrag || dragObject.dragSource == this) {
            this.mDeferRemoveExtraEmptyScreen = false;
            addExtraEmptyScreenOnDrag();
            if (dragObject.dragInfo.itemType == 4 && dragObject.dragSource != this) {
                int pageIndex = getPageNearestToCenterOfScreen();
                while (true) {
                    if (pageIndex >= getPageCount()) {
                        break;
                    } else if (((CellLayout) getPageAt(pageIndex)).hasReorderSolution(dragObject.dragInfo)) {
                        setCurrentPage(pageIndex);
                        break;
                    } else {
                        pageIndex++;
                    }
                }
            }
        }
        this.mLauncher.getStateManager().goToState(LauncherState.SPRING_LOADED);
    }

    public void deferRemoveExtraEmptyScreen() {
        this.mDeferRemoveExtraEmptyScreen = true;
    }

    public void onDragEnd() {
        if (!this.mDeferRemoveExtraEmptyScreen) {
            removeExtraEmptyScreen(true, this.mDragSourceInternal != null);
        }
        updateChildrenLayersEnabled();
        this.mDragInfo = null;
        this.mOutlineProvider = null;
        this.mDragSourceInternal = null;
    }

    /* access modifiers changed from: protected */
    public void initWorkspace() {
        this.mCurrentPage = 0;
        setClipToPadding(false);
        setupLayoutTransition();
        setWallpaperDimension();
    }

    private void setupLayoutTransition() {
        this.mLayoutTransition = new LayoutTransition();
        this.mLayoutTransition.enableTransitionType(3);
        this.mLayoutTransition.enableTransitionType(1);
        this.mLayoutTransition.disableTransitionType(2);
        this.mLayoutTransition.disableTransitionType(0);
        setLayoutTransition(this.mLayoutTransition);
    }

    /* access modifiers changed from: package-private */
    public void enableLayoutTransitions() {
        setLayoutTransition(this.mLayoutTransition);
    }

    /* access modifiers changed from: package-private */
    public void disableLayoutTransitions() {
        setLayoutTransition((LayoutTransition) null);
    }

    public void onViewAdded(View child) {
        if (child instanceof CellLayout) {
            CellLayout cl = (CellLayout) child;
            cl.setOnInterceptTouchListener(this);
            cl.setImportantForAccessibility(2);
            super.onViewAdded(child);
            return;
        }
        throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
    }

    public boolean isTouchActive() {
        return this.mTouchState != 0;
    }

    public void bindAndInitFirstWorkspaceScreen(View qsb) {
        CellLayout firstPage = insertNewWorkspaceScreen(0, 0);
        if (qsb == null) {
            qsb = LayoutInflater.from(getContext()).inflate(R.layout.search_container_workspace, firstPage, false);
        }
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(0, 0, firstPage.getCountX(), 1);
        lp.canReorder = false;
        if (!firstPage.addViewToCellLayout(qsb, 0, R.id.search_container_workspace, lp, true)) {
            Log.e(TAG, "Failed to add to item at (0, 0) to CellLayout");
        }
    }

    public void removeAllWorkspaceScreens() {
        disableLayoutTransitions();
        View qsb = findViewById(R.id.search_container_workspace);
        if (qsb != null) {
            ((ViewGroup) qsb.getParent()).removeView(qsb);
        }
        removeFolderListeners();
        removeAllViews();
        this.mScreenOrder.clear();
        this.mWorkspaceScreens.clear();
        bindAndInitFirstWorkspaceScreen(qsb);
        enableLayoutTransitions();
    }

    public void insertNewWorkspaceScreenBeforeEmptyScreen(long screenId) {
        int insertIndex = this.mScreenOrder.indexOf(-201L);
        if (insertIndex < 0) {
            insertIndex = this.mScreenOrder.size();
        }
        insertNewWorkspaceScreen(screenId, insertIndex);
    }

    public void insertNewWorkspaceScreen(long screenId) {
        insertNewWorkspaceScreen(screenId, getChildCount());
    }

    public CellLayout insertNewWorkspaceScreen(long screenId, int insertIndex) {
        if (!this.mWorkspaceScreens.containsKey(screenId)) {
            CellLayout newScreen = (CellLayout) LayoutInflater.from(getContext()).inflate(R.layout.workspace_screen, this, false);
            newScreen.getShortcutsAndWidgets().setId(R.id.workspace_page_container);
            int paddingLeftRight = this.mLauncher.getDeviceProfile().cellLayoutPaddingLeftRightPx;
            newScreen.setPadding(paddingLeftRight, 0, paddingLeftRight, this.mLauncher.getDeviceProfile().cellLayoutBottomPaddingPx);
            this.mWorkspaceScreens.put(screenId, newScreen);
            this.mScreenOrder.add(insertIndex, Long.valueOf(screenId));
            addView(newScreen, insertIndex);
            this.mStateTransitionAnimation.applyChildState(this.mLauncher.getStateManager().getState(), newScreen, insertIndex);
            if (this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
                newScreen.enableAccessibleDrag(true, 2);
            }
            return newScreen;
        }
        throw new RuntimeException("Screen id " + screenId + " already exists!");
    }

    public void addExtraEmptyScreenOnDrag() {
        boolean lastChildOnScreen = false;
        boolean childOnFinalScreen = false;
        this.mRemoveEmptyScreenRunnable = null;
        if (this.mDragSourceInternal != null) {
            if (this.mDragSourceInternal.getChildCount() == 1) {
                lastChildOnScreen = true;
            }
            if (indexOfChild((CellLayout) this.mDragSourceInternal.getParent()) == getChildCount() - 1) {
                childOnFinalScreen = true;
            }
        }
        if ((!lastChildOnScreen || !childOnFinalScreen) && !this.mWorkspaceScreens.containsKey(-201)) {
            insertNewWorkspaceScreen(-201);
        }
    }

    public boolean addExtraEmptyScreen() {
        if (this.mWorkspaceScreens.containsKey(-201)) {
            return false;
        }
        insertNewWorkspaceScreen(-201);
        return true;
    }

    private void convertFinalScreenToEmptyScreenIfNecessary() {
        if (!this.mLauncher.isWorkspaceLoading() && !hasExtraEmptyScreen() && this.mScreenOrder.size() != 0) {
            long finalScreenId = this.mScreenOrder.get(this.mScreenOrder.size() - 1).longValue();
            CellLayout finalScreen = (CellLayout) this.mWorkspaceScreens.get(finalScreenId);
            if (finalScreen.getShortcutsAndWidgets().getChildCount() == 0 && !finalScreen.isDropPending()) {
                this.mWorkspaceScreens.remove(finalScreenId);
                this.mScreenOrder.remove(Long.valueOf(finalScreenId));
                this.mWorkspaceScreens.put(-201, finalScreen);
                this.mScreenOrder.add(-201L);
                LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
            }
        }
    }

    public void removeExtraEmptyScreen(boolean animate, boolean stripEmptyScreens) {
        removeExtraEmptyScreenDelayed(animate, (Runnable) null, 0, stripEmptyScreens);
    }

    public void removeExtraEmptyScreenDelayed(final boolean animate, final Runnable onComplete, int delay, final boolean stripEmptyScreens) {
        if (!this.mLauncher.isWorkspaceLoading()) {
            if (delay > 0) {
                postDelayed(new Runnable() {
                    public void run() {
                        Workspace.this.removeExtraEmptyScreenDelayed(animate, onComplete, 0, stripEmptyScreens);
                    }
                }, (long) delay);
                return;
            }
            convertFinalScreenToEmptyScreenIfNecessary();
            if (hasExtraEmptyScreen()) {
                if (getNextPage() == this.mScreenOrder.indexOf(-201L)) {
                    snapToPage(getNextPage() - 1, SNAP_OFF_EMPTY_SCREEN_DURATION);
                    fadeAndRemoveEmptyScreen(SNAP_OFF_EMPTY_SCREEN_DURATION, 150, onComplete, stripEmptyScreens);
                    return;
                }
                snapToPage(getNextPage(), 0);
                fadeAndRemoveEmptyScreen(0, 150, onComplete, stripEmptyScreens);
                return;
            }
            if (stripEmptyScreens) {
                stripEmptyScreens();
            }
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    private void fadeAndRemoveEmptyScreen(int delay, int duration, final Runnable onComplete, final boolean stripEmptyScreens) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", new float[]{0.0f});
        PropertyValuesHolder bgAlpha = PropertyValuesHolder.ofFloat("backgroundAlpha", new float[]{0.0f});
        final CellLayout cl = (CellLayout) this.mWorkspaceScreens.get(-201);
        this.mRemoveEmptyScreenRunnable = new Runnable() {
            public void run() {
                if (Workspace.this.hasExtraEmptyScreen()) {
                    Workspace.this.mWorkspaceScreens.remove(-201);
                    Workspace.this.mScreenOrder.remove(-201L);
                    Workspace.this.removeView(cl);
                    if (stripEmptyScreens) {
                        Workspace.this.stripEmptyScreens();
                    }
                    Workspace.this.showPageIndicatorAtCurrentScroll();
                }
            }
        };
        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(cl, new PropertyValuesHolder[]{alpha, bgAlpha});
        oa.setDuration((long) duration);
        oa.setStartDelay((long) delay);
        oa.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (Workspace.this.mRemoveEmptyScreenRunnable != null) {
                    Workspace.this.mRemoveEmptyScreenRunnable.run();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        oa.start();
    }

    public boolean hasExtraEmptyScreen() {
        return this.mWorkspaceScreens.containsKey(-201) && getChildCount() > 1;
    }

    public long commitExtraEmptyScreen() {
        if (this.mLauncher.isWorkspaceLoading()) {
            return -1;
        }
        this.mWorkspaceScreens.remove(-201);
        this.mScreenOrder.remove(-201L);
        long newId = LauncherSettings.Settings.call(getContext().getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getLong(LauncherSettings.Settings.EXTRA_VALUE);
        this.mWorkspaceScreens.put(newId, (CellLayout) this.mWorkspaceScreens.get(-201));
        this.mScreenOrder.add(Long.valueOf(newId));
        LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
        return newId;
    }

    public CellLayout getScreenWithId(long screenId) {
        return (CellLayout) this.mWorkspaceScreens.get(screenId);
    }

    public long getIdForScreen(CellLayout layout) {
        int index = this.mWorkspaceScreens.indexOfValue(layout);
        if (index != -1) {
            return this.mWorkspaceScreens.keyAt(index);
        }
        return -1;
    }

    public int getPageIndexForScreenId(long screenId) {
        return indexOfChild((View) this.mWorkspaceScreens.get(screenId));
    }

    public long getScreenIdForPageIndex(int index) {
        if (index < 0 || index >= this.mScreenOrder.size()) {
            return -1;
        }
        return this.mScreenOrder.get(index).longValue();
    }

    public ArrayList<Long> getScreenOrder() {
        return this.mScreenOrder;
    }

    public void stripEmptyScreens() {
        if (!this.mLauncher.isWorkspaceLoading()) {
            if (isPageInTransition()) {
                this.mStripScreensOnPageStopMoving = true;
                return;
            }
            int currentPage = getNextPage();
            ArrayList<Long> removeScreens = new ArrayList<>();
            int total = this.mWorkspaceScreens.size();
            for (int i = 0; i < total; i++) {
                long id = this.mWorkspaceScreens.keyAt(i);
                CellLayout cl = (CellLayout) this.mWorkspaceScreens.valueAt(i);
                if (id > 0 && cl.getShortcutsAndWidgets().getChildCount() == 0) {
                    removeScreens.add(Long.valueOf(id));
                }
            }
            boolean isInAccessibleDrag = this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag();
            int pageShift = 0;
            Iterator<Long> it = removeScreens.iterator();
            while (it.hasNext()) {
                Long id2 = it.next();
                CellLayout cl2 = (CellLayout) this.mWorkspaceScreens.get(id2.longValue());
                this.mWorkspaceScreens.remove(id2.longValue());
                this.mScreenOrder.remove(id2);
                if (getChildCount() > 1) {
                    if (indexOfChild(cl2) < currentPage) {
                        pageShift++;
                    }
                    if (isInAccessibleDrag) {
                        cl2.enableAccessibleDrag(false, 2);
                    }
                    removeView(cl2);
                } else {
                    this.mRemoveEmptyScreenRunnable = null;
                    this.mWorkspaceScreens.put(-201, cl2);
                    this.mScreenOrder.add(-201L);
                }
            }
            if (!removeScreens.isEmpty()) {
                LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
            }
            if (pageShift >= 0) {
                setCurrentPage(currentPage - pageShift);
            }
        }
    }

    public void addInScreenFromBind(View child, ItemInfo info) {
        int x = info.cellX;
        int y = info.cellY;
        if (info.container == -101) {
            int screenId = (int) info.screenId;
            x = this.mLauncher.getHotseat().getCellXFromOrder(screenId);
            y = this.mLauncher.getHotseat().getCellYFromOrder(screenId);
        }
        addInScreen(child, info.container, info.screenId, x, y, info.spanX, info.spanY);
    }

    public void addInScreen(View child, ItemInfo info) {
        addInScreen(child, info.container, info.screenId, info.cellX, info.cellY, info.spanX, info.spanY);
    }

    private void addInScreen(View child, long container, long screenId, int x, int y, int spanX, int spanY) {
        CellLayout layout;
        CellLayout.LayoutParams lp;
        View view = child;
        long j = screenId;
        int i = x;
        int i2 = y;
        int i3 = spanX;
        int i4 = spanY;
        if (container == -100 && getScreenWithId(j) == null) {
            Log.e(TAG, "Skipping child, screenId " + j + " not found");
            new Throwable().printStackTrace();
        } else if (j != -201) {
            if (container == -101) {
                layout = this.mLauncher.getHotseat().getLayout();
                if (view instanceof FolderIcon) {
                    ((FolderIcon) view).setTextVisible(false);
                }
            } else {
                if (view instanceof FolderIcon) {
                    ((FolderIcon) view).setTextVisible(true);
                }
                layout = getScreenWithId(j);
            }
            ViewGroup.LayoutParams genericLp = child.getLayoutParams();
            if (genericLp == null || !(genericLp instanceof CellLayout.LayoutParams)) {
                lp = new CellLayout.LayoutParams(i, i2, i3, i4);
            } else {
                lp = (CellLayout.LayoutParams) genericLp;
                lp.cellX = i;
                lp.cellY = i2;
                lp.cellHSpan = i3;
                lp.cellVSpan = i4;
            }
            if (i3 < 0 && i4 < 0) {
                lp.isLockedToGrid = false;
            }
            ItemInfo info = (ItemInfo) child.getTag();
            ItemInfo itemInfo = info;
            CellLayout.LayoutParams lp2 = lp;
            ViewGroup.LayoutParams layoutParams = genericLp;
            if (!layout.addViewToCellLayout(child, -1, this.mLauncher.getViewIdForItem(info), lp2, !(view instanceof Folder))) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to add to item at (");
                CellLayout.LayoutParams lp3 = lp2;
                sb.append(lp3.cellX);
                sb.append(",");
                sb.append(lp3.cellY);
                sb.append(") to CellLayout");
                Log.e(TAG, sb.toString());
            }
            view.setHapticFeedbackEnabled(false);
            view.setOnLongClickListener(ItemLongClickListener.INSTANCE_WORKSPACE);
            if (view instanceof DropTarget) {
                this.mDragController.addDropTarget((DropTarget) view);
            }
        } else {
            throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent event) {
        return shouldConsumeTouch(v);
    }

    private boolean shouldConsumeTouch(View v) {
        return !workspaceIconsCanBeDragged() || (!workspaceInModalState() && indexOfChild(v) != this.mCurrentPage);
    }

    public boolean isSwitchingState() {
        return this.mIsSwitchingState;
    }

    public boolean isFinishedSwitchingState() {
        return !this.mIsSwitchingState || this.mTransitionProgress > 0.5f;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (workspaceInModalState() || !isFinishedSwitchingState()) {
            return false;
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == 0) {
            this.mXDown = ev.getX();
            this.mYDown = ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev) {
        if (isFinishedSwitchingState()) {
            float absDeltaX = Math.abs(ev.getX() - this.mXDown);
            float absDeltaY = Math.abs(ev.getY() - this.mYDown);
            if (Float.compare(absDeltaX, 0.0f) != 0) {
                float theta = (float) Math.atan((double) (absDeltaY / absDeltaX));
                if (absDeltaX > ((float) this.mTouchSlop) || absDeltaY > ((float) this.mTouchSlop)) {
                    cancelCurrentPageLongPress();
                }
                if (theta <= MAX_SWIPE_ANGLE) {
                    if (theta > START_DAMPING_TOUCH_SLOP_ANGLE) {
                        super.determineScrollingStart(ev, (TOUCH_SLOP_DAMPING_FACTOR * ((float) Math.sqrt((double) ((theta - START_DAMPING_TOUCH_SLOP_ANGLE) / START_DAMPING_TOUCH_SLOP_ANGLE)))) + 1.0f);
                    } else {
                        super.determineScrollingStart(ev);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        updateChildrenLayersEnabled();
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        super.onPageEndTransition();
        updateChildrenLayersEnabled();
        if (this.mDragController.isDragging() && workspaceInModalState()) {
            this.mDragController.forceTouchMove();
        }
        if (this.mStripScreensOnPageStopMoving) {
            stripEmptyScreens();
            this.mStripScreensOnPageStopMoving = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionBegin() {
        super.onScrollInteractionEnd();
        this.mScrollInteractionBegan = true;
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionEnd() {
        super.onScrollInteractionEnd();
        this.mScrollInteractionBegan = false;
        if (this.mStartedSendingScrollEvents) {
            this.mStartedSendingScrollEvents = false;
            this.mLauncherOverlay.onScrollInteractionEnd();
        }
    }

    public void setLauncherOverlay(Launcher.LauncherOverlay overlay) {
        this.mLauncherOverlay = overlay;
        this.mStartedSendingScrollEvents = false;
        onOverlayScrollChanged(0.0f);
    }

    private boolean isScrollingOverlay() {
        return this.mLauncherOverlay != null && ((this.mIsRtl && getUnboundedScrollX() > this.mMaxScrollX) || (!this.mIsRtl && getUnboundedScrollX() < 0));
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        if (isScrollingOverlay()) {
            this.mWasInOverscroll = false;
            snapToPageImmediately(0);
            return;
        }
        super.snapToDestination();
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!(this.mIsSwitchingState || (getLayoutTransition() != null && getLayoutTransition().isRunning()))) {
            showPageIndicatorAtCurrentScroll();
        }
        updatePageAlphaValues();
        enableHwLayersOnVisiblePages();
    }

    public void showPageIndicatorAtCurrentScroll() {
        if (this.mPageIndicator != null) {
            ((WorkspacePageIndicator) this.mPageIndicator).setScroll(getScrollX(), computeMaxScrollX());
        }
    }

    /* access modifiers changed from: protected */
    public void overScroll(float amount) {
        boolean shouldZeroOverlay = false;
        boolean shouldScrollOverlay = this.mLauncherOverlay != null && ((amount <= 0.0f && !this.mIsRtl) || (amount >= 0.0f && this.mIsRtl));
        if (!(this.mLauncherOverlay == null || this.mLastOverlayScroll == 0.0f || ((amount < 0.0f || this.mIsRtl) && (amount > 0.0f || !this.mIsRtl)))) {
            shouldZeroOverlay = true;
        }
        if (shouldScrollOverlay) {
            if (!this.mStartedSendingScrollEvents && this.mScrollInteractionBegan) {
                this.mStartedSendingScrollEvents = true;
                this.mLauncherOverlay.onScrollInteractionBegin();
            }
            this.mLastOverlayScroll = Math.abs(amount / ((float) getMeasuredWidth()));
            this.mLauncherOverlay.onScrollChange(this.mLastOverlayScroll, this.mIsRtl);
        } else {
            dampedOverScroll(amount);
        }
        if (shouldZeroOverlay) {
            this.mLauncherOverlay.onScrollChange(0.0f, this.mIsRtl);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldFlingForVelocity(int velocityX) {
        return Float.compare(Math.abs(this.mOverlayTranslation), 0.0f) == 0 && super.shouldFlingForVelocity(velocityX);
    }

    public void onOverlayScrollChanged(float scroll) {
        if (Float.compare(scroll, 1.0f) == 0) {
            if (!this.mOverlayShown) {
                this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, 3, 1, 0);
            }
            this.mOverlayShown = true;
        } else if (Float.compare(scroll, 0.0f) == 0) {
            if (this.mOverlayShown) {
                this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, 4, 1, -1);
            } else if (Float.compare(this.mOverlayTranslation, 0.0f) != 0) {
                announcePageForAccessibility();
            }
            this.mOverlayShown = false;
            tryRunOverlayCallback();
        }
        float scroll2 = Math.min(1.0f, Math.max(scroll - 0.0f, 0.0f) / (1.0f - 0.0f));
        float alpha = 1.0f - Interpolators.DEACCEL_3.getInterpolation(scroll2);
        float transX = ((float) this.mLauncher.getDragLayer().getMeasuredWidth()) * scroll2;
        if (this.mIsRtl) {
            transX = -transX;
        }
        this.mOverlayTranslation = transX;
        this.mLauncher.getDragLayer().setTranslationX(transX);
        this.mLauncher.getDragLayer().getAlphaProperty(0).setValue(alpha);
    }

    /* access modifiers changed from: private */
    public boolean tryRunOverlayCallback() {
        if (this.mOnOverlayHiddenCallback == null) {
            return true;
        }
        if (this.mOverlayShown || !hasWindowFocus()) {
            return false;
        }
        this.mOnOverlayHiddenCallback.run();
        this.mOnOverlayHiddenCallback = null;
        return true;
    }

    public boolean runOnOverlayHidden(Runnable callback) {
        if (this.mOnOverlayHiddenCallback == null) {
            this.mOnOverlayHiddenCallback = callback;
        } else {
            this.mOnOverlayHiddenCallback = new Runnable(this.mOnOverlayHiddenCallback, callback) {
                private final /* synthetic */ Runnable f$0;
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    Workspace.lambda$runOnOverlayHidden$0(this.f$0, this.f$1);
                }
            };
        }
        if (tryRunOverlayCallback()) {
            return false;
        }
        final ViewTreeObserver observer = getViewTreeObserver();
        if (observer == null || !observer.isAlive()) {
            return true;
        }
        observer.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            public void onWindowFocusChanged(boolean hasFocus) {
                if (Workspace.this.tryRunOverlayCallback() && observer.isAlive()) {
                    observer.removeOnWindowFocusChangeListener(this);
                }
            }
        });
        return true;
    }

    static /* synthetic */ void lambda$runOnOverlayHidden$0(Runnable oldCallback, Runnable callback) {
        oldCallback.run();
        callback.run();
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int prevPage) {
        super.notifyPageSwitchListener(prevPage);
        if (prevPage != this.mCurrentPage) {
            this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, prevPage < this.mCurrentPage ? 4 : 3, 1, prevPage);
        }
    }

    /* access modifiers changed from: protected */
    public void setWallpaperDimension() {
        Utilities.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            public void run() {
                Point size = LauncherAppState.getIDP(Workspace.this.getContext()).defaultWallpaperSize;
                if (size.x != Workspace.this.mWallpaperManager.getDesiredMinimumWidth() || size.y != Workspace.this.mWallpaperManager.getDesiredMinimumHeight()) {
                    Workspace.this.mWallpaperManager.suggestDesiredDimensions(size.x, size.y);
                }
            }
        });
    }

    public void lockWallpaperToDefaultPage() {
        this.mWallpaperOffset.setLockToDefaultPage(true);
    }

    public void unlockWallpaperFromDefaultPageOnNextLayout() {
        if (this.mWallpaperOffset.isLockedToDefaultPage()) {
            this.mUnlockWallpaperFromDefaultPageOnLayout = true;
            requestLayout();
        }
    }

    public void computeScroll() {
        super.computeScroll();
        this.mWallpaperOffset.syncWithScroll();
    }

    public void computeScrollWithoutInvalidation() {
        computeScrollHelper(false);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        if (!isSwitchingState()) {
            super.determineScrollingStart(ev, touchSlopScale);
        }
    }

    public void announceForAccessibility(CharSequence text) {
        if (!this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            super.announceForAccessibility(text);
        }
    }

    public void showOutlinesTemporarily() {
        if (!this.mIsPageInTransition && !isTouchActive()) {
            snapToPage(this.mCurrentPage);
        }
    }

    private void updatePageAlphaValues() {
        if (!workspaceInModalState() && !this.mIsSwitchingState && !this.mDragController.isDragging()) {
            int screenCenter = getScrollX() + (getMeasuredWidth() / 2);
            for (int i = 0; i < getChildCount(); i++) {
                CellLayout child = (CellLayout) getChildAt(i);
                if (child != null) {
                    float alpha = 1.0f - Math.abs(getScrollProgress(screenCenter, child, i));
                    if (this.mWorkspaceFadeInAdjacentScreens) {
                        child.getShortcutsAndWidgets().setAlpha(alpha);
                    } else {
                        child.getShortcutsAndWidgets().setImportantForAccessibility(alpha > 0.0f ? 0 : 4);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IBinder windowToken = getWindowToken();
        this.mWallpaperOffset.setWindowToken(windowToken);
        computeScroll();
        this.mDragController.setWindowToken(windowToken);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWallpaperOffset.setWindowToken((IBinder) null);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.mUnlockWallpaperFromDefaultPageOnLayout) {
            this.mWallpaperOffset.setLockToDefaultPage(false);
            this.mUnlockWallpaperFromDefaultPageOnLayout = false;
        }
        if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < getChildCount()) {
            this.mWallpaperOffset.syncWithScroll();
            this.mWallpaperOffset.jumpToFinal();
        }
        super.onLayout(changed, left, top, right, bottom);
        updatePageAlphaValues();
    }

    public int getDescendantFocusability() {
        if (workspaceInModalState()) {
            return 393216;
        }
        return super.getDescendantFocusability();
    }

    private boolean workspaceInModalState() {
        return !this.mLauncher.isInState(LauncherState.NORMAL);
    }

    public boolean workspaceIconsCanBeDragged() {
        return this.mLauncher.getStateManager().getState().workspaceIconsCanBeDragged;
    }

    private void updateChildrenLayersEnabled() {
        boolean enableChildrenLayers = this.mIsSwitchingState || isPageInTransition();
        if (enableChildrenLayers != this.mChildrenLayersEnabled) {
            this.mChildrenLayersEnabled = enableChildrenLayers;
            if (this.mChildrenLayersEnabled) {
                enableHwLayersOnVisiblePages();
                return;
            }
            for (int i = 0; i < getPageCount(); i++) {
                ((CellLayout) getChildAt(i)).enableHardwareLayer(false);
            }
        }
    }

    private void enableHwLayersOnVisiblePages() {
        if (this.mChildrenLayersEnabled) {
            int screenCount = getChildCount();
            int[] visibleScreens = getVisibleChildrenRange();
            int leftScreen = visibleScreens[0];
            int rightScreen = visibleScreens[1];
            if (this.mForceDrawAdjacentPages) {
                leftScreen = Utilities.boundToRange(getCurrentPage() - 1, 0, rightScreen);
                rightScreen = Utilities.boundToRange(getCurrentPage() + 1, leftScreen, getPageCount() - 1);
            }
            if (leftScreen == rightScreen) {
                if (rightScreen < screenCount - 1) {
                    rightScreen++;
                } else if (leftScreen > 0) {
                    leftScreen--;
                }
            }
            int i = 0;
            while (i < screenCount) {
                ((CellLayout) getPageAt(i)).enableHardwareLayer(leftScreen <= i && i <= rightScreen);
                i++;
            }
        }
    }

    public void onWallpaperTap(MotionEvent ev) {
        int[] position = this.mTempXY;
        getLocationOnScreen(position);
        int pointerIndex = ev.getActionIndex();
        position[0] = position[0] + ((int) ev.getX(pointerIndex));
        position[1] = position[1] + ((int) ev.getY(pointerIndex));
        this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), ev.getAction() == 1 ? "android.wallpaper.tap" : "android.wallpaper.secondaryTap", position[0], position[1], 0, (Bundle) null);
    }

    public void prepareDragWithProvider(DragPreviewProvider outlineProvider) {
        this.mOutlineProvider = outlineProvider;
    }

    public void snapToPageFromOverView(int whichPage) {
        snapToPage(whichPage, 250, (TimeInterpolator) Interpolators.ZOOM_IN);
    }

    /* access modifiers changed from: private */
    public void onStartStateTransition(LauncherState state) {
        this.mIsSwitchingState = true;
        this.mTransitionProgress = 0.0f;
        updateChildrenLayersEnabled();
    }

    /* access modifiers changed from: private */
    public void onEndStateTransition() {
        this.mIsSwitchingState = false;
        this.mForceDrawAdjacentPages = false;
        this.mTransitionProgress = 1.0f;
        updateChildrenLayersEnabled();
        updateAccessibilityFlags();
    }

    public void setState(LauncherState toState) {
        onStartStateTransition(toState);
        this.mStateTransitionAnimation.setState(toState);
        onEndStateTransition();
    }

    public void setStateWithAnimation(LauncherState toState, AnimatorSetBuilder builder, LauncherStateManager.AnimationConfig config) {
        StateTransitionListener listener = new StateTransitionListener(toState);
        this.mStateTransitionAnimation.setStateWithAnimation(toState, builder, config);
        if (toState.hasMultipleVisiblePages) {
            this.mForceDrawAdjacentPages = true;
        }
        invalidate();
        ValueAnimator stepAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        stepAnimator.addUpdateListener(listener);
        stepAnimator.setDuration(config.duration);
        stepAnimator.addListener(listener);
        builder.play(stepAnimator);
    }

    public void updateAccessibilityFlags() {
        int accessibilityFlag = this.mLauncher.getStateManager().getState().workspaceAccessibilityFlag;
        if (!this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
            int total = getPageCount();
            for (int i = 0; i < total; i++) {
                updateAccessibilityFlags(accessibilityFlag, (CellLayout) getPageAt(i));
            }
            setImportantForAccessibility(accessibilityFlag);
        }
    }

    private void updateAccessibilityFlags(int accessibilityFlag, CellLayout page) {
        page.setImportantForAccessibility(2);
        page.getShortcutsAndWidgets().setImportantForAccessibility(accessibilityFlag);
        page.setContentDescription((CharSequence) null);
        page.setAccessibilityDelegate((View.AccessibilityDelegate) null);
    }

    public void startDrag(CellLayout.CellInfo cellInfo, DragOptions options) {
        View child = cellInfo.cell;
        this.mDragInfo = cellInfo;
        child.setVisibility(4);
        if (options.isAccessibleDrag) {
            this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this, 2) {
                /* access modifiers changed from: protected */
                public void enableAccessibleDrag(boolean enable) {
                    super.enableAccessibleDrag(enable);
                    setEnableForLayout(Workspace.this.mLauncher.getHotseat().getLayout(), enable);
                }
            });
        }
        beginDragShared(child, this, options);
    }

    public void beginDragShared(View child, DragSource source, DragOptions options) {
        Object dragObject = child.getTag();
        if (dragObject instanceof ItemInfo) {
            beginDragShared(child, source, (ItemInfo) dragObject, new DragPreviewProvider(child), options);
            return;
        }
        throw new IllegalStateException("Drag started with a view that has no tag set. This will cause a crash (issue 11627249) down the line. View: " + child + "  tag: " + child.getTag());
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00b2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.launcher3.dragndrop.DragView beginDragShared(android.view.View r25, com.android.launcher3.DragSource r26, com.android.launcher3.ItemInfo r27, com.android.launcher3.graphics.DragPreviewProvider r28, com.android.launcher3.dragndrop.DragOptions r29) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r28
            r14 = r29
            r3 = 1065353216(0x3f800000, float:1.0)
            boolean r4 = r1 instanceof com.android.launcher3.BubbleTextView
            if (r4 == 0) goto L_0x0020
            r4 = r1
            com.android.launcher3.BubbleTextView r4 = (com.android.launcher3.BubbleTextView) r4
            android.graphics.drawable.Drawable r4 = r4.getIcon()
            boolean r5 = r4 instanceof com.android.launcher3.FastBitmapDrawable
            if (r5 == 0) goto L_0x0020
            r5 = r4
            com.android.launcher3.FastBitmapDrawable r5 = (com.android.launcher3.FastBitmapDrawable) r5
            float r3 = r5.getAnimatedScale()
        L_0x0020:
            r15 = r3
            r25.clearFocus()
            r3 = 0
            r1.setPressed(r3)
            r0.mOutlineProvider = r2
            android.graphics.Bitmap r13 = r28.createDragBitmap()
            int r4 = r2.previewPadding
            int r12 = r4 / 2
            int[] r4 = r0.mTempXY
            float r16 = r2.getScaleAndPosition(r13, r4)
            int[] r4 = r0.mTempXY
            r17 = r4[r3]
            int[] r4 = r0.mTempXY
            r5 = 1
            r4 = r4[r5]
            com.android.launcher3.Launcher r5 = r0.mLauncher
            com.android.launcher3.DeviceProfile r11 = r5.getDeviceProfile()
            r5 = 0
            r6 = 0
            boolean r7 = r1 instanceof com.android.launcher3.BubbleTextView
            if (r7 == 0) goto L_0x0069
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r6 = r1
            com.android.launcher3.BubbleTextView r6 = (com.android.launcher3.BubbleTextView) r6
            r6.getIconBounds(r3)
            int r6 = r3.top
            int r4 = r4 + r6
            android.graphics.Point r6 = new android.graphics.Point
            int r7 = -r12
            r6.<init>(r7, r12)
            r5 = r6
        L_0x0062:
            r19 = r3
            r20 = r4
            r18 = r5
            goto L_0x00a0
        L_0x0069:
            boolean r7 = r1 instanceof com.android.launcher3.folder.FolderIcon
            if (r7 == 0) goto L_0x008b
            int r7 = r11.folderIconSizePx
            android.graphics.Point r8 = new android.graphics.Point
            int r9 = -r12
            int r10 = r25.getPaddingTop()
            int r10 = r12 - r10
            r8.<init>(r9, r10)
            r5 = r8
            android.graphics.Rect r8 = new android.graphics.Rect
            int r9 = r25.getPaddingTop()
            int r10 = r25.getWidth()
            r8.<init>(r3, r9, r10, r7)
            r3 = r8
            goto L_0x0062
        L_0x008b:
            boolean r3 = r2 instanceof com.android.launcher3.shortcuts.ShortcutDragPreviewProvider
            if (r3 == 0) goto L_0x009a
            android.graphics.Point r3 = new android.graphics.Point
            int r7 = -r12
            r3.<init>(r7, r12)
            r18 = r3
            r20 = r4
            goto L_0x009e
        L_0x009a:
            r20 = r4
            r18 = r5
        L_0x009e:
            r19 = r6
        L_0x00a0:
            boolean r3 = r1 instanceof com.android.launcher3.BubbleTextView
            if (r3 == 0) goto L_0x00aa
            r3 = r1
            com.android.launcher3.BubbleTextView r3 = (com.android.launcher3.BubbleTextView) r3
            r3.clearPressedBackground()
        L_0x00aa:
            android.view.ViewParent r3 = r25.getParent()
            boolean r3 = r3 instanceof com.android.launcher3.ShortcutAndWidgetContainer
            if (r3 == 0) goto L_0x00ba
            android.view.ViewParent r3 = r25.getParent()
            com.android.launcher3.ShortcutAndWidgetContainer r3 = (com.android.launcher3.ShortcutAndWidgetContainer) r3
            r0.mDragSourceInternal = r3
        L_0x00ba:
            boolean r3 = r1 instanceof com.android.launcher3.BubbleTextView
            if (r3 == 0) goto L_0x00dc
            boolean r3 = r14.isAccessibleDrag
            if (r3 != 0) goto L_0x00dc
            r3 = r1
            com.android.launcher3.BubbleTextView r3 = (com.android.launcher3.BubbleTextView) r3
            com.android.launcher3.popup.PopupContainerWithArrow r3 = com.android.launcher3.popup.PopupContainerWithArrow.showForIcon(r3)
            if (r3 == 0) goto L_0x00dc
            com.android.launcher3.dragndrop.DragOptions$PreDragCondition r4 = r3.createPreDragCondition()
            r14.preDragCondition = r4
            com.android.launcher3.Launcher r4 = r0.mLauncher
            com.android.launcher3.logging.UserEventDispatcher r4 = r4.getUserEventDispatcher()
            java.lang.String r5 = "dragging started"
            r4.resetElapsedContainerMillis(r5)
        L_0x00dc:
            com.android.launcher3.dragndrop.DragController r3 = r0.mDragController
            float r21 = r16 * r15
            r4 = r13
            r5 = r17
            r6 = r20
            r7 = r26
            r8 = r27
            r9 = r18
            r10 = r19
            r22 = r11
            r11 = r21
            r21 = r12
            r12 = r16
            r23 = r13
            r13 = r29
            com.android.launcher3.dragndrop.DragView r3 = r3.startDrag(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            float r4 = r14.intrinsicIconScaleFactor
            r3.setIntrinsicIconScaleFactor(r4)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.beginDragShared(android.view.View, com.android.launcher3.DragSource, com.android.launcher3.ItemInfo, com.android.launcher3.graphics.DragPreviewProvider, com.android.launcher3.dragndrop.DragOptions):com.android.launcher3.dragndrop.DragView");
    }

    private boolean transitionStateShouldAllowDrop() {
        return (!isSwitchingState() || this.mTransitionProgress > ALLOW_DROP_TRANSITION_PROGRESS) && workspaceIconsCanBeDragged();
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        CellLayout dropTargetLayout;
        int spanX;
        int spanY;
        DropTarget.DragObject dragObject = d;
        CellLayout dropTargetLayout2 = this.mDropToLayout;
        if (dragObject.dragSource == this) {
            dropTargetLayout = dropTargetLayout2;
        } else if (dropTargetLayout2 == null || !transitionStateShouldAllowDrop()) {
            return false;
        } else {
            this.mDragViewVisualCenter = dragObject.getVisualCenter(this.mDragViewVisualCenter);
            if (this.mLauncher.isHotseatLayout(dropTargetLayout2)) {
                mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
            } else {
                mapPointFromSelfToChild(dropTargetLayout2, this.mDragViewVisualCenter);
            }
            if (this.mDragInfo != null) {
                CellLayout.CellInfo dragCellInfo = this.mDragInfo;
                int spanX2 = dragCellInfo.spanX;
                spanY = dragCellInfo.spanY;
                spanX = spanX2;
            } else {
                spanX = dragObject.dragInfo.spanX;
                spanY = dragObject.dragInfo.spanY;
            }
            int minSpanX = spanX;
            int minSpanY = spanY;
            if (dragObject.dragInfo instanceof PendingAddWidgetInfo) {
                minSpanX = ((PendingAddWidgetInfo) dragObject.dragInfo).minSpanX;
                minSpanY = ((PendingAddWidgetInfo) dragObject.dragInfo).minSpanY;
            }
            int minSpanX2 = minSpanX;
            int minSpanY2 = minSpanY;
            this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX2, minSpanY2, dropTargetLayout2, this.mTargetCell);
            float distance = dropTargetLayout2.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell);
            if (this.mCreateUserFolderOnDrop) {
                if (willCreateUserFolder(dragObject.dragInfo, dropTargetLayout2, this.mTargetCell, distance, true)) {
                    return true;
                }
            }
            if (this.mAddToExistingFolderOnDrop && willAddToExistingUserFolder(dragObject.dragInfo, dropTargetLayout2, this.mTargetCell, distance)) {
                return true;
            }
            dropTargetLayout = dropTargetLayout2;
            this.mTargetCell = dropTargetLayout2.performReorder((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX2, minSpanY2, spanX, spanY, (View) null, this.mTargetCell, new int[2], 4);
            if (!(this.mTargetCell[0] >= 0 && this.mTargetCell[1] >= 0)) {
                onNoCellFound(dropTargetLayout);
                return false;
            }
        }
        if (getIdForScreen(dropTargetLayout) == -201) {
            commitExtraEmptyScreen();
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean willCreateUserFolder(ItemInfo info, CellLayout target, int[] targetCell, float distance, boolean considerTimeout) {
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        return willCreateUserFolder(info, target.getChildAt(targetCell[0], targetCell[1]), considerTimeout);
    }

    /* access modifiers changed from: package-private */
    public boolean willCreateUserFolder(ItemInfo info, View dropOverView, boolean considerTimeout) {
        if (dropOverView != null) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) dropOverView.getLayoutParams();
            if (lp.useTmpCoords && !(lp.tmpCellX == lp.cellX && lp.tmpCellY == lp.cellY)) {
                return false;
            }
        }
        boolean hasntMoved = false;
        if (this.mDragInfo != null) {
            hasntMoved = dropOverView == this.mDragInfo.cell;
        }
        if (dropOverView == null || hasntMoved || (considerTimeout && !this.mCreateUserFolderOnDrop)) {
            return false;
        }
        boolean aboveShortcut = dropOverView.getTag() instanceof ShortcutInfo;
        boolean willBecomeShortcut = info.itemType == 0 || info.itemType == 1 || info.itemType == 6;
        if (!aboveShortcut || !willBecomeShortcut) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean willAddToExistingUserFolder(ItemInfo dragInfo, CellLayout target, int[] targetCell, float distance) {
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        return willAddToExistingUserFolder(dragInfo, target.getChildAt(targetCell[0], targetCell[1]));
    }

    /* access modifiers changed from: package-private */
    public boolean willAddToExistingUserFolder(ItemInfo dragInfo, View dropOverView) {
        if (dropOverView != null) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) dropOverView.getLayoutParams();
            if (lp.useTmpCoords && !(lp.tmpCellX == lp.cellX && lp.tmpCellY == lp.cellY)) {
                return false;
            }
        }
        if (!(dropOverView instanceof FolderIcon) || !((FolderIcon) dropOverView).acceptDrop(dragInfo)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean createUserFolderIfNecessary(View newView, long container, CellLayout target, int[] targetCell, float distance, boolean external, DragView dragView) {
        CellLayout cellLayout = target;
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View v = cellLayout.getChildAt(targetCell[0], targetCell[1]);
        boolean hasntMoved = false;
        if (this.mDragInfo != null) {
            hasntMoved = this.mDragInfo.cellX == targetCell[0] && this.mDragInfo.cellY == targetCell[1] && getParentCellLayoutForView(this.mDragInfo.cell) == cellLayout;
        }
        boolean hasntMoved2 = hasntMoved;
        if (v == null || hasntMoved2) {
            return false;
        } else if (!this.mCreateUserFolderOnDrop) {
            View view = v;
            return false;
        } else {
            this.mCreateUserFolderOnDrop = false;
            long screenId = getIdForScreen(cellLayout);
            boolean aboveShortcut = v.getTag() instanceof ShortcutInfo;
            boolean willBecomeShortcut = newView.getTag() instanceof ShortcutInfo;
            if (!aboveShortcut || !willBecomeShortcut) {
                boolean z = aboveShortcut;
                View view2 = v;
                return false;
            }
            ShortcutInfo sourceInfo = (ShortcutInfo) newView.getTag();
            ShortcutInfo destInfo = (ShortcutInfo) v.getTag();
            if (!external) {
                getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
            }
            Rect folderLocation = new Rect();
            float scale = this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(v, folderLocation);
            cellLayout.removeView(v);
            ShortcutInfo destInfo2 = destInfo;
            Rect folderLocation2 = folderLocation;
            ShortcutInfo sourceInfo2 = sourceInfo;
            boolean z2 = willBecomeShortcut;
            FolderIcon fi = this.mLauncher.addFolder(target, container, screenId, targetCell[0], targetCell[1]);
            destInfo2.cellX = -1;
            destInfo2.cellY = -1;
            sourceInfo2.cellX = -1;
            sourceInfo2.cellY = -1;
            if (dragView != null) {
                fi.setFolderBackground(this.mFolderCreateBg);
                this.mFolderCreateBg = new PreviewBackground();
                boolean z3 = aboveShortcut;
                fi.performCreateAnimation(destInfo2, v, sourceInfo2, dragView, folderLocation2, scale);
                return true;
            }
            fi.prepareCreateAnimation(v);
            fi.addItem(destInfo2);
            fi.addItem(sourceInfo2);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean addToExistingFolderIfNecessary(View newView, CellLayout target, int[] targetCell, float distance, DropTarget.DragObject d, boolean external) {
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View dropOverView = target.getChildAt(targetCell[0], targetCell[1]);
        if (!this.mAddToExistingFolderOnDrop) {
            return false;
        }
        this.mAddToExistingFolderOnDrop = false;
        if (dropOverView instanceof FolderIcon) {
            FolderIcon fi = (FolderIcon) dropOverView;
            if (fi.acceptDrop(d.dragInfo)) {
                fi.onDrop(d, false);
                if (!external) {
                    getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
                }
                return true;
            }
        }
        return false;
    }

    public void prepareAccessibilityDrop() {
    }

    /* JADX WARNING: type inference failed for: r11v5 */
    /* JADX WARNING: type inference failed for: r11v6 */
    /* JADX WARNING: type inference failed for: r11v7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01cd  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01d1  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDrop(com.android.launcher3.DropTarget.DragObject r56, com.android.launcher3.dragndrop.DragOptions r57) {
        /*
            r55 = this;
            r10 = r55
            r11 = r56
            float[] r0 = r10.mDragViewVisualCenter
            float[] r0 = r11.getVisualCenter(r0)
            r10.mDragViewVisualCenter = r0
            com.android.launcher3.CellLayout r9 = r10.mDropToLayout
            if (r9 == 0) goto L_0x0029
            com.android.launcher3.Launcher r0 = r10.mLauncher
            boolean r0 = r0.isHotseatLayout(r9)
            if (r0 == 0) goto L_0x0024
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.Hotseat r0 = r0.getHotseat()
            float[] r1 = r10.mDragViewVisualCenter
            r10.mapPointFromSelfToHotseatLayout(r0, r1)
            goto L_0x0029
        L_0x0024:
            float[] r0 = r10.mDragViewVisualCenter
            r10.mapPointFromSelfToChild(r9, r0)
        L_0x0029:
            r12 = 0
            r23 = -1
            r24 = 0
            com.android.launcher3.DragSource r0 = r11.dragSource
            r14 = 1
            r13 = 0
            if (r0 != r10) goto L_0x038e
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            if (r0 != 0) goto L_0x003a
            goto L_0x038e
        L_0x003a:
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            android.view.View r8 = r0.cell
            r16 = 0
            r25 = 0
            r26 = -1
            if (r9 == 0) goto L_0x02ef
            boolean r0 = r11.cancelled
            if (r0 != 0) goto L_0x02ef
            com.android.launcher3.CellLayout r0 = r10.getParentCellLayoutForView(r8)
            if (r0 == r9) goto L_0x0052
            r0 = 1
            goto L_0x0053
        L_0x0052:
            r0 = 0
        L_0x0053:
            r27 = r0
            com.android.launcher3.Launcher r0 = r10.mLauncher
            boolean r28 = r0.isHotseatLayout(r9)
            r29 = -101(0xffffffffffffff9b, double:NaN)
            if (r28 == 0) goto L_0x0062
            r0 = r29
            goto L_0x0064
        L_0x0062:
            r0 = -100
        L_0x0064:
            r41 = r0
            int[] r0 = r10.mTargetCell
            r0 = r0[r13]
            if (r0 >= 0) goto L_0x0071
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            long r0 = r0.screenId
            goto L_0x0075
        L_0x0071:
            long r0 = r10.getIdForScreen(r9)
        L_0x0075:
            r4 = r0
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            if (r0 == 0) goto L_0x007f
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            int r0 = r0.spanX
            goto L_0x0080
        L_0x007f:
            r0 = 1
        L_0x0080:
            r3 = r0
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            if (r0 == 0) goto L_0x008a
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            int r0 = r0.spanY
            goto L_0x008b
        L_0x008a:
            r0 = 1
        L_0x008b:
            r2 = r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r13]
            int r1 = (int) r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r14]
            int r0 = (int) r0
            int[] r6 = r10.mTargetCell
            r7 = r0
            r0 = r55
            r45 = r2
            r2 = r7
            r7 = r3
            r46 = r4
            r4 = r45
            r5 = r9
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r10.mTargetCell = r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r13]
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r14]
            int[] r2 = r10.mTargetCell
            float r43 = r9.getDistanceFromCell(r0, r1, r2)
            int[] r5 = r10.mTargetCell
            r17 = 0
            com.android.launcher3.dragndrop.DragView r6 = r11.dragView
            r0 = r55
            r1 = r8
            r2 = r41
            r4 = r9
            r18 = r6
            r6 = r43
            r15 = r7
            r7 = r17
            r49 = r8
            r8 = r18
            boolean r0 = r0.createUserFolderIfNecessary(r1, r2, r4, r5, r6, r7, r8)
            if (r0 != 0) goto L_0x02d8
            int[] r3 = r10.mTargetCell
            r6 = 0
            r0 = r55
            r1 = r49
            r2 = r9
            r4 = r43
            r5 = r56
            boolean r0 = r0.addToExistingFolderIfNecessary(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x00f2
            r8 = r11
            r48 = r15
            r20 = r45
            r51 = r46
            r13 = r49
            goto L_0x02e1
        L_0x00f2:
            com.android.launcher3.ItemInfo r8 = r11.dragInfo
            int r0 = r8.spanX
            int r1 = r8.spanY
            int r2 = r8.minSpanX
            if (r2 <= 0) goto L_0x0104
            int r2 = r8.minSpanY
            if (r2 <= 0) goto L_0x0104
            int r0 = r8.minSpanX
            int r1 = r8.minSpanY
        L_0x0104:
            r44 = r0
            r50 = r1
            long r0 = r8.screenId
            r6 = r46
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x0128
            long r0 = r8.container
            int r0 = (r0 > r41 ? 1 : (r0 == r41 ? 0 : -1))
            if (r0 != 0) goto L_0x0128
            int r0 = r8.cellX
            int[] r1 = r10.mTargetCell
            r1 = r1[r13]
            if (r0 != r1) goto L_0x0128
            int r0 = r8.cellY
            int[] r1 = r10.mTargetCell
            r1 = r1[r14]
            if (r0 != r1) goto L_0x0128
            r0 = 1
            goto L_0x0129
        L_0x0128:
            r0 = 0
        L_0x0129:
            r46 = r0
            if (r46 == 0) goto L_0x0133
            boolean r0 = r10.mIsSwitchingState
            if (r0 == 0) goto L_0x0133
            r0 = 1
            goto L_0x0134
        L_0x0133:
            r0 = 0
        L_0x0134:
            r47 = r0
            boolean r0 = r55.isFinishedSwitchingState()
            if (r0 != 0) goto L_0x0150
            if (r47 != 0) goto L_0x0150
            int[] r0 = r10.mTargetCell
            r0 = r0[r13]
            int[] r1 = r10.mTargetCell
            r1 = r1[r14]
            r4 = r45
            boolean r0 = r9.isRegionVacant(r0, r1, r15, r4)
            if (r0 != 0) goto L_0x0152
            r0 = 1
            goto L_0x0153
        L_0x0150:
            r4 = r45
        L_0x0152:
            r0 = 0
        L_0x0153:
            r45 = r0
            r0 = 2
            int[] r5 = new int[r0]
            if (r45 == 0) goto L_0x0168
            int[] r1 = r10.mTargetCell
            int[] r2 = r10.mTargetCell
            r2[r14] = r26
            r1[r13] = r26
            r48 = r15
            r1 = 1
            r2 = 2
            r11 = 0
            goto L_0x0192
        L_0x0168:
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r13]
            int r1 = (int) r1
            float[] r2 = r10.mDragViewVisualCenter
            r2 = r2[r14]
            int r2 = (int) r2
            int[] r3 = r10.mTargetCell
            r22 = 2
            r12 = r9
            r11 = 0
            r13 = r1
            r1 = 1
            r14 = r2
            r48 = r15
            r2 = 2
            r15 = r44
            r16 = r50
            r17 = r48
            r18 = r4
            r19 = r49
            r20 = r3
            r21 = r5
            int[] r0 = r12.performReorder(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r10.mTargetCell = r0
        L_0x0192:
            int[] r0 = r10.mTargetCell
            r0 = r0[r11]
            if (r0 < 0) goto L_0x01a0
            int[] r0 = r10.mTargetCell
            r0 = r0[r1]
            if (r0 < 0) goto L_0x01a0
            r0 = 1
            goto L_0x01a1
        L_0x01a0:
            r0 = 0
        L_0x01a1:
            r12 = r0
            if (r12 == 0) goto L_0x01cd
            r13 = r49
            boolean r0 = r13 instanceof android.appwidget.AppWidgetHostView
            if (r0 == 0) goto L_0x01cf
            r0 = r5[r11]
            int r3 = r8.spanX
            if (r0 != r3) goto L_0x01b6
            r0 = r5[r1]
            int r3 = r8.spanY
            if (r0 == r3) goto L_0x01cf
        L_0x01b6:
            r24 = 1
            r0 = r5[r11]
            r8.spanX = r0
            r0 = r5[r1]
            r8.spanY = r0
            r0 = r13
            android.appwidget.AppWidgetHostView r0 = (android.appwidget.AppWidgetHostView) r0
            com.android.launcher3.Launcher r3 = r10.mLauncher
            r14 = r5[r11]
            r15 = r5[r1]
            com.android.launcher3.AppWidgetResizeFrame.updateWidgetSizeRanges(r0, r3, r14, r15)
            goto L_0x01cf
        L_0x01cd:
            r13 = r49
        L_0x01cf:
            if (r12 == 0) goto L_0x029e
            int r0 = r10.mCurrentPage
            long r14 = r10.getScreenIdForPageIndex(r0)
            int r0 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x01e6
            if (r28 != 0) goto L_0x01e6
            int r0 = r10.getPageIndexForScreenId(r6)
            r10.snapToPage(r0)
            r23 = r0
        L_0x01e6:
            java.lang.Object r0 = r13.getTag()
            r14 = r0
            com.android.launcher3.ItemInfo r14 = (com.android.launcher3.ItemInfo) r14
            if (r27 == 0) goto L_0x0224
            com.android.launcher3.CellLayout r15 = r10.getParentCellLayoutForView(r13)
            if (r15 == 0) goto L_0x01f8
            r15.removeView(r13)
        L_0x01f8:
            int[] r0 = r10.mTargetCell
            r16 = r0[r11]
            int[] r0 = r10.mTargetCell
            r17 = r0[r1]
            int r3 = r14.spanX
            int r0 = r14.spanY
            r18 = r0
            r0 = r55
            r1 = r13
            r19 = r3
            r2 = r41
            r20 = r4
            r21 = r5
            r4 = r6
            r51 = r6
            r6 = r16
            r7 = r17
            r53 = r8
            r8 = r19
            r54 = r9
            r9 = r18
            r0.addInScreen(r1, r2, r4, r6, r7, r8, r9)
            goto L_0x022e
        L_0x0224:
            r20 = r4
            r21 = r5
            r51 = r6
            r53 = r8
            r54 = r9
        L_0x022e:
            android.view.ViewGroup$LayoutParams r0 = r13.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            int[] r1 = r10.mTargetCell
            r1 = r1[r11]
            r0.tmpCellX = r1
            r0.cellX = r1
            int[] r1 = r10.mTargetCell
            r2 = 1
            r1 = r1[r2]
            r0.tmpCellY = r1
            r0.cellY = r1
            r1 = r53
            int r3 = r1.spanX
            r0.cellHSpan = r3
            int r3 = r1.spanY
            r0.cellVSpan = r3
            r0.isLockedToGrid = r2
            int r3 = (r41 > r29 ? 1 : (r41 == r29 ? 0 : -1))
            if (r3 == 0) goto L_0x0277
            boolean r3 = r13 instanceof com.android.launcher3.widget.LauncherAppWidgetHostView
            if (r3 == 0) goto L_0x0277
            r3 = r54
            r4 = r13
            com.android.launcher3.widget.LauncherAppWidgetHostView r4 = (com.android.launcher3.widget.LauncherAppWidgetHostView) r4
            android.appwidget.AppWidgetProviderInfo r5 = r4.getAppWidgetInfo()
            if (r5 == 0) goto L_0x0277
            int r6 = r5.resizeMode
            if (r6 == 0) goto L_0x0277
            r8 = r56
            boolean r6 = r8.accessibleDrag
            if (r6 != 0) goto L_0x0279
            com.android.launcher3.Workspace$7 r6 = new com.android.launcher3.Workspace$7
            r6.<init>(r4, r3)
            r3 = r6
            r25 = r3
            goto L_0x0279
        L_0x0277:
            r8 = r56
        L_0x0279:
            com.android.launcher3.Launcher r3 = r10.mLauncher
            com.android.launcher3.model.ModelWriter r31 = r3.getModelWriter()
            int r3 = r0.cellX
            int r4 = r0.cellY
            int r5 = r1.spanX
            int r6 = r1.spanY
            r32 = r14
            r33 = r41
            r35 = r51
            r37 = r3
            r38 = r4
            r39 = r5
            r40 = r6
            r31.modifyItemInDatabase(r32, r33, r35, r37, r38, r39, r40)
            r12 = r25
            r9 = r54
            goto L_0x02d5
        L_0x029e:
            r20 = r4
            r21 = r5
            r51 = r6
            r1 = r8
            r54 = r9
            r2 = 1
            r8 = r56
            if (r45 != 0) goto L_0x02b2
            r9 = r54
            r10.onNoCellFound(r9)
            goto L_0x02b4
        L_0x02b2:
            r9 = r54
        L_0x02b4:
            android.view.ViewGroup$LayoutParams r0 = r13.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            int[] r3 = r10.mTargetCell
            int r4 = r0.cellX
            r3[r11] = r4
            int[] r3 = r10.mTargetCell
            int r4 = r0.cellY
            r3[r2] = r4
            android.view.ViewParent r3 = r13.getParent()
            android.view.ViewParent r3 = r3.getParent()
            com.android.launcher3.CellLayout r3 = (com.android.launcher3.CellLayout) r3
            r3.markCellsAsOccupiedForView(r13)
            r12 = r25
        L_0x02d5:
            r14 = 500(0x1f4, double:2.47E-321)
            goto L_0x02fb
        L_0x02d8:
            r8 = r11
            r48 = r15
            r20 = r45
            r51 = r46
            r13 = r49
        L_0x02e1:
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.LauncherStateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            r14 = 500(0x1f4, double:2.47E-321)
            r0.goToState((com.android.launcher3.LauncherState) r1, (long) r14)
            return
        L_0x02ef:
            r13 = r8
            r8 = r11
            r2 = 1
            r11 = 0
            r14 = 500(0x1f4, double:2.47E-321)
            r46 = r12
            r47 = r16
            r12 = r25
        L_0x02fb:
            android.view.ViewParent r0 = r13.getParent()
            android.view.ViewParent r0 = r0.getParent()
            r7 = r0
            com.android.launcher3.CellLayout r7 = (com.android.launcher3.CellLayout) r7
            com.android.launcher3.dragndrop.DragView r0 = r8.dragView
            boolean r0 = r0.hasDrawn()
            if (r0 == 0) goto L_0x0377
            if (r47 == 0) goto L_0x0333
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.dragndrop.DragController r0 = r0.getDragController()
            r1 = 150(0x96, float:2.1E-43)
            r0.animateDragViewToOriginalPosition(r12, r13, r1)
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.LauncherStateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            r0.goToState(r1)
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.DropTargetBar r0 = r0.getDropTargetBar()
            r0.onDragEnd()
            r7.onDropChild(r13)
            return
        L_0x0333:
            java.lang.Object r0 = r13.getTag()
            r6 = r0
            com.android.launcher3.ItemInfo r6 = (com.android.launcher3.ItemInfo) r6
            int r0 = r6.itemType
            r1 = 4
            if (r0 == r1) goto L_0x0347
            int r0 = r6.itemType
            r1 = 5
            if (r0 != r1) goto L_0x0345
            goto L_0x0347
        L_0x0345:
            r2 = 0
        L_0x0347:
            r16 = r2
            if (r16 == 0) goto L_0x0361
            if (r24 == 0) goto L_0x034f
            r5 = 2
            goto L_0x0350
        L_0x034f:
            r5 = 0
        L_0x0350:
            com.android.launcher3.dragndrop.DragView r3 = r8.dragView
            r4 = 0
            r11 = 0
            r0 = r55
            r1 = r6
            r2 = r7
            r17 = r6
            r6 = r13
            r14 = r7
            r7 = r11
            r0.animateWidgetDrop(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x0376
        L_0x0361:
            r17 = r6
            r14 = r7
            if (r23 >= 0) goto L_0x0367
            goto L_0x0369
        L_0x0367:
            r26 = 300(0x12c, float:4.2E-43)
        L_0x0369:
            r0 = r26
            com.android.launcher3.Launcher r1 = r10.mLauncher
            com.android.launcher3.dragndrop.DragLayer r1 = r1.getDragLayer()
            com.android.launcher3.dragndrop.DragView r2 = r8.dragView
            r1.animateViewIntoPosition(r2, r13, r0, r10)
        L_0x0376:
            goto L_0x037d
        L_0x0377:
            r14 = r7
            r8.deferDragViewCleanupPostAnimation = r11
            r13.setVisibility(r11)
        L_0x037d:
            r14.onDropChild(r13)
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.LauncherStateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            r2 = 500(0x1f4, double:2.47E-321)
            r0.goToState((com.android.launcher3.LauncherState) r1, (long) r2, (java.lang.Runnable) r12)
            goto L_0x03a8
        L_0x038e:
            r8 = r11
            r2 = 1
            r11 = 0
            r0 = 2
            int[] r0 = new int[r0]
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r11]
            int r1 = (int) r1
            r0[r11] = r1
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r2]
            int r1 = (int) r1
            r0[r2] = r1
            r10.onDropExternal(r0, r9, r8)
            r46 = r12
        L_0x03a8:
            com.android.launcher3.accessibility.DragViewStateAnnouncer r0 = r8.stateAnnouncer
            if (r0 == 0) goto L_0x03b6
            if (r46 != 0) goto L_0x03b6
            com.android.launcher3.accessibility.DragViewStateAnnouncer r0 = r8.stateAnnouncer
            r1 = 2131886203(0x7f12007b, float:1.9406978E38)
            r0.completeAction(r1)
        L_0x03b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.onDrop(com.android.launcher3.DropTarget$DragObject, com.android.launcher3.dragndrop.DragOptions):void");
    }

    public void onNoCellFound(View dropTargetLayout) {
        if (this.mLauncher.isHotseatLayout(dropTargetLayout)) {
            Hotseat hotseat = this.mLauncher.getHotseat();
            if (0 == 0) {
                showOutOfSpaceMessage(true);
                return;
            }
            return;
        }
        showOutOfSpaceMessage(false);
    }

    private void showOutOfSpaceMessage(boolean isHotseatLayout) {
        Toast.makeText(this.mLauncher, this.mLauncher.getString(isHotseatLayout ? R.string.hotseat_out_of_space : R.string.out_of_space), 0).show();
    }

    public void getPageAreaRelativeToDragLayer(Rect outArea) {
        CellLayout child = (CellLayout) getChildAt(getNextPage());
        if (child != null) {
            ShortcutAndWidgetContainer boundingLayout = child.getShortcutsAndWidgets();
            this.mTempXY[0] = getPaddingLeft() + boundingLayout.getLeft();
            this.mTempXY[1] = child.getTop() + boundingLayout.getTop();
            float scale = this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, this.mTempXY);
            outArea.set(this.mTempXY[0], this.mTempXY[1], (int) (((float) this.mTempXY[0]) + (((float) boundingLayout.getMeasuredWidth()) * scale)), (int) (((float) this.mTempXY[1]) + (((float) boundingLayout.getMeasuredHeight()) * scale)));
        }
    }

    public void onDragEnter(DropTarget.DragObject d) {
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDropToLayout = null;
        this.mDragViewVisualCenter = d.getVisualCenter(this.mDragViewVisualCenter);
        setDropLayoutForDragObject(d, this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1]);
    }

    public void onDragExit(DropTarget.DragObject d) {
        this.mDropToLayout = this.mDragTargetLayout;
        if (this.mDragMode == 1) {
            this.mCreateUserFolderOnDrop = true;
        } else if (this.mDragMode == 2) {
            this.mAddToExistingFolderOnDrop = true;
        }
        setCurrentDropLayout((CellLayout) null);
        setCurrentDragOverlappingLayout((CellLayout) null);
        this.mSpringLoadedDragController.cancel();
    }

    private void enforceDragParity(String event, int update, int expectedValue) {
        enforceDragParity(this, event, update, expectedValue);
        for (int i = 0; i < getChildCount(); i++) {
            enforceDragParity(getChildAt(i), event, update, expectedValue);
        }
    }

    private void enforceDragParity(View v, String event, int update, int expectedValue) {
        Object tag = v.getTag(R.id.drag_event_parity);
        int value = (tag == null ? 0 : ((Integer) tag).intValue()) + update;
        v.setTag(R.id.drag_event_parity, Integer.valueOf(value));
        if (value != expectedValue) {
            Log.e(TAG, event + ": Drag contract violated: " + value);
        }
    }

    /* access modifiers changed from: package-private */
    public void setCurrentDropLayout(CellLayout layout) {
        if (this.mDragTargetLayout != null) {
            this.mDragTargetLayout.revertTempState();
            this.mDragTargetLayout.onDragExit();
        }
        this.mDragTargetLayout = layout;
        if (this.mDragTargetLayout != null) {
            this.mDragTargetLayout.onDragEnter();
        }
        cleanupReorder(true);
        cleanupFolderCreation();
        setCurrentDropOverCell(-1, -1);
    }

    /* access modifiers changed from: package-private */
    public void setCurrentDragOverlappingLayout(CellLayout layout) {
        if (this.mDragOverlappingLayout != null) {
            this.mDragOverlappingLayout.setIsDragOverlapping(false);
        }
        this.mDragOverlappingLayout = layout;
        if (this.mDragOverlappingLayout != null) {
            this.mDragOverlappingLayout.setIsDragOverlapping(true);
        }
        this.mLauncher.getDragLayer().getScrim().invalidate();
    }

    public CellLayout getCurrentDragOverlappingLayout() {
        return this.mDragOverlappingLayout;
    }

    /* access modifiers changed from: package-private */
    public void setCurrentDropOverCell(int x, int y) {
        if (x != this.mDragOverX || y != this.mDragOverY) {
            this.mDragOverX = x;
            this.mDragOverY = y;
            setDragMode(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void setDragMode(int dragMode) {
        if (dragMode != this.mDragMode) {
            if (dragMode == 0) {
                cleanupAddToFolder();
                cleanupReorder(false);
                cleanupFolderCreation();
            } else if (dragMode == 2) {
                cleanupReorder(true);
                cleanupFolderCreation();
            } else if (dragMode == 1) {
                cleanupAddToFolder();
                cleanupReorder(true);
            } else if (dragMode == 3) {
                cleanupAddToFolder();
                cleanupFolderCreation();
            }
            this.mDragMode = dragMode;
        }
    }

    private void cleanupFolderCreation() {
        if (this.mFolderCreateBg != null) {
            this.mFolderCreateBg.animateToRest();
        }
        this.mFolderCreationAlarm.setOnAlarmListener((OnAlarmListener) null);
        this.mFolderCreationAlarm.cancelAlarm();
    }

    private void cleanupAddToFolder() {
        if (this.mDragOverFolderIcon != null) {
            this.mDragOverFolderIcon.onDragExit();
            this.mDragOverFolderIcon = null;
        }
    }

    private void cleanupReorder(boolean cancelAlarm) {
        if (cancelAlarm) {
            this.mReorderAlarm.cancelAlarm();
        }
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
    }

    /* access modifiers changed from: package-private */
    public void mapPointFromSelfToChild(View v, float[] xy) {
        xy[0] = xy[0] - ((float) v.getLeft());
        xy[1] = xy[1] - ((float) v.getTop());
    }

    /* access modifiers changed from: package-private */
    public boolean isPointInSelfOverHotseat(int x, int y) {
        this.mTempXY[0] = x;
        this.mTempXY[1] = y;
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, this.mTempXY, true);
        View hotseat = this.mLauncher.getHotseat();
        if (this.mTempXY[0] < hotseat.getLeft() || this.mTempXY[0] > hotseat.getRight() || this.mTempXY[1] < hotseat.getTop() || this.mTempXY[1] > hotseat.getBottom()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void mapPointFromSelfToHotseatLayout(Hotseat hotseat, float[] xy) {
        this.mTempXY[0] = (int) xy[0];
        this.mTempXY[1] = (int) xy[1];
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, this.mTempXY, true);
        this.mLauncher.getDragLayer().mapCoordInSelfToDescendant(hotseat.getLayout(), this.mTempXY);
        xy[0] = (float) this.mTempXY[0];
        xy[1] = (float) this.mTempXY[1];
    }

    private boolean isDragWidget(DropTarget.DragObject d) {
        return (d.dragInfo instanceof LauncherAppWidgetInfo) || (d.dragInfo instanceof PendingAddWidgetInfo);
    }

    public void onDragOver(DropTarget.DragObject d) {
        ItemInfo item;
        int i;
        DropTarget.DragObject dragObject = d;
        if (!transitionStateShouldAllowDrop() || (item = dragObject.dragInfo) == null) {
            return;
        }
        if (item.spanX < 0 || item.spanY < 0) {
            throw new RuntimeException("Improper spans found");
        }
        this.mDragViewVisualCenter = dragObject.getVisualCenter(this.mDragViewVisualCenter);
        View child = this.mDragInfo == null ? null : this.mDragInfo.cell;
        if (setDropLayoutForDragObject(dragObject, this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1])) {
            if (this.mLauncher.isHotseatLayout(this.mDragTargetLayout)) {
                this.mSpringLoadedDragController.cancel();
            } else {
                this.mSpringLoadedDragController.setAlarm(this.mDragTargetLayout);
            }
        }
        if (this.mDragTargetLayout != null) {
            if (this.mLauncher.isHotseatLayout(this.mDragTargetLayout)) {
                mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
            } else {
                mapPointFromSelfToChild(this.mDragTargetLayout, this.mDragViewVisualCenter);
            }
            int minSpanX = item.spanX;
            int minSpanY = item.spanY;
            if (item.minSpanX > 0 && item.minSpanY > 0) {
                minSpanX = item.minSpanX;
                minSpanY = item.minSpanY;
            }
            int minSpanX2 = minSpanX;
            int minSpanY2 = minSpanY;
            this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX2, minSpanY2, this.mDragTargetLayout, this.mTargetCell);
            int reorderX = this.mTargetCell[0];
            int reorderY = this.mTargetCell[1];
            setCurrentDropOverCell(this.mTargetCell[0], this.mTargetCell[1]);
            float targetCellDistance = this.mDragTargetLayout.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell);
            manageFolderFeedback(this.mDragTargetLayout, this.mTargetCell, targetCellDistance, dragObject);
            boolean nearestDropOccupied = this.mDragTargetLayout.isNearestDropLocationOccupied((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], item.spanX, item.spanY, child, this.mTargetCell);
            if (!nearestDropOccupied) {
                CellLayout cellLayout = this.mDragTargetLayout;
                DragPreviewProvider dragPreviewProvider = this.mOutlineProvider;
                int i2 = this.mTargetCell[0];
                float f = targetCellDistance;
                int i3 = this.mTargetCell[1];
                int reorderY2 = reorderY;
                int i4 = reorderX;
                int i5 = reorderY2;
                cellLayout.visualizeDropLocation(child, dragPreviewProvider, i2, i3, item.spanX, item.spanY, false, d);
            } else {
                int reorderY3 = reorderY;
                int reorderX2 = reorderX;
                if ((this.mDragMode == 0 || this.mDragMode == 3) && !this.mReorderAlarm.alarmPending() && !(this.mLastReorderX == reorderX2 && this.mLastReorderY == reorderY3)) {
                    int[] resultSpan = new int[2];
                    this.mDragTargetLayout.performReorder((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX2, minSpanY2, item.spanX, item.spanY, child, this.mTargetCell, resultSpan, 0);
                    i = 1;
                    int[] iArr = resultSpan;
                    this.mReorderAlarm.setOnAlarmListener(new ReorderAlarmListener(this.mDragViewVisualCenter, minSpanX2, minSpanY2, item.spanX, item.spanY, d, child));
                    this.mReorderAlarm.setAlarm(650);
                    if ((this.mDragMode == i || this.mDragMode == 2 || !nearestDropOccupied) && this.mDragTargetLayout != null) {
                        this.mDragTargetLayout.revertTempState();
                    }
                    return;
                }
            }
            i = 1;
            if (!(this.mDragMode == i || this.mDragMode == 2)) {
            }
            this.mDragTargetLayout.revertTempState();
        }
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.view.View] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean setDropLayoutForDragObject(com.android.launcher3.DropTarget.DragObject r8, float r9, float r10) {
        /*
            r7 = this;
            r0 = 0
            com.android.launcher3.Launcher r1 = r7.mLauncher
            com.android.launcher3.Hotseat r1 = r1.getHotseat()
            if (r1 == 0) goto L_0x0023
            boolean r1 = r7.isDragWidget(r8)
            if (r1 != 0) goto L_0x0023
            int r1 = r8.x
            int r2 = r8.y
            boolean r1 = r7.isPointInSelfOverHotseat(r1, r2)
            if (r1 == 0) goto L_0x0023
            com.android.launcher3.Launcher r1 = r7.mLauncher
            com.android.launcher3.Hotseat r1 = r1.getHotseat()
            com.android.launcher3.CellLayout r0 = r1.getLayout()
        L_0x0023:
            int r1 = r7.getNextPage()
            r2 = -1
            r3 = 0
            r4 = 1
            if (r0 != 0) goto L_0x0052
            boolean r5 = r7.isPageInTransition()
            if (r5 != 0) goto L_0x0052
            float[] r5 = r7.mTempTouchCoordinates
            int r6 = r8.x
            float r6 = (float) r6
            float r6 = java.lang.Math.min(r9, r6)
            r5[r3] = r6
            float[] r5 = r7.mTempTouchCoordinates
            int r6 = r8.y
            float r6 = (float) r6
            r5[r4] = r6
            boolean r5 = r7.mIsRtl
            if (r5 == 0) goto L_0x004a
            r5 = 1
            goto L_0x004b
        L_0x004a:
            r5 = -1
        L_0x004b:
            int r5 = r5 + r1
            float[] r6 = r7.mTempTouchCoordinates
            com.android.launcher3.CellLayout r0 = r7.verifyInsidePage(r5, r6)
        L_0x0052:
            if (r0 != 0) goto L_0x0079
            boolean r5 = r7.isPageInTransition()
            if (r5 != 0) goto L_0x0079
            float[] r5 = r7.mTempTouchCoordinates
            int r6 = r8.x
            float r6 = (float) r6
            float r6 = java.lang.Math.max(r9, r6)
            r5[r3] = r6
            float[] r5 = r7.mTempTouchCoordinates
            int r6 = r8.y
            float r6 = (float) r6
            r5[r4] = r6
            boolean r5 = r7.mIsRtl
            if (r5 == 0) goto L_0x0071
            goto L_0x0072
        L_0x0071:
            r2 = 1
        L_0x0072:
            int r2 = r2 + r1
            float[] r5 = r7.mTempTouchCoordinates
            com.android.launcher3.CellLayout r0 = r7.verifyInsidePage(r2, r5)
        L_0x0079:
            if (r0 != 0) goto L_0x008a
            if (r1 < 0) goto L_0x008a
            int r2 = r7.getPageCount()
            if (r1 >= r2) goto L_0x008a
            android.view.View r2 = r7.getChildAt(r1)
            r0 = r2
            com.android.launcher3.CellLayout r0 = (com.android.launcher3.CellLayout) r0
        L_0x008a:
            com.android.launcher3.CellLayout r2 = r7.mDragTargetLayout
            if (r0 == r2) goto L_0x0095
            r7.setCurrentDropLayout(r0)
            r7.setCurrentDragOverlappingLayout(r0)
            return r4
        L_0x0095:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.setDropLayoutForDragObject(com.android.launcher3.DropTarget$DragObject, float, float):boolean");
    }

    private CellLayout verifyInsidePage(int pageNo, float[] touchXy) {
        if (pageNo < 0 || pageNo >= getPageCount()) {
            return null;
        }
        CellLayout cl = (CellLayout) getChildAt(pageNo);
        mapPointFromSelfToChild(cl, touchXy);
        if (touchXy[0] < 0.0f || touchXy[0] > ((float) cl.getWidth()) || touchXy[1] < 0.0f || touchXy[1] > ((float) cl.getHeight())) {
            return null;
        }
        return cl;
    }

    private void manageFolderFeedback(CellLayout targetLayout, int[] targetCell, float distance, DropTarget.DragObject dragObject) {
        if (distance <= this.mMaxDistanceForFolderCreation) {
            View dragOverView = this.mDragTargetLayout.getChildAt(this.mTargetCell[0], this.mTargetCell[1]);
            ItemInfo info = dragObject.dragInfo;
            boolean userFolderPending = willCreateUserFolder(info, dragOverView, false);
            if (this.mDragMode != 0 || !userFolderPending || this.mFolderCreationAlarm.alarmPending()) {
                boolean willAddToFolder = willAddToExistingUserFolder(info, dragOverView);
                if (!willAddToFolder || this.mDragMode != 0) {
                    if (this.mDragMode == 2 && !willAddToFolder) {
                        setDragMode(0);
                    }
                    if (this.mDragMode == 1 && !userFolderPending) {
                        setDragMode(0);
                        return;
                    }
                    return;
                }
                this.mDragOverFolderIcon = (FolderIcon) dragOverView;
                this.mDragOverFolderIcon.onDragEnter(info);
                if (targetLayout != null) {
                    targetLayout.clearDragOutlines();
                }
                setDragMode(2);
                if (dragObject.stateAnnouncer != null) {
                    dragObject.stateAnnouncer.announce(WorkspaceAccessibilityHelper.getDescriptionForDropOver(dragOverView, getContext()));
                    return;
                }
                return;
            }
            FolderCreationAlarmListener listener = new FolderCreationAlarmListener(targetLayout, targetCell[0], targetCell[1]);
            if (!dragObject.accessibleDrag) {
                this.mFolderCreationAlarm.setOnAlarmListener(listener);
                this.mFolderCreationAlarm.setAlarm(0);
            } else {
                listener.onAlarm(this.mFolderCreationAlarm);
            }
            if (dragObject.stateAnnouncer != null) {
                dragObject.stateAnnouncer.announce(WorkspaceAccessibilityHelper.getDescriptionForDropOver(dragOverView, getContext()));
            }
        } else if (this.mDragMode != 0) {
            setDragMode(0);
        }
    }

    class FolderCreationAlarmListener implements OnAlarmListener {
        final PreviewBackground bg = new PreviewBackground();
        final int cellX;
        final int cellY;
        final CellLayout layout;

        public FolderCreationAlarmListener(CellLayout layout2, int cellX2, int cellY2) {
            this.layout = layout2;
            this.cellX = cellX2;
            this.cellY = cellY2;
            BubbleTextView cell = (BubbleTextView) layout2.getChildAt(cellX2, cellY2);
            this.bg.setup(Workspace.this.mLauncher, (View) null, cell.getMeasuredWidth(), cell.getPaddingTop());
            this.bg.isClipping = false;
        }

        public void onAlarm(Alarm alarm) {
            PreviewBackground unused = Workspace.this.mFolderCreateBg = this.bg;
            Workspace.this.mFolderCreateBg.animateToAccept(this.layout, this.cellX, this.cellY);
            this.layout.clearDragOutlines();
            Workspace.this.setDragMode(1);
        }
    }

    class ReorderAlarmListener implements OnAlarmListener {
        final View child;
        final DropTarget.DragObject dragObject;
        final float[] dragViewCenter;
        final int minSpanX;
        final int minSpanY;
        final int spanX;
        final int spanY;

        public ReorderAlarmListener(float[] dragViewCenter2, int minSpanX2, int minSpanY2, int spanX2, int spanY2, DropTarget.DragObject dragObject2, View child2) {
            this.dragViewCenter = dragViewCenter2;
            this.minSpanX = minSpanX2;
            this.minSpanY = minSpanY2;
            this.spanX = spanX2;
            this.spanY = spanY2;
            this.child = child2;
            this.dragObject = dragObject2;
        }

        public void onAlarm(Alarm alarm) {
            int[] resultSpan = new int[2];
            Workspace.this.mTargetCell = Workspace.this.findNearestArea((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, Workspace.this.mDragTargetLayout, Workspace.this.mTargetCell);
            Workspace.this.mLastReorderX = Workspace.this.mTargetCell[0];
            Workspace.this.mLastReorderY = Workspace.this.mTargetCell[1];
            Workspace.this.mTargetCell = Workspace.this.mDragTargetLayout.performReorder((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, this.spanX, this.spanY, this.child, Workspace.this.mTargetCell, resultSpan, 1);
            if (Workspace.this.mTargetCell[0] < 0 || Workspace.this.mTargetCell[1] < 0) {
                Workspace.this.mDragTargetLayout.revertTempState();
            } else {
                Workspace.this.setDragMode(3);
            }
            Workspace.this.mDragTargetLayout.visualizeDropLocation(this.child, Workspace.this.mOutlineProvider, Workspace.this.mTargetCell[0], Workspace.this.mTargetCell[1], resultSpan[0], resultSpan[1], (resultSpan[0] == this.spanX && resultSpan[1] == this.spanY) ? false : true, this.dragObject);
        }
    }

    public void getHitRectRelativeToDragLayer(Rect outRect) {
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this, outRect);
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0269  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0295  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02d5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onDropExternal(int[] r39, com.android.launcher3.CellLayout r40, com.android.launcher3.DropTarget.DragObject r41) {
        /*
            r38 = this;
            r15 = r38
            r14 = r40
            r12 = r41
            com.android.launcher3.ItemInfo r0 = r12.dragInfo
            boolean r0 = r0 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            if (r0 == 0) goto L_0x001a
            com.android.launcher3.ItemInfo r0 = r12.dragInfo
            com.android.launcher3.widget.PendingAddShortcutInfo r0 = (com.android.launcher3.widget.PendingAddShortcutInfo) r0
            com.android.launcher3.compat.ShortcutConfigActivityInfo r0 = r0.activityInfo
            com.android.launcher3.ShortcutInfo r0 = r0.createShortcutInfo()
            if (r0 == 0) goto L_0x001a
            r12.dragInfo = r0
        L_0x001a:
            com.android.launcher3.ItemInfo r13 = r12.dragInfo
            int r0 = r13.spanX
            int r1 = r13.spanY
            com.android.launcher3.CellLayout$CellInfo r2 = r15.mDragInfo
            if (r2 == 0) goto L_0x002c
            com.android.launcher3.CellLayout$CellInfo r2 = r15.mDragInfo
            int r0 = r2.spanX
            com.android.launcher3.CellLayout$CellInfo r2 = r15.mDragInfo
            int r1 = r2.spanY
        L_0x002c:
            r27 = r0
            r28 = r1
            com.android.launcher3.Launcher r0 = r15.mLauncher
            boolean r0 = r0.isHotseatLayout(r14)
            if (r0 == 0) goto L_0x003c
            r0 = -101(0xffffffffffffff9b, double:NaN)
        L_0x003a:
            r10 = r0
            goto L_0x003f
        L_0x003c:
            r0 = -100
            goto L_0x003a
        L_0x003f:
            long r8 = r15.getIdForScreen(r14)
            com.android.launcher3.Launcher r0 = r15.mLauncher
            boolean r0 = r0.isHotseatLayout(r14)
            if (r0 != 0) goto L_0x0066
            int r0 = r15.mCurrentPage
            long r0 = r15.getScreenIdForPageIndex(r0)
            int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x0066
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.SPRING_LOADED
            boolean r0 = r0.isInState(r1)
            if (r0 != 0) goto L_0x0066
            int r0 = r15.getPageIndexForScreenId(r8)
            r15.snapToPage(r0)
        L_0x0066:
            boolean r0 = r13 instanceof com.android.launcher3.PendingAddItemInfo
            r29 = 0
            r7 = 1
            if (r0 == 0) goto L_0x019a
            r6 = r13
            com.android.launcher3.PendingAddItemInfo r6 = (com.android.launcher3.PendingAddItemInfo) r6
            r16 = 1
            int r0 = r6.itemType
            if (r0 != r7) goto L_0x00bb
            r1 = r39[r29]
            r2 = r39[r7]
            int[] r5 = r15.mTargetCell
            r0 = r38
            r3 = r27
            r4 = r28
            r17 = r5
            r5 = r40
            r30 = r6
            r6 = r17
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r15.mTargetCell = r0
            float[] r0 = r15.mDragViewVisualCenter
            r0 = r0[r29]
            float[] r1 = r15.mDragViewVisualCenter
            r1 = r1[r7]
            int[] r2 = r15.mTargetCell
            float r6 = r14.getDistanceFromCell(r0, r1, r2)
            com.android.launcher3.ItemInfo r1 = r12.dragInfo
            int[] r3 = r15.mTargetCell
            r5 = 1
            r0 = r38
            r2 = r40
            r4 = r6
            boolean r0 = r0.willCreateUserFolder(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x00b8
            com.android.launcher3.ItemInfo r0 = r12.dragInfo
            int[] r1 = r15.mTargetCell
            boolean r0 = r15.willAddToExistingUserFolder(r0, r14, r1, r6)
            if (r0 == 0) goto L_0x00bd
        L_0x00b8:
            r16 = 0
            goto L_0x00bd
        L_0x00bb:
            r30 = r6
        L_0x00bd:
            r31 = r16
            com.android.launcher3.ItemInfo r6 = r12.dragInfo
            r0 = 0
            if (r31 == 0) goto L_0x0125
            int r1 = r6.spanX
            int r2 = r6.spanY
            int r3 = r6.minSpanX
            if (r3 <= 0) goto L_0x00d4
            int r3 = r6.minSpanY
            if (r3 <= 0) goto L_0x00d4
            int r1 = r6.minSpanX
            int r2 = r6.minSpanY
        L_0x00d4:
            r3 = 2
            int[] r3 = new int[r3]
            float[] r4 = r15.mDragViewVisualCenter
            r4 = r4[r29]
            int r4 = (int) r4
            float[] r5 = r15.mDragViewVisualCenter
            r5 = r5[r7]
            int r5 = (int) r5
            int r7 = r13.spanX
            r32 = r0
            int r0 = r13.spanY
            r23 = 0
            r33 = r8
            int[] r8 = r15.mTargetCell
            r26 = 3
            r16 = r40
            r17 = r4
            r18 = r5
            r19 = r1
            r20 = r2
            r21 = r7
            r22 = r0
            r24 = r8
            r25 = r3
            int[] r0 = r16.performReorder(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26)
            r15.mTargetCell = r0
            r0 = r3[r29]
            int r4 = r6.spanX
            if (r0 != r4) goto L_0x0118
            r0 = 1
            r4 = r3[r0]
            int r0 = r6.spanY
            if (r4 == r0) goto L_0x0115
            goto L_0x0118
        L_0x0115:
            r0 = r32
            goto L_0x0119
        L_0x0118:
            r0 = 1
        L_0x0119:
            r4 = r3[r29]
            r6.spanX = r4
            r4 = 1
            r5 = r3[r4]
            r6.spanY = r5
            r32 = r0
            goto L_0x012a
        L_0x0125:
            r32 = r0
            r33 = r8
            r4 = 1
        L_0x012a:
            com.android.launcher3.Workspace$8 r0 = new com.android.launcher3.Workspace$8
            r5 = 1
            r7 = r0
            r8 = r38
            r9 = r30
            r3 = r12
            r2 = r13
            r12 = r33
            r1 = r14
            r14 = r6
            r7.<init>(r9, r10, r12, r14)
            r4 = r0
            r8 = r30
            int r0 = r8.itemType
            r7 = 4
            if (r0 == r7) goto L_0x014a
            int r0 = r8.itemType
            r7 = 5
            if (r0 != r7) goto L_0x0149
            goto L_0x014a
        L_0x0149:
            goto L_0x014c
        L_0x014a:
            r29 = 1
        L_0x014c:
            r9 = r29
            if (r9 == 0) goto L_0x0156
            r0 = r8
            com.android.launcher3.widget.PendingAddWidgetInfo r0 = (com.android.launcher3.widget.PendingAddWidgetInfo) r0
            android.appwidget.AppWidgetHostView r0 = r0.boundWidget
            goto L_0x0157
        L_0x0156:
            r0 = 0
        L_0x0157:
            r12 = r0
            if (r12 == 0) goto L_0x0165
            if (r32 == 0) goto L_0x0165
            com.android.launcher3.Launcher r0 = r15.mLauncher
            int r5 = r6.spanX
            int r7 = r6.spanY
            com.android.launcher3.AppWidgetResizeFrame.updateWidgetSizeRanges(r12, r0, r5, r7)
        L_0x0165:
            r0 = 0
            if (r9 == 0) goto L_0x017d
            r5 = r8
            com.android.launcher3.widget.PendingAddWidgetInfo r5 = (com.android.launcher3.widget.PendingAddWidgetInfo) r5
            com.android.launcher3.LauncherAppWidgetProviderInfo r5 = r5.info
            if (r5 == 0) goto L_0x017d
            r5 = r8
            com.android.launcher3.widget.PendingAddWidgetInfo r5 = (com.android.launcher3.widget.PendingAddWidgetInfo) r5
            com.android.launcher3.widget.WidgetAddFlowHandler r5 = r5.getHandler()
            boolean r5 = r5.needsConfigure()
            if (r5 == 0) goto L_0x017d
            r0 = 1
        L_0x017d:
            r13 = r0
            com.android.launcher3.dragndrop.DragView r5 = r3.dragView
            r7 = 1
            r0 = r38
            r14 = r1
            r1 = r2
            r35 = r2
            r2 = r40
            r3 = r5
            r5 = r13
            r16 = r6
            r6 = r12
            r0.animateWidgetDrop(r1, r2, r3, r4, r5, r6, r7)
            r36 = r10
            r13 = r35
            r0 = r41
            goto L_0x02e6
        L_0x019a:
            r33 = r8
            r35 = r13
            r5 = 1
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.LauncherStateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            r2 = 500(0x1f4, double:2.47E-321)
            r0.goToState((com.android.launcher3.LauncherState) r1, (long) r2)
            r0 = r35
            int r1 = r0.itemType
            r2 = 6
            if (r1 == r2) goto L_0x01e0
            switch(r1) {
                case 0: goto L_0x01e0;
                case 1: goto L_0x01e0;
                case 2: goto L_0x01cf;
                default: goto L_0x01b6;
            }
        L_0x01b6:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown item type: "
            r2.append(r3)
            int r3 = r0.itemType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x01cf:
            r1 = 2131623989(0x7f0e0035, float:1.8875145E38)
            com.android.launcher3.Launcher r2 = r15.mLauncher
            r3 = r0
            com.android.launcher3.FolderInfo r3 = (com.android.launcher3.FolderInfo) r3
            com.android.launcher3.folder.FolderIcon r1 = com.android.launcher3.folder.FolderIcon.fromXml(r1, r2, r14, r3)
            r13 = r0
            r12 = r41
            goto L_0x0217
        L_0x01e0:
            long r1 = r0.container
            r3 = -1
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 != 0) goto L_0x020a
            boolean r1 = r0 instanceof com.android.launcher3.AppInfo
            if (r1 == 0) goto L_0x01f8
            r1 = r0
            com.android.launcher3.AppInfo r1 = (com.android.launcher3.AppInfo) r1
            com.android.launcher3.ShortcutInfo r13 = r1.makeShortcut()
            r12 = r41
            r12.dragInfo = r13
            goto L_0x020d
        L_0x01f8:
            r12 = r41
            boolean r1 = r0 instanceof com.android.launcher3.ShortcutInfo
            if (r1 == 0) goto L_0x020c
            com.android.launcher3.ShortcutInfo r1 = new com.android.launcher3.ShortcutInfo
            r2 = r0
            com.android.launcher3.ShortcutInfo r2 = (com.android.launcher3.ShortcutInfo) r2
            r1.<init>((com.android.launcher3.ShortcutInfo) r2)
            r13 = r1
            r12.dragInfo = r13
            goto L_0x020d
        L_0x020a:
            r12 = r41
        L_0x020c:
            r13 = r0
        L_0x020d:
            com.android.launcher3.Launcher r0 = r15.mLauncher
            r1 = r13
            com.android.launcher3.ShortcutInfo r1 = (com.android.launcher3.ShortcutInfo) r1
            android.view.View r1 = r0.createShortcut(r14, r1)
        L_0x0217:
            r9 = r1
            if (r39 == 0) goto L_0x0269
            r1 = r39[r29]
            r2 = r39[r5]
            int[] r6 = r15.mTargetCell
            r0 = r38
            r3 = r27
            r4 = r28
            r8 = 1
            r5 = r40
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r15.mTargetCell = r0
            float[] r0 = r15.mDragViewVisualCenter
            r0 = r0[r29]
            float[] r1 = r15.mDragViewVisualCenter
            r1 = r1[r8]
            int[] r2 = r15.mTargetCell
            float r16 = r14.getDistanceFromCell(r0, r1, r2)
            int[] r5 = r15.mTargetCell
            r7 = 1
            com.android.launcher3.dragndrop.DragView r6 = r12.dragView
            r0 = r38
            r1 = r9
            r2 = r10
            r4 = r40
            r17 = r6
            r6 = r16
            r12 = 1
            r8 = r17
            boolean r0 = r0.createUserFolderIfNecessary(r1, r2, r4, r5, r6, r7, r8)
            if (r0 == 0) goto L_0x0256
            return
        L_0x0256:
            int[] r3 = r15.mTargetCell
            r6 = 1
            r0 = r38
            r1 = r9
            r2 = r40
            r4 = r16
            r5 = r41
            boolean r0 = r0.addToExistingFolderIfNecessary(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x026a
            return
        L_0x0269:
            r12 = 1
        L_0x026a:
            if (r39 == 0) goto L_0x0295
            float[] r0 = r15.mDragViewVisualCenter
            r0 = r0[r29]
            int r0 = (int) r0
            float[] r1 = r15.mDragViewVisualCenter
            r1 = r1[r12]
            int r1 = (int) r1
            r19 = 1
            r20 = 1
            r21 = 1
            r22 = 1
            r23 = 0
            int[] r2 = r15.mTargetCell
            r25 = 0
            r26 = 3
            r16 = r40
            r17 = r0
            r18 = r1
            r24 = r2
            int[] r0 = r16.performReorder(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26)
            r15.mTargetCell = r0
            goto L_0x029a
        L_0x0295:
            int[] r0 = r15.mTargetCell
            r14.findCellForSpan(r0, r12, r12)
        L_0x029a:
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.model.ModelWriter r0 = r0.getModelWriter()
            int[] r1 = r15.mTargetCell
            r6 = r1[r29]
            int[] r1 = r15.mTargetCell
            r7 = r1[r12]
            r1 = r13
            r2 = r10
            r4 = r33
            r0.addOrMoveItemInDatabase(r1, r2, r4, r6, r7)
            int[] r0 = r15.mTargetCell
            r6 = r0[r29]
            int[] r0 = r15.mTargetCell
            r7 = r0[r12]
            int r8 = r13.spanX
            int r12 = r13.spanY
            r0 = r38
            r1 = r9
            r36 = r10
            r10 = r9
            r9 = r12
            r0.addInScreen(r1, r2, r4, r6, r7, r8, r9)
            r14.onDropChild(r10)
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r40.getShortcutsAndWidgets()
            r0.measureChild(r10)
            r0 = r41
            com.android.launcher3.dragndrop.DragView r1 = r0.dragView
            if (r1 == 0) goto L_0x02e6
            r38.setFinalTransitionTransform()
            com.android.launcher3.Launcher r1 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r1 = r1.getDragLayer()
            com.android.launcher3.dragndrop.DragView r2 = r0.dragView
            r1.animateViewIntoPosition(r2, r10, r15)
            r38.resetTransitionTransform()
        L_0x02e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.onDropExternal(int[], com.android.launcher3.CellLayout, com.android.launcher3.DropTarget$DragObject):void");
    }

    public Bitmap createWidgetBitmap(ItemInfo widgetInfo, View layout) {
        int[] unScaledSize = estimateItemSize(widgetInfo);
        int visibility = layout.getVisibility();
        layout.setVisibility(0);
        int width = View.MeasureSpec.makeMeasureSpec(unScaledSize[0], 1073741824);
        int height = View.MeasureSpec.makeMeasureSpec(unScaledSize[1], 1073741824);
        Bitmap b = Bitmap.createBitmap(unScaledSize[0], unScaledSize[1], Bitmap.Config.ARGB_8888);
        layout.measure(width, height);
        layout.layout(0, 0, unScaledSize[0], unScaledSize[1]);
        layout.draw(new Canvas(b));
        layout.setVisibility(visibility);
        return b;
    }

    private void getFinalPositionForDropAnimation(int[] loc, float[] scaleXY, DragView dragView, CellLayout layout, ItemInfo info, int[] targetCell, boolean scale) {
        int[] iArr = loc;
        ItemInfo itemInfo = info;
        CellLayout cellLayout = layout;
        Rect r = estimateItemPosition(cellLayout, targetCell[0], targetCell[1], itemInfo.spanX, itemInfo.spanY);
        if (itemInfo.itemType == 4) {
            DeviceProfile profile = this.mLauncher.getDeviceProfile();
            Utilities.shrinkRect(r, profile.appWidgetScale.x, profile.appWidgetScale.y);
        }
        iArr[0] = r.left;
        iArr[1] = r.top;
        setFinalTransitionTransform();
        float cellLayoutScale = this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(layout, iArr, true);
        resetTransitionTransform();
        if (scale) {
            float dragViewScaleX = (((float) r.width()) * 1.0f) / ((float) dragView.getMeasuredWidth());
            float dragViewScaleY = (((float) r.height()) * 1.0f) / ((float) dragView.getMeasuredHeight());
            iArr[0] = (int) (((double) iArr[0]) - (((double) ((((float) dragView.getMeasuredWidth()) - (((float) r.width()) * cellLayoutScale)) / 2.0f)) - Math.ceil((double) (((float) layout.getUnusedHorizontalSpace()) / 2.0f))));
            iArr[1] = (int) (((float) iArr[1]) - ((((float) dragView.getMeasuredHeight()) - (((float) r.height()) * cellLayoutScale)) / 2.0f));
            scaleXY[0] = dragViewScaleX * cellLayoutScale;
            scaleXY[1] = dragViewScaleY * cellLayoutScale;
            return;
        }
        float dragScale = dragView.getInitialScale() * cellLayoutScale;
        iArr[0] = (int) (((float) iArr[0]) + (((dragScale - 1.0f) * ((float) dragView.getWidth())) / 2.0f));
        iArr[1] = (int) (((float) iArr[1]) + (((dragScale - 1.0f) * ((float) dragView.getHeight())) / 2.0f));
        scaleXY[1] = dragScale;
        scaleXY[0] = dragScale;
        Rect dragRegion = dragView.getDragRegion();
        if (dragRegion != null) {
            iArr[0] = (int) (((float) iArr[0]) + (((float) dragRegion.left) * cellLayoutScale));
            iArr[1] = (int) (((float) iArr[1]) + (((float) dragRegion.top) * cellLayoutScale));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void animateWidgetDrop(com.android.launcher3.ItemInfo r29, com.android.launcher3.CellLayout r30, com.android.launcher3.dragndrop.DragView r31, java.lang.Runnable r32, int r33, android.view.View r34, boolean r35) {
        /*
            r28 = this;
            r15 = r28
            r14 = r29
            r13 = r31
            r12 = r33
            r11 = r34
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r10 = r0
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            r0.getViewRectRelativeToSelf(r13, r10)
            r8 = 2
            int[] r9 = new int[r8]
            float[] r6 = new float[r8]
            boolean r0 = r14 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            r16 = 0
            r5 = 1
            if (r0 != 0) goto L_0x0027
            r7 = 1
            goto L_0x0028
        L_0x0027:
            r7 = 0
        L_0x0028:
            int[] r4 = r15.mTargetCell
            r0 = r28
            r1 = r9
            r2 = r6
            r3 = r31
            r17 = r4
            r4 = r30
            r5 = r29
            r26 = r6
            r6 = r17
            r0.getFinalPositionForDropAnimation(r1, r2, r3, r4, r5, r6, r7)
            com.android.launcher3.Launcher r0 = r15.mLauncher
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131492871(0x7f0c0007, float:1.8609206E38)
            int r1 = r0.getInteger(r1)
            int r1 = r1 + -200
            int r2 = r14.itemType
            r3 = 4
            if (r2 == r3) goto L_0x0059
            int r2 = r14.itemType
            r4 = 5
            if (r2 != r4) goto L_0x0057
            goto L_0x0059
        L_0x0057:
            r2 = 0
            goto L_0x005a
        L_0x0059:
            r2 = 1
        L_0x005a:
            if (r12 == r8) goto L_0x005e
            if (r35 == 0) goto L_0x0072
        L_0x005e:
            if (r11 == 0) goto L_0x0072
            android.graphics.Bitmap r4 = r15.createWidgetBitmap(r14, r11)
            r13.setCrossFadeBitmap(r4)
            float r5 = (float) r1
            r6 = 1061997773(0x3f4ccccd, float:0.8)
            float r5 = r5 * r6
            int r5 = (int) r5
            r13.crossFade(r5)
            goto L_0x0084
        L_0x0072:
            if (r2 == 0) goto L_0x0084
            if (r35 == 0) goto L_0x0084
            r4 = r26[r16]
            r5 = 1
            r6 = r26[r5]
            float r4 = java.lang.Math.min(r4, r6)
            r26[r5] = r4
            r26[r16] = r4
            goto L_0x0085
        L_0x0084:
            r5 = 1
        L_0x0085:
            com.android.launcher3.Launcher r4 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r4 = r4.getDragLayer()
            if (r12 != r3) goto L_0x00af
            com.android.launcher3.Launcher r3 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r16 = r3.getDragLayer()
            r19 = 0
            r20 = 1036831949(0x3dcccccd, float:0.1)
            r21 = 1036831949(0x3dcccccd, float:0.1)
            r22 = 0
            r17 = r31
            r18 = r9
            r23 = r32
            r24 = r1
            r16.animateViewIntoPosition(r17, r18, r19, r20, r21, r22, r23, r24)
            r6 = r32
            r25 = r9
            r27 = r10
            goto L_0x00f3
        L_0x00af:
            if (r12 != r5) goto L_0x00b5
            r3 = 2
            r20 = r3
            goto L_0x00b7
        L_0x00b5:
            r20 = 0
        L_0x00b7:
            com.android.launcher3.Workspace$9 r3 = new com.android.launcher3.Workspace$9
            r6 = r32
            r3.<init>(r11, r6)
            r19 = r3
            int r3 = r10.left
            int r8 = r10.top
            r17 = r9[r16]
            r18 = r9[r5]
            r21 = 1065353216(0x3f800000, float:1.0)
            r22 = 1065353216(0x3f800000, float:1.0)
            r23 = 1065353216(0x3f800000, float:1.0)
            r24 = r26[r16]
            r5 = r26[r5]
            r16 = r8
            r8 = r4
            r25 = r9
            r9 = r31
            r27 = r10
            r10 = r3
            r11 = r16
            r12 = r17
            r13 = r18
            r14 = r21
            r15 = r22
            r16 = r23
            r17 = r24
            r18 = r5
            r21 = r1
            r22 = r28
            r8.animateViewIntoPosition(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
        L_0x00f3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.animateWidgetDrop(com.android.launcher3.ItemInfo, com.android.launcher3.CellLayout, com.android.launcher3.dragndrop.DragView, java.lang.Runnable, int, android.view.View, boolean):void");
    }

    public void setFinalTransitionTransform() {
        if (isSwitchingState()) {
            this.mCurrentScale = getScaleX();
            setScaleX(this.mStateTransitionAnimation.getFinalScale());
            setScaleY(this.mStateTransitionAnimation.getFinalScale());
        }
    }

    public void resetTransitionTransform() {
        if (isSwitchingState()) {
            setScaleX(this.mCurrentScale);
            setScaleY(this.mCurrentScale);
        }
    }

    public CellLayout.CellInfo getDragInfo() {
        return this.mDragInfo;
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, CellLayout layout, int[] recycle) {
        return layout.findNearestArea(pixelX, pixelY, spanX, spanY, recycle);
    }

    /* access modifiers changed from: package-private */
    public void setup(DragController dragController) {
        this.mSpringLoadedDragController = new SpringLoadedDragController(this.mLauncher);
        this.mDragController = dragController;
        updateChildrenLayersEnabled();
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
        CellLayout cellLayout;
        if (success) {
            if (!(target == this || this.mDragInfo == null)) {
                removeWorkspaceItem(this.mDragInfo.cell);
            }
        } else if (!(this.mDragInfo == null || (cellLayout = this.mLauncher.getCellLayout(this.mDragInfo.container, this.mDragInfo.screenId)) == null)) {
            cellLayout.onDropChild(this.mDragInfo.cell);
        }
        View cell = getHomescreenIconByItemId(d.originalDragInfo.id);
        if (d.cancelled && cell != null) {
            cell.setVisibility(0);
        }
        this.mDragInfo = null;
    }

    public void removeWorkspaceItem(View v) {
        CellLayout parentCell = getParentCellLayoutForView(v);
        if (parentCell != null) {
            parentCell.removeView(v);
        }
        if (v instanceof DropTarget) {
            this.mDragController.removeDropTarget((DropTarget) v);
        }
    }

    public void removeFolderListeners() {
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View view) {
                if (!(view instanceof FolderIcon)) {
                    return false;
                }
                ((FolderIcon) view).removeListeners();
                return false;
            }
        });
    }

    public boolean isDropEnabled() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        this.mSavedStates = container;
    }

    public void restoreInstanceStateForChild(int child) {
        if (this.mSavedStates != null) {
            this.mRestoredPages.add(Integer.valueOf(child));
            CellLayout cl = (CellLayout) getChildAt(child);
            if (cl != null) {
                cl.restoreInstanceState(this.mSavedStates);
            }
        }
    }

    public void restoreInstanceStateForRemainingPages() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (!this.mRestoredPages.contains(Integer.valueOf(i))) {
                restoreInstanceStateForChild(i);
            }
        }
        this.mRestoredPages.clear();
        this.mSavedStates = null;
    }

    public boolean scrollLeft() {
        boolean result = false;
        if (!workspaceInModalState() && !this.mIsSwitchingState) {
            result = super.scrollLeft();
        }
        Folder openFolder = Folder.getOpen(this.mLauncher);
        if (openFolder != null) {
            openFolder.completeDragExit();
        }
        return result;
    }

    public boolean scrollRight() {
        boolean result = false;
        if (!workspaceInModalState() && !this.mIsSwitchingState) {
            result = super.scrollRight();
        }
        Folder openFolder = Folder.getOpen(this.mLauncher);
        if (openFolder != null) {
            openFolder.completeDragExit();
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public CellLayout getParentCellLayoutForView(View v) {
        Iterator<CellLayout> it = getWorkspaceAndHotseatCellLayouts().iterator();
        while (it.hasNext()) {
            CellLayout layout = it.next();
            if (layout.getShortcutsAndWidgets().indexOfChild(v) > -1) {
                return layout;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<CellLayout> getWorkspaceAndHotseatCellLayouts() {
        ArrayList<CellLayout> layouts = new ArrayList<>();
        int screenCount = getChildCount();
        for (int screen = 0; screen < screenCount; screen++) {
            layouts.add((CellLayout) getChildAt(screen));
        }
        if (this.mLauncher.getHotseat() != null) {
            layouts.add(this.mLauncher.getHotseat().getLayout());
        }
        return layouts;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ShortcutAndWidgetContainer> getAllShortcutAndWidgetContainers() {
        ArrayList<ShortcutAndWidgetContainer> childrenLayouts = new ArrayList<>();
        int screenCount = getChildCount();
        for (int screen = 0; screen < screenCount; screen++) {
            childrenLayouts.add(((CellLayout) getChildAt(screen)).getShortcutsAndWidgets());
        }
        if (this.mLauncher.getHotseat() != null) {
            childrenLayouts.add(this.mLauncher.getHotseat().getLayout().getShortcutsAndWidgets());
        }
        return childrenLayouts;
    }

    public View getHomescreenIconByItemId(final long id) {
        return getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                return info != null && info.id == id;
            }
        });
    }

    public View getViewForTag(final Object tag) {
        return getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                return info == tag;
            }
        });
    }

    public LauncherAppWidgetHostView getWidgetForAppWidgetId(final int appWidgetId) {
        return (LauncherAppWidgetHostView) getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                return (info instanceof LauncherAppWidgetInfo) && ((LauncherAppWidgetInfo) info).appWidgetId == appWidgetId;
            }
        });
    }

    public View getFirstMatch(final ItemOperator operator) {
        final View[] value = new View[1];
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if (!operator.evaluate(info, v)) {
                    return false;
                }
                value[0] = v;
                return true;
            }
        });
        return value[0];
    }

    /* access modifiers changed from: package-private */
    public void clearDropTargets() {
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if (!(v instanceof DropTarget)) {
                    return false;
                }
                Workspace.this.mDragController.removeDropTarget((DropTarget) v);
                return false;
            }
        });
    }

    public void removeItemsByMatcher(ItemInfoMatcher matcher) {
        View parent;
        Iterator<CellLayout> it = getWorkspaceAndHotseatCellLayouts().iterator();
        while (it.hasNext()) {
            CellLayout layoutParent = it.next();
            ViewGroup layout = layoutParent.getShortcutsAndWidgets();
            LongArrayMap<View> idToViewMap = new LongArrayMap<>();
            ArrayList<ItemInfo> items = new ArrayList<>();
            for (int j = 0; j < layout.getChildCount(); j++) {
                View view = layout.getChildAt(j);
                if (view.getTag() instanceof ItemInfo) {
                    ItemInfo item = (ItemInfo) view.getTag();
                    items.add(item);
                    idToViewMap.put(item.id, view);
                }
            }
            Iterator<ItemInfo> it2 = matcher.filterItemInfos(items).iterator();
            while (it2.hasNext()) {
                ItemInfo itemToRemove = it2.next();
                View child = (View) idToViewMap.get(itemToRemove.id);
                if (child != null) {
                    layoutParent.removeViewInLayout(child);
                    if (child instanceof DropTarget) {
                        this.mDragController.removeDropTarget((DropTarget) child);
                    }
                } else if (itemToRemove.container >= 0 && (parent = (View) idToViewMap.get(itemToRemove.container)) != null) {
                    FolderInfo folderInfo = (FolderInfo) parent.getTag();
                    folderInfo.prepareAutoUpdate();
                    folderInfo.remove((ShortcutInfo) itemToRemove, false);
                }
            }
        }
        stripEmptyScreens();
    }

    /* access modifiers changed from: package-private */
    public void mapOverItems(boolean recurse, ItemOperator op) {
        ItemOperator itemOperator = op;
        ArrayList<ShortcutAndWidgetContainer> containers = getAllShortcutAndWidgetContainers();
        int containerCount = containers.size();
        for (int containerIdx = 0; containerIdx < containerCount; containerIdx++) {
            ShortcutAndWidgetContainer container = containers.get(containerIdx);
            int itemCount = container.getChildCount();
            for (int itemIdx = 0; itemIdx < itemCount; itemIdx++) {
                View item = container.getChildAt(itemIdx);
                ItemInfo info = (ItemInfo) item.getTag();
                if (recurse && (info instanceof FolderInfo) && (item instanceof FolderIcon)) {
                    ArrayList<View> folderChildren = ((FolderIcon) item).getFolder().getItemsInReadingOrder();
                    int childCount = folderChildren.size();
                    ItemInfo itemInfo = info;
                    int childIdx = 0;
                    while (childIdx < childCount) {
                        View child = folderChildren.get(childIdx);
                        if (!itemOperator.evaluate((ItemInfo) child.getTag(), child)) {
                            childIdx++;
                        } else {
                            return;
                        }
                    }
                    continue;
                } else if (itemOperator.evaluate(info, item)) {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateShortcuts(ArrayList<ShortcutInfo> shortcuts) {
        int total = shortcuts.size();
        final HashSet<ShortcutInfo> updates = new HashSet<>(total);
        final HashSet<Long> folderIds = new HashSet<>();
        for (int i = 0; i < total; i++) {
            ShortcutInfo s = shortcuts.get(i);
            updates.add(s);
            folderIds.add(Long.valueOf(s.container));
        }
        mapOverItems(true, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if ((info instanceof ShortcutInfo) && (v instanceof BubbleTextView) && updates.contains(info)) {
                    ShortcutInfo si = (ShortcutInfo) info;
                    BubbleTextView shortcut = (BubbleTextView) v;
                    Drawable oldIcon = shortcut.getIcon();
                    boolean z = true;
                    if (si.isPromise() == ((oldIcon instanceof PreloadIconDrawable) && ((PreloadIconDrawable) oldIcon).hasNotCompleted())) {
                        z = false;
                    }
                    shortcut.applyFromShortcutInfo(si, z);
                }
                return false;
            }
        });
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if ((info instanceof FolderInfo) && folderIds.contains(Long.valueOf(info.id))) {
                    ((FolderInfo) info).itemsChanged(false);
                }
                return false;
            }
        });
    }

    public void updateIconBadges(final Set<PackageUserKey> updatedBadges) {
        final PackageUserKey packageUserKey = new PackageUserKey((String) null, (UserHandle) null);
        final HashSet<Long> folderIds = new HashSet<>();
        mapOverItems(true, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if (!(info instanceof ShortcutInfo) || !(v instanceof BubbleTextView) || !packageUserKey.updateFromItemInfo(info) || !updatedBadges.contains(packageUserKey)) {
                    return false;
                }
                ((BubbleTextView) v).applyBadgeState(info, true);
                folderIds.add(Long.valueOf(info.container));
                return false;
            }
        });
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if (!(info instanceof FolderInfo) || !folderIds.contains(Long.valueOf(info.id)) || !(v instanceof FolderIcon)) {
                    return false;
                }
                FolderBadgeInfo folderBadgeInfo = new FolderBadgeInfo();
                Iterator<ShortcutInfo> it = ((FolderInfo) info).contents.iterator();
                while (it.hasNext()) {
                    folderBadgeInfo.addBadgeInfo(Workspace.this.mLauncher.getBadgeInfoForItem(it.next()));
                }
                ((FolderIcon) v).setBadgeInfo(folderBadgeInfo);
                return false;
            }
        });
    }

    public void removeAbandonedPromise(String packageName, UserHandle user) {
        HashSet<String> packages = new HashSet<>(1);
        packages.add(packageName);
        ItemInfoMatcher matcher = ItemInfoMatcher.ofPackages(packages, user);
        this.mLauncher.getModelWriter().deleteItemsFromDatabase(matcher);
        removeItemsByMatcher(matcher);
    }

    public void updateRestoreItems(final HashSet<ItemInfo> updates) {
        mapOverItems(true, new ItemOperator() {
            public boolean evaluate(ItemInfo info, View v) {
                if ((info instanceof ShortcutInfo) && (v instanceof BubbleTextView) && updates.contains(info)) {
                    ((BubbleTextView) v).applyPromiseState(false);
                } else if ((v instanceof PendingAppWidgetHostView) && (info instanceof LauncherAppWidgetInfo) && updates.contains(info)) {
                    ((PendingAppWidgetHostView) v).applyState();
                }
                return false;
            }
        });
    }

    public void widgetsRestored(final ArrayList<LauncherAppWidgetInfo> changedInfo) {
        AppWidgetProviderInfo widgetInfo;
        if (!changedInfo.isEmpty()) {
            DeferredWidgetRefresh widgetRefresh = new DeferredWidgetRefresh(changedInfo, this.mLauncher.getAppWidgetHost());
            LauncherAppWidgetInfo item = changedInfo.get(0);
            if (item.hasRestoreFlag(1)) {
                widgetInfo = AppWidgetManagerCompat.getInstance(this.mLauncher).findProvider(item.providerName, item.user);
            } else {
                widgetInfo = AppWidgetManagerCompat.getInstance(this.mLauncher).getLauncherAppWidgetInfo(item.appWidgetId);
            }
            if (widgetInfo != null) {
                widgetRefresh.run();
            } else {
                mapOverItems(false, new ItemOperator() {
                    public boolean evaluate(ItemInfo info, View view) {
                        if (!(view instanceof PendingAppWidgetHostView) || !changedInfo.contains(info)) {
                            return false;
                        }
                        ((LauncherAppWidgetInfo) info).installProgress = 100;
                        ((PendingAppWidgetHostView) view).applyState();
                        return false;
                    }
                });
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void moveToDefaultScreen() {
        if (!workspaceInModalState() && getNextPage() != 0) {
            snapToPage(0);
        }
        View child = getChildAt(0);
        if (child != null) {
            child.requestFocus();
        }
    }

    public int getExpectedHeight() {
        return (getMeasuredHeight() <= 0 || !this.mIsLayoutValid) ? this.mLauncher.getDeviceProfile().heightPx : getMeasuredHeight();
    }

    public int getExpectedWidth() {
        return (getMeasuredWidth() <= 0 || !this.mIsLayoutValid) ? this.mLauncher.getDeviceProfile().widthPx : getMeasuredWidth();
    }

    /* access modifiers changed from: protected */
    public boolean canAnnouncePageDescription() {
        return Float.compare(this.mOverlayTranslation, 0.0f) == 0;
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return getPageDescription(this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage);
    }

    private String getPageDescription(int page) {
        int nScreens = getChildCount();
        int extraScreenId = this.mScreenOrder.indexOf(-201L);
        if (extraScreenId >= 0 && nScreens > 1) {
            if (page == extraScreenId) {
                return getContext().getString(R.string.workspace_new_page);
            }
            nScreens--;
        }
        if (nScreens == 0) {
            return getContext().getString(R.string.all_apps_home_button_label);
        }
        return getContext().getString(R.string.workspace_scroll_format, new Object[]{Integer.valueOf(page + 1), Integer.valueOf(nScreens)});
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        target.gridX = info.cellX;
        target.gridY = info.cellY;
        target.pageIndex = getCurrentPage();
        targetParent.containerType = 1;
        if (info.container == -101) {
            target.rank = info.rank;
            targetParent.containerType = 2;
        } else if (info.container >= 0) {
            targetParent.containerType = 3;
        }
    }

    private class DeferredWidgetRefresh implements Runnable, LauncherAppWidgetHost.ProviderChangedListener {
        private final Handler mHandler = new Handler();
        private final LauncherAppWidgetHost mHost;
        private final ArrayList<LauncherAppWidgetInfo> mInfos;
        private boolean mRefreshPending = true;

        DeferredWidgetRefresh(ArrayList<LauncherAppWidgetInfo> infos, LauncherAppWidgetHost host) {
            this.mInfos = infos;
            this.mHost = host;
            this.mHost.addProviderChangeListener(this);
            this.mHandler.postDelayed(this, 10000);
        }

        public void run() {
            this.mHost.removeProviderChangeListener(this);
            this.mHandler.removeCallbacks(this);
            if (this.mRefreshPending) {
                this.mRefreshPending = false;
                ArrayList<PendingAppWidgetHostView> views = new ArrayList<>(this.mInfos.size());
                Workspace.this.mapOverItems(false, new ItemOperator(views) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean evaluate(ItemInfo itemInfo, View view) {
                        return Workspace.DeferredWidgetRefresh.lambda$run$0(Workspace.DeferredWidgetRefresh.this, this.f$1, itemInfo, view);
                    }
                });
                Iterator<PendingAppWidgetHostView> it = views.iterator();
                while (it.hasNext()) {
                    it.next().reInflate();
                }
            }
        }

        public static /* synthetic */ boolean lambda$run$0(DeferredWidgetRefresh deferredWidgetRefresh, ArrayList views, ItemInfo info, View view) {
            if (!(view instanceof PendingAppWidgetHostView) || !deferredWidgetRefresh.mInfos.contains(info)) {
                return false;
            }
            views.add((PendingAppWidgetHostView) view);
            return false;
        }

        public void notifyWidgetProvidersChanged() {
            run();
        }
    }

    private class StateTransitionListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
        private final LauncherState mToState;

        StateTransitionListener(LauncherState toState) {
            this.mToState = toState;
        }

        public void onAnimationUpdate(ValueAnimator anim) {
            float unused = Workspace.this.mTransitionProgress = anim.getAnimatedFraction();
        }

        public void onAnimationStart(Animator animation) {
            Workspace.this.onStartStateTransition(this.mToState);
        }

        public void onAnimationEnd(Animator animation) {
            Workspace.this.onEndStateTransition();
        }
    }
}
