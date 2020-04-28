package com.android.launcher3.allapps;

import android.view.KeyEvent;

public interface SearchUiManager {
    void initialize(AllAppsContainerView allAppsContainerView);

    void preDispatchKeyEvent(KeyEvent keyEvent);

    void resetSearch();
}
