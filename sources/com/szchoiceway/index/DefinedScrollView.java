package com.szchoiceway.index;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class DefinedScrollView extends ViewGroup {
    private static final int SNAP_VELOCITY = 600;
    private static final String TAG = "DefinedScrollView";
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private int mCurrentScreen;
    private int mDefaultScreen;
    private float mLastMotionY;
    private Scroller mScroller;
    private int mTouchSlop;
    private int mTouchState;
    private VelocityTracker mVelocityTracker;
    private PageListener pageListener;

    public interface PageListener {
        void page(int i);
    }

    public DefinedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDefaultScreen = 0;
        this.mTouchState = 0;
        this.mScroller = new Scroller(context);
        this.mCurrentScreen = this.mDefaultScreen;
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public DefinedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTop = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != 8) {
                int childHeight = childView.getMeasuredHeight();
                childView.layout(0, childTop, childView.getMeasuredHeight(), childTop + childHeight);
                childTop += childHeight;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "--->>> onMeasure widthsize = " + View.MeasureSpec.getSize(widthMeasureSpec) + ", heightsize = " + View.MeasureSpec.getSize(heightMeasureSpec));
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (View.MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
        } else if (View.MeasureSpec.getMode(heightMeasureSpec) != 1073741824) {
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
        } else {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            }
            scrollTo(0, this.mCurrentScreen * height);
        }
    }

    public void snapToDestination() {
        int screenHeight = getHeight();
        snapToScreen((getScrollY() + (screenHeight / 2)) / screenHeight);
    }

    public void snapToScreen(int whichScreen) {
        int whichScreen2 = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollY() != getHeight() * whichScreen2) {
            int delta = (getHeight() * whichScreen2) - getScrollY();
            this.mScroller.startScroll(0, getScrollY(), 0, delta, Math.abs(delta) * 2);
            this.mCurrentScreen = whichScreen2;
            if (this.mCurrentScreen > Configure.curentPage) {
                Configure.curentPage = whichScreen2;
            } else if (this.mCurrentScreen < Configure.curentPage) {
                Configure.curentPage = whichScreen2;
            }
            invalidate();
        }
    }

    public int getCurScreen() {
        return this.mCurrentScreen;
    }

    public int getPage() {
        return Configure.curentPage;
    }

    public void setToScreen(int whichScreen) {
        int whichScreen2 = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        this.mCurrentScreen = whichScreen2;
        scrollTo(0, getHeight() * whichScreen2);
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            postInvalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case 0:
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                this.mLastMotionY = y;
                return true;
            case 1:
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) velocityTracker.getYVelocity();
                if (velocityY > SNAP_VELOCITY && getCurScreen() > 0) {
                    snapToScreen(getCurScreen() - 1);
                } else if (velocityY >= -600 || getCurScreen() >= getChildCount() - 1) {
                    snapToDestination();
                } else {
                    snapToScreen(getCurScreen() + 1);
                }
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.mTouchState = 0;
                return true;
            case 2:
                this.mLastMotionY = y;
                scrollBy(0, (int) (this.mLastMotionY - y));
                return true;
            case 3:
                this.mTouchState = 0;
                return true;
            default:
                return true;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (Configure.isMove) {
            return false;
        }
        int action = ev.getAction();
        if (action == 2 && this.mTouchState != 0) {
            return true;
        }
        float y = ev.getY();
        switch (action) {
            case 0:
                this.mLastMotionY = y;
                this.mTouchState = this.mScroller.isFinished() ? 0 : 1;
                break;
            case 1:
            case 3:
                this.mTouchState = 0;
                break;
            case 2:
                if (((int) Math.abs(this.mLastMotionY - y)) > this.mTouchSlop) {
                    this.mTouchState = 1;
                    break;
                }
                break;
        }
        if (this.mTouchState == 0) {
            return false;
        }
        return true;
    }

    public void setPageListener(PageListener pageListener2) {
        this.pageListener = pageListener2;
    }
}
