package com.android.launcher3;

import android.graphics.Rect;
import android.view.animation.Interpolator;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.states.SpringLoadedState;
import com.android.launcher3.uioverrides.AllAppsState;
import com.android.launcher3.uioverrides.FastOverviewState;
import com.android.launcher3.uioverrides.OverviewState;
import com.android.launcher3.uioverrides.UiFactory;
import java.util.Arrays;

public class LauncherState {
    public static final LauncherState ALL_APPS = new AllAppsState(4);
    public static final int ALL_APPS_CONTENT = 16;
    public static final int ALL_APPS_HEADER = 4;
    public static final int ALL_APPS_HEADER_EXTRA = 8;
    protected static final PageAlphaProvider DEFAULT_ALPHA_PROVIDER = new PageAlphaProvider(Interpolators.ACCEL_2) {
        public float getPageAlpha(int pageIndex) {
            return 1.0f;
        }
    };
    public static final LauncherState FAST_OVERVIEW = new FastOverviewState(3);
    protected static final int FLAG_DISABLE_ACCESSIBILITY = 2;
    protected static final int FLAG_DISABLE_INTERACTION = 64;
    protected static final int FLAG_DISABLE_PAGE_CLIPPING = 16;
    protected static final int FLAG_DISABLE_RESTORE = 4;
    protected static final int FLAG_HAS_SYS_UI_SCRIM = 512;
    protected static final int FLAG_HIDE_BACK_BUTTON = 256;
    protected static final int FLAG_MULTI_PAGE = 1;
    protected static final int FLAG_OVERVIEW_UI = 128;
    protected static final int FLAG_PAGE_BACKGROUNDS = 32;
    protected static final int FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED = 8;
    public static final int HOTSEAT_ICONS = 1;
    public static final int HOTSEAT_SEARCH_BOX = 2;
    public static final int NONE = 0;
    public static final LauncherState NORMAL = new LauncherState(0, 1, 0, 780);
    public static final LauncherState OVERVIEW = new OverviewState(2);
    public static final LauncherState SPRING_LOADED = new SpringLoadedState(1);
    public static final int VERTICAL_SWIPE_INDICATOR = 32;
    private static final LauncherState[] sAllStates = new LauncherState[5];
    protected static final Rect sTempRect = new Rect();
    public final int containerType;
    public final boolean disableInteraction;
    public final boolean disablePageClipping;
    public final boolean disableRestore;
    public final boolean hasMultipleVisiblePages;
    public final boolean hasSysUiScrim;
    public final boolean hasWorkspacePageBackground;
    public final boolean hideBackButton;
    public final int ordinal;
    public final boolean overviewUi;
    public final int transitionDuration;
    public final int workspaceAccessibilityFlag;
    public final boolean workspaceIconsCanBeDragged;

    public LauncherState(int id, int containerType2, int transitionDuration2, int flags) {
        this.containerType = containerType2;
        this.transitionDuration = transitionDuration2;
        boolean z = false;
        this.hasWorkspacePageBackground = (flags & 32) != 0;
        this.hasMultipleVisiblePages = (flags & 1) != 0;
        this.workspaceAccessibilityFlag = (flags & 2) != 0 ? 4 : 0;
        this.disableRestore = (flags & 4) != 0;
        this.workspaceIconsCanBeDragged = (flags & 8) != 0;
        this.disablePageClipping = (flags & 16) != 0;
        this.disableInteraction = (flags & 64) != 0;
        this.overviewUi = (flags & 128) != 0;
        this.hideBackButton = (flags & 256) != 0;
        this.hasSysUiScrim = (flags & 512) != 0 ? true : z;
        this.ordinal = id;
        sAllStates[id] = this;
    }

    public static LauncherState[] values() {
        return (LauncherState[]) Arrays.copyOf(sAllStates, sAllStates.length);
    }

    public float[] getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new float[]{1.0f, 0.0f, 0.0f};
    }

    public float[] getOverviewScaleAndTranslationYFactor(Launcher launcher) {
        return new float[]{1.1f, 0.0f};
    }

    public void onStateEnabled(Launcher launcher) {
        dispatchWindowStateChanged(launcher);
    }

    public void onStateDisabled(Launcher launcher) {
    }

    public int getVisibleElements(Launcher launcher) {
        if (launcher.getDeviceProfile().isVerticalBarLayout()) {
            return 33;
        }
        return 35;
    }

    public float getVerticalProgress(Launcher launcher) {
        return 1.0f;
    }

    public float getWorkspaceScrimAlpha(Launcher launcher) {
        return 0.0f;
    }

    public String getDescription(Launcher launcher) {
        return launcher.getWorkspace().getCurrentPageDescription();
    }

    public PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        if (this != NORMAL || !launcher.getDeviceProfile().shouldFadeAdjacentWorkspaceScreens()) {
            return DEFAULT_ALPHA_PROVIDER;
        }
        final int centerPage = launcher.getWorkspace().getNextPage();
        return new PageAlphaProvider(Interpolators.ACCEL_2) {
            public float getPageAlpha(int pageIndex) {
                return pageIndex != centerPage ? 0.0f : 1.0f;
            }
        };
    }

    public LauncherState getHistoryForState(LauncherState previousState) {
        return NORMAL;
    }

    public void onStateTransitionEnd(Launcher launcher) {
        if (this == NORMAL || this == SPRING_LOADED) {
            UiFactory.resetOverview(launcher);
        }
        if (this == NORMAL) {
            launcher.getRotationHelper().setCurrentStateRequest(0);
        }
    }

    protected static void dispatchWindowStateChanged(Launcher launcher) {
        launcher.getWindow().getDecorView().sendAccessibilityEvent(32);
    }

    public static abstract class PageAlphaProvider {
        public final Interpolator interpolator;

        public abstract float getPageAlpha(int i);

        public PageAlphaProvider(Interpolator interpolator2) {
            this.interpolator = interpolator2;
        }
    }
}
