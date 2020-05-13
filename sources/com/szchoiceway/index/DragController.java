package com.szchoiceway.index;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import com.szchoiceway.index.DropTarget;
import java.util.ArrayList;
import java.util.Iterator;

public class DragController {
    public static int DRAG_ACTION_COPY = 1;
    public static int DRAG_ACTION_MOVE = 0;
    private static final float MAX_FLING_DEGREES = 35.0f;
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    private static final int RESCROLL_DELAY = 750;
    private static final int SCROLL_DELAY = 500;
    static final int SCROLL_LEFT = 0;
    static final int SCROLL_NONE = -1;
    private static final int SCROLL_OUTSIDE_ZONE = 0;
    static final int SCROLL_RIGHT = 1;
    private static final int SCROLL_WAITING_IN_ZONE = 1;
    private static final String TAG = "Launcher.DragController";
    private static final int VIBRATE_DURATION = 15;
    private final int[] mCoordinatesTemp = new int[2];
    /* access modifiers changed from: private */
    public int mDistanceSinceScroll = 0;
    private Rect mDragLayerRect = new Rect();
    private DropTarget.DragObject mDragObject;
    /* access modifiers changed from: private */
    public DragScroller mDragScroller;
    private boolean mDragging;
    private ArrayList<DropTarget> mDropTargets = new ArrayList<>();
    private DropTarget mFlingToDeleteDropTarget;
    protected int mFlingToDeleteThresholdVelocity;
    private Handler mHandler;
    private InputMethodManager mInputMethodManager;
    private DropTarget mLastDropTarget;
    /* access modifiers changed from: private */
    public int[] mLastTouch = new int[2];
    private long mLastTouchUpTime = -1;
    /* access modifiers changed from: private */
    public Launcher mLauncher;
    private ArrayList<DragListener> mListeners = new ArrayList<>();
    private int mMotionDownX;
    private int mMotionDownY;
    private View mMoveTarget;
    private Rect mRectTemp = new Rect();
    private ScrollRunnable mScrollRunnable = new ScrollRunnable();
    /* access modifiers changed from: private */
    public int mScrollState = 0;
    private View mScrollView;
    private int mScrollZone;
    private int[] mTmpPoint = new int[2];
    private VelocityTracker mVelocityTracker;
    private final Vibrator mVibrator;
    private IBinder mWindowToken;

    interface DragListener {
        void onDragEnd();

        void onDragStart(DragSource dragSource, Object obj, int i);
    }

    public DragController(Launcher launcher) {
        Resources r = launcher.getResources();
        this.mLauncher = launcher;
        this.mHandler = new Handler();
        this.mScrollZone = r.getDimensionPixelSize(R.dimen.scroll_zone);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mVibrator = (Vibrator) launcher.getSystemService("vibrator");
        this.mFlingToDeleteThresholdVelocity = (int) (((float) r.getInteger(R.integer.config_flingToDeleteMinVelocity)) * r.getDisplayMetrics().density);
    }

    public boolean dragging() {
        return this.mDragging;
    }

    public void startDrag(View v, Bitmap bmp, DragSource source, Object dragInfo, int dragAction, Point extraPadding, float initialDragViewScale) {
        int[] loc = this.mCoordinatesTemp;
        this.mLauncher.getDragLayer().getLocationInDragLayer(v, loc);
        startDrag(bmp, loc[0] + v.getPaddingLeft() + (extraPadding != null ? extraPadding.x : 0) + ((int) (((((float) bmp.getWidth()) * initialDragViewScale) - ((float) bmp.getWidth())) / 2.0f)), loc[1] + v.getPaddingTop() + (extraPadding != null ? extraPadding.y : 0) + ((int) (((((float) bmp.getHeight()) * initialDragViewScale) - ((float) bmp.getHeight())) / 2.0f)), source, dragInfo, dragAction, (Point) null, (Rect) null, initialDragViewScale);
        if (dragAction == DRAG_ACTION_MOVE) {
            v.setVisibility(8);
        }
    }

    public void startDrag(Bitmap b, int dragLayerX, int dragLayerY, DragSource source, Object dragInfo, int dragAction, Point dragOffset, Rect dragRegion, float initialDragViewScale) {
        if (this.mInputMethodManager == null) {
            this.mInputMethodManager = (InputMethodManager) this.mLauncher.getSystemService("input_method");
        }
        this.mInputMethodManager.hideSoftInputFromWindow(this.mWindowToken, 0);
        Iterator<DragListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onDragStart(source, dragInfo, dragAction);
        }
        int registrationX = this.mMotionDownX - dragLayerX;
        int registrationY = this.mMotionDownY - dragLayerY;
        int dragRegionLeft = dragRegion == null ? 0 : dragRegion.left;
        int dragRegionTop = dragRegion == null ? 0 : dragRegion.top;
        this.mDragging = true;
        this.mDragObject = new DropTarget.DragObject();
        this.mDragObject.dragComplete = false;
        this.mDragObject.xOffset = this.mMotionDownX - (dragLayerX + dragRegionLeft);
        this.mDragObject.yOffset = this.mMotionDownY - (dragLayerY + dragRegionTop);
        this.mDragObject.dragSource = source;
        this.mDragObject.dragInfo = dragInfo;
        this.mVibrator.vibrate(15);
        DropTarget.DragObject dragObject = this.mDragObject;
        DragView dragView = new DragView(this.mLauncher, b, registrationX, registrationY, 0, 0, b.getWidth(), b.getHeight(), initialDragViewScale);
        dragObject.dragView = dragView;
        if (dragOffset != null) {
            dragView.setDragVisualizeOffset(new Point(dragOffset));
        }
        if (dragRegion != null) {
            dragView.setDragRegion(new Rect(dragRegion));
        }
        dragView.show(this.mMotionDownX, this.mMotionDownY);
        handleMoveEvent(this.mMotionDownX, this.mMotionDownY);
    }

    /* access modifiers changed from: package-private */
    public Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        float alpha = v.getAlpha();
        v.setAlpha(1.0f);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e(TAG, "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setAlpha(alpha);
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return createBitmap;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mDragging;
    }

    public boolean isDragging() {
        return this.mDragging;
    }

    public void cancelDrag() {
        Log.i(TAG, "cancelDrag");
        if (this.mDragging) {
            if (this.mLastDropTarget != null) {
                this.mLastDropTarget.onDragExit(this.mDragObject);
            }
            if (this.mDragObject != null) {
                this.mDragObject.deferDragViewCleanupPostAnimation = false;
                this.mDragObject.cancelled = true;
                this.mDragObject.dragComplete = true;
                if (this.mDragObject.dragSource != null) {
                    this.mDragObject.dragSource.onDropCompleted((View) null, this.mDragObject, false, false);
                }
            }
        }
        endDrag();
    }

    public void onAppsRemoved(ArrayList<ApplicationInfo> appInfos, Context context) {
        if (this.mDragObject != null) {
            Object rawDragInfo = this.mDragObject.dragInfo;
            if (rawDragInfo instanceof ShortcutInfo) {
                ShortcutInfo dragInfo = (ShortcutInfo) rawDragInfo;
                Iterator<ApplicationInfo> it = appInfos.iterator();
                while (it.hasNext()) {
                    ApplicationInfo info = it.next();
                    if (dragInfo != null && dragInfo.intent != null && dragInfo.intent.getComponent().equals(info.componentName)) {
                        cancelDrag();
                        return;
                    }
                }
            }
        }
    }

    private void endDrag() {
        if (this.mDragging) {
            this.mDragging = false;
            clearScrollRunnable();
            boolean isDeferred = false;
            if (this.mDragObject.dragView != null) {
                isDeferred = this.mDragObject.deferDragViewCleanupPostAnimation;
                if (!isDeferred) {
                    this.mDragObject.dragView.remove();
                }
                this.mDragObject.dragView = null;
            }
            if (!isDeferred) {
                Iterator<DragListener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onDragEnd();
                }
            }
        }
        releaseVelocityTracker();
    }

    /* access modifiers changed from: package-private */
    public void onDeferredEndDrag(DragView dragView) {
        dragView.remove();
        Iterator<DragListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onDragEnd();
        }
    }

    /* access modifiers changed from: package-private */
    public void onDeferredEndFling(DropTarget.DragObject d) {
        d.dragSource.onFlingToDeleteCompleted();
    }

    private int[] getClampedDragLayerPos(float x, float y) {
        this.mLauncher.getDragLayer().getLocalVisibleRect(this.mDragLayerRect);
        this.mTmpPoint[0] = (int) Math.max((float) this.mDragLayerRect.left, Math.min(x, (float) (this.mDragLayerRect.right - 1)));
        this.mTmpPoint[1] = (int) Math.max((float) this.mDragLayerRect.top, Math.min(y, (float) (this.mDragLayerRect.bottom - 1)));
        return this.mTmpPoint;
    }

    /* access modifiers changed from: package-private */
    public long getLastGestureUpTime() {
        if (this.mDragging) {
            return System.currentTimeMillis();
        }
        return this.mLastTouchUpTime;
    }

    /* access modifiers changed from: package-private */
    public void resetLastGestureUpTime() {
        this.mLastTouchUpTime = -1;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction();
        int[] dragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int dragLayerX = dragLayerPos[0];
        int dragLayerY = dragLayerPos[1];
        switch (action) {
            case 0:
                this.mMotionDownX = dragLayerX;
                this.mMotionDownY = dragLayerY;
                this.mLastDropTarget = null;
                break;
            case 1:
                this.mLastTouchUpTime = System.currentTimeMillis();
                if (this.mDragging) {
                    PointF vec = isFlingingToDelete(this.mDragObject.dragSource);
                    if (vec != null) {
                        dropOnFlingToDeleteTarget((float) dragLayerX, (float) dragLayerY, vec);
                    } else {
                        drop((float) dragLayerX, (float) dragLayerY);
                    }
                }
                endDrag();
                break;
            case 3:
                cancelDrag();
                break;
        }
        return this.mDragging;
    }

    /* access modifiers changed from: package-private */
    public void setMoveTarget(View view) {
        this.mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return this.mMoveTarget != null && this.mMoveTarget.dispatchUnhandledMove(focused, direction);
    }

    private void clearScrollRunnable() {
        this.mHandler.removeCallbacks(this.mScrollRunnable);
        if (this.mScrollState == 1) {
            this.mScrollState = 0;
            this.mScrollRunnable.setDirection(1);
            this.mDragScroller.onExitScrollArea();
            this.mLauncher.getDragLayer().onExitScrollArea();
        }
    }

    private void handleMoveEvent(int x, int y) {
        this.mDragObject.dragView.move(x, y);
        int[] coordinates = this.mCoordinatesTemp;
        DropTarget dropTarget = findDropTarget(x, y, coordinates);
        this.mDragObject.x = coordinates[0];
        this.mDragObject.y = coordinates[1];
        checkTouchMove(dropTarget);
        this.mDistanceSinceScroll = (int) (((double) this.mDistanceSinceScroll) + Math.sqrt(Math.pow((double) (this.mLastTouch[0] - x), 2.0d) + Math.pow((double) (this.mLastTouch[1] - y), 2.0d)));
        this.mLastTouch[0] = x;
        this.mLastTouch[1] = y;
        checkScrollState(x, y);
    }

    public void forceTouchMove() {
        checkTouchMove(findDropTarget(this.mLastTouch[0], this.mLastTouch[1], this.mCoordinatesTemp));
    }

    private void checkTouchMove(DropTarget dropTarget) {
        if (dropTarget != null) {
            DropTarget delegate = dropTarget.getDropTargetDelegate(this.mDragObject);
            if (delegate != null) {
                dropTarget = delegate;
            }
            if (this.mLastDropTarget != dropTarget) {
                if (this.mLastDropTarget != null) {
                    this.mLastDropTarget.onDragExit(this.mDragObject);
                }
                dropTarget.onDragEnter(this.mDragObject);
            }
            dropTarget.onDragOver(this.mDragObject);
        } else if (this.mLastDropTarget != null) {
            this.mLastDropTarget.onDragExit(this.mDragObject);
        }
        this.mLastDropTarget = dropTarget;
    }

    /* access modifiers changed from: private */
    public void checkScrollState(int x, int y) {
        boolean isRtl;
        int forwardDirection;
        int backwardsDirection = 0;
        int delay = this.mDistanceSinceScroll < ViewConfiguration.get(this.mLauncher).getScaledWindowTouchSlop() ? RESCROLL_DELAY : 500;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (dragLayer.getLayoutDirection() == 1) {
            isRtl = true;
        } else {
            isRtl = false;
        }
        if (isRtl) {
            forwardDirection = 1;
        } else {
            forwardDirection = 0;
        }
        if (!isRtl) {
            backwardsDirection = 1;
        }
        if (x < this.mScrollZone) {
            if (this.mScrollState == 0) {
                this.mScrollState = 1;
                if (this.mDragScroller.onEnterScrollArea(x, y, forwardDirection)) {
                    dragLayer.onEnterScrollArea(forwardDirection);
                    this.mScrollRunnable.setDirection(forwardDirection);
                    this.mHandler.postDelayed(this.mScrollRunnable, (long) delay);
                }
            }
        } else if (x <= this.mScrollView.getWidth() - this.mScrollZone) {
            clearScrollRunnable();
        } else if (this.mScrollState == 0) {
            this.mScrollState = 1;
            if (this.mDragScroller.onEnterScrollArea(x, y, backwardsDirection)) {
                dragLayer.onEnterScrollArea(backwardsDirection);
                this.mScrollRunnable.setDirection(backwardsDirection);
                this.mHandler.postDelayed(this.mScrollRunnable, (long) delay);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mDragging) {
            return false;
        }
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction();
        int[] dragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int dragLayerX = dragLayerPos[0];
        int dragLayerY = dragLayerPos[1];
        switch (action) {
            case 0:
                this.mMotionDownX = dragLayerX;
                this.mMotionDownY = dragLayerY;
                if (dragLayerX >= this.mScrollZone && dragLayerX <= this.mScrollView.getWidth() - this.mScrollZone) {
                    this.mScrollState = 0;
                    break;
                } else {
                    this.mScrollState = 1;
                    this.mHandler.postDelayed(this.mScrollRunnable, 500);
                    break;
                }
            case 1:
                handleMoveEvent(dragLayerX, dragLayerY);
                this.mHandler.removeCallbacks(this.mScrollRunnable);
                if (this.mDragging) {
                    PointF vec = isFlingingToDelete(this.mDragObject.dragSource);
                    if (vec != null) {
                        dropOnFlingToDeleteTarget((float) dragLayerX, (float) dragLayerY, vec);
                    } else {
                        drop((float) dragLayerX, (float) dragLayerY);
                    }
                }
                endDrag();
                break;
            case 2:
                handleMoveEvent(dragLayerX, dragLayerY);
                break;
            case 3:
                this.mHandler.removeCallbacks(this.mScrollRunnable);
                cancelDrag();
                break;
        }
        return true;
    }

    private PointF isFlingingToDelete(DragSource source) {
        if (this.mFlingToDeleteDropTarget == null) {
            return null;
        }
        if (!source.supportsFlingToDelete()) {
            return null;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, (float) ViewConfiguration.get(this.mLauncher).getScaledMaximumFlingVelocity());
        if (this.mVelocityTracker.getYVelocity() < ((float) this.mFlingToDeleteThresholdVelocity)) {
            PointF vel = new PointF(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
            PointF upVec = new PointF(0.0f, -1.0f);
            if (((double) ((float) Math.acos((double) (((vel.x * upVec.x) + (vel.y * upVec.y)) / (vel.length() * upVec.length()))))) <= Math.toRadians(35.0d)) {
                return vel;
            }
        }
        return null;
    }

    private void dropOnFlingToDeleteTarget(float x, float y, PointF vel) {
        int[] coordinates = this.mCoordinatesTemp;
        this.mDragObject.x = coordinates[0];
        this.mDragObject.y = coordinates[1];
        if (!(this.mLastDropTarget == null || this.mFlingToDeleteDropTarget == this.mLastDropTarget)) {
            this.mLastDropTarget.onDragExit(this.mDragObject);
        }
        boolean accepted = false;
        this.mFlingToDeleteDropTarget.onDragEnter(this.mDragObject);
        this.mDragObject.dragComplete = true;
        this.mFlingToDeleteDropTarget.onDragExit(this.mDragObject);
        if (this.mFlingToDeleteDropTarget.acceptDrop(this.mDragObject)) {
            this.mFlingToDeleteDropTarget.onFlingToDelete(this.mDragObject, this.mDragObject.x, this.mDragObject.y, vel);
            accepted = true;
        }
        this.mDragObject.dragSource.onDropCompleted((View) this.mFlingToDeleteDropTarget, this.mDragObject, true, accepted);
    }

    private boolean drop(float x, float y) {
        int[] coordinates = this.mCoordinatesTemp;
        DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
        if (dropTarget == null && this.mLauncher != null) {
            Log.i(TAG, "dropTarget == null" + this.mDragObject.getClass().getName());
            dropTarget = this.mLauncher.getWorkspace();
        }
        this.mDragObject.x = coordinates[0];
        this.mDragObject.y = coordinates[1];
        boolean accepted = false;
        if (dropTarget != null) {
            this.mDragObject.dragComplete = true;
            dropTarget.onDragExit(this.mDragObject);
            if (dropTarget.acceptDrop(this.mDragObject)) {
                Log.i(TAG, "dropTarget.onDrop(mDragObject)");
                dropTarget.onDrop(this.mDragObject);
                accepted = true;
            }
        }
        this.mDragObject.dragSource.onDropCompleted((View) dropTarget, this.mDragObject, false, accepted);
        return accepted;
    }

    private DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        Rect r = this.mRectTemp;
        ArrayList<DropTarget> dropTargets = this.mDropTargets;
        for (int i = dropTargets.size() - 1; i >= 0; i--) {
            DropTarget target = dropTargets.get(i);
            if (target.isDropEnabled()) {
                target.getHitRect(r);
                target.getLocationInDragLayer(dropCoordinates);
                r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());
                this.mDragObject.x = x;
                this.mDragObject.y = y;
                if (r.contains(x, y)) {
                    DropTarget delegate = target.getDropTargetDelegate(this.mDragObject);
                    if (delegate != null) {
                        target = delegate;
                        target.getLocationInDragLayer(dropCoordinates);
                    }
                    dropCoordinates[0] = x - dropCoordinates[0];
                    dropCoordinates[1] = y - dropCoordinates[1];
                    return target;
                }
            }
        }
        return null;
    }

    public void setDragScoller(DragScroller scroller) {
        this.mDragScroller = scroller;
    }

    public void setWindowToken(IBinder token) {
        this.mWindowToken = token;
    }

    public void addDragListener(DragListener l) {
        this.mListeners.add(l);
    }

    public void removeDragListener(DragListener l) {
        this.mListeners.remove(l);
    }

    public void addDropTarget(DropTarget target) {
        this.mDropTargets.add(target);
    }

    public void removeDropTarget(DropTarget target) {
        this.mDropTargets.remove(target);
    }

    public void setFlingToDeleteDropTarget(DropTarget target) {
        this.mFlingToDeleteDropTarget = target;
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void setScrollView(View v) {
        this.mScrollView = v;
    }

    /* access modifiers changed from: package-private */
    public DragView getDragView() {
        return this.mDragObject.dragView;
    }

    private class ScrollRunnable implements Runnable {
        private int mDirection;

        ScrollRunnable() {
        }

        public void run() {
            if (DragController.this.mDragScroller != null) {
                if (this.mDirection == 0) {
                    DragController.this.mDragScroller.scrollLeft();
                } else {
                    DragController.this.mDragScroller.scrollRight();
                }
                int unused = DragController.this.mScrollState = 0;
                int unused2 = DragController.this.mDistanceSinceScroll = 0;
                DragController.this.mDragScroller.onExitScrollArea();
                DragController.this.mLauncher.getDragLayer().onExitScrollArea();
                if (DragController.this.isDragging()) {
                    DragController.this.checkScrollState(DragController.this.mLastTouch[0], DragController.this.mLastTouch[1]);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setDirection(int direction) {
            this.mDirection = direction;
        }
    }
}
