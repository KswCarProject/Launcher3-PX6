package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.util.Property;
import android.view.View;

public class PropertySetter {
    public static final PropertySetter NO_ANIM_PROPERTY_SETTER = new PropertySetter();

    public void setViewAlpha(View view, float alpha, TimeInterpolator interpolator) {
        if (view != null) {
            view.setAlpha(alpha);
            AlphaUpdateListener.updateVisibility(view);
        }
    }

    public <T> void setFloat(T target, Property<T, Float> property, float value, TimeInterpolator interpolator) {
        property.set(target, Float.valueOf(value));
    }

    public <T> void setInt(T target, Property<T, Integer> property, int value, TimeInterpolator interpolator) {
        property.set(target, Integer.valueOf(value));
    }

    public static class AnimatedPropertySetter extends PropertySetter {
        private final long mDuration;
        private final AnimatorSetBuilder mStateAnimator;

        public AnimatedPropertySetter(long duration, AnimatorSetBuilder builder) {
            this.mDuration = duration;
            this.mStateAnimator = builder;
        }

        public void setViewAlpha(View view, float alpha, TimeInterpolator interpolator) {
            if (view != null && view.getAlpha() != alpha) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{alpha});
                anim.addListener(new AlphaUpdateListener(view));
                anim.setDuration(this.mDuration).setInterpolator(interpolator);
                this.mStateAnimator.play(anim);
            }
        }

        public <T> void setFloat(T target, Property<T, Float> property, float value, TimeInterpolator interpolator) {
            if (property.get(target).floatValue() != value) {
                Animator anim = ObjectAnimator.ofFloat(target, property, new float[]{value});
                anim.setDuration(this.mDuration).setInterpolator(interpolator);
                this.mStateAnimator.play(anim);
            }
        }

        public <T> void setInt(T target, Property<T, Integer> property, int value, TimeInterpolator interpolator) {
            if (property.get(target).intValue() != value) {
                Animator anim = ObjectAnimator.ofInt(target, property, new int[]{value});
                anim.setDuration(this.mDuration).setInterpolator(interpolator);
                this.mStateAnimator.play(anim);
            }
        }
    }
}
