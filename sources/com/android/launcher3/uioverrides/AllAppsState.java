package com.android.launcher3.uioverrides;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.allapps.DiscoveryBounce;
import com.android.launcher3.anim.Interpolators;

public class AllAppsState extends LauncherState {
    private static final LauncherState.PageAlphaProvider PAGE_ALPHA_PROVIDER = new LauncherState.PageAlphaProvider(Interpolators.DEACCEL_2) {
        public float getPageAlpha(int pageIndex) {
            return 0.0f;
        }
    };
    private static final float PARALLAX_COEFFICIENT = 0.125f;
    private static final int STATE_FLAGS = 2;

    public AllAppsState(int id) {
        super(id, 4, LauncherAnimUtils.ALL_APPS_TRANSITION_MS, 2);
    }

    public void onStateEnabled(Launcher launcher) {
        if (!launcher.getSharedPrefs().getBoolean(DiscoveryBounce.HOME_BOUNCE_SEEN, false)) {
            launcher.getSharedPrefs().edit().putBoolean(DiscoveryBounce.HOME_BOUNCE_SEEN, true).apply();
        }
        AbstractFloatingView.closeAllOpenViews(launcher);
        dispatchWindowStateChanged(launcher);
    }

    public String getDescription(Launcher launcher) {
        return launcher.getString(R.string.all_apps_button_label);
    }

    public int getVisibleElements(Launcher launcher) {
        return 20;
    }

    public float[] getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new float[]{1.0f, 0.0f, (-launcher.getAllAppsController().getShiftRange()) * PARALLAX_COEFFICIENT};
    }

    public LauncherState.PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        return PAGE_ALPHA_PROVIDER;
    }

    public float getVerticalProgress(Launcher launcher) {
        return 0.0f;
    }
}
