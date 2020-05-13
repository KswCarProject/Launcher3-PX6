package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.szchoiceway.index.CellLayout;
import com.szchoiceway.index.DragController;
import com.szchoiceway.index.DropTarget;
import com.szchoiceway.index.FolderIcon;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Workspace extends SmoothPagedView implements DropTarget, DragSource, DragScroller, View.OnTouchListener, DragController.DragListener, LauncherTransitionable, ViewGroup.OnHierarchyChangeListener {
    private static final int ADJACENT_SCREEN_DROP_DURATION = 300;
    public static final int ANIMATE_INTO_POSITION_AND_DISAPPEAR = 0;
    public static final int ANIMATE_INTO_POSITION_AND_REMAIN = 1;
    public static final int ANIMATE_INTO_POSITION_AND_RESIZE = 2;
    private static final int BACKGROUND_FADE_OUT_DURATION = 350;
    public static final int CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION = 4;
    private static final int CHILDREN_OUTLINE_FADE_IN_DURATION = 100;
    private static final int CHILDREN_OUTLINE_FADE_OUT_DELAY = 0;
    private static final int CHILDREN_OUTLINE_FADE_OUT_DURATION = 375;
    public static final int COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION = 3;
    private static final int DEFAULT_CELL_COUNT_X = 4;
    private static final int DEFAULT_CELL_COUNT_Y = 4;
    public static final int DRAG_BITMAP_PADDING = 2;
    private static final int DRAG_MODE_ADD_TO_FOLDER = 2;
    private static final int DRAG_MODE_CREATE_FOLDER = 1;
    private static final int DRAG_MODE_NONE = 0;
    private static final int DRAG_MODE_REORDER = 3;
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    private static final int FOLDER_CREATION_TIMEOUT = 0;
    static final float MAX_SWIPE_ANGLE = 1.0471976f;
    private static final int REORDER_TIMEOUT = 250;
    static final float START_DAMPING_TOUCH_SLOP_ANGLE = 0.5235988f;
    private static final String TAG = "Launcher.Workspace";
    static final float TOUCH_SLOP_DAMPING_FACTOR = 4.0f;
    private static final float WALLPAPER_SCREENS_SPAN = 2.0f;
    private static final float WORKSPACE_OVERSCROLL_ROTATION = 24.0f;
    static Rect mLandscapeCellLayoutMetrics = null;
    static Rect mPortraitCellLayoutMetrics = null;
    private boolean mAddToExistingFolderOnDrop;
    boolean mAnimatingViewIntoPlace;
    private Drawable mBackground;
    private float mBackgroundAlpha;
    private ValueAnimator mBackgroundFadeInAnimation;
    private ValueAnimator mBackgroundFadeOutAnimation;
    private final Runnable mBindPages;
    private int mCameraDistance;
    boolean mChildrenLayersEnabled;
    private float mChildrenOutlineAlpha;
    private ObjectAnimator mChildrenOutlineFadeInAnimation;
    private ObjectAnimator mChildrenOutlineFadeOutAnimation;
    private boolean mCreateUserFolderOnDrop;
    private float mCurrentRotationY;
    private float mCurrentScaleX;
    private float mCurrentScaleY;
    private float mCurrentTranslationX;
    private float mCurrentTranslationY;
    private int mDefaultPage;
    /* access modifiers changed from: private */
    public Runnable mDelayedResizeRunnable;
    private Runnable mDelayedSnapToPageRunnable;
    /* access modifiers changed from: private */
    public Point mDisplaySize;
    /* access modifiers changed from: private */
    public DragController mDragController;
    private DropTarget.DragEnforcer mDragEnforcer;
    /* access modifiers changed from: private */
    public FolderIcon.FolderRingAnimator mDragFolderRingAnimator;
    private CellLayout.CellInfo mDragInfo;
    private int mDragMode;
    /* access modifiers changed from: private */
    public Bitmap mDragOutline;
    private FolderIcon mDragOverFolderIcon;
    private int mDragOverX;
    private int mDragOverY;
    private CellLayout mDragOverlappingLayout;
    /* access modifiers changed from: private */
    public CellLayout mDragTargetLayout;
    /* access modifiers changed from: private */
    public float[] mDragViewVisualCenter;
    boolean mDrawBackground;
    private CellLayout mDropToLayout;
    private final Alarm mFolderCreationAlarm;
    private IconCache mIconCache;
    private boolean mInScrollArea;
    boolean mIsDragOccuring;
    private boolean mIsStaticWallpaper;
    private boolean mIsSwitchingState;
    /* access modifiers changed from: private */
    public int mLastReorderX;
    /* access modifiers changed from: private */
    public int mLastReorderY;
    /* access modifiers changed from: private */
    public Launcher mLauncher;
    private float mMaxDistanceForFolderCreation;
    private float[] mNewAlphas;
    /* access modifiers changed from: private */
    public float[] mNewBackgroundAlphas;
    private float[] mNewRotationYs;
    private float[] mNewScaleXs;
    private float[] mNewScaleYs;
    private float[] mNewTranslationXs;
    private float[] mNewTranslationYs;
    private float[] mOldAlphas;
    /* access modifiers changed from: private */
    public float[] mOldBackgroundAlphas;
    private float[] mOldScaleXs;
    private float[] mOldScaleYs;
    private float[] mOldTranslationXs;
    private float[] mOldTranslationYs;
    private int mOriginalPageSpacing;
    private final HolographicOutlineHelper mOutlineHelper;
    private float mOverscrollFade;
    private boolean mOverscrollTransformsSet;
    private final Alarm mReorderAlarm;
    private final ArrayList<Integer> mRestoredPages;
    private float mSavedRotationY;
    private int mSavedScrollX;
    private SparseArray<Parcelable> mSavedStates;
    private float mSavedTranslationX;
    private SpringLoadedDragController mSpringLoadedDragController;
    private int mSpringLoadedPageSpacing;
    private float mSpringLoadedShrinkFactor;
    private State mState;
    /* access modifiers changed from: private */
    public int[] mTargetCell;
    private int[] mTempCell;
    private float[] mTempCellLayoutCenterCoordinates;
    private float[] mTempDragBottomRightCoordinates;
    private float[] mTempDragCoordinates;
    private int[] mTempEstimate;
    private Matrix mTempInverseMatrix;
    private final Rect mTempRect;
    private int[] mTempVisiblePagesRange;
    private final int[] mTempXY;
    private float mTransitionProgress;
    boolean mUpdateWallpaperOffsetImmediately;
    int mWallpaperHeight;
    /* access modifiers changed from: private */
    public final WallpaperManager mWallpaperManager;
    WallpaperOffsetInterpolator mWallpaperOffset;
    private float mWallpaperScrollRatio;
    private int mWallpaperTravelWidth;
    int mWallpaperWidth;
    private IBinder mWindowToken;
    private boolean mWorkspaceFadeInAdjacentScreens;
    private float mXDown;
    private float mYDown;
    private final ZoomInInterpolator mZoomInInterpolator;

    enum State {
        NORMAL,
        SPRING_LOADED,
        SMALL
    }

    enum WallpaperVerticalOffset {
        TOP,
        MIDDLE,
        BOTTOM
    }

    public Workspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Workspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChildrenOutlineAlpha = 0.0f;
        this.mDrawBackground = true;
        this.mBackgroundAlpha = 0.0f;
        this.mWallpaperScrollRatio = 1.0f;
        this.mTargetCell = new int[2];
        this.mDragOverX = -1;
        this.mDragOverY = -1;
        this.mDragTargetLayout = null;
        this.mDragOverlappingLayout = null;
        this.mDropToLayout = null;
        this.mTempCell = new int[2];
        this.mTempEstimate = new int[2];
        this.mDragViewVisualCenter = new float[2];
        this.mTempDragCoordinates = new float[2];
        this.mTempCellLayoutCenterCoordinates = new float[2];
        this.mTempDragBottomRightCoordinates = new float[2];
        this.mTempInverseMatrix = new Matrix();
        this.mState = State.NORMAL;
        this.mIsSwitchingState = false;
        this.mAnimatingViewIntoPlace = false;
        this.mIsDragOccuring = false;
        this.mChildrenLayersEnabled = true;
        this.mInScrollArea = false;
        this.mOutlineHelper = new HolographicOutlineHelper();
        this.mDragOutline = null;
        this.mTempRect = new Rect();
        this.mTempXY = new int[2];
        this.mTempVisiblePagesRange = new int[2];
        this.mOverscrollFade = 0.0f;
        this.mUpdateWallpaperOffsetImmediately = false;
        this.mDisplaySize = new Point();
        this.mFolderCreationAlarm = new Alarm();
        this.mReorderAlarm = new Alarm();
        this.mDragFolderRingAnimator = null;
        this.mDragOverFolderIcon = null;
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDragMode = 0;
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
        this.mRestoredPages = new ArrayList<>();
        this.mBindPages = new Runnable() {
            public void run() {
                Workspace.this.mLauncher.getModel().bindRemainingSynchronousPages();
            }
        };
        this.mZoomInInterpolator = new ZoomInInterpolator();
        this.mContentIsRefreshable = false;
        this.mOriginalPageSpacing = this.mPageSpacing;
        this.mDragEnforcer = new DropTarget.DragEnforcer(context);
        setDataIsReady();
        this.mLauncher = (Launcher) context;
        Resources res = getResources();
        this.mWorkspaceFadeInAdjacentScreens = res.getBoolean(R.bool.config_workspaceFadeAdjacentScreens);
        this.mFadeInAdjacentScreens = false;
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        int cellCountX = 4;
        int cellCountY = 4;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Workspace, defStyle, 0);
        if (LauncherApplication.isScreenLarge()) {
            float actionBarHeight = context.obtainStyledAttributes(new int[]{16843499}).getDimension(0, 0.0f);
            Point minDims = new Point();
            this.mLauncher.getWindowManager().getDefaultDisplay().getCurrentSizeRange(minDims, new Point());
            cellCountX = 1;
            while (CellLayout.widthInPortrait(res, cellCountX + 1) <= minDims.x) {
                cellCountX++;
            }
            cellCountY = 1;
            while (((float) CellLayout.heightInLandscape(res, cellCountY + 1)) + actionBarHeight <= ((float) minDims.y)) {
                cellCountY++;
            }
        }
        this.mSpringLoadedShrinkFactor = ((float) res.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage)) / 100.0f;
        this.mSpringLoadedPageSpacing = res.getDimensionPixelSize(R.dimen.workspace_spring_loaded_page_spacing);
        this.mCameraDistance = res.getInteger(R.integer.config_cameraDistance);
        int cellCountX2 = a.getInt(1, cellCountX);
        int cellCountY2 = a.getInt(2, cellCountY);
        this.mDefaultPage = a.getInt(0, 1);
        a.recycle();
        setOnHierarchyChangeListener(this);
        LauncherModel.updateWorkspaceLayoutCells(cellCountX2, cellCountY2);
        setHapticFeedbackEnabled(false);
        initWorkspace();
        setMotionEventSplittingEnabled(true);
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    public int[] estimateItemSize(int hSpan, int vSpan, ItemInfo itemInfo, boolean springLoaded) {
        int[] size = new int[2];
        if (getChildCount() > 0) {
            Rect r = estimateItemPosition((CellLayout) this.mLauncher.getWorkspace().getChildAt(0), itemInfo, 0, 0, hSpan, vSpan);
            size[0] = r.width();
            size[1] = r.height();
            if (springLoaded) {
                size[0] = (int) (((float) size[0]) * this.mSpringLoadedShrinkFactor);
                size[1] = (int) (((float) size[1]) * this.mSpringLoadedShrinkFactor);
            }
        } else {
            size[0] = Integer.MAX_VALUE;
            size[1] = Integer.MAX_VALUE;
        }
        return size;
    }

    public Rect estimateItemPosition(CellLayout cl, ItemInfo pendingInfo, int hCell, int vCell, int hSpan, int vSpan) {
        Rect r = new Rect();
        cl.cellToRect(hCell, vCell, hSpan, vSpan, r);
        return r;
    }

    public void onDragStart(DragSource source, Object info, int dragAction) {
        this.mIsDragOccuring = true;
        updateChildrenLayersEnabled(false);
        this.mLauncher.lockScreenOrientation();
        setChildrenBackgroundAlphaMultipliers(1.0f);
        InstallShortcutReceiver.enableInstallQueue();
        UninstallShortcutReceiver.enableUninstallQueue();
    }

    public void onDragEnd() {
        this.mIsDragOccuring = false;
        updateChildrenLayersEnabled(false);
        this.mLauncher.unlockScreenOrientation(false);
        InstallShortcutReceiver.disableAndFlushInstallQueue(getContext());
        UninstallShortcutReceiver.disableAndFlushUninstallQueue(getContext());
        Log.i(TAG, "onDragEnd");
    }

    /* access modifiers changed from: protected */
    public void initWorkspace() {
        Context context = getContext();
        this.mCurrentPage = this.mDefaultPage;
        Launcher.setScreen(this.mCurrentPage);
        this.mIconCache = ((LauncherApplication) context.getApplicationContext()).getIconCache();
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
        setChildrenDrawnWithCacheEnabled(true);
        Resources res = getResources();
        try {
            this.mBackground = res.getDrawable(R.drawable.apps_customize_bg);
        } catch (Resources.NotFoundException e) {
        }
        this.mWallpaperOffset = new WallpaperOffsetInterpolator();
        this.mLauncher.getWindowManager().getDefaultDisplay().getSize(this.mDisplaySize);
        this.mWallpaperTravelWidth = (int) (((float) this.mDisplaySize.x) * wallpaperTravelToScreenWidthRatio(this.mDisplaySize.x, this.mDisplaySize.y));
        this.mMaxDistanceForFolderCreation = 0.55f * ((float) res.getDimensionPixelSize(R.dimen.app_icon_size));
        this.mFlingThresholdVelocity = (int) (500.0f * this.mDensity);
    }

    /* access modifiers changed from: protected */
    public int getScrollMode() {
        return 1;
    }

    public void onChildViewAdded(View parent, View child) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        CellLayout cl = (CellLayout) child;
        cl.setOnInterceptTouchListener(this);
        cl.setClickable(true);
        cl.setContentDescription(getContext().getString(R.string.workspace_description_format, new Object[]{Integer.valueOf(getChildCount())}));
    }

    public void onChildViewRemoved(View parent, View child) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldDrawChild(View child) {
        CellLayout cl = (CellLayout) child;
        return super.shouldDrawChild(child) && (cl.getShortcutsAndWidgets().getAlpha() > 0.0f || cl.getBackgroundAlpha() > 0.0f);
    }

    /* access modifiers changed from: package-private */
    public Folder getOpenFolder() {
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        int count = dragLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = dragLayer.getChildAt(i);
            if (child instanceof Folder) {
                Folder folder = (Folder) child;
                if (folder.getInfo().opened) {
                    return folder;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean isTouchActive() {
        return this.mTouchState != 0;
    }

    /* access modifiers changed from: package-private */
    public void addInScreen(View child, long container, int screen, int x, int y, int spanX, int spanY) {
        addInScreen(child, container, screen, x, y, spanX, spanY, false);
    }

    /* access modifiers changed from: package-private */
    public void addInScreen(View child, long container, int screen, int x, int y, int spanX, int spanY, boolean insert) {
        CellLayout layout;
        CellLayout.LayoutParams lp;
        if (container != -100 || (screen >= 0 && screen < getChildCount())) {
            if (container == -101) {
                layout = this.mLauncher.getHotseat().getLayout();
                child.setOnKeyListener((View.OnKeyListener) null);
                child.setScaleX(WALLPAPER_SCREENS_SPAN);
                child.setScaleY(WALLPAPER_SCREENS_SPAN);
                if (child instanceof FolderIcon) {
                }
                if (screen < 0) {
                    screen = this.mLauncher.getHotseat().getOrderInHotseat(x, y);
                } else {
                    x = this.mLauncher.getHotseat().getCellXFromOrder(screen);
                    y = this.mLauncher.getHotseat().getCellYFromOrder(screen);
                }
            } else {
                child.setScaleX(1.0f);
                child.setScaleY(1.0f);
                if (child instanceof FolderIcon) {
                    ((FolderIcon) child).setTextVisible(true);
                }
                layout = (CellLayout) getChildAt(screen);
                child.setOnKeyListener(new IconKeyEventListener());
            }
            ViewGroup.LayoutParams genericLp = child.getLayoutParams();
            if (genericLp == null || !(genericLp instanceof CellLayout.LayoutParams)) {
                lp = new CellLayout.LayoutParams(x, y, spanX, spanY);
            } else {
                lp = (CellLayout.LayoutParams) genericLp;
                lp.cellX = x;
                lp.cellY = y;
                lp.cellHSpan = spanX;
                lp.cellVSpan = spanY;
            }
            if (spanX < 0 && spanY < 0) {
                lp.isLockedToGrid = false;
            }
            if (!layout.addViewToCellLayout(child, insert ? 0 : -1, LauncherModel.getCellLayoutChildId(container, screen, x, y, spanX, spanY), lp, !(child instanceof Folder))) {
                Log.w(TAG, "Failed to add to item at (" + lp.cellX + "," + lp.cellY + ") to CellLayout");
            }
            if (!(child instanceof Folder)) {
                child.setHapticFeedbackEnabled(false);
                child.setOnLongClickListener(this.mLongClickListener);
            }
            if (child instanceof DropTarget) {
                this.mDragController.addDropTarget((DropTarget) child);
                return;
            }
            return;
        }
        Log.e(TAG, "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child");
    }

    private boolean hitsPage(int index, float x, float y) {
        View page = getChildAt(index);
        if (page == null) {
            return false;
        }
        float[] localXY = {x, y};
        mapPointFromSelfToChild(page, localXY);
        if (localXY[0] < 0.0f || localXY[0] >= ((float) page.getWidth()) || localXY[1] < 0.0f || localXY[1] >= ((float) page.getHeight())) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean hitsPreviousPage(float x, float y) {
        return LauncherApplication.isScreenLarge() && hitsPage((this.mNextPage == -1 ? this.mCurrentPage : this.mNextPage) + -1, x, y);
    }

    /* access modifiers changed from: protected */
    public boolean hitsNextPage(float x, float y) {
        return LauncherApplication.isScreenLarge() && hitsPage((this.mNextPage == -1 ? this.mCurrentPage : this.mNextPage) + 1, x, y);
    }

    public boolean onTouch(View v, MotionEvent event) {
        return isSmall() || !isFinishedSwitchingState();
    }

    public boolean isSwitchingState() {
        return this.mIsSwitchingState;
    }

    public boolean isFinishedSwitchingState() {
        return !this.mIsSwitchingState || this.mTransitionProgress > 0.5f;
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int visibility) {
        this.mLauncher.onWindowVisibilityChanged(visibility);
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (isSmall() || !isFinishedSwitchingState()) {
            return false;
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & 255) {
            case 0:
                this.mXDown = ev.getX();
                this.mYDown = ev.getY();
                break;
            case 1:
            case 6:
                if (this.mTouchState == 0 && !((CellLayout) getChildAt(this.mCurrentPage)).lastDownOnOccupiedCell()) {
                    onWallpaperTap(ev);
                    break;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public void reinflateWidgetsIfNecessary() {
        int clCount = getChildCount();
        for (int i = 0; i < clCount; i++) {
            CellLayout cl = (CellLayout) getChildAt(i);
            ShortcutAndWidgetContainer swc = cl.getShortcutsAndWidgets();
            int itemCount = swc.getChildCount();
            for (int j = 0; j < itemCount; j++) {
                View v = swc.getChildAt(j);
                if (v.getTag() instanceof LauncherAppWidgetInfo) {
                    LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) v.getTag();
                    LauncherAppWidgetHostView lahv = (LauncherAppWidgetHostView) info.hostView;
                    if (lahv != null && lahv.orientationChangedSincedInflation()) {
                        this.mLauncher.removeAppWidget(info);
                        cl.removeView(lahv);
                        this.mLauncher.bindAppWidget(info);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev) {
        if (!isSmall() && isFinishedSwitchingState()) {
            float deltaX = Math.abs(ev.getX() - this.mXDown);
            float deltaY = Math.abs(ev.getY() - this.mYDown);
            if (Float.compare(deltaX, 0.0f) != 0) {
                float theta = (float) Math.atan((double) (deltaY / deltaX));
                if (deltaX > ((float) this.mTouchSlop) || deltaY > ((float) this.mTouchSlop)) {
                    cancelCurrentPageLongPress();
                }
                if (theta > MAX_SWIPE_ANGLE) {
                    return;
                }
                if (theta > START_DAMPING_TOUCH_SLOP_ANGLE) {
                    super.determineScrollingStart(ev, 1.0f + (TOUCH_SLOP_DAMPING_FACTOR * ((float) Math.sqrt((double) ((theta - START_DAMPING_TOUCH_SLOP_ANGLE) / START_DAMPING_TOUCH_SLOP_ANGLE)))));
                } else {
                    super.determineScrollingStart(ev);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPageBeginMoving() {
        boolean z;
        super.onPageBeginMoving();
        if (isHardwareAccelerated()) {
            updateChildrenLayersEnabled(false);
        } else if (this.mNextPage != -1) {
            enableChildrenCache(this.mCurrentPage, this.mNextPage);
        } else {
            enableChildrenCache(this.mCurrentPage - 1, this.mCurrentPage + 1);
        }
        if (LauncherApplication.isScreenLarge()) {
            showOutlines();
            if (this.mWallpaperManager.getWallpaperInfo() == null) {
                z = true;
            } else {
                z = false;
            }
            this.mIsStaticWallpaper = z;
        }
        if (!this.mWorkspaceFadeInAdjacentScreens) {
            for (int i = 0; i < getChildCount(); i++) {
                ((CellLayout) getPageAt(i)).setShortcutAndWidgetAlpha(1.0f);
            }
        }
        showScrollingIndicator(false);
    }

    /* access modifiers changed from: protected */
    public void onPageEndMoving() {
        super.onPageEndMoving();
        if (isHardwareAccelerated()) {
            updateChildrenLayersEnabled(false);
        } else {
            clearChildrenCache();
        }
        if (!this.mDragController.isDragging()) {
            if (LauncherApplication.isScreenLarge()) {
                hideOutlines();
            }
            if (!this.mDragController.isDragging()) {
                hideScrollingIndicator(false);
            }
        } else if (isSmall()) {
            this.mDragController.forceTouchMove();
        }
        if (this.mDelayedResizeRunnable != null) {
            this.mDelayedResizeRunnable.run();
            this.mDelayedResizeRunnable = null;
        }
        if (this.mDelayedSnapToPageRunnable != null) {
            this.mDelayedSnapToPageRunnable.run();
            this.mDelayedSnapToPageRunnable = null;
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener() {
        super.notifyPageSwitchListener();
        Launcher.setScreen(this.mCurrentPage);
    }

    private float wallpaperTravelToScreenWidthRatio(int width, int height) {
        return (0.30769226f * (((float) width) / ((float) height))) + 1.0076923f;
    }

    private int getScrollRange() {
        return getChildOffset(getChildCount() - 1) - getChildOffset(0);
    }

    /* access modifiers changed from: protected */
    public void setWallpaperDimension() {
        Point minDims = new Point();
        Point maxDims = new Point();
        this.mLauncher.getWindowManager().getDefaultDisplay().getCurrentSizeRange(minDims, maxDims);
        int maxDim = Math.max(maxDims.x, maxDims.y);
        int minDim = Math.min(minDims.x, minDims.y);
        if (LauncherApplication.isScreenLarge()) {
            this.mWallpaperWidth = (int) (((float) maxDim) * wallpaperTravelToScreenWidthRatio(maxDim, minDim));
            this.mWallpaperHeight = maxDim;
        } else {
            this.mWallpaperWidth = Math.max((int) (((float) minDim) * WALLPAPER_SCREENS_SPAN), maxDim);
            this.mWallpaperHeight = maxDim;
        }
        new Thread("setWallpaperDimension") {
            public void run() {
                Workspace.this.mWallpaperManager.suggestDesiredDimensions(Workspace.this.mWallpaperWidth, Workspace.this.mWallpaperHeight);
            }
        }.start();
    }

    private float wallpaperOffsetForCurrentScroll() {
        this.mWallpaperManager.setWallpaperOffsetSteps(1.0f / ((float) (getChildCount() - 1)), 1.0f);
        float layoutScale = this.mLayoutScale;
        this.mLayoutScale = 1.0f;
        int scrollRange = getScrollRange();
        this.mLayoutScale = layoutScale;
        float scrollProgress = (((float) Math.max(0, Math.min(getScrollX(), this.mMaxScrollX))) * this.mWallpaperScrollRatio) / ((float) scrollRange);
        if (!LauncherApplication.isScreenLarge() || !this.mIsStaticWallpaper) {
            return scrollProgress;
        }
        int wallpaperTravelWidth = Math.min(this.mWallpaperTravelWidth, this.mWallpaperWidth);
        return ((((float) wallpaperTravelWidth) * scrollProgress) + ((float) ((this.mWallpaperWidth - wallpaperTravelWidth) / 2))) / ((float) this.mWallpaperWidth);
    }

    private void syncWallpaperOffsetWithScroll() {
        if (isHardwareAccelerated()) {
            this.mWallpaperOffset.setFinalX(wallpaperOffsetForCurrentScroll());
        }
    }

    public void updateWallpaperOffsetImmediately() {
        this.mUpdateWallpaperOffsetImmediately = true;
    }

    private void updateWallpaperOffsets() {
        boolean keepUpdating;
        boolean updateNow;
        if (this.mUpdateWallpaperOffsetImmediately) {
            updateNow = true;
            keepUpdating = false;
            this.mWallpaperOffset.jumpToFinal();
            this.mUpdateWallpaperOffsetImmediately = false;
        } else {
            keepUpdating = this.mWallpaperOffset.computeScrollOffset();
            updateNow = keepUpdating;
        }
        if (updateNow && this.mWindowToken != null) {
            this.mWallpaperManager.setWallpaperOffsets(this.mWindowToken, this.mWallpaperOffset.getCurrX(), this.mWallpaperOffset.getCurrY());
        }
        if (keepUpdating) {
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void updateCurrentPageScroll() {
        super.updateCurrentPageScroll();
        computeWallpaperScrollRatio(this.mCurrentPage);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage) {
        super.snapToPage(whichPage);
        computeWallpaperScrollRatio(whichPage);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage, int duration) {
        super.snapToPage(whichPage, duration);
        computeWallpaperScrollRatio(whichPage);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int whichPage, Runnable r) {
        if (this.mDelayedSnapToPageRunnable != null) {
            this.mDelayedSnapToPageRunnable.run();
        }
        this.mDelayedSnapToPageRunnable = r;
        snapToPage(whichPage, 950);
    }

    private void computeWallpaperScrollRatio(int page) {
        float layoutScale = this.mLayoutScale;
        int scaled = getChildOffset(page) - getRelativeChildOffset(page);
        this.mLayoutScale = 1.0f;
        float unscaled = (float) (getChildOffset(page) - getRelativeChildOffset(page));
        this.mLayoutScale = layoutScale;
        if (scaled > 0) {
            this.mWallpaperScrollRatio = (1.0f * unscaled) / ((float) scaled);
        } else {
            this.mWallpaperScrollRatio = 1.0f;
        }
    }

    class WallpaperOffsetInterpolator {
        float mFinalHorizontalWallpaperOffset = 0.0f;
        float mFinalVerticalWallpaperOffset = 0.5f;
        float mHorizontalCatchupConstant = 0.35f;
        float mHorizontalWallpaperOffset = 0.0f;
        boolean mIsMovingFast;
        long mLastWallpaperOffsetUpdateTime;
        boolean mOverrideHorizontalCatchupConstant;
        float mVerticalCatchupConstant = 0.35f;
        float mVerticalWallpaperOffset = 0.5f;

        public WallpaperOffsetInterpolator() {
        }

        public void setOverrideHorizontalCatchupConstant(boolean override) {
            this.mOverrideHorizontalCatchupConstant = override;
        }

        public void setHorizontalCatchupConstant(float f) {
            this.mHorizontalCatchupConstant = f;
        }

        public void setVerticalCatchupConstant(float f) {
            this.mVerticalCatchupConstant = f;
        }

        public boolean computeScrollOffset() {
            float fractionToCatchUpIn1MsHorizontal;
            if (Float.compare(this.mHorizontalWallpaperOffset, this.mFinalHorizontalWallpaperOffset) == 0 && Float.compare(this.mVerticalWallpaperOffset, this.mFinalVerticalWallpaperOffset) == 0) {
                this.mIsMovingFast = false;
                return false;
            }
            boolean isLandscape = Workspace.this.mDisplaySize.x > Workspace.this.mDisplaySize.y;
            long timeSinceLastUpdate = Math.max(1, Math.min(33, System.currentTimeMillis() - this.mLastWallpaperOffsetUpdateTime));
            float xdiff = Math.abs(this.mFinalHorizontalWallpaperOffset - this.mHorizontalWallpaperOffset);
            if (!this.mIsMovingFast && ((double) xdiff) > 0.07d) {
                this.mIsMovingFast = true;
            }
            if (this.mOverrideHorizontalCatchupConstant) {
                fractionToCatchUpIn1MsHorizontal = this.mHorizontalCatchupConstant;
            } else if (this.mIsMovingFast) {
                fractionToCatchUpIn1MsHorizontal = isLandscape ? 0.5f : 0.75f;
            } else {
                fractionToCatchUpIn1MsHorizontal = isLandscape ? 0.27f : 0.5f;
            }
            float fractionToCatchUpIn1MsHorizontal2 = fractionToCatchUpIn1MsHorizontal / 33.0f;
            float fractionToCatchUpIn1MsVertical = this.mVerticalCatchupConstant / 33.0f;
            float hOffsetDelta = this.mFinalHorizontalWallpaperOffset - this.mHorizontalWallpaperOffset;
            float vOffsetDelta = this.mFinalVerticalWallpaperOffset - this.mVerticalWallpaperOffset;
            boolean jumpToFinalValue = Math.abs(hOffsetDelta) < 1.0E-5f && Math.abs(vOffsetDelta) < 1.0E-5f;
            if (!LauncherApplication.isScreenLarge() || jumpToFinalValue) {
                this.mHorizontalWallpaperOffset = this.mFinalHorizontalWallpaperOffset;
                this.mVerticalWallpaperOffset = this.mFinalVerticalWallpaperOffset;
            } else {
                float percentToCatchUpVertical = Math.min(1.0f, ((float) timeSinceLastUpdate) * fractionToCatchUpIn1MsVertical);
                this.mHorizontalWallpaperOffset += Math.min(1.0f, ((float) timeSinceLastUpdate) * fractionToCatchUpIn1MsHorizontal2) * hOffsetDelta;
                this.mVerticalWallpaperOffset += percentToCatchUpVertical * vOffsetDelta;
            }
            this.mLastWallpaperOffsetUpdateTime = System.currentTimeMillis();
            return true;
        }

        public float getCurrX() {
            return this.mHorizontalWallpaperOffset;
        }

        public float getFinalX() {
            return this.mFinalHorizontalWallpaperOffset;
        }

        public float getCurrY() {
            return this.mVerticalWallpaperOffset;
        }

        public float getFinalY() {
            return this.mFinalVerticalWallpaperOffset;
        }

        public void setFinalX(float x) {
            this.mFinalHorizontalWallpaperOffset = Math.max(0.0f, Math.min(x, 1.0f));
        }

        public void setFinalY(float y) {
            this.mFinalVerticalWallpaperOffset = Math.max(0.0f, Math.min(y, 1.0f));
        }

        public void jumpToFinal() {
            this.mHorizontalWallpaperOffset = this.mFinalHorizontalWallpaperOffset;
            this.mVerticalWallpaperOffset = this.mFinalVerticalWallpaperOffset;
        }
    }

    public void computeScroll() {
        super.computeScroll();
        syncWallpaperOffsetWithScroll();
    }

    /* access modifiers changed from: package-private */
    public void showOutlines() {
        if (!isSmall() && !this.mIsSwitchingState) {
            if (this.mChildrenOutlineFadeOutAnimation != null) {
                this.mChildrenOutlineFadeOutAnimation.cancel();
            }
            if (this.mChildrenOutlineFadeInAnimation != null) {
                this.mChildrenOutlineFadeInAnimation.cancel();
            }
            this.mChildrenOutlineFadeInAnimation = LauncherAnimUtils.ofFloat(this, "childrenOutlineAlpha", 1.0f);
            this.mChildrenOutlineFadeInAnimation.setDuration(100);
            this.mChildrenOutlineFadeInAnimation.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void hideOutlines() {
        if (!isSmall() && !this.mIsSwitchingState) {
            if (this.mChildrenOutlineFadeInAnimation != null) {
                this.mChildrenOutlineFadeInAnimation.cancel();
            }
            if (this.mChildrenOutlineFadeOutAnimation != null) {
                this.mChildrenOutlineFadeOutAnimation.cancel();
            }
            this.mChildrenOutlineFadeOutAnimation = LauncherAnimUtils.ofFloat(this, "childrenOutlineAlpha", 0.0f);
            this.mChildrenOutlineFadeOutAnimation.setDuration(375);
            this.mChildrenOutlineFadeOutAnimation.setStartDelay(0);
            this.mChildrenOutlineFadeOutAnimation.start();
        }
    }

    public void showOutlinesTemporarily() {
        if (!this.mIsPageMoving && !isTouchActive()) {
            snapToPage(this.mCurrentPage);
        }
    }

    public void setChildrenOutlineAlpha(float alpha) {
        this.mChildrenOutlineAlpha = alpha;
        for (int i = 0; i < getChildCount(); i++) {
            ((CellLayout) getChildAt(i)).setBackgroundAlpha(alpha);
        }
    }

    public float getChildrenOutlineAlpha() {
        return this.mChildrenOutlineAlpha;
    }

    /* access modifiers changed from: package-private */
    public void disableBackground() {
        this.mDrawBackground = false;
    }

    /* access modifiers changed from: package-private */
    public void enableBackground() {
        this.mDrawBackground = true;
    }

    private void animateBackgroundGradient(float finalAlpha, boolean animated) {
        if (this.mBackground != null) {
            if (this.mBackgroundFadeInAnimation != null) {
                this.mBackgroundFadeInAnimation.cancel();
                this.mBackgroundFadeInAnimation = null;
            }
            if (this.mBackgroundFadeOutAnimation != null) {
                this.mBackgroundFadeOutAnimation.cancel();
                this.mBackgroundFadeOutAnimation = null;
            }
            float startAlpha = getBackgroundAlpha();
            if (finalAlpha == startAlpha) {
                return;
            }
            if (animated) {
                this.mBackgroundFadeOutAnimation = LauncherAnimUtils.ofFloat(this, startAlpha, finalAlpha);
                this.mBackgroundFadeOutAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Workspace.this.setBackgroundAlpha(((Float) animation.getAnimatedValue()).floatValue());
                    }
                });
                this.mBackgroundFadeOutAnimation.setInterpolator(new DecelerateInterpolator(1.5f));
                this.mBackgroundFadeOutAnimation.setDuration(350);
                this.mBackgroundFadeOutAnimation.start();
                return;
            }
            setBackgroundAlpha(finalAlpha);
        }
    }

    public void setBackgroundAlpha(float alpha) {
        if (alpha != this.mBackgroundAlpha) {
            this.mBackgroundAlpha = alpha;
            invalidate();
        }
    }

    public float getBackgroundAlpha() {
        return this.mBackgroundAlpha;
    }

    /* access modifiers changed from: package-private */
    public float backgroundAlphaInterpolator(float r) {
        if (r < 0.1f) {
            return 0.0f;
        }
        if (r > 0.4f) {
            return 1.0f;
        }
        return (r - 0.1f) / (0.4f - 0.1f);
    }

    private void updatePageAlphaValues(int screenCenter) {
        boolean isInOverscroll = this.mOverScrollX < 0 || this.mOverScrollX > this.mMaxScrollX;
        if (this.mWorkspaceFadeInAdjacentScreens && this.mState == State.NORMAL && !this.mIsSwitchingState && !isInOverscroll) {
            for (int i = 0; i < getChildCount(); i++) {
                CellLayout child = (CellLayout) getChildAt(i);
                if (child != null) {
                    float scrollProgress = getScrollProgress(screenCenter, child, i);
                    child.getShortcutsAndWidgets().setAlpha(1.0f - Math.abs(scrollProgress));
                    if (!this.mIsDragOccuring) {
                        child.setBackgroundAlphaMultiplier(backgroundAlphaInterpolator(Math.abs(scrollProgress)));
                    } else {
                        child.setBackgroundAlphaMultiplier(1.0f);
                    }
                }
            }
        }
    }

    private void setChildrenBackgroundAlphaMultipliers(float a) {
        for (int i = 0; i < getChildCount(); i++) {
            ((CellLayout) getChildAt(i)).setBackgroundAlphaMultiplier(a);
        }
    }

    /* access modifiers changed from: protected */
    public void screenScrolled(int screenCenter) {
        int index;
        int index2;
        float pivotX;
        boolean isLeftPage;
        boolean isRtl = isLayoutRtl();
        super.screenScrolled(screenCenter);
        updatePageAlphaValues(screenCenter);
        enableHwLayersOnVisiblePages();
        if (this.mOverScrollX < 0 || this.mOverScrollX > this.mMaxScrollX) {
            int upperIndex = getChildCount() - 1;
            if (isRtl) {
                index2 = this.mOverScrollX < 0 ? upperIndex : 0;
                pivotX = index2 == 0 ? 0.25f : 0.75f;
            } else {
                if (this.mOverScrollX < 0) {
                    index = 0;
                } else {
                    index = upperIndex;
                }
                pivotX = index2 == 0 ? 0.75f : 0.25f;
            }
            CellLayout cl = (CellLayout) getChildAt(index2);
            float scrollProgress = getScrollProgress(screenCenter, cl, index2);
            if (isRtl) {
                isLeftPage = index2 > 0;
            } else {
                isLeftPage = index2 == 0;
            }
            cl.setOverScrollAmount(Math.abs(scrollProgress), isLeftPage);
            cl.setRotationY(-24.0f * scrollProgress);
            if (LauncherApplication.SetUIType() != 5) {
                setFadeForOverScroll(Math.abs(scrollProgress));
            }
            if (!this.mOverscrollTransformsSet) {
                this.mOverscrollTransformsSet = true;
                cl.setCameraDistance(this.mDensity * ((float) this.mCameraDistance));
                cl.setPivotX(((float) cl.getMeasuredWidth()) * pivotX);
                cl.setPivotY(((float) cl.getMeasuredHeight()) * 0.5f);
                cl.setOverscrollTransformsDirty(true);
                return;
            }
            return;
        }
        if (this.mOverscrollFade != 0.0f) {
            setFadeForOverScroll(0.0f);
        }
        if (this.mOverscrollTransformsSet) {
            this.mOverscrollTransformsSet = false;
            ((CellLayout) getChildAt(0)).resetOverscrollTransforms();
            ((CellLayout) getChildAt(getChildCount() - 1)).resetOverscrollTransforms();
        }
    }

    /* access modifiers changed from: protected */
    public void overScroll(float amount) {
        acceleratedOverScroll(amount);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWindowToken = getWindowToken();
        computeScroll();
        this.mDragController.setWindowToken(this.mWindowToken);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWindowToken = null;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < getChildCount()) {
            this.mUpdateWallpaperOffsetImmediately = true;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        updateWallpaperOffsets();
        if (this.mBackground != null && this.mBackgroundAlpha > 0.0f && this.mDrawBackground) {
            this.mBackground.setAlpha((int) (this.mBackgroundAlpha * 255.0f));
            this.mBackground.setBounds(getScrollX(), 0, getScrollX() + getMeasuredWidth(), getMeasuredHeight());
            this.mBackground.draw(canvas);
        }
        super.onDraw(canvas);
        post(this.mBindPages);
    }

    /* access modifiers changed from: package-private */
    public boolean isDrawingBackgroundGradient() {
        return this.mBackground != null && this.mBackgroundAlpha > 0.0f && this.mDrawBackground;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (this.mLauncher.isAllAppsVisible()) {
            return false;
        }
        Folder openFolder = getOpenFolder();
        if (openFolder != null) {
            return openFolder.requestFocus(direction, previouslyFocusedRect);
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    public int getDescendantFocusability() {
        if (isSmall()) {
            return 393216;
        }
        return super.getDescendantFocusability();
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (!this.mLauncher.isAllAppsVisible()) {
            Folder openFolder = getOpenFolder();
            if (openFolder != null) {
                openFolder.addFocusables(views, direction);
            } else {
                super.addFocusables(views, direction, focusableMode);
            }
        }
    }

    public boolean isSmall() {
        return this.mState == State.SMALL || this.mState == State.SPRING_LOADED;
    }

    /* access modifiers changed from: package-private */
    public void enableChildrenCache(int fromPage, int toPage) {
        if (fromPage > toPage) {
            int temp = fromPage;
            fromPage = toPage;
            toPage = temp;
        }
        int screenCount = getChildCount();
        int fromPage2 = Math.max(fromPage, 0);
        int toPage2 = Math.min(toPage, screenCount - 1);
        for (int i = fromPage2; i <= toPage2; i++) {
            CellLayout layout = (CellLayout) getChildAt(i);
            layout.setChildrenDrawnWithCacheEnabled(true);
            layout.setChildrenDrawingCacheEnabled(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void clearChildrenCache() {
        int screenCount = getChildCount();
        for (int i = 0; i < screenCount; i++) {
            CellLayout layout = (CellLayout) getChildAt(i);
            layout.setChildrenDrawnWithCacheEnabled(false);
            if (!isHardwareAccelerated()) {
                layout.setChildrenDrawingCacheEnabled(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateChildrenLayersEnabled(boolean force) {
        boolean small;
        boolean enableChildrenLayers = false;
        if (this.mState == State.SMALL || this.mIsSwitchingState) {
            small = true;
        } else {
            small = false;
        }
        if (force || small || this.mAnimatingViewIntoPlace || isPageMoving()) {
            enableChildrenLayers = true;
        }
        if (enableChildrenLayers != this.mChildrenLayersEnabled) {
            this.mChildrenLayersEnabled = enableChildrenLayers;
            if (this.mChildrenLayersEnabled) {
                enableHwLayersOnVisiblePages();
                return;
            }
            for (int i = 0; i < getPageCount(); i++) {
                ((CellLayout) getChildAt(i)).disableHardwareLayers();
            }
        }
    }

    private void enableHwLayersOnVisiblePages() {
        if (this.mChildrenLayersEnabled) {
            int screenCount = getChildCount();
            getVisiblePages(this.mTempVisiblePagesRange);
            int leftScreen = this.mTempVisiblePagesRange[0];
            int rightScreen = this.mTempVisiblePagesRange[1];
            if (leftScreen == rightScreen) {
                if (rightScreen < screenCount - 1) {
                    rightScreen++;
                } else if (leftScreen > 0) {
                    leftScreen--;
                }
            }
            for (int i = 0; i < screenCount; i++) {
                CellLayout layout = (CellLayout) getPageAt(i);
                if (leftScreen > i || i > rightScreen || !shouldDrawChild(layout)) {
                    layout.disableHardwareLayers();
                }
            }
            for (int i2 = 0; i2 < screenCount; i2++) {
                CellLayout layout2 = (CellLayout) getPageAt(i2);
                if (leftScreen <= i2 && i2 <= rightScreen && shouldDrawChild(layout2)) {
                    layout2.enableHardwareLayers();
                }
            }
        }
    }

    public void buildPageHardwareLayers() {
        updateChildrenLayersEnabled(true);
        if (getWindowToken() != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                ((CellLayout) getChildAt(i)).buildHardwareLayer();
            }
        }
        updateChildrenLayersEnabled(false);
    }

    /* access modifiers changed from: protected */
    public void onWallpaperTap(MotionEvent ev) {
        int[] position = this.mTempCell;
        getLocationOnScreen(position);
        int pointerIndex = ev.getActionIndex();
        position[0] = position[0] + ((int) ev.getX(pointerIndex));
        position[1] = position[1] + ((int) ev.getY(pointerIndex));
        this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), ev.getAction() == 1 ? "android.wallpaper.tap" : "android.wallpaper.secondaryTap", position[0], position[1], 0, (Bundle) null);
    }

    static class ZInterpolator implements TimeInterpolator {
        private float focalLength;

        public ZInterpolator(float foc) {
            this.focalLength = foc;
        }

        public float getInterpolation(float input) {
            return (1.0f - (this.focalLength / (this.focalLength + input))) / (1.0f - (this.focalLength / (this.focalLength + 1.0f)));
        }
    }

    static class InverseZInterpolator implements TimeInterpolator {
        private ZInterpolator zInterpolator;

        public InverseZInterpolator(float foc) {
            this.zInterpolator = new ZInterpolator(foc);
        }

        public float getInterpolation(float input) {
            return 1.0f - this.zInterpolator.getInterpolation(1.0f - input);
        }
    }

    static class ZoomOutInterpolator implements TimeInterpolator {
        private final DecelerateInterpolator decelerate = new DecelerateInterpolator(0.75f);
        private final ZInterpolator zInterpolator = new ZInterpolator(0.13f);

        ZoomOutInterpolator() {
        }

        public float getInterpolation(float input) {
            return this.decelerate.getInterpolation(this.zInterpolator.getInterpolation(input));
        }
    }

    static class ZoomInInterpolator implements TimeInterpolator {
        private final DecelerateInterpolator decelerate = new DecelerateInterpolator(3.0f);
        private final InverseZInterpolator inverseZInterpolator = new InverseZInterpolator(0.35f);

        ZoomInInterpolator() {
        }

        public float getInterpolation(float input) {
            return this.decelerate.getInterpolation(this.inverseZInterpolator.getInterpolation(input));
        }
    }

    public void onDragStartedWithItem(View v) {
        this.mDragOutline = createDragOutline(v, new Canvas(), 2);
    }

    public void onDragStartedWithItem(PendingAddItemInfo info, Bitmap b, boolean clipAlpha) {
        Canvas canvas = new Canvas();
        int[] size = estimateItemSize(info.spanX, info.spanY, info, false);
        this.mDragOutline = createDragOutline(b, canvas, 2, size[0], size[1], clipAlpha);
    }

    public void exitWidgetResizeMode() {
        this.mLauncher.getDragLayer().clearAllResizeFrames();
    }

    private void initAnimationArrays() {
        int childCount = getChildCount();
        if (this.mOldTranslationXs == null) {
            this.mOldTranslationXs = new float[childCount];
            this.mOldTranslationYs = new float[childCount];
            this.mOldScaleXs = new float[childCount];
            this.mOldScaleYs = new float[childCount];
            this.mOldBackgroundAlphas = new float[childCount];
            this.mOldAlphas = new float[childCount];
            this.mNewTranslationXs = new float[childCount];
            this.mNewTranslationYs = new float[childCount];
            this.mNewScaleXs = new float[childCount];
            this.mNewScaleYs = new float[childCount];
            this.mNewBackgroundAlphas = new float[childCount];
            this.mNewAlphas = new float[childCount];
            this.mNewRotationYs = new float[childCount];
        }
    }

    /* access modifiers changed from: package-private */
    public Animator getChangeStateAnimation(State state, boolean animated) {
        return getChangeStateAnimation(state, animated, 0);
    }

    /* access modifiers changed from: package-private */
    public Animator getChangeStateAnimation(State state, boolean animated, int delay) {
        int duration;
        if (this.mState == state) {
            return null;
        }
        initAnimationArrays();
        AnimatorSet anim = animated ? LauncherAnimUtils.createAnimatorSet() : null;
        Log.i(TAG, "state = " + state.toString());
        setCurrentPage(getNextPage());
        State oldState = this.mState;
        boolean oldStateIsNormal = oldState == State.NORMAL;
        boolean oldStateIsSpringLoaded = oldState == State.SPRING_LOADED;
        boolean oldStateIsSmall = oldState == State.SMALL;
        this.mState = state;
        boolean stateIsNormal = state == State.NORMAL;
        boolean stateIsSpringLoaded = state == State.SPRING_LOADED;
        boolean stateIsSmall = state == State.SMALL;
        float finalScaleFactor = 1.0f;
        float finalBackgroundAlpha = stateIsSpringLoaded ? 1.0f : 0.0f;
        boolean zoomIn = true;
        if (state == State.SMALL) {
            finalScaleFactor = 0.0f;
        } else if (state == State.SPRING_LOADED) {
            finalScaleFactor = 0.8f;
        } else if (state == State.NORMAL) {
            finalScaleFactor = 1.0f;
        }
        if (state != State.NORMAL) {
            setPageSpacing(this.mSpringLoadedPageSpacing);
            if (!oldStateIsNormal || !stateIsSmall) {
                finalBackgroundAlpha = 1.0f;
                setLayoutScale(finalScaleFactor);
            } else {
                zoomIn = false;
                setLayoutScale(finalScaleFactor);
                updateChildrenLayersEnabled(false);
            }
        } else {
            setPageSpacing(this.mOriginalPageSpacing);
            setLayoutScale(1.0f);
        }
        if (zoomIn) {
            duration = getResources().getInteger(R.integer.config_workspaceUnshrinkTime);
        } else {
            duration = getResources().getInteger(R.integer.config_appsCustomizeWorkspaceShrinkTime);
        }
        int i = 0;
        while (i < getChildCount()) {
            CellLayout cl = (CellLayout) getChildAt(i);
            float finalAlpha = (!this.mWorkspaceFadeInAdjacentScreens || stateIsSpringLoaded || i == this.mCurrentPage) ? 1.0f : 0.0f;
            float initialAlpha = cl.getShortcutsAndWidgets().getAlpha();
            if ((oldStateIsSmall && stateIsNormal) || (oldStateIsNormal && stateIsSmall)) {
                if (i == this.mCurrentPage || !animated || oldStateIsSpringLoaded) {
                    finalAlpha = 1.0f;
                } else {
                    initialAlpha = 0.0f;
                    finalAlpha = 0.0f;
                }
            }
            this.mOldAlphas[i] = initialAlpha;
            this.mNewAlphas[i] = finalAlpha;
            if (animated) {
                this.mOldTranslationXs[i] = cl.getTranslationX();
                this.mOldTranslationYs[i] = cl.getTranslationY();
                this.mOldScaleXs[i] = cl.getScaleX();
                this.mOldScaleYs[i] = cl.getScaleY();
                this.mOldBackgroundAlphas[i] = cl.getBackgroundAlpha();
                this.mNewTranslationXs[i] = 0.0f;
                this.mNewTranslationYs[i] = 0.0f;
                this.mNewScaleXs[i] = finalScaleFactor;
                this.mNewScaleYs[i] = finalScaleFactor;
                this.mNewBackgroundAlphas[i] = finalBackgroundAlpha;
            } else {
                cl.setTranslationX(0.0f);
                cl.setTranslationY(0.0f);
                cl.setScaleX(finalScaleFactor);
                cl.setScaleY(finalScaleFactor);
                cl.setBackgroundAlpha(finalBackgroundAlpha);
                cl.setShortcutAndWidgetAlpha(finalAlpha);
            }
            i++;
        }
        if (animated) {
            for (int index = 0; index < getChildCount(); index++) {
                final int i2 = index;
                final CellLayout cl2 = (CellLayout) getChildAt(i2);
                float currentAlpha = cl2.getShortcutsAndWidgets().getAlpha();
                if (this.mOldAlphas[i2] == 0.0f && this.mNewAlphas[i2] == 0.0f) {
                    cl2.setTranslationX(this.mNewTranslationXs[i2]);
                    cl2.setTranslationY(this.mNewTranslationYs[i2]);
                    cl2.setScaleX(this.mNewScaleXs[i2]);
                    cl2.setScaleY(this.mNewScaleYs[i2]);
                    cl2.setBackgroundAlpha(this.mNewBackgroundAlphas[i2]);
                    cl2.setShortcutAndWidgetAlpha(this.mNewAlphas[i2]);
                    cl2.setRotationY(this.mNewRotationYs[i2]);
                } else {
                    LauncherViewPropertyAnimator a = new LauncherViewPropertyAnimator(cl2);
                    a.translationX(this.mNewTranslationXs[i2]).translationY(this.mNewTranslationYs[i2]).scaleX(this.mNewScaleXs[i2]).scaleY(this.mNewScaleYs[i2]).setDuration((long) duration).setInterpolator(this.mZoomInInterpolator);
                    anim.play(a);
                    if (!(this.mOldAlphas[i2] == this.mNewAlphas[i2] && currentAlpha == this.mNewAlphas[i2])) {
                        LauncherViewPropertyAnimator alphaAnim = new LauncherViewPropertyAnimator(cl2.getShortcutsAndWidgets());
                        alphaAnim.alpha(this.mNewAlphas[i2]).setDuration((long) duration).setInterpolator(this.mZoomInInterpolator);
                        anim.play(alphaAnim);
                    }
                    if (this.mOldBackgroundAlphas[i2] != 0.0f || this.mNewBackgroundAlphas[i2] != 0.0f) {
                        ValueAnimator bgAnim = LauncherAnimUtils.ofFloat(cl2, 0.0f, 1.0f).setDuration((long) duration);
                        bgAnim.setInterpolator(this.mZoomInInterpolator);
                        bgAnim.addUpdateListener(new LauncherAnimatorUpdateListener() {
                            public void onAnimationUpdate(float a, float b) {
                                cl2.setBackgroundAlpha((Workspace.this.mOldBackgroundAlphas[i2] * a) + (Workspace.this.mNewBackgroundAlphas[i2] * b));
                            }
                        });
                        anim.play(bgAnim);
                    }
                }
            }
            anim.setStartDelay((long) delay);
        }
        if (stateIsSpringLoaded) {
            animateBackgroundGradient(((float) getResources().getInteger(R.integer.config_appsCustomizeSpringLoadedBgAlpha)) / 100.0f, false);
            return anim;
        }
        animateBackgroundGradient(0.0f, true);
        return anim;
    }

    public void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace) {
        this.mIsSwitchingState = true;
        updateChildrenLayersEnabled(false);
        cancelScrollingIndicatorAnimations();
    }

    public void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace) {
    }

    public void onLauncherTransitionStep(Launcher l, float t) {
        this.mTransitionProgress = t;
    }

    public void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace) {
        this.mIsSwitchingState = false;
        this.mWallpaperOffset.setOverrideHorizontalCatchupConstant(false);
        updateChildrenLayersEnabled(false);
        if (!this.mWorkspaceFadeInAdjacentScreens) {
            for (int i = 0; i < getChildCount(); i++) {
                ((CellLayout) getChildAt(i)).setShortcutAndWidgetAlpha(1.0f);
            }
        }
    }

    public View getContent() {
        return this;
    }

    private void drawDragView(View v, Canvas destCanvas, int padding, boolean pruneToDrawable) {
        Rect clipRect = this.mTempRect;
        v.getDrawingRect(clipRect);
        boolean textVisible = false;
        destCanvas.save();
        if (!(v instanceof TextView) || !pruneToDrawable) {
            if (v instanceof FolderIcon) {
                if (((FolderIcon) v).getTextVisible()) {
                    ((FolderIcon) v).setTextVisible(false);
                    textVisible = true;
                }
            } else if (v instanceof BubbleTextView) {
                BubbleTextView tv = (BubbleTextView) v;
                clipRect.bottom = (tv.getExtendedPaddingTop() - 3) + tv.getLayout().getLineTop(0);
            } else if (v instanceof TextView) {
                TextView tv2 = (TextView) v;
                clipRect.bottom = (tv2.getExtendedPaddingTop() - tv2.getCompoundDrawablePadding()) + tv2.getLayout().getLineTop(0);
            }
            destCanvas.translate((float) ((-v.getScrollX()) + (padding / 2)), (float) ((-v.getScrollY()) + (padding / 2)));
            destCanvas.clipRect(clipRect, Region.Op.REPLACE);
            v.draw(destCanvas);
            if (textVisible) {
                ((FolderIcon) v).setTextVisible(true);
            }
        } else {
            Drawable d = ((TextView) v).getCompoundDrawables()[1];
            clipRect.set(0, 0, d.getIntrinsicWidth() + padding, d.getIntrinsicHeight() + padding);
            destCanvas.translate((float) (padding / 2), (float) (padding / 2));
            d.draw(destCanvas);
        }
        destCanvas.restore();
    }

    public Bitmap createDragBitmap(View v, Canvas canvas, int padding) {
        Bitmap b;
        if (v instanceof TextView) {
            Drawable d = ((TextView) v).getCompoundDrawables()[1];
            b = Bitmap.createBitmap(d.getIntrinsicWidth() + padding, d.getIntrinsicHeight() + padding, Bitmap.Config.ARGB_8888);
        } else {
            b = Bitmap.createBitmap(v.getWidth() + padding, v.getHeight() + padding, Bitmap.Config.ARGB_8888);
        }
        canvas.setBitmap(b);
        drawDragView(v, canvas, padding, true);
        canvas.setBitmap((Bitmap) null);
        return b;
    }

    private Bitmap createDragOutline(View v, Canvas canvas, int padding) {
        int outlineColor = getResources().getColor(17170443);
        Bitmap b = Bitmap.createBitmap(v.getWidth() + padding, v.getHeight() + padding, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        drawDragView(v, canvas, padding, true);
        this.mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor);
        canvas.setBitmap((Bitmap) null);
        return b;
    }

    private Bitmap createDragOutline(Bitmap orig, Canvas canvas, int padding, int w, int h, boolean clipAlpha) {
        int outlineColor = getResources().getColor(17170443);
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        Rect src = new Rect(0, 0, orig.getWidth(), orig.getHeight());
        float scaleFactor = Math.min(((float) (w - padding)) / ((float) orig.getWidth()), ((float) (h - padding)) / ((float) orig.getHeight()));
        int scaledWidth = (int) (((float) orig.getWidth()) * scaleFactor);
        int scaledHeight = (int) (((float) orig.getHeight()) * scaleFactor);
        Rect dst = new Rect(0, 0, scaledWidth, scaledHeight);
        dst.offset((w - scaledWidth) / 2, (h - scaledHeight) / 2);
        canvas.drawBitmap(orig, src, dst, (Paint) null);
        this.mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor, clipAlpha);
        canvas.setBitmap((Bitmap) null);
        return b;
    }

    /* access modifiers changed from: package-private */
    public void startDrag(CellLayout.CellInfo cellInfo) {
        View child = cellInfo.cell;
        if (child.isInTouchMode()) {
            this.mDragInfo = cellInfo;
            child.setVisibility(4);
            ((CellLayout) child.getParent().getParent()).prepareChildForDrag(child);
            child.clearFocus();
            child.setPressed(false);
            this.mDragOutline = createDragOutline(child, new Canvas(), 2);
            beginDragShared(child, this);
        }
    }

    public void beginDragShared(View child, DragSource source) {
        Resources r = getResources();
        Bitmap b = createDragBitmap(child, new Canvas(), 2);
        int bmpWidth = b.getWidth();
        int bmpHeight = b.getHeight();
        float scale = this.mLauncher.getDragLayer().getLocationInDragLayer(child, this.mTempXY);
        int dragLayerX = Math.round(((float) this.mTempXY[0]) - ((((float) bmpWidth) - (((float) child.getWidth()) * scale)) / WALLPAPER_SCREENS_SPAN));
        int dragLayerY = Math.round((((float) this.mTempXY[1]) - ((((float) bmpHeight) - (((float) bmpHeight) * scale)) / WALLPAPER_SCREENS_SPAN)) - 1.0f);
        Point dragVisualizeOffset = null;
        Rect dragRect = null;
        if ((child instanceof BubbleTextView) || (child instanceof PagedViewIcon)) {
            int iconSize = r.getDimensionPixelSize(R.dimen.app_icon_size);
            int iconPaddingTop = r.getDimensionPixelSize(R.dimen.app_icon_padding_top);
            int top = child.getPaddingTop();
            int left = (bmpWidth - iconSize) / 2;
            dragLayerY += top;
            dragVisualizeOffset = new Point(-1, iconPaddingTop - 1);
            dragRect = new Rect(left, top, left + iconSize, top + iconSize);
        } else if (child instanceof FolderIcon) {
            dragRect = new Rect(0, 0, child.getWidth(), r.getDimensionPixelSize(R.dimen.folder_preview_size));
        }
        if (child instanceof BubbleTextView) {
            ((BubbleTextView) child).clearPressedOrFocusedBackground();
        }
        this.mDragController.startDrag(b, dragLayerX, dragLayerY, source, child.getTag(), DragController.DRAG_ACTION_MOVE, dragVisualizeOffset, dragRect, scale);
        b.recycle();
        showScrollingIndicator(false);
    }

    /* access modifiers changed from: package-private */
    public void addApplicationShortcut(ShortcutInfo info, CellLayout target, long container, int screen, int cellX, int cellY, boolean insertAtFirst, int intersectX, int intersectY) {
        View view = this.mLauncher.createShortcut(R.layout.application, target, info);
        int[] cellXY = new int[2];
        target.findCellForSpanThatIntersects(cellXY, 1, 1, intersectX, intersectY);
        addInScreen(view, container, screen, cellXY[0], cellXY[1], 1, 1, insertAtFirst);
        LauncherModel.addOrMoveItemInDatabase(this.mLauncher, info, container, screen, cellXY[0], cellXY[1]);
    }

    public boolean transitionStateShouldAllowDrop() {
        return (!isSwitchingState() || this.mTransitionProgress > 0.5f) && this.mState != State.SMALL;
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        int spanX;
        int spanY;
        CellLayout dropTargetLayout = this.mDropToLayout;
        if (d.dragSource != this) {
            if (dropTargetLayout == null || !transitionStateShouldAllowDrop()) {
                return false;
            }
            this.mDragViewVisualCenter = getDragViewVisualCenter(d.x, d.y, d.xOffset, d.yOffset, d.dragView, this.mDragViewVisualCenter);
            if (this.mLauncher.isHotseatLayout(dropTargetLayout)) {
                mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
            } else {
                mapPointFromSelfToChild(dropTargetLayout, this.mDragViewVisualCenter, (Matrix) null);
            }
            if (this.mDragInfo != null) {
                CellLayout.CellInfo dragCellInfo = this.mDragInfo;
                spanX = dragCellInfo.spanX;
                spanY = dragCellInfo.spanY;
            } else {
                ItemInfo dragInfo = (ItemInfo) d.dragInfo;
                spanX = dragInfo.spanX;
                spanY = dragInfo.spanY;
            }
            int minSpanX = spanX;
            int minSpanY = spanY;
            if (d.dragInfo instanceof PendingAddWidgetInfo) {
                minSpanX = ((PendingAddWidgetInfo) d.dragInfo).minSpanX;
                minSpanY = ((PendingAddWidgetInfo) d.dragInfo).minSpanY;
            }
            this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX, minSpanY, dropTargetLayout, this.mTargetCell);
            float distance = dropTargetLayout.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell);
            if (willCreateUserFolder((ItemInfo) d.dragInfo, dropTargetLayout, this.mTargetCell, distance, true)) {
                return true;
            }
            if (willAddToExistingUserFolder((ItemInfo) d.dragInfo, dropTargetLayout, this.mTargetCell, distance)) {
                return true;
            }
            this.mTargetCell = dropTargetLayout.createArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX, minSpanY, spanX, spanY, (View) null, this.mTargetCell, new int[2], 3);
            if (!(this.mTargetCell[0] >= 0 && this.mTargetCell[1] >= 0)) {
                boolean isHotseat = this.mLauncher.isHotseatLayout(dropTargetLayout);
                if (this.mTargetCell != null && isHotseat) {
                    Hotseat hotseat = this.mLauncher.getHotseat();
                    if (hotseat.isAllAppsButtonRank(hotseat.getOrderInHotseat(this.mTargetCell[0], this.mTargetCell[1]))) {
                        return false;
                    }
                }
                this.mLauncher.showOutOfSpaceMessage(isHotseat);
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean willCreateUserFolder(ItemInfo info, CellLayout target, int[] targetCell, float distance, boolean considerTimeout) {
        boolean willBecomeShortcut;
        boolean z = true;
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View dropOverView = target.getChildAt(targetCell[0], targetCell[1]);
        if (dropOverView != null) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) dropOverView.getLayoutParams();
            if (lp.useTmpCoords && !(lp.tmpCellX == lp.cellX && lp.tmpCellY == lp.tmpCellY)) {
                return false;
            }
        }
        boolean hasntMoved = false;
        if (this.mDragInfo != null) {
            if (dropOverView == this.mDragInfo.cell) {
                hasntMoved = true;
            } else {
                hasntMoved = false;
            }
        }
        if (dropOverView == null || hasntMoved) {
            return false;
        }
        if (considerTimeout && !this.mCreateUserFolderOnDrop) {
            return false;
        }
        boolean aboveShortcut = dropOverView.getTag() instanceof ShortcutInfo;
        if (info.itemType == 0 || info.itemType == 1) {
            willBecomeShortcut = true;
        } else {
            willBecomeShortcut = false;
        }
        if (!aboveShortcut || !willBecomeShortcut) {
            z = false;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean willAddToExistingUserFolder(Object dragInfo, CellLayout target, int[] targetCell, float distance) {
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View dropOverView = target.getChildAt(targetCell[0], targetCell[1]);
        if (dropOverView != null) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) dropOverView.getLayoutParams();
            if (lp.useTmpCoords && !(lp.tmpCellX == lp.cellX && lp.tmpCellY == lp.tmpCellY)) {
                return false;
            }
        }
        if (!(dropOverView instanceof FolderIcon) || !((FolderIcon) dropOverView).acceptDrop(dragInfo)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean createUserFolderIfNecessary(View newView, long container, CellLayout target, int[] targetCell, float distance, boolean external, DragView dragView, Runnable postAnimationRunnable) {
        int screen;
        if (distance > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View v = target.getChildAt(targetCell[0], targetCell[1]);
        boolean hasntMoved = false;
        if (this.mDragInfo != null) {
            hasntMoved = this.mDragInfo.cellX == targetCell[0] && this.mDragInfo.cellY == targetCell[1] && getParentCellLayoutForView(this.mDragInfo.cell) == target;
        }
        if (v == null || hasntMoved || !this.mCreateUserFolderOnDrop) {
            return false;
        }
        this.mCreateUserFolderOnDrop = false;
        if (targetCell == null) {
            screen = this.mDragInfo.screen;
        } else {
            screen = indexOfChild(target);
        }
        boolean aboveShortcut = v.getTag() instanceof ShortcutInfo;
        boolean willBecomeShortcut = newView.getTag() instanceof ShortcutInfo;
        if (!aboveShortcut || !willBecomeShortcut) {
            return false;
        }
        ShortcutInfo sourceInfo = (ShortcutInfo) newView.getTag();
        ShortcutInfo destInfo = (ShortcutInfo) v.getTag();
        if (!external) {
            getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
        }
        Rect folderLocation = new Rect();
        float scale = this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(v, folderLocation);
        target.removeView(v);
        FolderIcon fi = this.mLauncher.addFolder(target, container, screen, targetCell[0], targetCell[1]);
        destInfo.cellX = -1;
        destInfo.cellY = -1;
        sourceInfo.cellX = -1;
        sourceInfo.cellY = -1;
        if (dragView != null) {
            fi.performCreateAnimation(destInfo, v, sourceInfo, dragView, folderLocation, scale, postAnimationRunnable);
        } else {
            fi.addItem(destInfo);
            fi.addItem(sourceInfo);
        }
        return true;
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
        if (!(dropOverView instanceof FolderIcon)) {
            return false;
        }
        FolderIcon fi = (FolderIcon) dropOverView;
        if (!fi.acceptDrop(d.dragInfo)) {
            return false;
        }
        fi.onDrop(d);
        if (!external) {
            getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
        }
        return true;
    }

    public void onDrop(DropTarget.DragObject d) {
        int screen;
        Log.i(TAG, "onDrop");
        this.mDragViewVisualCenter = getDragViewVisualCenter(d.x, d.y, d.xOffset, d.yOffset, d.dragView, this.mDragViewVisualCenter);
        CellLayout dropTargetLayout = this.mDropToLayout;
        if (dropTargetLayout != null) {
            if (this.mLauncher.isHotseatLayout(dropTargetLayout)) {
                mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
            } else {
                mapPointFromSelfToChild(dropTargetLayout, this.mDragViewVisualCenter, (Matrix) null);
            }
        }
        int snapScreen = -1;
        boolean resizeOnDrop = false;
        if (d.dragSource != this) {
            onDropExternal(new int[]{(int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1]}, d.dragInfo, dropTargetLayout, false, d);
        } else if (this.mDragInfo != null) {
            View cell = this.mDragInfo.cell;
            AnonymousClass6 finalResizeRunnable = null;
            if (dropTargetLayout != null) {
                boolean hasMovedLayouts = getParentCellLayoutForView(cell) != dropTargetLayout;
                boolean hasMovedIntoHotseat = this.mLauncher.isHotseatLayout(dropTargetLayout);
                long container = hasMovedIntoHotseat ? -101 : -100;
                if (this.mTargetCell[0] < 0) {
                    screen = this.mDragInfo.screen;
                } else {
                    screen = indexOfChild(dropTargetLayout);
                }
                int spanX = this.mDragInfo != null ? this.mDragInfo.spanX : 1;
                int spanY = this.mDragInfo != null ? this.mDragInfo.spanY : 1;
                this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], spanX, spanY, dropTargetLayout, this.mTargetCell);
                float distance = dropTargetLayout.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell);
                if (!this.mInScrollArea) {
                    if (createUserFolderIfNecessary(cell, container, dropTargetLayout, this.mTargetCell, distance, false, d.dragView, (Runnable) null)) {
                        return;
                    }
                }
                if (!addToExistingFolderIfNecessary(cell, dropTargetLayout, this.mTargetCell, distance, d, false)) {
                    ItemInfo item = (ItemInfo) d.dragInfo;
                    int minSpanX = item.spanX;
                    int minSpanY = item.spanY;
                    if (item.minSpanX > 0 && item.minSpanY > 0) {
                        minSpanX = item.minSpanX;
                        minSpanY = item.minSpanY;
                    }
                    int[] resultSpan = new int[2];
                    this.mTargetCell = dropTargetLayout.createArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], minSpanX, minSpanY, spanX, spanY, cell, this.mTargetCell, resultSpan, 1);
                    boolean foundCell = this.mTargetCell[0] >= 0 && this.mTargetCell[1] >= 0;
                    if (foundCell && (cell instanceof AppWidgetHostView) && !(resultSpan[0] == item.spanX && resultSpan[1] == item.spanY)) {
                        resizeOnDrop = true;
                        item.spanX = resultSpan[0];
                        item.spanY = resultSpan[1];
                        AppWidgetResizeFrame.updateWidgetSizeRanges((AppWidgetHostView) cell, this.mLauncher, resultSpan[0], resultSpan[1]);
                    }
                    if (this.mCurrentPage != screen && !hasMovedIntoHotseat) {
                        snapScreen = screen;
                        snapToPage(screen);
                    }
                    if (foundCell) {
                        ItemInfo info = (ItemInfo) cell.getTag();
                        if (hasMovedLayouts) {
                            getParentCellLayoutForView(cell).removeView(cell);
                            addInScreen(cell, container, screen, this.mTargetCell[0], this.mTargetCell[1], info.spanX, info.spanY);
                        }
                        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) cell.getLayoutParams();
                        int i = this.mTargetCell[0];
                        lp.tmpCellX = i;
                        lp.cellX = i;
                        int i2 = this.mTargetCell[1];
                        lp.tmpCellY = i2;
                        lp.cellY = i2;
                        lp.cellHSpan = item.spanX;
                        lp.cellVSpan = item.spanY;
                        lp.isLockedToGrid = true;
                        cell.setId(LauncherModel.getCellLayoutChildId(container, this.mDragInfo.screen, this.mTargetCell[0], this.mTargetCell[1], this.mDragInfo.spanX, this.mDragInfo.spanY));
                        if (container != -101 && (cell instanceof LauncherAppWidgetHostView)) {
                            CellLayout cellLayout = dropTargetLayout;
                            LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) cell;
                            AppWidgetProviderInfo pinfo = hostView.getAppWidgetInfo();
                            if (!(pinfo == null || pinfo.resizeMode == 0)) {
                                final ItemInfo itemInfo = info;
                                final LauncherAppWidgetHostView launcherAppWidgetHostView = hostView;
                                final CellLayout cellLayout2 = cellLayout;
                                final AnonymousClass5 r2 = new Runnable() {
                                    public void run() {
                                        Workspace.this.mLauncher.getDragLayer().addResizeFrame(itemInfo, launcherAppWidgetHostView, cellLayout2);
                                    }
                                };
                                finalResizeRunnable = new Runnable() {
                                    public void run() {
                                        if (!Workspace.this.isPageMoving()) {
                                            r2.run();
                                        } else {
                                            Runnable unused = Workspace.this.mDelayedResizeRunnable = r2;
                                        }
                                    }
                                };
                            }
                        }
                        LauncherModel.moveItemInDatabase(this.mLauncher, info, container, screen, lp.cellX, lp.cellY);
                    } else {
                        CellLayout.LayoutParams lp2 = (CellLayout.LayoutParams) cell.getLayoutParams();
                        this.mTargetCell[0] = lp2.cellX;
                        this.mTargetCell[1] = lp2.cellY;
                        ((CellLayout) cell.getParent().getParent()).markCellsAsOccupiedForView(cell);
                    }
                } else {
                    return;
                }
            }
            CellLayout parent = (CellLayout) cell.getParent().getParent();
            final Runnable runnable = finalResizeRunnable;
            AnonymousClass7 r0 = new Runnable() {
                public void run() {
                    Workspace.this.mAnimatingViewIntoPlace = false;
                    Workspace.this.updateChildrenLayersEnabled(false);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            };
            this.mAnimatingViewIntoPlace = true;
            if (d.dragView.hasDrawn()) {
                ItemInfo info2 = (ItemInfo) cell.getTag();
                if (info2.itemType == 4) {
                    animateWidgetDrop(info2, parent, d.dragView, r0, resizeOnDrop ? 2 : 0, cell, false);
                } else {
                    this.mLauncher.getDragLayer().animateViewIntoPosition(d.dragView, cell, snapScreen < 0 ? -1 : 300, r0, this);
                }
            } else {
                d.deferDragViewCleanupPostAnimation = false;
                cell.setVisibility(0);
            }
            parent.onDropChild(cell);
        }
    }

    public void setFinalScrollForPageChange(int screen) {
        if (screen >= 0) {
            this.mSavedScrollX = getScrollX();
            CellLayout cl = (CellLayout) getChildAt(screen);
            this.mSavedTranslationX = cl.getTranslationX();
            this.mSavedRotationY = cl.getRotationY();
            setScrollX(getChildOffset(screen) - getRelativeChildOffset(screen));
            cl.setTranslationX(0.0f);
            cl.setRotationY(0.0f);
        }
    }

    public void resetFinalScrollForPageChange(int screen) {
        if (screen >= 0) {
            CellLayout cl = (CellLayout) getChildAt(screen);
            setScrollX(this.mSavedScrollX);
            cl.setTranslationX(this.mSavedTranslationX);
            cl.setRotationY(this.mSavedRotationY);
        }
    }

    public void getViewLocationRelativeToSelf(View v, int[] location) {
        getLocationInWindow(location);
        int x = location[0];
        int y = location[1];
        v.getLocationInWindow(location);
        int vX = location[0];
        int vY = location[1];
        location[0] = vX - x;
        location[1] = vY - y;
    }

    public void onDragEnter(DropTarget.DragObject d) {
        this.mDragEnforcer.onDragEnter();
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDropToLayout = null;
        CellLayout layout = getCurrentDropLayout();
        setCurrentDropLayout(layout);
        setCurrentDragOverlappingLayout(layout);
        if (LauncherApplication.isScreenLarge()) {
            showOutlines();
        }
    }

    static Rect getCellLayoutMetrics(Launcher launcher, int orientation) {
        Resources res = launcher.getResources();
        Display display = launcher.getWindowManager().getDefaultDisplay();
        Point smallestSize = new Point();
        Point largestSize = new Point();
        display.getCurrentSizeRange(smallestSize, largestSize);
        if (orientation == 0) {
            if (mLandscapeCellLayoutMetrics == null) {
                int paddingLeft = res.getDimensionPixelSize(R.dimen.workspace_left_padding_land);
                int paddingRight = res.getDimensionPixelSize(R.dimen.workspace_right_padding_land);
                int paddingTop = res.getDimensionPixelSize(R.dimen.workspace_top_padding_land);
                int paddingBottom = res.getDimensionPixelSize(R.dimen.workspace_bottom_padding_land);
                int width = (largestSize.x - paddingLeft) - paddingRight;
                int height = (smallestSize.y - paddingTop) - paddingBottom;
                mLandscapeCellLayoutMetrics = new Rect();
                CellLayout.getMetrics(mLandscapeCellLayoutMetrics, res, width, height, LauncherModel.getCellCountX(), LauncherModel.getCellCountY(), orientation);
            }
            return mLandscapeCellLayoutMetrics;
        } else if (orientation != 1) {
            return null;
        } else {
            if (mPortraitCellLayoutMetrics == null) {
                int paddingLeft2 = res.getDimensionPixelSize(R.dimen.workspace_left_padding_land);
                int paddingRight2 = res.getDimensionPixelSize(R.dimen.workspace_right_padding_land);
                int paddingTop2 = res.getDimensionPixelSize(R.dimen.workspace_top_padding_land);
                int paddingBottom2 = res.getDimensionPixelSize(R.dimen.workspace_bottom_padding_land);
                int width2 = (smallestSize.x - paddingLeft2) - paddingRight2;
                int height2 = (largestSize.y - paddingTop2) - paddingBottom2;
                mPortraitCellLayoutMetrics = new Rect();
                CellLayout.getMetrics(mPortraitCellLayoutMetrics, res, width2, height2, LauncherModel.getCellCountX(), LauncherModel.getCellCountY(), orientation);
            }
            return mPortraitCellLayoutMetrics;
        }
    }

    public void onDragExit(DropTarget.DragObject d) {
        this.mDragEnforcer.onDragExit();
        if (!this.mInScrollArea) {
            this.mDropToLayout = this.mDragTargetLayout;
        } else if (isPageMoving()) {
            this.mDropToLayout = (CellLayout) getPageAt(getNextPage());
        } else {
            this.mDropToLayout = this.mDragOverlappingLayout;
        }
        if (this.mDragMode == 1) {
            this.mCreateUserFolderOnDrop = true;
        } else if (this.mDragMode == 2) {
            this.mAddToExistingFolderOnDrop = true;
        }
        onResetScrollArea();
        setCurrentDropLayout((CellLayout) null);
        setCurrentDragOverlappingLayout((CellLayout) null);
        this.mSpringLoadedDragController.cancel();
        if (!this.mIsPageMoving) {
            hideOutlines();
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
        invalidate();
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
        if (this.mDragFolderRingAnimator != null) {
            this.mDragFolderRingAnimator.animateToNaturalState();
        }
        this.mFolderCreationAlarm.cancelAlarm();
    }

    private void cleanupAddToFolder() {
        if (this.mDragOverFolderIcon != null) {
            this.mDragOverFolderIcon.onDragExit((Object) null);
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

    public DropTarget getDropTargetDelegate(DropTarget.DragObject d) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public void mapPointFromSelfToChild(View v, float[] xy) {
        mapPointFromSelfToChild(v, xy, (Matrix) null);
    }

    /* access modifiers changed from: package-private */
    public void mapPointFromSelfToChild(View v, float[] xy, Matrix cachedInverseMatrix) {
        if (cachedInverseMatrix == null) {
            v.getMatrix().invert(this.mTempInverseMatrix);
            cachedInverseMatrix = this.mTempInverseMatrix;
        }
        int scrollX = getScrollX();
        if (this.mNextPage != -1) {
            scrollX = this.mScroller.getFinalX();
        }
        xy[0] = (xy[0] + ((float) scrollX)) - ((float) v.getLeft());
        xy[1] = (xy[1] + ((float) getScrollY())) - ((float) v.getTop());
        cachedInverseMatrix.mapPoints(xy);
    }

    /* access modifiers changed from: package-private */
    public void mapPointFromSelfToHotseatLayout(Hotseat hotseat, float[] xy) {
        hotseat.getLayout().getMatrix().invert(this.mTempInverseMatrix);
        xy[0] = (xy[0] - ((float) hotseat.getLeft())) - ((float) hotseat.getLayout().getLeft());
        xy[1] = (xy[1] - ((float) hotseat.getTop())) - ((float) hotseat.getLayout().getTop());
        this.mTempInverseMatrix.mapPoints(xy);
    }

    /* access modifiers changed from: package-private */
    public void mapPointFromChildToSelf(View v, float[] xy) {
        v.getMatrix().mapPoints(xy);
        int scrollX = getScrollX();
        if (this.mNextPage != -1) {
            scrollX = this.mScroller.getFinalX();
        }
        xy[0] = xy[0] - ((float) (scrollX - v.getLeft()));
        xy[1] = xy[1] - ((float) (getScrollY() - v.getTop()));
    }

    private static float squaredDistance(float[] point1, float[] point2) {
        float distanceX = point1[0] - point2[0];
        float distanceY = point2[1] - point2[1];
        return (distanceX * distanceX) + (distanceY * distanceY);
    }

    /* access modifiers changed from: package-private */
    public boolean overlaps(CellLayout cl, DragView dragView, int dragViewX, int dragViewY, Matrix cachedInverseMatrix) {
        float[] draggedItemTopLeft = this.mTempDragCoordinates;
        draggedItemTopLeft[0] = (float) dragViewX;
        draggedItemTopLeft[1] = (float) dragViewY;
        float[] draggedItemBottomRight = this.mTempDragBottomRightCoordinates;
        draggedItemBottomRight[0] = draggedItemTopLeft[0] + ((float) dragView.getDragRegionWidth());
        draggedItemBottomRight[1] = draggedItemTopLeft[1] + ((float) dragView.getDragRegionHeight());
        mapPointFromSelfToChild(cl, draggedItemTopLeft, cachedInverseMatrix);
        float overlapRegionLeft = Math.max(0.0f, draggedItemTopLeft[0]);
        float overlapRegionTop = Math.max(0.0f, draggedItemTopLeft[1]);
        if (overlapRegionLeft <= ((float) cl.getWidth()) && overlapRegionTop >= 0.0f) {
            mapPointFromSelfToChild(cl, draggedItemBottomRight, cachedInverseMatrix);
            float overlapRegionRight = Math.min((float) cl.getWidth(), draggedItemBottomRight[0]);
            float overlapRegionBottom = Math.min((float) cl.getHeight(), draggedItemBottomRight[1]);
            if (overlapRegionRight < 0.0f || overlapRegionBottom > ((float) cl.getHeight()) || (overlapRegionRight - overlapRegionLeft) * (overlapRegionBottom - overlapRegionTop) <= 0.0f) {
                return false;
            }
            return true;
        }
        return false;
    }

    private CellLayout findMatchingPageForDragOver(DragView dragView, float originX, float originY, boolean exact) {
        int screenCount = getChildCount();
        CellLayout bestMatchingScreen = null;
        float smallestDistSoFar = Float.MAX_VALUE;
        for (int i = 0; i < screenCount; i++) {
            CellLayout cl = (CellLayout) getChildAt(i);
            float[] touchXy = {originX, originY};
            cl.getMatrix().invert(this.mTempInverseMatrix);
            mapPointFromSelfToChild(cl, touchXy, this.mTempInverseMatrix);
            if (touchXy[0] >= 0.0f && touchXy[0] <= ((float) cl.getWidth()) && touchXy[1] >= 0.0f && touchXy[1] <= ((float) cl.getHeight())) {
                return cl;
            }
            if (!exact) {
                float[] cellLayoutCenter = this.mTempCellLayoutCenterCoordinates;
                cellLayoutCenter[0] = (float) (cl.getWidth() / 2);
                cellLayoutCenter[1] = (float) (cl.getHeight() / 2);
                mapPointFromChildToSelf(cl, cellLayoutCenter);
                touchXy[0] = originX;
                touchXy[1] = originY;
                float dist = squaredDistance(touchXy, cellLayoutCenter);
                if (dist < smallestDistSoFar) {
                    smallestDistSoFar = dist;
                    bestMatchingScreen = cl;
                }
            }
        }
        return bestMatchingScreen;
    }

    private float[] getDragViewVisualCenter(int x, int y, int xOffset, int yOffset, DragView dragView, float[] recycle) {
        float[] res;
        if (recycle == null) {
            res = new float[2];
        } else {
            res = recycle;
        }
        int x2 = x + getResources().getDimensionPixelSize(R.dimen.dragViewOffsetX);
        res[0] = (float) ((dragView.getDragRegion().width() / 2) + (x2 - xOffset));
        res[1] = (float) ((dragView.getDragRegion().height() / 2) + ((y + getResources().getDimensionPixelSize(R.dimen.dragViewOffsetY)) - yOffset));
        return res;
    }

    private boolean isDragWidget(DropTarget.DragObject d) {
        return (d.dragInfo instanceof LauncherAppWidgetInfo) || (d.dragInfo instanceof PendingAddWidgetInfo);
    }

    private boolean isExternalDragWidget(DropTarget.DragObject d) {
        return d.dragSource != this && isDragWidget(d);
    }

    public void onDragOver(DropTarget.DragObject d) {
        View child;
        if (!this.mInScrollArea && !this.mIsSwitchingState && this.mState != State.SMALL) {
            Rect r = new Rect();
            CellLayout layout = null;
            ItemInfo item = (ItemInfo) d.dragInfo;
            if (item.spanX < 0 || item.spanY < 0) {
                throw new RuntimeException("Improper spans found");
            }
            this.mDragViewVisualCenter = getDragViewVisualCenter(d.x, d.y, d.xOffset, d.yOffset, d.dragView, this.mDragViewVisualCenter);
            if (this.mDragInfo == null) {
                child = null;
            } else {
                child = this.mDragInfo.cell;
            }
            if (isSmall()) {
                if (this.mLauncher.getHotseat() != null && !isExternalDragWidget(d)) {
                    this.mLauncher.getHotseat().getHitRect(r);
                    if (r.contains(d.x, d.y)) {
                        layout = this.mLauncher.getHotseat().getLayout();
                    }
                }
                if (layout == null) {
                    layout = findMatchingPageForDragOver(d.dragView, (float) d.x, (float) d.y, false);
                }
                if (layout != this.mDragTargetLayout) {
                    setCurrentDropLayout(layout);
                    setCurrentDragOverlappingLayout(layout);
                    if (this.mState == State.SPRING_LOADED) {
                        if (this.mLauncher.isHotseatLayout(layout)) {
                            this.mSpringLoadedDragController.cancel();
                        } else {
                            this.mSpringLoadedDragController.setAlarm(this.mDragTargetLayout);
                        }
                    }
                }
            } else {
                if (this.mLauncher.getHotseat() != null && !isDragWidget(d)) {
                    this.mLauncher.getHotseat().getHitRect(r);
                    if (r.contains(d.x, d.y)) {
                        layout = this.mLauncher.getHotseat().getLayout();
                    }
                }
                if (layout == null) {
                    layout = getCurrentDropLayout();
                }
                if (layout != this.mDragTargetLayout) {
                    setCurrentDropLayout(layout);
                    setCurrentDragOverlappingLayout(layout);
                }
            }
            if (this.mDragTargetLayout != null) {
                if (this.mLauncher.isHotseatLayout(this.mDragTargetLayout)) {
                    mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
                } else {
                    mapPointFromSelfToChild(this.mDragTargetLayout, this.mDragViewVisualCenter, (Matrix) null);
                }
                this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], item.spanX, item.spanY, this.mDragTargetLayout, this.mTargetCell);
                setCurrentDropOverCell(this.mTargetCell[0], this.mTargetCell[1]);
                manageFolderFeedback((ItemInfo) d.dragInfo, this.mDragTargetLayout, this.mTargetCell, this.mDragTargetLayout.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell), this.mDragTargetLayout.getChildAt(this.mTargetCell[0], this.mTargetCell[1]));
                int minSpanX = item.spanX;
                int minSpanY = item.spanY;
                if (item.minSpanX > 0 && item.minSpanY > 0) {
                    minSpanX = item.minSpanX;
                    minSpanY = item.minSpanY;
                }
                boolean nearestDropOccupied = this.mDragTargetLayout.isNearestDropLocationOccupied((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], item.spanX, item.spanY, child, this.mTargetCell);
                if (!nearestDropOccupied) {
                    this.mDragTargetLayout.visualizeDropLocation(child, this.mDragOutline, (int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], this.mTargetCell[0], this.mTargetCell[1], item.spanX, item.spanY, false, d.dragView.getDragVisualizeOffset(), d.dragView.getDragRegion());
                } else if ((this.mDragMode == 0 || this.mDragMode == 3) && !this.mReorderAlarm.alarmPending() && !(this.mLastReorderX == this.mTargetCell[0] && this.mLastReorderY == this.mTargetCell[1])) {
                    this.mReorderAlarm.setOnAlarmListener(new ReorderAlarmListener(this.mDragViewVisualCenter, minSpanX, minSpanY, item.spanX, item.spanY, d.dragView, child));
                    this.mReorderAlarm.setAlarm(250);
                }
                if ((this.mDragMode == 1 || this.mDragMode == 2 || !nearestDropOccupied) && this.mDragTargetLayout != null) {
                    this.mDragTargetLayout.revertTempState();
                }
            }
        }
    }

    private void manageFolderFeedback(ItemInfo info, CellLayout targetLayout, int[] targetCell, float distance, View dragOverView) {
        boolean userFolderPending = willCreateUserFolder(info, targetLayout, targetCell, distance, false);
        if (this.mDragMode != 0 || !userFolderPending || this.mFolderCreationAlarm.alarmPending()) {
            boolean willAddToFolder = willAddToExistingUserFolder(info, targetLayout, targetCell, distance);
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
            return;
        }
        this.mFolderCreationAlarm.setOnAlarmListener(new FolderCreationAlarmListener(targetLayout, targetCell[0], targetCell[1]));
        this.mFolderCreationAlarm.setAlarm(0);
    }

    class FolderCreationAlarmListener implements OnAlarmListener {
        int cellX;
        int cellY;
        CellLayout layout;

        public FolderCreationAlarmListener(CellLayout layout2, int cellX2, int cellY2) {
            this.layout = layout2;
            this.cellX = cellX2;
            this.cellY = cellY2;
        }

        public void onAlarm(Alarm alarm) {
            if (Workspace.this.mDragFolderRingAnimator == null) {
                FolderIcon.FolderRingAnimator unused = Workspace.this.mDragFolderRingAnimator = new FolderIcon.FolderRingAnimator(Workspace.this.mLauncher, (FolderIcon) null);
            }
            Workspace.this.mDragFolderRingAnimator.setCell(this.cellX, this.cellY);
            Workspace.this.mDragFolderRingAnimator.setCellLayout(this.layout);
            Workspace.this.mDragFolderRingAnimator.animateToAcceptState();
            this.layout.showFolderAccept(Workspace.this.mDragFolderRingAnimator);
            this.layout.clearDragOutlines();
            Workspace.this.setDragMode(1);
        }
    }

    class ReorderAlarmListener implements OnAlarmListener {
        View child;
        DragView dragView;
        float[] dragViewCenter;
        int minSpanX;
        int minSpanY;
        int spanX;
        int spanY;

        public ReorderAlarmListener(float[] dragViewCenter2, int minSpanX2, int minSpanY2, int spanX2, int spanY2, DragView dragView2, View child2) {
            this.dragViewCenter = dragViewCenter2;
            this.minSpanX = minSpanX2;
            this.minSpanY = minSpanY2;
            this.spanX = spanX2;
            this.spanY = spanY2;
            this.child = child2;
            this.dragView = dragView2;
        }

        public void onAlarm(Alarm alarm) {
            int[] resultSpan = new int[2];
            int[] unused = Workspace.this.mTargetCell = Workspace.this.findNearestArea((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.spanX, this.spanY, Workspace.this.mDragTargetLayout, Workspace.this.mTargetCell);
            int unused2 = Workspace.this.mLastReorderX = Workspace.this.mTargetCell[0];
            int unused3 = Workspace.this.mLastReorderY = Workspace.this.mTargetCell[1];
            int[] unused4 = Workspace.this.mTargetCell = Workspace.this.mDragTargetLayout.createArea((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, this.spanX, this.spanY, this.child, Workspace.this.mTargetCell, resultSpan, 0);
            if (Workspace.this.mTargetCell[0] < 0 || Workspace.this.mTargetCell[1] < 0) {
                Workspace.this.mDragTargetLayout.revertTempState();
            } else {
                Workspace.this.setDragMode(3);
            }
            Workspace.this.mDragTargetLayout.visualizeDropLocation(this.child, Workspace.this.mDragOutline, (int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], Workspace.this.mTargetCell[0], Workspace.this.mTargetCell[1], resultSpan[0], resultSpan[1], (resultSpan[0] == this.spanX && resultSpan[1] == this.spanY) ? false : true, this.dragView.getDragVisualizeOffset(), this.dragView.getDragRegion());
        }
    }

    public void getHitRect(Rect outRect) {
        outRect.set(0, 0, this.mDisplaySize.x, this.mDisplaySize.y);
    }

    public boolean addExternalItemToScreen(ItemInfo dragInfo, CellLayout layout) {
        if (layout.findCellForSpan(this.mTempEstimate, dragInfo.spanX, dragInfo.spanY)) {
            onDropExternal(dragInfo.dropPos, dragInfo, layout, false);
            return true;
        }
        this.mLauncher.showOutOfSpaceMessage(this.mLauncher.isHotseatLayout(layout));
        return false;
    }

    private void onDropExternal(int[] touchXY, Object dragInfo, CellLayout cellLayout, boolean insertAtFirst) {
        onDropExternal(touchXY, dragInfo, cellLayout, insertAtFirst, (DropTarget.DragObject) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00c8, code lost:
        if (willAddToExistingUserFolder((com.szchoiceway.index.ItemInfo) r60.dragInfo, r58, r55.mTargetCell, r11) != false) goto L_0x00ca;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onDropExternal(int[] r56, java.lang.Object r57, com.szchoiceway.index.CellLayout r58, boolean r59, com.szchoiceway.index.DropTarget.DragObject r60) {
        /*
            r55 = this;
            com.szchoiceway.index.Workspace$8 r50 = new com.szchoiceway.index.Workspace$8
            r0 = r50
            r1 = r55
            r0.<init>()
            r30 = r57
            com.szchoiceway.index.ItemInfo r30 = (com.szchoiceway.index.ItemInfo) r30
            r0 = r30
            int r5 = r0.spanX
            r0 = r30
            int r6 = r0.spanY
            r0 = r55
            com.szchoiceway.index.CellLayout$CellInfo r2 = r0.mDragInfo
            if (r2 == 0) goto L_0x0027
            r0 = r55
            com.szchoiceway.index.CellLayout$CellInfo r2 = r0.mDragInfo
            int r5 = r2.spanX
            r0 = r55
            com.szchoiceway.index.CellLayout$CellInfo r2 = r0.mDragInfo
            int r6 = r2.spanY
        L_0x0027:
            r0 = r55
            com.szchoiceway.index.Launcher r2 = r0.mLauncher
            r0 = r58
            boolean r2 = r2.isHotseatLayout(r0)
            if (r2 == 0) goto L_0x01a9
            r26 = -101(0xffffffffffffff9b, double:NaN)
        L_0x0035:
            r0 = r55
            r1 = r58
            int r28 = r0.indexOfChild(r1)
            r0 = r55
            com.szchoiceway.index.Launcher r2 = r0.mLauncher
            r0 = r58
            boolean r2 = r2.isHotseatLayout(r0)
            if (r2 != 0) goto L_0x0060
            r0 = r55
            int r2 = r0.mCurrentPage
            r0 = r28
            if (r0 == r2) goto L_0x0060
            r0 = r55
            com.szchoiceway.index.Workspace$State r2 = r0.mState
            com.szchoiceway.index.Workspace$State r3 = com.szchoiceway.index.Workspace.State.SPRING_LOADED
            if (r2 == r3) goto L_0x0060
            r0 = r55
            r1 = r28
            r0.snapToPage(r1)
        L_0x0060:
            r0 = r30
            boolean r2 = r0 instanceof com.szchoiceway.index.PendingAddItemInfo
            if (r2 == 0) goto L_0x01b0
            r24 = r57
            com.szchoiceway.index.PendingAddItemInfo r24 = (com.szchoiceway.index.PendingAddItemInfo) r24
            r51 = 1
            r0 = r24
            int r2 = r0.itemType
            r3 = 1
            if (r2 != r3) goto L_0x00cc
            r2 = 0
            r3 = r56[r2]
            r2 = 1
            r4 = r56[r2]
            r0 = r55
            int[] r8 = r0.mTargetCell
            r2 = r55
            r7 = r58
            int[] r2 = r2.findNearestArea(r3, r4, r5, r6, r7, r8)
            r0 = r55
            r0.mTargetCell = r2
            r0 = r55
            float[] r2 = r0.mDragViewVisualCenter
            r3 = 0
            r2 = r2[r3]
            r0 = r55
            float[] r3 = r0.mDragViewVisualCenter
            r4 = 1
            r3 = r3[r4]
            r0 = r55
            int[] r4 = r0.mTargetCell
            r0 = r58
            float r11 = r0.getDistanceFromCell(r2, r3, r4)
            r0 = r60
            java.lang.Object r8 = r0.dragInfo
            com.szchoiceway.index.ItemInfo r8 = (com.szchoiceway.index.ItemInfo) r8
            r0 = r55
            int[] r10 = r0.mTargetCell
            r12 = 1
            r7 = r55
            r9 = r58
            boolean r2 = r7.willCreateUserFolder(r8, r9, r10, r11, r12)
            if (r2 != 0) goto L_0x00ca
            r0 = r60
            java.lang.Object r2 = r0.dragInfo
            com.szchoiceway.index.ItemInfo r2 = (com.szchoiceway.index.ItemInfo) r2
            r0 = r55
            int[] r3 = r0.mTargetCell
            r0 = r55
            r1 = r58
            boolean r2 = r0.willAddToExistingUserFolder(r2, r1, r3, r11)
            if (r2 == 0) goto L_0x00cc
        L_0x00ca:
            r51 = 0
        L_0x00cc:
            r0 = r60
            java.lang.Object r0 = r0.dragInfo
            r25 = r0
            com.szchoiceway.index.ItemInfo r25 = (com.szchoiceway.index.ItemInfo) r25
            r54 = 0
            if (r51 == 0) goto L_0x014f
            r0 = r25
            int r15 = r0.spanX
            r0 = r25
            int r0 = r0.spanY
            r16 = r0
            r0 = r25
            int r2 = r0.minSpanX
            if (r2 <= 0) goto L_0x00f8
            r0 = r25
            int r2 = r0.minSpanY
            if (r2 <= 0) goto L_0x00f8
            r0 = r25
            int r15 = r0.minSpanX
            r0 = r25
            int r0 = r0.minSpanY
            r16 = r0
        L_0x00f8:
            r2 = 2
            int[] r0 = new int[r2]
            r21 = r0
            r0 = r55
            float[] r2 = r0.mDragViewVisualCenter
            r3 = 0
            r2 = r2[r3]
            int r13 = (int) r2
            r0 = r55
            float[] r2 = r0.mDragViewVisualCenter
            r3 = 1
            r2 = r2[r3]
            int r14 = (int) r2
            r0 = r30
            int r0 = r0.spanX
            r17 = r0
            r0 = r30
            int r0 = r0.spanY
            r18 = r0
            r19 = 0
            r0 = r55
            int[] r0 = r0.mTargetCell
            r20 = r0
            r22 = 2
            r12 = r58
            int[] r2 = r12.createArea(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r0 = r55
            r0.mTargetCell = r2
            r2 = 0
            r2 = r21[r2]
            r0 = r25
            int r3 = r0.spanX
            if (r2 != r3) goto L_0x013f
            r2 = 1
            r2 = r21[r2]
            r0 = r25
            int r3 = r0.spanY
            if (r2 == r3) goto L_0x0141
        L_0x013f:
            r54 = 1
        L_0x0141:
            r2 = 0
            r2 = r21[r2]
            r0 = r25
            r0.spanX = r2
            r2 = 1
            r2 = r21[r2]
            r0 = r25
            r0.spanY = r2
        L_0x014f:
            com.szchoiceway.index.Workspace$9 r22 = new com.szchoiceway.index.Workspace$9
            r23 = r55
            r22.<init>(r24, r25, r26, r28)
            r0 = r24
            int r2 = r0.itemType
            r3 = 4
            if (r2 != r3) goto L_0x01ad
            r2 = r24
            com.szchoiceway.index.PendingAddWidgetInfo r2 = (com.szchoiceway.index.PendingAddWidgetInfo) r2
            android.appwidget.AppWidgetHostView r0 = r2.boundWidget
            r35 = r0
        L_0x0165:
            r0 = r35
            boolean r2 = r0 instanceof android.appwidget.AppWidgetHostView
            if (r2 == 0) goto L_0x0182
            if (r54 == 0) goto L_0x0182
            r49 = r35
            android.appwidget.AppWidgetHostView r49 = (android.appwidget.AppWidgetHostView) r49
            r0 = r55
            com.szchoiceway.index.Launcher r2 = r0.mLauncher
            r0 = r25
            int r3 = r0.spanX
            r0 = r25
            int r4 = r0.spanY
            r0 = r49
            com.szchoiceway.index.AppWidgetResizeFrame.updateWidgetSizeRanges(r0, r2, r3, r4)
        L_0x0182:
            r34 = 0
            r0 = r24
            int r2 = r0.itemType
            r3 = 4
            if (r2 != r3) goto L_0x0197
            com.szchoiceway.index.PendingAddWidgetInfo r24 = (com.szchoiceway.index.PendingAddWidgetInfo) r24
            r0 = r24
            android.appwidget.AppWidgetProviderInfo r2 = r0.info
            android.content.ComponentName r2 = r2.configure
            if (r2 == 0) goto L_0x0197
            r34 = 1
        L_0x0197:
            r0 = r60
            com.szchoiceway.index.DragView r0 = r0.dragView
            r32 = r0
            r36 = 1
            r29 = r55
            r31 = r58
            r33 = r22
            r29.animateWidgetDrop(r30, r31, r32, r33, r34, r35, r36)
        L_0x01a8:
            return
        L_0x01a9:
            r26 = -100
            goto L_0x0035
        L_0x01ad:
            r35 = 0
            goto L_0x0165
        L_0x01b0:
            r37 = 0
            r0 = r30
            int r2 = r0.itemType
            switch(r2) {
                case 0: goto L_0x01d6;
                case 1: goto L_0x01d6;
                case 2: goto L_0x0325;
                default: goto L_0x01b9;
            }
        L_0x01b9:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unknown item type: "
            java.lang.StringBuilder r3 = r3.append(r4)
            r0 = r30
            int r4 = r0.itemType
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x01d6:
            r0 = r30
            long r2 = r0.container
            r8 = -1
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x01f3
            r0 = r30
            boolean r2 = r0 instanceof com.szchoiceway.index.ApplicationInfo
            if (r2 == 0) goto L_0x01f3
            com.szchoiceway.index.ShortcutInfo r52 = new com.szchoiceway.index.ShortcutInfo
            com.szchoiceway.index.ApplicationInfo r30 = (com.szchoiceway.index.ApplicationInfo) r30
            r0 = r52
            r1 = r30
            r0.<init>((com.szchoiceway.index.ApplicationInfo) r1)
            r30 = r52
        L_0x01f3:
            r0 = r55
            com.szchoiceway.index.Launcher r3 = r0.mLauncher
            r4 = 2130968579(0x7f040003, float:1.7545816E38)
            r2 = r30
            com.szchoiceway.index.ShortcutInfo r2 = (com.szchoiceway.index.ShortcutInfo) r2
            r0 = r58
            android.view.View r37 = r3.createShortcut(r4, r0, r2)
        L_0x0204:
            if (r56 == 0) goto L_0x0272
            r2 = 0
            r3 = r56[r2]
            r2 = 1
            r4 = r56[r2]
            r0 = r55
            int[] r8 = r0.mTargetCell
            r2 = r55
            r7 = r58
            int[] r2 = r2.findNearestArea(r3, r4, r5, r6, r7, r8)
            r0 = r55
            r0.mTargetCell = r2
            r0 = r55
            float[] r2 = r0.mDragViewVisualCenter
            r3 = 0
            r2 = r2[r3]
            r0 = r55
            float[] r3 = r0.mDragViewVisualCenter
            r4 = 1
            r3 = r3[r4]
            r0 = r55
            int[] r4 = r0.mTargetCell
            r0 = r58
            float r11 = r0.getDistanceFromCell(r2, r3, r4)
            r0 = r50
            r1 = r60
            r1.postAnimationRunnable = r0
            r0 = r55
            int[] r0 = r0.mTargetCell
            r41 = r0
            r43 = 1
            r0 = r60
            com.szchoiceway.index.DragView r0 = r0.dragView
            r44 = r0
            r0 = r60
            java.lang.Runnable r0 = r0.postAnimationRunnable
            r45 = r0
            r36 = r55
            r38 = r26
            r40 = r58
            r42 = r11
            boolean r2 = r36.createUserFolderIfNecessary(r37, r38, r40, r41, r42, r43, r44, r45)
            if (r2 != 0) goto L_0x01a8
            r0 = r55
            int[] r0 = r0.mTargetCell
            r39 = r0
            r42 = 1
            r36 = r55
            r38 = r58
            r40 = r11
            r41 = r60
            boolean r2 = r36.addToExistingFolderIfNecessary(r37, r38, r39, r40, r41, r42)
            if (r2 != 0) goto L_0x01a8
        L_0x0272:
            if (r56 == 0) goto L_0x033c
            r0 = r55
            float[] r2 = r0.mDragViewVisualCenter
            r3 = 0
            r2 = r2[r3]
            int r0 = (int) r2
            r39 = r0
            r0 = r55
            float[] r2 = r0.mDragViewVisualCenter
            r3 = 1
            r2 = r2[r3]
            int r0 = (int) r2
            r40 = r0
            r41 = 1
            r42 = 1
            r43 = 1
            r44 = 1
            r45 = 0
            r0 = r55
            int[] r0 = r0.mTargetCell
            r46 = r0
            r47 = 0
            r48 = 2
            r38 = r58
            int[] r2 = r38.createArea(r39, r40, r41, r42, r43, r44, r45, r46, r47, r48)
            r0 = r55
            r0.mTargetCell = r2
        L_0x02a6:
            r0 = r55
            int[] r2 = r0.mTargetCell
            r3 = 0
            r41 = r2[r3]
            r0 = r55
            int[] r2 = r0.mTargetCell
            r3 = 1
            r42 = r2[r3]
            r0 = r30
            int r0 = r0.spanX
            r43 = r0
            r0 = r30
            int r0 = r0.spanY
            r44 = r0
            r36 = r55
            r38 = r26
            r40 = r28
            r45 = r59
            r36.addInScreen(r37, r38, r40, r41, r42, r43, r44, r45)
            r0 = r58
            r1 = r37
            r0.onDropChild(r1)
            android.view.ViewGroup$LayoutParams r53 = r37.getLayoutParams()
            com.szchoiceway.index.CellLayout$LayoutParams r53 = (com.szchoiceway.index.CellLayout.LayoutParams) r53
            com.szchoiceway.index.ShortcutAndWidgetContainer r2 = r58.getShortcutsAndWidgets()
            r0 = r37
            r2.measureChild(r0)
            r0 = r55
            com.szchoiceway.index.Launcher r0 = r0.mLauncher
            r38 = r0
            r0 = r53
            int r0 = r0.cellX
            r43 = r0
            r0 = r53
            int r0 = r0.cellY
            r44 = r0
            r39 = r30
            r40 = r26
            r42 = r28
            com.szchoiceway.index.LauncherModel.addOrMoveItemInDatabase(r38, r39, r40, r42, r43, r44)
            r0 = r60
            com.szchoiceway.index.DragView r2 = r0.dragView
            if (r2 == 0) goto L_0x01a8
            r0 = r55
            r1 = r58
            r0.setFinalTransitionTransform(r1)
            r0 = r55
            com.szchoiceway.index.Launcher r2 = r0.mLauncher
            com.szchoiceway.index.DragLayer r2 = r2.getDragLayer()
            r0 = r60
            com.szchoiceway.index.DragView r3 = r0.dragView
            r0 = r37
            r1 = r50
            r2.animateViewIntoPosition(r3, r0, r1)
            r0 = r55
            r1 = r58
            r0.resetTransitionTransform(r1)
            goto L_0x01a8
        L_0x0325:
            r3 = 2130968607(0x7f04001f, float:1.7545872E38)
            r0 = r55
            com.szchoiceway.index.Launcher r4 = r0.mLauncher
            r2 = r30
            com.szchoiceway.index.FolderInfo r2 = (com.szchoiceway.index.FolderInfo) r2
            r0 = r55
            com.szchoiceway.index.IconCache r7 = r0.mIconCache
            r0 = r58
            com.szchoiceway.index.FolderIcon r37 = com.szchoiceway.index.FolderIcon.fromXml(r3, r4, r0, r2, r7)
            goto L_0x0204
        L_0x033c:
            r0 = r55
            int[] r2 = r0.mTargetCell
            r3 = 1
            r4 = 1
            r0 = r58
            r0.findCellForSpan(r2, r3, r4)
            goto L_0x02a6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.Workspace.onDropExternal(int[], java.lang.Object, com.szchoiceway.index.CellLayout, boolean, com.szchoiceway.index.DropTarget$DragObject):void");
    }

    public Bitmap createWidgetBitmap(ItemInfo widgetInfo, View layout) {
        int[] unScaledSize = this.mLauncher.getWorkspace().estimateItemSize(widgetInfo.spanX, widgetInfo.spanY, widgetInfo, false);
        int visibility = layout.getVisibility();
        layout.setVisibility(0);
        int width = View.MeasureSpec.makeMeasureSpec(unScaledSize[0], 1073741824);
        int height = View.MeasureSpec.makeMeasureSpec(unScaledSize[1], 1073741824);
        Bitmap b = Bitmap.createBitmap(unScaledSize[0], unScaledSize[1], Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        layout.measure(width, height);
        layout.layout(0, 0, unScaledSize[0], unScaledSize[1]);
        layout.draw(c);
        c.setBitmap((Bitmap) null);
        layout.setVisibility(visibility);
        return b;
    }

    private void getFinalPositionForDropAnimation(int[] loc, float[] scaleXY, DragView dragView, CellLayout layout, ItemInfo info, int[] targetCell, boolean external, boolean scale) {
        float dragViewScaleX;
        float dragViewScaleY;
        CellLayout cellLayout = layout;
        ItemInfo itemInfo = info;
        Rect r = estimateItemPosition(cellLayout, itemInfo, targetCell[0], targetCell[1], info.spanX, info.spanY);
        loc[0] = r.left;
        loc[1] = r.top;
        setFinalTransitionTransform(layout);
        float cellLayoutScale = this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(layout, loc);
        resetTransitionTransform(layout);
        if (scale) {
            dragViewScaleX = (1.0f * ((float) r.width())) / ((float) dragView.getMeasuredWidth());
            dragViewScaleY = (1.0f * ((float) r.height())) / ((float) dragView.getMeasuredHeight());
        } else {
            dragViewScaleX = 1.0f;
            dragViewScaleY = 1.0f;
        }
        loc[0] = (int) (((float) loc[0]) - ((((float) dragView.getMeasuredWidth()) - (((float) r.width()) * cellLayoutScale)) / WALLPAPER_SCREENS_SPAN));
        loc[1] = (int) (((float) loc[1]) - ((((float) dragView.getMeasuredHeight()) - (((float) r.height()) * cellLayoutScale)) / WALLPAPER_SCREENS_SPAN));
        scaleXY[0] = dragViewScaleX * cellLayoutScale;
        scaleXY[1] = dragViewScaleY * cellLayoutScale;
    }

    public void animateWidgetDrop(ItemInfo info, CellLayout cellLayout, DragView dragView, Runnable onCompleteRunnable, int animationType, View finalView, boolean external) {
        int endStyle;
        Rect from = new Rect();
        this.mLauncher.getDragLayer().getViewRectRelativeToSelf(dragView, from);
        int[] finalPos = new int[2];
        float[] scaleXY = new float[2];
        getFinalPositionForDropAnimation(finalPos, scaleXY, dragView, cellLayout, info, this.mTargetCell, external, !(info instanceof PendingAddShortcutInfo));
        int duration = this.mLauncher.getResources().getInteger(R.integer.config_dropAnimMaxDuration) - 200;
        if ((finalView instanceof AppWidgetHostView) && external) {
            Log.d(TAG, "6557954 Animate widget drop, final view is appWidgetHostView");
            this.mLauncher.getDragLayer().removeView(finalView);
        }
        if ((animationType == 2 || external) && finalView != null) {
            dragView.setCrossFadeBitmap(createWidgetBitmap(info, finalView));
            dragView.crossFade((int) (((float) duration) * 0.8f));
        } else if (info.itemType == 4 && external) {
            float min = Math.min(scaleXY[0], scaleXY[1]);
            scaleXY[1] = min;
            scaleXY[0] = min;
        }
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (animationType == 4) {
            this.mLauncher.getDragLayer().animateViewIntoPosition(dragView, finalPos, 0.0f, 0.1f, 0.1f, 0, onCompleteRunnable, duration);
            return;
        }
        if (animationType == 1) {
            endStyle = 2;
        } else {
            endStyle = 0;
        }
        final View view = finalView;
        final Runnable runnable = onCompleteRunnable;
        dragLayer.animateViewIntoPosition(dragView, from.left, from.top, finalPos[0], finalPos[1], 1.0f, 1.0f, 1.0f, scaleXY[0], scaleXY[1], new Runnable() {
            public void run() {
                if (view != null) {
                    view.setVisibility(0);
                }
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, endStyle, duration, this);
    }

    public void setFinalTransitionTransform(CellLayout layout) {
        if (isSwitchingState()) {
            int index = indexOfChild(layout);
            this.mCurrentScaleX = layout.getScaleX();
            this.mCurrentScaleY = layout.getScaleY();
            this.mCurrentTranslationX = layout.getTranslationX();
            this.mCurrentTranslationY = layout.getTranslationY();
            this.mCurrentRotationY = layout.getRotationY();
            layout.setScaleX(this.mNewScaleXs[index]);
            layout.setScaleY(this.mNewScaleYs[index]);
            layout.setTranslationX(this.mNewTranslationXs[index]);
            layout.setTranslationY(this.mNewTranslationYs[index]);
            layout.setRotationY(this.mNewRotationYs[index]);
        }
    }

    public void resetTransitionTransform(CellLayout layout) {
        if (isSwitchingState()) {
            this.mCurrentScaleX = layout.getScaleX();
            this.mCurrentScaleY = layout.getScaleY();
            this.mCurrentTranslationX = layout.getTranslationX();
            this.mCurrentTranslationY = layout.getTranslationY();
            this.mCurrentRotationY = layout.getRotationY();
            layout.setScaleX(this.mCurrentScaleX);
            layout.setScaleY(this.mCurrentScaleY);
            layout.setTranslationX(this.mCurrentTranslationX);
            layout.setTranslationY(this.mCurrentTranslationY);
            layout.setRotationY(this.mCurrentRotationY);
        }
    }

    public CellLayout getCurrentDropLayout() {
        return (CellLayout) getChildAt(getNextPage());
    }

    public CellLayout.CellInfo getDragInfo() {
        return this.mDragInfo;
    }

    /* access modifiers changed from: private */
    public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, CellLayout layout, int[] recycle) {
        return layout.findNearestArea(pixelX, pixelY, spanX, spanY, recycle);
    }

    /* access modifiers changed from: package-private */
    public void setup(DragController dragController) {
        this.mSpringLoadedDragController = new SpringLoadedDragController(this.mLauncher);
        this.mDragController = dragController;
        updateChildrenLayersEnabled(false);
        setWallpaperDimension();
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        CellLayout cellLayout;
        if (success) {
            if (!(target == this || this.mDragInfo == null)) {
                getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
                if (this.mDragInfo.cell instanceof DropTarget) {
                    this.mDragController.removeDropTarget((DropTarget) this.mDragInfo.cell);
                }
            }
        } else if (this.mDragInfo != null) {
            if (this.mLauncher.isHotseatLayout(target)) {
                cellLayout = this.mLauncher.getHotseat().getLayout();
            } else {
                cellLayout = (CellLayout) getChildAt(this.mDragInfo.screen);
            }
            cellLayout.onDropChild(this.mDragInfo.cell);
        }
        if (d.cancelled && this.mDragInfo.cell != null) {
            this.mDragInfo.cell.setVisibility(0);
        }
        this.mDragOutline = null;
        this.mDragInfo = null;
        hideScrollingIndicator(false);
    }

    /* access modifiers changed from: package-private */
    public void updateItemLocationsInDatabase(CellLayout cl) {
        int count = cl.getShortcutsAndWidgets().getChildCount();
        int screen = indexOfChild(cl);
        int container = -100;
        if (this.mLauncher.isHotseatLayout(cl)) {
            screen = -1;
            container = -101;
        }
        for (int i = 0; i < count; i++) {
            ItemInfo info = (ItemInfo) cl.getShortcutsAndWidgets().getChildAt(i).getTag();
            if (info != null && info.requiresDbUpdate) {
                info.requiresDbUpdate = false;
                LauncherModel.modifyItemInDatabase(this.mLauncher, info, (long) container, screen, info.cellX, info.cellY, info.spanX, info.spanY);
            }
        }
    }

    public boolean supportsFlingToDelete() {
        return true;
    }

    public void onFlingToDelete(DropTarget.DragObject d, int x, int y, PointF vec) {
    }

    public void onFlingToDeleteCompleted() {
    }

    public boolean isDropEnabled() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        Launcher.setScreen(this.mCurrentPage);
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        this.mSavedStates = container;
    }

    public void restoreInstanceStateForChild(int child) {
        if (this.mSavedStates != null) {
            this.mRestoredPages.add(Integer.valueOf(child));
            ((CellLayout) getChildAt(child)).restoreInstanceState(this.mSavedStates);
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
    }

    public void scrollLeft() {
        if (!isSmall() && !this.mIsSwitchingState) {
            super.scrollLeft();
        }
        Folder openFolder = getOpenFolder();
        if (openFolder != null) {
            openFolder.completeDragExit();
        }
    }

    public void scrollRight() {
        if (!isSmall() && !this.mIsSwitchingState) {
            super.scrollRight();
        }
        Folder openFolder = getOpenFolder();
        if (openFolder != null) {
            openFolder.completeDragExit();
        }
    }

    public boolean onEnterScrollArea(int x, int y, int direction) {
        boolean isPortrait;
        int i = 1;
        if (!LauncherApplication.isScreenLandscape(getContext())) {
            isPortrait = true;
        } else {
            isPortrait = false;
        }
        if (this.mLauncher.getHotseat() != null && isPortrait) {
            Rect r = new Rect();
            this.mLauncher.getHotseat().getHitRect(r);
            if (r.contains(x, y)) {
                return false;
            }
        }
        boolean result = false;
        if (!isSmall() && !this.mIsSwitchingState) {
            this.mInScrollArea = true;
            int nextPage = getNextPage();
            if (direction == 0) {
                i = -1;
            }
            int page = nextPage + i;
            setCurrentDropLayout((CellLayout) null);
            if (page >= 0 && page < getChildCount()) {
                setCurrentDragOverlappingLayout((CellLayout) getChildAt(page));
                invalidate();
                result = true;
            }
        }
        return result;
    }

    public boolean onExitScrollArea() {
        if (!this.mInScrollArea) {
            return false;
        }
        invalidate();
        CellLayout layout = getCurrentDropLayout();
        setCurrentDropLayout(layout);
        setCurrentDragOverlappingLayout(layout);
        this.mInScrollArea = false;
        return true;
    }

    private void onResetScrollArea() {
        setCurrentDragOverlappingLayout((CellLayout) null);
        this.mInScrollArea = false;
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

    public Folder getFolderForTag(Object tag) {
        Iterator<ShortcutAndWidgetContainer> it = getAllShortcutAndWidgetContainers().iterator();
        while (it.hasNext()) {
            ShortcutAndWidgetContainer layout = it.next();
            int count = layout.getChildCount();
            int i = 0;
            while (true) {
                if (i < count) {
                    View child = layout.getChildAt(i);
                    if (child instanceof Folder) {
                        Folder f = (Folder) child;
                        if (f.getInfo() == tag && f.getInfo().opened) {
                            return f;
                        }
                    }
                    i++;
                }
            }
        }
        return null;
    }

    public View getViewForTag(Object tag) {
        Iterator<ShortcutAndWidgetContainer> it = getAllShortcutAndWidgetContainers().iterator();
        while (it.hasNext()) {
            ShortcutAndWidgetContainer layout = it.next();
            int count = layout.getChildCount();
            int i = 0;
            while (true) {
                if (i < count) {
                    View child = layout.getChildAt(i);
                    if (child.getTag() == tag) {
                        return child;
                    }
                    i++;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void clearDropTargets() {
        Iterator<ShortcutAndWidgetContainer> it = getAllShortcutAndWidgetContainers().iterator();
        while (it.hasNext()) {
            ShortcutAndWidgetContainer layout = it.next();
            int childCount = layout.getChildCount();
            for (int j = 0; j < childCount; j++) {
                View v = layout.getChildAt(j);
                if (v instanceof DropTarget) {
                    this.mDragController.removeDropTarget((DropTarget) v);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeItemsByPackageName(ArrayList<String> packages) {
        ComponentName cn;
        HashSet<String> packageNames = new HashSet<>();
        packageNames.addAll(packages);
        HashSet<ComponentName> cns = new HashSet<>();
        Iterator<CellLayout> it = getWorkspaceAndHotseatCellLayouts().iterator();
        while (it.hasNext()) {
            ViewGroup layout = it.next().getShortcutsAndWidgets();
            int childCount = layout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Object tag = layout.getChildAt(i).getTag();
                if (tag instanceof ShortcutInfo) {
                    ComponentName cn2 = ((ShortcutInfo) tag).intent.getComponent();
                    if (cn2 != null && packageNames.contains(cn2.getPackageName())) {
                        cns.add(cn2);
                    }
                } else if (tag instanceof FolderInfo) {
                    Iterator<ShortcutInfo> it2 = ((FolderInfo) tag).contents.iterator();
                    while (it2.hasNext()) {
                        ComponentName cn3 = it2.next().intent.getComponent();
                        if (cn3 != null && packageNames.contains(cn3.getPackageName())) {
                            cns.add(cn3);
                        }
                    }
                } else if ((tag instanceof LauncherAppWidgetInfo) && (cn = ((LauncherAppWidgetInfo) tag).providerName) != null && packageNames.contains(cn.getPackageName())) {
                    cns.add(cn);
                }
            }
        }
        removeItemsByComponentName(cns);
    }

    /* access modifiers changed from: package-private */
    public void removeItemsByApplicationInfo(ArrayList<ApplicationInfo> appInfos) {
        HashSet<ComponentName> cns = new HashSet<>();
        Iterator<ApplicationInfo> it = appInfos.iterator();
        while (it.hasNext()) {
            cns.add(it.next().componentName);
        }
        removeItemsByComponentName(cns);
    }

    /* access modifiers changed from: package-private */
    public void removeItemsByComponentName(final HashSet<ComponentName> componentNames) {
        Iterator<CellLayout> it = getWorkspaceAndHotseatCellLayouts().iterator();
        while (it.hasNext()) {
            final CellLayout layoutParent = it.next();
            final ViewGroup layout = layoutParent.getShortcutsAndWidgets();
            post(new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:26:0x00bd, code lost:
                    r8 = (com.szchoiceway.index.LauncherAppWidgetInfo) r15;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r19 = this;
                        java.util.ArrayList r5 = new java.util.ArrayList
                        r5.<init>()
                        r5.clear()
                        r0 = r19
                        android.view.ViewGroup r0 = r2
                        r17 = r0
                        int r4 = r17.getChildCount()
                        r11 = 0
                    L_0x0013:
                        if (r11 >= r4) goto L_0x00e8
                        r0 = r19
                        android.view.ViewGroup r0 = r2
                        r17 = r0
                        r0 = r17
                        android.view.View r16 = r0.getChildAt(r11)
                        java.lang.Object r15 = r16.getTag()
                        boolean r0 = r15 instanceof com.szchoiceway.index.ShortcutInfo
                        r17 = r0
                        if (r17 == 0) goto L_0x005b
                        r8 = r15
                        com.szchoiceway.index.ShortcutInfo r8 = (com.szchoiceway.index.ShortcutInfo) r8
                        android.content.Intent r9 = r8.intent
                        android.content.ComponentName r13 = r9.getComponent()
                        if (r13 == 0) goto L_0x0058
                        r0 = r19
                        java.util.HashSet r0 = r7
                        r17 = r0
                        r0 = r17
                        boolean r17 = r0.contains(r13)
                        if (r17 == 0) goto L_0x0058
                        r0 = r19
                        com.szchoiceway.index.Workspace r0 = com.szchoiceway.index.Workspace.this
                        r17 = r0
                        com.szchoiceway.index.Launcher r17 = r17.mLauncher
                        r0 = r17
                        com.szchoiceway.index.LauncherModel.deleteItemFromDatabase(r0, r8)
                        r0 = r16
                        r5.add(r0)
                    L_0x0058:
                        int r11 = r11 + 1
                        goto L_0x0013
                    L_0x005b:
                        boolean r0 = r15 instanceof com.szchoiceway.index.FolderInfo
                        r17 = r0
                        if (r17 == 0) goto L_0x00b7
                        r8 = r15
                        com.szchoiceway.index.FolderInfo r8 = (com.szchoiceway.index.FolderInfo) r8
                        java.util.ArrayList<com.szchoiceway.index.ShortcutInfo> r6 = r8.contents
                        int r7 = r6.size()
                        java.util.ArrayList r2 = new java.util.ArrayList
                        r2.<init>()
                        r12 = 0
                    L_0x0070:
                        if (r12 >= r7) goto L_0x0094
                        java.lang.Object r1 = r6.get(r12)
                        com.szchoiceway.index.ShortcutInfo r1 = (com.szchoiceway.index.ShortcutInfo) r1
                        android.content.Intent r9 = r1.intent
                        android.content.ComponentName r13 = r9.getComponent()
                        if (r13 == 0) goto L_0x0091
                        r0 = r19
                        java.util.HashSet r0 = r7
                        r17 = r0
                        r0 = r17
                        boolean r17 = r0.contains(r13)
                        if (r17 == 0) goto L_0x0091
                        r2.add(r1)
                    L_0x0091:
                        int r12 = r12 + 1
                        goto L_0x0070
                    L_0x0094:
                        java.util.Iterator r17 = r2.iterator()
                    L_0x0098:
                        boolean r18 = r17.hasNext()
                        if (r18 == 0) goto L_0x0058
                        java.lang.Object r10 = r17.next()
                        com.szchoiceway.index.ShortcutInfo r10 = (com.szchoiceway.index.ShortcutInfo) r10
                        r8.remove(r10)
                        r0 = r19
                        com.szchoiceway.index.Workspace r0 = com.szchoiceway.index.Workspace.this
                        r18 = r0
                        com.szchoiceway.index.Launcher r18 = r18.mLauncher
                        r0 = r18
                        com.szchoiceway.index.LauncherModel.deleteItemFromDatabase(r0, r10)
                        goto L_0x0098
                    L_0x00b7:
                        boolean r0 = r15 instanceof com.szchoiceway.index.LauncherAppWidgetInfo
                        r17 = r0
                        if (r17 == 0) goto L_0x0058
                        r8 = r15
                        com.szchoiceway.index.LauncherAppWidgetInfo r8 = (com.szchoiceway.index.LauncherAppWidgetInfo) r8
                        android.content.ComponentName r14 = r8.providerName
                        if (r14 == 0) goto L_0x0058
                        r0 = r19
                        java.util.HashSet r0 = r7
                        r17 = r0
                        r0 = r17
                        boolean r17 = r0.contains(r14)
                        if (r17 == 0) goto L_0x0058
                        r0 = r19
                        com.szchoiceway.index.Workspace r0 = com.szchoiceway.index.Workspace.this
                        r17 = r0
                        com.szchoiceway.index.Launcher r17 = r17.mLauncher
                        r0 = r17
                        com.szchoiceway.index.LauncherModel.deleteItemFromDatabase(r0, r8)
                        r0 = r16
                        r5.add(r0)
                        goto L_0x0058
                    L_0x00e8:
                        int r4 = r5.size()
                        r11 = 0
                    L_0x00ed:
                        if (r11 >= r4) goto L_0x011a
                        java.lang.Object r3 = r5.get(r11)
                        android.view.View r3 = (android.view.View) r3
                        r0 = r19
                        com.szchoiceway.index.CellLayout r0 = r3
                        r17 = r0
                        r0 = r17
                        r0.removeViewInLayout(r3)
                        boolean r0 = r3 instanceof com.szchoiceway.index.DropTarget
                        r17 = r0
                        if (r17 == 0) goto L_0x0117
                        r0 = r19
                        com.szchoiceway.index.Workspace r0 = com.szchoiceway.index.Workspace.this
                        r17 = r0
                        com.szchoiceway.index.DragController r17 = r17.mDragController
                        com.szchoiceway.index.DropTarget r3 = (com.szchoiceway.index.DropTarget) r3
                        r0 = r17
                        r0.removeDropTarget(r3)
                    L_0x0117:
                        int r11 = r11 + 1
                        goto L_0x00ed
                    L_0x011a:
                        if (r4 <= 0) goto L_0x012e
                        r0 = r19
                        android.view.ViewGroup r0 = r2
                        r17 = r0
                        r17.requestLayout()
                        r0 = r19
                        android.view.ViewGroup r0 = r2
                        r17 = r0
                        r17.invalidate()
                    L_0x012e:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.Workspace.AnonymousClass11.run():void");
                }
            });
        }
        final Context context = getContext();
        post(new Runnable() {
            public void run() {
                Set<String> newApps = context.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0).getStringSet(InstallShortcutReceiver.NEW_APPS_LIST_KEY, (Set) null);
                if (newApps != null) {
                    synchronized (newApps) {
                        Iterator<String> iter = newApps.iterator();
                        while (iter.hasNext()) {
                            try {
                                Intent intent = Intent.parseUri(iter.next(), 0);
                                if (componentNames.contains(intent.getComponent())) {
                                    iter.remove();
                                }
                                Iterator<ItemInfo> it = LauncherModel.getWorkspaceShortcutItemInfosWithIntent(intent).iterator();
                                while (it.hasNext()) {
                                    LauncherModel.deleteItemFromDatabase(context, it.next());
                                }
                            } catch (URISyntaxException e) {
                            }
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void updateShortcuts(ArrayList<ApplicationInfo> apps) {
        Iterator<ShortcutAndWidgetContainer> it = getAllShortcutAndWidgetContainers().iterator();
        while (it.hasNext()) {
            ShortcutAndWidgetContainer layout = it.next();
            int childCount = layout.getChildCount();
            for (int j = 0; j < childCount; j++) {
                View view = layout.getChildAt(j);
                Object tag = view.getTag();
                if (tag instanceof ShortcutInfo) {
                    ShortcutInfo info = (ShortcutInfo) tag;
                    Intent intent = info.intent;
                    ComponentName name = intent.getComponent();
                    if (info.itemType == 0 && "android.intent.action.MAIN".equals(intent.getAction()) && name != null) {
                        int appCount = apps.size();
                        for (int k = 0; k < appCount; k++) {
                            ApplicationInfo app = apps.get(k);
                            if (app.componentName.equals(name)) {
                                info.updateIcon(this.mIconCache);
                                info.title = app.title.toString();
                                ((BubbleTextView) view).applyFromShortcutInfo(info, this.mIconCache);
                            }
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void moveToDefaultScreen(boolean animate) {
        if (!isSmall()) {
            if (animate) {
                snapToPage(this.mDefaultPage);
            } else {
                setCurrentPage(this.mDefaultPage);
            }
        }
        getChildAt(this.mDefaultPage).requestFocus();
    }

    public void syncPages() {
    }

    public void syncPageItems(int page, boolean immediate) {
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return String.format(getContext().getString(R.string.workspace_scroll_format), new Object[]{Integer.valueOf((this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage) + 1), Integer.valueOf(getChildCount())});
    }

    public void getLocationInDragLayer(int[] loc) {
        this.mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }

    /* access modifiers changed from: package-private */
    public void setFadeForOverScroll(float fade) {
        if (isScrollingIndicatorEnabled()) {
            this.mOverscrollFade = fade;
            float reducedFade = 0.5f + ((1.0f - fade) * 0.5f);
            ViewGroup parent = (ViewGroup) getParent();
            ImageView qsbDivider = (ImageView) parent.findViewById(R.id.qsb_divider);
            ImageView dockDivider = (ImageView) parent.findViewById(R.id.dock_divider);
            View scrollIndicator = getScrollingIndicator();
            cancelScrollingIndicatorAnimations();
            if (qsbDivider != null) {
                qsbDivider.setAlpha(reducedFade);
            }
            if (dockDivider != null) {
                dockDivider.setAlpha(reducedFade);
            }
            scrollIndicator.setAlpha(1.0f - fade);
        }
    }
}
