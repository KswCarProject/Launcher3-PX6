package com.android.launcher3;

import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public class LauncherAppTransitionManager {
    public static LauncherAppTransitionManager newInstance(Context context) {
        return (LauncherAppTransitionManager) Utilities.getOverrideObject(LauncherAppTransitionManager.class, context, R.string.app_transition_manager_class);
    }

    public ActivityOptions getActivityLaunchOptions(Launcher launcher, View v) {
        Drawable icon;
        if (Utilities.ATLEAST_MARSHMALLOW) {
            int left = 0;
            int top = 0;
            int width = v.getMeasuredWidth();
            int height = v.getMeasuredHeight();
            if ((v instanceof BubbleTextView) && (icon = ((BubbleTextView) v).getIcon()) != null) {
                Rect bounds = icon.getBounds();
                left = (width - bounds.width()) / 2;
                top = v.getPaddingTop();
                width = bounds.width();
                height = bounds.height();
            }
            return ActivityOptions.makeClipRevealAnimation(v, left, top, width, height);
        } else if (Utilities.ATLEAST_LOLLIPOP_MR1 != 0) {
            return ActivityOptions.makeCustomAnimation(launcher, R.anim.task_open_enter, R.anim.no_anim);
        } else {
            return null;
        }
    }
}
