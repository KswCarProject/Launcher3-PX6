package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.PagedView;

public class AllAppsPagedView extends PagedView<PersonalWorkSlidingTabStrip> {
    static final float MAX_SWIPE_ANGLE = 1.0471976f;
    static final float START_DAMPING_TOUCH_SLOP_ANGLE = 0.5235988f;
    static final float TOUCH_SLOP_DAMPING_FACTOR = 4.0f;

    public AllAppsPagedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AllAppsPagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsPagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return "";
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ((PersonalWorkSlidingTabStrip) this.mPageIndicator).setScroll(l, this.mMaxScrollX);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev) {
        float absDeltaX = Math.abs(ev.getX() - getDownMotionX());
        float absDeltaY = Math.abs(ev.getY() - getDownMotionY());
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

    public boolean hasOverlappingRendering() {
        return false;
    }
}
