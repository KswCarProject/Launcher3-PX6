package com.android.launcher3.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PagingScrollHelper {
    private static final String TAG = "PagingScrollHelper";
    /* access modifiers changed from: private */
    public boolean bIsUser = false;
    /* access modifiers changed from: private */
    public boolean firstTouch = true;
    ValueAnimator mAnimator = null;
    /* access modifiers changed from: private */
    public MyOnFlingListener mOnFlingListener = new MyOnFlingListener();
    onPageChangeListener mOnPageChangeListener;
    private MyOnScrollListener mOnScrollListener = new MyOnScrollListener();
    private MyOnTouchListener mOnTouchListener = new MyOnTouchListener();
    /* access modifiers changed from: private */
    public ORIENTATION mOrientation = ORIENTATION.HORIZONTAL;
    RecyclerView mRecyclerView = null;
    /* access modifiers changed from: private */
    public int offsetX = 0;
    /* access modifiers changed from: private */
    public int offsetY = 0;
    int startX = 0;
    int startY = 0;

    enum ORIENTATION {
        HORIZONTAL,
        VERTICAL,
        NULL
    }

    public interface onPageChangeListener {
        void onPageChange(int i);
    }

    public void setUpRecycleView(RecyclerView recycleView) {
        if (recycleView != null) {
            this.mRecyclerView = recycleView;
            recycleView.setOnFlingListener(this.mOnFlingListener);
            recycleView.setOnScrollListener(this.mOnScrollListener);
            recycleView.setOnTouchListener(this.mOnTouchListener);
            updateLayoutManger();
            return;
        }
        throw new IllegalArgumentException("recycleView must be not null");
    }

    public void updateLayoutManger() {
        RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                this.mOrientation = ORIENTATION.VERTICAL;
            } else if (layoutManager.canScrollHorizontally()) {
                this.mOrientation = ORIENTATION.HORIZONTAL;
            } else {
                this.mOrientation = ORIENTATION.NULL;
            }
            if (this.mAnimator != null) {
                this.mAnimator.cancel();
            }
            this.startX = 0;
            this.startY = 0;
            this.offsetX = 0;
            this.offsetY = 0;
        }
    }

    public int getPageCount() {
        if (this.mRecyclerView == null || this.mOrientation == ORIENTATION.NULL) {
            return 0;
        }
        if (this.mOrientation == ORIENTATION.VERTICAL && this.mRecyclerView.computeVerticalScrollExtent() != 0) {
            return this.mRecyclerView.computeVerticalScrollRange() / this.mRecyclerView.computeVerticalScrollExtent();
        }
        if (this.mRecyclerView.computeHorizontalScrollExtent() != 0) {
            Log.i("zzz", "rang=" + this.mRecyclerView.computeHorizontalScrollRange() + " extent=" + this.mRecyclerView.computeHorizontalScrollExtent());
            return this.mRecyclerView.computeHorizontalScrollRange() / this.mRecyclerView.computeHorizontalScrollExtent();
        }
        return 0;
    }

    public void scrollToPosition(int position) {
        int endPoint;
        if (this.mAnimator == null) {
            this.mOnFlingListener.onFling(0, 0);
        }
        if (this.mAnimator != null) {
            int startPoint = this.mOrientation == ORIENTATION.VERTICAL ? this.offsetY : this.offsetX;
            if (this.mOrientation == ORIENTATION.VERTICAL) {
                endPoint = this.mRecyclerView.getHeight() * position;
            } else {
                endPoint = this.mRecyclerView.getWidth() * position;
            }
            if (startPoint != endPoint) {
                this.bIsUser = true;
                this.mAnimator.setIntValues(new int[]{startPoint, endPoint});
                this.mAnimator.start();
            }
        }
    }

    public class MyOnFlingListener extends RecyclerView.OnFlingListener {
        public MyOnFlingListener() {
        }

        public boolean onFling(int velocityX, int velocityY) {
            int endPoint;
            int startPoint;
            if (PagingScrollHelper.this.mOrientation == ORIENTATION.NULL) {
                return false;
            }
            int p = PagingScrollHelper.this.getStartPageIndex();
            if (PagingScrollHelper.this.mOrientation == ORIENTATION.VERTICAL) {
                startPoint = PagingScrollHelper.this.offsetY;
                if (velocityY < 0) {
                    p--;
                } else if (velocityY > 0) {
                    p++;
                }
                endPoint = PagingScrollHelper.this.mRecyclerView.getHeight() * p;
            } else {
                startPoint = PagingScrollHelper.this.offsetX;
                if (velocityX < 0) {
                    p--;
                } else if (velocityX > 0) {
                    p++;
                }
                endPoint = PagingScrollHelper.this.mRecyclerView.getWidth() * p;
            }
            if (endPoint < 0) {
                endPoint = 0;
            }
            if (PagingScrollHelper.this.mAnimator == null) {
                PagingScrollHelper pagingScrollHelper = PagingScrollHelper.this;
                new ValueAnimator();
                pagingScrollHelper.mAnimator = ValueAnimator.ofInt(new int[]{startPoint, endPoint});
                PagingScrollHelper.this.mAnimator.setDuration(180);
                Log.i(PagingScrollHelper.TAG, "onFling: duration = " + PagingScrollHelper.this.mAnimator.getDuration());
                PagingScrollHelper.this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int nowPoint = ((Integer) animation.getAnimatedValue()).intValue();
                        if (PagingScrollHelper.this.mOrientation == ORIENTATION.VERTICAL) {
                            PagingScrollHelper.this.mRecyclerView.scrollBy(0, nowPoint - PagingScrollHelper.this.offsetY);
                            return;
                        }
                        PagingScrollHelper.this.mRecyclerView.scrollBy(nowPoint - PagingScrollHelper.this.offsetX, 0);
                    }
                });
                PagingScrollHelper.this.mAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Log.i(PagingScrollHelper.TAG, "onAnimationEnd: bIsUser = " + PagingScrollHelper.this.bIsUser);
                        if (PagingScrollHelper.this.mOnPageChangeListener != null) {
                            PagingScrollHelper.this.mOnPageChangeListener.onPageChange(PagingScrollHelper.this.getPageIndex());
                        }
                        boolean unused = PagingScrollHelper.this.bIsUser = false;
                        PagingScrollHelper.this.mRecyclerView.stopScroll();
                        PagingScrollHelper.this.startY = PagingScrollHelper.this.offsetY;
                        PagingScrollHelper.this.startX = PagingScrollHelper.this.offsetX;
                    }
                });
            } else {
                PagingScrollHelper.this.mAnimator.cancel();
                PagingScrollHelper.this.mAnimator.setIntValues(new int[]{startPoint, endPoint});
            }
            PagingScrollHelper.this.mAnimator.start();
            return true;
        }
    }

    public class MyOnScrollListener extends RecyclerView.OnScrollListener {
        public MyOnScrollListener() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0 && PagingScrollHelper.this.mOrientation != ORIENTATION.NULL) {
                int vX = 0;
                int vY = 0;
                int i = 1000;
                boolean move = false;
                if (PagingScrollHelper.this.mOrientation == ORIENTATION.VERTICAL) {
                    if (Math.abs(PagingScrollHelper.this.offsetY - PagingScrollHelper.this.startY) > recyclerView.getHeight() / 2) {
                        move = true;
                    }
                    vY = 0;
                    if (move) {
                        if (PagingScrollHelper.this.offsetY - PagingScrollHelper.this.startY < 0) {
                            i = NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
                        }
                        vY = i;
                    }
                } else {
                    if (Math.abs(PagingScrollHelper.this.offsetX - PagingScrollHelper.this.startX) > recyclerView.getWidth() / 2) {
                        move = true;
                    }
                    if (move) {
                        if (PagingScrollHelper.this.offsetX - PagingScrollHelper.this.startX < 0) {
                            i = NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
                        }
                        vX = i;
                    }
                }
                PagingScrollHelper.this.mOnFlingListener.onFling(vX, vY);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int unused = PagingScrollHelper.this.offsetY = PagingScrollHelper.this.offsetY + dy;
            int unused2 = PagingScrollHelper.this.offsetX = PagingScrollHelper.this.offsetX + dx;
        }
    }

    public class MyOnTouchListener implements View.OnTouchListener {
        public MyOnTouchListener() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (PagingScrollHelper.this.firstTouch) {
                boolean unused = PagingScrollHelper.this.firstTouch = false;
                PagingScrollHelper.this.startY = PagingScrollHelper.this.offsetY;
                PagingScrollHelper.this.startX = PagingScrollHelper.this.offsetX;
            }
            if (event.getAction() == 1 || event.getAction() == 3) {
                boolean unused2 = PagingScrollHelper.this.firstTouch = true;
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public int getPageIndex() {
        if (this.mRecyclerView.getHeight() == 0 || this.mRecyclerView.getWidth() == 0) {
            return 0;
        }
        if (this.mOrientation == ORIENTATION.VERTICAL) {
            return this.offsetY / this.mRecyclerView.getHeight();
        }
        return this.offsetX / this.mRecyclerView.getWidth();
    }

    /* access modifiers changed from: private */
    public int getStartPageIndex() {
        if (this.mRecyclerView.getHeight() == 0 || this.mRecyclerView.getWidth() == 0) {
            return 0;
        }
        if (this.mOrientation == ORIENTATION.VERTICAL) {
            return this.startY / this.mRecyclerView.getHeight();
        }
        return this.startX / this.mRecyclerView.getWidth();
    }

    public void setOnPageChangeListener(onPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }
}
