package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class SwipeDetector {
    private static final float ANIMATION_DURATION = 1200.0f;
    private static final boolean DBG = false;
    public static final int DIRECTION_BOTH = 3;
    public static final int DIRECTION_NEGATIVE = 2;
    public static final int DIRECTION_POSITIVE = 1;
    public static final Direction HORIZONTAL = new Direction() {
        /* access modifiers changed from: package-private */
        public float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint) {
            return ev.getX(pointerIndex) - refPoint.x;
        }

        /* access modifiers changed from: package-private */
        public float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos) {
            return Math.abs(ev.getY(pointerIndex) - downPos.y);
        }
    };
    public static final float RELEASE_VELOCITY_PX_MS = 1.0f;
    public static final float SCROLL_VELOCITY_DAMPENING_RC = 15.915494f;
    private static final String TAG = "SwipeDetector";
    public static final Direction VERTICAL = new Direction() {
        /* access modifiers changed from: package-private */
        public float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint) {
            return ev.getY(pointerIndex) - refPoint.y;
        }

        /* access modifiers changed from: package-private */
        public float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos) {
            return Math.abs(ev.getX(pointerIndex) - downPos.x);
        }
    };
    protected int mActivePointerId;
    private long mCurrentMillis;
    private Direction mDir;
    private float mDisplacement;
    private final PointF mDownPos;
    private boolean mIgnoreSlopWhenSettling;
    private float mLastDisplacement;
    private final PointF mLastPos;
    private final Listener mListener;
    private int mScrollConditions;
    private ScrollState mState;
    private float mSubtractDisplacement;
    private final float mTouchSlop;
    private float mVelocity;

    public static abstract class Direction {
        /* access modifiers changed from: package-private */
        public abstract float getActiveTouchSlop(MotionEvent motionEvent, int i, PointF pointF);

        /* access modifiers changed from: package-private */
        public abstract float getDisplacement(MotionEvent motionEvent, int i, PointF pointF);
    }

    public interface Listener {
        boolean onDrag(float f, float f2);

        void onDragEnd(float f, boolean z);

        void onDragStart(boolean z);
    }

    enum ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    private void setState(ScrollState newState) {
        if (newState == ScrollState.DRAGGING) {
            initializeDragging();
            if (this.mState == ScrollState.IDLE) {
                reportDragStart(false);
            } else if (this.mState == ScrollState.SETTLING) {
                reportDragStart(true);
            }
        }
        if (newState == ScrollState.SETTLING) {
            reportDragEnd();
        }
        this.mState = newState;
    }

    public boolean isDraggingOrSettling() {
        return this.mState == ScrollState.DRAGGING || this.mState == ScrollState.SETTLING;
    }

    public boolean isIdleState() {
        return this.mState == ScrollState.IDLE;
    }

    public boolean isSettlingState() {
        return this.mState == ScrollState.SETTLING;
    }

    public boolean isDraggingState() {
        return this.mState == ScrollState.DRAGGING;
    }

    public SwipeDetector(@NonNull Context context, @NonNull Listener l, @NonNull Direction dir) {
        this((float) ViewConfiguration.get(context).getScaledTouchSlop(), l, dir);
    }

    @VisibleForTesting
    protected SwipeDetector(float touchSlope, @NonNull Listener l, @NonNull Direction dir) {
        this.mActivePointerId = -1;
        this.mState = ScrollState.IDLE;
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mTouchSlop = touchSlope;
        this.mListener = l;
        this.mDir = dir;
    }

    public void updateDirection(Direction dir) {
        this.mDir = dir;
    }

    public void setDetectableScrollConditions(int scrollDirectionFlags, boolean ignoreSlop) {
        this.mScrollConditions = scrollDirectionFlags;
        this.mIgnoreSlopWhenSettling = ignoreSlop;
    }

    public int getScrollDirections() {
        return this.mScrollConditions;
    }

    private boolean shouldScrollStart(MotionEvent ev, int pointerIndex) {
        if (Math.max(this.mDir.getActiveTouchSlop(ev, pointerIndex, this.mDownPos), this.mTouchSlop) > Math.abs(this.mDisplacement)) {
            return false;
        }
        if (((this.mScrollConditions & 2) <= 0 || this.mDisplacement <= 0.0f) && ((this.mScrollConditions & 1) <= 0 || this.mDisplacement >= 0.0f)) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        int newPointerIdx = 0;
        if (actionMasked != 6) {
            switch (actionMasked) {
                case 0:
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mDownPos.set(ev.getX(), ev.getY());
                    this.mLastPos.set(this.mDownPos);
                    this.mLastDisplacement = 0.0f;
                    this.mDisplacement = 0.0f;
                    this.mVelocity = 0.0f;
                    if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
                        setState(ScrollState.DRAGGING);
                        break;
                    }
                case 1:
                case 3:
                    if (this.mState == ScrollState.DRAGGING) {
                        setState(ScrollState.SETTLING);
                        break;
                    }
                    break;
                case 2:
                    int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex != -1) {
                        this.mDisplacement = this.mDir.getDisplacement(ev, pointerIndex, this.mDownPos);
                        computeVelocity(this.mDir.getDisplacement(ev, pointerIndex, this.mLastPos), ev.getEventTime());
                        if (this.mState != ScrollState.DRAGGING && shouldScrollStart(ev, pointerIndex)) {
                            setState(ScrollState.DRAGGING);
                        }
                        if (this.mState == ScrollState.DRAGGING) {
                            reportDragging();
                        }
                        this.mLastPos.set(ev.getX(pointerIndex), ev.getY(pointerIndex));
                        break;
                    }
                    break;
            }
        } else {
            int ptrIdx = ev.getActionIndex();
            if (ev.getPointerId(ptrIdx) == this.mActivePointerId) {
                if (ptrIdx == 0) {
                    newPointerIdx = 1;
                }
                this.mDownPos.set(ev.getX(newPointerIdx) - (this.mLastPos.x - this.mDownPos.x), ev.getY(newPointerIdx) - (this.mLastPos.y - this.mDownPos.y));
                this.mLastPos.set(ev.getX(newPointerIdx), ev.getY(newPointerIdx));
                this.mActivePointerId = ev.getPointerId(newPointerIdx);
            }
        }
        return true;
    }

    public void finishedScrolling() {
        setState(ScrollState.IDLE);
    }

    private boolean reportDragStart(boolean recatch) {
        this.mListener.onDragStart(!recatch);
        return true;
    }

    private void initializeDragging() {
        if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
            this.mSubtractDisplacement = 0.0f;
        }
        if (this.mDisplacement > 0.0f) {
            this.mSubtractDisplacement = this.mTouchSlop;
        } else {
            this.mSubtractDisplacement = -this.mTouchSlop;
        }
    }

    public boolean wasInitialTouchPositive() {
        return this.mSubtractDisplacement < 0.0f;
    }

    private boolean reportDragging() {
        if (this.mDisplacement == this.mLastDisplacement) {
            return true;
        }
        this.mLastDisplacement = this.mDisplacement;
        return this.mListener.onDrag(this.mDisplacement - this.mSubtractDisplacement, this.mVelocity);
    }

    private void reportDragEnd() {
        this.mListener.onDragEnd(this.mVelocity, Math.abs(this.mVelocity) > 1.0f);
    }

    public float computeVelocity(float delta, long currentMillis) {
        long previousMillis = this.mCurrentMillis;
        this.mCurrentMillis = currentMillis;
        float deltaTimeMillis = (float) (this.mCurrentMillis - previousMillis);
        float velocity = 0.0f;
        if (deltaTimeMillis > 0.0f) {
            velocity = delta / deltaTimeMillis;
        }
        if (Math.abs(this.mVelocity) < 0.001f) {
            this.mVelocity = velocity;
        } else {
            this.mVelocity = interpolate(this.mVelocity, velocity, computeDampeningFactor(deltaTimeMillis));
        }
        return this.mVelocity;
    }

    private static float computeDampeningFactor(float deltaTime) {
        return deltaTime / (15.915494f + deltaTime);
    }

    public static float interpolate(float from, float to, float alpha) {
        return ((1.0f - alpha) * from) + (alpha * to);
    }

    public static long calculateDuration(float velocity, float progressNeeded) {
        float velocityDivisor = Math.max(2.0f, Math.abs(0.5f * velocity));
        return (long) Math.max(100.0f, (ANIMATION_DURATION / velocityDivisor) * Math.max(0.2f, progressNeeded));
    }
}
