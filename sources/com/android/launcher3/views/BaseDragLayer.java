package com.android.launcher3.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.TouchController;
import java.util.ArrayList;

public abstract class BaseDragLayer<T extends BaseDraggingActivity> extends InsettableFrameLayout {
    protected TouchController mActiveController;
    protected final T mActivity;
    protected TouchController[] mControllers;
    protected final Rect mHitRect = new Rect();
    private final MultiValueAlpha mMultiValueAlpha;
    protected final int[] mTmpXY = new int[2];
    private TouchCompleteListener mTouchCompleteListener;

    public interface TouchCompleteListener {
        void onTouchComplete();
    }

    public BaseDragLayer(Context context, AttributeSet attrs, int alphaChannelCount) {
        super(context, attrs);
        this.mActivity = (BaseDraggingActivity) BaseActivity.fromContext(context);
        this.mMultiValueAlpha = new MultiValueAlpha(this, alphaChannelCount);
    }

    public boolean isEventOverView(View view, MotionEvent ev) {
        getDescendantRectRelativeToSelf(view, this.mHitRect);
        return this.mHitRect.contains((int) ev.getX(), (int) ev.getY());
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 1 || action == 3) {
            if (this.mTouchCompleteListener != null) {
                this.mTouchCompleteListener.onTouchComplete();
            }
            this.mTouchCompleteListener = null;
        } else if (action == 0) {
            this.mActivity.finishAutoCancelActionMode();
        }
        return findActiveController(ev);
    }

    /* access modifiers changed from: protected */
    public boolean findActiveController(MotionEvent ev) {
        this.mActiveController = null;
        AbstractFloatingView topView = AbstractFloatingView.getTopOpenView(this.mActivity);
        if (topView == null || !topView.onControllerInterceptTouchEvent(ev)) {
            for (TouchController controller : this.mControllers) {
                if (controller.onControllerInterceptTouchEvent(ev)) {
                    this.mActiveController = controller;
                    return true;
                }
            }
            return false;
        }
        this.mActiveController = topView;
        return true;
    }

    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        View topView = AbstractFloatingView.getTopOpenViewWithType(this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE);
        if (topView == null) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        if (child == topView) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        return false;
    }

    public void addChildrenForAccessibility(ArrayList<View> childrenForAccessibility) {
        View topView = AbstractFloatingView.getTopOpenViewWithType(this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE);
        if (topView != null) {
            addAccessibleChildToList(topView, childrenForAccessibility);
        } else {
            super.addChildrenForAccessibility(childrenForAccessibility);
        }
    }

    /* access modifiers changed from: protected */
    public void addAccessibleChildToList(View child, ArrayList<View> outList) {
        if (child.isImportantForAccessibility()) {
            outList.add(child);
        } else {
            child.addChildrenForAccessibility(outList);
        }
    }

    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof AbstractFloatingView) {
            postDelayed(new Runnable(child) {
                private final /* synthetic */ View f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    BaseDragLayer.lambda$onViewRemoved$0(this.f$0);
                }
            }, 16);
        }
    }

    static /* synthetic */ void lambda$onViewRemoved$0(View child) {
        AbstractFloatingView floatingView = (AbstractFloatingView) child;
        if (floatingView.isOpen()) {
            floatingView.close(false);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 1 || action == 3) {
            if (this.mTouchCompleteListener != null) {
                this.mTouchCompleteListener.onTouchComplete();
            }
            this.mTouchCompleteListener = null;
        }
        if (this.mActiveController != null) {
            return this.mActiveController.onControllerTouchEvent(ev);
        }
        return findActiveController(ev);
    }

    public float getDescendantRectRelativeToSelf(View descendant, Rect r) {
        this.mTmpXY[0] = 0;
        this.mTmpXY[1] = 0;
        float scale = getDescendantCoordRelativeToSelf(descendant, this.mTmpXY);
        r.set(this.mTmpXY[0], this.mTmpXY[1], (int) (((float) this.mTmpXY[0]) + (((float) descendant.getMeasuredWidth()) * scale)), (int) (((float) this.mTmpXY[1]) + (((float) descendant.getMeasuredHeight()) * scale)));
        return scale;
    }

    public float getLocationInDragLayer(View child, int[] loc) {
        loc[0] = 0;
        loc[1] = 0;
        return getDescendantCoordRelativeToSelf(child, loc);
    }

    public float getDescendantCoordRelativeToSelf(View descendant, int[] coord) {
        return getDescendantCoordRelativeToSelf(descendant, coord, false);
    }

    public float getDescendantCoordRelativeToSelf(View descendant, int[] coord, boolean includeRootScroll) {
        return Utilities.getDescendantCoordRelativeToAncestor(descendant, this, coord, includeRootScroll);
    }

    public void mapCoordInSelfToDescendant(View descendant, int[] coord) {
        Utilities.mapCoordInSelfToDescendant(descendant, this, coord);
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
        return AbstractFloatingView.getTopOpenView(this.mActivity) != null;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        View topView = AbstractFloatingView.getTopOpenView(this.mActivity);
        if (topView != null) {
            return topView.requestFocus(direction, previouslyFocusedRect);
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View topView = AbstractFloatingView.getTopOpenView(this.mActivity);
        if (topView != null) {
            topView.addFocusables(views, direction);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    public void setTouchCompleteListener(TouchCompleteListener listener) {
        this.mTouchCompleteListener = listener;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public MultiValueAlpha.AlphaProperty getAlphaProperty(int index) {
        return this.mMultiValueAlpha.getProperty(index);
    }

    public static class LayoutParams extends InsettableFrameLayout.LayoutParams {
        public boolean customPosition = false;
        public int x;
        public int y;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams lp) {
            super(lp);
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
}
