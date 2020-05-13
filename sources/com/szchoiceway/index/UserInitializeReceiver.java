package com.szchoiceway.index;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import java.io.IOException;
import java.util.ArrayList;

public class UserInitializeReceiver extends BroadcastReceiver {
    @SuppressLint({"ServiceCast"})
    public void onReceive(Context context, Intent intent) {
        String packageName;
        Resources resources = context.getResources();
        if (LauncherApplication.m_iUITypeVer == 41) {
            packageName = resources.getResourcePackageName(R.array.ksw_wallpapers);
        } else {
            packageName = resources.getResourcePackageName(R.array.wallpapers);
        }
        ArrayList<Integer> list = new ArrayList<>();
        if (LauncherApplication.m_iUITypeVer == 41) {
            addWallpapers(resources, packageName, R.array.ksw_wallpapers, list);
        } else {
            addWallpapers(resources, packageName, R.array.wallpapers, list);
        }
        addWallpapers(resources, packageName, R.array.extra_wallpapers, list);
        WallpaperManager wpm = (WallpaperManager) context.getSystemService("wallpaper");
        int i = 1;
        while (i < list.size()) {
            int resid = list.get(i).intValue();
            if (!wpm.hasResourceWallpaper(resid)) {
                try {
                    wpm.setResource(resid);
                    return;
                } catch (IOException e) {
                    return;
                }
            } else {
                i++;
            }
        }
    }

    private void addWallpapers(Resources resources, String packageName, int resid, ArrayList<Integer> outList) {
        for (String extra : resources.getStringArray(resid)) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                outList.add(Integer.valueOf(res));
            }
        }
    }
}
