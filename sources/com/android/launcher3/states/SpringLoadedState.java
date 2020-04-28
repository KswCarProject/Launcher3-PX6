package com.android.launcher3.states;

import android.graphics.Rect;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InstallShortcutReceiver;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.pageindicators.WorkspacePageIndicator;

public class SpringLoadedState extends LauncherState {
    private static final int STATE_FLAGS = 319;

    public SpringLoadedState(int id) {
        super(id, 6, 150, STATE_FLAGS);
    }

    public float[] getWorkspaceScaleAndTranslation(Launcher launcher) {
        DeviceProfile grid = launcher.getDeviceProfile();
        Workspace ws = launcher.getWorkspace();
        if (ws.getChildCount() == 0) {
            return super.getWorkspaceScaleAndTranslation(launcher);
        }
        if (grid.isVerticalBarLayout()) {
            return new float[]{grid.workspaceSpringLoadShrinkFactor, 0.0f, 0.0f};
        }
        float scale = grid.workspaceSpringLoadShrinkFactor;
        Rect insets = launcher.getDragLayer().getInsets();
        float shrunkTop = (float) (insets.top + grid.dropTargetBarSizePx);
        float totalShrunkSpace = ((float) (((ws.getMeasuredHeight() - insets.bottom) - grid.workspacePadding.bottom) - grid.workspaceSpringLoadedBottomSpace)) - shrunkTop;
        float halfHeight = (float) (ws.getHeight() / 2);
        return new float[]{scale, 0.0f, ((((totalShrunkSpace - (((float) ws.getNormalChildHeight()) * scale)) / 2.0f) + shrunkTop) - ((((float) ws.getTop()) + halfHeight) - ((halfHeight - ((float) ws.getChildAt(0).getTop())) * scale))) / scale};
    }

    public void onStateEnabled(Launcher launcher) {
        Workspace ws = launcher.getWorkspace();
        ws.showPageIndicatorAtCurrentScroll();
        ((WorkspacePageIndicator) ws.getPageIndicator()).setShouldAutoHide(false);
        InstallShortcutReceiver.enableInstallQueue(4);
        launcher.getRotationHelper().setCurrentStateRequest(2);
    }

    public float getWorkspaceScrimAlpha(Launcher launcher) {
        return 0.3f;
    }

    public void onStateDisabled(Launcher launcher) {
        ((WorkspacePageIndicator) launcher.getWorkspace().getPageIndicator()).setShouldAutoHide(true);
        InstallShortcutReceiver.disableAndFlushInstallQueue(4, launcher);
    }
}
