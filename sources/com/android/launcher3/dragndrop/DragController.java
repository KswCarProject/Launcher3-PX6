package com.android.launcher3.dragndrop;

import android.content.ComponentName;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragDriver;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.UiThreadHelper;
import java.util.ArrayList;
import java.util.Iterator;

public class DragController implements DragDriver.EventListener, TouchController {
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    private final int[] mCoordinatesTemp = new int[2];
    int mDistanceSinceScroll = 0;
    private DragDriver mDragDriver = null;
    private Rect mDragLayerRect = new Rect();
    private DropTarget.DragObject mDragObject;
    private ArrayList<DropTarget> mDropTargets = new ArrayList<>();
    private FlingToDeleteHelper mFlingToDeleteHelper;
    private boolean mIsInPreDrag;
    private DropTarget mLastDropTarget;
    int[] mLastTouch = new int[2];
    long mLastTouchUpTime = -1;
    Launcher mLauncher;
    private ArrayList<DragListener> mListeners = new ArrayList<>();
    private int mMotionDownX;
    private int mMotionDownY;
    private View mMoveTarget;
    private DragOptions mOptions;
    private Rect mRectTemp = new Rect();
    private int[] mTmpPoint = new int[2];
    private IBinder mWindowToken;

    public interface DragListener {
        void onDragEnd();

        void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions);
    }

    public DragController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mFlingToDeleteHelper = new FlingToDeleteHelper(launcher);
    }

    public DragView startDrag(Bitmap b, int dragLayerX, int dragLayerY, DragSource source, ItemInfo dragInfo, Point dragOffset, Rect dragRegion, float initialDragViewScale, float dragViewScaleOnDrop, DragOptions options) {
        ItemInfo itemInfo = dragInfo;
        Point point = dragOffset;
        Rect rect = dragRegion;
        UiThreadHelper.hideKeyboardAsync(this.mLauncher, this.mWindowToken);
        this.mOptions = options;
        if (this.mOptions.systemDndStartPoint != null) {
            this.mMotionDownX = this.mOptions.systemDndStartPoint.x;
            this.mMotionDownY = this.mOptions.systemDndStartPoint.y;
        }
        int registrationX = this.mMotionDownX - dragLayerX;
        int registrationY = this.mMotionDownY - dragLayerY;
        int dragRegionLeft = rect == null ? 0 : rect.left;
        int dragRegionTop = rect == null ? 0 : rect.top;
        this.mLastDropTarget = null;
        this.mDragObject = new DropTarget.DragObject();
        this.mIsInPreDrag = this.mOptions.preDragCondition != null && !this.mOptions.preDragCondition.shouldStartDrag(0.0d);
        Resources res = this.mLauncher.getResources();
        DragView dragView = r6;
        Resources resources = res;
        int i = registrationX;
        DragView dragView2 = new DragView(this.mLauncher, b, registrationX, registrationY, initialDragViewScale, dragViewScaleOnDrop, this.mIsInPreDrag ? (float) res.getDimensionPixelSize(R.dimen.pre_drag_view_scale) : 0.0f);
        this.mDragObject.dragView = dragView;
        DragView dragView3 = dragView;
        dragView3.setItemInfo(itemInfo);
        this.mDragObject.dragComplete = false;
        if (this.mOptions.isAccessibleDrag) {
            this.mDragObject.xOffset = b.getWidth() / 2;
            this.mDragObject.yOffset = b.getHeight() / 2;
            this.mDragObject.accessibleDrag = true;
        } else {
            this.mDragObject.xOffset = this.mMotionDownX - (dragLayerX + dragRegionLeft);
            this.mDragObject.yOffset = this.mMotionDownY - (dragLayerY + dragRegionTop);
            this.mDragObject.stateAnnouncer = DragViewStateAnnouncer.createFor(dragView3);
            this.mDragDriver = DragDriver.create(this.mLauncher, this, this.mDragObject, this.mOptions);
        }
        this.mDragObject.dragSource = source;
        this.mDragObject.dragInfo = itemInfo;
        this.mDragObject.originalDragInfo = new ItemInfo();
        this.mDragObject.originalDragInfo.copyFrom(itemInfo);
        if (point != null) {
            dragView3.setDragVisualizeOffset(new Point(point));
        }
        Rect rect2 = dragRegion;
        if (rect2 != null) {
            dragView3.setDragRegion(new Rect(rect2));
        }
        this.mLauncher.getDragLayer().performHapticFeedback(0);
        dragView3.show(this.mMotionDownX, this.mMotionDownY);
        this.mDistanceSinceScroll = 0;
        if (!this.mIsInPreDrag) {
            callOnDragStart();
        } else if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragStart(this.mDragObject);
        }
        this.mLastTouch[0] = this.mMotionDownX;
        this.mLastTouch[1] = this.mMotionDownY;
        handleMoveEvent(this.mMotionDownX, this.mMotionDownY);
        this.mLauncher.getUserEventDispatcher().resetActionDurationMillis();
        return dragView3;
    }

    private void callOnDragStart() {
        if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, true);
        }
        this.mIsInPreDrag = false;
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragStart(this.mDragObject, this.mOptions);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mDragDriver != null;
    }

    public boolean isDragging() {
        return this.mDragDriver != null || (this.mOptions != null && this.mOptions.isAccessibleDrag);
    }

    public void cancelDrag() {
        if (isDragging()) {
            if (this.mLastDropTarget != null) {
                this.mLastDropTarget.onDragExit(this.mDragObject);
            }
            this.mDragObject.deferDragViewCleanupPostAnimation = false;
            this.mDragObject.cancelled = true;
            this.mDragObject.dragComplete = true;
            if (!this.mIsInPreDrag) {
                dispatchDropComplete((View) null, false);
            }
        }
        endDrag();
    }

    private void dispatchDropComplete(View dropTarget, boolean accepted) {
        if (!accepted) {
            this.mLauncher.getStateManager().goToState(LauncherState.NORMAL, 500);
            this.mDragObject.deferDragViewCleanupPostAnimation = false;
        }
        this.mDragObject.dragSource.onDropCompleted(dropTarget, this.mDragObject, accepted);
    }

    public void onAppsRemoved(ItemInfoMatcher matcher) {
        ComponentName cn;
        if (this.mDragObject != null) {
            ItemInfo dragInfo = this.mDragObject.dragInfo;
            if ((dragInfo instanceof ShortcutInfo) && (cn = dragInfo.getTargetComponent()) != null && matcher.matches(dragInfo, cn)) {
                cancelDrag();
            }
        }
    }

    private void endDrag() {
        if (isDragging()) {
            this.mDragDriver = null;
            boolean isDeferred = false;
            if (this.mDragObject.dragView != null) {
                isDeferred = this.mDragObject.deferDragViewCleanupPostAnimation;
                if (!isDeferred) {
                    this.mDragObject.dragView.remove();
                } else if (this.mIsInPreDrag) {
                    animateDragViewToOriginalPosition((Runnable) null, (View) null, -1);
                }
                this.mDragObject.dragView = null;
            }
            if (!isDeferred) {
                callOnDragEnd();
            }
        }
        this.mFlingToDeleteHelper.releaseVelocityTracker();
    }

    public void animateDragViewToOriginalPosition(final Runnable onComplete, final View originalIcon, int duration) {
        this.mDragObject.dragView.animateTo(this.mMotionDownX, this.mMotionDownY, new Runnable() {
            public void run() {
                if (originalIcon != null) {
                    originalIcon.setVisibility(0);
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, duration);
    }

    private void callOnDragEnd() {
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, false);
        }
        this.mIsInPreDrag = false;
        this.mOptions = null;
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragEnd();
        }
    }

    /* access modifiers changed from: package-private */
    public void onDeferredEndDrag(DragView dragView) {
        dragView.remove();
        if (this.mDragObject.deferDragViewCleanupPostAnimation) {
            callOnDragEnd();
        }
    }

    private int[] getClampedDragLayerPos(float x, float y) {
        this.mLauncher.getDragLayer().getLocalVisibleRect(this.mDragLayerRect);
        this.mTmpPoint[0] = (int) Math.max((float) this.mDragLayerRect.left, Math.min(x, (float) (this.mDragLayerRect.right - 1)));
        this.mTmpPoint[1] = (int) Math.max((float) this.mDragLayerRect.top, Math.min(y, (float) (this.mDragLayerRect.bottom - 1)));
        return this.mTmpPoint;
    }

    public long getLastGestureUpTime() {
        if (this.mDragDriver != null) {
            return System.currentTimeMillis();
        }
        return this.mLastTouchUpTime;
    }

    public void resetLastGestureUpTime() {
        this.mLastTouchUpTime = -1;
    }

    public void onDriverDragMove(float x, float y) {
        int[] dragLayerPos = getClampedDragLayerPos(x, y);
        handleMoveEvent(dragLayerPos[0], dragLayerPos[1]);
    }

    public void onDriverDragExitWindow() {
        if (this.mLastDropTarget != null) {
            this.mLastDropTarget.onDragExit(this.mDragObject);
            this.mLastDropTarget = null;
        }
    }

    public void onDriverDragEnd(float x, float y) {
        DropTarget dropTarget;
        Runnable flingAnimation = this.mFlingToDeleteHelper.getFlingAnimation(this.mDragObject);
        if (flingAnimation != null) {
            dropTarget = this.mFlingToDeleteHelper.getDropTarget();
        } else {
            dropTarget = findDropTarget((int) x, (int) y, this.mCoordinatesTemp);
        }
        drop(dropTarget, flingAnimation);
        endDrag();
    }

    public void onDriverDragCancel() {
        cancelDrag();
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (this.mOptions != null && this.mOptions.isAccessibleDrag) {
            return false;
        }
        this.mFlingToDeleteHelper.recordMotionEvent(ev);
        int action = ev.getAction();
        int[] dragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int dragLayerX = dragLayerPos[0];
        int dragLayerY = dragLayerPos[1];
        switch (action) {
            case 0:
                this.mMotionDownX = dragLayerX;
                this.mMotionDownY = dragLayerY;
                break;
            case 1:
                this.mLastTouchUpTime = System.currentTimeMillis();
                break;
        }
        if (this.mDragDriver == null || !this.mDragDriver.onInterceptTouchEvent(ev)) {
            return false;
        }
        return true;
    }

    public boolean onDragEvent(long dragStartTime, DragEvent event) {
        this.mFlingToDeleteHelper.recordDragEvent(dragStartTime, event);
        return this.mDragDriver != null && this.mDragDriver.onDragEvent(event);
    }

    public void onDragViewAnimationEnd() {
        if (this.mDragDriver != null) {
            this.mDragDriver.onDragViewAnimationEnd();
        }
    }

    public void setMoveTarget(View view) {
        this.mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return this.mMoveTarget != null && this.mMoveTarget.dispatchUnhandledMove(focused, direction);
    }

    private void handleMoveEvent(int x, int y) {
        this.mDragObject.dragView.move(x, y);
        int[] coordinates = this.mCoordinatesTemp;
        DropTarget dropTarget = findDropTarget(x, y, coordinates);
        this.mDragObject.x = coordinates[0];
        this.mDragObject.y = coordinates[1];
        checkTouchMove(dropTarget);
        this.mDistanceSinceScroll = (int) (((double) this.mDistanceSinceScroll) + Math.hypot((double) (this.mLastTouch[0] - x), (double) (this.mLastTouch[1] - y)));
        this.mLastTouch[0] = x;
        this.mLastTouch[1] = y;
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null && this.mOptions.preDragCondition.shouldStartDrag((double) this.mDistanceSinceScroll)) {
            callOnDragStart();
        }
    }

    public float getDistanceDragged() {
        return (float) this.mDistanceSinceScroll;
    }

    public void forceTouchMove() {
        int[] dummyCoordinates = this.mCoordinatesTemp;
        DropTarget dropTarget = findDropTarget(this.mLastTouch[0], this.mLastTouch[1], dummyCoordinates);
        this.mDragObject.x = dummyCoordinates[0];
        this.mDragObject.y = dummyCoordinates[1];
        checkTouchMove(dropTarget);
    }

    private void checkTouchMove(DropTarget dropTarget) {
        if (dropTarget != null) {
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

    public boolean onControllerTouchEvent(MotionEvent ev) {
        if (this.mDragDriver == null || this.mOptions == null || this.mOptions.isAccessibleDrag) {
            return false;
        }
        this.mFlingToDeleteHelper.recordMotionEvent(ev);
        int action = ev.getAction();
        int[] dragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int dragLayerX = dragLayerPos[0];
        int dragLayerY = dragLayerPos[1];
        if (action == 0) {
            this.mMotionDownX = dragLayerX;
            this.mMotionDownY = dragLayerY;
        }
        return this.mDragDriver.onTouchEvent(ev);
    }

    public void prepareAccessibleDrag(int x, int y) {
        this.mMotionDownX = x;
        this.mMotionDownY = y;
    }

    public void completeAccessibleDrag(int[] location) {
        int[] coordinates = this.mCoordinatesTemp;
        DropTarget dropTarget = findDropTarget(location[0], location[1], coordinates);
        this.mDragObject.x = coordinates[0];
        this.mDragObject.y = coordinates[1];
        checkTouchMove(dropTarget);
        dropTarget.prepareAccessibilityDrop();
        drop(dropTarget, (Runnable) null);
        endDrag();
    }

    private void drop(DropTarget dropTarget, Runnable flingAnimation) {
        int[] coordinates = this.mCoordinatesTemp;
        this.mDragObject.x = coordinates[0];
        this.mDragObject.y = coordinates[1];
        if (dropTarget != this.mLastDropTarget) {
            if (this.mLastDropTarget != null) {
                this.mLastDropTarget.onDragExit(this.mDragObject);
            }
            this.mLastDropTarget = dropTarget;
            if (dropTarget != null) {
                dropTarget.onDragEnter(this.mDragObject);
            }
        }
        this.mDragObject.dragComplete = true;
        if (!this.mIsInPreDrag) {
            boolean accepted = false;
            if (dropTarget != null) {
                dropTarget.onDragExit(this.mDragObject);
                if (dropTarget.acceptDrop(this.mDragObject)) {
                    if (flingAnimation != null) {
                        flingAnimation.run();
                    } else {
                        dropTarget.onDrop(this.mDragObject, this.mOptions);
                    }
                    accepted = true;
                }
            }
            View dropTargetAsView = dropTarget instanceof View ? (View) dropTarget : null;
            this.mLauncher.getUserEventDispatcher().logDragNDrop(this.mDragObject, dropTargetAsView);
            dispatchDropComplete(dropTargetAsView, accepted);
        } else if (dropTarget != null) {
            dropTarget.onDragExit(this.mDragObject);
        }
    }

    private DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        this.mDragObject.x = x;
        this.mDragObject.y = y;
        Rect r = this.mRectTemp;
        ArrayList<DropTarget> dropTargets = this.mDropTargets;
        for (int i = dropTargets.size() - 1; i >= 0; i--) {
            DropTarget target = dropTargets.get(i);
            if (target.isDropEnabled()) {
                target.getHitRectRelativeToDragLayer(r);
                if (r.contains(x, y)) {
                    dropCoordinates[0] = x;
                    dropCoordinates[1] = y;
                    this.mLauncher.getDragLayer().mapCoordInSelfToDescendant((View) target, dropCoordinates);
                    return target;
                }
            }
        }
        dropCoordinates[0] = x;
        dropCoordinates[1] = y;
        this.mLauncher.getDragLayer().mapCoordInSelfToDescendant(this.mLauncher.getWorkspace(), dropCoordinates);
        return this.mLauncher.getWorkspace();
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
}
