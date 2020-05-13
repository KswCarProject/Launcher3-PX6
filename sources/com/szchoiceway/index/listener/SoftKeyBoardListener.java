package com.szchoiceway.index.listener;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class SoftKeyBoardListener {
    /* access modifiers changed from: private */
    public OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;
    /* access modifiers changed from: private */
    public View rootView;
    int rootViewVisibleHeight;

    public interface OnSoftKeyBoardChangeListener {
        void keyBoardHide(int i);

        void keyBoardShow(int i);
    }

    public SoftKeyBoardListener(Activity activity) {
        this.rootView = activity.getWindow().getDecorView();
        this.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                SoftKeyBoardListener.this.rootView.getWindowVisibleDisplayFrame(r);
                int visibleHeight = r.height();
                System.out.println("" + visibleHeight);
                if (SoftKeyBoardListener.this.rootViewVisibleHeight == 0) {
                    SoftKeyBoardListener.this.rootViewVisibleHeight = visibleHeight;
                } else if (SoftKeyBoardListener.this.rootViewVisibleHeight == visibleHeight) {
                } else {
                    if (SoftKeyBoardListener.this.rootViewVisibleHeight - visibleHeight > 200) {
                        if (SoftKeyBoardListener.this.onSoftKeyBoardChangeListener != null) {
                            SoftKeyBoardListener.this.onSoftKeyBoardChangeListener.keyBoardShow(SoftKeyBoardListener.this.rootViewVisibleHeight - visibleHeight);
                        }
                        SoftKeyBoardListener.this.rootViewVisibleHeight = visibleHeight;
                    } else if (visibleHeight - SoftKeyBoardListener.this.rootViewVisibleHeight > 200) {
                        if (SoftKeyBoardListener.this.onSoftKeyBoardChangeListener != null) {
                            SoftKeyBoardListener.this.onSoftKeyBoardChangeListener.keyBoardHide(visibleHeight - SoftKeyBoardListener.this.rootViewVisibleHeight);
                        }
                        SoftKeyBoardListener.this.rootViewVisibleHeight = visibleHeight;
                    }
                }
            }
        });
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener2) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener2;
    }

    public static void setListener(Activity activity, OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener2) {
        new SoftKeyBoardListener(activity).setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener2);
    }
}
