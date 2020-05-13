package android.support.v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(16)
class ViewPropertyAnimatorCompatJB {
    ViewPropertyAnimatorCompatJB() {
    }

    public static void setListener(final View view, final ViewPropertyAnimatorListener listener) {
        if (listener != null) {
            view.animate().setListener(new AnimatorListenerAdapter() {
                public void onAnimationCancel(Animator animation) {
                    listener.onAnimationCancel(view);
                }

                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(view);
                }

                public void onAnimationStart(Animator animation) {
                    listener.onAnimationStart(view);
                }
            });
        } else {
            view.animate().setListener((Animator.AnimatorListener) null);
        }
    }
}
