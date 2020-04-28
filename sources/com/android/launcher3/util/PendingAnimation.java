package com.android.launcher3.util;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

@TargetApi(26)
public class PendingAnimation {
    public final AnimatorSet anim;
    private final ArrayList<Consumer<OnEndListener>> mEndListeners = new ArrayList<>();

    public PendingAnimation(AnimatorSet anim2) {
        this.anim = anim2;
    }

    public void finish(boolean isSuccess, int logAction) {
        Iterator<Consumer<OnEndListener>> it = this.mEndListeners.iterator();
        while (it.hasNext()) {
            it.next().accept(new OnEndListener(isSuccess, logAction));
        }
        this.mEndListeners.clear();
    }

    public void addEndListener(Consumer<OnEndListener> listener) {
        this.mEndListeners.add(listener);
    }

    public static class OnEndListener {
        public boolean isSuccess;
        public int logAction;

        public OnEndListener(boolean isSuccess2, int logAction2) {
            this.isSuccess = isSuccess2;
            this.logAction = logAction2;
        }
    }
}
