package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTargetBar;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.ViewScrim;
import com.android.launcher3.graphics.WorkspaceAndHotseatScrim;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.uioverrides.UiFactory;
import com.android.launcher3.views.BaseDragLayer;
import java.util.ArrayList;

public class DragLayer extends BaseDragLayer<Launcher> {
    private static final int ALPHA_CHANNEL_COUNT = 4;
    public static final int ALPHA_INDEX_LAUNCHER_LOAD = 1;
    public static final int ALPHA_INDEX_OVERLAY = 0;
    public static final int ALPHA_INDEX_SWIPE_UP = 3;
    public static final int ALPHA_INDEX_TRANSITIONS = 2;
    public static final int ANIMATION_END_DISAPPEAR = 0;
    public static final int ANIMATION_END_REMAIN_VISIBLE = 2;
    View mAnchorView = null;
    int mAnchorViewInitialScrollX = 0;
    private int mChildCountOnLastUpdate = -1;
    private final TimeInterpolator mCubicEaseOutInterpolator = Interpolators.DEACCEL_1_5;
    DragController mDragController;
    /* access modifiers changed from: private */
    public ValueAnimator mDropAnim = null;
    DragView mDropView = null;
    private final ViewGroupFocusHelper mFocusIndicatorHelper;
    private boolean mHoverPointClosesFolder = false;
    private final WorkspaceAndHotseatScrim mScrim;
    private int mTopViewIndex;

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs, 4);
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        this.mFocusIndicatorHelper = new ViewGroupFocusHelper(this);
        this.mScrim = new WorkspaceAndHotseatScrim(this);
    }

    public void setup(DragController dragController, Workspace workspace) {
        this.mDragController = dragController;
        this.mScrim.setWorkspace(workspace);
        recreateControllers();
    }

    public void recreateControllers() {
        this.mControllers = UiFactory.createTouchControllers((Launcher) this.mActivity);
    }

    public ViewGroupFocusHelper getFocusIndicatorHelper() {
        return this.mFocusIndicatorHelper;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        ViewScrim scrim = ViewScrim.get(child);
        if (scrim != null) {
            scrim.draw(canvas, getWidth(), getHeight());
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    /* access modifiers changed from: protected */
    public boolean findActiveController(MotionEvent ev) {
        if (!((Launcher) this.mActivity).getStateManager().getState().disableInteraction) {
            return super.findActiveController(ev);
        }
        this.mActiveController = null;
        return true;
    }

    private boolean isEventOverAccessibleDropTargetBar(MotionEvent ev) {
        return isInAccessibleDrag() && isEventOverView(((Launcher) this.mActivity).getDropTargetBar(), ev);
    }

    public boolean onInterceptHoverEvent(MotionEvent ev) {
        if (this.mActivity == null || ((Launcher) this.mActivity).getWorkspace() == null) {
            return false;
        }
        AbstractFloatingView topView = AbstractFloatingView.getTopOpenView(this.mActivity);
        if ((topView instanceof Folder) && ((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            Folder currentFolder = (Folder) topView;
            int action = ev.getAction();
            if (action == 7) {
                boolean isOverFolderOrSearchBar = isEventOverView(topView, ev) || isEventOverAccessibleDropTargetBar(ev);
                if (!isOverFolderOrSearchBar && !this.mHoverPointClosesFolder) {
                    sendTapOutsideFolderAccessibilityEvent(currentFolder.isEditingName());
                    this.mHoverPointClosesFolder = true;
                    return true;
                } else if (!isOverFolderOrSearchBar) {
                    return true;
                } else {
                    this.mHoverPointClosesFolder = false;
                }
            } else if (action == 9) {
                if (!(isEventOverView(topView, ev) || isEventOverAccessibleDropTargetBar(ev))) {
                    sendTapOutsideFolderAccessibilityEvent(currentFolder.isEditingName());
                    this.mHoverPointClosesFolder = true;
                    return true;
                }
                this.mHoverPointClosesFolder = false;
            }
        }
        return false;
    }

    private void sendTapOutsideFolderAccessibilityEvent(boolean isEditingName) {
        AccessibilityManagerCompat.sendCustomAccessibilityEvent(this, 8, getContext().getString(isEditingName ? R.string.folder_tap_to_rename : R.string.folder_tap_to_close));
    }

    public boolean onHoverEvent(MotionEvent ev) {
        return false;
    }

    private boolean isInAccessibleDrag() {
        return ((Launcher) this.mActivity).getAccessibilityDelegate().isInAccessibleDrag();
    }

    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (!isInAccessibleDrag() || !(child instanceof DropTargetBar)) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        return true;
    }

    public void addChildrenForAccessibility(ArrayList<View> childrenForAccessibility) {
        View topView = AbstractFloatingView.getTopOpenViewWithType(this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE);
        if (topView != null) {
            addAccessibleChildToList(topView, childrenForAccessibility);
            if (isInAccessibleDrag()) {
                addAccessibleChildToList(((Launcher) this.mActivity).getDropTargetBar(), childrenForAccessibility);
                return;
            }
            return;
        }
        super.addChildrenForAccessibility(childrenForAccessibility);
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return super.dispatchUnhandledMove(focused, direction) || this.mDragController.dispatchUnhandledMove(focused, direction);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        ev.offsetLocation(getTranslationX(), 0.0f);
        try {
            return super.dispatchTouchEvent(ev);
        } finally {
            ev.offsetLocation(-getTranslationX(), 0.0f);
        }
    }

    public void animateViewIntoPosition(DragView dragView, int[] pos, float alpha, float scaleX, float scaleY, int animationEndStyle, Runnable onFinishRunnable, int duration) {
        Rect r = new Rect();
        getViewRectRelativeToSelf(dragView, r);
        int fromX = r.left;
        int fromY = r.top;
        int i = fromY;
        int i2 = fromX;
        animateViewIntoPosition(dragView, fromX, fromY, pos[0], pos[1], alpha, 1.0f, 1.0f, scaleX, scaleY, onFinishRunnable, animationEndStyle, duration, (View) null);
    }

    public void animateViewIntoPosition(DragView dragView, View child, View anchorView) {
        animateViewIntoPosition(dragView, child, -1, anchorView);
    }

    public void animateViewIntoPosition(DragView dragView, View child, int duration, View anchorView) {
        int toY;
        int toX;
        View view = child;
        ShortcutAndWidgetContainer parentChildren = (ShortcutAndWidgetContainer) child.getParent();
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        parentChildren.measureChild(view);
        Rect r = new Rect();
        getViewRectRelativeToSelf(dragView, r);
        float childScale = child.getScaleX();
        int[] coord = {lp.x + ((int) ((((float) child.getMeasuredWidth()) * (1.0f - childScale)) / 2.0f)), lp.y + ((int) ((((float) child.getMeasuredHeight()) * (1.0f - childScale)) / 2.0f))};
        float scale = getDescendantCoordRelativeToSelf((View) child.getParent(), coord) * childScale;
        int toX2 = coord[0];
        int toY2 = coord[1];
        float toScale = scale;
        if (view instanceof TextView) {
            toScale = scale / dragView.getIntrinsicIconScaleFactor();
            toY = (int) (((float) (toY2 + Math.round(((float) ((TextView) view).getPaddingTop()) * toScale))) - ((((float) dragView.getMeasuredHeight()) * (1.0f - toScale)) / 2.0f));
            if (dragView.getDragVisualizeOffset() != null) {
                toY -= Math.round(((float) dragView.getDragVisualizeOffset().y) * toScale);
            }
            toX = toX2 - ((dragView.getMeasuredWidth() - Math.round(((float) child.getMeasuredWidth()) * scale)) / 2);
        } else if (view instanceof FolderIcon) {
            toY = (int) (((float) ((int) (((float) (toY2 + Math.round(((float) (child.getPaddingTop() - dragView.getDragRegionTop())) * scale))) - ((((float) dragView.getBlurSizeOutline()) * scale) / 2.0f)))) - (((1.0f - scale) * ((float) dragView.getMeasuredHeight())) / 2.0f));
            toX = toX2 - ((dragView.getMeasuredWidth() - Math.round(((float) child.getMeasuredWidth()) * scale)) / 2);
        } else {
            toY = toY2 - (Math.round(((float) (dragView.getHeight() - child.getMeasuredHeight())) * scale) / 2);
            toX = toX2 - (Math.round(((float) (dragView.getMeasuredWidth() - child.getMeasuredWidth())) * scale) / 2);
        }
        int toX3 = toX;
        int toY3 = toY;
        float toScale2 = toScale;
        int fromX = r.left;
        int fromY = r.top;
        view.setVisibility(4);
        int i = fromY;
        int i2 = fromX;
        int[] iArr = coord;
        Rect rect = r;
        CellLayout.LayoutParams layoutParams = lp;
        ShortcutAndWidgetContainer shortcutAndWidgetContainer = parentChildren;
        animateViewIntoPosition(dragView, fromX, fromY, toX3, toY3, 1.0f, 1.0f, 1.0f, toScale2, toScale2, new Runnable(view) {
            private final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.setVisibility(0);
            }
        }, 0, duration, anchorView);
    }

    public void animateViewIntoPosition(DragView view, int fromX, int fromY, int toX, int toY, float finalAlpha, float initScaleX, float initScaleY, float finalScaleX, float finalScaleY, Runnable onCompleteRunnable, int animationEndStyle, int duration, View anchorView) {
        int i = fromX;
        int i2 = fromY;
        int i3 = toX;
        int i4 = toY;
        animateView(view, new Rect(i, i2, view.getMeasuredWidth() + i, view.getMeasuredHeight() + i2), new Rect(i3, i4, view.getMeasuredWidth() + i3, view.getMeasuredHeight() + i4), finalAlpha, initScaleX, initScaleY, finalScaleX, finalScaleY, duration, (Interpolator) null, (Interpolator) null, onCompleteRunnable, animationEndStyle, anchorView);
    }

    public void animateView(DragView view, Rect from, Rect to, float finalAlpha, float initScaleX, float initScaleY, float finalScaleX, float finalScaleY, int duration, Interpolator motionInterpolator, Interpolator alphaInterpolator, Runnable onCompleteRunnable, int animationEndStyle, View anchorView) {
        int duration2;
        Rect rect = from;
        Rect rect2 = to;
        float dist = (float) Math.hypot((double) (rect2.left - rect.left), (double) (rect2.top - rect.top));
        Resources res = getResources();
        float maxDist = (float) res.getInteger(R.integer.config_dropAnimMaxDist);
        if (duration < 0) {
            int duration3 = res.getInteger(R.integer.config_dropAnimMaxDuration);
            if (dist < maxDist) {
                duration3 = (int) (((float) duration3) * this.mCubicEaseOutInterpolator.getInterpolation(dist / maxDist));
            }
            duration2 = Math.max(duration3, res.getInteger(R.integer.config_dropAnimMinDuration));
        } else {
            duration2 = duration;
        }
        TimeInterpolator interpolator = null;
        if (alphaInterpolator == null || motionInterpolator == null) {
            interpolator = this.mCubicEaseOutInterpolator;
        }
        TimeInterpolator interpolator2 = interpolator;
        float initAlpha = view.getAlpha();
        final DragView dragView = view;
        final Interpolator interpolator3 = alphaInterpolator;
        final Interpolator interpolator4 = motionInterpolator;
        final float f = initScaleX;
        final float scaleX = view.getScaleX();
        final float f2 = initScaleY;
        final float f3 = finalScaleX;
        final float f4 = finalScaleY;
        float f5 = maxDist;
        final float maxDist2 = finalAlpha;
        Resources resources = res;
        final float f6 = initAlpha;
        float f7 = dist;
        final Rect rect3 = from;
        final Rect rect4 = to;
        animateView(view, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int i;
                float percent = ((Float) animation.getAnimatedValue()).floatValue();
                int width = dragView.getMeasuredWidth();
                int height = dragView.getMeasuredHeight();
                float alphaPercent = interpolator3 == null ? percent : interpolator3.getInterpolation(percent);
                float motionPercent = interpolator4 == null ? percent : interpolator4.getInterpolation(percent);
                float initialScaleX = f * scaleX;
                float initialScaleY = f2 * scaleX;
                float scaleX = (f3 * percent) + ((1.0f - percent) * initialScaleX);
                float scaleY = (f4 * percent) + ((1.0f - percent) * initialScaleY);
                float alpha = (maxDist2 * alphaPercent) + (f6 * (1.0f - alphaPercent));
                float fromLeft = ((float) rect3.left) + (((initialScaleX - 1.0f) * ((float) width)) / 2.0f);
                float fromTop = ((float) rect3.top) + (((initialScaleY - 1.0f) * ((float) height)) / 2.0f);
                int x = (int) (((float) Math.round((((float) rect4.left) - fromLeft) * motionPercent)) + fromLeft);
                int y = (int) (((float) Math.round((((float) rect4.top) - fromTop) * motionPercent)) + fromTop);
                if (DragLayer.this.mAnchorView == null) {
                    i = 0;
                    float f = percent;
                    int i2 = width;
                } else {
                    float f2 = percent;
                    int i3 = width;
                    i = (int) (DragLayer.this.mAnchorView.getScaleX() * ((float) (DragLayer.this.mAnchorViewInitialScrollX - DragLayer.this.mAnchorView.getScrollX())));
                }
                int anchorAdjust = i;
                int i4 = anchorAdjust;
                int i5 = height;
                DragLayer.this.mDropView.setTranslationX((float) ((x - DragLayer.this.mDropView.getScrollX()) + anchorAdjust));
                DragLayer.this.mDropView.setTranslationY((float) (y - DragLayer.this.mDropView.getScrollY()));
                DragLayer.this.mDropView.setScaleX(scaleX);
                DragLayer.this.mDropView.setScaleY(scaleY);
                DragLayer.this.mDropView.setAlpha(alpha);
            }
        }, duration2, interpolator2, onCompleteRunnable, animationEndStyle, anchorView);
    }

    public void animateView(DragView view, ValueAnimator.AnimatorUpdateListener updateCb, int duration, TimeInterpolator interpolator, final Runnable onCompleteRunnable, final int animationEndStyle, View anchorView) {
        if (this.mDropAnim != null) {
            this.mDropAnim.cancel();
        }
        this.mDropView = view;
        this.mDropView.cancelAnimation();
        this.mDropView.requestLayout();
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
                if (animationEndStyle == 0) {
                    DragLayer.this.clearAnimatedView();
                }
                ValueAnimator unused = DragLayer.this.mDropAnim = null;
            }
        });
        this.mDropAnim.start();
    }

    public void clearAnimatedView() {
        if (this.mDropAnim != null) {
            this.mDropAnim.cancel();
        }
        this.mDropAnim = null;
        if (this.mDropView != null) {
            this.mDragController.onDeferredEndDrag(this.mDropView);
        }
        this.mDropView = null;
        invalidate();
    }

    public View getAnimatedView() {
        return this.mDropView;
    }

    public void onViewAdded(View child) {
        super.onViewAdded(child);
        updateChildIndices();
        UiFactory.onLauncherStateOrFocusChanged((Launcher) this.mActivity);
    }

    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        updateChildIndices();
        UiFactory.onLauncherStateOrFocusChanged((Launcher) this.mActivity);
    }

    public void bringChildToFront(View child) {
        super.bringChildToFront(child);
        updateChildIndices();
    }

    private void updateChildIndices() {
        this.mTopViewIndex = -1;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof DragView) {
                this.mTopViewIndex = i;
            }
        }
        this.mChildCountOnLastUpdate = childCount;
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int childCount, int i) {
        if (this.mChildCountOnLastUpdate != childCount) {
            updateChildIndices();
        }
        if (this.mTopViewIndex == -1) {
            return i;
        }
        if (i == childCount - 1) {
            return this.mTopViewIndex;
        }
        if (i < this.mTopViewIndex) {
            return i;
        }
        return i + 1;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mScrim.draw(canvas);
        this.mFocusIndicatorHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mScrim.setSize(w, h);
    }

    public void setInsets(Rect insets) {
        super.setInsets(insets);
        this.mScrim.onInsetsChanged(insets);
    }

    public WorkspaceAndHotseatScrim getScrim() {
        return this.mScrim;
    }
}
