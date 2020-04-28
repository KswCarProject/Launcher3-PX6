package com.android.launcher3.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.RelativeLayout;
import com.android.launcher3.LauncherSettings;

public class SpringRelativeLayout extends RelativeLayout {
    private static final FloatPropertyCompat<SpringRelativeLayout> DAMPED_SCROLL = new FloatPropertyCompat<SpringRelativeLayout>(LauncherSettings.Settings.EXTRA_VALUE) {
        public float getValue(SpringRelativeLayout object) {
            return object.mDampedScrollShift;
        }

        public void setValue(SpringRelativeLayout object, float value) {
            object.setDampedScrollShift(value);
        }
    };
    private static final float DAMPING_RATIO = 0.5f;
    private static final float STIFFNESS = 850.0f;
    private static final float VELOCITY_MULTIPLIER = 0.3f;
    private SpringEdgeEffect mActiveEdge;
    /* access modifiers changed from: private */
    public float mDampedScrollShift;
    private final SpringAnimation mSpring;
    protected final SparseBooleanArray mSpringViews;

    public SpringRelativeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SpringRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mSpringViews = new SparseBooleanArray();
        this.mDampedScrollShift = 0.0f;
        this.mSpring = new SpringAnimation(this, DAMPED_SCROLL, 0.0f);
        this.mSpring.setSpring(new SpringForce(0.0f).setStiffness(STIFFNESS).setDampingRatio(0.5f));
    }

    public void addSpringView(int id) {
        this.mSpringViews.put(id, true);
    }

    public void removeSpringView(int id) {
        this.mSpringViews.delete(id);
        invalidate();
    }

    public int getCanvasClipTopForOverscroll() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (this.mDampedScrollShift == 0.0f || !this.mSpringViews.get(child.getId())) {
            return super.drawChild(canvas, child, drawingTime);
        }
        int saveCount = canvas.save();
        canvas.clipRect(0, getCanvasClipTopForOverscroll(), getWidth(), getHeight());
        canvas.translate(0.0f, this.mDampedScrollShift);
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(saveCount);
        return result;
    }

    /* access modifiers changed from: private */
    public void setActiveEdge(SpringEdgeEffect edge) {
        if (!(this.mActiveEdge == edge || this.mActiveEdge == null)) {
            float unused = this.mActiveEdge.mDistance = 0.0f;
        }
        this.mActiveEdge = edge;
    }

    /* access modifiers changed from: protected */
    public void setDampedScrollShift(float shift) {
        if (shift != this.mDampedScrollShift) {
            this.mDampedScrollShift = shift;
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void finishScrollWithVelocity(float velocity) {
        this.mSpring.setStartVelocity(velocity);
        this.mSpring.setStartValue(this.mDampedScrollShift);
        this.mSpring.start();
    }

    /* access modifiers changed from: protected */
    public void finishWithShiftAndVelocity(float shift, float velocity, DynamicAnimation.OnAnimationEndListener listener) {
        setDampedScrollShift(shift);
        this.mSpring.addEndListener(listener);
        finishScrollWithVelocity(velocity);
    }

    public RecyclerView.EdgeEffectFactory createEdgeEffectFactory() {
        return new SpringEdgeEffectFactory();
    }

    private class SpringEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {
        private SpringEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @NonNull
        public EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
            if (direction == 1) {
                return new SpringEdgeEffect(SpringRelativeLayout.this.getContext(), SpringRelativeLayout.VELOCITY_MULTIPLIER);
            }
            if (direction != 3) {
                return super.createEdgeEffect(view, direction);
            }
            return new SpringEdgeEffect(SpringRelativeLayout.this.getContext(), -0.3f);
        }
    }

    private class SpringEdgeEffect extends EdgeEffect {
        /* access modifiers changed from: private */
        public float mDistance;
        private final float mVelocityMultiplier;

        public SpringEdgeEffect(Context context, float velocityMultiplier) {
            super(context);
            this.mVelocityMultiplier = velocityMultiplier;
        }

        public boolean draw(Canvas canvas) {
            return false;
        }

        public void onAbsorb(int velocity) {
            SpringRelativeLayout.this.finishScrollWithVelocity(((float) velocity) * this.mVelocityMultiplier);
        }

        public void onPull(float deltaDistance, float displacement) {
            SpringRelativeLayout.this.setActiveEdge(this);
            this.mDistance += (this.mVelocityMultiplier / 3.0f) * deltaDistance;
            SpringRelativeLayout.this.setDampedScrollShift(this.mDistance * ((float) SpringRelativeLayout.this.getHeight()));
        }

        public void onRelease() {
            this.mDistance = 0.0f;
            SpringRelativeLayout.this.finishScrollWithVelocity(0.0f);
        }
    }
}
