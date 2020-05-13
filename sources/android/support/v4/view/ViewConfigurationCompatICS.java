package android.support.v4.view;

import android.support.annotation.RequiresApi;
import android.view.ViewConfiguration;

@RequiresApi(14)
class ViewConfigurationCompatICS {
    ViewConfigurationCompatICS() {
    }

    static boolean hasPermanentMenuKey(ViewConfiguration config) {
        return config.hasPermanentMenuKey();
    }
}
