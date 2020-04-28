package com.android.launcher3.util;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

public class TransformingTouchDelegate extends TouchDelegate {
    private static final Rect sTempRect = new Rect();
    private final RectF mBounds = new RectF();
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private final RectF mTouchCheckBounds = new RectF();
    private float mTouchExtension;
    private boolean mWasTouchOutsideBounds;

    public TransformingTouchDelegate(View delegateView) {
        super(sTempRect, delegateView);
        this.mDelegateView = delegateView;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        this.mBounds.set((float) left, (float) top, (float) right, (float) bottom);
        updateTouchBounds();
    }

    public void extendTouchBounds(float extension) {
        this.mTouchExtension = extension;
        updateTouchBounds();
    }

    private void updateTouchBounds() {
        this.mTouchCheckBounds.set(this.mBounds);
        this.mTouchCheckBounds.inset(-this.mTouchExtension, -this.mTouchExtension);
    }

    public void setDelegateView(View view) {
        this.mDelegateView = view;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean sendToDelegate = false;
        switch (event.getAction()) {
            case 0:
                this.mDelegateTargeted = this.mTouchCheckBounds.contains(event.getX(), event.getY());
                if (this.mDelegateTargeted) {
                    this.mWasTouchOutsideBounds = !this.mBounds.contains(event.getX(), event.getY());
                    sendToDelegate = true;
                    break;
                }
                break;
            case 1:
            case 3:
                sendToDelegate = this.mDelegateTargeted;
                this.mDelegateTargeted = false;
                break;
            case 2:
                sendToDelegate = this.mDelegateTargeted;
                break;
        }
        if (!sendToDelegate) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        if (this.mWasTouchOutsideBounds) {
            event.setLocation(this.mBounds.centerX(), this.mBounds.centerY());
        } else {
            event.offsetLocation(-this.mBounds.left, -this.mBounds.top);
        }
        boolean handled = this.mDelegateView.dispatchTouchEvent(event);
        event.setLocation(x, y);
        return handled;
    }
}
