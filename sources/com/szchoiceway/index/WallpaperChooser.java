package com.szchoiceway.index;

import android.app.Activity;
import android.os.Bundle;

public class WallpaperChooser extends Activity {
    private static final String TAG = "index.WallpaperChooser";

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.wallpaper_chooser_base);
        if (getFragmentManager().findFragmentById(R.id.wallpaper_chooser_fragment) == null) {
            WallpaperChooserDialogFragment.newInstance().show(getFragmentManager(), "dialog");
        }
    }
}
