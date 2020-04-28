package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.util.SparseArray;
import android.view.animation.Interpolator;
import com.android.launcher3.LauncherAnimUtils;
import java.util.ArrayList;
import java.util.List;

public class AnimatorSetBuilder {
    public static final int ANIM_ALL_APPS_FADE = 5;
    public static final int ANIM_OVERVIEW_FADE = 4;
    public static final int ANIM_OVERVIEW_SCALE = 3;
    public static final int ANIM_VERTICAL_PROGRESS = 0;
    public static final int ANIM_WORKSPACE_FADE = 2;
    public static final int ANIM_WORKSPACE_SCALE = 1;
    protected final ArrayList<Animator> mAnims = new ArrayList<>();
    private final SparseArray<Interpolator> mInterpolators = new SparseArray<>();
    /* access modifiers changed from: private */
    public List<Runnable> mOnFinishRunnables = new ArrayList();

    public void startTag(Object obj) {
    }

    public void play(Animator anim) {
        this.mAnims.add(anim);
    }

    public void addOnFinishRunnable(Runnable onFinishRunnable) {
        this.mOnFinishRunnables.add(onFinishRunnable);
    }

    public AnimatorSet build() {
        AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
        anim.playTogether(this.mAnims);
        if (!this.mOnFinishRunnables.isEmpty()) {
            anim.addListener(new AnimationSuccessListener() {
                public void onAnimationSuccess(Animator animation) {
                    for (Runnable onFinishRunnable : AnimatorSetBuilder.this.mOnFinishRunnables) {
                        onFinishRunnable.run();
                    }
                    AnimatorSetBuilder.this.mOnFinishRunnables.clear();
                }
            });
        }
        return anim;
    }

    public Interpolator getInterpolator(int animId, Interpolator fallback) {
        return this.mInterpolators.get(animId, fallback);
    }

    public void setInterpolator(int animId, Interpolator interpolator) {
        this.mInterpolators.put(animId, interpolator);
    }
}
