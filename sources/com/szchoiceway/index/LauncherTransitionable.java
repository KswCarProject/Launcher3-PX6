package com.szchoiceway.index;

import android.view.View;

/* compiled from: Launcher */
interface LauncherTransitionable {
    View getContent();

    void onLauncherTransitionEnd(Launcher launcher, boolean z, boolean z2);

    void onLauncherTransitionPrepare(Launcher launcher, boolean z, boolean z2);

    void onLauncherTransitionStart(Launcher launcher, boolean z, boolean z2);

    void onLauncherTransitionStep(Launcher launcher, float f);
}
