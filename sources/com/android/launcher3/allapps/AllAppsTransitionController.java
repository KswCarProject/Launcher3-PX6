package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.R;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.pageindicators.WorkspacePageIndicator;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.RecyclerViewFastScroller;
import com.android.launcher3.views.ScrimView;

public class AllAppsTransitionController implements LauncherStateManager.StateHandler, DeviceProfile.OnDeviceProfileChangeListener {
    public static final Property<AllAppsTransitionController, Float> ALL_APPS_PROGRESS = new Property<AllAppsTransitionController, Float>(Float.class, "allAppsProgress") {
        public Float get(AllAppsTransitionController controller) {
            return Float.valueOf(controller.mProgress);
        }

        public void set(AllAppsTransitionController controller, Float progress) {
            controller.setProgress(progress.floatValue());
        }
    };
    private AllAppsContainerView mAppsView;
    private final boolean mIsDarkTheme;
    private boolean mIsVerticalLayout;
    private final Launcher mLauncher;
    /* access modifiers changed from: private */
    public float mProgress;
    private ScrimView mScrimView;
    private float mScrollRangeDelta = 0.0f;
    private float mShiftRange;

    public AllAppsTransitionController(Launcher l) {
        this.mLauncher = l;
        this.mShiftRange = (float) this.mLauncher.getDeviceProfile().heightPx;
        this.mProgress = 1.0f;
        this.mIsDarkTheme = Themes.getAttrBoolean(this.mLauncher, R.attr.isMainColorDark);
        this.mIsVerticalLayout = this.mLauncher.getDeviceProfile().isVerticalBarLayout();
        this.mLauncher.addOnDeviceProfileChangeListener(this);
    }

    public float getShiftRange() {
        return this.mShiftRange;
    }

    /* access modifiers changed from: private */
    public void onProgressAnimationStart() {
        this.mAppsView.setVisibility(0);
    }

    public void onDeviceProfileChanged(DeviceProfile dp) {
        this.mIsVerticalLayout = dp.isVerticalBarLayout();
        setScrollRangeDelta(this.mScrollRangeDelta);
        if (this.mIsVerticalLayout) {
            this.mAppsView.setAlpha(1.0f);
            this.mLauncher.getHotseat().setTranslationY(0.0f);
            ((WorkspacePageIndicator) this.mLauncher.getWorkspace().getPageIndicator()).setTranslationY(0.0f);
        }
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        this.mScrimView.setProgress(progress);
        float shiftCurrent = this.mShiftRange * progress;
        this.mAppsView.setTranslationY(shiftCurrent);
        float hotseatTranslation = (-this.mShiftRange) + shiftCurrent;
        if (!this.mIsVerticalLayout) {
            this.mLauncher.getHotseat().setTranslationY(hotseatTranslation);
            ((WorkspacePageIndicator) this.mLauncher.getWorkspace().getPageIndicator()).setTranslationY(hotseatTranslation);
        }
        if (shiftCurrent - ((float) this.mScrimView.getDragHandleSize()) <= ((float) (this.mLauncher.getDeviceProfile().getInsets().top / 2))) {
            this.mLauncher.getSystemUiController().updateUiState(1, !this.mIsDarkTheme);
        } else {
            this.mLauncher.getSystemUiController().updateUiState(1, 0);
        }
    }

    public float getProgress() {
        return this.mProgress;
    }

    public void setState(LauncherState state) {
        setProgress(state.getVerticalProgress(this.mLauncher));
        setAlphas(state, (LauncherStateManager.AnimationConfig) null, new AnimatorSetBuilder());
        onProgressAnimationEnd();
    }

    public void setStateWithAnimation(LauncherState toState, AnimatorSetBuilder builder, LauncherStateManager.AnimationConfig config) {
        Interpolator interpolator;
        float targetProgress = toState.getVerticalProgress(this.mLauncher);
        if (Float.compare(this.mProgress, targetProgress) == 0) {
            setAlphas(toState, config, builder);
            onProgressAnimationEnd();
        } else if (config.playNonAtomicComponent()) {
            if (config.userControlled) {
                interpolator = Interpolators.LINEAR;
            } else {
                interpolator = toState == LauncherState.OVERVIEW ? builder.getInterpolator(3, Interpolators.FAST_OUT_SLOW_IN) : Interpolators.FAST_OUT_SLOW_IN;
            }
            ObjectAnimator anim = ObjectAnimator.ofFloat(this, ALL_APPS_PROGRESS, new float[]{this.mProgress, targetProgress});
            anim.setDuration(config.duration);
            anim.setInterpolator(builder.getInterpolator(0, interpolator));
            anim.addListener(getProgressAnimatorListener());
            builder.play(anim);
            setAlphas(toState, config, builder);
        }
    }

    private void setAlphas(LauncherState toState, LauncherStateManager.AnimationConfig config, AnimatorSetBuilder builder) {
        PropertySetter setter;
        if (config == null) {
            setter = PropertySetter.NO_ANIM_PROPERTY_SETTER;
        } else {
            setter = config.getPropertySetter(builder);
        }
        int visibleElements = toState.getVisibleElements(this.mLauncher);
        boolean hasContent = true;
        int i = 0;
        boolean hasHeader = (visibleElements & 4) != 0;
        boolean hasHeaderExtra = (visibleElements & 8) != 0;
        if ((visibleElements & 16) == 0) {
            hasContent = false;
        }
        Interpolator allAppsFade = builder.getInterpolator(5, Interpolators.LINEAR);
        float f = 0.0f;
        setter.setViewAlpha(this.mAppsView.getSearchView(), hasHeader ? 1.0f : 0.0f, allAppsFade);
        setter.setViewAlpha(this.mAppsView.getContentView(), hasContent ? 1.0f : 0.0f, allAppsFade);
        RecyclerViewFastScroller scrollBar = this.mAppsView.getScrollBar();
        if (hasContent) {
            f = 1.0f;
        }
        setter.setViewAlpha(scrollBar, f, allAppsFade);
        this.mAppsView.getFloatingHeaderView().setContentVisibility(hasHeaderExtra, hasContent, setter, allAppsFade);
        ScrimView scrimView = this.mScrimView;
        Property<ScrimView, Integer> property = ScrimView.DRAG_HANDLE_ALPHA;
        if ((visibleElements & 32) != 0) {
            i = 255;
        }
        setter.setInt(scrimView, property, i, Interpolators.LINEAR);
    }

    public AnimatorListenerAdapter getProgressAnimatorListener() {
        return new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                AllAppsTransitionController.this.onProgressAnimationEnd();
            }

            public void onAnimationStart(Animator animation) {
                AllAppsTransitionController.this.onProgressAnimationStart();
            }
        };
    }

    public void setupViews(AllAppsContainerView appsView) {
        this.mAppsView = appsView;
        this.mScrimView = (ScrimView) this.mLauncher.findViewById(R.id.scrim_view);
    }

    public void setScrollRangeDelta(float delta) {
        this.mScrollRangeDelta = delta;
        this.mShiftRange = ((float) this.mLauncher.getDeviceProfile().heightPx) - this.mScrollRangeDelta;
        if (this.mScrimView != null) {
            this.mScrimView.reInitUi();
        }
    }

    /* access modifiers changed from: private */
    public void onProgressAnimationEnd() {
        if (Float.compare(this.mProgress, 1.0f) == 0) {
            this.mAppsView.setVisibility(4);
            this.mAppsView.reset(false);
        } else if (Float.compare(this.mProgress, 0.0f) == 0) {
            this.mAppsView.setVisibility(0);
            this.mAppsView.onScrollUpEnd();
        } else {
            this.mAppsView.setVisibility(0);
        }
    }
}
