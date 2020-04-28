package com.android.launcher3.uioverrides;

import android.app.Activity;
import android.content.Context;
import android.os.CancellationSignal;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.util.TouchController;
import java.io.PrintWriter;

public class UiFactory {
    public static TouchController[] createTouchControllers(Launcher launcher) {
        return new TouchController[]{launcher.getDragController(), new AllAppsSwipeController(launcher)};
    }

    public static void setOnTouchControllersChangedListener(Context context, Runnable listener) {
    }

    public static LauncherStateManager.StateHandler[] getStateHandler(Launcher launcher) {
        return new LauncherStateManager.StateHandler[]{launcher.getAllAppsController(), launcher.getWorkspace()};
    }

    public static void resetOverview(Launcher launcher) {
    }

    public static void onLauncherStateOrFocusChanged(Launcher launcher) {
    }

    public static void onCreate(Launcher launcher) {
    }

    public static void onStart(Launcher launcher) {
    }

    public static void onEnterAnimationComplete(Context context) {
    }

    public static void onLauncherStateOrResumeChanged(Launcher launcher) {
    }

    public static void onTrimMemory(Launcher launcher, int level) {
    }

    public static void useFadeOutAnimationForLauncherStart(Launcher launcher, CancellationSignal cancellationSignal) {
    }

    public static boolean dumpActivity(Activity activity, PrintWriter writer) {
        return false;
    }

    public static void prepareToShowOverview(Launcher launcher) {
    }

    public static void setBackButtonAlpha(Launcher launcher, float alpha, boolean animate) {
    }
}
