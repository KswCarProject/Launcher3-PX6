package com.android.launcher3.util;

import android.view.Window;
import com.android.launcher3.Utilities;
import java.util.Arrays;

public class SystemUiController {
    public static final int FLAG_DARK_NAV = 2;
    public static final int FLAG_DARK_STATUS = 8;
    public static final int FLAG_LIGHT_NAV = 1;
    public static final int FLAG_LIGHT_STATUS = 4;
    public static final int UI_STATE_ALL_APPS = 1;
    public static final int UI_STATE_BASE_WINDOW = 0;
    public static final int UI_STATE_OVERVIEW = 4;
    public static final int UI_STATE_ROOT_VIEW = 3;
    public static final int UI_STATE_WIDGET_BOTTOM_SHEET = 2;
    private final int[] mStates = new int[5];
    private final Window mWindow;

    public SystemUiController(Window window) {
        this.mWindow = window;
    }

    public void updateUiState(int uiState, boolean isLight) {
        updateUiState(uiState, isLight ? 5 : 10);
    }

    public void updateUiState(int uiState, int flags) {
        if (this.mStates[uiState] != flags) {
            this.mStates[uiState] = flags;
            int oldFlags = this.mWindow.getDecorView().getSystemUiVisibility();
            int newFlags = oldFlags;
            for (int stateFlag : this.mStates) {
                if (Utilities.ATLEAST_OREO) {
                    if ((stateFlag & 1) != 0) {
                        newFlags |= 16;
                    } else if ((stateFlag & 2) != 0) {
                        newFlags &= -17;
                    }
                }
                if ((stateFlag & 4) != 0) {
                    newFlags |= 8192;
                } else if ((stateFlag & 8) != 0) {
                    newFlags &= -8193;
                }
            }
            if (newFlags != oldFlags) {
                this.mWindow.getDecorView().setSystemUiVisibility(newFlags);
            }
        }
    }

    public String toString() {
        return "mStates=" + Arrays.toString(this.mStates);
    }
}
