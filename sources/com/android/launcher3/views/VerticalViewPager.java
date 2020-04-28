package com.android.launcher3.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class VerticalViewPager extends ViewPager {
    /* access modifiers changed from: private */
    public OnItemClickListener mOnItemClickListener;
    private float scaleY;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setup();
    }

    private void init() {
        setPageTransformer(true, new VerticalPageTransformer());
        setOverScrollMode(2);
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
        ev.getAction();
        if (ev.getAction() == 0) {
            this.scaleY = ev.getY();
        }
        if (ev.getAction() == 1 && this.scaleY == ev.getY()) {
            this.scaleY = 0.0f;
            return false;
        }
        try {
            return super.onTouchEvent(swapXY(ev));
        } catch (Exception e) {
            return true;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private void setup() {
        final GestureDetector tapGestureDetector = new GestureDetector(getContext(), new TapGestureListener());
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tapGestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        private TapGestureListener() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (VerticalViewPager.this.mOnItemClickListener == null) {
                return true;
            }
            VerticalViewPager.this.mOnItemClickListener.onItemClick(VerticalViewPager.this.getCurrentItem());
            return true;
        }
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
                view.setTranslationY(((float) view.getHeight()) * position);
            } else {
                view.setAlpha(0.0f);
            }
        }
    }
}
