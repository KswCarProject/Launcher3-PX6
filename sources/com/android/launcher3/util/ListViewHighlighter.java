package com.android.launcher3.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.android.launcher3.R;

public class ListViewHighlighter implements AbsListView.OnScrollListener, AbsListView.RecyclerListener, View.OnLayoutChangeListener {
    private boolean mColorAnimated = false;
    private final ListView mListView;
    private int mPosHighlight;

    public ListViewHighlighter(ListView listView, int posHighlight) {
        this.mListView = listView;
        this.mPosHighlight = posHighlight;
        this.mListView.setOnScrollListener(this);
        this.mListView.setRecyclerListener(this);
        this.mListView.addOnLayoutChangeListener(this);
        this.mListView.post(new Runnable() {
            public final void run() {
                ListViewHighlighter.this.tryHighlight();
            }
        });
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        this.mListView.post(new Runnable() {
            public final void run() {
                ListViewHighlighter.this.tryHighlight();
            }
        });
    }

    /* access modifiers changed from: private */
    public void tryHighlight() {
        if (this.mPosHighlight >= 0 && this.mListView.getChildCount() != 0 && !highlightIfVisible(this.mListView.getFirstVisiblePosition(), this.mListView.getLastVisiblePosition())) {
            this.mListView.smoothScrollToPosition(this.mPosHighlight);
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        highlightIfVisible(firstVisibleItem, firstVisibleItem + visibleItemCount);
    }

    private boolean highlightIfVisible(int start, int end) {
        if (this.mPosHighlight < 0 || this.mListView.getChildCount() == 0 || start > this.mPosHighlight || this.mPosHighlight > end) {
            return false;
        }
        highlightView(this.mListView.getChildAt(this.mPosHighlight - start));
        this.mListView.setOnScrollListener((AbsListView.OnScrollListener) null);
        this.mListView.removeOnLayoutChangeListener(this);
        this.mPosHighlight = -1;
        return true;
    }

    public void onMovedToScrapHeap(View view) {
        unhighlightView(view);
    }

    private void highlightView(View view) {
        if (!Boolean.TRUE.equals(view.getTag(R.id.view_highlighted))) {
            view.setTag(R.id.view_highlighted, true);
            view.setTag(R.id.view_unhighlight_background, view.getBackground());
            view.setBackground(getHighlightBackground());
            view.postDelayed(new Runnable(view) {
                private final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ListViewHighlighter.this.unhighlightView(this.f$1);
                }
            }, 15000);
        }
    }

    /* access modifiers changed from: private */
    public void unhighlightView(View view) {
        if (Boolean.TRUE.equals(view.getTag(R.id.view_highlighted))) {
            Object background = view.getTag(R.id.view_unhighlight_background);
            if (background instanceof Drawable) {
                view.setBackground((Drawable) background);
            }
            view.setTag(R.id.view_unhighlight_background, (Object) null);
            view.setTag(R.id.view_highlighted, false);
        }
    }

    private ColorDrawable getHighlightBackground() {
        int color = ColorUtils.setAlphaComponent(Themes.getColorAccent(this.mListView.getContext()), 26);
        if (this.mColorAnimated) {
            return new ColorDrawable(color);
        }
        this.mColorAnimated = true;
        ColorDrawable bg = new ColorDrawable(-1);
        ObjectAnimator anim = ObjectAnimator.ofInt(bg, "color", new int[]{-1, color});
        anim.setEvaluator(new ArgbEvaluator());
        anim.setDuration(200);
        anim.setRepeatMode(2);
        anim.setRepeatCount(4);
        anim.start();
        return bg;
    }
}
