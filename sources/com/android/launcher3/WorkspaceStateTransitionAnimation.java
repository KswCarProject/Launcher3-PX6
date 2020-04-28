package com.android.launcher3;

import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.graphics.WorkspaceAndHotseatScrim;

public class WorkspaceStateTransitionAnimation {
    private final Launcher mLauncher;
    private float mNewScale;
    private final Workspace mWorkspace;

    public WorkspaceStateTransitionAnimation(Launcher launcher, Workspace workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
    }

    public void setState(LauncherState toState) {
        setWorkspaceProperty(toState, PropertySetter.NO_ANIM_PROPERTY_SETTER, new AnimatorSetBuilder(), new LauncherStateManager.AnimationConfig());
    }

    public void setStateWithAnimation(LauncherState toState, AnimatorSetBuilder builder, LauncherStateManager.AnimationConfig config) {
        setWorkspaceProperty(toState, config.getPropertySetter(builder), builder, config);
    }

    public float getFinalScale() {
        return this.mNewScale;
    }

    private void setWorkspaceProperty(LauncherState state, PropertySetter propertySetter, AnimatorSetBuilder builder, LauncherStateManager.AnimationConfig config) {
        LauncherState launcherState = state;
        PropertySetter propertySetter2 = propertySetter;
        AnimatorSetBuilder animatorSetBuilder = builder;
        float[] scaleAndTranslation = launcherState.getWorkspaceScaleAndTranslation(this.mLauncher);
        int i = 0;
        this.mNewScale = scaleAndTranslation[0];
        LauncherState.PageAlphaProvider pageAlphaProvider = launcherState.getWorkspacePageAlphaProvider(this.mLauncher);
        int childCount = this.mWorkspace.getChildCount();
        while (true) {
            int i2 = i;
            if (i2 >= childCount) {
                break;
            }
            applyChildState(state, (CellLayout) this.mWorkspace.getChildAt(i2), i2, pageAlphaProvider, propertySetter, builder, config);
            i = i2 + 1;
        }
        int elements = launcherState.getVisibleElements(this.mLauncher);
        Interpolator fadeInterpolator = animatorSetBuilder.getInterpolator(2, pageAlphaProvider.interpolator);
        boolean playAtomicComponent = config.playAtomicComponent();
        if (playAtomicComponent) {
            propertySetter2.setFloat(this.mWorkspace, LauncherAnimUtils.SCALE_PROPERTY, this.mNewScale, animatorSetBuilder.getInterpolator(1, Interpolators.ZOOM_OUT));
            float hotseatIconsAlpha = (elements & 1) != 0 ? 1.0f : 0.0f;
            propertySetter2.setViewAlpha(this.mLauncher.getHotseat().getLayout(), hotseatIconsAlpha, fadeInterpolator);
            propertySetter2.setViewAlpha(this.mLauncher.getWorkspace().getPageIndicator(), hotseatIconsAlpha, fadeInterpolator);
        }
        if (config.playNonAtomicComponent()) {
            Interpolator translationInterpolator = !playAtomicComponent ? Interpolators.LINEAR : Interpolators.ZOOM_OUT;
            propertySetter2.setFloat(this.mWorkspace, View.TRANSLATION_X, scaleAndTranslation[1], translationInterpolator);
            propertySetter2.setFloat(this.mWorkspace, View.TRANSLATION_Y, scaleAndTranslation[2], translationInterpolator);
            propertySetter2.setViewAlpha(this.mLauncher.getHotseatSearchBox(), (elements & 2) != 0 ? 1.0f : 0.0f, fadeInterpolator);
            WorkspaceAndHotseatScrim scrim = this.mLauncher.getDragLayer().getScrim();
            propertySetter2.setFloat(scrim, WorkspaceAndHotseatScrim.SCRIM_PROGRESS, launcherState.getWorkspaceScrimAlpha(this.mLauncher), Interpolators.LINEAR);
            propertySetter2.setFloat(scrim, WorkspaceAndHotseatScrim.SYSUI_PROGRESS, launcherState.hasSysUiScrim ? 1.0f : 0.0f, Interpolators.LINEAR);
        }
    }

    public void applyChildState(LauncherState state, CellLayout cl, int childIndex) {
        applyChildState(state, cl, childIndex, state.getWorkspacePageAlphaProvider(this.mLauncher), PropertySetter.NO_ANIM_PROPERTY_SETTER, new AnimatorSetBuilder(), new LauncherStateManager.AnimationConfig());
    }

    private void applyChildState(LauncherState state, CellLayout cl, int childIndex, LauncherState.PageAlphaProvider pageAlphaProvider, PropertySetter propertySetter, AnimatorSetBuilder builder, LauncherStateManager.AnimationConfig config) {
        float pageAlpha = pageAlphaProvider.getPageAlpha(childIndex);
        int drawableAlpha = Math.round(((float) (state.hasWorkspacePageBackground ? 255 : 0)) * pageAlpha);
        if (config.playNonAtomicComponent()) {
            propertySetter.setInt(cl.getScrimBackground(), LauncherAnimUtils.DRAWABLE_ALPHA, drawableAlpha, Interpolators.ZOOM_OUT);
        }
        if (config.playAtomicComponent()) {
            propertySetter.setFloat(cl.getShortcutsAndWidgets(), View.ALPHA, pageAlpha, builder.getInterpolator(2, pageAlphaProvider.interpolator));
        }
    }
}
