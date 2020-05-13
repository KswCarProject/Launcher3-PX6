package com.szchoiceway.index;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import org.apache.http.HttpStatus;

public class VerticalPager extends ViewGroup {
    private static final String TAG = "VerticalPager";
    private Context mContext;
    private int mLastMotionY;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    public VerticalPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mScroller = new Scroller(context);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int totalHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).layout(l, totalHeight, r, totalHeight + b);
            totalHeight += b;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(width, height);
        }
        setMeasuredDimension(width, height);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "--->>>VerticalPager MotionEvent.onTouchEvent");
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case 0:
                Log.i(TAG, "--->>>VerticalPager MotionEvent.ACTION_DOWN");
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                this.mLastMotionY = (int) y;
                Log.d("montion", "" + getScrollY());
                return true;
            case 1:
                Log.i(TAG, "--->>>VerticalPager MotionEvent.ACTION_UP");
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                if (getScrollY() < 0) {
                    this.mScroller.startScroll(0, -400, 0, HttpStatus.SC_BAD_REQUEST);
                } else if (getScrollY() > getHeight() * (getChildCount() - 1)) {
                    this.mScroller.startScroll(0, getChildAt(getChildCount() - 1).getTop() + HttpStatus.SC_MULTIPLE_CHOICES, 0, -300);
                } else {
                    int position = getScrollY() / getHeight();
                    if (getScrollY() % getHeight() > getHeight() / 3) {
                        this.mScroller.startScroll(0, getChildAt(position + 1).getTop() - 300, 0, HttpStatus.SC_MULTIPLE_CHOICES);
                    } else {
                        this.mScroller.startScroll(0, getChildAt(position).getTop() + HttpStatus.SC_MULTIPLE_CHOICES, 0, -300);
                    }
                }
                invalidate();
                return true;
            case 2:
                Log.i(TAG, "--->>>VerticalPager MotionEvent.ACTION_MOVE");
                scrollBy(0, (int) (((float) this.mLastMotionY) - y));
                invalidate();
                this.mLastMotionY = (int) y;
                return true;
            default:
                return true;
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(0, this.mScroller.getCurrY());
        }
    }
}
