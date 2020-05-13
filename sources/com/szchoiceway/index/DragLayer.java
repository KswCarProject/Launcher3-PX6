package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.szchoiceway.index.CellLayout;
import java.util.ArrayList;
import java.util.Iterator;

public class DragLayer extends FrameLayout implements ViewGroup.OnHierarchyChangeListener {
    public static final int ANIMATION_END_DISAPPEAR = 0;
    public static final int ANIMATION_END_FADE_OUT = 1;
    public static final int ANIMATION_END_REMAIN_VISIBLE = 2;
    /* access modifiers changed from: private */
    public View mAnchorView = null;
    /* access modifiers changed from: private */
    public int mAnchorViewInitialScrollX = 0;
    private TimeInterpolator mCubicEaseOutInterpolator = new DecelerateInterpolator(1.5f);
    private AppWidgetResizeFrame mCurrentResizeFrame;
    /* access modifiers changed from: private */
    public DragController mDragController;
    private ValueAnimator mDropAnim = null;
    /* access modifiers changed from: private */
    public DragView mDropView = null;
    private ValueAnimator mFadeOutAnim = null;
    private Rect mHitRect = new Rect();
    private boolean mHoverPointClosesFolder = false;
    private boolean mInScrollArea;
    private Launcher mLauncher;
    private Drawable mLeftHoverDrawable;
    private int mQsbIndex = -1;
    private final ArrayList<AppWidgetResizeFrame> mResizeFrames = new ArrayList<>();
    private Drawable mRightHoverDrawable;
    private int[] mTmpXY = new int[2];
    private int mWorkspaceIndex = -1;
    private int mXDown;
    private int mYDown;

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        setOnHierarchyChangeListener(this);
        this.mLeftHoverDrawable = getResources().getDrawable(R.drawable.page_hover_left_holo);
        this.mRightHoverDrawable = getResources().getDrawable(R.drawable.page_hover_right_holo);
    }

    public void setup(Launcher launcher, DragController controller) {
        this.mLauncher = launcher;
        this.mDragController = controller;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    private boolean isEventOverFolderTextRegion(Folder folder, MotionEvent ev) {
        getDescendantRectRelativeToSelf(folder.getEditTextRegion(), this.mHitRect);
        if (this.mHitRect.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }
        return false;
    }

    private boolean isEventOverFolder(Folder folder, MotionEvent ev) {
        getDescendantRectRelativeToSelf(folder, this.mHitRect);
        if (this.mHitRect.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }
        return false;
    }

    private boolean handleTouchDown(MotionEvent ev, boolean intercept) {
        Rect hitRect = new Rect();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        Iterator<AppWidgetResizeFrame> it = this.mResizeFrames.iterator();
        while (it.hasNext()) {
            AppWidgetResizeFrame child = it.next();
            child.getHitRect(hitRect);
            if (hitRect.contains(x, y) && child.beginResizeIfPointInRegion(x - child.getLeft(), y - child.getTop())) {
                this.mCurrentResizeFrame = child;
                this.mXDown = x;
                this.mYDown = y;
                requestDisallowInterceptTouchEvent(true);
                return true;
            }
        }
        Folder currentFolder = this.mLauncher.getWorkspace().getOpenFolder();
        if (currentFolder != null && !this.mLauncher.isFolderClingVisible() && intercept) {
            if (!currentFolder.isEditingName() || isEventOverFolderTextRegion(currentFolder, ev)) {
                getDescendantRectRelativeToSelf(currentFolder, hitRect);
                if (!isEventOverFolder(currentFolder, ev)) {
                    this.mLauncher.closeFolder();
                    return true;
                }
            } else {
                currentFolder.dismissEditingName();
                return true;
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && handleTouchDown(ev, true)) {
            return true;
        }
        clearAllResizeFrames();
        return this.mDragController.onInterceptTouchEvent(ev);
    }

    public boolean onInterceptHoverEvent(MotionEvent ev) {
        Folder currentFolder;
        if (this.mLauncher == null || this.mLauncher.getWorkspace() == null || (currentFolder = this.mLauncher.getWorkspace().getOpenFolder()) == null || !((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            return false;
        }
        switch (ev.getAction()) {
            case 7:
                break;
            case 9:
                boolean isOverFolder = isEventOverFolder(currentFolder, ev);
                if (!isOverFolder) {
                    sendTapOutsideFolderAccessibilityEvent(currentFolder.isEditingName());
                    this.mHoverPointClosesFolder = true;
                    return true;
                } else if (isOverFolder) {
                    this.mHoverPointClosesFolder = false;
                    break;
                } else {
                    return true;
                }
            default:
                return false;
        }
        boolean isOverFolder2 = isEventOverFolder(currentFolder, ev);
        if (!isOverFolder2 && !this.mHoverPointClosesFolder) {
            sendTapOutsideFolderAccessibilityEvent(currentFolder.isEditingName());
            this.mHoverPointClosesFolder = true;
            return true;
        } else if (!isOverFolder2) {
            return true;
        } else {
            this.mHoverPointClosesFolder = false;
            return false;
        }
    }

    private void sendTapOutsideFolderAccessibilityEvent(boolean isEditingName) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            int stringId = isEditingName ? R.string.folder_tap_to_rename : R.string.folder_tap_to_close;
            AccessibilityEvent event = AccessibilityEvent.obtain(8);
            onInitializeAccessibilityEvent(event);
            event.getText().add(getContext().getString(stringId));
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        Folder currentFolder = this.mLauncher.getWorkspace().getOpenFolder();
        if (currentFolder == null) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        if (child == currentFolder) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        return false;
    }

    public void addChildrenForAccessibility(ArrayList<View> childrenForAccessibility) {
        Folder currentFolder = this.mLauncher.getWorkspace().getOpenFolder();
        if (currentFolder != null) {
            childrenForAccessibility.add(currentFolder);
        } else {
            super.addChildrenForAccessibility(childrenForAccessibility);
        }
    }

    public boolean onHoverEvent(MotionEvent ev) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (ev.getAction() == 0 && ev.getAction() == 0 && handleTouchDown(ev, false)) {
            return true;
        }
        if (this.mCurrentResizeFrame != null) {
            handled = true;
            switch (action) {
                case 1:
                case 3:
                    this.mCurrentResizeFrame.visualizeResizeForDelta(x - this.mXDown, y - this.mYDown);
                    this.mCurrentResizeFrame.onTouchUp();
                    this.mCurrentResizeFrame = null;
                    break;
                case 2:
                    this.mCurrentResizeFrame.visualizeResizeForDelta(x - this.mXDown, y - this.mYDown);
                    break;
            }
        }
        if (!handled) {
            return this.mDragController.onTouchEvent(ev);
        }
        return true;
    }

    public float getDescendantRectRelativeToSelf(View descendant, Rect r) {
        this.mTmpXY[0] = 0;
        this.mTmpXY[1] = 0;
        float scale = getDescendantCoordRelativeToSelf(descendant, this.mTmpXY);
        r.set(this.mTmpXY[0], this.mTmpXY[1], this.mTmpXY[0] + descendant.getWidth(), this.mTmpXY[1] + descendant.getHeight());
        return scale;
    }

    public float getLocationInDragLayer(View child, int[] loc) {
        loc[0] = 0;
        loc[1] = 0;
        return getDescendantCoordRelativeToSelf(child, loc);
    }

    public float getDescendantCoordRelativeToSelf(View descendant, int[] coord) {
        float[] pt = {(float) coord[0], (float) coord[1]};
        descendant.getMatrix().mapPoints(pt);
        float scale = 1.0f * descendant.getScaleX();
        pt[0] = pt[0] + ((float) descendant.getLeft());
        pt[1] = pt[1] + ((float) descendant.getTop());
        ViewParent viewParent = descendant.getParent();
        while ((viewParent instanceof View) && viewParent != this) {
            View view = (View) viewParent;
            view.getMatrix().mapPoints(pt);
            scale *= view.getScaleX();
            pt[0] = pt[0] + ((float) (view.getLeft() - view.getScrollX()));
            pt[1] = pt[1] + ((float) (view.getTop() - view.getScrollY()));
            viewParent = view.getParent();
        }
        coord[0] = Math.round(pt[0]);
        coord[1] = Math.round(pt[1]);
        return scale;
    }

    public void getViewRectRelativeToSelf(View v, Rect r) {
        int[] loc = new int[2];
        getLocationInWindow(loc);
        int x = loc[0];
        int y = loc[1];
        v.getLocationInWindow(loc);
        int left = loc[0] - x;
        int top = loc[1] - y;
        r.set(left, top, v.getMeasuredWidth() + left, v.getMeasuredHeight() + top);
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return this.mDragController.dispatchUnhandledMove(focused, direction);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean customPosition = false;
        public int x;
        public int y;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return this.width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }

        public void setX(int x2) {
            this.x = x2;
        }

        public int getX() {
            return this.x;
        }

        public void setY(int y2) {
            this.y = y2;
        }

        public int getY() {
            return this.y;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) child.getLayoutParams();
            if (flp instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) flp;
                if (lp.customPosition) {
                    child.layout(lp.x, lp.y, lp.x + lp.width, lp.y + lp.height);
                }
            }
        }
    }

    public void clearAllResizeFrames() {
        if (this.mResizeFrames.size() > 0) {
            Iterator<AppWidgetResizeFrame> it = this.mResizeFrames.iterator();
            while (it.hasNext()) {
                AppWidgetResizeFrame frame = it.next();
                frame.commitResize();
                removeView(frame);
            }
            this.mResizeFrames.clear();
        }
    }

    public boolean hasResizeFrames() {
        return this.mResizeFrames.size() > 0;
    }

    public boolean isWidgetBeingResized() {
        return this.mCurrentResizeFrame != null;
    }

    public void addResizeFrame(ItemInfo itemInfo, LauncherAppWidgetHostView widget, CellLayout cellLayout) {
        AppWidgetResizeFrame resizeFrame = new AppWidgetResizeFrame(getContext(), widget, cellLayout, this);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.customPosition = true;
        addView(resizeFrame, lp);
        this.mResizeFrames.add(resizeFrame);
        resizeFrame.snapToWidget(false);
    }

    public void animateViewIntoPosition(DragView dragView, View child) {
        animateViewIntoPosition(dragView, child, (Runnable) null);
    }

    public void animateViewIntoPosition(DragView dragView, int[] pos, float alpha, float scaleX, float scaleY, int animationEndStyle, Runnable onFinishRunnable, int duration) {
        Rect r = new Rect();
        getViewRectRelativeToSelf(dragView, r);
        animateViewIntoPosition(dragView, r.left, r.top, pos[0], pos[1], alpha, 1.0f, 1.0f, scaleX, scaleY, onFinishRunnable, animationEndStyle, duration, (View) null);
    }

    public void animateViewIntoPosition(DragView dragView, View child, Runnable onFinishAnimationRunnable) {
        animateViewIntoPosition(dragView, child, -1, onFinishAnimationRunnable, (View) null);
    }

    public void animateViewIntoPosition(DragView dragView, View child, int duration, Runnable onFinishAnimationRunnable, View anchorView) {
        int toY;
        int toX;
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        ((ShortcutAndWidgetContainer) child.getParent()).measureChild(child);
        Rect r = new Rect();
        getViewRectRelativeToSelf(dragView, r);
        float childScale = child.getScaleX();
        int[] coord = {lp.x + ((int) ((((float) child.getMeasuredWidth()) * (1.0f - childScale)) / 2.0f)), lp.y + ((int) ((((float) child.getMeasuredHeight()) * (1.0f - childScale)) / 2.0f))};
        float scale = getDescendantCoordRelativeToSelf((View) child.getParent(), coord) * childScale;
        int toX2 = coord[0];
        int toY2 = coord[1];
        if (child instanceof TextView) {
            toY = (int) (((float) (toY2 + Math.round(((float) ((TextView) child).getPaddingTop()) * scale))) - ((((float) dragView.getMeasuredHeight()) * (1.0f - scale)) / 2.0f));
            toX = toX2 - ((dragView.getMeasuredWidth() - Math.round(((float) child.getMeasuredWidth()) * scale)) / 2);
        } else if (child instanceof FolderIcon) {
            toY = (int) (((float) ((int) (((float) toY2) - ((2.0f * scale) / 2.0f)))) - (((1.0f - scale) * ((float) dragView.getMeasuredHeight())) / 2.0f));
            toX = toX2 - ((dragView.getMeasuredWidth() - Math.round(((float) child.getMeasuredWidth()) * scale)) / 2);
        } else {
            toY = toY2 - (Math.round(((float) (dragView.getHeight() - child.getMeasuredHeight())) * scale) / 2);
            toX = toX2 - (Math.round(((float) (dragView.getMeasuredWidth() - child.getMeasuredWidth())) * scale) / 2);
        }
        int fromX = r.left;
        int fromY = r.top;
        child.setVisibility(4);
        final View view = child;
        final Runnable runnable = onFinishAnimationRunnable;
        animateViewIntoPosition(dragView, fromX, fromY, toX, toY, 1.0f, 1.0f, 1.0f, scale, scale, new Runnable() {
            public void run() {
                view.setVisibility(0);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, 0, duration, anchorView);
    }

    public void animateViewIntoPosition(DragView view, int fromX, int fromY, int toX, int toY, float finalAlpha, float initScaleX, float initScaleY, float finalScaleX, float finalScaleY, Runnable onCompleteRunnable, int animationEndStyle, int duration, View anchorView) {
        animateView(view, new Rect(fromX, fromY, view.getMeasuredWidth() + fromX, view.getMeasuredHeight() + fromY), new Rect(toX, toY, view.getMeasuredWidth() + toX, view.getMeasuredHeight() + toY), finalAlpha, initScaleX, initScaleY, finalScaleX, finalScaleY, duration, (Interpolator) null, (Interpolator) null, onCompleteRunnable, animationEndStyle, anchorView);
    }

    public void animateView(DragView view, Rect from, Rect to, float finalAlpha, float initScaleX, float initScaleY, float finalScaleX, float finalScaleY, int duration, Interpolator motionInterpolator, Interpolator alphaInterpolator, Runnable onCompleteRunnable, int animationEndStyle, View anchorView) {
        float dist = (float) Math.sqrt(Math.pow((double) (to.left - from.left), 2.0d) + Math.pow((double) (to.top - from.top), 2.0d));
        Resources res = getResources();
        float maxDist = (float) res.getInteger(R.integer.config_dropAnimMaxDist);
        if (duration < 0) {
            int duration2 = res.getInteger(R.integer.config_dropAnimMaxDuration);
            if (dist < maxDist) {
                duration2 = (int) (((float) duration2) * this.mCubicEaseOutInterpolator.getInterpolation(dist / maxDist));
            }
            duration = Math.max(duration2, res.getInteger(R.integer.config_dropAnimMinDuration));
        }
        TimeInterpolator interpolator = null;
        if (alphaInterpolator == null || motionInterpolator == null) {
            interpolator = this.mCubicEaseOutInterpolator;
        }
        final float initAlpha = view.getAlpha();
        final float dropViewScale = view.getScaleX();
        final DragView dragView = view;
        final Interpolator interpolator2 = alphaInterpolator;
        final Interpolator interpolator3 = motionInterpolator;
        final float f = initScaleX;
        final float f2 = initScaleY;
        final float f3 = finalScaleX;
        final float f4 = finalScaleY;
        final float f5 = finalAlpha;
        final Rect rect = from;
        final Rect rect2 = to;
        animateView(view, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float alphaPercent;
                float motionPercent;
                float percent = ((Float) animation.getAnimatedValue()).floatValue();
                int width = dragView.getMeasuredWidth();
                int height = dragView.getMeasuredHeight();
                if (interpolator2 == null) {
                    alphaPercent = percent;
                } else {
                    alphaPercent = interpolator2.getInterpolation(percent);
                }
                if (interpolator3 == null) {
                    motionPercent = percent;
                } else {
                    motionPercent = interpolator3.getInterpolation(percent);
                }
                float initialScaleX = f * dropViewScale;
                float initialScaleY = f2 * dropViewScale;
                float scaleX = (f3 * percent) + ((1.0f - percent) * initialScaleX);
                float scaleY = (f4 * percent) + ((1.0f - percent) * initialScaleY);
                float alpha = (f5 * alphaPercent) + (initAlpha * (1.0f - alphaPercent));
                float fromLeft = ((float) rect.left) + (((initialScaleX - 1.0f) * ((float) width)) / 2.0f);
                float fromTop = ((float) rect.top) + (((initialScaleY - 1.0f) * ((float) height)) / 2.0f);
                int x = (int) (((float) Math.round((((float) rect2.left) - fromLeft) * motionPercent)) + fromLeft);
                int y = (int) (((float) Math.round((((float) rect2.top) - fromTop) * motionPercent)) + fromTop);
                int xPos = (x - DragLayer.this.mDropView.getScrollX()) + (DragLayer.this.mAnchorView != null ? DragLayer.this.mAnchorViewInitialScrollX - DragLayer.this.mAnchorView.getScrollX() : 0);
                int yPos = y - DragLayer.this.mDropView.getScrollY();
                DragLayer.this.mDropView.setTranslationX((float) xPos);
                DragLayer.this.mDropView.setTranslationY((float) yPos);
                DragLayer.this.mDropView.setScaleX(scaleX);
                DragLayer.this.mDropView.setScaleY(scaleY);
                DragLayer.this.mDropView.setAlpha(alpha);
            }
        }, duration, interpolator, onCompleteRunnable, animationEndStyle, anchorView);
    }

    public void animateView(DragView view, ValueAnimator.AnimatorUpdateListener updateCb, int duration, TimeInterpolator interpolator, final Runnable onCompleteRunnable, final int animationEndStyle, View anchorView) {
        if (this.mDropAnim != null) {
            this.mDropAnim.cancel();
        }
        if (this.mFadeOutAnim != null) {
            this.mFadeOutAnim.cancel();
        }
        this.mDropView = view;
        this.mDropView.cancelAnimation();
        this.mDropView.resetLayoutParams();
        if (anchorView != null) {
            this.mAnchorViewInitialScrollX = anchorView.getScrollX();
        }
        this.mAnchorView = anchorView;
        this.mDropAnim = new ValueAnimator();
        this.mDropAnim.setInterpolator(interpolator);
        this.mDropAnim.setDuration((long) duration);
        this.mDropAnim.setFloatValues(new float[]{0.0f, 1.0f});
        this.mDropAnim.addUpdateListener(updateCb);
        this.mDropAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                }
                switch (animationEndStyle) {
                    case 0:
                        DragLayer.this.clearAnimatedView();
                        return;
                    case 1:
                        DragLayer.this.fadeOutDragView();
                        return;
                    default:
                        return;
                }
            }
        });
        this.mDropAnim.start();
    }

    public void clearAnimatedView() {
        if (this.mDropAnim != null) {
            this.mDropAnim.cancel();
        }
        if (this.mDropView != null) {
            this.mDragController.onDeferredEndDrag(this.mDropView);
        }
        this.mDropView = null;
        invalidate();
    }

    public View getAnimatedView() {
        return this.mDropView;
    }

    /* access modifiers changed from: private */
    public void fadeOutDragView() {
        this.mFadeOutAnim = new ValueAnimator();
        this.mFadeOutAnim.setDuration(150);
        this.mFadeOutAnim.setFloatValues(new float[]{0.0f, 1.0f});
        this.mFadeOutAnim.removeAllUpdateListeners();
        this.mFadeOutAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                DragLayer.this.mDropView.setAlpha(1.0f - ((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        this.mFadeOutAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (DragLayer.this.mDropView != null) {
                    DragLayer.this.mDragController.onDeferredEndDrag(DragLayer.this.mDropView);
                }
                DragView unused = DragLayer.this.mDropView = null;
                DragLayer.this.invalidate();
            }
        });
        this.mFadeOutAnim.start();
    }

    public void onChildViewAdded(View parent, View child) {
        updateChildIndices();
    }

    public void onChildViewRemoved(View parent, View child) {
        updateChildIndices();
    }

    private void updateChildIndices() {
        if (this.mLauncher != null) {
            this.mWorkspaceIndex = indexOfChild(this.mLauncher.getWorkspace());
            this.mQsbIndex = indexOfChild(this.mLauncher.getSearchBar());
        }
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int childCount, int i) {
        return i;
    }

    /* access modifiers changed from: package-private */
    public void onEnterScrollArea(int direction) {
        this.mInScrollArea = true;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void onExitScrollArea() {
        this.mInScrollArea = false;
        invalidate();
    }

    private boolean isLayoutDirectionRtl() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mInScrollArea && !LauncherApplication.isScreenLarge()) {
            Workspace workspace = this.mLauncher.getWorkspace();
            int width = workspace.getWidth();
            Rect childRect = new Rect();
            getDescendantRectRelativeToSelf(workspace.getChildAt(0), childRect);
            int page = workspace.getNextPage();
            boolean isRtl = isLayoutDirectionRtl();
            CellLayout leftPage = (CellLayout) workspace.getChildAt(isRtl ? page + 1 : page - 1);
            CellLayout rightPage = (CellLayout) workspace.getChildAt(isRtl ? page - 1 : page + 1);
            if (leftPage != null && leftPage.getIsDragOverlapping()) {
                this.mLeftHoverDrawable.setBounds(0, childRect.top, this.mLeftHoverDrawable.getIntrinsicWidth(), childRect.bottom);
                this.mLeftHoverDrawable.draw(canvas);
            } else if (rightPage != null && rightPage.getIsDragOverlapping()) {
                this.mRightHoverDrawable.setBounds(width - this.mRightHoverDrawable.getIntrinsicWidth(), childRect.top, width, childRect.bottom);
                this.mRightHoverDrawable.draw(canvas);
            }
        }
    }
}
