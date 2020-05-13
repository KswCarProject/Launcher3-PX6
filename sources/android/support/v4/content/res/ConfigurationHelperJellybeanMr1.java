package android.support.v4.content.res;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@RequiresApi(17)
class ConfigurationHelperJellybeanMr1 {
    ConfigurationHelperJellybeanMr1() {
    }

    static int getDensityDpi(@NonNull Resources resources) {
        return resources.getConfiguration().densityDpi;
    }
}
