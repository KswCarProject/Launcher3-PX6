package com.szchoiceway.index;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerticalViewPager extends ViewPager {
    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setPageTransformer(true, new VerticalPageTransformer());
        setOverScrollMode(2);
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {
        private VerticalPageTransformer() {
        }

        public void transformPage(View view, float position) {
            if (position < -1.0f) {
                view.setAlpha(0.0f);
            } else if (position <= 1.0f) {
                view.setAlpha(1.0f);
                view.setTranslationX(((float) view.getWidth()) * (-position));
                view.setTranslationY(position * ((float) view.getHeight()));
            } else {
                view.setAlpha(0.0f);
            }
        }
    }

    private MotionEvent swapXY(MotionEvent ev) {
        float width = (float) getWidth();
        float height = (float) getHeight();
        ev.setLocation((ev.getY() / height) * width, (ev.getX() / width) * height);
        return ev;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev);
        return intercepted;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }
}
